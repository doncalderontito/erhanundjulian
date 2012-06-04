package de.tud.kom.challenge.processors.util;

/**
 * CsvChecker kontrolliert, ob die Eingabedaten gültig sind.
 * Ungültig:
 * - Leere Datei
 * - Immer ausgeschaltetes Gerät
 * 
 * Einstellbare Parameter:
 * - _deviceOffThreshold (ab wann ist das Gerät ausgeschaltet)
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */
import java.util.Arrays;

import de.tud.kom.challenge.csvdatareader.CsvContainer;
import de.tud.kom.challenge.processors.FeatureProcessor;

public class CsvChecker {
	
	private float _deviceOffThreshold = 5;
	
	private CsvContainer _csv;
	private FeatureProcessor _fp;
	
	private boolean alwaysOff() {
		TimeSeries ts = TimeSeries.createFromListOfStringArray(_csv.getEntries(), false, 2);
		return ts.isAlwaysBelow(_deviceOffThreshold);
	}
	
	private void throwOnEmptyFile() {
		if (_csv.getEntries().size() == 0) {
			throw new IllegalArgumentException("Empty File");
		}		
	}
	
	/**
	 * Checks if CsvFile is ok.
	 * @return null if everything is ok, a String-Array filled with "?" else.
	 */
	public String[] doChecks(boolean emptyFileCheck, boolean alwaysOffCheck) {
		if (emptyFileCheck) {
			throwOnEmptyFile();
		}
		if (alwaysOffCheck && alwaysOff()) {
			int length = _fp.getAttributeNames().length;
			String[] out = new String[length];
			Arrays.fill(out, "?");
			return out;
		}
		
		return null;
	}

	
	public CsvChecker(CsvContainer csv, FeatureProcessor fp) {
		super();
		this._csv = csv;
		this._fp = fp;
	}
	
}
