package com.clubconnect.models;

import java.util.List;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *
 *  ÉTAPE 1 — Squelette AGL (déterministe) :
 *    Service d'administration — opérations réservées aux
 *    utilisateurs avec le rôle "admin".
 *    Package : com.clubconnect.models
 *    Pattern : constructeur vide → méthodes métier (signatures)
 *    Nommage français : voirUtilisateurs(), supprimerMembre(),
 *    supprimerPost(), afficherDetails()
 *
 *  ÉTAPE 2 — Implémentation IA assistée :
 *    Prompt utilisé :
 *      "Crée un AdminService Java qui vérifie que l'utilisateur
 *       courant (via Session) est admin avant chaque opération.
 *       Méthodes : voirUtilisateurs() qui liste tous les membres
 *       depuis AuthService, et supprimerPost(id) qui délègue à
 *       PostService. Lancer IllegalStateException si non admin."
 *
 *    Code généré par l'IA :
 *      static void voirUtilisateurs() {
 *          if (!Session.isAdmin()) throw new IllegalStateException("Admin requis.");
 *          for (User u : AuthService.users) System.out.println(u);
 *      }
 *
 *    Corrections humaines :
 *      - Utilisation de Session.estAdmin() (nommage français)
 *      - Accès à AuthService via getUsers() (encapsulation)
 *      - Ajout de supprimerMembre() avec vérification anti-auto-suppression
 *      - Ajout de afficherDetails() pour résumé admin
 * ============================================================
 */
public class AdminService {

    // ============================================================
    //  ÉTAPE 1 — Constructeur vide (squelette AGL)
    // ============================================================

    public AdminService() { }

    // ============================================================
    //  ÉTAPE 2 — Méthodes métier (implémentation)
    // ============================================================

    /**
     * CF-14 : Lister tous les membres inscrits.
     * Réservé aux administrateurs.
     */
    public static void voirUtilisateurs() {
        verifierAdmin();
        List<User> membres = AuthService.getUsers();
        System.out.println("=== Liste des membres (" + membres.size() + ") ===");
        if (membres.isEmpty()) {
            System.out.println("  Aucun membre inscrit.");
        } else {
            for (User u : membres) {
                System.out.println("  - " + u.getUsername()
                    + " | " + u.getEmail()
                    + " | rôle=" + u.getRole()
                    + " | solde=" + u.getSolde() + " DT");
            }
        }
    }

    /**
     * CF-15 : Supprimer un post par son ID.
     * Réservé aux administrateurs. Délègue à PostService.
     *
     * @param postId      ID du post à supprimer
     * @param postService Instance du PostService
     */
    public static void supprimerPost(int postId, PostService postService) {
        verifierAdmin();
        if (postService == null)
            throw new IllegalArgumentException("PostService invalide.");
        User admin = Session.obtenirUtilisateurCourant();
        postService.supprimerPost(postId, admin);
        System.out.println("Post#" + postId + " supprimé par l'admin.");
    }

    /**
     * Supprimer un membre par son ID.
     * Un admin ne peut pas se supprimer lui-même.
     *
     * @param membreId  ID du membre à supprimer
     */
    public static void supprimerMembre(int membreId) {
        verifierAdmin();
        User admin = Session.obtenirUtilisateurCourant();
        if (admin != null && admin.getId() == membreId)
            throw new IllegalStateException("Un admin ne peut pas supprimer son propre compte.");

        List<User> membres = AuthService.getUsers();
        User cible = null;
        for (User u : membres) {
            if (u.getId() == membreId) {
                cible = u;
                break;
            }
        }
        if (cible == null)
            throw new IllegalArgumentException("Membre#" + membreId + " introuvable.");

        membres.remove(cible);
        System.out.println("Membre '" + cible.getUsername() + "' supprimé par l'admin.");
    }

    /**
     * Afficher le tableau de bord administrateur.
     */
    public static void afficherDetails() {
        verifierAdmin();
        User admin = Session.obtenirUtilisateurCourant();
        System.out.println("=== Tableau de bord Admin ===");
        System.out.println("Connecté en tant que : " + (admin != null ? admin.getUsername() : "N/A"));
        System.out.println("Membres inscrits      : " + AuthService.getUsers().size());
    }

    // ============================================================
    //  toString
    // ============================================================

    @Override
    public String toString() {
        return "AdminService | session=" + (Session.estAdmin() ? "admin actif" : "non admin");
    }

    // ============================================================
    //  Utilitaire privé
    // ============================================================

    /**
     * Vérifie que la session courante est celle d'un admin.
     * Lance IllegalStateException sinon.
     */
    private static void verifierAdmin() {
        if (!Session.estAdmin())
            throw new IllegalStateException("Droits administrateur requis.");
    }
}
