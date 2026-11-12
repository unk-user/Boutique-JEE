package ma.ensa.boutique.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/deconnexion")
public class DeconnexionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest requete, HttpServletResponse reponse) throws IOException {
        HttpSession session = requete.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        reponse.sendRedirect(requete.getContextPath() + "/connexion?deconnecte");
    }
}
