package com.clubconnect.models;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *
 *  ETAPE 1 — Squelette AGL (deterministe) :
 *    Classe utilitaire de gestion de la session courante.
 *    Package : com.clubconnect.models
 *    Pattern : champ statique prive -> methodes statiques metier
 *    Nommage francais : estConnecte(), estAdmin(),
 *    obtenirUtilisateurCourant(), afficherDetails()
 *
 *  ETAPE 2 — Implementation IA assistee :
 *    Prompt utilise :
 *      "Cree une classe Session Java avec un champ statique
 *       currentUser de type User. Ajoute les methodes statiques
 *       login(User), logout(), isLoggedIn() et isAdmin().
 *       isAdmin() retourne true si le role de l'utilisateur
 *       courant est 'admin'."
 *
 *    Code genere par l'IA :
 *      static void login(User user)  { currentUser = user; }
 *      static void logout()          { currentUser = null; }
 *      static boolean isLoggedIn()   { return currentUser != null; }
 *      static boolean isAdmin()      { return currentUser != null
 *                                        && "admin".equals(currentUser.getRole()); }
 *
 *    Corrections humaines :
 *      - Champ rendu private (acces via getter)
 *      - Ajout de obtenirUtilisateurCourant()
 *      - Ajout de afficherDetails() pour affichage console
 * ============================================================
 */
public class Session {

    // ============================================================
    //  ETAPE 1 — Champ prive (squelette AGL)
    // ============================================================

    private static User utilisateurCourant;

    // ============================================================
    //  ETAPE 2 — Methodes metier (implementation)
    // ============================================================

    /** Ouvrir une session pour l'utilisateur donne. */
    public static void login(User user) {
        utilisateurCourant = user;
    }

    /** Fermer la session courante. */
    public static void logout() {
        utilisateurCourant = null;
    }

    /** Verifie si un utilisateur est connecte. */
    public static boolean estConnecte() {
        return utilisateurCourant != null;
    }

    /** Verifie si l'utilisateur courant est administrateur. */
    public static boolean estAdmin() {
        return utilisateurCourant != null
            && "admin".equalsIgnoreCase(utilisateurCourant.getRole());
    }

    /** Retourne l'utilisateur connecte, ou null si aucune session. */
    public static User obtenirUtilisateurCourant() {
        return utilisateurCourant;
    }

    /** Afficher l'etat de la session en console. */
    public static void afficherDetails() {
        if (utilisateurCourant == null) {
            System.out.println("  Session : aucune session active.");
        } else {
            System.out.println("  Session active : " + utilisateurCourant.getUsername()
                + " | role=" + utilisateurCourant.getRole());
        }
    }

    // ============================================================
    //  toString
    // ============================================================

    @Override
    public String toString() {
        return utilisateurCourant == null
            ? "Session [aucune]"
            : "Session [" + utilisateurCourant.getUsername()
              + " / " + utilisateurCourant.getRole() + "]";
    }
}
