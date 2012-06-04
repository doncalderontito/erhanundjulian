package de.tud.kom.challenge.processors.fex;

import java.util.ArrayList;

public class DeviceUsage {
	private ArrayList<TimeInterval> usage = new ArrayList<TimeInterval>();
	private int splitTimeSeconds;

	public ArrayList<TimeInterval> getIntervals() {
		return usage;
	}

	public int getSplitTimeSeconds() {
		return splitTimeSeconds;
	}

	public TimeInterval getFirstInterval() {
		if (size() > 0)
			return usage.get(0);
		return null;
	}

	public int size() {
		return usage.size();
	}

	public TimeInterval getLastInterval() {
		if (size() > 0)
			return usage.get(size() - 1);
		return null;
	}

	public DeviceUsage newCopy() {
		DeviceUsage du = new DeviceUsage();
		du.usage = ProcessorUtilities.copyTimeIntervals(this.usage);
		du.splitTimeSeconds = this.splitTimeSeconds;
		return du;
	}
	
	// create usages out of TimeTntervals, therefore it contains OnIntervals with OffIntervals smaller than splitTimeSeconds between each other.
	public static ArrayList<DeviceUsage> splitOnTime(ArrayList<TimeInterval> _intervals, int splitTimeSeconds) {
		ArrayList<TimeInterval> intervals = ProcessorUtilities.copyTimeIntervals(_intervals);
		ArrayList<DeviceUsage> result = new ArrayList<DeviceUsage>();
		DeviceUsage u = new DeviceUsage();
		result.add(u);
		u.splitTimeSeconds = splitTimeSeconds;
		TimeInterval ti = intervals.get(0);
		u.usage.add(ti);
		int lastTime = ti.getEnd();
		for (int i = 1; i < intervals.size(); i++) {
			ti = intervals.get(i);
			if (ti.getStart() <= (lastTime + splitTimeSeconds)) {
				u.usage.add(ti);
				lastTime = ti.getEnd();
			} else {
				u = new DeviceUsage();
				result.add(u);
				u.splitTimeSeconds = splitTimeSeconds;
				u.usage.add(ti);
				lastTime = ti.getEnd();
			}
		}
		return result;
	}
	
	// merge TimeIntervals in usages on power
	public static ArrayList<DeviceUsage> mergeIntervalsOnPower(ArrayList<DeviceUsage> _usages, int fillTime, double noiseLevel) throws Exception {
		ArrayList<DeviceUsage> ret = new ArrayList<DeviceUsage>();

		ArrayList<DeviceUsage> usages = ProcessorUtilities.copyDeviceUsages(_usages);
		for (DeviceUsage u : usages) {
			ret.add(merageIntervalsOnPower(u, fillTime, noiseLevel));
		}
		return ret;
	}
	
	// merge sequenced TimeIntervals if powerlevel difference is smaller than noisLevel
	// ignore OffIntervals <= fillTime
	private static DeviceUsage merageIntervalsOnPower(DeviceUsage usage, int fillTime, double noiseLevel) throws Exception {
		DeviceUsage u = new DeviceUsage();
		u.splitTimeSeconds = usage.getSplitTimeSeconds();
		TimeInterval ti = usage.getFirstInterval();
		ArrayList<TimeInterval> temp = new ArrayList<TimeInterval>();
		temp.add(ti);
		int lastTime = ti.getEnd();
		double currentPower = ti.getLevel();
		double minPower = currentPower;
		double maxPower = currentPower;

		for (int i = 1; i < usage.getIntervals().size(); i++) {
			ti = usage.getIntervals().get(i);
			currentPower = ti.getLevel();
			if (minPower > currentPower)
				minPower = currentPower;
			if (maxPower < currentPower)
				maxPower = currentPower;

			if ((ti.getStart() <= (lastTime + fillTime)) && (Math.abs(maxPower - minPower) <= noiseLevel)) {
				temp.add(ti);
				lastTime = ti.getEnd();
			} else {
				
				// add level to u.
				u.usage.add(makeOne(temp));
				temp.clear();
				
				// start again.
				temp.add(ti);
				lastTime = ti.getEnd();
				currentPower = ti.getLevel();
				minPower = currentPower;
				maxPower = currentPower;
			}
		}
		if (temp.size() > 0) {
			
			// add level to u.
			u.usage.add(makeOne(temp));
			temp.clear();
		}

		return u;
	}
	
	// merge a list of TimeIntervals to one TimeInterval
	private static TimeInterval makeOne(ArrayList<TimeInterval> intervals) throws Exception {
		if (intervals.size() == 0)
			throw new Exception("Can't merage empty set of intervals!");
		double energy = 0;
		int time = 0;
		for (TimeInterval ti : intervals) {
			time += ti.getLength();
			energy += ti.getLevel() * ti.getLength();
		}
		int start = intervals.get(0).getStart();
		int end = intervals.get(intervals.size() - 1).getEnd();
		return new TimeInterval(energy / time, start, end - start + 1, 0);
	}

	public Double getEnergy() {
		double ret = 0;
		for (TimeInterval ti : this.usage) {
			ret += ti.getLevel() * ti.getLength();
		}
		return ret;
	}
	
	// get average power of the usage
	public Double getPower() {
		double ret = 0;
		int length = 0;
		for (TimeInterval ti : this.usage) {
			ret += ti.getLevel() * ti.getLength();
			length += ti.getLength();
		}
		return (ret / length);
	}
}
