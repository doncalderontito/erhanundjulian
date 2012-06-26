package de.tud.kom.challenge.prediction.processors;

import java.util.Vector;

import de.tud.kom.challenge.prediction.DataContainer;
import de.tud.kom.challenge.prediction.DataEntry;
import de.tud.kom.challenge.prediction.PredictionFeature;

public class DaySegmentProcessor implements PredictionProcessor {
	
	@Override
	public void setCompleteData(DataContainer data) {
		// TODO Auto-generated method stub

	}

	@Override
	public Vector<PredictionFeature> addValueToModel(DataEntry entry) {
		Vector<PredictionFeature> features = new Vector<PredictionFeature>();
		String segment;
		if(entry.getTime() % 86400 < 21600)
			segment = "1";
		else if(entry.getTime() % 86400 < 21600 * 2)
			segment = "2";
		else if(entry.getTime() % 86400 < 21600 * 3)
			segment = "3";
		else
			segment = "4";
		features.add(new PredictionFeature("DaySegment", segment));
		return features;
	}

	@Override
	public String[] getResultTypes() {
		return new String[]{"DaySegment"};
	}

	@Override
	public String[] getResultRanges() {
		return new String[]{"numeric"};
	}

}

