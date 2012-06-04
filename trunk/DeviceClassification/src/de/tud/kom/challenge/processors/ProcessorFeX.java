package de.tud.kom.challenge.processors;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import de.tud.kom.challenge.csvdatareader.CsvContainer;
import de.tud.kom.challenge.processors.fex.DeviceUsage;
import de.tud.kom.challenge.processors.fex.ExtractUsages;
import de.tud.kom.challenge.processors.fex.Filters;
import de.tud.kom.challenge.processors.fex.ProcessorUtilities;
import de.tud.kom.challenge.processors.fex.SubProcessorEnergyAndPower;
import de.tud.kom.challenge.processors.fex.SubProcessorNoise;
import de.tud.kom.challenge.processors.fex.SubProcessorShape;
import de.tud.kom.challenge.processors.fex.SubProcessorTime;
import de.tud.kom.challenge.processors.fex.TimeInterval;

/**
 * 
 * @author Wassim Suleiman, Sebastian Koessler
 *
 */
public class ProcessorFeX implements FeatureProcessor {

	private static final double noiseLevel = 3; // noiselevel used for filters
	private static final double energyToFilterUsageAsNoise = 3 * 6; // min usage energy
	private static final int splitTimeInSeconds = 3 * 60; // max OffInterval's duration between two OnIntervals
	private static final int fillTimeInSeconds = 1; // used for mergeIntervalsOnPower
	public static final double LAMBDA = 0.01; // weight for l1 trendfiltering: small LAMBDA -> only small changes

	private void addAll(ArrayList<String> lst, String[] arr) {
		for (int i = 0; i < arr.length; i++) {
			lst.add(arr[i]);
		}
	}
	
	@Override
	public String getProcessorName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String[] getAttributeNames() {
		ArrayList<String> result = new ArrayList<String>();
		addAll(result, SubProcessorTime.getAttributeNames());
		addAll(result, SubProcessorEnergyAndPower.getAttributeNames());
		addAll(result, SubProcessorNoise.getAttributeNames());
		addAll(result, SubProcessorShape.getAttributeNames());
		return result.toArray(new String[0]);
	}

	@Override
	public String[] getAttributeValueranges() {
		ArrayList<String> result = new ArrayList<String>();
		addAll(result, SubProcessorTime.getAttributeValueranges());
		addAll(result, SubProcessorEnergyAndPower.getAttributeValueranges());
		addAll(result, SubProcessorNoise.getAttributeValueranges());
		addAll(result, SubProcessorShape.getAttributeValueranges());
		return result.toArray(new String[0]);
	}

	@Override
	public String[] processInput(CsvContainer csv) throws Exception {

		List<String[]> csvBuffer = csv.getEntries();
		String csvPath = csv.getFilename();
		String fileName = csvPath.substring((csvPath.lastIndexOf(".csv")) - 21);
		fileName = fileName.replace(".csv", "");
		ArrayList<Object> result = new ArrayList<Object>();
		if (csvBuffer != null) {
			ArrayList<TimeInterval> originalIntervals;
			try {
				originalIntervals = extractIntervals(csvBuffer);
			} catch (Exception e) {
				System.out.println("Extracting intervals error!");
				return null;
			}
			if (originalIntervals.size() > 0) {
				
				// remove low power TimeIntervals for discarding standby devices
				ArrayList<TimeInterval> noiseFilteredIntervals = Filters.removeBelow(originalIntervals, noiseLevel);

				if (noiseFilteredIntervals.size() > 0) {
					
					// DeviceUsage is used to get an idea of how long is the device turned on
					ArrayList<DeviceUsage> splitedUsages = DeviceUsage.splitOnTime(noiseFilteredIntervals, splitTimeInSeconds);
					
					// merge TimeIntervals in usages on power to reduce computing time
					ArrayList<DeviceUsage> mergedUsages = DeviceUsage.mergeIntervalsOnPower(splitedUsages, fillTimeInSeconds, noiseLevel);
					
					// remove usages with energy smaller than a certain percentage
					Filters.removeSmallEnergyUsages(mergedUsages, 1);
					
					// remove usages with only few outliners of low power. However they are higher than noise level.
					ArrayList<DeviceUsage> smallEnergieFilteredUsages = Filters.removeSmallEnergyUsagesTest(mergedUsages, energyToFilterUsageAsNoise);
					
					// remove low power TimeIntervals only to calculate slopes
					ArrayList<DeviceUsage> aboveAverageUsages = Filters.removeIntervalsBelowAveragePower(smallEnergieFilteredUsages);

					// generates matlabscripts to visualize original intervals, noise filtered intervals and usages
					// scripts are saved in training folder
					
					//String deviceName = getDeviceName(csvPath);
					//String mFileName = FileMapper.trainingPath + File.separator + deviceName + "_" + fileName + ".m";
					//MATLABVisualizer.saveMATLAB(originalIntervals, noiseFilteredIntervals, mergedUsages, deviceName, mFileName);

					if (smallEnergieFilteredUsages.size() > 0) {
						
						// ExtractUsages now describe also On- and OffIntervals
						// this simplify the feature extraction process
						ExtractUsages eus = ExtractUsages.extract(smallEnergieFilteredUsages);
						ExtractUsages aboveAverageExtractUsages = ExtractUsages.extract(aboveAverageUsages);

						ProcessorUtilities.addResult(result, SubProcessorTime.process(eus));
						ProcessorUtilities.addResult(result, SubProcessorEnergyAndPower.process(eus));
						ProcessorUtilities.addResult(result, SubProcessorNoise.process(eus, originalIntervals));
						ProcessorUtilities.addResult(result, SubProcessorShape.process(eus, aboveAverageExtractUsages, noiseLevel));

						// generates matlab script to visualize global slopes
						// you should use a small training set and use this method only for research purpose
						// hint: the devicetype dishwasher works nicely to visualize global slopes
						//MATLABVisualizer.saveMATLAB_TestLeastSquare(originalIntervals, aboveAverageExtractUsages,  mergedUsages,deviceName, mFileName);
					}
				} else {
					System.out.println("$$$$$ File will be labeled as noise: " + csvPath);
				}
			} else {
				System.out.println("$$$$$ File is empty: " + csvPath);
			}
		} else
			System.out.println("Error reading the csv File");

		return toString(result);

	}

