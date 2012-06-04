package de.tud.kom.challenge.arff;

import java.io.File;

public class FileMapper {
	
	public static final String trainingPath = "training";
	public static final String trainingArff = "training.arff";

	public static final String testingPath = "testing";
	public static final String testingArff = "test.arff";
	
	public static final String optimumPath = "training";
	public static final String optimumCSV = "output.csv";
	
	public static String getDeviceTypeFromPathToFile(final String s) {
		String deviceType = "?";
		final String[] parts = s.split("\\" + File.separator);
		if(parts.length >= 2) {
			deviceType = parts[parts.length - 2].toLowerCase();
		}
		return deviceType;
	}
	
}
