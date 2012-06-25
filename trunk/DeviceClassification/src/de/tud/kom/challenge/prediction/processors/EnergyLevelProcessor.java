package de.tud.kom.challenge.prediction.processors;

import java.util.Vector;

import de.tud.kom.challenge.prediction.DataContainer;
import de.tud.kom.challenge.prediction.DataEntry;
import de.tud.kom.challenge.prediction.PredictionFeature;

public class EnergyLevelProcessor implements PredictionProcessor {

	private final int levelRange = 50;

	private int[] levelRepresentatives = new int[levelRange];
	private long[] levelMax = new long[levelRange];
	private long[] levelMin = new long[levelRange];

	private int levelsUsed = 1;

	private double marginePercentage = 0.1;
	private double margineAbsolut = 4;
	
	private int lastLevel = -1;
	private long startTime = -1;

	public EnergyLevelProcessor() {
		for(int i = 0; i < levelRange; i++) {
			levelMin[i] = Long.MAX_VALUE;
		}
	}
	
	@Override
	public void setCompleteData(DataContainer data) {

	}

	@Override
	public Vector<PredictionFeature> addValueToModel(DataEntry input) {

		int consumption = input.getValue();
		int level = 0;
		long duration = 0;
		if(startTime == -1)
			startTime = input.getTime();

		Vector<PredictionFeature> features = new Vector<PredictionFeature>();

		if (consumption != 0) {
			boolean settled = false;
			for (int i = 1; i < Math.min(levelsUsed, levelRange); i++) {
				int levelRepresentative = levelRepresentatives[i];
				boolean tooHigh = ((double) levelRepresentative)
						* (1 + marginePercentage) < (double) consumption;
				boolean tooLow = ((double) levelRepresentative)
						* (1 - marginePercentage) > (double) consumption;

				if (!((tooHigh || tooLow) && Math.abs(levelRepresentative - consumption) > margineAbsolut)) {
					settled = true;
					level = i;
					break;
				}

			}
			
			if(settled == false){
				level = levelsUsed++;
				levelRepresentatives[level] = consumption;
			}
		}

		features.add(new PredictionFeature("EnergyLevel", "Level" + level));
		
		if(level != lastLevel && lastLevel != -1) {
			duration = input.getTime() - startTime;
			if(duration < levelMin[lastLevel])
				levelMin[lastLevel] = duration;
			if(duration > levelMax[lastLevel])
				levelMax[lastLevel] = duration;
			startTime = input.getTime();
		}
		features.add(new PredictionFeature("LevelDurationMin", "" + levelMin[level]));
		features.add(new PredictionFeature("LevelDurationMax", "" + levelMax[level]));
		lastLevel = level;
		return features;
	}

	@Override
	public String[] getResultTypes() {
		return new String[] { "EnergyLevel", "LevelDurationMin", "LevelDurationMax" };
	}

	@Override
	public String[] getResultRanges() {
		String result = "{Level0";
		for (int i = 1; i < levelRange; i++)
			result += ",Level" + i;
		result += "}";
		return new String[] { result, "numeric", "numeric"};
	}

}
