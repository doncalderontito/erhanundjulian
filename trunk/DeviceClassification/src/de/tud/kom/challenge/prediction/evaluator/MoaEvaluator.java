package de.tud.kom.challenge.prediction.evaluator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
	private final static Logger log = Logger.getLogger(MoaEvaluator.class
			.getSimpleName());
	Instances dataset = null;
	Clustering clustering = null;
	int oldNumberOfClusters = 0;
	
	//filters for recognizing same data processings
	Vector<Instance> instanceFilter = new Vector<Instance>();
	int filtersize = 10;

	//values to compare for manually checking of min and max power consumptions
	private double maxSoFar;
	private double minSoFar;

	// a set of clusterers
	private AbstractClusterer[] clusterers = new AbstractClusterer[] { new CobWeb()};
	private int[] clustererSizes = new int[clusterers.length];
	private int clustererCounter = 0;
	
	//a set of datasets
	private Vector<Instances> datasets = new Vector<Instances>();
	private int datasetCounter = 0;
	

	@Override
	public boolean evaluate(Vector<PredictionFeature> results, boolean training) {

		if (dataset == null) {
			log.error("dataset not initialized - trainFromArff should be called before");
			return false;
		}

		Vector<PredictionFeature> resultsPart1 = new Vector<PredictionFeature>();
		resultsPart1.add(results.get(0));
		Instance instance1 = this.predictionFeatureToInstance(resultsPart1);
		boolean event1 = evaluate(instance1, false);
		
		Vector<PredictionFeature> resultsPart2 = new Vector<PredictionFeature>();
		resultsPart2.add(results.get(3));
		resultsPart2.add(results.get(4));
		Instance instance2 = this.predictionFeatureToInstance(resultsPart2);
		boolean event2 = evaluate(instance2, true);
		
		return event1 || event2;
	}

	private boolean evaluate(Instance instance,
			boolean toBeClustered) {

		timestamp++;
		
		// filter the same instances out for performance purposes
		if (instanceFiltered(instance)) {
			return false;
		}
		
		boolean event = false;

		if(!toBeClustered){
			for (int i = 0; i < instance.numValues(); i++) {
				double outOfClusterValue = instance.value(i);
				event = evaluateValueManually(outOfClusterValue, i);
			}
			
		}
		
		else{
			AbstractClusterer currentClusterer = clusterers[clustererCounter];
			currentClusterer.trainOnInstance(instance);
			int numberOfClusters = ((CobWeb) currentClusterer).numberOfClusters();
			
			if (clustererSizes[clustererCounter] < numberOfClusters) {
				String txt = "event: new cluster created :" + numberOfClusters
						+ " assigned to:";

				double[] result = currentClusterer.getVotesForInstance(instance);
				for (int i = 0; i < result.length; i++)
					txt += i + "=" + result[i] + " | ";

				log.info("step:" + timestamp + " --> " + instance + " --> " + txt);

				event = true;
				clustererSizes[clustererCounter] = numberOfClusters;
				if(++clustererCounter == clusterers.length)
					clustererCounter = 0;
			}
		}
	
		return event;

	}

	public String toString() {
		return "hellooooo";
	}

	@Override
	public void trainFromArff(String path) {
		for(AbstractClusterer clusterer : clusterers){
			clusterer.prepareForUse();
		}

		ArffLoader loader = new ArffLoader();
		try {
			loader.setFile(new File(path));

			dataset = loader.getDataSet();

			Instances dataset0 = new Instances(dataset);
			dataset0.deleteAttributeAt(1);
			dataset0.deleteAttributeAt(1);
			dataset0.deleteAttributeAt(1);
			dataset0.deleteAttributeAt(1);
			datasets.add(dataset0);
			
			Instances dataset1 = new Instances(dataset);
			dataset1.deleteAttributeAt(0);
			dataset1.deleteAttributeAt(0);
			dataset1.deleteAttributeAt(0);
			datasets.add(dataset1);

			for (int i = 0; i < dataset.numInstances(); i++) {
				Instance instance0 = dataset0.get(i);
				evaluate(instance0, false);

				Instance instance1 = dataset1.get(i);
				evaluate(instance1, true);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private boolean instanceFiltered(Instance instance) {
		boolean found = false;
		for (Instance filterInstance : instanceFilter) {
			if(filterInstance.numAttributes() != instance.numAttributes())
				continue;
			
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

	private boolean evaluateValueManually(double value, int type) {

		boolean result = false;

		switch (type) {
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
	
	private Instance predictionFeatureToInstance(Vector<PredictionFeature> results){
		Instances dataset = datasets.get(datasetCounter);
		Instance instance = new DenseInstance(dataset.numAttributes());
		instance.setDataset(dataset);
		int pos = 0;

		for (PredictionFeature feature : results) {
			if (feature.getResult() == null) {
				pos++;
				continue;
			}

			if (dataset.attribute(pos).isNumeric()) {
				if (!feature.getResult().equals("?")) {
					instance.setValue(pos,
							(double) Double.valueOf(feature.getResult()));
				} else {
					instance.setMissing(pos);
				}

			} else {
				instance.setValue(pos, feature.getResult());
			}

			pos++;
		}
		
		
		if(++datasetCounter == datasets.size())
			datasetCounter = 0;
		
		return instance;
	}

}
