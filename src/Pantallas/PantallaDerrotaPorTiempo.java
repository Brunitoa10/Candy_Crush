package Pantallas;

public class PantallaDerrotaPorTiempo extends PantallaMensaje {

	private static final long serialVersionUID = 1L;

	public PantallaDerrotaPorTiempo(CentralPantallas central) {
		super(central, "Reintentar", central::agregarFuncionalidadBotonReiniciar,
				"PERDISTE", "Te quedaste sin tiempo", "Pierdes una vida");
	}
}
