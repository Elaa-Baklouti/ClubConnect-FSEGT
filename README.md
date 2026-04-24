# ClubConnect FSEGT

## Pitch
ClubConnect est une application Java de gestion de club étudiant développée dans le cadre du cours AGL (Analyse et Génie Logiciel) à la FSEGT. Elle permet aux membres de s'inscrire, publier des posts, interagir, participer à des événements et être gérés par un administrateur.

## Membres de l'équipe

| Membre | Fonctionnalité | Branche |
|--------|---------------|---------|
| Elaa   | Authentification (CF-1, CF-2) | feature/authentification |
| Eya    | Gestion des postes (CF-4, CF-5) | feature/gestionpostes |
| Ines   | Interaction (CF-10, CF-11) | feature/interaction |
| Ons    | Événement (CF-12, CF-13) | feature/evenement |
| Lamis  | Admin (CF-14, CF-15) | feature/admin |

## Fonctionnalités

- **CF-1** : Inscription d'un membre
- **CF-2** : Connexion / Déconnexion
- **CF-4** : Créer un post
- **CF-5** : Voir les posts
- **CF-10** : Commenter un post
- **CF-11** : Liker un post
- **CF-12** : Créer un événement
- **CF-13** : Participer à un événement
- **CF-14** : Voir tous les utilisateurs (admin)
- **CF-15** : Supprimer un post (admin)

## Structure du projet

```
ClubConnect-FSEGT/
├── README.md
├── docs/
├── diagrammes/
│   ├── DCU_ClubConnect.puml
│   ├── DC_ClubConnect.puml
│   └── DS_*.puml
├── src/
│   └── com/clubconnect/models/
│       ├── Main.java
│       ├── authentification/
│       ├── gestionpostes/
│       ├── interaction/
│       ├── evenement/
│       └── admin/
└── tests/
```

## Compilation et exécution

```bash
javac -encoding UTF-8 -d out src/com/clubconnect/models/authentification/*.java src/com/clubconnect/models/gestionpostes/*.java src/com/clubconnect/models/interaction/*.java src/com/clubconnect/models/evenement/*.java src/com/clubconnect/models/admin/*.java src/com/clubconnect/models/Main.java
java -cp out com.clubconnect.models.Main
```
