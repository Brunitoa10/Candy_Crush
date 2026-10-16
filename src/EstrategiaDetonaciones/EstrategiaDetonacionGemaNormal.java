package EstrategiaDetonaciones;

import Entidades.Entidad;
import Tablero.Tablero;

public class EstrategiaDetonacionGemaNormal extends EstrategiaDetonacionBase {

	@Override
	public void detonar(Entidad entidad, Tablero tablero) {
		entidad.aplicarDetonacionVisual();
		detonarAdyacentesAfectadas(entidad, tablero, ADYACENTES_ORTOGONALES);
	}
}
