package EstrategiaDetonaciones;

import java.util.ArrayList;
import java.util.List;

import Entidades.Entidad;
import Tablero.Tablero;

/** Detona el área de 3x3 alrededor de la gema. */
public class EstrategiaDetonacionGemaEnvuelta extends EstrategiaDetonacionBase {

	@Override
	public void detonar(Entidad entidad, Tablero tablero) {
		List<Entidad> area = new ArrayList<>();
		for (int f = entidad.get_fila() - 1; f <= entidad.get_fila() + 1; f++) {
			for (int c = entidad.get_columna() - 1; c <= entidad.get_columna() + 1; c++) {
				if (tablero.en_rango(f, c)) {
					area.add(tablero.get_entidad(f, c));
				}
			}
		}
		detonarArea(entidad, tablero, area);
	}
}
