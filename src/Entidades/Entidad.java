package Entidades;

import EstrategiaDetonaciones.EstategiaDetonacion;
import GUI.EntidadGrafica;
import Logica.*;
import Tablero.Tablero;
import Tablero.TableroNotificable;

public abstract class Entidad implements EntidadLogica, Enfocable, Intercambiable, Matchable, Detonable, Ocultable
{
	// Índices del arreglo de imágenes representativas
	protected static final int IMG_NORMAL = 0;
	protected static final int IMG_ENFOCADA = 1;
	protected static final int IMG_DETONADA = 2;
	protected static final int IMG_ENFOCADA_DETONADA = 3;
	protected static final int IMG_INTERNA = 4;
	protected static final int CANTIDAD_IMAGENES = 5;

	//Atributos de instancia

	protected int fila,columna;
	protected boolean enfocada,detonada,visible;
	protected EntidadGrafica entidadGrafica;
	protected Color color;
	protected String rutadeLaImagen;
	protected String[] imagenes;
	protected EstategiaDetonacion estrategiadeDetonacion;
	protected TableroNotificable tablero;
	protected String skin;

	//Constructor

	public Entidad(TableroNotificable tablero ,int fila, int columna, String rutadeLaImagen, Color color, boolean visible)  {
		this.tablero = tablero;
		this.fila=fila;
		this.columna=columna;
		this.enfocada=false;
		this.detonada = false;
		this.visible = visible;
		this.color=color;
		this.rutadeLaImagen = rutadeLaImagen;
	}

	//Setters

	public void set_EstrategiaDetonacion(EstategiaDetonacion estrategiaDetonacion) {
		this.estrategiadeDetonacion = estrategiaDetonacion;
	}

	public void set_EntidadGrafica_entidadInterna(EntidadGrafica entidadGrafica)
	{
		this.entidadGrafica=entidadGrafica;
	}

	public void set_EntidadGrafica(EntidadGrafica entidadGrafica){
		this.entidadGrafica=entidadGrafica;
	}

	public void set_FilayColumna(int fila, int columna) {
		this.fila = fila;
		this.columna = columna;
	}

	public void set_color(int color) {
		this.color.set_color(color);
	}

	//Getters

	public EntidadGrafica get_EntidadGrafica() {
		return entidadGrafica;
	}

	public String[] get_imagenes_representativas() {
		int indice = enfocada ? IMG_ENFOCADA : IMG_NORMAL;
		return new String[] { imagenes[indice], imagenes[IMG_INTERNA] };
	}

	public int get_color() {
		return color.get_color();
	}

	public int get_fila(){
		return fila;
	}

	public int get_columna(){
		return columna;
	}

	public String get_Skin()
	{
		return skin;
	}

	public String get_rutadeLaImagen() {
		return rutadeLaImagen;
	}

	public boolean get_visibilidad() {
		return visible;
	}

	public int get_score()
	{
		return color.get_score();
	}

	public TableroNotificable get_TableroNotificable()
	{
		return tablero;
	}

	public abstract Entidad get_gema_interna();

	//Metodos particulares

	public boolean estaDetonada() {
		return detonada;
	}

	public boolean estaEnfocada() {
		return enfocada;
	}

	/**
	 * Comportamiento por defecto (patrón Strategy): delega en la estrategia de detonación
	 * configurada por cada subclase. Las subclases solo redefinen si necesitan algo extra.
	 */
	public void detonar(Tablero tablero) {
		estrategiadeDetonacion.detonar(this, tablero);
	}

	/**
	 * Deja la entidad vacía (transparente) y notifica a su representación gráfica.
	 * Centraliza el código que antes se repetía en todas las estrategias de detonación.
	 */
	public void aplicarDetonacionVisual() {
		set_color(Color.TRANSPARENTE);
		cargarImagenesRepresentativas(rutadeLaImagen);
		notificar(EntidadGrafica::notificarse_detonacion);
	}

	public void cargarImagenesRepresentativas(String ri) {
		imagenes = new String [CANTIDAD_IMAGENES];
		imagenes[IMG_NORMAL] = ri + color.get_color() +".png";
		imagenes[IMG_ENFOCADA] = ri + color.get_color() +"-cursor.png";
		imagenes[IMG_DETONADA] = ri + "detonado.gif";
		imagenes[IMG_ENFOCADA_DETONADA] = ri + "enfocado-detonado.gif";
		imagenes[IMG_INTERNA] = null;
	}

	public abstract boolean esAfectadaPorExplosionAdyacente();

	public boolean tieneGemaInterna()
	{
		return false;
	}

	//Metodos visuales

	public void cambiar_posicion(int nueva_fila, int nueva_columna) {
		set_FilayColumna(nueva_fila, nueva_columna);
		notificar(EntidadGrafica::notificarse_intercambio);
	}

	public void intercambiar_Posicion(int nuevaFila, int nuevaColumna) {
		cambiar_posicion(nuevaFila, nuevaColumna);
	}

	public void intercambiar_Caida(int nuevaFila, int nuevaColumna) {
		set_FilayColumna(nuevaFila, nuevaColumna);
		notificar(EntidadGrafica::notificarse_caida);
	}

	public void enfocar() {
		enfocada = true;
		notificar(EntidadGrafica::notificarse_cambio_foco);
	}

	public void desenfocar() {
		enfocada = false;
		notificar(EntidadGrafica::notificarse_cambio_foco);
	}

	public void mostrar() {
		visible = true;
		notificar(EntidadGrafica::notificarse_cambio_visibilidad);
	}

	public void ocultar() {
		visible = false;
		notificar(EntidadGrafica::notificarse_cambio_visibilidad);
	}

	/**
	 * Notifica a la representación gráfica si existe. Algunas entidades auxiliares
	 * (las celdas vacías que deja la gravedad) no tienen celda asociada y antes lanzaban NPE.
	 */
	protected void notificar(java.util.function.Consumer<EntidadGrafica> notificacion) {
		if (entidadGrafica != null) {
			notificacion.accept(entidadGrafica);
		}
	}

	//Metodos protegidos

	protected void intercambiar_entidad_y_entidad(Entidad origen, Entidad destino) {
		int nueva_fila_origen = destino.get_fila();
		int nueva_columna_origen = destino.get_columna();
		destino.cambiar_posicion(origen.get_fila(), origen.get_columna());
		origen.cambiar_posicion(nueva_fila_origen, nueva_columna_origen);
		tablero.reubicar(origen);
		tablero.reubicar(destino);
	}

	protected void intercambiar_gema_y_hielo(Entidad gema, Hielo hielo) {
		Entidad gema_interna_hielo = hielo.get_gema_interna();
		int nueva_fila_gema = hielo.get_fila();
		int nueva_columna_gema = hielo.get_columna();

		gema_interna_hielo.cambiar_posicion(gema.get_fila(), gema.get_columna());
		gema.cambiar_posicion(nueva_fila_gema, nueva_columna_gema);
		hielo.set_gema_interna(gema);
		tablero.reubicar(gema_interna_hielo);
	}

	protected void intercambiar_hielo_y_hielo(Hielo hielo1, Hielo hielo2)
	{
		Entidad gema_interna_hielo1 = hielo1.get_gema_interna();
		Entidad gema_interna_hielo2 = hielo2.get_gema_interna();

		gema_interna_hielo1.cambiar_posicion(hielo2.get_fila(), hielo2.get_columna());
		gema_interna_hielo2.cambiar_posicion(hielo1.get_fila(), hielo1.get_columna());
		hielo1.set_gema_interna(gema_interna_hielo2);
		hielo2.set_gema_interna(gema_interna_hielo1);
	}
}
