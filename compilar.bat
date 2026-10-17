@echo off
rem ==========================================================
rem  Compila Candy Crush SIN internet ni IDE.
rem  Sirve cualquier JDK 8 o superior (el resultado corre en Java 8+ y Windows 7+).
rem  Resultado: carpeta dist\ lista para jugar o copiar.
rem  (dist\ ya viene compilado: solo hace falta esto si cambias el codigo)
rem ==========================================================
setlocal
cd /d "%~dp0"
where javac >nul 2>nul || (echo Falta el JDK ^(8 o superior^) en el PATH. Para jugar no hace falta: usa dist\Jugar.bat & pause & exit /b 1)
rem Con JDK 9+ se usa --release 8 (verifica que no se use API nueva); con JDK 8, -source/-target.
set "REL=--release 8"
for /f "tokens=2" %%v in ('javac -version 2^>^&1') do set "JV=%%v"
if "%JV:~0,3%"=="1.8" set "REL=-source 1.8 -target 1.8"
if exist out rmdir /s /q out
mkdir out\classes
mkdir out\obstaculos\assets
dir /s /b src\*.java > out\sources.txt
javac %REL% -encoding UTF-8 -nowarn -d out\classes -cp "lib\*" @out\sources.txt || (pause & exit /b 1)
rem Recursos: niveles e imagenes en el .jar principal; los obstaculos en un .jar aparte
xcopy /e /i /y /q src\Niveles out\classes\Niveles >nul
xcopy /e /i /y /q src\assets out\classes\assets >nul
rmdir /s /q out\classes\assets\obstaculo
xcopy /e /i /y /q src\assets\obstaculo out\obstaculos\assets\obstaculo >nul
if not exist dist\lib mkdir dist\lib
copy /y lib\*.jar dist\lib\ >nul
jar cfm dist\candycrush.jar build-support\MANIFEST.MF -C out\classes . || (pause & exit /b 1)
jar cf dist\lib\candycrush-obstaculos.jar -C out\obstaculos . || (pause & exit /b 1)
copy /y Jugar.bat dist\ >nul
copy /y Jugar.sh dist\ >nul
rmdir /s /q out
echo.
echo Listo: dist\candycrush.jar  ^(ejecuta dist\Jugar.bat^)
pause
