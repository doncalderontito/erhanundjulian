package de.tud.kom.challenge.statistic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.print.attribute.HashAttributeSet;

import de.tud.kom.challenge.arff.FeatureExtractor;
import de.tud.kom.challenge.arff.FileMapper;
import de.tud.kom.challenge.processors.FeatureProcessor;
import de.tud.kom.challenge.util.ParalellTasks;
import de.tud.kom.challenge.util.Progress;
import de.tud.kom.challenge.util.Task;

/**
 * Initialises the serveral analyse functions
 * 
 * @author Kristopher
 *
 */

public class Initialiser {
	
	//String = DeviceName; HashMap<String,Double> = <Attribute,Value>
	private AttributeQuantifier attributeQuantifier;
	
	
	//constructor
	public Initialiser(){
		attributeQuantifier = new AttributeQuantifier();
	}

	
	//cross-validation between the trainingdata
	public void crossValidation() {
		//set up attributeQuantifier
		//read complete Trainingsdataset
		CompleteDataset trainingDataset = new ARFFtoCD().getCompleteDatasetByPath(FileMapper.trainingPath+File.separator+FileMapper.trainingArff);
		//extract random datasets of read complete dataset to a new one
		Extractor ex = new Extractor(trainingDataset);
	
		CompleteDataset extractedDataset = ex.getExtractedDataset();
		//build/get statistic result of remaining dataset
		StatisticResults remainingStatisticResults = ex.getRemainingStatisticResults();
			//comparing original extracted data and reduce effect of data not in min/max intervall
			//TODO - seperation of different devices
		String deviceNames[] = remainingStatisticResults.getDeviceNames();
		System.out.println("deviceNAmes: "+Arrays.toString(deviceNames));
		for(int i = 0; i<deviceNames.length; i++){
			System.out.println("i: "+i);
//			CompleteDataset partialCompleteDataset = new CompleteDataset(extractedDataset.getAttributes(), extractedDataset.getDataTypes());
//			partialCompleteDataset.put(deviceNames[i], extractedDataset.get(deviceNames[i]));
			
			CompleteDataset currentCD = ex.getExtractedDatasetOfDevice(deviceNames[i]);
			System.out.println("devicename of currentCD: "+Arrays.toString(currentCD.getDeviceNames()));
			SFFgenerator newSFF = new SFFgenerator();
			System.out.println("1");
			HashMap<String, Double> deviceHashMap = newSFF.generateForDevice("statistic"+File.separator+"crossvalidation"+File.separator+"normalResult", deviceNames[i], remainingStatisticResults, extractedDataset);
			System.out.println("2");
			attributeQuantifier.highValuesAreBeneficial(deviceNames[i], deviceHashMap);
			System.out.println("3");
		}
		
		//ausgabe der Daten
		System.out.println("4");
//		for(Map.Entry e : attributeQuantifier.entrySet()){
//			  System.out.println(e.getKey() +"      " +attributeQuantifier.get(e.getKey()).size());
//			  System.out.println(e.getValue());
//		}
	}
	
