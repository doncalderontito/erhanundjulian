package de.tud.kom.challenge.processors.fex;

import java.util.ArrayList;
import java.util.TreeSet;

public class Filters {

	// remove TimeIntervals with powerlevel smaller than noiseLevel
	public static ArrayList<TimeInterval> removeBelow(ArrayList<TimeInterval> intervals, double noiseLevel) {
		ArrayList<TimeInterval> out = new ArrayList<TimeInterval>();
		ArrayList<TimeInterval> is = ProcessorUtilities.copyTimeIntervals(intervals);
		for (int i = 0; i < is.size(); i++) {
			TimeInterval interval = is.get(i);
			if (Math.abs(interval.getLevel()) > noiseLevel) {
				out.add(interval);
			}
		}
		return out;
	}

	// remove TimeIntervals with powerlevel bigger than noiseLevel
	public static ArrayList<TimeInterval> removeAbove(ArrayList<TimeInterval> intervals, double noiseLevel) {
		ArrayList<TimeInterval> out = new ArrayList<TimeInterval>();
		ArrayList<TimeInterval> is = ProcessorUtilities.copyTimeIntervals(intervals);
		for (int i = 0; i < is.size(); i++) {
			TimeInterval interval = is.get(i);
			if (Math.abs(interval.getLevel()) <= noiseLevel) {
				out.add(interval);
			}
		}
		return out;
	}

	// remove TimeIntervals with powerlevel smaller than noiseLevel
	// but keep TimeIntervals if powerlevel of neighbours is bigger than noiseLevel
	public static ArrayList<TimeInterval> removeBelowWhenNeighboursBelow(ArrayList<TimeInterval> intervals, double noiseLevel, double neighbourLevel) {
		ArrayList<TimeInterval> out = new ArrayList<TimeInterval>();
		for (int i = 0; i < intervals.size(); i++) {
			TimeInterval interval = intervals.get(i);
			if (Math.abs(interval.getLevel()) > noiseLevel) {
				out.add(interval);
			} else {
				TimeInterval t1 = null;
				TimeInterval t2 = null;
				if (i > 0) {
					t1 = intervals.get(i - 1);
				}
				int j = i + 1;
				if (j < intervals.size()) {
					t2 = intervals.get(j);
				}
				if ((t1 != null) && (t1.getLevel() > neighbourLevel) || (t2 != null) && (t2.getLevel() > neighbourLevel)) {
					out.add(interval);
				}
				if (t1 == null && t2 == null) {
					out.add(interval);
				}
			}
		}
		return out;
	}

	// average TimeIntervals in timeWindow by observing neighbours
	// only when powerlevel difference is smaller than toleranceDifference
	public static ArrayList<TimeInterval> average(ArrayList<TimeInterval> _intervals, int timeWindow, double toleranceDifference ) {
		ArrayList<TimeInterval> out = new ArrayList<TimeInterval>();
		ArrayList<TimeInterval> intervals = ProcessorUtilities.copyTimeIntervals(_intervals);

		for (int i = 0; i < intervals.size(); i++) {
			TimeInterval interval = intervals.get(i);
			if (interval.getLength() >= timeWindow) {
				out.add(interval);
			} else {

				double after = interval.getLevel();
				double before = interval.getLevel();
				int iAfter = i + 1;
				int iBefore = i - 1;
				if (iAfter != intervals.size())
					after = intervals.get(iAfter).getLevel();
				if (iBefore != -1)
					before = intervals.get(iBefore).getLevel();
				if (Math.abs(after - interval.getLevel()) > toleranceDifference)
					after = interval.getLevel();
				if (Math.abs(before - interval.getLevel()) > toleranceDifference)
					before = interval.getLevel();
				double level = (after + before) / 2.0;
				level = (level * (timeWindow - interval.getLength()) + (interval.getLevel() * interval.getLength())) / (timeWindow * 1.0);

				interval.setLevel(level);

				out.add(interval);
			}
		}
		return out;
	}

	// remove TimeIntervals that are smaller than ( max powerlevel + average powerlevel ) / 2
	public static ArrayList<DeviceUsage> removeIntervalsBelowAveragePower(ArrayList<DeviceUsage> _usages) {
		ArrayList<DeviceUsage> usages = ProcessorUtilities.copyDeviceUsages(_usages);
		for (int i = 0; i < usages.size(); i++) {
			DeviceUsage u = usages.get(i);
			ArrayList<TimeInterval> intervals = u.getIntervals();
			double p = u.getPower();
			double max = 0;
			for (int j = intervals.size() - 1; j >= 0; j--) {
				if (intervals.get(j).getLevel() > max) {
					max = intervals.get(j).getLevel();
				}
			}

			double newLevel = (1 * max + p) / 2;

			for (int j = intervals.size() - 1; j >= 0; j--) {
				if (intervals.get(j).getLevel() < newLevel) {
					intervals.remove(j);
				}
			}
		}
		return usages;
	}

	// remove usages with energy smaller than (smallestEnergyPercentage * highest_energy)
	public static void removeSmallEnergyUsages(ArrayList<DeviceUsage> dus, double smallestEnergyPercentage) {
		ArrayList<Double> energies = new ArrayList<Double>();
		for (DeviceUsage u : dus) {
			energies.add(u.getEnergy());
		}
		if (energies.size() > 1) {
			ArrayList<Object> smallestHighestAvg = ProcessorUtilities.calculateSmallestHighestAvgDouble(energies);
			double highest = (Double) smallestHighestAvg.get(1);
			double smallestAllowedEnergy = smallestEnergyPercentage * highest / 100;
			for (int i = energies.size() - 1; i >= 0; i--) {
				if (energies.get(i) < smallestAllowedEnergy) {
					dus.remove(i);
				}
			}
		}
	}

	// remove usages with energy smaller than smallestEngery
	public static ArrayList<DeviceUsage> removeSmallEnergyUsagesTest(ArrayList<DeviceUsage> dus, double smallestEnergy) {
		ArrayList<DeviceUsage> result = new ArrayList<DeviceUsage>();
		for (DeviceUsage u : dus) {
			if (u.getEnergy() > smallestEnergy)
				result.add(u);
		}
		return result;
	}
	
	// check if sample is 24 hours long
	public static boolean checkForWholeDaySample(TreeSet<Integer> hours){
		for(int i=0;i<24;i++){
			if(!hours.contains(i)) return false;
		}
		return true;
	}
}
