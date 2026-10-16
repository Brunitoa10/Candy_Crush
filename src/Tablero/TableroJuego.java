package Tablero;

import Entidades.Entidad;
import Entidades.Hielo;
import Logica.Direccion;
import Timer.ObservableTimer;

public interface TableroJuego extends TableroNotificable{

	public int getFila();

	public int getColumna();

	public void asociar_entidades_logicas_y_graficas();

	public void resetar_tablero(int cant_filas, int cant_columnas);

	public void agregar_entidad(Entidad e);

	public void agregar_entidad_y_asociada(Hielo g);

	public void fijar_jugador(int fila_destino, int columna_destino);

	public void mover_jugador(Direccion direccion);

	public ObservableTimer obtenerObserver();

	/** Invocado por una bomba cuando su cuenta regresiva llega a cero. */
	public void notificarExplosionBomba();

	/** @return true si el intercambio produjo un match (y por lo tanto consume un movimiento). */
	public boolean intercambiar_entidades(Direccion direccion);
}
