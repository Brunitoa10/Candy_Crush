# Candy Crush (Java Swing)

Juego tipo Candy Crush en Java. Corre en **Windows 7, 8, 10 y 11** con **Java 8 o superior**, y también en Linux.
**Funciona sin red**: el TOP 5 se guarda en una base **SQLite** local (`data\candycrush.db`), dentro de la carpeta del juego.

## Cómo jugar

| Opción | Pasos |
|---|---|
| Con Java 8+ instalado | Doble clic en `dist\Jugar.bat` (ya viene compilado) |
| Compilar sin internet | `compilar.bat` (cualquier JDK 8+, usa las librerías de `lib\`) |
| Portable (sin instalar Java) | `crear-portable.bat` (con un Java 8) → copiá `portable\CandyCrush` a un pendrive → `Jugar.bat` |
| Desde el IDE | Abrí la carpeta (Eclipse/VS Code leen `.classpath`) y ejecutá `Logica.Logica` |

### Windows 7
- Instalá **Java 8** (Oracle Java 8 o Temurin 8). En PCs de 32 bits usá el Java de 32 bits.
- Para varias PCs sin instalar nada, generá la versión portable con ese Java 8: se copia dentro de `jre\`.

## Datos
- Errores: si algo falla aparece un cartel y el detalle queda en `data\error.log`.
- Ranking: `data\candycrush.db` (tabla `puntajes`). Si el juego está en una carpeta sin permiso de escritura, usa `%APPDATA%\CandyCrush`.
- Si existía un `TOP5.csv` viejo, se importa solo la primera vez.
- Si falta `lib\sqlite-jdbc.jar`, el juego sigue funcionando y guarda en `data\TOP5.csv`.

## Estructura de la distribución
```
CandyCrush\
  Jugar.bat / Jugar.sh
  candycrush.jar      (código + imágenes + niveles)
  lib\                (imágenes de obstáculos, sqlite-jdbc, slf4j)
  data\               (se crea al jugar)
  jre\                (solo en la versión portable)
```

## Niveles
Agregar `src\Niveles\NivelN.txt` y recompilar: la cantidad de niveles se detecta sola.
