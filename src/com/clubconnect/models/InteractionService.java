package com.clubconnect.models;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 *  DOCUMENTATION TP3 — Approche IA assistee
 * ============================================================
 *
 *  METHODE : commenter()
 *  ----------------------
 *  Prompt utilise :
 *    "Implemente commenter(int postId, User utilisateur, String texte)
 *     dans InteractionService Java. Verifier que l'utilisateur n'est
 *     pas banni, que le texte n'est pas vide et ne depasse pas 280
 *     caracteres. Retrouver le post via PostService et appeler
 *     ajouterCommentaire(). Lancer des exceptions metier explicites."
 *
 *  Code genere par l'IA :
 *    public void commenter(int postId, User utilisateur, String texte) {
 *        if (texte.length() > 280)
 *            throw new IllegalArgumentException("Commentaire trop long.");
 *        Post post = postService.trouverParId(postId);
 *        post.ajouterCommentaire(utilisateur, texte);
 *    }
 *
 *  Code final apres corrections humaines :
 *    - Ajout validation utilisateur non null et non banni via validerUtilisateur()
 *    - Verification texte non null avant appel .length()
 *    - Verification post non null avec message d'erreur enrichi
 *    - Interaction : PostService.trouverParId() -> Post.ajouterCommentaire()
 *
 * ============================================================
 *
 *  METHODE : liker() / retirerLike()
 *  ------------------------------------
 *  Prompt utilise :
 *    "Implemente liker(int postId, User utilisateur) et retirerLike()
 *     dans InteractionService. Verifier que l'utilisateur est valide
 *     et non banni. Deleguer la logique metier a Post.liker() et
 *     Post.retirerLike(). Lancer une exception si le post est introuvable."
 *
 *  Code genere par l'IA :
 *    public void liker(int postId, User utilisateur) {
 *        postService.trouverParId(postId).liker(utilisateur);
 *    }
 *    public void retirerLike(int postId, User utilisateur) {
 *        postService.trouverParId(postId).retirerLike(utilisateur);
 *    }
 *
 *  Code final apres corrections humaines :
 *    - Ajout validation utilisateur via validerUtilisateur()
 *    - Remplacement de l'appel direct par getPostOuException()
 *      pour un message d'erreur clair si post introuvable
 *    - Interaction : PostService -> Post.liker() / Post.retirerLike()
 *
 * ============================================================
 *
 *  METHODE : signalerPost()
 *  -------------------------
 *  Prompt utilise :
 *    "Implemente signalerPost(int postId, User signaleur, String raison)
 *     dans InteractionService. Deleguer a Post.signalerPost(). Si le
 *     nombre de signalements atteint le seuil (3), masquer le post
 *     automatiquement en le retirant de la liste PostService."
 *
 *  Code genere par l'IA :
 *    public void signalerPost(int postId, User signaleur, String raison) {
 *        Post post = postService.trouverParId(postId);
 *        post.signalerPost(signaleur, raison);
 *        if (post.getSignalements().size() >= 3)
 *            System.out.println("Post masque.");
 *    }
 *
 *  Code final apres corrections humaines :
 *    - Seuil configurable via SEUIL_SIGNALEMENT (pas de 3 en dur)
 *    - Masquage reel : postService.getPosts().remove(post)
 *    - Ajout validation signaleur via validerUtilisateur()
 *    - Interaction : Post.signalerPost() -> PostService.getPosts().remove()
 *
 * ============================================================
 *
 *  METHODE : voirCommentairesPublics()
 *  -------------------------------------
 *  Prompt utilise :
 *    "Implemente voirCommentairesPublics(int postId) dans
 *     InteractionService. Accessible sans connexion (visiteur).
 *     Verifier que le post est publie. Afficher uniquement les
 *     commentaires visibles (filtrer les marqueurs [like:])."
 *
 *  Code genere par l'IA :
 *    public void voirCommentairesPublics(int postId) {
 *        Post post = postService.trouverParId(postId);
 *        post.getCommentaires().forEach(System.out::println);
 *    }
 *
 *  Code final apres corrections humaines :
 *    - Verification post non null
 *    - Verification post publie (acces lecture seule)
 *    - Filtre des marqueurs internes [like:] avant affichage
 *    - Interaction : Post.isPublie(), Post.getCommentaires()
 *
 * ============================================================
 */
public class InteractionService {

    // ============================================================
    //  Constantes metier
    // ============================================================

    /** Nombre de signalements avant masquage automatique */
    public static final int SEUIL_SIGNALEMENT = 3;

    /** Limite de caracteres par commentaire */
    public static final int MAX_COMMENTAIRE   = 280;

    // ============================================================
    //  Champs prives
    // ============================================================

    private PostService postService;

    // ============================================================
    //  Constructeurs
    // ============================================================

    /** Constructeur vide — cree un PostService par defaut */
    public InteractionService() {
        this.postService = new PostService();
    }

    /** Constructeur avec injection de dependance */
    public InteractionService(PostService postService) {
        this.postService = postService;
    }

    // ============================================================
    //  Methodes metier — logique reelle
    // ============================================================

