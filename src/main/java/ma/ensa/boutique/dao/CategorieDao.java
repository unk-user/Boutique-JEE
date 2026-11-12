package ma.ensa.boutique.dao;

import ma.ensa.boutique.model.Categorie;

import java.util.List;

public class CategorieDao extends GenericDao<Categorie> {

    public CategorieDao() {
        super(Categorie.class);
    }

    @Override
    public List<Categorie> listerTous() {
        return session().createQuery("from Categorie c order by c.nom", Categorie.class).getResultList();
    }

    public Categorie trouverParNom(String nom) {
        return session()
                .createQuery("from Categorie c where c.nom = :nom", Categorie.class)
                .setParameter("nom", nom)
                .uniqueResult();
    }

    public long compterProduits(Long categorieId) {
        return session()
                .createQuery("select count(p) from Produit p where p.categorie.id = :id", Long.class)
                .setParameter("id", categorieId)
                .uniqueResult();
    }
}
