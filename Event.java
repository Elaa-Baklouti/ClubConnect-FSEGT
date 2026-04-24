import java.util.ArrayList;
import java.util.List;

public class Event {

    private int id;
    private String titre;
    private String lieu;
    private String date;
    private User organisateur;
    private List<String> participants;
    private int capaciteMax;
    private double fraisInscription; // en DT
    private boolean annule;

    public Event() {
        this.participants     = new ArrayList<>();
        this.capaciteMax      = 50;
        this.fraisInscription = 0.0;
        this.annule           = false;
    }

    public Event(int id, String titre, String lieu, String date,
                 User organisateur, int capaciteMax, double fraisInscription) {
        this.id               = id;
        this.titre            = titre;
        this.lieu             = lieu;
        this.date             = date;
        this.organisateur     = organisateur;
        this.participants     = new ArrayList<>();
        this.capaciteMax      = capaciteMax;
        this.fraisInscription = fraisInscription;
        this.annule           = false;
    }

    public Event(int id, String titre, String lieu, String date, User organisateur) {
        this(id, titre, lieu, date, organisateur, 50, 0.0);
    }

    public void ajouterParticipant(User user) {
        if (annule)
            throw new IllegalStateException("L'événement est annulé.");
        if (organisateur != null && organisateur.getId() == user.getId())
            throw new IllegalStateException("L'organisateur ne peut pas s'inscrire.");
        if (participants.contains(user.getUsername()))
            throw new IllegalStateException(user.getUsername() + " est déjà inscrit.");
        if (participants.size() >= capaciteMax)
            throw new IllegalStateException("Événement complet (" + capaciteMax + " max).");
        if (fraisInscription > 0) {
            if (user.getSolde() < fraisInscription)
                throw new IllegalStateException("Solde insuffisant. Frais : " + fraisInscription + " DT.");
            user.debiterSolde(fraisInscription);
        }
        participants.add(user.getUsername());
    }

    public void annulerParticipation(User user) {
        if (!participants.contains(user.getUsername()))
            throw new IllegalStateException(user.getUsername() + " n'est pas inscrit.");
        participants.remove(user.getUsername());
        if (fraisInscription > 0)
            user.crediterSolde(fraisInscription);
    }

    public void annulerEvenement(User demandeur, List<User> tousLesUsers) {
        if (organisateur == null || organisateur.getId() != demandeur.getId())
            throw new IllegalStateException("Seul l'organisateur peut annuler.");
        if (annule)
            throw new IllegalStateException("Déjà annulé.");
        if (fraisInscription > 0)
            for (User u : tousLesUsers)
                if (participants.contains(u.getUsername()))
                    u.crediterSolde(fraisInscription);
        this.annule = true;
        participants.clear();
    }

    public boolean estParticipant(String username) {
        return participants.contains(username);
    }

    public int placesRestantes() {
        return capaciteMax - participants.size();
    }

    public void afficherDetails() {
        System.out.println("=== Détails Événement ===");
        System.out.println("ID             : " + id);
        System.out.println("Titre          : " + titre);
        System.out.println("Lieu           : " + lieu);
        System.out.println("Date           : " + date);
        System.out.println("Organisateur   : " + (organisateur != null ? organisateur.getUsername() : "N/A"));
        System.out.println("Participants   : " + participants.size() + "/" + capaciteMax);
        System.out.println("Places restant.: " + placesRestantes());
        System.out.println("Frais          : " + fraisInscription + " DT");
        System.out.println("Statut         : " + (annule ? "ANNULÉ" : "Actif"));
    }

    @Override
    public String toString() {
        return "Event#" + id + " [" + titre + "] à " + lieu
             + " le " + date
             + " | " + participants.size() + "/" + capaciteMax
             + " | " + fraisInscription + " DT"
             + (annule ? " [ANNULÉ]" : "");
    }

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

    public int getCapaciteMax() { return capaciteMax; }
    public void setCapaciteMax(int capaciteMax) { this.capaciteMax = capaciteMax; }

    public double getFraisInscription() { return fraisInscription; }
    public void setFraisInscription(double f) { this.fraisInscription = f; }

    public boolean isAnnule() { return annule; }
    public void setAnnule(boolean annule) { this.annule = annule; }
}
