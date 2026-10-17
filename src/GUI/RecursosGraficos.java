package GUI;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Punto único de carga y escalado de imágenes (patrón Flyweight + precarga).
 *
 * Cuidado con la memoria (Java 8 de 32 bits tiene poco heap): muchos PNG miden 1080x1080
 * o 3464x3464 pero se muestran a 70x70. Por eso:
 * - Se cachea SOLO la imagen ya escalada; el original se descarta enseguida.
 * - El PNG se decodifica submuestreado (lee 1 de cada N píxeles) cuando es mucho más
 *   grande que el destino: un 3464x3464 ya no ocupa 48 MB al leerse.
 * - Los fondos guardan un único tamaño por imagen (el de la ventana actual).
 * - Las imágenes sin escalar se guardan con SoftReference: si falta memoria, el GC las libera.
 * - Los GIF animados no se cachean para que cada celda tenga su propia animación.
 */
public final class RecursosGraficos {

	private static final String IMAGEN_VACIA = "/assets/gemas/original/gema_normal/0.png";
	/** Se decodifica a por lo menos el doble del destino, para que el escalado final quede suave. */
	private static final int MARGEN_CALIDAD = 2;

	private static final Map<String, Icon> CACHE_ICONOS = new ConcurrentHashMap<>();
	private static final Map<String, Fondo> CACHE_FONDOS = new ConcurrentHashMap<>();
	private static final Map<String, SoftReference<BufferedImage>> CACHE_ORIGINALES = new ConcurrentHashMap<>();

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
		return CACHE_ICONOS.computeIfAbsent(clave, k -> new ImageIcon(cargarEscalada(ruta, ancho, alto, pixelado)));
	}

	/** Imagen sin escalar (por ejemplo, el ícono de la ventana). */
	public static Image imagen(String ruta) {
		SoftReference<BufferedImage> ref = CACHE_ORIGINALES.get(ruta);
		BufferedImage imagen = ref == null ? null : ref.get();
		if (imagen == null) {
			imagen = leer(ruta, 1);
			CACHE_ORIGINALES.put(ruta, new SoftReference<>(imagen));
		}
		return imagen;
	}

	/** Imagen escalada a un tamaño exacto (el fondo al tamaño de la ventana). Guarda solo el último tamaño. */
	public static Image imagen(String ruta, int ancho, int alto) {
		if (ancho <= 0 || alto <= 0) {
			return imagen(ruta);
		}
		Fondo fondo = CACHE_FONDOS.get(ruta);
		if (fondo == null || !fondo.mide(ancho, alto)) {
			fondo = new Fondo(ancho, alto, cargarEscalada(ruta, ancho, alto, false));
			CACHE_FONDOS.put(ruta, fondo); // reemplaza el tamaño anterior: no se acumulan fondos de 8 MB
		}
		return fondo.imagen;
	}

	/** Carga y escala en un hilo de baja prioridad; si después se pide la misma imagen, ya está lista. */
	public static void precargarEnSegundoPlano(Collection<Runnable> tareas) {
		for (Runnable tarea : tareas) {
			PRECARGA.execute(() -> {
				try {
					tarea.run();
				} catch (RuntimeException | OutOfMemoryError e) {
					// la precarga es optimista: si algo falla, se cargará al usarse
					liberarMemoria();
				}
			});
		}
	}

	/** Vacía las cachés regenerables (se vuelven a cargar a demanda). */
	public static void liberarMemoria() {
		CACHE_ORIGINALES.clear();
		CACHE_FONDOS.clear();
	}

	// ----------------------------------------------------------------- Implementación

	private static BufferedImage cargarEscalada(String ruta, int ancho, int alto, boolean pixelado) {
		int[] tamano = medir(ruta);
		int factor = pixelado ? 1 : factorSubmuestreo(tamano, ancho, alto);
		BufferedImage origen;
		try {
			origen = leer(ruta, factor);
		} catch (OutOfMemoryError e) {
			liberarMemoria(); // reintenta una vez con las cachés vacías y más submuestreo
			origen = leer(ruta, Math.max(2, factor * 2));
		}
		return escalar(origen, ancho, alto, pixelado);
	}

	private static int factorSubmuestreo(int[] tamano, int ancho, int alto) {
		if (tamano == null) {
			return 1;
		}
		int factor = Math.min(tamano[0] / (ancho * MARGEN_CALIDAD), tamano[1] / (alto * MARGEN_CALIDAD));
		return Math.max(1, factor);
	}

	/** Ancho y alto sin decodificar la imagen (solo lee el encabezado). */
	private static int[] medir(String ruta) {
		try (InputStream in = recurso(ruta).openStream(); ImageInputStream iis = ImageIO.createImageInputStream(in)) {
			Iterator<ImageReader> lectores = ImageIO.getImageReaders(iis);
			if (!lectores.hasNext()) {
				return null;
			}
			ImageReader lector = lectores.next();
			try {
				lector.setInput(iis, true, true);
				return new int[] { lector.getWidth(0), lector.getHeight(0) };
			} finally {
				lector.dispose();
			}
		} catch (IOException | RuntimeException e) {
			return null;
		}
	}

	/** Lee la imagen tomando 1 de cada {@code factor} píxeles en cada eje. */
	private static BufferedImage leer(String ruta, int factor) {
		try (InputStream in = recurso(ruta).openStream(); ImageInputStream iis = ImageIO.createImageInputStream(in)) {
			Iterator<ImageReader> lectores = iis == null ? null : ImageIO.getImageReaders(iis);
			if (lectores != null && lectores.hasNext()) {
				ImageReader lector = lectores.next();
				try {
					lector.setInput(iis, true, true);
					ImageReadParam parametros = lector.getDefaultReadParam();
					if (factor > 1) {
						parametros.setSourceSubsampling(factor, factor, 0, 0);
					}
					BufferedImage imagen = lector.read(0, parametros);
					if (imagen != null) {
						return imagen;
					}
				} finally {
					lector.dispose();
				}
			}
		} catch (IOException | RuntimeException e) {
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

	/** Fondo escalado al tamaño actual de la ventana. */
	private static final class Fondo {
		private final int ancho;
		private final int alto;
		private final Image imagen;

		private Fondo(int ancho, int alto, Image imagen) {
			this.ancho = ancho;
			this.alto = alto;
			this.imagen = imagen;
		}

		private boolean mide(int ancho, int alto) {
			return this.ancho == ancho && this.alto == alto;
		}
	}
}
