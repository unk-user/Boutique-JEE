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
import java.util.List;
import java.util.function.Supplier;

public class CommandeService {

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
