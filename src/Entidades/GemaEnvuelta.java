package Entidades;

import EstrategiaDetonaciones.EstategiaDetonacion;
import EstrategiaDetonaciones.EstrategiaDetonacionGemaEnvuelta;
import Logica.Color;
import Tablero.TableroNotificable;

public class GemaEnvuelta extends Gema{

	private static final int SCORE = 100;
	private static final EstategiaDetonacion DETONACION = new EstrategiaDetonacionGemaEnvuelta();

	public GemaEnvuelta(TableroNotificable tablero, int f, int c, Color col, boolean visible, String skin) {
		super(tablero, f, c, col, skin, "gema_envuelta/", visible);
		set_EstrategiaDetonacion(DETONACION);
		cargarImagenesRepresentativas(rutadeLaImagen);
	}

	public int get_score()
	{
		return SCORE;
	}

	public void intercambiar(Entidad entidad) {
		entidad.intercambiar_con(this);
	}

	//Metodos de la interfaz matcheable

	public boolean es_posible_intercambiar(Entidad e) {
		return e.puede_recibir(this);
	}

	public boolean se_produce_match_con(Entidad e) {
		return e.aplica_match_con(this);
	}

	public boolean aplica_match_con(GemaEnvuelta c) {
		return true;
	}

	public boolean aplica_match_con(GemaRayada c) {
		return true;
	}

	public boolean aplica_match_con(GemaCruzada c) {
		return true;
	}
}
