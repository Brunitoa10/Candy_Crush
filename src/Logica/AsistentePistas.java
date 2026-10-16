package Logica;

import java.util.ArrayList;
import java.util.List;

import Entidades.Entidad;
import GUI.EntidadGrafica;
import GUI.GUI;
import Tablero.Pista;
import Tablero.Tablero;

/**
 * Muestra una pista (gemas que parpadean) cuando el jugador pasa cierto tiempo sin intercambiar.
 * Se alimenta del tick del reloj del nivel (Observer) y no hace nada mientras haya animaciones.
 */
public class AsistentePistas {

	public static final int SEGUNDOS_PARA_PISTA = 5;

	private final GUI gui;
	private int segundosInactivo;
	private boolean pistaVisible;

	public AsistentePistas(GUI gui) {
		this.gui = gui;
	}

	/** Llamado una vez por segundo por el reloj del nivel. */
	public void tick(Tablero tablero) {
		if (pistaVisible || gui.getBloquear_intercambios()) {
			return; // no se cuenta mientras el tablero se está animando
		}
		segundosInactivo++;
		if (segundosInactivo >= SEGUNDOS_PARA_PISTA) {
			mostrar(tablero.buscarPista());
		}
	}

	/** El jugador hizo algo: se oculta la pista y vuelve a contar desde cero. */
	public void reiniciar() {
		segundosInactivo = 0;
		if (pistaVisible) {
			gui.ocultarPista();
			pistaVisible = false;
		}
	}

	public boolean hayPistaVisible() {
		return pistaVisible;
	}

	private void mostrar(Pista pista) {
		if (pista == null) {
			segundosInactivo = 0; // sin jugadas posibles: el tablero se mezcla solo (ver Tablero)
			return;
		}
		List<EntidadGrafica> celdas = new ArrayList<>();
		for (Entidad entidad : pista.getEntidadesAResaltar()) {
			celdas.add(entidad.get_EntidadGrafica());
		}
		gui.mostrarPista(celdas);
		pistaVisible = true;
	}
}
