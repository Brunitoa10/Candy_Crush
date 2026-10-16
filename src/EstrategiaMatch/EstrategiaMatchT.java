package EstrategiaMatch;

import java.util.LinkedList;

import Entidades.Entidad;

public class EstrategiaMatchT extends EstrategiaMatchEnvuelta {

	public EstrategiaMatchT() {
		super(3, "MATCH T VALIDO");
	}

	@Override
	protected boolean cumpleForma(LinkedList<Entidad> horizontal, LinkedList<Entidad> vertical) {
		Entidad primeraH = horizontal.getFirst(), ultimaH = horizontal.getLast();
		Entidad primeraV = vertical.getFirst(), ultimaV = vertical.getLast();
		boolean tDesdeHorizontal = primeraH.equals(ultimaV) && !primeraH.equals(primeraV) && horizontal.contains(primeraV);
		boolean tDesdeVertical = !primeraV.equals(ultimaH) && !primeraV.equals(primeraH) && vertical.contains(primeraH);
		return tDesdeHorizontal || tDesdeVertical;
	}
}
