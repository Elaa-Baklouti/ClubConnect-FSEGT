package com.clubconnect.models.gestionpostes;

import com.clubconnect.models.authentification.Session;
import java.util.ArrayList;
import java.util.List;

public class PostService {
    static List<Post> posts = new ArrayList<>();
    static int nextId = 1;

    static Post creerPost(String title, String content) {
        if (!Session.isLoggedIn())
            throw new IllegalStateException("Connexion requise.");
        Post post = new Post(nextId++, title, content, Session.currentUser);
        posts.add(post);
        System.out.println("Post cree : " + title);
        return post;
    }

    static void voirPosts() {
        if (posts.isEmpty()) System.out.println("Aucun post.");
        else posts.forEach(System.out::println);
    }

    static void supprimerPost(int id) {
        if (!Session.isLoggedIn())
            throw new IllegalStateException("Connexion requise.");
        posts.removeIf(p -> {
            if (p.id == id) {
                if (p.author.getId() != Session.currentUser.getId() && !Session.isAdmin())
                    throw new IllegalStateException("Permission refusee.");
                System.out.println("Post #" + id + " supprime.");
                return true;
            }
            return false;
        });
    }
}
