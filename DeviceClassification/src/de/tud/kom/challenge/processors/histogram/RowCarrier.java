package de.tud.kom.challenge.processors.histogram;

/**
 * RowCarrier ist ein einfacher Conainer, um Speicherplatz während des 
 * Histogramm-Erstellens zu sparen.
 * RowCarrier speichert einen konkreten Wert ab und wird durch die 
 * Baumstruktur bis zu einem NodeCompartmentAdapter weitergereicht
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */
public class RowCarrier {

	private String[] _row;
	private int _power1;
	
	
	public void set(String[] row) {
		this._row = row;
		_power1 = Integer.parseInt(row[1]);
	}
	
	public int getPower1() {
		return this._power1;
	}
	
	public String[] getCarriedObject() {
		return _row;
	}
}
