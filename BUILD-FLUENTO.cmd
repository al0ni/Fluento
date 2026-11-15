@echo off
setlocal
cd /d "%~dp0"
call mvn -B -ntp clean verify
if errorlevel 1 (
  echo Build failed. Check the messages above.
  pause
  exit /b 1
)
if not exist dist mkdir dist
copy /y target\fluento-1.0-SNAPSHOT.jar dist\fluento.jar
echo Build verified. START-FLUENTO.cmd now launches the updated application.
pause
