package de.tud.kom.challenge;

import java.util.Vector;

import de.tud.kom.challenge.prediction.processors.AbsolutePowerJumpProcessor;
import de.tud.kom.challenge.prediction.processors.HistoricYesterdayPredictor;
import de.tud.kom.challenge.prediction.processors.PredictionProcessor;
import de.tud.kom.challenge.prediction.processors.SimpleMaxWindowProcessor;
import de.tud.kom.challenge.prediction.processors.SimplePipeThroughProcessor;
import de.tud.kom.challenge.prediction.processors.SimpleTimeProcessor;
import de.tud.kom.challenge.prediction.processors.SimpleWindowPredictor;
import de.tud.kom.challenge.prediction.processors.SmoothValueProcessor;

public class PredictorList {
	
	public static Vector<PredictionProcessor> getPredictors() {
		Vector<PredictionProcessor> processors = new Vector<PredictionProcessor>();
		
		processors.add(new SmoothValueProcessor());			//smoothes the noise
		
		processors.add(new HistoricYesterdayPredictor());
		processors.add(new AbsolutePowerJumpProcessor());
		processors.add(new SimpleWindowPredictor());
		processors.add(new SimpleMaxWindowProcessor());
		processors.add(new SimpleTimeProcessor());
		processors.add(new SimplePipeThroughProcessor());
		
		return processors;
	}
}
