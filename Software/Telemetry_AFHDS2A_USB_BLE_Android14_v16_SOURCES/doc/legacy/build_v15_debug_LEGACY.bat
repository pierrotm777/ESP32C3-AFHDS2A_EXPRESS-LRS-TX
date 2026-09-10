@echo off
setlocal
rem BLE-v15 debug build. Requires JDK 11 and local.properties with sdk.dir + ndk.dir.
call gradlew.bat clean :app:assembleDebug
if errorlevel 1 exit /b %errorlevel%
echo.
echo APK: app\build\outputs\apk\debug\app-debug.apk
endlocal
