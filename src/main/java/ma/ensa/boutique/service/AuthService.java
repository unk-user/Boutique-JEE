package ma.ensa.boutique.service;

import ma.ensa.boutique.dao.UtilisateurDao;
import ma.ensa.boutique.model.Role;
import ma.ensa.boutique.model.Utilisateur;
import ma.ensa.boutique.util.HibernateUtil;
import org.hibernate.Session;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class AuthService {

    private static final Pattern FORMAT_EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final int LONGUEUR_MIN_MOT_DE_PASSE = 6;
    public static final int TAILLE_PAGE = 10;

    private final UtilisateurDao utilisateurDao = new UtilisateurDao();

    public Utilisateur inscrire(Utilisateur utilisateur, String motDePasse) {
        verifierChamps(utilisateur, motDePasse);
        utilisateur.setNom(utilisateur.getNom().trim());
        utilisateur.setPrenom(utilisateur.getPrenom().trim());
        utilisateur.setEmail(utilisateur.getEmail().trim().toLowerCase());
        utilisateur.setAdresse(nettoyer(utilisateur.getAdresse()));
        utilisateur.setTelephone(nettoyer(utilisateur.getTelephone()));

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        try {
            // RG01
            if (utilisateurDao.trouverParEmail(utilisateur.getEmail()) != null) {
                throw new RegleMetierException("Cette adresse e-mail est déjà utilisée.");
            }
            // RG02
            utilisateur.setRole(Role.CLIENT);
            utilisateur.setActif(true);
            utilisateur.setMotDePasse(BCrypt.hashpw(motDePasse, BCrypt.gensalt()));
            utilisateurDao.sauvegarder(utilisateur);
            session.getTransaction().commit();
            return utilisateur;
        } catch (RuntimeException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    public Utilisateur authentifier(String email, String motDePasse) {
        if (estVide(email) || estVide(motDePasse)) {
            throw new RegleMetierException("Veuillez saisir votre e-mail et votre mot de passe.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Utilisateur utilisateur;
        try {
            utilisateur = utilisateurDao.trouverParEmail(email.trim().toLowerCase());
            session.getTransaction().commit();
        } catch (RuntimeException e) {
            session.getTransaction().rollback();
            throw e;
        }
        if (utilisateur == null || !BCrypt.checkpw(motDePasse, utilisateur.getMotDePasse())) {
            throw new RegleMetierException("E-mail ou mot de passe incorrect.");
        }
        if (!utilisateur.isActif()) {
            throw new RegleMetierException("Ce compte est désactivé.");
        }
        return utilisateur;
    }

    public List<Utilisateur> listerUtilisateurs(int page) {
        return enTransaction(() -> utilisateurDao.listerPagine((Math.max(page, 1) - 1) * TAILLE_PAGE, TAILLE_PAGE));
    }

    public long compterUtilisateurs() {
        return enTransaction(utilisateurDao::compter);
    }

    public void changerEtatCompte(Long id, boolean actif) {
        enTransaction(() -> {
            Utilisateur utilisateur = utilisateurDao.trouverParId(id);
            if (utilisateur == null) {
                throw new RegleMetierException("Utilisateur introuvable.");
            }
            if (!actif && utilisateur.getRole() == Role.ADMIN) {
                throw new RegleMetierException("Un compte administrateur ne peut pas être désactivé.");
            }
            utilisateur.setActif(actif);
            return null;
        });
    }

    private void verifierChamps(Utilisateur utilisateur, String motDePasse) {
        if (estVide(utilisateur.getNom()) || estVide(utilisateur.getPrenom())
                || estVide(utilisateur.getEmail()) || estVide(motDePasse)) {
            throw new RegleMetierException("Le nom, le prénom, l'e-mail et le mot de passe sont obligatoires.");
        }
        if (!FORMAT_EMAIL.matcher(utilisateur.getEmail().trim()).matches()) {
            throw new RegleMetierException("L'adresse e-mail n'est pas valide.");
        }
        if (motDePasse.length() < LONGUEUR_MIN_MOT_DE_PASSE) {
            throw new RegleMetierException("Le mot de passe doit contenir au moins "
                    + LONGUEUR_MIN_MOT_DE_PASSE + " caractères.");
        }
        if (depasse(utilisateur.getNom(), 100) || depasse(utilisateur.getPrenom(), 100)
                || depasse(utilisateur.getEmail(), 150) || depasse(utilisateur.getAdresse(), 255)
                || depasse(utilisateur.getTelephone(), 20)) {
            throw new RegleMetierException("Un des champs saisis est trop long.");
        }
    }

    private boolean depasse(String valeur, int longueurMax) {
        return valeur != null && valeur.trim().length() > longueurMax;
    }

    private String nettoyer(String valeur) {
        return estVide(valeur) ? null : valeur.trim();
    }

    private boolean estVide(String valeur) {
        return valeur == null || valeur.isBlank();
    }

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
}
