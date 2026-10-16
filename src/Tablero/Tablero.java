package Tablero;

import java.util.*;

import Entidades.*;
import EstrategiaMatch.*;
import GeneradorEntidades.EntidadFactory;
import GeneradorEntidades.EntidadFactoryRegistry;
import Logica.*;
import Timer.ObservableTimer;

public class Tablero implements TableroJuego{

	private static final Random RANDOM = new Random();
	private static final String RUTA_GEMAS = "/assets/gemas/";

	protected Logica miLogica;
	protected Entidad entidades[][];
	protected int filas,columnas;
	protected int fJugador,cJugador;
	protected List<Entidad> entidades_asociadas;
	protected String skin;
	protected AdministradorEstrategias miAdministradordeEstrategias;

	public Tablero(Logica l) {
		this(l, null);
	}

	public Tablero(Logica l, String skin) {
		miLogica = l;
		this.skin = skin;
	}

	// ----------------------------------------------------------------- Getters / setters

	public Entidad get_entidad(int f, int c) {
		return entidades[f][c];
	}

	public void set_Skin(String skin)
	{
		this.skin=skin;
	}

	public int getFila() {
		return filas;
	}

	public int getColumna() {
		return columnas;
	}

	public LinkedList<Estrategias> getEstrategias()
	{
		return miAdministradordeEstrategias.get_lista_de_Estrategias();
	}

	public void crearAdministradorEstrategias(LinkedList<Estrategias> estrategias) {
		miAdministradordeEstrategias = new AdministradorEstrategias(estrategias, this);
	}

	@Override
	public ObservableTimer obtenerObserver() {
		return miLogica.getTimer();
	}

	@Override
	public void notificarExplosionBomba() {
		miLogica.notificarDerrotaPorVidas(); // el timer ya notifica en el hilo de Swing
	}

	// ----------------------------------------------------------------- Armado del tablero

	public void resetar_tablero(int cant_filas, int cant_columnas) {
		filas = cant_filas;
		columnas = cant_columnas;
		fJugador = cJugador = 0;
		entidades = new Entidad[cant_filas][cant_columnas];
		entidades_asociadas = new LinkedList<Entidad>();
	}

	public void agregar_entidad(Entidad e) {
		entidades[e.get_fila()][e.get_columna()] = e;
	}

	public void agregar_entidad_y_asociada(Hielo g) {
		agregar_entidad(g);
		entidades_asociadas.add(g.get_gema_interna());
	}

	public void asociar_entidades_logicas_y_graficas() {
		for (int f = 0; f < filas; f++) {
			for (int c = 0; c < columnas; c++) {
				Entidad entidad = entidades[f][c];
				if (entidad.tieneGemaInterna()) {
					// la gema dentro del hielo tiene su propia celda, debajo de la del hielo
					miLogica.asociar_entidad_logica_y_grafica(entidad.get_gema_interna());
				}
				miLogica.asociar_entidad_logica_y_grafica(entidad);
			}
		}
		for (Entidad entidad : entidades_asociadas) {
			miLogica.asociar_entidad_logica_y_grafica(entidad);
		}
	}

	public void reubicar(Entidad e) {
		agregar_entidad(e);
	}

	// ----------------------------------------------------------------- Jugador

	public void fijar_jugador(int f, int c) {
		entidades[fJugador][cJugador].desenfocar();
		entidades[f][c].enfocar();
		fJugador = f;
		cJugador = c;
	}

	public void mover_jugador(Direccion d) {
		int nf = d.filaDestino(fJugador);
		int nc = d.columnaDestino(cJugador);
		if (en_rango(nf, nc)) {
			fijar_jugador(nf, nc);
		}
	}

	protected void cambiar_posicion_jugador(int nueva_fila, int nueva_columna) {
		fJugador = nueva_fila;
		cJugador = nueva_columna;
	}

	// ----------------------------------------------------------------- Consultas

	public boolean en_rango(int fila, int columna){
		return 0 <= fila && fila < filas && 0 <= columna && columna < columnas;
	}

	public boolean hayEfectoExplosionAdyacente(int filaVecina, int columnaVecina) {
		return en_rango(filaVecina, columnaVecina) && entidades[filaVecina][columnaVecina].esAfectadaPorExplosionAdyacente();
	}

	/** Ruta de la imagen que representa a un tipo de gema (usada en el panel de objetivos). */
	public String obtenerTipoGema(int tipoGema) {
		return RUTA_GEMAS + skin + "/gema_normal/" + tipoGema + ".png";
	}

