@echo off
echo Compilation de ClubConnect FSEGT...
mkdir out 2>nul
javac -encoding UTF-8 -d out ^
  src/com/clubconnect/models/authentification/User.java ^
  src/com/clubconnect/models/authentification/Session.java ^
  src/com/clubconnect/models/authentification/AuthService.java ^
  src/com/clubconnect/models/gestionpostes/Post.java ^
  src/com/clubconnect/models/gestionpostes/PostService.java ^
  src/com/clubconnect/models/interaction/InteractionService.java ^
  src/com/clubconnect/models/evenement/Event.java ^
  src/com/clubconnect/models/evenement/EventService.java ^
  src/com/clubconnect/models/admin/AdminService.java ^
  src/com/clubconnect/models/Main.java
if %errorlevel% == 0 (
    echo Compilation reussie !
    echo Execution...
    java -cp out com.clubconnect.models.Main
) else (
    echo Erreur de compilation.
)
