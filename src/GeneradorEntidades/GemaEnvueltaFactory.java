package GeneradorEntidades;

import Entidades.Entidad;
import Entidades.GemaEnvuelta;
import Logica.Color;
import Tablero.TableroJuego;

public class GemaEnvueltaFactory implements EntidadFactory {

	@Override
	public Entidad crear(TableroJuego tablero, int i, int j, String[] partes, String skin) {
		return new GemaEnvuelta(tablero, i, j, new Color(valor(partes)), true, skin);
	}
}
