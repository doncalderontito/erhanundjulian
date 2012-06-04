package de.tud.kom.challenge.processors.histogram;

/**
 * BuildCompartmentsStrategy dient als Abstraktionsschicht über den verschiedenen
 * Implementierungsalgorithmen zur Unterteilung der Eingabedaten in Compartments.
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

import java.util.List;

public interface BuildCompartmentsStrategy {
	
	public List<Compartment> buildCompartments(int maxValue);
	
}
