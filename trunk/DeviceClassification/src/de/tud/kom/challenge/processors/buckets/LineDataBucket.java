package de.tud.kom.challenge.processors.buckets;

import java.util.List;

import de.tud.kom.challenge.csvdatareader.DateTime;
import de.tud.kom.challenge.csvdatareader.LineData;
import de.tud.kom.challenge.util.CollectionUtil;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 */
public class LineDataBucket {
	
	private final DateTime from;
	private final DateTime to;
	private final List<LineData> data;
	
	public LineDataBucket(final DateTime from, final DateTime to, final List<LineData> data) {
		super();
		this.from = from;
		this.to = to;
		this.data = data;
	}
	
	public DateTime getFrom() {
		return this.from;
	}
	
	public DateTime getTo() {
		return this.to;
	}
	
	public List<LineData> getData() {
		return this.data;
	}
	
	public void addData(final LineData ld) {
		this.data.add(ld);
	}
	
	@Override
	public String toString() {
		return "From: " + this.from + " to: " + this.to + "; Data: " + this.data.size() + "\n";
	}
	
	public int getMaxPowerConsumption() {
		int maxVal = 0;
		
		for(final LineData line : this.data) {
			maxVal = Math.max(maxVal, line.getSmoothedConsumptionInWatt());
		}
		
		return maxVal;
	}
	
	public Integer calculateAvgPowerConsumptionInWatt() {
		final int diffInSeconds = this.from.getDiffInSeconds(this.to);
		
		if(diffInSeconds == 0) {
			return null;
		}
		
		return this.calculateWattSeconds() / diffInSeconds;
	}
	
	public int calculateWattSeconds() {
		int sum = 0;
		for(int i = 0; i < this.data.size(); i++) {
			
			// final DateTime first = this.data.get(i - 1).getDateTime();
			final int consumption = this.data.get(i).getConsumptionInWatt();
			// final DateTime second = this.data.get(i).getDateTime();
			sum += consumption * this.data.get(i).getDuration();
			// sum += this.getkWs(first, second, consumption);
		}
		return sum;
	}
	
	public boolean hasEnergyConsumption() {
		return CollectionUtil.first(this.data).getSmoothedConsumptionInWatt() != 0;
	}
	
}
