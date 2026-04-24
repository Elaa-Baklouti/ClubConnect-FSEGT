import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *  DOCUMENTATION TP3 — Approche IA assistée
 * ============================================================
 *
 *  MÉTHODE : inscrire()
 *  --------------------------------
 *  Prompt utilisé :
 *    "Implémente inscrire(username, email, password) dans
 *     AuthService. Valide que le username n'est pas vide, que
 *     l'email contient '@' et '.', que le mot de passe fait
 *     au moins 6 caractères, et que l'email n'est pas déjà
 *     utilisé. Crée et retourne le User."
 *
 *  Code généré par l'IA :
 *    static User inscrire(String u, String e, String p) {
 *        if (u.isBlank()) throw new IllegalArgumentException("...");
 *        if (!e.contains("@")) throw new IllegalArgumentException("...");
 *        if (p.length() < 6) throw new IllegalArgumentException("...");
 *        User user = new User(nextId++, u, e, p);
 *        users.add(user);
 *        return user;
 *    }
 *
 *  Corrections humaines :
 *    - Vérification unicité email (doublon interdit)
 *    - Validation email : présence de '.' en plus de '@'
 *    - Extraction trouverParEmail() utilitaire privé
 * ============================================================
 *
 *  MÉTHODE : seConnecter()
 *  --------------------------------
 *  Prompt utilisé :
 *    "Implémente seConnecter(email, password) dans AuthService.
 *     Cherche l'utilisateur par email, compare le mot de passe,
 *     ouvre la session via Session.login() et retourne le User.
 *     Lance IllegalStateException si les identifiants sont
 *     incorrects."
 *
 *  Code généré par l'IA :
 *    static User seConnecter(String email, String password) {
 *        for (User u : users)
 *            if (u.getEmail().equals(email) && u.getPassword().equals(password)) {
 *                Session.login(u);
 *                return u;
 *            }
 *        throw new IllegalStateException("Identifiants incorrects.");
 *    }
 *
 *  Corrections humaines :
 *    - Comparaison email insensible à la casse (equalsIgnoreCase)
 *    - Séparation recherche et vérification mot de passe
 *      pour message d'erreur plus précis
 * ============================================================
 */
public class AuthService {

    // --- Champs ---
    static List<User> users = new ArrayList<User>();
    static int nextId = 1;

    // --- Constructeur vide ---
    public AuthService() { }

    // ============================================================
    //  MÉTHODES MÉTIER — Logique réelle
    // ============================================================

    /**
     * CF-1 : Inscrire un nouveau membre.
     * Valide username, email (unicité + format) et mot de passe.
     */
    public static User inscrire(String username, String email, String password) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Le username est obligatoire.");
        if (email == null || !email.contains("@") || !email.contains("."))
            throw new IllegalArgumentException("Email invalide.");
        if (password == null || password.length() < 6)
            throw new IllegalArgumentException("Mot de passe trop court (6 caractères min).");
        if (trouverParEmail(email) != null)
            throw new IllegalStateException("Un compte existe déjà avec cet email.");

        User user = new User(nextId++, username, email, password);
        users.add(user);
        return user;
    }

    /**
     * CF-2 : Connecter un membre existant.
     * Recherche par email (insensible à la casse) + vérification mot de passe.
     */
    public static User seConnecter(String email, String password) {
        User user = trouverParEmail(email);
        if (user == null || !user.getPassword().equals(password))
            throw new IllegalStateException("Email ou mot de passe incorrect.");
        Session.login(user);
        return user;
    }

    /**
     * CF-2b : Déconnecter le membre courant.
     */
    public static void seDeconnecter() {
        Session.logout();
    }

    /**
     * Afficher tous les membres inscrits.
     */
    public static void afficherDetails() {
        System.out.println("=== Membres inscrits (" + users.size() + ") ===");
        for (User u : users)
            System.out.println("  - " + u);
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

    // --- Utilitaire privé ---
    private static User trouverParEmail(String email) {
        for (User u : users)
            if (u.getEmail().equalsIgnoreCase(email)) return u;
        return null;
    }

    // --- Alias compatibilité ---
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
