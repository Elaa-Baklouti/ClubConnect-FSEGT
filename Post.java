import java.util.ArrayList;
import java.util.Date;
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
=======
 *  DOCUMENTATION TP3 — Approche IA assistee
 * 
 *
 *  METHODE : publier()
 *  -------------------
 *  Prompt utilise :
 *    "Implemente une methode publier() pour un Post dans un reseau
 *     social de clubs universitaires. Elle doit verifier que le titre
 *     et le contenu ne sont pas vides, que l'auteur est valide, puis
 *     marquer le post comme publie avec une date de publication.
 *     Lancer des exceptions metier si les regles sont violees."
 *
 *  Code genere par l'IA :
 *    public void publier() {
 *        if (titre == null || titre.isEmpty())
 *            throw new IllegalStateException("Titre vide.");
 *        if (contenu == null || contenu.isEmpty())
 *            throw new IllegalStateException("Contenu vide.");
 *        this.publie = true;
 *        this.datePublication = new Date();
 *    }
 *
 *  Corrections humaines :
 *    - Ajout validation auteur non null
 *    - Ajout verification post non deja publie
 *    - Utilisation de trim() pour les espaces
 *
 * ============================================================
 *
 *  METHODE : epingler() / desepingler()
 *  -------------------------------------
 *  Prompt utilise :
 *    "Ajoute une methode epingler() a la classe Post. Seul l'auteur
 *     ou un admin peut epingler un post. Passer l'utilisateur demandeur
 *     en parametre et lancer une exception si non autorise."
 *
 *  Code genere par l'IA :
 *    public void epingler(User demandeur) {
 *        if (!demandeur.getUsername().equals(auteur.getUsername()))
 *            throw new IllegalStateException("Non autorise.");
 *        this.epingle = true;
 *    }
 *
 *  Corrections humaines :
 *    - Comparaison par ID (plus robuste que par username)
 *    - Ajout du cas admin
 *    - Ajout de desepingler() symetrique
 *
 * ============================================================
 *
 *  METHODE : signalerPost()
 *  ------------------------
 *  Prompt utilise :
 *    "Implemente signalerPost(User signaleur, String raison) dans
 *     la classe Post. Un utilisateur ne peut pas signaler son propre
 *     post. Un post ne peut etre signale qu'une seule fois par
 *     utilisateur. Stocker les signalements."
 *
 *  Code genere par l'IA :
 *    public void signalerPost(User signaleur, String raison) {
 *        if (signaleur.getId() == auteur.getId())
 *            throw new IllegalStateException("Impossible de signaler son propre post.");
 *        signalements.add(signaleur.getUsername() + " : " + raison);
 *    }
 *
 *  Corrections humaines :
 *    - Ajout verification doublon signalement par meme utilisateur
 *    - Ajout compteur signalements pour moderation
 *

 * ============================================================
 */
public class Post {


    // --- Champs privés ---

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

    private List<String> commentaires;   // format : "username : texte"
    private List<String> signalements;   // format : "username : raison"
    private int likes;
    private boolean publie;
    private boolean epingle;
    private Date datePublication;

    // --- Constructeur vide ---
    public Post() {
        this.commentaires  = new ArrayList<>();
        this.signalements  = new ArrayList<>();
        this.likes         = 0;
        this.publie        = false;
        this.epingle       = false;

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

        this.id            = id;
        this.titre         = titre;
        this.contenu       = contenu;
        this.auteur        = auteur;
        this.commentaires  = new ArrayList<>();
        this.signalements  = new ArrayList<>();
        this.likes         = 0;
        this.publie        = false;
        this.epingle       = false;
    }

    // ============================================================
    //  METHODES METIER — logique reelle
    // ============================================================

    /**
     * CF-4 : Publier le post.
     * Valide titre, contenu et auteur avant de marquer comme publie.
     */
    public void publier() {
        if (auteur == null)
            throw new IllegalStateException("Auteur invalide : impossible de publier.");
        if (titre == null || titre.trim().isEmpty())
            throw new IllegalStateException("Le titre ne peut pas etre vide.");
        if (contenu == null || contenu.trim().isEmpty())
            throw new IllegalStateException("Le contenu ne peut pas etre vide.");
        if (publie)
            throw new IllegalStateException("Post#" + id + " est deja publie.");
        this.publie          = true;
        this.datePublication = new Date();
    }

    /**
     * CF-10 : Ajouter un commentaire.
     * Le post doit etre publie. Format stocke : "username : texte"
     */
    public void ajouterCommentaire(User utilisateur, String texte) {
        if (utilisateur == null)
            throw new IllegalArgumentException("Utilisateur invalide.");
        if (texte == null || texte.trim().isEmpty())
            throw new IllegalArgumentException("Le commentaire ne peut pas etre vide.");
        if (!publie)
            throw new IllegalStateException("Impossible de commenter un post non publie.");
        commentaires.add(utilisateur.getUsername() + " : " + texte.trim());

    }

