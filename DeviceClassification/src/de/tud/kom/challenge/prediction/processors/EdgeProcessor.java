package de.tud.kom.challenge.prediction.processors;

import java.util.Vector;

import de.tud.kom.challenge.prediction.DataContainer;
import de.tud.kom.challenge.prediction.DataEntry;
import de.tud.kom.challenge.prediction.PredictionFeature;

public class EdgeProcessor implements PredictionProcessor {

	private int oldValue = -1;
	public static final float ERROR_MARGIN_PERCENT = 0.2f;
	public static final float ERROR_MARGIN_ABSOLUTE = 10.1f;
	
	private static int print = 0;
	private static long count = 0;
	
	@Override
	public void setCompleteData(DataContainer data) {
		// TODO Auto-generated method stub

	}
	
	public static boolean isEdge(int oldVal, int newVal) {
		if(oldVal == -1)
			return true;
		
		if(oldVal == 0 && newVal != 0)
			return true;
		
		if((newVal > oldVal * (1 + ERROR_MARGIN_PERCENT) || newVal < oldVal * (1 - ERROR_MARGIN_PERCENT)) && 
				(Math.abs(newVal - oldVal) > ERROR_MARGIN_ABSOLUTE)) {
			return true;
		}
		return false;
	}

	@Override
	public Vector<PredictionFeature> addValueToModel(DataEntry entry) {
		Vector<PredictionFeature> features = new Vector<PredictionFeature>();
		if(oldValue == -1) {
			features.add(new PredictionFeature("EdgeDetection", "false"));
		}
		else {
			if(isEdge(oldValue, entry.getValue())) {
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
