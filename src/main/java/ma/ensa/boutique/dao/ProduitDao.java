package ma.ensa.boutique.dao;

import ma.ensa.boutique.model.Produit;

import org.hibernate.LockMode;
import org.hibernate.query.Query;

import java.util.List;

public class ProduitDao extends GenericDao<Produit> {

    public ProduitDao() {
        super(Produit.class);
    }

    // Tous les produits, actifs ou non (back-office)
    public List<Produit> listerPagine(int debut, int taille) {
        return session()
                .createQuery("from Produit p order by p.id desc", Produit.class)
                .setFirstResult(debut)
                .setMaxResults(taille)
                .getResultList();
    }

    public long compter() {
        return session().createQuery("select count(p) from Produit p", Long.class).uniqueResult();
    }

    public boolean estCommande(Long produitId) {
        Long nombre = session()
                .createQuery("select count(l) from LigneCommande l where l.produit.id = :id", Long.class)
                .setParameter("id", produitId)
                .uniqueResult();
        return nombre > 0;
    }

    // Passage de commande (RG12) : recharge le produit avec un verrou en écriture (select ... for update).
    // Deux commandes simultanées sur le même produit sont ainsi traitées l'une après l'autre,
    // ce qui empêche de vendre plus que le stock. Le verrou est libéré au commit ou au rollback.
    public Produit trouverEtVerrouiller(Long id) {
        return id == null ? null : session().get(Produit.class, id, LockMode.PESSIMISTIC_WRITE);
    }

    // ---------- Catalogue public : produits actifs uniquement (RG15) ----------

    // categorieId et motCle sont facultatifs (null = pas de filtre) ; la recherche ignore la casse
    public List<Produit> rechercher(Long categorieId, String motCle, int debut, int taille) {
        Query<Produit> requete = session().createQuery(
                "from Produit p" + conditions(categorieId, motCle) + " order by p.dateCreation desc, p.id desc",
                Produit.class);
        parametrer(requete, categorieId, motCle);
        return requete.setFirstResult(debut).setMaxResults(taille).getResultList();
    }

    public long compterRecherche(Long categorieId, String motCle) {
        Query<Long> requete = session().createQuery(
                "select count(p) from Produit p" + conditions(categorieId, motCle), Long.class);
        parametrer(requete, categorieId, motCle);
        return requete.uniqueResult();
    }

    public Produit trouverActifParId(Long id) {
        if (id == null) {
            return null;
        }
        return session()
                .createQuery("from Produit p where p.id = :id and p.actif = true", Produit.class)
                .setParameter("id", id)
                .uniqueResult();
    }

    // La clause where n'est construite qu'avec des paramètres nommés : jamais de valeur concaténée
    private String conditions(Long categorieId, String motCle) {
        StringBuilder hql = new StringBuilder(" where p.actif = true");
        if (categorieId != null) {
            hql.append(" and p.categorie.id = :categorieId");
        }
        if (motCle != null) {
            hql.append(" and (lower(p.nom) like :motCle or lower(p.description) like :motCle)");
        }
        return hql.toString();
    }

    private void parametrer(Query<?> requete, Long categorieId, String motCle) {
        if (categorieId != null) {
            requete.setParameter("categorieId", categorieId);
        }
        if (motCle != null) {
            requete.setParameter("motCle", "%" + motCle.toLowerCase() + "%");
        }
    }
}
