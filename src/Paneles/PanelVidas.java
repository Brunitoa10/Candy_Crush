package Paneles;

import GUI.PanelTranslucido;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import GUI.Estilo;
import GUI.RecursosGraficos;

public class PanelVidas extends PanelTranslucido implements Paneles {

	private static final long serialVersionUID = 1L;

	private static final int MAX_VIDAS = 3;
	private static final int TAMANO_CORAZON = 40;
	private static final String CORAZON_LLENO = "/assets/nivel/corazon.png";
	private static final String CORAZON_VACIO = "/assets/nivel/corazonVacio.png";

	private final CentralPaneles miCentral;
	private final JLabel[] corazones = new JLabel[MAX_VIDAS];

	public PanelVidas(CentralPaneles mi_central) {
		miCentral = mi_central;
	}

	@Override
	public void crearPanel() {
		setLayout(new GridLayout(1, MAX_VIDAS, 5, 5));
		setBackground(Estilo.TRANSPARENTE);
		for (int i = 0; i < MAX_VIDAS; i++) {
			corazones[i] = new JLabel(icono(CORAZON_LLENO));
			add(corazones[i]);
		}
	}

	@Override
	public void agregarAPanelPrincipal() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 0, 10, 0);
		miCentral.agregarConGBCs(this, gbc, 6, 6, 2, 1);
	}

	/** Pinta llenos tantos corazones como vidas queden (antes estaba fijo para 2 y 1 vida). */
	public void actualizarVidas() {
		int vidas = miCentral.getVidas();
		for (int i = 0; i < MAX_VIDAS; i++) {
			corazones[i].setIcon(icono(i < vidas ? CORAZON_LLENO : CORAZON_VACIO));
		}
		repaint();
	}

	private Icon icono(String ruta) {
		return RecursosGraficos.icono(ruta, TAMANO_CORAZON, TAMANO_CORAZON);
	}
}
