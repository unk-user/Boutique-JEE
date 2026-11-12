package ma.ensa.boutique.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

// Crée la SessionFactory au déploiement : les tables sont générées dès le démarrage
@WebListener
public class DemarrageListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent evenement) {
        HibernateUtil.getSessionFactory();
    }

    @Override
    public void contextDestroyed(ServletContextEvent evenement) {
        HibernateUtil.fermer();
    }
}
