package ma.ensa.boutique.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.ensa.boutique.model.Utilisateur;
import ma.ensa.boutique.service.AuthService;
import ma.ensa.boutique.service.RegleMetierException;

import java.io.IOException;

// BF01
@WebServlet("/inscription")
public class InscriptionServlet extends HttpServlet {

    private static final String VUE = "/WEB-INF/views/commun/inscription.jsp";

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        requete.getRequestDispatcher(VUE).forward(requete, reponse);
    }

    @Override
    protected void doPost(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(requete.getParameter("nom"));
        utilisateur.setPrenom(requete.getParameter("prenom"));
        utilisateur.setEmail(requete.getParameter("email"));
        utilisateur.setAdresse(requete.getParameter("adresse"));
        utilisateur.setTelephone(requete.getParameter("telephone"));
        String motDePasse = requete.getParameter("motDePasse");
        String confirmation = requete.getParameter("confirmation");

        try {
            if (motDePasse == null || !motDePasse.equals(confirmation)) {
                throw new RegleMetierException("Les deux mots de passe ne correspondent pas.");
            }
            authService.inscrire(utilisateur, motDePasse);
            reponse.sendRedirect(requete.getContextPath() + "/connexion?inscrit");
        } catch (RegleMetierException e) {
            // Le formulaire est réaffiché avec les valeurs saisies (sauf le mot de passe)
            requete.setAttribute("erreur", e.getMessage());
            requete.setAttribute("saisie", utilisateur);
            requete.getRequestDispatcher(VUE).forward(requete, reponse);
        }
    }
}
