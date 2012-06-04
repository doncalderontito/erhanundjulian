package de.tud.kom.challenge.prediction.processors;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.challenge.csvdatareader.DateTime;
import de.tud.kom.challenge.prediction.DataContainer;
import de.tud.kom.challenge.prediction.DataEntry;
import de.tud.kom.challenge.prediction.PredictionFeature;

public class SimpleTimeProcessor implements PredictionProcessor {

	private final Logger log = Logger.getLogger(this.getClass().getSimpleName());
	private static final String[] days = new String[]{"","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
	private static final String type1 = "HourOfCollection";
	private static final String type2 = "WeekdayOfCollection";
	
	public void setCompleteData(DataContainer data) {
		// stateless processor
	}

	public int numResults() {
		return 1;
	}
	
	public Vector<PredictionFeature> addValueToModel(DataEntry input) {
		Vector<PredictionFeature> features = new Vector<PredictionFeature>();
		
		DateTime time = DateTime.fromLong(input.getTime());
		int hour = time.getHour();
		features.add(new PredictionFeature(type1,""+hour));	
		
		int day = time.getDay();
		int month = time.getMonth();
		int year = time.getYear();
		
		Calendar cal = new GregorianCalendar(year, month-1, day);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		log.debug("Trace was collected on a " +days[dayOfWeek]);
		features.add(new PredictionFeature(type2,days[dayOfWeek]));	
		
		return features;
	}
	
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	public String[] getResultTypes() {
		return new String[]{type1,type2};
	}

	public String[] getResultRanges() {
		return new String[]{"numeric","{Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday}"};
	}
	

}
