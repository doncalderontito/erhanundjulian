package de.tud.kom.challenge.arff.featuredescription;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 */
public abstract class GenericFeatureDescription implements FeatureDescription {
	
	private final String name;
	
	public GenericFeatureDescription(final String name) {
		this.name = name;
	}
	
	public abstract String getValueRange();
	
	public String getName() {
		return this.name;
	}
	
	public String toString() {
		return this.getName() + ": " + this.getValueRange();
	}
	
}
