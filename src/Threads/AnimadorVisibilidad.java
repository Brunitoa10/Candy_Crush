package Threads;

import java.awt.Image;

import GUI.Celda;
import GUI.RecursosGraficos;

public class AnimadorVisibilidad extends AnimadorBase {

	private static final String GIF_DESTELLO = "/assets/gemas/detonado.gif";

	protected final boolean debe_aparecer;
	protected final int delay;
	protected final String[] imagenes_estado;

	public AnimadorVisibilidad(CentralAnimaciones manager, Celda celda, int delay) {
		super(manager, celda, PrioridadAnimaciones.PRIORIDAD_VISIBILIDAD);
		this.delay = delay;
		this.debe_aparecer = celda.get_entidad_logica().get_visibilidad();
		this.imagenes_estado = celda.get_entidad_logica().get_imagenes_representativas();
	}

	@Override
	public void comenzar_animacion() {
		if (!debe_aparecer) {
			celda_animada.setVisible(false);
			finalizar();
			return;
		}
		int size = celda_animada.get_size_label();
		celda_animada.setVisible(true);
		celda_animada.setIcon(RecursosGraficos.icono(GIF_DESTELLO, size, size, Image.SCALE_REPLICATE));
		despues(delay, () -> {
			celda_animada.mostrarImagenes(imagenes_estado);
			finalizar();
		});
	}
}
