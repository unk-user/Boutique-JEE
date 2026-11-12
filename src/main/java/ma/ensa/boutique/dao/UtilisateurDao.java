package ma.ensa.boutique.dao;

import ma.ensa.boutique.model.Utilisateur;

import java.util.List;

public class UtilisateurDao extends GenericDao<Utilisateur> {

    public UtilisateurDao() {
        super(Utilisateur.class);
    }

    public Utilisateur trouverParEmail(String email) {
        return session()
                .createQuery("from Utilisateur u where u.email = :email", Utilisateur.class)
                .setParameter("email", email)
                .uniqueResult();
    }

    // BF22 : plus récents d'abord
    public List<Utilisateur> listerPagine(int debut, int taille) {
        return session()
                .createQuery("from Utilisateur u order by u.dateInscription desc, u.id desc", Utilisateur.class)
                .setFirstResult(debut)
                .setMaxResults(taille)
                .getResultList();
    }

    public long compter() {
        return session().createQuery("select count(u) from Utilisateur u", Long.class).uniqueResult();
    }
}
