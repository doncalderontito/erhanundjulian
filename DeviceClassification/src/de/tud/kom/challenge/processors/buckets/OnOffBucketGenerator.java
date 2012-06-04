package de.tud.kom.challenge.processors.buckets;

import java.util.ArrayList;
import java.util.List;

import de.tud.kom.challenge.csvdatareader.LineData;
import de.tud.kom.challenge.util.CollectionUtil;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 */
public class OnOffBucketGenerator implements IBucketGenerator {
	
	public List<LineDataBucket> getBuckets(final List<LineData> lineData) {
		
		final ArrayList<LineDataBucket> result = new ArrayList<LineDataBucket>();
		
		boolean isActive = CollectionUtil.first(lineData).getSmoothedConsumptionInWatt() != 0;
		ArrayList<LineData> collectedData = new ArrayList<LineData>();
		
		for(final LineData data : lineData) {
			if(this.hasSwitchedOn(isActive, data)) {
				result.add(this.createBucket(collectedData));
				collectedData = new ArrayList<LineData>();
				isActive = true;
			} else if(this.hasSwitchedOff(isActive, data)) {
				result.add(this.createBucket(collectedData));
				collectedData = new ArrayList<LineData>();
				isActive = false;
			}
			collectedData.add(data);
		}
		result.add(this.createBucket(collectedData));
		
		return result;
	}
	
	private boolean hasSwitchedOff(final boolean isActive, final LineData data) {
		return (data.getSmoothedConsumptionInWatt() == 0) && isActive;
	}
	
	private boolean hasSwitchedOn(final boolean isActive, final LineData data) {
		return (data.getSmoothedConsumptionInWatt() != 0) && !isActive;
	}
	
	private LineDataBucket createBucket(final ArrayList<LineData> collectedData) {
		return new LineDataBucket(CollectionUtil.first(collectedData).getDateTime(), CollectionUtil.last(collectedData).getDateTime().addSeconds(CollectionUtil.last(collectedData).getDuration()), collectedData);
	}
	
}
