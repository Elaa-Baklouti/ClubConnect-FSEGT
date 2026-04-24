package com.clubconnect.models.interaction;

import com.clubconnect.models.authentification.Session;
import com.clubconnect.models.gestionpostes.Post;
import com.clubconnect.models.gestionpostes.PostService;

public class InteractionService {

    static void commenter(int postId, String content) {
        if (!Session.isLoggedIn())
            throw new IllegalStateException("Connexion requise.");
        for (Post p : PostService.posts) {
            if (p.id == postId) {
                p.comments.add(Session.currentUser.getUsername() + " : " + content);
                System.out.println("Commentaire ajoute sur Post#" + postId);
                return;
            }
        }
        throw new IllegalArgumentException("Post introuvable.");
    }

    static void liker(int postId) {
        if (!Session.isLoggedIn())
            throw new IllegalStateException("Connexion requise.");
        for (Post p : PostService.posts) {
            if (p.id == postId) {
                p.likes++;
                System.out.println(Session.currentUser.getUsername()
                    + " a like Post#" + postId + " (" + p.likes + " likes)");
                return;
            }
        }
        throw new IllegalArgumentException("Post introuvable.");
    }
}
