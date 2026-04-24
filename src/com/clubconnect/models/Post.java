package com.clubconnect.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 *  DOCUMENTATION TP3 — Approche IA assistee
 * ============================================================
 *
 *  METHODE : publier()
 *  --------------------
 *  Prompt utilise :
 *    "Implemente publier() dans la classe Post Java pour un reseau
 *     social de clubs universitaires. Verifier que le titre et le
 *     contenu ne sont pas vides, que l'auteur est non null, et que
 *     le post n'est pas deja publie. Marquer comme publie avec la
 *     date courante. Lancer des exceptions metier explicites."
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
 *  Code final apres corrections humaines :
 *    - Ajout validation auteur non null
 *    - Utilisation de trim() pour detecter les espaces seuls
 *    - Verification post non deja publie avant de publier
 *    - Message d'erreur enrichi avec Post#id
 *
 * ============================================================
 *
 *  METHODE : liker()
 *  ------------------
 *  Prompt utilise :
 *    "Implemente liker(User utilisateur) dans la classe Post.
 *     Un utilisateur ne peut liker qu'une seule fois (anti-doublon).
 *     L'auteur du post ne peut pas liker son propre post.
 *     Le post doit etre publie. Incrementer le compteur likes.
 *     Stocker le like via un marqueur interne dans la liste
 *     commentaires pour eviter un doublon."
 *
 *  Code genere par l'IA :
 *    public void liker(User utilisateur) {
 *        if (utilisateur == null)
 *            throw new IllegalArgumentException("Utilisateur invalide.");
 *        if (commentaires.contains("[like:" + utilisateur.getUsername() + "]"))
 *            throw new IllegalStateException("Deja like.");
 *        commentaires.add("[like:" + utilisateur.getUsername() + "]");
 *        likes++;
 *    }
 *
 *  Code final apres corrections humaines :
 *    - Ajout verification post publie avant like
 *    - Ajout verification auteur != utilisateur (comparaison par ID)
 *    - Extraction du marqueur dans une variable locale pour lisibilite
 *
 * ============================================================
 *
 *  METHODE : signalerPost()
 *  -------------------------
 *  Prompt utilise :
 *    "Implemente signalerPost(User signaleur, String raison) dans
 *     la classe Post. Un utilisateur ne peut pas signaler son propre
 *     post. Un meme utilisateur ne peut signaler qu'une seule fois.
 *     Stocker les signalements avec le format [signalement:username]
 *     suivi de la raison."
 *
 *  Code genere par l'IA :
 *    public void signalerPost(User signaleur, String raison) {
 *        if (signaleur.getId() == auteur.getId())
 *            throw new IllegalStateException("Impossible de signaler son propre post.");
 *        signalements.add("[signalement:" + signaleur.getUsername() + "] " + raison);
 *    }
 *
 *  Code final apres corrections humaines :
 *    - Ajout validation signaleur non null et raison non vide
 *    - Verification doublon via stream().anyMatch() sur la cle prefixee
 *    - Trim() sur la raison avant stockage
 *
 * ============================================================
 *
 *  METHODE : epingler() / desepingler()
 *  --------------------------------------
 *  Prompt utilise :
 *    "Implemente epingler(User demandeur) dans la classe Post.
 *     Seul l'auteur du post ou un utilisateur avec le role admin
 *     peut epingler. Empecher d'epingler un post deja epingle.
 *     Ajoute aussi desepingler() symetrique."
 *
 *  Code genere par l'IA :
 *    public void epingler(User demandeur) {
 *        if (!demandeur.getUsername().equals(auteur.getUsername()))
 *            throw new IllegalStateException("Non autorise.");
 *        this.epingle = true;
 *    }
 *
 *  Code final apres corrections humaines :
 *    - Comparaison par ID (plus robuste que par username)
 *    - Ajout du cas admin via getRole()
 *    - Verification post non deja epingle
 *    - desepingler() symetrique avec verification post epingle
 *
 * ============================================================
 */
public class Post {

