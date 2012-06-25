package de.tud.kom.challenge.prediction.processors;

import java.util.Vector;

import de.tud.kom.challenge.prediction.DataContainer;
import de.tud.kom.challenge.prediction.DataEntry;
import de.tud.kom.challenge.prediction.PredictionFeature;

public class EnergyLevelProcessor implements PredictionProcessor {

	private final int levelRange = 50;

	private int[] levelRepresentatives = new int[levelRange];

	private int levelsUsed = 1;

	private double marginePercentage = 0.05;
	private double margineAbsolut = 2;

	@Override
	public void setCompleteData(DataContainer data) {

	}

	@Override
	public Vector<PredictionFeature> addValueToModel(DataEntry input) {

		int consumption = input.getValue();
		int level = 0;

		Vector<PredictionFeature> features = new Vector<PredictionFeature>();

		if (consumption != 0) {
			boolean settled = false;
			for (int i = 1; i < levelsUsed; i++) {
				int levelRepresentative = levelRepresentatives[i];
				boolean tooHigh = ((double) levelRepresentative)
						* (1 + marginePercentage) < (double) consumption;
				boolean tooLow = ((double) levelRepresentative)
						* (1 - marginePercentage) > (double) consumption;

				if (!tooHigh && !tooLow) {
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

		return features;
	}

	@Override
	public String[] getResultTypes() {
		return new String[] { "EnergyLevel" };
	}

	@Override
	public String[] getResultRanges() {
		String result = "{Level0";
		for (int i = 1; i < levelRange; i++)
			result += ", Level" + i;
		result += "}";
		return new String[] { result };
	}

}
