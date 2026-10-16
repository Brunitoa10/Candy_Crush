package EstrategiaMatch;

import java.util.LinkedList;

import Entidades.Entidad;

public class EstrategiaMatchS extends EstrategiaMatchEnvuelta {

	public EstrategiaMatchS() {
		super(2, "MATCH SIGMOIDE VALIDO");
	}

	@Override
	protected boolean cumpleForma(LinkedList<Entidad> horizontal, LinkedList<Entidad> vertical) {
		Entidad primeraH = horizontal.getFirst(), ultimaH = horizontal.getLast();
		Entidad primeraV = vertical.getFirst(), ultimaV = vertical.getLast();
		return !primeraH.equals(ultimaV) && !primeraH.equals(primeraV) && !primeraV.equals(ultimaH);
	}
}
