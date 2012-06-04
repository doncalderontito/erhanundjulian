package de.tud.kom.challenge.prediction.processors;

import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.challenge.csvdatareader.DateTime;
import de.tud.kom.challenge.prediction.DataContainer;
import de.tud.kom.challenge.prediction.DataEntry;
import de.tud.kom.challenge.prediction.PredictionFeature;

public class AbsolutePowerJumpProcessor implements PredictionProcessor {

	private final Logger log = Logger.getLogger(this.getClass().getSimpleName());
	private DataContainer data;
	private int[] jumps = new int[7200];
	private static final String type1 = "KnownPowerJump";
	
	public void setCompleteData(DataContainer data) {
		this.data = data;
		if (data.getSize() == 0) return;
		
		log.info("Restoring state information for "+this.getClass().getSimpleName());
		log.info("Extracting default state from "+data.getSize()+" instances...");
		
		Iterator<Integer> i = data.getValueIterator();
		int lastValue = Integer.MAX_VALUE;
		while (i.hasNext()) {
			Integer ix = i.next();
			if (ix != null) {
				if (lastValue < Integer.MAX_VALUE) {
					int diff = ix.intValue() - lastValue;
					diff = Math.min(diff,3599);
					diff = Math.max(diff,-3600);
					diff += 3600;
					jumps[diff]++;
				}
				lastValue = ix.intValue();
			} else {
				lastValue = Integer.MAX_VALUE;
			}
		}
	}

	public Vector<PredictionFeature> addValueToModel(DataEntry input) {
		Vector<PredictionFeature> features = new Vector<PredictionFeature>();

		if (input.getValue() >= 0) {
			if (data.getEntry(data.getLastTime()) == null) {
				log.info("Too long break in data trace at time "+DateTime.fromLong(data.getLastTime()).toTimeString());
				features.add(new PredictionFeature(type1, "?"));
			} else {
				int diff = input.getValue()-data.getEntry(data.getLastTime());
				
				// Shift it to the array indices
				diff = Math.min(diff,3599);
				diff = Math.max(diff,-3600);
				diff += 3600;
				
				if (jumps[diff] == 0) {
					//log.info("Found new jump"+(diff-3600));
					features.add(new PredictionFeature(type1, "true"));
				} else {
					features.add(new PredictionFeature(type1, "false"));		
				}
				
				// We keep some state in this processor
				jumps[diff]++;
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
