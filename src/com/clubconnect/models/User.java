package com.clubconnect.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
 *      "Ajoute un champ solde en DT a la classe User avec
 *       debiterSolde(double) et crediterSolde(double).
 *       Valider montant positif et solde non negatif.
 *       Arrondir a 3 decimales (millimes DT)."
 *
 *    Code genere par l'IA :
 *      public void debiterSolde(double montant) {
 *          if (montant <= 0) throw new IllegalArgumentException("Montant invalide.");
 *          if (solde < montant) throw new IllegalStateException("Solde insuffisant.");
 *          solde -= montant;
 *      }
 *
 *    Corrections humaines :
 *      - Arrondi 3 decimales via Math.round
 *      - Message enrichi avec solde actuel et montant demande
 *      - seConnecter() delegue a Session.login(this)
 * ============================================================
 */
public class User {

    // ============================================================
    //  ETAPE 1 — Champs prives
    // ============================================================

    private int           id;
    private String        username;
    private String        email;
    private String        password;
    private String        role;
    private double        solde;
    private LocalDateTime derniereConnexion;
    private int           nbConnexions;

    // ============================================================
    //  ETAPE 1 — Constructeurs
    // ============================================================

    public User() {
        this.role         = "user";
        this.solde        = 0.0;
        this.nbConnexions = 0;
    }

    public User(int id, String username, String email, String password) {
        this.id           = id;
        this.username     = username;
        this.email        = email;
        this.password     = password;
        this.role         = "user";
        this.solde        = 0.0;
        this.nbConnexions = 0;
    }

    public User(int id, String username, String email, String password, double soldeInitial) {
        this(id, username, email, password);
        this.solde = soldeInitial;
    }

    // ============================================================
    //  ETAPE 2 — Methodes metier
    // ============================================================

    public void seConnecter() {
        if (username == null || email == null)
            throw new IllegalStateException("Compte invalide.");
        if ("banni".equalsIgnoreCase(role))
            throw new IllegalStateException("Compte banni : " + username);
        Session.login(this);
        this.derniereConnexion = LocalDateTime.now();
        this.nbConnexions++;
    }

    public void seDeconnecter() {
        Session.logout();
    }

    public void debiterSolde(double montant) {
        if (montant <= 0)
            throw new IllegalArgumentException("Montant invalide : " + montant);
        if (solde < montant)
            throw new IllegalStateException(
                "Solde insuffisant pour " + username
                + ". Solde : " + solde + " DT, demande : " + montant + " DT.");
        solde = Math.round((solde - montant) * 1000.0) / 1000.0;
    }

    public void crediterSolde(double montant) {
        if (montant <= 0)
            throw new IllegalArgumentException("Montant invalide : " + montant);
        solde = Math.round((solde + montant) * 1000.0) / 1000.0;
    }

    public void afficherDetails() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        System.out.println("  ID           : " + id);
        System.out.println("  Username     : " + username);
        System.out.println("  Email        : " + email);
        System.out.println("  Role         : " + role);
        System.out.println("  Solde        : " + String.format("%.3f", solde) + " DT");
        System.out.println("  Nb connexions: " + nbConnexions);
        System.out.println("  Derniere cnx : "
            + (derniereConnexion != null ? derniereConnexion.format(fmt) : "jamais"));
    }

    @Override
    public String toString() {
        return "User#" + id + " [" + username + "] (" + role + ")"
             + " | solde=" + String.format("%.3f", solde) + " DT";
    }

    // ============================================================
    //  Getters / Setters
    // ============================================================

    public int    getId()                      { return id; }
    public void   setId(int id)                { this.id = id; }
    public String getUsername()                { return username; }
    public void   setUsername(String u)        { this.username = u; }
    public String getEmail()                   { return email; }
    public void   setEmail(String e)           { this.email = e; }
    public String getPassword()                { return password; }
    public void   setPassword(String p)        { this.password = p; }
    public String getRole()                    { return role; }
    public void   setRole(String r)            { this.role = r; }
    public double getSolde()                   { return solde; }
    public void   setSolde(double s)           { this.solde = s; }
    public int    getNbConnexions()            { return nbConnexions; }
    public LocalDateTime getDerniereConnexion(){ return derniereConnexion; }
}
