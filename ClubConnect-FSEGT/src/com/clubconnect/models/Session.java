package com.clubconnect.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ============================================================
 *  DOCUMENTATION TP3 — Approche IA assistee
 * ============================================================
 *
 *  METHODE : login() / logout()
 *  ----------------------------
 *  Prompt utilise :
 *    "Cree une classe Session Java avec un champ statique prive
 *     currentUser de type User. Ajoute login(User), logout(),
 *     isLoggedIn() et isAdmin(). Ajoute aussi un timestamp
 *     d'ouverture de session et une methode getDureeSession()
 *     qui retourne le nombre de minutes depuis la connexion."
 *
 *  Code genere par l'IA :
 *    private static User currentUser;
 *    private static LocalDateTime debutSession;
 *
 *    public static void login(User user) {
 *        currentUser  = user;
 *        debutSession = LocalDateTime.now();
 *    }
 *    public static void logout() {
 *        currentUser  = null;
 *        debutSession = null;
 *    }
 *    public static long getDureeSession() {
 *        if (debutSession == null) return 0;
 *        return Duration.between(debutSession, LocalDateTime.now()).toMinutes();
 *    }
 *
 *  Code final apres corrections humaines :
 *    - Champ renomme utilisateurCourant (nommage francais)
 *    - Ajout de obtenirUtilisateurCourant() pour acces externe
 *    - Ajout de estConnecte() et estAdmin() en nommage francais
 *    - afficherDetails() formate la date avec DateTimeFormatter
 *    - getDureeSession() retourne les secondes (plus precis pour les tests)
 *
 * ============================================================
 */
public class Session {

    // ============================================================
    //  Champs prives
    // ============================================================

    private static User          utilisateurCourant;
    private static LocalDateTime debutSession;

    // ============================================================
    //  Methodes metier — logique reelle
    // ============================================================

    /**
     * Ouvrir une session pour l'utilisateur donne.
     * Enregistre le timestamp de debut de session.
     *
     * @param user Utilisateur a connecter (non null)
     */
    public static void login(User user) {
        if (user == null)
            throw new IllegalArgumentException("Impossible d'ouvrir une session pour un utilisateur null.");
        utilisateurCourant = user;
        debutSession       = LocalDateTime.now();
    }

    /**
     * Fermer la session courante.
     * Remet utilisateurCourant et debutSession a null.
     */
    public static void logout() {
        utilisateurCourant = null;
        debutSession       = null;
    }

    /**
     * Verifie si un utilisateur est actuellement connecte.
     *
     * @return true si une session est active
     */
    public static boolean estConnecte() {
        return utilisateurCourant != null;
    }

    /**
     * Verifie si l'utilisateur courant a le role "admin".
     * Interaction avec User : appel de getRole()
     *
     * @return true si admin connecte
     */
    public static boolean estAdmin() {
        return utilisateurCourant != null
            && "admin".equalsIgnoreCase(utilisateurCourant.getRole());
    }

    /**
     * Retourne l'utilisateur actuellement connecte.
     *
     * @return User connecte, ou null si aucune session
     */
    public static User obtenirUtilisateurCourant() {
        return utilisateurCourant;
    }

    /**
     * Retourne la duree de la session courante en secondes.
     * Retourne 0 si aucune session active.
     *
     * @return duree en secondes
     */
    public static long getDureeSession() {
        if (debutSession == null) return 0;
        return java.time.Duration.between(debutSession, LocalDateTime.now()).getSeconds();
    }

    /**
     * Afficher l'etat complet de la session en console.
     */
    public static void afficherDetails() {
        if (utilisateurCourant == null) {
            System.out.println("  Session : aucune session active.");
        } else {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            System.out.println("  Session active   : " + utilisateurCourant.getUsername()
                + " | role=" + utilisateurCourant.getRole());
            System.out.println("  Debut session    : "
                + (debutSession != null ? debutSession.format(fmt) : "N/A"));
            System.out.println("  Duree session    : " + getDureeSession() + " sec");
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
