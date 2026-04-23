package com.clubconnect.models;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *
 *  ÉTAPE 1 — Squelette AGL (déterministe) :
 *    Classe utilitaire de gestion de la session courante.
 *    Package : com.clubconnect.models
 *    Pattern : champ statique privé → méthodes statiques métier
 *    Nommage français : ouvrirSession(), fermerSession(),
 *    estConnecte(), estAdmin(), obtenirUtilisateurCourant()
 *
 *  ÉTAPE 2 — Implémentation IA assistée :
 *    Prompt utilisé :
 *      "Crée une classe Session Java avec un champ statique
 *       currentUser de type User. Ajoute les méthodes statiques
 *       login(User), logout(), isLoggedIn() et isAdmin().
 *       isAdmin() retourne true si le rôle de l'utilisateur
 *       courant est 'admin'."
 *
 *    Code généré par l'IA :
 *      static void login(User user)  { currentUser = user; }
 *      static void logout()          { currentUser = null; }
 *      static boolean isLoggedIn()   { return currentUser != null; }
 *      static boolean isAdmin()      { return currentUser != null
 *                                        && "admin".equals(currentUser.getRole()); }
 *
 *    Corrections humaines :
 *      - Ajout de obtenirUtilisateurCourant() pour accès externe
 *      - Ajout de afficherDetails() pour debug/affichage session
 *      - Champ rendu private (accès via getter)
 * ============================================================
 */
public class Session {

    // ============================================================
    //  ÉTAPE 1 — Champ privé (squelette AGL)
    // ============================================================

    private static User utilisateurCourant;

    // ============================================================
    //  ÉTAPE 2 — Méthodes métier (implémentation)
    // ============================================================

    /**
     * Ouvrir une session pour l'utilisateur donné.
     */
    public static void login(User user) {
        utilisateurCourant = user;
    }

    /**
     * Fermer la session courante.
     */
    public static void logout() {
        utilisateurCourant = null;
    }

    /**
     * Vérifie si un utilisateur est actuellement connecté.
     */
    public static boolean estConnecte() {
        return utilisateurCourant != null;
    }

    /**
     * Vérifie si l'utilisateur courant est un administrateur.
     */
    public static boolean estAdmin() {
        return utilisateurCourant != null
            && "admin".equalsIgnoreCase(utilisateurCourant.getRole());
    }

    /**
     * Retourne l'utilisateur actuellement connecté.
     * Retourne null si aucune session active.
     */
    public static User obtenirUtilisateurCourant() {
        return utilisateurCourant;
    }

    /**
     * Afficher les détails de la session courante.
     */
    public static void afficherDetails() {
        if (utilisateurCourant == null) {
            System.out.println("=== Session === Aucune session active.");
        } else {
            System.out.println("=== Session active ===");
            System.out.println("Utilisateur : " + utilisateurCourant.getUsername());
            System.out.println("Rôle        : " + utilisateurCourant.getRole());
        }
    }

    // ============================================================
    //  toString
    // ============================================================

    @Override
    public String toString() {
        return utilisateurCourant == null
            ? "Session [aucune]"
            : "Session [" + utilisateurCourant.getUsername() + " / " + utilisateurCourant.getRole() + "]";
    }

    // ============================================================
    //  Compatibilité — alias pour l'ancien code
    // ============================================================

    /** @deprecated Utiliser estConnecte() */
    public static boolean isLoggedIn() { return estConnecte(); }

    /** @deprecated Utiliser estAdmin() */
    public static boolean isAdmin()    { return estAdmin(); }
}
