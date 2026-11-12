package ma.ensa.boutique.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.ensa.boutique.service.AuthService;
import ma.ensa.boutique.service.RegleMetierException;
import ma.ensa.boutique.util.Parametres;

import java.io.IOException;

// BF22 : liste des utilisateurs, activation et désactivation d'un compte
@WebServlet("/admin/utilisateurs")
public class AdminUtilisateurServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        long total = authService.compterUtilisateurs();
        int nbPages = Parametres.nombrePages(total, AuthService.TAILLE_PAGE);
        int page = Math.min(Math.max(Parametres.lireEntier(requete.getParameter("page"), 1), 1), nbPages);
        requete.setAttribute("utilisateurs", authService.listerUtilisateurs(page));
        requete.setAttribute("total", total);
        requete.setAttribute("pageCourante", page);
        requete.setAttribute("nbPages", nbPages);
        requete.getRequestDispatcher("/WEB-INF/views/admin/utilisateurs.jsp").forward(requete, reponse);
    }

    @Override
    protected void doPost(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        Long id = Parametres.lireLong(requete.getParameter("id"));
        String action = requete.getParameter("action");

        try {
            if ("activer".equals(action)) {
                authService.changerEtatCompte(id, true);
                requete.getSession().setAttribute("flashSucces", "Le compte a été activé.");
            } else if ("desactiver".equals(action)) {
                authService.changerEtatCompte(id, false);
                requete.getSession().setAttribute("flashSucces", "Le compte a été désactivé : il ne peut plus se connecter.");
            } else {
                requete.getSession().setAttribute("flashErreur", "Action inconnue.");
            }
        } catch (RegleMetierException e) {
            requete.getSession().setAttribute("flashErreur", e.getMessage());
        }
        // Retour à la même page de la liste
        reponse.sendRedirect(requete.getContextPath() + "/admin/utilisateurs?page="
                + Parametres.lireEntier(requete.getParameter("page"), 1));
    }
}
