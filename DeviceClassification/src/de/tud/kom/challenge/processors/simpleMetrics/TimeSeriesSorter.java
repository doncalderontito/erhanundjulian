package de.tud.kom.challenge.processors.simpleMetrics;

/**
 * sortiert eine TimeSeries nach aufsteigenden Werten
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */
import java.util.Arrays;

import de.tud.kom.challenge.processors.util.TimeSeries;

public class TimeSeriesSorter {
	private float[] sortedMeasurementSeries;
	
	
	public TimeSeriesSorter(TimeSeries ts) {
		sortedMeasurementSeries = ts.getValueArrayCopy();
		sort();
	}
	
	
	public float[] getSortedMeasurementSeries() {
		return this.sortedMeasurementSeries;
	}
	
	public int getSize() {
		return sortedMeasurementSeries.length;
	}
	
	public float getMax() {
		return sortedMeasurementSeries[getSize() - 1];
	}

	public float getMin() {
		return sortedMeasurementSeries[0];
	}
	

	private void sort() {
		Arrays.sort(sortedMeasurementSeries);
	}
	
}
