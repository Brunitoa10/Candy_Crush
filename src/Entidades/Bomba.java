package Entidades;

import EstrategiaDetonaciones.EstategiaDetonacion;
import EstrategiaDetonaciones.EstrategiaDetonacionBomba;
import Logica.Color;
import Tablero.Tablero;
import Tablero.TableroNotificable;
import Timer.ObservableTimer;
import Timer.TickEvent;
import Timer.TickObserver;

/**
 * Obstáculo con cuenta regresiva (Observer del timer). Si llega a cero, termina la partida.
 * Ya no depende de Logica: recibe el timer y una acción a ejecutar al explotar (DIP).
 */
public class Bomba extends Obstaculo implements TickObserver {

	protected static final int TIEMPO_INICIAL = 30;
	private static final EstategiaDetonacion DETONACION = new EstrategiaDetonacionBomba();

	protected int tiempo = TIEMPO_INICIAL;
	protected final ObservableTimer notificador;
	protected final Runnable alExplotar;

	public Bomba(TableroNotificable tablero, int f, int c, Color col, boolean visible, ObservableTimer notificador, Runnable alExplotar) {
		super(tablero, f, c, "/assets/obstaculo/bomba/", col, visible);
		this.notificador = notificador;
		this.alExplotar = alExplotar;
		set_EstrategiaDetonacion(DETONACION);
		cargarImagenesRepresentativas(rutadeLaImagen);
		notificador.addObserver(this);
	}

	@Override
	public void detonar(Tablero t) {
		notificador.desuscribirse(this);
		super.detonar(t);
	}

	private void explotar() {
		notificador.desuscribirse(this);
		alExplotar.run();
	}

	public boolean esAfectadaPorExplosionAdyacente()
	{
		return true;
	}

	@Override
	public void cargarImagenesRepresentativas(String ri) {
		super.cargarImagenesRepresentativas(ri);
		imagenes[IMG_NORMAL] = ri + tiempo + "/" + tiempo + ".png";
		imagenes[IMG_ENFOCADA] = ri + tiempo + "/" + tiempo + "-cursor.png";
	}

	public int getTiempo() {
		return tiempo;
	}

	@Override
	public void update(TickEvent event) {
		tiempo--;
		cargarImagenesRepresentativas(rutadeLaImagen);
		refrescarImagen();
		if (tiempo <= 0) {
			explotar();
		}
	}

	private void refrescarImagen() {
		if (enfocada) {
			enfocar();
		} else {
			desenfocar();
		}
	}
}
