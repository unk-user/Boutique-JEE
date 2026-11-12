package ma.ensa.boutique.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.ensa.boutique.model.Produit;
import ma.ensa.boutique.service.CatalogueService;
import ma.ensa.boutique.service.RegleMetierException;
import ma.ensa.boutique.util.Parametres;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/admin/produits")
public class AdminProduitServlet extends HttpServlet {

    private static final String VUE_LISTE = "/WEB-INF/views/admin/produits.jsp";
    private static final String VUE_FORMULAIRE = "/WEB-INF/views/admin/produit-form.jsp";

    private final CatalogueService catalogueService = new CatalogueService();

    @Override
    protected void doGet(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        String action = requete.getParameter("action");

        if ("nouveau".equals(action)) {
            afficherFormulaire(requete, reponse, new Produit(), null);
        } else if ("modifier".equals(action)) {
            Long id = Parametres.lireLong(requete.getParameter("id"));
            Produit produit = id == null ? null : catalogueService.trouverProduit(id);
            if (produit == null) {
                requete.getSession().setAttribute("flashErreur", "Produit introuvable.");
                reponse.sendRedirect(requete.getContextPath() + "/admin/produits");
                return;
            }
            afficherFormulaire(requete, reponse, produit, produit.getCategorie().getId());
        } else {
            long total = catalogueService.compterTousProduits();
            int nbPages = Parametres.nombrePages(total, CatalogueService.TAILLE_PAGE);
            int page = Math.min(Math.max(Parametres.lireEntier(requete.getParameter("page"), 1), 1), nbPages);
            requete.setAttribute("produits", catalogueService.listerTousProduits(page));
            requete.setAttribute("pageCourante", page);
            requete.setAttribute("nbPages", nbPages);
            requete.getRequestDispatcher(VUE_LISTE).forward(requete, reponse);
        }
    }

    @Override
    protected void doPost(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        String action = requete.getParameter("action");
        Long id = Parametres.lireLong(requete.getParameter("id"));
        String retourListe = requete.getContextPath() + "/admin/produits?page="
                + Parametres.lireEntier(requete.getParameter("page"), 1);

        if ("supprimer".equals(action)) {
            try {
                boolean supprime = catalogueService.supprimerProduit(id);
                requete.getSession().setAttribute("flashSucces", supprime
                        ? "Produit supprimé."
                        : "Produit déjà commandé : il a été désactivé et retiré du catalogue.");
            } catch (RegleMetierException e) {
                requete.getSession().setAttribute("flashErreur", e.getMessage());
            }
            reponse.sendRedirect(retourListe);
            return;
        }

        if ("stock".equals(action)) {
            try {
                catalogueService.modifierStock(id, lireStock(requete.getParameter("stock")));
                requete.getSession().setAttribute("flashSucces", "Stock mis à jour.");
            } catch (RegleMetierException e) {
                requete.getSession().setAttribute("flashErreur", e.getMessage());
            }
            reponse.sendRedirect(retourListe);
            return;
        }

        Produit produit = new Produit();
        produit.setId(id);
        produit.setNom(requete.getParameter("nom"));
        produit.setDescription(requete.getParameter("description"));
        produit.setImageUrl(requete.getParameter("imageUrl"));
        produit.setActif(requete.getParameter("actif") != null);
        Long categorieId = Parametres.lireLong(requete.getParameter("categorieId"));
        try {
            produit.setPrix(lirePrix(requete.getParameter("prix")));
            produit.setStock(lireStock(requete.getParameter("stock")));
            catalogueService.enregistrerProduit(produit, categorieId);
            requete.getSession().setAttribute("flashSucces", "Produit enregistré.");
            reponse.sendRedirect(requete.getContextPath() + "/admin/produits");
        } catch (RegleMetierException e) {
            requete.setAttribute("erreur", e.getMessage());
            afficherFormulaire(requete, reponse, produit, categorieId);
        }
    }

    private void afficherFormulaire(HttpServletRequest requete, HttpServletResponse reponse,
                                    Produit produit, Long categorieId) throws ServletException, IOException {
        requete.setAttribute("produit", produit);
        requete.setAttribute("categorieId", categorieId);
        requete.setAttribute("categories", catalogueService.listerCategories());
        requete.getRequestDispatcher(VUE_FORMULAIRE).forward(requete, reponse);
    }

    private BigDecimal lirePrix(String valeur) {
        try {
            return new BigDecimal(valeur.trim().replace(',', '.'));
        } catch (NullPointerException | NumberFormatException e) {
            throw new RegleMetierException("Le prix doit être un nombre (exemple : 149,90).");
        }
    }

    private int lireStock(String valeur) {
        try {
            return Integer.parseInt(valeur.trim());
        } catch (NullPointerException | NumberFormatException e) {
            throw new RegleMetierException("Le stock doit être un nombre entier.");
        }
    }
}
