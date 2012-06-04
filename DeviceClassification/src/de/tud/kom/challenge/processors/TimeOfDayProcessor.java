package de.tud.kom.challenge.processors;

import java.util.List;

import de.tud.kom.challenge.arff.featuredescription.FeatureDescription;
import de.tud.kom.challenge.arff.featuredescription.FeatureDescriptionGenerator;
import de.tud.kom.challenge.csvdatareader.CsvContainer;
import de.tud.kom.challenge.csvdatareader.DateTime;
import de.tud.kom.challenge.csvdatareader.LineData;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 * 
 * This feature processor divides the day into 144 time slots.
 * Each slot has a length of ten minutes.
 * Then it checks for each time slot if there is a power consumption during that time.
 * If this is the case it outputs true for that time slot and false otherwise.
 * This feature is an improvement over the TimeRegionProcessor.
 * It provides more information how a device is used.
 * But it may highly depend on the owner of an device.
 * For example a coffee machine is in use after it’s owner gets up in the morning.
 * Different owners gets up at a different time in the morning.
 * 
 */
public class TimeOfDayProcessor implements IFeaturePreProcessor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8859254253462156076L;
	private final int binCount = 144; // Every 10 minutes (1440 minutes in a day)
	
	public String[] processInput(final CsvContainer file) {
		
		// Store output
		final String[] result = new String[this.binCount];
		int time = 0, lastTime = 0;
		
		boolean activity = false;
		
		for(final LineData ln : file.getCompressedEntries()) { // for each line
		
			// Collect time of sample
			int hour, minute, watt;
			final DateTime date = ln.getDateTime();
			hour = date.getHour();
			minute = date.getMinute();
			watt = ln.getConsumptionInWatt();
			
			// Check for activity in current time interval
			if(watt > 2) {
				activity = true;
			}
			time = ((hour * 60 * this.binCount) / 1440) + ((minute * this.binCount) / 1440);
			
			if(time != lastTime) {
				result[lastTime] = "" + activity;
				activity = false;
				lastTime = time;
			}
		}
		result[time] = "" + activity;
		
		for(int k = 0; k < result.length; k++) {
			if(result[k] == null) {
				result[k] = "?";
			}
		}
		
		return result;
	}
	

	public Iterable<FeatureDescription> getFeatureDescription() {
		// final ArrayList<FeatureDescription> descr = new ArrayList<FeatureDescription>();
		//
		// for(int k = 0; k < this.binCount; k++) {
		// final DecimalFormat nf = new DecimalFormat("00");
		// final int t1a = k / 6;
		// final int t1b = (k - (t1a * 6)) * 10;
		// final int t2a = (k + 1) / 6;
		// final int t2b = ((k + 1) - (t2a * 6)) * 10;
		// descr.add(new BooleanFeatureDescription("active_from_" + nf.format(t1a) + nf.format(t1b) + "_to_" + nf.format(t2a) + nf.format(t2b)));
		//
		// }
		
		final List<FeatureDescription> fd = FeatureDescriptionGenerator.getBooleanFeatureDescription("active", 600);
		// return descr;
		return fd;
	}
	
}
