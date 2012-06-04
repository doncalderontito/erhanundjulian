package de.tud.kom.challenge.processors;

import java.text.DecimalFormat;
import java.util.List;

import de.tud.kom.challenge.csvdatareader.CsvContainer;

/**
 * Calculates an average power level over blocks of two hours
 * 
 * @author Daniel Burgstahler
 *
 */
public class AverageLevelProcessor2 implements FeatureProcessor {
	
	public String getProcessorName() {
		return "AverageLevelProcessor2";
	}
	
	private final int range = 2; 																//Split in blocks of two hours
	private final int rangecount = 24/range;													//No of time ranges per day

	@Override
	public String[] processInput(CsvContainer csv) throws Exception {
		List<String[]> csvBuffer = csv.getEntries();
		String[] result = new String[rangecount];
		
		int time = 0, lastTime = 0;
		long averagePowerSum = 0;
		int hour=0,minute=0,second=0, watt=0, lastWatt=0;
		int interval=0, lastInterval=0;
		int intervalBegin=0;
		
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
				time = second + (minute * 60) + (hour * 3600);									//calculate actual time in seconds
				if(time>lastTime){																//check if sample is minimum one second after last sample, want to avoid more than one sample per second
					interval = hour/range;														//calculate actual the actual interval we are in
					
					if(interval>lastInterval){													//if new range -> write value
						int intervalRange = time - intervalBegin;								//measured range in current interval, end = current time
						if(i!=0){																//avoid interpreting missing samples at beginning as interval of zero value
							result[lastInterval]=""+Math.round(averagePowerSum/intervalRange);	//write value to result
						}
						averagePowerSum = 0;													//rest averagePower value
						intervalBegin=time;
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
					averagePowerSum += (timeDiff * lowerValue) 
													+ (0.5 * timeDiff * (upperValue-lowerValue));  //calculate rectangle and triangle

					lastInterval=interval;														//set last values = current values
					lastTime=time;
					lastWatt=watt;
				}				
			}	
			int intervalRange = time - intervalBegin;											//measured range in current interval, end = current time
			result[interval]=""+Math.round(averagePowerSum/intervalRange);						//ad value of last interval
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
		String[] result = new String[rangecount];
		DecimalFormat df = new DecimalFormat("00");
		for (int i=0;i<rangecount;i++){
			result[i]="average_level_from_" + df.format((i*range)) + ":00_TO_" 
						+ df.format(((i+1)*range)) +":00";
		}
		return result;		
	}

	@Override
	public String[] getAttributeValueranges() {
		String[] result = new String[rangecount];	
		for (int i=0;i<rangecount;i++){
			result[i]="numeric";
		}
		return result;		
	}
	
}

	