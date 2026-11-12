package ma.ensa.boutique.service;

import ma.ensa.boutique.dao.ProduitDao;
import ma.ensa.boutique.model.Panier;
import ma.ensa.boutique.model.Produit;
import ma.ensa.boutique.util.HibernateUtil;
import org.hibernate.Session;

import java.util.function.Supplier;

// BF09 à BF11 : le panier est un objet de la session HTTP ; le service recharge le produit
// depuis la base à chaque opération pour contrôler le stock réel.
public class PanierService {

    private final ProduitDao produitDao = new ProduitDao();

    public void ajouter(Panier panier, Long produitId, int quantite) {
        Produit produit = chargerProduit(produitId);
        verifierQuantite(quantite);
        // RG06
        if (produit.getStock() == 0) {
            throw new RegleMetierException("Produit en rupture de stock.");
        }
        // RG07 : quantité déjà au panier + quantité ajoutée
        if (panier.getQuantite(produitId) + quantite > produit.getStock()) {
            throw new RegleMetierException("Quantité demandée supérieure au stock disponible ("
                    + produit.getStock() + " en stock, " + panier.getQuantite(produitId) + " déjà dans votre panier).");
        }
        panier.ajouter(produit, quantite);
    }

    public void modifierQuantite(Panier panier, Long produitId, int quantite) {
        if (panier.getQuantite(produitId) == 0) {
            throw new RegleMetierException("Ce produit n'est pas dans votre panier.");
        }
        Produit produit = chargerProduit(produitId);
        verifierQuantite(quantite);
        // RG06, RG07
        if (produit.getStock() == 0) {
            throw new RegleMetierException("Produit en rupture de stock.");
        }
        if (quantite > produit.getStock()) {
            throw new RegleMetierException("Quantité demandée supérieure au stock disponible ("
                    + produit.getStock() + " en stock).");
        }
        // ajouter() met aussi à jour la copie du produit gardée dans la ligne (prix à jour),
        // on lui passe donc l'écart de quantité
        panier.ajouter(produit, quantite - panier.getQuantite(produitId));
    }

    public void retirer(Panier panier, Long produitId) {
        panier.retirer(produitId);
    }

    public void vider(Panier panier) {
        panier.vider();
    }

    // RG15 : un produit inconnu ou désactivé ne peut pas être mis au panier
    private Produit chargerProduit(Long produitId) {
        Produit produit = enTransaction(() -> produitDao.trouverActifParId(produitId));
        if (produit == null) {
            throw new RegleMetierException("Produit introuvable.");
        }
        return produit;
    }

    private void verifierQuantite(int quantite) {
        if (quantite <= 0) {
            throw new RegleMetierException("Quantité invalide : indiquez un nombre entier supérieur ou égal à 1.");
        }
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
