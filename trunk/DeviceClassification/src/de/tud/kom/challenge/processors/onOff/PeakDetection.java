package de.tud.kom.challenge.processors.onOff;

/**
 * In PeakDetection wird überprüft, ob bei einem Einschaltvorgang
 * ein Peak stattgefunden hat.
 * 
 * Einstellbare Parameter:
 * - peakThreshold (ab welchem Ausschlag ist es ein Peak?)
 * - onOffThreshold (Grenze für den Einschaltvorgang)
 * - durationOfPeak (Zeitdauer, in der nach einem Peak gesucht wird)
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */
import de.tud.kom.challenge.processors.util.TimeSeries;

public class PeakDetection {

	private double peakThreshold = 2.3;
	private int onOffThreshold = 20;
	private TimeSeries ts; 
	private boolean peakDetected = false;
	private int durationOfPeek = 10; // in seconds
	boolean turnedOn = false;

	
	public boolean isPeakDetected() {
		return peakDetected;
	}
	
	public int getDurationOfPeek() {
		return durationOfPeek;
	}

	public void setDurationOfPeek(int durationOfPeek) {
		this.durationOfPeek = durationOfPeek;
	}
	
	public double getPeakThreshold() {
		return peakThreshold;
	}
	
	public void setPeakThreshold(double peakThreshold) {
		this.peakThreshold = peakThreshold;
	}
	
	public int getOnOffThreshold() {
		return onOffThreshold;
	}
	
	public void setOnOffThreshold(int onOffThreshold) {
		this.onOffThreshold = onOffThreshold;
	}
	

	
	public void runPeakDetection(int index, TimeSeries ts) {
		this.ts = ts;
		
		if ((ts.getValueAtIndex(index) >= onOffThreshold) && !turnedOn) {
			// if peak has already been detected, ignore the new peakDetection
			peakDetected = peakDetected ? true : peakDetection(index);
			turnedOn = true;
		}
		else if (ts.getValueAtIndex(index) < onOffThreshold)
			turnedOn = false;
	}
	
	public String getStringOfResults() {
		return String.valueOf(isPeakDetected());
	}
		
	
	
	private boolean peakDetection(int startIndex) {
		int currentIndex = startIndex;
		int indexOfHighestValue = startIndex;
		
		while (ts.indexToSecondOfDay(currentIndex) < ts.indexToSecondOfDay(startIndex) + durationOfPeek) {
			if (ts.getValueAtIndex(currentIndex) >  ts.getValueAtIndex(indexOfHighestValue)) {
				indexOfHighestValue = currentIndex;
			}
			currentIndex++;
		}
		
		if (ts.getValueAtIndex(currentIndex) * peakThreshold <= ts.getValueAtIndex(indexOfHighestValue))
			return true;
		else return false;
	}
	
}
