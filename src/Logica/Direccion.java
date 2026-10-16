package Logica;

/**
 * Direcciones posibles de movimiento/intercambio sobre el tablero.
 * Encapsula el desplazamiento en filas y columnas, evitando constantes "mágicas"
 * y que el Tablero dependa de la GUI.
 */
public enum Direccion {
	ARRIBA(-1, 0),
	ABAJO(1, 0),
	IZQUIERDA(0, -1),
	DERECHA(0, 1);

	private final int deltaFila;
	private final int deltaColumna;

	Direccion(int deltaFila, int deltaColumna) {
		this.deltaFila = deltaFila;
		this.deltaColumna = deltaColumna;
	}

	public int filaDestino(int filaOrigen) {
		return filaOrigen + deltaFila;
	}

	public int columnaDestino(int columnaOrigen) {
		return columnaOrigen + deltaColumna;
	}
}
