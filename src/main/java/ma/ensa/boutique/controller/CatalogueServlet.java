package ma.ensa.boutique.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/catalogue")
public class CatalogueServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        requete.getRequestDispatcher("/WEB-INF/views/client/catalogue.jsp").forward(requete, reponse);
    }
}
