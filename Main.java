public class Main {
    public static void main(String[] args) {

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
                "Rejoignez-nous le 10 mai - Prix : 500 DT !", alice);
        System.out.println("   OK Post publie : " + p1.getTitre());
        System.out.println("   OK Solde alice apres publication : "
                + alice.getSolde() + " DT");
        System.out.println();

        System.out.println("3. Bob publie un post...");
        Post p2 = postService.creerEtPublier(
                "Atelier Java Debutants",
                "Samedi 15h - Salle B. Gratuit !", bob);
        System.out.println("   OK Post publie : " + p2.getTitre());
        System.out.println();

        System.out.println("4. Carol tente de publier (solde insuffisant)...");
        User carolPauvre = new User(6, "carol2", "carol2@fsegt.tn", "p", 0.2);
        try {
            postService.creerEtPublier("Post impossible", "...", carolPauvre);
        } catch (IllegalStateException e) {
            System.out.println("   ERREUR attendue : " + e.getMessage());
        }
        System.out.println();

        // ============================================================
        // Scenario 2 : Interactions sur les posts
        // ============================================================
        System.out.println("Scenario 2 : Interactions sur les posts");
        System.out.println("----------------------------------------");

        System.out.println("1. Bob et Carol commentent le post de Alice...");
        interactions.commenter(p1.getId(), bob,   "Super initiative, je participe !");
        interactions.commenter(p1.getId(), carol, "Quelle est la date limite ?");
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

        System.out.println("3. 3e signalement - masquage automatique...");
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

        long totalPublies  = postService.getPosts().stream().filter(Post::isPublie).count();
        long totalEpingles = postService.getPosts().stream().filter(Post::isEpingle).count();
        int  totalLikes    = postService.getPosts().stream().mapToInt(Post::getLikes).sum();
        double revenus     = totalPublies * PostService.FRAIS_PUBLICATION;

        System.out.println("- Posts publies        : " + totalPublies);
        System.out.println("- Posts epingles       : " + totalEpingles);
        System.out.println("- Total likes          : " + totalLikes);
        System.out.println("- Revenus publication  : "
                + String.format("%.3f", revenus) + " DT");
        System.out.println();

        System.out.println("Alertes :");
        postService.getPosts().stream()
                .filter(Post::isPublie)
                .filter(p -> p.getLikes() == 0
                        && p.getCommentaires().stream().noneMatch(c -> !c.startsWith("[like:")))
                .forEach(p -> System.out.println("  ALERTE : Post#" + p.getId()
                        + " [" + p.getTitre() + "] n'a aucune interaction."));
        System.out.println();

        System.out.println("--- Posts par popularite ---");
        postService.classerParPopularite().forEach(System.out::println);

        // ============================================================
        // Scenario 5 : Visiteur — lecture seule sans connexion
        // ============================================================
        System.out.println("Scenario 5 : Visiteur (sans connexion)");
        System.out.println("---------------------------------------");

        System.out.println("1. Visiteur consulte les posts publics...");
        postService.voirPostsPublics();
        System.out.println();

        System.out.println("2. Visiteur consulte les commentaires du Post#" + p1.getId() + "...");
        interactions.voirCommentairesPublics(p1.getId());
        System.out.println();

        System.out.println("3. Visiteur tente de liker (non autorise)...");
        try {
            interactions.liker(p1.getId(), null);
        } catch (IllegalStateException e) {
            System.out.println("   ERREUR attendue : " + e.getMessage());
        }
        System.out.println();

        System.out.println("4. Visiteur tente de commenter (non autorise)...");
        try {
            interactions.commenter(p1.getId(), null, "test");
        } catch (IllegalStateException e) {
            System.out.println("   ERREUR attendue : " + e.getMessage());
        }

        System.out.println("\n=== Fin demonstration ===");
    }
}
