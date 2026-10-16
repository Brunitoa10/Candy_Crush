package Pantallas;

public class PantallaDerrotaPorMovimientos extends PantallaMensaje {

	private static final long serialVersionUID = 1L;

	public PantallaDerrotaPorMovimientos(CentralPantallas central) {
		super(central, "Reintentar", central::agregarFuncionalidadBotonReiniciar,
				"PERDISTE", "Te quedaste sin movimientos", "Pierdes una vida");
	}
}
