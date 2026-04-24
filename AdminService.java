import java.util.List;

/**
 * ============================================================
 *  DOCUMENTATION TP3 — Approche IA assistée
 * ============================================================
 *
 *  MÉTHODE : voirUtilisateurs()
 *  --------------------------------
 *  Prompt utilisé :
 *    "Implémente voirUtilisateurs() dans AdminService. Elle doit
 *     vérifier que l'utilisateur courant est admin via Session,
 *     puis afficher la liste complète des membres avec username,
 *     email, rôle et solde en DT. Lance IllegalStateException
 *     si non admin."
 *
 *  Code généré par l'IA :
 *    static void voirUtilisateurs() {
 *        if (!Session.isAdmin())
 *            throw new IllegalStateException("Admin requis.");
 *        for (User u : AuthService.users)
 *            System.out.println(u.getUsername() + " " + u.getRole());
 *    }
 *
 *  Corrections humaines :
 *    - Affichage enrichi : email + solde en DT
 *    - Comptage total membres
 *    - Extraction verifierAdmin() pour réutilisation
 * ============================================================
 *
 *  MÉTHODE : supprimerMembre()
 *  --------------------------------
 *  Prompt utilisé :
 *    "Implémente supprimerMembre(int membreId) dans AdminService.
 *     Vérifie les droits admin, empêche l'auto-suppression,
 *     cherche le membre par ID dans AuthService.users et le
 *     supprime. Lance des exceptions métier appropriées."
 *
 *  Code généré par l'IA :
 *    static void supprimerMembre(int id) {
 *        if (!Session.isAdmin())
 *            throw new IllegalStateException("Admin requis.");
 *        AuthService.users.removeIf(u -> u.getId() == id);
 *    }
 *
 *  Corrections humaines :
 *    - Ajout vérification anti-auto-suppression
 *    - Ajout exception si membre introuvable
 *    - Retour du membre supprimé pour confirmation
 * ============================================================
 *
 *  MÉTHODE : changerRole()
 *  --------------------------------
 *  Prompt utilisé :
 *    "Implémente changerRole(int membreId, String nouveauRole)
 *     dans AdminService. Valide que le rôle est 'user' ou 'admin',
 *     retrouve le membre par ID et met à jour son rôle."
 *
 *  Code généré par l'IA :
 *    static void changerRole(int id, String role) {
 *        trouverMembre(id).setRole(role);
 *    }
 *
 *  Corrections humaines :
 *    - Validation de la valeur du rôle (whitelist)
 *    - Vérification droits admin
 *    - Empêcher de rétrograder le dernier admin
 * ============================================================
 */
public class AdminService {

    // --- Constructeur vide ---
    public AdminService() { }

    // ============================================================
    //  MÉTHODES MÉTIER — Logique réelle
    // ============================================================

    /**
     * CF-14 : Lister tous les membres inscrits.
     * Affiche username, email, rôle et solde en DT.
     */
    public static void voirUtilisateurs() {
        verifierAdmin();
        List<User> membres = AuthService.users;
        System.out.println("=== Liste des membres (" + membres.size() + ") ===");
        if (membres.isEmpty()) {
            System.out.println("  Aucun membre inscrit.");
            return;
        }
        for (User u : membres) {
            System.out.println("  - " + u.getUsername()
                + " | " + u.getEmail()
                + " | role=" + u.getRole()
                + " | solde=" + u.getSolde() + " DT");
        }
    }

    /**
     * CF-15 : Supprimer un post par son ID.
     * Délègue à PostService après vérification des droits.
     */
    public static void supprimerPost(int postId) {
        verifierAdmin();
        PostService.supprimerPost(postId);
    }

    /**
     * Supprimer un membre par son ID.
     * Règles :
     *  - Droits admin requis
     *  - Un admin ne peut pas supprimer son propre compte
     *  - Le membre doit exister
     */
    public static void supprimerMembre(int membreId) {
        verifierAdmin();

        User admin = Session.currentUser;
        if (admin != null && admin.getId() == membreId)
            throw new IllegalStateException("Un admin ne peut pas supprimer son propre compte.");

        User cible = trouverMembre(membreId);
        AuthService.users.remove(cible);
    }

    /**
     * Changer le rôle d'un membre (user ↔ admin).
     * Règles :
     *  - Droits admin requis
     *  - Rôle doit être "user" ou "admin"
     *  - Impossible de rétrograder le dernier admin
     */
    public static void changerRole(int membreId, String nouveauRole) {
        verifierAdmin();

        if (!"user".equals(nouveauRole) && !"admin".equals(nouveauRole))
            throw new IllegalArgumentException("Rôle invalide. Valeurs acceptées : 'user', 'admin'.");

        User cible = trouverMembre(membreId);

        // Empêcher de rétrograder le dernier admin
        if ("user".equals(nouveauRole) && "admin".equals(cible.getRole())) {
            long nbAdmins = AuthService.users.stream()
                .filter(u -> "admin".equals(u.getRole()))
                .count();
            if (nbAdmins <= 1)
                throw new IllegalStateException("Impossible : ce membre est le dernier administrateur.");
        }

        cible.setRole(nouveauRole);
    }

    /**
     * Afficher le tableau de bord administrateur.
     * Montre le nombre de membres et le solde total en DT.
     */
    public static void afficherDetails() {
        verifierAdmin();
        User admin = Session.currentUser;
        double soldeTotalMembres = AuthService.users.stream()
            .mapToDouble(User::getSolde)
            .sum();
        long nbAdmins = AuthService.users.stream()
            .filter(u -> "admin".equals(u.getRole()))
            .count();

        System.out.println("=== Tableau de bord Admin ===");
        System.out.println("Connecté en tant que : " + (admin != null ? admin.getUsername() : "N/A"));
        System.out.println("Membres inscrits      : " + AuthService.users.size());
        System.out.println("Dont administrateurs  : " + nbAdmins);
        System.out.println("Solde total membres   : " + String.format("%.3f", soldeTotalMembres) + " DT");
    }

    // --- toString ---
    @Override
    public String toString() {
        return "AdminService | session=" + (Session.isAdmin() ? "admin actif" : "non admin");
    }

    // ============================================================
    //  Utilitaires privés
    // ============================================================

    /** Vérifie que la session courante est celle d'un admin. */
    private static void verifierAdmin() {
        if (!Session.isAdmin())
            throw new IllegalStateException("Droits administrateur requis.");
    }

    /** Retrouve un membre par son ID, lance une exception si introuvable. */
    private static User trouverMembre(int id) {
        for (User u : AuthService.users)
            if (u.getId() == id) return u;
        throw new IllegalArgumentException("Membre#" + id + " introuvable.");
    }
}
