package de.tud.kom.challenge.processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.tud.kom.challenge.csvdatareader.CsvContainer;

/**
 * The interval processor analyzes the longest period (in time) of day during which 
 * the device is active. (used for the challenge)
 *
 * The collected attributes are:
 * <ul>
 * <li> number of the active intervals during the day
 * <li> length of the analyzed active interval in seconds
 * <li> maximal value of the analyzed interval
 * <li> minimal value of the analyzed interval
 * <li>	integration over the analyzed interval
 * <li> average value of the analyzed interval
 * <li> percentage of values == maximum in the analyzed interval
 * <li> percentage of values == minimum in the analyzed interval
 * <li> percentage of values between maximum and minimum of the analyzed interval
 * <li> if there are only consumption peaks (intervals with length of 1 second) during the day, <code>true</code> or <code>false</code>
 * <li> if there are no intervals at all during the day, <code>true</code> or <code>false</code>
 * </ul>
 * 
 * @author Quingli Yan
 * @author M. Tobias Tomasi
 * @version final
 * 
 *
 */

public class IntervalProcessor implements FeatureProcessor {
	
	@Override
	public String getProcessorName() {
		return this.getClass().getSimpleName();
	}
	
	private final static int threshold = 2;
	
	/**
	 *	Processes the CSV-file	
	 *
	 *	@param file  a CSV-file
	 *	@return String array with 11 elements containing the attributes in numeric and boolean values 
	 *			
	 */

	
	public String[] processInput(CsvContainer csv) throws Exception {
		
		List<String[]> csvBuffer = csv.getEntries();
		String[] result = new String[11];
		
		if (csvBuffer != null) {
			int csvBufferSize = csvBuffer.size();
			
			int time = 0, oldTime = 0;
			int  watt, wattbefore = 0;
			int intervalCounter = 0;
			int peakCounter = 0; //count the peaks 
			int time0 = 0;
			int time1 = 0;
			int time2 = 0;
			
			ArrayList<Integer> tempVec = new ArrayList<Integer>(); 
			ArrayList<Integer> interval = new ArrayList<Integer>();
			
			interval.clear(); //reset the ArryaLists
			tempVec.clear();
			
			for(int i = 0; i< result.length-2; i++){		//reset the result-array		
				result[i] = 0+"";
			}
			result[9] = "false";
			result[10] = "false";
						
			for (int i=0; i<csvBufferSize; i++) { // for each line
				int hour, minute, second;
				
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
				if (td == 0 || watt >=3700  || wattbefore >=3700 ) continue; // avoid analyzing more than one samples per second and erroneous measurements of watt
				
				if(watt >= threshold){
					if(tempVec.size() == 0){time0 = time;}		//saving starting time of interval
					tempVec.add(watt);							
					
					if(i == csvBufferSize-1 && tempVec.size() > interval.size()) {		// make sure to analyze intervals at the end of the CSV-file
						interval.clear();
						interval.trimToSize();
						interval.addAll(tempVec);
						time1 = time0;
						time2 = time+1; // avoid time1 - time2 = 0 in case of peak at the end of the CSV-file
					}
				}
				
				else{ //so if watt < threshold
					
					if(tempVec.size() > 1){						//ignore peaks (intervals of 1 sec) now 
						if(tempVec.size() > interval.size()){
							interval.clear();					//make sure it's empty
							interval.trimToSize();
							interval.addAll(tempVec);		//securing the longest interval
							time1 = time0; 					// securing starting time
							time2 = time;  					// saving ending time
						}
						
						intervalCounter++;
						
					}
					
					if(tempVec.size() == 1){peakCounter++;} //counting the peaks (intervals of 1 sec)
					
					tempVec.clear();
					tempVec.trimToSize();
				}
								
				wattbefore = watt;
				oldTime = time;
			}
			
			if(interval.isEmpty() && intervalCounter == 0){
				result[10] = "true"; //no intervals
			}
			
			result[0] = intervalCounter+""; //number of intervals
			
			int totNum = 0;
			
			totNum = interval.size();
			
			result[1] = (time2 - time1)+""; //duration of the interval
			
						
			int max = 0;
			int min = 0;
			
			int summ =0; 	// summ of watt
			double summMax = 0; 	//number of maxima
			double summMin = 0;
			double summMid = 0;
			
			if(!interval.isEmpty()){ // otherwise can't find max and min
				
				max = Collections.max(interval);
				min = Collections.min(interval);
			
				result[2] = max+"";// max value of the interval
			
				result[3] = min+""; //min value of the interval
				
				int var;
				
				for(int i = 0; i<interval.size(); i++){				
					
					var = interval.get(i);
					summ += var;
					
					if(var == max ){   
						summMax++;
					}
					if(var == min){
						summMin++;
					}
					if(var < max && var > min ){
						summMid++;
					}
				}
				
				result[5] = (summ/interval.size())+""; //average value of the interval
				
			}
			else{
				result[2] = 0+"";
				result[3] = 0+"";
				
				result[5] = 0+"";
			}
			
			
			result[4] = summ+""; //Integral over the interval
			
			if(totNum > 0){			//avoid dividing by zero
			
			result[6] = (summMax/totNum)*100+""; //number of maxima in percentage
						
			result[7] = (summMin/totNum)*100+""; //number of minima in percentage
			
			result[8] = (summMid/totNum)*100+""; //number of values between max and min in percentage
			}
			
			else{
				result[6] = 0+"";
				result[7] = 0+"";
				result[8] = 0+"";
			}
			
			if(peakCounter > 0){
				result[9] = "true";		//if true, there are also peaks 
			}
			
			
			}
		else {
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
		String[] names = new String[11];
		
		names[0] = "numberOfIntervals";
		names[1] = "lenghtOftheInterval";
		names[2] = "maxOfInterval";
		names[3] = "minOfInterval";
		names[4] = "integrationOfInterval";
		names[5] = "averageOfInterval";
		names[6] = "percentOfMaxima";
		names[7] = "percentOfMinima";
		names[8] = "percentOfValuesBetweenMaxMin";
		names[9] = "thereArePeaks";
		names[10] = "NoIntervals";
		
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
		String[] values = new String[11];
		
		for(int i = 0; i<values.length-2; i++){
			values[i] = "numeric";
		}
		values[9] = "{true, false}";
		values[10] = "{true, false}";
		
		return values;
	}

	

}
