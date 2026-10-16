package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import EstrategiaMatch.Estrategias;
import Logica.Direccion;
import Logica.EntidadLogica;
import Logica.Logica;
import Paneles.CentralPaneles;
import Pantallas.CentralPantallas;
import Score.Jugador;
import Threads.CentralAnimaciones;

public class GUI extends JFrame implements VentanaAnimable, VentanaNotificable, VentanaJuego {

	private static final long serialVersionUID = 1L;

	private static final String TITULO = "Proyecto Candy Crush - Comision-06";
	private static final String ICONO = "/assets/nivel/Icono.png";
	private static final int LARGO_NOMBRE_JUGADOR = 3;
	private static final Color FONDO_TABLERO_INICIAL = new Color(0, 0, 0, 125);
	private static final Color FONDO_TABLERO_REINICIO = new Color(0, 0, 0, 255);
	private static final Color TRANSPARENTE = new Color(0, 0, 0, 0);

	//Atributos
	protected Logica miLogica;

	protected int filas, columnas;
	protected int animaciones_pendientes;
	protected int movimientosRestantes;
	private final int size_label = 70;
	private CentralPantallas mi_central_pantallas;
	protected boolean bloquear_intercambios;
	protected CentralPaneles mi_central_paneles;
	protected CentralAnimaciones mi_animador;
	protected JPanel panel_tablero, panel_principal;
	private final Imagenfondo fondo = new Imagenfondo();
	/** Código de tecla -> acción (Command). Se arma una sola vez. */
	private final Map<Integer, Runnable> acciones = new HashMap<>();

	public GUI(Logica l, int f, int c) {
		miLogica = l;
		filas = f;
		columnas = c;
		animaciones_pendientes = 0;
		bloquear_intercambios = false;
		mi_animador = new CentralAnimaciones(this);
		configurarAccionesTeclado();
		inicializarGUI();
	}

	// ----------------------------------------------------------------- Construcción de la ventana

	private void inicializarGUI() {
		crearPanelPrincipal();
		mostrarModosDeJuego();
		// mientras el jugador está en el menú, se cargan las imágenes del juego en segundo plano
		PrecargaRecursos.iniciar(size_label, Toolkit.getDefaultToolkit().getScreenSize());
	}

	public void inicializarJuego(int f, int c) {
		filas = f;
		columnas = c;
		setContentPane(fondo);
		refrescar();
		inicializarPanels();
		construirPanelTablero(FONDO_TABLERO_INICIAL);
		getContentPane().add(panel_principal);
		darFocoAlTableroLuego();
	}

	public void reiniciarGUI(int f, int c) {
		filas = f;
		columnas = c;
		movimientosRestantes = miLogica.getMovimientos();
		animaciones_pendientes = 0;
		bloquear_intercambios = false;
		mi_animador = new CentralAnimaciones(this);

		setContentPane(fondo);
		getContentPane().remove(panel_principal);
		crearPanelPrincipal();
		inicializarPanels();
		construirPanelTablero(FONDO_TABLERO_REINICIO);
		darFocoAlTableroLuego();
		getContentPane().add(panel_principal);
	}

	/** Pasos comunes de armado (antes duplicados entre inicializarGUI e inicializarGUI2). */
	private void crearPanelPrincipal() {
		setContentPane(fondo);
		setIconImage(RecursosGraficos.imagen(ICONO));

		panel_principal = new JPanel();
		panel_principal.setOpaque(false);
		panel_principal.setBackground(TRANSPARENTE);

		mi_central_pantallas = new CentralPantallas(panel_principal, this, miLogica);
		mi_central_paneles = new CentralPaneles(panel_principal, this);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setearJFrame(screenSize);
		panel_principal.setSize(screenSize);
		panel_principal.setLayout(new GridBagLayout());
	}

