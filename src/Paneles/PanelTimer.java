package Paneles;

import GUI.PanelTranslucido;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import GUI.Estilo;

public class PanelTimer extends PanelTranslucido implements Paneles {

	private static final long serialVersionUID = 1L;
	private static final int DIGITOS = 3;

	private final CentralPaneles miCentral;
	private JLabel timerLabel;

	public PanelTimer(CentralPaneles mi_central) {
		miCentral = mi_central;
	}

	@Override
	public void crearPanel() {
		setBackground(new Color(0, 0, 0, 220));
		timerLabel = Estilo.label(texto(miCentral.getTiempoRestante()), 20);
		add(timerLabel);
	}

	@Override
	public void agregarAPanelPrincipal() {
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 10, 0, 0);
		miCentral.agregarConGBCs(this, c, 0, 0, 3, 1);
	}

	public void actualizarTimer(int tiempo) {
		timerLabel.setText(texto(tiempo));
		repaint();
		revalidate();
	}

	private String texto(int tiempo) {
		return "Tiempo restante: " + Estilo.conCeros(tiempo, DIGITOS);
	}
}
