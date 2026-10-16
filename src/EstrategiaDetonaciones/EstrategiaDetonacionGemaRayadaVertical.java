package EstrategiaDetonaciones;

import Entidades.Entidad;
import Tablero.Tablero;

/** Detona la columna completa y los obstáculos pegados a izquierda/derecha de la gema. */
public class EstrategiaDetonacionGemaRayadaVertical extends EstrategiaDetonacionBase {

	@Override
	public void detonar(Entidad entidad, Tablero tablero) {
		detonarAdyacentesAfectadas(entidad, tablero, ADYACENTES_HORIZONTALES);
		detonarArea(entidad, tablero, columna(tablero, entidad.get_columna()));
	}
}
