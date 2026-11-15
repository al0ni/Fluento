@echo off
setlocal
set "FLUENTO_JAVA=java"
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "FLUENTO_JAVA=%JAVA_HOME%\bin\java.exe"
if not exist "%~dp0dist\fluento.jar" (
  echo The application file is missing. Extract the complete ZIP first.
  pause
  exit /b 1
)
"%FLUENTO_JAVA%" -jar "%~dp0dist\fluento.jar"
if errorlevel 1 (
  echo.
  echo Fluento needs Java 21 or newer. Check: java -version
  echo You can also open pom.xml in IntelliJ and run com.fluento.app.Main.
  pause
  exit /b 1
)