	//cross-validation to modified trainingdata
	public void crossValidationWithVariation(Vector<FeatureProcessor> processors){
		//set up attributeQuantifier
		//read complete Trainingsdataset
		CompleteDataset trainingDataset = new ARFFtoCD().getCompleteDatasetByPath(FileMapper.trainingPath+File.separator+FileMapper.trainingArff);
		
		StatisticResults trainingStatisticResults = new StatisticResults(trainingDataset);
		//comparing manipulated data TODO: and increasing effect of data not in min/max intervall
		//build manipulated files
		new Manipulator().generateSomeFiles();
		//machine learner on manipulated files
		//damit später für jedes Gerät eine ARFF erstellt werden kann
		//dazu schleife über die GEräte bzw. Ordner
		//Array mit Gerätenamen
		String deviceNames[] = trainingDataset.getDeviceNames();
		for(int i = 0; i<deviceNames.length; i++){
			String pathToCurrentDevice = "statistic"+File.separator+"crossvalidation"+File.separator+"manipulated"+File.separator+deviceNames[i];
			//feature extractor individual for every device. the target is to generate the Attributevalues, not to identify
			FeatureExtractor fe = new FeatureExtractor(processors);
			fe.createCustomizedTrainingSet(pathToCurrentDevice);
			//CompleteDataset of generated ARFF based on the manipulated data 
			CompleteDataset currentManipulatedCD = new ARFFtoCD().getCompleteDatasetByPath(pathToCurrentDevice+File.separator+FileMapper.trainingArff);
			//building StatisticResults containing only the relevant device
			CompleteDataset fullTrainingDataset = new ARFFtoCD().getCompleteDatasetByPath(FileMapper.trainingPath+File.separator+FileMapper.trainingArff);
			StatisticResults relevantStatisticResults = new StatisticResults(fullTrainingDataset);
			relevantStatisticResults.removeAllDevicesExcept(deviceNames[i]);
			//generate the SFF
			SFFgenerator newSFF = new SFFgenerator();
			HashMap<String, Double> deviceHashMap = newSFF.generateComplete("statistic"+File.separator+"crossvalidation"+File.separator+"manipulatedResult", relevantStatisticResults, currentManipulatedCD);
			System.out.println("deviceNames[i]: "+deviceNames[i] +"  -  deviceHashMap: " +deviceHashMap.size());
			attributeQuantifier.lowValuesAreBeneficial(deviceNames[i], deviceHashMap);
		}
		//ausgabe der Daten
		for(Map.Entry e : attributeQuantifier.entrySet()){
		  System.out.println(e.getKey() +"      " +attributeQuantifier.get(e.getKey()).size());
		  System.out.println(e.getValue());
		}
	}
	
	
	//validation of Features/Attributes regarding their relevance for error detection based on a set of reference Devices
	//the devices are located in statistic/reference
	public void validateToReferenceDevices(FeatureExtractor fe) {		
		//ARFF für reference Daten erstellen
			//mit trainings ARFF generator um zuordnung der Featurewerte zu den Geräten zu erhalten
		String path = "statistic" + File.separator + "reference";
		fe.createCustomizedTrainingSet(path);
		
//		//ARFF für trainingsdaten notwendig
//		if(/*trainingsdaten existieren nicht*/false){
//			fe.createTrainingSet();
//		}
		
		//StatisticResults der Trainingsdaten erstellen
		CompleteDataset trainingDataset = new ARFFtoCD().getCompleteDatasetByPath(FileMapper.trainingPath+File.separator+FileMapper.trainingArff);
//		StatisticResults trainingStatisticResults = new StatisticResults(trainingDataset);
		
		//statistical comparator über beide Datensätze laufen lassen
		String deviceNames[] = trainingDataset.getDeviceNames();
		for(int i = 0; i<deviceNames.length; i++){
			String pathToCurrentDevice = "statistic"+File.separator+"crossvalidation"+File.separator+"manipulated"+File.separator+deviceNames[i];
			//feature extractor individual for every device. the target is to generate the Attributevalues, not to identify
//			FeatureExtractor fe = new FeatureExtractor(processors);
			fe.createCustomizedTrainingSet(pathToCurrentDevice);
			//CompleteDataset of generated ARFF based on the reference data 
			CompleteDataset referenceCompleteDataset = new ARFFtoCD().getCompleteDatasetByPath("statistic"+File.separator+"reference"+File.separator+FileMapper.trainingArff);
			//building StatisticResults containing only the relevant device
			CompleteDataset fullTrainingDataset = new ARFFtoCD().getCompleteDatasetByPath(FileMapper.trainingPath+File.separator+FileMapper.trainingArff);
			StatisticResults relevantStatisticResults = new StatisticResults(fullTrainingDataset);
			relevantStatisticResults.removeAllDevicesExcept(deviceNames[i]);
			//generate the SFF
			SFFgenerator newSFF = new SFFgenerator();
			HashMap<String, Double> deviceHashMap = newSFF.generateComplete("statistic"+File.separator+"referenceResult", relevantStatisticResults, referenceCompleteDataset);
			System.out.println("deviceNames[i]: "+deviceNames[i] +"  -  deviceHashMap: " +deviceHashMap.size());
			attributeQuantifier.lowValuesAreBeneficial(deviceNames[i], deviceHashMap);
		}
		//ausgabe der Daten
		for(Map.Entry e : attributeQuantifier.entrySet()){
		  System.out.println(e.getKey() +"      " +attributeQuantifier.get(e.getKey()).size());
		  System.out.println(e.getValue());
		}	
	}


	public void analyseTestingData(Vector<FeatureProcessor> processors, FeatureExtractor fe) {

		//process of 'a)'
		this.crossValidation();
		//process of 'b)'
		this.crossValidationWithVariation(processors);
		//process of 'c)'
		this.validateToReferenceDevices(fe);
	//process of 'd)'
		//building complete StatisticResults based on training Dataset
		CompleteDataset fullTrainingDataset = new ARFFtoCD().getCompleteDatasetByPath(FileMapper.trainingPath+File.separator+FileMapper.trainingArff);
		StatisticResults relevantStatisticResults = new StatisticResults(fullTrainingDataset);

		//generate classified testing.ARFF
		//WARNING: generation direct before reading leads to 'java.io.FileNotFoundException' -> 'FileNotFoundException'
		ClassifiedTestingARFFGenerator CTARFFG = new ClassifiedTestingARFFGenerator();
		CTARFFG.generateTestingARFF();
		//read in testing ARFF
		CompleteDataset testingCompleteDataset = new ARFFtoCD().getCompleteDatasetByPath("statistic"+File.separator+"classifiedTestingARFF"+File.separator+"classifiedTesting.arff");
		
		//compare
		//damit später für jedes Gerät eine ARFF erstellt werden kann
		//dazu schleife über die GEräte bzw. Ordner
		//Array mit Gerätenamen
		String deviceNames[] = testingCompleteDataset.getDeviceNames();
		for(int i = 0; i<deviceNames.length; i++){			
			SFFgenerator newSFF = new SFFgenerator();
//			System.out.println("this.attributeQuanitifer.toString(): "+this.attributeQuantifier.toString());
//			System.out.println("attributeQuanitifer.toString(): "+attributeQuantifier.toString());
			newSFF.generateWeightedForDevice("statistic"+File.separator+"testingResult", deviceNames[i], relevantStatisticResults, testingCompleteDataset, this.attributeQuantifier);	
		}
		//TODO - integrate weighting
		// TODO Auto-generated method stub
//		System.out.println("'analyseTestingData() :'");
//		for(Map.Entry e : attributeQuantifier.entrySet()){
//			  System.out.println(e.getKey() +"      " +attributeQuantifier.get(e.getKey()).size());
//			  System.out.println(e.getValue());
//		}	
		
	}
	
	
	
	

}
