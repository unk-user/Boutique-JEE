package ma.ensa.boutique.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.ensa.boutique.service.CatalogueService;
import ma.ensa.boutique.util.Parametres;

import java.io.IOException;

// BF05 (liste), BF06 (filtre par catégorie), BF07 (recherche), BNF10 (pagination)
@WebServlet("/catalogue")
public class CatalogueServlet extends HttpServlet {

    private final CatalogueService catalogueService = new CatalogueService();

    @Override
    protected void doGet(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        Long categorieId = Parametres.lireLong(requete.getParameter("categorie"));
        String motCle = requete.getParameter("motCle");
        if (motCle != null) {
            motCle = motCle.trim();
        }

        long total = catalogueService.compterProduits(categorieId, motCle);
        int nbPages = Parametres.nombrePages(total, CatalogueService.TAILLE_PAGE);
        int page = Math.min(Math.max(Parametres.lireEntier(requete.getParameter("page"), 1), 1), nbPages);

        requete.setAttribute("produits", catalogueService.listerProduits(categorieId, motCle, page));
        requete.setAttribute("categories", catalogueService.listerCategories());
        requete.setAttribute("categorieId", categorieId);
        requete.setAttribute("motCle", motCle);
        requete.setAttribute("total", total);
        requete.setAttribute("pageCourante", page);
        requete.setAttribute("nbPages", nbPages);
        requete.getRequestDispatcher("/WEB-INF/views/client/catalogue.jsp").forward(requete, reponse);
    }
}
