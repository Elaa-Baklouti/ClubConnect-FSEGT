package com.clubconnect.models;

/**
 * ============================================================
 *  ClubConnect FSEGT — Main.java (V1)
 *  Demonstration du module InteractionService
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
        System.out.println("  ClubConnect FSEGT — Demo InteractionService");
        System.out.println("==============================================");
        System.out.println();

        // ============================================================
        //  DONNEES DE BASE (en dur)
        // ============================================================
        User alice = new User(1, "alice", "alice@fsegt.tn", "pass123", 10.0);
        User bob   = new User(2, "bob",   "bob@fsegt.tn",   "pass456",  5.0);
        User carol = new User(3, "carol", "carol@fsegt.tn", "pass789",  8.0);
        User admin = new User(4, "admin", "admin@fsegt.tn", "admin00",  0.0);
        admin.setRole("admin");
        User banni = new User(5, "spam",  "spam@fsegt.tn",  "xxx",      0.0);
        banni.setRole("banni");

        PostService        postService  = new PostService();
        InteractionService interactions = new InteractionService(postService);

        // ============================================================
        //  SCENARIO 1 : Publication de posts
        // ============================================================
        System.out.println("--- Scenario 1 : Publication de posts ---");

        System.out.println("1. Alice publie un post (frais : "
            + PostService.FRAIS_PUBLICATION + " DT)...");
        Post p1 = postService.creerEtPublier(
            "Hackathon FSEGT 2026",
            "Rejoignez-nous le 10 mai. Prix : 500 DT !", alice);
        System.out.println("   OK : " + p1);
        System.out.println("   Solde alice apres : "
            + String.format("%.3f", alice.getSolde()) + " DT");
        System.out.println();

        System.out.println("2. Bob publie un post...");
        Post p2 = postService.creerEtPublier(
            "Atelier Java Debutants",
            "Samedi 15h - Salle B. Gratuit !", bob);
        System.out.println("   OK : " + p2);
        System.out.println();

        System.out.println("3. Tentative publication (solde insuffisant)...");
        User pauvre = new User(6, "pauvre", "pauvre@fsegt.tn", "p", 0.2);
        try {
            postService.creerEtPublier("Post impossible", "...", pauvre);
        } catch (IllegalStateException e) {
            System.out.println("   ERREUR attendue -> " + e.getMessage());
        }
        System.out.println();

        System.out.println("4. Tentative publication (compte banni)...");
        try {
            postService.creerEtPublier("Post banni", "...", banni);
        } catch (IllegalStateException e) {
            System.out.println("   ERREUR attendue -> " + e.getMessage());
        }
        System.out.println();

        // ============================================================
        //  SCENARIO 2 : Commentaires
        // ============================================================
        System.out.println("--- Scenario 2 : Commentaires ---");

        System.out.println("1. Bob commente le post de alice...");
        interactions.commenter(p1.getId(), bob, "Super initiative, je participe !");
        System.out.println("   OK");

        System.out.println("2. Carol commente...");
        interactions.commenter(p1.getId(), carol, "Quelle est la date limite ?");
        System.out.println("   OK");

        System.out.println("3. Affichage des commentaires :");
        interactions.afficherCommentaires(p1.getId());
        System.out.println();

        System.out.println("4. Tentative commentaire (compte banni)...");
        try {
            interactions.commenter(p1.getId(), banni, "Spam !");
        } catch (IllegalStateException e) {
            System.out.println("   ERREUR attendue -> " + e.getMessage());
        }
        System.out.println();

        System.out.println("5. Tentative commentaire trop long (> 280 car.)...");
        try {
            interactions.commenter(p1.getId(), bob, "x".repeat(281));
        } catch (IllegalArgumentException e) {
            System.out.println("   ERREUR attendue -> " + e.getMessage());
        }
        System.out.println();

        // ============================================================
        //  SCENARIO 3 : Likes
        // ============================================================
        System.out.println("--- Scenario 3 : Likes ---");

        System.out.println("1. Bob et Carol likent le post de alice...");
        interactions.liker(p1.getId(), bob);
        interactions.liker(p1.getId(), carol);
        System.out.println("   Likes Post#" + p1.getId() + " : " + p1.getLikes());
        System.out.println();

        System.out.println("2. Alice tente de liker son propre post...");
        try {
            interactions.liker(p1.getId(), alice);
        } catch (IllegalStateException e) {
            System.out.println("   ERREUR attendue -> " + e.getMessage());
        }
        System.out.println();

        System.out.println("3. Bob tente de liker deux fois...");
        try {
            interactions.liker(p1.getId(), bob);
        } catch (IllegalStateException e) {
            System.out.println("   ERREUR attendue -> " + e.getMessage());
        }
        System.out.println();

        System.out.println("4. Bob retire son like...");
        interactions.retirerLike(p1.getId(), bob);
        System.out.println("   Likes apres retrait : " + p1.getLikes());
        System.out.println();

        // ============================================================
        //  SCENARIO 4 : Epinglage
        // ============================================================
        System.out.println("--- Scenario 4 : Epinglage ---");

        System.out.println("1. Alice epingle son post...");
        p1.epingler(alice);
        System.out.println("   Post epingle : " + p1.isEpingle());
        System.out.println();

        System.out.println("2. Bob tente d'epingler le post de alice...");
        try {
            p1.epingler(bob);
        } catch (IllegalStateException e) {
            System.out.println("   ERREUR attendue -> " + e.getMessage());
        }
        System.out.println();

        System.out.println("3. Admin epingle le post de bob...");
        p2.epingler(admin);
        System.out.println("   Post#" + p2.getId() + " epingle par admin : " + p2.isEpingle());
        System.out.println();

        System.out.println("4. Affichage posts (epingles en premier) :");
        postService.voirPosts();
        System.out.println();

        // ============================================================
        //  SCENARIO 5 : Signalement et moderation
        // ============================================================
        System.out.println("--- Scenario 5 : Signalement et moderation ---");

        System.out.println("1. Bob publie un post douteux...");
        Post pDouteux = postService.creerEtPublier(
            "Contenu douteux", "Message inapproprie...", bob);
        System.out.println("   OK : Post#" + pDouteux.getId());
        System.out.println();

        System.out.println("2. Alice et Carol signalent le post...");
        interactions.signalerPost(pDouteux.getId(), alice, "Contenu inapproprie");
        interactions.signalerPost(pDouteux.getId(), carol, "Spam");
        System.out.println("   Signalements : " + pDouteux.getSignalements().size()
            + " / " + InteractionService.SEUIL_SIGNALEMENT);
        System.out.println();

        System.out.println("3. 3e signalement -> masquage automatique...");
        User dave = new User(7, "dave", "dave@fsegt.tn", "dave99", 5.0);
        postService.creerEtPublier("Post dave", "test", dave);
        interactions.signalerPost(pDouteux.getId(), dave, "Fausses informations");
        System.out.println("   Post#" + pDouteux.getId()
            + " masque automatiquement (seuil "
            + InteractionService.SEUIL_SIGNALEMENT + " atteint)");
        System.out.println("   Posts restants : " + postService.getPosts().size());
        System.out.println();

        System.out.println("4. Admin modere un post signale...");
        Post pModere = postService.creerEtPublier("Post a moderer", "...", carol);
        interactions.signalerPost(pModere.getId(), alice, "Inapproprie");
        double soldeAvant = carol.getSolde();
        postService.modererPost(pModere.getId(), admin);
        System.out.println("   Post#" + pModere.getId() + " supprime par admin");
        System.out.println("   Remboursement carol : "
            + String.format("%.3f", carol.getSolde())
            + " DT (avant : " + String.format("%.3f", soldeAvant) + " DT)");
        System.out.println();

        // ============================================================
        //  SCENARIO 6 : Tableau de bord
        // ============================================================
        System.out.println("--- Scenario 6 : Tableau de bord ---");

        long   totalPublies = postService.getPosts().stream()
                                .filter(Post::isPublie).count();
        long   totalEpingles = postService.getPosts().stream()
                                .filter(Post::isEpingle).count();
        int    totalLikes   = postService.getPosts().stream()
                                .mapToInt(Post::getLikes).sum();
        double revenus      = totalPublies * PostService.FRAIS_PUBLICATION;

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
        postService.getPosts().stream()
            .filter(Post::isPublie)
            .filter(p -> p.getLikes() == 0
                && p.getCommentaires().stream()
                       .noneMatch(c -> !c.startsWith("[like:")))
            .forEach(p -> System.out.println("  !! Post#" + p.getId()
                + " [" + p.getTitre() + "] sans interaction"));
        System.out.println();

        System.out.println("  Details Post#" + p1.getId() + " :");
        interactions.afficherDetailsPost(p1.getId());
        System.out.println();

        System.out.println("==============================================");
        System.out.println("  Fin demo — InteractionService V1 OK        ");
        System.out.println("==============================================");
    }
}
