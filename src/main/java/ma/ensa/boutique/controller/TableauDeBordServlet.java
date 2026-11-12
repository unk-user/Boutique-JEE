package ma.ensa.boutique.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.ensa.boutique.model.StatutCommande;
import ma.ensa.boutique.service.CatalogueService;
import ma.ensa.boutique.service.CommandeService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

// BF23 : page d'accueil de l'administrateur avec les indicateurs de la boutique.
// Cette servlet appelle deux services (commandes et catalogue) car le tableau de bord les réunit.
@WebServlet("/admin/tableau-de-bord")
public class TableauDeBordServlet extends HttpServlet {

    private final CommandeService commandeService = new CommandeService();
    private final CatalogueService catalogueService = new CatalogueService();

    @Override
    protected void doGet(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        // Nombre de commandes par statut, dans l'ordre du cycle de vie
        Map<StatutCommande, Long> parStatut = new LinkedHashMap<>();
        for (StatutCommande statut : StatutCommande.values()) {
            parStatut.put(statut, commandeService.compterCommandes(statut));
        }
        requete.setAttribute("nombreCommandes", commandeService.compterCommandes(null));
        requete.setAttribute("commandesParStatut", parStatut);
        requete.setAttribute("chiffreAffaires", commandeService.calculerChiffreAffaires());
        requete.setAttribute("produitsEnRupture", catalogueService.listerProduitsEnRupture());
        requete.getRequestDispatcher("/WEB-INF/views/admin/tableau-de-bord.jsp").forward(requete, reponse);
    }
}
