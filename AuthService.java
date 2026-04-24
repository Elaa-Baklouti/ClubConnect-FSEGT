package com.clubconnect.models;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *  DOCUMENTATION TP1 — Approche hybride
 * ============================================================
 *
 *  ÉTAPE 1 — Squelette AGL (déterministe) :
 *    Service d'authentification — gère l'inscription, la
 *    connexion et la déconnexion des membres.
 *    Package : com.clubconnect.models
 *    Pattern : champs privés → constructeur vide
 *             → méthodes métier (signatures) → getters/setters
 *    Nommage français : inscrire(), seConnecter(),
 *    seDeconnecter(), afficherDetails()
 *
 *  ÉTAPE 2 — Implémentation IA assistée :
 *    Prompt utilisé :
 *      "Implémente un AuthService Java avec une liste statique
 *       d'utilisateurs. Méthodes : inscription(username, email,
 *       password) qui valide les champs et crée un User, et
 *       login(email, password) qui cherche l'utilisateur et
 *       ouvre la session. Lancer des exceptions métier si les
 *       règles sont violées."
 *
 *    Code généré par l'IA :
 *      static User inscription(String username, String email, String password) {
 *          // validation + new User + users.add()
 *      }
 *      static User login(String email, String password) {
 *          // recherche dans users + Session.login()
 *      }
 *
 *    Corrections humaines :
 *      - Validation email : vérification présence de '@' et '.'
 *      - Validation password : longueur minimale 6 caractères
 *      - Ajout de trouverParEmail() comme utilitaire privé
 *      - Ajout de afficherDetails() pour lister les membres
 * ============================================================
 */
public class AuthService {

    // ============================================================
    //  ÉTAPE 1 — Champs privés (squelette AGL)
    // ============================================================

    private static List<User> users  = new ArrayList<>();
    private static int        nextId = 1;

    // ============================================================
    //  ÉTAPE 1 — Constructeur vide
    // ============================================================

    public AuthService() { }

    // ============================================================
    //  ÉTAPE 2 — Méthodes métier (implémentation)
    // ============================================================

    /**
     * CF-1 : Inscrire un nouveau membre.
     * Valide username, email et mot de passe avant création.
     *
     * @param username  Nom d'utilisateur (non vide)
     * @param email     Adresse email valide (contient '@' et '.')
     * @param password  Mot de passe (6 caractères minimum)
     * @return          Le User créé et ajouté à la liste
     */
    public static User inscrire(String username, String email, String password) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Le nom d'utilisateur est obligatoire.");
        if (email == null || !email.contains("@") || !email.contains("."))
            throw new IllegalArgumentException("Adresse email invalide.");
        if (password == null || password.length() < 6)
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères.");

        // Vérifier unicité de l'email
        if (trouverParEmail(email) != null)
            throw new IllegalStateException("Un compte existe déjà avec l'email : " + email);

        User user = new User(nextId++, username, email, password);
        users.add(user);
        System.out.println("Inscription réussie : " + username);
        return user;
    }

    /**
     * CF-2 : Connecter un membre existant.
     * Recherche par email + mot de passe, puis ouvre la session.
     *
     * @param email     Email du membre
     * @param password  Mot de passe du membre
     * @return          Le User connecté
     */
    public static User seConnecter(String email, String password) {
        User user = trouverParEmail(email);
        if (user == null || !user.getPassword().equals(password))
            throw new IllegalStateException("Email ou mot de passe incorrect.");

        Session.login(user);
        System.out.println("Connecté : " + user.getUsername());
        return user;
    }

    /**
     * CF-2b : Déconnecter le membre courant.
     */
    public static void seDeconnecter() {
        Session.logout();
        System.out.println("Déconnexion effectuée.");
    }

    /**
     * Afficher la liste de tous les membres inscrits.
     */
    public static void afficherDetails() {
        System.out.println("=== Membres inscrits (" + users.size() + ") ===");
        if (users.isEmpty()) {
            System.out.println("  Aucun membre.");
        } else {
            for (User u : users) {
                System.out.println("  - " + u);
            }
        }
    }

    // ============================================================
    //  Utilitaire privé
    // ============================================================

    /**
     * Rechercher un utilisateur par son email.
     * Retourne null si introuvable.
     */
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

    public static List<User> getUsers()          { return users; }
    public static void setUsers(List<User> u)    { users = u; }

    public static int getNextId()                { return nextId; }
    public static void setNextId(int id)         { nextId = id; }

    // ============================================================
    //  Compatibilité — alias pour l'ancien code
    // ============================================================

    /** @deprecated Utiliser inscrire() */
    public static User inscription(String username, String email, String password) {
        return inscrire(username, email, password);
    }

    /** @deprecated Utiliser seConnecter() */
    public static User login(String email, String password) {
        return seConnecter(email, password);
    }

    /** @deprecated Utiliser seDeconnecter() */
    public static void logout() {
        seDeconnecter();
    }
}