    // ============================================================
    //  Champs prives
    // ============================================================

    private int          id;
    private String       titre;
    private String       contenu;
    private User         auteur;
    private List<String> commentaires;   // "username : texte" ou "[like:username]"
    private List<String> signalements;   // "[signalement:username] raison"
    private int          likes;
    private boolean      publie;
    private boolean      epingle;
    private Date         datePublication;

    // ============================================================
    //  Constructeurs
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
    //  Methodes metier — logique reelle
    // ============================================================

    /**
     * CF-4 : Publier le post.
     * Valide auteur, titre et contenu. Marque comme publie avec timestamp.
     */
    public void publier() {
        if (auteur == null)
            throw new IllegalStateException(
                "Auteur invalide : impossible de publier Post#" + id + ".");
        if (titre == null || titre.trim().isEmpty())
            throw new IllegalStateException(
                "Le titre ne peut pas etre vide (Post#" + id + ").");
        if (contenu == null || contenu.trim().isEmpty())
            throw new IllegalStateException(
                "Le contenu ne peut pas etre vide (Post#" + id + ").");
        if (publie)
            throw new IllegalStateException(
                "Post#" + id + " est deja publie.");
        this.publie          = true;
        this.datePublication = new Date();
    }

    /**
     * CF-10 : Ajouter un commentaire au post.
     * Le post doit etre publie. Format stocke : "username : texte"
     * Interaction avec User : getUsername()
     *
     * @param utilisateur Auteur du commentaire (non null)
     * @param texte       Texte du commentaire (non vide)
     */
    public void ajouterCommentaire(User utilisateur, String texte) {
        if (utilisateur == null)
            throw new IllegalArgumentException("Utilisateur invalide.");
        if (texte == null || texte.trim().isEmpty())
            throw new IllegalArgumentException("Le commentaire ne peut pas etre vide.");
        if (!publie)
            throw new IllegalStateException(
                "Impossible de commenter Post#" + id + " : non publie.");
        commentaires.add(utilisateur.getUsername() + " : " + texte.trim());
    }

    /**
     * CF-11 : Liker le post.
     * Anti-doublon via marqueur [like:username]. Auteur ne peut pas liker son post.
     * Interaction avec User : getId(), getUsername()
     *
     * @param utilisateur Utilisateur qui like (non null)
     */
    public void liker(User utilisateur) {
        if (utilisateur == null)
            throw new IllegalArgumentException("Utilisateur invalide.");
        if (!publie)
            throw new IllegalStateException(
                "Impossible de liker Post#" + id + " : non publie.");
        if (auteur != null && auteur.getId() == utilisateur.getId())
            throw new IllegalStateException(
                "L'auteur ne peut pas liker son propre post.");
        String marqueLike = "[like:" + utilisateur.getUsername() + "]";
        if (commentaires.contains(marqueLike))
            throw new IllegalStateException(
                utilisateur.getUsername() + " a deja like ce post.");
        commentaires.add(marqueLike);
        likes++;
    }

    /**
     * Retirer son like d'un post.
     * Decremente le compteur et retire le marqueur interne.
     * Interaction avec User : getUsername()
     *
     * @param utilisateur Utilisateur qui retire son like
     */
    public void retirerLike(User utilisateur) {
        if (utilisateur == null)
            throw new IllegalArgumentException("Utilisateur invalide.");
        String marqueLike = "[like:" + utilisateur.getUsername() + "]";
        if (!commentaires.contains(marqueLike))
            throw new IllegalStateException(
                utilisateur.getUsername() + " n'a pas like ce post.");
        commentaires.remove(marqueLike);
        likes--;
    }

    /**
     * Epingler le post — auteur ou admin seulement.
     * Interaction avec User : getId(), getRole()
     *
     * @param demandeur Utilisateur qui epingle
     */
    public void epingler(User demandeur) {
        if (demandeur == null)
            throw new IllegalArgumentException("Demandeur invalide.");
        boolean estAuteur = auteur != null && auteur.getId() == demandeur.getId();
        boolean estAdmin  = "admin".equalsIgnoreCase(demandeur.getRole());
        if (!estAuteur && !estAdmin)
            throw new IllegalStateException(
                "Seul l'auteur ou un admin peut epingler ce post.");
        if (epingle)
            throw new IllegalStateException(
                "Post#" + id + " est deja epingle.");
        this.epingle = true;
    }