    /**
     * CF-11 : Liker le post.
     * Un utilisateur ne peut liker qu'une seule fois.


     * L'auteur ne peut pas liker son propre post.

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

        if (!publie)
            throw new IllegalStateException("Impossible de liker un post non publie.");
        if (auteur != null && auteur.getId() == utilisateur.getId())
            throw new IllegalStateException("L'auteur ne peut pas liker son propre post.");
        String marqueLike = "[like:" + utilisateur.getUsername() + "]";
        if (commentaires.contains(marqueLike))
            throw new IllegalStateException(utilisateur.getUsername() + " a deja like ce post.");
        commentaires.add(marqueLike);
        likes++;
    }

    /**
     * Retirer son like d'un post.
     */
    public void retirerLike(User utilisateur) {
        if (utilisateur == null)
            throw new IllegalArgumentException("Utilisateur invalide.");
        String marqueLike = "[like:" + utilisateur.getUsername() + "]";
        if (!commentaires.contains(marqueLike))
            throw new IllegalStateException(utilisateur.getUsername() + " n'a pas like ce post.");
        commentaires.remove(marqueLike);
        likes--;
    }

    /**
     * Epingler le post — auteur ou admin seulement.
     */
    public void epingler(User demandeur) {
        if (demandeur == null)
            throw new IllegalArgumentException("Demandeur invalide.");
        boolean estAuteur = auteur != null && auteur.getId() == demandeur.getId();
        boolean estAdmin  = "admin".equalsIgnoreCase(demandeur.getRole());
        if (!estAuteur && !estAdmin)
            throw new IllegalStateException("Seul l'auteur ou un admin peut epingler ce post.");
        if (epingle)
            throw new IllegalStateException("Post#" + id + " est deja epingle.");
        this.epingle = true;
    }

    /**
     * Desepingler le post — auteur ou admin seulement.
     */
    public void desepingler(User demandeur) {
        if (demandeur == null)
            throw new IllegalArgumentException("Demandeur invalide.");
        boolean estAuteur = auteur != null && auteur.getId() == demandeur.getId();
        boolean estAdmin  = "admin".equalsIgnoreCase(demandeur.getRole());
        if (!estAuteur && !estAdmin)
            throw new IllegalStateException("Seul l'auteur ou un admin peut desepingler ce post.");
        if (!epingle)
            throw new IllegalStateException("Post#" + id + " n'est pas epingle.");
        this.epingle = false;
    }

    /**
     * Signaler un post pour contenu inapproprie.
     * Un utilisateur ne peut pas signaler son propre post ni signaler deux fois.
     */
    public void signalerPost(User signaleur, String raison) {
        if (signaleur == null)
            throw new IllegalArgumentException("Signaleur invalide.");
        if (raison == null || raison.trim().isEmpty())
            throw new IllegalArgumentException("La raison du signalement est obligatoire.");
        if (auteur != null && auteur.getId() == signaleur.getId())
            throw new IllegalStateException("Impossible de signaler son propre post.");
        String cle = "[signalement:" + signaleur.getUsername() + "]";
        boolean dejaSignale = signalements.stream()
                .anyMatch(s -> s.startsWith(cle));
        if (dejaSignale)
            throw new IllegalStateException(signaleur.getUsername() + " a deja signale ce post.");
        signalements.add(cle + " " + raison.trim());
    }

    /**
     * Supprimer un commentaire par index (visible uniquement).
     * Seul l'auteur du post ou un admin peut supprimer.
     */
    public void supprimerCommentaire(int index, User demandeur) {
        if (demandeur == null)
            throw new IllegalArgumentException("Demandeur invalide.");
        boolean estAuteur = auteur != null && auteur.getId() == demandeur.getId();
        boolean estAdmin  = "admin".equalsIgnoreCase(demandeur.getRole());
        if (!estAuteur && !estAdmin)
            throw new IllegalStateException("Permission refusee.");
        List<String> visibles = commentaires.stream()
                .filter(c -> !c.startsWith("[like:"))
                .collect(Collectors.toList());
        if (index < 0 || index >= visibles.size())
            throw new IndexOutOfBoundsException("Index commentaire invalide : " + index);
        String cible = visibles.get(index);
        commentaires.remove(cible);
    }

    /**
     * Afficher les details complets du post.
     */
    public void afficherDetails() {
        System.out.println("=== Details Post#" + id + " ===");
        System.out.println("Titre      : " + titre + (epingle ? " [EPINGLE]" : ""));
        System.out.println("Contenu    : " + contenu);
        System.out.println("Auteur     : " + (auteur != null ? auteur.getUsername() : "N/A"));
        System.out.println("Statut     : " + (publie ? "Publie le " + datePublication : "Brouillon"));
        System.out.println("Likes      : " + likes);
        System.out.println("Signalements : " + signalements.size());

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

        long nbComm = commentaires.stream()
                .filter(c -> !c.startsWith("[like:")).count();
        return "Post#" + id
             + (epingle ? " [EPINGLE]" : "")
             + " [" + titre + "] par "
             + (auteur != null ? auteur.getUsername() : "N/A")
             + " | likes=" + likes
             + " | commentaires=" + nbComm
             + " | " + (publie ? "publie" : "brouillon");

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

    public List<String> getSignalements() { return signalements; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public boolean isPublie() { return publie; }
    public boolean isEpingle() { return epingle; }
    public Date getDatePublication() { return datePublication; }
}
