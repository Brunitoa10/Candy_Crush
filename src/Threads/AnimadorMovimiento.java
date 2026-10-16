package Threads;

import javax.swing.Timer;

import GUI.Celda;

/** Desplaza la celda hasta la posición lógica de su entidad, de a "step" píxeles cada "delay" ms. */
public class AnimadorMovimiento extends AnimadorBase {

	protected final int step;
	protected final int delay;
	protected final int pos_x_destino;
	protected final int pos_y_destino;
	private Timer timer;

	public AnimadorMovimiento(CentralAnimaciones manager, int step, int delay, Celda celda) {
		this(manager, step, delay, celda, PrioridadAnimaciones.PRIORIDAD_INTERCAMBIO);
	}

	protected AnimadorMovimiento(CentralAnimaciones manager, int step, int delay, Celda celda, int prioridad) {
		super(manager, celda, prioridad);
		this.step = step;
		this.delay = delay;
		int size_label = celda.get_size_label();
		// el destino se toma al encolar: es la posición lógica en ese momento
		pos_x_destino = celda.get_entidad_logica().get_columna() * size_label;
		pos_y_destino = celda.get_entidad_logica().get_fila() * size_label;
	}

	@Override
	public void comenzar_animacion() {
		if (llegoAlDestino()) {
			finalizar();
			return;
		}
		timer = new Timer(delay, e -> ejecutarSeguro(this::avanzarUnPaso));
		timer.start();
	}

	private void avanzarUnPaso() {
		int x = acercar(celda_animada.getX(), pos_x_destino);
		int y = acercar(celda_animada.getY(), pos_y_destino);
		celda_animada.setLocation(x, y);
		if (llegoAlDestino()) {
			timer.stop();
			finalizar();
		}
	}

	private boolean llegoAlDestino() {
		return celda_animada.getX() == pos_x_destino && celda_animada.getY() == pos_y_destino;
	}

	/** Avanza un paso hacia el destino sin pasarse. */
	protected int acercar(int actual, int destino) {
		int distancia = destino - actual;
		return actual + Integer.signum(distancia) * Math.min(step, Math.abs(distancia));
	}
}
