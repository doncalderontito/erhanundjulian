package de.tud.kom.challenge.statistic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import weka.core.Attribute;
import weka.core.Instances;
import de.tud.kom.challenge.arff.FileMapper;

public class StatisticalComparatorDerivation {
	
	//constructor
	public StatisticalComparatorDerivation(){
		
		//'CompleteDataset' und 'StatisticResults' aus training-ARFF generieren
		//Featureliste:
		String[] attributes = null;
		//Datentypen der Features
		String[] datatype =  null;
		//von AbstractList erbende Struktur die die einzelnen Tagessätze in Form von Instances enthält. 
		Instances data = null;
		//Speichern der Gerätemöglichkeiten aus der ARFF-Datei ( dort unter dem Attribut "deviceName" )
		Vector<String> deviceVector = null;

		/*
		 * 		- Reading of ARFF Files
		 */
		Object[] valuesOfFeatures = null;
			try {
				FileReader fstreamRead = null;

					fstreamRead = new FileReader(FileMapper.trainingPath+File.separator+FileMapper.trainingArff);
				
				BufferedReader reader = new BufferedReader(fstreamRead);
				data = new Instances(reader);			
				reader.close();
				
			
				
				//setting class attribute
				data.setClassIndex(data.numAttributes()-1);
				//einlesen der Werte zu den Attributen

		/*
		 * 		-Values
		 */				
//				valuesOfFeatures = data.toArray();
				
				//TODO abgleich erstellen mit alternativer Auslesemethode!!!
				Enumeration en = data.enumerateInstances();
				int numberOfElements = 0;
				while(en.hasMoreElements()){
					numberOfElements++;
					en.nextElement();
				}
				System.out.println("anzahl der Elemente duch Enumeration: "+numberOfElements);
	
		/*
		 * 		- Devices
		 */	
				//Zugriff auf die Gerätetypen/Devices
				Attribute  devices = data.classAttribute();
				//Speichern der Gerätemöglichkeiten aus der ARFF-Datei ( dort unter dem Attribut "deviceName"
				deviceVector = new Vector<String>();
		
				Enumeration extractedDevices = devices.enumerateValues();
				
				while(extractedDevices.hasMoreElements()){
					deviceVector.add(extractedDevices.nextElement().toString());
				}
				System.out.println("deviceVector:");
				System.out.println(deviceVector);
				//es ist unerlässlich den Vektor zu trimen für die nachfolgende Verarbeitung.
				deviceVector.trimToSize();
				System.out.println("groesse des deviceVector: "+deviceVector.capacity());
//				Object[] testArray = deviceVector.toArray();
//			
//				System.out.println("Objectarray: "+testArray);
//				System.out.println("groesse des testArray: "+testArray.length);
				
		/*
		 * 		- Attributes
		 */
				//Zugriff auf die Attribute/Features und deren Datentyp
				ArrayList aList = Collections.list(data.enumerateAttributes());
				
				//create String array for attributes with corresponding size
				attributes =  new String[aList.size()];				
				//create String array for datatypes with corresponding size
				datatype =  new String[aList.size()];
				
				//fill the string arrays by seperating the read in data 
				for(int i = 0; i < aList.size(); i++){
					String toSplit = aList.get(i).toString();
					String[] splittet = toSplit.split(" ");
					attributes[i] = splittet[1];
					datatype[i] = splittet[2];					
				}				
				
			} catch (FileNotFoundException e) { //für den FileReader
				System.out.println("FileNotFoundException");
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) { //für Instances
				// TODO Auto-generated catch block
				System.out.println("IOException");
				e.printStackTrace();
			}
		
		
		//II:	process data
				//erzeugen der HashMap die alle Daten enthält
				CompleteDataset completeDataset = new CompleteDataset(/*valuesOfFeatures*/data, deviceVector, attributes, datatype);
				
				StatisticResults statisticResults = new StatisticResults(completeDataset);
				
				
				
		//'CompleteDataset' aus testing-ARFF generieren
		CompleteDataset testingCompleteDataset = new ARFFtoCD().getCompleteDatasetByPath(FileMapper.testingPath);
		//übergeben an SSFgenerator
		SFFgeneratorDerivation newSFF = new SFFgeneratorDerivation(FileMapper.trainingPath, statisticResults, testingCompleteDataset);
	}

}
