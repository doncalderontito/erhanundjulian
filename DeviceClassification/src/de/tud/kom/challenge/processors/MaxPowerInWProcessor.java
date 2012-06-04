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
 * This feature processor calculates the maximum power consumption of a 24 hour period.
 * Each device has a characteristically maximal power consumption.
 * But there are many different kinds of devices in a class.
 * Each type has it’s characteristically power consumption.
 * So this feature might not be that helpful.
 * 
 */
public class MaxPowerInWProcessor implements IFeaturePreProcessor {
	
	/**
	 * default
	 */
	private static final long serialVersionUID = 3987218512973543097L;

	public Iterable<FeatureDescription> getFeatureDescription() {
		final ArrayList<FeatureDescription> features = new ArrayList<FeatureDescription>();
		features.add(new IntFeatureDescription("max_power_in_w"));
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
		
		return new String[] { maxPowerConsumption + "" };
	}
	
}
