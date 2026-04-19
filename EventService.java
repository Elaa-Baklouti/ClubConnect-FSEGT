import java.util.ArrayList;
import java.util.List;

public class EventService {
    static List<Event> events = new ArrayList<>();
    static int nextId = 1;

    // CF-12 : Créer événement
    static Event creerEvenement(String title, String location) {
        if (!Session.isLoggedIn())
            throw new IllegalStateException("Connexion requise.");
        Event event = new Event(nextId++, title, location, Session.currentUser);
        events.add(event);
        System.out.println("Événement créé : " + title);
        return event;
    }

    // CF-13 : Participer
    static void participer(int eventId) {
        if (!Session.isLoggedIn())
            throw new IllegalStateException("Connexion requise.");
        for (Event e : events) {
            if (e.id == eventId) {
                e.participants.add(Session.currentUser.username);
                System.out.println(Session.currentUser.username
                    + " participe à " + e.title);
                return;
            }
        }
        throw new IllegalArgumentException("Événement introuvable.");
    }
}
