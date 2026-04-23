import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *  ÉTAPE 1 — Squelette AGL (déterministe) :
 *    Service interactions sociales : commentaires et likes.
 *    Pattern : champs privés → constructeurs → méthodes métier
 *    → getters/setters
 *    Nommage français : commenter(), liker(),
 *    afficherCommentaires(), afficherDetailsPost()
 *
 *  ÉTAPE 2 — Implémentation IA assistée :
 *    Prompt utilisé :
 *      "Crée un InteractionService Java avec PostService injecté.
 *       Méthodes : commenter(postId, user, texte),
 *       liker(postId, user), afficherCommentaires(postId)."
 *    Corrections humaines :
 *      - Injection par constructeur (pas de static)
 *      - Filtre marqueurs [like:] dans afficherCommentaires()
 * ============================================================
 */
public class InteractionService {

    // --- Champs privés ---
    private PostService postService;

    // --- Constructeur vide ---
    public InteractionService() {
        this.postService = new PostService();
    }

    // --- Constructeur complet ---
    public InteractionService(PostService postService) {
        this.postService = postService;
    }

    // ============================================================
    //  MÉTHODES MÉTIER
    // ============================================================

    /** CF-10 : Commenter un post. */
    public void commenter(int postId, User utilisateur, String texte) {
        if (utilisateur == null)
            throw new IllegalStateException("Connexion requise pour commenter.");
        Post post = postService.trouverParId(postId);
        if (post == null)
            throw new IllegalArgumentException("Post#" + postId + " introuvable.");
        post.ajouterCommentaire(utilisateur, texte);
    }

    /** CF-11 : Liker un post. */
    public void liker(int postId, User utilisateur) {
        if (utilisateur == null)
            throw new IllegalStateException("Connexion requise pour liker.");
        Post post = postService.trouverParId(postId);
        if (post == null)
            throw new IllegalArgumentException("Post#" + postId + " introuvable.");
        post.liker(utilisateur);
    }

    /** Afficher les commentaires visibles d'un post. */
    public void afficherCommentaires(int postId) {
        Post post = postService.trouverParId(postId);
        if (post == null)
            throw new IllegalArgumentException("Post#" + postId + " introuvable.");
        List<String> visibles = post.getCommentaires().stream()
                .filter(c -> !c.startsWith("[like:"))
                .collect(Collectors.toList());
        System.out.println("=== Commentaires Post#" + postId + " ===");
        if (visibles.isEmpty())
            System.out.println("Aucun commentaire.");
        else
            visibles.forEach(System.out::println);
    }

    /** Afficher les détails complets d'un post. */
    public void afficherDetailsPost(int postId) {
        Post post = postService.trouverParId(postId);
        if (post == null)
            throw new IllegalArgumentException("Post#" + postId + " introuvable.");
        post.afficherDetails();
    }

    // --- Getters / Setters ---
    public PostService getPostService() { return postService; }
    public void setPostService(PostService postService) { this.postService = postService; }
}
