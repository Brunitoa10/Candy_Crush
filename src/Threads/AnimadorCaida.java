package Threads;

import GUI.Celda;

/** Igual que un movimiento, pero con su propia prioridad (se ejecuta después de las detonaciones). */
public class AnimadorCaida extends AnimadorMovimiento {

	public AnimadorCaida(CentralAnimaciones manager, int step, int delay, Celda celda) {
		super(manager, step, delay, celda, PrioridadAnimaciones.PRIORIDAD_CAIDA);
	}
}
