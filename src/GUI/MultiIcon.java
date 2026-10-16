package GUI;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/** Icono compuesto: dibuja icon2 encima de icon1 (por ejemplo, el hielo sobre la gema). */
public class MultiIcon implements Icon {

	private Icon icon1;
	private Icon icon2;

	public MultiIcon(Icon icon1, Icon icon2) {
		this.icon1 = icon1;
		this.icon2 = icon2;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		if (icon1 != null) {
			icon1.paintIcon(c, g, x, y);
		}
		if (icon2 != null) {
			icon2.paintIcon(c, g, x, y);
		}
	}

	@Override
	public int getIconWidth() {
		return Math.max(ancho(icon1), ancho(icon2)); // antes comparaba icon1 consigo mismo
	}

	@Override
	public int getIconHeight() {
		return Math.max(alto(icon1), alto(icon2));
	}

	public boolean hasSingleIcon() {
		return icon2 == null;
	}

	public Icon getIcon1() {
		return icon1;
	}

	public Icon getIcon2() {
		return icon2;
	}

	public void removeIcon1() {
		icon1 = null;
	}

	public void removeIcon2() {
		icon2 = null;
	}

	/** Reescala los iconos (solo si son ImageIcon). */
	public void setIconSize(int width, int height) {
		icon1 = escalar(icon1, width, height);
		icon2 = escalar(icon2, width, height);
	}

	private static Icon escalar(Icon icono, int width, int height) {
		if (!(icono instanceof ImageIcon) || (icono.getIconWidth() == width && icono.getIconHeight() == height)) {
			return icono;
		}
		Image imagen = ((ImageIcon) icono).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return new ImageIcon(imagen);
	}

	private static int ancho(Icon icono) {
		return icono == null ? 0 : icono.getIconWidth();
	}

	private static int alto(Icon icono) {
		return icono == null ? 0 : icono.getIconHeight();
	}
}
