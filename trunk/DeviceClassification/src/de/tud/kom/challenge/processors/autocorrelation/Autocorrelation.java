package de.tud.kom.challenge.processors.autocorrelation;

/**
 * In Autocorrelation wird die Autokorrelation berechnet
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

import org.apache.commons.math.stat.correlation.Covariance;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;

import de.tud.kom.challenge.processors.fourier.Spectrum;
import de.tud.kom.challenge.processors.util.TimeSeries;

public class Autocorrelation {

	private CorrelationTimeSeries cts;
	private TimeSeries ts;
	private int timeStep = 60; //interval of shifting-time //in seconds 
	private boolean pearsons = true; //!!#
	private TimeSeries shiftedTS;
	
	public Autocorrelation(TimeSeries ts) {
		this.ts = ts;
		computeAutocorrelation();
	}
	
	
	public void computeAutocorrelation() {
		ts = ts.resample(timeStep);
		cts = new CorrelationTimeSeries(ts, timeStep);
		double[] ctsValues = new double[cts.size()];
		shiftedTS = copyTS();
		
		int numberOfTimeSteps = ts.size() / 2;
		
		PearsonsCorrelation pc = new PearsonsCorrelation();
		Covariance cv = new Covariance();
		
		for (int i = 0; i < numberOfTimeSteps; i++) {
			//shift();
			shiftedTS.shift();
			
			double[] array1 = Spectrum.convertFloatsToDoubles(ts.getValue());
			double[] array2 = Spectrum.convertFloatsToDoubles(shiftedTS.getValue());
			if (pearsons) {
				ctsValues[i] = pc.correlation(array1, array2);
			}
			else {
				ctsValues[i] = cv.covariance(array1, array2);
			}
		}
		
		cts.setValues(ctsValues);
		
		//return cts;
	}
	
	
	private TimeSeries copyTS() {
		//initialize
		int valueCount = ts.size();
		String date = ts.getDate();
		boolean isUniform = ts.isUniformSampling();
		int sampleIntervalTimeInSeconds = ts.getSampleIntervalTimeInSeconds();
		
		TimeSeries newTS = TimeSeries.createEmpty(valueCount, date, isUniform, sampleIntervalTimeInSeconds, 0);
		
		newTS.setTime(ts.getTimeArrayCopy());
		newTS.setValue(ts.getValueArrayCopy());
		
		//newTS.shift();
		
		return newTS;
	}
	

	public int getTimeStep() {
		return timeStep;
	}


	public void setTimeStep(int timeStep) {
		this.timeStep = timeStep;
	}


	public CorrelationTimeSeries getCts() {
		return cts;
	}
	
}
