package de.tud.kom.challenge.processors.histogram;

/**
 * HistogramAdapter bietet für den HistogramProcessor die nötigen Schnittstellen,
 * um die Anzahl der Elemente pro Compartment auswerten zu können
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

public class HistogramAdapter {
		
	private Histogram _histogram;
	private InterpretationOptions _interpretationOptions;
	
	
	public String[] getValues() {
		return countsAsString(getCounts());
	}
	
	private int[] getCounts() {
		int[] values = new int[_histogram.getBuildOptions().getSteps()];
		int i = 0;
		
		for (Compartment c : _histogram.getAllCompartments()) {
			values[i] = c.getCount();
			i++;
		}
		
		if (_interpretationOptions.isNormalize()) {
			normalize(values);
		}
		
		return values;
	}
	
	private int[] normalize(int[] counts) {
		long allCount = 0;
		for (int i : counts) {
			allCount += i;
		}
		
		for (int i = 0; i < counts.length; i++) {
			double count = counts[i];
			double normalizedCount = count / allCount;
			double normalizedRebasedCount =  _interpretationOptions.getNormalizationBase() * normalizedCount;
			counts[i] = (int)normalizedRebasedCount;
		}
		
		return counts;
	}
	
	
	private String[] countsAsString(int[] counts) {
		String[] values = new String[counts.length];
		
		for (int i = 0; i < values.length; i++) {
			values[i] = String.valueOf(counts[i]);
		}
		
		return values;
	}
	
	public HistogramAdapter(Histogram h, InterpretationOptions io) {
		this._histogram = h;
		this._interpretationOptions = io;
	}
}
