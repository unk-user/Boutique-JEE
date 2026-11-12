package ma.ensa.boutique.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {

    // Une seule SessionFactory pour toute l'application (objet coûteux à créer)
    private static final SessionFactory sessionFactory = construire();

    private HibernateUtil() {
    }

    private static SessionFactory construire() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Échec de la création de la SessionFactory : " + e.getMessage());
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void fermer() {
        sessionFactory.close();
    }
}
