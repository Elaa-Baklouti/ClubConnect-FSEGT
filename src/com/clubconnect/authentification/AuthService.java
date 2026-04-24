package com.clubconnect.models.authentification;

import java.util.ArrayList;
import java.util.List;

public class AuthService {

    public static List<User> users = new ArrayList<>();
    static int nextId = 1;

    public AuthService() { }

    public static User inscrire(String username, String email, String password) {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Username obligatoire.");
        if (email == null || !email.contains("@") || !email.contains(".")) throw new IllegalArgumentException("Email invalide.");
        if (password == null || password.length() < 6) throw new IllegalArgumentException("Mot de passe trop court.");
        if (trouverParEmail(email) != null) throw new IllegalStateException("Email deja utilise.");
        User user = new User(nextId++, username, email, password);
        users.add(user);
        return user;
    }

    public static User seConnecter(String email, String password) {
        User user = trouverParEmail(email);
        if (user == null || !user.getPassword().equals(password))
            throw new IllegalStateException("Email ou mot de passe incorrect.");
        Session.login(user);
        return user;
    }

    public static void seDeconnecter() { Session.logout(); }

    public static void afficherDetails() {
        System.out.println("=== Membres inscrits (" + users.size() + ") ===");
        for (User u : users) System.out.println("  - " + u);
    }

    @Override public String toString() { return "AuthService | membres=" + users.size(); }

    public static List<User> getUsers() { return users; }
    public static void setUsers(List<User> u) { users = u; }
    public static int getNextId() { return nextId; }
    public static void setNextId(int id) { nextId = id; }

    private static User trouverParEmail(String email) {
        for (User u : users) if (u.getEmail().equalsIgnoreCase(email)) return u;
        return null;
    }
}
