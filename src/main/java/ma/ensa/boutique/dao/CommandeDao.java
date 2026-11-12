package ma.ensa.boutique.dao;

import ma.ensa.boutique.model.Commande;
import ma.ensa.boutique.model.StatutCommande;

import org.hibernate.LockMode;
import org.hibernate.query.Query;

import java.util.List;

public class CommandeDao extends GenericDao<Commande> {

    public CommandeDao() {
        super(Commande.class);
    }

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
}
