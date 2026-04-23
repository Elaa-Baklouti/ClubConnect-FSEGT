package com.clubconnect.models;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *
 *  ETAPE 1 — Squelette AGL (deterministe) :
 *    Package : com.clubconnect.models
 *    Pattern : champs prives -> constructeurs (vide + complet)
 *             -> methodes metier (signatures) -> getters/setters
 *    Nommage francais : seConnecter(), seDeconnecter(),
 *    debiterSolde(), crediterSolde(), afficherDetails()
 *
 *  ETAPE 2 — Implementation IA assistee :
 *    Prompt utilise :
 *      "Ajoute un champ solde en DT (Dinar Tunisien) a la classe
 *       User avec les methodes debiterSolde(double montant) et
 *       crediterSolde(double montant). Valider que le montant est
 *       positif et que le solde ne devient pas negatif."
 *
 *    Code genere par l'IA :
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
 *      - Arrondi a 3 decimales (standard DT)
 *      - seConnecter() delegue a Session.login()
 *      - seDeconnecter() delegue a Session.logout()
 * ============================================================
 */
public class User {

    // ============================================================
    //  ETAPE 1 — Champs prives (squelette AGL)
    // ============================================================

    private int    id;
    private String username;
    private String email;
    private String password;
    private String role;
    private double solde; // en DT (Dinar Tunisien)

    // ============================================================
    //  ETAPE 1 — Constructeurs
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
    //  ETAPE 2 — Methodes metier (implementation)
    // ============================================================

    /** CF-1 : Connexion — ouvre la session pour cet utilisateur. */
    public void seConnecter() {
        if (username == null || email == null)
            throw new IllegalStateException("Compte invalide : username ou email manquant.");
        Session.login(this);
        System.out.println(username + " connecte.");
    }

    /** CF-2 : Deconnexion — ferme la session courante. */
    public void seDeconnecter() {
        Session.logout();
        System.out.println(username + " deconnecte.");
    }

    /**
     * Debiter le solde (paiement frais).
     * Montant en DT — arrondi a 3 decimales.
     */
    public void debiterSolde(double montant) {
        if (montant <= 0)
            throw new IllegalArgumentException("Le montant a debiter doit etre positif.");
        if (solde < montant)
            throw new IllegalStateException(
                "Solde insuffisant. Solde : " + solde + " DT, demande : " + montant + " DT.");
        solde = Math.round((solde - montant) * 1000.0) / 1000.0;
    }

    /**
     * Crediter le solde (remboursement).
     * Montant en DT — arrondi a 3 decimales.
     */
    public void crediterSolde(double montant) {
        if (montant <= 0)
            throw new IllegalArgumentException("Le montant a crediter doit etre positif.");
        solde = Math.round((solde + montant) * 1000.0) / 1000.0;
    }

    /** Afficher le profil complet du membre. */
    public void afficherDetails() {
        System.out.println("  ID       : " + id);
        System.out.println("  Username : " + username);
        System.out.println("  Email    : " + email);
        System.out.println("  Role     : " + role);
        System.out.println("  Solde    : " + solde + " DT");
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
}
