package ma.ensa.boutique.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.ensa.boutique.model.Commande;
import ma.ensa.boutique.model.Panier;
import ma.ensa.boutique.model.Utilisateur;
import ma.ensa.boutique.service.CommandeService;
import ma.ensa.boutique.service.RegleMetierException;

import java.io.IOException;
import java.util.Locale;

// BF13 : sous /client/*, donc réservé au client authentifié par AuthFilter (RG08)
@WebServlet("/client/commandes")
public class CommandeServlet extends HttpServlet {

    private static final String VUE_VALIDATION = "/WEB-INF/views/client/commande-validation.jsp";
    private static final String VUE_LISTE = "/WEB-INF/views/client/commandes.jsp";

    private final CommandeService commandeService = new CommandeService();

    @Override
    protected void doGet(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        if ("valider".equals(requete.getParameter("action"))) {
            // Adresse proposée par défaut : celle du profil du client
            Utilisateur utilisateur = (Utilisateur) requete.getSession().getAttribute("utilisateur");
            afficherValidation(requete, reponse, utilisateur.getAdresse());
        } else {
            requete.getRequestDispatcher(VUE_LISTE).forward(requete, reponse);
        }
    }

    @Override
    protected void doPost(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        HttpSession session = requete.getSession();
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        Panier panier = (Panier) session.getAttribute("panier");
        String adresseLivraison = requete.getParameter("adresseLivraison");

        try {
            Commande commande = commandeService.passerCommande(utilisateur.getId(), panier, adresseLivraison);
            panier.vider();
            session.setAttribute("flashSucces", "Votre commande n° " + commande.getId()
                    + " a bien été enregistrée. Montant total : "
                    + String.format(Locale.FRANCE, "%,.2f", commande.getMontantTotal()) + " DH.");
            reponse.sendRedirect(requete.getContextPath() + "/client/commandes");
        } catch (RegleMetierException e) {
            // Le panier n'est pas modifié : le client corrige et confirme à nouveau
            requete.setAttribute("erreur", e.getMessage());
            afficherValidation(requete, reponse, adresseLivraison);
        }
    }

    private void afficherValidation(HttpServletRequest requete, HttpServletResponse reponse, String adresse)
            throws ServletException, IOException {
        requete.setAttribute("panier", requete.getSession().getAttribute("panier"));
        requete.setAttribute("adresseLivraison", adresse);
        requete.getRequestDispatcher(VUE_VALIDATION).forward(requete, reponse);
    }
}
