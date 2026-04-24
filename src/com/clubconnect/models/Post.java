package com.clubconnect.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *
 *  ETAPE 1 — Squelette AGL (deterministe) :
 *    Structure generee depuis le diagramme de classes.
 *    Package : com.clubconnect.models
 *    Pattern : champs prives -> constructeurs (vide + complet)
 *             -> methodes metier (signatures) -> getters/setters
 *    Nommage francais : publier(), ajouterCommentaire(), liker(),
 *    retirerLike(), epingler(), desepingler(),
 *    signalerPost(), supprimerCommentaire(), afficherDetails()
 *
 *  ETAPE 2 — Implementation IA assistee :
 *    Prompt utilise :
 *      "Implemente la classe Post pour un reseau social de clubs
 *       universitaires. Champs : id, titre, contenu, auteur (User),
 *       liste commentaires (String), liste signalements (String),
 *       compteur likes, boolean publie, boolean epingle, date
 *       publication. Ajoute toString(), afficherDetails(), et les
 *       methodes metier : publier(), ajouterCommentaire(), liker(),
 *       retirerLike(), epingler(), signalerPost()."
 *
 *    Code genere par l'IA (base) :
 *      - Constructeurs, getters/setters, toString()
 *      - Logique de base pour publier(), ajouterCommentaire(), liker()
 *
 *    Corrections humaines :
 *      - Anti-doublon sur liker() via marqueur [like:username]
 *      - epingler() : comparaison par ID (plus robuste que username)
 *      - signalerPost() : cle prefixee [signalement:username]
 *      - supprimerCommentaire() filtre les marqueurs internes
 *      - afficherDetails() masque les marqueurs [like:]
 * ============================================================
 */
public class Post {

    // ============================================================
    //  ETAPE 1 — Champs prives (squelette AGL)
    // ============================================================

    private int          id;
    private String       titre;
    private String       contenu;
    private User         auteur;
    private List<String> commentaires;   // format : "username : texte"
    private List<String> signalements;   // format : "[signalement:username] raison"
    private int          likes;
    private boolean      publie;
    private boolean      epingle;
    private Date         datePublication;

    // ============================================================
    //  ETAPE 1 — Constructeurs
    // ============================================================

    /** Constructeur vide */
    public Post() {
        this.commentaires = new ArrayList<>();
        this.signalements = new ArrayList<>();
        this.likes        = 0;
        this.publie       = false;
        this.epingle      = false;
    }

    /** Constructeur complet */
    public Post(int id, String titre, String contenu, User auteur) {
        this.id           = id;
        this.titre        = titre;
        this.contenu      = contenu;
        this.auteur       = auteur;
        this.commentaires = new ArrayList<>();
        this.signalements = new ArrayList<>();
        this.likes        = 0;
        this.publie       = false;
        this.epingle      = false;
    }

    // ============================================================
    //  ETAPE 2 — Methodes metier (implementation)
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
     * Interaction avec User : appel getUsername()
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
     * Anti-doublon via marqueur interne. L'auteur ne peut pas liker son post.
     * Interaction avec User : getId(), getUsername()
     */
    public void liker(User utilisateur) {
        if (utilisateur == null)
            throw new IllegalArgumentException("Utilisateur invalide.");
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
     * Interaction avec User : getUsername()
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
     * Interaction avec User : getId(), getRole()
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
     * Interaction avec User : getId(), getRole()
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
     * Pas de doublon, pas de signalement de son propre post.
     * Interaction avec User : getId(), getUsername()
     */
    public void signalerPost(User signaleur, String raison) {
        if (signaleur == null)
            throw new IllegalArgumentException("Signaleur invalide.");
        if (raison == null || raison.trim().isEmpty())
            throw new IllegalArgumentException("La raison du signalement est obligatoire.");
        if (auteur != null && auteur.getId() == signaleur.getId())
            throw new IllegalStateException("Impossible de signaler son propre post.");
        String cle = "[signalement:" + signaleur.getUsername() + "]";
        if (signalements.stream().anyMatch(s -> s.startsWith(cle)))
            throw new IllegalStateException(signaleur.getUsername() + " a deja signale ce post.");
        signalements.add(cle + " " + raison.trim());
    }

    /**
     * Supprimer un commentaire par index (visible uniquement).
     * Auteur du post ou admin seulement.
     * Interaction avec User : getId(), getRole()
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
        commentaires.remove(visibles.get(index));
    }

    /**
     * Afficher les details complets du post.
     * Masque les marqueurs internes [like:].
     */
    public void afficherDetails() {
        System.out.println("=== Details Post#" + id + " ===");
        System.out.println("Titre        : " + titre + (epingle ? " [EPINGLE]" : ""));
        System.out.println("Contenu      : " + contenu);
        System.out.println("Auteur       : " + (auteur != null ? auteur.getUsername() : "N/A"));
        System.out.println("Statut       : " + (publie ? "Publie le " + datePublication : "Brouillon"));
        System.out.println("Likes        : " + likes);
        System.out.println("Signalements : " + signalements.size());
        List<String> visibles = commentaires.stream()
                .filter(c -> !c.startsWith("[like:"))
                .collect(Collectors.toList());
        System.out.println("Commentaires (" + visibles.size() + ") :");
        for (int i = 0; i < visibles.size(); i++)
            System.out.println("  [" + i + "] " + visibles.get(i));
    }

    // ============================================================
    //  toString
    // ============================================================

    @Override
    public String toString() {
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

    // ============================================================
    //  Getters / Setters
    // ============================================================

    public int     getId()                          { return id; }
    public void    setId(int id)                    { this.id = id; }

    public String  getTitre()                       { return titre; }
    public void    setTitre(String titre)           { this.titre = titre; }

    public String  getContenu()                     { return contenu; }
    public void    setContenu(String contenu)       { this.contenu = contenu; }

    public User    getAuteur()                      { return auteur; }
    public void    setAuteur(User auteur)           { this.auteur = auteur; }

    public List<String> getCommentaires()           { return commentaires; }
    public void    setCommentaires(List<String> c)  { this.commentaires = c; }

    public List<String> getSignalements()           { return signalements; }

    public int     getLikes()                       { return likes; }
    public void    setLikes(int likes)              { this.likes = likes; }

    public boolean isPublie()                       { return publie; }
    public boolean isEpingle()                      { return epingle; }
    public Date    getDatePublication()             { return datePublication; }
}
