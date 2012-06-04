package de.tud.kom.challenge.processors.histogram;

/**
 * In BuildOptions werden alle Einstellungen gespeichert, die mit der
 * Erstellung der Compartments (= den Histogramm-Unterteilungen) zu tun haben.
 * 
 * Einstellbare Parameter:
 * - _steps (gibt an, in wieviele Compartments unterteilt werden soll)
 * - _usePerInstanceMaximum (true, wenn _maxAbsolutePower als Maximalwert genutzt werden soll)
 * - _maxAbsolutePower (höchster definierter Maximalwert, der für die Unterteilungen berücksichtigt wird)
 * - _buildStrategy (Algorithmus der Unterteilung)
 * - _intervalls (einstellbar im Konstruktor)
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class BuildOptions {
	
	//Defaults
	private int _steps = 18;
	private boolean _usePerInstanceMaximum = false;
	private int _maxAbsolutePower = 3600;
	//private BuildCompartmentsStrategy _buildStrategy = new LinearCompartmentsBuild(this);
	private BuildCompartmentsStrategy _buildStrategy = new LogarithmicCompartmentsBuild(this);
	private List<TimeInterval> _intervalls; //(default: see Constructor)
	
		
	public BuildOptions() throws ParseException {
		_intervalls = createDayNightInterval();
	}
	
	/**
	 * How the sizes of the single compartments are determined (default=linear)
	 * @return
	 */
	public BuildCompartmentsStrategy getBuildStrategy() {
		return _buildStrategy;
	}

	public void setBuildStrategy(BuildCompartmentsStrategy _buildStrategy) {
		this._buildStrategy = _buildStrategy;
	}

	
	/**
	 * Of how many compartments should the histogram consist (default=20)
	 * @return
	 */
	public int getSteps() {
		return _steps;
	}

	public void setSteps(int _steps) {
		this._steps = _steps;
	}

	
	/**
	 * Should the Maximum be determined for each instance seperately or should a global Maximum be used (set by MaxAbsolutePower, default=false)
	 * @return
	 */
	public boolean isUsePerInstanceMaximum() {
		return _usePerInstanceMaximum;
	}

	public void setUsePerInstanceMaximum(boolean _usePerInstanceMaximum) {
		this._usePerInstanceMaximum = _usePerInstanceMaximum;
	}
	

	/**
	 * If the same global maximum should be used (UsePerInstanceMaximum=false), this returns the value of this maximum (default=3600)
	 * @return
	 */
	public int getMaxAbsolutePower() {
		return _maxAbsolutePower;
	}

	public void setMaxAbsolutePower(int _maxAbsolutePower) {
		this._maxAbsolutePower = _maxAbsolutePower;
	}

	
	/**
	 * List of intervals, for which histograms are generated
	 * @return
	 */
	public List<TimeInterval> getIntervalls() {
		return _intervalls;
	}

	public void setIntervalls(List<TimeInterval> _intervalls) {
		this._intervalls = _intervalls;
	}


	
	@SuppressWarnings("unused")
	private List<TimeInterval> createWholeDayInterval() throws ParseException {
		ArrayList<TimeInterval> l = new ArrayList<TimeInterval>();
		l.add(new TimeInterval("00:00:00", "23:59:59"));
		return l;
	}
	
	/*private List<TimeInterval> createQuarterDayInterval() throws ParseException {
		ArrayList<TimeInterval> l = new ArrayList<TimeInterval>();
		l.add(new TimeInterval("00:00:00", "05:59:59"));
		l.add(new TimeInterval("06:00:00", "11:59:59"));
		l.add(new TimeInterval("12:00:00", "17:59:59"));
		l.add(new TimeInterval("18:00:00", "23:59:59"));
		return l;
	}*/
	
	private List<TimeInterval> createDayNightInterval() throws ParseException {
		ArrayList<TimeInterval> l = new ArrayList<TimeInterval>();
		l.add(new TimeInterval("00:00:00", "05:29:59"));
		l.add(new TimeInterval("05:30:00", "17:29:59"));
		l.add(new TimeInterval("17:30:00", "23:59:59"));
		return l;
	}
}
