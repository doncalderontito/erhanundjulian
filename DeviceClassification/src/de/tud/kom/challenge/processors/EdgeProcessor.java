package de.tud.kom.challenge.processors;

import java.util.Vector;

import de.tud.kom.challenge.prediction.DataContainer;
import de.tud.kom.challenge.prediction.DataEntry;
import de.tud.kom.challenge.prediction.PredictionFeature;
import de.tud.kom.challenge.prediction.processors.PredictionProcessor;

public class EdgeProcessor implements PredictionProcessor {

	int oldValue = -1;
	static final float ERROR_MARGIN_PERCENT = 0.05f;
	static final float ERROR_MARGIN_ABSOLUTE = 1.0f;
	
	@Override
	public void setCompleteData(DataContainer data) {
		// TODO Auto-generated method stub

	}

	@Override
	public Vector<PredictionFeature> addValueToModel(DataEntry entry) {
		Vector<PredictionFeature> features = new Vector<PredictionFeature>();
		if(oldValue == -1) {
			features.add(new PredictionFeature("EdgeDetection", "?"));
		}
		else {
			if((entry.getValue() > oldValue * (1 + ERROR_MARGIN_PERCENT) || entry.getValue() < oldValue * (1 - ERROR_MARGIN_PERCENT)) && 
					(Math.abs(entry.getValue() - oldValue) > ERROR_MARGIN_ABSOLUTE)) {
				features.add(new PredictionFeature("EdgeDetection", "true"));
			}
			else {
				features.add(new PredictionFeature("EdgeDetection", "false"));
			}
		}
		oldValue = entry.getValue();
		return features;
	}

	@Override
	public String[] getResultTypes() {
		return new String[]{"EdgeDetection"};
	}

	@Override
	public String[] getResultRanges() {
		return new String[]{"{true, false}"};
	}

}
