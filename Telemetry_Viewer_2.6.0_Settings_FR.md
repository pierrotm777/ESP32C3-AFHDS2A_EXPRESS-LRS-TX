# Telemetry Viewer 2.6.0 – Guide des paramètres

Ce document décrit les options actuellement présentes dans **Settings** de Telemetry Viewer 2.6.0 avec le support **AFHDS2A**.

> **Remarque AFHDS2A**
>
> AFHDS2A ne nécessite pas de réglage particulier dans cet écran. Le protocole est détecté automatiquement à la connexion, de la même manière que CRSF, FrSky S.PORT, GHST, MAVLink, etc.

---

## 1. Paramètres généraux

### Telemetry logging RAW

**Valeur par défaut : activé**

Enregistre le flux de télémétrie brut dans un fichier `.tlm`.

Les fichiers sont placés dans le dossier public :

```text
TelemetryLogs
```

Ces fichiers peuvent ensuite être relus avec la fonction de **Playback** de Telemetry Viewer.

C'est le format le plus utile pour rejouer exactement les données reçues.

---

### Telemetry logging to CSV

**Valeur par défaut : activé**

Enregistre les données de télémétrie décodées dans un fichier `.csv`, au format proche de celui d'OpenTX/EdgeTX.

Le fichier CSV est également utilisé pendant une relecture pour retrouver notamment :

- l'heure du vol ;
- la position enregistrée de l'opérateur ;
- les données télémétriques converties.

Les fichiers sont enregistrés dans le dossier `TelemetryLogs`.

---

### Location while the app is away

Indique si Telemetry Viewer est autorisé à connaître la position GPS du téléphone lorsque l'application n'est pas au premier plan ou lorsque l'écran est éteint.

Cette autorisation est utile si **Record operator position** est activé.

Les états possibles sont essentiellement :

- **non autorisé** : la position de l'opérateur ne peut pas être enregistrée ;
- **autorisé seulement pendant l'utilisation** : l'enregistrement peut s'arrêter lorsque l'application passe en arrière-plan ;
- **autorisé tout le temps** : la position de l'opérateur peut continuer à être enregistrée pendant tout le vol.

Un appui sur cette ligne ouvre les réglages Android nécessaires.

---

### Record operator position

**Valeur par défaut : activé**  
**Dépend de : Telemetry logging to CSV**

Ajoute dans le CSV la position du téléphone de l'opérateur :

- latitude ;
- longitude ;
- précision GPS ;
- orientation du téléphone lorsqu'elle est disponible.

Lors d'un Playback, Telemetry Viewer peut alors afficher la position où se trouvait réellement l'opérateur pendant le vol.

---

### Background compass

**Valeur par défaut : activé**  
**Dépend de : Record operator position**

Continue à enregistrer l'orientation du téléphone lorsque l'écran est éteint ou que l'application est en arrière-plan.

Ceci permet de conserver la direction de l'opérateur dans le fichier de vol.

---

### Lock screen orientation

**Valeur par défaut : No lock**

Permet de forcer l'orientation de l'écran :

| Réglage | Effet |
|---|---|
| No lock | Android choisit automatiquement |
| Portrait | Mode portrait |
| Landscape | Mode paysage |
| Reverse Landscape | Paysage inversé |

---

### Automatically reconnect a lost radio link

**Valeur par défaut : activé**

Après une coupure de liaison **Bluetooth Classic, BLE ou USB série**, l'application essaie de se reconnecter automatiquement pendant environ une minute.

Pour l'USB, si le câble ou l'adaptateur revient, l'application essaie de reprendre le même vol et le même enregistrement.

Pour notre ESP32-C3 en **BLE**, il est conseillé de laisser cette option activée.

---

### Automatically reconnect lost network connection

**Valeur par défaut : activé**

Même principe, mais pour une télémétrie reçue par **Wi-Fi TCP/UDP**.

Cette option est indépendante de la reconnexion Bluetooth/BLE/USB.

---

### Voice messages on connection status changes

**Valeur par défaut : activé**

Active les annonces vocales lors des changements d'état de la connexion, par exemple connexion ou déconnexion.

