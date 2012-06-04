package de.tud.kom.challenge.processors;

import java.util.List;

import de.tud.kom.challenge.csvdatareader.CsvContainer;
/**
 * Extracts a debounced peak level
 * 
 * @author Daniel Burgstahler
 *
 * Edit: Hristo Chonov
 */
public class PeakLevelProcessor implements FeatureProcessor {

	
	@Override
	public String getProcessorName() {
		return getClass().getSimpleName();
	}
	
	@Override
	public String[] processInput(CsvContainer csv) throws Exception {
		List<String[]> csvBuffer = csv.getEntries();
		String[] result = new String[]{"?"};
		
		int time = 0, lastTime = 0;
		double tmpValue = 0;
		long peakPower = 0;

		
		if (csvBuffer != null) {
			int csvBufferSize = csvBuffer.size();			
			for (int i=0; i<csvBufferSize; i++) {
				int hour,minute,second, watt;
				try{
					hour = Integer.valueOf(csvBuffer.get(i)[0].substring(11, 13));
					minute = Integer.valueOf(csvBuffer.get(i)[0].substring(14, 16));
					second = Integer.valueOf(csvBuffer.get(i)[0].substring(17, 19));
					watt = Integer.parseInt(csvBuffer.get(i)[1]);
				} catch (NumberFormatException ex) {
					throw new Exception("line "+i);
				}
				
				if (watt > 3){
					time = second + (minute * 60) + (hour * 3600);
					if(time>lastTime){
						int timeDiff = time-lastTime;
						//use a kind of lowpass to debounce the signal
						//dependent of time difference
						int lastMeasureWeight = 21-timeDiff; 
						if (lastMeasureWeight<1){
							lastMeasureWeight=1;
						}
						tmpValue = ((double)lastMeasureWeight/100) * tmpValue 
									+ ((double)(100-lastMeasureWeight)/100) * watt; 
						
						if (peakPower<tmpValue){
							peakPower=Math.round(tmpValue);
						}
					}
					lastTime = time;
				}
			}
		}
		
		result[0]= ""+peakPower;		
		for (int k=0;k<result.length;k++) {
			if (result[k] == null || result[k].contentEquals("0")){
				result[k] = "?";
			}
		}
		return result;
	}
	
	@Override
	public String[] getAttributeNames() {
		String[] result = new String[]{"peak_level"};
		return result;		
	}

	@Override
	public String[] getAttributeValueranges() {
		String[] result = new String[]{"numeric"};		
		return result;
	}
}

