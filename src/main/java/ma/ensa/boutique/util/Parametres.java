package ma.ensa.boutique.util;

// Conversion des paramètres de requête : une valeur absente ou mal formée ne doit pas provoquer d'erreur 500
public final class Parametres {

    private Parametres() {
    }

    public static Long lireLong(String valeur) {
        try {
            return valeur == null || valeur.isBlank() ? null : Long.valueOf(valeur.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static int lireEntier(String valeur, int parDefaut) {
        try {
            return valeur == null || valeur.isBlank() ? parDefaut : Integer.parseInt(valeur.trim());
        } catch (NumberFormatException e) {
            return parDefaut;
        }
    }

    public static int nombrePages(long total, int taillePage) {
        return (int) Math.max(1, (total + taillePage - 1) / taillePage);
    }
}
