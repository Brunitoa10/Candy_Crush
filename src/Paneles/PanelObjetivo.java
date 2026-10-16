package Paneles;

import GUI.PanelTranslucido;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import GUI.Estilo;

public class PanelObjetivo extends PanelTranslucido implements Paneles {

	private static final long serialVersionUID = 1L;

	/** obtenerInfoObjetivos() devuelve 4 datos por objetivo: texto, imagen, cantidad, tipo. */
	private static final int DATOS_POR_OBJETIVO = 4;
	private static final int TEXTO = 0, IMAGEN = 1, CANTIDAD = 2, TIPO = 3;

	private final CentralPaneles miCentral;
	private final int[] objetivoColores;
	private final int[] objetivoTotales;
	private final JLabel[] objetivoProgreso;

	public PanelObjetivo(CentralPaneles mi_central) {
		miCentral = mi_central;
		int cantidad = miCentral.getCantidadDeObjetivos();
		objetivoColores = new int[cantidad];
		objetivoTotales = new int[cantidad];
		objetivoProgreso = new JLabel[cantidad];
	}

	@Override
	public void crearPanel() {
		setSize(100, 100);
		setLayout(new GridBagLayout());
		setBackground(Estilo.FONDO_PANEL);
	}

	@Override
	public void agregarAPanelPrincipal() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 10, 0, 0);
		miCentral.agregarConGBCs(this, gbc, 0, 1, 2, 2);
	}

	public void mostrarObjetivos() {
		GridBagConstraints cTitulo = new GridBagConstraints();
		cTitulo.insets = new Insets(0, 0, 0, 0);
		cTitulo.gridx = 0;
		cTitulo.gridy = 0;
		add(Estilo.label(" OBJETIVOS:", Estilo.FUENTE_PRINCIPAL, Font.PLAIN, 20, Estilo.COLOR_TEXTO), cTitulo);

		String[] info = miCentral.obtenerInfoObjetivos(); // antes se pedía en cada vuelta del for
		int coordenada_y = 1;
		for (int n = 0; n < objetivoColores.length; n++) {
			int base = n * DATOS_POR_OBJETIVO;
			objetivoColores[n] = Integer.parseInt(info[base + TIPO]);
			objetivoTotales[n] = Integer.parseInt(info[base + CANTIDAD]);

			JLabel texto = Estilo.label(info[base + TEXTO], Estilo.FUENTE_PRINCIPAL, Font.PLAIN, 15, Estilo.COLOR_TEXTO);
			JLabel imagen = miCentral.crearImagen(info[base + IMAGEN]);
			JLabel progreso = Estilo.label(textoProgreso(0, n), Estilo.FUENTE_NUMEROS, Font.PLAIN, 15, Estilo.COLOR_TEXTO);

			miCentral.agregarConGBCs(texto, this, new GridBagConstraints(), 0, coordenada_y, 1, 1);
			miCentral.agregarConGBCs(imagen, this, new GridBagConstraints(), 1, coordenada_y, 1, 1);
			miCentral.agregarConGBCs(progreso, this, new GridBagConstraints(), 0, coordenada_y + 1, 2, 1);

			objetivoProgreso[n] = progreso;
			coordenada_y += 2;
		}
	}

	public void reiniciarProgreso() {
		for (int n = 0; n < objetivoProgreso.length; n++) {
			objetivoProgreso[n].setText(textoProgreso(0, n));
		}
	}

	public void actualizarProgreso(int gemasRestantes, int tipoGema) {
		for (int n = 0; n < objetivoColores.length; n++) {
			if (tipoGema == objetivoColores[n] && objetivoProgreso[n] != null) {
				int logradas = Math.min(objetivoTotales[n], objetivoTotales[n] - Math.max(gemasRestantes, 0));
				objetivoProgreso[n].setText(textoProgreso(logradas, n));
			}
		}
		repaint();
	}

	private String textoProgreso(int logradas, int n) {
		return logradas + "/" + objetivoTotales[n];
	}
}
