package de.tud.kom.challenge.processors.util;

/**
 * TimeSeries speichert die Eingabedaten in einem arbeitsfähigen Format ab 
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

import java.util.List;


public class TimeSeries {
	
	private int _sampleIntervalTimeInSeconds;
	private boolean _uniformSampling;

	private String _date;
	private int[] _time;
	private float[] _value;
	
	public TimeSeries resample(int newSampleTimeInterval) {
		if (!_uniformSampling) {
			return resampleNonUniform(newSampleTimeInterval);
		}
		else return null;
	}
	
	private TimeSeries resampleNonUniform(int sampleInterval) {
		int newSeconds = this.indexToSecondOfDay(this.size()-1) - this.indexToSecondOfDay(0) + 1;
		int newSize = (newSeconds / sampleInterval);
		int offsetSecondOfDay = this.indexToSecondOfDay(0);
		
		TimeSeries newTs = TimeSeries.createEmpty(newSize, getDate(), true, sampleInterval, offsetSecondOfDay);
		
		int j = 0;
		for (int i = 0; i < newSize; i++) {
			int iSecOfDay = newTs.indexToSecondOfDay(i);
			int jSecOfDay = this.indexToSecondOfDay(j);
			
			while (jSecOfDay < iSecOfDay) {
				j++;
				jSecOfDay = this.indexToSecondOfDay(j);
			}
			
			if (jSecOfDay == iSecOfDay) {
				newTs.setValueAtIndex(i, this.getValueAtIndex(j));
			}
			else if (jSecOfDay > iSecOfDay) {
				float iValue = interpolate(this, newTs, j, i, iSecOfDay);
				newTs.setValueAtIndex(i, iValue);
			}
			else throw new IllegalStateException("Something went terribly wrong.");
		}
		
		return newTs;
	}

	private float interpolate(TimeSeries oldTs, TimeSeries newTs, int j, int i, int x) {
		MovingInterpolator mi = new MovingInterpolator();
		
		int x1 = oldTs.indexToSecondOfDay(j-1); 
		int x2 = oldTs.indexToSecondOfDay(j);
		
		float y1 = oldTs.getValueAtIndex(j-1);
		float y2 = oldTs.getValueAtIndex(j);		
		
		mi.setPoint1(x1, y1);
		mi.setPoint2(x2, y2);
		
		return mi.interpolateAt(x);
	}
	
	public boolean isAlwaysBelow(float value) {
		for (int i = 0; i < _value.length; i++) {
			if (_value[i] >= value) {
				return false;
			}
		}
		return true;
	}
	
	public float[] getValue() {
		return _value;
	}
	
	public void setValue(float[] values) {
		this._value = values;
	}
	
	public int[] getTime() {
		return _time;
	}
	
	public void setTime(int[] times) {
		this._time = times;
	}
	

	
	public static final int timeToSecondOfDay(String time) {
		int hours = Integer.parseInt(time.substring(0, 2));
		int minutes = Integer.parseInt(time.substring(3, 5));
		int seconds = Integer.parseInt(time.substring(6, 8));
		int secondOfDay = hours*3600 + minutes*60 + seconds;
		return secondOfDay;
	}
	
	/**
	 * Gives time as formatted string. THIS IS SLOW!
	 * @param secondOfDay
	 * @return
	 */
	public static final String secondOfDayToTime(int secondOfDay) {
		int minuteOfDay = secondOfDay / 60;
		int hourOfDay = minuteOfDay / 60;
		int minuteOfHour = minuteOfDay - (hourOfDay * 60);
		int secondOfMinute = secondOfDay % 60;
		
		String hour = Integer.toString(hourOfDay);
		String min = Integer.toString(minuteOfHour);
		String sec = Integer.toString(secondOfMinute);
		
		StringBuilder sb = new StringBuilder();
		
		if (hourOfDay <= 9) {
			sb.append("0");
		}
		sb.append(hour);
		
		sb.append(":");
		
		if (minuteOfHour <= 9) {
			sb.append("0");
		}
		sb.append(min);
		
		sb.append(":");
		
		if (secondOfMinute <= 9) {
			sb.append("0");
		}
		sb.append(sec);
		
		return sb.toString();
	}
	
	public static int uniformIndexToSecondOfDay(int sampleIntervalTimeInSeconds, int index) {
		int secondOfDay = sampleIntervalTimeInSeconds * index;
		return secondOfDay;
	}
	
	public final int indexToSecondOfDay(int index) {
		int timeAtIndex = _time[index];
		return timeAtIndex;
	}
	
	public final int secondOfDayToIndex(int secondOfDay) {
		if (_uniformSampling) {
			double secondOfDayD = secondOfDay;
			double indexD = secondOfDayD / _sampleIntervalTimeInSeconds;
			int output = (int) indexD;
			return output;
		}
		else {
			throw new UnsupportedOperationException("Not implemented: too slow");
		}
	}


	/**
	 * 
	 * @param input
	 * @param isUniformSampled
	 * @param index selects index in stringarray of input. (should be 1 for csvbuffer)
	 * @return
	 */
	public static final TimeSeries createFromListOfStringArray(List<String[]> input, boolean isUniformSampled, int index) {
		float values[] = new float[input.size()];
		int time[] = new int[input.size()];
		String date = input.get(0)[0].substring(0, 10);
		
		for (int i = 0; i < input.size(); i++) {
			values[i] = Integer.parseInt(input.get(i)[index]);
			time[i] = TimeSeries.timeToSecondOfDay(input.get(i)[0].substring(11));
		}
		
		return new TimeSeries(1, false, date, time, values);
	}
	

	public static final TimeSeries createEmpty(int valueCount, String date, boolean isUniform, int sampleIntervalTimeInSeconds, int offsetSecondOfDay) {
		float values[] = new float[valueCount];
		int time[] = new int[valueCount];
		
		for (int i = 0; i < time.length; i++) {
			int secondOfDay = TimeSeries.uniformIndexToSecondOfDay(sampleIntervalTimeInSeconds, i) + offsetSecondOfDay;
			time[i] = secondOfDay;
		}
		
		return new TimeSeries(sampleIntervalTimeInSeconds, isUniform, date, time, values);
	}

	public TimeSeries(
			int sampleIntervalTimeInSeconds,
			boolean uniformSampling,
			String date,
			int[] time,
			float[] value
			) {
		super();
		this._sampleIntervalTimeInSeconds = sampleIntervalTimeInSeconds;
		this._uniformSampling = uniformSampling;
		this._date = date;
		this._time = time;
		this._value = value;
	}
	
	
	public final float getValueAtTime(String time) throws IllegalAccessException {
		if (!_uniformSampling) {
			throw new UnsupportedOperationException("Not Implemented:");
		}
		
		int secondOfDay = timeToSecondOfDay(time);
		int index = secondOfDayToIndex(secondOfDay);
		
		return _value[index];
	}
	
	public final float getValueAtSecondOfDay(int secondOfDay) {
		int index = secondOfDayToIndex(secondOfDay);
		
		return _value[index];
	}
	
	public final float getValueAtIndex(int index) {
		return _value[index];
	}
	
	public final void setValueAtIndex(int index, float value) {
		_value[index] = value;
	}
	
	public final int getTimeAtIndex(int index) {
		return _time[index];
	}
	
	public final float[] getValueArrayCopy() {
		float[] copy = new float[_value.length];
		System.arraycopy(_value, 0, copy, 0, _value.length);
		return copy;
	}
	
	public final int[] getTimeArrayCopy() {
		int[] copy = new int[_time.length];
		System.arraycopy(_time, 0, copy, 0, _time.length);
		return copy;
	}
	
	
	public final int size() {
		return _value.length;
	}
	
	public final String getDate() {
		return _date;
	}
	
	public final int getSampleIntervalTimeInSeconds() {
		return _sampleIntervalTimeInSeconds;
	}

	public final boolean isUniformSampling() {
		return _uniformSampling;
	}

	public void removeEqualTimeIntervals() {
		float newValue = _value[0];
		int newTime = _time[0];
		int countNewLength = 0;
		
		for (int i = 0; i < size() - 1; i++) {
			if (_time[i] != _time[i+1]) {
				_value[countNewLength] = newValue;
				_time[countNewLength] = newTime;
				
				newValue = _value[i+1];
				newTime = _time[i+1];
				countNewLength++;
			}
			else {
				// average
				newValue = (newValue + _value[i+1]) / 2;
			}
			
		}
		
		float[] copy = new float[countNewLength];
		System.arraycopy(_value, 0, copy, 0, countNewLength);
		_value = copy;
		
		int[] copy2 = new int[countNewLength];
		System.arraycopy(_time, 0, copy2, 0, countNewLength);
		_time = copy2;
		
	}
	
	/*public TimeSeries copyTimeSeries() {
		// get parameters to copy this TimeSeries
		int sampleIntervalTimeInSeconds = getSampleIntervalTimeInSeconds();
		boolean isUniform = isUniformSampling();
		String date = getDate();
		
		int[] time = new int[size()];
		System.arraycopy(getTimeArrayCopy(), 0, time, 0, size());
		
		float[] value = new float[size()];
		System.arraycopy(getValueArrayCopy(), 0, value, 0, size());
		
		return new TimeSeries(sampleIntervalTimeInSeconds, isUniform, date, time, value);
	}*/
	
	/**
	 * shifts the TimeSeries for one timestep to the left
	 */
	public void shift() {
		float firstValue = _value[0];
		int firstTime = _time[0];
		
		for (int i = 0; i < _value.length - 1; i++) {
			_value[i] = _value[i + 1];
			
			_time[i] = _time[i + 1];
		}
		_value[_value.length - 1] = firstValue;
		_time[_time.length - 1] = firstTime;
	}
	
}
