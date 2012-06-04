package de.tud.kom.challenge.processors.histogram;

/**
 * BuildCompartmentsStrategy dient als Abstraktionsschicht �ber den verschiedenen
 * Implementierungsalgorithmen zur Unterteilung der Eingabedaten in Compartments.
 * 
 * @author Felix R�ttiger
 * @author Vanessa W�hrl
 */

import java.util.List;

public interface BuildCompartmentsStrategy {
	
	public List<Compartment> buildCompartments(int maxValue);
	
}
