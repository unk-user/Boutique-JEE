package ma.ensa.boutique.dao;

import ma.ensa.boutique.model.Commande;

// sauvegarder() (GenericDao) enregistre la commande et ses lignes (cascade ALL)
public class CommandeDao extends GenericDao<Commande> {

    public CommandeDao() {
        super(Commande.class);
    }
}
