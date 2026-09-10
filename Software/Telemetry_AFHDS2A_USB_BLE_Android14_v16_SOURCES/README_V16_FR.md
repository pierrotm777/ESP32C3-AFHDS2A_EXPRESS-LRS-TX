# Telemetry AFHDS2A USB/BLE — Android 14 v16

Base moderne et recompilable du fork Android Telemetry Viewer.

Fonctions conservées / intégrées:

- Google Maps et OpenStreetMap
- télémétrie FrSky, CRSF, LTM, MAVLink et FlySky AFHDS2A
- Bluetooth classique, BLE et USB série
- caméra USB UVC
- lecture des logs et affichage des capteurs
- compatibilité Android 14 / targetSdk 34

## Première compilation

Sous Windows, utilisez simplement:

1. `SET_GOOGLE_MAPS_KEY.bat`
2. `BUILD_V16_DEBUG.bat`
3. `INSTALL_V16_DEBUG.bat`

La clé Google Maps n'est pas stockée dans cette archive. Elle reste dans votre `local.properties` local.

## Crédits

- application d'origine: **CrazyDude1994 — Android Taranis SmartPort Telemetry**
- fork et améliorations importantes: **Roman Lut**
- pile UVC historique: **saki / Serenegiant**
- moteur UVC moderne: **HanShiYing (shiyinghan) — UVCAndroid**
- adaptation AFHDS2A, USB/BLE et Android 14: ce fork

Voir `FORK_NOTICE.md` pour les crédits et la décharge de responsabilité détaillés.
