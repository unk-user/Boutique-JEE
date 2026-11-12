package ma.ensa.boutique.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

// BNF09 : UTF-8 de bout en bout. Déclaré en premier dans web.xml : il doit s'appliquer avant toute lecture des paramètres
public class EncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest requete, ServletResponse reponse, FilterChain chaine)
            throws IOException, ServletException {
        requete.setCharacterEncoding("UTF-8");
        reponse.setCharacterEncoding("UTF-8");
        chaine.doFilter(requete, reponse);
    }
}
