package de.tud.kom.challenge.statistic;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import au.com.bytecode.opencsv.CSVWriter;
import de.tud.kom.challenge.arff.FileMapper;

public class SFFgenerator {
	
	private boolean weighted = false;
	
	public AttributeQuantifier attributeQuantifier;
	

	
	//constructor
	public SFFgenerator(){
		
	}
		
	//special constructor to compare devices with other testing-devices
	public HashMap<String, Double> generateComplete(String dataPathToSaveFiles, StatisticResults statisticResults, CompleteDataset completeDatasetToCompare ){
		CompleteDataset completeDataset = statisticResults.getCompleteDataset();
		
		//return HashMap
		HashMap<String,Double> returnHashMap = new HashMap<String,Double>();
		
		//TODO - vielleicht einfach eine eigene Ausgabefunktion schreiben, und wieder zu BufferedWriter wechseln
		// Ausgabe als XML alternativ
		
				Set<String> devices = completeDataset.keySet();
				String[] deviceVector = Arrays.copyOf(devices.toArray(), devices.toArray().length,  String[].class);
				//generate one File per Gerätetyp/Device
				for(int i = 0; i<deviceVector.length; i++){
					
					try{
						//path and filename
						FileWriter fstream = new FileWriter(dataPathToSaveFiles+File.separator+deviceVector[i]+".csv");

						//Semikolon als Seperator anstelle von Komma
						char outputSeperator = ';'; 
						CSVWriter out = new CSVWriter(fstream, outputSeperator);
						
						AmountInMinMax amountInMinMax = new AmountInMinMax();
						String minArray[] = new String[0];
						String maxArray[] = new String[0];
						
						//line with number of datasets the statistial data are based on
						int numberOfDevices = completeDataset.numberOfDatasets(deviceVector[i]);
						String numberOfDatasets[] = {"numberOfDatasets:",String.valueOf(numberOfDevices)};
						out.writeNext(numberOfDatasets);
							
						//Attributes/Features - line 2
						String featureArray[] = (String[])completeDataset.getAttributes().toArray(new String[0]);
						out.writeNext(addCellInFront(featureArray));

						//datatypes of Attributes/Features - line 3
						String dataTypes[] = new String[completeDataset.getDataTypes().toArray(new String[0]).length+1];
						out.writeNext(addCellInFront(dataTypes));
						
				
						//laden des aktuellen 'StatisticValuesPerDevice'-Objektes
						StatisticValuesPerDevice currentStatisticValuesPerDevice = statisticResults.get(deviceVector[i]);
//						System.out.println("oder hier?");
						//laden eines Array mit allen String an statistischen Werten des aktuellen Gerätes
						String statisticalValue[] = currentStatisticValuesPerDevice.getStatisticValues();
//						System.out.println("statisticalValueArray: " +Arrays.toString(statisticalValue));
							//jeweils den namen des statistischen Wertes gefolgt von den Werten zu den einzelnen Attributen 

							//=>
							//Schleife über die statistischen Werte
//						System.out.println("Test2 - Runde: "+currentStatisticValuesPerDevice.getDevicename());
						for(int j = 0; j<statisticalValue.length;j++){
							//Array zu jedem statistischen Wert
							Vector<String> outputStringVector = new Vector<String>();
							
							String currentStatisticalValue = statisticalValue[j];
//							System.out.println("Test2 - Name des statistischen Wertes: "+currentStatisticalValue);
							// Name des statistischen Wertes gefolgt von den entsprechenden Zahlen der jeweiligen Feature
							outputStringVector.add(statisticalValue[j]);
								//Schleife über die Attribute(Feature), da jeder statistische Wert eine eingene (ausgabe-)Zeile hat.
									//=>für jedes Gerät muss 'ersichtlich'sein Wieviel und welche statistischen Werte existieren bzw. eine Funktion/Variable existieren.
							
							String outputStringArray[] = getOutputlineOfStatisticalValue(currentStatisticValuesPerDevice, statisticalValue[j], featureArray, amountInMinMax);
							
							//TODO - unnötig geworden durch anpassung von "AmountInMinMax" Klasse
							if(outputStringArray[0].equals("min")){

								minArray = outputStringArray;
							}
							if( outputStringArray[0].equals("max")){

								maxArray = outputStringArray;
							}
							out.writeNext(outputStringArray);	
						}
						
						//Schleife über Geräte des trainingCompleteDatasets
						System.out.println("!!Schleife über Geräte des trainingCompleteDatasets!!");
						String[] testingDeviceVector = completeDatasetToCompare.getDeviceNames();
						for(int j = 0; j<testingDeviceVector.length; j++){
							DataPerDevice currentDeviceData = completeDatasetToCompare.get(testingDeviceVector[j]);
							//schleife über die einzelenn Tagesdatensätze eines Gerätes, vorrsaugesetzt es sind überhaupt mehrere Tagesdatensätze vorhanden
							for(int k = 0; k<currentDeviceData.size(); k++){
								DataPerDay currentDayData = currentDeviceData.get(k);
								System.out.println("!!!!!! currentDayData.size(): "+currentDayData.size());
								//Vector mit Gerätename vorangestellt, gefolgt von den Featurewerten
								String[] outputRow = generateOutputRow(testingDeviceVector[j], currentDayData, rowMinMax(minArray, maxArray, currentDayData));
								//adding data to feature/attribute weighting
								amountInMinMax.addData(currentDayData.getValues());
								//printing line
								out.writeNext(outputRow);
							}
						}
						
						out.writeNext(amountInMinMax.getResult());

						out.close();
						System.out.println("filegeneration done for: " +(i+1) +" - " +deviceVector[i]);
						//TODO - logging in apache log einfügen
						
						//fill returnHashMap
						Double values[] = amountInMinMax.getValues();
						for(int l = 0; l<featureArray.length; l++){
							returnHashMap.put(featureArray[l], values[l]);
						}
						
					}
					catch (Exception e) {
						System.err.println("Error: "+e.getMessage());
					}
				}
				return returnHashMap;
	}

	
	//special method to compare StatisticalResults of devices with other testing-devices
		public HashMap<String, Double> generateForDevice(String dataPathToSaveFiles, String deviceName, StatisticResults statisticResults, CompleteDataset completeDatasetToCompare ){
			//completeDataset on which the Statistical Results are based
			CompleteDataset completeDataset = statisticResults.getCompleteDataset();
			//return HashMap
			HashMap<String,Double> returnHashMap = new HashMap<String,Double>();
			
					//generate one File per DeviceType
						try{
							//path and filename
							FileWriter fstream = new FileWriter(dataPathToSaveFiles+File.separator+deviceName+".csv");

							//Semikolon als Seperator anstelle von Komma
							char outputSeperator = ';'; 
							CSVWriter out = new CSVWriter(fstream, outputSeperator);
							
							AmountInMinMax amountInMinMax = new AmountInMinMax();
							String minArray[] = new String[0];
							String maxArray[] = new String[0];
							
							//line with number of datasets the statistial data are based on
							int numberOfDevices = completeDataset.numberOfDatasets(deviceName);
							String numberOfDatasets[] = {"numberOfDatasets:",String.valueOf(numberOfDevices)};
							out.writeNext(numberOfDatasets);
								
							//Attributes/Features - line 2
							String featureArray[] = (String[])completeDataset.getAttributes().toArray(new String[0]);
							out.writeNext(addCellInFront(featureArray));
							System.out.println("featureArray[]: "+Arrays.toString(featureArray));

							//datatypes of Attributes/Features - line 3
							String dataTypes[] = (String[]) completeDataset.getDataTypes().toArray(new String[completeDataset.getDataTypes().size()]);
							out.writeNext(addCellInFront(dataTypes));
							System.out.println("dataTypes[]: "+Arrays.toString(dataTypes));
							
							
							//laden des aktuellen 'StatisticValuesPerDevice'-Objektes
							StatisticValuesPerDevice currentStatisticValuesPerDevice = statisticResults.get(deviceName);

							//laden eines Array mit allen String an statistischen Werten des aktuellen Gerätes
							String statisticalValue[] = currentStatisticValuesPerDevice.getStatisticValues();							
							
						//jeweils den namen des statistischen Wertes gefolgt von den Werten zu den einzelnen Attributen 
							//Schleife über die statistischen Werte
							for(int j = 0; j<statisticalValue.length;j++){					

								//jeweils Name des statistischen Wertes gefolgt von den entsprechenden Zahlen der jeweiligen Feature
								String outputStringArray[] = getOutputlineOfStatisticalValue(currentStatisticValuesPerDevice, statisticalValue[j], featureArray, amountInMinMax);
								
								//saving the 'min' an 'max' output Array to enable comparison to a CompleteDataset later on
								if(outputStringArray[0].equals("min")){
									minArray = outputStringArray;
								}
								if(outputStringArray[0].equals("max")){
									maxArray = outputStringArray;
								}
								out.writeNext(outputStringArray);	
							}
							
							//Schleife über Geräte des trainingCompleteDatasets
							System.out.println("!!Schleife über Gerätedaten des Gerätes: "+deviceName);
								DataPerDevice currentDeviceData = completeDatasetToCompare.get(deviceName);
								//schleife über die einzelenn Tagesdatensätze eines Gerätes, vorrasugesetzt es sind überhaupt mehrere Tagesdatensätze vorhanden
								for(int k = 0; k<currentDeviceData.size(); k++){
									DataPerDay currentDayData = currentDeviceData.get(k);
									System.out.println("!!!!!! currentDayData.size(): "+currentDayData.size());
									//Vector mit Gerätename vorangestellt, gefolgt von den Featurewerten
									String[] outputRow = new String[0];
									if(weighted){
										System.out.println("receiving weighting[]");
										Double weighting[] = attributeQuantifier.getWeighting(deviceName, featureArray);
										if(weighting == null)
											System.out.println("weighting[] = null");
										System.out.println("weighting receipt");
//										String minMax = rowMinMax(minArray, maxArray, currentDayData, weighting);
										outputRow = generateOutputRow(deviceName, currentDayData, rowMinMax(minArray, maxArray, currentDayData, weighting));
										System.out.println("weighted outputrow generated");
									} else {
										System.out.println("ELSE");
									outputRow = generateOutputRow(deviceName, currentDayData, rowMinMax(minArray, maxArray, currentDayData));
									}
									//adding data to feature/attribute weighting
									amountInMinMax.addData(currentDayData.getValues());
									//printing line
									System.out.println("outputRow: "+Arrays.toString(outputRow));
									out.writeNext(outputRow);
								}
							
							out.writeNext(amountInMinMax.getResult());
							
							out.close();
							System.out.println("filegeneration done for: " +deviceName);
							//TODO - logging in apache log einfügen
							
							//fill returnHashMap
							Double values[] = amountInMinMax.getValues();
							for(int l = 0; l<featureArray.length; l++){
								returnHashMap.put(featureArray[l], values[l]);
							}
							
						}
						catch (Exception e) {
							System.err.println("Error: "+e.getMessage());
						}
				return returnHashMap;
		}
		
		




