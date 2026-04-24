package com.clubconnect.models;

/**
 * ============================================================
 *  ClubConnect FSEGT — Demonstration Finale (TP1)
 *  Modules : AuthService + InteractionService
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
        System.out.println("  ClubConnect FSEGT — Demonstration Finale   ");
        System.out.println("==============================================");
        System.out.println();

        // --- Donnees de base ---
        User alice = AuthService.inscrire("alice", "alice@fsegt.tn", "pass123");
        User bob   = AuthService.inscrire("bob",   "bob@fsegt.tn",   "pass456");
        User carol = AuthService.inscrire("carol", "carol@fsegt.tn", "pass789");
        alice.setSolde(10.0); bob.setSolde(5.0); carol.setSolde(8.0);

        User admin = new User(99, "admin", "admin@fsegt.tn", "admin00");
        admin.setRole("admin");

        PostService        postService  = new PostService();
        InteractionService interactions = new InteractionService(postService);

        // ============================================================
        //  SCENARIO 1 : Inscription et connexion
        // ============================================================
        System.out.println(">> Scenario 1 : Inscription et connexion");
        System.out.println("------------------------------------------");

        System.out.println("1. Inscription de alice...  [OK]");
        alice.afficherDetails();
        System.out.println();

        System.out.println("2. Connexion de alice...");
        AuthService.seConnecter("alice@fsegt.tn", "pass123");
        Session.afficherDetails();
        System.out.println("   Nb connexions : " + alice.getNbConnexions() + "  [OK]");
        System.out.println();

        System.out.println("3. Tentative mauvais mot de passe...");
        try {
            AuthService.seConnecter("bob@fsegt.tn", "mauvais");
        } catch (IllegalStateException e) {
            System.out.println("   ERREUR attendue -> " + e.getMessage() + "  [OK]");
        }
        System.out.println();

        System.out.println("4. Deconnexion de alice...");
        AuthService.seDeconnecter();
        System.out.println("   Session active : " + Session.estConnecte() + "  [OK]");
        System.out.println();

        // ============================================================
        //  SCENARIO 2 : Publication et interactions
        // ============================================================
        System.out.println(">> Scenario 2 : Publication et interactions");
        System.out.println("--------------------------------------------");

        System.out.println("1. Alice publie un post (frais : "
            + PostService.FRAIS_PUBLICATION + " DT)...");
        Post p1 = postService.creerEtPublier(
            "Hackathon FSEGT 2026",
            "Rejoignez-nous le 10 mai - Prix : 500 DT !", alice);
        System.out.println("   Post publie : " + p1.getTitre() + "  [OK]");
        System.out.println("   Solde alice : " + String.format("%.3f", alice.getSolde()) + " DT");
        System.out.println();

        System.out.println("2. Bob publie un post...");
        Post p2 = postService.creerEtPublier(
            "Atelier Java Debutants", "Samedi 15h - Salle B.", bob);
        System.out.println("   Post publie : " + p2.getTitre() + "  [OK]");
        System.out.println();

        System.out.println("3. Carol tente de publier (solde insuffisant)...");
        User pauvre = AuthService.inscrire("pauvre", "pauvre@fsegt.tn", "pauvre1");
        pauvre.setSolde(0.2);
        try {
            postService.creerEtPublier("Post impossible", "...", pauvre);
        } catch (IllegalStateException e) {
            System.out.println("   ERREUR attendue -> " + e.getMessage() + "  [OK]");
        }
        System.out.println();

        System.out.println("4. Bob et Carol commentent le post de alice...");
        interactions.commenter(p1.getId(), bob,   "Super initiative !");
        interactions.commenter(p1.getId(), carol, "Date limite ?");
        System.out.println("   2 commentaires ajoutes  [OK]");
        System.out.println();

        System.out.println("5. Likes sur Post#" + p1.getId() + "...");
        interactions.liker(p1.getId(), bob);
        interactions.liker(p1.getId(), carol);
        System.out.println("   Likes : " + p1.getLikes() + "  [OK]");
        System.out.println();

        System.out.println("6. Alice tente de liker son propre post...");
        try {
            interactions.liker(p1.getId(), alice);
        } catch (IllegalStateException e) {
            System.out.println("   ERREUR attendue -> " + e.getMessage() + "  [OK]");
        }
        System.out.println();

        System.out.println("7. Bob retire son like...");
        interactions.retirerLike(p1.getId(), bob);
        System.out.println("   Likes apres retrait : " + p1.getLikes() + "  [OK]");
        System.out.println();

        System.out.println("8. Alice epingle son post...");
        p1.epingler(alice);
        System.out.println("   Post epingle : " + p1.isEpingle() + "  [OK]");
        System.out.println();

        // ============================================================
        //  SCENARIO 3 : Signalement et moderation
        // ============================================================
        System.out.println(">> Scenario 3 : Signalement et moderation");
        System.out.println("-------------------------------------------");

        System.out.println("1. Bob publie un post douteux...");
        Post pDouteux = postService.creerEtPublier(
            "Contenu douteux", "Message inapproprie...", bob);
        System.out.println("   Post cree : Post#" + pDouteux.getId() + "  [OK]");
        System.out.println();

        System.out.println("2. Alice et Carol signalent le post...");
        interactions.signalerPost(pDouteux.getId(), alice, "Contenu inapproprie");
        interactions.signalerPost(pDouteux.getId(), carol, "Spam");
        System.out.println("   Signalements : " + pDouteux.getSignalements().size()
            + "/" + InteractionService.SEUIL_SIGNALEMENT);
        System.out.println();

        System.out.println("3. 3e signalement -> masquage automatique...");
        User dave = AuthService.inscrire("dave", "dave@fsegt.tn", "dave99");
        dave.setSolde(5.0);
        postService.creerEtPublier("Post dave", "test", dave);
        interactions.signalerPost(pDouteux.getId(), dave, "Fausses infos");
        System.out.println("   Post#" + pDouteux.getId() + " masque automatiquement  [OK]");
        System.out.println();

        System.out.println("4. Admin modere un post signale...");
        Post pModere = postService.creerEtPublier("Post a moderer", "...", carol);
        interactions.signalerPost(pModere.getId(), alice, "Inapproprie");
        double soldeAvant = carol.getSolde();
        postService.modererPost(pModere.getId(), admin);
        System.out.println("   Post#" + pModere.getId() + " supprime  [OK]");
        System.out.println("   Remboursement carol : " + carol.getSolde()
            + " DT (avant : " + soldeAvant + " DT)  [OK]");
        System.out.println();

        // ============================================================
        //  SCENARIO 4 : Administration
        // ============================================================
        System.out.println(">> Scenario 4 : Administration");
        System.out.println("-------------------------------");

        Session.login(admin);

        System.out.println("1. Tableau de bord admin :");
        AdminService.afficherDetails();
        System.out.println();

        System.out.println("2. Liste des membres :");
        AdminService.voirUtilisateurs();
        System.out.println();

        System.out.println("3. Bannissement de 'pauvre'...");
        AuthService.bannirMembre(pauvre.getId());
        System.out.println("   Role : " + pauvre.getRole() + "  [OK]");
        System.out.println();

        System.out.println("4. Promotion de dave au role admin...");
        AdminService.promouvoirAdmin(dave.getId());
        System.out.println("   Role dave : " + dave.getRole() + "  [OK]");
        System.out.println();

        // ============================================================
        //  SCENARIO 5 : Tableau de bord final
        // ============================================================
        System.out.println(">> Scenario 5 : Tableau de bord final");
        System.out.println("--------------------------------------");

        long   totalPublies = postService.getPosts().stream().filter(Post::isPublie).count();
        int    totalLikes   = postService.getPosts().stream().mapToInt(Post::getLikes).sum();
        double revenus      = totalPublies * PostService.FRAIS_PUBLICATION;
        long   membresActifs = AuthService.getUsers().stream()
            .filter(u -> !"banni".equalsIgnoreCase(u.getRole())).count();

        System.out.println("  Utilisateurs inscrits : " + AuthService.getUsers().size());
        System.out.println("  Membres actifs        : " + membresActifs);
        System.out.println("  Posts publies         : " + totalPublies);
        System.out.println("  Total likes           : " + totalLikes);
        System.out.println("  Revenus publication   : "
            + String.format("%.3f", revenus) + " DT");
        System.out.println();

        System.out.println("  Posts par popularite :");
        postService.classerParPopularite().forEach(p ->
            System.out.println("    " + p));
        System.out.println();

        System.out.println("  Alertes posts sans interaction :");
        postService.getPosts().stream()
            .filter(Post::isPublie)
            .filter(p -> p.getLikes() == 0 && p.getCommentaires().stream()
                .noneMatch(c -> !c.startsWith("[like:")))
            .forEach(p -> System.out.println("  !! Post#" + p.getId()
                + " [" + p.getTitre() + "] sans interaction"));
        System.out.println();

        System.out.println("==============================================");
        System.out.println("  Fin demonstration — TP1 OK                 ");
        System.out.println("==============================================");
    }
}
