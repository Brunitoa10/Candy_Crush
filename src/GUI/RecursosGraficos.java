package GUI;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Punto único de carga y escalado de imágenes (patrón Flyweight + precarga).
 *
 * - Las imágenes estáticas se cachean por ruta y tamaño: cada una se lee y escala UNA vez.
 * - Se escala con Graphics2D (bilineal, por pasos), mucho más rápido que
 *   Image.getScaledInstance(SCALE_SMOOTH), que era lo que congelaba el arranque.
 * - precargarEnSegundoPlano() llena la caché mientras el jugador está en el menú.
 * - Los GIF animados no se cachean para que cada celda tenga su propia animación.
 */
public final class RecursosGraficos {

	private static final String IMAGEN_VACIA = "/assets/gemas/original/gema_normal/0.png";

	private static final Map<String, Icon> CACHE_ICONOS = new ConcurrentHashMap<>();
	private static final Map<String, BufferedImage> CACHE_IMAGENES = new ConcurrentHashMap<>();

	private static final ExecutorService PRECARGA = Executors.newSingleThreadExecutor(r -> {
		Thread hilo = new Thread(r, "precarga-imagenes");
		hilo.setDaemon(true);
		hilo.setPriority(Thread.MIN_PRIORITY);
		return hilo;
	});

	private RecursosGraficos() {}

	// ----------------------------------------------------------------- API

	public static Icon icono(String ruta, int ancho, int alto) {
		return icono(ruta, ancho, alto, Image.SCALE_SMOOTH);
	}

	public static Icon icono(String ruta, int ancho, int alto, int modoEscalado) {
		if (esAnimado(ruta)) {
			// un GIF animado debe pasar por ImageIcon/Toolkit para conservar la animación
			Image gif = new ImageIcon(recurso(ruta)).getImage();
			return new ImageIcon(gif.getScaledInstance(ancho, alto, Image.SCALE_DEFAULT));
		}
		boolean pixelado = modoEscalado == Image.SCALE_REPLICATE || modoEscalado == Image.SCALE_FAST;
		String clave = ruta + "@" + ancho + "x" + alto + (pixelado ? "#p" : "#s");
		return CACHE_ICONOS.computeIfAbsent(clave, k -> new ImageIcon(escalar(original(ruta), ancho, alto, pixelado)));
	}

	/** Imagen sin escalar. */
	public static Image imagen(String ruta) {
		return original(ruta);
	}

	/** Imagen escalada a un tamaño exacto (por ejemplo, el fondo al tamaño de la ventana). */
	public static Image imagen(String ruta, int ancho, int alto) {
		if (ancho <= 0 || alto <= 0) {
			return original(ruta);
		}
		String clave = ruta + "@" + ancho + "x" + alto;
		return CACHE_IMAGENES.computeIfAbsent(clave, k -> escalar(original(ruta), ancho, alto, false));
	}

	/** Carga y escala en un hilo de baja prioridad; si después se pide la misma imagen, ya está lista. */
	public static void precargarEnSegundoPlano(Collection<Runnable> tareas) {
		for (Runnable tarea : tareas) {
			PRECARGA.execute(() -> {
				try {
					tarea.run();
				} catch (RuntimeException e) {
					// la precarga es optimista: si algo falla, se cargará al usarse
				}
			});
		}
	}

	// ----------------------------------------------------------------- Implementación

	private static BufferedImage original(String ruta) {
		return CACHE_IMAGENES.computeIfAbsent(ruta, RecursosGraficos::leer);
	}

	private static BufferedImage leer(String ruta) {
		try {
			BufferedImage imagen = ImageIO.read(recurso(ruta));
			if (imagen != null) {
				return imagen;
			}
		} catch (IOException e) {
			System.err.println("[RecursosGraficos] No se pudo leer " + ruta + ": " + e.getMessage());
		}
		return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * Escalado por pasos (mitades sucesivas + bilineal): rápido y sin el "serrucho"
	 * de reducir de golpe una imagen de 1000px a 70px.
	 */
	private static BufferedImage escalar(BufferedImage origen, int ancho, int alto, boolean pixelado) {
		BufferedImage actual = origen;
		int w = origen.getWidth();
		int h = origen.getHeight();
		if (!pixelado) {
			while (w / 2 >= ancho && h / 2 >= alto) {
				w /= 2;
				h /= 2;
				actual = dibujar(actual, w, h, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			}
		}
		Object interpolacion = pixelado ? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
				: RenderingHints.VALUE_INTERPOLATION_BILINEAR;
		return dibujar(actual, ancho, alto, interpolacion);
	}

	private static BufferedImage dibujar(BufferedImage origen, int ancho, int alto, Object interpolacion) {
		BufferedImage destino = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = destino.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolacion);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.drawImage(origen, 0, 0, ancho, alto, null);
		g.dispose();
		return destino;
	}

	/**
	 * Si falta un asset (por ejemplo "0.png" de una roca ya detonada) se usa una imagen
	 * vacía en lugar de lanzar una excepción que deja a la animación trabada.
	 */
	private static URL recurso(String ruta) {
		URL url = RecursosGraficos.class.getResource(ruta);
		if (url == null) {
			System.err.println("[RecursosGraficos] No se encontró " + ruta + ", se usa imagen vacía");
			url = RecursosGraficos.class.getResource(IMAGEN_VACIA);
		}
		if (url == null) {
			throw new IllegalArgumentException("No se encontró el recurso gráfico: " + ruta);
		}
		return url;
	}

	private static boolean esAnimado(String ruta) {
		return ruta.toLowerCase().endsWith(".gif");
	}
}
