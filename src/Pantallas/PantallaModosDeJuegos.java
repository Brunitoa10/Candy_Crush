package Pantallas;

import javax.swing.JButton;

import GUI.Estilo;
import GUI.RecursosGraficos;

public class PantallaModosDeJuegos extends PantallaBase {

	private static final long serialVersionUID = 1L;
	private static final int TAMANO_IMAGEN = 500;

	public PantallaModosDeJuegos(CentralPantallas mi_central) {
		super(mi_central);
	}

	@Override
	public void agregarComponentes() {
		miCentral.agregarConGBCs(miCentral.crearLabel("Seleccionar modo de juego", 30), this, new java.awt.GridBagConstraints(), 0, 0, 3, 1);
		agregarModo("original", 0);
		agregarModo("halloween", 2);
	}

	private void agregarModo(String skin, int columna) {
		JButton boton = crearBotonConImagen("/assets/modosDeJuego/" + skin + ".png");
		miCentral.agregarConGBCs(boton, this, new java.awt.GridBagConstraints(), columna, 1, 1, 1);
		miCentral.agregarFuncionalidadSkin(this, boton, skin);
	}

	private JButton crearBotonConImagen(String imagePath) {
		JButton boton = new JButton(RecursosGraficos.icono(imagePath, TAMANO_IMAGEN, TAMANO_IMAGEN));
		boton.setSize(TAMANO_IMAGEN, TAMANO_IMAGEN);
		boton.setOpaque(false);
		boton.setContentAreaFilled(false);
		boton.setFocusable(false);
		boton.setBorderPainted(false);
		boton.setRolloverEnabled(false);
		boton.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR)); // indica que es clickeable
		boton.setBackground(Estilo.TRANSPARENTE);
		return boton;
	}
}
