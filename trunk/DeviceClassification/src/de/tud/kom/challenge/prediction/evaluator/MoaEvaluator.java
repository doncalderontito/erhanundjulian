package de.tud.kom.challenge.prediction.evaluator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import moa.cluster.Clustering;
import moa.clusterers.AbstractClusterer;
import moa.clusterers.CobWeb;
import moa.gui.visualization.DataPoint;

import org.apache.log4j.Logger;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import de.tud.kom.challenge.prediction.PredictionFeature;

public class MoaEvaluator implements Evaluator {

	int timestamp = 0;
	AbstractClusterer clusterer = new CobWeb();
	private final static Logger log = Logger.getLogger(MoaEvaluator.class
			.getSimpleName());
	Instances dataset = null;
	Instances newInstances = null;
	Clustering clustering = null;
	int oldNumberOfClusters = 0;
	Vector<Instance> instanceFilter = new Vector<Instance>();
	int filtersize = 10;

	static int clusterOffset = 1;

	private double maxSoFar;
	private double minSoFar;

	@Override
	public boolean evaluate(Vector<PredictionFeature> oldResults, boolean training) {
		Vector<PredictionFeature> results;
		if (dataset == null) {
			log.error("dataset not initialized - trainFromArff should be called before");
			return false;
		}

		results = new Vector<PredictionFeature>();
		results.add(oldResults.firstElement());
		
		Instance instance = new DenseInstance(newInstances.numAttributes());
		instance.setDataset(newInstances);
		int pos = 0;

		for (PredictionFeature feature : results) {
			if (feature.getResult() == null) {
				pos++;
				continue;
			}

			if (dataset.attribute(pos).isNumeric()) {
				instance.setValue(pos,
						(double) Double.valueOf(feature.getResult()));

			} else {
				instance.setValue(pos, feature.getResult());
			}

			pos++;
		}

		return evaluate(instance);
	}

	private boolean evaluate(Instance instance) {

		boolean event = false;

		// check simple compare values by hand and set them missing for the clustering
		for (int i = 0; i < clusterOffset; i++) {
			double outOfClusterValue = instance.value(i);
			event = evaluateValueManually(outOfClusterValue, i);
			instance.setMissing(i);
		}

		// filter the same instances out for performance purposes
		if (instanceFiltered(instance)) {
			return false;
		}

		clusterer.trainOnInstanceImpl(instance);

		timestamp++;

		int numberOfClusters = ((CobWeb) clusterer).numberOfClusters();
		if (oldNumberOfClusters < numberOfClusters) {
			String txt = "event: new cluster created :" + numberOfClusters
					+ " assigned to:";

			double[] result = clusterer.getVotesForInstance(instance);
			for (int i = 0; i < result.length; i++)
				txt += i + "=" + result[i] + " | ";

			log.info("step:" + timestamp + " --> " + instance + " --> " + txt);

			event = true;
		}

		oldNumberOfClusters = numberOfClusters;

		return event;

	}

	public String toString() {
		return clusterer.toString();
	}

	@Override
	public void trainFromArff(String path) {
		clusterer.prepareForUse();
		ArffLoader loader = new ArffLoader();
		try {
			loader.setFile(new File(path));

			dataset = loader.getDataSet();

			//experiment
			newInstances = new Instances(dataset);
			for(int i = dataset.numAttributes() - 1; i > 0; i--){
				newInstances.deleteAttributeAt(i);
			}
			
			for(int k = 0; k < newInstances.numInstances(); k++){
				Instance instance = newInstances.get(k);
				evaluate(instance);
			}
			//experiment end
			
//			for (int i = 0; i < dataset.numInstances(); i++) {
//				Instance instance = dataset.get(i);
//				evaluate(instance);
//			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private boolean instanceFiltered(Instance instance) {
		boolean found = false;
		for (Instance filterInstance : instanceFilter) {
			boolean equal = true;
			for (int i = 0; i < instance.numValues(); i++) {
				if (!("" + instance.value(i)).equals(""
						+ filterInstance.value(i))) {
					equal = false;
					break;
				}
			}
			if (equal) {
				found = true;
				break;
			}
		}

		if (found) {
			return true;
		} else {
			instanceFilter.add(instance);
			if (instanceFilter.size() > filtersize)
				instanceFilter.remove(0);
		}
		return false;
	}

	private boolean evaluateValueManually(double value, int index) {

		boolean result = false;

		switch (index) {
		case 0:
			if (value > maxSoFar) {
				maxSoFar = value;
				result = true;
			}
			if (value < minSoFar) {
				minSoFar = value;
				result = true;
			}
			break;
		case 1:
			break;
		}

		return result;
	}

}