    /**
     * CF-10 : Commenter un post.
     * Regles : utilisateur non banni, texte non vide, <= 280 caracteres.
     * Interaction avec User    : getRole(), getUsername()
     * Interaction avec Post    : ajouterCommentaire()
     * Interaction avec PostService : trouverParId()
     *
     * @param postId      ID du post a commenter
     * @param utilisateur Auteur du commentaire
     * @param texte       Contenu du commentaire
     */
    public void commenter(int postId, User utilisateur, String texte) {
        validerUtilisateur(utilisateur, "commenter");
        if (texte == null || texte.trim().isEmpty())
            throw new IllegalArgumentException(
                "Le commentaire ne peut pas etre vide.");
        if (texte.length() > MAX_COMMENTAIRE)
            throw new IllegalArgumentException(
                "Commentaire trop long (" + texte.length()
                + "/" + MAX_COMMENTAIRE + " caracteres).");
        getPostOuException(postId).ajouterCommentaire(utilisateur, texte);
    }

    /**
     * CF-11 : Liker un post.
     * Interaction avec User    : getRole()
     * Interaction avec Post    : liker()
     * Interaction avec PostService : trouverParId()
     *
     * @param postId      ID du post a liker
     * @param utilisateur Utilisateur qui like
     */
    public void liker(int postId, User utilisateur) {
        validerUtilisateur(utilisateur, "liker");
        getPostOuException(postId).liker(utilisateur);
    }

    /**
     * Retirer son like d'un post.
     * Interaction avec User    : getRole()
     * Interaction avec Post    : retirerLike()
     * Interaction avec PostService : trouverParId()
     *
     * @param postId      ID du post
     * @param utilisateur Utilisateur qui retire son like
     */
    public void retirerLike(int postId, User utilisateur) {
        validerUtilisateur(utilisateur, "retirer un like");
        getPostOuException(postId).retirerLike(utilisateur);
    }

    /**
     * Signaler un post pour contenu inapproprie.
     * Masquage automatique si seuil atteint (suppression de PostService).
     * Interaction avec User    : getRole()
     * Interaction avec Post    : signalerPost(), getSignalements()
     * Interaction avec PostService : getPosts().remove()
     *
     * @param postId    ID du post a signaler
     * @param signaleur Utilisateur qui signale
     * @param raison    Raison du signalement
     */
    public void signalerPost(int postId, User signaleur, String raison) {
        validerUtilisateur(signaleur, "signaler");
        Post post = getPostOuException(postId);
        post.signalerPost(signaleur, raison);
        if (post.getSignalements().size() >= SEUIL_SIGNALEMENT) {
            postService.getPosts().remove(post);
        }
    }

    /**
     * Afficher les commentaires visibles d'un post (sans marqueurs internes).
     * Interaction avec Post : getCommentaires()
     *
     * @param postId ID du post
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
     * Interaction avec Post : afficherDetails()
     *
     * @param postId ID du post
     */
    public void afficherDetailsPost(int postId) {
        getPostOuException(postId).afficherDetails();
    }

    /**
     * Acces visiteur — voir les commentaires publics sans connexion.
     * Verifie que le post est publie avant d'afficher.
     * Interaction avec Post : isPublie(), getCommentaires()
     *
     * @param postId ID du post
     */
    public void voirCommentairesPublics(int postId) {
        Post post = postService.trouverParId(postId);
        if (post == null)
            throw new IllegalArgumentException("Post#" + postId + " introuvable.");
        if (!post.isPublie())
            throw new IllegalStateException("Ce post n'est pas accessible.");
        List<String> visibles = post.getCommentaires().stream()
                .filter(c -> !c.startsWith("[like:"))
                .collect(Collectors.toList());
        System.out.println("=== Commentaires publics Post#" + postId
                + " (" + visibles.size() + ") ===");
        if (visibles.isEmpty())
            System.out.println("  Aucun commentaire.");
        else
            visibles.forEach(c -> System.out.println("  - " + c));
    }

    // ============================================================
    //  toString
    // ============================================================

    @Override
    public String toString() {
        return "InteractionService | seuil=" + SEUIL_SIGNALEMENT
             + " | maxCommentaire=" + MAX_COMMENTAIRE;
    }

    // ============================================================
    //  Utilitaires prives
    // ============================================================

    /**
     * Valider qu'un utilisateur peut effectuer une action.
     * Lance une exception si null ou banni.
     * Interaction avec User : getRole(), getUsername()
     */
    private void validerUtilisateur(User u, String action) {
        if (u == null)
            throw new IllegalStateException(
                "Connexion requise pour " + action + ".");
        if ("banni".equalsIgnoreCase(u.getRole()))
            throw new IllegalStateException(
                u.getUsername() + " est banni et ne peut pas " + action + ".");
    }

    /**
     * Retrouver un post par ID ou lancer une exception claire.
     * Interaction avec PostService : trouverParId()
     */
    private Post getPostOuException(int postId) {
        Post post = postService.trouverParId(postId);
        if (post == null)
            throw new IllegalArgumentException("Post#" + postId + " introuvable.");
        return post;
    }

    // ============================================================
    //  Getters / Setters
    // ============================================================

    public PostService getPostService()               { return postService; }
    public void        setPostService(PostService ps) { this.postService = ps; }
}
