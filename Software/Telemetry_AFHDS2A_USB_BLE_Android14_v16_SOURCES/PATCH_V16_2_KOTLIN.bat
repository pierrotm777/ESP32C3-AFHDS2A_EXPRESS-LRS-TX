@echo off
setlocal
cd /d "%~dp0"
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0PATCH_V16_2_KOTLIN.ps1"
set "ERR=%ERRORLEVEL%"
echo.
pause
exit /b %ERR%
