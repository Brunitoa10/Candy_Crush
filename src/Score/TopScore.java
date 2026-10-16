package Score;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

/** Persistencia del TOP 5 en un CSV "nombre,score". */
public class TopScore
{
	public static final int MAX_ELEMENTOS = 5;
	private static final String ARCHIVO_DE_GUARDADO = "TOP5.csv";

	private final PriorityQueue<Jugador> listadeJugadores;

	public TopScore() {
		listadeJugadores = new PriorityQueue<>(MAX_ELEMENTOS, new ComparadorJugadores());
	}

	public void agregarJugador(String nombre, int score) {
		cargarLista();
		listadeJugadores.add(new Jugador(nombre, score));
		while (listadeJugadores.size() > MAX_ELEMENTOS) {
			listadeJugadores.poll(); // sale el de menor score
		}
		guardarLista();
	}

	public PriorityQueue<Jugador> obtenerListadeJugadores()
	{
		cargarLista();
		return new PriorityQueue<>(listadeJugadores); // copia: quien la consuma no rompe el estado interno
	}

	/** Ranking ordenado de mayor a menor score. */
	public List<Jugador> obtenerRanking() {
		cargarLista();
		List<Jugador> ranking = new ArrayList<>(listadeJugadores);
		ranking.sort(Collections.reverseOrder(new ComparadorJugadores()));
		return ranking;
	}

	public boolean entraEnElTop(int score) {
		cargarLista();
		return score > 0 && (listadeJugadores.size() < MAX_ELEMENTOS || listadeJugadores.peek().get_score() <= score);
	}

	public void guardarLista() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_DE_GUARDADO, false))) {
			for (Jugador jugador : listadeJugadores) {
				writer.write(jugador.get_nombre() + "," + jugador.get_score());
				writer.newLine();
			}
		} catch (IOException e) {
			System.err.println("No se pudo guardar el TOP 5: " + e.getMessage());
		}
	}

	public void cargarLista() {
		listadeJugadores.clear();
		File archivo = new File(ARCHIVO_DE_GUARDADO);
		if (!archivo.exists()) {
			return; // primera partida: todavía no hay ranking
		}
		try (Scanner scanner = new Scanner(archivo, "UTF-8")) {
			while (scanner.hasNextLine()) {
				agregarDesdeLinea(scanner.nextLine());
			}
		} catch (IOException e) {
			System.err.println("No se pudo leer el TOP 5: " + e.getMessage());
		}
	}

	private void agregarDesdeLinea(String linea) {
		String[] partes = linea.split(",");
		if (partes.length < 2) {
			return; // línea vacía o corrupta
		}
		try {
			listadeJugadores.add(new Jugador(partes[0].trim(), Integer.parseInt(partes[1].trim())));
		} catch (NumberFormatException e) {
			// se ignora la línea inválida
		}
	}
}
