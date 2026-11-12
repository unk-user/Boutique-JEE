package ma.ensa.boutique.dao;

import ma.ensa.boutique.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

// CRUD commun à toutes les entités ; la transaction est ouverte par le service
public abstract class GenericDao<T> {

    protected final Class<T> classe;

    protected GenericDao(Class<T> classe) {
        this.classe = classe;
    }

    protected Session session() {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }

    public void sauvegarder(T entite) {
        session().persist(entite);
    }

    public T modifier(T entite) {
        return session().merge(entite);
    }

    public void supprimer(T entite) {
        session().remove(entite);
    }

    public T trouverParId(Long id) {
        return session().get(classe, id);
    }

    public List<T> listerTous() {
        return session().createQuery("from " + classe.getSimpleName(), classe).getResultList();
    }
}
