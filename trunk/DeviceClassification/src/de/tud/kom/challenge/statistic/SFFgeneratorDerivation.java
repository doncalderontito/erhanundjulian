package de.tud.kom.challenge.statistic;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Set;
import java.util.Vector;

import au.com.bytecode.opencsv.CSVWriter;
import de.tud.kom.challenge.statistic.StatisticValuesPerAttribute;
import de.tud.kom.challenge.statistic.StatisticValuesPerDevice;
import de.tud.kom.challenge.arff.FileMapper;

public class SFFgeneratorDerivation {

	//special constructor to compare devices with devices of same kind which are manipulated
		public SFFgeneratorDerivation(String dataPathToSaveFiles, StatisticResults statisticResults, CompleteDataset testingCD ){
			CompleteDataset completeDataset = statisticResults.getCompleteDataset();
			
			//TODO - vielleicht einfach eine eigene Ausgabefunktion schreiben, und wieder zu BufferedWriter wechseln
			// Ausgabe als XML alternativ
			
					Set<String> devices = completeDataset.keySet();
					String[] deviceVector = Arrays.copyOf(devices.toArray(), devices.toArray().length,  String[].class);
					//generate one File per Gerätetyp/Device
					for(int i = 0; i<deviceVector.length; i++){
						
						try{
							//path and filename
							FileWriter fstream = null;
								
							if(dataPathToSaveFiles == FileMapper.testingPath){
								fstream = new FileWriter(FileMapper.testingPath+File.separator+deviceVector[i]+".csv");
							}else if(dataPathToSaveFiles == FileMapper.trainingPath){
								fstream = new FileWriter(FileMapper.trainingPath+File.separator+deviceVector[i]+".csv");
							}else{
								//
								System.out.println("illegal argument given to method: createStatisticFile()");
							}
							
							//Semikolon als Seperator anstelle von Komma
							char outputSeperator = ';'; 
							CSVWriter out = new CSVWriter(fstream, outputSeperator);
							
							AmountInMinMax amountInMinMax = new AmountInMinMax();
							String minArray[] = new String[0];
							String maxArray[] = new String[0];

							
							
							//BufferedWriter democode
							/*
							BufferedWriter out = new BufferedWriter(fstream);
							
							out.append("Daten die in die Datei sollen:");
							out.newLine();
							out.append("Größe des Arrays vom Typ 'Object': "+testarray.length);
							out.newLine();
							for(int i = 0; i<testarray.length; i++){
								String line = testarray[i].toString();
								out.append(i+". Arrayzeile: "+line);
								out.newLine();
							}
							*/
							
							//TODO!!! an richtiger stelle einfügen für einen eigene Zeile je Attribut.
								// dazu besseres doumentieren der verknüpften Schleife
								// alternativ StatisticValuePerAttribute abändern, so dass die anzhal der Datensätze einfach abgelegt wird
							//Zeile mit der Anzahl der Datensätze
							String numberOfDatasets[] = {"numberOfDatasets:","bla"};
							out.writeNext(numberOfDatasets);
							
						
							
							//Features
							String featureArray[] = (String[])completeDataset.getAttributes().toArray(new String[0]);
							//leeres Feld("-") voran gestellt zur besseren Visualisierung in Excel 
							String attributesForOutputline[] = new String[featureArray.length+1];
							attributesForOutputline[0] = "-";
							System.arraycopy(featureArray, 0, attributesForOutputline, 1, attributesForOutputline.length-1);
							out.writeNext(attributesForOutputline);

							
							//Datentypen der Features - kommasepariert:		datatype[]
							//leeres Feld("-") voran gestellt zur besseren Visualisierung in Excel 
							String dataTypes[] = new String[completeDataset.getDataTypes().toArray(new String[0]).length+1];
							dataTypes[0] = "-";
							System.arraycopy((String[])completeDataset.getDataTypes().toArray(new String[0]), 0, dataTypes, 1, dataTypes.length-1);
							out.writeNext(dataTypes);
							
							

//							System.out.println("hier?");
							//laden des aktuellen 'StatisticValuesPerDevice'-Objektes
							StatisticValuesPerDevice currentStatisticValuesPerDevice = statisticResults.get(deviceVector[i]);
//							System.out.println("oder hier?");
							//laden eines Array mit allen String an statistischen Werten des aktuellen Gerätes
							String statisticalValue[] = currentStatisticValuesPerDevice.getStatisticValues();
//							System.out.println("statisticalValueArray: " +Arrays.toString(statisticalValue));
								//jeweils den namen des statistischen Wertes gefolgt von den Werten zu den einzelnen Attributen 

								//=>
								//Schleife über die statistischen Werte
//							System.out.println("Test2 - Runde: "+currentStatisticValuesPerDevice.getDevicename());
							for(int j = 0; j<statisticalValue.length;j++){
								//Array zu jedem statistischen Wert
								Vector<String> outputStringVector = new Vector<String>();
								
								String currentStatisticalValue = statisticalValue[j];
//								System.out.println("Test2 - Name des statistischen Wertes: "+currentStatisticalValue);
									//jeweils Name des statistischen Wertes gefolgt von den entsprechenden Zahlen der jeweiligen Feature
								outputStringVector.add(currentStatisticalValue);
									//Schleife über die Attribute(Feature), da jeder statistische Wert eine eingene (ausgabe-)Zeile hat.
										//=>für jedes Gerät muss 'ersichtlich'sein Wieviel und welche statistischen Werte existieren bzw. eine Funktion/Variable existieren.
								
								Double values[] = new Double[0];
								
								String outputStringArray[] = getOutputlineOfStatisticalValue(currentStatisticValuesPerDevice, currentStatisticalValue, featureArray, values);
								
								if(outputStringArray[0].equals("min")){
									amountInMinMax.addData(values);
									minArray = outputStringArray;
								}
								if( outputStringArray[0].equals("max")){
									amountInMinMax.addData(values);
									maxArray = outputStringArray;
								}
								out.writeNext(outputStringArray);	
							}
							
							//Schleife über Geräte des trainingCompleteDatasets
							System.out.println("!!Schleife über Geräte des trainingCompleteDatasets!!");
							Set<String> testingDevices = testingCD.keySet();
							String[] testingDeviceVector = Arrays.copyOf(testingDevices.toArray(), testingDevices.toArray().length,  String[].class);
							for(int j = 0; j<testingDeviceVector.length; j++){
								DataPerDevice currentDeviceData = testingCD.get(testingDeviceVector[j]);
								//schleife über die einzelenn Tagesdatensätze eines Gerätes, vorrasugesetzt es sind überhaupt mehrere Tagesdatensätze vorhanden
								for(int k = 0; k<currentDeviceData.size(); k++){
									DataPerDay currentDayData = currentDeviceData.get(k);
									//TODO - manipulation here!!!
									//schleife über die 12 manipulationen
									String manipulationStringArray[] = {"+5%","-5%","+10%","-10%","+20%","-20%","+5","+10","+50","-5","-10","-50"};
									Double factorArray[] = {1.05, 0.95, 1.1, 0.9, 1.2, 0.8, 5.0, 10.0, 50.0, 5.0, 10.0, 50.0};
									for(int l = 0; l<12; l++){
										//daten in double wandeln, 
										Vector<String> toManipulate = currentDayData;
										toManipulate.removeElementAt(toManipulate.size()-1);
										String toManipulateArray[] = new String[toManipulate.size()];
										toManipulateArray = toManipulate.toArray(toManipulateArray);
										//Vector mit manipulierten Daten; Gerätename vorangestellt, gefolgt von den Featurewerten
										Vector<String> manipulatedData = new Vector<String>();
										manipulatedData.add(testingDeviceVector[j].concat(manipulationStringArray[l]));
										//Transformation von String[] -> Double[] & eigentliche Manipulation
										for(int m = 0; m<toManipulateArray.length; m++){
											double currentDoubleValue = Double.parseDouble(toManipulateArray[m]);
											double result = 0;
											//daten manipulieren
											if(l<6){
												result = currentDoubleValue * factorArray[l];
											}else if(l<9){
												result = currentDoubleValue + factorArray[l];
											}else if(l<12){
												result = currentDoubleValue - factorArray[l];
											}
											//daten in String wandeln und anhängen
											 manipulatedData.add(String.valueOf(result));
										}
											
										manipulatedData.add("add min/max ratio here");
											String outputArray[] = new String[manipulatedData.size()];
											outputArray = manipulatedData.toArray(outputArray);
											
											int isBetween = rowMinMax(minArray,	maxArray, manipulatedData);
											
											outputArray[outputArray.length-1] = "in [min/max]".concat(String.valueOf(isBetween).concat("of").concat(String.valueOf(outputArray.length-3)));
													
										//TODO This was broken, but might be crucial:
											// amountInMinMax.addData(outputArray);
										out.writeNext(outputArray);
									}
								}
							}
							
								//abfrage ob entsprechendes Feature vorhanden, ansonsten "-" einfügen
							
							//Array aus dem Vector je Gerät
							
							
//							System.out.println("'for' außen fertig");
							
							
							out.writeNext(amountInMinMax.getResult());
//							System.out.println(Arrays.toString(amountInMinMax.getResult()));
							out.close();
							System.out.println("filegeneration done for: " +(i+1) +" - " +deviceVector[i]);
							//TODO - logging in apache log einfügen
							
						}
						catch (Exception e) {
							System.err.println("Error: "+e.getMessage());
						}
					}
		}




