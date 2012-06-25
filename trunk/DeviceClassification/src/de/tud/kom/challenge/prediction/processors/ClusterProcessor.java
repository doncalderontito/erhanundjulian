package de.tud.kom.challenge.prediction.processors;

import java.util.Vector;

import de.tud.kom.challenge.prediction.DataContainer;
import de.tud.kom.challenge.prediction.DataEntry;
import de.tud.kom.challenge.prediction.PredictionFeature;

public class ClusterProcessor implements PredictionProcessor {
	
	private int max[], min[];
	private int currentCluster = 0;
	private int oldValue = -1;
	private int clusterNeeded = 1;
	private int cluster = 0;
	
	private int getClusterNumber() {
		return 50;
	}
	
	public ClusterProcessor() {
		max = new int[getClusterNumber()];
		min = new int[getClusterNumber()];
		for(int i = 0; i < getClusterNumber(); i++)
			min[i] = Integer.MAX_VALUE;
	}
	
	@Override
	public void setCompleteData(DataContainer data) {
		// TODO Auto-generated method stub

	}

	@Override
	public Vector<PredictionFeature> addValueToModel(DataEntry entry) {
		
		Vector<PredictionFeature> features = new Vector<PredictionFeature>();
		if(oldValue != -1) {
			if(EdgeProcessor.isEdge(oldValue, entry.getValue())) {
				oldValue = entry.getValue();
				boolean apprClusterFound = false;
				for(int i = 0; i < getClusterNumber()-1; i++) {
					if(entry.getValue() >= min[i] && entry.getValue() <= max[i]) {
						cluster = i;
						apprClusterFound = true;
						break;
					}
				}
				if(apprClusterFound == false){
					cluster = clusterNeeded++;					
				}
			}
		}
		if(entry.getValue() < min[cluster])
			min[cluster] = entry.getValue();
		if(entry.getValue() > max[cluster])
			max[cluster] = entry.getValue();
		features.add(new PredictionFeature("EnergyClass", "Cluster" + cluster));
		return features;
	}

	@Override
	public String[] getResultTypes() {
		return new String[] {"EnergyCluster"};
	}

	@Override
	public String[] getResultRanges() {
		String result = "{Cluster0";
		for(int i = 1; i < getClusterNumber(); i++)
			result += ", Cluster" + i;
		result += "}";
		return new String[] {result};
	}

}

