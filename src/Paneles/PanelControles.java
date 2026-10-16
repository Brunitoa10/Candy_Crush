package Paneles;

import GUI.PanelTranslucido;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import GUI.RecursosGraficos;

public class PanelControles extends PanelTranslucido implements Paneles {

	private static final long serialVersionUID = 1L;
	private static final int TAMANO_TEXTO = 25;

	private final CentralPaneles miCentral;

	public PanelControles(CentralPaneles mi_central) {
		miCentral = mi_central;
	}

	@Override
	public void crearPanel() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 0, 10, 0);
		miCentral.agregarConGBCs(this, gbc, 0, 6, 5, 1);
	}

	@Override
	public void agregarAPanelPrincipal() {
		setOpaque(false);
		agregarControl("/assets/controles/arrow_keys.png", 120, "Mover");
		agregarControl("/assets/controles/WASD.png", 120, "Intercambiar");
		agregarControl("/assets/controles/R.png", 30, "Ver mejores puntajes");
		repaint();
	}

	private void agregarControl(String rutaImagen, int anchoImagen, String descripcion) {
		add(new JLabel(RecursosGraficos.icono(rutaImagen, anchoImagen, 30, Image.SCALE_REPLICATE)));
		add(miCentral.crearLabel(descripcion, TAMANO_TEXTO));
	}
}
