package ma.ensa.boutique.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

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
