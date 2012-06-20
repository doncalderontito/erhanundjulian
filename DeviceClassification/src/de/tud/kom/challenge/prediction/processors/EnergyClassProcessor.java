package de.tud.kom.challenge.prediction.processors;

import java.util.Vector;

import de.tud.kom.challenge.prediction.DataContainer;
import de.tud.kom.challenge.prediction.DataEntry;
import de.tud.kom.challenge.prediction.PredictionFeature;

public class EnergyClassProcessor implements PredictionProcessor {
	
	@Override
	public void setCompleteData(DataContainer data) {
		// TODO Auto-generated method stub

	}

	@Override
	public Vector<PredictionFeature> addValueToModel(DataEntry entry) {
		Vector<PredictionFeature> features = new Vector<PredictionFeature>();
		String energyClass;
		if(entry.getValue() == 0)
			energyClass = "0";
		else if(entry.getValue() < 11)
			energyClass = "0-10";
		else if(entry.getValue() < 101)
			energyClass = "11-100";
		else if(entry.getValue() < 1001)
			energyClass = "101-1000";
		else
			energyClass = "1000+";
		features.add(new PredictionFeature("EnergyClass", energyClass));
		return features;
	}

	@Override
	public String[] getResultTypes() {
		return new String[]{"EnergyClass"};
	}

	@Override
	public String[] getResultRanges() {
		return new String[]{"{0, 0-10, 11-100, 101-1000, 1000+}"};
	}

}

