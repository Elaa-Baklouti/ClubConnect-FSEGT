import java.util.ArrayList;
import java.util.List;

public class PostService {
    static List<Post> posts = new ArrayList<>();
    static int nextId = 1;

    // CF-4 : Créer post
    static Post creerPost(String title, String content) {
        if (!Session.isLoggedIn())
            throw new IllegalStateException("Connexion requise.");
        Post post = new Post(nextId++, title, content, Session.currentUser);
        posts.add(post);
        System.out.println("Post créé : " + title);
        return post;
    }

    // CF-5 : Voir tous les posts
    static void voirPosts() {
        if (posts.isEmpty())
            System.out.println("Aucun post.");
        else
            posts.forEach(System.out::println);
    }

    // CF-15 : Supprimer post
    static void supprimerPost(int id) {
        if (!Session.isLoggedIn())
            throw new IllegalStateException("Connexion requise.");
        posts.removeIf(p -> {
            if (p.id == id) {
                if (p.author.id != Session.currentUser.id && !Session.isAdmin())
                    throw new IllegalStateException("Permission refusée.");
                System.out.println("Post #" + id + " supprimé.");
                return true;
            }
            return false;
        });
    }
}
