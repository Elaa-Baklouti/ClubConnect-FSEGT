package com.clubconnect.interaction;

import com.clubconnect.gestionpostes.Post;
import com.clubconnect.gestionpostes.PostService;
import com.clubconnect.models.User;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 *  DOCUMENTATION TP3 — Approche IA assistee
 * ============================================================
 *  METHODE : commenter()
 *  Prompt utilise :
 *    "commenter(postId, utilisateur, texte). Verifier non banni,
 *     post publie, texte <= 280 caracteres."
 *  Code genere par l'IA :
 *    public void commenter(int postId, User utilisateur, String texte) {
 *        if (texte.length() > 280) throw ...;
 *        postService.trouverParId(postId).ajouterCommentaire(utilisateur, texte);
 *    }
 *  Corrections humaines :
 *    - Verification utilisateur non null et non banni
 *    - Verification post non null avant appel
 * ============================================================
 *  METHODE : signalerPost()
 *  Prompt utilise :
 *    "signalerPost(postId, signaleur, raison). Si >= 3 signalements,
 *     masquer automatiquement."
 *  Code genere par l'IA :
 *    public void signalerPost(int postId, User signaleur, String raison) {
 *        Post post = postService.trouverParId(postId);
 *        post.signalerPost(signaleur, raison);
 *        if (post.getSignalements().size() >= 3) System.out.println("Masque.");
 *    }
 *  Corrections humaines :
 *    - Seuil configurable SEUIL_SIGNALEMENT
 *    - Masquage reel via suppression de la liste
 * ============================================================
 */
public class InteractionService {

    public static final int SEUIL_SIGNALEMENT = 3;
    public static final int MAX_COMMENTAIRE   = 280;

    private PostService postService;

    public InteractionService() { this.postService = new PostService(); }
    public InteractionService(PostService postService) { this.postService = postService; }

    public void commenter(int postId, User utilisateur, String texte) {
        validerUtilisateur(utilisateur, "commenter");
        if (texte == null || texte.trim().isEmpty())
            throw new IllegalArgumentException("Commentaire vide.");
        if (texte.length() > MAX_COMMENTAIRE)
            throw new IllegalArgumentException("Commentaire trop long ("
                    + texte.length() + "/" + MAX_COMMENTAIRE + " caracteres).");
        getPostOuException(postId).ajouterCommentaire(utilisateur, texte);
    }

    public void liker(int postId, User utilisateur) {
        validerUtilisateur(utilisateur, "liker");
        getPostOuException(postId).liker(utilisateur);
    }

    public void retirerLike(int postId, User utilisateur) {
        validerUtilisateur(utilisateur, "retirer un like");
        getPostOuException(postId).retirerLike(utilisateur);
    }

    public void signalerPost(int postId, User signaleur, String raison) {
        validerUtilisateur(signaleur, "signaler");
        Post post = getPostOuException(postId);
        post.signalerPost(signaleur, raison);
        if (post.getSignalements().size() >= SEUIL_SIGNALEMENT)
            postService.getPosts().remove(post);
    }

    public void voirCommentairesPublics(int postId) {
        Post post = postService.trouverParId(postId);
        if (post == null) throw new IllegalArgumentException("Post#" + postId + " introuvable.");
        if (!post.isPublie()) throw new IllegalStateException("Post non accessible.");
        List<String> visibles = post.getCommentaires().stream()
                .filter(c -> !c.startsWith("[like:")).collect(Collectors.toList());
        System.out.println("=== Commentaires Post#" + postId + " (" + visibles.size() + ") ===");
        if (visibles.isEmpty()) System.out.println("  Aucun commentaire.");
        else visibles.forEach(c -> System.out.println("  - " + c));
    }

    public void afficherCommentaires(int postId) { voirCommentairesPublics(postId); }

    public void afficherDetailsPost(int postId) { getPostOuException(postId).afficherDetails(); }

    private void validerUtilisateur(User u, String action) {
        if (u == null) throw new IllegalStateException("Connexion requise pour " + action + ".");
        if ("banni".equalsIgnoreCase(u.getRole()))
            throw new IllegalStateException(u.getUsername() + " est banni.");
    }

    private Post getPostOuException(int postId) {
        Post post = postService.trouverParId(postId);
        if (post == null) throw new IllegalArgumentException("Post#" + postId + " introuvable.");
        return post;
    }

    public PostService getPostService() { return postService; }
    public void setPostService(PostService postService) { this.postService = postService; }
}
