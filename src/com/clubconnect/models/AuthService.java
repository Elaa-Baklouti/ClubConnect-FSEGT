package com.clubconnect.models;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *
 *  ETAPE 1 — Squelette AGL :
 *    Service d'authentification.
 *    Nommage francais : inscrire(), seConnecter(), seDeconnecter()
 *
 *  ETAPE 2 — Implementation IA assistee :
 *    Prompt utilise :
 *      "AuthService Java avec liste statique d'utilisateurs.
 *       inscrire() valide username/email/password et verifie
 *       unicite email. seConnecter() cherche par email et ouvre
 *       la session via Session.login()."
 *
 *    Code genere par l'IA :
 *      static User inscrire(String username, String email, String password) {
 *          if (!email.contains("@")) throw ...;
 *          User u = new User(nextId++, username, email, password);
 *          users.add(u); return u;
 *      }
 *
 *    Corrections humaines :
 *      - Validation email : '@' ET '.'
 *      - Verification null avant appel .length()
 *      - Unicite email via trouverParEmail()
 *      - bannirMembre() : forcer deconnexion si connecte
 * ============================================================
 */
public class AuthService {

    private static List<User> users  = new ArrayList<>();
    private static int        nextId = 1;

    public AuthService() { }

    public static User inscrire(String username, String email, String password) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username obligatoire.");
        if (email == null || !email.contains("@") || !email.contains("."))
            throw new IllegalArgumentException("Email invalide : " + email);
        if (password == null || password.length() < 6)
            throw new IllegalArgumentException("Mot de passe trop court (min 6).");
        if (trouverParEmail(email) != null)
            throw new IllegalStateException("Email deja utilise : " + email);
        User user = new User(nextId++, username, email, password);
        users.add(user);
        return user;
    }

    public static User seConnecter(String email, String password) {
        User user = trouverParEmail(email);
        if (user == null || !user.getPassword().equals(password))
            throw new IllegalStateException("Email ou mot de passe incorrect.");
        if ("banni".equalsIgnoreCase(user.getRole()))
            throw new IllegalStateException("Compte banni.");
        user.seConnecter();
        return user;
    }

    public static void seDeconnecter() {
        if (!Session.estConnecte())
            throw new IllegalStateException("Aucune session active.");
        Session.obtenirUtilisateurCourant().seDeconnecter();
    }

    public static void bannirMembre(int membreId) {
        if (!Session.estAdmin())
            throw new IllegalStateException("Droits admin requis.");
        User cible = trouverParId(membreId);
        if (cible == null)
            throw new IllegalArgumentException("Membre#" + membreId + " introuvable.");
        if ("admin".equalsIgnoreCase(cible.getRole()))
            throw new IllegalStateException("Impossible de bannir un admin.");
        cible.setRole("banni");
        User courant = Session.obtenirUtilisateurCourant();
        if (courant != null && courant.getId() == membreId) Session.logout();
    }

    public static void afficherDetails() {
        System.out.println("  Membres (" + users.size() + ") :");
        for (User u : users) System.out.println("    - " + u);
    }

    private static User trouverParEmail(String email) {
        for (User u : users)
            if (u.getEmail().equalsIgnoreCase(email)) return u;
        return null;
    }

    private static User trouverParId(int id) {
        for (User u : users)
            if (u.getId() == id) return u;
        return null;
    }

    @Override public String toString() { return "AuthService|membres=" + users.size(); }

    public static List<User> getUsers()       { return users; }
    public static void setUsers(List<User> u) { users = u; }
    public static int  getNextId()            { return nextId; }
    public static void setNextId(int id)      { nextId = id; }
}
