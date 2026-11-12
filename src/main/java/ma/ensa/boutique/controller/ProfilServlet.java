package ma.ensa.boutique.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.ensa.boutique.model.Utilisateur;
import ma.ensa.boutique.service.AuthService;
import ma.ensa.boutique.service.RegleMetierException;

import java.io.IOException;

@WebServlet("/client/profil")
public class ProfilServlet extends HttpServlet {

    private static final String VUE = "/WEB-INF/views/client/profil.jsp";

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        requete.getRequestDispatcher(VUE).forward(requete, reponse);
    }

    @Override
    protected void doPost(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        if ("motdepasse".equals(requete.getParameter("action"))) {
            changerMotDePasse(requete, reponse);
        } else {
            modifierInformations(requete, reponse);
        }
    }

    private void modifierInformations(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        HttpSession session = requete.getSession();
        Utilisateur connecte = (Utilisateur) session.getAttribute("utilisateur");
        Utilisateur saisie = new Utilisateur();
        saisie.setId(connecte.getId());
        saisie.setNom(requete.getParameter("nom"));
        saisie.setPrenom(requete.getParameter("prenom"));
        saisie.setEmail(requete.getParameter("email"));
        saisie.setAdresse(requete.getParameter("adresse"));
        saisie.setTelephone(requete.getParameter("telephone"));

        try {
            session.setAttribute("utilisateur", authService.modifierProfil(saisie));
            session.setAttribute("flashSucces", "Votre profil a été mis à jour.");
            reponse.sendRedirect(requete.getContextPath() + "/client/profil");
        } catch (RegleMetierException e) {
            requete.setAttribute("erreur", e.getMessage());
            requete.setAttribute("saisie", saisie);
            requete.getRequestDispatcher(VUE).forward(requete, reponse);
        }
    }

    private void changerMotDePasse(HttpServletRequest requete, HttpServletResponse reponse) throws IOException {
        HttpSession session = requete.getSession();
        Utilisateur connecte = (Utilisateur) session.getAttribute("utilisateur");
        String nouveau = requete.getParameter("nouveauMotDePasse");

        try {
            if (nouveau == null || !nouveau.equals(requete.getParameter("confirmation"))) {
                throw new RegleMetierException("Les deux mots de passe ne correspondent pas.");
            }
            authService.changerMotDePasse(connecte.getId(), requete.getParameter("motDePasseActuel"), nouveau);
            session.setAttribute("flashSucces", "Votre mot de passe a été modifié.");
        } catch (RegleMetierException e) {
            session.setAttribute("flashErreur", e.getMessage());
        }
        reponse.sendRedirect(requete.getContextPath() + "/client/profil");
    }
}
