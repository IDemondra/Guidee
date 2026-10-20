# Guide Me — Services de Proximité & Transport inDrive (Madagascar)

Application mobile moderne, fluide et performante développée avec **Jetpack Compose** et **Material Design 3**, spécialement optimisée pour le contexte malagasy (Antananarivo, Tamatave, Majunga, Diego, Antsirabe).

L'ensemble des données fonctionne en **mode 100% statique et offline** (aucun serveur distant requis), garantissant une exécution immédiate, fluide et résiliente même en cas de coupure de réseau.

---

## Fonctionnalités Principales

### 1. Découverte de Services & Carte Interactive OpenStreetMap
* **Intégration OpenStreetMap (OSMDroid)** avec rendu des tuiles officielles d'Antananarivo (Analakely, Lac Anosy, Ankorondrano, Isoraka).
* **Bascule instantanée** entre OpenStreetMap direct et Carte Vectorielle Stylisée Hors-Ligne.
* **Pins par catégorie** avec code couleur distinct (Restaurants, Hôtels, Pharmacies de garde, Commerces, Banques / Stations / Cliniques).
* **Cercle de rayon configurable** avec pulsation GPS utilisateur : 500 m, 1 km, 2 km, 5 km, 10 km.
* **Fiche établissement Bottom Sheet** avec statut ouvert/fermé, horaires, itinéraire, appel direct et commande de course inDrive.

### 2. Module Transport inDrive (Négociation & Prix Proposé)
* **Proposition de tarif par le passager** : Slider et saisie libre en Ariary (Ar), sans prix imposé.
* **Choix du type de véhicule** : Voiture (4 places), Moto / Tuk-Tuk, Livraison / Colis.
* **Radar de recherche** & réception des offres des chauffeurs en temps réel.
* **Négociation interactive** : Accepter, Refuser ou Contre-proposer un prix au chauffeur.
* **Suivi de course en direct** avec étapes progressives (Chauffeur en route → Arrivé → En cours → Terminé), bouton SOS urgence Madagascar (117, 119).
* **Paiement & Reçu** : MVola (Telma), Orange Money, Airtel Money, Espèces, Carte bancaire, notation 1 à 5 étoiles et téléchargement de reçu PDF.

### 3. Multi-Rôles Intégré
* **Mode Client / Passager** : Exploration, commandes de transport, historique et favoris.
* **Mode Chauffeur inDrive** : Tableau de bord, switch En ligne / Hors ligne, gains du jour en Ariary, réception des courses avec propositions de prix des passagers, retrait Mobile Money (MVola).
* **Mode Prestataire / Commerce** : Gestion de fiche établissement, statut ouvert/fermé, gestion des services et tarifs, statistiques d'impact (vues, appels, itinéraires, favoris).

### 4. Respect Strict de l'Iconographie & Design System
* **Aucun emoji dans toute l'interface** : Utilisation exclusive d'icônes vectorielles cohérentes (`Material Symbols / Icons`).
* **Design System M3** : Palette locale inspirée de Madagascar (Vert Forêt `#006C4C`, Ambre / Terre rouge `#B45309`, Cyan Océan `#006874`, Jaune MVola `#F9A825`, Orange Money `#FF6D00`).
* **Support bilingue** : Bascule instantanée Français / Malagasy.
* **Thème Sombre & Thème Clair**.

---

## Architecture du Code

```
com.example/
├── MainActivity.kt                      # Point d'entrée avec edge-to-edge
├── MadaProxiApp.kt                      # Orchestrateur de navigation & BackHandler
├── model/
│   └── Models.kt                       # Modèles de données (Establishment, RideRequest, DriverOffer, etc.)
├── data/
│   └── StaticData.kt                   # Référentiel de données statiques et AppRepository StateFlow
├── ui/
│   ├── theme/
│   │   ├── Color.kt                    # Palette M3 personnalisée (Madagascar brand)
│   │   ├── Theme.kt                    # MadaProxiTheme clair/sombre
│   │   └── Type.kt                     # Typographie Material 3
│   ├── components/
│   │   ├── CommonComponents.kt         # AppButton, AppCard, AppChip, AppIcon, AppInput, RatingBar
│   │   └── MapCanvas.kt                # Rendu Canvas haute fidélité d'Antananarivo
│   └── screens/
│       ├── SplashScreen.kt             # Splash & Onboarding 4 slides avec permission GPS
│       ├── AuthScreen.kt               # Connexion, Inscription, OTP SMS Madagascar (+261)
│       ├── HomeScreen.kt               # Écran central avec AppBar, recherche, chips, carrousel, bannière inDrive
│       ├── MapScreen.kt                # Carte interactive, filtres, sélecteur de rayon & bottom sheet
│       ├── TransportScreen.kt          # Module inDrive complet (Demande, Bids, Tracking, Reçu)
│       ├── EstablishmentDetailScreen.kt# Fiche complète, avis clients, actions rapides
│       ├── DriverDashboardScreen.kt    # Vue Chauffeur inDrive (Gains, Demandes, Retraits)
│       ├── MerchantDashboardScreen.kt  # Vue Commerce & Prestataire (Fiche, Services, Stats)
│       ├── HistoryScreen.kt            # Historique des courses & services visités
│       ├── ProfileScreen.kt            # Profil, préférences FR/MG, thème, adresses enregistrées
│       ├── NotificationsScreen.kt      # Centre de notifications en temps réel
│       └── FavoritesScreen.kt          # Liste des établissements favoris
```

---

## Instructions de Compilation & Exécution

L'application est prête à être exécutée dans l'environnement Android Studio :

```bash
# Compilation
gradle assembleDebug

# Lancement des tests unitaires et Robolectric
gradle :app:testDebugUnitTest
```
