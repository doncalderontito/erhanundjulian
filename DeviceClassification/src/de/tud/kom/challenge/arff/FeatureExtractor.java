package de.tud.kom.challenge.arff;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.challenge.csvdatareader.CachedCsvContainer;
import de.tud.kom.challenge.csvdatareader.CsvContainer;
import de.tud.kom.challenge.processors.FeatureProcessor;
import de.tud.kom.challenge.processors.ProcessorX;
import de.tud.kom.challenge.util.FileUtil;
import de.tud.kom.challenge.util.ParalellTasks;
import de.tud.kom.challenge.util.ProgressLogger;
import de.tud.kom.challenge.util.Task;

/**
 * @author Andreas Reinhardt
 * @author Andreas Schaller
 * @author Frank Englert
 * @author Hristo Chonov
 * 
 * @recentChanges Andreas, Frank: Added some degree of parallelism to the createTrainingSet-Method
 * 
 */
public class FeatureExtractor {
	
	private static Logger log = Logger.getLogger(FeatureExtractor.class.getSimpleName());
	private Vector<FeatureProcessor> processorList;
	private String headerCopy;
	private int quantization = 1;
	
	public FeatureExtractor(final Vector<FeatureProcessor> processors) {
		log.info("Instantiated " + processors.size() + " processing module(s):");
		headerCopy = null;
		processorList = processors;
		
		for(int f = 0; f < processors.size(); f++) {
			log.info((f + 1) + ") " + processors.elementAt(f).getClass().getSimpleName());
		}
	}
	
	public void setQuantization(int factor) {
		if (factor >=2) quantization = factor;
		else quantization = 1;
	}
	
