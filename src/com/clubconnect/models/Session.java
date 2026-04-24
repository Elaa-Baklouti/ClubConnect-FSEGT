package com.clubconnect.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *
 *  ETAPE 1 — Squelette AGL :
 *    Gestion de la session courante (singleton statique).
 *    Nommage francais : estConnecte(), estAdmin(),
 *    obtenirUtilisateurCourant(), afficherDetails()
 *
 *  ETAPE 2 — Implementation IA assistee :
 *    Prompt utilise :
 *      "Cree une classe Session Java avec champ statique User.
 *       Methodes : login(User), logout(), isLoggedIn(), isAdmin(),
 *       timestamp de debut session, getDureeSession() en secondes."
 *
 *    Code genere par l'IA :
 *      static void login(User u) { currentUser = u; debutSession = LocalDateTime.now(); }
 *      static void logout()      { currentUser = null; debutSession = null; }
 *
 *    Corrections humaines :
 *      - Champ renomme utilisateurCourant (nommage francais)
 *      - getDureeSession() en secondes (plus precis que minutes)
 *      - afficherDetails() avec DateTimeFormatter
 * ============================================================
 */
public class Session {

    private static User          utilisateurCourant;
    private static LocalDateTime debutSession;

    public static void login(User user) {
        if (user == null)
            throw new IllegalArgumentException("Utilisateur null.");
        utilisateurCourant = user;
        debutSession       = LocalDateTime.now();
    }

    public static void logout() {
        utilisateurCourant = null;
        debutSession       = null;
    }

    public static boolean estConnecte() {
        return utilisateurCourant != null;
    }

    public static boolean estAdmin() {
        return utilisateurCourant != null
            && "admin".equalsIgnoreCase(utilisateurCourant.getRole());
    }

    public static User obtenirUtilisateurCourant() {
        return utilisateurCourant;
    }

    public static long getDureeSession() {
        if (debutSession == null) return 0;
        return java.time.Duration.between(debutSession, LocalDateTime.now()).getSeconds();
    }

    public static void afficherDetails() {
        if (utilisateurCourant == null) {
            System.out.println("  Session : aucune.");
        } else {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            System.out.println("  Session : " + utilisateurCourant.getUsername()
                + " | role=" + utilisateurCourant.getRole()
                + " | depuis=" + (debutSession != null ? debutSession.format(fmt) : "N/A"));
        }
    }

    @Override
    public String toString() {
        return utilisateurCourant == null ? "Session[aucune]"
            : "Session[" + utilisateurCourant.getUsername() + "]";
    }
}
