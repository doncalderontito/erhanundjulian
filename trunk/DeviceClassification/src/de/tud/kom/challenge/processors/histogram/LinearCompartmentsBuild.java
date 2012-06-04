package de.tud.kom.challenge.processors.histogram;

/**
 * In LinearCompartmentsBuild werden die Compartments so aufgeteilt, dass die 
 * Unterteilung des Wertebereichs linear ist.
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

import java.util.ArrayList;
import java.util.List;

public class LinearCompartmentsBuild implements BuildCompartmentsStrategy{
	
	private BuildOptions _buildOptions;
	
	public LinearCompartmentsBuild(BuildOptions buildOptions) {
		_buildOptions = buildOptions;
	}

	public List<Compartment> buildCompartments(int maxValue){
		ArrayList<Compartment> list = new ArrayList<Compartment>(_buildOptions.getSteps());
		final int stepSize = maxValue / _buildOptions.getSteps();
		int min = 0, max = 0;
		
		for (int i = 1; i < _buildOptions.getSteps(); i++) {
			max = min + stepSize;
			Compartment c = new Compartment(min, max);
			list.add(c);
			min = max;
		}
		
		//Last Compartment contains compartment-size rounding errors
		Compartment c = new Compartment(min, maxValue);
		list.add(c);
		
		return list;
	}
	
}
