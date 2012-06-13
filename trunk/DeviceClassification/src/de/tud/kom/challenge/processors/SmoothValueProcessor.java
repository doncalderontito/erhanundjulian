package de.tud.kom.challenge.processors;

import java.util.Vector;

import de.tud.kom.challenge.prediction.DataContainer;
import de.tud.kom.challenge.prediction.DataEntry;
import de.tud.kom.challenge.prediction.PredictionFeature;
import de.tud.kom.challenge.prediction.processors.PredictionProcessor;

public class SmoothValueProcessor implements PredictionProcessor {

	int oldValue = -1;
	static final float loading = 0.5f;
	
	@Override
	public void setCompleteData(DataContainer data) {
		// TODO Auto-generated method stub

	}

	@Override
	public Vector<PredictionFeature> addValueToModel(DataEntry entry) {
		Vector<PredictionFeature> features = new Vector<PredictionFeature>();
		if(oldValue == -1) {
			features.add(new PredictionFeature("SmoothPowerConsumption", "" + entry.getValue()));
		}
		else {
			features.add(new PredictionFeature("SmoothPowerConsumption", "" + Math.round((entry.getValue() * loading + oldValue * (1 - loading)))));
		}
		oldValue = entry.getValue();
		return features;
	}

	@Override
	public String[] getResultTypes() {
		return new String[]{"SmoothPowerConsumption"};
	}

	@Override
	public String[] getResultRanges() {
		return new String[]{"numeric"};
	}

}
