package com.clubconnect.models.admin;

import com.clubconnect.models.authentification.AuthService;
import com.clubconnect.models.authentification.Session;
import com.clubconnect.models.authentification.User;
import com.clubconnect.models.gestionpostes.PostService;
import java.util.List;

public class AdminService {

    public AdminService() { }

    public static void voirUtilisateurs() {
        verifierAdmin();
        List<User> membres = AuthService.users;
        System.out.println("=== Liste des membres (" + membres.size() + ") ===");
        if (membres.isEmpty()) { System.out.println("  Aucun membre inscrit."); return; }
        for (User u : membres)
            System.out.println("  - " + u.getUsername() + " | " + u.getEmail()
                + " | role=" + u.getRole() + " | solde=" + u.getSolde() + " DT");
    }

    public static void supprimerPost(int postId) {
        verifierAdmin();
        PostService.supprimerPost(postId);
    }

    public static void supprimerMembre(int membreId) {
        verifierAdmin();
        User admin = Session.currentUser;
        if (admin != null && admin.getId() == membreId)
            throw new IllegalStateException("Un admin ne peut pas supprimer son propre compte.");
        User cible = trouverMembre(membreId);
        AuthService.users.remove(cible);
    }

    public static void changerRole(int membreId, String nouveauRole) {
        verifierAdmin();
        if (!"user".equals(nouveauRole) && !"admin".equals(nouveauRole))
            throw new IllegalArgumentException("Role invalide.");
        User cible = trouverMembre(membreId);
        if ("user".equals(nouveauRole) && "admin".equals(cible.getRole())) {
            long nbAdmins = AuthService.users.stream().filter(u -> "admin".equals(u.getRole())).count();
            if (nbAdmins <= 1)
                throw new IllegalStateException("Impossible : ce membre est le dernier administrateur.");
        }
        cible.setRole(nouveauRole);
    }

    public static void afficherDetails() {
        verifierAdmin();
        User admin = Session.currentUser;
        double soldeTotalMembres = AuthService.users.stream().mapToDouble(User::getSolde).sum();
        long nbAdmins = AuthService.users.stream().filter(u -> "admin".equals(u.getRole())).count();
        System.out.println("=== Tableau de bord Admin ===");
        System.out.println("Connecte en tant que  : " + (admin != null ? admin.getUsername() : "N/A"));
        System.out.println("Membres inscrits      : " + AuthService.users.size());
        System.out.println("Dont administrateurs  : " + nbAdmins);
        System.out.println("Solde total membres   : " + String.format("%.3f", soldeTotalMembres) + " DT");
    }

    @Override
    public String toString() {
        return "AdminService | session=" + (Session.isAdmin() ? "admin actif" : "non admin");
    }

    private static void verifierAdmin() {
        if (!Session.isAdmin()) throw new IllegalStateException("Droits administrateur requis.");
    }

    private static User trouverMembre(int id) {
        for (User u : AuthService.users) if (u.getId() == id) return u;
        throw new IllegalArgumentException("Membre#" + id + " introuvable.");
    }
}
