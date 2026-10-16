package GeneradorEntidades;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Registro de factories por prefijo del archivo de nivel.
 * Para agregar una entidad nueva alcanza con registrarla acá (Open/Closed).
 */
public final class EntidadFactoryRegistry {

	public static final String GEMA_NORMAL = "n";

	private static final Map<String, EntidadFactory> FACTORIES = new HashMap<>();

	static {
		FACTORIES.put(GEMA_NORMAL, new GemaNormalFactory());
		FACTORIES.put("r", new RocaFactory());
		FACTORIES.put("b", new BombaFactory());
		FACTORIES.put("p", new GemaRayadaFactory());
		FACTORIES.put("e", new GemaEnvueltaFactory());
		FACTORIES.put("c", new GemaCruzadaFactory());
		FACTORIES.put("hn", new HieloGemaNormalFactory());
		FACTORIES.put("hp", new HieloGemaRayadaFactory());
		FACTORIES.put("he", new HieloGemaEnvueltaFactory());
		FACTORIES.put("hc", new HieloGemaCruzadaFactory());
	}

	private EntidadFactoryRegistry() {}

	public static Map<String, EntidadFactory> getEntidadFactories() {
		return Collections.unmodifiableMap(FACTORIES);
	}

	public static EntidadFactory obtener(String prefijo) {
		EntidadFactory factory = FACTORIES.get(prefijo.trim());
		if (factory == null) {
			throw new IllegalArgumentException("Tipo de entidad desconocido en el nivel: '" + prefijo + "'");
		}
		return factory;
	}

	public static String[] getFactoryKeys() {
		return FACTORIES.keySet().toArray(new String[0]);
	}
}
