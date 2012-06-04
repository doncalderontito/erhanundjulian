package de.tud.kom.challenge.prediction.evaluator;

import java.util.Vector;

import de.tud.kom.challenge.prediction.PredictionFeature;

public interface Evaluator {

	/**
	 * This is the call for the initial training from a pre-determined ARFF file
	 * @param path the path of the training ARFF
	 */
	public void trainFromArff(String path);
	
	/**
	 * This method evaluates the set of PredictionFeatures and returns whether an
	 * exceptional event has occurred in the data set.
	 * 
	 * @param results the Vector of generated PredictionFeatures (make sure to keep this in the same order)
	 * @param training a flag that specifies if the PredictionFeatures were collected during an error-free period (training) or not (testing)
	 * @return whether abnormal behavior is detected
	 */
	public boolean evaluate(Vector<PredictionFeature> results, boolean training);
	
}
