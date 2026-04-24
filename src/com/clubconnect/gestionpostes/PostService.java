package com.clubconnect.gestionpostes;

import com.clubconnect.models.User;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 *  DOCUMENTATION TP3 — Approche IA assistee
 * ============================================================
 *  METHODE : creerEtPublier()
 *  Prompt utilise :
 *    "creerEtPublier(titre, contenu, auteur). Creer, valider et
 *     publier en une operation. Exception si banni."
 *  Code genere par l'IA :
 *    public Post creerEtPublier(String titre, String contenu, User auteur) {
 *        Post post = new Post(nextId++, titre, contenu, auteur);
 *        post.publier(); posts.add(post); return post;
 *    }
 *  Corrections humaines :
 *    - Verification role "banni"
 *    - Frais 0.500 DT debites du solde auteur
 *    - Rollback nextId si echec
 * ============================================================
 *  METHODE : modererPost()
 *  Prompt utilise :
 *    "modererPost(postId, moderateur). Admin uniquement.
 *     Post doit avoir au moins 1 signalement."
 *  Code genere par l'IA :
 *    public void modererPost(int postId, User moderateur) {
 *        if (!"admin".equals(moderateur.getRole())) throw ...;
 *        posts.removeIf(p -> p.getId() == postId);
 *    }
 *  Corrections humaines :
 *    - Verification post existe et >= 1 signalement
 *    - Remboursement frais a l'auteur
 * ============================================================
 */
public class PostService {

    public static final double FRAIS_PUBLICATION = 0.500;

    private List<Post> posts;
    private int nextId;

    public PostService() {
        this.posts = new ArrayList<>();
        this.nextId = 1;
    }

    public Post creerPost(String titre, String contenu, User auteur) {
        if (auteur == null) throw new IllegalArgumentException("Auteur obligatoire.");
        if ("banni".equalsIgnoreCase(auteur.getRole()))
            throw new IllegalStateException(auteur.getUsername() + " est banni.");
        if (titre == null || titre.trim().isEmpty()) throw new IllegalArgumentException("Titre obligatoire.");
        Post post = new Post(nextId++, titre, contenu, auteur);
        posts.add(post);
        return post;
    }

    public Post creerEtPublier(String titre, String contenu, User auteur) {
        if (auteur == null) throw new IllegalArgumentException("Auteur obligatoire.");
        if ("banni".equalsIgnoreCase(auteur.getRole()))
            throw new IllegalStateException(auteur.getUsername() + " est banni.");
        if (auteur.getSolde() < FRAIS_PUBLICATION)
            throw new IllegalStateException("Solde insuffisant. Frais : "
                    + FRAIS_PUBLICATION + " DT. Solde : " + auteur.getSolde() + " DT.");
        int idReserve = nextId++;
        Post post = new Post(idReserve, titre, contenu, auteur);
        try {
            post.publier();
            auteur.debiterSolde(FRAIS_PUBLICATION);
        } catch (Exception e) {
            nextId = idReserve; throw e;
        }
        posts.add(post);
        return post;
    }

    public void voirPostsPublics() {
        List<Post> publies = posts.stream().filter(Post::isPublie).collect(Collectors.toList());
        if (publies.isEmpty()) { System.out.println("Aucun post disponible."); return; }
        System.out.println("=== Posts publics (" + publies.size() + ") ===");
        publies.stream().filter(Post::isEpingle).forEach(System.out::println);
        publies.stream().filter(p -> !p.isEpingle()).forEach(System.out::println);
    }

    public void voirPosts() {
        List<Post> publies = posts.stream().filter(Post::isPublie).collect(Collectors.toList());
        if (publies.isEmpty()) { System.out.println("Aucun post publie."); return; }
        System.out.println("=== Posts publies (" + publies.size() + ") ===");
        publies.stream().filter(Post::isEpingle).forEach(System.out::println);
        publies.stream().filter(p -> !p.isEpingle()).forEach(System.out::println);
    }

    public void supprimerPost(int id, User demandeur) {
        if (demandeur == null) throw new IllegalArgumentException("Demandeur invalide.");
        Post cible = trouverParId(id);
        if (cible == null) throw new IllegalArgumentException("Post#" + id + " introuvable.");
        boolean estAuteur = cible.getAuteur() != null && cible.getAuteur().getId() == demandeur.getId();
        boolean estAdmin  = "admin".equalsIgnoreCase(demandeur.getRole());
        if (!estAuteur && !estAdmin) throw new IllegalStateException("Permission refusee.");
        if (cible.isPublie() && cible.getAuteur() != null)
            cible.getAuteur().crediterSolde(FRAIS_PUBLICATION);
        posts.remove(cible);
    }

    public void modererPost(int postId, User moderateur) {
        if (moderateur == null || !"admin".equalsIgnoreCase(moderateur.getRole()))
            throw new IllegalStateException("Seul un admin peut moderer.");
        Post cible = trouverParId(postId);
        if (cible == null) throw new IllegalArgumentException("Post#" + postId + " introuvable.");
        if (cible.getSignalements().isEmpty())
            throw new IllegalStateException("Post#" + postId + " n'a aucun signalement.");
        if (cible.isPublie() && cible.getAuteur() != null)
            cible.getAuteur().crediterSolde(FRAIS_PUBLICATION);
        posts.remove(cible);
    }

    public List<Post> rechercherParTitre(String motCle) {
        return posts.stream().filter(Post::isPublie)
                .filter(p -> motCle == null || motCle.isBlank()
                        || p.getTitre().toLowerCase().contains(motCle.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Post> classerParPopularite() {
        return posts.stream().filter(Post::isPublie)
                .sorted(Comparator.comparingInt(Post::getLikes).reversed())
                .collect(Collectors.toList());
    }

    public Post trouverParId(int id) {
        return posts.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) { this.posts = posts; }
    public int getNextId() { return nextId; }
    public void setNextId(int nextId) { this.nextId = nextId; }
}
