public class Main {
    public static void main(String[] args) {

        System.out.println("=== ClubConnect FSEGT — Demo Module Post (TP3) ===\n");

        // --- Donnees de test ---
        User alice = new User(1, "alice", "alice@fsegt.tn", "pass123", 10.0);
        User bob   = new User(2, "bob",   "bob@fsegt.tn",   "pass456", 5.0);
        User carol = new User(3, "carol", "carol@fsegt.tn", "pass789", 0.2);
        User admin = new User(4, "admin", "admin@fsegt.tn", "admin00", 0.0);
        admin.setRole("admin");
        User banni = new User(5, "spam",  "spam@fsegt.tn",  "xxx",    0.0);
        banni.setRole("banni");

        PostService        postService   = new PostService();
        InteractionService interactions  = new InteractionService(postService);

        // ------------------------------------------------
        // CF-4b : Creer et publier (avec frais 0.500 DT)
        // ------------------------------------------------
        System.out.println("--- CF-4 : Creation et publication (frais 0.500 DT) ---");
        Post p1 = postService.creerEtPublier("Bienvenue au club Java",
                "Premier post du club FSEGT !", alice);
        Post p2 = postService.creerEtPublier("Hackathon 2026",
                "Inscrivez-vous avant le 01/05 — Prix : 500 DT", bob);
        System.out.println("Solde alice apres publication : " + alice.getSolde() + " DT");
        System.out.println("Solde bob   apres publication : " + bob.getSolde()   + " DT");

        // Solde insuffisant
        try {
            postService.creerEtPublier("Post impossible", "...", carol);
        } catch (IllegalStateException e) {
            System.out.println("Erreur attendue : " + e.getMessage());
        }

        // Utilisateur banni
        try {
            postService.creerEtPublier("Spam", "...", banni);
        } catch (IllegalStateException e) {
            System.out.println("Erreur attendue : " + e.getMessage());
        }
        System.out.println();

        // ------------------------------------------------
        // CF-5 : Voir les posts (epingles en premier)
        // ------------------------------------------------
        System.out.println("--- CF-5 : Liste des posts ---");
        p1.epingler(alice);
        postService.voirPosts();
        System.out.println();

        // ------------------------------------------------
        // CF-10 : Commenter
        // ------------------------------------------------
        System.out.println("--- CF-10 : Commentaires ---");
        interactions.commenter(p1.getId(), bob,   "Super initiative !");
        interactions.commenter(p1.getId(), carol, "Hâte de participer.");
        // Commentaire trop long
        try {
            interactions.commenter(p1.getId(), bob, "x".repeat(300));
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur attendue : " + e.getMessage());
        }
        // Utilisateur banni
        try {
            interactions.commenter(p1.getId(), banni, "spam");
        } catch (IllegalStateException e) {
            System.out.println("Erreur attendue : " + e.getMessage());
        }
        System.out.println();

        // ------------------------------------------------
        // CF-11 : Likes + retrait
        // ------------------------------------------------
        System.out.println("--- CF-11 : Likes ---");
        interactions.liker(p1.getId(), bob);
        interactions.liker(p1.getId(), carol);
        interactions.liker(p2.getId(), alice);
        // Double like
        try {
            interactions.liker(p1.getId(), bob);
        } catch (IllegalStateException e) {
            System.out.println("Erreur attendue : " + e.getMessage());
        }
        // Auteur like son propre post
        try {
            interactions.liker(p1.getId(), alice);
        } catch (IllegalStateException e) {
            System.out.println("Erreur attendue : " + e.getMessage());
        }
        // Retrait like
        interactions.retirerLike(p1.getId(), carol);
        System.out.println("Likes Post#1 apres retrait carol : " + p1.getLikes());
        System.out.println();

        // ------------------------------------------------
        // Classement par popularite
        // ------------------------------------------------
        System.out.println("--- Classement par popularite ---");
        postService.classerParPopularite().forEach(System.out::println);
        System.out.println();

        // ------------------------------------------------
        // Details complets
        // ------------------------------------------------
        System.out.println("--- Details Post#" + p1.getId() + " ---");
        interactions.afficherDetailsPost(p1.getId());
        System.out.println();

        // ------------------------------------------------
        // Signalement + masquage automatique
        // ------------------------------------------------
        System.out.println("--- Signalement (seuil=" + InteractionService.SEUIL_SIGNALEMENT + ") ---");
        Post p3 = postService.creerEtPublier("Contenu douteux", "...", bob);
        interactions.signalerPost(p3.getId(), alice, "Contenu inapproprie");
        interactions.signalerPost(p3.getId(), carol, "Spam");
        System.out.println("Signalements : " + p3.getSignalements().size());
        // 3e signalement → masquage automatique
        User dave = new User(6, "dave", "dave@fsegt.tn", "pass", 5.0);
        postService.creerEtPublier("Post dave", "test", dave); // pour avoir un user avec solde
        interactions.signalerPost(p3.getId(), dave, "Fausses informations");
        System.out.println("Post#" + p3.getId() + " masque automatiquement (3 signalements).");
        System.out.println();

        // ------------------------------------------------
        // Moderation admin
        // ------------------------------------------------
        System.out.println("--- Moderation admin ---");
        Post p4 = postService.creerEtPublier("Post a moderer", "contenu", alice);
        interactions.signalerPost(p4.getId(), bob,  "Inapproprie");
        double soldeAvant = alice.getSolde();
        postService.modererPost(p4.getId(), admin);
        System.out.println("Post#" + p4.getId() + " modere par admin.");
        System.out.println("Remboursement alice : " + alice.getSolde()
                + " DT (avant=" + soldeAvant + " DT)");
        System.out.println();

        // ------------------------------------------------
        // CF-15 : Suppression
        // ------------------------------------------------
        System.out.println("--- CF-15 : Suppression ---");
        try {
            postService.supprimerPost(p2.getId(), alice); // pas auteur ni admin
        } catch (IllegalStateException e) {
            System.out.println("Erreur attendue : " + e.getMessage());
        }
        double soldeBobAvant = bob.getSolde();
        postService.supprimerPost(p2.getId(), bob); // auteur supprime
        System.out.println("Remboursement bob : " + bob.getSolde()
                + " DT (avant=" + soldeBobAvant + " DT)");
        System.out.println();

        // ------------------------------------------------
        // Posts restants
        // ------------------------------------------------
        System.out.println("--- Posts restants ---");
        postService.voirPosts();

        System.out.println("\n=== Fin demonstration ===");
    }
}
