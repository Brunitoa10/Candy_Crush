package EstrategiaDetonaciones;

import Entidades.Entidad;
import Tablero.Tablero;

/** Detona la fila completa y los obstáculos pegados arriba/abajo de la gema. */
public class EstrategiaDetonacionGemaRayadaHorizontal extends EstrategiaDetonacionBase {

	@Override
	public void detonar(Entidad entidad, Tablero tablero) {
		detonarAdyacentesAfectadas(entidad, tablero, ADYACENTES_VERTICALES);
		detonarArea(entidad, tablero, fila(tablero, entidad.get_fila()));
	}
}
