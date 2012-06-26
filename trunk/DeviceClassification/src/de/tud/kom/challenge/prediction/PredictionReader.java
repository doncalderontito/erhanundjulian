package de.tud.kom.challenge.prediction;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

import de.tud.kom.challenge.csvdatareader.CSVDataReader;
import de.tud.kom.challenge.csvdatareader.LineData;

public class PredictionReader {
	
	private DataContainer data;
	private final static Logger log = Logger.getLogger(PredictionReader.class.getSimpleName());

	/**
	 * Initializes a DataContainer with all readings from the provided path. PredictionReader 
	 * is a wrapper around the DataContainer and offers a number of convenient access methods.
	 * 
	 * @param path the directory which contains the initial data from the PredictionReader
	 */
	public PredictionReader(String path) {
		data = new DataContainer();
	
		File file = new File(path);
		if(file.isDirectory()) {
			String[] files = getFiles(path);
		
			// Sort by collection date (only works when file names are unchanged)
			Arrays.sort(files);
			
			for (String f:files) {
				readFile(f, data);
			}
			
			log.info("Successfully read input data from "+path);
			log.info("We have "+data.getStats());
		} else {
			log.warn("Specified input data path does not exist - DataContainer is empty!");
		}
	}

	/**
	 * Appends an entry to the DataContainer (this is needed when new data are forwarded to
	 * the processors bit by bit and thus need to be added to the container on the fly).
	 * 
	 * @param entry The DataEntry to append to the DataContainer
	 */
	public void appendEntry(DataEntry entry) {
		data.addEntry(entry);
	}

	/**
	 * Get the complete DataContainer
	 * @return all data that are collected in the DataContainer
	 */
	public DataContainer getCompleteData() {
		return data;
	}
	
	/**
	 * Informational method
	 * @return some statistics of the DataContainer
	 */
	public String getSize() {
		return data.getStats();
	}
	
	private void readFile(String filename, DataContainer data) {
		CSVDataReader csv = new CSVDataReader(filename, 1);
		Iterator<LineData> lines = csv.iterator();
		if (!lines.hasNext()) return; // terminate if there is no content
		
		LineData l = null;
		while (lines.hasNext()) {
			l = lines.next();
			DataEntry de = new DataEntry(l.getDateTime().toLong(), l.getConsumptionInWatt());
			data.addEntry(de);
		}
	}

	private static String[] getFiles(String path) {
		final HashSet<String> files = new HashSet<String>();
		recursePath(path, files);
		return files.toArray(new String[0]);
	}
	
	private static void recursePath(String path, HashSet<String> result) {
		final File file = new File(path);
		if(file.isDirectory()) {

			final File[] children = file.listFiles();
			if(children == null) {
				log.error(new Exception("Input directory does not exist or is not a directory."));
			} else {
				for(int i = 0; i < children.length; i++) {
					final String childPath = children[i].getAbsolutePath();
					recursePath(childPath, result);
				}
			}
		} else if(file.getName().endsWith(".csv")) {
			result.add(file.getAbsolutePath());
		}
	}
}
