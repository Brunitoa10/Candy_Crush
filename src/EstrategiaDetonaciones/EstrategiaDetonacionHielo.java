package EstrategiaDetonaciones;

import Entidades.Entidad;
import Tablero.Tablero;

public class EstrategiaDetonacionHielo extends EstrategiaDetonacionBase {

	@Override
	public void detonar(Entidad entidad, Tablero tablero) {
		entidad.get_gema_interna().detonar(tablero);
		entidad.aplicarDetonacionVisual();
	}
}
