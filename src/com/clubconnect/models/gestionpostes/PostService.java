package com.clubconnect.models.gestionpostes;

import com.clubconnect.models.authentification.User;
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
 *    "Cree creerEtPublier(titre, contenu, auteur) dans PostService.
 *     Creer, valider et publier en une operation. Exception si banni."
 *  Code genere par l'IA :
 *    public Post creerEtPublier(String titre, String contenu, User auteur) {
 *        Post post = new Post(nextId++, titre, contenu, auteur);
 *        post.publier();
 *        posts.add(post);
 *        return post;
 *    }
 *  Corrections humaines :
 *    - Verification role "banni"
 *    - Frais 0.500 DT debites du solde auteur
 *    - Rollback nextId si echec
 * ============================================================
 *  METHODE : modererPost()
 *  Prompt utilise :
 *    "Implemente modererPost(int postId, User moderateur). Admin
 *     uniquement. Post doit avoir au moins 1 signalement."
 *  Code genere par l'IA :
 *    public void modererPost(int postId, User moderateur) {
 *        if (!"admin".equals(moderateur.getRole())) throw ...;
 *        posts.removeIf(p -> p.getId() == postId);
 *    }
 *  Corrections humaines :
 *    - Verification post existe
 *    - Verification >= 1 signalement
 *    - Remboursement frais a l'auteur
 * ============================================================
 *  METHODE : classerParPopularite()
 *  Prompt utilise :
 *    "classerParPopularite() : posts publies tries par likes desc."
 *  Code genere par l'IA :
 *    return posts.stream()
 *        .sorted((a, b) -> b.getLikes() - a.getLikes())
 *        .collect(Collectors.toList());
 *  Corrections humaines :
 *    - Filtre posts publies uniquement
 *    - Comparator.comparingInt pour lisibilite
 * ============================================================
 */
public class PostService {

    public static final double FRAIS_PUBLICATION = 0.500;

    private List<Post> posts;
    private int nextId;

    public PostService() {
        this.posts  = new ArrayList<>();
        this.nextId = 1;
    }

    /** CF-4 : Creer un post en brouillon. */
    public Post creerPost(String titre, String contenu, User auteur) {
        if (auteur == null)
            throw new IllegalArgumentException("L'auteur est obligatoire.");
        if ("banni".equalsIgnoreCase(auteur.getRole()))
            throw new IllegalStateException(auteur.getUsername() + " est banni.");
        if (titre == null || titre.trim().isEmpty())
            throw new IllegalArgumentException("Le titre est obligatoire.");
        Post post = new Post(nextId++, titre, contenu, auteur);
        posts.add(post);
        return post;
    }

    /** CF-4b : Creer et publier — debite 0.500 DT du solde auteur. */
    public Post creerEtPublier(String titre, String contenu, User auteur) {
        if (auteur == null)
            throw new IllegalArgumentException("L'auteur est obligatoire.");
        if ("banni".equalsIgnoreCase(auteur.getRole()))
            throw new IllegalStateException(auteur.getUsername() + " est banni.");
        if (auteur.getSolde() < FRAIS_PUBLICATION)
            throw new IllegalStateException("Solde insuffisant pour publier. Frais : "
                    + FRAIS_PUBLICATION + " DT. Solde actuel : " + auteur.getSolde() + " DT.");
        int idReserve = nextId++;
        Post post = new Post(idReserve, titre, contenu, auteur);
        try {
            post.publier();
            auteur.debiterSolde(FRAIS_PUBLICATION);
        } catch (Exception e) {
            nextId = idReserve;
            throw e;
        }
        posts.add(post);
        return post;
    }

    /** VISITEUR : Voir les posts publics sans connexion. */
    public void voirPostsPublics() {
        List<Post> publies = posts.stream().filter(Post::isPublie).collect(Collectors.toList());
        if (publies.isEmpty()) {
            System.out.println("Aucun post disponible.");
        } else {
            System.out.println("=== Posts publics (" + publies.size() + ") ===");
            publies.stream().filter(Post::isEpingle).forEach(System.out::println);
            publies.stream().filter(p -> !p.isEpingle()).forEach(System.out::println);
        }
    }

    /** CF-5 : Afficher tous les posts publies (epingles en premier). */
    public void voirPosts() {
        List<Post> publies = posts.stream().filter(Post::isPublie).collect(Collectors.toList());
        if (publies.isEmpty()) {
            System.out.println("Aucun post publie.");
        } else {
            System.out.println("=== Posts publies (" + publies.size() + ") ===");
            publies.stream().filter(Post::isEpingle).forEach(System.out::println);
            publies.stream().filter(p -> !p.isEpingle()).forEach(System.out::println);
        }
    }

    /** CF-15 : Supprimer — auteur ou admin. Rembourse frais si publie. */
    public void supprimerPost(int id, User demandeur) {
        if (demandeur == null)
            throw new IllegalArgumentException("Demandeur invalide.");
        Post cible = trouverParId(id);
        if (cible == null)
            throw new IllegalArgumentException("Post#" + id + " introuvable.");
        boolean estAuteur = cible.getAuteur() != null
                         && cible.getAuteur().getId() == demandeur.getId();
        boolean estAdmin  = "admin".equalsIgnoreCase(demandeur.getRole());
        if (!estAuteur && !estAdmin)
            throw new IllegalStateException("Permission refusee : auteur ou admin requis.");
        if (cible.isPublie() && cible.getAuteur() != null)
            cible.getAuteur().crediterSolde(FRAIS_PUBLICATION);
        posts.remove(cible);
    }

    /** Moderation admin — post doit avoir >= 1 signalement. Rembourse auteur. */
    public void modererPost(int postId, User moderateur) {
        if (moderateur == null || !"admin".equalsIgnoreCase(moderateur.getRole()))
            throw new IllegalStateException("Seul un admin peut moderer un post.");
        Post cible = trouverParId(postId);
        if (cible == null)
            throw new IllegalArgumentException("Post#" + postId + " introuvable.");
        if (cible.getSignalements().isEmpty())
            throw new IllegalStateException("Post#" + postId + " n'a aucun signalement.");
        if (cible.isPublie() && cible.getAuteur() != null)
            cible.getAuteur().crediterSolde(FRAIS_PUBLICATION);
        posts.remove(cible);
    }

    /** Rechercher posts publies par mot-cle dans le titre. */
    public List<Post> rechercherParTitre(String motCle) {
        return posts.stream()
                .filter(Post::isPublie)
                .filter(p -> motCle == null || motCle.isBlank()
                        || p.getTitre().toLowerCase().contains(motCle.toLowerCase()))
                .collect(Collectors.toList());
    }

    /** Classer posts publies par likes decroissants. */
    public List<Post> classerParPopularite() {
        return posts.stream()
                .filter(Post::isPublie)
                .sorted(Comparator.comparingInt(Post::getLikes).reversed())
                .collect(Collectors.toList());
    }

    /** Trouver un post par ID. */
    public Post trouverParId(int id) {
        return posts.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    /** Afficher les details d'un post specifique. */
    public void afficherDetails(int id) {
        Post post = trouverParId(id);
        if (post == null)
            throw new IllegalArgumentException("Post#" + id + " introuvable.");
        post.afficherDetails();
    }

    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) { this.posts = posts; }
    public int getNextId() { return nextId; }
    public void setNextId(int nextId) { this.nextId = nextId; }
}
