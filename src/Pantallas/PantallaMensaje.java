package Pantallas;

import java.util.function.BiConsumer;

import javax.swing.JButton;

/**
 * Pantalla genérica de mensaje: un título, líneas de texto y, opcionalmente, un botón.
 * Las pantallas de victoria/derrota solo la configuran (antes eran clases casi idénticas).
 */
public class PantallaMensaje extends PantallaBase {

	private static final long serialVersionUID = 1L;
	private static final int TAMANO_TITULO = 50;
	private static final int TAMANO_TEXTO = 30;

	private final String titulo;
	private final String[] lineas;
	private final String textoBoton;
	private final BiConsumer<Pantalla, JButton> accionBoton;

	public PantallaMensaje(CentralPantallas central, String titulo, String... lineas) {
		this(central, null, null, titulo, lineas);
	}

	public PantallaMensaje(CentralPantallas central, String textoBoton, BiConsumer<Pantalla, JButton> accionBoton,
			String titulo, String... lineas) {
		super(central);
		this.titulo = titulo;
		this.lineas = lineas;
		this.textoBoton = textoBoton;
		this.accionBoton = accionBoton;
	}

	@Override
	public void agregarComponentes() {
		int fila = 0;
		agregarEnFila(miCentral.crearLabel(titulo, TAMANO_TITULO), fila++);
		for (String linea : lineas) {
			agregarEnFila(miCentral.crearLabel(linea, TAMANO_TEXTO), fila++);
		}
		if (textoBoton != null) {
			JButton boton = miCentral.crearBoton(textoBoton);
			agregarEnFila(boton, fila);
			accionBoton.accept(this, boton);
		}
	}
}
