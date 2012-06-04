package de.tud.kom.challenge.csvdatareader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import de.tud.kom.challenge.csvdatareader.CSVDataReader;
import de.tud.kom.challenge.csvdatareader.LineData;
import de.tud.kom.challenge.util.FileUtil;

/**
 * This class reads the contents of a CSV file and stores it along with its metadata (i.e. filename)
 * 
 * @author Andreas Schaller
 * @author Frank Englert
 * @author Andreas Reinhardt
 * @author Hristo Chonov
 */
public class CsvContainer {
	
	private final static Logger log = Logger.getLogger(CsvContainer.class.getSimpleName());
	private final String filename;
	private int quantization;
	protected ArrayList<LineData> data;
	
	public CsvContainer(final String path, int quantization) {
		this.filename = path;
		this.quantization = quantization;
		data = null;
	}
	
	public String getFilename() {
		return this.filename;
	}
	
	
	/**
	 * Please use getcompressedEntries instead of this
	 * @return the parsed document with one line per entry
	 */
	public List<String[]> getEntries() {
		try {
			CSVReader reader = new CSVReader(new FileReader(filename), ';');
			List<String[]> readAll = reader.readAll();
			if (quantization == 1) return readAll;
			
			for(String[] line:readAll) {
				int currentPower = Integer.valueOf(line[1]);
				int avgPower = Integer.valueOf(line[2]);
				
				if(currentPower != 0) {
					int quanto = currentPower % quantization;
					currentPower -= quanto;					
					line[1] = ""+currentPower;
				}
					
				if(avgPower != 0) {
					int quanto = avgPower % quantization;
					avgPower -= quanto;										
					line[2] = ""+avgPower;
				}
			}
			
			//TODO Check this
			log.warn("One needs to check if this quantization really works...");
			
			return readAll;
				
		} catch(final Exception e) {
			log.error("Error while reading CSV file: " + FileUtil.getShortPath(this.filename));
		}
		return null;
	}
	
	/**
	 * Returns the data of an csv file. the csv-file should have the following format:
	 * <dateTime>;<actualPowerInWatt>;<smoothedPowerInWatt>
	 * 
	 * In the CSV-File there is typically one line per second. This method uses a compressed format.
	 * Only changes in the power consumption are written to the list of power consumption values.
	 * 
	 * @return A list of LineData.
	 */
	public ArrayList<LineData> getCompressedEntries() {
		if(data == null) {
			data = new ArrayList<LineData>();
			for(LineData l:new CSVDataReader(filename, quantization)) {
				data.add(l);
			}
		}
		return data;
	}
	
	/***
	 * 
	 * @return true if there is any line data with an smoothed avg consumption greater than 2
	 */
	public boolean isDataSignificant() {
		int max = 0;
		
		for(LineData line:getCompressedEntries()) {
			max = Math.max(max, line.getSmoothedConsumptionInWatt());
		}
		return max > 2;
	}

	public void clear() {
		if (data!=null) data.clear();
	}
}
