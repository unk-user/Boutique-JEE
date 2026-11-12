package ma.ensa.boutique.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// Page d'accueil de l'administrateur ; les indicateurs (BF23) viendront plus tard
@WebServlet("/admin/tableau-de-bord")
public class TableauDeBordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        requete.getRequestDispatcher("/WEB-INF/views/admin/tableau-de-bord.jsp").forward(requete, reponse);
    }
}
