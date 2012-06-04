package de.tud.kom.challenge.processors.simpleMetrics;

/**
 * StatisticalMeasureValues berechnet einige statistische (simple) Werte
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */
import java.util.Date;

import org.apache.commons.math.stat.descriptive.moment.Variance;

import de.tud.kom.challenge.processors.fourier.Spectrum;
import de.tud.kom.challenge.processors.util.TimeSeries;

public class StatisticalMeasureValues {
	
	private TimeSeries ts;
	private float[] sortedSeries;
	
	public int getSize() {
		return sortedSeries.length;
	}
	
	public StatisticalMeasureValues(TimeSeries ts) {
		this.ts = ts;
		TimeSeriesSorter tss = new TimeSeriesSorter(ts);
		sortedSeries = tss.getSortedMeasurementSeries();
	}

	public double arithmeticMean() {
		int added = 0;
		for (int i = 0; i < getSize(); i++) {
			added += sortedSeries[i];
		}
		int average = added / getSize();
		return average;
	}
	
	public double quadraticMean() {
		double added = 0;
		for (int i = 0; i < getSize(); i++) {
			added += (sortedSeries[i] * sortedSeries[i]);
		}
		double quadMean = Math.sqrt(added / getSize());
		return quadMean;
	}
	
	public double midrange() {
		float max = getMax();
		float min = getMin();
		
		return ((max + min) / 2);
	}
	

	public float qQuantile(int q) {		
		if (sortedSeries.length % 2 == 0) {
			return sortedSeries[getSize() / q];
		}
		else return sortedSeries[(getSize() / q) + 1];
	}
	
	
	public float median() {
		return qQuantile(2);
	}
	
	public float range() {
		return (sortedSeries[getSize() - 1] - sortedSeries[0]);
	}
	
	public double empiricalVariance() {
		double arithmeticMean = arithmeticMean();
		double sum = 0;
		for (int i = 0; i < getSize(); i++) {
			sum += Math.pow((ts.getValueAtIndex(i) - arithmeticMean), 2);
		}
		
		return (sum / (getSize() - 1));
	}
	
	public double empiricalScattering() {
		return Math.sqrt(empiricalVariance());
	}
	
	public float quartileDistance() {
		float value1 = qQuantile(75);
		float value2 = qQuantile(25);
		return (value1 - value2);
	}
	
	public float getMax() {
		return sortedSeries[getSize() - 1];
	}
	
	public float getMin() {
		return sortedSeries[0];
	}
	
	public double variance() {
		Variance v = new Variance();
		double[] values = Spectrum.convertFloatsToDoubles(ts.getValueArrayCopy());
		
		return v.evaluate(values);		
	}

	@SuppressWarnings("deprecation")
	public int getDayOfWeek() {
		String stringDate = ts.getDate();
		int year = Integer.parseInt(stringDate.substring(0, 2));
		int month = Integer.parseInt(stringDate.substring(3, 5));
		int date = Integer.parseInt(stringDate.substring(6));
		Date myDate = new Date(year, month, date);
		return myDate.getDay(); // 0 = Sunday, 1 = Monday, 2 = Tuesday, 3 = Wednesday, 4 = Thursday, 5 = Friday, 6 = Saturday
	}
	
}
