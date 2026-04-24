import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *  DOCUMENTATION TP3 — Approche IA assistée
 * ============================================================
 *
 *  SCÉNARIOS ADMIN — Prompt utilisé :
 *    "Génère un Main Java qui démontre un module admin complet.
 *     Scénario 1 : inscription de membres via AuthService,
 *     connexion admin, affichage liste membres.
 *     Scénario 2 : changement de rôle d'un membre.
 *     Scénario 3 : suppression d'un membre avec vérification.
 *     Scénario 4 : tableau de bord avec soldes en DT.
 *     Scénario 5 : tentative d'accès sans droits admin."
 *
 *  Code généré par l'IA :
 *    Session.login(admin);
 *    AdminService.voirUtilisateurs();
 *    AdminService.supprimerMembre(id);
 *
 *  Corrections humaines :
 *    - Ajout scénario changerRole() avec protection dernier admin
 *    - Ajout soldes en DT dans le tableau de bord
 *    - Intégration interaction Admin ↔ Event (annulation événement)
 *    - Ajout gestion des exceptions avec messages lisibles
 * ============================================================
 */
public class Main {
    public static void main(String[] args) {

        System.out.println("=== ClubConnect FSEGT — Démonstration Finale ===\n");

        // ============================================================
        //  DONNÉES DE BASE
        // ============================================================
        EventService eventService = new EventService();

        // Membres créés directement (partie Event)
        User alice = new User(1, "alice", "alice@fsegt.tn", "pass123", 150.0);
        User bob   = new User(2, "bob",   "bob@fsegt.tn",   "pass456", 80.0);
        User carol = new User(3, "carol", "carol@fsegt.tn", "pass789", 10.0);
        User admin = new User(4, "admin", "admin@fsegt.tn", "admin00", 0.0);
        admin.setRole("admin");

        // Inscrire dans AuthService pour que AdminService puisse les gérer
        AuthService.users.add(alice);
        AuthService.users.add(bob);
        AuthService.users.add(carol);
        AuthService.users.add(admin);

        List<User> tousLesUsers = new ArrayList<>(AuthService.users);

        // ============================================================
        //  PARTIE EVENT
        // ============================================================
        System.out.println("📌 Scénario 1 : Inscription à un événement payant");
        System.out.println("---------------------------------------------------");

        Event hackathon = eventService.creerEvenement(
            "Hackathon FSEGT", "Salle A", "2026-05-10", alice, 3, 25.0);
        Event networking = eventService.creerEvenement(
            "Soirée Networking", "Hall B", "2026-06-01", alice);

        System.out.println("Événements créés :");
        for (Event e : eventService.getEvenements())
            System.out.println("  " + e);
        System.out.println();

        // Bob s'inscrit — 25 DT débités
        eventService.participer(hackathon.getId(), bob);
        System.out.println("✅ Solde bob après inscription : " + bob.getSolde() + " DT");

        // Carol — solde insuffisant
        try {
            eventService.participer(hackathon.getId(), carol);
        } catch (IllegalStateException e) {
            System.out.println("⚠️  Erreur attendue : " + e.getMessage());
        }

        // Annulation + remboursement
        eventService.annulerParticipation(hackathon.getId(), bob);
        System.out.println("✅ Solde bob après remboursement : " + bob.getSolde() + " DT");
        System.out.println();

        hackathon.afficherDetails();
        System.out.println();

        // ============================================================
        //  PARTIE ADMIN — Scénario 2 : Connexion et liste membres
        // ============================================================
        System.out.println("📌 Scénario 2 : Connexion admin et gestion des membres");
        System.out.println("--------------------------------------------------------");

        System.out.println("1. Connexion admin...");
        Session.login(admin);
        Session.afficherDetails();
        System.out.println("   ✅ Connecté");
        System.out.println();

        System.out.println("2. Liste complète des membres :");
        AdminService.voirUtilisateurs();
        System.out.println();

        System.out.println("3. Tableau de bord :");
        AdminService.afficherDetails();
        System.out.println();

        // ============================================================
        //  PARTIE ADMIN — Scénario 3 : Changement de rôle
        // ============================================================
        System.out.println("📌 Scénario 3 : Changement de rôle");
        System.out.println("------------------------------------");

        System.out.println("1. Promotion de bob en admin...");
        AdminService.changerRole(bob.getId(), "admin");
        System.out.println("   ✅ Nouveau rôle de bob : " + bob.getRole());
        System.out.println();

        System.out.println("2. Rétrogradation de bob en user...");
        AdminService.changerRole(bob.getId(), "user");
        System.out.println("   ✅ Nouveau rôle de bob : " + bob.getRole());
        System.out.println();

        System.out.println("3. Tentative de rétrograder le dernier admin...");
        try {
            AdminService.changerRole(admin.getId(), "user");
        } catch (IllegalStateException e) {
            System.out.println("   ⚠️  Erreur attendue : " + e.getMessage());
        }
        System.out.println();

        // ============================================================
        //  PARTIE ADMIN — Scénario 4 : Suppression membre
        // ============================================================
        System.out.println("📌 Scénario 4 : Suppression d'un membre");
        System.out.println("-----------------------------------------");

        System.out.println("1. Suppression de carol (id=3)...");
        AdminService.supprimerMembre(carol.getId());
        System.out.println("   ✅ Membres restants : " + AuthService.getUsers().size());
        System.out.println();

        System.out.println("2. Tentative auto-suppression admin...");
        try {
            AdminService.supprimerMembre(admin.getId());
        } catch (IllegalStateException e) {
            System.out.println("   ⚠️  Erreur attendue : " + e.getMessage());
        }
        System.out.println();

        // ============================================================
        //  PARTIE ADMIN — Scénario 5 : Interaction Admin ↔ Event
        // ============================================================
        System.out.println("📌 Scénario 5 : Admin annule un événement");
        System.out.println("------------------------------------------");

        // Bob se réinscrit pour tester l'annulation
        eventService.participer(hackathon.getId(), bob);
        System.out.println("Bob inscrit au hackathon. Solde : " + bob.getSolde() + " DT");

        // Admin annule l'événement → remboursement automatique
        eventService.annulerEvenement(hackathon.getId(), alice, tousLesUsers);
        System.out.println("✅ Événement annulé par l'organisateur");
        System.out.println("✅ Solde bob après remboursement : " + bob.getSolde() + " DT");
        System.out.println();

        // ============================================================
        //  PARTIE ADMIN — Scénario 6 : Accès refusé sans droits
        // ============================================================
        System.out.println("📌 Scénario 6 : Contrôle d'accès");
        System.out.println("---------------------------------");

        Session.logout();
        System.out.println("Session fermée. isLoggedIn : " + Session.isLoggedIn());

        try {
            AdminService.voirUtilisateurs();
        } catch (IllegalStateException e) {
            System.out.println("⚠️  Erreur attendue : " + e.getMessage());
        }

        // Connexion membre normal — pas admin
        Session.login(bob);
        try {
            AdminService.afficherDetails();
        } catch (IllegalStateException e) {
            System.out.println("⚠️  Erreur attendue : " + e.getMessage());
        }
        Session.logout();

        System.out.println("\n=== Fin de démonstration ===");
    }
}
