package EstrategiaMatch;

import java.util.LinkedList;

import Entidades.Entidad;

public class EstrategiaMatchL extends EstrategiaMatchEnvuelta {

	public EstrategiaMatchL() {
		super(4, "MATCH L VALIDO");
	}

	@Override
	protected boolean cumpleForma(LinkedList<Entidad> horizontal, LinkedList<Entidad> vertical) {
		Entidad primeraH = horizontal.getFirst(), ultimaH = horizontal.getLast();
		Entidad primeraV = vertical.getFirst(), ultimaV = vertical.getLast();
		return primeraH.equals(ultimaV) || primeraH.equals(primeraV) || ultimaH.equals(ultimaV) || ultimaH.equals(primeraV);
	}
}
