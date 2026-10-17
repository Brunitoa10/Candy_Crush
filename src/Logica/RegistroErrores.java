package Logica;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import Score.RutasDeDatos;

/**
 * Captura los errores no controlados. Al lanzar con javaw (Jugar.bat) no hay consola,
 * así que sin esto el juego se cerraría sin decir nada.
 */
public final class RegistroErrores {

	private static final String ARCHIVO = "error.log";
	private static volatile boolean avisado;

	private RegistroErrores() {
	}

	public static void instalar() {
		Thread.setDefaultUncaughtExceptionHandler((hilo, error) -> registrar(error));
	}

	public static void registrar(Throwable error) {
		error.printStackTrace();
		File log = RutasDeDatos.archivo(ARCHIVO);
		try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(log, true), StandardCharsets.UTF_8))) {
			out.println("==== " + new Date() + " | Java " + System.getProperty("java.version")
					+ " | " + System.getProperty("os.name"));
			error.printStackTrace(out);
		} catch (Exception e) {
			// si no se puede escribir el log, al menos queda el cartel
		}
		avisar(error, log);
	}

	private static void avisar(Throwable error, File log) {
		if (avisado) {
			return; // un solo cartel aunque el error se repita en cada repintado
		}
		avisado = true;
		String mensaje = "Ocurrió un error: " + error + "\nDetalle en: " + log.getAbsolutePath();
		Runnable mostrar = () -> JOptionPane.showMessageDialog(null, mensaje, "Candy Crush", JOptionPane.ERROR_MESSAGE);
		if (SwingUtilities.isEventDispatchThread()) {
			mostrar.run();
		} else {
			SwingUtilities.invokeLater(mostrar);
		}
	}
}
