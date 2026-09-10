@echo off
setlocal EnableExtensions
cd /d "%~dp0"
set "ADB=C:\Android\Sdk\platform-tools\adb.exe"
set "APK=%CD%\app\build\outputs\apk\debug\app-debug.apk"

if not exist "%ADB%" (
  echo ERREUR: adb introuvable: %ADB%
  pause
  exit /b 1
)
if not exist "%APK%" (
  echo ERREUR: APK introuvable. Lancez d'abord BUILD_V16_DEBUG.bat
  pause
  exit /b 1
)

echo Installation / mise a jour de la v16...
"%ADB%" install -r "%APK%"
if errorlevel 1 (
  echo.
  echo ECHEC. Ne desinstallez rien pour l'instant; envoyez-moi ce message.
  pause
  exit /b 1
)

echo.
echo Installation OK.
pause
