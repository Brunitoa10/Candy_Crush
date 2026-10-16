package EstrategiaMatch;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Factory de estrategias de match a partir del caracter usado en los archivos de nivel.
 * Crea una instancia nueva por pedido (las estrategias tienen prioridad mutable).
 */
public final class EstrategiaFactory {

	private static final Map<Character, Supplier<Estrategias>> ESTRATEGIAS = new HashMap<>();

	static {
		ESTRATEGIAS.put('4', EstrategiaMatch4::new);
		ESTRATEGIAS.put('l', EstrategiaMatchL::new);
		ESTRATEGIAS.put('s', EstrategiaMatchS::new);
		ESTRATEGIAS.put('t', EstrategiaMatchT::new);
	}

	private EstrategiaFactory() {}

	public static Estrategias createEstrategia(char tipo) {
		Supplier<Estrategias> creador = ESTRATEGIAS.get(Character.toLowerCase(tipo));
		if (creador == null) {
			throw new IllegalArgumentException("Estrategia de match desconocida: '" + tipo + "'");
		}
		return creador.get();
	}
}
