package Entidades;

import Logica.*;
import Tablero.TableroNotificable;

/**
 * Comportamiento por defecto de los obstáculos: no se intercambian ni matchean.
 * Las subclases que sí lo permiten (por ejemplo Hielo) redefinen lo necesario.
 */
public abstract class Obstaculo extends Entidad{

	public Obstaculo(TableroNotificable tablero,int f, int c, String ri, Color col, boolean visible) {
		super(tablero,f, c, ri,col, visible);
	}

	//Interfaz de intercambiable

	@Override
	public boolean puede_recibir(GemaNormal c) {
		return false;
	}

	@Override
	public boolean puede_recibir(GemaRayada p) {
		return false;
	}

	@Override
	public boolean puede_recibir(GemaEnvuelta p) {
		return false;
	}

	@Override
	public boolean puede_recibir(GemaCruzada p) {
		return false;
	}

	public boolean puede_recibir(Hielo p) {
		return false;
	}

	public boolean puede_recibir(Bomba p) {
		return false;
	}

	public boolean puede_recibir(Roca p) {
		return false;
	}

	public boolean es_posible_intercambiar(Entidad e) {
		return false;
	}

	//interfaz de matcheable
	public boolean se_produce_match_con(Entidad e) {
		return false;
	}

	public boolean aplica_match_con(GemaNormal c) {
		return false;
	}

	public boolean aplica_match_con(GemaEnvuelta c) {
		return false;
	}

	public boolean aplica_match_con(GemaRayada c) {
		return false;
	}

	public boolean aplica_match_con(GemaCruzada c) {
		return false;
	}

	public boolean aplica_match_con(Roca r) {
		return false;
	}

	public void intercambiar(Entidad entidad) {
		// Un obstáculo no inicia intercambios (antes delegaba y podía entrar en recursión infinita)
	}

	//Interfaz de intercambiable: por defecto los obstáculos no se mueven

	public void intercambiar_con(GemaNormal gemaNormal) {}

	public void intercambiar_con(GemaRayada gemaRayada) {}

	public void intercambiar_con(GemaEnvuelta gemaEnvuelta) {}

	public void intercambiar_con(GemaCruzada gemaCruzada) {}

	public void intercambiar_con(Roca roca) {}

	public void intercambiar_con(Bomba bomba) {}

	public void intercambiar_con(Hielo hielo) {}

	public Entidad get_gema_interna()
	{
		return this;
	}
}
