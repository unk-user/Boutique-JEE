package ma.ensa.boutique.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "produit")
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(length = 2000)
    private String description;

    // RG05 : prix > 0, vérifié dans le service
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;

    // RG05 : stock >= 0, vérifié dans le service
    @Column(nullable = false)
    private int stock;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // RG15 : un produit déjà commandé est désactivé au lieu d'être supprimé
    @Column(nullable = false)
    private boolean actif = true;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    // RG03
    @ManyToOne(optional = false)
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    public Produit() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }
}
