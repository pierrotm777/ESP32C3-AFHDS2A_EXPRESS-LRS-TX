@echo off
setlocal
set KS=%USERPROFILE%\.android\debug.keystore
if not exist "%KS%" (
  echo Debug keystore not found: "%KS%"
  echo Run one debug build first: gradlew.bat :app:assembleDebug
  exit /b 1
)
echo Package name: crazydude.com.telemetr2
echo.
keytool -list -v -keystore "%KS%" -alias androiddebugkey -storepass android -keypass android | findstr /I "SHA1:"
endlocal
