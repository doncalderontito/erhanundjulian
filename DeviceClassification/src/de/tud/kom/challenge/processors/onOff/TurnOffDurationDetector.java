package de.tud.kom.challenge.processors.onOff;

/**
 * Der TurnOffDurationDetector bestimmt für jeden Grenzwert wie lange das Gerät
 * bei einem "Ausschaltvorgang" braucht, um auf einen bestimmten
 * Minimalwert zu gelangen. (~> Zeit bis zum völligen Aus)
 * 
 * Einstellbare Parameter:
 * - _maxConsideredAsOff (ab wann ist das Gerät komplett ausgeschaltet)
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import de.tud.kom.challenge.processors.util.TimeSeries;
import de.tud.kom.challenge.processors.util.TimeSeriesDerivation;

public class TurnOffDurationDetector {

	private float _maxConsideredAsOff = 2;
	private int _lookBehindIntervalInSeconds = 10;
	
	private List<DeviceTurnedOnEvent> _eventsToProcess;
	private TimeSeries _ts;
	private TimeSeriesDerivation _dts;
	
	
	public void process() {
		for (DeviceTurnedOnEvent doe : _eventsToProcess) {
			processEvent(doe);
		}
	}
	
	public void processEvent(DeviceTurnedOnEvent e) {
		int thresholdOffIndex = e.getEndIndex();
		int zeroIndex = findZeroIndex(thresholdOffIndex);
		
		// What do the following lines do?
		int minIndex = findIndexOfDerivativeMin(zeroIndex);	
		int tBeginTurnOff = _ts.indexToSecondOfDay(minIndex);
		int tEndTurnOff = _ts.indexToSecondOfDay(zeroIndex);
		int duration = tEndTurnOff - tBeginTurnOff;
		duration = duration + 1 - 1;
		// End of lines to check
		
		int tBeginTurnOff2 = _ts.indexToSecondOfDay(thresholdOffIndex);
		int tEndTurnOff2 = _ts.indexToSecondOfDay(zeroIndex);
		int duration2 = tEndTurnOff2 - tBeginTurnOff2;
		
		e.setTurnOffDuration(duration2);
	}
	
	private int findZeroIndex(int thresholdOffIndex) {
		int i = thresholdOffIndex;
		
		int iUBound = _dts.size()-1;
		int iLBound = 0;
		
		while (_ts.getValueAtIndex(i) > _maxConsideredAsOff && indexIsBetweenExclusive(i, iUBound, iLBound)) {
			i++;
		}
		
		return i;
	}

	private int findIndexOfDerivativeMin(int iEndSearch) {
		int iBeginSearch = getIndexAfterMoveInTime(iEndSearch, -_lookBehindIntervalInSeconds);
		if (iBeginSearch < 0) {
			return iEndSearch;
		}
		double searchRangeExpansionThreshold = 0.3 * _lookBehindIntervalInSeconds;
		
		int tBeginToFoundMin = 0;
		int iMin = 0;
		while (tBeginToFoundMin < searchRangeExpansionThreshold) {
			iMin = findDerivativeMinBetween(iBeginSearch, iEndSearch, false);
			tBeginToFoundMin = _ts.indexToSecondOfDay(iMin) - _ts.indexToSecondOfDay(iBeginSearch);
			iBeginSearch = getIndexAfterMoveInTime(iBeginSearch, -_lookBehindIntervalInSeconds); //expand search range
			if (iBeginSearch < 0) {
				iBeginSearch = 0;
				break;
			}
		}
		
		return iMin;
	}
	
	/**
	 * @param iBegin
	 * @param iEnd
	 * @param dir true = forward search, false = search from end to begin
	 * @return
	 */
	private int findDerivativeMinBetween(int iBegin, int iEnd, boolean dir) {
		int iUBound = _dts.size()-1;
		int iLBound = 0;
		
		iBegin = Math.max(iLBound, iBegin); //ensure iBegin >= iLBound
		iEnd = Math.min(iUBound, iEnd); //ensure iEnd <= iUBound
		if (iBegin > iEnd) {
			throw new IllegalStateException("iBegin must be smaller than iEnd.");
		}
		
		int i = dir ? iBegin : iEnd;

		float min = Float.MAX_VALUE;
		int minIndex = 0;
		
		if (dir) {
			while (i <= iEnd) {
				float value = _dts.getValueAtIndex(i);
				if (value < min) {
					min = value;
					minIndex = i;
				}
				i++;
			}
		}
		else {
			while (i >= iBegin) {
				float value = _dts.getValueAtIndex(i);
				if (value < min) {
					min = value;
					minIndex = i;
				}
				i--;
			}
		}
		
		return minIndex;
	}
	
	private int getIndexAfterMoveInTime(int iBegin, int seconds) {
		
		int iUBound = _dts.size()-1;
		int iLBound = 0;
		
		int tCurrent = _ts.indexToSecondOfDay(iBegin);
		int tTarget = tCurrent + seconds;
		int direction = tTarget - tCurrent > 0 ? 1 : -1;
		
		
		int i = iBegin;
		if (direction < 0) {
			while (tCurrent > tTarget) {
				i--;
				if (i < iLBound) {
					return -1;
				}
				tCurrent = _ts.indexToSecondOfDay(i);
			}
		}
		else if (direction > 0) {
			while (tCurrent < tTarget && indexIsBetweenExclusive(i, iUBound, iLBound)) {
				i++;
				if (i > iUBound) {
					return -2;
				}
				tCurrent = _ts.indexToSecondOfDay(i);
			}
		}
		
		return i;
	}
	
	private boolean indexIsBetweenExclusive(int i, int iUBound, int iLBound) {
		if (i < iUBound && i > iLBound) {
			return true;
		}
		else return false;
	}
	

	
	public List<String> getResults() {
		ArrayList<String> result = new ArrayList<String>();
		
		SummaryStatistics stats = new SummaryStatistics();
		
		for (DeviceTurnedOnEvent doe : _eventsToProcess) {
			stats.addValue(doe.getTurnOffDuration());
		}
		
		if (String.valueOf(stats.getMean()).equals("NaN")) {
			result.add("?");
		}
		else {
			result.add(String.valueOf(stats.getMean()));
		}
		
		return result;
	}
	
	
	public TurnOffDurationDetector(List<DeviceTurnedOnEvent> events, TimeSeries ts, TimeSeriesDerivation dts) {
		_ts = ts;
		_dts = dts;
		_eventsToProcess = events;
	}
	
}
