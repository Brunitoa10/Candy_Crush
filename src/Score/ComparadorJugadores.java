package Score;

import java.util.Comparator;

/** Ordena de menor a mayor score (la cabeza de la PriorityQueue es el peor del top). */
public class ComparadorJugadores implements Comparator<Jugador> {
	@Override
	public int compare(Jugador jugador1, Jugador jugador2) {
		return Integer.compare(jugador1.get_score(), jugador2.get_score());
	}
}
