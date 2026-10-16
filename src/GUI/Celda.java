package GUI;

import javax.swing.Icon;
import javax.swing.JLabel;

import Logica.EntidadLogica;

public class Celda extends JLabel implements EntidadGrafica {

	private static final long serialVersionUID = 1L;

	protected GUI ventana;
	protected EntidadLogica entidad_logica;
	protected MultiIcon miMultiIcon;

	protected int size_label;
	private boolean resaltada;

	private static final java.awt.Color BRILLO_PISTA = new java.awt.Color(255, 255, 255, 90);
	private static final java.awt.Color BORDE_PISTA = new java.awt.Color(255, 215, 0, 230);

	public Celda(GUI ventana, EntidadLogica entidad_logica, int size_label, boolean visible) {
		super();

		this.ventana = ventana;
		this.entidad_logica = entidad_logica;
		this.size_label = size_label;
		this.setVisible(visible);

		setBounds(entidad_logica.get_columna() * size_label, entidad_logica.get_fila() * size_label, size_label, size_label);
		actualizarImagen();
	}

	/** Toma las imágenes actuales de la entidad lógica; si hay imagen interna (hielo) se superponen. */
	public void actualizarImagen() {
		mostrarImagenes(entidad_logica.get_imagenes_representativas());
	}

	/**
	 * @param imagenes [0] = imagen principal. La gema que está dentro de un hielo tiene su propia
	 * celda (debajo de la del hielo), así se anima sola al intercambiarse y no hay que superponer íconos.
	 */
	public void mostrarImagenes(String[] imagenes) {
		fijar_imagen_escalada_para_celda(imagenes[0]);
		repaint();
	}

	public EntidadLogica get_entidad_logica() {
		return entidad_logica;
	}

	public int get_size_label() {
		return size_label;
	}

	public void eliminar_de_ventana() {
		ventana.eliminar_celda(this);
	}

	public void resaltar(boolean resaltada) {
		if (this.resaltada != resaltada) {
			this.resaltada = resaltada;
			repaint();
		}
	}

	@Override
	protected void paintComponent(java.awt.Graphics g) {
		super.paintComponent(g);
		if (resaltada) {
			java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
			g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(BRILLO_PISTA);
			g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 18, 18);
			g2.setColor(BORDE_PISTA);
			g2.setStroke(new java.awt.BasicStroke(3f));
			g2.drawRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 18, 18);
			g2.dispose();
		}
	}

	public MultiIcon getMultiIcon() {
		return miMultiIcon;
	}

	// Operaciones para Entidad Grafica (Celda <-- Entidad Lógica)

	public void notificarse_intercambio(){
		ventana.animar_intercambio(this);
	}

	public void notificarse_cambio_foco() {
		ventana.animar_cambio_foco(this);
	}

	public void notificarse_detonacion() {
		ventana.animar_detonacion(this);
	}

	public void notificarse_caida() {
		ventana.animar_caida(this);
	}

	public void notificarse_cambio_visibilidad() {
		ventana.animar_cambio_visibilidad(this);
	}

	// Operaciones locales a celda

	protected void fijar_imagen_escalada_para_celda(String path_imagen) {
		miMultiIcon = new MultiIcon(icono(path_imagen), null);
		setIcon(miMultiIcon);
	}

	protected void fijar_imagen_escalada_para_celda(String path_imagen1, String path_imagen2) {
		miMultiIcon = new MultiIcon(icono(path_imagen1), icono(path_imagen2));
		setIcon(miMultiIcon);
	}

	private Icon icono(String ruta) {
		return RecursosGraficos.icono(ruta, size_label, size_label);
	}
}
