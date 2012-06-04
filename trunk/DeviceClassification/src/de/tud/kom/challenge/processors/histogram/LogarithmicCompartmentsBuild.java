package de.tud.kom.challenge.processors.histogram;

/**
 * In LogarithmicCompartmentsBuild wird der Wertebereich zur Unterteilung in 
 * Compartments logarithmisch aufgeteilt.
 * Dabei ist das Compartment mit dem niedrigsten Wertebereich am kleinsten
 * und das Compartment mit dem größten Wertebereich am größten.
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

import java.util.ArrayList;
import java.util.List;

public class LogarithmicCompartmentsBuild implements BuildCompartmentsStrategy{

private BuildOptions _buildOptions;
	
	public LogarithmicCompartmentsBuild(BuildOptions buildOptions) {
		_buildOptions = buildOptions;
	}

	public List<Compartment> buildCompartments(int maxValue){
		ArrayList<Compartment> list = new ArrayList<Compartment>(_buildOptions.getSteps());
		
		double min = 0;
		double max = 0;
		double stepSize = Math.log(maxValue) / _buildOptions.getSteps();
		
		for (int i = 1; i <= _buildOptions.getSteps(); i++) {
			//max = max + calculateCompartmentIntervall(_buildOptions.getSteps() - i, maxValue);
			max = Math.exp(stepSize * i);
			
			Compartment c = new Compartment ((int) min, (int) max);
			list.add(c);
			
			min = max;
		}
		
		return list;
	}
	
	
}
