@echo off
setlocal EnableExtensions
cd /d "%~dp0"

if not exist "app\build.gradle" (
  echo ERREUR: app\build.gradle introuvable.
  echo Placez ce fichier BAT a la racine du projet v16.
  pause
  exit /b 1
)

echo Sauvegarde de app\build.gradle...
copy /y "app\build.gradle" "app\build.gradle.bak_v16_4" >nul

echo Correction de l'entete build.gradle...
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$p='app\build.gradle';" ^
  "$s=[IO.File]::ReadAllText($p);" ^
  "$s=$s.Replace('localPropertmes','localProperties').Replace('Propertmes','Properties');" ^
  "if($s.StartsWith('java.util.Properties')){$s='import '+$s};" ^
  "$s=$s.Replace('com.google.android:flexbox:3.0.0','com.google.android.flexbox:flexbox:3.0.0');" ^
  "[IO.File]::WriteAllText($p,$s,(New-Object Text.UTF8Encoding($false)))"

if errorlevel 1 (
  echo.
  echo ECHEC DE LA CORRECTION.
  pause
  exit /b 1
)

echo.
echo Les 10 premieres lignes sont maintenant:
echo ------------------------------------------------------------
powershell -NoProfile -Command "Get-Content 'app\build.gradle' -TotalCount 10"
echo ------------------------------------------------------------
echo.
echo Relance de la compilation...
call gradlew.bat clean assembleDebug
echo.
pause
