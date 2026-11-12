package ma.ensa.boutique.service;

import ma.ensa.boutique.dao.CategorieDao;
import ma.ensa.boutique.dao.ProduitDao;
import ma.ensa.boutique.model.Categorie;
import ma.ensa.boutique.model.Produit;
import ma.ensa.boutique.util.HibernateUtil;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Supplier;

public class CatalogueService {

    public static final int TAILLE_PAGE = 10;

    // Limite imposée par la colonne prix DECIMAL(10,2)
    private static final BigDecimal PRIX_MAX = new BigDecimal("99999999.99");

    private final CategorieDao categorieDao = new CategorieDao();
    private final ProduitDao produitDao = new ProduitDao();

    // ---------- Catalogue public (BF05 à BF08) ----------

    // RG15 : seuls les produits actifs sont proposés ; RG06 : le stock 0 reste visible
    public List<Produit> listerProduits(Long categorieId, String motCle, int page) {
        String mot = nettoyer(motCle);
        return enTransaction(() -> produitDao.rechercher(categorieId, mot, debut(page), TAILLE_PAGE));
    }

    public long compterProduits(Long categorieId, String motCle) {
        String mot = nettoyer(motCle);
        return enTransaction(() -> produitDao.compterRecherche(categorieId, mot));
    }

    // RG15 : la fiche d'un produit désactivé est introuvable (retourne null)
    public Produit trouverProduitActif(Long id) {
        return enTransaction(() -> produitDao.trouverActifParId(id));
    }

    // ---------- Catégories (BF17) ----------

    public List<Categorie> listerCategories() {
        return enTransaction(categorieDao::listerTous);
    }

    public Categorie trouverCategorie(Long id) {
        return enTransaction(() -> categorieDao.trouverParId(id));
    }

    public void enregistrerCategorie(Categorie categorie) {
        if (estVide(categorie.getNom())) {
            throw new RegleMetierException("Le nom de la catégorie est obligatoire.");
        }
        categorie.setNom(categorie.getNom().trim());
        categorie.setDescription(nettoyer(categorie.getDescription()));
        if (categorie.getNom().length() > 100 || longueur(categorie.getDescription()) > 500) {
            throw new RegleMetierException("Le nom (100 caractères) ou la description (500 caractères) est trop long.");
        }

        enTransaction(() -> {
            Categorie homonyme = categorieDao.trouverParNom(categorie.getNom());
            if (homonyme != null && !homonyme.getId().equals(categorie.getId())) {
                throw new RegleMetierException("Une catégorie porte déjà ce nom.");
            }
            if (categorie.getId() == null) {
                categorieDao.sauvegarder(categorie);
            } else {
                Categorie existante = categorieDao.trouverParId(categorie.getId());
                if (existante == null) {
                    throw new RegleMetierException("Catégorie introuvable.");
                }
                existante.setNom(categorie.getNom());
                existante.setDescription(categorie.getDescription());
            }
            return null;
        });
    }

    public void supprimerCategorie(Long id) {
        enTransaction(() -> {
            Categorie categorie = categorieDao.trouverParId(id);
            if (categorie == null) {
                throw new RegleMetierException("Catégorie introuvable.");
            }
            // RG04
            if (categorieDao.compterProduits(id) > 0) {
                throw new RegleMetierException("Cette catégorie contient des produits : elle ne peut pas être supprimée.");
            }
            categorieDao.supprimer(categorie);
            return null;
        });
    }

    // ---------- Produits (BF18, BF19) ----------

    public List<Produit> listerTousProduits(int page) {
        return enTransaction(() -> produitDao.listerPagine(debut(page), TAILLE_PAGE));
    }

    public long compterTousProduits() {
        return enTransaction(produitDao::compter);
    }

    public Produit trouverProduit(Long id) {
        return enTransaction(() -> produitDao.trouverParId(id));
    }

