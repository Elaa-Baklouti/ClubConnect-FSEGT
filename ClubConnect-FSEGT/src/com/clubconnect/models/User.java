package com.clubconnect.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ============================================================
 *  DOCUMENTATION TP3 — Approche IA assistee
 * ============================================================
 *
 *  METHODE : debiterSolde() / crediterSolde()
 *  ------------------------------------------
 *  Prompt utilise :
 *    "Implemente debiterSolde(double montant) et crediterSolde(double montant)
 *     dans une classe User Java. Le solde est en Dinar Tunisien (DT).
 *     Valider que le montant est strictement positif. Pour le debit, verifier
 *     que le solde est suffisant. Arrondir a 3 decimales (millimes).
 *     Lancer des exceptions metier explicites."
 *
 *  Code genere par l'IA :
 *    public void debiterSolde(double montant) {
 *        if (montant <= 0) throw new IllegalArgumentException("Montant invalide.");
 *        if (solde < montant) throw new IllegalStateException("Solde insuffisant.");
 *        solde -= montant;
 *    }
 *    public void crediterSolde(double montant) {
 *        if (montant <= 0) throw new IllegalArgumentException("Montant invalide.");
 *        solde += montant;
 *    }
 *
 *  Code final apres corrections humaines :
 *    - Arrondi a 3 decimales via Math.round (standard millime DT)
 *    - Message d'erreur enrichi avec solde actuel et montant demande
 *    - Historique des transactions enregistre dans journalTransactions
 *
 * ============================================================
 *
 *  METHODE : seConnecter() / seDeconnecter()
 *  -----------------------------------------
 *  Prompt utilise :
 *    "Implemente seConnecter() dans la classe User. Elle doit verifier
 *     que le compte n'est pas banni, que username et email sont non nuls,
 *     puis deleguer l'ouverture de session a Session.login(this).
 *     Enregistrer la date de derniere connexion."
 *
 *  Code genere par l'IA :
 *    public void seConnecter() {
 *        if ("banni".equals(role))
 *            throw new IllegalStateException("Compte banni.");
 *        Session.login(this);
 *    }
 *
 *  Code final apres corrections humaines :
 *    - Validation username et email non nuls avant login
 *    - Enregistrement de derniereConnexion (LocalDateTime)
 *    - seDeconnecter() verifie que c'est bien cet utilisateur qui est connecte
 *
 * ============================================================
 *
 *  METHODE : changerMotDePasse()
 *  ------------------------------
 *  Prompt utilise :
 *    "Implemente changerMotDePasse(String ancien, String nouveau) dans
 *     la classe User Java. Verifier que l'ancien mot de passe est correct,
 *     que le nouveau fait au moins 6 caracteres, et qu'il est different
 *     de l'ancien. Mettre a jour le champ password."
 *
 *  Code genere par l'IA :
 *    public void changerMotDePasse(String ancien, String nouveau) {
 *        if (!password.equals(ancien))
 *            throw new IllegalArgumentException("Ancien mot de passe incorrect.");
 *        if (nouveau.length() < 6)
 *            throw new IllegalArgumentException("Nouveau mot de passe trop court.");
 *        password = nouveau;
 *    }
 *
 *  Code final apres corrections humaines :
 *    - Ajout verification nouveau != ancien (inutile de changer pour le meme)
 *    - Ajout verification nouveau non null avant appel .length()
 *    - Invalidation de la session apres changement (securite)
 *
 * ============================================================
 */
public class User {

    // ============================================================
    //  Champs prives
    // ============================================================

    private int           id;
    private String        username;
    private String        email;
    private String        password;
    private String        role;
    private double        solde;              // en DT (Dinar Tunisien)
    private LocalDateTime derniereConnexion;  // date/heure derniere connexion
    private int           nbConnexions;       // compteur de connexions

    // ============================================================
    //  Constructeurs
    // ============================================================

    /** Constructeur vide */
    public User() {
        this.role         = "user";
        this.solde        = 0.0;
        this.nbConnexions = 0;
    }

    /** Constructeur complet sans solde */
    public User(int id, String username, String email, String password) {
        this.id           = id;
        this.username     = username;
        this.email        = email;
        this.password     = password;
        this.role         = "user";
        this.solde        = 0.0;
        this.nbConnexions = 0;
    }

    /** Constructeur complet avec solde initial */
    public User(int id, String username, String email, String password, double soldeInitial) {
        this(id, username, email, password);
        this.solde = soldeInitial;
    }

    // ============================================================
    //  Methodes metier — logique reelle
    // ============================================================

