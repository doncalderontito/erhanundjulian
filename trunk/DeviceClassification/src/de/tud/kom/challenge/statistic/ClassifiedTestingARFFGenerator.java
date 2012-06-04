package de.tud.kom.challenge.statistic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import de.tud.kom.challenge.arff.FileMapper;
import de.tud.kom.challenge.weka.MachineLearner;

public class ClassifiedTestingARFFGenerator {
	
	static String targetPath = "statistic"+File.separator+"classifiedTestingARFF";
	static String sourceARFF = FileMapper.testingPath+File.separator+FileMapper.testingArff;
	
	static String attributeString = "@attribute";
	static String dataString = "@DATA";	
	
	
	public void generateTestingARFF(){
		//cleaning of old data
		File directoryToDelete = new File(targetPath);
		deleteDirectoryAndContent(directoryToDelete);
		//building directory
		new File(targetPath).mkdir();
		
		/*
		 * copying Header to new ARFF and counting the attributes
		 */
		try{
			FileReader fstream = new FileReader(sourceARFF);
			BufferedReader reader = new BufferedReader(fstream);
		
			//Filewriter for new ARFF file
			FileWriter fwriter = new FileWriter(targetPath+File.separator+"classifiedTesting.arff");
			BufferedWriter out = new BufferedWriter(fwriter);

		
			//signal of reching 'DATA' section
			boolean dataReached = false;
			
			/*
			 * Copy all except DATA content
			 */
			//reading of the first line
			String currentLine = reader.readLine();
			//walking through all the lines of the current read in file
			while((currentLine != null) && !dataReached){		
				//ignore empty lines
				if(currentLine.isEmpty()){
					out.newLine();
				//ignore comment lines
				}else if(currentLine.charAt(0) == '%'){
					out.write(currentLine);
					out.newLine();
				//count attributes
				}else if(currentLine.contains(attributeString)){
					out.write(currentLine);
					out.newLine();
				//identify data sectioon
				}else if(currentLine.contains(dataString)){
					out.write(currentLine);
					out.newLine();
					dataReached = true;
				//copy of unknown lines
				}else{
					out.write(currentLine);
					out.newLine();
				}
				currentLine = reader.readLine();
			}
			
			MachineLearner ml = new MachineLearner(FileMapper.trainingPath + File.separator + FileMapper.trainingArff);
			
			String resultData[] = ml.classificationResult(sourceARFF, MachineLearner.getClassifier());
			
			for(int i= 0; i<resultData.length; i++){
				System.out.println(i+": "+resultData[i]);
				out.write(resultData[i]);
				out.newLine();
			}
			
			
			out.close();
			fwriter.close();
		}catch(Exception e){}
				
		
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
