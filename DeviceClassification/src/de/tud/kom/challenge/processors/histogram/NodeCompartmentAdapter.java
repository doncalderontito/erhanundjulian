package de.tud.kom.challenge.processors.histogram;

/**
 * Ein NodeCompartmentAdapter reicht jeweils einen Wert weiter an das
 * zugehörige Compartment-Objekt.
 * Ein NodeCompartmentAdapter-Objekt entspricht in der Baumstruktur den Blättern.
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */
public class NodeCompartmentAdapter extends Node {
	
	private Compartment _c;

	public NodeCompartmentAdapter(Compartment c) {
		super(null, null, 0, c.get_minPower(), c.get_maxPower()); //we have Compartment instead of nodes
		_c = c;
	}

	@Override
	public void traverse(RowCarrier row) {
		_c.addValue(row.getCarriedObject());
	}

}
