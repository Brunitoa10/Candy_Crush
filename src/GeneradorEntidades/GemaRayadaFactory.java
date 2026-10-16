package GeneradorEntidades;

import Entidades.Entidad;
import Entidades.GemaRayada;
import Logica.Color;
import Tablero.TableroJuego;

/** Código de 2 dígitos: decena = dirección (5 horizontal / 1 vertical), unidad = color. Ej: "p,52". */
public class GemaRayadaFactory implements EntidadFactory {

	@Override
	public Entidad crear(TableroJuego tablero, int i, int j, String[] partes, String skin) {
		int codigo = valor(partes);
		return new GemaRayada(tablero, i, j, new Color(codigo % 10), codigo / 10, true, skin);
	}
}
