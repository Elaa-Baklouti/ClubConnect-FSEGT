import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *  ÉTAPE 1 — Squelette AGL (déterministe) :
 *    Service CRUD posts en mémoire.
 *    Pattern : champs privés → constructeurs → méthodes métier
 *    → getters/setters
 *    Nommage français : creerPost(), voirPosts(),
 *    supprimerPost(), rechercherParTitre(), afficherDetails()
 *
 *  ÉTAPE 2 — Implémentation IA assistée :
 *    Prompt utilisé :
 *      "Crée un PostService Java avec liste en mémoire.
 *       Méthodes : creerPost(titre, contenu, auteur),
 *       voirPosts(), supprimerPost(id, demandeur),
 *       rechercherParTitre(mot)."
 *    Corrections humaines :
 *      - rechercherParTitre() insensible à la casse
 *      - Suppression : auteur OU admin seulement
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

    /** CF-4 : Créer un nouveau post. */
    public Post creerPost(String titre, String contenu, User auteur) {
        if (auteur == null)
            throw new IllegalArgumentException("L'auteur ne peut pas etre null.");
        if (titre == null || titre.isBlank())
            throw new IllegalArgumentException("Le titre est obligatoire.");
        Post post = new Post(nextId++, titre, contenu, auteur);
        posts.add(post);
        System.out.println("Post cree : " + titre + " par " + auteur.getUsername());
        return post;
    }

    /** CF-5 : Afficher tous les posts. */
    public void voirPosts() {
        if (posts.isEmpty())
            System.out.println("Aucun post disponible.");
        else {
            System.out.println("=== Liste des Posts ===");
            posts.forEach(System.out::println);
        }
    }

    /** CF-15 : Supprimer un post — auteur ou admin seulement. */
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
        posts.remove(cible);
        System.out.println("Post#" + id + " supprime par " + demandeur.getUsername());
    }

    /** Rechercher des posts par mot-clé dans le titre. */
    public List<Post> rechercherParTitre(String motCle) {
        if (motCle == null || motCle.isBlank())
            return new ArrayList<>(posts);
        return posts.stream()
                .filter(p -> p.getTitre().toLowerCase().contains(motCle.toLowerCase()))
                .collect(Collectors.toList());
    }

    /** Trouver un post par son ID. */
    public Post trouverParId(int id) {
        return posts.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /** Afficher les détails d'un post spécifique. */
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
