package ma.ensa.boutique.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

public class EncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest requete, ServletResponse reponse, FilterChain chaine)
            throws IOException, ServletException {
        requete.setCharacterEncoding("UTF-8");
        reponse.setCharacterEncoding("UTF-8");
        chaine.doFilter(requete, reponse);
    }
}
