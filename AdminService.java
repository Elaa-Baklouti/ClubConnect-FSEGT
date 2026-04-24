import java.util.List;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *
 *  ÉTAPE 1 — Squelette AGL :
 *    Champs privés → constructeur vide → méthodes métier
 *    (signatures) → getters/setters
 *    Nommage français : voirUtilisateurs(), supprimerMembre(),
 *    supprimerPost(), afficherDetails()
 *
 *  ÉTAPE 2 — Implémentation IA assistée :
 *    Prompt utilisé :
 *      "Crée un AdminService Java avec les méthodes
 *       voirUtilisateurs() et supprimerPost(id). Chaque méthode
 *       vérifie que l'utilisateur courant est admin via Session,
 *       sinon lance une IllegalStateException."
 *
 *    Code généré par l'IA :
 *      static void voirUtilisateurs() {
 *          if (!Session.isAdmin())
 *              throw new IllegalStateException("Admin requis.");
 *          for (User u : AuthService.users)
 *              System.out.println(u);
 *      }
 *
 *    Corrections humaines :
 *      - Accès via getters (encapsulation)
 *      - Ajout supprimerMembre() avec anti-auto-suppression
 *      - Ajout afficherDetails() pour tableau de bord
 * ============================================================
 */
public class AdminService {

    // --- Constructeur vide ---
    public AdminService() { }

    // --- Méthodes métier ---

    /** CF-14 : Lister tous les membres inscrits */
    public static void voirUtilisateurs() {
        if (!Session.isAdmin())
            throw new IllegalStateException("Droits admin requis.");
        List<User> membres = AuthService.users;
        System.out.println("=== Liste des membres (" + membres.size() + ") ===");
        for (User u : membres)
            System.out.println("  - " + u.getUsername()
                + " | " + u.getEmail()
                + " | role=" + u.getRole()
                + " | solde=" + u.getSolde() + " DT");
    }

    /** CF-15 : Supprimer un post par son ID */
    public static void supprimerPost(int postId) {
        if (!Session.isAdmin())
            throw new IllegalStateException("Droits admin requis.");
        PostService.supprimerPost(postId);
        System.out.println("Post#" + postId + " supprime par l'admin.");
    }

    /** Supprimer un membre par son ID */
    public static void supprimerMembre(int membreId) {
        if (!Session.isAdmin())
            throw new IllegalStateException("Droits admin requis.");
        User admin = Session.currentUser;
        if (admin != null && admin.getId() == membreId)
            throw new IllegalStateException("Un admin ne peut pas supprimer son propre compte.");
        List<User> membres = AuthService.users;
        for (User u : membres) {
            if (u.getId() == membreId) {
                membres.remove(u);
                System.out.println("Membre '" + u.getUsername() + "' supprime.");
                return;
            }
        }
        throw new IllegalArgumentException("Membre#" + membreId + " introuvable.");
    }

    /** Afficher le tableau de bord admin */
    public static void afficherDetails() {
        if (!Session.isAdmin())
            throw new IllegalStateException("Droits admin requis.");
        System.out.println("=== Tableau de bord Admin ===");
        System.out.println("Membres inscrits : " + AuthService.users.size());
        System.out.println("Connecte en tant que : " + Session.currentUser.getUsername());
    }

    // --- toString ---
    @Override
    public String toString() {
        return "AdminService | session=" + (Session.isAdmin() ? "admin actif" : "non admin");
    }
}
