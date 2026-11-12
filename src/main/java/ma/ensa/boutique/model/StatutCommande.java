package ma.ensa.boutique.model;

// Le libellé français est celui affiché dans les pages et les messages
public enum StatutCommande {
    EN_ATTENTE("En attente"),
    VALIDEE("Validée"),
    EXPEDIEE("Expédiée"),
    LIVREE("Livrée"),
    ANNULEE("Annulée");

    private final String libelle;

    StatutCommande(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
