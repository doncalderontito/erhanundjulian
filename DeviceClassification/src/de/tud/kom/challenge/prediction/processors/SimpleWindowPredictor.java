package de.tud.kom.challenge.prediction.processors;

import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.challenge.prediction.DataContainer;
import de.tud.kom.challenge.prediction.DataEntry;
import de.tud.kom.challenge.prediction.PredictionFeature;

public class SimpleWindowPredictor implements PredictionProcessor {

	private final Logger log = Logger.getLogger(this.getClass().getSimpleName());
	private static final String type1 = "DiffersFromDailyAverage";
	private DataContainer data;
	
	public void setCompleteData(DataContainer data) {
		this.data = data;
	}
	
	
	public Vector<PredictionFeature> addValueToModel(DataEntry entry) {
		DataContainer day = data.getLastDay();
		int avg = 0, num = 0;
		Iterator<Integer> it = day.getLastDay().getValueIterator();
		while (it.hasNext()) {
			Integer d = it.next();
			avg += d.intValue();
			num++;
		}
		
		
		
		double average = (double)((double)avg/(double)num);

		
		log.debug("Average consumption over day is "+average);
	
		double diff=Math.abs(average-entry.getValue());
		diff = Math.round( diff  );
		
		Vector<PredictionFeature> features = new Vector<PredictionFeature>();
		features.add(new PredictionFeature(type1, ""+diff));
		return features;
	}
	
	public String[] getResultTypes() {
		return new String[]{type1};
	}

	public String[] getResultRanges() {
		return new String[]{"numeric"};
	}
}
