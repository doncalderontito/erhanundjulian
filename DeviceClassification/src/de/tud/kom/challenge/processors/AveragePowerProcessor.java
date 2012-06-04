package de.tud.kom.challenge.processors;

import java.util.List;

import de.tud.kom.challenge.csvdatareader.CsvContainer;

/** 
 * This processor collects the average power consumption 
 * during the active duration of the whole day
 *
 * @author Qingli Yan
 * @author Tobias Tomasi
 */

public class AveragePowerProcessor implements FeatureProcessor {
	
	@Override
	public String getProcessorName() {
		return this.getClass().getSimpleName();
	}

	private final static int threshold = 10; 
	// Define lower bound of threshold energy consumption value that we observe
	
	private final static int avgPowerIntervNum = 70;
	// The number of features we extract in this processor
	
/**
 *	Processes the CSV-file	
 *
 *	@param csv  a CSV-file
 *	@return String array with 71 elements containing "true" or "false" 
 *			according to the value of the average power-consumption
 */	
	
	public String[] processInput(CsvContainer csv) throws Exception {
		
		List<String[]> csvBuffer = csv.getEntries();
		String result[] = new String[avgPowerIntervNum+1];		// 71 features inserted
		
		// start processing the Buffer if reading was successful
		if (csvBuffer != null) {
			int csvBufferSize = csvBuffer.size();
			int time = 0, oldTime = 0;  //, activeIndex = 0;
			long totalCounter = 0, avg_watt = 0;
			
			for (int i=0; i<csvBufferSize; i++) { // for each line
				int hour, minute, second, watt;
				
				try {
					// Collect time and watt of sample
					hour = Integer.valueOf(csvBuffer.get(i)[0].substring(11, 13));
					minute = Integer.valueOf(csvBuffer.get(i)[0].substring(14, 16));
					second = Integer.valueOf(csvBuffer.get(i)[0].substring(17, 19));
					watt = Integer.parseInt(csvBuffer.get(i)[1]);
					
				} catch (NumberFormatException ex) {
					throw new Exception("line "+i);
				}
				
				time = (hour * 3600) + (minute * 60) + second;		//time in seconds
				int td = time - oldTime;
				if (td == 0) continue; // Make sure to only regard at most 1 sample per second
				if (watt > threshold && watt < 3700) {
					totalCounter += td;		// Sum up the active duration
					avg_watt += watt;		// Sum of all energy consumption values higher than the threshold
					}		 
				
				// Segment activity into daily regions defined above
 			
				oldTime = time;
				
			}
 		
			if(totalCounter!=0){
			// If the active duration for energy consumption, which is higher than the defined threshold, is not zero, 
				avg_watt /= totalCounter; 
				// then calculate average power consumption during the active duration of the day.
			}
			else {
			// otherwise, the average energy consumption will be set to 0 
				avg_watt = 0; 			
			}
			// initialization 
			for (int i = 0; i < result.length; i++) {
				result[i] = "false";
			}
			// classify average energy consumption
			for(int i = 0;  i< 19; i++){							
					// 10-100 Watt; if one of these intervals 19 intervals, between 10 and 100 and with
					if(avg_watt >= (i+1)*5 && avg_watt < (i+2)*5){	
					//span of 5, holds avg_watt,  then set it "true"
						result[i] = "true";
					}
				}

			for (int i = 19; i < result.length - 2; i++) {			
				// 100 - 2500 Watt; same as above for the intervlas with a span of 50, between 100 and 2500
				if (avg_watt >= (i-8)*50 && avg_watt < (i-7)*50) {
					result[i] = "true";
				}
			}

			if (avg_watt >= 2500) {								
			// larger than 2500 Watt; set this interval "true" if avg_watt is over 2500
				result[result.length - 2] = "true";
			}
			
			result[result.length - 1] = avg_watt+""; 		// numeric value of avg_watt
			
		} else {
			System.out.println("Error at reading the csv File");
			System.exit(1);
		}
 
		return result;
	}	

	/**
	 * Gets the names of the attributes
	 * 
	 * @return String array with the names of the intervals defined by the processInput(CsvContainer) 
	 * @see #processInput(CsvContainer)
	 */
	
	public String[] getAttributeNames() {
		
		String[] names = new String[avgPowerIntervNum + 1]; 
		for(int i = 0; i < 19; i++){										// for average power between 10 and 100 Watt
			names[i] = "avgPower_" + (i+1) * 5 + "_to_" + (i + 2) * 5;
		}

		for (int i = 19; i < names.length - 2; i++) {						// for average power between 100 and 2500 Watt
			names[i] = "AvgPower_" + (i-8) * 50 + "_to_" + (i-7) * 50;
		}

		names[names.length - 2] = "avgPower_over2500";					// for average power larger than 2500 Watt
		
		names[names.length - 1] = "Average_Power:";				// numeric
 
		return names;
	}
	
	/**
	 * Gets the value ranges of the attributes 
	 * 
	 * @return	String array with the value-ranges of the attributes defined in getAttributesNames() in this case "numeric" and "true" or "false"
	 * @see #getAttributeNames()
	 * @see #processInput(CsvContainer)
	 */
	
	public String[] getAttributeValueranges() {
		String[] results = new String[avgPowerIntervNum+1]; 
		for (int k=0;k<results.length - 1;k++) {
			results[k] = "{true, false}";
		}
		results[results.length - 1]  = "numeric";
		return results;
	}
}