		//special method to compare StatisticalResults of devices with other testing-devices
		public void generateWeightedForDevice(String dataPathToSaveFiles, String deviceName, StatisticResults statisticResults, CompleteDataset completeDatasetToCompare, AttributeQuantifier attributeQuantifier){
			this.weighted = true;
//			System.out.println("this.attributeQuanitifer.toString(): "+this.attributeQuantifier.toString());
//			System.out.println("attributeQuanitifer.toString(): "+attributeQuantifier.toString());
			this.attributeQuantifier = attributeQuantifier;
//			System.out.println("this.attributeQuanitifer.toString(): "+this.attributeQuantifier.toString());
//			System.out.println("attributeQuanitifer.toString(): "+attributeQuantifier.toString());
			
			this.generateForDevice(dataPathToSaveFiles, deviceName, statisticResults, completeDatasetToCompare);
		}


		//TODO - have to be modified to fit to boolean values
	//method to calculate the number of feature that are in the min max boundary
	private String rowMinMax(String[] minArray, String[] maxArray,	DataPerDay outputVectorPerDevice) {
		System.out.println("ROW_MIN_MAX");

		//transformation von Vector<String> -> Double[]
		Double[] analyseValues = outputVectorPerDevice.getValues();
		int isBetween = 0;
		int numberOfAnalysed = 0;
		for(int m = 0; m<analyseValues.length-1; m++){
			if((Double.parseDouble(minArray[m+1]) == Double.NaN) || (Double.parseDouble(maxArray[m+1]) == Double.NaN) || (analyseValues[m] == Double.NaN)){
				
			} else{
				numberOfAnalysed++;
			System.out.println("analyseValues[m]: "+analyseValues[m]);
				double currentDoubleValue = analyseValues[m];
			System.out.println("currentDoubleValue: "+currentDoubleValue);
				double currentMin = Double.parseDouble(minArray[m+1]);
			System.out.println("currentMin: "+currentMin);
				double currentMax = Double.parseDouble(maxArray[m+1]);
			System.out.println("currentMax: "+currentMax);
				if(currentMin <= currentDoubleValue && currentDoubleValue <= currentMax){
					isBetween++;
				}
			}
			System.out.println("m: "+m +" von: "+(analyseValues.length-1) +" fertig");			
		}
		System.out.println("for-schleife fertig");
		String returnString = "in [min/max]"+String.valueOf(isBetween)+" of "+String.valueOf(numberOfAnalysed);
		System.out.println(returnString);
		return returnString;
	}
	
