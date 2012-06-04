package de.tud.kom.challenge.processors;

import java.util.List;

import de.tud.kom.challenge.csvdatareader.CsvContainer;

/**
 *	This processor collects information about the 
 *	variation of the power-consumtion douring the day
 *
 *	@author Qingli Yan
 *	@author Tobias Tomasi
 */


public class PowerOscProcessor implements FeatureProcessor {
	
	@Override
	public String getProcessorName() {
		return this.getClass().getSimpleName();
	}
	

	private final static int threshold = 10; 	
	// Define lower bound of threshold energy consumption value that we observe
	
/**
 *	Processes the CSV-file	
 *
 *	@param csv  a CSV-file
 *	@return String array with 2 elements containing "numeric" values 
 *			related to the variation of the consmtion douring the day
 */
 	
	public String[] processInput(CsvContainer csv) throws Exception {
		
		List<String[]> csvBuffer = csv.getEntries();
		String[] result = new String[2];	
		// start processing the Buffer if reading was successful
		if (csvBuffer != null) {
			int csvBufferSize = csvBuffer.size();
			int time = 0, oldTime = 0;  
			long pre_watt = 0, var_watt = 0, var_watt_dr = 0;
			
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
				
				if (watt > threshold && watt < 3700) {				// 3700 is the highest possible power-consumption measurement
 					if(Math.max(watt, pre_watt) != 0){
 						  
 						// only calculate oscillation over 5%
 						var_watt += Math.pow((pre_watt - watt),2)/Math.max(watt, pre_watt);
						
								// if the energy consumption 3 times higher than the threhold and the variation is more than 60%, 
 								// then document the variation
 						if(watt > 3 * threshold && Math.abs(pre_watt - watt)/Math.max(watt, pre_watt)> 0.6){
 								
 							var_watt_dr += (pre_watt - watt)^2/Math.max(watt, pre_watt);
						}
 					}
				}		 
				
				pre_watt = watt;
				oldTime = time;
				
			} 
			result[0] = var_watt+""; // variation of the power consumtion
			result[1] = var_watt_dr + ""; //value that describes sudden rises and falls of the power-consumption
			
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
 		String[] names = new String[2];  
		names[0] = "Power_Variation";
		names[names.length - 1] = "Power_dramatically_drop_rise_suddenly";
		return names;
	}
	
/**
 * Gets the value ranges of the attributes 
 * 
 * @return	String array with the valueranges of the attributes defined in getAttributesNames() in this case "numeric"
 * @see #getAttributeNames()
 * @see #processInput(CsvContainer)
 */
	
	public String[] getAttributeValueranges() {
		String[] results = new String[2]; 
 
		results[0]  = "numeric";
		results[1]  = "numeric";
		return results;
	}
}
