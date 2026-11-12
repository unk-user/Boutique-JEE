package ma.ensa.boutique.dao;

import ma.ensa.boutique.model.Utilisateur;

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
}
