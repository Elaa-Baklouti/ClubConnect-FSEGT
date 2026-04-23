public class Session {
    static User currentUser;

    static boolean isLoggedIn() {
        return currentUser != null;
    }

    static void login(User user) {
        currentUser = user;
    }

    static void logout() {
        currentUser = null;
    }

    static boolean isAdmin() {
        return currentUser != null && "admin".equals(currentUser.role);
    }
}
