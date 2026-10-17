package Score;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/** Reglas del TOP 5. Dónde se guarda lo decide el {@link RepositorioPuntajes} (SQLite o CSV). */
public class TopScore
{
	public static final int MAX_ELEMENTOS = 5;

	private final RepositorioPuntajes repositorio;
	private final PriorityQueue<Jugador> listadeJugadores;

	public TopScore() {
		this(FabricaRepositorioPuntajes.obtener());
	}

	public TopScore(RepositorioPuntajes repositorio) {
		this.repositorio = repositorio;
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
		repositorio.guardar(new ArrayList<>(listadeJugadores));
	}

	public void cargarLista() {
		listadeJugadores.clear();
		listadeJugadores.addAll(repositorio.cargar());
		while (listadeJugadores.size() > MAX_ELEMENTOS) {
			listadeJugadores.poll();
		}
	}
}
