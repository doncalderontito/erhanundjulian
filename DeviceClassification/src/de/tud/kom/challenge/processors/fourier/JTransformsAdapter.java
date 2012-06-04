package de.tud.kom.challenge.processors.fourier;

/**
 * JTransformsAdapter bereitet die aus der Bibliothek JTransforms gewonnenen
 * Ergebnisse der Fourier-Transformation auf und stellt sie als double-Array
 * zur Verfügung
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class JTransformsAdapter implements SpectrumInputAdapter {

	private double[] _jtOutput;
	
	public double[] getRealPart() {
		double[] realPart;
		if (_jtOutput.length % 2 == 0) {
			realPart = new double[(_jtOutput.length/2) + 1];

			for (int i = 0; i < realPart.length - 1; i++) {
				realPart[i] = _jtOutput[i*2];
			}
			realPart[realPart.length-1] = _jtOutput[1];
		} else {
			realPart = new double[(_jtOutput.length/2) - 1];
			for (int i = 1; i < realPart.length; i++) {
				realPart[i] = _jtOutput[i*2];
			}
		}
		
		return realPart;
	}

	public double[] getImaginaryPart() {
		double[] imagPart;
		if (_jtOutput.length % 2 == 0) {
			imagPart = new double[(_jtOutput.length/2) - 1];
			
			for (int i = 0; i < imagPart.length; i++) {
				imagPart[i] = _jtOutput[(i*2)+1];
			}
		} else {
			imagPart = new double[(_jtOutput.length/2) + 1];
			
			for (int i = 0; i < imagPart.length - 1; i++) {
				imagPart[i] = _jtOutput[i*2];
			}
			imagPart[imagPart.length-1] = _jtOutput[1];
		}
		
		return imagPart;
	}

	public void transform(double[] input) {
		DoubleFFT_1D fft = new DoubleFFT_1D(input.length);
		fft.realForward(input);
		_jtOutput = input;
	}
}
