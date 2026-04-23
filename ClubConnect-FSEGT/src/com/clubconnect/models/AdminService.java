package com.clubconnect.models;

import java.util.List;

/**
 * ============================================================
 *  DOCUMENTATION TP3 — Approche IA assistee
 * ============================================================
 *
 *  METHODE : voirUtilisateurs()
 *  ----------------------------
 *  Prompt utilise :
 *    "Implemente voirUtilisateurs() dans AdminService Java. Verifier
 *     que l'utilisateur courant (via Session) a le role admin. Lister
 *     tous les membres depuis AuthService avec leur username, email,
 *     role et solde en DT. Afficher aussi un resume : nombre total,
 *     nombre de bannis, solde total en circulation."
 *
 *  Code genere par l'IA :
 *    public static void voirUtilisateurs() {
 *        if (!Session.isAdmin())
 *            throw new IllegalStateException("Admin requis.");
 *        for (User u : AuthService.getUsers())
 *            System.out.println(u.getUsername() + " | " + u.getEmail()
 *                + " | " + u.getRole() + " | " + u.getSolde() + " DT");
 *    }
 *
 *  Code final apres corrections humaines :
 *    - Utilisation de Session.estAdmin() (nommage francais)
 *    - Ajout du resume : total membres, bannis, solde total en DT
 *    - Interaction avec User : appel getSolde(), getRole(), getUsername()
 *    - Interaction avec AuthService : appel getUsers()
 *
 * ============================================================
 *
 *  METHODE : supprimerMembre()
 *  ---------------------------
 *  Prompt utilise :
 *    "Implemente supprimerMembre(int membreId) dans AdminService.
 *     Verifier les droits admin. Retrouver le membre par ID dans
 *     AuthService. Empecher un admin de se supprimer lui-meme.
 *     Supprimer le membre de la liste. Si le membre supprime est
 *     connecte, invalider sa session."
 *
 *  Code genere par l'IA :
 *    public static void supprimerMembre(int membreId) {
 *        if (!Session.isAdmin())
 *            throw new IllegalStateException("Admin requis.");
 *        User admin = Session.getCurrentUser();
 *        if (admin.getId() == membreId)
 *            throw new IllegalStateException("Auto-suppression interdite.");
 *        AuthService.getUsers().removeIf(u -> u.getId() == membreId);
 *    }
 *
 *  Code final apres corrections humaines :
 *    - Verification que le membre existe avant suppression (l'IA utilisait
 *      removeIf sans verifier si l'element existait)
 *    - Invalidation de session si le membre supprime est connecte
 *    - Interaction avec Session : obtenirUtilisateurCourant() + logout()
 *    - Retour du User supprime pour confirmation
 *
 * ============================================================
 *
 *  METHODE : promouvoirAdmin()
 *  ---------------------------
 *  Prompt utilise :
 *    "Implemente promouvoirAdmin(int membreId) dans AdminService.
 *     Verifier les droits admin. Retrouver le membre. Changer son
 *     role de 'user' a 'admin'. Empecher de promouvoir un membre
 *     deja admin ou banni."
 *
 *  Code genere par l'IA :
 *    public static void promouvoirAdmin(int membreId) {
 *        if (!Session.isAdmin())
 *            throw new IllegalStateException("Admin requis.");
 *        User u = trouverMembre(membreId);
 *        u.setRole("admin");
 *    }
 *
 *  Code final apres corrections humaines :
 *    - Verification que le membre n'est pas deja admin
 *    - Verification que le membre n'est pas banni (ne peut pas promouvoir un banni)
 *    - Interaction avec User : appel setRole("admin")
 *    - Interaction avec AuthService : recherche dans getUsers()
 *
 * ============================================================
 */
public class AdminService {

    // ============================================================
    //  Constructeur vide
    // ============================================================

    public AdminService() { }

    // ============================================================
    //  Methodes metier — logique reelle
    // ============================================================

