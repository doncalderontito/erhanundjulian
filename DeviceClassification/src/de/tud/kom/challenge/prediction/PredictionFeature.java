package de.tud.kom.challenge.prediction;

public class PredictionFeature {

	String name, result;
	
	public PredictionFeature(String name, String result) {
		this.name = name;
		this.result = result;
	}

	public String getResult() {
		return result;
	}
	
	public String getName() {
		return name;
	}
}
