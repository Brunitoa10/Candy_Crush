package Score;

import java.io.File;

/**
 * Ubica la carpeta de datos. Igual que en la versión portable: "data\" al lado del juego
 * (Jugar.bat hace cd a su carpeta). Si ahí no se puede escribir (por ejemplo, en Archivos de programa),
 * se usa %APPDATA%\CandyCrush o ~/.candycrush.
 */
public final class RutasDeDatos {

	private static final String CARPETA_LOCAL = "data";
	private static File carpeta;

	private RutasDeDatos() {
	}

	public static synchronized File carpetaDeDatos() {
		if (carpeta == null) {
			File local = new File(CARPETA_LOCAL);
			carpeta = esEscribible(local) ? local : carpetaDelUsuario();
		}
		return carpeta;
	}

	public static File archivo(String nombre) {
		return new File(carpetaDeDatos(), nombre);
	}

	private static File carpetaDelUsuario() {
		String appData = System.getenv("APPDATA");
		File base = appData != null ? new File(appData, "CandyCrush")
				: new File(System.getProperty("user.home"), ".candycrush");
		base.mkdirs();
		return base;
	}

	private static boolean esEscribible(File dir) {
		return (dir.isDirectory() || dir.mkdirs()) && dir.canWrite();
	}
}
