package Pantallas;

import GUI.PanelTranslucido;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import GUI.Estilo;

/**
 * Comportamiento común de todas las pantallas (Template Method): layout, fondo,
 * visibilidad y refresco. Cada pantalla solo define sus componentes.
 */
public abstract class PantallaBase extends PanelTranslucido implements Pantalla {

	private static final long serialVersionUID = 1L;

	protected final CentralPantallas miCentral;

	protected PantallaBase(CentralPantallas miCentral) {
		this.miCentral = miCentral;
	}

	@Override
	public void crearPantalla(int width, int height) {
		setLayout(new GridBagLayout());
		setSize(new Dimension(width, height));
		setBackground(Estilo.FONDO_PANTALLA);
	}

	@Override
	public abstract void agregarComponentes();

	@Override
	public void setVisibilidad(boolean vis) {
		setVisible(vis);
	}

	@Override
	public void refrescar() {
		repaint();
		revalidate();
	}

	/** Agrega un componente en la columna 0, fila indicada. */
	protected void agregarEnFila(java.awt.Component componente, int fila) {
		miCentral.agregarConGBCs(componente, this, new GridBagConstraints(), 0, fila, 1, 1);
	}
}
