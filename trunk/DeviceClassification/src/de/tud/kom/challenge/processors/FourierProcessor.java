package de.tud.kom.challenge.processors;

/**
 * Der FourierProcessor führt auf den Eingabedaten eine FourierAnalyse aus
 * und extrahiert folgende Daten:
 * - größte Frequenz
 * - Bandbreite mit je 5%, 15% und 25% der größten Frequenz
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

import de.tud.kom.challenge.csvdatareader.CsvContainer;
import de.tud.kom.challenge.processors.fourier.JTransformsAdapter;
import de.tud.kom.challenge.processors.fourier.Spectrum;
import de.tud.kom.challenge.processors.util.CsvChecker;
import de.tud.kom.challenge.processors.util.TimeSeries;

public class FourierProcessor implements FeatureProcessor {
	
	public String getProcessorName() {
		return this.getClass().getSimpleName();
	}

	public String[] getAttributeNames() {
		return new String[] {"maxFrequency", "bandwidth_50", "bandwidth_150", "bandwidth_250"};
	}

	public String[] getAttributeValueranges() {
		return new String[] {"numeric", "numeric", "numeric", "numeric"};
	}

	public String[] processInput(CsvContainer csv) throws Exception {
		String[] result = checkCsv(csv);
		if (result != null) {
			throw new IllegalArgumentException("Device is always turned off.");
		}
		
		TimeSeries ts = TimeSeries.createFromListOfStringArray(csv.getEntries(), false, 2);
		TimeSeries rts = ts.resample(1);
		
		Spectrum s = Spectrum.createByTransform(rts, new JTransformsAdapter());
		
		s.cutOffSteadyComponent(10);
		
		int maxIndex = s.indexOfMax();
		
		s.normalize(1000);
		int bw50 = s.bandwidth(50);
		int bw150 = s.bandwidth(150);
		int bw250 = s.bandwidth(250);
		
		return new String[] {String.valueOf(maxIndex), String.valueOf(bw50), String.valueOf(bw150), String.valueOf(bw250)};
	}
	
	
	private String[] checkCsv(CsvContainer csv) {
		CsvChecker c = new CsvChecker(csv, this);
		String[] result = c.doChecks(true, true);
		return result;
	}

}
