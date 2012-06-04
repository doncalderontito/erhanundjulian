package de.tud.kom.challenge.processors.autocorrelation;

/**
 * In CorrelationTimeSeries wird das Ergebnis der Autokorrelation abgespeichert
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

import java.util.ArrayList;

import de.tud.kom.challenge.processors.util.TimeSeries;

public class CorrelationTimeSeries {

	private double[] values;
	private int[] timeIntervals; //in seconds
	private int startTime;
	private int endTime; //half a day
	private int size;
	private int timeStep; //scale of time-Axis //in seconds
	private ArrayList<Double> maxMinValues = new ArrayList<Double>();
	private ArrayList<Integer> maxMinTimes = new ArrayList<Integer>();
	
	public ArrayList<Double> getMaxMinValues() {
		return maxMinValues;
	}
	
	public ArrayList<Integer> getMaxMinTimes() {
		return maxMinTimes;
	}
	
	public CorrelationTimeSeries(TimeSeries ts, int timeStep) {
		//this.ts = ts;
		this.timeStep = timeStep;
		startTime = ts.getTimeAtIndex(0);
		endTime = ts.getTimeAtIndex(ts.size() - 1);
		size = ts.size() / 2;
		fillTimeIntervals();
		values = new double[timeIntervals.length];
	}
		
	public double getValueOfTime(int time) {
		//time should be a multiple of timeStep
		return values[Math.round(time / timeStep)];
	}
	
	
	/**
	 * @return float-Array {maximum, timeOfMax}. First parameter is the max value of this TimeSeries.
	 * Second parameter is the time position of this max value
	 */
	public double[] getMaximumValueAndTime() {
		double maximum = 0;
		double timeOfMax = 0;
		
		for (int i = 0; i < values.length; i++) {
			if (values[i] >= maximum) {
				maximum = values[i];
				timeOfMax = i * timeStep;
			}
		}
		
		double[] out = {maximum, timeOfMax};
		return out;
	}
	
	public int size() {
		return timeIntervals.length;
	}
	
	public int getTimeDuration() {
		return (endTime - startTime);
	}
	
	public double[] getValues() {
		return values;
	}


	public void setValues(double[] values) {
		this.values = values;
	}


	public int getStartTime() {
		return startTime;
	}


	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}


	public int getEndTime() {
		return endTime;
	}


	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}


	
	private void fillTimeIntervals() {
		timeIntervals = new int[size];
		
		int tempTime = startTime;
		for (int i = 0; i < timeIntervals.length; i++) {
			timeIntervals[i] = tempTime;
			tempTime += timeStep;
		}
	}

	

	public void getMaxAndMinValues() {
		boolean increasing = false;
		
		for (int i = 0; i < size() - 1; i++) {
			if (!increasing && values[i] < values[i + 1]) {
				increasing = true;
				maxMinValues.add(values[i]);
				maxMinTimes.add(timeIntervals[i]);
			}
			if (increasing && values[i] > values[i + 1]) {
				increasing = false;
				maxMinValues.add(values[i]);
				maxMinTimes.add(timeIntervals[i]);
			}
			
			
		}
		
	}

	public double[] getMaxOfMaxMin() {
		double[] out = new double[2];
		
		double maxValue = maxMinValues.get(1);
		int maxTime = maxMinTimes.get(1);
		
		for (int i = 0; i < maxMinValues.size(); i++) {
			if (maxMinValues.get(i) > maxValue) {
				maxValue = maxMinValues.get(i);
				maxTime = maxMinTimes.get(i);
			}
		}
		
		out[0] = maxValue;
		out[1] = maxTime;
		
		return out;
	}
	
	
	public double[] getMinOfMaxMin() {
		double[] out = new double[2];
		
		double minValue = maxMinValues.get(0);
		int minTime = maxMinTimes.get(0);
		
		//except the first element
		for (int i = 2; i < maxMinValues.size(); i++) {
			if (maxMinValues.get(i) < minValue) {
				minValue = maxMinValues.get(i);
				minTime = maxMinTimes.get(i);
			}
		}
		
		out[0] = minValue;
		out[1] = minTime;
		
		return out;
	}
	
	
}
