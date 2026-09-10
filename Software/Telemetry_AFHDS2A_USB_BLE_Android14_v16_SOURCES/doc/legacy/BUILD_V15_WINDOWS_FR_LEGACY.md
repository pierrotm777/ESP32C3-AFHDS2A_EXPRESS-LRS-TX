# Compilation BLE-v15 sous Windows

## 1. Ce qu'il faut installer

Le projet conserve l'ancien moteur UVC/UVCCamera. Pour éviter de moderniser en même temps Android, Gradle, Kotlin et le code natif UVC, la méthode recommandée utilise une chaîne de compilation compatible avec le projet historique.

Installez :

- **JDK 11** (important : ne pas utiliser JDK 17/21 avec ce Gradle historique).
- Android SDK dans `C:\Android\Sdk`.
- **Android SDK Platform 32**.
- **Android SDK Build-Tools 30.0.3**.
- **Android NDK r16b** pour le module UVC, qui utilise encore `ndk-build` et GCC 4.9 dans `Application.mk`.

Vous pouvez garder Platform 34 / Build-Tools 34 déjà installés ; ils n'ont pas besoin d'être supprimés. Il faut seulement ajouter Platform 32 et Build-Tools 30.0.3.

Le projet utilise :

- Gradle 6.7.1 (wrapper fourni)
- Android Gradle Plugin 4.2.2
- Kotlin 1.4.32
- compileSdk 32
- targetSdk 32
- minSdk 17

## 2. Décompresser les sources

Par exemple :

`C:\AndroidProjects\Telemetry_AFHDS2A_USB_BLE_v15`

Évitez si possible les chemins très longs et les caractères accentués, surtout à cause du vieux NDK/UVC.

## 3. Créer `local.properties`

À la racine du projet, créez un fichier `local.properties` :

```properties
sdk.dir=C\:\\Android\\Sdk
ndk.dir=C\:\\Android\\android-ndk-r16b
```

Adaptez la deuxième ligne à l'emplacement réel de votre NDK r16b.

Exemple si le NDK est dans le SDK :

```properties
ndk.dir=C\:\\Android\\Sdk\\ndk\\android-ndk-r16b
```

## 4. Vérifier Java

Dans une invite de commandes :

```bat
java -version
```

Il faut voir une version Java 11.

Si plusieurs Java sont installés :

```bat
set "JAVA_HOME=C:\Program Files\Java\jdk-11"
set "PATH=%JAVA_HOME%\bin;%PATH%"
java -version
```

Adaptez le chemin à votre installation.

## 5. Première compilation

Placez-vous à la racine des sources :

```bat
cd /d C:\AndroidProjects\Telemetry_AFHDS2A_USB_BLE_v15
```

Puis :

```bat
gradlew.bat clean :app:assembleDebug
```

La première compilation télécharge les dépendances Gradle/Maven ; une connexion Internet est donc nécessaire la première fois.

Le module UVC lance automatiquement `ndk-build`.

## 6. APK produit

L'APK debug se trouve normalement ici :

```text
app\build\outputs\apk\debug\app-debug.apk
```

## 7. Installation sur le Samsung A52 5G

Le `test15` que vous avez actuellement est signé avec une clé de test différente de la clé debug générée sur votre PC. Android refusera donc une mise à jour directe à signature différente.

Pour installer votre première compilation locale :

```bat
C:\Android\Sdk\platform-tools\adb.exe uninstall crazydude.com.telemetr2
C:\Android\Sdk\platform-tools\adb.exe install "app\build\outputs\apk\debug\app-debug.apk"
```

Ensuite, tant que vous compilez avec la même clé debug sur le même PC, vous pourrez utiliser :

```bat
C:\Android\Sdk\platform-tools\adb.exe install -r "app\build\outputs\apk\debug\app-debug.apk"
```

## 8. Compilation avec Android Studio

Vous pouvez aussi ouvrir directement le dossier du projet dans Android Studio.

Points importants :

1. Laissez Android Studio synchroniser Gradle.
2. Dans les réglages Gradle, choisissez **JDK 11** comme Gradle JDK.
3. Vérifiez que Platform 32 et Build-Tools 30.0.3 sont installés.
4. Vérifiez `local.properties` et le chemin `ndk.dir`.
5. Choisissez la variante **debug**.
6. Build > Build APK(s).

Le projet désactive volontairement la variante release, comme le projet UVC historique, car le code caméra posait des problèmes lorsqu'il était optimisé en release.

## 9. Pourquoi les logs sont désactivés dans v15

Le code hx1.6.3 crée les fichiers RAW/CSV dans l'ancien dossier public `TelemetryLogs` avec `Environment.getExternalStoragePublicDirectory()`.

Sur Android 14, cette écriture a empêché `DataService.connect()` d'atteindre `connectGatt()`. Le message affiché était trompeur : `Failed to connect to bluetooth`.

Dans BLE-v15 :

```kotlin
fun isLoggingEnabled(): Boolean = false
fun isCSVLoggingEnabled(): Boolean = false
```

Cela reproduit exactement le correctif qui a permis la connexion BLE. Une future version pourra remettre les logs avec MediaStore ou un dossier privé/scoped-storage compatible Android moderne.

## 10. Test BLE après compilation

Côté ESP32-C3 :

```text
bt on
btsimu on
```

Dans l'application :

- Connect > Bluetooth LE
- sélectionner `AF2A-C3`

Côté C3, la commande :

```text
bt
```

doit afficher notamment :

```text
link=CONNECTED
simu=ON
sent=...
sim=...
```

La carte doit recevoir la position GPS simulée et la vitesse doit être proche de 18 km/h.

## 11. Si Gradle échoue sur UVC/NDK

Vérifiez d'abord :

- que `ndk.dir` pointe bien vers **NDK r16b** ;
- que `ndk-build.cmd` existe dans ce dossier ;
- que le projet n'est pas dans un chemin trop long ;
- que JDK 11 est réellement utilisé par Gradle ;
- que Platform 32 et Build-Tools 30.0.3 sont présents.

Ne remplacez pas immédiatement le module UVC : il est volontairement conservé dans cette branche.
