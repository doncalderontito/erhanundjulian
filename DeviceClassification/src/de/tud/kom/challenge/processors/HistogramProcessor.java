package de.tud.kom.challenge.processors;

/**
 * Der HistogramProcessor spaltet die Wertemenge der Eingabedaten in Teilbereiche auf.
 * Es erfolgt auch eine Teilung der zeitlichen Achse.
 * Extrahiert werden die Anzahlen der jeweiligen Unterteilungen
 * 
 * Einstellbare Parameter:
 * - _buildOptions (in der Methode setupBuildOptions())
 * - _interpretationOptions (in der Methode InterpretationOptions())
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.challenge.csvdatareader.CsvContainer;
import de.tud.kom.challenge.processors.histogram.BuildOptions;
import de.tud.kom.challenge.processors.histogram.Histogram;
import de.tud.kom.challenge.processors.histogram.HistogramAdapter;
import de.tud.kom.challenge.processors.histogram.HistogramBuilder;
import de.tud.kom.challenge.processors.histogram.InterpretationOptions;
import de.tud.kom.challenge.processors.util.CsvChecker;

public class HistogramProcessor implements FeatureProcessor {
	
	private final Logger log = Logger.getLogger(this.getClass().getSimpleName());
	
	public String getProcessorName() {
		return this.getClass().getSimpleName();
	}

	private BuildOptions _buildOptions;
	private InterpretationOptions _interpretationOptions;
	
	
	private void setupBuildOptions()  {
		try {
			_buildOptions = new BuildOptions();
		} catch (ParseException ex) {
			log.error("Could not parse time string");
		}
		//changes here
	}

	
	private void setupInterpretationOptions() {
		_interpretationOptions = new InterpretationOptions();
		
		//changes here
	}

	public HistogramProcessor() {
		
		setupBuildOptions();
		setupInterpretationOptions();
	}
	
	
	public String[] getAttributeNames() {
		int stepCount = getStepCount();
		int intervalCount = getIntervalCount();
		
		
		String[] names = new String[stepCount * intervalCount];
		
		for (int i = 0; i < intervalCount; i++) {
			for (int j = 0; j < stepCount; j++) {
				names[i*stepCount + j] = "histogram_compartment_" + String.valueOf(i+1) + "_" + String.valueOf(j+1);
			}
		}
		
		return names;
	}

	public String[] getAttributeValueranges() {
		String [] valueTypes = new String[getStepCount() * getIntervalCount()];
		
		for (int j = 0; j < valueTypes.length; j++) {
			valueTypes[j] = "numeric";
		}
		
		return valueTypes;
	}

	
	public String[] processInput(CsvContainer csv) throws Exception {
		String[] result = checkCsv(csv);
		if (result != null) {
			throw new IllegalStateException("Device is always turned off.");
		}
		
		HistogramBuilder builder = new HistogramBuilder(_buildOptions); //init foreach file, because of multi threading
		List<Histogram> h = builder.build(csv);
		
		
		String[] out = new String[0];
		for (Histogram histogram : h) {
			HistogramAdapter ha = new HistogramAdapter(histogram, _interpretationOptions);
			out = concat(out, ha.getValues());
		}
		
		return out;
	}
	
	
	private int getIntervalCount() {
		return _buildOptions.getIntervalls().size();
	}

	private int getStepCount() {
		return _buildOptions.getSteps();
	}
	
	private static <T> T[] concat(T[] first, T[] second) {
		  T[] result = Arrays.copyOf(first, first.length + second.length);
		  System.arraycopy(second, 0, result, first.length, second.length);
		  return result;
	}
	
	private String[] checkCsv(CsvContainer csv) {
		CsvChecker c = new CsvChecker(csv, this);
		String[] result = c.doChecks(true, true);
		return result;
	}
}
