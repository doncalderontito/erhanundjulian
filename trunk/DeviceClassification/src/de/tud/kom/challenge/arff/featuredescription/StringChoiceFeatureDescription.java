package de.tud.kom.challenge.arff.featuredescription;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 */
public class StringChoiceFeatureDescription extends GenericFeatureDescription {
	
	private final String[] choices;
	
	public StringChoiceFeatureDescription(final String name, final String... choices) {
		super(name);
		this.choices = choices;
	}
	
	public String getValueRange() {
		
		String result = "";
		for(final String item : this.choices) {
			result += item + ", ";
		}
		
		return "{" + result.substring(0, result.length() - 2) + "}";
	}
	
}
