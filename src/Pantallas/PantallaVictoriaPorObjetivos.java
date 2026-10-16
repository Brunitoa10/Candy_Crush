package Pantallas;

public class PantallaVictoriaPorObjetivos extends PantallaMensaje {

	private static final long serialVersionUID = 1L;

	public PantallaVictoriaPorObjetivos(CentralPantallas central) {
		super(central, "Siguiente Nivel", central::agregarFuncionalidadBotonSiguienteNivel,
				"GANASTE", "Felicidades! Avanza al siguiente nivel");
	}
}
