import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.out.println("=== ClubConnect FSEGT — Démonstration Finale ===\n");

        // ============================================================
        //  DONNÉES DE BASE
        // ============================================================
        EventService eventService = new EventService();
        List<User> tousLesUsers  = new ArrayList<>();

        User alice = new User(1, "alice", "alice@fsegt.tn", "pass123", 150.0);
        User bob   = new User(2, "bob",   "bob@fsegt.tn",   "pass456", 80.0);
        User carol = new User(3, "carol", "carol@fsegt.tn", "pass789", 10.0);
        User admin = new User(4, "admin", "admin@fsegt.tn", "admin00", 0.0);
        admin.setRole("admin");

        tousLesUsers.add(alice);
        tousLesUsers.add(bob);
        tousLesUsers.add(carol);
        tousLesUsers.add(admin);

        // ============================================================
        //  SCÉNARIO 1 : Un membre s'inscrit à un événement payant
        // ============================================================
        System.out.println("📌 Scénario 1 : Un membre s'inscrit à un événement payant");
        System.out.println("----------------------------------------------------------");

        // 1. Création du compte
        System.out.println("1. Inscription de Carol (membre)...");
        carol.afficherDetails();
        System.out.println("   ✅ Compte créé");
        System.out.println();

        // 2. Création de l'événement par alice
        System.out.println("2. Alice crée l'événement 'Hackathon FSEGT'...");
        Event hackathon = eventService.creerEvenement(
            "Hackathon FSEGT", "Salle A", "2026-05-10", alice, 3, 25.0
        );
        System.out.println("   ✅ Événement créé : " + hackathon.getTitre()
            + " | Frais : " + hackathon.getFraisInscription() + " DT"
            + " | Capacité : " + hackathon.getCapaciteMax());
        System.out.println();

        // 3. Bob s'inscrit
        System.out.println("3. Bob s'inscrit au Hackathon FSEGT...");
        eventService.participer(hackathon.getId(), bob);
        System.out.println("   ✅ Place réservée (reste " + hackathon.placesRestantes()
            + "/" + hackathon.getCapaciteMax() + ")");
        System.out.println();

        // 4. Paiement automatique
        System.out.println("4. Paiement : 25.0 DT débité du solde de Bob...");
        System.out.println("   ✅ Solde bob après inscription : " + bob.getSolde() + " DT");
        System.out.println();

        // 5. Carol tente de s'inscrire (solde insuffisant)
        System.out.println("5. Carol tente de s'inscrire (solde : " + carol.getSolde() + " DT)...");
        try {
            eventService.participer(hackathon.getId(), carol);
        } catch (IllegalStateException ex) {
            System.out.println("   ⚠️  Inscription refusée : " + ex.getMessage());
        }
        System.out.println();

        // ============================================================
        //  SCÉNARIO 2 : Annulation et remboursement
        // ============================================================
        System.out.println("📌 Scénario 2 : Annulation de participation et remboursement");
        System.out.println("--------------------------------------------------------------");

        System.out.println("1. Bob annule sa participation au Hackathon...");
        eventService.annulerParticipation(hackathon.getId(), bob);
        System.out.println("   ✅ Participation annulée");
        System.out.println("   ✅ Remboursement effectué — Solde bob : " + bob.getSolde() + " DT");
        System.out.println();

        System.out.println("2. Alice annule l'événement entier...");
        // D'abord réinscrire bob pour tester l'annulation globale
        eventService.participer(hackathon.getId(), bob);
        eventService.annulerEvenement(hackathon.getId(), alice, tousLesUsers);
        System.out.println("   ✅ Événement annulé");
        System.out.println("   ✅ Remboursement bob : " + bob.getSolde() + " DT");
        System.out.println();

        // ============================================================
        //  SCÉNARIO 3 : Événement gratuit et recherche
        // ============================================================
        System.out.println("📌 Scénario 3 : Événement gratuit et recherche par lieu");
        System.out.println("----------------------------------------------------------");

        Event networking = eventService.creerEvenement(
            "Soirée Networking", "Hall B", "2026-06-01", alice
        );
        Event atelier = eventService.creerEvenement(
            "Atelier Java", "Hall B", "2026-06-15", bob
        );

        eventService.participer(networking.getId(), bob);
        eventService.participer(networking.getId(), carol);
        eventService.participer(atelier.getId(), carol);

        System.out.println("1. Recherche événements à 'Hall B'...");
        List<Event> resultats = eventService.rechercherParLieu("Hall B");
        System.out.println("   ✅ " + resultats.size() + " événement(s) trouvé(s) :");
        for (Event e : resultats) {
            System.out.println("      - " + e);
        }
        System.out.println();

        // ============================================================
        //  SCÉNARIO 4 : Tableau de bord
        // ============================================================
        System.out.println("📌 Scénario 4 : Tableau de bord");
        System.out.println("--------------------------------");

        int totalActifs = 0;
        double revenusTotal = 0.0;
        for (Event e : eventService.getEvenements()) {
            if (!e.isAnnule()) {
                totalActifs++;
                revenusTotal += e.getParticipants().size() * e.getFraisInscription();
            }
        }

        System.out.println("- Membres inscrits       : " + tousLesUsers.size());
        System.out.println("- Événements actifs      : " + totalActifs);
        System.out.println("- Revenus générés        : " + revenusTotal + " DT");

        // Alerte événement sans participants
        System.out.println();
        for (Event e : eventService.getEvenements()) {
            if (!e.isAnnule() && e.getParticipants().isEmpty()) {
                System.out.println("⚠️  Alerte : '" + e.getTitre()
                    + "' (Event#" + e.getId() + ") n'a aucun participant.");
            }
        }

        System.out.println("\n=== Fin de démonstration ===");
    }
}
