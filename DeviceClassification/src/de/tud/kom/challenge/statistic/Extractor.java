package de.tud.kom.challenge.statistic;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import de.tud.kom.challenge.util.RandomInteger;

public class Extractor {
	
	private CompleteDataset inputDataset;
	private CompleteDataset partialDataset;
//	private HashMap<String,CompleteDataset> manipulatedDataset;
	private StatisticResults outputStatisticResults;
	
	Extractor(CompleteDataset completeDataset){
		this.inputDataset = completeDataset;
		this.partialDataset = getPartialDataset(completeDataset);
	}
	
	
	//method to split a part away from a given CompleteDataset
	//this function can be alternative part of the 'CompleteDataset' class 
	private CompleteDataset getPartialDataset(CompleteDataset completeDataset){
		//Initialization of the returnDataset
		CompleteDataset newCompleteDataset = new CompleteDataset(completeDataset.getAttributes(), completeDataset.getDataTypes());
		//iteration over the devices
		Set<String> deviceNames = completeDataset.keySet();
		Iterator<String> iter = deviceNames.iterator();
		while (iter.hasNext()) {
			String devicename = iter.next();
			newCompleteDataset.put(devicename, new DataPerDevice());
			Integer randomNumbers[] = getRandomNumbers(inputDataset.numberOfDatasets(devicename));
			//slope for transferring DataPerDay from the inputDataset to the returned extracted outputDataset
			for(int i = 0; i<randomNumbers.length; i++){
				DataPerDay temporaryDayData = completeDataset.get(devicename).remove((int)randomNumbers[i]);
				newCompleteDataset.addDatasetOfADay(devicename, temporaryDayData);
			}
		}
		return newCompleteDataset;
	}
	
	
	//TODO - nicht nötig, wird von Manipulator-klasse erledigt => löschen
//	//this function can be alternative part of the 'CompleteDataset' class 
//	private HashMap<String,CompleteDataset> getManipulatedDatasets(CompleteDataset partialDataset){
//		//Initialization of the returnHashMap
//		HashMap<String,CompleteDataset> returnHashMap = new HashMap<String,CompleteDataset>();
//		
//		CompleteDataset newCompleteDataset = new CompleteDataset(partialDataset.getAttributes(), partialDataset.getDataTypes());
//		//iteration over the devices
//		Set<String> deviceNames = partialDataset.keySet();
//		Iterator<String> iter = deviceNames.iterator();
//		while (iter.hasNext()) {
//			//erzeugen eines "Complete Dataset" je Gerätename mit einem Gerät je manipulation
//			String devicename = iter.next();
//			//Initialization of the CompleteDataset per Device
//			CompleteDataset completeDatasetPerDevice = new CompleteDataset(partialDataset.getAttributes(), partialDataset.getDataTypes());
//			//erzeugen der manipulierten devices
//			returnHashMap.put(devicename, completeDatasetPerDevice);
//		}		
//		return returnHashMap;
//	}

	
	private Integer[] getRandomNumbers(int numberOfDatasets) {
		final int percentage = 10;
		
		Vector<Integer> returnVector = new Vector<Integer>();
		//TODO - extract percentage calculation
		double interimResult = numberOfDatasets*((double)percentage/100);
		int numberOfReturnValues = (int) Math.round(interimResult);

		for(int i = 0; i<numberOfReturnValues; i++){
			//generating random number in the interval from 0 to the quantity of Datasets, minus one to prevent getting out of the index of the Vector/array 
			RandomInteger rnd = new RandomInteger(0, numberOfDatasets-1);
			int newRandom = rnd.getRandomInteger();
			//protection of double values in the resulting array 
			while(returnVector.contains(newRandom)){
				newRandom = rnd.getRandomInteger();
				//TODO - maybe integration of a loop counter to break and prevent a deadlock
			}
			returnVector.add(newRandom);
		}
		//safety mechanism - prevents crush on small datasets
		if(returnVector.size() == 0)
			returnVector.add(0);
		//returnVector sorting and  converting to array
		Collections.sort(returnVector);
		//array must be returned in descending order to prevent problems with the 'remove' method		
		Collections.reverse(returnVector);
		Integer reverseArray[] = returnVector.toArray(new Integer[returnVector.size()]);

		return reverseArray;
	}
	
	

	public CompleteDataset getExtractedDataset() {
		return partialDataset;
	}
	
	public CompleteDataset getExtractedDatasetOfDevice(String deviceName) {
		CompleteDataset returnCD = new CompleteDataset(partialDataset.getAttributes(), partialDataset.getDataTypes());
		returnCD.put(deviceName, partialDataset.get(deviceName));
		return returnCD;
	}
	
	//TODO - inputDataset are not the remaining data.
	//=> fix naming, because result is sattifing
	public StatisticResults getRemainingStatisticResults() {
		return new StatisticResults(inputDataset);
	}

}
