package Score;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * Elige la persistencia (Factory): SQLite si el driver está disponible; si no, CSV.
 * La primera vez que se usa SQLite importa el TOP5.csv viejo para no perder el ranking.
 */
public final class FabricaRepositorioPuntajes {

	private static final String BASE = "candycrush.db";
	private static final String CSV = "TOP5.csv";
	private static RepositorioPuntajes instancia;

	private FabricaRepositorioPuntajes() {
	}

	public static synchronized RepositorioPuntajes obtener() {
		if (instancia == null) {
			instancia = crear();
		}
		return instancia;
	}

	private static RepositorioPuntajes crear() {
		RepositorioPuntajesCSV csv = new RepositorioPuntajesCSV(RutasDeDatos.archivo(CSV));
		if (!RepositorioPuntajesSQLite.disponible()) {
			System.err.println("SQLite no disponible: el TOP 5 se guarda en " + CSV);
			return csv;
		}
		try {
			File base = RutasDeDatos.archivo(BASE);
			RepositorioPuntajesSQLite sqlite = new RepositorioPuntajesSQLite(base);
			migrarSiHaceFalta(sqlite);
			return sqlite;
		} catch (SQLException e) {
			System.err.println("No se pudo abrir la base SQLite (" + e.getMessage() + "), se usa " + CSV);
			return csv;
		}
	}

	/** Copia el ranking de un TOP5.csv anterior (en data\ o en la carpeta del juego) si la base está vacía. */
	private static void migrarSiHaceFalta(RepositorioPuntajesSQLite sqlite) {
		if (!sqlite.cargar().isEmpty()) {
			return;
		}
		for (File candidato : new File[] { RutasDeDatos.archivo(CSV), new File(CSV) }) {
			RepositorioPuntajesCSV viejo = new RepositorioPuntajesCSV(candidato);
			if (viejo.existe()) {
				List<Jugador> jugadores = viejo.cargar();
				if (!jugadores.isEmpty()) {
					sqlite.guardar(jugadores);
					return;
				}
			}
		}
	}
}
