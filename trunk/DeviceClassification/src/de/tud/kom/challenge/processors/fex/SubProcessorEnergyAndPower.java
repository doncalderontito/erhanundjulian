package de.tud.kom.challenge.processors.fex;

import java.util.ArrayList;

public final class SubProcessorEnergyAndPower {

	public static String[] getAttributeNames() {
		return new String[] { 
				"energyPerUsage_smallest",
				"energyPerUsage_highest",
				"energyPerUsage_avg",
				"energyPerDay",
				"powerlevelPerUsage_smallest",
				"powerlevelPerUsage_highest",
				"powerlevelPerUsage_avg",
				"powerlevelPerDay",
				// "increasingPowerstep_smallest",
				"increasingPowerstep_highest",
				"increasingPowerstep_avg",
				// "decreasingPowerstep_smallest",
				"decreasingPowerstep_highest",
				"decreasingPowerstep_avg",
				"usageEnergiesVar",
				"usagePowersVar"
				};
	}

	public static String[] getAttributeValueranges() {
		return new String[] { 
				"numeric",
				"numeric",
				"numeric",
				"numeric",

				"numeric",
				"numeric",
				"numeric",

				"numeric",

				// "numeric",
				"numeric",
				"numeric",
				// "numeric",
				"numeric",
				"numeric",

				"numeric",
				"numeric" };
	}

	public static ArrayList<Object> process(ExtractUsages eus) throws Exception {
		ArrayList<Object> result = new ArrayList<Object>();

		// energyPerUsage_smallest
		// energyPerUsage_highest
		// energyPerUsage_avg
		ArrayList<Double> usagesEnergy = eus.getUsageEnergies();
		ProcessorUtilities.addResult(result, ProcessorUtilities
				.calculateSmallestHighestAvgDouble(usagesEnergy));

		// energyPerDay
		Double energyPerDay = ProcessorUtilities.sumDouble(usagesEnergy);
		result.add(energyPerDay);

		// powerlevelPerUsage_smallest
		// powerlevelPerUsage_highest
		// powerlevelPerUsage_avg
		ArrayList<Double> usagesPowers = eus.getUsagePowers();
		ProcessorUtilities.addResult(result, ProcessorUtilities
				.calculateSmallestHighestAvgDouble(usagesPowers));

		// powerlevelPerDay
		Double powerPerDay = ProcessorUtilities.sumDouble(usagesPowers)	/ usagesPowers.size();
		result.add(powerPerDay);

		// increasingPowerstep_smallest
		// increasingPowerstep_highest
		// increasingPowerstep_avg
		// decreasingPowerstep_smallest
		// decreasingPowerstep_highest
		// decreasingPowerstep_avg
		ArrayList<Double> powerSteps = eus.getPowerSteps();
		ArrayList<Double> powerStepIncrease = new ArrayList<Double>();
		ArrayList<Double> powerStepDecrease = new ArrayList<Double>();
		for (double d : powerSteps) {
			if (d > 0) {
				powerStepIncrease.add(d);
			} else {
				powerStepDecrease.add(-d);
			}
		}
		ArrayList<Object> powerStepIncreaseHighestAvg = ProcessorUtilities
				.calculateSmallestHighestAvgDouble(powerStepIncrease);
		ArrayList<Object> powerStepDecreaseHighestAvg = ProcessorUtilities
				.calculateSmallestHighestAvgDouble(powerStepDecrease);
		powerStepIncreaseHighestAvg.remove(0);
		powerStepDecreaseHighestAvg.remove(0);
		ProcessorUtilities.addResult(result, powerStepIncreaseHighestAvg);
		ProcessorUtilities.addResult(result, powerStepDecreaseHighestAvg);

		// usageEnergiesVar
		// usagePowersVar
		Double[] energyVar = calculateEnergyVariances(eus);
		result.add(energyVar[0]);
		result.add(energyVar[1]);

		return result;
	}
	// calculate variance of usages by using energy
	private static Double[] calculateEnergyVariances(ExtractUsages eus) {
		Double[] result = new Double[] { null, null };
		if (eus.size() >= 2) {
			result[0] = ProcessorUtilities
					.calcVarDouble(eus.getUsageEnergies());
			result[1] = ProcessorUtilities.calcVarDouble(eus.getUsagePowers());
		}
		return result;
	}

}
