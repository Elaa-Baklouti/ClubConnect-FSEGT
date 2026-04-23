public class Main {
    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("       ClubConnect — Démonstration      ");
        System.out.println("========================================\n");

        // --- Création des utilisateurs ---
        User alice = new User(1, "alice", "alice@email.com", "pass123");
        User bob   = new User(2, "bob",   "bob@email.com",   "pass456");

        alice.afficherDetails();
        System.out.println();
        bob.afficherDetails();
        System.out.println();

        // --- Connexion ---
        alice.seConnecter();
        bob.seConnecter();
        System.out.println();

        // --- Création du service événement ---
        EventService eventService = new EventService();

        // --- Créer des événements ---
        Event e1 = eventService.creerEvenement("Hackathon FSEGT", "Salle A", "2026-05-10", alice);
        Event e2 = eventService.creerEvenement("Soirée Networking", "Hall B", "2026-06-01", bob);
        System.out.println();

        // --- Afficher les détails d'un événement ---
        e1.afficherDetails();
        System.out.println();

        // --- Participer à un événement ---
        eventService.participer(e1.getId(), bob);
        eventService.participer(e1.getId(), bob); // doublon
        eventService.participer(e2.getId(), alice);
        System.out.println();

        // --- Afficher tous les événements ---
        System.out.println("--- Liste des événements ---");
        eventService.afficherDetails();
        System.out.println();

        // --- Déconnexion ---
        alice.seDeconnecter();
        bob.seDeconnecter();

        System.out.println("\n========================================");
        System.out.println("           Fin de démonstration         ");
        System.out.println("========================================");
    }
}
