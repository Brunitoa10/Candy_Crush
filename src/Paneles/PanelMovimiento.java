package Paneles;

import GUI.PanelTranslucido;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import GUI.Estilo;

public class PanelMovimiento extends PanelTranslucido implements Paneles {

	private static final long serialVersionUID = 1L;

	private final CentralPaneles miCentral;
	private JLabel movimientosLabel;

	public PanelMovimiento(CentralPaneles mi_central) {
		miCentral = mi_central;
	}

	@Override
	public void crearPanel() {
		setBackground(Estilo.FONDO_PANEL);
		movimientosLabel = Estilo.label(texto(miCentral.getCantidadDeMovimientos()), 20);
		add(movimientosLabel);
	}

	@Override
	public void agregarAPanelPrincipal() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0;
		gbc.insets = new Insets(0, 0, 0, 10);
		miCentral.agregarConGBCs(this, gbc, 6, 0, 2, 1);
	}

	public void actualizarMovimientos(int movimientosRestantes) {
		movimientosLabel.setText(texto(movimientosRestantes));
	}

	private String texto(int movimientos) {
		return "Movimientos restantes: " + movimientos;
	}
}
