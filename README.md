¤ StreamVOD — Application Android de Streaming

![Android](https://img.shields.io/badge/Android-API%2026+-green) ![Kotlin](https://img.shields.io/badge/Kotlin-1.9-purple) ![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2025-blue)

¤ Membres de l'équipe

| Nom & Prénom | Rôle |
|---|---|
| Rami Noha | Développeuse Backend (Firebase, Cloudinary, Room) |
| Asrih Fatima Azahrae | Développeuse Frontend (UI Compose, Navigation, Communauté) |

---

¤ Description du projet

StreamVOD est une application Android native développée en **Kotlin** avec **Jetpack Compose**. Elle permet aux utilisateurs de découvrir des films et séries via l'API TMDB, de gérer leurs favoris, de consulter leur historique, de recevoir des recommandations personnalisées et de partager des vidéos au sein d'une communauté.

---

¤ Fonctionnalités principales

-  **Authentification** — Inscription / Connexion via Firebase Auth
-  **Accueil** — Catalogue de films & séries (API TMDB)
-  **Recherche** — Recherche en temps réel par titre
-  **Favoris** — Sauvegarde locale avec Room Database
-  **Historique** — Suivi automatique des contenus consultés
-  **Communauté** — Upload et lecture de vidéos entre utilisateurs
-  **Recommandations** — Suggestions personnalisées basées sur les favoris
-  **Profil** — Gestion du compte utilisateur

---

¤ Architecture du projet

com.groupe9.streamvod/

├── data/

│   ├── local/          # Room Database (FavoriteEntity, HistoryEntity, DAOs)

│   ├── remote/         # API TMDB, CloudinaryUploader

│   └── repository/     # Repositories (Auth, Video, Favorite, History, UserVideo)

├── di/                 # Injection de dépendances (Hilt - AppModule)

├── domain/

│   └── model/          # Modèles métier (Video, UserVideo)

└── ui/

├── auth/           # Login, Register

├── community/      # CommunityScreen, StreamPlayerScreen, MyVideos

├── detail/         # Détail d'un film/série

├── favorites/      # Écran Favoris

├── history/        # Écran Historique

├── home/           # Écran Accueil

├── navigation/     # AppNavigation, BottomNavBar

├── player/         # Lecteur YouTube

├── profile/        # Écran Profil

├── search/         # Écran Recherche

└── theme/          # Couleurs, typographie, thème

**Pattern** : MVVM (Model-View-ViewModel)

---

¤ Dépendances principales

| Dépendance | Version | Usage |
|---|---|---|
| Jetpack Compose BOM | 2025.05.00 | UI déclarative |
| Firebase Auth | BOM 33.1.0 | Authentification |
| Firebase Firestore | BOM 33.1.0 | Base de données cloud |
| Firebase Storage | BOM 33.1.0 | Stockage fichiers |
| Room | 2.6.1 | Base de données locale |
| Hilt | 2.51.1 | Injection de dépendances |
| Retrofit | 2.9.0 | Appels API REST |
| ExoPlayer (Media3) | 1.4.1 | Lecture vidéo |
| Coil | 2.6.0 | Chargement d'images |
| Cloudinary | - | Upload vidéo cloud |
| TMDB API | - | Catalogue films/séries |

¤ Configuration requise

Créer un fichier `local.properties` à la racine du projet et y ajouter :
TMDB_API_KEY=db5ab02bd48fcf4faa542c554adcdcb2

CLOUDINARY_CLOUD_NAME=dp4pmmir1

CLOUDINARY_UPLOAD_PRESET=streamvod_unsigned

¤ Installation & Lancement

1. Cloner le projet :
```bash
git clone https://github.com/noharami-devops/Projet-Android-StreamVOD.git
```

2. Ouvrir le projet dans **Android Studio**

3. Configurer `local.properties` avec les clés API (voir section ci-dessus)

4. Synchroniser Gradle : **File → Sync Project with Gradle Files**

5. Lancer sur un émulateur ou téléphone Android **(API 26 minimum)**

¤ Captures d'écran

| Écran | Capture | Description |
|---
 * Accueil | <img width="574" height="1280" alt="image" src="https://github.com/user-attachments/assets/0d908294-c038-4f1b-a769-805b28576042" /> | Catalogue de films et séries via API TMDB |
|
* Recherche | <img width="574" height="1280" alt="image" src="https://github.com/user-attachments/assets/f3326b98-4de8-4f4c-8fec-89e542251b04" />
 | Recherche en temps réel par titre |
|
* Communauté | <img width="574" height="1280" alt="image" src="https://github.com/user-attachments/assets/0a5602aa-589b-4fba-b0d7-710102b964ab" /> | Feed de vidéos avec bouton upload
|
* Favoris | <img width="574" height="1280" alt="image" src="https://github.com/user-attachments/assets/7048af07-65bd-434b-91f6-e37fa066bce1" /> | Films et séries sauvegardés localement |
|
* Historique | <img width="574" height="1280" alt="image" src="https://github.com/user-attachments/assets/e35cd429-bc17-4dcc-bd47-b09b0fdbc54a" /> | Contenus consultés récemment |
|
* Profil | <img width="720" height="1604" alt="image" src="https://github.com/user-attachments/assets/9686450b-60ef-4390-9c78-8c490e5ff5f0" /> | Gestion du compte utilisateur |

¤ Prérequis

- Android Studio Hedgehog ou version plus récente
- JDK 11+
- Téléphone ou émulateur Android API 26+
- Connexion internet (pour Firebase et TMDB)

¤ Fichiers importants

| Fichier | Description |
|---|---|
| `google-services.json` | Configuration Firebase (présent dans le projet) |
| `local.properties` | Clés API locales (à configurer manuellement) |
| `app/build.gradle.kts` | Dépendances et configuration du build |
| `AndroidManifest.xml` | Permissions et configuration de l'app |
