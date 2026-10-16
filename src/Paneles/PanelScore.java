package Paneles;

import GUI.PanelTranslucido;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import GUI.Estilo;

public class PanelScore extends PanelTranslucido implements Paneles {

	private static final long serialVersionUID = 1L;
	private static final int DIGITOS = 4;

	private final CentralPaneles miCentral;
	private JLabel scoreLabel;

	public PanelScore(CentralPaneles mi_central) {
		miCentral = mi_central;
	}

	@Override
	public void crearPanel() {
		setBackground(Estilo.FONDO_PANEL);
		scoreLabel = Estilo.label(texto(miCentral.getScore()), 40);
		add(scoreLabel);
	}

	@Override
	public void agregarAPanelPrincipal() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 0, 0, 0);
		miCentral.agregarConGBCs(this, gbc, 2, 0, 4, 1);
	}

	public void actualizarScore(int score)
	{
		scoreLabel.setText(texto(score));
	}

	private String texto(int score) {
		return "SCORE: " + Estilo.conCeros(score, DIGITOS);
	}
}