	private void construirPanelTablero(Color colorFondo) {
		panel_tablero = new PanelTranslucido();
		// Posicionamiento absoluto: las celdas se ubican por (fila, columna) y las animan los Animadores.
		// Antes el GridBagLayout las reacomodaba en cada revalidate y "peleaba" con las animaciones.
		panel_tablero.setLayout(null);
		Dimension tamanoTablero = new Dimension(size_label * columnas, size_label * filas);
		panel_tablero.setPreferredSize(tamanoTablero);
		panel_tablero.setMinimumSize(tamanoTablero);
		panel_tablero.setSize(tamanoTablero);
		panel_tablero.setBackground(colorFondo);
		panel_tablero.setVisible(true);
		panel_tablero.setRequestFocusEnabled(true);
		panel_tablero.setFocusable(true);
		panel_tablero.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				Runnable accion = acciones.get(e.getKeyCode());
				if (accion != null) {
					repaint();
					accion.run();
				}
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 0, 0, 0);
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.CENTER;
		agregarConGBCs(panel_tablero, panel_principal, c, 2, 1, 4, 4);
	}

	private void darFocoAlTableroLuego() {
		SwingUtilities.invokeLater(() -> {
			panel_principal.requestFocus();
			darFocusATablero();
		});
	}

	public void darFocusATablero() {
		panel_tablero.requestFocus();
		panel_tablero.grabFocus();
		refrescar();
	}

	private void configurarAccionesTeclado() {
		// Cursor: flechas
		acciones.put(KeyEvent.VK_UP,    () -> miLogica.mover_jugador(Direccion.ARRIBA));
		acciones.put(KeyEvent.VK_DOWN,  () -> miLogica.mover_jugador(Direccion.ABAJO));
		acciones.put(KeyEvent.VK_LEFT,  () -> miLogica.mover_jugador(Direccion.IZQUIERDA));
		acciones.put(KeyEvent.VK_RIGHT, () -> miLogica.mover_jugador(Direccion.DERECHA));

		// Intercambios: WASD (bloqueados mientras hay animaciones en curso)
		acciones.put(KeyEvent.VK_W, siNoHayAnimaciones(() -> miLogica.intercambiar(Direccion.ARRIBA)));
		acciones.put(KeyEvent.VK_S, siNoHayAnimaciones(() -> miLogica.intercambiar(Direccion.ABAJO)));
		acciones.put(KeyEvent.VK_A, siNoHayAnimaciones(() -> miLogica.intercambiar(Direccion.IZQUIERDA)));
		acciones.put(KeyEvent.VK_D, siNoHayAnimaciones(() -> miLogica.intercambiar(Direccion.DERECHA)));

		acciones.put(KeyEvent.VK_R, siNoHayAnimaciones(this::mostrarPuntajes));
	}

	private Runnable siNoHayAnimaciones(Runnable accion) {
		return () -> {
			if (!bloquear_intercambios) {
				accion.run();
			}
		};
	}

	private void setearJFrame(Dimension screenSize) {
		setTitle(TITULO);
		setSize(screenSize);
		setLayout(new BorderLayout());
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	// ----------------------------------------------------------------- Diálogos

	/** @return nombre de 3 letras, o null si el jugador cancela. */
	public String obtenerNombreJugador() {
		String nombre = abrirVentanaParaIngresarNombre();
		while (nombre != null && nombre.trim().length() != LARGO_NOMBRE_JUGADOR) {
			JOptionPane.showMessageDialog(this, "El nombre debe tener " + LARGO_NOMBRE_JUGADOR + " letras");
			nombre = abrirVentanaParaIngresarNombre();
		}
		return nombre == null ? null : nombre.trim();
	}

	public String abrirVentanaParaIngresarNombre() {
		JPanel panelNombre = new JPanel();
		JTextField textFieldNombre = new JTextField(5);
		panelNombre.add(new JLabel("Ingresa tu nombre (" + LARGO_NOMBRE_JUGADOR + " letras):"));
		panelNombre.add(textFieldNombre);

		int opcion = JOptionPane.showConfirmDialog(this, panelNombre, "¡Top 5 alcanzado!", JOptionPane.OK_CANCEL_OPTION);
		return opcion == JOptionPane.OK_OPTION ? textFieldNombre.getText() : null;
	}

	// ----------------------------------------------------------------- Entidades / celdas

	public EntidadGrafica agregar_entidad(EntidadLogica e) {
		Celda celda = new Celda(this, e, size_label, true);
		panel_tablero.add(celda);
		if (e.tieneGemaInterna()) {
			// el hielo se dibuja ENCIMA de la celda de su gema (que tiene su propia celda)
			panel_tablero.setComponentZOrder(celda, 0);
		}
		panel_tablero.repaint(celda.getBounds());
		return celda;
	}

	public void eliminar_celda(Celda celda) {
		java.awt.Rectangle area = celda.getBounds();
		panel_tablero.remove(celda); // las celdas viven en el panel del tablero, no en el principal
		panel_tablero.repaint(area);
	}

	// ----------------------------------------------------------------- Animaciones

	public void notificarse_animacion_en_progreso() {
		synchronized (this) {
			animaciones_pendientes++;
			bloquear_intercambios = true;
		}
	}

	public void notificarse_animacion_finalizada() {
		synchronized (this) {
			animaciones_pendientes--;
			bloquear_intercambios = animaciones_pendientes > 0;
		}
	}

	public void animar_intercambio(Celda celda) {
		mi_animador.animar_intercambio(celda);
	}

	public void animar_cambio_foco(Celda celda) {
		mi_animador.animar_cambio_foco(celda);
	}

	public void animar_detonacion(Celda celda) {
		mi_animador.animar_detonacion(celda);
	}

	public void animar_caida(Celda celda) {
		mi_animador.animar_caida(celda);
	}

	public void animar_cambio_visibilidad(Celda celda) {
		mi_animador.animar_cambio_visibilidad(celda);
	}

	public void refrescar() {
		repaint();
		revalidate();
	}

	public boolean getBloquear_intercambios() {
		return bloquear_intercambios;
	}

	// ----------------------------------------------------------------- Paneles

	public void inicializarPanels() {
		mi_central_paneles.mostrarPanelScore();
		mi_central_paneles.mostrarPanelObjetivo();
		mi_central_paneles.mostrarPanelControles();
		mi_central_paneles.mostrarPanelVidas();
		mi_central_paneles.mostrarPanelMovimiento();
		mi_central_paneles.mostrarPanelReglas();
		mi_central_paneles.mostrarPanelTimer();
	}

	public CentralPaneles obtenerCentralPaneles()
	{
		return mi_central_paneles;
	}

	public void actualizarMovimientos(int movimientos) {
		mi_central_paneles.actualizarMovimientos(movimientos);
	}

	public void actualizarTimer(int tiempo) {
		mi_central_paneles.actualizarTimer(tiempo);
	}

	public void actualizarProgreso(int gemasRestantes, int tipoGema) {
		mi_central_paneles.actualizarProgreso(gemasRestantes, tipoGema);
	}

	public void reiniciarProgreso() {
		mi_central_paneles.reiniciarProgresoObjetivos();
	}

	public void actualizarVidas() {
		mi_central_paneles.actualizarVidas();
	}

	public void actualizarScore(int score) {
		mi_central_paneles.actualizarScore(score);
	}

	// ----------------------------------------------------------------- Pantallas

	public void mostrarMensajeDerrotaPorMovimientos() {
		mi_central_pantallas.mostrarMensajeDerrotaPorMovimientos();
	}

	public void mostrarMensajeDerrotaPorVidas() {
		mi_central_pantallas.mostrarMensajeDerrotaPorVidas();
	}

	public void mostrarMensajeDerrotaPorTiempo() {
		mi_central_pantallas.mostrarMensajeDerrotaPorTiempo();
	}

	public void mostrarMensajeFinDelJuego() {
		mi_central_pantallas.mostrarMensajeFinDelJuego();
	}

	public void mostrarMensajeVictoriaPorObjetivos() {
		mi_central_pantallas.mostrarMensajeVictoriaPorObjetivos();
	}

	public void mostrarPuntajes() {
		mi_central_pantallas.mostrarPuntajes();
	}

	public void mostrarModosDeJuego() {
		mi_central_pantallas.mostrarModosDeJuego();
	}

	// ----------------------------------------------------------------- Consultas a la lógica

	public int getVidas() {
		return miLogica.getVidas();
	}

	public int getScore() {
		return miLogica.getScore();
	}

	public int getTiempoRestante() {
		return miLogica.getTiempo();
	}

	public int getMovimientos() {
		return miLogica.getMovimientos();
	}

	public int getCantidadDeObjetivos() {
		return miLogica.getCantidadDeObjetivos();
	}

	public String[] obtenerInfoObjetivos() {
		return miLogica.obtenerInfoObjetivos();
	}

	public LinkedList<Estrategias> getEstrategias(){
		return miLogica.getEstrategias();
	}

	public List<Jugador> obtenerRanking() {
		return miLogica.obtenerRanking();
	}

	public PriorityQueue<Jugador> obtenerListadeJugadores() {
		return miLogica.obtenerListadeJugadores();
	}

	// ----------------------------------------------------------------- VentanaJuego

	public void limpiarGUI() {
		movimientosRestantes = getMovimientos();
		animaciones_pendientes = 0;
		bloquear_intercambios = false;

		limpiarMatrizGUI();
		mi_central_paneles.actualizarVidas();
		mi_central_paneles.actualizarTimer(getTiempoRestante());
		mi_central_paneles.actualizarMovimientos(getMovimientos());
	}

	private void limpiarMatrizGUI() {
		panel_tablero.removeAll();
		panel_tablero.revalidate();
		panel_tablero.repaint();
	}

	private void agregarConGBCs(Component componenteAAgregar, JPanel panelBase, GridBagConstraints gbc, int gridx, int gridy, int gridwidth, int gridheight) {
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridwidth = gridwidth;
		gbc.gridheight = gridheight;
		panelBase.add(componenteAAgregar, gbc);
	}

	@Override
	public void ocultar() {
		this.setVisible(false);
	}

	@Override
	public void mostrar() {
		this.setVisible(true);
	}

	@Override
	public void transicionar() {
		// To DO. Podría servir para mostrar información o carteles.
	}

	@Override
	public void resetear(Logica logica, int filas, int columnas) {
		panel_principal = new JPanel();
		animaciones_pendientes = 0;
		bloquear_intercambios = false;
		miLogica = logica;
		this.filas = filas;
		this.columnas = columnas;

		limpiarMatrizGUI();
		mi_central_paneles.mostrarPanelVidas();
		mi_central_paneles.mostrarPanelScore();
		mi_central_paneles.mostrarPanelObjetivo();
		mi_central_paneles.mostrarPanelControles();
		mi_central_paneles.actualizarTimer(getTiempoRestante());
		mi_central_paneles.actualizarMovimientos(getMovimientos());
	}

	public void cambiarFondo(int nivel) {
		fondo.cambiarFondo(nivel);
		repaint();
	}

	//Clase auxiliar para dibujar el fondo del nivel

	private static class Imagenfondo extends JPanel {

		private static final long serialVersionUID = 4180857344385898634L;

		private int nivelActual = 1;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			// Se usa la imagen ya escalada al tamaño del panel (cacheada): antes se leía del disco
			// y se escalaba la imagen completa en cada repintado, incluso en cada cuadro de animación
			g.drawImage(RecursosGraficos.imagen(rutaFondo(), getWidth(), getHeight()), 0, 0, this);
		}

		private String rutaFondo() {
			return "/assets/nivel/" + nivelActual + "_Nivel.png";
		}

		public void cambiarFondo(int nivel) {
			nivelActual = nivel;
			repaint();
		}
	}
}
