package EstrategiaDetonaciones;

import Entidades.Entidad;
import Tablero.Tablero;

public class EstrategiaDetonacionRocas extends EstrategiaDetonacionBase {

	@Override
	public void detonar(Entidad entidad, Tablero tablero) {
		entidad.aplicarDetonacionVisual();
	}
}
