package ma.ensa.boutique.dao;

import ma.ensa.boutique.model.Produit;

import org.hibernate.LockMode;
import org.hibernate.query.Query;

import java.util.List;

public class ProduitDao extends GenericDao<Produit> {

    public ProduitDao() {
        super(Produit.class);
    }

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

    public Produit trouverEtVerrouiller(Long id) {
        if (id == null) {
            return null;
        }
        return session()
                .createQuery("from Produit p where p.id = :id", Produit.class)
                .setParameter("id", id)
                .setHibernateLockMode(LockMode.PESSIMISTIC_WRITE)
                .uniqueResult();
    }

    public void reintegrerStock(Long id, int quantite) {
        session()
                .createMutationQuery("update Produit p set p.stock = p.stock + :quantite where p.id = :id")
                .setParameter("quantite", quantite)
                .setParameter("id", id)
                .executeUpdate();
    }

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
