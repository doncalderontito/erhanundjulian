package de.tud.kom.challenge.processors;

import java.util.List;

import de.tud.kom.challenge.csvdatareader.CsvContainer;

/**
 *	This processor collects the maximal power-consumption of the device
 * and the trace of standby power consumption (vampire power)
 *	during the day (used for the challenge)
 *
 *	@author Qingli Yan
 *	@author Tobias Tomasi
 *	@version final
 */

public class MaxPowerProcessor implements FeatureProcessor {
	
	@Override
	public String getProcessorName() {
		return this.getClass().getSimpleName();
	}
	

/**
 *	Processes the CSV-file	
 *
 *	@param file  a CSV-file
 *	@return String array with 35 elements containing "true" or "false" 
 *			according to the value of the maximal power-consumption
 */

	public String[] processInput(CsvContainer file) {

		List<String[]> csvBuffer = file.getEntries();

		// Store output
		String[] result = new String[35];
		// Reset output
		for (int i = 0; i < result.length; i++) {
			result[i] = "false";
		}

		if (csvBuffer != null) { // avoid incorrect input

			int csvBufferSize = csvBuffer.size();

			int wattlow = 0;
			int wattmax = 0;
			for (int i = 0; i < csvBufferSize; i++) { // for each line of the CSV-file

				// Collect watt of sample

				wattlow = Integer.parseInt(csvBuffer.get(i)[1]);

				if (wattlow >= wattmax && wattlow < 3700 && wattlow < 3700) { // 3680 is the maximal power measurement of the plugs, 
					wattmax = wattlow;										//	so every value over 3700 is certainly wrong and needs to be eliminated
				}

			}
			
			//classify wattmax
			for(int i = 0;  i< 9; i++){								//10-100; if one of these intervals 9 intervals, between 10 and 100 and with 
				if(wattmax >= (i+1)*10 && wattmax < (i+2)*10){		//span of 10, holds wattmax than set it "true"
					result[i] = "true";
				}
			}

			for (int e = 9; e < result.length - 2; e++) {		// 100 - 2500; same as above for the intervlas with a span of 100, between 100 and 2500
				if (wattmax >= (e-8)*100 && wattmax < (e-7)*100) {
					result[e] = "true";
				}
			}

			if (wattmax >= 2500) {								// >2500; set this interval "true" if wattmax is over 2500
				result[result.length - 2] = "true";
			}
			
			if(wattmax >= 2 && wattmax < 10){				//2-10;	set the interval "true" if watmax is between 2 and 10
				result[result.length - 1] = "true";
			}
			
			
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

		String[] names = new String[35];
		
		for(int h = 0; h < 9; h++){											// maximal power consumption between 10-100 Watt
			names[h] = "maxPower" + (h+1) * 10 + "to" + (h + 2) * 10;
		}

		for (int i = 9; i < names.length - 2; i++) {						// maximal power consumption between 100 - 2500 Watt
			names[i] = "maxPower" + (i-8) * 100 + "to" + (i-7) * 100;
		}

		names[names.length - 2] = "maxPowerover2500";						// maximal power consumption larger than 2500 Watt
		
		names[names.length - 1] = "vampirePower";						// standby power consumption between 2-10
		
		
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

		String[] valueranges = new String[35];
		
		for (int i = 0; i < valueranges.length; i++) {
			valueranges[i] = "{true, false}";
		}

		return valueranges;
	}

}
