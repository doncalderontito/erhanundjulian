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
	
	private int getClusterNumber() {
		return (int) Math.floor(1.0f / EdgeProcessor.ERROR_MARGIN_PERCENT / 2.0f);
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
		int cluster = 0;
		if(oldValue != -1) {
			if(EdgeProcessor.isEdge(oldValue, entry.getValue())) {
				cluster = Math.min(getClusterNumber() - 1, clusterNeeded);
				for(int i = 0; i < clusterNeeded; i++) {
					if(entry.getValue() >= min[i] && entry.getValue() <= max[i]) {
						cluster = i;
						break;
					}
				}
				if(cluster == clusterNeeded) {
					clusterNeeded = Math.min(getClusterNumber() - 1, clusterNeeded + 1);
				}
			}
		}
		if(entry.getValue() < min[cluster])
			min[cluster] = entry.getValue();
		if(entry.getValue() > max[cluster])
			max[cluster] = entry.getValue();
		features.add(new PredictionFeature("EnergyClass", "Cluster" + cluster));
		oldValue = entry.getValue();
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

