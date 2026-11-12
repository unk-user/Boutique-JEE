package ma.ensa.boutique.dao;

import ma.ensa.boutique.model.Commande;
import ma.ensa.boutique.model.StatutCommande;

import org.hibernate.LockMode;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.List;

// sauvegarder() (GenericDao) enregistre la commande et ses lignes (cascade ALL)
// Les listes paginées ne chargent pas les lignes (lazy) : seul trouverAvecLignes() les récupère
public class CommandeDao extends GenericDao<Commande> {

    public CommandeDao() {
        super(Commande.class);
    }

    // Plus récentes d'abord
    public List<Commande> listerParUtilisateur(Long utilisateurId, int debut, int taille) {
        return session()
                .createQuery("from Commande c where c.utilisateur.id = :utilisateurId"
                        + " order by c.dateCommande desc, c.id desc", Commande.class)
                .setParameter("utilisateurId", utilisateurId)
                .setFirstResult(debut)
                .setMaxResults(taille)
                .getResultList();
    }

    public long compterParUtilisateur(Long utilisateurId) {
        return session()
                .createQuery("select count(c) from Commande c where c.utilisateur.id = :utilisateurId", Long.class)
                .setParameter("utilisateurId", utilisateurId)
                .uniqueResult();
    }

    // statut null : toutes les commandes. Le client est chargé dans la même requête (affiché dans la liste)
    public List<Commande> listerParStatut(StatutCommande statut, int debut, int taille) {
        Query<Commande> requete = session().createQuery(
                "select c from Commande c join fetch c.utilisateur"
                        + (statut == null ? "" : " where c.statut = :statut")
                        + " order by c.dateCommande desc, c.id desc", Commande.class);
        if (statut != null) {
            requete.setParameter("statut", statut);
        }
        return requete.setFirstResult(debut).setMaxResults(taille).getResultList();
    }

    public long compterParStatut(StatutCommande statut) {
        Query<Long> requete = session().createQuery(
                "select count(c) from Commande c" + (statut == null ? "" : " where c.statut = :statut"), Long.class);
        if (statut != null) {
            requete.setParameter("statut", statut);
        }
        return requete.uniqueResult();
    }

    // Page de détail : lignes et produits chargés en une requête (la session est fermée dans la JSP)
    public Commande trouverAvecLignes(Long id) {
        if (id == null) {
            return null;
        }
        return session()
                .createQuery("select distinct c from Commande c join fetch c.lignes l join fetch l.produit"
                        + " where c.id = :id", Commande.class)
                .setParameter("id", id)
                .uniqueResult();
    }

    // Changement de statut (RG13, RG14) : verrou en écriture sur la ligne de la commande.
    // Deux changements simultanés sur la même commande sont traités l'un après l'autre :
    // le second voit le statut déjà modifié, ce qui empêche de réintégrer le stock deux fois.
    public Commande trouverEtVerrouiller(Long id) {
        if (id == null) {
            return null;
        }
        return session()
                .createQuery("from Commande c where c.id = :id", Commande.class)
                .setParameter("id", id)
                .setHibernateLockMode(LockMode.PESSIMISTIC_WRITE)
                .uniqueResult();
    }

    // BF23 : somme des montants des commandes non annulées (0 s'il n'y en a aucune)
    public BigDecimal calculerChiffreAffaires() {
        return session()
                .createQuery("select coalesce(sum(c.montantTotal), 0) from Commande c where c.statut <> :annulee",
                        BigDecimal.class)
                .setParameter("annulee", StatutCommande.ANNULEE)
                .uniqueResult();
    }
}
