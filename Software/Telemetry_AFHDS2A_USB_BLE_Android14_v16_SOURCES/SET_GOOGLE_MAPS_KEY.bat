@echo off
setlocal
cd /d "%~dp0"
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0tools\SetMapsKey.ps1"
if errorlevel 1 (
  echo.
  echo ECHEC de la configuration Maps.
  pause
  exit /b 1
)
echo.
echo Configuration Google Maps locale terminee.
pause