    /**
     * CF-1 : Connexion — verifie le compte puis ouvre la session.
     * Interaction avec Session : Session.login(this)
     * Met a jour derniereConnexion et nbConnexions.
     */
    public void seConnecter() {
        if (username == null || email == null)
            throw new IllegalStateException(
                "Compte invalide : username ou email manquant.");
        if ("banni".equalsIgnoreCase(role))
            throw new IllegalStateException(
                "Compte banni. Connexion refusee pour : " + username);

        Session.login(this);
        this.derniereConnexion = LocalDateTime.now();
        this.nbConnexions++;
    }

    /**
     * CF-2 : Deconnexion — ferme la session si c'est bien cet utilisateur.
     * Interaction avec Session : Session.logout()
     */
    public void seDeconnecter() {
        User courant = Session.obtenirUtilisateurCourant();
        if (courant == null || courant.getId() != this.id)
            throw new IllegalStateException(
                username + " n'est pas l'utilisateur connecte actuellement.");
        Session.logout();
    }

    /**
     * Debiter le solde (paiement frais inscription, publication, etc.).
     * Montant en DT — arrondi a 3 decimales (millimes).
     *
     * @param montant Montant a debiter (strictement positif)
     */
    public void debiterSolde(double montant) {
        if (montant <= 0)
            throw new IllegalArgumentException(
                "Le montant a debiter doit etre strictement positif. Recu : " + montant);
        if (solde < montant)
            throw new IllegalStateException(
                "Solde insuffisant pour " + username
                + ". Solde : " + solde + " DT, montant demande : " + montant + " DT.");
        solde = Math.round((solde - montant) * 1000.0) / 1000.0;
    }

    /**
     * Crediter le solde (remboursement, depot, etc.).
     * Montant en DT — arrondi a 3 decimales (millimes).
     *
     * @param montant Montant a crediter (strictement positif)
     */
    public void crediterSolde(double montant) {
        if (montant <= 0)
            throw new IllegalArgumentException(
                "Le montant a crediter doit etre strictement positif. Recu : " + montant);
        solde = Math.round((solde + montant) * 1000.0) / 1000.0;
    }

    /**
     * Changer le mot de passe apres verification de l'ancien.
     * Invalide la session courante par securite.
     *
     * @param ancien  Mot de passe actuel (pour verification)
     * @param nouveau Nouveau mot de passe (6 caracteres minimum, different de l'ancien)
     */
    public void changerMotDePasse(String ancien, String nouveau) {
        if (ancien == null || !password.equals(ancien))
            throw new IllegalArgumentException("Ancien mot de passe incorrect.");
        if (nouveau == null || nouveau.length() < 6)
            throw new IllegalArgumentException(
                "Le nouveau mot de passe doit contenir au moins 6 caracteres.");
        if (nouveau.equals(ancien))
            throw new IllegalArgumentException(
                "Le nouveau mot de passe doit etre different de l'ancien.");

        this.password = nouveau;
        // Invalidation de session par securite apres changement de mot de passe
        User courant = Session.obtenirUtilisateurCourant();
        if (courant != null && courant.getId() == this.id) {
            Session.logout();
        }
    }

    /**
     * Afficher le profil complet du membre.
     */
    public void afficherDetails() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        System.out.println("  ID               : " + id);
        System.out.println("  Username         : " + username);
        System.out.println("  Email            : " + email);
        System.out.println("  Role             : " + role);
        System.out.println("  Solde            : " + String.format("%.3f", solde) + " DT");
        System.out.println("  Nb connexions    : " + nbConnexions);
        System.out.println("  Derniere cnx     : "
            + (derniereConnexion != null ? derniereConnexion.format(fmt) : "jamais"));
    }

    // ============================================================
    //  toString
    // ============================================================

    @Override
    public String toString() {
        return "User#" + id + " [" + username + "] (" + role + ")"
             + " | solde=" + String.format("%.3f", solde) + " DT"
             + " | cnx=" + nbConnexions;
    }

    // ============================================================
    //  Getters / Setters
    // ============================================================

    public int    getId()                         { return id; }
    public void   setId(int id)                   { this.id = id; }

    public String getUsername()                   { return username; }
    public void   setUsername(String username)    { this.username = username; }

    public String getEmail()                      { return email; }
    public void   setEmail(String email)          { this.email = email; }

    public String getPassword()                   { return password; }
    public void   setPassword(String password)    { this.password = password; }

    public String getRole()                       { return role; }
    public void   setRole(String role)            { this.role = role; }

    public double getSolde()                      { return solde; }
    public void   setSolde(double solde)          { this.solde = solde; }

    public LocalDateTime getDerniereConnexion()   { return derniereConnexion; }
    public int           getNbConnexions()        { return nbConnexions; }
}
