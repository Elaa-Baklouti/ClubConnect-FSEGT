import java.util.ArrayList;
import java.util.List;

public class EventService {

    private List<Event> evenements;
    private int nextId;

    public EventService() {
        this.evenements = new ArrayList<>();
        this.nextId     = 1;
    }

    /** CF-12 : Créer un événement avec validation complète */
    public Event creerEvenement(String titre, String lieu, String date,
                                User organisateur, int capaciteMax, double fraisInscription) {
        if (titre == null || titre.isBlank())
            throw new IllegalArgumentException("Le titre est obligatoire.");
        if (lieu == null || lieu.isBlank())
            throw new IllegalArgumentException("Le lieu est obligatoire.");
        if (date == null || date.isBlank())
            throw new IllegalArgumentException("La date est obligatoire.");
        if (organisateur == null)
            throw new IllegalArgumentException("L'organisateur est obligatoire.");
        if (capaciteMax < 1 || capaciteMax > 200)
            throw new IllegalArgumentException("Capacité entre 1 et 200.");
        if (fraisInscription < 0)
            throw new IllegalArgumentException("Frais ne peuvent pas être négatifs.");

        Event event = new Event(nextId++, titre, lieu, date, organisateur, capaciteMax, fraisInscription);
        evenements.add(event);
        return event;
    }

    /** Surcharge sans frais ni capacité */
    public Event creerEvenement(String titre, String lieu, String date, User organisateur) {
        return creerEvenement(titre, lieu, date, organisateur, 50, 0.0);
    }

    /** CF-13 : Inscrire un utilisateur */
    public void participer(int eventId, User user) {
        trouverParId(eventId).ajouterParticipant(user);
    }

    /** CF-13b : Annuler la participation */
    public void annulerParticipation(int eventId, User user) {
        trouverParId(eventId).annulerParticipation(user);
    }

    /** Annuler un événement entier */
    public void annulerEvenement(int eventId, User demandeur, List<User> tousLesUsers) {
        trouverParId(eventId).annulerEvenement(demandeur, tousLesUsers);
    }

    /** Rechercher par lieu */
    public List<Event> rechercherParLieu(String lieu) {
        List<Event> resultats = new ArrayList<>();
        for (Event e : evenements)
            if (e.getLieu().equalsIgnoreCase(lieu) && !e.isAnnule())
                resultats.add(e);
        return resultats;
    }

    /** Afficher tous les événements actifs */
    public void afficherDetails() {
        boolean aucun = true;
        for (Event e : evenements) {
            if (!e.isAnnule()) { System.out.println(e); aucun = false; }
        }
        if (aucun) System.out.println("Aucun événement actif.");
    }

    private Event trouverParId(int id) {
        for (Event e : evenements)
            if (e.getId() == id) return e;
        throw new IllegalArgumentException("Événement #" + id + " introuvable.");
    }

    @Override
    public String toString() {
        return "EventService | événements=" + evenements.size();
    }

    public List<Event> getEvenements() { return evenements; }
    public void setEvenements(List<Event> evenements) { this.evenements = evenements; }
}
