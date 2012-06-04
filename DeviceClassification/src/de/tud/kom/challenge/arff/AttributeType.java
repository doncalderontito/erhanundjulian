package de.tud.kom.challenge.arff;

public class AttributeType {
	
	private final String valueRange;
	private final String name;
	
	public AttributeType(String name, String valueRange) {
		this.name = name;
		this.valueRange = valueRange;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValuerange() {
		return valueRange;
	}
	
}
