import java.util.ArrayList;


import java.util.Comparator;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
<<<<<<< HEAD
 *  DOCUMENTATION TP1 — Approche hybride
 *
 *
 *  ÉTAPE 1 — Squelette AGL (déterministe) :
 *    Service de gestion des posts. Centralise les opérations CRUD
 *    sur la liste des posts (données en mémoire).
 *    Pattern : champs privés → constructeurs (vide + complet)
 *    → méthodes métier (signatures) → getters/setters
 *    Nommage français : creerPost(), voirPosts(),
 *    supprimerPost(), rechercherParTitre(), afficherDetails()
 *
 *  ÉTAPE 2 — Implémentation IA assistée :
 *    Prompt utilisé :
 *      "Crée un PostService en Java pour gérer une liste de posts
 *       en mémoire. Méthodes : creerPost(titre, contenu, auteur),
 *       voirPosts(), supprimerPost(id, demandeur),
 *       rechercherParTitre(mot). Utilise des exceptions métier
 *       pour les règles d'accès."
 *
 *    Code généré par l'IA (base) :
 *      - Structure de la liste, nextId, creerPost(), voirPosts()
 *
 *    Corrections humaines :
 *      - rechercherParTitre() insensible à la casse
 *      - Règle suppression : auteur OU admin seulement
 *      - Ajout de trouverParId() utilitaire

 *  DOCUMENTATION TP3 — Approche IA assistee
 *
 *
 *  METHODE : creerEtPublier()
 *  --------------------------
 *  Prompt utilise :
 *    "Cree une methode creerEtPublier(titre, contenu, auteur) dans
 *     PostService qui cree un Post, le valide et le publie en une
 *     seule operation. Retourner le post publie. Lancer une exception
 *     si l'auteur n'a pas le droit de publier (role banni)."
 *
 *  Code genere par l'IA :
 *    public Post creerEtPublier(String titre, String contenu, User auteur) {
 *        Post post = new Post(nextId++, titre, contenu, auteur);
 *        post.publier();
 *        posts.add(post);
 *        return post;
 *    }
 *
 *  Corrections humaines :
 *    - Ajout verification role "banni"
 *    - Ajout frais de publication en DT (debite du solde auteur)
 *    - Rollback nextId si echec
 *
 * ============================================================
 *
 *  METHODE : modererPost()
 *  -----------------------
 *  Prompt utilise :
 *    "Implemente modererPost(int postId, User moderateur) dans
 *     PostService. Un moderateur (admin) peut supprimer un post
 *     signale. Verifier que le post a au moins 1 signalement avant
 *     de le supprimer."
 *
 *  Code genere par l'IA :
 *    public void modererPost(int postId, User moderateur) {
 *        if (!"admin".equals(moderateur.getRole()))
 *            throw new IllegalStateException("Admin requis.");
 *        posts.removeIf(p -> p.getId() == postId);
 *    }
 *
 *  Corrections humaines :
 *    - Ajout verification post existe
 *    - Ajout verification au moins 1 signalement
 *    - Remboursement frais publication a l'auteur si modere
 *
 * ============================================================
 *
 *  METHODE : classerParPopularite()
 *  ---------------------------------
 *  Prompt utilise :
 *    "Cree classerParPopularite() dans PostService qui retourne
 *     la liste des posts tries par nombre de likes decroissant,
 *     en ne retournant que les posts publies."
 *
 *  Code genere par l'IA :
 *    public List<Post> classerParPopularite() {
 *        return posts.stream()
 *            .sorted((a, b) -> b.getLikes() - a.getLikes())
 *            .collect(Collectors.toList());
 *    }
 *
 *  Corrections humaines :
 *    - Filtre posts publies uniquement
 *    - Utilisation de Comparator.comparingInt pour lisibilite
 *

 * ============================================================
 */
public class PostService {


    // --- Champs privés ---

    // Frais de publication en DT
    public static final double FRAIS_PUBLICATION = 0.500; // 0.500 DT

    // --- Champs prives ---

    private List<Post> posts;
    private int nextId;

    // --- Constructeur vide ---
    public PostService() {
        this.posts  = new ArrayList<>();
        this.nextId = 1;
    }

    // ============================================================

    //  MÉTHODES MÉTIER
    // ============================================================

