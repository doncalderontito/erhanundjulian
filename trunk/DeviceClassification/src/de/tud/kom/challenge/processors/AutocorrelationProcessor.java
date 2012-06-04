package de.tud.kom.challenge.processors;

/**
 * Der AutocorrelationProcessor führt auf den Eingabedaten eine Autokorrelation aus und extrahiert
 * folgende Merkmale aus dem Ergebnisgraphen:
 * - Die Anzahl der Wendepunkte
 * - Die beiden Werte des ersten Minimums
 * - Die jeweiligen beiden Werte des lokalen Minimums und Maximums im gesamten Bereich nach dem ersten Minimum
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

import java.util.ArrayList;

import de.tud.kom.challenge.csvdatareader.CsvContainer;
import de.tud.kom.challenge.processors.autocorrelation.Autocorrelation;
import de.tud.kom.challenge.processors.util.CsvChecker;
import de.tud.kom.challenge.processors.util.TimeSeries;

public class AutocorrelationProcessor implements FeatureProcessor{
	
	public String getProcessorName() {
		return this.getClass().getSimpleName();
	}

	public String[] getAttributeNames() {
		String[] out = {
				"number_of_autocorrelation_turnpoints",
				"first_min_autocorrelation_value",
				"first_min_autocorrelation_time",
				"max_autocorrelation_value",
				"max_autocorreltation_time",
				"min_autocorrelation_value",
				"min_autocorrelation_time"
				};
		return out;
	}

	public String[] getAttributeValueranges() {
		String[] out = {
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric"
				};
		return out;
	}

	public String[] processInput(CsvContainer csv) throws Exception {
		String[] out = new String[7];
		
		String[] result = checkCsv(csv);
		if (result != null) {
			throw new IllegalArgumentException("Device is always turned off.");
		}
		
		TimeSeries ts = TimeSeries.createFromListOfStringArray(csv.getEntries(), false, 1);
		Autocorrelation autocorrelation = new Autocorrelation(ts);
	
		autocorrelation.getCts().getMaxAndMinValues();
		ArrayList<Double> values = autocorrelation.getCts().getMaxMinValues();
		ArrayList<Integer> times = autocorrelation.getCts().getMaxMinTimes();
		
		out[0] = String.valueOf(values.size());
		out[1] = String.valueOf(values.get(0));
		out[2] = String.valueOf(times.get(0));
		out[3] = String.valueOf(autocorrelation.getCts().getMaxOfMaxMin()[0]);
		out[4] = String.valueOf(autocorrelation.getCts().getMaxOfMaxMin()[1]);
		out[5] = String.valueOf(autocorrelation.getCts().getMinOfMaxMin()[0]);
		out[6] = String.valueOf(autocorrelation.getCts().getMinOfMaxMin()[1]);
		
		return out;
	}

	
	private String[] checkCsv(CsvContainer csv) {
		CsvChecker c = new CsvChecker(csv, this);
		String[] result = c.doChecks(true, true);
		return result;
	}
	
}
