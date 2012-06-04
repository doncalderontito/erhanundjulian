package de.tud.kom.challenge.processors;

import java.util.ArrayList;
import java.util.List;

import de.tud.kom.challenge.arff.featuredescription.FeatureDescription;
import de.tud.kom.challenge.arff.featuredescription.IntFeatureDescription;
import de.tud.kom.challenge.csvdatareader.CsvContainer;
import de.tud.kom.challenge.csvdatareader.LineData;
import de.tud.kom.challenge.processors.buckets.LineDataBucket;
import de.tud.kom.challenge.processors.buckets.OnOffBucketGenerator;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 * This feature covers the average power consumption when a device is on.
 * To calculate this value all Off-Buckets are thrown away.
 * An Off-Bucket is a period of time where the device was off.
 * A device is assumed as off if it’s power consumption is zero.
 * 
 * This feature fits better for device classification that a simple average power consumption over a whole day.
 * A simple average over a whole day would depend on the number of on cycles of the device.
 * 
 */
public class AvgPowerInWProcessor implements IFeaturePreProcessor {
	
	/**
	 * default
	 */
	private static final long serialVersionUID = 5224906521935825012L;

	public Iterable<FeatureDescription> getFeatureDescription() {
		final ArrayList<FeatureDescription> features = new ArrayList<FeatureDescription>();
		features.add(new IntFeatureDescription("avg_power_in_w"));
		return features;
	}
	

	public String[] processInput(final CsvContainer csv) {
		
		if(csv.getCompressedEntries().size() <= 2) {
			return null;
		}
		
		final ArrayList<LineData> lst = new ArrayList<LineData>(csv.getCompressedEntries());
		
		final OnOffBucketGenerator generator = new OnOffBucketGenerator();
		final List<LineDataBucket> buckets = generator.getBuckets(lst);
		
		int deviceOnCount = 0;
		
		final ArrayList<Double> runtimes = new ArrayList<Double>();
		
		final int avgPowerSum = 0;
		int maxPowerConsumption = 0;
		
		for(final LineDataBucket bucket : buckets) {
			if(!bucket.hasEnergyConsumption()) {
				continue;
			}
			
			final int runtimeInSec = bucket.getFrom().getDiffInSeconds(bucket.getTo());
			deviceOnCount++;
			runtimes.add(new Double(runtimeInSec));
			
			maxPowerConsumption = Math.max(bucket.getMaxPowerConsumption(), maxPowerConsumption);
		}
		
		if(deviceOnCount == 0) {
			return new String[] { "?" };
		}
		
		return new String[] { ((1.0 * avgPowerSum) / deviceOnCount) + "" };
	}
	
}
