package de.tud.kom.challenge.prediction.processors;

import java.util.Vector;

import de.tud.kom.challenge.prediction.DataContainer;
import de.tud.kom.challenge.prediction.DataEntry;
import de.tud.kom.challenge.prediction.PredictionFeature;

public class EnergyLevelProcessor implements PredictionProcessor {

	private final int levelRange = 7200;

	private int[] levelRepresentatives = new int[levelRange];


	private int levelsUsed = 1;

	private double marginePercentage = 0.1;
	private double margineAbsolut = 4;

	private int lastLevel = -1;
	private long lastLevelStartTime = -1;

	public EnergyLevelProcessor() {

	}

	@Override
	public void setCompleteData(DataContainer data) {

	}

	@Override
	public Vector<PredictionFeature> addValueToModel(DataEntry input) {

		int consumption = input.getValue();
		int level = 0;
		
		if (lastLevelStartTime == -1)
			lastLevelStartTime = input.getTime();

		Vector<PredictionFeature> features = new Vector<PredictionFeature>();

		if (consumption != 0) {
			boolean settled = false;
			for (int i = 1; i < Math.min(levelsUsed, levelRange); i++) {
				int levelRepresentative = levelRepresentatives[i];
				boolean tooHigh = ((double) levelRepresentative)
						* (1 + marginePercentage) < (double) consumption;
				boolean tooLow = ((double) levelRepresentative)
						* (1 - marginePercentage) > (double) consumption;

				if (!((tooHigh || tooLow) && Math.abs(levelRepresentative
						- consumption) > margineAbsolut)) {
					settled = true;
					level = i;
					break;
				}

			}

			if (settled == false) {
				
				level = levelsUsed++;
				levelRepresentatives[level] = consumption;
				System.out.println("new energy level: " + level + " for value " + input.getValue() * (1-marginePercentage) + " - " + input.getValue() * (1+marginePercentage));
			}
		}

		features.add(new PredictionFeature("EnergyLevel", "" + level));

		long duration = 0;
		if(level == lastLevel){
			duration = input.getTime() - lastLevelStartTime;
		}
		else{
			lastLevel = level;
			lastLevelStartTime = input.getTime();
		}

		features.add(new PredictionFeature("LevelDuration", ""+duration));
		return features;
	}

	@Override
	public String[] getResultTypes() {
		return new String[] { "EnergyLevel", "LevelDuration" };
	}

	@Override
	public String[] getResultRanges() {
		return new String[] { "numeric", "numeric" };
	}

}
