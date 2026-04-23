import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *
 *  ÉTAPE 1 — Squelette AGL (déterministe) :
 *    Service dédié aux interactions sociales sur les posts :
 *    commentaires et likes.
 *    Pattern : champs privés → constructeurs (vide + complet)
 *    → méthodes métier (signatures) → getters/setters
 *    Nommage français : commenter(), liker(),
 *    afficherCommentaires(), afficherDetailsPost()
 *
 *  ÉTAPE 2 — Implémentation IA assistée :
 *    Prompt utilisé :
 *      "Crée un InteractionService Java qui gère les commentaires
 *       et les likes sur des posts. Il reçoit un PostService en
 *       dépendance. Méthodes : commenter(postId, utilisateur, texte),
 *       liker(postId, utilisateur), afficherCommentaires(postId).
 *       Lancer des exceptions si le post est introuvable."
 *
 *    Code généré par l'IA (base) :
 *      - Structure avec PostService injecté, commenter(), liker()
 *
 *    Corrections humaines :
 *      - Injection par constructeur (pas de static)
 *      - afficherCommentaires() filtre les marqueurs [like:]
 *      - Ajout de afficherDetailsPost() délégant à Post
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

    /**
     * CF-10 : Commenter un post.
     */
    public void commenter(int postId, User utilisateur, String texte) {
        if (utilisateur == null)
            throw new IllegalStateException("Connexion requise pour commenter.");
        Post post = postService.trouverParId(postId);
        if (post == null)
            throw new IllegalArgumentException("Post#" + postId + " introuvable.");
        post.ajouterCommentaire(utilisateur, texte);
    }

    /**
     * CF-11 : Liker un post.
     */
    public void liker(int postId, User utilisateur) {
        if (utilisateur == null)
            throw new IllegalStateException("Connexion requise pour liker.");
        Post post = postService.trouverParId(postId);
        if (post == null)
            throw new IllegalArgumentException("Post#" + postId + " introuvable.");
        post.liker(utilisateur);
    }

    /**
     * Afficher les commentaires visibles d'un post (sans marqueurs de like).
     */
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

    /**
     * Afficher les détails complets d'un post.
     */
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
