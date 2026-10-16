package EstrategiaMatch;

import java.util.LinkedList;

import Entidades.Entidad;
import Entidades.GemaRayada;
import Logica.Color;

/** 4 en línea: genera una gema rayada en la dirección del match. */
public class EstrategiaMatch4 extends EstrategiaMatchBase {

	private static final int LARGO_MATCH = 4;
	private static final int MAXIMO_EJE_OPUESTO = 3;

	public EstrategiaMatch4() {
		super(1, "MATCH 4 VALIDO");
	}

	@Override
	public Resultado se_Cumple_la_Estrategia(LinkedList<Entidad> horizontal, LinkedList<Entidad> vertical, Entidad origen) {
		if (horizontal.size() == LARGO_MATCH && vertical.size() < MAXIMO_EJE_OPUESTO) {
			return crearResultado(horizontal, GemaRayada.HORIZONTAL);
		}
		if (vertical.size() == LARGO_MATCH && horizontal.size() < MAXIMO_EJE_OPUESTO) {
			// Antes se devolvía la lista horizontal (vacía) y el match vertical no rompía gemas
			return crearResultado(vertical, GemaRayada.VERTICAL);
		}
		return null;
	}

	private Resultado crearResultado(LinkedList<Entidad> linea, int direccion) {
		Entidad aux = linea.getFirst().get_gema_interna(); // si está dentro de un hielo, se usa la gema
		Entidad gemaCreada = new GemaRayada(aux.get_TableroNotificable(), aux.get_fila(), aux.get_columna(),
				new Color(aux.get_color()), direccion, false, aux.get_Skin());
		return new Resultado(linea, gemaCreada);
	}
}
