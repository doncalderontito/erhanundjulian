package de.tud.kom.challenge.processors.onOff;

/**
 * DeviceTurnedOnEvent speichert jene Zeiten und Werte ab, zu denen
 * das Gerät sich in einem eingeschalteten Zustand befindet.
 * Der Zustand des Eingeschaltetseins wird durch die Grenzwerte im 
 * OnOffCharacteristicsProzessor definiert.
 * 
 * @author Vany
 *
 */
public class DeviceTurnedOnEvent {

	private int _beginIndex;
	private int _endIndex;
	private int _triggeredByThreshold;
	private int _turnOffDurationInSeconds;
	
	
	

	public int getBeginIndex() {
		return _beginIndex;
	}
	
	public void setBeginIndex(int _beginIndex) {
		this._beginIndex = _beginIndex;
	}
	
	public int getEndIndex() {
		return _endIndex;
	}
	
	public void setEndIndex(int _endIndex) {
		this._endIndex = _endIndex;
	}
	
	public int getTriggeredByThreshold() {
		return _triggeredByThreshold;
	}
	
	public void setTriggeredByThreshold(int _triggeredByThreshold) {
		this._triggeredByThreshold = _triggeredByThreshold;
	}

	public int getTurnOffDuration() {
		return _turnOffDurationInSeconds;
	}

	public void setTurnOffDuration(int _turnOffDuration) {
		this._turnOffDurationInSeconds = _turnOffDuration;
	}
	
	
	public DeviceTurnedOnEvent(
			int _beginIndex,
			int _endIndex,
			int _triggeredByThreshold
			) {
		super();
		this._beginIndex = _beginIndex;
		this._endIndex = _endIndex;
		this._triggeredByThreshold = _triggeredByThreshold;
	}
	
}
