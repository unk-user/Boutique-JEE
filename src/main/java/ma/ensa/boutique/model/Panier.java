package ma.ensa.boutique.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

// Panier du client, conservé dans la HttpSession sous l'attribut « panier » (jamais persisté).
// Les règles (RG06, RG07) sont contrôlées par PanierService avant d'appeler ces méthodes.
public class Panier implements Serializable {

    private static final long serialVersionUID = 1L;

    // Clé : identifiant du produit ; LinkedHashMap pour garder l'ordre d'ajout
    private final Map<Long, LignePanier> lignes = new LinkedHashMap<>();

    public void ajouter(Produit produit, int quantite) {
        LignePanier ligne = lignes.get(produit.getId());
        if (ligne == null) {
            lignes.put(produit.getId(), new LignePanier(produit, quantite));
        } else {
            // On remplace la copie du produit par la version rechargée (prix et stock à jour)
            ligne.setProduit(produit);
            ligne.setQuantite(ligne.getQuantite() + quantite);
        }
    }

    public void modifierQuantite(Long produitId, int quantite) {
        LignePanier ligne = lignes.get(produitId);
        if (ligne != null) {
            ligne.setQuantite(quantite);
        }
    }

    public void retirer(Long produitId) {
        lignes.remove(produitId);
    }

    public void vider() {
        lignes.clear();
    }

    // Copie dans une ArrayList pour que les JSP (EL) puissent appeler size()
    public Collection<LignePanier> getLignes() {
        return new ArrayList<>(lignes.values());
    }

    public int getQuantite(Long produitId) {
        LignePanier ligne = lignes.get(produitId);
        return ligne == null ? 0 : ligne.getQuantite();
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (LignePanier ligne : lignes.values()) {
            total = total.add(ligne.getSousTotal());
        }
        return total;
    }

    public boolean estVide() {
        return lignes.isEmpty();
    }
}