---

# 2. Map markers

## Model

**Valeur par défaut : Quad**

Choisit la représentation du modèle sur la carte et dans la vue 3D :

- Quad ;
- Plane or wing ;
- Helicopter.

Ce choix ne change pas le protocole de télémétrie.

---

## Model color

Choisit la couleur du modèle affiché sur la carte et dans la vue 3D.

---

## Show clock

**Valeur par défaut : activé**

Affiche l'heure :

- heure actuelle pendant un vol réel ;
- heure enregistrée pendant un Playback.

---

## My position color

Choisit la couleur de la flèche représentant la position actuelle du téléphone.

---

## Show home line

**Valeur par défaut : activé**

Trace une ligne entre le modèle et la position actuelle du téléphone.

Elle est utile pour visualiser rapidement la direction et la distance vers le modèle, par exemple pour retrouver un modèle posé ou perdu.

---

## Home line color

Choisit la couleur et éventuellement la transparence de la ligne entre le modèle et le téléphone.

Cette option dépend de **Show home line**.

---

## Show recorded operator

**Valeur par défaut : activé**

Pendant un Playback, affiche la position de l'opérateur enregistrée au moment du vol.

Elle est distincte de la position actuelle du téléphone.

---

## Recorded operator color

Choisit la couleur utilisée pour la position enregistrée de l'opérateur.

---

## Show operator line

**Valeur par défaut : activé**

Pendant un Playback, trace une ligne entre le modèle et la position enregistrée de l'opérateur.

---

## Operator line color

Choisit la couleur et la transparence de cette ligne.

---

## Show heading line

**Valeur par défaut : activé**

Affiche devant le modèle une ligne indiquant son cap.

Elle utilise les informations de cap disponibles dans la télémétrie.

---

## Heading line color

Choisit la couleur de la ligne de cap.

---

## Loading tiles

**Valeur par défaut : activé**

Dans la vue 3D, affiche une petite grille indiquant progressivement le chargement des tuiles de terrain.

Cela n'a aucun effet sur la télémétrie.

---

# 3. Route line

## Route line color

Choisit la couleur de la trace représentant le trajet parcouru par le modèle sur la carte.

---

# 4. Flight plans

## Show flight plans

**Valeur par défaut : activé**

Affiche sur la carte les plans de vol importés.

---

## Import flight plan

Permet d'importer un fichier CSV contenant des points de passage.

Le format attendu est basé sur des coordonnées :

```text
latitude,longitude
```

avec un point de passage par ligne.

---

## Manage flight plans

Permet de gérer les plans de vol déjà importés :

- afficher ou masquer un plan ;
- changer sa couleur ;
- le supprimer.

---

# 5. USB Serial

## Baudrate

**Valeur par défaut : 57600 bauds**

Définit la vitesse du port série lorsqu'une télémétrie est reçue par USB.

Valeurs proposées :

```text
9600
19200
38400
57600
115200
230400
400000
420000
460800
921600
```

Ce réglage n'a aucun effet sur le BLE.

---

# 6. Playback

## Automatic playback start

**Valeur par défaut : activé**

Démarre automatiquement la lecture après avoir chargé un fichier de log.

Si cette option est désactivée, le fichier est chargé mais la lecture attend une action de l'utilisateur.

---

# 7. Sensors

## Sensor display settings

Ouvre l'écran de configuration des widgets de télémétrie.

Il permet de choisir les informations visibles et leur position dans les zones haute et basse de l'écran.

Parmi les widgets disponibles dans la version 2.6.0 :

```text
Satellites
Battery
Voltage
Amperage
Rssi
Uplink / Downlink SNR
Uplink / Downlink LQ
CRSF Rate
Active Antenna
Uplink Power
RSSI antennes
Speed
Distance
Traveled Distance
Altitude
AirSpeed
Vertical Speed
Cell Voltage
Altitude above MSL
Throttle
Telemetry rate
RC Channels
Protocol
Phone Battery
```

Tous les protocoles ne fournissent pas nécessairement toutes ces données. Par exemple, l'affichage dépend des capteurs et des informations effectivement reçus.

