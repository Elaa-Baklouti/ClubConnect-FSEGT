public class InteractionService {

    // CF-10 : Commenter
    static void commenter(int postId, String content) {
        if (!Session.isLoggedIn())
            throw new IllegalStateException("Connexion requise.");
        for (Post p : PostService.posts) {
            if (p.id == postId) {
                p.comments.add(Session.currentUser.getUsername() + " : " + content);
                System.out.println("Commentaire ajouté sur Post#" + postId);
                return;
            }
        }
        throw new IllegalArgumentException("Post introuvable.");
    }

    // CF-11 : Liker
    static void liker(int postId) {
        if (!Session.isLoggedIn())
            throw new IllegalStateException("Connexion requise.");
        for (Post p : PostService.posts) {
            if (p.id == postId) {
                p.likes++;
                System.out.println(Session.currentUser.getUsername()
                    + " a liké Post#" + postId + " (" + p.likes + " likes)");
                return;
            }
        }
        throw new IllegalArgumentException("Post introuvable.");
    }
}