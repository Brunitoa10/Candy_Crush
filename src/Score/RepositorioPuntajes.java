package Score;

import java.util.List;

/**
 * Persistencia del ranking (DIP): TopScore depende de esta interfaz y no de SQLite ni de archivos.
 */
public interface RepositorioPuntajes {

	/** Devuelve todos los jugadores guardados (sin orden garantizado). */
	public List<Jugador> cargar();

	/** Reemplaza el ranking guardado por la lista recibida. */
	public void guardar(List<Jugador> jugadores);
}
