public class Main {
    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("   ClubConnect — Démonstration TP3     ");
        System.out.println("========================================\n");

        // --- Création des utilisateurs avec solde initial en DT ---
        User alice = new User(1, "alice", "alice@email.com", "pass123", 100.0);
        User bob   = new User(2, "bob",   "bob@email.com",   "pass456", 30.0);
        User carol = new User(3, "carol", "carol@email.com", "pass789", 5.0);

        alice.afficherDetails();
        System.out.println();

        // --- Service événement ---
        EventService eventService = new EventService();

        // --- Créer un événement avec frais (20 DT) et capacité 2 ---
        Event e1 = eventService.creerEvenement(
            "Hackathon FSEGT", "Salle A", "2026-05-10", alice, 2, 20.0
        );
        // --- Créer un événement gratuit ---
        Event e2 = eventService.creerEvenement(
            "Soirée Networking", "Hall B", "2026-06-01", alice
        );

        System.out.println("--- Événements créés ---");
        eventService.afficherDetails();
        System.out.println();

        // --- Participation avec débit automatique du solde ---
        System.out.println("--- Inscriptions ---");
        eventService.participer(e1.getId(), bob);   // bob paie 20 DT
        System.out.println("Solde bob après inscription : " + bob.getSolde() + " DT");

        // --- Tentative avec solde insuffisant ---
        System.out.println();
        try {
            eventService.participer(e1.getId(), carol); // carol n'a que 5 DT
        } catch (IllegalStateException ex) {
            System.out.println("Erreur attendue : " + ex.getMessage());
        }

        // --- Tentative doublon ---
        try {
            eventService.participer(e1.getId(), bob);
        } catch (IllegalStateException ex) {
            System.out.println("Erreur attendue : " + ex.getMessage());
        }

        // --- Tentative organisateur s'inscrit lui-même ---
        try {
            eventService.participer(e1.getId(), alice);
        } catch (IllegalStateException ex) {
            System.out.println("Erreur attendue : " + ex.getMessage());
        }

        System.out.println();
        e1.afficherDetails();
        System.out.println();

        // --- Annulation de participation avec remboursement ---
        System.out.println("--- Annulation participation bob ---");
        eventService.annulerParticipation(e1.getId(), bob);
        System.out.println("Solde bob après remboursement : " + bob.getSolde() + " DT");
        System.out.println();

        // --- Événement gratuit ---
        System.out.println("--- Inscription événement gratuit ---");
        eventService.participer(e2.getId(), bob);
        eventService.participer(e2.getId(), carol);
        e2.afficherDetails();
        System.out.println();

        // --- Recherche par lieu ---
        System.out.println("--- Recherche par lieu 'Hall B' ---");
        for (Event e : eventService.rechercherParLieu("Hall B")) {
            System.out.println(e);
        }

        System.out.println("\n========================================");
        System.out.println("           Fin de démonstration         ");
        System.out.println("========================================");
    }
}