---

## Battery units

**Valeur par défaut : mAh**

Choisit la façon dont la capacité de batterie est affichée :

| Option | Signification |
|---|---|
| Percentage | Pourcentage |
| mAh | Milliampères-heures |
| mWh | Milliwatt-heures |

---

## Voltage reported by telemetry

**Valeur par défaut : Battery**

Indique à l'application si la tension reçue correspond :

- à la **tension totale du pack** (`Battery`) ;
- à la **tension d'une cellule** (`Cell`).

Ce réglage est principalement prévu pour CRSF et FrSky.

Lorsque `Cell` est sélectionné, le réglage **Battery cells** devient inutile et est désactivé.

---

## Battery cells

**Valeur par défaut : Auto**

Utilisé lorsque la télémétrie fournit la tension totale du pack.

L'application divise cette tension par le nombre de cellules afin de calculer une tension par cellule.

Choix disponibles :

```text
Auto
1S 2S 3S 4S 5S 6S 7S 8S
10S 12S 14S 16S
```

Le mode **Auto** essaie de déterminer automatiquement le nombre de cellules. Un choix manuel est préférable si la batterie est déjà fortement déchargée au moment de la connexion.

---

## Show artificial horizon view

**Valeur par défaut : activé**

Affiche l'horizon artificiel lorsque le protocole fournit les informations d'attitude, notamment roulis et tangage.

Avec AFHDS2A, cet affichage fonctionne uniquement si les informations d'attitude sont effectivement transmises.

La mention `frsky_pitch_roll` dans le texte d'origine concerne surtout INAV/FrSky.

---

# 8. FlightRadar24 Nearby Aircraft

Ces options concernent l'affichage d'avions réels à proximité du modèle sur la carte.

Elles nécessitent une connexion Internet et n'interviennent pas dans la liaison AFHDS2A.

## Show nearby aircraft

**Valeur par défaut : désactivé**

Active l'affichage des avions récupérés depuis FlightRadar24.

Cette fonction est utilisée uniquement en direct, pas pendant la relecture d'un ancien vol.

---

## Search radius

**Valeur par défaut : 50 km**

Rayon autour de la zone de vol dans lequel les avions sont recherchés.

Choix : 10, 25, 50, 100 ou 200 km.

---

## Display altitude ceiling

**Valeur par défaut : 3000 m**

Masque les avions volant au-dessus de cette altitude.

---

## Warning altitude ceiling

**Valeur par défaut : 1500 m**

Les avertissements de proximité ne concernent que les avions situés sous cette altitude.

L'application empêche cette valeur d'être supérieure à **Display altitude ceiling**.

---

## Warning distance

**Valeur par défaut : 5000 m**

Définit la distance à partir de laquelle un avion réel proche peut déclencher un avertissement.

La distance est calculée par rapport au modèle lorsqu'il est localisé, ou par rapport au téléphone lorsque le modèle ne peut pas être placé sur la carte.

---

## Poll interval

**Valeur par défaut : 10 secondes**

Détermine la fréquence de mise à jour des positions des avions FlightRadar24.

---

# 9. Video

## Video source

**Valeur par défaut : Off**

Choisit la source vidéo affichable à côté de la carte :

| Option | Utilisation |
|---|---|
| Off | Pas de vidéo |
| USB camera (UVC) | Caméra, récepteur vidéo ou lunettes compatibles UVC via USB/OTG |
| Network stream | Flux vidéo reçu par le réseau |

Lorsque la vidéo est active, un bouton dans la barre supérieure permet de l'afficher ou de la masquer.

---

## Stream address

Disponible uniquement lorsque **Video source = Network stream**.

L'adresse détermine automatiquement le type de flux.

Exemples :

```text
rtsp://192.168.1.10:554/stream0
http://192.168.1.20:8080/video
udp://5600
```

- `rtsp://` : flux RTSP ;
- `http://` : généralement MJPEG ;
- `udp://` : écoute d'un flux RTP H.264/H.265 envoyé vers le téléphone.

L'application mémorise également les dernières adresses utilisées.

---

