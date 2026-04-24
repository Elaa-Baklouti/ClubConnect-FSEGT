import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("   ClubConnect — Démonstration V1      ");
        System.out.println("========================================\n");

        // ============================================================
        //  PARTIE EVENT
        // ============================================================
        System.out.println("📌 PARTIE EVENT");
        System.out.println("---------------");

        EventService eventService = new EventService();
        List<User> tousLesUsers  = new ArrayList<>();

        User alice = new User(1, "alice", "alice@fsegt.tn", "pass123", 150.0);
        User bob   = new User(2, "bob",   "bob@fsegt.tn",   "pass456", 80.0);
        User carol = new User(3, "carol", "carol@fsegt.tn", "pass789", 10.0);

        tousLesUsers.add(alice);
        tousLesUsers.add(bob);
        tousLesUsers.add(carol);

        // Créer événements
        Event hackathon = eventService.creerEvenement(
            "Hackathon FSEGT", "Salle A", "2026-05-10", alice, 3, 25.0);
        Event networking = eventService.creerEvenement(
            "Soirée Networking", "Hall B", "2026-06-01", alice);

        System.out.println("Événements créés :");
        eventService.afficherDetails();
        System.out.println();

        // Inscription
        eventService.participer(hackathon.getId(), bob);
        System.out.println("Solde bob après inscription : " + bob.getSolde() + " DT");

        // Solde insuffisant
        try {
            eventService.participer(hackathon.getId(), carol);
        } catch (IllegalStateException e) {
            System.out.println("Erreur attendue : " + e.getMessage());
        }

        // Annulation + remboursement
        eventService.annulerParticipation(hackathon.getId(), bob);
        System.out.println("Solde bob après remboursement : " + bob.getSolde() + " DT");
        System.out.println();

        hackathon.afficherDetails();
        System.out.println();

        // ============================================================
        //  PARTIE ADMIN
        // ============================================================
        System.out.println("📌 PARTIE ADMIN");
        System.out.println("---------------");

        // Créer un admin
        User admin = new User(4, "admin", "admin@fsegt.tn", "admin00", 0.0);
        admin.setRole("admin");
        tousLesUsers.add(admin);

        // Inscrire des membres via AuthService
        AuthService.inscrire("alice",  "alice@fsegt.tn",  "pass123");
        AuthService.inscrire("bob",    "bob@fsegt.tn",    "pass456");
        AuthService.inscrire("carol",  "carol@fsegt.tn",  "pass789");

        // 1. Connexion admin
        System.out.println("1. Connexion admin...");
        Session.login(admin);
        Session.afficherDetails();
        System.out.println();

        // 2. Voir tous les membres
        System.out.println("2. Liste des membres :");
        AdminService.voirUtilisateurs();
        System.out.println();

        // 3. Tableau de bord admin
        System.out.println("3. Tableau de bord :");
        AdminService.afficherDetails();
        System.out.println();

        // 4. Supprimer un membre
        System.out.println("4. Suppression du membre carol...");
        AdminService.supprimerMembre(3);
        System.out.println("Membres restants : " + AuthService.getUsers().size());
        System.out.println();

        // 5. Tentative sans droits admin
        System.out.println("5. Tentative accès admin sans connexion...");
        Session.logout();
        try {
            AdminService.voirUtilisateurs();
        } catch (IllegalStateException e) {
            System.out.println("Erreur attendue : " + e.getMessage());
        }
        System.out.println();

        // 6. Reconnexion et vérification session
        System.out.println("6. Reconnexion admin...");
        Session.login(admin);
        System.out.println("isAdmin : " + Session.isAdmin());
        System.out.println("isLoggedIn : " + Session.isLoggedIn());
        Session.logout();
        System.out.println("Après logout — isLoggedIn : " + Session.isLoggedIn());

        System.out.println("\n========================================");
        System.out.println("        Fin de démonstration V1         ");
        System.out.println("========================================");
    }
}
