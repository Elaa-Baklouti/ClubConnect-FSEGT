import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *  ETAPE 1 — Squelette AGL (deterministe) :
 *    Pattern : champs prives → constructeurs (vide + complet)
 *    → methodes metier (signatures) → getters/setters
 *    Nommage francais : ajouterCommentaire(), liker(),
 *    supprimerCommentaire(), afficherDetails()
 *
 *  ETAPE 2 — Implementation IA assistee :
 *    Prompt utilise :
 *      "Implemente la classe Post pour un reseau social de clubs.
 *       Champs : id, titre, contenu, auteur (User), commentaires,
 *       likes. Ajoute toString(), afficherDetails(),
 *       ajouterCommentaire(), liker(), supprimerCommentaire()."
 *    Corrections humaines :
 *      - Anti-doublon sur liker() via marqueur interne [like:x]
 *      - supprimerCommentaire() avec verification d'index
 *      - afficherDetails() filtre les marqueurs [like:]
 * ============================================================
 */
public class Post {

    // --- Champs prives ---
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
    //  METHODES METIER
    // ============================================================

    /** CF-10 : Ajouter un commentaire. Format : "username : texte" */
    public void ajouterCommentaire(User utilisateur, String texte) {
        if (utilisateur == null || texte == null || texte.trim().isEmpty())
            throw new IllegalArgumentException("Utilisateur ou texte invalide.");
        commentaires.add(utilisateur.getUsername() + " : " + texte);
        System.out.println("Commentaire ajoute sur Post#" + id
                + " par " + utilisateur.getUsername());
    }

    /** CF-11 : Liker le post — un seul like par utilisateur. */
    public void liker(User utilisateur) {
        if (utilisateur == null)
            throw new IllegalArgumentException("Utilisateur invalide.");
        String marqueLike = "[like:" + utilisateur.getUsername() + "]";
        if (commentaires.contains(marqueLike))
            throw new IllegalStateException(
                    utilisateur.getUsername() + " a deja like ce post.");
        commentaires.add(marqueLike);
        likes++;
        System.out.println(utilisateur.getUsername()
                + " a like Post#" + id + " (" + likes + " likes)");
    }

    /** Supprimer un commentaire par index. */
    public void supprimerCommentaire(int index) {
        if (index < 0 || index >= commentaires.size())
            throw new IndexOutOfBoundsException("Index invalide : " + index);
        String supprime = commentaires.remove(index);
        System.out.println("Commentaire supprime : " + supprime);
    }

    /** Afficher les details complets du post. */
    public void afficherDetails() {
        System.out.println("=== Details Post ===");
        System.out.println("ID       : " + id);
        System.out.println("Titre    : " + titre);
        System.out.println("Contenu  : " + contenu);
        System.out.println("Auteur   : "
                + (auteur != null ? auteur.getUsername() : "N/A"));
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
        long nbComm = commentaires.stream()
                .filter(c -> !c.startsWith("[like:")).count();
        return "Post#" + id + " [" + titre + "] par "
             + (auteur != null ? auteur.getUsername() : "N/A")
             + " | likes=" + likes + " | commentaires=" + nbComm;
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
    public void setCommentaires(List<String> c) { this.commentaires = c; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
}
