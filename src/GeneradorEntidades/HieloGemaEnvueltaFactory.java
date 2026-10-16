package GeneradorEntidades;

/** Gema Envuelta dentro de un hielo. */
public class HieloGemaEnvueltaFactory extends HieloFactory {

	public HieloGemaEnvueltaFactory() {
		super(new GemaEnvueltaFactory());
	}
}
