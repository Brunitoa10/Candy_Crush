@echo off
rem ==================================================================
rem  Crea una version PORTABLE con Java incluido: no necesita instalar
rem  nada ni internet. Copia la carpeta portable\CandyCrush a un
rem  pendrive o a cualquier PC (Windows 7 en adelante) y abri Jugar.bat
rem
rem  IMPORTANTE para Windows 7: ejecuta este script con un Java 8
rem  (JAVA_HOME apuntando a un JDK/JRE 8), porque se copia ESE Java.
rem  Si las PC destino son de 32 bits, usa un Java 8 de 32 bits.
rem ==================================================================
setlocal
cd /d "%~dp0"
if not exist dist\candycrush.jar call compilar.bat
if not exist dist\candycrush.jar (echo No se encontro dist\candycrush.jar & pause & exit /b 1)
set "JROOT="
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "JROOT=%JAVA_HOME%"
rem Si no hay JAVA_HOME, se le pregunta a java donde esta instalado (evita copiar el atajo "javapath")
if not defined JROOT for /f "tokens=2 delims==" %%h in ('java -XshowSettings:properties -version 2^>^&1 ^| findstr /c:"java.home"') do if not defined JROOT set "JROOT=%%h"
if defined JROOT for /f "tokens=* delims= " %%h in ("%JROOT%") do set "JROOT=%%h"
if not defined JROOT (echo No se encontro Java. Defini JAVA_HOME. & pause & exit /b 1)
if exist "%JROOT%\jre\bin\java.exe" set "JROOT=%JROOT%\jre"
if not exist "%JROOT%\bin\javaw.exe" (echo Java invalido en: %JROOT% & pause & exit /b 1)
echo Usando Java en: %JROOT%
if exist portable rmdir /s /q portable
mkdir portable\CandyCrush\data
xcopy /e /i /y /q dist portable\CandyCrush >nul
if exist portable\CandyCrush\data\candycrush.db del /q portable\CandyCrush\data\candycrush.db
if exist portable\CandyCrush\data\error.log del /q portable\CandyCrush\data\error.log
xcopy /e /i /y /q "%JROOT%" portable\CandyCrush\jre >nul
copy /y Jugar.bat portable\CandyCrush\ >nul
if exist README.md copy /y README.md portable\CandyCrush\ >nul
echo.
echo Version portable lista en: portable\CandyCrush  ^(abrir Jugar.bat^)
pause