	// ----------------------------------------------------------------- Intercambio

	private static final int MAXIMO_CASCADAS = 20;

	public boolean intercambiar_entidades(Direccion d) {
		int filaDestino = d.filaDestino(fJugador);
		int columnaDestino = d.columnaDestino(cJugador);
		if (!en_rango(filaDestino, columnaDestino)) {
			return false;
		}

		Entidad entidadOrigen = entidades[fJugador][cJugador];
		Entidad entidadDestino = entidades[filaDestino][columnaDestino];
		if (!entidadOrigen.es_posible_intercambiar(entidadDestino)) {
			return false;
		}
		return realizarIntercambioYTransicion(entidadOrigen, entidadDestino, fJugador, cJugador, filaDestino, columnaDestino);
	}

	private boolean realizarIntercambioYTransicion(Entidad entidadOrigen, Entidad entidadDestino,
			int filaOrigen, int columnaOrigen, int filaDestino, int columnaDestino) {
		cambiar_posicion_jugador(filaDestino, columnaDestino);
		entidadOrigen.intercambiar(entidadDestino);

		EfectosDeTransicion efecto = calcular_efectos_por_intercambio(entidadOrigen, entidadDestino,
				filaOrigen, columnaOrigen, filaDestino, columnaDestino);

		if (efecto.existen_entidades_a_detonar()) {
			transicionar_proximo_estado(efecto);
			reenfocarJugador();
			return true;
		}
		deshacerIntercambio(filaOrigen, columnaOrigen, filaDestino, columnaDestino);
		return false; // un intercambio sin match no consume movimientos
	}

	/**
	 * Se intercambia lo que HOY está en cada celda (no las referencias originales): con hielo,
	 * la gema que salió del hielo es otra, y reintercambiar las referencias originales no deshacía nada.
	 */
	private void deshacerIntercambio(int filaOrigen, int columnaOrigen, int filaDestino, int columnaDestino) {
		cambiar_posicion_jugador(filaOrigen, columnaOrigen);
		entidades[filaOrigen][columnaOrigen].intercambiar(entidades[filaDestino][columnaDestino]);
		reenfocarJugador();
	}

	protected EfectosDeTransicion calcular_efectos_por_intercambio(Entidad entidad_origen, Entidad entidad_destino,
			int filaOrigen, int columnaOrigen, int filaDestino, int columnaDestino) {
		EfectosDeTransicion efecto = new EfectosDeTransicion();
		if (entidad_origen.se_produce_match_con(entidad_destino)) {
			efecto.agregar_entidad_a_detonar(entidad_origen);
			efecto.agregar_entidad_a_detonar(entidad_destino);
		} else {
			// se analizan las dos CELDAS involucradas (antes, con hielo, se analizaba dos veces la misma)
			aplicarResultado(efecto, miAdministradordeEstrategias.buscar_match(entidades[filaOrigen][columnaOrigen]));
			aplicarResultado(efecto, miAdministradordeEstrategias.buscar_match(entidades[filaDestino][columnaDestino]));
		}
		return efecto;
	}

	private void aplicarResultado(EfectosDeTransicion efecto, Resultado resultado) {
		if (resultado == null) {
			return;
		}
		List<Entidad> gemasARomper = resultado.get_Gemas_a_romper();
		if (!gemasARomper.isEmpty()) {
			miLogica.actualizarObjetivos(gemasARomper);
			gemasARomper.forEach(efecto::agregar_entidad_a_detonar);
		}
		if (resultado.get_resultado() != null) {
			efecto.agregar_potenciador(resultado.get_resultado());
		}
	}

	// ----------------------------------------------------------------- Transición de estado

	/**
	 * 1) detona, 2) ubica los potenciadores generados, 3) aplica gravedad y rellena,
	 * 4) repite mientras se formen líneas nuevas (cascadas).
	 * Toda la lógica se resuelve de una vez; las animaciones quedan encoladas en orden
	 * (detonación -> caída) gracias a las prioridades de CentralAnimaciones.
	 */
	protected void transicionar_proximo_estado(EfectosDeTransicion efecto) {
		detonar(efecto.entidades_a_detonar());
		efecto.potenciadores().forEach(this::incorporarPotenciador);
		aplicarGravedadYRellenar();
		resolverCascadas();
	}

