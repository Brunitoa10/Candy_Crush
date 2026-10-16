package Pantallas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.util.List;
import java.util.PriorityQueue;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import GUI.Estilo;
import GUI.GUI;
import Logica.Logica;
import Score.Jugador;

/** Mediator entre la GUI/Lógica y las pantallas superpuestas (menú, victoria, derrota, puntajes). */
public class CentralPantallas {

	public static final String SKIN_ORIGINAL = "original";
	public static final String SKIN_HALLOWEEN = "halloween";

	protected JPanel panelPrincipal;
	protected GUI miGUI;
	protected Logica miLogica;
	protected PantallaModosDeJuegos miModosDeJuegos;

	public CentralPantallas(JPanel p_principal, GUI mi_GUI, Logica mi_logica) {
		panelPrincipal = p_principal;
		miGUI = mi_GUI;
		miLogica = mi_logica;
	}

	/**
	 * Pasos comunes para mostrar cualquier pantalla (antes copiados en 6 métodos):
	 * oculta el juego, pausa el tiempo y superpone la pantalla.
	 */
	private <P extends PantallaBase> P mostrarPantalla(P pantalla) {
		panelPrincipal.setVisible(false);
		miLogica.pausarTiempo();
		pantalla.crearPantalla(Frame.MAXIMIZED_HORIZ, Frame.MAXIMIZED_HORIZ);
		pantalla.agregarComponentes();
		miGUI.getContentPane().add(pantalla);
		pantalla.setVisibilidad(true);
		pantalla.refrescar();
		return pantalla;
	}

	public void mostrarMensajeDerrotaPorMovimientos() {
		mostrarPantalla(new PantallaDerrotaPorMovimientos(this));
	}

	public void mostrarMensajeDerrotaPorTiempo() {
		mostrarPantalla(new PantallaDerrotaPorTiempo(this));
	}

	public void mostrarMensajeFinDelJuego() {
		mostrarPantalla(new PantallaFinDelJuego(this));
	}

	public void mostrarMensajeDerrotaPorVidas() {
		mostrarPantalla(new PantallaDerrotaPorVidas(this));
	}

	public void mostrarMensajeVictoriaPorObjetivos() {
		mostrarPantalla(new PantallaVictoriaPorObjetivos(this));
	}

	public void mostrarModosDeJuego() {
		miModosDeJuegos = mostrarPantalla(new PantallaModosDeJuegos(this));
	}

	public void mostrarPuntajes() {
		mostrarPantalla(new PantallaPuntajes(this));
	}

	public void eliminarPantallaModosDeJuego() {
		miGUI.setFocusable(true);
		if (miModosDeJuegos != null) {
			miModosDeJuegos.setVisibilidad(false);
		}
		panelPrincipal.setFocusable(true);
		panelPrincipal.requestFocusInWindow();
	}

	// ----------------------------------------------------------------- Datos para las pantallas

	public List<Jugador> obtenerRanking() {
		return miGUI.obtenerRanking();
	}

	public PriorityQueue<Jugador> obtenerListadeJugadores() {
		return miGUI.obtenerListadeJugadores();
	}

	// ----------------------------------------------------------------- Fábricas de componentes

	public void agregarConGBCs(Component componenteAAgregar, JPanel panelBase, GridBagConstraints gbc, int gridx, int gridy, int gridwidth, int gridheight) {
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridwidth = gridwidth;
		gbc.gridheight = gridheight;
		panelBase.add(componenteAAgregar, gbc);
	}

	public JLabel crearLabel(String texto, int tamanoFuente) {
		return Estilo.label(texto, tamanoFuente);
	}

	/** Los parámetros gridwidth/gridheight se conservan por compatibilidad (no se usaban). */
	public JLabel crearLabelConColor(String texto, String fuente, int estilo, int tamano, Color color, int gridwidth, int gridheight) {
		return Estilo.label(texto, fuente, estilo, tamano, color);
	}

	public JButton crearBoton(String texto) {
		return Estilo.boton(texto);
	}

	// ----------------------------------------------------------------- Acciones de los botones

	public void agregarFuncionalidadSkin(Pantalla p, JButton boton, String skin) {
		boton.addActionListener(e -> {
			boton.setEnabled(false); // evita doble clic mientras arranca
			miGUI.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			miLogica.set_skin(skin);
			volverAlJuego(p);
			eliminarPantallaModosDeJuego();
			// se deja que Swing pinte primero el cambio de pantalla y recién después se arma el nivel,
			// así el clic se siente inmediato
			SwingUtilities.invokeLater(() -> {
				try {
					miLogica.inicializarJuego();
				} finally {
					miGUI.setCursor(Cursor.getDefaultCursor());
				}
			});
		});
	}

	public void agregarFuncionalidadSkinOriginal(Pantalla p, JButton botonOriginal) {
		agregarFuncionalidadSkin(p, botonOriginal, SKIN_ORIGINAL);
	}

	public void agregarFuncionalidadSkinHalloween(Pantalla p, JButton botonHalloween) {
		agregarFuncionalidadSkin(p, botonHalloween, SKIN_HALLOWEEN);
	}

	public void agregarFuncionalidadBotonReiniciar(Pantalla p, JButton botonReiniciar) {
		botonReiniciar.addActionListener(e -> {
			volverAlJuego(p);
			miLogica.reintentarNivel();
		});
	}

	public void agregarFuncionalidadBotonSiguienteNivel(Pantalla p, JButton botonSiguienteNivel) {
		botonSiguienteNivel.addActionListener(e -> {
			volverAlJuego(p);
			miLogica.reanudarTiempo();
			miLogica.iniciarSiguienteNivel();
		});
	}

	public void agregarFuncionalidadBotonVolver(Pantalla p, JButton botonVolver) {
		botonVolver.addActionListener(e -> {
			volverAlJuego(p);
			miLogica.reanudarTiempo();
			SwingUtilities.invokeLater(() -> {
				panelPrincipal.requestFocus();
				miGUI.refrescar();
				miGUI.darFocusATablero();
			});
		});
	}

	private void volverAlJuego(Pantalla p) {
		panelPrincipal.setFocusable(true);
		panelPrincipal.requestFocusInWindow();
		panelPrincipal.setVisible(true);
		p.setVisibilidad(false);
		panelPrincipal.repaint();
		panelPrincipal.revalidate();
	}
}
