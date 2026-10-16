package Nivel;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Entidades.Entidad;
import EstrategiaMatch.Estrategias;
import Logica.Logica;

public class Nivel {
	//Atributos
	protected int movimientos, totalmovimientos;
	protected final Map<Integer, Objetivos> mapeoDeObjetivos;
	protected final Set<Objetivos> objetivosCumplidos;
	protected int fila_inicial_jugador;
	protected int columna_inicial_jugador;
	protected int tiempo, tiempoTotal;
	protected int vidas;
	protected final Logica miLogica; // antes era static y se usaba como "service locator"
	protected LinkedList<Estrategias> estrategias = new LinkedList<>();

	//Constructor
	public Nivel(int posX, int posY, Logica l) {
		fila_inicial_jugador = posX;
		columna_inicial_jugador = posY;
		mapeoDeObjetivos = new LinkedHashMap<>();
		objetivosCumplidos = new HashSet<>();
		miLogica = l;
	}

	//Posición inicial
	public int getFilaInicialJugador() {
		return fila_inicial_jugador;
	}

	public int getColumnaInicialJugador() {
		return columna_inicial_jugador;
	}

	public void setPosicionJugador(int posX, int posY) {
		fila_inicial_jugador = posX;
		columna_inicial_jugador = posY;
	}

	//Movimientos
	public void setMovimientos(int movimientos) {
		this.movimientos = movimientos;
	}

	public int getMovimientos() {
		return movimientos;
	}

	public int getTotalMovimientos() {
		return totalmovimientos; // antes devolvía los movimientos restantes
	}

	public void setTotalMovimientos(int m) {
		totalmovimientos = m;
	}

	/**
	 * Al quedarse sin movimientos se muestra la pantalla de derrota; la vida se descuenta
	 * al reintentar (igual que con el tiempo). Antes se descontaba dos veces y, con la última
	 * vida, se notificaba la derrota por vidas por duplicado.
	 */
	public void restarMovimientos() {
		movimientos--;
		if (movimientos == 0) {
			miLogica.notificarDerrotaPorMovimientos();
		}
	}

	//Tiempo
	public int getTiempo() {
		return tiempo;
	}

	public void setTiempo(int tiempo) {
		this.tiempo = tiempo;
	}

	public int getTotalTiempo() {
		return tiempoTotal;
	}

	public void setTotalTiempo(int t) {
		tiempoTotal = t;
	}

	//Vidas
	public int getVidas() {
		return vidas;
	}

	public void setVidas(int vida) {
		this.vidas = vida;
	}

	/** Solo descuenta: quién llama decide qué notificar (evita notificaciones duplicadas). */
	public void restarVidas() {
		vidas--;
	}

	//Estrategias
	public void setEstrategias(LinkedList<Estrategias> estrategias)
	{
		this.estrategias = estrategias;
	}

	public LinkedList<Estrategias> getEstrategias() {
		return estrategias;
	}

	//Objetivos
	public int getCantidadDeObjetivos() {
		return mapeoDeObjetivos.size();
	}

	public void agregarObjetivo(int id, Objetivos objetivo) {
		mapeoDeObjetivos.put(id, objetivo);
	}

	public Objetivos obtenerObjetivo(int id) {
		return mapeoDeObjetivos.get(id);
	}

	public String[] obtenerInfoObjetivos() {
		String[] info = new String[mapeoDeObjetivos.size() * 4];
		int i = 0;
		for (Objetivos objetivo : mapeoDeObjetivos.values()) {
			info[i++] = "Destruir " + objetivo.getCantGemas() + " gemas de tipo: ";
			info[i++] = miLogica.obtenerTipoDeGema(objetivo.getTipoGema());
			info[i++] = String.valueOf(objetivo.getCantGemas());
			info[i++] = String.valueOf(objetivo.getTipoGema());
		}
		return info;
	}

	public void actualizarObjetivos(List<Entidad> listaCombos) {
		if (objetivosCumplidos.size() == mapeoDeObjetivos.size()) {
			return; // el nivel ya fue ganado
		}
		for (Entidad entidad : listaCombos) {
			if (entidad != null) {
				registrarGemaDestruida(entidad.get_gema_interna().get_color()); // un hielo cuenta como la gema que contiene
			}
		}
		if (objetivosCumplidos.size() == mapeoDeObjetivos.size()) {
			miLogica.notificarVictoriaPorObjetivos();
		}
	}

	private void registrarGemaDestruida(int tipoGema) {
		for (Objetivos objetivo : mapeoDeObjetivos.values()) {
			if (!objetivo.estaCumplido() && objetivo.getTipoGema() == tipoGema) {
				// Antes se acumulaba "progreso++" y se restaba 1, 2, 3... por gema
				objetivo.aumentarProgreso(1);
				if (objetivo.estaCumplido()) {
					objetivosCumplidos.add(objetivo);
				}
				miLogica.notificar_actualizacion_objetivos(objetivo.getCantGemas(), objetivo.getTipoGema());
			}
		}
	}
}