    public void enregistrerProduit(Produit produit, Long categorieId) {
        verifierProduit(produit);

        enTransaction(() -> {
            // RG03 : un produit appartient à une et une seule catégorie
            Categorie categorie = categorieId == null ? null : categorieDao.trouverParId(categorieId);
            if (categorie == null) {
                throw new RegleMetierException("Veuillez choisir une catégorie.");
            }
            if (produit.getId() == null) {
                produit.setCategorie(categorie);
                produitDao.sauvegarder(produit);
            } else {
                // On modifie le produit chargé pour conserver la date de création
                Produit existant = produitDao.trouverParId(produit.getId());
                if (existant == null) {
                    throw new RegleMetierException("Produit introuvable.");
                }
                existant.setNom(produit.getNom());
                existant.setDescription(produit.getDescription());
                existant.setPrix(produit.getPrix());
                existant.setStock(produit.getStock());
                existant.setImageUrl(produit.getImageUrl());
                existant.setActif(produit.isActif());
                existant.setCategorie(categorie);
            }
            return null;
        });
    }

    // Retourne vrai si le produit a été supprimé, faux s'il a seulement été désactivé
    public boolean supprimerProduit(Long id) {
        return enTransaction(() -> {
            Produit produit = produitDao.trouverParId(id);
            if (produit == null) {
                throw new RegleMetierException("Produit introuvable.");
            }
            // RG15 : un produit déjà commandé est désactivé, pas supprimé
            if (produitDao.estCommande(id)) {
                produit.setActif(false);
                return false;
            }
            produitDao.supprimer(produit);
            return true;
        });
    }

    public void modifierStock(Long id, int stock) {
        // RG05
        if (stock < 0) {
            throw new RegleMetierException("Le stock doit être un entier supérieur ou égal à 0.");
        }
        enTransaction(() -> {
            Produit produit = produitDao.trouverParId(id);
            if (produit == null) {
                throw new RegleMetierException("Produit introuvable.");
            }
            produit.setStock(stock);
            return null;
        });
    }

    // ---------- Outils ----------

    private void verifierProduit(Produit produit) {
        if (estVide(produit.getNom())) {
            throw new RegleMetierException("Le nom du produit est obligatoire.");
        }
        produit.setNom(produit.getNom().trim());
        produit.setDescription(nettoyer(produit.getDescription()));
        produit.setImageUrl(nettoyer(produit.getImageUrl()));
        if (produit.getNom().length() > 150 || longueur(produit.getDescription()) > 2000
                || longueur(produit.getImageUrl()) > 500) {
            throw new RegleMetierException("Un des champs saisis est trop long.");
        }
        // RG05 : prix strictement positif (après arrondi au centime), stock >= 0
        if (produit.getPrix() != null) {
            produit.setPrix(produit.getPrix().setScale(2, RoundingMode.HALF_UP));
        }
        if (produit.getPrix() == null || produit.getPrix().signum() <= 0) {
            throw new RegleMetierException("Le prix doit être strictement positif.");
        }
        if (produit.getPrix().compareTo(PRIX_MAX) > 0) {
            throw new RegleMetierException("Le prix est trop élevé.");
        }
        if (produit.getStock() < 0) {
            throw new RegleMetierException("Le stock doit être un entier supérieur ou égal à 0.");
        }
    }

    private int debut(int page) {
        return (Math.max(page, 1) - 1) * TAILLE_PAGE;
    }

    // Exécute le traitement dans une transaction : validation si tout va bien, annulation sinon
    private <R> R enTransaction(Supplier<R> traitement) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        try {
            R resultat = traitement.get();
            session.getTransaction().commit();
            return resultat;
        } catch (RuntimeException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    private String nettoyer(String valeur) {
        return estVide(valeur) ? null : valeur.trim();
    }

    private int longueur(String valeur) {
        return valeur == null ? 0 : valeur.length();
    }

    private boolean estVide(String valeur) {
        return valeur == null || valeur.isBlank();
    }
}
