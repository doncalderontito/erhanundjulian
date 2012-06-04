package de.tud.kom.challenge.processors.fourier;

/**
 * Spectrum speichert das Ergebnis der Fourier-Transformation ab
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

import de.tud.kom.challenge.processors.util.TimeSeries;

public class Spectrum {

	private double[] _realPart;
	private double[] _imaginaryPart;
	
	private Spectrum(SpectrumInputAdapter sia) {
		_realPart = sia.getRealPart();
		_imaginaryPart = sia.getImaginaryPart();
	}
	
	public void normalize(int maxValue) {
		if (_imaginaryPart.length == 0) ;
		
		double maxReal = maxReal();		
		for (int i = 0; i < _realPart.length; i++) {
			double value = _realPart[i];
			double normValue = value / maxReal;
			double normRescaledValue = normValue * maxValue;
			_realPart[i] = normRescaledValue;
		}
	}
	
	public double maxReal() {
		double maxReal = 0;
		for (int i = 0; i < _realPart.length; i++) {
			double value = Math.abs(_realPart[i]);
			if (value > maxReal) {
				maxReal = value;
			}
		}
		
		return maxReal;
	}
	
	public int indexOfMax() {
		int maxIndex = 0;
		double maxReal = 0;
		for (int i = 0; i < _realPart.length; i++) {
			double value = Math.abs(_realPart[i]);
			if (value > maxReal) {
				maxIndex = i;
				maxReal = value;
			}
		}
		
		return maxIndex;
	}
	
	public void cutOffSteadyComponent(int samplesToCutOff) {
		double[] newRealPart = new double[_realPart.length-samplesToCutOff];
		System.arraycopy(_realPart, samplesToCutOff, newRealPart, 0, newRealPart.length);
		_realPart = newRealPart;
	}
	
	public int bandwidth(double threshold) {
		int beginIndex = 0;
		int endIndex = 0;
		
		for (int i = 0; i < _realPart.length; i++) {
			double value = Math.abs(_realPart[i]);
			if (value > threshold) {
				beginIndex = i;
				break;
			}
		}
		
		for (int i = _realPart.length-1; i >= 0; i--) {
			double value = Math.abs(_realPart[i]);
			if (value > threshold) {
				endIndex = i;
				break;
			}
		}
		
		return endIndex - beginIndex;
	}
	
	public static Spectrum createByTransform(TimeSeries ts, SpectrumInputAdapter sia) {
		double[] input = convertFloatsToDoubles(ts.getValueArrayCopy());
		sia.transform(input);
		Spectrum s = new Spectrum(sia);
		return s;
	}
	
	public static double[] convertFloatsToDoubles(float[] input) {
	    double[] output = new double[input.length];
	    for (int i = 0; i < input.length; i++) {
	        output[i] = input[i];
	    }
	    return output;
	}
}
