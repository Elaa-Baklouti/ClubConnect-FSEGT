public class Main {
    public static void main(String[] args) {

        System.out.println("=== ClubConnect FSEGT — Démonstration Posts ===\n");

        // --- Données de test ---
        User alice = new User(1, "alice", "alice@fsegt.tn", "pass123");
        User bob   = new User(2, "bob",   "bob@fsegt.tn",   "pass456");
        User admin = new User(3, "admin", "admin@fsegt.tn", "admin00");
        admin.setRole("admin");

        PostService postService           = new PostService();
        InteractionService interactions   = new InteractionService(postService);

        // ------------------------------------------------
        // CF-4 : Créer des posts
        // ------------------------------------------------
        System.out.println("--- CF-4 : Création de posts ---");
        Post p1 = postService.creerPost("Bienvenue au club Java", "Premier post du club !", alice);
        Post p2 = postService.creerPost("Hackathon 2026", "Inscrivez-vous avant le 01/05", bob);
        System.out.println();

        // ------------------------------------------------
        // CF-5 : Voir tous les posts
        // ------------------------------------------------
        System.out.println("--- CF-5 : Liste des posts ---");
        postService.voirPosts();
        System.out.println();

        // ------------------------------------------------
        // CF-10 : Commenter
        // ------------------------------------------------
        System.out.println("--- CF-10 : Commentaires ---");
        interactions.commenter(p1.getId(), bob,   "Super initiative !");
        interactions.commenter(p1.getId(), admin, "Bienvenue à tous.");
        System.out.println();

        // ------------------------------------------------
        // CF-11 : Liker
        // ------------------------------------------------
        System.out.println("--- CF-11 : Likes ---");
        interactions.liker(p1.getId(), bob);
        interactions.liker(p2.getId(), alice);
        // Tentative de double like
        try {
            interactions.liker(p1.getId(), bob);
        } catch (IllegalStateException e) {
            System.out.println("Erreur attendue : " + e.getMessage());
        }
        System.out.println();

        // ------------------------------------------------
        // Afficher détails complets d'un post
        // ------------------------------------------------
        System.out.println("--- Détails Post#" + p1.getId() + " ---");
        interactions.afficherDetailsPost(p1.getId());
        System.out.println();

        // ------------------------------------------------
        // Recherche par titre
        // ------------------------------------------------
        System.out.println("--- Recherche 'hack' ---");
        postService.rechercherParTitre("hack").forEach(System.out::println);
        System.out.println();

        // ------------------------------------------------
        // CF-15 : Supprimer un post
        // ------------------------------------------------
        System.out.println("--- CF-15 : Suppression ---");
        // Tentative sans permission
        try {
            postService.supprimerPost(p2.getId(), alice);
        } catch (IllegalStateException e) {
            System.out.println("Erreur attendue : " + e.getMessage());
        }
        // Admin supprime
        postService.supprimerPost(p2.getId(), admin);
        System.out.println();

        System.out.println("--- Posts restants ---");
        postService.voirPosts();

        System.out.println("\n=== Fin démonstration ===");
    }
}
