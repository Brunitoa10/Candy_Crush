package Entidades;

import EstrategiaDetonaciones.EstategiaDetonacion;
import EstrategiaDetonaciones.EstrategiaDetonacionGemaNormal;
import Logica.Color;
import Tablero.TableroNotificable;

public class GemaNormal extends Gema{

	private static final EstategiaDetonacion DETONACION = new EstrategiaDetonacionGemaNormal();

	public GemaNormal(TableroNotificable tablero, int f, int c, Color col, boolean visible, String skin) {
		super(tablero, f, c, col, skin, "gema_normal/", visible);
		set_EstrategiaDetonacion(DETONACION);
		cargarImagenesRepresentativas(rutadeLaImagen);
	}

	public void intercambiar(Entidad entidad) {
		entidad.intercambiar_con(this);
	}

	//Metodos de interfaces
	public boolean es_posible_intercambiar(Entidad entidad) {
		return entidad.puede_recibir(this);
	}

	public boolean se_produce_match_con(Entidad e) {
		return e.aplica_match_con(this);
	}

	public boolean aplica_match_con(GemaCruzada c) {
		return c.get_color() == this.get_color();
	}
}
