package EstrategiaMatch;

import java.util.LinkedList;

import Entidades.Entidad;
import Entidades.GemaEnvuelta;
import Logica.Color;

/**
 * Template Method para los matches con forma (L, T, S): todos requieren al menos 3 gemas
 * en horizontal y 3 en vertical, y todos generan una GemaEnvuelta. Cada subclase solo
 * define la condición geométrica de su forma.
 */
public abstract class EstrategiaMatchEnvuelta extends EstrategiaMatchBase {

	private static final int MINIMO_POR_EJE = 3;

	protected EstrategiaMatchEnvuelta(int prioridad, String nombreRegla) {
		super(prioridad, nombreRegla);
	}

	@Override
	public final Resultado se_Cumple_la_Estrategia(LinkedList<Entidad> horizontal, LinkedList<Entidad> vertical, Entidad origen) {
		if (horizontal.size() < MINIMO_POR_EJE || vertical.size() < MINIMO_POR_EJE || !cumpleForma(horizontal, vertical)) {
			return null;
		}
		Entidad gema = origen.get_gema_interna();
		Entidad gemaCreada = new GemaEnvuelta(gema.get_TableroNotificable(), gema.get_fila(), gema.get_columna(),
				new Color(gema.get_color()), false, gema.get_Skin());
		return new Resultado(unir(horizontal, vertical), gemaCreada);
	}

	/** Condición geométrica propia de cada forma. */
	protected abstract boolean cumpleForma(LinkedList<Entidad> horizontal, LinkedList<Entidad> vertical);
}
