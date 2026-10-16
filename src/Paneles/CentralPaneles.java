package Paneles;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import EstrategiaMatch.Estrategias;
import GUI.Estilo;
import GUI.GUI;
import GUI.RecursosGraficos;

/** Mediator entre la GUI y los paneles informativos del juego. */
public class CentralPaneles {

	private static final int TAMANO_ICONO = 40;

	protected JPanel panelPrincipal;
	protected GUI miGUI;
	protected PanelScore miPanelScore;
	protected PanelObjetivo miPanelObjetivo;
	protected PanelVidas miPanelVidas;
	protected PanelMovimiento miPanelMovimiento;
	protected PanelReglas miPanelReglas;
	protected PanelTimer miPanelTimer;

	public CentralPaneles(JPanel p_principal, GUI mi_GUI) {
		panelPrincipal = p_principal;
		miGUI = mi_GUI;
	}

	/** Template: todos los paneles se crean y agregan igual. */
	private <P extends Paneles> P mostrar(P panel) {
		panel.crearPanel();
		panel.agregarAPanelPrincipal();
		return panel;
	}

	// ----------------------------------------------------------------- Reglas

	public void mostrarPanelReglas() {
		miPanelReglas = mostrar(new PanelReglas(this));
	}

	public LinkedList<Estrategias> getEstrategias(){
		return miGUI.getEstrategias();
	}

	public List<String> getNombresEstrategiasEnUso() {
		List<String> nombres = new ArrayList<>();
		for (Estrategias estrategia : getEstrategias()) {
			nombres.add(estrategia.get_NombreRegla());
		}
		return nombres;
	}

	// ----------------------------------------------------------------- Score

	public void mostrarPanelScore() {
		miPanelScore = mostrar(new PanelScore(this));
	}

	public void actualizarScore(int score) {
		if (miPanelScore != null) {
			miPanelScore.actualizarScore(score);
		}
	}

	public int getScore() {
		return miGUI.getScore();
	}

	// ----------------------------------------------------------------- Controles

	public void mostrarPanelControles() {
		mostrar(new PanelControles(this));
	}

	// ----------------------------------------------------------------- Timer

	public void mostrarPanelTimer() {
		miPanelTimer = mostrar(new PanelTimer(this));
	}

	public void actualizarTimer(int tiempo) {
		if (miPanelTimer != null) {
			miPanelTimer.actualizarTimer(tiempo);
		}
	}

	public int getTiempoRestante() {
		return miGUI.getTiempoRestante();
	}

	// ----------------------------------------------------------------- Vidas

	public void mostrarPanelVidas() {
		miPanelVidas = mostrar(new PanelVidas(this));
	}

	public int getVidas() {
		return miGUI.getVidas();
	}

	public void actualizarVidas() {
		if (miPanelVidas != null) {
			miPanelVidas.actualizarVidas();
		}
	}

	// ----------------------------------------------------------------- Movimientos

	public void mostrarPanelMovimiento() {
		miPanelMovimiento = mostrar(new PanelMovimiento(this));
	}

	public void actualizarMovimientos(int movimientosRestantes) {
		if (miPanelMovimiento != null) {
			miPanelMovimiento.actualizarMovimientos(movimientosRestantes);
		}
	}

	public int getCantidadDeMovimientos() {
		return miGUI.getMovimientos();
	}

	// ----------------------------------------------------------------- Objetivos

	public void mostrarPanelObjetivo() {
		miPanelObjetivo = new PanelObjetivo(this);
		miPanelObjetivo.crearPanel();
		miPanelObjetivo.mostrarObjetivos();
		miPanelObjetivo.agregarAPanelPrincipal();
	}

	public int getCantidadDeObjetivos() {
		return miGUI.getCantidadDeObjetivos();
	}

	public String[] obtenerInfoObjetivos() {
		return miGUI.obtenerInfoObjetivos();
	}

	public void actualizarProgreso(int gemasRestantes, int tipoGema) {
		if (miPanelObjetivo != null) {
			miPanelObjetivo.actualizarProgreso(gemasRestantes, tipoGema);
		}
	}

	public void reiniciarProgresoObjetivos() {
		miPanelObjetivo.reiniciarProgreso();
	}

	// ----------------------------------------------------------------- Utilidades de layout

	public void agregarConGBCs(Component componenteAAgregar, GridBagConstraints gbc, int gridx, int gridy, int gridwidth, int gridheight) {
		agregarConGBCs(componenteAAgregar, panelPrincipal, gbc, gridx, gridy, gridwidth, gridheight);
	}

	public void agregarConGBCs(Component componenteAAgregar, JPanel panelAAgregar, GridBagConstraints gbc, int gridx, int gridy, int gridwidth, int gridheight) {
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridwidth = gridwidth;
		gbc.gridheight = gridheight;
		panelAAgregar.add(componenteAAgregar, gbc);
	}

	public JLabel crearLabel(String texto, int tamanoFuente) {
		return Estilo.label(texto, tamanoFuente);
	}

	/** Los parámetros gridwidth/gridheight se conservan por compatibilidad (no se usaban). */
	public JLabel crearLabelConColor(String texto, String fuente, int estilo, int tamano, Color color, int gridwidth, int gridheight) {
		return Estilo.label(texto, fuente, estilo, tamano, color);
	}

	public JLabel crearImagen(String ruta) {
		return new JLabel(RecursosGraficos.icono(ruta, TAMANO_ICONO, TAMANO_ICONO));
	}
}
