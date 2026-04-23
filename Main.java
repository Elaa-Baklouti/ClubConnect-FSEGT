import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

<<<<<<< HEAD
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
=======
        System.out.println("=== ClubConnect FSEGT — Demonstration Finale ===\n");

        // --- Donnees de base ---
        User alice = new User(1, "alice", "alice@fsegt.tn", "pass123", 10.0);
        User bob   = new User(2, "bob",   "bob@fsegt.tn",   "pass456", 5.0);
        User carol = new User(3, "carol", "carol@fsegt.tn", "pass789", 8.0);
        User admin = new User(4, "admin", "admin@fsegt.tn", "admin00", 0.0);
        admin.setRole("admin");
        User banni = new User(5, "spam",  "spam@fsegt.tn",  "xxx",    0.0);
        banni.setRole("banni");

        PostService        postService  = new PostService();
        InteractionService interactions = new InteractionService(postService);

        // ============================================================
        // Scenario 1 : Un membre publie un post
        // ============================================================
        System.out.println("Scenario 1 : Un membre publie un post");
        System.out.println("--------------------------------------");

        System.out.println("1. Inscription de Alice (membre)...");
        alice.afficherDetails();
        System.out.println("   OK Compte cree");
        System.out.println();

        System.out.println("2. Alice publie un post (frais : "
                + PostService.FRAIS_PUBLICATION + " DT)...");
        Post p1 = postService.creerEtPublier(
                "Hackathon FSEGT 2026",
                "Rejoignez-nous le 10 mai — Prix : 500 DT !", alice);
        System.out.println("   OK Post publie : " + p1.getTitre());
        System.out.println("   OK Solde alice apres publication : "
                + alice.getSolde() + " DT");
        System.out.println();

        System.out.println("3. Bob publie un post...");
        Post p2 = postService.creerEtPublier(
                "Atelier Java Debutants",
                "Samedi 15h — Salle B. Gratuit !", bob);
        System.out.println("   OK Post publie : " + p2.getTitre());
        System.out.println();

        System.out.println("4. Carol tente de publier (solde insuffisant)...");
        User carolPauvre = new User(6, "carol2", "carol2@fsegt.tn", "p", 0.2);
        try {
            postService.creerEtPublier("Post impossible", "...", carolPauvre);
        } catch (IllegalStateException e) {
            System.out.println("   ERREUR attendue : " + e.getMessage());
>>>>>>> feature/posts
        }
        System.out.println();

        // ============================================================
<<<<<<< HEAD
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
=======
        // Scenario 2 : Interactions sur les posts
        // ============================================================
        System.out.println("Scenario 2 : Interactions sur les posts");
        System.out.println("----------------------------------------");

        System.out.println("1. Bob et Carol commentent le post de Alice...");
        interactions.commenter(p1.getId(), bob,   "Super initiative, je participe !");
        interactions.commenter(p1.getId(), carol, "Quelle est la date limite d'inscription ?");
        System.out.println("   OK 2 commentaires ajoutes");
        System.out.println();

        System.out.println("2. Likes sur le post #" + p1.getId() + "...");
        interactions.liker(p1.getId(), bob);
        interactions.liker(p1.getId(), carol);
        System.out.println("   OK Likes Post#" + p1.getId() + " : " + p1.getLikes());
        System.out.println();

        System.out.println("3. Alice tente de liker son propre post...");
        try {
            interactions.liker(p1.getId(), alice);
        } catch (IllegalStateException e) {
            System.out.println("   ERREUR attendue : " + e.getMessage());
        }
        System.out.println();

        System.out.println("4. Bob retire son like...");
        interactions.retirerLike(p1.getId(), bob);
        System.out.println("   OK Likes Post#" + p1.getId()
                + " apres retrait : " + p1.getLikes());
        System.out.println();

        System.out.println("5. Alice epingle son post...");
        p1.epingler(alice);
        System.out.println("   OK Post epingle : " + p1.isEpingle());
        System.out.println();

        // ============================================================
        // Scenario 3 : Signalement et moderation
        // ============================================================
        System.out.println("Scenario 3 : Signalement et moderation");
        System.out.println("---------------------------------------");

        System.out.println("1. Creation d'un post douteux par bob...");
        Post pDouteux = postService.creerEtPublier(
                "Contenu douteux", "Message inapproprie...", bob);
        System.out.println("   OK Post cree : Post#" + pDouteux.getId());
        System.out.println();

        System.out.println("2. Alice et Carol signalent le post...");
        interactions.signalerPost(pDouteux.getId(), alice, "Contenu inapproprie");
        interactions.signalerPost(pDouteux.getId(), carol, "Spam");
        System.out.println("   Signalements : " + pDouteux.getSignalements().size()
                + "/" + InteractionService.SEUIL_SIGNALEMENT);
        System.out.println();

        System.out.println("3. Admin signale aussi — masquage automatique...");
        User dave = new User(7, "dave", "dave@fsegt.tn", "p", 5.0);
        postService.creerEtPublier("Post dave", "test", dave);
        interactions.signalerPost(pDouteux.getId(), dave, "Fausses informations");
        System.out.println("   OK Post#" + pDouteux.getId()
                + " masque automatiquement (" + InteractionService.SEUIL_SIGNALEMENT
                + " signalements atteints)");
        System.out.println();

        System.out.println("4. Admin modere un autre post signale...");
        Post pModere = postService.creerEtPublier("Post a moderer", "...", carol);
        interactions.signalerPost(pModere.getId(), alice, "Inapproprie");
        double soldeAvant = carol.getSolde();
        postService.modererPost(pModere.getId(), admin);
        System.out.println("   OK Post#" + pModere.getId() + " supprime par admin");
        System.out.println("   OK Remboursement carol : " + carol.getSolde()
                + " DT (avant : " + soldeAvant + " DT)");
        System.out.println();

        // ============================================================
        // Scenario 4 : Tableau de bord
        // ============================================================
        System.out.println("Scenario 4 : Tableau de bord");
        System.out.println("----------------------------");

        long totalPublies = postService.getPosts().stream()
                .filter(Post::isPublie).count();
        long totalEpingles = postService.getPosts().stream()
                .filter(Post::isEpingle).count();
        int totalLikes = postService.getPosts().stream()
                .mapToInt(Post::getLikes).sum();
        double revenus = postService.getPosts().stream()
                .filter(Post::isPublie).count() * PostService.FRAIS_PUBLICATION;

        System.out.println("- Posts publies        : " + totalPublies);
        System.out.println("- Posts epingles       : " + totalEpingles);
        System.out.println("- Total likes          : " + totalLikes);
        System.out.println("- Revenus publication  : "
                + String.format("%.3f", revenus) + " DT");
        System.out.println();

        // Alerte posts sans interaction
        System.out.println("Alertes :");
        postService.getPosts().stream()
                .filter(Post::isPublie)
                .filter(p -> p.getLikes() == 0
                        && p.getCommentaires().stream()
                               .noneMatch(c -> !c.startsWith("[like:")))
                .forEach(p -> System.out.println("  ALERTE : Post#" + p.getId()
                        + " [" + p.getTitre() + "] n'a aucune interaction."));
        System.out.println();

        System.out.println("--- Posts par popularite ---");
        postService.classerParPopularite().forEach(System.out::println);

        System.out.println("\n=== Fin demonstration ===");
>>>>>>> feature/posts
    }
}
