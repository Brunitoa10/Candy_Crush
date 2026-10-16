package Threads;

/** Constantes de prioridad de animaciones (ahora final: antes eran modificables desde cualquier lado). */
public final class PrioridadAnimaciones {

	public static final int PRIORIDAD_INTERCAMBIO = 1015;
	public static final int PRIORIDAD_DETONACION = 1016;
	public static final int PRIORIDAD_CAIDA = 1017;
	public static final int PRIORIDAD_VISIBILIDAD = 1018;
	public static final int PRIORIDAD_FOCO = 1019;
	public static final int PRIORIDAD_SIN_PRIORIDAD = 5000;

	private PrioridadAnimaciones() {}
}
