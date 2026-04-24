import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *
 *  ÉTAPE 1 — Squelette AGL :
 *    Service d'authentification — inscription, connexion,
 *    déconnexion des membres.
 *    Champs privés → constructeur vide → méthodes métier
 *    (signatures) → getters/setters
 *    Nommage français : inscrire(), seConnecter(),
 *    seDeconnecter(), afficherDetails()
 *
 *  ÉTAPE 2 — Implémentation IA assistée :
 *    Prompt utilisé :
 *      "Implémente un AuthService Java avec une liste statique
 *       d'utilisateurs. Méthodes : inscription(username, email,
 *       password) qui valide les champs et crée un User, et
 *       login(email, password) qui cherche l'utilisateur et
 *       ouvre la session. Lancer des exceptions si les règles
 *       sont violées."
 *
 *    Code généré par l'IA :
 *      static User inscription(String u, String e, String p) {
 *          // validation + new User + users.add()
 *      }
 *      static User login(String email, String password) {
 *          // recherche + Session.login()
 *      }
 *
 *    Corrections humaines :
 *      - Vérification unicité email
 *      - Extraction trouverParEmail() utilitaire privé
 *      - Ajout afficherDetails()
 * ============================================================
 */
public class AuthService {

    // --- Champs ---
    static List<User> users = new ArrayList<>();
    static int nextId = 1;

    // --- Constructeur vide ---
    public AuthService() { }

    // --- Méthodes métier ---

    /** CF-1 : Inscrire un nouveau membre */
    public static User inscrire(String username, String email, String password) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username obligatoire.");
        if (email == null || !email.contains("@") || !email.contains("."))
            throw new IllegalArgumentException("Email invalide.");
        if (password == null || password.length() < 6)
            throw new IllegalArgumentException("Mot de passe trop court (6 min).");
        if (trouverParEmail(email) != null)
            throw new IllegalStateException("Email deja utilise.");

        User user = new User(nextId++, username, email, password);
        users.add(user);
        System.out.println("Inscription OK : " + username);
        return user;
    }

    /** CF-2 : Connecter un membre */
    public static User seConnecter(String email, String password) {
        User user = trouverParEmail(email);
        if (user == null || !user.getPassword().equals(password))
            throw new IllegalStateException("Email ou mot de passe incorrect.");
        Session.login(user);
        System.out.println("Connecte : " + user.getUsername());
        return user;
    }

    /** CF-2b : Déconnecter le membre courant */
    public static void seDeconnecter() {
        Session.logout();
        System.out.println("Deconnexion effectuee.");
    }

    /** Afficher tous les membres */
    public static void afficherDetails() {
        System.out.println("=== Membres inscrits (" + users.size() + ") ===");
        for (User u : users)
            System.out.println("  - " + u);
    }

    // --- Utilitaire privé ---
    private static User trouverParEmail(String email) {
        for (User u : users)
            if (u.getEmail().equalsIgnoreCase(email)) return u;
        return null;
    }

    // --- toString ---
    @Override
    public String toString() {
        return "AuthService | membres=" + users.size();
    }

    // --- Getters / Setters ---
    public static List<User> getUsers() { return users; }
    public static void setUsers(List<User> u) { users = u; }

    public static int getNextId() { return nextId; }
    public static void setNextId(int id) { nextId = id; }

    // --- Alias compatibilité ancien code ---
    public static User inscription(String username, String email, String password) {
        return inscrire(username, email, password);
    }
    public static User login(String email, String password) {
        return seConnecter(email, password);
    }
    public static void logout() {
        seDeconnecter();
    }
}
