package Logica;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import Entidades.Entidad;

/**
 * Resultado de un intercambio: qué entidades se detonan y qué potenciadores se generan.
 * Las entidades no se repiten (antes, si dos matches compartían gemas, se detonaban
 * y puntuaban dos veces).
 */
public class EfectosDeTransicion {

	protected final Set<Entidad> entidades_a_detonar = new LinkedHashSet<>();
	protected final List<Entidad> potenciadores = new ArrayList<>();

	public void agregar_entidad_a_detonar(Entidad entidad) {
		entidades_a_detonar.add(entidad);
	}

	/** Compatibilidad con el nombre anterior: el reemplazo ahora lo resuelve la gravedad. */
	public void agregar_entidad_a_detonar_y_reemplazar(Entidad entidad) {
		agregar_entidad_a_detonar(entidad);
	}

	public void agregar_potenciador(Entidad potenciador) {
		potenciadores.add(potenciador);
	}

	public boolean existen_entidades_a_detonar() {
		return !entidades_a_detonar.isEmpty();
	}

	public List<Entidad> entidades_a_detonar() {
		return new ArrayList<>(entidades_a_detonar);
	}

	public List<Entidad> potenciadores() {
		return potenciadores;
	}
}