    /**
     * Desepingler le post — auteur ou admin seulement.
     * Interaction avec User : getId(), getRole()
     *
     * @param demandeur Utilisateur qui desepingle
     */
    public void desepingler(User demandeur) {
        if (demandeur == null)
            throw new IllegalArgumentException("Demandeur invalide.");
        boolean estAuteur = auteur != null && auteur.getId() == demandeur.getId();
        boolean estAdmin  = "admin".equalsIgnoreCase(demandeur.getRole());
        if (!estAuteur && !estAdmin)
            throw new IllegalStateException(
                "Seul l'auteur ou un admin peut desepingler ce post.");
        if (!epingle)
            throw new IllegalStateException(
                "Post#" + id + " n'est pas epingle.");
        this.epingle = false;
    }

    /**
     * Signaler un post pour contenu inapproprie.
     * Pas de doublon par utilisateur. Auteur ne peut pas signaler son post.
     * Interaction avec User : getId(), getUsername()
     *
     * @param signaleur Utilisateur qui signale
     * @param raison    Raison du signalement (non vide)
     */
    public void signalerPost(User signaleur, String raison) {
        if (signaleur == null)
            throw new IllegalArgumentException("Signaleur invalide.");
        if (raison == null || raison.trim().isEmpty())
            throw new IllegalArgumentException(
                "La raison du signalement est obligatoire.");
        if (auteur != null && auteur.getId() == signaleur.getId())
            throw new IllegalStateException(
                "Impossible de signaler son propre post.");
        String cle = "[signalement:" + signaleur.getUsername() + "]";
        if (signalements.stream().anyMatch(s -> s.startsWith(cle)))
            throw new IllegalStateException(
                signaleur.getUsername() + " a deja signale ce post.");
        signalements.add(cle + " " + raison.trim());
    }

    /**
     * Supprimer un commentaire visible par index.
     * Auteur du post ou admin seulement.
     * Interaction avec User : getId(), getRole()
     *
     * @param index     Index du commentaire visible (0-base)
     * @param demandeur Utilisateur qui supprime
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
            throw new IndexOutOfBoundsException(
                "Index commentaire invalide : " + index);
        commentaires.remove(visibles.get(index));
    }

    /**
     * Afficher les details complets du post.
     * Masque les marqueurs internes [like:].
     */
    public void afficherDetails() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        System.out.println("=== Details Post#" + id + " ===");
        System.out.println("Titre        : " + titre + (epingle ? " [EPINGLE]" : ""));
        System.out.println("Contenu      : " + contenu);
        System.out.println("Auteur       : "
            + (auteur != null ? auteur.getUsername() : "N/A"));
        System.out.println("Statut       : "
            + (publie ? "Publie le " + sdf.format(datePublication) : "Brouillon"));
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

    public int          getId()                         { return id; }
    public void         setId(int id)                   { this.id = id; }
    public String       getTitre()                      { return titre; }
    public void         setTitre(String titre)          { this.titre = titre; }
    public String       getContenu()                    { return contenu; }
    public void         setContenu(String contenu)      { this.contenu = contenu; }
    public User         getAuteur()                     { return auteur; }
    public void         setAuteur(User auteur)          { this.auteur = auteur; }
    public List<String> getCommentaires()               { return commentaires; }
    public void         setCommentaires(List<String> c) { this.commentaires = c; }
    public List<String> getSignalements()               { return signalements; }
    public int          getLikes()                      { return likes; }
    public void         setLikes(int likes)             { this.likes = likes; }
    public boolean      isPublie()                      { return publie; }
    public boolean      isEpingle()                     { return epingle; }
    public Date         getDatePublication()            { return datePublication; }
}
