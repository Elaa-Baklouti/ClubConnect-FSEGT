/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *
 *  ÉTAPE 1 — Squelette AGL :
 *    Classe utilitaire de gestion de la session courante.
 *    Champ statique → méthodes statiques métier
 *    Nommage français : ouvrirSession(), fermerSession(),
 *    estConnecte(), estAdmin()
 *
 *  ÉTAPE 2 — Implémentation IA assistée :
 *    Prompt utilisé :
 *      "Crée une classe Session Java avec un champ statique
 *       currentUser de type User. Ajoute login(User), logout(),
 *       isLoggedIn() et isAdmin(). isAdmin() retourne true si
 *       le rôle est 'admin'."
 *
 *    Code généré par l'IA :
 *      static void login(User u)   { currentUser = u; }
 *      static void logout()        { currentUser = null; }
 *      static boolean isLoggedIn() { return currentUser != null; }
 *      static boolean isAdmin()    { return currentUser != null
 *          && "admin".equals(currentUser.getRole()); }
 *
 *    Corrections humaines :
 *      - Ajout afficherDetails() pour debug
 *      - currentUser package-private pour compatibilité
 * ============================================================
 */
public class Session {

    // --- Champ ---
    static User currentUser;

    // --- Constructeur vide ---
    public Session() { }

    // --- Méthodes métier ---

    /** Ouvrir une session */
    public static void login(User user) {
        currentUser = user;
    }

    /** Fermer la session */
    public static void logout() {
        currentUser = null;
    }

    /** Vérifie si un utilisateur est connecté */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /** Vérifie si l'utilisateur courant est admin */
    public static boolean isAdmin() {
        return currentUser != null
            && "admin".equalsIgnoreCase(currentUser.getRole());
    }

    /** Afficher les détails de la session */
    public static void afficherDetails() {
        if (currentUser == null)
            System.out.println("=== Session === Aucune session active.");
        else {
            System.out.println("=== Session active ===");
            System.out.println("Utilisateur : " + currentUser.getUsername());
            System.out.println("Role        : " + currentUser.getRole());
        }
    }

    // --- toString ---
    @Override
    public String toString() {
        return currentUser == null
            ? "Session [aucune]"
            : "Session [" + currentUser.getUsername() + " / " + currentUser.getRole() + "]";
    }

    // --- Getters / Setters ---
    public static User getCurrentUser() { return currentUser; }
    public static void setCurrentUser(User user) { currentUser = user; }
}
