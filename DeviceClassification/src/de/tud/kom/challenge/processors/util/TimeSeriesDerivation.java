package de.tud.kom.challenge.processors.util;

/**
 * TimeSeriesDerivation berechnet die Ableitung einer TimeSeries
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

public class TimeSeriesDerivation {

	private TimeSeries ts;
	private TimeSeries derivatedTimeSeries;

	public TimeSeriesDerivation(TimeSeries ts) {
		ts.removeEqualTimeIntervals();
		this.ts = ts;
		createEmptyDerivatedTimeSeriesFromTS();
		calculateAllDerivatives();
	}
	
	public float getValueAtIndex(int i) {
		return derivatedTimeSeries.getValueAtIndex(i);
	}
	
	public int size() {
		return derivatedTimeSeries.size();
	}
	
	private void calculateAllDerivatives() {
		for (int i = 0; i < derivatedTimeSeries.size(); i++) {
			int intervalStep = getIntervalStep(i); //delta_x
			float valueStep = getValueStep(i); //delta_y
			
			if (intervalStep == 0)
				throw new UnsupportedOperationException("Timeintervals must be greater than zero");
			
			float derivative = valueStep / intervalStep;
			derivatedTimeSeries.setValueAtIndex(i, derivative);
		}
	}
	
	
	private int getIntervalStep(int index) {
		if (derivatedTimeSeries.isUniformSampling())
			return derivatedTimeSeries.getSampleIntervalTimeInSeconds();
		else {
			int time = ts.getTimeAtIndex(index + 1) - ts.getTimeAtIndex(index);
			return Math.abs(time);
		}
	}
	
	private float getValueStep(int index) {
		return (ts.getValueAtIndex(index + 1) - ts.getValueAtIndex(index));
	}
	
	private void createEmptyDerivatedTimeSeriesFromTS() {
		// get parameters to copy ts to derivatedTimeSeries
		int sampleIntervalTimeInSeconds = ts.getSampleIntervalTimeInSeconds();
		boolean isUniform = ts.isUniformSampling();
		String date = ts.getDate();
		
		int[] time = new int[ts.size() - 1];
		System.arraycopy(ts.getTimeArrayCopy(), 0, time, 0, ts.size() - 1);
		
		float[] value = new float[ts.size() - 1];
		//Arrays.fill(value, 0);
		
		derivatedTimeSeries = new TimeSeries(sampleIntervalTimeInSeconds, isUniform, date, time, value);
	}
	
}
