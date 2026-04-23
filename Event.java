import java.util.ArrayList;
import java.util.List;

/**
 * Représente un événement créé par un membre du club.
 * Prompt IA utilisé : "génère constructeur vide, complet, getters/setters et toString pour cette classe"
 */
public class Event {

    // --- Champs privés ---
    private int id;
    private String titre;
    private String lieu;
    private String date;
    private User organisateur;
    private List<String> participants;

    // --- Constructeur vide ---
    public Event() {
        this.participants = new ArrayList<>();
    }

    // --- Constructeur complet ---
    public Event(int id, String titre, String lieu, String date, User organisateur) {
        this.id           = id;
        this.titre        = titre;
        this.lieu         = lieu;
        this.date         = date;
        this.organisateur = organisateur;
        this.participants = new ArrayList<>();
    }

    // --- Méthodes métier ---

    /** CF-12 : Ajouter un participant à l'événement */
    public void ajouterParticipant(String username) {
        participants.add(username);
        System.out.println(username + " ajouté à l'événement : " + titre);
    }

    /** CF-13 : Afficher les détails de l'événement */
    public void afficherDetails() {
        System.out.println("=== Détails Événement ===");
        System.out.println("ID           : " + id);
        System.out.println("Titre        : " + titre);
        System.out.println("Lieu         : " + lieu);
        System.out.println("Date         : " + date);
        System.out.println("Organisateur : " + (organisateur != null ? organisateur.getUsername() : "N/A"));
        System.out.println("Participants : " + participants.size());
    }

    /** Vérifie si un utilisateur participe déjà */
    public boolean estParticipant(String username) {
        return participants.contains(username);
    }

    // --- toString ---
    @Override
    public String toString() {
        return "Event#" + id + " [" + titre + "] à " + lieu
             + " le " + date + " | participants=" + participants.size();
    }

    // --- Getters / Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public User getOrganisateur() { return organisateur; }
    public void setOrganisateur(User organisateur) { this.organisateur = organisateur; }

    public List<String> getParticipants() { return participants; }
    public void setParticipants(List<String> participants) { this.participants = participants; }
}
