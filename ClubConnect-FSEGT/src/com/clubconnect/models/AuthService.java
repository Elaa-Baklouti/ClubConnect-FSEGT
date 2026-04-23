package com.clubconnect.models;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *  DOCUMENTATION TP3 — Approche IA assistee
 * ============================================================
 *
 *  METHODE : inscrire()
 *  --------------------
 *  Prompt utilise :
 *    "Implemente inscrire(String username, String email, String password)
 *     dans un AuthService Java. Valider : username non vide, email contenant
 *     '@' et '.', password d'au moins 6 caracteres. Verifier l'unicite de
 *     l'email dans la liste. Creer un User avec un id auto-incremente et
 *     l'ajouter a la liste. Lancer des exceptions metier explicites."
 *
 *  Code genere par l'IA :
 *    public static User inscrire(String username, String email, String password) {
 *        if (username == null || username.isEmpty())
 *            throw new IllegalArgumentException("Username obligatoire.");
 *        if (!email.contains("@"))
 *            throw new IllegalArgumentException("Email invalide.");
 *        if (password.length() < 6)
 *            throw new IllegalArgumentException("Mot de passe trop court.");
 *        User user = new User(nextId++, username, email, password);
 *        users.add(user);
 *        return user;
 *    }
 *
 *  Code final apres corrections humaines :
 *    - Validation email : presence de '@' ET '.' (l'IA ne verifiait que '@')
 *    - Verification null sur email et password avant appel de methodes
 *    - Verification unicite email via trouverParEmail() avant creation
 *    - Solde initial a 0.0 DT (explicite dans le constructeur User)
 *
 * ============================================================
 *
 *  METHODE : seConnecter()
 *  -----------------------
 *  Prompt utilise :
 *    "Implemente seConnecter(String email, String password) dans AuthService.
 *     Rechercher l'utilisateur par email dans la liste. Verifier le mot de
 *     passe. Verifier que le compte n'est pas banni. Ouvrir la session via
 *     Session.login(). Retourner le User connecte."
 *
 *  Code genere par l'IA :
 *    public static User seConnecter(String email, String password) {
 *        for (User u : users) {
 *            if (u.getEmail().equals(email) && u.getPassword().equals(password)) {
 *                Session.login(u);
 *                return u;
 *            }
 *        }
 *        throw new IllegalStateException("Identifiants incorrects.");
 *    }
 *
 *  Code final apres corrections humaines :
 *    - Separation des cas : email introuvable vs mot de passe incorrect
 *      (message d'erreur generique pour ne pas divulguer si l'email existe)
 *    - Verification role "banni" avant ouverture de session
 *    - Delegation a user.seConnecter() pour centraliser la logique
 *      (interaction User <-> Session via la methode de User)
 *
 * ============================================================
 *
 *  METHODE : reinitialiserMotDePasse()
 *  ------------------------------------
 *  Prompt utilise :
 *    "Implemente reinitialiserMotDePasse(String email, String nouveauPassword)
 *     dans AuthService. Retrouver l'utilisateur par email. Valider le nouveau
 *     mot de passe (6 caracteres min). Mettre a jour directement le champ
 *     password (simulation reset sans token). Lancer une exception si
 *     l'email est introuvable."
 *
 *  Code genere par l'IA :
 *    public static void reinitialiserMotDePasse(String email, String nouveauPassword) {
 *        User user = trouverParEmail(email);
 *        if (user == null) throw new IllegalArgumentException("Email introuvable.");
 *        if (nouveauPassword.length() < 6)
 *            throw new IllegalArgumentException("Mot de passe trop court.");
 *        user.setPassword(nouveauPassword);
 *    }
 *
 *  Code final apres corrections humaines :
 *    - Validation null sur nouveauPassword avant appel .length()
 *    - Invalidation de la session si l'utilisateur reinitialise est connecte
 *      (securite : forcer reconnexion apres reset)
 *    - Interaction avec Session : verifier Session.obtenirUtilisateurCourant()
 *
 * ============================================================
 */
public class AuthService {

    // ============================================================
    //  Champs prives
    // ============================================================

    private static List<User> users  = new ArrayList<>();
    private static int        nextId = 1;

    // ============================================================
    //  Constructeur vide
    // ============================================================

    public AuthService() { }

    // ============================================================
    //  Methodes metier — logique reelle
    // ============================================================

    /**
     * CF-1 : Inscrire un nouveau membre.
     * Valide les champs, verifie l'unicite de l'email, cree le User.
     * Interaction avec User : new User(nextId++, username, email, password)
     *
     * @param username  Nom d'utilisateur (non vide)
     * @param email     Email valide (contient '@' et '.')
     * @param password  Mot de passe (6 caracteres minimum)
     * @return          Le User cree avec solde initial 0.000 DT
     */
    public static User inscrire(String username, String email, String password) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException(
                "Le nom d'utilisateur est obligatoire.");
        if (email == null || !email.contains("@") || !email.contains("."))
            throw new IllegalArgumentException(
                "Adresse email invalide : " + email);
        if (password == null || password.length() < 6)
            throw new IllegalArgumentException(
                "Le mot de passe doit contenir au moins 6 caracteres.");
        if (trouverParEmail(email) != null)
            throw new IllegalStateException(
                "Un compte existe deja avec l'email : " + email);

        User user = new User(nextId++, username, email, password);
        users.add(user);
        return user;
    }

    /**
     * CF-2 : Connecter un membre existant.
     * Recherche par email, verifie le mot de passe et le statut du compte.
     * Interaction avec User : user.seConnecter() -> Session.login(user)
     * Interaction avec Session : ouverture de session + timestamp
     *
     * @param email     Email du membre
     * @param password  Mot de passe du membre
     * @return          Le User connecte
     */
    public static User seConnecter(String email, String password) {
        User user = trouverParEmail(email);
        // Message generique pour ne pas divulguer si l'email existe
        if (user == null || !user.getPassword().equals(password))
            throw new IllegalStateException(
                "Email ou mot de passe incorrect.");
        if ("banni".equalsIgnoreCase(user.getRole()))
            throw new IllegalStateException(
                "Compte banni. Connexion refusee.");

        // Delegation a User.seConnecter() qui appelle Session.login()
        user.seConnecter();
        return user;
    }

    /**
     * CF-2b : Deconnecter le membre courant.
     * Interaction avec Session : Session.logout()
     */
    public static void seDeconnecter() {
        if (!Session.estConnecte())
            throw new IllegalStateException(
                "Aucune session active a fermer.");
        User courant = Session.obtenirUtilisateurCourant();
        courant.seDeconnecter(); // delegue a User -> Session.logout()
    }

    /**
     * Reinitialiser le mot de passe d'un membre (simulation sans token).
     * Invalide la session si le membre concerne est connecte.
     * Interaction avec Session : verification + invalidation si necessaire
     *
     * @param email           Email du membre
     * @param nouveauPassword Nouveau mot de passe (6 caracteres minimum)
     */
    public static void reinitialiserMotDePasse(String email, String nouveauPassword) {
        User user = trouverParEmail(email);
        if (user == null)
            throw new IllegalArgumentException(
                "Aucun compte trouve avec l'email : " + email);
        if (nouveauPassword == null || nouveauPassword.length() < 6)
            throw new IllegalArgumentException(
                "Le nouveau mot de passe doit contenir au moins 6 caracteres.");

        user.setPassword(nouveauPassword);

        // Invalidation de session si l'utilisateur reinitialise est connecte
        User courant = Session.obtenirUtilisateurCourant();
        if (courant != null && courant.getId() == user.getId()) {
            Session.logout();
        }
    }

    /**
     * Bannir un membre (reserve a l'admin).
     * Interaction avec Session : verifie les droits admin
     * Interaction avec User : modifie le role -> "banni"
     *
     * @param membreId ID du membre a bannir
     */
    public static void bannirMembre(int membreId) {
        if (!Session.estAdmin())
            throw new IllegalStateException(
                "Droits administrateur requis pour bannir un membre.");

        User cible = trouverParId(membreId);
        if (cible == null)
            throw new IllegalArgumentException(
                "Membre#" + membreId + " introuvable.");
        if ("admin".equalsIgnoreCase(cible.getRole()))
            throw new IllegalStateException(
                "Impossible de bannir un administrateur.");

        cible.setRole("banni");

        // Forcer la deconnexion si le membre banni est connecte
        User courant = Session.obtenirUtilisateurCourant();
        if (courant != null && courant.getId() == membreId) {
            Session.logout();
        }
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
    //  Utilitaires prives
    // ============================================================

    /** Rechercher un utilisateur par email (insensible a la casse). */
    private static User trouverParEmail(String email) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email))
                return u;
        }
        return null;
    }

    /** Rechercher un utilisateur par ID. */
    private static User trouverParId(int id) {
        for (User u : users) {
            if (u.getId() == id) return u;
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
