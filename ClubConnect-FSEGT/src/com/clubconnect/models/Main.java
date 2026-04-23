package com.clubconnect.models;

/**
 * ============================================================
 *  ClubConnect FSEGT — Main.java (TP1 V1)
 *  Demonstration du module AuthService
 * ============================================================
 *  Compile :
 *    javac -encoding UTF-8 -d out src/com/clubconnect/models/*.java
 *  Execute :
 *    java -cp out com.clubconnect.models.Main
 * ============================================================
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("============================================");
        System.out.println("  ClubConnect FSEGT — Demo AuthService TP1");
        System.out.println("============================================");
        System.out.println();

        // ============================================================
        //  SCENARIO 1 : Inscription de membres
        // ============================================================
        System.out.println("--- Scenario 1 : Inscription de membres ---");

        System.out.println("1. Inscription de alice...");
        User alice = AuthService.inscrire("alice", "alice@fsegt.tn", "pass123");
        alice.afficherDetails();
        System.out.println();

        System.out.println("2. Inscription de bob...");
        User bob = AuthService.inscrire("bob", "bob@fsegt.tn", "pass456");
        bob.afficherDetails();
        System.out.println();

        System.out.println("3. Inscription de carol...");
        User carol = AuthService.inscrire("carol", "carol@fsegt.tn", "pass789");
        System.out.println();

        System.out.println("4. Tentative avec email invalide...");
        try {
            AuthService.inscrire("test", "email-invalide", "pass123");
        } catch (IllegalArgumentException e) {
            System.out.println("  ERREUR attendue : " + e.getMessage());
        }
        System.out.println();

        System.out.println("5. Tentative avec mot de passe trop court...");
        try {
            AuthService.inscrire("test2", "test2@fsegt.tn", "abc");
        } catch (IllegalArgumentException e) {
            System.out.println("  ERREUR attendue : " + e.getMessage());
        }
        System.out.println();

        System.out.println("6. Tentative avec email deja utilise...");
        try {
            AuthService.inscrire("alice2", "alice@fsegt.tn", "autrepass");
        } catch (IllegalStateException e) {
            System.out.println("  ERREUR attendue : " + e.getMessage());
        }
        System.out.println();

        // ============================================================
        //  SCENARIO 2 : Connexion et session
        // ============================================================
        System.out.println("--- Scenario 2 : Connexion et session ---");

        System.out.println("1. Connexion de alice...");
        AuthService.seConnecter("alice@fsegt.tn", "pass123");
        Session.afficherDetails();
        System.out.println("   Session active : " + Session.estConnecte());
        System.out.println();

        System.out.println("2. Tentative avec mauvais mot de passe...");
        try {
            AuthService.seConnecter("bob@fsegt.tn", "mauvais");
        } catch (IllegalStateException e) {
            System.out.println("  ERREUR attendue : " + e.getMessage());
        }
        System.out.println();

        System.out.println("3. Deconnexion de alice...");
        AuthService.seDeconnecter();
        System.out.println("   Session active : " + Session.estConnecte());
        System.out.println();

        // ============================================================
        //  SCENARIO 3 : Solde — debit et credit
        // ============================================================
        System.out.println("--- Scenario 3 : Gestion du solde ---");

        alice.setSolde(100.0);
        System.out.println("1. Solde initial alice : " + alice.getSolde() + " DT");

        System.out.println("2. Debit de 25.500 DT...");
        alice.debiterSolde(25.500);
        System.out.println("   Solde apres debit : " + alice.getSolde() + " DT");

        System.out.println("3. Credit de 10.000 DT...");
        alice.crediterSolde(10.000);
        System.out.println("   Solde apres credit : " + alice.getSolde() + " DT");

        System.out.println("4. Tentative de debit superieur au solde...");
        try {
            alice.debiterSolde(500.0);
        } catch (IllegalStateException e) {
            System.out.println("  ERREUR attendue : " + e.getMessage());
        }
        System.out.println();

        // ============================================================
        //  SCENARIO 4 : Administration
        // ============================================================
        System.out.println("--- Scenario 4 : Administration ---");

        System.out.println("1. Tentative admin sans etre connecte...");
        try {
            AdminService.voirUtilisateurs();
        } catch (IllegalStateException e) {
            System.out.println("  ERREUR attendue : " + e.getMessage());
        }
        System.out.println();

        System.out.println("2. Connexion en tant qu'admin...");
        // Creer le compte admin manuellement (role admin)
        User admin = new User(99, "admin", "admin@fsegt.tn", "admin00");
        admin.setRole("admin");
        Session.login(admin);
        Session.afficherDetails();
        System.out.println();

        System.out.println("3. Tableau de bord admin...");
        AdminService.afficherDetails();
        System.out.println();

        System.out.println("4. Liste de tous les membres...");
        AdminService.voirUtilisateurs();
        System.out.println();

        System.out.println("5. Suppression du membre carol (id=" + carol.getId() + ")...");
        AdminService.supprimerMembre(carol.getId());
        System.out.println("   Membres restants apres suppression :");
        AdminService.voirUtilisateurs();
        System.out.println();

        System.out.println("6. Tentative de se supprimer soi-meme...");
        try {
            AdminService.supprimerMembre(admin.getId());
        } catch (IllegalStateException e) {
            System.out.println("  ERREUR attendue : " + e.getMessage());
        }
        System.out.println();

        // ============================================================
        //  RECAPITULATIF
        // ============================================================
        System.out.println("--- Recapitulatif ---");
        AuthService.afficherDetails();
        System.out.println();
        System.out.println("============================================");
        System.out.println("  Fin de demonstration — AuthService OK");
        System.out.println("============================================");
    }
}
