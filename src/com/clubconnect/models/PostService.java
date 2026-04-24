package com.clubconnect.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 *  DOCUMENTATION TP3 — Approche IA assistee
 * ============================================================
 *
 *  METHODE : creerEtPublier()
 *  ---------------------------
 *  Prompt utilise :
 *    "Implemente creerEtPublier(String titre, String contenu, User auteur)
 *     dans PostService Java. Verifier que l'auteur n'est pas banni et que
 *     son solde est suffisant pour payer les frais de publication (0.500 DT).
 *     Creer le post, le publier, debiter le solde de l'auteur. Faire un
 *     rollback de l'ID si une exception survient. Retourner le post publie."
 *
 *  Code genere par l'IA :
 *    public Post creerEtPublier(String titre, String contenu, User auteur) {
 *        if ("banni".equals(auteur.getRole()))
 *            throw new IllegalStateException("Auteur banni.");
 *        if (auteur.getSolde() < FRAIS_PUBLICATION)
 *            throw new IllegalStateException("Solde insuffisant.");
 *        Post post = new Post(nextId++, titre, contenu, auteur);
 *        post.publier();
 *        auteur.debiterSolde(FRAIS_PUBLICATION);
 *        posts.add(post);
 *        return post;
 *    }
 *
 *  Code final apres corrections humaines :
 *    - Validation auteur non null avant appel getRole()
 *    - equalsIgnoreCase() pour "banni" (robustesse casse)
 *    - Rollback nextId si post.publier() ou debiterSolde() echoue
 *    - Interaction avec User : getSolde(), debiterSolde()
 *    - Interaction avec Post : publier()
 *
 * ============================================================
 *
 *  METHODE : supprimerPost()
 *  --------------------------
 *  Prompt utilise :
 *    "Implemente supprimerPost(int id, User demandeur) dans PostService.
 *     Seul l'auteur du post ou un admin peut supprimer. Si le post est
 *     publie, rembourser les frais de publication (0.500 DT) a l'auteur.
 *     Lancer des exceptions metier si le post est introuvable ou si
 *     l'utilisateur n'a pas les droits."
 *
 *  Code genere par l'IA :
 *    public void supprimerPost(int id, User demandeur) {
 *        Post cible = trouverParId(id);
 *        if (cible == null) throw new IllegalArgumentException("Post introuvable.");
 *        if (!cible.getAuteur().getId() == demandeur.getId()
 *            && !"admin".equals(demandeur.getRole()))
 *            throw new IllegalStateException("Permission refusee.");
 *        posts.remove(cible);
 *    }
 *
 *  Code final apres corrections humaines :
 *    - Correction operateur : != au lieu de !(...==...)
 *    - Ajout remboursement frais si post publie
 *    - Interaction avec User : getId(), getRole(), crediterSolde()
 *    - Interaction avec Post : getAuteur(), isPublie()
 *
 * ============================================================
 *
 *  METHODE : modererPost()
 *  ------------------------
 *  Prompt utilise :
 *    "Implemente modererPost(int postId, User moderateur) dans PostService.
 *     Seul un admin peut moderer. Le post doit avoir au moins 1 signalement.
 *     Rembourser les frais de publication a l'auteur si le post est publie.
 *     Supprimer le post de la liste."
 *
 *  Code genere par l'IA :
 *    public void modererPost(int postId, User moderateur) {
 *        if (!"admin".equals(moderateur.getRole()))
 *            throw new IllegalStateException("Admin requis.");
 *        posts.removeIf(p -> p.getId() == postId);
 *    }
 *
 *  Code final apres corrections humaines :
 *    - Verification moderateur non null
 *    - Verification post existe avant suppression
 *    - Verification >= 1 signalement (l'IA ne le faisait pas)
 *    - Remboursement auteur via crediterSolde()
 *    - Interaction avec Post : getSignalements(), isPublie(), getAuteur()
 *
 * ============================================================
 *
 *  METHODE : classerParPopularite()
 *  ---------------------------------
 *  Prompt utilise :
 *    "Implemente classerParPopularite() dans PostService. Retourner
 *     uniquement les posts publies, tries par nombre de likes decroissant.
 *     En cas d'egalite de likes, trier par nombre de commentaires
 *     decroissant."
 *
 *  Code genere par l'IA :
 *    public List<Post> classerParPopularite() {
 *        return posts.stream()
 *            .sorted((a, b) -> b.getLikes() - a.getLikes())
 *            .collect(Collectors.toList());
 *    }
 *
 *  Code final apres corrections humaines :
 *    - Filtre posts publies uniquement
 *    - Comparator.comparingInt pour lisibilite
 *    - Tri secondaire par nb commentaires visibles en cas d'egalite
 *
 * ============================================================
 */
public class PostService {

    // ============================================================
    //  Constante metier
    // ============================================================

    /** Frais de publication en DT (Dinar Tunisien) */
    public static final double FRAIS_PUBLICATION = 0.500;

    // ============================================================
    //  Champs prives
    // ============================================================

    private List<Post> posts;
    private int        nextId;

    // ============================================================
    //  Constructeur
    // ============================================================

    public PostService() {
        this.posts  = new ArrayList<>();
        this.nextId = 1;
    }

    // ============================================================
    //  Methodes metier — logique reelle
    // ============================================================

    /**
     * CF-4 : Creer un post en brouillon (sans publier).
     * Interaction avec User : getRole(), getUsername()
     */
    public Post creerPost(String titre, String contenu, User auteur) {
        if (auteur == null)
            throw new IllegalArgumentException("L'auteur est obligatoire.");
        if ("banni".equalsIgnoreCase(auteur.getRole()))
            throw new IllegalStateException(
                auteur.getUsername() + " est banni et ne peut pas poster.");
        if (titre == null || titre.trim().isEmpty())
            throw new IllegalArgumentException("Le titre est obligatoire.");
        Post post = new Post(nextId++, titre, contenu, auteur);
        posts.add(post);
        return post;
    }

    /**
     * CF-4b : Creer et publier un post en une operation.
     * Debite 0.500 DT du solde de l'auteur.
     * Interaction avec User : getRole(), getSolde(), debiterSolde()
     * Interaction avec Post : publier()
     *
     * @param titre   Titre du post
     * @param contenu Contenu du post
     * @param auteur  Auteur (non banni, solde >= 0.500 DT)
     * @return        Le Post publie
     */
    public Post creerEtPublier(String titre, String contenu, User auteur) {
        if (auteur == null)
            throw new IllegalArgumentException("L'auteur est obligatoire.");
        if ("banni".equalsIgnoreCase(auteur.getRole()))
            throw new IllegalStateException(auteur.getUsername() + " est banni.");
        if (auteur.getSolde() < FRAIS_PUBLICATION)
            throw new IllegalStateException(
                "Solde insuffisant pour publier. Frais : " + FRAIS_PUBLICATION
                + " DT. Solde actuel : " + auteur.getSolde() + " DT.");

        int idReserve = nextId++;
        Post post = new Post(idReserve, titre, contenu, auteur);
        try {
            post.publier();                        // Post -> marque publie + date
            auteur.debiterSolde(FRAIS_PUBLICATION); // User -> solde mis a jour
        } catch (Exception e) {
            nextId = idReserve;                    // rollback ID si echec
            throw e;
        }
        posts.add(post);
        return post;
    }

    /**
     * CF-5 : Afficher tous les posts publies (epingles en premier).
     * Interaction avec Post : isPublie(), isEpingle()
     */
    public void voirPosts() {
        List<Post> publies = posts.stream()
                .filter(Post::isPublie)
                .collect(Collectors.toList());
        if (publies.isEmpty()) {
            System.out.println("Aucun post publie.");
        } else {
            System.out.println("=== Posts publies (" + publies.size() + ") ===");
            publies.stream().filter(Post::isEpingle).forEach(System.out::println);
            publies.stream().filter(p -> !p.isEpingle()).forEach(System.out::println);
        }
    }

    /**
     * CF-15 : Supprimer un post — auteur ou admin seulement.
     * Rembourse 0.500 DT a l'auteur si le post est publie.
     * Interaction avec User : getId(), getRole(), crediterSolde()
     * Interaction avec Post : getAuteur(), isPublie()
     *
     * @param id        ID du post a supprimer
     * @param demandeur Utilisateur qui demande la suppression
     */
    public void supprimerPost(int id, User demandeur) {
        if (demandeur == null)
            throw new IllegalArgumentException("Demandeur invalide.");
        Post cible = trouverParId(id);
        if (cible == null)
            throw new IllegalArgumentException("Post#" + id + " introuvable.");
        boolean estAuteur = cible.getAuteur() != null
                         && cible.getAuteur().getId() == demandeur.getId();
        boolean estAdmin  = "admin".equalsIgnoreCase(demandeur.getRole());
        if (!estAuteur && !estAdmin)
            throw new IllegalStateException(
                "Permission refusee : auteur ou admin requis.");
        if (cible.isPublie() && cible.getAuteur() != null)
            cible.getAuteur().crediterSolde(FRAIS_PUBLICATION);
        posts.remove(cible);
    }

    /**
     * Moderation admin — supprimer un post signale.
     * Le post doit avoir au moins 1 signalement.
     * Rembourse 0.500 DT a l'auteur.
     * Interaction avec User : getRole(), crediterSolde()
     * Interaction avec Post : getSignalements(), isPublie(), getAuteur()
     *
     * @param postId     ID du post a moderer
     * @param moderateur Utilisateur admin
     */
    public void modererPost(int postId, User moderateur) {
        if (moderateur == null || !"admin".equalsIgnoreCase(moderateur.getRole()))
            throw new IllegalStateException("Seul un admin peut moderer un post.");
        Post cible = trouverParId(postId);
        if (cible == null)
            throw new IllegalArgumentException("Post#" + postId + " introuvable.");
        if (cible.getSignalements().isEmpty())
            throw new IllegalStateException(
                "Post#" + postId + " n'a aucun signalement.");
        if (cible.isPublie() && cible.getAuteur() != null)
            cible.getAuteur().crediterSolde(FRAIS_PUBLICATION);
        posts.remove(cible);
    }

    /**
     * Rechercher des posts publies par mot-cle dans le titre.
     * Insensible a la casse.
     * Interaction avec Post : isPublie(), getTitre()
     *
     * @param motCle Mot-cle (null ou vide = tous les posts publies)
     * @return       Liste des posts correspondants
     */
    public List<Post> rechercherParTitre(String motCle) {
        return posts.stream()
                .filter(Post::isPublie)
                .filter(p -> motCle == null || motCle.isBlank()
                        || p.getTitre().toLowerCase()
                               .contains(motCle.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Classer les posts publies par popularite.
     * Tri primaire : likes decroissants.
     * Tri secondaire : nb commentaires visibles decroissants.
     * Interaction avec Post : isPublie(), getLikes(), getCommentaires()
     *
     * @return Liste triee par popularite
     */
    public List<Post> classerParPopularite() {
        return posts.stream()
                .filter(Post::isPublie)
                .sorted(Comparator
                    .comparingInt(Post::getLikes).reversed()
                    .thenComparingInt(p -> (int) -p.getCommentaires().stream()
                        .filter(c -> !c.startsWith("[like:")).count()))
                .collect(Collectors.toList());
    }

    /**
     * Trouver un post par son ID.
     * Retourne null si introuvable.
     *
     * @param id ID du post
     * @return   Le Post, ou null
     */
    public Post trouverParId(int id) {
        return posts.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Afficher les details d'un post specifique.
     * Interaction avec Post : afficherDetails()
     */
    public void afficherDetails(int id) {
        Post post = trouverParId(id);
        if (post == null)
            throw new IllegalArgumentException("Post#" + id + " introuvable.");
        post.afficherDetails();
    }

    // ============================================================
    //  toString
    // ============================================================

    @Override
    public String toString() {
        long nbPublies = posts.stream().filter(Post::isPublie).count();
        return "PostService | posts=" + posts.size() + " | publies=" + nbPublies;
    }

    // ============================================================
    //  Getters / Setters
    // ============================================================

    public List<Post> getPosts()             { return posts; }
    public void       setPosts(List<Post> p) { this.posts = p; }
    public int        getNextId()            { return nextId; }
    public void       setNextId(int n)       { this.nextId = n; }
}
