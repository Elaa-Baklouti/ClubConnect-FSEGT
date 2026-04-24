package com.clubconnect.models;

import com.clubconnect.models.authentification.AuthService;
import com.clubconnect.models.authentification.Session;
import com.clubconnect.models.authentification.User;
import com.clubconnect.models.admin.AdminService;
import com.clubconnect.models.evenement.Event;
import com.clubconnect.models.evenement.EventService;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.out.println("=== ClubConnect FSEGT — Demonstration Finale ===\n");

        EventService eventService = new EventService();

        User alice = new User(1, "alice", "alice@fsegt.tn", "pass123", 150.0);
        User bob   = new User(2, "bob",   "bob@fsegt.tn",   "pass456", 80.0);
        User carol = new User(3, "carol", "carol@fsegt.tn", "pass789", 10.0);
        User admin = new User(4, "admin", "admin@fsegt.tn", "admin00", 0.0);
        admin.setRole("admin");

        AuthService.users.add(alice);
        AuthService.users.add(bob);
        AuthService.users.add(carol);
        AuthService.users.add(admin);

        // --- Scenario 1 : Inscription evenement ---
        System.out.println("Scenario 1 : Inscription a un evenement payant");
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

        // --- Scenario 2 : Annulation ---
        System.out.println("Scenario 2 : Annulation et remboursement");
        eventService.annulerParticipation(hackathon.getId(), bob);
        System.out.println("   Solde bob apres remboursement : " + bob.getSolde() + " DT");
        System.out.println();

        // --- Scenario 3 : Admin ---
        System.out.println("Scenario 3 : Tableau de bord admin");
        Session.login(admin);
        AdminService.voirUtilisateurs();
        System.out.println();
        AdminService.afficherDetails();
        System.out.println();

        // --- Scenario 4 : Gestion membres ---
        System.out.println("Scenario 4 : Gestion des membres");
        AdminService.changerRole(bob.getId(), "admin");
        System.out.println("   Role bob : " + bob.getRole());
        AdminService.changerRole(bob.getId(), "user");
        System.out.println("   Role bob : " + bob.getRole());
        AdminService.supprimerMembre(carol.getId());
        System.out.println("   Membres restants : " + AuthService.getUsers().size());
        System.out.println();

        // --- Scenario 5 : Controle acces ---
        System.out.println("Scenario 5 : Controle d'acces");
        Session.logout();
        try {
            AdminService.voirUtilisateurs();
        } catch (IllegalStateException e) {
            System.out.println("   Erreur attendue : " + e.getMessage());
        }

        System.out.println("\n=== Fin de demonstration ===");
    }
}
