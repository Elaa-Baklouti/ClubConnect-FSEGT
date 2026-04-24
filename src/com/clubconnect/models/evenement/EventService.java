package com.clubconnect.models.evenement;

import com.clubconnect.models.authentification.User;
import java.util.ArrayList;
import java.util.List;

public class EventService {

    private List<Event> evenements;
    private int nextId;

    public EventService() {
        this.evenements = new ArrayList<>();
        this.nextId     = 1;
    }

    public Event creerEvenement(String titre, String lieu, String date,
                                User organisateur, int capaciteMax, double fraisInscription) {
        if (titre == null || titre.isBlank()) throw new IllegalArgumentException("Titre obligatoire.");
        if (lieu == null || lieu.isBlank()) throw new IllegalArgumentException("Lieu obligatoire.");
        if (date == null || date.isBlank()) throw new IllegalArgumentException("Date obligatoire.");
        if (organisateur == null) throw new IllegalArgumentException("Organisateur obligatoire.");
        if (capaciteMax < 1 || capaciteMax > 200) throw new IllegalArgumentException("Capacite entre 1 et 200.");
        if (fraisInscription < 0) throw new IllegalArgumentException("Frais ne peuvent pas etre negatifs.");
        Event event = new Event(nextId++, titre, lieu, date, organisateur, capaciteMax, fraisInscription);
        evenements.add(event);
        return event;
    }

    public Event creerEvenement(String titre, String lieu, String date, User organisateur) {
        return creerEvenement(titre, lieu, date, organisateur, 50, 0.0);
    }

    public void participer(int eventId, User user) { trouverParId(eventId).ajouterParticipant(user); }
    public void annulerParticipation(int eventId, User user) { trouverParId(eventId).annulerParticipation(user); }
    public void annulerEvenement(int eventId, User demandeur, List<User> tousLesUsers) {
        trouverParId(eventId).annulerEvenement(demandeur, tousLesUsers);
    }

    public List<Event> rechercherParLieu(String lieu) {
        List<Event> resultats = new ArrayList<>();
        for (Event e : evenements)
            if (e.getLieu().equalsIgnoreCase(lieu) && !e.isAnnule()) resultats.add(e);
        return resultats;
    }

    public void afficherDetails() {
        boolean aucun = true;
        for (Event e : evenements) { if (!e.isAnnule()) { System.out.println(e); aucun = false; } }
        if (aucun) System.out.println("Aucun evenement actif.");
    }

    private Event trouverParId(int id) {
        for (Event e : evenements) if (e.getId() == id) return e;
        throw new IllegalArgumentException("Evenement #" + id + " introuvable.");
    }

    @Override public String toString() { return "EventService | evenements=" + evenements.size(); }
    public List<Event> getEvenements() { return evenements; }
    public void setEvenements(List<Event> evenements) { this.evenements = evenements; }
}
