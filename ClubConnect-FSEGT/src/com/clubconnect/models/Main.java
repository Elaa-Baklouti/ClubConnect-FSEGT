package com.clubconnect.models;

/**
 * ============================================================
 *  ClubConnect FSEGT — Demonstration Finale (TP3)
 *  Module : AuthService — scenarios de bout en bout
 * ============================================================
 *  Compile :
 *    javac -encoding UTF-8 -d out src/com/clubconnect/models/*.java
 *  Execute :
 *    java -cp out com.clubconnect.models.Main
 * ============================================================
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("==============================================");
        System.out.println("  ClubConnect FSEGT — Demonstration Finale   ");
        System.out.println("==============================================");
        System.out.println();

        // ============================================================
        //  DONNEES DE BASE — membres inscrits via AuthService
        // ============================================================
        User fatma  = AuthService.inscrire("Fatma",  "fatma@fsegt.tn",  "fatma123");
        User khalil = AuthService.inscrire("Khalil", "khalil@fsegt.tn", "khalil99");
        User sarra  = AuthService.inscrire("Sarra",  "sarra@fsegt.tn",  "sarra456");
        User mehdi  = AuthService.inscrire("Mehdi",  "mehdi@fsegt.tn",  "mehdi789");

        // Crediter les soldes initiaux (en DT)
        fatma.crediterSolde(80.000);
        khalil.crediterSolde(50.000);
        sarra.crediterSolde(30.000);
        mehdi.crediterSolde(20.000);

        // Compte admin (hors AuthService — role specifique)
        User admin = new User(99, "AdminCC", "admin@fsegt.tn", "admin00");
        admin.setRole("admin");

        // ============================================================
        //  SCENARIO 1 : Un membre s'inscrit et se connecte
        // ============================================================
        System.out.println(">> Scenario 1 : Inscription et connexion");
        System.out.println("------------------------------------------");

        System.out.println("1. Inscription de Fatma (membre)...");
        System.out.println("   Compte cree : " + fatma.getUsername()
            + " | solde initial : " + String.format("%.3f", fatma.getSolde()) + " DT  [OK]");
        System.out.println();

        System.out.println("2. Connexion de Fatma...");
        AuthService.seConnecter("fatma@fsegt.tn", "fatma123");
        System.out.println("   Connectee : " + Session.obtenirUtilisateurCourant().getUsername()
            + " | nb connexions : " + fatma.getNbConnexions() + "  [OK]");
        Session.afficherDetails();
        System.out.println();

        System.out.println("3. Tentative connexion avec mauvais mot de passe...");
        try {
            AuthService.seConnecter("khalil@fsegt.tn", "mauvais");
        } catch (IllegalStateException e) {
            System.out.println("   Acces refuse -> " + e.getMessage() + "  [OK]");
        }
        System.out.println();

        System.out.println("4. Deconnexion de Fatma...");
        AuthService.seDeconnecter();
        System.out.println("   Session active : " + Session.estConnecte() + "  [OK]");
        System.out.println();

        // ============================================================
        //  SCENARIO 2 : Paiement et gestion du solde en DT
        // ============================================================
        System.out.println(">> Scenario 2 : Paiement frais d'inscription (solde DT)");
        System.out.println("----------------------------------------------------------");

        double fraisInscription = 15.000; // 15 DT

        System.out.println("1. Fatma s'inscrit a un evenement (frais : "
            + String.format("%.3f", fraisInscription) + " DT)...");
        System.out.println("   Solde avant : " + String.format("%.3f", fatma.getSolde()) + " DT");
        fatma.debiterSolde(fraisInscription);
        System.out.println("   Paiement de " + String.format("%.3f", fraisInscription)
            + " DT debite  [OK]");
        System.out.println("   Solde apres : " + String.format("%.3f", fatma.getSolde()) + " DT");
        System.out.println();

        System.out.println("2. Khalil s'inscrit aussi (frais : "
            + String.format("%.3f", fraisInscription) + " DT)...");
        khalil.debiterSolde(fraisInscription);
        System.out.println("   Solde Khalil apres : "
            + String.format("%.3f", khalil.getSolde()) + " DT  [OK]");
        System.out.println();

        System.out.println("3. Sarra tente de s'inscrire (solde insuffisant)...");
        User sarraFauchee = AuthService.inscrire("SarraB", "sarrab@fsegt.tn", "sarrab99");
        sarraFauchee.crediterSolde(5.000); // solde insuffisant : 5 DT < 15 DT
        try {
            sarraFauchee.debiterSolde(fraisInscription);
        } catch (IllegalStateException e) {
            System.out.println("   Inscription refusee -> " + e.getMessage() + "  [OK]");
        }
        System.out.println();

        System.out.println("4. Remboursement de Fatma (annulation)...");
        fatma.crediterSolde(fraisInscription);
        System.out.println("   Solde Fatma apres remboursement : "
            + String.format("%.3f", fatma.getSolde()) + " DT  [OK]");
        System.out.println();

        // ============================================================
        //  SCENARIO 3 : Changement et reinitialisation de mot de passe
        // ============================================================
        System.out.println(">> Scenario 3 : Securite — mot de passe");
        System.out.println("-----------------------------------------");

        System.out.println("1. Sarra change son mot de passe...");
        AuthService.seConnecter("sarra@fsegt.tn", "sarra456");
        System.out.println("   Session ouverte : " + Session.estConnecte());
        sarra.changerMotDePasse("sarra456", "sarra_new99");
        System.out.println("   Session invalidee apres changement : "
            + !Session.estConnecte() + "  [OK]");
        System.out.println();

        System.out.println("2. Reconnexion avec nouveau mot de passe...");
        AuthService.seConnecter("sarra@fsegt.tn", "sarra_new99");
        System.out.println("   Reconnexion reussie : "
            + Session.obtenirUtilisateurCourant().getUsername() + "  [OK]");
        AuthService.seDeconnecter();
        System.out.println();

        System.out.println("3. Reinitialisation mot de passe de Mehdi (admin)...");
        Session.login(admin);
        AuthService.reinitialiserMotDePasse("mehdi@fsegt.tn", "mehdi_reset1");
        System.out.println("   Mot de passe reinitialise  [OK]");
        System.out.println("   Reconnexion Mehdi avec nouveau mot de passe...");
        AuthService.seConnecter("mehdi@fsegt.tn", "mehdi_reset1");
        System.out.println("   Connexion reussie : "
            + Session.obtenirUtilisateurCourant().getUsername() + "  [OK]");
        AuthService.seDeconnecter();
        System.out.println();

        // ============================================================
        //  SCENARIO 4 : Bannissement d'un membre
        // ============================================================
        System.out.println(">> Scenario 4 : Bannissement d'un membre");
        System.out.println("------------------------------------------");

        Session.login(admin);

        System.out.println("1. Bannissement de SarraB (compte suspect)...");
        AuthService.bannirMembre(sarraFauchee.getId());
        System.out.println("   Role SarraB apres bannissement : "
            + sarraFauchee.getRole() + "  [OK]");
        System.out.println();

        System.out.println("2. Tentative de connexion du compte banni...");
        try {
            AuthService.seConnecter("sarrab@fsegt.tn", "sarrab99");
        } catch (IllegalStateException e) {
            System.out.println("   Connexion refusee -> " + e.getMessage() + "  [OK]");
        }
        System.out.println();

        System.out.println("3. Tentative de bannir un membre deja admin (Khalil)...");
        // Khalil sera promu admin au scenario 5 — on teste la regle metier
        // en utilisant un membre inscrit via AuthService avec role admin
        AdminService.promouvoirAdmin(khalil.getId()); // promotion anticipee pour le test
        try {
            AuthService.bannirMembre(khalil.getId());
        } catch (IllegalStateException e) {
            System.out.println("   Refuse -> " + e.getMessage() + "  [OK]");
        }
        // Khalil reste admin pour le scenario 5
        Session.logout();
        System.out.println();

        // ============================================================
        //  SCENARIO 5 : Tableau de bord admin
        // ============================================================
        System.out.println(">> Scenario 5 : Tableau de bord administrateur");
        System.out.println("------------------------------------------------");

        Session.login(admin);

        System.out.println("1. Khalil est deja admin (promu au scenario 4)...");
        System.out.println("   Role Khalil : " + khalil.getRole() + "  [OK]");
        System.out.println();

        System.out.println("2. Tableau de bord :");
        AdminService.afficherDetails();
        System.out.println();

        System.out.println("3. Liste complete des membres :");
        AdminService.voirUtilisateurs();
        System.out.println();

        // Calcul revenus : somme des debits effectues
        double revenusTotal = fraisInscription * 2; // Fatma + Khalil ont paye
        long   membresActifs = AuthService.getUsers().stream()
            .filter(u -> !"banni".equalsIgnoreCase(u.getRole())).count();

        System.out.println("4. Statistiques globales :");
        System.out.println("   Utilisateurs inscrits : " + AuthService.getUsers().size());
        System.out.println("   Membres actifs        : " + membresActifs);
        System.out.println("   Revenus generes       : "
            + String.format("%.3f", revenusTotal) + " DT");
        System.out.println();

        System.out.println("5. Suppression du compte SarraB (banni)...");
        User supprime = AdminService.supprimerMembre(sarraFauchee.getId());
        System.out.println("   Membre supprime : " + supprime.getUsername() + "  [OK]");
        System.out.println("   Membres restants : " + AuthService.getUsers().size());
        System.out.println();

        // ============================================================
        //  RECAPITULATIF FINAL
        // ============================================================
        System.out.println(">> Recapitulatif final");
        System.out.println("----------------------");
        System.out.println("   Profil Fatma :");
        fatma.afficherDetails();
        System.out.println();
        System.out.println("   Profil Khalil :");
        khalil.afficherDetails();
        System.out.println();
        System.out.println("   Liste finale des membres :");
        AdminService.voirUtilisateurs();
        System.out.println();

        System.out.println("==============================================");
        System.out.println("  Fin demonstration — AuthService TP3  [OK]  ");
        System.out.println("==============================================");
    }
}
