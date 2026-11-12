package ma.ensa.boutique.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;

// BNF09 : UTF-8 de bout en bout (doit s'appliquer avant toute lecture des paramètres)
@WebFilter("/*")
public class EncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest requete, ServletResponse reponse, FilterChain chaine)
            throws IOException, ServletException {
        requete.setCharacterEncoding("UTF-8");
        reponse.setCharacterEncoding("UTF-8");
        chaine.doFilter(requete, reponse);
    }
}
