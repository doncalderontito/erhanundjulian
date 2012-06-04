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
 * 
 * This feature processor counts how often the power consumption arises from zero and goes down to zero again during a day.
 * This feature does not provide that much information itself but other feature requires this value.
 * It is required by the AvgOnTimeProcessor the StddevOnTimeProcessor and the AvgOnPowerInWProcessor.
 * 
 */
public class DeviceOnCountProcessor implements IFeaturePreProcessor {
	
	private static final long serialVersionUID = 8374969903122363681L;

	public Iterable<FeatureDescription> getFeatureDescription() {
		ArrayList<FeatureDescription> features = new ArrayList<FeatureDescription>();
		features.add(new IntFeatureDescription("device_on_count"));
		return features;
	}
	

	public String[] processInput(CsvContainer csv) {
		
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
			return new String[] { "0" };
		}
		
		return new String[] { deviceOnCount + "" };
	}
	
}
