package de.tud.kom.challenge.prediction.processors;

import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.challenge.prediction.DataContainer;
import de.tud.kom.challenge.prediction.DataEntry;
import de.tud.kom.challenge.prediction.PredictionFeature;

public class SimplePipeThroughProcessor implements PredictionProcessor {

	public void setCompleteData(DataContainer data) {
		// nil
	}
	
	public Vector<PredictionFeature> addValueToModel(DataEntry entry) {
		Vector<PredictionFeature> features = new Vector<PredictionFeature>();
		features.add(new PredictionFeature("CurrentPowerConsumption", ""+Math.abs(entry.getValue())));
		return features;
	}
	
	public String[] getResultTypes() {
		return new String[]{"CurrentPowerConsumption"};
	}

	public String[] getResultRanges() {
		return new String[]{"numeric"};
	}
}
