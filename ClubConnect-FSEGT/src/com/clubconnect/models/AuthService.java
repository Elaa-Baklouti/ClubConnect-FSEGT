package com.clubconnect.models;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *
 *  ETAPE 1 — Squelette AGL (deterministe) :
 *    Service d'authentification — inscription, connexion,
 *    deconnexion des membres.
 *    Package : com.clubconnect.models
 *    Pattern : champs prives -> constructeur vide
 *             -> methodes metier (signatures) -> getters/setters
 *    Nommage francais : inscrire(), seConnecter(),
 *    seDeconnecter(), afficherDetails()
 *
 *  ETAPE 2 — Implementation IA assistee :
 *    Prompt utilise :
 *      "Implemente un AuthService Java avec une liste statique
 *       d'utilisateurs. Methodes : inscription(username, email,
 *       password) qui valide les champs et cree un User, et
 *       login(email, password) qui cherche l'utilisateur et
 *       ouvre la session. Lancer des exceptions metier si les
 *       regles sont violees."
 *
 *    Code genere par l'IA :
 *      static User inscription(String username, String email, String password) {
 *          // validation + new User + users.add()
 *      }
 *      static User login(String email, String password) {
 *          // recherche dans users + Session.login()
 *      }
 *
 *    Corrections humaines :
 *      - Validation email : presence de '@' et '.'
 *      - Validation password : longueur minimale 6 caracteres
 *      - Unicite de l'email verifiee avant creation
 *      - Ajout de trouverParEmail() comme utilitaire prive
 *      - Ajout de afficherDetails() pour lister les membres
 * ============================================================
 */
public class AuthService {

    // ============================================================
    //  ETAPE 1 — Champs prives (squelette AGL)
    // ============================================================

    private static List<User> users  = new ArrayList<>();
    private static int        nextId = 1;

    // ============================================================
    //  ETAPE 1 — Constructeur vide
    // ============================================================

    public AuthService() { }

    // ============================================================
    //  ETAPE 2 — Methodes metier (implementation)
    // ============================================================

    /**
     * CF-1 : Inscrire un nouveau membre.
     *
     * @param username  Nom d'utilisateur (non vide)
     * @param email     Email valide (contient '@' et '.')
     * @param password  Mot de passe (6 caracteres minimum)
     * @return          Le User cree
     */
    public static User inscrire(String username, String email, String password) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Le nom d'utilisateur est obligatoire.");
        if (email == null || !email.contains("@") || !email.contains("."))
            throw new IllegalArgumentException("Adresse email invalide.");
        if (password == null || password.length() < 6)
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caracteres.");
        if (trouverParEmail(email) != null)
            throw new IllegalStateException("Un compte existe deja avec l'email : " + email);

        User user = new User(nextId++, username, email, password);
        users.add(user);
        System.out.println("  Inscription reussie : " + username);
        return user;
    }

    /**
     * CF-2 : Connecter un membre existant.
     *
     * @param email     Email du membre
     * @param password  Mot de passe du membre
     * @return          Le User connecte
     */
    public static User seConnecter(String email, String password) {
        User user = trouverParEmail(email);
        if (user == null || !user.getPassword().equals(password))
            throw new IllegalStateException("Email ou mot de passe incorrect.");

        Session.login(user);
        System.out.println("  Connecte : " + user.getUsername()
            + " | role=" + user.getRole());
        return user;
    }

    /**
     * CF-2b : Deconnecter le membre courant.
     */
    public static void seDeconnecter() {
        User courant = Session.obtenirUtilisateurCourant();
        Session.logout();
        System.out.println("  Deconnexion : "
            + (courant != null ? courant.getUsername() : "aucun utilisateur"));
    }

    /**
     * Afficher la liste de tous les membres inscrits.
     */
    public static void afficherDetails() {
        System.out.println("  Membres inscrits (" + users.size() + ") :");
        if (users.isEmpty()) {
            System.out.println("    Aucun membre.");
        } else {
            for (User u : users) {
                System.out.println("    - " + u);
            }
        }
    }

    // ============================================================
    //  Utilitaire prive
    // ============================================================

    /** Rechercher un utilisateur par email. Retourne null si introuvable. */
    private static User trouverParEmail(String email) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email))
                return u;
        }
        return null;
    }

    // ============================================================
    //  toString
    // ============================================================

    @Override
    public String toString() {
        return "AuthService | membres=" + users.size();
    }

    // ============================================================
    //  Getters / Setters
    // ============================================================

    public static List<User> getUsers()       { return users; }
    public static void setUsers(List<User> u) { users = u; }

    public static int  getNextId()            { return nextId; }
    public static void setNextId(int id)      { nextId = id; }
}
