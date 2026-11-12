package ma.ensa.boutique.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.ensa.boutique.model.Categorie;
import ma.ensa.boutique.service.CatalogueService;
import ma.ensa.boutique.service.RegleMetierException;
import ma.ensa.boutique.util.Parametres;

import java.io.IOException;

// BF17
@WebServlet("/admin/categories")
public class AdminCategorieServlet extends HttpServlet {

    private static final String VUE_LISTE = "/WEB-INF/views/admin/categories.jsp";
    private static final String VUE_FORMULAIRE = "/WEB-INF/views/admin/categorie-form.jsp";

    private final CatalogueService catalogueService = new CatalogueService();

    @Override
    protected void doGet(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        String action = requete.getParameter("action");

        if ("nouveau".equals(action)) {
            requete.setAttribute("categorie", new Categorie());
            requete.getRequestDispatcher(VUE_FORMULAIRE).forward(requete, reponse);
        } else if ("modifier".equals(action)) {
            Long id = Parametres.lireLong(requete.getParameter("id"));
            Categorie categorie = id == null ? null : catalogueService.trouverCategorie(id);
            if (categorie == null) {
                requete.getSession().setAttribute("flashErreur", "Catégorie introuvable.");
                reponse.sendRedirect(requete.getContextPath() + "/admin/categories");
                return;
            }
            requete.setAttribute("categorie", categorie);
            requete.getRequestDispatcher(VUE_FORMULAIRE).forward(requete, reponse);
        } else {
            requete.setAttribute("categories", catalogueService.listerCategories());
            requete.getRequestDispatcher(VUE_LISTE).forward(requete, reponse);
        }
    }

    @Override
    protected void doPost(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        String action = requete.getParameter("action");
        Long id = Parametres.lireLong(requete.getParameter("id"));

        if ("supprimer".equals(action)) {
            try {
                catalogueService.supprimerCategorie(id);
                requete.getSession().setAttribute("flashSucces", "Catégorie supprimée.");
            } catch (RegleMetierException e) {
                requete.getSession().setAttribute("flashErreur", e.getMessage());
            }
            reponse.sendRedirect(requete.getContextPath() + "/admin/categories");
            return;
        }

        Categorie categorie = new Categorie();
        categorie.setId(id);
        categorie.setNom(requete.getParameter("nom"));
        categorie.setDescription(requete.getParameter("description"));
        try {
            catalogueService.enregistrerCategorie(categorie);
            requete.getSession().setAttribute("flashSucces", "Catégorie enregistrée.");
            reponse.sendRedirect(requete.getContextPath() + "/admin/categories");
        } catch (RegleMetierException e) {
            requete.setAttribute("erreur", e.getMessage());
            requete.setAttribute("categorie", categorie);
            requete.getRequestDispatcher(VUE_FORMULAIRE).forward(requete, reponse);
        }
    }
}
