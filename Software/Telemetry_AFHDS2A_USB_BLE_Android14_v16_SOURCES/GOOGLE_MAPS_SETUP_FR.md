# Google Maps - configuration pour Telemetry BLE v15

## Identite Android a autoriser

- Package Android : `crazydude.com.telemetr2`
- Build recommande pour ce projet : **Debug APK** (la branche UVC historique desactive le build release).
- La restriction Google Maps doit utiliser le **SHA-1 du certificat qui signe l'APK que vous installez**.

> Ne reutilisez pas l'empreinte SHA-1 d'un ancien APK si vous compilez avec une autre cle de signature.

## 1. Generer/obtenir le certificat Debug

Depuis la racine du projet :

```bat
gradlew.bat :app:assembleDebug
```

Le premier build cree normalement :

```text
%USERPROFILE%\.android\debug.keystore
```

Puis lancez :

```bat
SHOW_DEBUG_SHA1.bat
```

Ou manuellement :

```bat
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
```

Recopiez la valeur `SHA1:`.

## 2. Google Cloud / Google Maps Platform

1. Creez ou selectionnez un projet Google Cloud.
2. Activez la facturation pour ce projet si Google la demande.
3. Activez **Maps SDK for Android**.
4. Ouvrez **Google Maps Platform > Credentials**.
5. Creez une **API key**.
6. Editez la cle :
   - **Application restrictions** : `Android apps`
   - ajoutez :
     - Package : `crazydude.com.telemetr2`
     - SHA-1 : celui obtenu avec `SHOW_DEBUG_SHA1.bat`
7. Dans **API restrictions** :
   - `Restrict key`
   - autorisez uniquement **Maps SDK for Android**.
8. Enregistrez.

## 3. Mettre la cle dans le projet

Methode simple :

```bat
SET_GOOGLE_MAPS_KEY.bat VOTRE_CLE_API
```

Le script renseigne :

```text
app\src\debug\res\values\google_maps_api.xml
app\src\release\res\values\google_maps_api.xml
```

Ou editez manuellement la valeur `YOUR_GOOGLE_MAPS_API_KEY` dans ces deux fichiers.

## 4. Compiler

```bat
gradlew.bat clean :app:assembleDebug
```

APK :

```text
app\build\outputs\apk\debug\app-debug.apk
```

## 5. Installer

Si l'APK deja installe a ete signe avec une autre cle :

```bat
adb uninstall crazydude.com.telemetr2
adb install "app\build\outputs\apk\debug\app-debug.apk"
```

Ensuite, les mises a jour compilees avec le meme debug.keystore peuvent etre installees avec :

```bat
adb install -r "app\build\outputs\apk\debug\app-debug.apk"
```

## 6. Verification

Dans l'application, les quatre modes Google doivent charger :

- Road Map (Google)
- Satellite (Google)
- Terrain (Google)
- Hybrid (Google)

OpenStreetMap et OpenTopoMap restent independants de la cle Google.

En cas d'ecran gris Google Maps, verifier d'abord :

- package exact `crazydude.com.telemetr2`
- SHA-1 exact du certificat de l'APK installe
- Maps SDK for Android active
- API key restreinte a Maps SDK for Android
- connexion Internet
