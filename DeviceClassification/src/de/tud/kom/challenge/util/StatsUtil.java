package de.tud.kom.challenge.util;

import java.util.List;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 */
public class StatsUtil {
	
	public static double calculateAvg(final List<Double> nums) {
		if(nums.isEmpty()) {
			return 0;
		}
		double sum = 0;
		for(final double num : nums) {
			sum += num;
		}
		return sum / nums.size();
	}
	
	public static double calculateStdDev(final List<Double> nums) {
		double sqDiffs = 0;
		final double avg = StatsUtil.calculateAvg(nums);
		for(final double num : nums) {
			sqDiffs += (num - avg) * (num - avg);
		}
		return Math.sqrt(sqDiffs / nums.size());
	}
}
