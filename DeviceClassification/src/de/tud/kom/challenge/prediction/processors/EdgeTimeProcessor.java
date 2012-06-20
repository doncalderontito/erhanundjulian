package de.tud.kom.challenge.prediction.processors;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import de.tud.kom.challenge.prediction.DataContainer;
import de.tud.kom.challenge.prediction.DataEntry;
import de.tud.kom.challenge.prediction.PredictionFeature;

public class EdgeTimeProcessor implements PredictionProcessor {

	private long oldDay = -1;
	private int oldValue = -1;
	private int edgeCounter = 0;
	
	@Override
	public void setCompleteData(DataContainer data) {
		// TODO Auto-generated method stub

	}

	@Override
	public Vector<PredictionFeature> addValueToModel(DataEntry entry) {
		//Calendar calendar = Calendar.getInstance();
		Vector<PredictionFeature> features = new Vector<PredictionFeature>();
		//calendar.setTimeInMillis(entry.getTime() * 1000);
		if(oldDay != (entry.getTime() / 86400) && oldDay != -1) {
			features.add(new PredictionFeature("EdgePerTimeDetection", "" + edgeCounter));
			edgeCounter = 0;
		}
		else {
			features.add(new PredictionFeature("EdgePerTimeDetection", "?"));
			if(EdgeProcessor.isEdge(oldValue, entry.getValue()) && oldValue != -1) {
				edgeCounter++;
			}
		}
		oldValue = entry.getValue();
		oldDay = (entry.getTime() / 86400);
		return features;
	}

	@Override
	public String[] getResultTypes() {
		return new String[]{"EdgePerTimeDetection"};
	}

	@Override
	public String[] getResultRanges() {
		return new String[]{"numeric"};
	}

}
