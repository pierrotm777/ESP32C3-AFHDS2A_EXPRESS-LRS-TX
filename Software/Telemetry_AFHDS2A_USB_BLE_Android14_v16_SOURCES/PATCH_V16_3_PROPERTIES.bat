@echo off
setlocal EnableExtensions
cd /d "%~dp0"

if not exist "app\build.gradle" (
  echo ERREUR: app\build.gradle introuvable.
  echo Placez ce fichier BAT a la racine du projet v16.
  pause
  exit /b 1
)

echo Correction de Propertmes ^> Properties...
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$p='app\build.gradle'; $s=[IO.File]::ReadAllText($p); if(-not $s.Contains('Propertmes') -and -not $s.Contains('Properties')){Write-Error 'Motif introuvable'; exit 2}; $s=$s.Replace('Propertmes','Properties'); [IO.File]::WriteAllText($p,$s,(New-Object Text.UTF8Encoding($false)))"

if errorlevel 1 (
  echo.
  echo ECHEC DE LA CORRECTION.
  pause
  exit /b 1
)

echo.
echo Debut du fichier apres correction:
powershell -NoProfile -Command "Get-Content 'app\build.gradle' -TotalCount 8"
echo.
echo Relance de la compilation...
call gradlew.bat clean assembleDebug
echo.
pause
