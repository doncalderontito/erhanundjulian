package de.tud.kom.challenge.processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.tud.kom.challenge.csvdatareader.CsvContainer;

/**
 * Calculates an average power level over time periods of 6 hours, half days, the whole day and the median over the whole day 
 * 
 * @author Daniel Burgstahler
 *
 */
public class AverageLevelProcessor1 implements FeatureProcessor {
	
	@Override
	public String getProcessorName() {
		return "AverageLevelProcessor1";
	}
	
	private final int range = 6; 																//Split in blocks of two hours
	
	@Override
	public String[] processInput(CsvContainer csv) throws Exception {
		List<String[]> csvBuffer = csv.getEntries();
		String[] result = new String[8];
		
		int time = 0, lastTime = 0;
		double averagePowerSum = 0, averagePowerSumHalfDay = 0,averagePowerSumDay = 0;
		int hour=0,minute=0,second=0, watt=0, lastWatt=0;
		int interval=0, lastInterval=0;
		int intervalBegin=0;
		
		List<Integer>allValues=new ArrayList<Integer>();										//for median calculation
		
		
		int dayBegin=0;
		int halfDayBegin=0;
		int midOfDay = 12 * 3600;
		boolean firstHalf = true;
		
		if (csvBuffer != null) {
			int csvBufferSize = csvBuffer.size();			
			for (int i=0; i<csvBufferSize; i++) {				
				try{																			//get actual time of sample
					hour = Integer.valueOf(csvBuffer.get(i)[0].substring(11, 13));
					minute = Integer.valueOf(csvBuffer.get(i)[0].substring(14, 16));
					second = Integer.valueOf(csvBuffer.get(i)[0].substring(17, 19));
					watt = Integer.parseInt(csvBuffer.get(i)[1]);
				} catch (NumberFormatException ex) {
					throw new Exception("line "+i);					
				}
				
				allValues.add(watt);
				
				time = second + (minute * 60) + (hour * 3600);									//calculate actual time in seconds
				
				if (i==0){
					dayBegin=time;
					halfDayBegin=time;
				}
				
				if(time>lastTime){																//check if sample is minimum one second after last sample, want to avoid more than one sample per second
					interval = hour/range;														//calculate actual the actual interval we are in
					
					if(interval>lastInterval){													//if new Interval -> write value
						int intervalRange = time - intervalBegin;								//measured range in current interval, end = current time
						if(i!=0){																//avoid interpreting missing samples at beginning as interval of zero value
							result[lastInterval]=""+Math.round(averagePowerSum/intervalRange);	//write value to result
						}
						averagePowerSum = 0;													//rest averagePower value
						intervalBegin=time;
					}
					
					if(time>=midOfDay && firstHalf){
						firstHalf=false;														//avoid second run
						int intervalRangeHalfDay = time - halfDayBegin;							//measured range in current half day interval, end = current time
						result[4]=""+Math.round(averagePowerSumHalfDay/intervalRangeHalfDay);	//add value of 1st half of day
						halfDayBegin = time;													//set begin of 2nd half of day
						averagePowerSumHalfDay=0;												//rest averagePowerSumHalfDay for 2nd half of day		
					}
					
					int timeDiff = time - lastTime;												//interpolate sum of power between last and current sample					
					int lowerValue=0, upperValue=0;
					if (lastWatt>watt){
						upperValue = lastWatt;
						lowerValue = watt;						
					}else{
						upperValue = watt;
						lowerValue = lastWatt;							
					}																			//interpolate sum of power between last and current sample
					double tmpNewSum = (timeDiff * lowerValue) 
													+ (0.5 * timeDiff * (upperValue-lowerValue));//calculate rectangle and triangle
					averagePowerSum += tmpNewSum; 												
					averagePowerSumHalfDay+= tmpNewSum;											
					averagePowerSumDay += tmpNewSum; 											
					
					lastInterval=interval;														//set 'last' values = current values
					lastTime=time;
					lastWatt=watt;
				}				
			}	
			int intervalRange = time - intervalBegin;											//measured range in current interval, end = current time
			int intervalRangeHalfDay = time - halfDayBegin;										//measured range in current half day interval, end = current time
			int intervalRangeDay = time - dayBegin;												//measured range on whole day, end = current time
			result[interval]=""+Math.round(averagePowerSum/intervalRange);						//add value of last interval
			result[5]=""+Math.round(averagePowerSumHalfDay/intervalRangeHalfDay);				//add value of 2nd half of day
			result[6]=""+Math.round(averagePowerSumDay/intervalRangeDay);						//add value of whole day
					
		}
		
		//calculate median
		Collections.sort(allValues);
		if((allValues.size() % 2) == 0){
			int position = allValues.size()/2;
			int tmpMedian = Math.round( (allValues.get(position)+allValues.get(position+1))/2); 
			result[7]="" + tmpMedian;
		}else{
			int position=(allValues.size()/2)+1;
			result[7]="" + allValues.get(position);
		}
		
		for (int k=0;k<result.length;k++) {
			if (result[k] == null){
				result[k] = "?";
			}
		}
		return result;
		
		
	}

	@Override
	public String[] getAttributeNames() {
		String[] result = new String[]{
				"average_level_night",
				"average_level_morning",
				"average_level_afternoon",
				"average_level_evening",
				"average_level_first_half_day",
				"average_level_second_half_day",
				"average_level_all_day",
				"median"
				};
		return result;		
	}

	@Override
	public String[] getAttributeValueranges() {
		String[] result = new String[]{
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric"
				};		
		return result;
	}	
}
