package com.clubconnect.models;

/**
 * ============================================================
 *  ClubConnect FSEGT — Demonstration Finale
 *  Module : InteractionService — scenarios de bout en bout
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
        System.out.println("  ClubConnect FSEGT -- Demonstration Finale  ");
        System.out.println("==============================================");
        System.out.println();

        // ============================================================
        //  DONNEES DE BASE
        // ============================================================
        User alice = new User(1, "alice", "alice@fsegt.tn", "pass123", 20.0);
        User bob   = new User(2, "bob",   "bob@fsegt.tn",   "pass456", 15.0);
        User carol = new User(3, "carol", "carol@fsegt.tn", "pass789", 10.0);
        User dave  = new User(4, "dave",  "dave@fsegt.tn",  "dave99",   8.0);
        User admin = new User(5, "admin", "admin@fsegt.tn", "admin00",  0.0);
        admin.setRole("admin");
        User spam  = new User(6, "spam",  "spam@fsegt.tn",  "xxx",      0.0);
        spam.setRole("banni");

        PostService        postService  = new PostService();
        InteractionService interactions = new InteractionService(postService);

        // ============================================================
        //  SCENARIO 1 : Un membre publie un post et recoit des interactions
        // ============================================================
        System.out.println(">> Scenario 1 : Un membre publie un post");
        System.out.println("------------------------------------------");

        System.out.println("1. Inscription de Alice (membre)...");
        alice.afficherDetails();
        System.out.println("   Compte cree  [OK]");
        System.out.println();

        System.out.println("2. Alice publie 'Hackathon FSEGT 2026'"
            + " (frais : " + PostService.FRAIS_PUBLICATION + " DT)...");
        Post p1 = postService.creerEtPublier(
            "Hackathon FSEGT 2026",
            "Rejoignez-nous le 10 mai - Prix : 500 DT !", alice);
        System.out.println("   Post publie : " + p1.getTitre() + "  [OK]");
        System.out.println("   Solde alice apres publication : "
            + String.format("%.3f", alice.getSolde()) + " DT  [OK]");
        System.out.println();

        System.out.println("3. Bob publie 'Atelier Java Debutants'...");
        Post p2 = postService.creerEtPublier(
            "Atelier Java Debutants",
            "Samedi 15h - Salle B. Gratuit !", bob);
        System.out.println("   Post publie : " + p2.getTitre() + "  [OK]");
        System.out.println();

        System.out.println("4. Carol publie 'Soiree Networking'...");
        Post p3 = postService.creerEtPublier(
            "Soiree Networking",
            "Vendredi 18h - Hall principal. Venez nombreux !", carol);
        System.out.println("   Post publie : " + p3.getTitre() + "  [OK]");
        System.out.println();

        System.out.println("5. Tentative publication (solde insuffisant)...");
        User pauvre = new User(7, "pauvre", "pauvre@fsegt.tn", "p", 0.2);
        try {
            postService.creerEtPublier("Post impossible", "...", pauvre);
        } catch (IllegalStateException e) {
            System.out.println("   Refuse -> " + e.getMessage() + "  [OK]");
        }
        System.out.println();

        System.out.println("6. Tentative publication (compte banni)...");
        try {
            postService.creerEtPublier("Post spam", "...", spam);
        } catch (IllegalStateException e) {
            System.out.println("   Refuse -> " + e.getMessage() + "  [OK]");
        }
        System.out.println();

        // ============================================================
        //  SCENARIO 2 : Interactions — commentaires et likes
        // ============================================================
        System.out.println(">> Scenario 2 : Commentaires et likes");
        System.out.println("--------------------------------------");

        System.out.println("1. Bob et Carol commentent le post de Alice...");
        interactions.commenter(p1.getId(), bob,
            "Super initiative, je participe !");
        interactions.commenter(p1.getId(), carol,
            "Quelle est la date limite d'inscription ?");
        interactions.commenter(p1.getId(), dave,
            "Je serai present avec mon equipe !");
        System.out.println("   3 commentaires ajoutes  [OK]");
        interactions.afficherCommentaires(p1.getId());
        System.out.println();

        System.out.println("2. Likes sur le post de Alice...");
        interactions.liker(p1.getId(), bob);
        interactions.liker(p1.getId(), carol);
        interactions.liker(p1.getId(), dave);
        System.out.println("   Likes Post#" + p1.getId()
            + " : " + p1.getLikes() + "  [OK]");
        System.out.println();

        System.out.println("3. Alice tente de liker son propre post...");
        try {
            interactions.liker(p1.getId(), alice);
        } catch (IllegalStateException e) {
            System.out.println("   Refuse -> " + e.getMessage() + "  [OK]");
        }
        System.out.println();

        System.out.println("4. Bob tente de liker deux fois...");
        try {
            interactions.liker(p1.getId(), bob);
        } catch (IllegalStateException e) {
            System.out.println("   Refuse -> " + e.getMessage() + "  [OK]");
        }
        System.out.println();

        System.out.println("5. Carol retire son like...");
        interactions.retirerLike(p1.getId(), carol);
        System.out.println("   Likes apres retrait : "
            + p1.getLikes() + "  [OK]");
        System.out.println();

        System.out.println("6. Compte banni tente de commenter...");
        try {
            interactions.commenter(p1.getId(), spam, "Spam !");
        } catch (IllegalStateException e) {
            System.out.println("   Refuse -> " + e.getMessage() + "  [OK]");
        }
        System.out.println();

        System.out.println("7. Commentaire trop long (> 280 car.)...");
        try {
            interactions.commenter(p1.getId(), bob, "x".repeat(281));
        } catch (IllegalArgumentException e) {
            System.out.println("   Refuse -> " + e.getMessage() + "  [OK]");
        }
        System.out.println();

        // ============================================================
        //  SCENARIO 3 : Epinglage de posts
        // ============================================================
        System.out.println(">> Scenario 3 : Epinglage");
        System.out.println("-------------------------");

        System.out.println("1. Alice epingle son post...");
        p1.epingler(alice);
        System.out.println("   Post#" + p1.getId()
            + " epingle : " + p1.isEpingle() + "  [OK]");
        System.out.println();

        System.out.println("2. Admin epingle le post de Bob...");
        p2.epingler(admin);
        System.out.println("   Post#" + p2.getId()
            + " epingle par admin : " + p2.isEpingle() + "  [OK]");
        System.out.println();

        System.out.println("3. Dave tente d'epingler le post de Carol...");
        try {
            p3.epingler(dave);
        } catch (IllegalStateException e) {
            System.out.println("   Refuse -> " + e.getMessage() + "  [OK]");
        }
        System.out.println();

        System.out.println("4. Posts publies (epingles en premier) :");
        postService.voirPosts();
        System.out.println();

        // ============================================================
        //  SCENARIO 4 : Signalement et moderation
        // ============================================================
        System.out.println(">> Scenario 4 : Signalement et moderation");
        System.out.println("------------------------------------------");

        System.out.println("1. Dave publie un post douteux...");
        Post pDouteux = postService.creerEtPublier(
            "Contenu douteux", "Message inapproprie...", dave);
        System.out.println("   Post cree : Post#" + pDouteux.getId() + "  [OK]");
        System.out.println();

        System.out.println("2. Alice et Bob signalent le post...");
        interactions.signalerPost(pDouteux.getId(), alice, "Contenu inapproprie");
        interactions.signalerPost(pDouteux.getId(), bob,   "Spam");
        System.out.println("   Signalements : "
            + pDouteux.getSignalements().size()
            + " / " + InteractionService.SEUIL_SIGNALEMENT);
        System.out.println();

        System.out.println("3. Carol signale aussi -> masquage automatique...");
        interactions.signalerPost(pDouteux.getId(), carol, "Fausses informations");
        System.out.println("   Post#" + pDouteux.getId()
            + " masque automatiquement (seuil "
            + InteractionService.SEUIL_SIGNALEMENT + " atteint)  [OK]");
        System.out.println("   Posts restants : "
            + postService.getPosts().size());
        System.out.println();

        System.out.println("4. Bob publie un post signale pour moderation...");
        Post pModere = postService.creerEtPublier(
            "Post a moderer", "Contenu litigieux...", bob);
        interactions.signalerPost(pModere.getId(), alice, "Inapproprie");
        double soldeAvant = bob.getSolde();
        postService.modererPost(pModere.getId(), admin);
        System.out.println("   Post#" + pModere.getId()
            + " supprime par admin  [OK]");
        System.out.println("   Remboursement bob : "
            + String.format("%.3f", bob.getSolde())
            + " DT (avant : " + String.format("%.3f", soldeAvant) + " DT)  [OK]");
        System.out.println();

        System.out.println("5. Tentative moderation sans droits admin...");
        try {
            postService.modererPost(p3.getId(), alice);
        } catch (IllegalStateException e) {
            System.out.println("   Refuse -> " + e.getMessage() + "  [OK]");
        }
        System.out.println();

        // ============================================================
        //  SCENARIO 5 : Visiteur — lecture seule sans connexion
        // ============================================================
        System.out.println(">> Scenario 5 : Acces visiteur (sans connexion)");
        System.out.println("------------------------------------------------");

        System.out.println("1. Visiteur consulte les commentaires publics...");
        interactions.voirCommentairesPublics(p1.getId());
        System.out.println();

        System.out.println("2. Visiteur tente de liker (non autorise)...");
        try {
            interactions.liker(p1.getId(), null);
        } catch (IllegalStateException e) {
            System.out.println("   Refuse -> " + e.getMessage() + "  [OK]");
        }
        System.out.println();

        System.out.println("3. Visiteur tente de commenter (non autorise)...");
        try {
            interactions.commenter(p1.getId(), null, "test");
        } catch (IllegalStateException e) {
            System.out.println("   Refuse -> " + e.getMessage() + "  [OK]");
        }
        System.out.println();

        // ============================================================
        //  SCENARIO 6 : Tableau de bord final
        // ============================================================
        System.out.println(">> Scenario 6 : Tableau de bord");
        System.out.println("--------------------------------");

        long   totalPublies  = postService.getPosts().stream()
                                   .filter(Post::isPublie).count();
        long   totalEpingles = postService.getPosts().stream()
                                   .filter(Post::isEpingle).count();
        int    totalLikes    = postService.getPosts().stream()
                                   .mapToInt(Post::getLikes).sum();
        double revenus       = totalPublies * PostService.FRAIS_PUBLICATION;

        System.out.println("  Posts publies   : " + totalPublies);
        System.out.println("  Posts epingles  : " + totalEpingles);
        System.out.println("  Total likes     : " + totalLikes);
        System.out.println("  Revenus         : "
            + String.format("%.3f", revenus) + " DT");
        System.out.println();

        System.out.println("  Classement par popularite :");
        postService.classerParPopularite()
            .forEach(p -> System.out.println("    " + p));
        System.out.println();

        System.out.println("  Alertes (posts sans interaction) :");
        boolean aucuneAlerte = true;
        for (Post p : postService.getPosts()) {
            if (!p.isPublie()) continue;
            long nbComm = p.getCommentaires().stream()
                .filter(c -> !c.startsWith("[like:")).count();
            if (p.getLikes() == 0 && nbComm == 0) {
                System.out.println("  !! Post#" + p.getId()
                    + " [" + p.getTitre() + "] sans interaction");
                aucuneAlerte = false;
            }
        }
        if (aucuneAlerte)
            System.out.println("  Aucune alerte.");
        System.out.println();

        System.out.println("  Details Post#" + p1.getId() + " :");
        interactions.afficherDetailsPost(p1.getId());
        System.out.println();

        System.out.println("==============================================");
        System.out.println("  Fin demonstration -- InteractionService OK  ");
        System.out.println("==============================================");
    }
}
