package Score;

import java.util.List;
import java.util.PriorityQueue;

/**
 * Lleva el score de la partida y consulta el TOP 5.
 * Ya no conoce a la GUI (antes guardaba una referencia a CentralPaneles que quedaba
 * desactualizada al cambiar de nivel y el score dejaba de refrescarse).
 */
public class AdministradordeScore {
	protected final TopScore topScore;
	protected int scoreActual;

	public AdministradordeScore()
	{
		topScore = new TopScore();
		scoreActual = 0;
	}

	public int getScore()
	{
		return scoreActual;
	}

	public void agregarScore(int puntos)
	{
		scoreActual += puntos;
	}

	public boolean entro_en_el_top5()
	{
		return topScore.entraEnElTop(scoreActual);
	}

	public PriorityQueue<Jugador> obtenerListadeJugadores()
	{
		return topScore.obtenerListadeJugadores();
	}

	/** Ranking de mayor a menor (puede tener menos de 5 jugadores). */
	public List<Jugador> obtenerRanking()
	{
		return topScore.obtenerRanking();
	}

	public void mejorJugador(String nombre_del_Jugador)
	{
		topScore.agregarJugador(nombre_del_Jugador, scoreActual);
	}
}
