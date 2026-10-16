package Pantallas;

public class PantallaDerrotaPorVidas extends PantallaMensaje {

	private static final long serialVersionUID = 1L;

	public PantallaDerrotaPorVidas(CentralPantallas central) {
		super(central, "PERDISTE", "Te quedaste sin vidas", "Vuelve a empezar desde el nivel 1");
	}
}