		private int rowMinMax(String[] minArray, String[] maxArray, Vector<String> outputVectorPerDevice) {
			//transformation von Vector<String> -> Double[]
			Vector<String> minMaxVector = outputVectorPerDevice;
			minMaxVector.removeElementAt(minMaxVector.size()-1);
			minMaxVector.removeElementAt(0);
			String minMaxArray[] = new String[minMaxVector.size()];
			minMaxArray = minMaxVector.toArray(minMaxArray);
			int isBetween = 0;
			//Transformation von String[] -> Double[] & eigentliche Analyse 
			for(int m = 0; m<minMaxArray.length-1; m++){
				double currentDoubleValue = Double.parseDouble(minMaxArray[m]);
				double currentMin = Double.parseDouble(minArray[m+1]);
				double currentMax = Double.parseDouble(maxArray[m+1]);
				if(currentMin <= currentDoubleValue && currentDoubleValue <= currentMax){
					isBetween++;
				}
			}
			return isBetween;
		}
		
		
		
		
		//method to create the outputline of one statistical value
		private String[] getOutputlineOfStatisticalValue(StatisticValuesPerDevice currentStatisticValuesPerDevice,String currentStatisticalValue, String featureArray[], Double[] values){
			 
			String outputStringArray[] = new String[featureArray.length+1];
			outputStringArray[0] = currentStatisticalValue;
			values = new Double[featureArray.length];
			
			//Schleife über die Features
			for(int i = 0; i<featureArray.length; i++){
				//laden des 'StatisticalValuesPerAttribute' Obektes
				StatisticValuesPerAttribute currentStatisticValuesPerAttribute = currentStatisticValuesPerDevice.get(featureArray[i]);
				if(currentStatisticValuesPerAttribute.get(currentStatisticalValue) != null){
					outputStringArray[i+1] =  String.valueOf(currentStatisticValuesPerAttribute.get(currentStatisticalValue));
					values[i] = currentStatisticValuesPerAttribute.get(currentStatisticalValue);
				}
				else{
					outputStringArray[i+1] = "-";
				}			
			}
			
			return outputStringArray;
		}
		
	}
