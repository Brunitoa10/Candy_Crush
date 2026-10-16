package EstrategiaDetonaciones;

import java.util.ArrayList;
import java.util.List;

import Entidades.Entidad;
import Logica.Color;
import Tablero.Tablero;

/**
 * Comportamiento común a todas las estrategias de detonación (Template Method / helpers).
 * Las estrategias concretas solo definen QUÉ área afectan.
 */
public abstract class EstrategiaDetonacionBase implements EstategiaDetonacion {

	/** Vecinos ortogonales: arriba, abajo, izquierda, derecha. */
	protected static final int[][] ADYACENTES_ORTOGONALES = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };
	protected static final int[][] ADYACENTES_VERTICALES = { {-1, 0}, {1, 0} };
	protected static final int[][] ADYACENTES_HORIZONTALES = { {0, -1}, {0, 1} };

	/**
	 * Detona en cadena todas las entidades del área.
	 * 1) Registra el área en el tablero (score + objetivos).
	 * 2) Marca el origen como detonado ANTES de propagar, así dos potenciadores
	 *    en la misma línea no se detonan mutuamente en un bucle infinito.
	 * 3) Detona el resto de las entidades que todavía no fueron detonadas.
	 */
	protected void detonarArea(Entidad origen, Tablero tablero, List<Entidad> area) {
		tablero.detono(area);
		origen.aplicarDetonacionVisual();
		for (Entidad entidad : area) {
			if (!mismaPosicion(entidad, origen) && !estaVacia(entidad)) {
				entidad.detonar(tablero);
			}
		}
	}

	/** Detona los vecinos indicados que son afectados por explosiones adyacentes (rocas, bombas...). */
	protected void detonarAdyacentesAfectadas(Entidad origen, Tablero tablero, int[][] desplazamientos) {
		for (int[] d : desplazamientos) {
			int fila = origen.get_fila() + d[0];
			int columna = origen.get_columna() + d[1];
			if (tablero.hayEfectoExplosionAdyacente(fila, columna)) {
				Entidad vecina = tablero.get_entidad(fila, columna);
				if (!estaVacia(vecina)) {
					vecina.detonar(tablero);
				}
			}
		}
	}

	protected List<Entidad> fila(Tablero tablero, int fila) {
		List<Entidad> entidades = new ArrayList<>();
		for (int columna = 0; columna < tablero.getColumna(); columna++) {
			entidades.add(tablero.get_entidad(fila, columna));
		}
		return entidades;
	}

	protected List<Entidad> columna(Tablero tablero, int columna) {
		List<Entidad> entidades = new ArrayList<>();
		for (int fila = 0; fila < tablero.getFila(); fila++) {
			entidades.add(tablero.get_entidad(fila, columna));
		}
		return entidades;
	}

	/** Se compara por posición: si el origen está dentro de un Hielo, en el tablero figura el Hielo. */
	protected boolean mismaPosicion(Entidad a, Entidad b) {
		return a.get_fila() == b.get_fila() && a.get_columna() == b.get_columna();
	}

	protected boolean estaVacia(Entidad entidad) {
		return entidad.get_color() == Color.TRANSPARENTE;
	}
}
