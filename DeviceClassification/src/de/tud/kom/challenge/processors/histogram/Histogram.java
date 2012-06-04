package de.tud.kom.challenge.processors.histogram;

/**
 * In Histogram werden die Compartments, d.h. die Unterteilungen, gespeichert
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

import java.util.List;

public class Histogram {
	
	private List<Compartment> _compartments;
	private BuildOptions _buildOptions;
	
	
	public Compartment getCompartment(int i) {
		return _compartments.get(i);
	}
	
	public List<Compartment> getAllCompartments() {
		return _compartments;
	}
		
	
	public BuildOptions getBuildOptions() {
		return _buildOptions;
	}

	public void setBuildOptions(BuildOptions _buildOptions) {
		this._buildOptions = _buildOptions;
	}
	
	
	
	public Histogram(List<Compartment> compartments) {
		_compartments = compartments;
	}
}
