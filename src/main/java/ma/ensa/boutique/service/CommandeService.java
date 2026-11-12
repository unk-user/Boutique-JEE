package ma.ensa.boutique.service;

import ma.ensa.boutique.dao.CommandeDao;
import ma.ensa.boutique.dao.ProduitDao;
import ma.ensa.boutique.dao.UtilisateurDao;
import ma.ensa.boutique.model.Commande;
import ma.ensa.boutique.model.LigneCommande;
import ma.ensa.boutique.model.LignePanier;
import ma.ensa.boutique.model.Panier;
import ma.ensa.boutique.model.Produit;
import ma.ensa.boutique.model.StatutCommande;
import ma.ensa.boutique.model.Utilisateur;
import ma.ensa.boutique.util.HibernateUtil;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CommandeService {

    public static final int TAILLE_PAGE = 10;

    // Taille de la colonne adresse_livraison
    private static final int ADRESSE_MAX = 255;

    private final CommandeDao commandeDao = new CommandeDao();
    private final ProduitDao produitDao = new ProduitDao();
    private final UtilisateurDao utilisateurDao = new UtilisateurDao();

    // BF13, BNF05 : tout se fait dans une seule transaction ; la moindre erreur annule tout (RG12)
    public Commande passerCommande(Long utilisateurId, Panier panier, String adresseLivraison) {
        // RG09
        if (panier == null || panier.estVide()) {
            throw new RegleMetierException("Votre panier est vide.");
        }
        String adresse = adresseLivraison == null ? "" : adresseLivraison.trim();
        if (adresse.isEmpty()) {
            throw new RegleMetierException("L'adresse de livraison est obligatoire.");
        }
        if (adresse.length() > ADRESSE_MAX) {
            throw new RegleMetierException("L'adresse de livraison est trop longue (" + ADRESSE_MAX + " caractères maximum).");
        }

        // Les produits sont verrouillés par id croissant : deux commandes simultanées
        // prennent les verrous dans le même ordre et ne peuvent pas se bloquer mutuellement
        List<LignePanier> lignesPanier = new ArrayList<>(panier.getLignes());
        lignesPanier.sort(Comparator.comparing(ligne -> ligne.getProduit().getId()));

        return enTransaction(() -> {
            // L'utilisateur en session est détaché : on le recharge
            Utilisateur utilisateur = utilisateurDao.trouverParId(utilisateurId);
            if (utilisateur == null) {
                throw new RegleMetierException("Utilisateur introuvable.");
            }
            // Compte désactivé par un administrateur depuis la connexion (BF22)
            if (!utilisateur.isActif()) {
                throw new RegleMetierException("Votre compte est désactivé : vous ne pouvez plus commander.");
            }
            Commande commande = new Commande(utilisateur, adresse, StatutCommande.EN_ATTENTE);

            for (LignePanier lignePanier : lignesPanier) {
                // Le produit du panier est une copie qui peut être périmée : on relit prix, stock et état en base
                Produit produit = produitDao.trouverEtVerrouiller(lignePanier.getProduit().getId());
                // RG15 : produit supprimé ou désactivé depuis l'ajout au panier
                if (produit == null || !produit.isActif()) {
                    throw new RegleMetierException("Le produit « " + lignePanier.getProduit().getNom()
                            + " » n'est plus disponible : retirez-le de votre panier.");
                }
                int quantite = lignePanier.getQuantite();
                // RG12 : une exception ici annule la commande et tous les stocks déjà décrémentés
                if (produit.getStock() < quantite) {
                    throw new RegleMetierException("Stock insuffisant pour « " + produit.getNom() + " » ("
                            + produit.getStock() + " en stock, " + quantite + " demandé(s)).");
                }
                produit.setStock(produit.getStock() - quantite);
                // RG10 : prix actuel du produit copié dans la ligne
                commande.ajouterLigne(new LigneCommande(produit, quantite, produit.getPrix()));
            }

            // RG11
            BigDecimal total = BigDecimal.ZERO;
            for (LigneCommande ligne : commande.getLignes()) {
                total = total.add(ligne.getPrixUnitaire().multiply(BigDecimal.valueOf(ligne.getQuantite())));
            }
            commande.setMontantTotal(total);

            commandeDao.sauvegarder(commande);
            return commande;
        });
    }

    // ---------- Consultation par le client (BF14, BF15, RG16) ----------

    public List<Commande> listerCommandesClient(Long utilisateurId, int page) {
        return enTransaction(() -> commandeDao.listerParUtilisateur(utilisateurId, debut(page), TAILLE_PAGE));
    }

    public long compterCommandesClient(Long utilisateurId) {
        return enTransaction(() -> commandeDao.compterParUtilisateur(utilisateurId));
    }

    // RG16 : la commande d'un autre client est traitée comme une commande inexistante (même message)
    public Commande trouverCommandeClient(Long commandeId, Long utilisateurId) {
        Commande commande = enTransaction(() -> commandeDao.trouverAvecLignes(commandeId));
        if (commande == null || !commande.getUtilisateur().getId().equals(utilisateurId)) {
            throw new RegleMetierException("Commande introuvable.");
        }
        return commande;
    }

    // ---------- Back-office (BF20, BF21) ----------

    // statut null : toutes les commandes
    public List<Commande> listerCommandes(StatutCommande statut, int page) {
        return enTransaction(() -> commandeDao.listerParStatut(statut, debut(page), TAILLE_PAGE));
    }

    public long compterCommandes(StatutCommande statut) {
        return enTransaction(() -> commandeDao.compterParStatut(statut));
    }

    public Commande trouverCommande(Long id) {
        Commande commande = enTransaction(() -> commandeDao.trouverAvecLignes(id));
        if (commande == null) {
            throw new RegleMetierException("Commande introuvable.");
        }
        return commande;
    }

    // RG13 : statuts que l'on peut atteindre depuis le statut actuel (liste vide : statut final)
    public List<StatutCommande> statutsSuivants(StatutCommande statut) {
        if (statut == null) {
            return List.of();
        }
        switch (statut) {
            case EN_ATTENTE: return List.of(StatutCommande.VALIDEE, StatutCommande.ANNULEE);
            case VALIDEE:    return List.of(StatutCommande.EXPEDIEE, StatutCommande.ANNULEE);
            case EXPEDIEE:   return List.of(StatutCommande.LIVREE);
            default:         return List.of(); // LIVREE et ANNULEE sont définitifs
        }
    }

    // Transitions de chaque statut, pour n'afficher que les choix autorisés dans le back-office
    public Map<StatutCommande, List<StatutCommande>> transitions() {
        Map<StatutCommande, List<StatutCommande>> transitions = new EnumMap<>(StatutCommande.class);
        for (StatutCommande statut : StatutCommande.values()) {
            transitions.put(statut, statutsSuivants(statut));
        }
        return transitions;
    }

    // BF21 : une seule transaction ; en cas d'erreur, ni le statut ni le stock ne changent
    public void changerStatut(Long commandeId, StatutCommande nouveauStatut) {
        enTransaction(() -> {
            // Verrou sur la commande : deux annulations simultanées ne réintègrent pas le stock deux fois
            Commande commande = commandeDao.trouverEtVerrouiller(commandeId);
            if (commande == null) {
                throw new RegleMetierException("Commande introuvable.");
            }
            appliquerStatut(commande, nouveauStatut);
            return null;
        });
    }

    // Partagé avec l'annulation par le client (BF16) ; appelé dans une transaction, commande verrouillée
    private void appliquerStatut(Commande commande, StatutCommande nouveauStatut) {
        if (nouveauStatut == null) {
            throw new RegleMetierException("Statut de commande inconnu.");
        }
        // RG13 : refuse aussi le même statut, les retours en arrière, les sauts d'étape et tout changement
        // depuis LIVREE ou ANNULEE
        if (!statutsSuivants(commande.getStatut()).contains(nouveauStatut)) {
            throw new RegleMetierException("Changement de statut non autorisé : une commande « "
                    + commande.getStatut().getLibelle() + " » ne peut pas passer à « " + nouveauStatut.getLibelle() + " ».");
        }
        if (nouveauStatut == StatutCommande.ANNULEE) {
            // RG14 : chaque quantité commandée retourne dans le stock du produit (même s'il est désactivé).
            // Produits traités par id croissant, dans le même ordre que passerCommande (pas d'interblocage)
            List<LigneCommande> lignes = new ArrayList<>(commande.getLignes());
            lignes.sort(Comparator.comparing(ligne -> ligne.getProduit().getId()));
            for (LigneCommande ligne : lignes) {
                produitDao.reintegrerStock(ligne.getProduit().getId(), ligne.getQuantite());
            }
        }
        commande.setStatut(nouveauStatut);
    }

    private int debut(int page) {
        return (Math.max(page, 1) - 1) * TAILLE_PAGE;
    }

    private <R> R enTransaction(Supplier<R> traitement) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        try {
            R resultat = traitement.get();
            session.getTransaction().commit();
            return resultat;
        } catch (RuntimeException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }
}
