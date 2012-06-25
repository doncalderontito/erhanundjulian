package de.tud.kom.challenge.prediction.processors;

import java.util.Vector;

import de.tud.kom.challenge.prediction.DataContainer;
import de.tud.kom.challenge.prediction.DataEntry;
import de.tud.kom.challenge.prediction.PredictionFeature;

public class EnergyClassProcessor implements PredictionProcessor {
	
	private long lastNewClassTime = -1;
	private int lastClassType = -1;
	
	@Override
	public void setCompleteData(DataContainer data) {
		// TODO Auto-generated method stub

	}

	@Override
	public Vector<PredictionFeature> addValueToModel(DataEntry entry) {
		Vector<PredictionFeature> features = new Vector<PredictionFeature>();
		
		String energyClass;
		int classType;
		if(entry.getValue() == 0){
			energyClass = "0";
			classType = 0;
		}
			
		else if(entry.getValue() < 11){
			energyClass = "1-10";
			classType = 1;
		}
			
		else if(entry.getValue() < 101){
			energyClass = "11-100";
			classType = 2;
		}
			
		else if(entry.getValue() < 1001){
			energyClass = "101-1000";
			classType = 3;
		}
			
		else{
			energyClass = "1000+";
			classType = 4;
		}
			
		features.add(new PredictionFeature("EnergyClass", energyClass));
		
		if(classType != lastClassType){
			lastNewClassTime = entry.getTime();
			lastClassType = classType;
			features.add(new PredictionFeature("Since", String.valueOf(0)));
		}
		else{
			long sinceInHour = (entry.getTime() - lastNewClassTime) / (60*60);
			features.add(new PredictionFeature("Since", String.valueOf(sinceInHour)));
		}
		
		
		return features;
	}

	@Override
	public String[] getResultTypes() {
		return new String[]{"EnergyClass", "Since"};
	}

	@Override
	public String[] getResultRanges() {
		return new String[]{"{0, 1-10, 11-100, 101-1000, 1000+}", "numeric"};
	}

}

