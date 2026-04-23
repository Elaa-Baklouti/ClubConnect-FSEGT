/**
 * Représente un membre inscrit sur ClubConnect.
 * Prompt IA utilisé : "génère constructeur vide, complet, getters/setters et toString pour cette classe"
 */
public class User {

    // --- Champs privés ---
    private int id;
    private String username;
    private String email;
    private String password;
    private String role;

    // --- Constructeur vide ---
    public User() {
        this.role = "user";
    }

    // --- Constructeur complet ---
    public User(int id, String username, String email, String password) {
        this.id       = id;
        this.username = username;
        this.email    = email;
        this.password = password;
        this.role     = "user";
    }

    // --- Méthodes métier ---

    /** CF-1 : Connexion du membre */
    public void seConnecter() {
        System.out.println(username + " s'est connecté.");
    }

    /** CF-2 : Déconnexion du membre */
    public void seDeconnecter() {
        System.out.println(username + " s'est déconnecté.");
    }

    /** Afficher le profil du membre */
    public void afficherDetails() {
        System.out.println("=== Profil Membre ===");
        System.out.println("ID       : " + id);
        System.out.println("Username : " + username);
        System.out.println("Email    : " + email);
        System.out.println("Rôle     : " + role);
    }

    // --- toString ---
    @Override
    public String toString() {
        return "User#" + id + " [" + username + "] (" + role + ")";
    }

    // --- Getters / Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
