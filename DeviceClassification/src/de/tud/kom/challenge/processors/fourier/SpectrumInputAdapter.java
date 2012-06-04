package de.tud.kom.challenge.processors.fourier;

/**
 * SpectrumInputAdapter dient zur Abstraktion über JTransformsAdapter,
 * sodass leicht eine andere Bibliothek zur Berechnung der Fourier-
 * Transformation benutzt werden kann
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

public interface SpectrumInputAdapter {
	
	public double[] getRealPart();
	public double[] getImaginaryPart();
	public void transform(double[] input);

}
