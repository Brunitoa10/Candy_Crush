package Threads;

import java.awt.Image;

import GUI.Celda;
import GUI.RecursosGraficos;

/** Muestra la explosión y después quita la celda del tablero (la entidad ya no existe). */
public class AnimadorDetonacion extends AnimadorBase {

	private static final String GIF_EXPLOSION = "/assets/gemas/detonado.gif";

	protected final int delay;

	public AnimadorDetonacion(CentralAnimaciones manager, Celda celda, int delay) {
		super(manager, celda, PrioridadAnimaciones.PRIORIDAD_DETONACION);
		this.delay = delay;
	}

	@Override
	public void comenzar_animacion() {
		int size = celda_animada.get_size_label();
		celda_animada.setIcon(RecursosGraficos.icono(GIF_EXPLOSION, size, size, Image.SCALE_REPLICATE));
		despues(delay, () -> {
			// antes la celda quedaba en el panel con una imagen transparente y se acumulaban celdas "fantasma"
			celda_animada.eliminar_de_ventana();
			finalizar();
		});
	}
}