	//method to calculate the number of feature that are in the min max boundary with integrated weighting
	private String rowMinMax(String[] minArray, String[] maxArray, DataPerDay outputVectorPerDevice, Double[] weighting){
		//transformation von Vector<String> -> Double[]
		Double[] analyseValues = outputVectorPerDevice.getValues();
		
		int isBetween = 0;
		Double weightedIsBetween = new Double(0);//0.0;
		for(int m = 0; m<analyseValues.length-1; m++){
			double currentDoubleValue = analyseValues[m];
			double currentMin = Double.parseDouble(minArray[m+1]);
			double currentMax = Double.parseDouble(maxArray[m+1]);
			if(currentMin <= currentDoubleValue && currentDoubleValue <= currentMax){
				isBetween++;
				if(weighting != null){
					if(weighting[m] != Double.NaN){
						System.out.println("weighting[m]: "+weighting[m]);
						weightedIsBetween = weightedIsBetween + weighting[m];
						System.out.println("weightedIsBetween: "+weightedIsBetween);
					}
				}
			}
		}
		double ratioDouble = Double.NaN;
		double ratio = ((double)isBetween)/((double)(outputVectorPerDevice.size()-1));
		String returnString = "in [min/max]"+String.valueOf(isBetween)+" of "+String.valueOf(outputVectorPerDevice.size()-1) +" [ratio: " +ratio +"] ";
		if(weighting == null)
			returnString = returnString+" - weighted: WARNING - no existing weighting data";
		else
			ratioDouble = weightedIsBetween/sumOfArrayValues(weighting);
			//TODO - round 'ratioDouble' to two digits
			returnString = returnString+" - weighted: "+weightedIsBetween.toString()+" of "+sumOfArrayValues(weighting).toString() +" [ratio: " +ratioDouble +"] ";
		return returnString;		
	}
	
	
	
