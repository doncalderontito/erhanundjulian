package de.tud.kom.challenge.csvdatareader;

import java.util.ArrayList;
import java.util.Hashtable;

import de.tud.kom.challenge.csvdatareader.CSVDataReader;
import de.tud.kom.challenge.csvdatareader.LineData;

/**
 * This class reads the contents of a CSV file and stores it along with its metadata (i.e. filename)
 * 
 * @author Andreas Reinhardt
 * @author Andreas Schaller
 * @author Frank Englert
 */
public class CachedCsvContainer extends CsvContainer {
	
	private static Hashtable<String, ArrayList<LineData>> cachedData = new Hashtable<String, ArrayList<LineData>>();
	private int quantization = 1;
	
	public CachedCsvContainer(final String path, int quantization) {
		super(path, quantization);
		this.quantization = quantization;
	}
	
	public ArrayList<LineData> getCompressedEntries() {
		data = cachedData.get(this.getFilename());
		
		if(data == null) {
			data = new ArrayList<LineData>();
			for(LineData l:new CSVDataReader(getFilename(), quantization)) {
				data.add(l);
			}
			cachedData.put(getFilename(), data);
		}
		return data;
	}
}
