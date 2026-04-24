import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 *  DOCUMENTATION TP3 — Approche IA assistee
 * ============================================================
 *  METHODE : commenter()
 *  Prompt utilise :
 *    "Implemente commenter(postId, utilisateur, texte). Verifier
 *     non banni, post publie, texte <= 280 caracteres."
 *  Code genere par l'IA :
 *    public void commenter(int postId, User utilisateur, String texte) {
 *        if (texte.length() > 280) throw ...;
 *        postService.trouverParId(postId).ajouterCommentaire(utilisateur, texte);
 *    }
 *  Corrections humaines :
 *    - Verification utilisateur non null
 *    - Verification role "banni"
 *    - Verification post non null avant appel
 * ============================================================
 *  METHODE : liker() / retirerLike()
 *  Prompt utilise :
 *    "Implemente liker() et retirerLike() en delegant a Post."
 *  Code genere par l'IA :
 *    public void liker(int postId, User utilisateur) {
 *        postService.trouverParId(postId).liker(utilisateur);
 *    }
 *  Corrections humaines :
 *    - Verification post non null avec message clair
 *    - Verification utilisateur non banni
 * ============================================================
 *  METHODE : signalerPost()
 *  Prompt utilise :
 *    "signalerPost(postId, signaleur, raison). Si >= 3 signalements,
 *     masquer automatiquement le post."
 *  Code genere par l'IA :
 *    public void signalerPost(int postId, User signaleur, String raison) {
 *        Post post = postService.trouverParId(postId);
 *        post.signalerPost(signaleur, raison);
 *        if (post.getSignalements().size() >= 3)
 *            System.out.println("Post masque.");
 *    }
 *  Corrections humaines :
 *    - Seuil configurable SEUIL_SIGNALEMENT
 *    - Masquage reel via suppression de la liste
 * ============================================================
 */
public class InteractionService {

    public static final int SEUIL_SIGNALEMENT = 3;
    public static final int MAX_COMMENTAIRE   = 280;

    // --- Champs prives ---
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
    //  METHODES METIER
    // ============================================================

    /** CF-10 : Commenter un post. */
    public void commenter(int postId, User utilisateur, String texte) {
        validerUtilisateur(utilisateur, "commenter");
        if (texte == null || texte.trim().isEmpty())
            throw new IllegalArgumentException("Le commentaire ne peut pas etre vide.");
        if (texte.length() > MAX_COMMENTAIRE)
            throw new IllegalArgumentException("Commentaire trop long ("
                    + texte.length() + "/" + MAX_COMMENTAIRE + " caracteres).");
        getPostOuException(postId).ajouterCommentaire(utilisateur, texte);
    }

    /** CF-11 : Liker un post. */
    public void liker(int postId, User utilisateur) {
        validerUtilisateur(utilisateur, "liker");
        getPostOuException(postId).liker(utilisateur);
    }

    /** Retirer son like. */
    public void retirerLike(int postId, User utilisateur) {
        validerUtilisateur(utilisateur, "retirer un like");
        getPostOuException(postId).retirerLike(utilisateur);
    }

    /**
     * Signaler un post. Masquage automatique si seuil atteint.
     * Interaction : Post.signalerPost() → PostService retire le post si seuil.
     */
    public void signalerPost(int postId, User signaleur, String raison) {
        validerUtilisateur(signaleur, "signaler");
        Post post = getPostOuException(postId);
        post.signalerPost(signaleur, raison);
        if (post.getSignalements().size() >= SEUIL_SIGNALEMENT)
            postService.getPosts().remove(post);
    }

    /** Afficher les commentaires visibles d'un post. */
    public void afficherCommentaires(int postId) {
        Post post = getPostOuException(postId);
        List<String> visibles = post.getCommentaires().stream()
                .filter(c -> !c.startsWith("[like:"))
                .collect(Collectors.toList());
        System.out.println("=== Commentaires Post#" + postId
                + " (" + visibles.size() + ") ===");
        if (visibles.isEmpty())
            System.out.println("  Aucun commentaire.");
        else
            visibles.forEach(c -> System.out.println("  - " + c));
    }

    /** Afficher les details complets d'un post. */
    public void afficherDetailsPost(int postId) {
        getPostOuException(postId).afficherDetails();
    }

    // --- Utilitaires prives ---
    private void validerUtilisateur(User u, String action) {
        if (u == null)
            throw new IllegalStateException("Connexion requise pour " + action + ".");
        if ("banni".equalsIgnoreCase(u.getRole()))
            throw new IllegalStateException(u.getUsername() + " est banni et ne peut pas " + action + ".");
    }

    private Post getPostOuException(int postId) {
        Post post = postService.trouverParId(postId);
        if (post == null)
            throw new IllegalArgumentException("Post#" + postId + " introuvable.");
        return post;
    }

    // --- Getters / Setters ---
    public PostService getPostService() { return postService; }
    public void setPostService(PostService postService) { this.postService = postService; }
}
