import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *  DOCUMENTATION TP3 — Approche IA assistée
 * ============================================================
 *
 * MÉTHODE : creerEvenement()
 * --------------------------------
 * Prompt utilisé :
 *   "Implémente creerEvenement() dans EventService. Elle doit valider que
 *    le titre n'est pas vide, que la capacité est entre 1 et 200, que les
 *    frais sont positifs ou nuls (en DT), et que l'utilisateur est connecté.
 *    Lance des IllegalArgumentException pour les données invalides."
 *
 * Code généré par l'IA :
 *   public Event creerEvenement(String titre, String lieu, String date,
 *                               User organisateur, int capacite, double frais) {
 *       if (titre == null || titre.isBlank())
 *           throw new IllegalArgumentException("Titre obligatoire.");
 *       if (capacite < 1 || capacite > 200)
 *           throw new IllegalArgumentException("Capacité invalide.");
 *       if (frais < 0)
 *           throw new IllegalArgumentException("Frais invalides.");
 *       Event e = new Event(nextId++, titre, lieu, date, organisateur, capacite, frais);
 *       evenements.add(e);
 *       return e;
 *   }
 *
 * Corrections humaines :
 *   - Ajout validation lieu et date non vides
 *   - Ajout vérification organisateur non null
 * ============================================================
 *
 * MÉTHODE : participer()
 * --------------------------------
 * Prompt utilisé :
 *   "Implémente participer(int eventId, User user) dans EventService.
 *    Elle doit retrouver l'événement par id, déléguer la logique d'inscription
 *    à Event.ajouterParticipant(), et propager les exceptions métier."
 *
 * Code généré par l'IA :
 *   public void participer(int eventId, User user) {
 *       Event e = trouverParId(eventId);
 *       e.ajouterParticipant(user);
 *   }
 *
 * Corrections humaines :
 *   - Extraction de trouverParId() comme méthode privée réutilisable
 *   - Ajout de annulerParticipation() avec la même logique
 * ============================================================
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

    // ============================================================
    //  MÉTHODES MÉTIER — Logique réelle
    // ============================================================

    /**
     * CF-12 : Créer un événement avec validation complète.
     */
    public Event creerEvenement(String titre, String lieu, String date,
                                User organisateur, int capaciteMax, double fraisInscription) {
        if (titre == null || titre.isBlank())
            throw new IllegalArgumentException("Le titre de l'événement est obligatoire.");
        if (lieu == null || lieu.isBlank())
            throw new IllegalArgumentException("Le lieu de l'événement est obligatoire.");
        if (date == null || date.isBlank())
            throw new IllegalArgumentException("La date de l'événement est obligatoire.");
        if (organisateur == null)
            throw new IllegalArgumentException("L'organisateur est obligatoire.");
        if (capaciteMax < 1 || capaciteMax > 200)
            throw new IllegalArgumentException("La capacité doit être entre 1 et 200.");
        if (fraisInscription < 0)
            throw new IllegalArgumentException("Les frais d'inscription ne peuvent pas être négatifs.");

        Event event = new Event(nextId++, titre, lieu, date, organisateur, capaciteMax, fraisInscription);
        evenements.add(event);
        return event;
    }

    /**
     * Surcharge sans frais ni capacité (valeurs par défaut).
     */
    public Event creerEvenement(String titre, String lieu, String date, User organisateur) {
        return creerEvenement(titre, lieu, date, organisateur, 50, 0.0);
    }

    /**
     * CF-13 : Inscrire un utilisateur à un événement.
     * Délègue la logique métier à Event.ajouterParticipant().
     */
    public void participer(int eventId, User user) {
        Event event = trouverParId(eventId);
        event.ajouterParticipant(user);
    }

    /**
     * CF-13b : Annuler la participation d'un utilisateur.
     * Délègue la logique métier à Event.annulerParticipation().
     */
    public void annulerParticipation(int eventId, User user) {
        Event event = trouverParId(eventId);
        event.annulerParticipation(user);
    }

    /**
     * Annuler un événement entier (organisateur uniquement).
     * Rembourse tous les participants inscrits.
     */
    public void annulerEvenement(int eventId, User demandeur, List<User> tousLesUsers) {
        Event event = trouverParId(eventId);
        event.annulerEvenement(demandeur, tousLesUsers);
    }

    /**
     * Rechercher les événements par lieu.
     */
    public List<Event> rechercherParLieu(String lieu) {
        List<Event> resultats = new ArrayList<>();
        for (Event e : evenements) {
            if (e.getLieu().equalsIgnoreCase(lieu) && !e.isAnnule()) {
                resultats.add(e);
            }
        }
        return resultats;
    }

    /**
     * Afficher tous les événements actifs.
     */
    public void afficherDetails() {
        boolean aucun = true;
        for (Event e : evenements) {
            if (!e.isAnnule()) {
                System.out.println(e);
                aucun = false;
            }
        }
        if (aucun) System.out.println("Aucun événement actif.");
    }

    // --- Méthode privée utilitaire ---
    private Event trouverParId(int id) {
        for (Event e : evenements) {
            if (e.getId() == id) return e;
        }
        throw new IllegalArgumentException("Événement #" + id + " introuvable.");
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
