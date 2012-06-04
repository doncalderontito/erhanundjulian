package de.tud.kom.challenge.prediction.processors;

import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.challenge.prediction.DataContainer;
import de.tud.kom.challenge.prediction.DataEntry;
import de.tud.kom.challenge.prediction.PredictionFeature;

public class HistoricYesterdayPredictor implements PredictionProcessor {

	private final Logger log = Logger.getLogger(this.getClass().getSimpleName());
	private DataContainer data;
	private static final String type1 = "SameAsYesterday";
	
	public void setCompleteData(DataContainer data) {
		this.data = data;
	}

	public Vector<PredictionFeature> addValueToModel(DataEntry entry) {
		Vector<PredictionFeature> features = new Vector<PredictionFeature>();
		
		long offset = 60*60*24; // the offset is calculated in seconds
		Integer yesterday = data.getEntry(entry.getTime() - offset);
		
		if (yesterday == null) {
			features.add(new PredictionFeature(type1, "?"));
		} else {
			log.debug("Yesterday's value is "+yesterday+" and today's is "+entry.getValue());
			int y = yesterday.intValue();
			if (Math.abs(y-entry.getValue()) > 10) {
				features.add(new PredictionFeature(type1, "true"));
			} else {
				features.add(new PredictionFeature(type1, "false"));
			}
		}
		return features;
	}

	public String[] getResultTypes() {
		return new String[]{type1};
	}

	public String[] getResultRanges() {
		return new String[]{"{true, false}"};
	}
}
