package Tablero;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import Entidades.Entidad;
import Logica.Color;
import Logica.Direccion;

/**
 * Busca intercambios que produzcan un match, sin modificar el tablero: simula el
 * intercambio sobre una copia de los colores (SRP: el Tablero no se ensucia con esto).
 * Se usa para las pistas que parpadean cuando el jugador se queda quieto.
 */
public class BuscadorDeJugadas {

	private static final int MINIMO_MATCH = 3;
	private static final Direccion[] DIRECCIONES = { Direccion.DERECHA, Direccion.ABAJO };

	private final Tablero tablero;
	private final Random random = new Random();

	public BuscadorDeJugadas(Tablero tablero) {
		this.tablero = tablero;
	}

	/** @return una jugada posible elegida al azar, o null si no hay ninguna. */
	public Pista buscarPista() {
		List<Pista> jugadas = buscarTodas();
		return jugadas.isEmpty() ? null : jugadas.get(random.nextInt(jugadas.size()));
	}

	public List<Pista> buscarTodas() {
		List<Pista> jugadas = new ArrayList<>();
		int[][] colores = leerColores();
		for (int f = 0; f < tablero.getFila(); f++) {
			for (int c = 0; c < tablero.getColumna(); c++) {
				for (Direccion d : DIRECCIONES) {
					Pista pista = evaluar(colores, f, c, d);
					if (pista != null) {
						jugadas.add(pista);
					}
				}
			}
		}
		return jugadas;
	}

	private Pista evaluar(int[][] colores, int f, int c, Direccion d) {
		int fd = d.filaDestino(f);
		int cd = d.columnaDestino(c);
		if (!tablero.en_rango(fd, cd)) {
			return null;
		}
		Entidad origen = tablero.get_entidad(f, c);
		Entidad destino = tablero.get_entidad(fd, cd);
		if (!origen.es_posible_intercambiar(destino)) {
			return null;
		}
		if (origen.se_produce_match_con(destino)) { // combinación de dos especiales
			return new Pista(f, c, d, Arrays.asList(origen, destino));
		}

		intercambiar(colores, f, c, fd, cd);
		Set<int[]> lineaDestino = lineaEn(colores, fd, cd);
		Set<int[]> lineaOrigen = lineaEn(colores, f, c);
		intercambiar(colores, f, c, fd, cd); // se deshace la simulación

		if (lineaDestino.isEmpty() && lineaOrigen.isEmpty()) {
			return null;
		}
		// Se resaltan las gemas que quedan en línea más la que hay que mover
		// (la celda destino de la simulación todavía tiene a la otra gema).
		Set<Entidad> resaltar = new LinkedHashSet<>();
		agregarLinea(resaltar, lineaDestino, fd, cd, origen);
		agregarLinea(resaltar, lineaOrigen, f, c, destino);
		return new Pista(f, c, d, new ArrayList<>(resaltar));
	}

	private void agregarLinea(Set<Entidad> resaltar, Set<int[]> linea, int filaMovida, int columnaMovida, Entidad movida) {
		for (int[] pos : linea) {
			boolean esLaCeldaMovida = pos[0] == filaMovida && pos[1] == columnaMovida;
			resaltar.add(esLaCeldaMovida ? movida : tablero.get_entidad(pos[0], pos[1]));
		}
	}

	/** Celdas de la fila y/o columna de (f, c) que forman 3 o más del mismo color. */
	private Set<int[]> lineaEn(int[][] colores, int f, int c) {
		Set<int[]> linea = new LinkedHashSet<>();
		int color = colores[f][c];
		if (color < 0) {
			return linea;
		}
		agregarSiEsLinea(linea, colores, f, c, 0, 1, color);
		agregarSiEsLinea(linea, colores, f, c, 1, 0, color);
		return linea;
	}

	private void agregarSiEsLinea(Set<int[]> linea, int[][] colores, int f, int c, int df, int dc, int color) {
		List<int[]> eje = new ArrayList<>();
		eje.add(new int[] { f, c });
		for (int sentido : new int[] { -1, 1 }) {
			int nf = f + sentido * df, nc = c + sentido * dc;
			while (tablero.en_rango(nf, nc) && colores[nf][nc] == color) {
				eje.add(new int[] { nf, nc });
				nf += sentido * df;
				nc += sentido * dc;
			}
		}
		if (eje.size() >= MINIMO_MATCH) {
			linea.addAll(eje);
		}
	}

	/** Colores "jugables" de cada celda (hielo -> color de su gema); -1 si no participa de matches. */
	private int[][] leerColores() {
		int[][] colores = new int[tablero.getFila()][tablero.getColumna()];
		for (int f = 0; f < tablero.getFila(); f++) {
			for (int c = 0; c < tablero.getColumna(); c++) {
				int color = tablero.get_entidad(f, c).get_gema_interna().get_color();
				colores[f][c] = (color >= 1 && color <= Color.CANTIDAD_COLORES_GEMA) ? color : -1;
			}
		}
		return colores;
	}

	private static void intercambiar(int[][] m, int f1, int c1, int f2, int c2) {
		int aux = m[f1][c1];
		m[f1][c1] = m[f2][c2];
		m[f2][c2] = aux;
	}
}
