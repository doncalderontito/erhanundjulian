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
 * This feature processor determines how long a device was running over the day in average.
 * The calculation of this feature is done via summing up all on durations over a day.
 * After all the result is divided by the number of on cycles of the device.
 * 
 * The intension of this feature is that some devices have nearly the same run time when they are used.
 * E.g a kettle might always need the same amount of time to heat up the same amount of water.
 * This feature fits best for automatically running devices.
 * It might not work for devices that are used on demand.
 * A television is unlikely to have always the same run time.
 * 
 */
public class AvgOnTimeProcessor implements IFeaturePreProcessor {
	
	
	/**
	 * default
	 */
	private static final long serialVersionUID = 1356661710729954687L;
	
	public Iterable<FeatureDescription> getFeatureDescription() {
		final ArrayList<FeatureDescription> features = new ArrayList<FeatureDescription>();
		features.add(new IntFeatureDescription("avg_on_time"));
		return features;
	}
	

	public String[] processInput(final CsvContainer csv) {
		
		if(csv.getCompressedEntries().size() <= 2) {
			return null;
		}
		
		final ArrayList<LineData> lst = new ArrayList<LineData>(csv.getCompressedEntries());
		
		final OnOffBucketGenerator generator = new OnOffBucketGenerator();
		final List<LineDataBucket> buckets = generator.getBuckets(lst);
		
		int deviceOnSum = 0;
		int deviceOnCount = 0;
		
		final ArrayList<Double> runtimes = new ArrayList<Double>();
		
		int maxPowerConsumption = 0;
		
		for(final LineDataBucket bucket : buckets) {
			if(!bucket.hasEnergyConsumption()) {
				continue;
			}
			
			final int runtimeInSec = bucket.getFrom().getDiffInSeconds(bucket.getTo());
			deviceOnSum += runtimeInSec;
			deviceOnCount++;
			runtimes.add(new Double(runtimeInSec));
			
			maxPowerConsumption = Math.max(bucket.getMaxPowerConsumption(), maxPowerConsumption);
		}
		
		if(deviceOnCount == 0) {
			return new String[] { "0" };
		}
		
		return new String[] { (deviceOnSum / deviceOnCount) + "" };
	}
	
}
