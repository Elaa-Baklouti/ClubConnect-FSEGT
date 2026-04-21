import java.util.List;

public class AdminService {

    // CF-14 : Voir tous les utilisateurs
    static void voirUtilisateurs() {
        if (!Session.isAdmin())
            throw new IllegalStateException("Droits admin requis.");
        System.out.println("Liste des utilisateurs :");
        for (User u : AuthService.users)
            System.out.println("  - " + u.username + " (" + u.role + ")");
    }

    // CF-15 : Supprimer post (admin)
    static void supprimerPost(int postId) {
        if (!Session.isAdmin())
            throw new IllegalStateException("Droits admin requis.");
        PostService.supprimerPost(postId);
    }
}