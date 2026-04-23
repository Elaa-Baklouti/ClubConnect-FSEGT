import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
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
 * ============================================================
 */
public class PostService {

    // --- Champs privés ---
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
