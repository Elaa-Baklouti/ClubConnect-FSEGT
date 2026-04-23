import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *
 *  ÉTAPE 1 — Squelette AGL (déterministe) :
 *    Structure générée depuis le diagramme de classes.
 *    Package : com.clubconnect.models
 *    Pattern : champs privés → constructeurs (vide + complet)
 *    → méthodes métier (signatures) → getters/setters
 *    Nommage français : ajouterCommentaire(), liker(),
 *    supprimerCommentaire(), afficherDetails()
 *
 *  ÉTAPE 2 — Implémentation IA assistée :
 *    Prompt utilisé :
 *      "Implémente la classe Post pour un réseau social de clubs
 *       universitaires. Elle doit avoir : id, titre, contenu,
 *       auteur (User), liste de commentaires (String), compteur
 *       de likes. Ajoute toString(), afficherDetails(), et les
 *       méthodes métier ajouterCommentaire(), liker(),
 *       supprimerCommentaire()."
 *
 *    Code généré par l'IA (base) :
 *      - Constructeurs, getters/setters, toString()
 *      - Logique de base pour ajouterCommentaire() et liker()
 *
 *    Corrections humaines :
 *      - Validation auteur non null
 *      - Anti-doublon sur liker() via marqueur interne
 *      - supprimerCommentaire() avec vérification d'index
 *      - afficherDetails() filtre les marqueurs [like:]
 * ============================================================
 */
public class Post {

    // --- Champs privés ---
    private int id;
    private String titre;
    private String contenu;
    private User auteur;
    private List<String> commentaires;
    private int likes;

    // --- Constructeur vide ---
    public Post() {
        this.commentaires = new ArrayList<>();
        this.likes = 0;
    }

    // --- Constructeur complet ---
    public Post(int id, String titre, String contenu, User auteur) {
        this.id           = id;
        this.titre        = titre;
        this.contenu      = contenu;
        this.auteur       = auteur;
        this.commentaires = new ArrayList<>();
        this.likes        = 0;
    }

    // ============================================================
    //  MÉTHODES MÉTIER
    // ============================================================

    /**
     * CF-10 : Ajouter un commentaire au post.
     * Format stocké : "username : texte"
     */
    public void ajouterCommentaire(User utilisateur, String texte) {
        if (utilisateur == null || texte == null || texte.isBlank())
            throw new IllegalArgumentException("Utilisateur ou texte invalide.");
        commentaires.add(utilisateur.getUsername() + " : " + texte);
        System.out.println("Commentaire ajouté sur Post#" + id + " par " + utilisateur.getUsername());
    }

    /**
     * CF-11 : Liker le post.
     * Un utilisateur ne peut liker qu'une seule fois.
     */
    public void liker(User utilisateur) {
        if (utilisateur == null)
            throw new IllegalArgumentException("Utilisateur invalide.");
        String marqueLike = "[like:" + utilisateur.getUsername() + "]";
        if (commentaires.contains(marqueLike))
            throw new IllegalStateException(utilisateur.getUsername() + " a déjà liké ce post.");
        commentaires.add(marqueLike);
        likes++;
        System.out.println(utilisateur.getUsername() + " a liké Post#" + id + " (" + likes + " likes)");
    }

    /**
     * Supprimer un commentaire par index.
     */
    public void supprimerCommentaire(int index) {
        if (index < 0 || index >= commentaires.size())
            throw new IndexOutOfBoundsException("Index commentaire invalide : " + index);
        String supprime = commentaires.remove(index);
        System.out.println("Commentaire supprimé : " + supprime);
    }

    /**
     * Afficher les détails complets du post.
     */
    public void afficherDetails() {
        System.out.println("=== Détails Post ===");
        System.out.println("ID       : " + id);
        System.out.println("Titre    : " + titre);
        System.out.println("Contenu  : " + contenu);
        System.out.println("Auteur   : " + (auteur != null ? auteur.getUsername() : "N/A"));
        System.out.println("Likes    : " + likes);
        List<String> visibles = commentaires.stream()
                .filter(c -> !c.startsWith("[like:"))
                .collect(Collectors.toList());
        System.out.println("Commentaires (" + visibles.size() + ") :");
        for (int i = 0; i < visibles.size(); i++)
            System.out.println("  [" + i + "] " + visibles.get(i));
    }

    // --- toString ---
    @Override
    public String toString() {
        long nbCommentaires = commentaires.stream().filter(c -> !c.startsWith("[like:")).count();
        return "Post#" + id + " [" + titre + "] par "
             + (auteur != null ? auteur.getUsername() : "N/A")
             + " | likes=" + likes
             + " | commentaires=" + nbCommentaires;
    }

    // --- Getters / Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public User getAuteur() { return auteur; }
    public void setAuteur(User auteur) { this.auteur = auteur; }

    public List<String> getCommentaires() { return commentaires; }
    public void setCommentaires(List<String> commentaires) { this.commentaires = commentaires; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
}
