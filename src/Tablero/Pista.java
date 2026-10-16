package Tablero;

import java.util.List;

import Entidades.Entidad;
import Logica.Direccion;

/**
 * Una jugada posible: mover la entidad de (fila, columna) hacia "direccion".
 * "entidadesAResaltar" son las gemas que el jugador debería ver parpadear.
 */
public class Pista {

	private final int fila;
	private final int columna;
	private final Direccion direccion;
	private final List<Entidad> entidadesAResaltar;

	public Pista(int fila, int columna, Direccion direccion, List<Entidad> entidadesAResaltar) {
		this.fila = fila;
		this.columna = columna;
		this.direccion = direccion;
		this.entidadesAResaltar = entidadesAResaltar;
	}

	public int getFila() {
		return fila;
	}

	public int getColumna() {
		return columna;
	}

	public Direccion getDireccion() {
		return direccion;
	}

	public List<Entidad> getEntidadesAResaltar() {
		return entidadesAResaltar;
	}
}