## RTSP over UDP

**Valeur par défaut : désactivé**

Ne concerne que les adresses `rtsp://`.

- **OFF** : RTP transporté par TCP. Plus robuste sur une liaison avec des pertes.
- **ON** : RTP transporté par UDP. Latence potentiellement plus faible mais davantage sensible aux pertes de paquets.

Ce réglage ne concerne pas la télémétrie UDP ; il concerne uniquement la vidéo RTSP.

---

# 10. Mock location

## Drone GPS as phone location

**Valeur par défaut : désactivé**

Cette fonction republie la position GPS reçue du modèle comme si elle était la position GPS du téléphone Android.

Exemple :

```text
GPS du modèle
    ↓
AFHDS2A / CRSF / autre télémétrie
    ↓
Telemetry Viewer
    ↓
Mock location Android
    ↓
Autre application GPS
```

Elle permet par exemple à une application de suivi installée sur le même téléphone d'envoyer la position du modèle au lieu de celle du téléphone.

Lorsque la liaison télémétrique s'arrête, le téléphone reprend sa véritable position GPS.

Pour utiliser cette fonction, Telemetry Viewer doit être sélectionné comme **application de localisation fictive** dans les Options développeur Android.

---

## Mock location app

Indique si Telemetry Viewer est actuellement autorisé par Android à fournir une position fictive.

Si ce n'est pas le cas, un appui ouvre les **Options développeur** afin de choisir Telemetry Viewer dans :

```text
Select mock location app
```

Si les Options développeur ne sont pas encore actives, Android demande généralement de les activer en appuyant plusieurs fois sur **Build number / Numéro de build** dans les informations du téléphone.

---

# 11. Diagnostics

Cette section est présente uniquement dans les builds **Debug**.

Elle n'est normalement pas affichée dans une Release.

## Copy debug info

Copie les informations de diagnostic dans le presse-papiers afin de pouvoir les envoyer pour analyse.

---

## Clear debug info

Efface les informations de diagnostic enregistrées.

---

# 12. Réglages réseau TCP/UDP

Les paramètres de télémétrie réseau ne sont volontairement **pas placés dans l'écran Settings**.

Ils sont choisis au moment de la connexion via **Network telemetry**.

Selon le mode choisi, l'application peut fonctionner en :

- TCP client ;
- TCP server selon les fonctions disponibles ;
- UDP listen ;
- TBS WebSocket / presets spécifiques.

Le transport réseau et le protocole de télémétrie sont indépendants. Par exemple, une trame AFHDS2A reçue par UDP peut être détectée comme AFHDS2A exactement comme lorsqu'elle est reçue par BLE.

---

# 13. Particularités de la version 2.6.0 AFHDS2A

La version 2.6.0 ajoute la détection et le décodage AFHDS2A, notamment pour les trames MULTI :

```text
0x06  télémétrie AFHDS2A
0x0C  télémétrie AFHDS2A étendue
0x0D  voies RC
```

Il n'existe actuellement aucun interrupteur **AFHDS2A ON/OFF** dans Settings : le protocole est reconnu automatiquement.

De même, dans la version 2.6.0 actuelle, l'écran Settings n'affiche pas encore directement :

```text
Connexion : Bluetooth LE
Télémétrie : AFHDS2A
```

Cette information pourra être ajoutée dans une version ultérieure comme information d'état en lecture seule.

---

## Réglages conseillés pour notre utilisation ESP32-C3 + AFHDS2A + BLE

Pour une utilisation normale avec notre ESP32-C3 :

| Réglage | Conseil |
|---|---|
| Telemetry logging RAW | Activé |
| Telemetry logging to CSV | Activé si les logs sont utiles |
| Automatically reconnect a lost radio link | Activé |
| Automatically reconnect lost network connection | Peu important en BLE |
| Voice messages | Selon préférence |
| USB Baudrate | Sans effet en BLE |
| Mock location | Désactivé sauf besoin précis |
| FlightRadar24 | Désactivé sauf besoin |
| Video | Off sauf utilisation vidéo |
| Protocol | Détection automatique AFHDS2A |

