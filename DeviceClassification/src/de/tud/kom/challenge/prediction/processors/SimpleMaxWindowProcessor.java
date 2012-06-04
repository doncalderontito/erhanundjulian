package de.tud.kom.challenge.prediction.processors;

import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.challenge.prediction.DataContainer;
import de.tud.kom.challenge.prediction.DataEntry;
import de.tud.kom.challenge.prediction.PredictionFeature;

public class SimpleMaxWindowProcessor implements PredictionProcessor {

	private final Logger log = Logger.getLogger(this.getClass().getSimpleName());
	private static final int window_size = 10;
	private Vector<Integer> window = new Vector<Integer>();
	private static final String type1 = "MaximumPowerOverLast"+window_size+"Seconds";
	
	
	public void setCompleteData(DataContainer data) { 
		// Processor keeps no state information
	}
	
	public Vector<PredictionFeature> addValueToModel(DataEntry input) {
		window.add(input.getValue());
		while (window.size() > window_size) window.remove(0);
		
		int max = Integer.MIN_VALUE;
		for (Integer value:window) {
			if (value > max) max = value;
		}
		
		log.debug("Maximum is "+max);
		Vector<PredictionFeature> features = new Vector<PredictionFeature>();
		features.add(new PredictionFeature(type1, ""+max));		
		return features;
	}
	
	public String[] getResultTypes() {
		return new String[]{type1};
	}

	public String[] getResultRanges() {
		return new String[]{"numeric"};
	}
}
