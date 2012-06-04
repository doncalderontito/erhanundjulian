package de.tud.kom.challenge.processors.onOff;

/**
 * In ThresholdDetector kann ermittelt werden, wann und wie lange
 * ein Einschaltvorgang stattgefunden hat.
 * Dazu muss der OnOffCharacteristicsProcessor über die Eingabedaten iterieren
 * und sie mit der Methode update an den ThresholdDetector weiterreichen.
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

import java.util.ArrayList;
import java.util.List;

import de.tud.kom.challenge.processors.util.TimeSeries;

public class ThresholdDetector {
	
	static final int RISE_ABOVE = 0;
	static final int FALL_BELOW = 1;
	static final int NO_CHANGE = 2;
	
	private int _threshold;
	private TimeSeries _ts;
	private boolean _state;
	private boolean _previousState;
	private List<DeviceTurnedOnEvent> _events = new ArrayList<DeviceTurnedOnEvent>();
	private DeviceTurnedOnEvent _pendingEvent;
	
	public ThresholdDetector(int threshold, TimeSeries ts) {
		super();
		this._threshold = threshold;
		_ts = ts;
	}
	
	public void update(int index) {
		updateState(_ts.getValueAtIndex(index));
		
		if (stateChanged() == RISE_ABOVE) {
			riseAboveThreshold(index);
		}
		else if (stateChanged() == FALL_BELOW) {
			fallBelowThreshold(index);
		}
	}
	
	private void updateState(float value) {
		_previousState = _state;
		if (value > _threshold) {
			if (!_state) {
				_state = true;
			}
		}
		else {
			if (_state) {
				_state = false;
			}
		}
	}
	
	private int stateChanged() {
		if (_previousState && !_state) {
			return FALL_BELOW;
		}
		else if (!_previousState && _state) {
			return RISE_ABOVE;
		}
		else {
			return NO_CHANGE;
		}
	}
	
	private void riseAboveThreshold(int index) {
		DeviceTurnedOnEvent e = new DeviceTurnedOnEvent(index, Integer.MAX_VALUE, _threshold);
		_pendingEvent = e;
	}
	
	private void fallBelowThreshold(int index) {
		_pendingEvent.setEndIndex(index);
		_events.add(_pendingEvent);
		_pendingEvent = null;
	}
	
	
	
	public int getCount() {
		return _events.size();
	}
	
	public int getDuration() {
		int duration = 0;
		for (DeviceTurnedOnEvent doe : _events) {
			duration += _ts.indexToSecondOfDay(doe.getEndIndex()) - _ts.indexToSecondOfDay(doe.getBeginIndex());
		}
		return duration;
	}
	
	public boolean isAboveThreshold() {
		return _state;
	}
	
	public List<DeviceTurnedOnEvent> getEvents() {
		return _events;
	}

}