	private void resolverCascadas() {
		for (int i = 0; i < MAXIMO_CASCADAS; i++) {
			List<Entidad> lineas = miAdministradordeEstrategias.buscarTodasLasLineas();
			if (lineas.isEmpty()) {
				return;
			}
			miLogica.actualizarObjetivos(lineas);
			detonar(lineas);
			aplicarGravedadYRellenar();
		}
	}

	protected void detonar(List<Entidad> entidades_a_detonar) {
		for (Entidad e : entidades_a_detonar) {
			if (!estaVacia(e)) { // pudo haber explotado antes por un potenciador de la misma tanda
				miLogica.agregarScore(e.get_score());
				e.detonar(this);
			}
		}
	}

	/** Registra (score + objetivos) un área detonada por un potenciador. */
	public void detono(List<Entidad> entidadesDetonadas) {
		miLogica.actualizarObjetivos(entidadesDetonadas);
		for (Entidad ent : entidadesDetonadas) {
			miLogica.agregarScore(ent.get_score());
		}
	}

	/** Ubica la gema especial generada por un match (solo si su celda quedó libre) y le crea su celda gráfica. */
	private void incorporarPotenciador(Entidad potenciador) {
		int f = potenciador.get_fila();
		int c = potenciador.get_columna();
		if (en_rango(f, c) && estaVacia(entidades[f][c])) {
			agregar_entidad(potenciador);
			miLogica.asociar_entidad_logica_y_grafica(potenciador);
			potenciador.mostrar();
		}
	}

	/**
	 * Gravedad por columna: compacta hacia abajo todo lo que no está vacío y rellena arriba con
	 * gemas nuevas que "caen" desde fuera del tablero. Cada entidad conserva su única celda.
	 * (Antes se creaba una celda nueva por cada paso de caída y las viejas quedaban como fantasmas,
	 * y las gemas nuevas se escribían en la fila 0 pisando a las que estaban ahí.)
	 */
	private void aplicarGravedadYRellenar() {
		for (int c = 0; c < columnas; c++) {
			int filaLibre = filas - 1;
			for (int f = filas - 1; f >= 0; f--) {
				Entidad entidad = entidades[f][c];
				if (!estaVacia(entidad)) {
					if (f != filaLibre) {
						entidades[filaLibre][c] = entidad;
						entidad.intercambiar_Caida(filaLibre, c);
					}
					filaLibre--;
				}
			}
			int faltantes = filaLibre + 1;
			for (int f = filaLibre; f >= 0; f--) {
				GemaNormal nueva = crearGemaNormalRandom(f - faltantes, c); // nace arriba, fuera del tablero
				entidades[f][c] = nueva;
				miLogica.asociar_entidad_logica_y_grafica(nueva);
				nueva.intercambiar_Caida(f, c);
			}
		}
	}

	/** Deja el cursor solo en la celda del jugador (las entidades se mueven y explotan). */
	private void reenfocarJugador() {
		for (int f = 0; f < filas; f++) {
			for (int c = 0; c < columnas; c++) {
				boolean esCursor = f == fJugador && c == cJugador;
				Entidad entidad = entidades[f][c];
				desenfocarSiCorresponde(entidad, esCursor);
				if (entidad.get_gema_interna() != entidad) {
					desenfocarSiCorresponde(entidad.get_gema_interna(), false);
				}
			}
		}
		Entidad actual = entidades[fJugador][cJugador];
		if (!actual.estaEnfocada()) {
			actual.enfocar();
		}
	}

	private void desenfocarSiCorresponde(Entidad entidad, boolean esCursor) {
		if (entidad.estaEnfocada() && !esCursor) {
			entidad.desenfocar();
		}
	}

	private boolean estaVacia(Entidad entidad) {
		return entidad == null || entidad.get_color() == Color.TRANSPARENTE;
	}

	private GemaNormal crearGemaNormalRandom(int fila, int columna) {
		EntidadFactory gemaNormalFactory = EntidadFactoryRegistry.obtener(EntidadFactoryRegistry.GEMA_NORMAL);
		int colorAleatorio = RANDOM.nextInt(Color.CANTIDAD_COLORES_GEMA) + 1;
		String[] token = { EntidadFactoryRegistry.GEMA_NORMAL, String.valueOf(colorAleatorio) };
		return (GemaNormal) gemaNormalFactory.crear(this, fila, columna, token, skin);
	}
}
