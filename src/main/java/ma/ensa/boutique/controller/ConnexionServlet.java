package ma.ensa.boutique.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.ensa.boutique.model.Role;
import ma.ensa.boutique.model.Utilisateur;
import ma.ensa.boutique.service.AuthService;
import ma.ensa.boutique.service.RegleMetierException;

import java.io.IOException;

@WebServlet("/connexion")
public class ConnexionServlet extends HttpServlet {

    private static final String VUE = "/WEB-INF/views/commun/connexion.jsp";

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        if (requete.getParameter("inscrit") != null) {
            requete.setAttribute("succes", "Inscription réussie. Vous pouvez vous connecter.");
        } else if (requete.getParameter("deconnecte") != null) {
            requete.setAttribute("succes", "Vous êtes déconnecté.");
        }
        requete.getRequestDispatcher(VUE).forward(requete, reponse);
    }

    @Override
    protected void doPost(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        String email = requete.getParameter("email");
        try {
            Utilisateur utilisateur = authService.authentifier(email, requete.getParameter("motDePasse"));
            requete.getSession().invalidate();
            requete.getSession(true).setAttribute("utilisateur", utilisateur);

            String cible = utilisateur.getRole() == Role.ADMIN ? "/admin/tableau-de-bord" : "/catalogue";
            reponse.sendRedirect(requete.getContextPath() + cible);
        } catch (RegleMetierException e) {
            requete.setAttribute("erreur", e.getMessage());
            requete.setAttribute("email", email);
            requete.getRequestDispatcher(VUE).forward(requete, reponse);
        }
    }
}
