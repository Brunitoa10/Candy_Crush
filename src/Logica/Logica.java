package Logica;

import java.awt.EventQueue;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import Entidades.Entidad;
import EstrategiaMatch.Estrategias;
import GUI.EntidadGrafica;
import GUI.GUI;
import Nivel.GeneradorNivel;
import Nivel.Nivel;
import Score.AdministradordeScore;
import Score.Jugador;
import Tablero.Tablero;
import Timer.ObservableTimer;
import Timer.TickObserver;

/**
 * Controlador del juego: coordina tablero, nivel, score, timer y GUI.
 */
public class Logica {

	private static final int NIVEL_INICIAL = 1;
	private static final int CANTIDAD_NIVELES = 5;

	// Atributos
	protected Tablero miTablero;
	protected GUI miGUI;
	protected Nivel miNivel;
	private int nivelActual;
	protected AdministradordeScore administradordeScore;
	protected String skin;
	protected final ObservableTimer observableTimer = new ObservableTimer();
	private boolean partidaTerminada;

	/** Reloj del nivel. Se vuelve a suscribir en cada carga, porque las cargas limpian el timer. */
	private final TickObserver relojNivel = evento -> avanzarReloj(); // el timer ya notifica en el hilo de Swing

	// Constructor
	public Logica() {
		miTablero = crearTablero();
		miGUI = new GUI(this, miTablero.getFila(), miTablero.getColumna());
		miGUI.mostrar(); // la GUI ya muestra la pantalla de modos de juego al construirse
	}

	// ----------------------------------------------------------------- Ciclo de vida de la partida

	public void inicializarJuego(){
		nivelActual = NIVEL_INICIAL;
		partidaTerminada = false;
		administradordeScore = new AdministradordeScore();
		prepararNivel(nivelActual);
		miGUI.inicializarJuego(miTablero.getFila(), miTablero.getColumna());
		ubicarEntidadesYJugador();
		observableTimer.resume();
	}

	public void iniciarSiguienteNivel() {
		nivelActual++;
		if (nivelActual <= CANTIDAD_NIVELES) {
			cargarNivel(nivelActual);
		} else {
			finalizarJuego();
		}
	}

	/** Se llama al reintentar tras perder por tiempo o por movimientos: descuenta una vida. */
	public void reintentarNivel() {
		miNivel.restarVidas();
		int vidasRestantes = miNivel.getVidas();
		if (vidasRestantes >= 1) {
			cargarNivel(nivelActual);
			miNivel.setVidas(vidasRestantes);
			miGUI.actualizarVidas();
		} else {
			notificarDerrotaPorVidas();
		}
	}

	/** Compatibilidad con el nombre anterior. */
	public void notificarDerrotaPorTiempo() {
		reintentarNivel();
	}

	public void reiniciarNivel(int nuevoNivel) {
		this.nivelActual = nuevoNivel;
		cargarNivel(nuevoNivel);
	}

	private void cargarNivel(int nivel) {
		prepararNivel(nivel);
		miGUI.reiniciarGUI(miTablero.getFila(), miTablero.getColumna());
		miGUI.cambiarFondo(nivel);
		miGUI.actualizarScore(administradordeScore.getScore());
		ubicarEntidadesYJugador();
		reanudarTiempo();
	}

	/** Pasos comunes a toda carga de nivel (antes estaban copiados en tres métodos). */
	private void prepararNivel(int nivel) {
		observableTimer.desuscribirTodo(); // antes de cargar: las bombas del nivel nuevo se suscriben al crearse
		miTablero = crearTablero();
		miNivel = GeneradorNivel.cargar_nivel_y_tablero(miTablero, nivel, this, skin);
		miTablero.crearAdministradorEstrategias(miNivel.getEstrategias());
		observableTimer.addObserver(relojNivel);
	}

	private void ubicarEntidadesYJugador() {
		miTablero.asociar_entidades_logicas_y_graficas();
		miTablero.fijar_jugador(miNivel.getFilaInicialJugador(), miNivel.getColumnaInicialJugador());
	}

	private Tablero crearTablero() {
		return new Tablero(this, skin); // antes el tablero nuevo quedaba sin skin al reiniciar
	}

	private void finalizarJuego() {
		registrarEnTopSiCorresponde();
		miGUI.mostrarMensajeFinDelJuego();
	}

