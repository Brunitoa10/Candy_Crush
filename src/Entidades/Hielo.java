package Entidades;

import EstrategiaDetonaciones.EstategiaDetonacion;
import EstrategiaDetonaciones.EstrategiaDetonacionHielo;
import GUI.EntidadGrafica;
import Logica.Color;
import Tablero.TableroNotificable;

/**
 * Obstáculo que envuelve a una gema (patrón Decorator): suma su score al de la gema
 * interna y, a diferencia del resto de obstáculos, sí se puede intercambiar.
 */
public class Hielo extends Obstaculo {

	private static final EstategiaDetonacion DETONACION = new EstrategiaDetonacionHielo();

	protected Entidad caramelo_interno;

	public Hielo(TableroNotificable tablero, int f, int c, Color color, boolean visible, Entidad ent) {
		super(tablero, f, c, "/assets/obstaculo/hielo/", color, visible);
		caramelo_interno = ent;
		set_EstrategiaDetonacion(DETONACION);
		cargarImagenesRepresentativas(rutadeLaImagen);
	}

	public void set_gema_interna(Entidad e)
	{
		caramelo_interno = e;
	}

	public void set_EntidadGrafica_entidadInterna(EntidadGrafica entidadGrafica)
	{
		caramelo_interno.set_EntidadGrafica(entidadGrafica);
	}

	public void cargarImagenesRepresentativas(String ri) {
		super.cargarImagenesRepresentativas(ri);
		imagenes[IMG_INTERNA] = caramelo_interno.get_imagenes_representativas()[0];
	}

	public int get_score()
	{
		return color.get_score() + caramelo_interno.get_score();
	}

	public boolean esAfectadaPorExplosionAdyacente()
	{
		return false;
	}

	public Entidad get_gema_interna() {
		return caramelo_interno;
	}

	public boolean tieneGemaInterna()
	{
		return true;
	}

	public void intercambiar(Entidad entidad) {
		entidad.intercambiar_con(this);
	}

	public void intercambiar_con(GemaNormal gemaNormal) {
		intercambiar_gema_y_hielo(gemaNormal, this);
	}

	public void intercambiar_con(GemaRayada gemaRayada) {
		intercambiar_gema_y_hielo(gemaRayada, this);
	}

	public void intercambiar_con(GemaEnvuelta gemaEnvuelta) {
		intercambiar_gema_y_hielo(gemaEnvuelta, this);
	}

	public void intercambiar_con(GemaCruzada gemaCruzada) {
		intercambiar_gema_y_hielo(gemaCruzada, this);
	}

	public void intercambiar_con(Hielo hielo) {
		intercambiar_hielo_y_hielo(this, hielo);
	}

	@Override
	public boolean puede_recibir(GemaNormal c) {
		return true;
	}

	@Override
	public boolean puede_recibir(GemaRayada p) {
		return true;
	}

	@Override
	public boolean puede_recibir(GemaEnvuelta p) {
		return true;
	}

	@Override
	public boolean puede_recibir(GemaCruzada p) {
		return true;
	}

	@Override
	public boolean puede_recibir(Hielo p) {
		return true;
	}

	@Override
	public boolean es_posible_intercambiar(Entidad e) {
		return e.puede_recibir(this);
	}
}
