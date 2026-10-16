package Threads;

import GUI.Celda;

public class AnimadorCambioFoco extends AnimadorBase {

	protected final String[] path_imagenes_estados;

	public AnimadorCambioFoco(CentralAnimaciones manager, Celda celda) {
		super(manager, celda, PrioridadAnimaciones.PRIORIDAD_FOCO);
		// se toma la imagen al encolar, para respetar el orden de los cambios
		path_imagenes_estados = celda.get_entidad_logica().get_imagenes_representativas();
	}

	@Override
	public void comenzar_animacion() {
		ejecutarSeguro(() -> celda_animada.mostrarImagenes(path_imagenes_estados));
		finalizar();
	}
}
