package GUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.JPanel;

/**
 * JPanel con fondo semitransparente que se pinta bien.
 * Un JPanel opaco con color alfa (new Color(0,0,0,200)) no limpia lo que había debajo y,
 * al repintar zonas chicas (timer, animaciones), deja rastros y parpadeos. Este panel se
 * declara no opaco (Swing repinta primero lo de atrás) y dibuja su fondo a mano.
 */
public class PanelTranslucido extends JPanel {

	private static final long serialVersionUID = 1L;

	public PanelTranslucido() {
		super();
		setOpaque(false);
	}

	public PanelTranslucido(LayoutManager layout) {
		super(layout);
		setOpaque(false);
	}

	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Color fondo = getBackground();
		if (fondo != null && fondo.getAlpha() > 0) {
			g.setColor(fondo);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		super.paintComponent(g);
	}
}
