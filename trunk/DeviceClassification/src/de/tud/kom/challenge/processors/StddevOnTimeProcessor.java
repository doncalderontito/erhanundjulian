package de.tud.kom.challenge.processors;

import java.util.ArrayList;
import java.util.List;

import de.tud.kom.challenge.arff.featuredescription.FeatureDescription;
import de.tud.kom.challenge.arff.featuredescription.IntFeatureDescription;
import de.tud.kom.challenge.csvdatareader.CsvContainer;
import de.tud.kom.challenge.csvdatareader.LineData;
import de.tud.kom.challenge.processors.buckets.LineDataBucket;
import de.tud.kom.challenge.processors.buckets.OnOffBucketGenerator;
import de.tud.kom.challenge.util.StatsUtil;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 * This feature processor completes the AvgOnTimeProcessor.
 * It’s value is the standard deviation of the device on time.
 * 
 * If the standard deviation is high the AvgOnTime of the device is worthless because it differs to much.
 * If the standard deviation is low, the AvgOnTime is a good feature to determine the device class.
 * 
 */
public class StddevOnTimeProcessor implements IFeaturePreProcessor {
	
	/**
	 * default
	 */
	private static final long serialVersionUID = -5730551274527411138L;
	
	public Iterable<FeatureDescription> getFeatureDescription() {
		final ArrayList<FeatureDescription> features = new ArrayList<FeatureDescription>();
		features.add(new IntFeatureDescription("stddev_on_time"));
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
		
		final double stdDev = StatsUtil.calculateStdDev(runtimes);
		
		return new String[] { stdDev + "" };
	}
	
}