	private String[] toString(ArrayList<Object> result) {
		if (result == null)
			return new String[0];
		if (result.size() <= 0)
			return new String[0];

		String[] arr = new String[result.size()];
		for (int i = 0; i < result.size(); i++) {
			arr[i] = ProcessorUtilities.objectToString(result.get(i));
		}
		return arr;
	}

	@SuppressWarnings("unused")
	private String getDeviceName(String path) {
		String name = path;
		int i = name.lastIndexOf('\\');
		if (i != -1)
			name = name.substring(0, i);

		i = name.lastIndexOf('\\');
		if (i != -1)
			name = name.substring(i + 1);
		return name;
	}
	
	// create TimeIntervals based on the csv file
	private ArrayList<TimeInterval> extractIntervals(List<String[]> csvBuffer) throws Exception {
		ArrayList<TimeInterval> intervals = new ArrayList<TimeInterval>();
		// Store output
		int time = 0, lastTime = -1;
		int watt = 0, lastWatt = 0;
		TimeInterval interval = null;

		int csvBufferSize = csvBuffer.size();
		TreeSet<Integer> activehours = new TreeSet<Integer>();

		for (int i = 0; i < csvBufferSize; i++) { // for each line

			// Collect time of sample
			int hour, minute, second;
			try {
				hour = Integer.valueOf(csvBuffer.get(i)[0].substring(11, 13));
				minute = Integer.valueOf(csvBuffer.get(i)[0].substring(14, 16));
				second = Integer.valueOf(csvBuffer.get(i)[0].substring(17, 19));
				watt = Integer.valueOf(csvBuffer.get(i)[1]);
			} catch (NumberFormatException ex) {
				System.out.println(ex.getMessage());
				throw new Exception("line " + i);
			}

			// Check for activity in current time interval
			time = (hour * 60 + minute) * 60 + second;
			activehours.add(hour);
			if (time > lastTime) {
				if (interval == null) {
					interval = new TimeInterval(watt, time, 1, 0);
					intervals.add(interval);
				} else {
					if (interval.getLevel() != watt) {
						// increase the time for the previous interval:
						interval.increase(time - lastTime - 1);
						interval = new TimeInterval(watt, time, 1, watt - lastWatt);
						intervals.add(interval);
					} else {
						interval.increase(time - lastTime);
						lastTime = time;
					}
				}
				lastWatt = watt;
			} else {
				interval.increaseReadings();
			}
			lastTime = time;
		}
		
		// check if sample is 24 hours long
		/*
		 * if(!Filters.checkForWholeDaySample(activehours)){
		 * 		System.out.println("$$$$$ File is too short!!!");
		 * 		return new ArrayList<TimeInterval>();
		 * }
		 */
		return intervals;
	}
}
