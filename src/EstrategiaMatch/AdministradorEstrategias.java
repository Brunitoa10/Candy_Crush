package EstrategiaMatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import Entidades.Entidad;
import Tablero.Tablero;

/**
 * Evalúa las estrategias de match del nivel (patrón Strategy + Chain of Responsibility):
 * se prueban en orden de prioridad y gana la primera que se cumple. Si ninguna aplica,
 * se rompen las líneas simples de 3 o más gemas.
 */
public class AdministradorEstrategias {

	private static final int MINIMO_MATCH = 3;

	protected LinkedList<Estrategias> listas_Estrategias;
	protected Tablero miTablero;

	public AdministradorEstrategias(LinkedList<Estrategias> estrategias, Tablero miTablero)
	{
		listas_Estrategias = new LinkedList<>(estrategias);
		listas_Estrategias.sort(Comparator.comparingInt(Estrategias::get_prioridad));
		this.miTablero = miTablero;
	}

	public LinkedList<Estrategias> get_lista_de_Estrategias()
	{
		return listas_Estrategias;
	}

	public Resultado buscar_match(Entidad entidad)
	{
		int fila = entidad.get_fila();
		int columna = entidad.get_columna();
		LinkedList<Entidad> enFila = buscarCombos(fila, columna, 0, 1);
		LinkedList<Entidad> enColumna = buscarCombos(fila, columna, 1, 0);
		return matchea(enFila, enColumna, entidad);
	}

	private Resultado matchea(LinkedList<Entidad> enFila, LinkedList<Entidad> enColumna, Entidad entidad)
	{
		// Antes, si la primera estrategia era MATCH 4 y no se cumplía, nunca se evaluaban L/T/S.
		for (Estrategias estrategia : listas_Estrategias) {
			Resultado resultado = estrategia.se_Cumple_la_Estrategia(enFila, enColumna, entidad);
			if (resultado != null) {
				return resultado;
			}
		}
		return lineasSimples(enFila, enColumna);
	}

	private Resultado lineasSimples(LinkedList<Entidad> enFila, LinkedList<Entidad> enColumna)
	{
		LinkedList<Entidad> aRomper = new LinkedList<>();
		if (enFila.size() >= MINIMO_MATCH) {
			aRomper.addAll(enFila);
		}
		if (enColumna.size() >= MINIMO_MATCH) {
			for (Entidad e : enColumna) {
				if (!aRomper.contains(e)) {
					aRomper.add(e);
				}
			}
		}
		return new Resultado(aRomper, null);
	}

	/**
	 * Busca gemas del mismo color que la de (fila, columna) en ambos sentidos de un eje.
	 * (deltaFila, deltaColumna) = (0, 1) recorre la fila; (1, 0) recorre la columna.
	 * Reemplaza a los dos métodos casi idénticos que había para fila y columna.
	 */
	private LinkedList<Entidad> buscarCombos(int fila, int columna, int deltaFila, int deltaColumna) {
		LinkedList<Entidad> combos = new LinkedList<>();
		Entidad entidadEnPosicion = miTablero.get_entidad(fila, columna);
		int colorBuscado = colorDe(entidadEnPosicion);
		if (!esColorDeGema(colorBuscado)) {
			return combos; // celdas vacías, rocas o hielo vacío no forman matches entre sí
		}

		agregarConsecutivas(combos, fila, columna, -deltaFila, -deltaColumna, colorBuscado);
		agregarConsecutivas(combos, fila, columna, deltaFila, deltaColumna, colorBuscado);

		if (combos.size() + 1 >= MINIMO_MATCH) {
			combos.add(entidadEnPosicion);
		} else {
			combos.clear();
		}
		return combos;
	}

	private void agregarConsecutivas(LinkedList<Entidad> combos, int fila, int columna, int df, int dc, int color) {
		int f = fila + df;
		int c = columna + dc;
		while (miTablero.en_rango(f, c) && colorDe(miTablero.get_entidad(f, c)) == color) {
			combos.add(miTablero.get_entidad(f, c));
			f += df;
			c += dc;
		}
	}

	/**
	 * Todas las líneas de 3 o más gemas del mismo color en el tablero (para las cascadas,
	 * después de que caen gemas nuevas). No repite entidades.
	 */
	public List<Entidad> buscarTodasLasLineas() {
		Set<Entidad> encontradas = new LinkedHashSet<>();
		for (int f = 0; f < miTablero.getFila(); f++) {
			agregarLineas(encontradas, f, 0, 0, 1);
		}
		for (int c = 0; c < miTablero.getColumna(); c++) {
			agregarLineas(encontradas, 0, c, 1, 0);
		}
		return new ArrayList<>(encontradas);
	}

	/** Recorre una fila o columna completa desde (f, c) agregando las secuencias de largo >= 3. */
	private void agregarLineas(Set<Entidad> encontradas, int f, int c, int df, int dc) {
		List<Entidad> secuencia = new ArrayList<>();
		int colorSecuencia = -1;
		while (miTablero.en_rango(f, c)) {
			Entidad entidad = miTablero.get_entidad(f, c);
			int color = colorDe(entidad);
			if (esColorDeGema(color) && color == colorSecuencia) {
				secuencia.add(entidad);
			} else {
				cerrarSecuencia(encontradas, secuencia);
				secuencia.clear();
				colorSecuencia = esColorDeGema(color) ? color : -1;
				if (colorSecuencia != -1) {
					secuencia.add(entidad);
				}
			}
			f += df;
			c += dc;
		}
		cerrarSecuencia(encontradas, secuencia);
	}

	private void cerrarSecuencia(Set<Entidad> encontradas, List<Entidad> secuencia) {
		if (secuencia.size() >= MINIMO_MATCH) {
			encontradas.addAll(secuencia);
		}
	}

	private boolean esColorDeGema(int color) {
		return color >= 1 && color <= Logica.Color.CANTIDAD_COLORES_GEMA;
	}

	/** El color "real" de una celda: si es un hielo, el de la gema que contiene. */
	private int colorDe(Entidad entidad) {
		return entidad.get_gema_interna().get_color();
	}
}
