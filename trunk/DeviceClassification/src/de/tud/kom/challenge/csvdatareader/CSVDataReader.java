package de.tud.kom.challenge.csvdatareader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.tud.kom.challenge.util.CollectionUtil;
import de.tud.kom.challenge.util.FileUtil;

/**
 * Improved version of the CSVReader. It is ~6 times faster than
 * the OpenCSV-Reader.
 * 
 * @author Andreas Reinhardt
 * @author Andreas Schaller
 * @author Frank Englert
 */
public class CSVDataReader implements Iterable<LineData> {
	
	private final static Logger log = Logger.getLogger(CSVDataReader.class.getSimpleName());
	private final ArrayListImpl iterator;
	private double quantization = 1;
	
	public CSVDataReader(final String filename, int quantization) {
		this.iterator = new ArrayListImpl(filename);
		this.quantization = quantization;
	}
	
	public Iterator<LineData> iterator() {
		return iterator.getItems().iterator();
	}
	
	class ArrayListImpl {
		
		public List<LineData> getItems() {
			return this.lineDataList;
		}
		
		private BufferedReader reader;
		private ArrayList<LineData> lineDataList;
		private String filename;
		
		public ArrayListImpl(final String filename) {
			try {
				this.filename = filename;
				this.reader = new BufferedReader(new FileReader(filename));
				this.lineDataList = this.processLines();
			} catch(final FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		private ArrayList<LineData> processLines() {
			int redundancy = 0;
			ArrayList<LineData> lines = new ArrayList<LineData>();
			while(true) {
				try {
					final String lineData = reader.readLine();
					if(lineData == null) {
						if(this.dayisCovered()) {
							break;
						}
						log.warn("End of file before end of day.");
						log.warn("Stop processing file: " + FileUtil.getShortPath(filename));
						lines = new ArrayList<LineData>();
						break;
					}
					
					final LineData line = this.getLineData(lineData);
					
					if(!this.isTimeAcceptable(line.getDateTime())) {
						log.warn("Occurence of a time difference bigger than 30 minutes: " + checkDateTime.toTimeString()+ " - " +line.getDateTime().toTimeString());
						log.warn("Stop processing file: " + FileUtil.getShortPath(filename));
						lines = new ArrayList<LineData>();
						break;
					}
					
					/*if(this.isValueRedundant(CollectionUtil.last(lines), line)) {
						redundancy++;
						continue;
					}*/
					this.appendLine(lines, line);
				} catch (IOException e) {}
			}
			if (redundancy != 0) log.info(redundancy+" values in file "+FileUtil.getShortPath(filename)+" were disregarded due to redundancy...");
			return lines;
		}
		
		private boolean dayisCovered() {
			if(this.checkDateTime == null) {
				return false;
			}
			return this.isTimeAcceptable(new DateTime(this.checkDateTime).getDate().addSeconds(24 * 60 * 60));
		}
		
		private DateTime checkDateTime = null;
		
		private boolean isTimeAcceptable(final DateTime dateTime) {
			if(this.checkDateTime == null) {
				this.checkDateTime = dateTime.getDate();
			}
			
			// time difference is acceptable if smaller than maxDiff seconds
			final int maxDiff = 60 * 30;
			final int timeDiff = dateTime.getDiffInSeconds(this.checkDateTime);
			if(timeDiff < maxDiff) {
				this.checkDateTime = dateTime;
				return true;
			}
			
			return false;
		}
		
		private void appendLine(final ArrayList<LineData> lines, final LineData line) {
			final LineData last = CollectionUtil.last(lines);
			
			if(last != null) {
				if(last.getDateTime().isAfter(line.getDateTime())) {
					return;
				}
				last.setDuration(last.getDateTime().getDiffInSeconds(line.getDateTime()));
			}
			lines.add(line);
		}
		
		private LineData getLineData(final String lineData) {
			int startOfDate = 0;
			int startOfWatt = 0;
			int startOfAvg = 0;
			
			if(lineData.startsWith("\"")) {
				startOfDate = 1;
			}
			startOfWatt = lineData.indexOf(';') + 1;
			startOfAvg = lineData.lastIndexOf(';') + 1;
			
			final int currentPower = this.readConsumption(lineData, startOfWatt, startOfAvg);
			final int avgPower = this.readAvgConsumption(lineData, startOfAvg);
			
			if(quantization<=1)
				return new LineData(this.readDate(lineData, startOfDate), currentPower, avgPower);
			
			int adjustetCurrentPower = 0;
			int adjustetavgPower = 0;
			
			if(currentPower != 0)
				if(currentPower > 12)
					adjustetCurrentPower = ((int) Math.round(currentPower/quantization)) * (int) quantization;
				else adjustetCurrentPower = (int) quantization;
			
			if(avgPower != 0)
				if(avgPower > 12)
					adjustetavgPower = ((int) Math.round(avgPower/quantization)) * (int) quantization;
				else adjustetavgPower = (int) quantization;
			
			return new LineData(this.readDate(lineData, startOfDate), adjustetCurrentPower, adjustetavgPower);
		}
		
		private boolean hasChanged(final int power, final int lastPower) {
			return power != lastPower;
		}
		
		private boolean isValueRedundant(final LineData lastLine, final LineData currentLine) {
			if(lastLine == null) {
				return false;
			}
			
			if(this.hasChanged(lastLine.getSmoothedConsumptionInWatt(), currentLine.getSmoothedConsumptionInWatt())) {
				return false;
			}
			
			if(this.isOnlyPowerMeterConsumption(lastLine, currentLine)) {
				return true;
			}
			
			return !this.hasChanged(lastLine.getConsumptionInWatt(), currentLine.getConsumptionInWatt());
		}
		
		private boolean isOnlyPowerMeterConsumption(final LineData lastLine, final LineData currentLine) {
			return (lastLine.getConsumptionInWatt() - currentLine.getConsumptionInWatt()) == -2;
		}
		
		private DateTime readDate(final String line, final int startOfDate) {
			List<Integer> numbers = extractGroupOfNumbersFromString(line);
			final int year = numbers.get(2);
			final int month = numbers.get(1);
			final int day = numbers.get(0);
			final int hour =numbers.get(3);
			final int minute = numbers.get(4);
			final int second = numbers.get(5);
			return new DateTime(year, month, day, hour, minute, second);
		}
		
		private List<Integer> extractGroupOfNumbersFromString(String line) {
			ArrayList<Integer> numbers = new ArrayList<Integer>();

			Pattern p = Pattern.compile("\\d+");
			Matcher m = p.matcher(line);

			while (m.find()) {
				numbers.add(Integer.valueOf(m.group()));
			}

			return numbers;
		}
		
		private int readConsumption(final String line, final int startOfWatt, final int startOfAvg) {
			return extractGroupOfNumbersFromString(line).get(6);
		}
		
		private int readAvgConsumption(final String line, final int startOfAvg) {
			return extractGroupOfNumbersFromString(line).get(7);
		}
	}
	
}