	private void registrarEnTopSiCorresponde() {
		if (administradordeScore.entro_en_el_top5()) {
			String nombre = miGUI.obtenerNombreJugador();
			if (nombre != null) {
				administradordeScore.mejorJugador(nombre);
			}
		}
	}

	// ----------------------------------------------------------------- Acciones del jugador

	public void set_skin(String skin){
		this.skin = skin;
		miTablero.set_Skin(skin);
	}

	public void mover_jugador(Direccion direccion) {
		miTablero.mover_jugador(direccion);
	}

	public void intercambiar(Direccion direccion) {
		if (miTablero.intercambiar_entidades(direccion)) {
			miNivel.restarMovimientos();
			miGUI.actualizarMovimientos(miNivel.getMovimientos());
		}
	}

	// ----------------------------------------------------------------- Notificaciones de fin de nivel

	public void notificarDerrotaPorMovimientos() {
		miGUI.mostrarMensajeDerrotaPorMovimientos();
	}

	public void notificarDerrotaPorVidas() {
		if (partidaTerminada) {
			return; // evita doble pantalla / doble pedido de nombre
		}
		partidaTerminada = true;
		miGUI.mostrarMensajeDerrotaPorVidas();
		registrarEnTopSiCorresponde();
		nivelActual = NIVEL_INICIAL;
	}

	public void notificarVictoriaPorObjetivos() {
		miGUI.mostrarMensajeVictoriaPorObjetivos();
	}

	// ----------------------------------------------------------------- Tiempo

	private void avanzarReloj() {
		disminuirTiempo();
		miGUI.actualizarTimer(getTiempo());
		miGUI.refrescar();
	}

	public int disminuirTiempo() {
		if (miNivel.getTiempo() <= 0) {
			return 0; // ya se mostró la derrota (puede llegar un tick encolado después de pausar)
		}
		int tiempo = miNivel.getTiempo() - 1;
		if (tiempo <= 0) {
			miNivel.setTiempo(0);
			miGUI.mostrarMensajeDerrotaPorTiempo();
		} else {
			miNivel.setTiempo(tiempo);
		}
		return Math.max(tiempo, 0);
	}

	public ObservableTimer getTimer() {
		return observableTimer;
	}

	public void pausarTiempo() {
		observableTimer.pause();
	}

	public void reanudarTiempo() {
		observableTimer.resume();
	}

	// ----------------------------------------------------------------- Score

	public void agregarScore(int score) {
		administradordeScore.agregarScore(score);
		miGUI.actualizarScore(administradordeScore.getScore());
	}

	public int getScore() {
		return administradordeScore == null ? 0 : administradordeScore.getScore();
	}

	public List<Jugador> obtenerRanking() {
		return new AdministradordeScore().obtenerRanking(); // se puede consultar aun sin partida iniciada
	}

	public PriorityQueue<Jugador> obtenerListadeJugadores() {
		return new AdministradordeScore().obtenerListadeJugadores();
	}

	// ----------------------------------------------------------------- Consultas para la GUI

	public void asociar_entidad_logica_y_grafica(Entidad entidad_logica) {
		EntidadGrafica entidad_grafica = miGUI.agregar_entidad(entidad_logica);
		entidad_logica.set_EntidadGrafica(entidad_grafica);
	}

	public String[] obtenerInfoObjetivos() {
		return miNivel.obtenerInfoObjetivos();
	}

	public int getCantidadDeObjetivos() {
		return miNivel.getCantidadDeObjetivos();
	}

	public void notificar_actualizacion_objetivos(int cant, int tipoGema) {
		miGUI.actualizarProgreso(cant, tipoGema);
	}

	public void actualizarObjetivos(List<Entidad> listaCombos) {
		miNivel.actualizarObjetivos(listaCombos);
	}

	public String obtenerTipoDeGema(int tipoGema) {
		return miTablero.obtenerTipoGema(tipoGema);
	}

	public int getTiempo() {
		return miNivel.getTiempo();
	}

	public int getMovimientos() {
		return miNivel.getMovimientos();
	}

	public int getVidas() {
		return miNivel.getVidas();
	}

	public int getNivelActual() {
		return nivelActual;
	}

	public LinkedList<Estrategias> getEstrategias()
	{
		return miTablero.getEstrategias();
	}

	public Entidad getEntidadDelTablero(int posx, int posy) {
		return miTablero.get_entidad(posx, posy);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(Logica::new);
	}
}
