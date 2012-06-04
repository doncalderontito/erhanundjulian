package de.tud.kom.challenge.processors;

/**
 * Der OnOffCharacteristicsProcessor untersucht das Schaltverhalten der Eingabedaten.
 * Extrahiert werden folgende Merkmale:
 * - Anzahl der Einschaltvorgänge pro Grenzwert
 * - Zeitliche Dauer der Einschaltzustände pro Grenzwert
 * - Ist ein Peak beim Einschaltvorgang vorhanden?
 * 
 * Einstellbare Parameter:
 * - _thresholds
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */


import java.util.ArrayList;
import java.util.List;

import de.tud.kom.challenge.csvdatareader.CsvContainer;
import de.tud.kom.challenge.processors.onOff.PeakDetection;
import de.tud.kom.challenge.processors.onOff.ThresholdDetector;
import de.tud.kom.challenge.processors.onOff.TurnOffDurationDetector;
import de.tud.kom.challenge.processors.util.CsvChecker;
import de.tud.kom.challenge.processors.util.TimeSeries;
import de.tud.kom.challenge.processors.util.TimeSeriesDerivation;

public class OnOffCharacteristicsProcessor implements FeatureProcessor{
	
	public String getProcessorName() {
		return this.getClass().getSimpleName();
	}
	
	
	private int[] _thresholds = {5, 10, 50, 200, 500, 2000};//{5, 50, 500}; //{5, 10, 50, 200, 500, 2000}; // initial values
	
	
	public String[] getAttributeNames() {
		String[] names = new String[_thresholds.length * 3 + 1];
		
		// for each threshold: {"number_of_turned_on", "running_seconds", "running_minutes", "running_hours"};
		int a = 0;
		
		for (int threshold : _thresholds) {
			names[a] = "times_of_power_is_above_" + threshold;
			names[a+1] = "total_seconds_of_being_above_" + threshold;
			a++; a++;
		}
		names[a] = "contains_peek";
		a++;
		for (int threshold : _thresholds) {
			names[a] = "turnoff_duration_avg_for_threshold_" + threshold;
			a++;
		}
		
		
		return names;
	}

	public String[] getAttributeValueranges() {
		String[] valueTypes = new String[_thresholds.length * 3 + 1];
		
		int i;
		for (i = 0; i < valueTypes.length; i++) {
			valueTypes[i] = "numeric";
		}
		valueTypes[i-_thresholds.length-1] = "{true, false}";
				
		return valueTypes;
	}

	public String[] processInput(CsvContainer csv) throws Exception {
		String[] result = checkCsv(csv);
		if (result != null) {
			throw new IllegalArgumentException("Device is always turned off.");
		}
				
		TimeSeries ts = TimeSeries.createFromListOfStringArray(csv.getEntries(), false, 1);
		TimeSeriesDerivation dts = new TimeSeriesDerivation(ts);
		List<ThresholdDetector> detectors = createThresoldDetectors(ts);
		PeakDetection peakDetector = new PeakDetection();
		
		//compute
		for (int indexOfTime = 0; indexOfTime < ts.size(); indexOfTime++) {
			peakDetector.runPeakDetection(indexOfTime, ts);
			for (ThresholdDetector detector : detectors) {
				detector.update(indexOfTime);
			}
		}
		
		ArrayList<String> turnOffDurationsString = new ArrayList<String>();
		for (ThresholdDetector d : detectors) {
			TurnOffDurationDetector turnOffDetector = new TurnOffDurationDetector(d.getEvents(), ts, dts);
			turnOffDetector.process();
			turnOffDurationsString.addAll(turnOffDetector.getResults());
		}
		
		//fill output
		ArrayList<String> outList = new ArrayList<String>();
		outList.addAll(getThresholdDetectorResults(detectors));
		outList.add(peakDetector.getStringOfResults());
		outList.addAll(turnOffDurationsString);
		
		String[] out = new String[1];
		return outList.toArray(out);
	}

	private String[] checkCsv(CsvContainer csv) {
		CsvChecker c = new CsvChecker(csv, this);
		String[] result = c.doChecks(true, true);
		return result;
	}
	
	private List<ThresholdDetector> createThresoldDetectors(TimeSeries ts) {
		ArrayList<ThresholdDetector> detectors = new ArrayList<ThresholdDetector>();
		for (int threshold : _thresholds) {
			detectors.add(new ThresholdDetector(threshold, ts));
		}
		return detectors;
	}
	
	public ArrayList<String> getThresholdDetectorResults(List<ThresholdDetector> detectors) {
		ArrayList<String> output = new ArrayList<String>();
		
		for (ThresholdDetector d : detectors) {
			output.add(String.valueOf(d.getCount()));
			output.add(String.valueOf(d.getDuration()));
		}
		return output;
	}
}
