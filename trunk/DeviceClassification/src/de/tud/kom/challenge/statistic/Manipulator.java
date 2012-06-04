package de.tud.kom.challenge.statistic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import de.tud.kom.challenge.util.RandomInteger;

public class Manipulator {
	
	private String targetDirectory = "statistic"+File.separator+"crossvalidation"+File.separator+"manipulated";
	private String sourceDirectory = "training";
	
	final String manipulationStringArray[] = {"+5pro","-5pro","+10pro","-10pro","+20pro","-20pro","+5","+10","+50","-5","-10","-50"};
	final Double factorArray[] = {1.05, 0.95, 1.1, 0.9, 1.2, 0.8, 5.0, 10.0, 50.0, 5.0, 10.0, 50.0};
	
	//simple constructor
	public Manipulator(){
		
	}
	
	public void generateAllFiles(){
		generateFiles(true);
	}
	
	public void generateSomeFiles(){
		generateFiles(false);
	}
	
	//
	private void generateFiles(boolean generateAll){
		//reading of Files an Folders in the training directory
		String fileAndFolderNames[] = new File(sourceDirectory).list();
		
//		System.out.println("read files an folders: "+Arrays.toString(fileAndFolderNames));
		
		//removing of 'arff'-files
		Vector<String> pathnames = new Vector<String>();
		for(int i = 0; i<fileAndFolderNames.length; i++){
			if(!fileAndFolderNames[i].contains(".arff"))
				pathnames.add(fileAndFolderNames[i]);
		}
		
		String pathnameArray[] = pathnames.toArray(new String[pathnames.size()]);
		System.out.println("relevante Ordner: "+Arrays.toString(pathnameArray));
		
		
		//cleaning of old data by deleting
		File directoryToDelete = new File(targetDirectory);
		deleteDirectoryAndContent(directoryToDelete);
		
		
		//erzeugen des 'maipulated' directory
		new File(targetDirectory).mkdir();
		
		
		//schleife über die Geräteordner
		for( int j= 0; j<pathnameArray.length; j++){
			System.out.println("bearbeite Gerät: "+pathnameArray[j]);
			//current source folder
			String currentSourcePath = sourceDirectory+File.separator+pathnameArray[j];
			//creation of target folder for current device
			String currentTargetPath = targetDirectory+File.separator+pathnameArray[j];
			new File(currentTargetPath).mkdir();
			
			//anlegen der Unterordner entsprechnd der manipulation - in extra methode auslagern mit übergabeparameter = pfad zu geräteordner
			String deriviationFolderPaths[] = new String[manipulationStringArray.length];
			for(int k = 0; k<manipulationStringArray.length; k++){
				String path = currentTargetPath+File.separator+manipulationStringArray[k];
				new File(path).mkdir();
				deriviationFolderPaths[k] = path;
			}

			//einlesen der Dateinamen
			String CSVfilenames[] = new File(currentSourcePath).list();
			//aussortieren der dateien ohne ".csv"
			
			//auswahl einiger der Dateien
			if(!generateAll){
				Vector<String> tempCSVfilenames = new Vector<String>();
				//schleife über die Dateien
				for(int o = 0; o<CSVfilenames.length; o++){
					//wenn die random funktion einen WErt der unteren 10% zurückgibt speichern 
					if(Math.random()<0.1)
						tempCSVfilenames.add(CSVfilenames[o]);
				}
				//protection of zero manipulated files
				if(tempCSVfilenames.isEmpty()){
					tempCSVfilenames.add(CSVfilenames[0]);
				}
				CSVfilenames = tempCSVfilenames.toArray(new String[tempCSVfilenames.size()]);
			}
			
			//manipulationsallgorithmus 
			
			//Schleife über die Dateien
			for(int l = 0; l<CSVfilenames.length; l++){
				System.out.println("bearbeite Datei: "+CSVfilenames[l]);
				//Filereader to read in the files
				try{
					FileReader fstream = new FileReader(currentSourcePath+File.separator+CSVfilenames[l]);
					BufferedReader reader = new BufferedReader(fstream);
					
					//Filewriter for writing all 12 kinds of manipulated data
					FileWriter fwriter[] = new FileWriter[12];
					
					BufferedWriter out[] = new BufferedWriter[12];
					//Initializing of the 12 'writer' objects
					for(int m = 0; m<12; m++){
						fwriter[m] = new FileWriter(deriviationFolderPaths[m]+File.separator+CSVfilenames[l]);
						out[m] = new BufferedWriter(fwriter[m]);
					}
					
					//reading of the first line
					String currentLine = reader.readLine();
					//walking through all the lines of the current read in file
					while(currentLine != null){
						//splitting in timestamp and and the two energy consumption values
						String line[] = currentLine.split(";");
						for(int n = 0; n<factorArray.length; n++){
							double currentDoubleValue1 = Double.parseDouble(line[1]);
							double currentDoubleValue2 = Double.parseDouble(line[2]);
							double result1 = 0;
							double result2 = 0;
							int result1int = 0;
							int result2int = 0;
							//data manipulation
							if(n<6){
								result1 = currentDoubleValue1 * factorArray[n];
								result2 = currentDoubleValue2 * factorArray[n];
							}else if(n<9){
								result1 = currentDoubleValue1 + factorArray[n];
								result2 = currentDoubleValue2 + factorArray[n];
							}else if(n<12){
								result1 = currentDoubleValue1 - factorArray[n];
								result2 = currentDoubleValue2 - factorArray[n];
							}
							//runden
							if(result1 > 0){
								result1int = (int)Math.round(result1);
							}
							if(result2 > 0){
								result2int = (int)Math.round(result2);
							}
							//transformation to String and concatenation of the data
							String resultArray[] = {line[0], Integer.toString(result1int), Integer.toString(result2int)};
							out[n].write(resultArray[0]);
							out[n].write(";");
							out[n].write(resultArray[1]);
							out[n].write(";");
							out[n].write(resultArray[2]);
							out[n].newLine();
						}
						
						//reading of the next line
						currentLine = reader.readLine();
					}
					for(int o = 0; o<factorArray.length; o++){
						out[o].close();
					}
				} catch(Exception FileNotFound){}	
			}
		}

	}

	
	
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
	
	
    private void deleteDirectoryAndContent(File directory){
    	if (directory.isDirectory()){
    		String[] entries = directory.list();
    		for (int i = 0; i<entries.length; i++){
    			File currentFile = new File(directory.getPath(), entries[i]);
    			deleteDirectoryAndContent(currentFile);
    		}
    		directory.delete();
    	}
    	if(directory.delete())
    		System.out.println(directory+" deleted");
    }

}
