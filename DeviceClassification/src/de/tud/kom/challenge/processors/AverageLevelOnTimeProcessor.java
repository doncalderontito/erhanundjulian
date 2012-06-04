package de.tud.kom.challenge.processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.tud.kom.challenge.csvdatareader.CsvContainer;

/**
 * Calculates an average power level over the on time
 * 
 * @author Daniel Burgstahler
 *
 */
public class AverageLevelOnTimeProcessor implements FeatureProcessor {
	
	@Override
	public String[] processInput(CsvContainer csv) throws Exception {
		List<String[]> csvBuffer = csv.getEntries();
		String[] result = new String[4];
		
		
		if (csvBuffer != null) {
			int csvBufferSize = csvBuffer.size();		
			
			int onCount=0, onOffCycles=0;
			long daySum = 0, peak=0, overDayLevel=0;
			double value=0,lastValue=0;
			
			List<Integer>allValuesOnTime=new ArrayList<Integer>();								//for median calculation of on time
			

			for (int i=0; i<csvBufferSize; i++) {												//get an average over day level and a peak value
				try{																			//get peak and average over day level
					value = Integer.parseInt(csvBuffer.get(i)[1]);					
					if(value>3){
						onCount++;
					}
					daySum += value;
					value = (0.85*value) + (0.15*lastValue);
					lastValue = value;					
					if(peak<value){
						peak = Math.round(value+1);
					}
					
				} catch (NumberFormatException ex) {
					throw new Exception("line "+i);					
				}							
			}
			overDayLevel = daySum/csvBufferSize;												//calculate an average level over the whole day
			double onTimeProportion = (double)onCount/(double)csvBufferSize;									//calculate proportion of on time
			long onThreshold = Math.round(0.8 * (1/onTimeProportion) * overDayLevel);			//calculate a threshold of when the device seems to be on
			
			int time = 0, lastTime = 0;
			long averagePowerSum = 0;
			int powerTimecount = 0;
			int hour=0,minute=0,second=0, watt=0, lastWatt=0;
			boolean lastWasOn = false;
			
			for (int i=0; i<csvBufferSize; i++) {
				watt=0;
				try{																			//get actual time of sample
					hour = Integer.valueOf(csvBuffer.get(i)[0].substring(11, 13));
					minute = Integer.valueOf(csvBuffer.get(i)[0].substring(14, 16));
					second = Integer.valueOf(csvBuffer.get(i)[0].substring(17, 19));
					watt = Integer.parseInt(csvBuffer.get(i)[1]);
				} catch (NumberFormatException ex) {
					throw new Exception("line "+i);					
				}
				time = second + (minute * 60) + (hour * 3600);
				
				if (watt > onThreshold ){	
					
					allValuesOnTime.add(watt);
					
					if (time>lastTime){
						if (lastWasOn){
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
															+ (0.5 * timeDiff * (upperValue-lowerValue)); //calculate rectangle and triangle
							averagePowerSum += tmpNewSum;
							powerTimecount += timeDiff;
						}else{
							onOffCycles++;
							averagePowerSum += watt;
							powerTimecount++;
						}
					}	
					lastWatt = watt;
					lastTime = time;
					lastWasOn = true;
					
				}else{
					lastWasOn = false;
				}				
			}
			if(powerTimecount>0){
				long tmpResult = Math.round(averagePowerSum/powerTimecount);
				result[0]=""+tmpResult;
				result[1]=""+powerTimecount;
			}			
			result[2]=""+onOffCycles;
			
			//calculate median
			if(allValuesOnTime.size()>100){							
				Collections.sort(allValuesOnTime);
				if((allValuesOnTime.size() % 2) == 0){
					int position = allValuesOnTime.size()/2;
					int tmpMedian = Math.round( (allValuesOnTime.get(position)+allValuesOnTime.get(position+1))/2); 
					result[3]="" + tmpMedian;
				}else{
					int position = (allValuesOnTime.size()/2)+1;
					result[3]="" + allValuesOnTime.get(position);
				}
			}
			
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
				"average_level_at_on_time",
				"on_Time_of_device",
				"on_off_cycles_per_day",
				"median_on_time"};
		return result;		
	}

	@Override
	public String[] getAttributeValueranges() {
		String[] result = new String[]{
				"numeric",
				"numeric",
				"numeric",
				"numeric"};		
		return result;		
	}



	@Override
	public String getProcessorName() {
		return this.getClass().getSimpleName();
	}
	
}

	