package ma.ensa.boutique.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.ensa.boutique.model.StatutCommande;
import ma.ensa.boutique.service.CommandeService;
import ma.ensa.boutique.service.RegleMetierException;
import ma.ensa.boutique.util.Parametres;

import java.io.IOException;

// BF20 (liste filtrée par statut), BF21 (changement de statut)
@WebServlet("/admin/commandes")
public class AdminCommandeServlet extends HttpServlet {

    private static final String VUE_LISTE = "/WEB-INF/views/admin/commandes.jsp";
    private static final String VUE_DETAIL = "/WEB-INF/views/admin/commande-detail.jsp";

    private final CommandeService commandeService = new CommandeService();

    @Override
    protected void doGet(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        requete.setAttribute("statuts", StatutCommande.values());
        // Pour chaque statut, les statuts suivants autorisés (RG13) : seuls ceux-là sont proposés
        requete.setAttribute("transitions", commandeService.transitions());

        if (requete.getParameter("id") != null) {
            try {
                Long id = Parametres.lireLong(requete.getParameter("id"));
                requete.setAttribute("commande", commandeService.trouverCommande(id));
                requete.getRequestDispatcher(VUE_DETAIL).forward(requete, reponse);
            } catch (RegleMetierException e) {
                requete.getSession().setAttribute("flashErreur", e.getMessage());
                reponse.sendRedirect(requete.getContextPath() + "/admin/commandes");
            }
            return;
        }

        // Statut absent ou inconnu : toutes les commandes
        StatutCommande statut = lireStatut(requete.getParameter("statut"));
        long total = commandeService.compterCommandes(statut);
        int nbPages = Parametres.nombrePages(total, CommandeService.TAILLE_PAGE);
        int page = Math.min(Math.max(Parametres.lireEntier(requete.getParameter("page"), 1), 1), nbPages);
        requete.setAttribute("commandes", commandeService.listerCommandes(statut, page));
        requete.setAttribute("statutFiltre", statut);
        requete.setAttribute("total", total);
        requete.setAttribute("pageCourante", page);
        requete.setAttribute("nbPages", nbPages);
        requete.getRequestDispatcher(VUE_LISTE).forward(requete, reponse);
    }

    @Override
    protected void doPost(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        Long commandeId = Parametres.lireLong(requete.getParameter("commandeId"));
        // Un statut forgé ou inconnu devient null : le service le refuse avec un message
        StatutCommande nouveauStatut = lireStatut(requete.getParameter("nouveauStatut"));

        try {
            commandeService.changerStatut(commandeId, nouveauStatut);
            requete.getSession().setAttribute("flashSucces", "La commande n° " + commandeId
                    + " est maintenant « " + nouveauStatut.getLibelle() + " »."
                    + (nouveauStatut == StatutCommande.ANNULEE ? " Le stock a été réintégré." : ""));
        } catch (RegleMetierException e) {
            requete.getSession().setAttribute("flashErreur", e.getMessage());
        }

        // Retour à la page d'origine : détail de la commande, ou liste avec son filtre et sa page
        String retour = requete.getContextPath() + "/admin/commandes";
        if ("detail".equals(requete.getParameter("depuis")) && commandeId != null) {
            retour += "?id=" + commandeId;
        } else {
            retour += "?page=" + Parametres.lireEntier(requete.getParameter("page"), 1);
            StatutCommande filtre = lireStatut(requete.getParameter("statut"));
            if (filtre != null) {
                retour += "&statut=" + filtre.name();
            }
        }
        reponse.sendRedirect(retour);
    }

    private StatutCommande lireStatut(String valeur) {
        try {
            return valeur == null || valeur.isBlank() ? null : StatutCommande.valueOf(valeur.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
