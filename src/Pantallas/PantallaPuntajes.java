package Pantallas;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;

import GUI.Estilo;
import Score.Jugador;
import Score.TopScore;

public class PantallaPuntajes extends PantallaBase {

	private static final long serialVersionUID = 1L;
	private static final int DIGITOS_SCORE = 5;
	private static final Color[] COLORES_PODIO = { Color.YELLOW, Color.GRAY, Color.ORANGE };

	public PantallaPuntajes(CentralPantallas mi_central) {
		super(mi_central);
	}

	@Override
	public void agregarComponentes() {
		JLabel titulo = Estilo.label("MEJORES PUNTAJES:", Estilo.FUENTE_PRINCIPAL, Font.PLAIN, 40, Estilo.COLOR_TEXTO);
		miCentral.agregarConGBCs(titulo, this, new GridBagConstraints(), 0, 0, 3, 1);

		// Antes se leía el archivo y se armaban los arrays en cada vuelta, y fallaba con menos de 5 jugadores
		List<Jugador> ranking = miCentral.obtenerRanking();
		for (int i = 0; i < ranking.size(); i++) {
			agregarFila(i, ranking.get(i));
		}

		JButton botonVolver = miCentral.crearBoton("Volver");
		miCentral.agregarFuncionalidadBotonVolver(this, botonVolver);
		miCentral.agregarConGBCs(botonVolver, this, new GridBagConstraints(), 0, TopScore.MAX_ELEMENTOS + 1, 3, 1);
	}

	private void agregarFila(int posicion, Jugador jugador) {
		Color color = posicion < COLORES_PODIO.length ? COLORES_PODIO[posicion] : Estilo.COLOR_TEXTO;
		int fila = posicion + 1;

		JLabel numero = Estilo.label((posicion + 1) + " - ", Estilo.FUENTE_PRINCIPAL, Font.PLAIN, 30, color);
		JLabel nombre = Estilo.label(jugador.get_nombre().toUpperCase(), Estilo.FUENTE_PRINCIPAL, Font.PLAIN, 30, color);
		JLabel puntaje = miCentral.crearLabel(Estilo.conCeros(jugador.get_score(), DIGITOS_SCORE), 30);

		miCentral.agregarConGBCs(numero, this, new GridBagConstraints(), 0, fila, 1, 1);
		miCentral.agregarConGBCs(nombre, this, new GridBagConstraints(), 1, fila, 1, 1);
		miCentral.agregarConGBCs(puntaje, this, new GridBagConstraints(), 2, fila, 1, 1);
	}
}
