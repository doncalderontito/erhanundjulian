package de.tud.kom.challenge.prediction.evaluator;

import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.challenge.prediction.PredictionFeature;

public class SimpleEvaluator implements Evaluator {

	private final Logger log = Logger.getLogger(this.getClass().getSimpleName());
	private String lastVector = null;
	
	/**
	 * This is a straightforward event evaluator - once any of the processors
	 * determines an event, this is directly forwarded to the framework
	 */
	public boolean evaluate(Vector<PredictionFeature> results, boolean training) {
		if (training) {
			// We have a stateless model, we don't need to train anything...
			return false;
		}
		
		String newVector = "";
		for (PredictionFeature p:results) {
			newVector += p.getResult()+";";
		}
		
		if (lastVector==null) {
			// Initialize
			lastVector = newVector;
		} else if (!lastVector.equals(newVector)) {
			log.info("Something has happened - raising event!");
			return true;
		}

		return false;
	}
	
	public String toString() {
		return "Stateless model";
	}

	public void trainFromArff(String path) {
		// Nothing to be done here...
	}
}
