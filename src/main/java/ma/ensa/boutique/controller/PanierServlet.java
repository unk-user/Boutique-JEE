package ma.ensa.boutique.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.ensa.boutique.model.Panier;
import ma.ensa.boutique.service.PanierService;
import ma.ensa.boutique.service.RegleMetierException;
import ma.ensa.boutique.util.Parametres;

import java.io.IOException;

// BF09 à BF12 : sous /client/*, donc réservé au client authentifié par AuthFilter (RG08)
@WebServlet("/client/panier")
public class PanierServlet extends HttpServlet {

    private final PanierService panierService = new PanierService();

    @Override
    protected void doGet(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        // BF12
        requete.setAttribute("panier", lirePanier(requete.getSession()));
        requete.getRequestDispatcher("/WEB-INF/views/client/panier.jsp").forward(requete, reponse);
    }

    @Override
    protected void doPost(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        HttpSession session = requete.getSession();
        Panier panier = lirePanier(session);
        String action = requete.getParameter("action");
        Long produitId = Parametres.lireLong(requete.getParameter("produitId"));
        // Une quantité absente ou non numérique vaut 0 : le service la refuse
        int quantite = Parametres.lireEntier(requete.getParameter("quantite"), 0);
        String cible = "/client/panier";

        try {
            if ("ajouter".equals(action)) {
                panierService.ajouter(panier, produitId, quantite);
                session.setAttribute("flashSucces", "Produit ajouté au panier.");
            } else if ("modifier".equals(action)) {
                panierService.modifierQuantite(panier, produitId, quantite);
                session.setAttribute("flashSucces", "Quantité mise à jour.");
            } else if ("retirer".equals(action)) {
                panierService.retirer(panier, produitId);
                session.setAttribute("flashSucces", "Produit retiré du panier.");
            } else if ("vider".equals(action)) {
                panierService.vider(panier);
                session.setAttribute("flashSucces", "Panier vidé.");
            } else {
                session.setAttribute("flashErreur", "Action inconnue.");
            }
        } catch (RegleMetierException e) {
            session.setAttribute("flashErreur", e.getMessage());
            // Un refus à l'ajout ramène à la fiche du produit
            if ("ajouter".equals(action)) {
                cible = produitId == null ? "/catalogue" : "/produit?id=" + produitId;
            }
        }
        reponse.sendRedirect(requete.getContextPath() + cible);
    }

    // Le panier est créé et stocké en session à la première utilisation
    private Panier lirePanier(HttpSession session) {
        Panier panier = (Panier) session.getAttribute("panier");
        if (panier == null) {
            panier = new Panier();
            session.setAttribute("panier", panier);
        }
        return panier;
    }
}