    /**
     * CF-14 : Lister tous les membres avec resume statistique.
     * Interaction avec AuthService : getUsers()
     * Interaction avec User : getUsername(), getEmail(), getRole(), getSolde()
     */
    public static void voirUtilisateurs() {
        verifierAdmin();
        List<User> membres = AuthService.getUsers();

        int    nbBannis    = 0;
        double soldeTotal  = 0.0;

        System.out.println("  +-------------------------------------------------+");
        System.out.println("  | LISTE DES MEMBRES                               |");
        System.out.println("  +-------------------------------------------------+");
        for (User u : membres) {
            System.out.println("  | " + String.format("%-12s", u.getUsername())
                + " | " + String.format("%-22s", u.getEmail())
                + " | " + String.format("%-6s", u.getRole())
                + " | " + String.format("%8.3f", u.getSolde()) + " DT |");
            if ("banni".equalsIgnoreCase(u.getRole())) nbBannis++;
            soldeTotal += u.getSolde();
        }
        System.out.println("  +-------------------------------------------------+");
        System.out.println("  Total membres    : " + membres.size());
        System.out.println("  Membres bannis   : " + nbBannis);
        System.out.println("  Solde total      : " + String.format("%.3f", soldeTotal) + " DT");
    }

    /**
     * Supprimer un membre par son ID.
     * Invalide la session si le membre supprime est connecte.
     * Interaction avec Session : obtenirUtilisateurCourant() + logout()
     * Interaction avec AuthService : getUsers() -> remove()
     *
     * @param membreId ID du membre a supprimer
     * @return         Le User supprime
     */
    public static User supprimerMembre(int membreId) {
        verifierAdmin();
        User admin = Session.obtenirUtilisateurCourant();
        if (admin != null && admin.getId() == membreId)
            throw new IllegalStateException(
                "Un administrateur ne peut pas supprimer son propre compte.");

        User cible = trouverMembre(membreId);
        AuthService.getUsers().remove(cible);

        // Invalider la session si le membre supprime est connecte
        User courant = Session.obtenirUtilisateurCourant();
        if (courant != null && courant.getId() == membreId) {
            Session.logout();
        }

        return cible;
    }

    /**
     * Promouvoir un membre au role admin.
     * Interaction avec User : setRole("admin")
     * Interaction avec AuthService : recherche dans getUsers()
     *
     * @param membreId ID du membre a promouvoir
     */
    public static void promouvoirAdmin(int membreId) {
        verifierAdmin();
        User cible = trouverMembre(membreId);

        if ("admin".equalsIgnoreCase(cible.getRole()))
            throw new IllegalStateException(
                cible.getUsername() + " est deja administrateur.");
        if ("banni".equalsIgnoreCase(cible.getRole()))
            throw new IllegalStateException(
                "Impossible de promouvoir un membre banni : " + cible.getUsername());

        cible.setRole("admin");
    }

    /**
     * Afficher le tableau de bord administrateur.
     * Interaction avec Session : obtenirUtilisateurCourant()
     * Interaction avec AuthService : getUsers()
     */
    public static void afficherDetails() {
        verifierAdmin();
        User admin   = Session.obtenirUtilisateurCourant();
        List<User> m = AuthService.getUsers();

        long nbActifs = m.stream()
            .filter(u -> !"banni".equalsIgnoreCase(u.getRole())).count();
        long nbBannis = m.stream()
            .filter(u -> "banni".equalsIgnoreCase(u.getRole())).count();

        System.out.println("  === Tableau de bord Admin ===");
        System.out.println("  Admin connecte   : "
            + (admin != null ? admin.getUsername() : "N/A"));
        System.out.println("  Total membres    : " + m.size());
        System.out.println("  Membres actifs   : " + nbActifs);
        System.out.println("  Membres bannis   : " + nbBannis);
        Session.afficherDetails();
    }

    // ============================================================
    //  toString
    // ============================================================

    @Override
    public String toString() {
        return "AdminService | " + (Session.estAdmin() ? "admin actif" : "non admin");
    }

    // ============================================================
    //  Utilitaires prives
    // ============================================================

    /** Verifie que la session courante est celle d'un admin. */
    private static void verifierAdmin() {
        if (!Session.estAdmin())
            throw new IllegalStateException(
                "Droits administrateur requis.");
    }

    /**
     * Retrouver un membre par ID dans AuthService.
     * Lance IllegalArgumentException si introuvable.
     */
    private static User trouverMembre(int membreId) {
        for (User u : AuthService.getUsers()) {
            if (u.getId() == membreId) return u;
        }
        throw new IllegalArgumentException(
            "Membre#" + membreId + " introuvable.");
    }
}
