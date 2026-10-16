package GeneradorEntidades;

/** Gema Cruzada dentro de un hielo. */
public class HieloGemaCruzadaFactory extends HieloFactory {

	public HieloGemaCruzadaFactory() {
		super(new GemaCruzadaFactory());
	}
}
