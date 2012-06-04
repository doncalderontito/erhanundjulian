package de.tud.kom.challenge.processors;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.tud.kom.challenge.arff.featuredescription.FeatureDescription;
import de.tud.kom.challenge.arff.featuredescription.IntFeatureDescription;
import de.tud.kom.challenge.csvdatareader.CsvContainer;
import de.tud.kom.challenge.csvdatareader.DateTime;
import de.tud.kom.challenge.csvdatareader.LineData;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 * 
 * This feature determines if a device had a power consumption in one of the following time regions:
 * 1. from 0 to 6 o’Clock
 * 2. from 6 to 10 o’Clock
 * 3. from 10 to 18 o’Clock
 * 4. from 18 to 24 o’Clock.
 * The intension of this feature is a different usage of different devices.
 * A television might be used in the evening whereas a coffee machine might be used in the morning.
 * 
 */
public class TimeRegionProcessor implements IFeaturePreProcessor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8292501790927053957L;
	private final static int threshold = 2; // Watts
	private final static int[] hours = new int[] { 6, 10, 18, 24 }; // End of segment (hour)

	public String[] processInput(final CsvContainer csv) {
		
		final String result[] = new String[5];
		
		// start processing the Buffer if reading was successful
		
		int time = 0, oldTime = 0;
		long activityCounter = 0, totalCounter = 0;
		int hourIndex = 0;
		
		for(final LineData ln : csv.getCompressedEntries()) { // for each line
			int hour, minute, second, watt;
			
			// Collect time of sample
			final DateTime date = ln.getDateTime();
			hour = date.getHour();
			minute = date.getMinute();
			second = date.getSecond();
			watt = ln.getConsumptionInWatt();
			
			time = (hour * 3600) + (minute * 60) + second;
			final int td = time - oldTime;
			if(td == 0) {
				continue; // Make sure to only regard at most 1 sample per second
				// System.out.println("h"+hour+" m"+minute+" s"+second+" w"+watt+" t"+time+" o"+oldTime+" d"+td);
			}
			
			if(watt > TimeRegionProcessor.threshold) {
				activityCounter += td;
			}
			
			// Segment activity into daily regions defined above
			if(hour >= TimeRegionProcessor.hours[hourIndex]) {
				result[hourIndex++] = activityCounter + "";
				totalCounter += activityCounter;
				activityCounter = 0;
			}
			oldTime = time;
		}
		result[hourIndex++] = activityCounter + "";
		while(hourIndex < TimeRegionProcessor.hours.length) {
			result[hourIndex++] = "?";
		}
		
		totalCounter += activityCounter;
		result[result.length - 1] = totalCounter + "";
		
		return result;
	}
	

	public Iterable<FeatureDescription> getFeatureDescription() {
		final DecimalFormat df = new DecimalFormat("00");
		
		final ArrayList<FeatureDescription> descr = new ArrayList<FeatureDescription>();
		
		for(final int hour : TimeRegionProcessor.hours) {
			descr.add(new IntFeatureDescription("active_seconds_till" + df.format(hour) + "00"));
		}
		
		descr.add(new IntFeatureDescription("active_seconds_allday"));
		
		return descr;
	}
	
}
