import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 *  DOCUMENTATION TP3 — Approche IA assistee
 * ============================================================
 *
 *  METHODE : commenter()
 *  ---------------------
 *  Prompt utilise :
 *    "Implemente commenter(int postId, User utilisateur, String texte)
 *     dans InteractionService. Verifier que l'utilisateur n'est pas
 *     banni, que le post est publie, et que le texte n'est pas vide.
 *     Un commentaire ne peut pas depasser 280 caracteres."
 *
 *  Code genere par l'IA :
 *    public void commenter(int postId, User utilisateur, String texte) {
 *        if (texte.length() > 280)
 *            throw new IllegalArgumentException("Commentaire trop long.");
 *        Post post = postService.trouverParId(postId);
 *        post.ajouterCommentaire(utilisateur, texte);
 *    }
 *
 *  Corrections humaines :
 *    - Ajout verification utilisateur non null
 *    - Ajout verification role "banni"
 *    - Ajout verification post non null avant appel
 *
 * ============================================================
 *
 *  METHODE : liker() / retirerLike()
 *  -----------------------------------
 *  Prompt utilise :
 *    "Implemente liker(int postId, User utilisateur) et retirerLike()
 *     dans InteractionService. Deleguer la logique metier a Post.liker()
 *     et Post.retirerLike(). Verifier que l'utilisateur est valide."
 *
 *  Code genere par l'IA :
 *    public void liker(int postId, User utilisateur) {
 *        postService.trouverParId(postId).liker(utilisateur);
 *    }
 *
 *  Corrections humaines :
 *    - Ajout verification post non null avec message clair
 *    - Ajout verification utilisateur non banni
 *
 * ============================================================
 *
 *  METHODE : signalerPost()
 *  ------------------------
 *  Prompt utilise :
 *    "Implemente signalerPost(int postId, User signaleur, String raison)
 *     dans InteractionService. Deleguer a Post.signalerPost(). Si le
 *     post atteint 3 signalements, le masquer automatiquement."
 *
 *  Code genere par l'IA :
 *    public void signalerPost(int postId, User signaleur, String raison) {
 *        Post post = postService.trouverParId(postId);
 *        post.signalerPost(signaleur, raison);
 *        if (post.getSignalements().size() >= 3)
 *            System.out.println("Post masque automatiquement.");
 *    }
 *
 *  Corrections humaines :
 *    - Seuil configurable (SEUIL_SIGNALEMENT) au lieu de 3 en dur
 *    - Masquage reel via flag au lieu de simple println
 *
 * ============================================================
 */
public class InteractionService {

    // Seuil de signalements avant masquage automatique
    public static final int SEUIL_SIGNALEMENT = 3;
    // Limite caracteres par commentaire
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
    //  METHODES METIER — logique reelle
    // ============================================================

    /**
     * CF-10 : Commenter un post.
     * - Utilisateur non banni
     * - Post publie
     * - Texte <= 280 caracteres
     */
    public void commenter(int postId, User utilisateur, String texte) {
        validerUtilisateur(utilisateur, "commenter");
        if (texte == null || texte.trim().isEmpty())
            throw new IllegalArgumentException("Le commentaire ne peut pas etre vide.");
        if (texte.length() > MAX_COMMENTAIRE)
            throw new IllegalArgumentException("Commentaire trop long ("
                    + texte.length() + "/" + MAX_COMMENTAIRE + " caracteres).");
        Post post = getPostOuException(postId);
        post.ajouterCommentaire(utilisateur, texte);
    }

    /**
     * CF-11 : Liker un post.
     * Interaction avec Post : incremente likes, anti-doublon.
     */
    public void liker(int postId, User utilisateur) {
        validerUtilisateur(utilisateur, "liker");
        Post post = getPostOuException(postId);
        post.liker(utilisateur);
    }

    /**
     * Retirer son like d'un post.
     */
    public void retirerLike(int postId, User utilisateur) {
        validerUtilisateur(utilisateur, "retirer un like");
        Post post = getPostOuException(postId);
        post.retirerLike(utilisateur);
    }

    /**
     * Signaler un post pour contenu inapproprie.
     * Si le seuil de signalements est atteint, le post est masque automatiquement
     * (supprime de la liste publique via PostService).
     * Interaction : Post.signalerPost() → PostService.supprimerPost() si seuil atteint.
     */
    public void signalerPost(int postId, User signaleur, String raison) {
        validerUtilisateur(signaleur, "signaler");
        Post post = getPostOuException(postId);
        post.signalerPost(signaleur, raison);
        // Masquage automatique si seuil atteint
        if (post.getSignalements().size() >= SEUIL_SIGNALEMENT) {
            postService.getPosts().remove(post);
        }
    }

    /**
     * Afficher les commentaires visibles d'un post (sans marqueurs internes).
     */
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

    /**
     * Afficher les details complets d'un post.
     */
    public void afficherDetailsPost(int postId) {
        getPostOuException(postId).afficherDetails();
    }

    // ============================================================
    //  UTILITAIRES PRIVES
    // ============================================================

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
