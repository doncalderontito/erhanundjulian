package de.tud.kom.challenge.processors;

import java.util.ArrayList;

import de.tud.kom.challenge.arff.featuredescription.FeatureDescription;
import de.tud.kom.challenge.csvdatareader.CsvContainer;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 * Correction: Hristo Chonov
 */
public class ProcessorAdapter implements FeatureProcessor {
	
	private IFeaturePreProcessor processor;
	
	public ProcessorAdapter(IFeaturePreProcessor processor) {
		this.processor = processor;
	}
	
	public String[] getAttributeNames() {
		final ArrayList<String> result = new ArrayList<String>();
		for(FeatureDescription fd:processor.getFeatureDescription()) {
			result.add(fd.getName());
		}
		return result.toArray(new String[0]);
	}
	
	public String[] getAttributeValueranges() {
		ArrayList<String> result = new ArrayList<String>();
		for(FeatureDescription fd:processor.getFeatureDescription()) {
			result.add(fd.getValueRange());
		}
		return result.toArray(new String[0]);
	}
	
	public String[] processInput(CsvContainer csv) throws Exception {
		return processor.processInput(csv);
	}
	
	public String getProcessorName() {
		return processor.getClass().getSimpleName();
	}
}
