package Pantallas;

public class PantallaFinDelJuego extends PantallaMensaje {

	private static final long serialVersionUID = 1L;

	public PantallaFinDelJuego(CentralPantallas central) {
		super(central, "GANASTE", "¡Felicitaciones! ¡Llegaste al final del juego!", "¡Gracias por jugar!");
	}
}
