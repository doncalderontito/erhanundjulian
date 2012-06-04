package de.tud.kom.challenge.processors;

import java.io.Serializable;

import de.tud.kom.challenge.arff.featuredescription.FeatureDescription;
import de.tud.kom.challenge.csvdatareader.CsvContainer;

public interface IFeaturePreProcessor extends Serializable {
	
	public Iterable<FeatureDescription> getFeatureDescription();
	
	/**
	 * Processes the provided input into the features according to the attribute names and value ranges
	 * 
	 * @param csv
	 *            a CsvContainer which contains the contents of a CSV file
	 * @return the String array containing the processed features
	 * @throws Exception
	 *             in case something goes really wrong!
	 */
	public String[] processInput(CsvContainer csv);
	
}
