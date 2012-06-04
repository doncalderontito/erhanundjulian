package de.tud.kom.challenge.processors.histogram;

/**
 * In Compartments werden die Werte einer Histogramm-Unterteilung abgespeichert.
 * Zum Beispiel alle Stromverbrauchswerte von 300 bis 500 Watt. 
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

import java.util.ArrayList;
import java.util.List;

public class Compartment {
	
	private List<String[]> _values = new ArrayList<String[]>();
	private int _minPower;
	private int _maxPower;

	public String[] getSingleValue(int i) {
		return _values.get(i);
	}
	
	public int getCount() {
		return _values.size();
	}
	
	public void addValue(String[] value) {
		_values.add(value);
	}
	
	public Compartment(int min, int max) {
		this._maxPower = max;
		this._minPower = min;
	}
	
	public int get_minPower() {
		return _minPower;
	}

	public int get_maxPower() {
		return _maxPower;
	}
}