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
	
	private static final int PIPELINE_SIZE = 4;
	private int[] pipeline = new int[PIPELINE_SIZE];
	private int fillLevel = 0;

	public EnergyLevelProcessor() {
		for(int i = 0; i < PIPELINE_SIZE; i++)
		{
			pipeline[i] = -1;
		}
	}

	@Override
	public void setCompleteData(DataContainer data) {

	}

	@Override
	public Vector<PredictionFeature> addValueToModel(DataEntry input) {

		int level = 0;

		Vector<PredictionFeature> features = new Vector<PredictionFeature>();
		
		for(int i = PIPELINE_SIZE - 1; i > 0; i--)
			pipeline[i] = pipeline[i - 1];
		pipeline[0] = input.getValue();
		
		if(pipeline[PIPELINE_SIZE - 1] == -1) {
			features.add(new PredictionFeature("EnergyLevel", "?"));
			features.add(new PredictionFeature("LevelDuration", "?"));
			return features;
		}
		
		if(EdgeProcessor.isEdge(pipeline[0], pipeline[1]) && EdgeProcessor.isEdge(pipeline[2], pipeline[1])) {
			pipeline[1] = pipeline[0];
		}

		if(EdgeProcessor.isEdge(pipeline[0], pipeline[1]) && EdgeProcessor.isEdge(pipeline[3], pipeline[2])) {
			pipeline[1] = pipeline[0];
			pipeline[2] = pipeline[0];
		}

		int consumption = pipeline[PIPELINE_SIZE - 1];
		
		if (lastLevelStartTime == -1)
			lastLevelStartTime = input.getTime();


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
