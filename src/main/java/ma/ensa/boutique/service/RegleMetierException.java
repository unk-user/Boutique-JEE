package ma.ensa.boutique.service;

// Levée quand une règle de gestion (RG) est violée ; le message est affiché à l'utilisateur
public class RegleMetierException extends RuntimeException {

    public RegleMetierException(String message) {
        super(message);
    }
}
