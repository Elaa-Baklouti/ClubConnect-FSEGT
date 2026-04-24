package com.clubconnect.models.authentification;

public class Session {

    public static User currentUser;

    public Session() { }

    public static void login(User user) { currentUser = user; }
    public static void logout() { currentUser = null; }
    public static boolean isLoggedIn() { return currentUser != null; }
    public static boolean isAdmin() {
        return currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole());
    }

    public static void afficherDetails() {
        if (currentUser == null)
            System.out.println("=== Session === Aucune session active.");
        else {
            System.out.println("=== Session active ===");
            System.out.println("Utilisateur : " + currentUser.getUsername());
            System.out.println("Role        : " + currentUser.getRole());
        }
    }

    @Override
    public String toString() {
        return currentUser == null ? "Session [aucune]"
            : "Session [" + currentUser.getUsername() + " / " + currentUser.getRole() + "]";
    }

    public static User getCurrentUser() { return currentUser; }
    public static void setCurrentUser(User user) { currentUser = user; }
}
