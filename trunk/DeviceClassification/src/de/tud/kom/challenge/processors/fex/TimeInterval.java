package de.tud.kom.challenge.processors.fex;

public class TimeInterval extends BaseInterval {

	public TimeInterval(double level, int start, int length, double powerStep) {
		super(level, start, length, powerStep);
	}

	public void increaseReadings() {
		readingsCount++;
	}

	public void increase(int seconds) {
		readingsCount++;
		length += seconds;
	}

	public void setLevel(double level) {
		this.level = level;
	}

	@Override
	public TimeInterval newCopy() {
		TimeInterval ret = new TimeInterval(level, start, length, powerStep);
		ret.readingsCount = this.readingsCount;
		return ret;
	}
}