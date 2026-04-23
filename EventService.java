import java.util.ArrayList;
import java.util.List;

/**
 * Service de gestion des événements.
 * Prompt IA utilisé : "implémente les méthodes métier simples avec System.out.println temporaires"
 */
public class EventService {

    // --- Champs privés ---
    private List<Event> evenements;
    private int nextId;

    // --- Constructeur vide ---
    public EventService() {
        this.evenements = new ArrayList<>();
        this.nextId     = 1;
    }

    // --- Méthodes métier ---

    /** CF-12 : Créer un événement */
    public Event creerEvenement(String titre, String lieu, String date, User organisateur) {
        Event event = new Event(nextId++, titre, lieu, date, organisateur);
        evenements.add(event);
        System.out.println("Événement créé : " + titre);
        return event;
    }

    /** CF-13 : Participer à un événement */
    public void participer(int eventId, User user) {
        for (Event e : evenements) {
            if (e.getId() == eventId) {
                if (e.estParticipant(user.getUsername())) {
                    System.out.println(user.getUsername() + " participe déjà à cet événement.");
                    return;
                }
                e.ajouterParticipant(user.getUsername());
                return;
            }
        }
        System.out.println("Événement introuvable.");
    }

    /** Afficher tous les événements */
    public void afficherDetails() {
        if (evenements.isEmpty()) {
            System.out.println("Aucun événement.");
        } else {
            evenements.forEach(System.out::println);
        }
    }

    // --- toString ---
    @Override
    public String toString() {
        return "EventService | événements=" + evenements.size();
    }

    // --- Getters / Setters ---
    public List<Event> getEvenements() { return evenements; }
    public void setEvenements(List<Event> evenements) { this.evenements = evenements; }
}
