import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.out.println("=== ClubConnect FSEGT — Démonstration Finale ===\n");

        // ============================================================
        //  DONNÉES DE BASE
        // ============================================================
        EventService eventService = new EventService();

        User alice = new User(1, "alice", "alice@fsegt.tn", "pass123", 150.0);
        User bob   = new User(2, "bob",   "bob@fsegt.tn",   "pass456", 80.0);
        User carol = new User(3, "carol", "carol@fsegt.tn", "pass789", 10.0);
        User admin = new User(4, "admin", "admin@fsegt.tn", "admin00", 0.0);
        admin.setRole("admin");

        AuthService.users.add(alice);
        AuthService.users.add(bob);
        AuthService.users.add(carol);
        AuthService.users.add(admin);

        List<User> tousLesUsers = new ArrayList<User>(AuthService.users);

        // ============================================================
        //  SCÉNARIO 1 : Un membre s'inscrit à un événement
        // ============================================================
        System.out.println("📌 Scénario 1 : Un membre s'inscrit à un événement payant");
        System.out.println("-----------------------------------------------------------");

        System.out.println("1. Création du compte alice (organisatrice)... ✅ Compte créé");

        Event hackathon = eventService.creerEvenement(
            "Hackathon FSEGT", "Salle A", "2026-05-10", alice, 3, 25.0);
        System.out.println("2. Création événement '" + hackathon.getTitre()
            + "'... ✅ Événement créé (frais : " + hackathon.getFraisInscription() + " DT)");

        eventService.participer(hackathon.getId(), bob);
        System.out.println("3. Inscription de bob au Hackathon... ✅ Place réservée (reste "
            + hackathon.placesRestantes() + "/" + hackathon.getCapaciteMax() + ")");

        System.out.println("4. Paiement : " + hackathon.getFraisInscription()
            + " DT... ✅ Solde débité — solde bob : " + bob.getSolde() + " DT");

        try {
            eventService.participer(hackathon.getId(), carol);
        } catch (IllegalStateException e) {
            System.out.println("5. Inscription carol (solde " + carol.getSolde()
                + " DT)... ⚠️  " + e.getMessage());
        }
        System.out.println();

        // ============================================================
        //  SCÉNARIO 2 : Annulation et remboursement
        // ============================================================
        System.out.println("📌 Scénario 2 : Annulation de participation");
        System.out.println("--------------------------------------------");

        eventService.annulerParticipation(hackathon.getId(), bob);
        System.out.println("1. Bob annule sa participation... ✅ Annulation effectuée");
        System.out.println("2. Remboursement : " + hackathon.getFraisInscription()
            + " DT... ✅ Solde bob : " + bob.getSolde() + " DT");
        System.out.println();

        // ============================================================
        //  SCÉNARIO 3 : Tableau de bord admin
        // ============================================================
        System.out.println("📌 Scénario 3 : Tableau de bord admin");
        System.out.println("--------------------------------------");

        Session.login(admin);

        System.out.println("1. Connexion admin... ✅ Session ouverte");

        AdminService.voirUtilisateurs();
        System.out.println();

        // Recréer quelques événements pour le tableau de bord
        Event networking = eventService.creerEvenement(
            "Soirée Networking", "Hall B", "2026-06-01", alice);
        Event atelier = eventService.creerEvenement(
            "Atelier Java", "Salle B", "2026-06-15", bob, 10, 15.0);

        eventService.participer(networking.getId(), bob);
        eventService.participer(networking.getId(), carol);
        eventService.participer(atelier.getId(), alice);

        // Calcul tableau de bord
        long membresInscrits = AuthService.users.size();
        long evenementsActifs = eventService.getEvenements().stream()
            .filter(e -> !e.isAnnule()).count();
        double revenusTotal = eventService.getEvenements().stream()
            .filter(e -> !e.isAnnule())
            .mapToDouble(e -> e.getParticipants().size() * e.getFraisInscription())
            .sum();

        System.out.println("2. Tableau de bord :");
        System.out.println("   - Membres inscrits  : " + membresInscrits);
        System.out.println("   - Événements actifs : " + evenementsActifs);
        System.out.println("   - Revenus générés   : " + String.format("%.3f", revenusTotal) + " DT");
        System.out.println();

        // Alertes : événements sans participants
        System.out.println("3. Alertes :");
        boolean alerteTrouvee = false;
        for (Event e : eventService.getEvenements()) {
            if (!e.isAnnule() && e.getParticipants().isEmpty()) {
                System.out.println("   ⚠️  Alerte : '" + e.getTitre()
                    + "' (Event#" + e.getId() + ") n'a aucun participant.");
                alerteTrouvee = true;
            }
        }
        if (!alerteTrouvee) System.out.println("   ✅ Aucune alerte.");
        System.out.println();

        // ============================================================
        //  SCÉNARIO 4 : Gestion des membres par l'admin
        // ============================================================
        System.out.println("📌 Scénario 4 : Gestion des membres");
        System.out.println("-------------------------------------");

        System.out.println("1. Promotion de bob en admin...");
        AdminService.changerRole(bob.getId(), "admin");
        System.out.println("   ✅ Rôle de bob : " + bob.getRole());

        System.out.println("2. Rétrogradation de bob en user...");
        AdminService.changerRole(bob.getId(), "user");
        System.out.println("   ✅ Rôle de bob : " + bob.getRole());

        System.out.println("3. Suppression de carol...");
        AdminService.supprimerMembre(carol.getId());
        System.out.println("   ✅ Membres restants : " + AuthService.getUsers().size());

        System.out.println("4. Tentative auto-suppression admin...");
        try {
            AdminService.supprimerMembre(admin.getId());
        } catch (IllegalStateException e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }
        System.out.println();

        // ============================================================
        //  SCÉNARIO 5 : Contrôle d'accès
        // ============================================================
        System.out.println("📌 Scénario 5 : Contrôle d'accès");
        System.out.println("---------------------------------");

        Session.logout();
        System.out.println("1. Déconnexion... ✅ Session fermée");
        System.out.println("   isLoggedIn : " + Session.isLoggedIn());

        System.out.println("2. Accès admin sans connexion...");
        try {
            AdminService.voirUtilisateurs();
        } catch (IllegalStateException e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }

        System.out.println("3. Connexion membre normal (bob)...");
        Session.login(bob);
        try {
            AdminService.afficherDetails();
        } catch (IllegalStateException e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }
        Session.logout();

        System.out.println("\n=== Fin de démonstration ===");
    }
}
