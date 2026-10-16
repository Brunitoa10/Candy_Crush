package GeneradorEntidades;

import Entidades.Bomba;
import Entidades.Entidad;
import Logica.Color;
import Tablero.TableroJuego;

public class BombaFactory implements EntidadFactory {

	@Override
	public Entidad crear(TableroJuego tablero, int i, int j, String[] partes, String skin) {
		return new Bomba(tablero, i, j, new Color(valor(partes)), true, tablero.obtenerObserver(), tablero::notificarExplosionBomba);
	}
}
