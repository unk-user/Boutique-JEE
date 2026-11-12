package ma.ensa.boutique.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.ensa.boutique.model.Produit;
import ma.ensa.boutique.service.CatalogueService;
import ma.ensa.boutique.util.Parametres;

import java.io.IOException;

// BF08 : fiche détaillée d'un produit
@WebServlet("/produit")
public class ProduitServlet extends HttpServlet {

    private final CatalogueService catalogueService = new CatalogueService();

    @Override
    protected void doGet(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        Long id = Parametres.lireLong(requete.getParameter("id"));
        Produit produit = catalogueService.trouverProduitActif(id);
        if (produit == null) {
            // RG15 : produit inconnu ou désactivé
            requete.getSession().setAttribute("flashErreur", "Produit introuvable.");
            reponse.sendRedirect(requete.getContextPath() + "/catalogue");
            return;
        }
        requete.setAttribute("produit", produit);
        requete.getRequestDispatcher("/WEB-INF/views/client/produit.jsp").forward(requete, reponse);
    }
}
