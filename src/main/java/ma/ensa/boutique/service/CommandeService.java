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

    private static final int ADRESSE_MAX = 255;

    private final CommandeDao commandeDao = new CommandeDao();
    private final ProduitDao produitDao = new ProduitDao();
    private final UtilisateurDao utilisateurDao = new UtilisateurDao();

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

        List<LignePanier> lignesPanier = new ArrayList<>(panier.getLignes());
        lignesPanier.sort(Comparator.comparing(ligne -> ligne.getProduit().getId()));

        return enTransaction(() -> {
            Utilisateur utilisateur = utilisateurDao.trouverParId(utilisateurId);
            if (utilisateur == null) {
                throw new RegleMetierException("Utilisateur introuvable.");
            }
            if (!utilisateur.isActif()) {
                throw new RegleMetierException("Votre compte est désactivé : vous ne pouvez plus commander.");
            }
            Commande commande = new Commande(utilisateur, adresse, StatutCommande.EN_ATTENTE);

            for (LignePanier lignePanier : lignesPanier) {
                Produit produit = produitDao.trouverEtVerrouiller(lignePanier.getProduit().getId());
                // RG15
                if (produit == null || !produit.isActif()) {
                    throw new RegleMetierException("Le produit « " + lignePanier.getProduit().getNom()
                            + " » n'est plus disponible : retirez-le de votre panier.");
                }
                int quantite = lignePanier.getQuantite();
                // RG12
                if (produit.getStock() < quantite) {
                    throw new RegleMetierException("Stock insuffisant pour « " + produit.getNom() + " » ("
                            + produit.getStock() + " en stock, " + quantite + " demandé(s)).");
                }
                produit.setStock(produit.getStock() - quantite);
                // RG10
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

    public List<Commande> listerCommandesClient(Long utilisateurId, int page) {
        return enTransaction(() -> commandeDao.listerParUtilisateur(utilisateurId, debut(page), TAILLE_PAGE));
    }

    public long compterCommandesClient(Long utilisateurId) {
        return enTransaction(() -> commandeDao.compterParUtilisateur(utilisateurId));
    }

    // RG16
    public Commande trouverCommandeClient(Long commandeId, Long utilisateurId) {
        Commande commande = enTransaction(() -> commandeDao.trouverAvecLignes(commandeId));
        if (commande == null || !commande.getUtilisateur().getId().equals(utilisateurId)) {
            throw new RegleMetierException("Commande introuvable.");
        }
        return commande;
    }

    public void annulerCommandeClient(Long commandeId, Long utilisateurId) {
        enTransaction(() -> {
            Commande commande = commandeDao.trouverEtVerrouiller(commandeId);
            // RG16
            if (commande == null || !commande.getUtilisateur().getId().equals(utilisateurId)) {
                throw new RegleMetierException("Commande introuvable.");
            }
            if (commande.getStatut() != StatutCommande.EN_ATTENTE) {
                throw new RegleMetierException("Seule une commande « En attente » peut être annulée.");
            }
            // RG13, RG14
            appliquerStatut(commande, StatutCommande.ANNULEE);
            return null;
        });
    }

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

    // RG13
    public List<StatutCommande> statutsSuivants(StatutCommande statut) {
        if (statut == null) {
            return List.of();
        }
        switch (statut) {
            case EN_ATTENTE: return List.of(StatutCommande.VALIDEE, StatutCommande.ANNULEE);
            case VALIDEE:    return List.of(StatutCommande.EXPEDIEE, StatutCommande.ANNULEE);
            case EXPEDIEE:   return List.of(StatutCommande.LIVREE);
            default:         return List.of();
        }
    }

    public Map<StatutCommande, List<StatutCommande>> transitions() {
        Map<StatutCommande, List<StatutCommande>> transitions = new EnumMap<>(StatutCommande.class);
        for (StatutCommande statut : StatutCommande.values()) {
            transitions.put(statut, statutsSuivants(statut));
        }
        return transitions;
    }

    public void changerStatut(Long commandeId, StatutCommande nouveauStatut) {
        enTransaction(() -> {
            Commande commande = commandeDao.trouverEtVerrouiller(commandeId);
            if (commande == null) {
                throw new RegleMetierException("Commande introuvable.");
            }
            appliquerStatut(commande, nouveauStatut);
            return null;
        });
    }

    private void appliquerStatut(Commande commande, StatutCommande nouveauStatut) {
        if (nouveauStatut == null) {
            throw new RegleMetierException("Statut de commande inconnu.");
        }
        // RG13
        if (!statutsSuivants(commande.getStatut()).contains(nouveauStatut)) {
            throw new RegleMetierException("Changement de statut non autorisé : une commande « "
                    + commande.getStatut().getLibelle() + " » ne peut pas passer à « " + nouveauStatut.getLibelle() + " ».");
        }
        if (nouveauStatut == StatutCommande.ANNULEE) {
            // RG14
            List<LigneCommande> lignes = new ArrayList<>(commande.getLignes());
            lignes.sort(Comparator.comparing(ligne -> ligne.getProduit().getId()));
            for (LigneCommande ligne : lignes) {
                produitDao.reintegrerStock(ligne.getProduit().getId(), ligne.getQuantite());
            }
        }
        commande.setStatut(nouveauStatut);
    }

    public BigDecimal calculerChiffreAffaires() {
        return enTransaction(commandeDao::calculerChiffreAffaires);
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
