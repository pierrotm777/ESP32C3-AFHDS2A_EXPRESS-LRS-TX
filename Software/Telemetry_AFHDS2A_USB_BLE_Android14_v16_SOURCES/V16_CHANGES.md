# v16 — Android 14 buildable source baseline

- Gradle 8.7 / AGP 8.5.2 / Kotlin 1.9.24 / JDK 17.
- compileSdk/targetSdk 34, minSdk 21.
- applicationId conservé: `crazydude.com.telemetr2`.
- Google Maps API key retirée des sources et injectée depuis `local.properties`.
- retrait de l'ancien `google-services.json` et des références Firebase inutilisées.
- Android 14 foreground service type `connectedDevice`.
- récepteur dynamique USB compatible targetSdk 34.
- permission Bluetooth classique/BLE demandée avant les opérations qui la nécessitent.
- UVC migré vers `com.herohan:UVCAndroid:1.0.13`.
- callback UVC `onError` ajouté pour l'API actuelle.
- dépendance RecyclerView explicite ajoutée.
- crédits et décharge de responsabilité présents dans Paramètres > About / Credits.
- anciens fichiers de build v15 déplacés dans `doc/legacy/` pour éviter le mélange des toolchains.
