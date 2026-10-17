#!/bin/sh
# Compila Candy Crush sin internet ni IDE (JDK 8+). Resultado en dist/ (corre en Java 8+ / Windows 7+)
set -e
cd "$(dirname "$0")"
REL="--release 8"
javac -version 2>&1 | grep -q '1\.8' && REL="-source 1.8 -target 1.8"
rm -rf out && mkdir -p out/classes out/obstaculos/assets dist/lib
find src -name "*.java" > out/sources.txt
javac $REL -encoding UTF-8 -nowarn -d out/classes -cp "lib/*" @out/sources.txt
cp -r src/Niveles src/assets out/classes/
mv out/classes/assets/obstaculo out/obstaculos/assets/
cp lib/*.jar dist/lib/
jar cfm dist/candycrush.jar build-support/MANIFEST.MF -C out/classes .
jar cf dist/lib/candycrush-obstaculos.jar -C out/obstaculos .
cp Jugar.bat Jugar.sh dist/ 2>/dev/null || true
rm -rf out
echo "Listo: dist/candycrush.jar"
