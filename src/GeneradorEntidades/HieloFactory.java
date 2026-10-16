package GeneradorEntidades;

import Entidades.Entidad;
import Entidades.Hielo;
import Logica.Color;
import Tablero.TableroJuego;

/**
 * Envuelve en hielo a la entidad creada por otra factory (Decorator sobre las factories).
 * Reemplaza la lógica duplicada de las cuatro factories "HieloGema...".
 */
public class HieloFactory implements EntidadFactory {

	private final EntidadFactory factoryInterna;

	public HieloFactory(EntidadFactory factoryInterna) {
		this.factoryInterna = factoryInterna;
	}

	@Override
	public Entidad crear(TableroJuego tablero, int i, int j, String[] partes, String skin) {
		Entidad interna = factoryInterna.crear(tablero, i, j, partes, skin);
		return new Hielo(tablero, i, j, new Color(Color.HIELO), false, interna);
	}
}
