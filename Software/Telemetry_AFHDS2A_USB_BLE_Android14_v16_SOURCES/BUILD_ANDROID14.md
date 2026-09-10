# Build Android 14 — v16

Cette branche est la base moderne du fork Telemetry AFHDS2A USB/BLE.

## Chaîne de compilation

- Android Gradle Plugin: **8.5.2**
- Gradle wrapper: **8.7**
- Kotlin: **1.9.24**
- Java/JDK: **17**
- compileSdk: **34**
- targetSdk: **34**
- minSdk: **21**

## Windows — méthode recommandée

1. Décompresser l'archive dans un chemin court, par exemple `C:\tmp\Telemetry_AFHDS2A_USB_BLE_Android14_v16_SOURCES`.
2. Vérifier que le SDK Android est dans `C:\Android\Sdk`.
3. Lancer `SET_GOOGLE_MAPS_KEY.bat`. La saisie de la clé est masquée et la clé est écrite uniquement dans `local.properties`, qui est ignoré par Git.
4. Lancer `BUILD_V16_DEBUG.bat`.
5. L'APK est produit dans `app\build\outputs\apk\debug\app-debug.apk`.
6. Lancer `INSTALL_V16_DEBUG.bat` pour une mise à jour avec `adb install -r`.

Le build debug utilise le keystore Android debug local (`%USERPROFILE%\.android\debug.keystore`).

## Google Maps

La clé API n'est pas incluse dans les sources. `app/build.gradle` lit `MAPS_API_KEY` depuis `local.properties` (ou la variable d'environnement `MAPS_API_KEY`) et l'injecte comme manifest placeholder.

La restriction Google Cloud recommandée est:

- API: **Maps SDK for Android** uniquement
- package: `crazydude.com.telemetr2`
- SHA-1: empreinte du `debug.keystore` local

## Android 14

- permissions BLE Android 12+ (`BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`)
- service de télémétrie déclaré `connectedDevice` avec la permission FGS correspondante
- réception de permission USB dynamique compatible targetSdk 34
- logs dans le stockage externe propre à l'application
- UVC via `com.herohan:UVCAndroid:1.0.13` (Android 5.0+)
- enregistrement UVC via MediaStore dans `Movies/TelemetryCamera` sur Android 10+

## Important

Ne pas réutiliser les instructions historiques Gradle 6.x / JDK 11 / NDK r16b avec cette branche. Elles sont archivées dans `doc/legacy/` uniquement pour référence historique.
