package de.tud.kom.challenge.processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.tud.kom.challenge.arff.featuredescription.FeatureDescription;
import de.tud.kom.challenge.arff.featuredescription.IntFeatureDescription;
import de.tud.kom.challenge.csvdatareader.CsvContainer;
import de.tud.kom.challenge.csvdatareader.LineData;
import de.tud.kom.challenge.processors.buckets.LineDataBucket;
import de.tud.kom.challenge.processors.buckets.OnOffBucketGenerator;
import de.tud.kom.challenge.processors.buckets.RelativeWidthBucketGenerator;

/***
 * Wir sortieren alle Messwerte nach der Leistungsaufnahme.
 * Dann unterteilen wir die sortierten Messwerte in 10 Teile und berechnen die durchschnittliche Leistung pro Teil.
 * Die 10 Teile werden als Features zurückgegeben
 * 
 * Ziel ist es zu sagen:
 * 10% der Zeit ist das Gerät mit voller Leistungsaufnahme gefahren
 * 80% der Zeit ist das Gerät mit halber Leistungsaufnahme gefahren
 * 10% der Zeit ist das Gerät mit geringer Leistungsaufnahme gefahren
 * 
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 * This feature processor takes all on cycles over a 24 hour period.
 * Then it normalizes the power consumption to values between 0 to 1.
 * The highest power consumption gets the value 1.
 * After the normalization step all consumptions are sorted in decreasing order.
 * After all the highest power consumption is at the first place and the lowest power consumption is at the last place.
 * Now the power consumptions are merged together into N buckets.
 * The first bucket has the values with the highest power consumption and the last bucket has the values with the lowest power consumption.
 * That means a histogram of power consumption values is built up.
 * An empirically determined value for N is 10 buckets.
 * This value caused the highest precision.
 * The result of this feature processor is the average power consumption for each bucket.
 * The intension of this feature processor is similar to the intension of the DFTProcessor.
 * Each device has its own characteristically power consumption over time.
 * This characteristics is captured by the histogram.
 * 
 */
public class SummarizedPowerConsumptionProcessor implements IFeaturePreProcessor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 420065741790226205L;

	public Iterable<FeatureDescription> getFeatureDescription() {
		final ArrayList<FeatureDescription> features = new ArrayList<FeatureDescription>();
		features.add(new IntFeatureDescription("bucket_0"));
		features.add(new IntFeatureDescription("bucket_1"));
		features.add(new IntFeatureDescription("bucket_2"));
		features.add(new IntFeatureDescription("bucket_3"));
		features.add(new IntFeatureDescription("bucket_4"));
		features.add(new IntFeatureDescription("bucket_5"));
		features.add(new IntFeatureDescription("bucket_6"));
		features.add(new IntFeatureDescription("bucket_7"));
		features.add(new IntFeatureDescription("bucket_8"));
		features.add(new IntFeatureDescription("bucket_9"));
		return features;
	}
	

	public String[] processInput(final CsvContainer csv) {
		
		final OnOffBucketGenerator generator = new OnOffBucketGenerator();
		final List<LineDataBucket> buckets = generator.getBuckets(csv.getCompressedEntries());
		
		final ArrayList<LineData> data = new ArrayList<LineData>();
		
		for(final LineDataBucket bucket : buckets) {
			if(!bucket.hasEnergyConsumption()) {
				continue;
			}
			
			data.addAll(bucket.getData());
		}
		
		if(data.size() < 2) {
			return null;
		}
		
		Collections.sort(data, new Comparator<LineData>() {
		
			public int compare(final LineData o1, final LineData o2) {
				return o2.getConsumptionInWatt() - o1.getConsumptionInWatt();
			}
		});
		
		final RelativeWidthBucketGenerator g = new RelativeWidthBucketGenerator(10);
		final List<LineDataBucket> dataBuckets = g.getBuckets(data);
		
		if(dataBuckets.isEmpty()) {
			return Collections.nCopies(10, "?").toArray(new String[0]);
		}
		
		final String[] result = new String[10];
		for(int i = 0; i < 10; i++) {
			result[i] = "" + ((100 * dataBuckets.get(i).calculateAvgPowerConsumptionInWatt()) / data.get(0).getConsumptionInWatt());
		}
		
		return result;
	}
	
}
