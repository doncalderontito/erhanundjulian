package de.tud.kom.challenge.prediction;

public class DataEntry {

	private int value;
	private long time;
	
	public DataEntry(long time, int value) {
		this.time = time;
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public long getTime() {
		return time;
	}
}
