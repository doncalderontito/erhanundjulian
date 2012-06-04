package de.tud.kom.challenge.arff.featuredescription;

import java.util.ArrayList;
import java.util.List;

import de.tud.kom.challenge.csvdatareader.DateTime;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 */
public class FeatureDescriptionGenerator {
	
	private static final DateTime dayBegin = new DateTime(0, 0, 0, 0, 0, 0);
	private static final DateTime dayEnd = FeatureDescriptionGenerator.dayBegin.addSeconds((60 * 60 * 24) - 1);
	
	public static List<FeatureDescription> getIntFeatureDescription(String nameTemplate, int bucketWidthInSec) {
		ArrayList<FeatureDescription> descr = new ArrayList<FeatureDescription>();
		
		DateTime current = new DateTime(FeatureDescriptionGenerator.dayBegin);
		while(current.isBefore(FeatureDescriptionGenerator.dayEnd)) {
			String name = nameTemplate + "_from_" + current.toTimeString();
			current = current.addSeconds(bucketWidthInSec);
			name += "_to_" + current.toTimeString();
			descr.add(new IntFeatureDescription(name));
		}
		
		return descr;
	}
	
	public static List<FeatureDescription> getBooleanFeatureDescription(String nameTemplate, int bucketWidthInSec) {
		ArrayList<FeatureDescription> descr = new ArrayList<FeatureDescription>();
		
		DateTime current = new DateTime(FeatureDescriptionGenerator.dayBegin);
		while(current.isBefore(FeatureDescriptionGenerator.dayEnd)) {
			String name = nameTemplate + "_from_" + current.toTimeString();
			current = current.addSeconds(bucketWidthInSec);
			name += "_to_" + current.toTimeString();
			descr.add(new BooleanFeatureDescription(name));
		}
		
		return descr;
	}
}
