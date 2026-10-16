package Nivel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Scanner;

import EstrategiaMatch.EstrategiaFactory;
import EstrategiaMatch.Estrategias;
import GeneradorEntidades.EntidadFactoryRegistry;
import Logica.Logica;
import Tablero.TableroJuego;

/**
 * Lee un archivo de nivel y arma el Nivel y el Tablero (patrón Builder "simplificado").
 * Formato: tamaño / fila,col inicial / movimientos / tiempo / vidas / t / objetivos... / f /
 *          estrategias... / f / matriz de entidades.
 */
public final class GeneradorNivel {

	private static final String RECURSO_NIVEL = "/Niveles/Nivel%d.txt";
	private static final String ARCHIVO_NIVEL = "src/Niveles/Nivel%d.txt";
	private static final String TERMINADOR_ENCABEZADO = "t";
	private static final String FIN_DE_SECCION = "f";

	private GeneradorNivel() {}

	public static Nivel cargar_nivel_y_tablero(TableroJuego t, int nivel, Logica l, String skin) {
		Nivel miNivel = new Nivel(0, 0, l);
		try (Scanner input = abrirNivel(nivel)) {
			int filas = leerEntero(input);
			int columnas = filas;

			leerEncabezado(input, miNivel);
			leerObjetivos(input, miNivel);
			miNivel.setEstrategias(leerEstrategias(input));

			t.resetar_tablero(filas, columnas);
			leerTablero(input, t, filas, columnas, skin);
		} catch (Exception ex) {
			throw new IllegalStateException("No se pudo cargar el nivel " + nivel + ": " + ex.getMessage(), ex);
		}
		return miNivel;
	}

	/** Busca el nivel en el classpath (funciona también desde un .jar); si no, en el disco. */
	private static Scanner abrirNivel(int nivel) throws FileNotFoundException {
		InputStream recurso = GeneradorNivel.class.getResourceAsStream(String.format(RECURSO_NIVEL, nivel));
		if (recurso != null) {
			return new Scanner(recurso, "UTF-8");
		}
		return new Scanner(new File(String.format(ARCHIVO_NIVEL, nivel)), "UTF-8");
	}

	private static void leerEncabezado(Scanner input, Nivel miNivel) {
		String[] posicionInicial = input.nextLine().split(",");
		miNivel.setPosicionJugador(Integer.parseInt(posicionInicial[0].trim()), Integer.parseInt(posicionInicial[1].trim()));

		int movimientos = leerEntero(input);
		miNivel.setMovimientos(movimientos);
		miNivel.setTotalMovimientos(movimientos);

		int tiempo = leerEntero(input);
		miNivel.setTiempo(tiempo);
		miNivel.setTotalTiempo(tiempo);

		miNivel.setVidas(leerEntero(input));

		String terminador = input.nextLine().trim();
		if (!terminador.equals(TERMINADOR_ENCABEZADO)) {
			throw new IllegalArgumentException("Formato de archivo inválido. Se esperaba un terminador 't'.");
		}
	}

	private static void leerObjetivos(Scanner input, Nivel miNivel) {
		int id = 0;
		String linea;
		while (input.hasNextLine() && !(linea = input.nextLine().trim()).equals(FIN_DE_SECCION)) {
			String[] datos = linea.split(",");
			miNivel.agregarObjetivo(id++, new Objetivos(Integer.parseInt(datos[0].trim()), Integer.parseInt(datos[1].trim())));
		}
	}

	private static LinkedList<Estrategias> leerEstrategias(Scanner input) {
		LinkedList<Estrategias> estrategias = new LinkedList<>();
		String linea;
		while (input.hasNextLine() && !(linea = input.nextLine().trim()).equals(FIN_DE_SECCION)) {
			if (!linea.isEmpty()) {
				estrategias.addLast(EstrategiaFactory.createEstrategia(linea.charAt(0)));
			}
		}
		return estrategias;
	}

	private static void leerTablero(Scanner input, TableroJuego t, int filas, int columnas, String skin) {
		for (int i = 0; i < filas; i++) {
			String[] valores = input.nextLine().trim().split("\\s+");
			for (int j = 0; j < columnas; j++) {
				String[] partes = valores[j].split(",");
				t.agregar_entidad(EntidadFactoryRegistry.obtener(partes[0]).crear(t, i, j, partes, skin));
			}
		}
	}

	private static int leerEntero(Scanner input) {
		return Integer.parseInt(input.nextLine().replace("\uFEFF", "").trim());
	}
}
