package de.tud.kom.challenge.processors.histogram;

/**
 * TimeInterval speichert ein zeitliches Intervall ab. 
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeInterval {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
	
	private Date _start;
	private Date _end;
	
	
	/**
	 * @param start date-object with time set and date-part set to 1.1.1970
	 * @param end date-object with time set and date-part set to 1.1.1970
	 */
	public TimeInterval(Date start, Date end) {
		super();
		this._start = start;
		this._end = end;
	}
	
	
	/**
	 * @param start time in format "HH:MM:SS"
	 * @param end time in format "HH:MM:SS"
	 * @throws ParseException 
	 */
	public TimeInterval(String start, String end) throws ParseException {
		_start = DATE_FORMAT.parse(start);
		_end = DATE_FORMAT.parse(end);
	}


	public Date getStart() {
		return _start;
	}


	public void setStart(Date _start) {
		this._start = _start;
	}


	public Date getEnd() {
		return _end;
	}


	public void setEnd(Date _end) {
		this._end = _end;
	}
	
	public String getFormattedStart() {
		return DATE_FORMAT.format(this._start);
	}
	
	public String getFormattedEnd() {
		return DATE_FORMAT.format(this._end);
	}
	

}
