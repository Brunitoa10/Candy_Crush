package GUI;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

/** Hace parpadear un grupo de celdas (la pista) hasta que se lo detiene. */
public class ParpadeoPista {

	private static final int INTERVALO_MS = 350;

	private final List<EntidadGrafica> celdas = new ArrayList<>();
	private final Timer timer;
	private boolean encendido;

	public ParpadeoPista() {
		timer = new Timer(INTERVALO_MS, e -> alternar());
	}

	public void mostrar(List<EntidadGrafica> nuevasCeldas) {
		detener();
		for (EntidadGrafica celda : nuevasCeldas) {
			if (celda != null) {
				celdas.add(celda);
			}
		}
		if (!celdas.isEmpty()) {
			encendido = false;
			alternar();
			timer.start();
		}
	}

	public void detener() {
		timer.stop();
		for (EntidadGrafica celda : celdas) {
			celda.resaltar(false);
		}
		celdas.clear();
	}

	public boolean estaActiva() {
		return timer.isRunning();
	}

	private void alternar() {
		encendido = !encendido;
		for (EntidadGrafica celda : celdas) {
			celda.resaltar(encendido);
		}
	}
}
