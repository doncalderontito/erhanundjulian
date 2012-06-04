package de.tud.kom.challenge.prediction.evaluator;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.apache.log4j.Logger;

import weka.clusterers.Cobweb;
import weka.clusterers.UpdateableClusterer;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import de.tud.kom.challenge.prediction.PredictionFeature;

public class WekaEvaluator implements Evaluator {

	private final Logger log = Logger.getLogger(this.getClass().getSimpleName());

	UpdateableClusterer clusterer = new Cobweb();

	int timestamp = 0;
	Vector<Instance> filter = new Vector<Instance>();
	int filtersize = 10;
	int eventCounter = 0;
	int oldNumberOfClusters = 0;
	Instances dataset = null;


	@Override
	public boolean evaluate(Vector<PredictionFeature> results, boolean training) {
		if (dataset == null) {
			log.error("dataset not initialized - trainFromArff must be called first!");
			return false;
		}

		Instance instance = new DenseInstance(dataset.numAttributes());
		instance.setDataset(dataset);
		int pos = 0;

		for (PredictionFeature feature : results) {
			if (feature.getResult() == null) {
				pos++;
				continue;
			}

			if (dataset.attribute(pos).isNumeric()) {
				instance.setValue(pos, (double) Double.valueOf(feature.getResult()));

			} else {
				if (feature.getResult().equals(Boolean.TRUE))
					instance.setValue(pos, "true");
				else
					instance.setValue(pos, "false");
			}

			pos++;
		}
		// instance.setClassValue(0.0);

		return evaluate(instance);
	}

	public String toString() {
		return "\n" + clusterer.toString();
	}

	private boolean evaluate(Instance instance) {

		boolean found = false;
		for (Instance filterInstance:filter) {
			boolean equal = true;
			for (int i = 0; i < instance.numValues(); i++) {
				if (!("" + instance.value(i)).equals(""	+ filterInstance.value(i))) {
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
//			log.info("filter:"+instance);
			return false;
		}

		filter.add(instance);
		if (filter.size() > filtersize) filter.remove(0);

		int first = 0;

		try {
			long time = System.currentTimeMillis();

			if (timestamp > 0) first = ((Cobweb) clusterer).clusterInstance(instance);

			clusterer.updateClusterer(instance);

			int second = ((Cobweb) clusterer).clusterInstance(instance);

			log.info("evaluation:" + (System.currentTimeMillis() - time)
					+ " step:" + timestamp + "instance:" + instance
					+ " result:" + first + "/" + second);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		timestamp++;

		int numberOfClusters = ((Cobweb) clusterer).numberOfClusters();
		if (oldNumberOfClusters != numberOfClusters) {
			String txt = "event: cluster changed :" + oldNumberOfClusters
					+ " --> " + numberOfClusters;

			log.info("step:" + timestamp + " --> " + instance + " --> " + txt);

			if (oldNumberOfClusters < numberOfClusters)
				eventCounter++;

		} else {
			eventCounter = 0;			
		}

		oldNumberOfClusters = numberOfClusters;

		return eventCounter > 2;
	}

	@Override
	public void trainFromArff(String path) {
		// ((Cobweb)clusterer).setCutoff(0.003); 
		// the higher the more often the number of cluster will be reduced;
		
		((Cobweb) clusterer).setAcuity(2.9); // the higher the bigger and the
											 // lower the number of cluster
		log.info("cutoff:" + ((Cobweb) clusterer).getCutoff() + " acuity:" + ((Cobweb) clusterer).getAcuity());

		ArffLoader loader = new ArffLoader();
		try {
			loader.setFile(new File(path));

			dataset = loader.getDataSet();

			for (int i = 0; i < dataset.numInstances(); i++) {
				Instance instance = dataset.get(i);
				evaluate(instance);
			}

			log.info("train finished - dataset stats\n:" + dataset.toSummaryString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
