package de.tud.kom.challenge.processors;

import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.challenge.csvdatareader.CsvContainer;


/**
 * Checks if there is a peak and tries to describe the slope before and after the peak.
 * 
 * @author Daniel Burgstahler
 *
 */
public class PeakSlopeProcessor implements FeatureProcessor {
	
	
	@Override
	public String getProcessorName() {
		return "PeakSlopeProcessor";
	}
	
	
	private final static Logger log = Logger.getLogger(PeakSlopeProcessor.class.getSimpleName());
	
	@Override
	public String[] processInput(CsvContainer csv) throws Exception {
		List<String[]> csvBuffer = csv.getEntries();
		String[] result = new String[5];
		
		int time = 0, lastTime = 0, timeBegin=0;
		boolean lastWasOn = false;
		double tmpValue = 0;
		long peakPower = 0;
		int powerTimecount = 0;;
		double averagePowerSum1 = 0;
		double averagePowerSum2 = 0;
		
		try{
		if (csvBuffer != null) {
			int csvBufferSize = csvBuffer.size();
			int peakPosition = 0;
			int[] sampleValues = new int[csvBuffer.size()];
			for (int i=0; i<csvBufferSize; i++) {
				int hour,minute,second, watt, lastWatt=0;;
						
				try{																				//get time
					hour = Integer.valueOf(csvBuffer.get(i)[0].substring(11, 13));
					minute = Integer.valueOf(csvBuffer.get(i)[0].substring(14, 16));
					second = Integer.valueOf(csvBuffer.get(i)[0].substring(17, 19));
					watt = Integer.parseInt(csvBuffer.get(i)[1]);
				} catch (NumberFormatException ex) {
					throw new Exception("line "+i);
				}
				time = second + (minute * 60) + (hour * 3600);
				
				if(i==0){
					timeBegin=time;
				}

				if(time>lastTime){																//calculate average power level to get a threshold
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
					averagePowerSum1 += tmpNewSum; 
				}
								
				if (watt > 3){																	//calculate position of strongest peak
					
					if(time>lastTime){
						int timeDiff = time-lastTime;
						//use a kind of lowpass to debounce the signal, so only values are detected as peak that are much higher than neighbours 
						//dependent of time difference
						int lastMeasureWeight = 21-timeDiff; 
						if (lastMeasureWeight<1){
							lastMeasureWeight=1;
						}
						tmpValue = ((double)lastMeasureWeight/100) * tmpValue 
									+ ((double)(100-lastMeasureWeight)/100) * watt; 
						
						sampleValues[i]=watt;
						
						if (peakPower<tmpValue){
							peakPower=Math.round(tmpValue);
							peakPosition = i;
						}

						if (lastWasOn){				
							int lowerValue=0, upperValue=0;												//interpolate sum of power between last and current sample
							if (lastWatt>watt){
								upperValue = lastWatt;
								lowerValue = watt;										
							}else{
								upperValue = watt;
								lowerValue = lastWatt;							
							}																			//interpolate sum of power between last and current sample
							double tmpNewSum = (timeDiff * lowerValue) 								
															+ (0.5 * timeDiff * (upperValue-lowerValue)); //calculate rectangle and triangle
							averagePowerSum2 += tmpNewSum;
							powerTimecount += timeDiff;
						}else{
							averagePowerSum2 += watt;
							powerTimecount++;
						}	
					}					
					lastWatt = watt;
					lastTime = time;
					lastWasOn = true;
				}else{
					lastWasOn = false;
				}
				
			}//end for loop
			
			if(powerTimecount<1){															//prevent dividing by 0
				powerTimecount=0;
			}
			
			long thresholdLow = Math.round(averagePowerSum1 /(time-timeBegin));				//thresholdLow = average power level over day			
			long thresholdHigh = Math.round(averagePowerSum2 /powerTimecount);				//thresholdHigh = average power level while device is running

			//now analyze the peak slope 
			if(peakPower>(Math.round(1.5 * thresholdLow))){
				// compare average level of 10 previous samples
				long tmpPreSum = 0;			
				for (int i =0; i<10; i++){
					int position = peakPosition - 12 + i;
					if (position <0){
						position = 0;
					}
					tmpPreSum += sampleValues[position];
				}
				tmpPreSum = tmpPreSum/10;			
				if (tmpPreSum<thresholdLow){
					result[0]="true";
				}else if (tmpPreSum>(0.8*peakPower)){
					result[0]="false";
				}
				
			
				if(peakPower>(Math.round(1.5 * thresholdHigh))){
					// compare average level of 10 previous samples
					tmpPreSum = 0;			
					for (int i =0; i<10; i++){
						int position = peakPosition - 12 + i;
						if (position <0){
							position = 0;
						}
						tmpPreSum += sampleValues[position];
					}
					tmpPreSum = tmpPreSum/10;			
					if (tmpPreSum<thresholdHigh){
						result[1]="true";
					}else if (tmpPreSum>(0.8*peakPower)){
						result[1]="false";
					}
				}

				// compare average level of 10 successing samples
				long tmpPostSum = 0;
				for (int i =0; i<10; i++){
					int position = peakPosition + 2 + i;
					if (position >= sampleValues.length){
						position = sampleValues.length-1;
					}
					tmpPostSum += sampleValues[position];
				}
				tmpPostSum = tmpPostSum/10;
				if (tmpPostSum>(peakPower*0.9)){
					result[2]="true";
				} else if (tmpPostSum<(peakPower*0.75)){
					result[2]="false";
				}
				
				int distanceCounter=0;				
				int[] lastValues = new int[]{0,0,0};
								
				while(peakPosition<(sampleValues.length-1)){
					peakPosition++;
					distanceCounter++;
					//until level up					
					lastValues[2]=lastValues[1];
					lastValues[1]=lastValues[0];
					lastValues[0]=sampleValues[peakPosition-1];																									
					double past3average = ((double)(lastValues[0]+lastValues[1]+lastValues[2])/3);
					
					if((Math.abs(past3average-sampleValues[peakPosition]))<(0.02*sampleValues[peakPosition])){
						result[3]=""+distanceCounter;
						result[4]= ""+((double)(peakPower - sampleValues[peakPosition])/distanceCounter);
						break;
					}
				}	
			}		
		}	
		}catch(Exception e){
			e.printStackTrace();
			log.error(e);
		}
		for (int k=0;k<result.length;k++) {
			if (result[k] == null || result[k].contentEquals("0")){
				result[k] = "?";
			}
		}
		return result;
	}
	
	
	@Override
	public String[] getAttributeNames() {
		String[] result = new String[]{
				"directUp",
				"rapidRise",
				"keepHigh",
				"timeToNormalizedLevel",
				"slope"
			};
		return result;		
	}

	@Override
	public String[] getAttributeValueranges() {
		String[] result = new String[]{
				"{true,false}",
				"{true,false}",
				"{true,false}",
				"numeric",
				"real"
				};
		return result;
	}
	
	
}

