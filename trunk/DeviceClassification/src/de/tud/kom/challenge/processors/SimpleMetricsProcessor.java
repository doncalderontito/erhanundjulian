package de.tud.kom.challenge.processors;

/**
 * Der SimpleMetricsProcessor extrahiert folgende simple Metriken:
 * - Varianz
 * - Median
 * - Maximalwert
 * - Minimalwert
 * 
 * Einstellbare Parameter:
 * => siehe verfügbare Methoden in Klasse StatisticalMeasureValues
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

import de.tud.kom.challenge.csvdatareader.CsvContainer;
import de.tud.kom.challenge.processors.simpleMetrics.StatisticalMeasureValues;
import de.tud.kom.challenge.processors.util.CsvChecker;
import de.tud.kom.challenge.processors.util.TimeSeries;

public class SimpleMetricsProcessor implements FeatureProcessor{
	
	public String getProcessorName() {
		return this.getClass().getSimpleName();
	}
	

	public String[] getAttributeNames() {
		String[] out = {
				"variance", 
				//"arithmetic_mean", 
				//"quadratic_mean", 
				//"midrange", 
				//"empirical_variance", 
				//"empirical_scattering", 
				"SMPmedian", 
				//"quartile_distance", 
				"maximum_value", 
				"minimum_value", 
				//"day_of_the_week"
		};
		
		return out;
	}

	public String[] getAttributeValueranges() {
		String[] out = new String[4];
		
		for (int i = 0; i < out.length; i++) {
			out[i] = "numeric";
		}
		
		return out;
	}

	public String[] processInput(CsvContainer csv) throws Exception {
		String[] out = new String[4];
		
		String[] result = checkCsv(csv);
		if (result != null) {
			return null;
		}
		
		if (csv.getEntries().size() == 0) {
			throw new IllegalArgumentException("Empty File");
		}
		
		TimeSeries ts = TimeSeries.createFromListOfStringArray(csv.getEntries(), false, 1);
		StatisticalMeasureValues smv = new StatisticalMeasureValues(ts);
		
		out[0] = String.valueOf(smv.variance());
		//out[0] = String.valueOf(smv.arithmeticMean());
		//out[2] = String.valueOf(smv.quadraticMean());
		//out[1] = String.valueOf(smv.midrange());
		//out[1] = String.valueOf(smv.empiricalVariance());
		//out[1] = String.valueOf(smv.empiricalScattering());
		out[1] = String.valueOf(smv.median());
		//out[2] = String.valueOf(smv.quartileDistance());
		out[2] = String.valueOf(smv.getMax());
		out[3] = String.valueOf(smv.getMin());
		//out[3] = String.valueOf(smv.getDayOfWeek());
		
		
		return out;
	}

	private String[] checkCsv(CsvContainer csv) {
		CsvChecker c = new CsvChecker(csv, this);
		String[] result = c.doChecks(true, true);
		return result;
	}

	
	
}
