package de.tud.kom.challenge.csvdatareader;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 */
public class LineData {
	
	private final DateTime date;
	private int durationInSec = 1;
	private final int consumption;
	private final int consumptionSmoothed;
	
	public LineData(final DateTime date, final int consumption, final int consumptionSmoothed) {
		super();
		this.date = date;
		this.consumption = consumption;
		this.consumptionSmoothed = consumptionSmoothed;
	}
	
	public DateTime getDateTime() {
		return this.date;
	}
	
	public int getDuration() {
		return this.durationInSec;
	}
	
	public void setDuration(final int duration) {
		this.durationInSec = duration;
	}
	
	public int getConsumptionInWatt() {
		return this.consumption;
	}
	
	public int getSmoothedConsumptionInWatt() {
		return this.consumptionSmoothed;
	}
	
	@Override
	public String toString() {
		return "Date: " + this.date + " (" + this.consumption + "; " + this.consumptionSmoothed + ") for " + this.durationInSec + "s";
	}
	
	/**
	 * creates a LineData from the current from second from to second to
	 * 
	 * @param from
	 *            from value in seconds
	 * @param to
	 *            to value in seconds
	 * @return
	 */
	public LineData createSubLineData(final int from, final int to) {
		if(to <= from) {
			throw new IllegalArgumentException("to shall never be equal or lesser than from");
		}
		
		final LineData ld = new LineData(this.date.addSeconds(from), this.consumption, this.consumptionSmoothed);
		ld.setDuration(to - from);
		return ld;
	}
	
	/**
	 * creates a LineData from the current from second from till end
	 * 
	 * @param from
	 *            from value in seconds
	 * @return
	 */
	public LineData createSubLineData(final int from) {
		final LineData ld = new LineData(this.date.addSeconds(from), this.consumption, this.consumptionSmoothed);
		ld.setDuration(this.date.getDiffInSeconds(this.date.addSeconds(this.getDuration())) - from);
		return ld;
	}
	
}
