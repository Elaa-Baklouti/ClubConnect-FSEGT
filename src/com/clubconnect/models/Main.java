package com.clubconnect.models;

import com.clubconnect.models.authentification.AuthService;
import com.clubconnect.models.authentification.Session;
import com.clubconnect.models.authentification.User;
import com.clubconnect.models.admin.AdminService;
import com.clubconnect.models.evenement.Event;
import com.clubconnect.models.evenement.EventService;
import com.clubconnect.models.gestionpostes.Post;
import com.clubconnect.models.gestionpostes.PostService;
import com.clubconnect.models.interaction.InteractionService;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.out.println("=== ClubConnect FSEGT — Demonstration Finale ===\n");

        // --- Donnees de base ---
        User alice = new User(1, "alice", "alice@fsegt.tn", "pass123", 150.0);
        User bob   = new User(2, "bob",   "bob@fsegt.tn",   "pass456", 80.0);
        User carol = new User(3, "carol", "carol@fsegt.tn", "pass789", 10.0);
        User admin = new User(4, "admin", "admin@fsegt.tn", "admin00", 0.0);
        admin.setRole("admin");

        AuthService.users.add(alice);
        AuthService.users.add(bob);
        AuthService.users.add(carol);
        AuthService.users.add(admin);

        EventService        eventService = new EventService();
        PostService         postService  = new PostService();
        InteractionService  interactions = new InteractionService(postService);
        List<User>          tousLesUsers = new ArrayList<>();
        tousLesUsers.add(alice); tousLesUsers.add(bob);
        tousLesUsers.add(carol); tousLesUsers.add(admin);

        // ============================================================
        // Scenario 1 : Inscription a un evenement payant
        // ============================================================
        System.out.println("Scenario 1 : Inscription a un evenement payant");
        System.out.println("-----------------------------------------------");
        Event hackathon = eventService.creerEvenement(
                "Hackathon FSEGT", "Salle A", "2026-05-10", alice, 3, 25.0);
        System.out.println("   Evenement cree : " + hackathon.getTitre()
                + " | Frais : " + hackathon.getFraisInscription() + " DT");
        eventService.participer(hackathon.getId(), bob);
        System.out.println("   Solde bob apres inscription : " + bob.getSolde() + " DT");
        try {
            eventService.participer(hackathon.getId(), carol);
        } catch (IllegalStateException e) {
            System.out.println("   Erreur attendue : " + e.getMessage());
        }
        System.out.println();

        // ============================================================
        // Scenario 2 : Annulation et remboursement
        // ============================================================
        System.out.println("Scenario 2 : Annulation et remboursement");
        System.out.println("-----------------------------------------");
        eventService.annulerParticipation(hackathon.getId(), bob);
        System.out.println("   Solde bob apres remboursement : " + bob.getSolde() + " DT");
        System.out.println();

        // ============================================================
        // Scenario 3 : Visiteur consulte sans connexion
        // ============================================================
        System.out.println("Scenario 3 : Visiteur (sans connexion)");
        System.out.println("---------------------------------------");
        Post p1 = postService.creerEtPublier("Hackathon FSEGT 2026",
                "Rejoignez-nous le 10 mai !", alice);
        Post p2 = postService.creerEtPublier("Atelier Java",
                "Samedi 15h - Salle B", bob);
        postService.voirPostsPublics();
        System.out.println();
        interactions.voirCommentairesPublics(p1.getId());
        try {
            interactions.liker(p1.getId(), null);
        } catch (IllegalStateException e) {
            System.out.println("   Erreur attendue : " + e.getMessage());
        }
        System.out.println();

        // ============================================================
        // Scenario 4 : Interactions membres sur les posts
        // ============================================================
        System.out.println("Scenario 4 : Interactions membres");
        System.out.println("----------------------------------");
        interactions.commenter(p1.getId(), bob,   "Super initiative !");
        interactions.commenter(p1.getId(), carol, "Quelle est la date limite ?");
        interactions.liker(p1.getId(), bob);
        interactions.liker(p1.getId(), carol);
        p1.epingler(alice);
        System.out.println("   Likes Post#1 : " + p1.getLikes());
        System.out.println("   Epingle : " + p1.isEpingle());
        System.out.println();

        // ============================================================
        // Scenario 5 : Signalement et moderation
        // ============================================================
        System.out.println("Scenario 5 : Signalement et moderation");
        System.out.println("---------------------------------------");
        Post pDouteux = postService.creerEtPublier("Contenu douteux", "...", bob);
        interactions.signalerPost(pDouteux.getId(), alice, "Inapproprie");
        interactions.signalerPost(pDouteux.getId(), carol, "Spam");
        User dave = new User(5, "dave", "dave@fsegt.tn", "p", 5.0);
        postService.creerEtPublier("Post dave", "test", dave);
        interactions.signalerPost(pDouteux.getId(), dave, "Fausses informations");
        System.out.println("   Post masque automatiquement (3 signalements)");
        Post pModere = postService.creerEtPublier("Post a moderer", "...", carol);
        interactions.signalerPost(pModere.getId(), alice, "Inapproprie");
        double soldeAvant = carol.getSolde();
        postService.modererPost(pModere.getId(), admin);
        System.out.println("   Remboursement carol : " + carol.getSolde()
                + " DT (avant : " + soldeAvant + " DT)");
        System.out.println();

        // ============================================================
        // Scenario 6 : Tableau de bord admin
        // ============================================================
        System.out.println("Scenario 6 : Tableau de bord admin");
        System.out.println("-----------------------------------");
        Session.login(admin);
        AdminService.voirUtilisateurs();
        System.out.println();
        AdminService.afficherDetails();
        System.out.println();

        long totalPublies  = postService.getPosts().stream().filter(Post::isPublie).count();
        int  totalLikes    = postService.getPosts().stream().mapToInt(Post::getLikes).sum();
        double revenus     = totalPublies * PostService.FRAIS_PUBLICATION;
        System.out.println("- Posts publies   : " + totalPublies);
        System.out.println("- Total likes     : " + totalLikes);
        System.out.println("- Revenus posts   : " + String.format("%.3f", revenus) + " DT");
        System.out.println();

        postService.getPosts().stream()
                .filter(Post::isPublie)
                .filter(p -> p.getLikes() == 0
                        && p.getCommentaires().stream().noneMatch(c -> !c.startsWith("[like:")))
                .forEach(p -> System.out.println("  ALERTE : Post#" + p.getId()
                        + " [" + p.getTitre() + "] sans interaction."));

        System.out.println("\n=== Fin demonstration ===");
    }
}
