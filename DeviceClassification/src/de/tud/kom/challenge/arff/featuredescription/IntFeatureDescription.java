package de.tud.kom.challenge.arff.featuredescription;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 */
public class IntFeatureDescription extends GenericFeatureDescription {
	
	public IntFeatureDescription(final String name) {
		super(name);
	}
	
	public String getValueRange() {
		return "numeric";
	}
}
