package com.clubconnect.models;

import java.util.List;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *
 *  ETAPE 1 — Squelette AGL :
 *    Operations reservees au role admin.
 *    Nommage francais : voirUtilisateurs(), supprimerMembre(),
 *    promouvoirAdmin(), afficherDetails()
 *
 *  ETAPE 2 — Implementation IA assistee :
 *    Prompt utilise :
 *      "AdminService Java : verifier Session.isAdmin() avant
 *       chaque operation. voirUtilisateurs() liste les membres
 *       avec stats (total, bannis, solde total DT)."
 *
 *    Code genere par l'IA :
 *      static void voirUtilisateurs() {
 *          if (!Session.isAdmin()) throw ...;
 *          for (User u : AuthService.getUsers()) System.out.println(u);
 *      }
 *
 *    Corrections humaines :
 *      - Session.estAdmin() (nommage francais)
 *      - Ajout stats : total, bannis, solde total
 *      - supprimerMembre() invalide session si membre connecte
 *      - promouvoirAdmin() verifie non-banni avant promotion
 * ============================================================
 */
public class AdminService {

    public AdminService() { }

    public static void voirUtilisateurs() {
        verifierAdmin();
        List<User> m = AuthService.getUsers();
        int nbBannis = 0; double soldeTotal = 0;
        System.out.println("  +--------------------------------------------------+");
        System.out.println("  | MEMBRES                                          |");
        System.out.println("  +--------------------------------------------------+");
        for (User u : m) {
            System.out.printf("  | %-12s | %-20s | %-6s | %8.3f DT |%n",
                u.getUsername(), u.getEmail(), u.getRole(), u.getSolde());
            if ("banni".equalsIgnoreCase(u.getRole())) nbBannis++;
            soldeTotal += u.getSolde();
        }
        System.out.println("  +--------------------------------------------------+");
        System.out.println("  Total: " + m.size() + " | Bannis: " + nbBannis
            + " | Solde total: " + String.format("%.3f", soldeTotal) + " DT");
    }

    public static User supprimerMembre(int membreId) {
        verifierAdmin();
        User admin = Session.obtenirUtilisateurCourant();
        if (admin != null && admin.getId() == membreId)
            throw new IllegalStateException("Auto-suppression interdite.");
        User cible = trouverMembre(membreId);
        AuthService.getUsers().remove(cible);
        User courant = Session.obtenirUtilisateurCourant();
        if (courant != null && courant.getId() == membreId) Session.logout();
        return cible;
    }

    public static void promouvoirAdmin(int membreId) {
        verifierAdmin();
        User cible = trouverMembre(membreId);
        if ("admin".equalsIgnoreCase(cible.getRole()))
            throw new IllegalStateException(cible.getUsername() + " est deja admin.");
        if ("banni".equalsIgnoreCase(cible.getRole()))
            throw new IllegalStateException("Impossible de promouvoir un membre banni.");
        cible.setRole("admin");
    }

    public static void afficherDetails() {
        verifierAdmin();
        User admin = Session.obtenirUtilisateurCourant();
        List<User> m = AuthService.getUsers();
        long actifs = m.stream().filter(u -> !"banni".equalsIgnoreCase(u.getRole())).count();
        long bannis = m.stream().filter(u -> "banni".equalsIgnoreCase(u.getRole())).count();
        System.out.println("  Admin    : " + (admin != null ? admin.getUsername() : "N/A"));
        System.out.println("  Total    : " + m.size() + " | Actifs: " + actifs + " | Bannis: " + bannis);
    }

    @Override public String toString() {
        return "AdminService|" + (Session.estAdmin() ? "admin" : "non-admin");
    }

    private static void verifierAdmin() {
        if (!Session.estAdmin())
            throw new IllegalStateException("Droits administrateur requis.");
    }

    private static User trouverMembre(int id) {
        for (User u : AuthService.getUsers())
            if (u.getId() == id) return u;
        throw new IllegalArgumentException("Membre#" + id + " introuvable.");
    }
}
