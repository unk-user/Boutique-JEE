package ma.ensa.boutique.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.ensa.boutique.model.Role;
import ma.ensa.boutique.model.Utilisateur;

import java.io.IOException;

@WebFilter("/admin/*")
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse rep, FilterChain chaine)
            throws IOException, ServletException {
        HttpServletRequest requete = (HttpServletRequest) req;
        HttpServletResponse reponse = (HttpServletResponse) rep;

        HttpSession session = requete.getSession(false);
        Utilisateur utilisateur = session == null ? null : (Utilisateur) session.getAttribute("utilisateur");

        if (utilisateur == null) {
            reponse.sendRedirect(requete.getContextPath() + "/connexion");
        } else if (utilisateur.getRole() != Role.ADMIN) {
            reponse.sendError(HttpServletResponse.SC_FORBIDDEN);
        } else {
            chaine.doFilter(requete, reponse);
        }
    }
}
