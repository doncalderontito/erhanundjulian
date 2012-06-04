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

/**
 * provides the functionality to read in a ARFF file and returns a 'CompleteDataset'-object
 * 
 * @author Kristopher
 *
 */

public class ARFFtoCD {
	
	
	//TODO - Contructor
	public ARFFtoCD(){
	}
	
	
	//primary method	
	public CompleteDataset getCompleteDatasetByPath(String arff) {
		CompleteDataset returnCD;
		
		/*I:	read in ARFF data
		 * 
		 * 			- Devices
		 * 			- Attributes
		 * 			- Values
		 */
		

		//Attribute-/Featureliste:
		String[] attributes = null;
		//datatypes of Attributes/Features
		String[] datatype =  null;
		//Instances is inherit of AbstractList and contains the datasets per day in kind of objects of the Instance class
		Instances data = null;
		//saves the different devicenames
		Vector<String> deviceVector = null;
		
		
		/*
		 *		- Reading of ARFF Files
		 * 		Alternativer Ansatz über WEKA ARFF - bisher nicht funktionsfähig
		 */		/*
		ArffLoader loaderTest = new ArffLoader();
		
		//FileReader fstreamRead2 = new FileReader(FileMapper.testingPath+File.separator+FileMapper.testingArff);
		//BufferedReader reader = new BufferedReader(fstreamRead2);
		File testfile = new File(FileMapper.testingPath+File.separator+FileMapper.testingArff);
		try{
			loaderTest.setFile(testfile);
			Instances data = loaderTest.getStructure();
				*/
	
		
			
		/*
		 * 		- Reading of ARFF Files
		 */
			try {		
				FileReader fstreamRead = new FileReader(arff);				
				
				BufferedReader reader = new BufferedReader(fstreamRead);
				data = new Instances(reader);			
				reader.close();
				//setting class attribute
				data.setClassIndex(data.numAttributes()-1);
				//einlesen der Werte zu den Attributen


				//check of fully read in datalines
//				Enumeration en = data.enumerateInstances();
//				int numberOfElements = 0;
//				while(en.hasMoreElements()){
//					numberOfElements++;
//					en.nextElement();
//				}
//				System.out.println("anzahl der Elemente duch Enumeration: "+numberOfElements);
					

		/*
		 * 		-Values
		 * 		Alternativer Ansatz mit einer Schleife 
		 */		/*	
				//herausfinden was die einzelnen Instances sind
				//ERGEBNIS: beinhaltet die Daten zu den Attributen, also die numerischen und boolschen Werte 
				System.out.println("INSTANCE TEST");
				Instance testInstance = null;
				for(int i = 0; i<data.numInstances(); i++){
					testInstance = data.instance(i);
					System.out.println("AttributeStats "+i+":");
					System.out.println(testInstance);
				}
				System.out.println("INSTANCE TEST");
				  */
				
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
				
				
				/*
				//Test der vorhandenen Klasse AttributeStats
				//ERGEBNIS: gibt statistische Werte zu jedem Attribut aus
				//TODO - welche Werte das im einzelnen sind ist noch herauszufinden
				AttributeStats teststats = null;
				for(int i = 0; i<10; i++){
					teststats = data.attributeStats(i);
					System.out.println("AttributeStats "+i+":");
					System.out.println(teststats);
				}
				 */		
				
				
			} catch (FileNotFoundException e) { //für den FileReader
				System.out.println("FileNotFoundException");
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) { //für Instances
				// TODO Auto-generated catch block
				System.out.println("IOException");
				e.printStackTrace();
			}
		

				//erzeugen der HashMap die alle Daten enthält
			returnCD = new CompleteDataset(/*valuesOfFeatures*/data, deviceVector, attributes, datatype);
			return returnCD;
	}

}