	//method to create the outputline of one statistical value
	//jeweils Name des statistischen Wertes gefolgt von den entsprechenden Zahlen der jeweiligen Feature
	//Schleife über die Attribute(Feature), da jeder statistische Wert eine eingene (ausgabe-)Zeile hat.
	private String[] getOutputlineOfStatisticalValue(StatisticValuesPerDevice currentStatisticValuesPerDevice,String currentStatisticalValue, String featureArray[], AmountInMinMax amountInMinMax){
		
		String outputStringArray[] = new String[featureArray.length+1];
		outputStringArray[0] = currentStatisticalValue;
		
		Double doubleValues[] = new Double[featureArray.length];
		
		//Schleife über die Features/Attribute
		for(int i = 0; i<featureArray.length; i++){
			//laden des 'StatisticalValuesPerAttribute' Objektes
			StatisticValuesPerAttribute currentStatisticValuesPerAttribute = currentStatisticValuesPerDevice.get(featureArray[i]);
			if(currentStatisticValuesPerAttribute.get(currentStatisticalValue) != null){
				outputStringArray[i+1] =  String.valueOf(currentStatisticValuesPerAttribute.get(currentStatisticalValue));
				doubleValues[i] = currentStatisticValuesPerAttribute.get(currentStatisticalValue);
			}
			else{
				outputStringArray[i+1] = "-";
				doubleValues[i] = Double.NaN;
			}			
		}
		if(currentStatisticalValue.equalsIgnoreCase("min"))
				amountInMinMax.setMinValues(doubleValues);
		if(currentStatisticalValue.equalsIgnoreCase("max"))
			amountInMinMax.setMaxValues(doubleValues);		
		return outputStringArray;
	}
	