    /**
     * CF-4 : Créer un nouveau post.
     */
    public Post creerPost(String titre, String contenu, User auteur) {
        if (auteur == null)
            throw new IllegalArgumentException("L'auteur ne peut pas être null.");
        if (titre == null || titre.isBlank())
            throw new IllegalArgumentException("Le titre est obligatoire.");
        Post post = new Post(nextId++, titre, contenu, auteur);
        posts.add(post);
        System.out.println("Post créé : " + titre + " par " + auteur.getUsername());

    //  METHODES METIER — logique reelle
    

    /**
     * CF-4 : Creer un post en brouillon (sans publier).
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

     * CF-5 : Afficher tous les posts.
     */
    public void voirPosts() {
        if (posts.isEmpty())
            System.out.println("Aucun post disponible.");
        else {
            System.out.println("=== Liste des Posts ===");
            posts.forEach(System.out::println);
        }
    }

    /**
     * CF-15 : Supprimer un post par ID.
     * Seul l'auteur ou un admin peut supprimer.
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
            throw new IllegalStateException("Permission refusée : seul l'auteur ou un admin peut supprimer ce post.");
        posts.remove(cible);
        System.out.println("Post#" + id + " supprimé par " + demandeur.getUsername());
    }

    /**
     * Rechercher des posts par mot-clé dans le titre (insensible à la casse).
     */
    public List<Post> rechercherParTitre(String motCle) {
        if (motCle == null || motCle.isBlank())
            return new ArrayList<>(posts);
        return posts.stream()
                .filter(p -> p.getTitre().toLowerCase().contains(motCle.toLowerCase()))

     * CF-4b : Creer et publier un post en une operation.
     * Debite les frais de publication (0.500 DT) du solde de l'auteur.
     * Interaction avec User : debiterSolde()
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
            nextId = idReserve; // rollback ID
            throw e;
        }
        posts.add(post);
        return post;
    }

    /**
     * CF-5 : Afficher tous les posts publies.
     */
    public void voirPosts() {
        List<Post> publies = posts.stream()
                .filter(Post::isPublie)
                .collect(Collectors.toList());
        if (publies.isEmpty())
            System.out.println("Aucun post publie.");
        else {
            System.out.println("=== Posts publies (" + publies.size() + ") ===");
            // Posts epingles en premier
            publies.stream().filter(Post::isEpingle).forEach(System.out::println);
            publies.stream().filter(p -> !p.isEpingle()).forEach(System.out::println);
        }
    }

    /**
     * CF-15 : Supprimer un post — auteur ou admin seulement.
     * Rembourse les frais de publication a l'auteur.
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
        // Remboursement frais si post publie
        if (cible.isPublie() && cible.getAuteur() != null)
            cible.getAuteur().crediterSolde(FRAIS_PUBLICATION);
        posts.remove(cible);
    }

    /**
     * Moderation : supprimer un post signale (admin uniquement).
     * Le post doit avoir au moins 1 signalement.
     * Rembourse les frais a l'auteur.
     */
    public void modererPost(int postId, User moderateur) {
        if (moderateur == null || !"admin".equalsIgnoreCase(moderateur.getRole()))
            throw new IllegalStateException("Seul un admin peut moderer un post.");
        Post cible = trouverParId(postId);
        if (cible == null)
            throw new IllegalArgumentException("Post#" + postId + " introuvable.");
        if (cible.getSignalements().isEmpty())
            throw new IllegalStateException("Post#" + postId + " n'a aucun signalement.");
        // Remboursement auteur
        if (cible.isPublie() && cible.getAuteur() != null)
            cible.getAuteur().crediterSolde(FRAIS_PUBLICATION);
        posts.remove(cible);
    }

    /**
     * Rechercher des posts publies par mot-cle dans le titre.
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
     */
    public List<Post> classerParPopularite() {
        return posts.stream()
                .filter(Post::isPublie)
                .sorted(Comparator.comparingInt(Post::getLikes).reversed())

                .collect(Collectors.toList());
    }

    /**
     * Trouver un post par son ID.
     */
    public Post trouverParId(int id) {
        return posts.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**

     * Afficher les détails d'un post spécifique.

     * Afficher les details d'un post specifique.

     */
    public void afficherDetails(int id) {
        Post post = trouverParId(id);
        if (post == null)
            throw new IllegalArgumentException("Post#" + id + " introuvable.");
        post.afficherDetails();
    }

    // --- Getters / Setters ---
    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) { this.posts = posts; }

    public int getNextId() { return nextId; }
    public void setNextId(int nextId) { this.nextId = nextId; }
}
