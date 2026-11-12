package ma.ensa.boutique.dao;

import ma.ensa.boutique.model.Produit;

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
}
