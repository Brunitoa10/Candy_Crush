package GeneradorEntidades;

import Entidades.Entidad;
import Tablero.TableroJuego;

/**
 * Crea una entidad a partir de un token del archivo de nivel, por ejemplo "n,3" -> partes = {"n", "3"}.
 */
public interface EntidadFactory {

	Entidad crear(TableroJuego tablero, int i, int j, String[] partes, String skin);

	/** Valor numérico del token (color o código de la entidad). */
	default int valor(String[] partes) {
		if (partes.length < 2) {
			throw new IllegalArgumentException("Token de entidad sin valor: " + String.join(",", partes));
		}
		return Integer.parseInt(partes[1].trim());
	}
}
