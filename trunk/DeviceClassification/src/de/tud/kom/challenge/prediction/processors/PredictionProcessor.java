package de.tud.kom.challenge.prediction.processors;

import java.util.Vector;

import de.tud.kom.challenge.prediction.DataContainer;
import de.tud.kom.challenge.prediction.DataEntry;
import de.tud.kom.challenge.prediction.PredictionFeature;

public interface PredictionProcessor {

	/**
	 * This method is called once initially. It pushes the reference to the data container
	 * to the PredictionProcessor, such that a buffered access to previous elements 
	 * is given.
	 * 
	 * @param data the DataContainer the stores all samples up to the current timestamp
	 */
	public void setCompleteData(DataContainer data);
	
	/**
	 * This is the core function of all PredictionProcessors. It is provided a new instance
	 * of data with a consecutively increasing timestamp (in seconds), and needs to 
	 * determine a constant set of features.
	 * 
	 * ATTENTION: This function must always return a result set in the same order
	 * Also, the result should be null in case no statement can be made.
	 * 
	 * @param input the DataEntry (time and value) of the next testing entry
	 * @return a Vector of features extracted from the input data
	 */
	public Vector<PredictionFeature> addValueToModel(DataEntry input);

	/**
	 * This interface must be provided to indicate the names of the features this processor extracts.
	 * 
	 * @return the names of the extracted features (make sure to not use the same name twice)
	 */
	public String[] getResultTypes();
	
	/**
	 * This interface must be provided to indicate the value ranges of the features this processor extracts.
	 * 
	 * @return the value ranges of the extracted features (same order as the names)
	 */
	public String[] getResultRanges();
}
