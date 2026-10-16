package GUI;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

/**
 * Lista de las imágenes que usa el juego, para cargarlas en segundo plano mientras
 * el jugador está en el menú. Así, al elegir el modo de juego, el tablero aparece al instante.
 */
public final class PrecargaRecursos {

	private static final String[] SKINS = { "original", "halloween" };
	private static final int CANTIDAD_NIVELES = 5;
	private static final int MAX_TIEMPO_BOMBA = 30;

	private PrecargaRecursos() {}

	public static void iniciar(int tamanoCelda, Dimension pantalla) {
		List<Runnable> tareas = new ArrayList<>();

		// 1) Lo primero que se ve al empezar: fondo del nivel 1 y gemas de ambos modos
		agregarFondo(tareas, 1, pantalla);
		for (String skin : SKINS) {
			String base = "/assets/gemas/" + skin + "/";
			for (int color = 0; color <= 8; color++) {
				agregarCelda(tareas, base + "gema_normal/" + color, tamanoCelda);
			}
		}
		agregarIcono(tareas, "/assets/nivel/corazon.png", 40, 40);
		agregarIcono(tareas, "/assets/nivel/corazonVacio.png", 40, 40);

		// 2) Obstáculos y gemas especiales
		agregarCelda(tareas, "/assets/obstaculo/7", tamanoCelda);
		agregarCelda(tareas, "/assets/obstaculo/hielo/8", tamanoCelda);
		for (String skin : SKINS) {
			String base = "/assets/gemas/" + skin + "/";
			for (int color = 1; color <= 6; color++) {
				agregarCelda(tareas, base + "gema_cruz/" + color, tamanoCelda);
				agregarCelda(tareas, base + "gema_envuelta/" + color, tamanoCelda);
				agregarCelda(tareas, base + "gema_rayada/1" + color, tamanoCelda);
				agregarCelda(tareas, base + "gema_rayada/5" + color, tamanoCelda);
			}
		}

		// 3) Bombas y fondos del resto de los niveles
		for (int t = MAX_TIEMPO_BOMBA; t >= 0; t--) {
			agregarCelda(tareas, "/assets/obstaculo/bomba/" + t + "/" + t, tamanoCelda);
		}
		for (int nivel = 2; nivel <= CANTIDAD_NIVELES; nivel++) {
			agregarFondo(tareas, nivel, pantalla);
		}

		RecursosGraficos.precargarEnSegundoPlano(tareas);
	}

	/** Imagen normal y con cursor de una celda. */
	private static void agregarCelda(List<Runnable> tareas, String rutaSinExtension, int tamano) {
		agregarIcono(tareas, rutaSinExtension + ".png", tamano, tamano);
		agregarIcono(tareas, rutaSinExtension + "-cursor.png", tamano, tamano);
	}

	private static void agregarIcono(List<Runnable> tareas, String ruta, int ancho, int alto) {
		tareas.add(() -> RecursosGraficos.icono(ruta, ancho, alto));
	}

	private static void agregarFondo(List<Runnable> tareas, int nivel, Dimension pantalla) {
		String ruta = "/assets/nivel/" + nivel + "_Nivel.png";
		tareas.add(() -> RecursosGraficos.imagen(ruta, pantalla.width, pantalla.height));
	}
}
