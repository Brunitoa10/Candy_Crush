package Threads;

import javax.swing.Timer;

import GUI.Celda;

/**
 * Base de los animadores. Todas las animaciones corren en el hilo de Swing usando
 * javax.swing.Timer (antes cada animación era un Thread que tocaba componentes Swing
 * desde afuera del EDT, lo que producía parpadeos, excepciones y bloqueos).
 */
public abstract class AnimadorBase implements Animador {

	protected final CentralAnimaciones manager;
	protected final Celda celda_animada;
	protected int prioridad;
	private boolean finalizado;

	protected AnimadorBase(CentralAnimaciones manager, Celda celda, int prioridad) {
		this.manager = manager;
		this.celda_animada = celda;
		this.prioridad = prioridad;
	}

	@Override
	public Celda get_celda_asociada() {
		return celda_animada;
	}

	@Override
	public int get_prioridad() {
		return prioridad;
	}

	/** Notifica el fin una única vez, pase lo que pase (si no, se traban los intercambios). */
	protected final void finalizar() {
		if (!finalizado) {
			finalizado = true;
			manager.notificarse_finalizacion_animador(this);
		}
	}

	/** Ejecuta una acción una sola vez dentro de "delayMs" milisegundos, en el hilo de Swing. */
	protected void despues(int delayMs, Runnable accion) {
		Timer timer = new Timer(delayMs, e -> ejecutarSeguro(accion));
		timer.setRepeats(false);
		timer.start();
	}

	protected void ejecutarSeguro(Runnable accion) {
		try {
			accion.run();
		} catch (RuntimeException e) {
			e.printStackTrace();
			finalizar();
		}
	}
}
