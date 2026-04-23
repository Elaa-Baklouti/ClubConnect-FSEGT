package com.clubconnect.models;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *
 *  ÉTAPE 1 — Squelette AGL (déterministe) :
 *    Structure générée depuis le diagramme de classes.
 *    Package : com.clubconnect.models
 *    Pattern : champs privés → constructeurs (vide + complet)
 *             → méthodes métier (signatures) → getters/setters
 *    Nommage français : seConnecter(), seDeconnecter(),
 *    debiterSolde(), crediterSolde(), afficherDetails()
 *
 *  ÉTAPE 2 — Implémentation IA assistée :
 *    Prompt utilisé :
 *      "Ajoute un champ solde en DT (Dinar Tunisien) à la classe
 *       User avec les méthodes debiterSolde(double montant) et
 *       crediterSolde(double montant). Valider que le montant est
 *       positif et que le solde ne devient pas négatif."
 *
 *    Code généré par l'IA :
 *      public void debiterSolde(double montant) {
 *          if (montant <= 0) throw new IllegalArgumentException("Montant invalide.");
 *          if (solde < montant) throw new IllegalStateException("Solde insuffisant.");
 *          solde -= montant;
 *      }
 *      public void crediterSolde(double montant) {
 *          if (montant <= 0) throw new IllegalArgumentException("Montant invalide.");
 *          solde += montant;
 *      }
 *
 *    Corrections humaines :
 *      - Ajout du constructeur avec solde initial
 *      - Arrondi à 3 décimales (standard DT)
 *      - seConnecter() délègue à Session.login()
 *      - seDeconnecter() délègue à Session.logout()
 * ============================================================
 */
public class User {

    // ============================================================
    //  ÉTAPE 1 — Champs privés (squelette AGL)
    // ============================================================

    private int    id;
    private String username;
    private String email;
    private String password;
    private String role;
    private double solde; // en DT (Dinar Tunisien)

    // ============================================================
    //  ÉTAPE 1 — Constructeurs
    // ============================================================

    /** Constructeur vide */
    public User() {
        this.role  = "user";
        this.solde = 0.0;
    }

    /** Constructeur complet (sans solde initial) */
    public User(int id, String username, String email, String password) {
        this.id       = id;
        this.username = username;
        this.email    = email;
        this.password = password;
        this.role     = "user";
        this.solde    = 0.0;
    }

    /** Constructeur complet avec solde initial */
    public User(int id, String username, String email, String password, double soldeInitial) {
        this(id, username, email, password);
        this.solde = soldeInitial;
    }

    // ============================================================
    //  ÉTAPE 2 — Méthodes métier (implémentation)
    // ============================================================

    /**
     * CF-1 : Connexion — vérifie que le compte est valide
     * puis ouvre la session.
     */
    public void seConnecter() {
        if (username == null || email == null)
            throw new IllegalStateException("Compte invalide : username ou email manquant.");
        Session.login(this);
        System.out.println(username + " connecté.");
    }

    /**
     * CF-2 : Déconnexion — ferme la session courante.
     */
    public void seDeconnecter() {
        Session.logout();
        System.out.println(username + " déconnecté.");
    }

    /**
     * Débiter le solde (ex. paiement frais d'inscription événement).
     * Montant en DT — arrondi à 3 décimales (standard DT).
     */
    public void debiterSolde(double montant) {
        if (montant <= 0)
            throw new IllegalArgumentException("Le montant à débiter doit être positif.");
        if (solde < montant)
            throw new IllegalStateException(
                "Solde insuffisant. Solde actuel : " + solde + " DT, montant demandé : " + montant + " DT.");
        solde = Math.round((solde - montant) * 1000.0) / 1000.0;
    }

    /**
     * Créditer le solde (ex. remboursement annulation événement).
     * Montant en DT — arrondi à 3 décimales (standard DT).
     */
    public void crediterSolde(double montant) {
        if (montant <= 0)
            throw new IllegalArgumentException("Le montant à créditer doit être positif.");
        solde = Math.round((solde + montant) * 1000.0) / 1000.0;
    }

    /**
     * Afficher le profil complet du membre.
     */
    public void afficherDetails() {
        System.out.println("=== Profil Membre ===");
        System.out.println("ID       : " + id);
        System.out.println("Username : " + username);
        System.out.println("Email    : " + email);
        System.out.println("Rôle     : " + role);
        System.out.println("Solde    : " + solde + " DT");
    }

    // ============================================================
    //  toString
    // ============================================================

    @Override
    public String toString() {
        return "User#" + id + " [" + username + "] (" + role + ") | solde=" + solde + " DT";
    }

    // ============================================================
    //  Getters / Setters
    // ============================================================

    public int getId()                    { return id; }
    public void setId(int id)             { this.id = id; }

    public String getUsername()                   { return username; }
    public void setUsername(String username)      { this.username = username; }

    public String getEmail()                      { return email; }
    public void setEmail(String email)            { this.email = email; }

    public String getPassword()                   { return password; }
    public void setPassword(String password)      { this.password = password; }

    public String getRole()                       { return role; }
    public void setRole(String role)              { this.role = role; }

    public double getSolde()                      { return solde; }
    public void setSolde(double solde)            { this.solde = solde; }
}
