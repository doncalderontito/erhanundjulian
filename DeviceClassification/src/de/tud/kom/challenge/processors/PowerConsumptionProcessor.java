package de.tud.kom.challenge.processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.tud.kom.challenge.arff.featuredescription.FeatureDescription;
import de.tud.kom.challenge.arff.featuredescription.FeatureDescriptionGenerator;
import de.tud.kom.challenge.csvdatareader.CsvContainer;
import de.tud.kom.challenge.csvdatareader.LineData;
import de.tud.kom.challenge.processors.buckets.FixedWidthBucketGenerator;
import de.tud.kom.challenge.processors.buckets.IBucketGenerator;
import de.tud.kom.challenge.processors.buckets.LineDataBucket;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 * 
 * The power consumption processor is similar to the TimeOfDayProcessor.
 * It also divides a day into 144 time slots.
 * Then it calculates the average power consumption for each time slot. 
 * 
 */
public class PowerConsumptionProcessor implements IFeaturePreProcessor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2958729335037154514L;
	private static final int BUCKET_WIDTH_IN_SEC = 600;
	
	public List<FeatureDescription> getFeatureDescription() {
		return FeatureDescriptionGenerator.getIntFeatureDescription("kwh", PowerConsumptionProcessor.BUCKET_WIDTH_IN_SEC);
	}
	

	public String[] processInput(final CsvContainer csv) {
		final ArrayList<LineData> lst = new ArrayList<LineData>();
		for(final LineData l : csv.getCompressedEntries()) {
			lst.add(l);
		}
		if(lst.size() <= 2) {
			return Collections.nCopies(this.getFeatureDescription().size(), "?").toArray(new String[0]);
		}
		
		final IBucketGenerator generator = new FixedWidthBucketGenerator(PowerConsumptionProcessor.BUCKET_WIDTH_IN_SEC);
		final List<? extends LineDataBucket> buckets = generator.getBuckets(lst);
		
		// Store output in 10 minute intervals
		final ArrayList<String> result = new ArrayList<String>(buckets.size());
		
		for(final LineDataBucket lineDataBucket : buckets) {
			final int sum = lineDataBucket.calculateWattSeconds();
			
			result.add(sum + "");
		}
		
		return result.toArray(new String[0]);
	}
	
}