	public void createTrainingSet() {
		FeatureExtractor.log.info("Creating training set");
		final Set<String> devices = Collections.synchronizedSet(new HashSet<String>());
		
		// Traverse all training data
		String[] csvFileList = FeatureExtractor.getFiles(FileMapper.trainingPath);
		
		Task<String, String> task = new Task<String, String>() {
			public String calculate(final String path) {
				final String dev = FileMapper.getDeviceTypeFromPathToFile(path);
				if(!dev.equals("?")) {
					devices.add(dev);
				}
				
				String result = processFile(processorList, path);
				if(result != null) {
					result += "," + dev + "\n";
				}
				
				return result;
			}
		};
		
		ProgressLogger progress = new ProgressLogger("Extraction");
		Collection<String> featureLineDataList = ParalellTasks.map(Arrays.asList(csvFileList), task, progress);
		
		try {
			String fname = FileMapper.trainingPath + File.separator + FileMapper.trainingArff;
			FileWriter fstream = new FileWriter(fname);
			BufferedWriter out = new BufferedWriter(fstream);
			out.append(generateHeader(processorList, devices));
			out.append(generateData(featureLineDataList));
			out.close();
			FeatureExtractor.log.debug("Training ARFF file has been written!");
		} catch(final Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	/**
	 * @param processors the processors to apply to the file
	 * @param file the file to process
	 * @return null if the file could not be processed, its ARFF line if it could
	 */
	private String processFile(Vector<FeatureProcessor> procs, String file) {		
		String shortFileName = FileUtil.getShortPath(file);
		int entryNumber = 0;
		String out = "";
		
		CsvContainer csv = new CachedCsvContainer(file, quantization);
		String[] results = new String[0];
		
		log.info("Start processing file " + shortFileName+"...");
		
		//if(csv.isDataSignificant()) {
			for(FeatureProcessor fp:procs) {
					
				log.debug("Start processing file " + shortFileName + " with feature processor " + fp.getProcessorName());
				try {
					results = fp.processInput(csv);
					if(results == null) {
						log.warn("Applying feature processor: " + fp.getProcessorName() + " to file "+shortFileName+" failed!");
						return null;
					}
					for(int r = 0; r < results.length; r++) {
						if(entryNumber++ != 0) {
							out += ",";
						}
						if (results[r] == null || results[r].equals("null")) out += "?";
						else out += results[r];
					}
				} catch(final Exception e) {
					//e.printStackTrace();
					log.error("File parsing error! File: " + file + " and processor "+fp.getProcessorName()+": (" + e.getMessage() + ")");
					
					for(int r = 0; r < fp.getAttributeNames().length; r++) {
						if(entryNumber++ != 0) {
							out += ",";
						}
						out += "?";
					}
					//break;
				}
			}
		//}
		
		if(results.length == 0) {
			log.info("File "+shortFileName+" FAILED processing!");
			return null;
		}
		
		log.info("File "+shortFileName+" successfully completed processing!");
		csv.clear();
		csv = null;
		return out;
	}

	/**
	 * Generates the header for the given selection of processors
	 * @param devices the ground truth for the possible device types
	 * @return the header of the ARFF file
	 */
	private String generateHeader(Vector<FeatureProcessor> procs, Set<String> devices) {
		final StringWriter header = new StringWriter();
		
		header.append("@relation ConsumerIdentification\n\n");
		
		// Initialize ARFF header
		for(FeatureProcessor fp:procs) {
			for(int i = 0; i < fp.getAttributeNames().length; i++) {
				final AttributeType att = new AttributeType(fp.getAttributeNames()[i], fp.getAttributeValueranges()[i]);
				try {
					header.append("@attribute " + att.getName() + " " + att.getValuerange() + "\n");
				} catch(final Exception e) {
					FeatureExtractor.error(e);
				}
			}
		}
		
		// Add ground truth column
		header.append("@attribute deviceName {");
		String types = "";
		int k = 0;
		for(final String d : devices) {
			if(k++ != 0) {
				types += ",";
			}
			types += d;
		}
		header.append(types + "}\n");
		header.flush();
		
		log.info("Possible device types are: " + types);
		
		return header.toString();
	}
	
	private String generateData(Collection<String> featureLineDataList) {
		
		final List<String> dataEntries = new ArrayList<String>(featureLineDataList);
		//Collections.sort(dataEntries);
		
		final StringWriter data = new StringWriter();
		data.append("\n@DATA\n");
		
		for(final String featureLineData : dataEntries) {
			data.append(featureLineData);
		}
		log.info("Created " + featureLineDataList.size() + " instance(s) of training data");
		
		data.flush();
		return data.toString();
	}
	
	
	
	public void createTestingSet() {
		// Get header
		if(headerCopy == null) {
			headerCopy = "";
			FeatureExtractor.log.info("Using header information from previous training ARFF");
			try {
				final FileInputStream fstream = new FileInputStream(FileMapper.trainingPath + File.separator + FileMapper.trainingArff);
				final DataInputStream in = new DataInputStream(fstream);
				final BufferedReader br = new BufferedReader(new InputStreamReader(in));
				
				String strLine;
				while((strLine = br.readLine()) != null) {
					if(strLine.contains("@DATA")) {
						break;
					}
					headerCopy += strLine + "\n";
				}
				in.close();
			} catch(final Exception e) {
				FeatureExtractor.error(e);
			}
		} else {
			log.info("Using header information from previous training set generation");
		}
		log.debug("Header information is\n" + headerCopy);
		
		final StringWriter data = new StringWriter();
		data.append("\n@DATA\n");
		
		// Traverse all training data
		final String[] csvPath = getFiles(FileMapper.testingPath);
		for(final String path : csvPath) {
			final String out = processFile(processorList, path);
			if(out != null) {
				data.append(out + ",?\n");
			}
		}
		
		data.flush();
		
		log.debug("Generated testing ARFF file:\n" + headerCopy + "\n" + data.toString());
		try {
			final FileWriter fstream = new FileWriter(FileMapper.testingPath + File.separator + FileMapper.testingArff);
			final BufferedWriter out = new BufferedWriter(fstream);
			out.append(headerCopy);
			out.append(data.toString());
			out.close();
			log.info("Testing ARFF file with " + csvPath.length + " instance(s) has been written!");
		} catch(final Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public static String[] getFiles(String path) {
		final HashSet<String> files = new HashSet<String>();
		recursePath(path, files);
		return files.toArray(new String[0]);
	}
	
	private static void recursePath(String path, HashSet<String> result) {
		final File file = new File(path);
		if(file.isDirectory()) {
			final File[] children = file.listFiles();
			if(children == null) {
				error(new Exception("Input directory does not exist or is not a directory."));
			} else {
				for(int i = 0; i < children.length; i++) {
					final String childPath = children[i].getAbsolutePath();
					FeatureExtractor.recursePath(childPath, result);
				}
			}
		} else if(file.getName().endsWith(".csv")) {
			result.add(file.getAbsolutePath());
		}
	}
	
	private static void error(Exception e) {
		log.error("Fatal problem - exiting...");
		log.error(e);
		System.exit(1);
	}
	//Kristopher: customized trainingSet generation for statistical  analyse
	public void createCustomizedTrainingSet(String path) {
		FeatureExtractor.log.info("Creating training set on path: "+path);
		final Set<String> devices = Collections.synchronizedSet(new HashSet<String>());
		
		// Traverse all training data
		final String[] csvFileList = FeatureExtractor.getFiles(path);
		
		final Task<String, String> task = new Task<String, String>() {
			public String calcualte(final String path) {
				final String dev = FileMapper.getDeviceTypeFromPathToFile(path);
				if(!dev.equals("?")) {
					devices.add(dev);
				}
				
				String result = processFile(processorList, path);
				if(result != null) {
					result += "," + dev + "\n";
				}
				
				return result;
			}

			@Override
			public String calculate(String input) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		
		ProgressLogger progress = new ProgressLogger("Extraction");
		Collection<String> featureLineDataList = ParalellTasks.map(Arrays.asList(csvFileList), task, progress);
		
		System.out.println("csvFileList to process: "+Arrays.toString(csvFileList));
		
		
		try {
			final FileWriter fstream = new FileWriter(path + File.separator + FileMapper.trainingArff);
			final BufferedWriter out = new BufferedWriter(fstream);
			out.append(this.generateHeader(processorList, devices));
			out.append(this.generateData(featureLineDataList));
			out.close();
			FeatureExtractor.log.debug("Training ARFF file has been written to path: "+path);
		} catch(final Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
			

}
