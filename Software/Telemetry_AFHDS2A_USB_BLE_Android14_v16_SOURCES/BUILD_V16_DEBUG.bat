@echo off
setlocal EnableExtensions
cd /d "%~dp0"
title Telemetry AFHDS2A USB BLE - Build Android 14 v16

echo ============================================================
echo  Telemetry AFHDS2A USB BLE - Android 14 v16
echo  Build debug - Gradle 8.7 / AGP 8.5.2 / JDK 17 / SDK 34
echo ============================================================
echo.

where java >nul 2>nul
if errorlevel 1 (
  echo ERREUR: Java n'est pas accessible dans le PATH.
  pause
  exit /b 1
)

java -version
if errorlevel 1 goto :fail

echo.
if not exist "local.properties" (
  echo Creation de local.properties pour C:\Android\Sdk ...
  >"local.properties" echo sdk.dir=C\:\\Android\\Sdk
)

findstr /B /C:"MAPS_API_KEY=AIza" "local.properties" >nul 2>nul
if errorlevel 1 (
  echo.
  echo La cle Google Maps n'est pas encore configuree dans local.properties.
  echo Lancez d'abord SET_GOOGLE_MAPS_KEY.bat puis relancez ce fichier.
  pause
  exit /b 2
)

echo.
echo Lancement de la compilation...
call gradlew.bat clean assembleDebug
if errorlevel 1 goto :fail

set "APK=%CD%\app\build\outputs\apk\debug\app-debug.apk"
if not exist "%APK%" (
  echo ERREUR: BUILD termine mais APK introuvable:
  echo %APK%
  pause
  exit /b 3
)

echo.
echo ============================================================
echo BUILD SUCCESSFUL
echo APK: %APK%
echo ============================================================

echo.
if exist "C:\Android\Sdk\build-tools\34.0.0\apksigner.bat" (
  echo Certificat de signature de l'APK:
  call "C:\Android\Sdk\build-tools\34.0.0\apksigner.bat" verify --print-certs "%APK%"
)

echo.
echo Vous pouvez maintenant lancer INSTALL_V16_DEBUG.bat
pause
exit /b 0

:fail
echo.
echo ============================================================
echo ECHEC DE COMPILATION
 echo Copiez-moi la fin du texte affiche au-dessus.
echo ============================================================
pause
exit /b 1
