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
 * This processor is a combination of following processors:
 * 
 * DeviceOnCount
 * AvgOnTime
 * StddevOnTime
 * AvgOnPowerInW
 * MaxPowerInW
 * 
 */
public class OnOffTimeProcessor implements IFeaturePreProcessor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8782022818090989366L;
	
	public Iterable<FeatureDescription> getFeatureDescription() {
		final ArrayList<FeatureDescription> features = new ArrayList<FeatureDescription>();
		features.add(new IntFeatureDescription("avg_on_time1"));
		features.add(new IntFeatureDescription("device_on_count1"));
		features.add(new IntFeatureDescription("stddev_on_time1"));
		features.add(new IntFeatureDescription("avg_power_in_w1"));
		features.add(new IntFeatureDescription("max_power_in_w1"));
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
		
		int avgPowerSum = 0;
		int maxPowerConsumption = 0;
		
		for(final LineDataBucket bucket : buckets) {
			if(!bucket.hasEnergyConsumption()) {
				continue;
			}
			
			final int runtimeInSec = bucket.getFrom().getDiffInSeconds(bucket.getTo());
			deviceOnSum += runtimeInSec;
			deviceOnCount++;
			runtimes.add(new Double(runtimeInSec));
			
			final Integer calculateAvgPowerConsumption = bucket.calculateAvgPowerConsumptionInWatt();
			if(calculateAvgPowerConsumption != null) {
				avgPowerSum += calculateAvgPowerConsumption;
			}
			
			maxPowerConsumption = Math.max(bucket.getMaxPowerConsumption(), maxPowerConsumption);
		}
		
		if(deviceOnCount == 0) {
			return new String[] { "0", "0", "?", "?", "?" };
		}
		
		final double stdDev = StatsUtil.calculateStdDev(runtimes);
		
		return new String[] { (deviceOnSum / deviceOnCount) + "", deviceOnCount + "", stdDev + "", ((1.0 * avgPowerSum) / deviceOnCount) + "", maxPowerConsumption + "" };
	}
	
}
