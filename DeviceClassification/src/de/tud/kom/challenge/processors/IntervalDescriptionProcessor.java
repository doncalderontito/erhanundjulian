package de.tud.kom.challenge.processors;

import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.challenge.csvdatareader.CsvContainer;

/**
 * Checks if there are activity intervals and tries to extract a description of the interval 
 * 
 * @author Daniel Burgstahler
 *
 */
public class IntervalDescriptionProcessor implements FeatureProcessor {
	
	private final static Logger log = Logger.getLogger(PeakSlopeProcessor.class.getSimpleName());
	
	@Override
	public String getProcessorName() {
		return "IntervalDescriptionProcessor";
	}
	
	@Override
	public String[] processInput(CsvContainer csv) throws Exception {
		List<String[]> csvBuffer = csv.getEntries();
		String[] result = new String[2];
		
		int time = 0, lastTimeOnTime = 0, lastTime=0;
		boolean lastWasOn = false;
		long powerTimecount = 0, totalTimeCount=0;
		double averagePowerSum2 = 0;
		
		try{
			int csvBufferSize = csvBuffer.size();
			int[] sampleValues = new int[csvBuffer.size()];
			for (int i=0; i<csvBufferSize; i++) {
				int hour,minute,second, watt, lastWatt=0;;
						
				try{																						//get time
					hour = Integer.valueOf(csvBuffer.get(i)[0].substring(11, 13));
					minute = Integer.valueOf(csvBuffer.get(i)[0].substring(14, 16));
					second = Integer.valueOf(csvBuffer.get(i)[0].substring(17, 19));
					watt = Integer.parseInt(csvBuffer.get(i)[1]);
				} catch (NumberFormatException ex) {
					throw new Exception("line "+i);
				}
				time = second + (minute * 60) + (hour * 3600);
				totalTimeCount += (time-lastTime);				
				
				if (watt > 3){																				//calculate position of strongest peak
					
					if(time>lastTimeOnTime){
						int timeDiff = time-lastTimeOnTime;
						int lastMeasureWeight = 21-timeDiff; 
						if (lastMeasureWeight<1){
							lastMeasureWeight=1;
						}
						
						sampleValues[i]=watt;
						
						if (lastWasOn){				
							int lowerValue=0, upperValue=0;													//interpolate sum of power between last and current sample
							if (lastWatt>watt){
								upperValue = lastWatt;
								lowerValue = watt;										
							}else{
								upperValue = watt;
								lowerValue = lastWatt;							
							}																				//interpolate sum of power between last and current sample
							double tmpNewSum = (timeDiff * lowerValue) 								
															+ (0.5 * timeDiff * (upperValue-lowerValue)); 	//calculate rectangle and triangle
							averagePowerSum2 += tmpNewSum;
							powerTimecount += timeDiff;
						}else{
							averagePowerSum2 += watt;
							powerTimecount++;
						}	
					}					
					lastWatt = watt;
					lastTimeOnTime = time;
					lastWasOn = true;
				}else{
					lastWasOn = false;
				}
				lastTime=time;
			}
			
			long threshold = Math.round(averagePowerSum2 /powerTimecount);									//thresholdHigh = average power level while device is running
			
			boolean isInterval;
			int intervalCount=0;
			if(sampleValues[0]<threshold){
				isInterval=false;				
			}else{
				isInterval=true;
				intervalCount++;
			}
			
			for (int i=0;i<sampleValues.length;i++){			//for all samples
				if(isInterval){									//if we are in an interval
					if((sampleValues[i]<threshold)){			//and the value is lower the threshold
						isInterval=false;						//then the interval seems to be finish
					}
				}else{											//if we are not in an interval
					if((sampleValues[i]>threshold)){			//and the value is higher the threshold
						isInterval=true;						//then it seems to be a new interval
						intervalCount++;
					}
				}	
			}
						
			result[0]=""+intervalCount;
			
			long offTime = (totalTimeCount-powerTimecount);
			@SuppressWarnings("unused")
			double averageIntervalDuration;
			if (offTime>0){
				result[1]="" + (double) Math.round(((double)powerTimecount/(double)offTime)*100)/100;
				
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
				"intervalCount",
				"on_off_proportion"
				};
		return result;		
	}

	@Override
	public String[] getAttributeValueranges() {
		String[] result = new String[]{
				"numeric",
				"real"
				};
		return result;
	}


	
}