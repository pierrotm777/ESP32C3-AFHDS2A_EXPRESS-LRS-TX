@echo off
setlocal
set "ADB=C:\Android\Sdk\platform-tools\adb.exe"
set "APK=app\build\outputs\apk\debug\app-debug.apk"
if not exist "%APK%" (
  echo APK not found: %APK%
  exit /b 1
)
"%ADB%" install -r "%APK%"
endlocal
