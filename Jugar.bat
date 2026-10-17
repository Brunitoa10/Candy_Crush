@echo off
rem ==========================================================
rem  Candy Crush - Windows 7, 8, 10 y 11.
rem  Busca Java en este orden: jre\ (version portable), JAVA_HOME, PATH,
rem  instalaciones comunes (Oracle, Temurin, Zulu, Microsoft) y el Java de VS Code.
rem  El TOP 5 se guarda en data\candycrush.db (SQLite). Errores: data\error.log
rem ==========================================================
setlocal EnableDelayedExpansion
cd /d "%~dp0"
if exist candycrush.jar (set "JAR=candycrush.jar") else (set "JAR=dist\candycrush.jar")
if not exist "%JAR%" (echo No se encontro el juego ^(candycrush.jar^). & pause & exit /b 1)

set "JW="
if exist "jre\bin\javaw.exe" set "JW=jre\bin\javaw.exe"
if not defined JW if defined JAVA_HOME if exist "%JAVA_HOME%\bin\javaw.exe" set "JW=%JAVA_HOME%\bin\javaw.exe"
if not defined JW for /f "delims=" %%j in ('where javaw 2^>nul') do if not defined JW set "JW=%%j"
if not defined JW for %%b in ("%ProgramFiles%" "%ProgramFiles(x86)%") do (
  for %%v in (Java "Eclipse Adoptium" Zulu Microsoft AdoptOpenJDK Amazon Corretto) do (
    for /d %%d in ("%%~b\%%~v\*") do if not defined JW if exist "%%d\bin\javaw.exe" set "JW=%%d\bin\javaw.exe"
  )
)
if not defined JW for /d %%e in ("%USERPROFILE%\.vscode\extensions\redhat.java-*") do (
  for /d %%d in ("%%e\jre\*") do if not defined JW if exist "%%d\bin\javaw.exe" set "JW=%%d\bin\javaw.exe"
)
if not defined JW (
  echo No se encontro Java. Instala Java 8 o superior, o usa la version portable ^(crear-portable.bat^).
  pause
  exit /b 1
)
start "" "%JW%" -Xms256m -Xmx768m -jar "%JAR%"
