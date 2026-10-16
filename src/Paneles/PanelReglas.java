package Paneles;

import GUI.PanelTranslucido;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import GUI.Estilo;

public class PanelReglas extends PanelTranslucido implements Paneles {

	private static final long serialVersionUID = 1L;

	private final CentralPaneles miCentral;

	public PanelReglas(CentralPaneles mi_central) {
		miCentral = mi_central;
	}

	@Override
	public void crearPanel() {
		setSize(100, 100);
		setLayout(new GridBagLayout());
		setBackground(Estilo.FONDO_PANEL);
	}

	@Override
	public void agregarAPanelPrincipal() {
		mostrarReglas();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0;
		gbc.insets = new Insets(0, 0, 0, 10);
		miCentral.agregarConGBCs(this, gbc, 6, 1, 2, 1);
	}

	public void mostrarReglas() {
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 0, 0, 0);
		c.gridx = 0;
		c.gridy = 0;
		add(Estilo.label(" REGLAS:", Estilo.FUENTE_PRINCIPAL, Font.PLAIN, 20, Color.RED), c);

		for (String regla : miCentral.getNombresEstrategiasEnUso()) {
			c.gridy++;
			add(Estilo.label(regla, Estilo.FUENTE_PRINCIPAL, Font.PLAIN, 20, Estilo.COLOR_TEXTO), c);
		}
	}
}
