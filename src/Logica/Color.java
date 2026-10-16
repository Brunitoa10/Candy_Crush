package Logica;

public class Color {
	//en caso de cambiar los valores, avisenle a Nacho para cambiar los nombres de los assets :)
	public static final int TRANSPARENTE = 0;
	public static final int ROJO = 1;
	public static final int NARANJA = 2;
	public static final int AMARILLO = 3;
	public static final int VERDE = 4;
	public static final int AZUL = 5;
	public static final int PURPURA = 6;
	public static final int ROCA = 7;
	public static final int HIELO = 8;
	public static final int BOMBA = 9;

	/** Cantidad de colores de gema "jugables" (1..CANTIDAD_COLORES_GEMA). */
	public static final int CANTIDAD_COLORES_GEMA = 6;

	protected int color;

	public Color(int color) {
		this.color = color;
	}

	public int get_color() {
		return color;
	}

	public void set_color(int color) {
		this.color = color;
	}

	public int get_score() {
		return scorePara(color);
	}

	public static int scorePara(int color) {
		switch (color) {
			case ROJO:     return 5;
			case VERDE:    return 10;
			case NARANJA:  return 15;
			case AMARILLO:
			case AZUL:     return 20;
			case PURPURA:
			case ROCA:     return 25;
			case BOMBA:    return 150;
			default:       return 0;
		}
	}
}
