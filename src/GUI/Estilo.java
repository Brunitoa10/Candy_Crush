package GUI;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * Estilos visuales compartidos por paneles y pantallas (fuentes, colores y fábricas de
 * componentes). Antes estos métodos estaban copiados en CentralPaneles y CentralPantallas.
 */
public final class Estilo {

	public static final String FUENTE_PRINCIPAL = "Algerian";
	public static final String FUENTE_NUMEROS = "Arial";
	public static final Color COLOR_TEXTO = Color.WHITE;
	public static final Color FONDO_PANEL = new Color(0, 0, 0, 200);
	public static final Color FONDO_PANTALLA = new Color(0, 0, 0, 120);
	public static final Color TRANSPARENTE = new Color(0, 0, 0, 0);

	private Estilo() {}

	public static JLabel label(String texto, int tamanoFuente) {
		return label(texto, FUENTE_PRINCIPAL, Font.PLAIN, tamanoFuente, COLOR_TEXTO);
	}

	public static JLabel label(String texto, String fuente, int estilo, int tamano, Color color) {
		JLabel label = new JLabel(texto);
		label.setFont(new Font(fuente, estilo, tamano));
		label.setForeground(color);
		return label;
	}

	public static JButton boton(String texto) {
		JButton boton = new JButton(texto);
		boton.setBackground(FONDO_PANEL);
		boton.setForeground(COLOR_TEXTO);
		return boton;
	}

	/** Rellena con ceros a la izquierda: conCeros(45, 4) = "0045". */
	public static String conCeros(int valor, int digitos) {
		return String.format("%0" + digitos + "d", Math.max(valor, 0));
	}
}
