package de.tud.kom.challenge.processors;

import java.util.List;

import de.tud.kom.challenge.csvdatareader.CsvContainer;

/**
 * Calculates if the value is relatively constant over the whole day
 * 
 * @author Leo Fuhr, Daniel Burgstahler 
 *
 */
public class EqualOverDayProcessor  implements FeatureProcessor{


	@Override
	public String getProcessorName() {
		return "EqualOverDayProcessor";
	}

	@Override
	public String[] processInput(CsvContainer csv) throws Exception {
		List<String[]> csvBuffer = csv.getEntries();
		String[] result = new String[2];

		if (csvBuffer != null) 
		{	
			long average = 0;
			int error = 0;
			int deltaValue = 3;
			
			for(int i=0;i<csvBuffer.size();i++){									//calculate the average value over the day:
				average += Integer.parseInt(csvBuffer.get(0)[1]);					//if the value is relatively constant over the day, then
			}																		//nearly all values must be in a delta range to this value
			average = average/csvBuffer.size();
			
			for(int i = 1; i<csvBuffer.size()&&(error<21); i++)						// a maximum of 20 meanderings are tolerated
			{
				try
				{
					if((Integer.parseInt(csvBuffer.get(i)[1])<(average-deltaValue))||(Integer.parseInt(csvBuffer.get(i)[1])>(average+deltaValue)))
					{
						error = error +1;											//every time the value is to big/small a counter is increased
					}
				}
				catch(Exception e)
				{
					throw new Exception("line "+i);		
				}
			}
			if((error>20)||(average<4))
			{
				result[0] = "false";
				result[1] = "?";
			}
			else
			{
				result[0] = "true";
				result[1] = ""+average;
			}
		}
		return result;
	}
	
	@Override
	public String[] getAttributeNames() {
		String[] result = new String[]{
				"is_constant_over_day",
				"constant_value"};
		return result;		
	}

	@Override
	public String[] getAttributeValueranges() {
		String[] result = new String[]{
				"{true,false}",
				"numeric"};		
		return result;
	}
	
}
