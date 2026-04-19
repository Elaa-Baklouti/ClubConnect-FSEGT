import java.util.ArrayList;
import java.util.List;

public class AuthService {
    static List<User> users = new ArrayList<>();
    static int nextId = 1;

    // CF-1 : Inscription
    static User inscription(String username, String email, String password) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username obligatoire.");
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Email invalide.");
        if (password == null || password.length() < 6)
            throw new IllegalArgumentException("Mot de passe trop court.");

        User user = new User(nextId++, username, email, password);
        users.add(user);
        System.out.println("Inscription OK : " + username);
        return user;
    }

    // CF-2 : Login
    static User login(String email, String password) {
        for (User u : users) {
            if (u.email.equals(email) && u.password.equals(password)) {
                Session.login(u);
                System.out.println("Connecté : " + u.username);
                return u;
            }
        }
        throw new IllegalStateException("Email ou mot de passe incorrect.");
    }

    static void logout() { Session.logout(); }
}