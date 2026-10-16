package EstrategiaDetonaciones;

import java.util.List;

import Entidades.Entidad;
import Tablero.Tablero;

/** Detona la fila y la columna completas de la gema. */
public class EstrategiaDetonacionGemaCruz extends EstrategiaDetonacionBase {

	@Override
	public void detonar(Entidad entidad, Tablero tablero) {
		List<Entidad> area = fila(tablero, entidad.get_fila());
		for (Entidad e : columna(tablero, entidad.get_columna())) {
			if (e.get_fila() != entidad.get_fila()) { // la intersección ya está en la fila
				area.add(e);
			}
		}
		detonarArea(entidad, tablero, area);
	}
}
