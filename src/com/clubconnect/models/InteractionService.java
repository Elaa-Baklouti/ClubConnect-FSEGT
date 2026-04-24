package com.clubconnect.models;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *
 *  ETAPE 1 — Squelette AGL (deterministe) :
 *    Service dedie aux interactions sociales sur les posts :
 *    commentaires, likes, signalements.
 *    Package : com.clubconnect.models
 *    Pattern : champs prives -> constructeurs (vide + complet)
 *             -> methodes metier (signatures) -> getters/setters
 *    Nommage francais : commenter(), liker(), retirerLike(),
 *    signalerPost(), afficherCommentaires(), afficherDetailsPost()
 *
 *  ETAPE 2 — Implementation IA assistee :
 *    Prompt utilise :
 *      "Cree un InteractionService Java qui gere les commentaires
 *       et les likes sur des posts. Il recoit un PostService en
 *       dependance. Methodes : commenter(postId, utilisateur, texte),
 *       liker(postId, utilisateur), afficherCommentaires(postId).
 *       Lancer des exceptions si le post est introuvable ou si
 *       l'utilisateur est banni."
 *
 *    Code genere par l'IA (base) :
 *      - Structure avec PostService injecte, commenter(), liker()
 *      - Verification utilisateur non null
 *
 *    Corrections humaines :
 *      - Injection par constructeur (pas de static)
 *      - Seuil signalement configurable (SEUIL_SIGNALEMENT = 3)
 *      - Limite commentaire configurable (MAX_COMMENTAIRE = 280)
 *      - afficherCommentaires() filtre les marqueurs [like:]
 *      - signalerPost() : masquage automatique si seuil atteint
 *      - Ajout validerUtilisateur() utilitaire prive
 * ============================================================
 */
public class InteractionService {

    // ============================================================
    //  Constantes metier
    // ============================================================

    /** Nombre de signalements avant masquage automatique du post */
    public static final int SEUIL_SIGNALEMENT = 3;

    /** Limite de caracteres par commentaire */
    public static final int MAX_COMMENTAIRE   = 280;

    // ============================================================
    //  ETAPE 1 — Champs prives (squelette AGL)
    // ============================================================

    private PostService postService;

    // ============================================================
    //  ETAPE 1 — Constructeurs
    // ============================================================

    /** Constructeur vide — cree un PostService par defaut */
    public InteractionService() {
        this.postService = new PostService();
    }

    /** Constructeur complet — injection de dependance */
    public InteractionService(PostService postService) {
        this.postService = postService;
    }

    // ============================================================
    //  ETAPE 2 — Methodes metier (implementation)
    // ============================================================

    /**
     * CF-10 : Commenter un post.
     * Regles : utilisateur non banni, texte non vide, <= 280 caracteres.
     * Interaction avec User : getRole(), getUsername()
     * Interaction avec Post : ajouterCommentaire()
     * Interaction avec PostService : trouverParId()
     *
     * @param postId      ID du post a commenter
     * @param utilisateur Auteur du commentaire
     * @param texte       Contenu du commentaire
     */
    public void commenter(int postId, User utilisateur, String texte) {
        validerUtilisateur(utilisateur, "commenter");
        if (texte == null || texte.trim().isEmpty())
            throw new IllegalArgumentException("Le commentaire ne peut pas etre vide.");
        if (texte.length() > MAX_COMMENTAIRE)
            throw new IllegalArgumentException("Commentaire trop long ("
                    + texte.length() + "/" + MAX_COMMENTAIRE + " caracteres).");
        getPostOuException(postId).ajouterCommentaire(utilisateur, texte);
    }

    /**
     * CF-11 : Liker un post.
     * Interaction avec User : getRole()
     * Interaction avec Post : liker()
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
     * Interaction avec User : getRole()
     * Interaction avec Post : retirerLike()
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
     * Masquage automatique si le seuil de signalements est atteint.
     * Interaction avec User : getRole()
     * Interaction avec Post : signalerPost(), getSignalements()
     * Interaction avec PostService : getPosts().remove() si seuil atteint
     *
     * @param postId   ID du post a signaler
     * @param signaleur Utilisateur qui signale
     * @param raison   Raison du signalement
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
     */
    private void validerUtilisateur(User u, String action) {
        if (u == null)
            throw new IllegalStateException("Connexion requise pour " + action + ".");
        if ("banni".equalsIgnoreCase(u.getRole()))
            throw new IllegalStateException(
                u.getUsername() + " est banni et ne peut pas " + action + ".");
    }

    /**
     * Retrouver un post par ID ou lancer une exception.
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

    public PostService getPostService()                  { return postService; }
    public void        setPostService(PostService ps)    { this.postService = ps; }
}
