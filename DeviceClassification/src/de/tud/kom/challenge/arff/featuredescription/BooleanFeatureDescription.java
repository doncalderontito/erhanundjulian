package de.tud.kom.challenge.arff.featuredescription;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 */
public class BooleanFeatureDescription extends GenericFeatureDescription {
	
	public BooleanFeatureDescription(final String name) {
		super(name);
	}
	
	public String getValueRange() {
		return "{true, false}";
	}
	
}
