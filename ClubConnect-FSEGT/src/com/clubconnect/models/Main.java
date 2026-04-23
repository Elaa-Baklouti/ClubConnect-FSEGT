package com.clubconnect.models;

/**
 * ============================================================
 *  ClubConnect FSEGT — Main.java (TP3 V1)
 *  Demonstration du module AuthService — logique metier reelle
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
        System.out.println("  ClubConnect FSEGT — Demo AuthService TP3");
        System.out.println("============================================");
        System.out.println();

        // ============================================================
        //  SCENARIO 1 : Inscription de membres
        // ============================================================
        System.out.println("--- Scenario 1 : Inscription ---");

        User alice = AuthService.inscrire("alice", "alice@fsegt.tn", "pass123");
        User bob   = AuthService.inscrire("bob",   "bob@fsegt.tn",   "pass456");
        User carol = AuthService.inscrire("carol", "carol@fsegt.tn", "pass789");
        System.out.println("  OK : 3 membres inscrits");
        System.out.println();

        System.out.println("  Tentative email invalide :");
        try {
            AuthService.inscrire("test", "email-invalide", "pass123");
        } catch (IllegalArgumentException e) {
            System.out.println("  ERREUR attendue -> " + e.getMessage());
        }

        System.out.println("  Tentative mot de passe trop court :");
        try {
            AuthService.inscrire("test2", "test2@fsegt.tn", "abc");
        } catch (IllegalArgumentException e) {
            System.out.println("  ERREUR attendue -> " + e.getMessage());
        }

        System.out.println("  Tentative email deja utilise :");
        try {
            AuthService.inscrire("alice2", "alice@fsegt.tn", "autrepass");
        } catch (IllegalStateException e) {
            System.out.println("  ERREUR attendue -> " + e.getMessage());
        }
        System.out.println();

        // ============================================================
        //  SCENARIO 2 : Connexion, session et deconnexion
        // ============================================================
        System.out.println("--- Scenario 2 : Connexion et session ---");

        System.out.println("  Connexion de alice...");
        AuthService.seConnecter("alice@fsegt.tn", "pass123");
        Session.afficherDetails();
        System.out.println("  Nb connexions alice : " + alice.getNbConnexions());
        System.out.println();

        System.out.println("  Tentative mauvais mot de passe :");
        try {
            AuthService.seConnecter("bob@fsegt.tn", "mauvais");
        } catch (IllegalStateException e) {
            System.out.println("  ERREUR attendue -> " + e.getMessage());
        }
        System.out.println();

        System.out.println("  Deconnexion de alice...");
        AuthService.seDeconnecter();
        System.out.println("  Session active : " + Session.estConnecte());
        System.out.println();

        // ============================================================
        //  SCENARIO 3 : Gestion du solde en DT
        // ============================================================
        System.out.println("--- Scenario 3 : Gestion du solde (DT) ---");

        alice.setSolde(100.000);
        System.out.println("  Solde initial alice : " + alice.getSolde() + " DT");

        alice.debiterSolde(25.500);
        System.out.println("  Apres debit 25.500 DT  : " + alice.getSolde() + " DT");

        alice.crediterSolde(10.250);
        System.out.println("  Apres credit 10.250 DT : " + alice.getSolde() + " DT");

        System.out.println("  Tentative debit superieur au solde :");
        try {
            alice.debiterSolde(500.0);
        } catch (IllegalStateException e) {
            System.out.println("  ERREUR attendue -> " + e.getMessage());
        }
        System.out.println();

        // ============================================================
        //  SCENARIO 4 : Changement de mot de passe
        // ============================================================
        System.out.println("--- Scenario 4 : Changement de mot de passe ---");

        AuthService.seConnecter("bob@fsegt.tn", "pass456");
        System.out.println("  Session avant changement : " + Session.estConnecte());

        bob.changerMotDePasse("pass456", "newpass99");
        System.out.println("  Session apres changement (doit etre false) : "
            + Session.estConnecte());

        System.out.println("  Reconnexion avec nouveau mot de passe...");
        AuthService.seConnecter("bob@fsegt.tn", "newpass99");
        System.out.println("  OK : bob reconnecte");
        AuthService.seDeconnecter();
        System.out.println();

        System.out.println("  Tentative avec ancien mot de passe incorrect :");
        try {
            bob.changerMotDePasse("mauvais", "autre123");
        } catch (IllegalArgumentException e) {
            System.out.println("  ERREUR attendue -> " + e.getMessage());
        }

        System.out.println("  Tentative nouveau = ancien :");
        try {
            bob.changerMotDePasse("newpass99", "newpass99");
        } catch (IllegalArgumentException e) {
            System.out.println("  ERREUR attendue -> " + e.getMessage());
        }
        System.out.println();

        // ============================================================
        //  SCENARIO 5 : Reinitialisation de mot de passe
        // ============================================================
        System.out.println("--- Scenario 5 : Reinitialisation mot de passe ---");

        AuthService.seConnecter("carol@fsegt.tn", "pass789");
        System.out.println("  Carol connectee : " + Session.estConnecte());

        AuthService.reinitialiserMotDePasse("carol@fsegt.tn", "reset999");
        System.out.println("  Session apres reset (doit etre false) : "
            + Session.estConnecte());

        System.out.println("  Reconnexion carol avec nouveau mot de passe...");
        AuthService.seConnecter("carol@fsegt.tn", "reset999");
        System.out.println("  OK : carol reconnectee");
        AuthService.seDeconnecter();
        System.out.println();

        // ============================================================
        //  SCENARIO 6 : Administration
        // ============================================================
        System.out.println("--- Scenario 6 : Administration ---");

        System.out.println("  Tentative admin sans droits :");
        try {
            AdminService.voirUtilisateurs();
        } catch (IllegalStateException e) {
            System.out.println("  ERREUR attendue -> " + e.getMessage());
        }
        System.out.println();

        // Creer et connecter l'admin
        User admin = new User(99, "admin", "admin@fsegt.tn", "admin00");
        admin.setRole("admin");
        Session.login(admin);

        System.out.println("  Tableau de bord admin :");
        AdminService.afficherDetails();
        System.out.println();

        System.out.println("  Liste complete des membres :");
        AdminService.voirUtilisateurs();
        System.out.println();

        System.out.println("  Bannissement de carol (id=" + carol.getId() + ")...");
        AuthService.bannirMembre(carol.getId());
        System.out.println("  Role carol apres bannissement : " + carol.getRole());

        System.out.println("  Tentative connexion compte banni :");
        try {
            AuthService.seConnecter("carol@fsegt.tn", "reset999");
        } catch (IllegalStateException e) {
            System.out.println("  ERREUR attendue -> " + e.getMessage());
        }
        System.out.println();

        System.out.println("  Promotion de bob au role admin...");
        AdminService.promouvoirAdmin(bob.getId());
        System.out.println("  Role bob apres promotion : " + bob.getRole());

        System.out.println("  Tentative de promouvoir bob une 2eme fois :");
        try {
            AdminService.promouvoirAdmin(bob.getId());
        } catch (IllegalStateException e) {
            System.out.println("  ERREUR attendue -> " + e.getMessage());
        }
        System.out.println();

        System.out.println("  Suppression de carol (id=" + carol.getId() + ")...");
        User supprime = AdminService.supprimerMembre(carol.getId());
        System.out.println("  Membre supprime : " + supprime.getUsername());

        System.out.println("  Tentative auto-suppression admin :");
        try {
            AdminService.supprimerMembre(admin.getId());
        } catch (IllegalStateException e) {
            System.out.println("  ERREUR attendue -> " + e.getMessage());
        }
        System.out.println();

        // ============================================================
        //  RECAPITULATIF
        // ============================================================
        System.out.println("--- Recapitulatif final ---");
        AdminService.voirUtilisateurs();
        System.out.println();
        System.out.println("  Profil alice :");
        alice.afficherDetails();
        System.out.println();
        System.out.println("============================================");
        System.out.println("  Fin demonstration — AuthService TP3 OK");
        System.out.println("============================================");
    }
}
