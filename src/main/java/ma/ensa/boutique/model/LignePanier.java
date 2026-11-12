package ma.ensa.boutique.model;

import java.io.Serializable;
import java.math.BigDecimal;

// Ligne du panier en session : jamais persistée
public class LignePanier implements Serializable {

    private static final long serialVersionUID = 1L;

    private Produit produit;
    private int quantite;

    public LignePanier(Produit produit, int quantite) {
        this.produit = produit;
        this.quantite = quantite;
    }

    public Produit getProduit() { return produit; }
    public void setProduit(Produit produit) { this.produit = produit; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public BigDecimal getSousTotal() {
        return produit.getPrix().multiply(BigDecimal.valueOf(quantite));
    }
}
