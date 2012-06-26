package de.tud.kom.challenge.prediction.evaluator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import de.tud.kom.challenge.prediction.PredictionFeature;

public class TimedEnergyLevelEvaluator implements Evaluator {

	private final static Logger log = Logger
			.getLogger(TimedEnergyLevelEvaluator.class.getSimpleName());

	private Instances dataset;

	// filter
	private final int filterSize = 1;
	private Vector<Instance> instanceFilter = new Vector<Instance>();
	boolean filterActive = true;

	private int lastLevel = -1;
	private double lastDuration;
	private HashMap<Integer, Double> levelsToMinDuration = new HashMap<Integer, Double>();
	private HashMap<Integer, Double> levelsToMaxDuration = new HashMap<Integer, Double>();

	private HashSet<Integer> seenValues = new HashSet<Integer>();
	private int failureCandidate0 = -1;
	private int failureCandidate1 = -1;

	private String message = "";


	@Override
	public boolean evaluate(Vector<PredictionFeature> results, boolean training) {

		if (dataset == null) {
			log.error("dataset not initialized - trainFromArff should be called before");
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
				instance.setValue(pos,
						(double) Double.valueOf(feature.getResult()));

			} else {
				instance.setValue(pos, feature.getResult());
			}

			pos++;
		}

		return evaluate(instance, training);
	}

	private boolean evaluate(Instance instance, boolean training) {

		int level = (int) instance.value(0);
		double duration = (double) instance.value(1);
		int day = (int) instance.value(2);
		int daySegment = (int) instance.value(3);

		boolean event = false;

		event = this.evaluateDuration(level, duration, training);

		// filter out if same instance without duration data
		instance.setValue(1, 0);
		if (!event && instanceFiltered(instance)) {
			return false;
		}

		event = event
				| this.evaluateEnergyLevelWithTime(level, day, daySegment,
						training);

		return event;

	}

	public String toString() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void trainFromArff(String path) {

		ArffLoader loader = new ArffLoader();
		try {
			loader.setFile(new File(path));

			dataset = loader.getDataSet();

			for (int i = 0; i < dataset.numInstances(); i++) {
				Instance instance = dataset.get(i);
				evaluate(instance, true);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private boolean instanceFiltered(Instance instance) {

		if (filterActive == false) {
			return false;
		}

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
			if (instanceFilter.size() > filterSize)
				instanceFilter.remove(0);
		}
		return false;
	}

	private boolean evaluateDuration(int level, double duration,
			boolean training) {

		if (!levelsToMinDuration.containsKey(level)) {
			levelsToMinDuration.put(level, Double.MAX_VALUE);
			levelsToMaxDuration.put(level, (double) 0);
		}

		boolean result = false;

		if (training) {
			double oldMaxDuration = levelsToMaxDuration.get(level);
			if (duration > oldMaxDuration)
				levelsToMaxDuration.put(level, duration);

			if (lastLevel != level && lastLevel != -1) {
				double oldMinDuration = levelsToMinDuration.get(lastLevel);
				if (lastDuration < oldMinDuration)
					levelsToMinDuration.put(lastLevel, lastDuration);
			}
		}

		else {
			boolean maxEvent = false;
			boolean minEvent = false;

			double oldMaxDuration = levelsToMaxDuration.get(level);
			maxEvent = (duration > 1.15 * oldMaxDuration)
					&& (duration - oldMaxDuration > 60 * 15);
			if (maxEvent) {
				System.out.println("maxEvent in " + level);
			}

			if (level != lastLevel) {
				double oldMinDuration = levelsToMinDuration.get(lastLevel);
				minEvent = (oldMinDuration * 0.85 > lastDuration)
						&& (oldMinDuration - lastDuration > 4);
				if (minEvent) {
					System.out.println("minEvent in " + lastLevel);
				}
			}

			result = maxEvent || minEvent;

		}

		lastLevel = level;
		lastDuration = duration;

		return result;
	}

	private boolean evaluateEnergyLevelWithTime(int level, int day,
			int daySegment, boolean training) {

		int argumentsHashValue = level * 100 + day * 10 + daySegment * 1;

		if (!training) {
			if (failureCandidate1 == level) {
				return true;
			}
			if (failureCandidate0 == level) {
				failureCandidate1 = level;
				return false;
			}
		}

		boolean found = seenValues.contains(argumentsHashValue);

		if (!found) {
			if (training) {
				seenValues.add(argumentsHashValue);
			} else {
				System.out.println("failureCandidate : " + level);
				failureCandidate0 = level;
				failureCandidate1 = -1;
				filterActive = false;
			}
		} else {
			filterActive = true;
		}
		return false;

	}

}
