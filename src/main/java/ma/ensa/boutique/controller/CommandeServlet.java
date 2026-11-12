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
import ma.ensa.boutique.util.Parametres;

import java.io.IOException;
import java.util.Locale;

@WebServlet("/client/commandes")
public class CommandeServlet extends HttpServlet {

    private static final String VUE_VALIDATION = "/WEB-INF/views/client/commande-validation.jsp";
    private static final String VUE_LISTE = "/WEB-INF/views/client/commandes.jsp";
    private static final String VUE_DETAIL = "/WEB-INF/views/client/commande-detail.jsp";

    private final CommandeService commandeService = new CommandeService();

    @Override
    protected void doGet(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        Utilisateur utilisateur = (Utilisateur) requete.getSession().getAttribute("utilisateur");

        if ("valider".equals(requete.getParameter("action"))) {
            afficherValidation(requete, reponse, utilisateur.getAdresse());
        } else if (requete.getParameter("id") != null) {
            try {
                Long id = Parametres.lireLong(requete.getParameter("id"));
                requete.setAttribute("commande", commandeService.trouverCommandeClient(id, utilisateur.getId()));
                requete.getRequestDispatcher(VUE_DETAIL).forward(requete, reponse);
            } catch (RegleMetierException e) {
                requete.getSession().setAttribute("flashErreur", e.getMessage());
                reponse.sendRedirect(requete.getContextPath() + "/client/commandes");
            }
        } else {
            long total = commandeService.compterCommandesClient(utilisateur.getId());
            int nbPages = Parametres.nombrePages(total, CommandeService.TAILLE_PAGE);
            int page = Math.min(Math.max(Parametres.lireEntier(requete.getParameter("page"), 1), 1), nbPages);
            requete.setAttribute("commandes", commandeService.listerCommandesClient(utilisateur.getId(), page));
            requete.setAttribute("pageCourante", page);
            requete.setAttribute("nbPages", nbPages);
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
