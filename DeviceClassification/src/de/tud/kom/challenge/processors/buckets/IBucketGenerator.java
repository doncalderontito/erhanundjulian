package de.tud.kom.challenge.processors.buckets;

import java.util.List;

import de.tud.kom.challenge.csvdatareader.LineData;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 */
public interface IBucketGenerator {
	
	public abstract List<? extends LineDataBucket> getBuckets(final List<LineData> lineData);
	
}
