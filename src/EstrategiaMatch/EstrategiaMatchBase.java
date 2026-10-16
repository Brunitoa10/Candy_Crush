package EstrategiaMatch;

import java.util.LinkedList;
import java.util.List;

import Entidades.Entidad;

/**
 * Base común de las estrategias de match: prioridad, nombre de la regla y utilidades.
 * Elimina el código que estaba copiado en cada estrategia.
 */
public abstract class EstrategiaMatchBase implements Estrategias {

	protected int prioridad;
	private final String nombreRegla;

	protected EstrategiaMatchBase(int prioridad, String nombreRegla) {
		this.prioridad = prioridad;
		this.nombreRegla = nombreRegla;
	}

	@Override
	public void set_prioridad(int prioridad) {
		this.prioridad = prioridad;
	}

	@Override
	public int get_prioridad() {
		return prioridad;
	}

	@Override
	public String get_NombreRegla() {
		return nombreRegla;
	}

	protected LinkedList<Entidad> unir(List<Entidad> lista1, List<Entidad> lista2) {
		LinkedList<Entidad> union = new LinkedList<>(lista1);
		union.addAll(lista2);
		return union;
	}
}
