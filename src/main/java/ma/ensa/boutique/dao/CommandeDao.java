package ma.ensa.boutique.dao;

import ma.ensa.boutique.model.Commande;

public class CommandeDao extends GenericDao<Commande> {

    public CommandeDao() {
        super(Commande.class);
    }
}
