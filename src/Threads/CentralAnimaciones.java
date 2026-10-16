package Threads;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingUtilities;

import GUI.Celda;
import GUI.VentanaNotificable;

public class CentralAnimaciones implements ManejadorAnimaciones{

	private static final int PASO_MOVIMIENTO = 10;
	private static final int DELAY_MOVIMIENTO_MS = 16; // ~60 fps
	private static final int DELAY_DETONACION_MS = 400;
	private static final int DELAY_VISIBILIDAD_MS = 750;
	
	protected VentanaNotificable ventana; 
	protected List<Animador> animadores_pendientes;
	protected HashMap<Celda, List<Animador>> mapeo_celda_animadores;
	protected int prioridad_ultimo_animador_lanzado;
	protected int cantidad_animadores_ultima_prioridad_lanzados;
	
	
	public CentralAnimaciones(VentanaNotificable ventana) {
		this.ventana = ventana;
		animadores_pendientes = new LinkedList<Animador>();
		mapeo_celda_animadores = new HashMap<Celda, List<Animador>>();
		prioridad_ultimo_animador_lanzado = PrioridadAnimaciones.PRIORIDAD_SIN_PRIORIDAD;
		cantidad_animadores_ultima_prioridad_lanzados = 0;
	}
	
	public void animar_intercambio(Celda celda) {
		Animador animador = new AnimadorMovimiento(this, PASO_MOVIMIENTO, DELAY_MOVIMIENTO_MS, celda);
		agregar_animador_y_lanzar_pendientes(animador);
	}
	
	public void animar_cambio_foco(Celda celda) {
		Animador animador = new AnimadorCambioFoco(this, celda);
		agregar_animador_y_lanzar_pendientes(animador);
	}

	public void animar_detonacion(Celda celda) {
		Animador animador = new AnimadorDetonacion(this, celda, DELAY_DETONACION_MS);
		agregar_animador_y_lanzar_pendientes(animador);
	}
	
	public void animar_caida(Celda celda) {
		Animador animador = new AnimadorCaida(this, PASO_MOVIMIENTO, DELAY_MOVIMIENTO_MS, celda);
		agregar_animador_y_lanzar_pendientes(animador);
	}
	
	public void animar_cambio_visibilidad(Celda celda) {
		Animador animador = new AnimadorVisibilidad(this, celda, DELAY_VISIBILIDAD_MS);
		agregar_animador_y_lanzar_pendientes(animador);
	}
	
	/**
	 * Confinamiento al hilo de Swing: todas las animaciones se encolan, ejecutan y terminan
	 * en el EDT, así la cola y el mapa no necesitan sincronización. Si por error se llama
	 * desde otro hilo, se reencola en el EDT.
	 */
	protected void agregar_animador_y_lanzar_pendientes(Animador animador) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(() -> agregar_animador_y_lanzar_pendientes(animador));
			return;
		}
		ventana.notificarse_animacion_en_progreso();
		animadores_pendientes.add(animador);
		lanzar_pendientes();
	}
	
	protected void lanzar_pendientes() {
		Animador animador;
		int prioridad_animador;
		
		while(existen_pendientes() && existe_proxima_animacion_preparada()) {
			animador = animadores_pendientes.remove(0);
			prioridad_animador = animador.get_prioridad();
			agregar_animador_en_progreso(animador);
			incrementar_animadores_activos_y_actualizar_ultima_prioridad_lanzado(prioridad_animador);
			animador.comenzar_animacion();
		}
	}
	
	protected void agregar_animador_en_progreso(Animador animador) {
		Celda celda_animador = animador.get_celda_asociada();
		List<Animador> animadores_celda = mapeo_celda_animadores.get(celda_animador);
		if (animadores_celda != null) {
			animadores_celda.add(animador);
		}else {
			animadores_celda = new LinkedList<Animador>();
			animadores_celda.add(animador);
			mapeo_celda_animadores.put(celda_animador, animadores_celda);
		}
	}
	
	protected void incrementar_animadores_activos_y_actualizar_ultima_prioridad_lanzado(int prioridad_ultimo) {
		prioridad_ultimo_animador_lanzado = prioridad_ultimo;
		cantidad_animadores_ultima_prioridad_lanzados ++;
	}
	
	protected boolean existen_pendientes() {
		return ! animadores_pendientes.isEmpty();
	}
	
	protected boolean existe_proxima_animacion_preparada() {
		boolean retorno = false;
		
		if (existen_animadores_activos()) {
			retorno = existe_preparada_con_animadores_activos();
		}else {
			retorno = existe_preparada_sin_animadores_activos();
		}
		return retorno;
	}
	
	protected boolean existen_animadores_activos() {
		return cantidad_animadores_ultima_prioridad_lanzados != 0;
	}
	
	protected boolean existe_preparada_con_animadores_activos() {
		Animador animador_en_tope_espera;
		Celda celda_animador;
		int prioridad_animador;
		boolean retorno = false;
		
		if (existen_pendientes()) {
			animador_en_tope_espera = animadores_pendientes.get(0);
			celda_animador = animador_en_tope_espera.get_celda_asociada();
			retorno = ! tiene_animadores_en_progreso(celda_animador);
			prioridad_animador = animador_en_tope_espera.get_prioridad();
			retorno = retorno && tiene_igual_prioridad_que_ultimo_animador_lanzado(prioridad_animador);
		}
		return retorno;
	}
	
	protected boolean existe_preparada_sin_animadores_activos() {
		return existen_pendientes();
	}

	protected boolean tiene_animadores_en_progreso(Celda celda) {
		boolean retorno = false;
		List<Animador> animadores_celda_pendientes = mapeo_celda_animadores.get(celda);
		if (animadores_celda_pendientes != null) {
			retorno = ! animadores_celda_pendientes.isEmpty();
		}
		return retorno;
	}
	
	protected boolean tiene_igual_prioridad_que_ultimo_animador_lanzado(int prioridad) {
		return prioridad == prioridad_ultimo_animador_lanzado;
	}
	
	public void notificarse_finalizacion_animador(Animador animador) {
		ventana.notificarse_animacion_finalizada();
		quitar_animador_en_progreso(animador);
		decrementar_animadores_activos_y_actualizar_ultima_prioridad_lanzado();
		lanzar_pendientes();
	}
	
	protected void quitar_animador_en_progreso(Animador animador) {
		Celda celda_animador = animador.get_celda_asociada();
		List<Animador> animadores_celda = mapeo_celda_animadores.get(celda_animador);
		if (animadores_celda != null) {
			animadores_celda.remove(animador);
			if (animadores_celda.isEmpty()) {
				mapeo_celda_animadores.remove(celda_animador); // evita acumular celdas ya eliminadas
			}
		}
	}
	
	protected void decrementar_animadores_activos_y_actualizar_ultima_prioridad_lanzado() {
		cantidad_animadores_ultima_prioridad_lanzados --;
		if (cantidad_animadores_ultima_prioridad_lanzados == 0)
			prioridad_ultimo_animador_lanzado = PrioridadAnimaciones.PRIORIDAD_SIN_PRIORIDAD;
	}
}