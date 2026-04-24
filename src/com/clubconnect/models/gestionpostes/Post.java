package com.clubconnect.models.gestionpostes;

import com.clubconnect.models.authentification.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 *  DOCUMENTATION TP3 — Approche IA assistee
 * ============================================================
 *  METHODE : publier()
 *  Prompt utilise :
 *    "Implemente publier() pour un Post. Verifier titre, contenu
 *     et auteur non vides, marquer comme publie avec date."
 *  Code genere par l'IA :
 *    public void publier() {
 *        if (titre == null || titre.isEmpty()) throw ...;
 *        this.publie = true;
 *        this.datePublication = new Date();
 *    }
 *  Corrections humaines :
 *    - Validation auteur non null
 *    - Verification post non deja publie
 *    - trim() pour les espaces
 * ============================================================
 *  METHODE : epingler() / desepingler()
 *  Prompt utilise :
 *    "Ajoute epingler(User demandeur). Seul auteur ou admin autorise."
 *  Code genere par l'IA :
 *    public void epingler(User demandeur) {
 *        if (!demandeur.getUsername().equals(auteur.getUsername()))
 *            throw new IllegalStateException("Non autorise.");
 *        this.epingle = true;
 *    }
 *  Corrections humaines :
 *    - Comparaison par ID (plus robuste)
 *    - Ajout cas admin
 *    - desepingler() symetrique
 * ============================================================
 *  METHODE : signalerPost()
 *  Prompt utilise :
 *    "Implemente signalerPost(User signaleur, String raison).
 *     Pas de signalement de son propre post, pas de doublon."
 *  Code genere par l'IA :
 *    public void signalerPost(User signaleur, String raison) {
 *        if (signaleur.getId() == auteur.getId()) throw ...;
 *        signalements.add(signaleur.getUsername() + " : " + raison);
 *    }
 *  Corrections humaines :
 *    - Verification doublon par utilisateur
 *    - Cle prefixee [signalement:username]
 * ============================================================
 */
public class Post {

    // --- Champs prives ---
    private int id;
    private String titre;
    private String contenu;
    private User auteur;
    private List<String> commentaires;
    private List<String> signalements;
    private int likes;
    private boolean publie;
    private boolean epingle;
    private Date datePublication;

    // --- Constructeur vide ---
    public Post() {
        this.commentaires = new ArrayList<>();
        this.signalements = new ArrayList<>();
        this.likes        = 0;
        this.publie       = false;
        this.epingle      = false;
    }

    // --- Constructeur complet ---
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
    //  METHODES METIER
    // ============================================================

    /** CF-4 : Publier le post. */
    public void publier() {
        if (auteur == null)
            throw new IllegalStateException("Auteur invalide.");
        if (titre == null || titre.trim().isEmpty())
            throw new IllegalStateException("Le titre ne peut pas etre vide.");
        if (contenu == null || contenu.trim().isEmpty())
            throw new IllegalStateException("Le contenu ne peut pas etre vide.");
        if (publie)
            throw new IllegalStateException("Post#" + id + " est deja publie.");
        this.publie          = true;
        this.datePublication = new Date();
    }

    /** CF-10 : Ajouter un commentaire. Post doit etre publie. */
    public void ajouterCommentaire(User utilisateur, String texte) {
        if (utilisateur == null)
            throw new IllegalArgumentException("Utilisateur invalide.");
        if (texte == null || texte.trim().isEmpty())
            throw new IllegalArgumentException("Le commentaire ne peut pas etre vide.");
        if (!publie)
            throw new IllegalStateException("Impossible de commenter un post non publie.");
        commentaires.add(utilisateur.getUsername() + " : " + texte.trim());
    }

    /** CF-11 : Liker le post. Anti-doublon + auteur ne peut pas liker son post. */
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

    /** Retirer son like. */
    public void retirerLike(User utilisateur) {
        if (utilisateur == null)
            throw new IllegalArgumentException("Utilisateur invalide.");
        String marqueLike = "[like:" + utilisateur.getUsername() + "]";
        if (!commentaires.contains(marqueLike))
            throw new IllegalStateException(utilisateur.getUsername() + " n'a pas like ce post.");
        commentaires.remove(marqueLike);
        likes--;
    }

    /** Epingler — auteur ou admin seulement. */
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

    /** Desepingler — auteur ou admin seulement. */
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

    /** Signaler un post. Pas de doublon, pas de signalement de son propre post. */
    public void signalerPost(User signaleur, String raison) {
        if (signaleur == null)
            throw new IllegalArgumentException("Signaleur invalide.");
        if (raison == null || raison.trim().isEmpty())
            throw new IllegalArgumentException("La raison est obligatoire.");
        if (auteur != null && auteur.getId() == signaleur.getId())
            throw new IllegalStateException("Impossible de signaler son propre post.");
        String cle = "[signalement:" + signaleur.getUsername() + "]";
        if (signalements.stream().anyMatch(s -> s.startsWith(cle)))
            throw new IllegalStateException(signaleur.getUsername() + " a deja signale ce post.");
        signalements.add(cle + " " + raison.trim());
    }

    /** Supprimer un commentaire — auteur du post ou admin. */
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
            throw new IndexOutOfBoundsException("Index invalide : " + index);
        commentaires.remove(visibles.get(index));
    }

    /** Afficher les details complets du post. */
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

    @Override
    public String toString() {
        long nbComm = commentaires.stream().filter(c -> !c.startsWith("[like:")).count();
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
