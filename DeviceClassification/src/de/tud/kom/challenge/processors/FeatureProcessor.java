package de.tud.kom.challenge.processors;

import de.tud.kom.challenge.csvdatareader.CsvContainer;

public interface FeatureProcessor {
	
	/**
	 * Lists the names of the generated features
	 * 
	 * @return a String array of all feature names in the corresponding order
	 */
	public String[] getAttributeNames();
	
	/**
	 * Lists the value ranges of the generated features
	 * 
	 * @return a String array of either discrete values in curly brackets, or the string "numeric"
	 */
	public String[] getAttributeValueranges();
	
	/**
	 * Processes the provided input into the features according to the attribute names and value ranges
	 * 
	 * @param csv
	 *            a CsvContainer which contains the contents of a CSV file
	 * @return the String array containing the processed features
	 * @throws Exception
	 *             in case something goes really wrong!
	 */
	public String[] processInput(CsvContainer csv) throws Exception;
	
	/**
	 * Returns the name of the Feature Processor
	 * @return the name of the processor
	 */
	public String getProcessorName();
	
}
