package com.clubconnect.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *
 *  ETAPE 1 — Squelette AGL (deterministe) :
 *    Service de gestion des posts. Centralise les operations CRUD
 *    sur la liste des posts (donnees en memoire).
 *    Package : com.clubconnect.models
 *    Pattern : champs prives -> constructeur vide
 *             -> methodes metier (signatures) -> getters/setters
 *    Nommage francais : creerPost(), creerEtPublier(), voirPosts(),
 *    supprimerPost(), modererPost(), rechercherParTitre(),
 *    classerParPopularite(), afficherDetails()
 *
 *  ETAPE 2 — Implementation IA assistee :
 *    Prompt utilise :
 *      "Cree un PostService en Java pour gerer une liste de posts
 *       en memoire. Methodes : creerPost(titre, contenu, auteur),
 *       voirPosts(), supprimerPost(id, demandeur),
 *       rechercherParTitre(mot). Utilise des exceptions metier
 *       pour les regles d'acces."
 *
 *    Code genere par l'IA (base) :
 *      - Structure de la liste, nextId, creerPost(), voirPosts()
 *
 *    Corrections humaines :
 *      - rechercherParTitre() insensible a la casse
 *      - Regle suppression : auteur OU admin seulement
 *      - creerEtPublier() : frais 0.500 DT debites du solde auteur
 *      - modererPost() : verification >= 1 signalement + remboursement
 *      - Ajout trouverParId() utilitaire
 * ============================================================
 */
public class PostService {

    // ============================================================
    //  Constante metier
    // ============================================================

    /** Frais de publication en DT (Dinar Tunisien) */
    public static final double FRAIS_PUBLICATION = 0.500;

    // ============================================================
    //  ETAPE 1 — Champs prives (squelette AGL)
    // ============================================================

    private List<Post> posts;
    private int        nextId;

    // ============================================================
    //  ETAPE 1 — Constructeur vide
    // ============================================================

    public PostService() {
        this.posts  = new ArrayList<>();
        this.nextId = 1;
    }

    // ============================================================
    //  ETAPE 2 — Methodes metier (implementation)
    // ============================================================

    /**
     * CF-4 : Creer un post en brouillon (sans publier).
     * Interaction avec User : getRole(), getUsername()
     *
     * @param titre   Titre du post (non vide)
     * @param contenu Contenu du post
     * @param auteur  Auteur du post (non banni)
     * @return        Le Post cree en brouillon
     */
    public Post creerPost(String titre, String contenu, User auteur) {
        if (auteur == null)
            throw new IllegalArgumentException("L'auteur est obligatoire.");
        if ("banni".equalsIgnoreCase(auteur.getRole()))
            throw new IllegalStateException(auteur.getUsername() + " est banni et ne peut pas poster.");
        if (titre == null || titre.trim().isEmpty())
            throw new IllegalArgumentException("Le titre est obligatoire.");
        Post post = new Post(nextId++, titre, contenu, auteur);
        posts.add(post);
        return post;
    }

    /**
     * CF-4b : Creer et publier un post en une operation.
     * Debite les frais de publication (0.500 DT) du solde de l'auteur.
     * Interaction avec User : getRole(), getSolde(), debiterSolde()
     * Interaction avec Post : publier()
     *
     * @param titre   Titre du post
     * @param contenu Contenu du post
     * @param auteur  Auteur (non banni, solde suffisant)
     * @return        Le Post publie
     */
    public Post creerEtPublier(String titre, String contenu, User auteur) {
        if (auteur == null)
            throw new IllegalArgumentException("L'auteur est obligatoire.");
        if ("banni".equalsIgnoreCase(auteur.getRole()))
            throw new IllegalStateException(auteur.getUsername() + " est banni.");
        if (auteur.getSolde() < FRAIS_PUBLICATION)
            throw new IllegalStateException("Solde insuffisant pour publier. Frais : "
                    + FRAIS_PUBLICATION + " DT. Solde actuel : " + auteur.getSolde() + " DT.");

        int idReserve = nextId++;
        Post post = new Post(idReserve, titre, contenu, auteur);
        try {
            post.publier();
            auteur.debiterSolde(FRAIS_PUBLICATION);
        } catch (Exception e) {
            nextId = idReserve; // rollback ID si echec
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
     * Rembourse les frais de publication a l'auteur si post publie.
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
            throw new IllegalStateException("Permission refusee : auteur ou admin requis.");
        if (cible.isPublie() && cible.getAuteur() != null)
            cible.getAuteur().crediterSolde(FRAIS_PUBLICATION);
        posts.remove(cible);
    }

    /**
     * Moderation admin — supprimer un post signale.
     * Le post doit avoir au moins 1 signalement.
     * Rembourse les frais a l'auteur.
     * Interaction avec User : getRole(), crediterSolde()
     * Interaction avec Post : getSignalements(), isPublie()
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
            throw new IllegalStateException("Post#" + postId + " n'a aucun signalement.");
        if (cible.isPublie() && cible.getAuteur() != null)
            cible.getAuteur().crediterSolde(FRAIS_PUBLICATION);
        posts.remove(cible);
    }

    /**
     * Rechercher des posts publies par mot-cle dans le titre (insensible a la casse).
     * Interaction avec Post : isPublie(), getTitre()
     *
     * @param motCle Mot-cle de recherche (null ou vide = tous les posts)
     * @return       Liste des posts correspondants
     */
    public List<Post> rechercherParTitre(String motCle) {
        return posts.stream()
                .filter(Post::isPublie)
                .filter(p -> motCle == null || motCle.isBlank()
                        || p.getTitre().toLowerCase().contains(motCle.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Classer les posts publies par popularite (likes decroissants).
     * Interaction avec Post : isPublie(), getLikes()
     *
     * @return Liste triee par likes decroissants
     */
    public List<Post> classerParPopularite() {
        return posts.stream()
                .filter(Post::isPublie)
                .sorted(Comparator.comparingInt(Post::getLikes).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Trouver un post par son ID.
     * Retourne null si introuvable.
     *
     * @param id ID du post
     * @return   Le Post trouve, ou null
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
     *
     * @param id ID du post
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

    public List<Post> getPosts()              { return posts; }
    public void       setPosts(List<Post> p)  { this.posts = p; }

    public int  getNextId()                   { return nextId; }
    public void setNextId(int nextId)         { this.nextId = nextId; }
}
