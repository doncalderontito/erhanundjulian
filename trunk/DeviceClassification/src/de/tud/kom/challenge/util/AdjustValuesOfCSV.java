package de.tud.kom.challenge.util;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import de.tud.kom.challenge.arff.FeatureExtractor;


public class AdjustValuesOfCSV {

	/**
	 * @author Hristo Chonov
	 * 
	 */

	public static void readAndUpdateFile(String filename, double divisor, boolean replaceValues) {
	    InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(new FileInputStream (filename));
		} catch (FileNotFoundException e) {
			System.err.println("Error: " + e.getMessage());
			return;
		}
	    
	    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	    StringBuffer sb = new StringBuffer();
	    
	    while(true) {
	    	String readLine = null;
			try {
				readLine = bufferedReader.readLine();
			} catch (IOException e) {
				System.err.println("Error: " + e.getMessage());
				break;
			}
	    	if(readLine == null || readLine.length() == 0) break;
	    	
	    	int first = readLine.indexOf(';');
	    	first++;
	    	int second = readLine.indexOf(';', first);
	    	int third = readLine.indexOf(';', second+1);
	    	
	    	String watt = readLine.substring(first, second);
	    	
	    	int wattNow = Integer.valueOf(watt);
	    	int newValue = 0;
	    	
	    	if(wattNow != 0) {
	    		int newWatt = (int) Math.round(wattNow/divisor);
	    		newValue = (int) Math.max(divisor, newWatt * (int) divisor);
	    	}
	    	
	    	if(replaceValues)
	    		sb.append(readLine.substring(0, first) + newValue + readLine.substring(second) + "\n");
	    	else if(third == -1)
	    			sb.append(readLine + ";" + newValue + "\n");
	    		else sb.append(readLine.substring(0, third) + ";" + newValue + "\n");
	    }
	    
	    try {
			bufferedReader.close();
			inputStreamReader.close();
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
			return;
		}

	    FileUtil.simpleWriteToDisc(filename, sb, true);
	}
	
	/**
	 * @param replaceValues = true => the 1s values will be replaced based on the divisor
	 * @param replaceValues = false => instead of replacing the existing values every line will be extended and the end the new value will get written.
	 */
	public static void readAndUpdateFolder(String dirname, double divisor, boolean replaceValues) {
		String[] files = FeatureExtractor.getFiles(dirname);
		for(String file: files) 
			readAndUpdateFile(file, divisor, replaceValues);
	}
	
	
	public static void main(final String[] args) {
		AdjustValuesOfCSV.readAndUpdateFolder("E:\\training2", 12, true);
	}

}
