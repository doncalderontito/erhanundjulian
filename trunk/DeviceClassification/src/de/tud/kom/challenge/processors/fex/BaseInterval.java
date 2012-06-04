package de.tud.kom.challenge.processors.fex;

import java.util.ArrayList;


public class BaseInterval {
	protected int length; // seconds
	protected int start; // absolute value seconds
	protected double level; // power level
	protected int readingsCount; // number of readings
	protected double powerStep; // powerstep between this and the previous interval

	public BaseInterval(double level, int start, int length, double powerStep) {
		this.level = level;
		this.start = start;
		this.length = length;
		this.powerStep = powerStep;
		readingsCount = 1;
	}

	public int getReadingCount() {
		return readingsCount;
	}

	public double getLevel() {
		return level;
	}
	
	public int getStart() {
		return start;
	}

	public int getLength() {
		return length;
	}

	public double getPowerStep() {
		return powerStep;
	}

	// end is the absolute position of the last point in time with the same value as level.
	public int getEnd() {
		return start + length - 1;
	}
	
	// calculates length of the intersection
	public int getIntersectionLengthWith(int start, int end) {
		int s = Math.max(start, getStart());
		int e = Math.min(end, getEnd());
		return Math.max(0, e - s);
	}

	public BaseInterval newCopy() {
		BaseInterval ret = new BaseInterval(level, start, length, powerStep);
		ret.readingsCount = this.readingsCount;
		return ret;
	}
	
	// returns all intervals of list that intersect with this interval
	public ArrayList<TimeInterval> getIntersectionWithList(ArrayList<TimeInterval> list) {
		ArrayList<TimeInterval> ret = new ArrayList<TimeInterval>();
		for (TimeInterval ti : list) {
			if (getIntersectionLengthWith(ti.getStart(), ti.getEnd()) > 0) {
				ret.add(ti);
			}
		}
		return ret;
	}
}
