package de.tud.kom.challenge.processors;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.tud.kom.challenge.csvdatareader.CsvContainer;

/**
 * Check if day is on weekend 
 * 
 * @author Daniel Burgstahler
 *
 */
public class IsWeekendProcessor implements FeatureProcessor {
	
	@Override
	public String getProcessorName() {
		return "IsWeekendProcessor";
	}
	

	@SuppressWarnings("deprecation")
	@Override
	public String[] processInput(CsvContainer csv) throws Exception {
		List<String[]> csvBuffer = csv.getEntries();
		String[] result = new String[]{"?"};
		
		if (csvBuffer != null) {
			try{
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			    Date d = formatter.parse(csvBuffer.get(0)[0].substring(0, 10));
				if((d.getDay()%6)==0){
					result[0]="true";
				}
				else{
					result[0]="false";
				}
			}
			catch(Exception e){
				throw new Exception("IsWeekendProcessor");
			}
		}
		return result;
	}
	
	@Override
	public String[] getAttributeNames() {
		String[] result = new String[]{"is_weekend"};
		return result;		
	}

	@Override
	public String[] getAttributeValueranges() {
		String[] result = new String[]{"{true,false}"};		
		return result;
	}
}

