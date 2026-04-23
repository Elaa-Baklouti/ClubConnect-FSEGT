import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *  DOCUMENTATION TP3 — Approche IA assistée
 * ============================================================
 *
 * MÉTHODE : ajouterParticipant()
 * --------------------------------
 * Prompt utilisé :
 *   "Implémente la méthode ajouterParticipant(User user) pour un événement
 *    ClubConnect. Elle doit vérifier que l'utilisateur n'est pas déjà inscrit,
 *    que l'événement n'est pas complet (max 50 participants), et que
 *    l'organisateur ne peut pas s'inscrire lui-même. Lance des exceptions
 *    métier si une règle est violée."
 *
 * Code généré par l'IA :
 *   public void ajouterParticipant(User user) {
 *       if (participants.contains(user.getUsername()))
 *           throw new IllegalStateException("Déjà inscrit.");
 *       if (participants.size() >= 50)
 *           throw new IllegalStateException("Événement complet.");
 *       participants.add(user.getUsername());
 *   }
 *
 * Corrections humaines :
 *   - Ajout de la vérification organisateur != participant
 *   - Ajout du frais d'inscription en DT
 *   - Ajout du champ capaciteMax configurable (pas hardcodé à 50)
 * ============================================================
 *
 * MÉTHODE : annulerParticipation()
 * --------------------------------
 * Prompt utilisé :
 *   "Implémente annulerParticipation(User user) qui retire un participant
 *    d'un événement. Vérifie que l'utilisateur est bien inscrit avant
 *    de le retirer et rembourse les frais d'inscription."
 *
 * Code généré par l'IA :
 *   public void annulerParticipation(User user) {
 *       if (!participants.contains(user.getUsername()))
 *           throw new IllegalStateException("Non inscrit.");
 *       participants.remove(user.getUsername());
 *   }
 *
 * Corrections humaines :
 *   - Ajout du remboursement du frais en DT sur le solde User
 *   - Ajout du message de confirmation avec montant remboursé
 * ============================================================
 */
public class Event {

    // --- Champs privés ---
    private int id;
    private String titre;
    private String lieu;
    private String date;
    private User organisateur;
    private List<String> participants;
    private int capaciteMax;
    private double fraisInscription; // en DT (Dinar Tunisien)
    private boolean annule;

    // --- Constructeur vide ---
    public Event() {
        this.participants  = new ArrayList<>();
        this.capaciteMax   = 50;
        this.fraisInscription = 0.0;
        this.annule        = false;
    }

    // --- Constructeur complet ---
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

    // --- Constructeur simplifié (sans frais) ---
    public Event(int id, String titre, String lieu, String date, User organisateur) {
        this(id, titre, lieu, date, organisateur, 50, 0.0);
    }

    // ============================================================
    //  MÉTHODES MÉTIER — Logique réelle
    // ============================================================

    /**
     * CF-13 : Inscrire un participant à l'événement.
     * Règles :
     *  - L'événement ne doit pas être annulé
     *  - L'organisateur ne peut pas s'inscrire lui-même
     *  - L'utilisateur ne doit pas être déjà inscrit
     *  - La capacité max ne doit pas être atteinte
     *  - Débite les frais d'inscription du solde de l'utilisateur
     */
    public void ajouterParticipant(User user) {
        if (annule)
            throw new IllegalStateException("Impossible de s'inscrire : l'événement est annulé.");

        if (organisateur != null && organisateur.getId() == user.getId())
            throw new IllegalStateException("L'organisateur ne peut pas s'inscrire à son propre événement.");

        if (participants.contains(user.getUsername()))
            throw new IllegalStateException(user.getUsername() + " est déjà inscrit à cet événement.");

        if (participants.size() >= capaciteMax)
            throw new IllegalStateException("L'événement est complet (" + capaciteMax + " participants max).");

        if (fraisInscription > 0) {
            if (user.getSolde() < fraisInscription)
                throw new IllegalStateException("Solde insuffisant. Frais : " + fraisInscription + " DT.");
            user.debiterSolde(fraisInscription);
        }

        participants.add(user.getUsername());
    }

    /**
     * CF-13b : Annuler la participation d'un utilisateur.
     * Rembourse les frais d'inscription si applicable.
     */
    public void annulerParticipation(User user) {
        if (!participants.contains(user.getUsername()))
            throw new IllegalStateException(user.getUsername() + " n'est pas inscrit à cet événement.");

        participants.remove(user.getUsername());

        if (fraisInscription > 0) {
            user.crediterSolde(fraisInscription);
        }
    }

    /**
     * Annuler l'événement entier (réservé à l'organisateur).
     * Rembourse tous les participants.
     */
    public void annulerEvenement(User demandeur, List<User> tousLesUsers) {
        if (organisateur == null || organisateur.getId() != demandeur.getId())
            throw new IllegalStateException("Seul l'organisateur peut annuler l'événement.");

        if (annule)
            throw new IllegalStateException("L'événement est déjà annulé.");

        // Rembourser chaque participant
        if (fraisInscription > 0) {
            for (User u : tousLesUsers) {
                if (participants.contains(u.getUsername())) {
                    u.crediterSolde(fraisInscription);
                }
            }
        }

        this.annule = true;
        participants.clear();
    }

    /**
     * Vérifie si un utilisateur est déjà inscrit.
     */
    public boolean estParticipant(String username) {
        return participants.contains(username);
    }

    /**
     * Retourne le nombre de places restantes.
     */
    public int placesRestantes() {
        return capaciteMax - participants.size();
    }

    /**
     * Afficher les détails complets de l'événement.
     */
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

    // --- toString ---
    @Override
    public String toString() {
        return "Event#" + id + " [" + titre + "] à " + lieu
             + " le " + date
             + " | " + participants.size() + "/" + capaciteMax
             + " | " + fraisInscription + " DT"
             + (annule ? " [ANNULÉ]" : "");
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

    public int getCapaciteMax() { return capaciteMax; }
    public void setCapaciteMax(int capaciteMax) { this.capaciteMax = capaciteMax; }

    public double getFraisInscription() { return fraisInscription; }
    public void setFraisInscription(double fraisInscription) { this.fraisInscription = fraisInscription; }

    public boolean isAnnule() { return annule; }
    public void setAnnule(boolean annule) { this.annule = annule; }
}
