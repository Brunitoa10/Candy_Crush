package Entidades;

import EstrategiaDetonaciones.EstategiaDetonacion;
import EstrategiaDetonaciones.EstrategiaDetonacionRocas;
import Logica.Color;
import Tablero.TableroNotificable;

public class Roca extends Obstaculo{

	private static final EstategiaDetonacion DETONACION = new EstrategiaDetonacionRocas();

	public Roca(TableroNotificable tablero, int f, int c, boolean visible) {
		super(tablero, f, c, "/assets/obstaculo/", new Color(Color.ROCA), visible);
		set_EstrategiaDetonacion(DETONACION);
		cargarImagenesRepresentativas(rutadeLaImagen);
	}

	public boolean esAfectadaPorExplosionAdyacente() {
		return true;
	}
}
