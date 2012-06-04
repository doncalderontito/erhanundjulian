package de.tud.kom.challenge.processors.fourier;

/**
 * SpectrumInputAdapter dient zur Abstraktion �ber JTransformsAdapter,
 * sodass leicht eine andere Bibliothek zur Berechnung der Fourier-
 * Transformation benutzt werden kann
 * 
 * @author Felix R�ttiger
 * @author Vanessa W�hrl
 */

public interface SpectrumInputAdapter {
	
	public double[] getRealPart();
	public double[] getImaginaryPart();
	public void transform(double[] input);

}
