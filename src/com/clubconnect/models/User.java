package com.clubconnect.models;

/**
 * ============================================================
 *  DOCUMENTATION TP3 — Approche IA assistee
 * ============================================================
 *  METHODE : debiterSolde() / crediterSolde()
 *  Prompt utilise :
 *    "Ajoute un champ solde en DT a la classe User avec
 *     debiterSolde(double montant) et crediterSolde(double montant).
 *     Valider que le montant est positif et que le solde ne devient
 *     pas negatif."
 *  Code genere par l'IA :
 *    public void debiterSolde(double montant) {
 *        if (montant <= 0) throw new IllegalArgumentException("Montant invalide.");
 *        if (solde < montant) throw new IllegalStateException("Solde insuffisant.");
 *        solde -= montant;
 *    }
 *  Corrections humaines :
 *    - Ajout constructeur avec solde initial
 *    - Arrondi 3 decimales (standard DT)
 * ============================================================
 */
public class User {

    private int id;
    private String username;
    private String email;
    private String password;
    private String role;
    private double solde;

    public User() { this.role = "user"; this.solde = 0.0; }

    public User(int id, String username, String email, String password) {
        this.id = id; this.username = username;
        this.email = email; this.password = password;
        this.role = "user"; this.solde = 0.0;
    }

    public User(int id, String username, String email, String password, double soldeInitial) {
        this(id, username, email, password);
        this.solde = soldeInitial;
    }

    public void seConnecter() {
        if (username == null || email == null)
            throw new IllegalStateException("Compte invalide.");
        System.out.println(username + " connecte.");
    }

    public void seDeconnecter() { System.out.println(username + " deconnecte."); }

    public void debiterSolde(double montant) {
        if (montant <= 0) throw new IllegalArgumentException("Montant invalide.");
        if (solde < montant) throw new IllegalStateException("Solde insuffisant : " + solde + " DT.");
        solde = Math.round((solde - montant) * 1000.0) / 1000.0;
    }

    public void crediterSolde(double montant) {
        if (montant <= 0) throw new IllegalArgumentException("Montant invalide.");
        solde = Math.round((solde + montant) * 1000.0) / 1000.0;
    }

    public void afficherDetails() {
        System.out.println("=== Profil Membre ===");
        System.out.println("ID       : " + id);
        System.out.println("Username : " + username);
        System.out.println("Email    : " + email);
        System.out.println("Role     : " + role);
        System.out.println("Solde    : " + solde + " DT");
    }

    @Override
    public String toString() {
        return "User#" + id + " [" + username + "] (" + role + ") | solde=" + solde + " DT";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public double getSolde() { return solde; }
    public void setSolde(double solde) { this.solde = solde; }
}
