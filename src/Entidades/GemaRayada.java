package Entidades;

import EstrategiaDetonaciones.EstategiaDetonacion;
import EstrategiaDetonaciones.EstrategiaDetonacionGemaRayadaHorizontal;
import EstrategiaDetonaciones.EstrategiaDetonacionGemaRayadaVertical;
import Logica.Color;
import Tablero.TableroNotificable;

public class GemaRayada extends Gema{

	public static final int HORIZONTAL = 5;
	public static final int VERTICAL = 1;

	private static final int SCORE_HORIZONTAL = 45;
	private static final int SCORE_VERTICAL = 35;
	private static final EstategiaDetonacion DETONACION_HORIZONTAL = new EstrategiaDetonacionGemaRayadaHorizontal();
	private static final EstategiaDetonacion DETONACION_VERTICAL = new EstrategiaDetonacionGemaRayadaVertical();

	private final int direccion;

	public GemaRayada(TableroNotificable tablero, int f, int c, Color col, int direccion, boolean visible, String skin) {
		super(tablero, f, c, col, skin, "gema_rayada/" + direccion, visible);
		this.direccion = direccion;
		set_EstrategiaDetonacion(esHorizontal() ? DETONACION_HORIZONTAL : DETONACION_VERTICAL);
		cargarImagenesRepresentativas(rutadeLaImagen);
	}

	public boolean esHorizontal() {
		return direccion == HORIZONTAL;
	}

	public boolean es_posible_intercambiar(Entidad e) {
		return e.puede_recibir(this);
	}

	public void intercambiar(Entidad entidad) {
		entidad.intercambiar_con(this);
	}

	public int get_score()
	{
		return esHorizontal() ? SCORE_HORIZONTAL : SCORE_VERTICAL;
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
