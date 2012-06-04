package de.tud.kom.challenge.processors;

import java.text.DecimalFormat;
import java.util.List;

import de.tud.kom.challenge.csvdatareader.CsvContainer;

/**
 *	This processor collects the time point when the device was first activated during the day
 *	and saves it in a boolean-interval
 *
 * Remark 1: here, "active" means the energy consumption is higher than the threhold we've defined,
 *				  which means there could be exception for low-energy-consumption devices
 * Remark 2: for some earlier traces that begin at 1 a.m. caused by the problem in python script, 
 * 			 this processor does not work properly.
 * Remark 2: this processor is strongly dependent on the regularity of users' behavior
 *
 *
 *	@author Qingli Yan
 *	@author Tobias Tomasi
 */

public class StartTimeProcessor implements FeatureProcessor {
	
	@Override
	public String getProcessorName() {
		return this.getClass().getSimpleName();
	}

	private final static int threshold = 10; 
	// Define lower bound of threshold energy consumption value that we observe
	
	private final static int[] hours = new int[]{0,1,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24}; // End of segment (hour)
	private final static int StartTimeNum = 20; 
	
	public String[] processInput(CsvContainer csv) throws Exception {
		
		List<String[]> csvBuffer = csv.getEntries();
		String result[] = new String[StartTimeNum];		 
		// start processing the Buffer if reading was successful
		if (csvBuffer != null) {
			int csvBufferSize = csvBuffer.size();
			int time = 0, oldTime = 0, timeStamp = 0;

			
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
				
				time = (hour * 3600) + (minute * 60) + second;
				
				int td = time - oldTime;
				if (td == 0) continue; // Make sure to only regard at most 1 sample per second
				
				if (watt > threshold && watt < 3700 && timeStamp == 0) {
					timeStamp = time; 
					}		 
				// Segment activity into daily regions defined above 
				oldTime = time;
				
			}
			// Initialize the result-array
 			for (int i = 0; i < result.length; i++) {
				result[i] = "false";
			}
			
			if(timeStamp >= 0 && timeStamp < 3600) result[0] = "true"; //true if start time is between 00:00 and 01:00am
			if(timeStamp >= 3600 && timeStamp < 21600) result[1] = "true"; // true if start time is between 01:00am and 06:00am
			
			else{ 
				for(int i = 2; i < StartTimeNum; i++){								//true if the start time is contained in one of the 
						if(timeStamp >= (i+4)*3600  && timeStamp < (i+5)*3600)		//segments defined in the array hours
								result[i] = "true";
					}
				
			}
 

		} else {
			System.out.println("Error at reading the csv File");			// Error handling for css file error reading
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
		DecimalFormat df = new DecimalFormat("00");
 		String[] names = new String[StartTimeNum];
		
		names[0] = "Starts_right_after_midnght";		// if the device is on from the very beginning of the day

		for(int i = 1; i < names.length ; i++){
			names[i] = "Start_between_" + df.format(hours[i])+"00" + "_and_" + df.format(hours[i+1])+"00";
		}
 

		return names;
	}
	
/**
 * Gets the value ranges of the attributes 
 * 
 * @return	String array with the valueranges of the attributes defined in getAttributesNames() in this case "true" or "false"
 * @see #getAttributeNames()
 * @see #processInput(CsvContainer)
 */
	
	public String[] getAttributeValueranges() {
		String[] results = new String[StartTimeNum];  
 
		for (int k=0;k<results.length; k++) {
			results[k] = "{true, false}";
		}
		return results;
	}
}
