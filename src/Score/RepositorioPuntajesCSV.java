package Score;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** Ranking en un CSV "nombre,score". Respaldo si falta el driver de SQLite y origen de la migración. */
public class RepositorioPuntajesCSV implements RepositorioPuntajes {

	private final File archivo;

	public RepositorioPuntajesCSV(File archivo) {
		this.archivo = archivo;
	}

	public boolean existe() {
		return archivo.isFile() && archivo.length() > 0;
	}

	@Override
	public List<Jugador> cargar() {
		List<Jugador> jugadores = new ArrayList<>();
		if (!archivo.exists()) {
			return jugadores; // primera partida: todavía no hay ranking
		}
		try (Scanner scanner = new Scanner(archivo, "UTF-8")) {
			while (scanner.hasNextLine()) {
				Jugador jugador = desdeLinea(scanner.nextLine());
				if (jugador != null) {
					jugadores.add(jugador);
				}
			}
		} catch (IOException e) {
			System.err.println("No se pudo leer el TOP 5: " + e.getMessage());
		}
		return jugadores;
	}

	@Override
	public void guardar(List<Jugador> jugadores) {
		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(archivo, false), StandardCharsets.UTF_8))) {
			for (Jugador jugador : jugadores) {
				writer.write(jugador.get_nombre() + "," + jugador.get_score());
				writer.newLine();
			}
		} catch (IOException e) {
			System.err.println("No se pudo guardar el TOP 5: " + e.getMessage());
		}
	}

	private static Jugador desdeLinea(String linea) {
		String[] partes = linea.split(",");
		if (partes.length < 2) {
			return null; // línea vacía o corrupta
		}
		try {
			return new Jugador(partes[0].trim(), Integer.parseInt(partes[1].trim()));
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
