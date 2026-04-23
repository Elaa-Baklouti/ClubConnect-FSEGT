package com.clubconnect.models;

import java.util.List;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *
 *  ETAPE 1 — Squelette AGL (deterministe) :
 *    Service d'administration — operations reservees au role admin.
 *    Package : com.clubconnect.models
 *    Pattern : constructeur vide -> methodes metier (signatures)
 *    Nommage francais : voirUtilisateurs(), supprimerMembre(),
 *    afficherDetails()
 *
 *  ETAPE 2 — Implementation IA assistee :
 *    Prompt utilise :
 *      "Cree un AdminService Java qui verifie que l'utilisateur
 *       courant (via Session) est admin avant chaque operation.
 *       Methodes : voirUtilisateurs() qui liste tous les membres
 *       depuis AuthService, et supprimerMembre(id) qui retire un
 *       membre de la liste. Lancer IllegalStateException si non admin."
 *
 *    Code genere par l'IA :
 *      static void voirUtilisateurs() {
 *          if (!Session.isAdmin()) throw new IllegalStateException("Admin requis.");
 *          for (User u : AuthService.getUsers()) System.out.println(u);
 *      }
 *
 *    Corrections humaines :
 *      - Utilisation de Session.estAdmin() (nommage francais)
 *      - Ajout de supprimerMembre() avec verification anti-auto-suppression
 *      - Ajout de afficherDetails() pour tableau de bord admin
 * ============================================================
 */
public class AdminService {

    // ============================================================
    //  ETAPE 1 — Constructeur vide (squelette AGL)
    // ============================================================

    public AdminService() { }

    // ============================================================
    //  ETAPE 2 — Methodes metier (implementation)
    // ============================================================

    /**
     * CF-14 : Lister tous les membres inscrits.
     * Reserve aux administrateurs.
     */
    public static void voirUtilisateurs() {
        verifierAdmin();
        List<User> membres = AuthService.getUsers();
        System.out.println("  Membres inscrits (" + membres.size() + ") :");
        for (User u : membres) {
            System.out.println("    - " + u.getUsername()
                + " | " + u.getEmail()
                + " | role=" + u.getRole()
                + " | solde=" + u.getSolde() + " DT");
        }
    }

    /**
     * Supprimer un membre par son ID.
     * Un admin ne peut pas supprimer son propre compte.
     *
     * @param membreId  ID du membre a supprimer
     */
    public static void supprimerMembre(int membreId) {
        verifierAdmin();
        User admin = Session.obtenirUtilisateurCourant();
        if (admin != null && admin.getId() == membreId)
            throw new IllegalStateException("Un admin ne peut pas supprimer son propre compte.");

        List<User> membres = AuthService.getUsers();
        User cible = null;
        for (User u : membres) {
            if (u.getId() == membreId) { cible = u; break; }
        }
        if (cible == null)
            throw new IllegalArgumentException("Membre#" + membreId + " introuvable.");

        membres.remove(cible);
        System.out.println("  Membre '" + cible.getUsername() + "' supprime par l'admin.");
    }

    /**
     * Afficher le tableau de bord administrateur.
     */
    public static void afficherDetails() {
        verifierAdmin();
        User admin = Session.obtenirUtilisateurCourant();
        System.out.println("  Admin connecte  : " + (admin != null ? admin.getUsername() : "N/A"));
        System.out.println("  Membres inscrits : " + AuthService.getUsers().size());
    }

    // ============================================================
    //  toString
    // ============================================================

    @Override
    public String toString() {
        return "AdminService | " + (Session.estAdmin() ? "admin actif" : "non admin");
    }

    // ============================================================
    //  Utilitaire prive
    // ============================================================

    /** Verifie que la session courante est celle d'un admin. */
    private static void verifierAdmin() {
        if (!Session.estAdmin())
            throw new IllegalStateException("Droits administrateur requis.");
    }
}