	//method to add a cell with '-' at the beginning of the array
	private String[] addCellInFront(String[] inputArray){
		String returnArray[] = new String[inputArray.length+1];
		returnArray[0] = "-";
		System.arraycopy(inputArray, 0, returnArray, 1, returnArray.length-1);
		return returnArray;
	}
	
	
	private Double[] stringArrayToDoubleArray(String[] inputArray){
		Double returnArray[] = new Double[inputArray.length];
		for (int i = 0; i < inputArray.length; i++){
		    try {
		    	returnArray[i] = Double.parseDouble(inputArray[i]);
		    } catch (NumberFormatException e) {
		    	returnArray[i] = Double.NaN;
		    }
		}
		return returnArray;
	}
	
	//results in: [devicename, (DATA,devicename), statisticResult]
	private String[] generateOutputRow(String deviceName, Vector<String> values, String statisticResult){
//		System.out.println("generating outputrow for: "+deviceName);
		Vector<String> returnVector = new Vector<String>();
		
		returnVector.add(deviceName);
		returnVector.addAll(values);
		returnVector.add(statisticResult);
		
		System.out.println("Result for: " +deviceName +" - " +statisticResult);
		
		String outputArray[] = new String[returnVector.size()];
		outputArray = returnVector.toArray(outputArray);
		return outputArray;
	}
	
	private Double sumOfArrayValues(Double[] values){
		Double sum = 0.0;
		for (int i = 0; i<values.length; i++) {
			sum += values[i];
		}
		return sum;
	}
	
//	//Ausgabe der Vergleichsdaten des Complete Dataset
//	private Vector<String> writeDataOfCompleteDataset(CompleteDataset completeDatasetToCompare){
//		Set<String> testingDevices = completeDatasetToCompare.keySet();
//		String[] testingDeviceVector = Arrays.copyOf(testingDevices.toArray(), testingDevices.toArray().length,  String[].class);
//		for(int j = 0; j<testingDeviceVector.length; j++){
//			DataPerDevice currentDeviceData = completeDatasetToCompare.get(testingDeviceVector[j]);
//			//schleife über die einzelenn Tagesdatensätze eines Gerätes, vorrasugesetzt es sind überhaupt mehrere Tagesdatensätze vorhanden
//			for(int k = 0; k<currentDeviceData.size(); k++){
//				DataPerDay currentDayData = currentDeviceData.get(k);
//				//Vector mit Gerätename vorangestellt, gefolgt von den Featurewerten
//				Vector<String> outputVectorPerDevice = new Vector<String>();
//				outputVectorPerDevice.add(testingDeviceVector[j]);
//				//anhängen der DAten eines Tages an den ausgabeVector - VORSICHT! sehr fehleranfällig. es findet keine gesicherte Zuordnung zwischen Feature und Wert statt
//				outputVectorPerDevice.addAll(currentDayData);
//					outputVectorPerDevice.add("add min/max ratio here");
//					String outputArray[] = outputVectorPerDevice.toArray(new String[outputVectorPerDevice.size()]);
//					
//					int isBetween = rowMinMax(minArray,	maxArray, outputVectorPerDevice);
//					
//					outputArray[outputArray.length-1] = "in [min/max]".concat(String.valueOf(isBetween).concat("of").concat(String.valueOf(outputArray.length-3)));
//
//				
//				amountInMinMax.addData(outputArray);
//				out.writeNext(outputArray);
//			}
//	}
}
