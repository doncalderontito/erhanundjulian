package de.tud.kom.challenge.processors;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import de.tud.kom.challenge.csvdatareader.CsvContainer;
import de.tud.kom.challenge.csvdatareader.DateTime;
import de.tud.kom.challenge.csvdatareader.LineData;

/**
 * @author Hristo Chonov
 */
public class ProcessorX implements FeatureProcessor {

	private List<Object[]> intervals;
	
	@Override
	public String getProcessorName() {
		return this.getClass().getSimpleName();
	}

	public String[] getAttributeNames() {
		return new String[]{
				"dayOfWeek", 
				"wholeDayInterval",
				"Time_Between_Intervals_shortest",
				"Time_Between_Intervals_longest",
				"Time_Between_Intervals_average",
				"Time_Between_IntervalsAbvAvgDur_shortest",
				"Time_Between_IntervalsAbvAvgDur_longest",
				"Time_Between_IntervalsAbvAvgDur_average",
				"Time_Between_IntervalsBlwAvgDur_shortest",
				"Time_Between_IntervalsBlwAvgDur_longest",
				"Time_Between_IntervalsBlwAvgDur_average",
				
				"Intervals_Duration_shortest",
				"Intervals_Duration_longest",
				"Intervals_Duration_average",
				"IntervalsAbvAvgDur_Duration_shortest",
				"IntervalsAbvAvgDu_Duration_longest",
				"IntervalsAbvAvgDu_Duration_average ",
				"IntervalsBlwAvgDur_Duration_shortest",
				"IntervalsBlwAvgDu_Duration_longest",
				"IntervalsBlwAvgDu_Duration_average",
				
				"totalEnergyConsumption",
				"Intervals_Energy_Consumption_smallest",
				"Intervals_Energy_Consumption_biggest",
				"Intervals_Energy_Consumption_average",
				"IntervalsAbvAvgDur_Energy_Consumption_smallest",
				"IntervalsAbvAvgDur_Energy_Consumption_biggest",
				"IntervalsAbvAvgDur_Energy_Consumption_average",
				"IntervalsBlwAvgDur_Energy_Consumption_smallest",
				"IntervalsBlwAvgDur_Energy_Consumption_biggest",
				"IntervalsBlwAvgDur_Energy_Consumption_average",
				
				"most_intensive_interval_start",
				
				"startPeaks_smallest",
				"startPeaks_biggest",
				"startPeaks_average",
				"startPeaksAbvAvgDurIntervals_smallest",
				"startPeaksAbvAvgDurInterval_biggest",
				"startPeaksAbvAvgDurInterval_average",
				"startPeaksBlwAvgDurInterval_smallest",
				"startPeaksBlwAvgDurInterval_biggest",
				"startPeaksBlwAvgDurInterval_average",
				"ratioBetweenStartPeaksInAbv_and_BlwAvgDur",
				
				"numerOfIntervals",
				"numerOfIntervalsBlwAvgDur",
				"numerOfIntervalsAbvAvgDur",
				
				"PowerRates_smallest",
				"PowerRates_biggest",
				"PowerRates_average",
				"PowerRatesAbvAvgDurIntervals_smallest",
				"PowerRatesAbvAvgDurInterval_biggest",
				"PowerRatesAbvAvgDurInterval_average",
				"PowerRatesBlwAvgDurInterval_smallest",
				"PowerRatesBlwAvgDurInterval_biggest",
				"PowerRatesBlwAvgDurInterval_average",
				"ratioBetweenPowerRatesInAbv_and_BlwAvgDur",
				
				"earliest_start",
				"latest_start",
	
				"EQofAbvAvgDurIntervals_basedOnDuration",
				"EQofAbvAvgDurIntervals_basedOnConsumption",
				"EQofAbvAvgDurIntervals_basedOnTimeBetweenIntervals",
				"EQofBlwAvgDurIntervals_basedOnDuration",
				"EQofBlwAvgDurIntervals_basedOnConsumption",
				"EQofBlwAvgDurIntervals_basedOnTimeBetweenIntervals",

				"detectedVampirePower",

				"most_active00-06",
				"most_active06-10",
				"most_active10-18",
				"most_active18-24",
				"most_activeAllDay",

				"activeDuring_1",
				"activeDuring_2",
				"activeDuring_3",
				"activeDuring_4",
				"activeDuring_5", 
				"activeDuring_6", 
				"activeDuring_7", 
				"activeDuring_8", 
				"activeDuring_9", 
				"activeDuring_10", 
				"activeDuring_11",
				"activeDuring_12",
				"activeDuring_13",
				"activeDuring_14",
				"activeDuring_15", 
				"activeDuring_16", 
				"activeDuring_17",
				"activeDuring_18", 
				"activeDuring_19", 
				"activeDuring_20", 
				"activeDuring_21",
				"activeDuring_22", 
				"activeDuring_23", 
				"activeDuring_24", 
				"activeDuring_25", 
				"activeDuring_26", 
				"activeDuring_27", 
				"activeDuring_28",
				"activeDuring_29",
				"activeDuring_30", 
				"activeDuring_31", 
				"activeDuring_32", 
				"activeDuring_33", 
				"activeDuring_34", 
				"activeDuring_35", 
				"activeDuring_36", 
				"activeDuring_37", 
				"activeDuring_38", 
				"activeDuring_39", 
				"activeDuring_40", 
				"activeDuring_41", 
				"activeDuring_42", 
				"activeDuring_43",
				"activeDuring_44", 
				"activeDuring_45", 
				"activeDuring_46",
				"activeDuring_47", 
				"activeDuring_48", 
				"activeDuring_49",
				"activeDuring_50",
				"activeDuring_51",
				"activeDuring_52",
				"activeDuring_53",
				"activeDuring_54",
				"activeDuring_55",
				"activeDuring_56",
				"activeDuring_57",
				"activeDuring_58",
				"activeDuring_59",
				"activeDuring_60",
				"activeDuring_61",
				"activeDuring_62",
				"activeDuring_63",
				"activeDuring_64",
				"activeDuring_65",
				"activeDuring_66",
				"activeDuring_67",
				"activeDuring_68",
				"activeDuring_69",
				"activeDuring_70",
				"activeDuring_71",
				"activeDuring_72",
				"activeDuring_73",
				"activeDuring_74",
				"activeDuring_75",
				"activeDuring_76",
				"activeDuring_77",
				"activeDuring_78",
				"activeDuring_79",
				"activeDuring_80",
				"activeDuring_81",
				"activeDuring_82",
				"activeDuring_83",
				"activeDuring_84",
				"activeDuring_85",
				"activeDuring_86",
				"activeDuring_87",
				"activeDuring_88",
				"activeDuring_89",
				"activeDuring_90",
				"activeDuring_91",
				"activeDuring_92",
				"activeDuring_93", 
				"activeDuring_94",
				"activeDuring_95",
				"activeDuring_96",
				"activeDuring_97",
				"activeDuring_98",
				"activeDuring_99",
				"activeDuring_100",
				"activeDuring_101",
				"activeDuring_102",
				"activeDuring_103",
				"activeDuring_104",
				"activeDuring_105",
				"activeDuring_106",
				"activeDuring_107",
				"activeDuring_108",
				"activeDuring_109",
				"activeDuring_110",
				"activeDuring_111",
				"activeDuring_112",
				"activeDuring_113",
				"activeDuring_114",
				"activeDuring_115",
				"activeDuring_116",
				"activeDuring_117",
				"activeDuring_118",
				"activeDuring_119",
				"activeDuring_120",
				"activeDuring_121",
				"activeDuring_122",
				"activeDuring_123",
				"activeDuring_124",
				"activeDuring_125",
				"activeDuring_126",
				"activeDuring_127",
				"activeDuring_128",
				"activeDuring_129",
				"activeDuring_130",
				"activeDuring_131",
				"activeDuring_132",
				"activeDuring_133",
				"activeDuring_134",
				"activeDuring_135",
				"activeDuring_136",
				"activeDuring_137",
				"activeDuring_138",
				"activeDuring_139",
				"activeDuring_140",
				"activeDuring_141",
				"activeDuring_142",
				"activeDuring_143",
				"activeDuring_144",
				"activeDuringAllDay",
				
				"deviceType"
				
				
		};	
	}
	
	public String[] getAttributeValueranges() {
		return new String[]{
				
				//day of week
				"{Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}",
				
				//whole day interval
				"{true, false}",
				
				//time between intervals
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
			
				//intervals durations
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
			
				//Energy consumption
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				
				//most intensive interval start
				"date \"HH:mm:ss\"",
				
				//start peaks
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				
				//number of intervals
				"numeric",
				"numeric",
				"numeric",
				
				//power consumptions
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				
				//earliest and latest start
				"date \"HH:mm:ss\"",
				"date \"HH:mm:ss\"",

				//intervals' similarities
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				
				//vampire power
				"{true, false}",
				
				//most active
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				
				//active during
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				
				//device types
				"{coffeemaker, dishwasher, refrigerator, toaster, tv, waterkettle, waterfountain, macmini, desktopcomputer, monitor, hifistereo, coffeegrinder, alarmclock, microwave, dimmable-floor-lamp, wii, raclette, energy-saving-bulb, washingmachine, unknownDevType}"
					
			};
	}
	
	public String[] processInput(CsvContainer csv) {
		//System.out.println("processInput");
		clearVariables();
		this.csvBuffer = csv.getEntries();
		String csvPath = csv.getFilename();
		LineData l = csv.getCompressedEntries().get(0);
		dt = l.getDateTime();
		
		//filename = csvPath.substring((csvPath.lastIndexOf(".csv")) - 21);
		filename = csvPath;
		//filenameDate = filename.substring(11, 21);
		////System.out.println(filenameDate);
		
		extractIntervalsFromBuffer(dt);
		process(csvPath);
		
		
		
		if (intervals.size() == 0) return new String[0];
		return collectResults();
	}

	private String[] collectResults() {
		
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.GERMAN);
		otherSymbols.setDecimalSeparator('.');
		otherSymbols.setGroupingSeparator(','); 
		DecimalFormat formatter = new DecimalFormat("0.0000000", otherSymbols);
		
		// Return processing results
		String[] out = new String[213]; 
		int i=0;
		
		out[i++] = dayOfWeek;
		
		out[i++] = "" + alreadyVisitedForBigIntervalsSize1;
		
		out[i++] = ""+timeBetweenIntervalsInSeconds[0];
		out[i++] = ""+timeBetweenIntervalsInSeconds[1];
		out[i++] = ""+timeBetweenIntervalsInSeconds[2];
		out[i++] = ""+timeBetweenIntervalsAboveAvgDurInSeconds[0];
		out[i++] = ""+timeBetweenIntervalsAboveAvgDurInSeconds[1];
		out[i++] = ""+timeBetweenIntervalsAboveAvgDurInSeconds[2];
		out[i++] = ""+timeBetweenIntervalsBelowAvgDurInSeconds[0];
		out[i++] = ""+timeBetweenIntervalsBelowAvgDurInSeconds[1];
		out[i++] = ""+timeBetweenIntervalsBelowAvgDurInSeconds[2];
		
		out[i++] = ""+intervalsDurationInSeconds[0];
		out[i++] = ""+intervalsDurationInSeconds[1];
		out[i++] = ""+intervalsDurationInSeconds[2];
		out[i++] = ""+intervalsAboveAvgDurDurationInSeconds[0];
		out[i++] = ""+intervalsAboveAvgDurDurationInSeconds[1];
		out[i++] = ""+intervalsAboveAvgDurDurationInSeconds[2];
		out[i++] = ""+intervalsBelowAvgDurDurationInSeconds[0];
		out[i++] = ""+intervalsBelowAvgDurDurationInSeconds[1];
		out[i++] = ""+intervalsBelowAvgDurDurationInSeconds[2];
		
		out[i++] = formatter.format(totalEnergyConsumption);

		out[i++] = formatter.format(intervalsConsumption[0]);
		out[i++] = formatter.format(intervalsConsumption[1]);
		out[i++] = formatter.format(intervalsConsumption[2]); 
		out[i++] = formatter.format(intervalsAbvAvgDurConsumption[0]);
		out[i++] = formatter.format(intervalsAbvAvgDurConsumption[1]);
		out[i++] = formatter.format(intervalsAbvAvgDurConsumption[2]);
		out[i++] = formatter.format(intervalsBlwAvgDurConsumption[0]);
		out[i++] = formatter.format(intervalsBlwAvgDurConsumption[1]);
		out[i++] = formatter.format(intervalsBlwAvgDurConsumption[2]);
		
		out[i++] = mostIntensiveIntervalStartTime;
		
		//min = 0, max = 1, average = 2
		out[i++] = ""+intervalsStartPeaks[0];
		out[i++] = ""+intervalsStartPeaks[1];
		out[i++] = ""+intervalsStartPeaks[2];
		out[i++] = ""+intervalsAbvAvgDurStartPeaks[0];
		out[i++] = ""+intervalsAbvAvgDurStartPeaks[1];
		out[i++] = ""+intervalsAbvAvgDurStartPeaks[2];
		out[i++] = ""+intervalsBlwAvgDurStartPeaks[0];
		out[i++] = ""+intervalsBlwAvgDurStartPeaks[1];
		out[i++] = ""+intervalsBlwAvgDurStartPeaks[2];
		out[i++] = formatter.format(similarityStartPeaksBelowAboveAvgDuration);
		
		out[i++] = ""+numberOfIntervals; 
		out[i++] = ""+belowAvgCounter; 
		out[i++] = ""+aboveAvgCounter;  
			
		out[i++] = formatter.format(PowerRates[0]);
		out[i++] = formatter.format(PowerRates[1]);
		out[i++] = formatter.format(PowerRates[2]);
		out[i++] = formatter.format(aboveAvgDurationPowerRates[0]);
		out[i++] = formatter.format(aboveAvgDurationPowerRates[1]);
		out[i++] = formatter.format(aboveAvgDurationPowerRates[2]);
		out[i++] = formatter.format(belowAvgDurationPowerRates[0]);
		out[i++] = formatter.format(belowAvgDurationPowerRates[1]);
		out[i++] = formatter.format(belowAvgDurationPowerRates[2]);
		out[i++] = formatter.format(similarityPRSbelowAboveAvgDuration);
		
		out[i++] = earliestStart;
		out[i++] = latestStart;
		
		out[i++] = formatter.format(eqBasedOnDurationAbv);
		out[i++] = formatter.format(eqBasedOnConsumptionAbv);
		out[i++] = formatter.format(eqBasedOnTimeBetweenIntervalsAbv);
		out[i++] = formatter.format(eqBasedOnDurationBlw);
		out[i++] = formatter.format(eqBasedOnConsumptionBlw);
		out[i++] = formatter.format(eqBasedOnTimeBetweenIntervalsAbv);
		
		out[i++] = ""+detectedVampirePower;
		
		out[i++] = ""+f00t06;
		out[i++] = ""+f06t10;
		out[i++] = ""+f10t18;
		out[i++] = ""+f18t24;
		out[i++] = ""+mostActiveAllDay;

		for(String s: activeDuring.split(",")) out[i++] = s;
		out[i++] = ""+activeAllDay;
		

		
		return out;
	}
	
	private void clearVariables() {
		year = 0;
		month = 0;
		date = 0;
		dt = null;
		
		deviceType = "?";
		intervals = new ArrayList<Object[]>();
		bigIntervals = new ArrayList<Object[]>();
		checkForEQintervals = new ArrayList<Object[]>();
		newIntervals = new ArrayList<Object[]>();
		
		intervalsLongerThanAvgDuration = new ArrayList<Object[]>();
		intervalsShorterThanAvgDuration = new ArrayList<Object[]>();
		
		csvLinks = new ArrayList<String>();

		intervalsConsumptions = new ArrayList<Double>();
		intervalsAbvAvgDurConsumptions = new ArrayList<Double>();
		intervalsBlwAvgDurConsumptions = new ArrayList<Double>();
		EQintervalsConsumptions = new ArrayList<Double>();

		equalPeriodicIntervals = "false";
		
		// shortest, longest, average
		timeBetweenIntervals = new ArrayList<Long>();
		timeBetweenIntervalsAboveAvgDur = new ArrayList<Long>();
		timeBetweenIntervalsBelowAvgDur = new ArrayList<Long>();
		timeBetweenIntervalsInSeconds = new long[] {0, 0, 0};
		timeBetweenIntervalsAboveAvgDurInSeconds = new long[] {0, 0, 0};
		timeBetweenIntervalsBelowAvgDurInSeconds = new long[] {0, 0, 0};
		EQtimeBetweenIntervalsInSeconds = new long[] {0, 0, 0};
		
		// shortest, longest, average
		intervalsDurationInSeconds = new long[] {0, 0, 0};
		intervalsAboveAvgDurDurationInSeconds = new long[] {0, 0, 0};
		intervalsBelowAvgDurDurationInSeconds = new long[] {0, 0, 0};
		EQintervalsDurationInSeconds = new long[] {0, 0, 0};
		
		// smallest, biggest, average
		intervalsConsumption = new double[] {0, 0, 0};
		intervalsStartPeaks = new double[] {0, 0, 0};
		intervalsAbvAvgDurConsumption = new double[] {0, 0, 0};
		intervalsAbvAvgDurStartPeaks = new double[] {0, 0, 0};
		intervalsBlwAvgDurConsumption = new double[] {0, 0, 0};
		intervalsBlwAvgDurStartPeaks = new double[] {0, 0, 0};
		EQintervalsConsumption = new double[] {0, 0, 0};
		EQintervalsStartPeaks = new double[] {0, 0, 0};

		totalEnergyConsumption = 0;

		output = new String[0];
		filename = null;
		//filenameDate = null;
		
		dayOfWeek = null;

		earliestStart = null;
		latestStart = null;

		activeDuring = null;
		active = new ArrayList<Boolean>();
		activeAllDay = false;
		
		//most Active in based on Intervals' consumptions
		f00t06 = false;
		f06t10 = false;
		f10t18 = false;
		f18t24 = false;
		mostActiveAllDay = false;
		
		
		//numberOfIntervalsBelowAndAboveAverageDuration
		aboveAvgCounter = 0;
		belowAvgCounter = 0;
		
		//number of Intervals
		numberOfIntervals = 0;
		
		//bigIntervals PowerRates
		PowerRates = new double[] {0, 0, 0};
		
		//belowAvgDurationPowerRates
		belowAvgDurationPowerRates = new double[] {0, 0, 0};

		//aboveAvgDurationPowerRates
		aboveAvgDurationPowerRates = new double[] {0, 0, 0};
		
		//indicates if the original file had only 1 Interval and was 
		//scanned again to retrieve the intervals inside that interval
		//this technique is used for devices that have been running the
		//whole day without interruption. for example: coffe maker, watter fountain
		//or pc that wasnt turned off
		alreadyVisitedForBigIntervalsSize1 = false;
		
		//Equality in Percents of Intervals for AbvAvgDur Intervals
		eqBasedOnDurationAbv = 0;
		eqBasedOnConsumptionAbv = 0;
		eqBasedOnTimeBetweenIntervalsAbv = 0;
		eqBasedOnStartPeaksAbv = 0;
		
		//Equality in Percents of Intervals for BlwAvgDur Intervals
		eqBasedOnDurationBlw = 0;
		eqBasedOnConsumptionBlw = 0;
		eqBasedOnTimeBetweenIntervalsBlw = 0;
		eqBasedOnStartPeaksBlw = 0;
		
		//similarity in PRs between belowAvgDurationPR and aboveAvgDurationPR
		similarityPRSbelowAboveAvgDuration = 0;
		
		//similarity in StartPeaks between belowAvgDuration and aboveAvgDuration
		similarityStartPeaksBelowAboveAvgDuration = 0;
		
		detectedVampirePower = false;
		
		//startTime of most Intensive Interval
		mostIntensiveIntervalStartTime = "00:00:00";
		
	}
	
	/* Processor's variables */
	int year, month, date;
	DateTime dt = null;
	String deviceType = "?";
	SimpleDateFormat strToDate = new SimpleDateFormat("HH:mm:ss");

	List<String[]> csvBuffer;
	
	List<Object[]> bigIntervals;
	List<Object[]> checkForEQintervals;
	List<Object[]> newIntervals;

	List<Object[]> intervalsLongerThanAvgDuration;
	List<Object[]> intervalsShorterThanAvgDuration;
	
	List<String> csvLinks;

	List<Double> intervalsConsumptions;
	List<Double> intervalsAbvAvgDurConsumptions;
	List<Double> intervalsBlwAvgDurConsumptions;
	List<Double> EQintervalsConsumptions;

	String equalPeriodicIntervals;
	
	// shortest, longest, average
	List<Long> timeBetweenIntervals;
	List<Long> timeBetweenIntervalsAboveAvgDur;
	List<Long> timeBetweenIntervalsBelowAvgDur;
	long[] timeBetweenIntervalsInSeconds;
	long[] timeBetweenIntervalsAboveAvgDurInSeconds;
	long[] timeBetweenIntervalsBelowAvgDurInSeconds;
	long[] EQtimeBetweenIntervalsInSeconds;
	
	// shortest, longest, average
	long[] intervalsDurationInSeconds;
	long[] intervalsAboveAvgDurDurationInSeconds;
	long[] intervalsBelowAvgDurDurationInSeconds;
	long[] EQintervalsDurationInSeconds;
	
	// smallest, biggest, average
	double[] intervalsConsumption;
	double[] intervalsStartPeaks;
	double[] intervalsAbvAvgDurConsumption;
	double[] intervalsAbvAvgDurStartPeaks;
	double[] intervalsBlwAvgDurConsumption;
	double[] intervalsBlwAvgDurStartPeaks;
	double[] EQintervalsConsumption;
	double[] EQintervalsStartPeaks;

	double totalEnergyConsumption;

	String[] output = null;
	String filename = null;
	//String filenameDate = null;
	
	String dayOfWeek = null;

	String earliestStart = null;
	String latestStart = null;

	// 24x6=144 booleans representing every 10minutes of the day
	String activeDuring = null;
	List<Boolean> active;
	boolean activeAllDay = false;
	
	//most Active in based on IntervalsAboveAverageDuration
	boolean f00t06 = false;
	boolean f06t10 = false;
	boolean f10t18 = false;
	boolean f18t24 = false;
	boolean mostActiveAllDay = false;
	
	
	//numberOfIntervalsBelowAndAboveAverageDuration
	int aboveAvgCounter = 0;
	int belowAvgCounter = 0;
	
	//number of Intervals
	int numberOfIntervals = 0;
	
	//bigIntervals PowerRates
	double[] PowerRates;
	
	//belowAvgDurationPowerRates
	double[] belowAvgDurationPowerRates;

	//aboveAvgDurationPowerRates
	double[] aboveAvgDurationPowerRates;
	
	//indicates if the original file had only 1 Interval and was 
	//scanned again to retrieve the intervals inside that interval
	//this technique is used for devices that have been running the
	//whole day without interruption. for example: coffe maker, watter fountain
	//or pc that wasnt turned off
	boolean alreadyVisitedForBigIntervalsSize1;

	//Equality in Percents of Intervals for AbvAvgDur Intervals
	double eqBasedOnDurationAbv;
	double eqBasedOnConsumptionAbv;
	double eqBasedOnTimeBetweenIntervalsAbv;
	double eqBasedOnStartPeaksAbv;
	
	//Equality in Percents of Intervals for BlwAvgDur Intervals
	double eqBasedOnDurationBlw;
	double eqBasedOnConsumptionBlw;
	double eqBasedOnTimeBetweenIntervalsBlw;
	double eqBasedOnStartPeaksBlw;
	
	//similarity in PRs between belowAvgDurationPR and aboveAvgDurationPR
	double similarityPRSbelowAboveAvgDuration;
	
	//similarity in StartPeaks between belowAvgDuration and aboveAvgDuration
	double similarityStartPeaksBelowAboveAvgDuration;
	
	boolean detectedVampirePower;
	
	//startTime of most Intensive Interval
	String mostIntensiveIntervalStartTime;
	
		
	@SuppressWarnings("unused")
	private double roundDouble(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d).replace(",","."));
	}
	
	private void extractIntervalsFromBuffer(DateTime datetime) {
		//System.out.println("extractIntervalsFromBuffer");
		// start processing the Buffer if reading was successful
		if (csvBuffer != null) {

			long startState = 0;
			long endState = 0;
			int startPos = 0;
			int endPos = 0;

			int wattSum = 0;

			long tempTime = Long.MIN_VALUE;
			
			int csvBufferSize = csvBuffer.size();
			int iLimit = csvBufferSize - 1;
			
			long lastIntervalStart = Long.MAX_VALUE;
			
			for (int i = 1; i < csvBufferSize; i++) {
				
				year = datetime.getYear(); 
				//= Integer.valueOf(filenameDate.substring(0, 4));
				month = datetime.getMonth();
					//Integer.valueOf(filenameDate.substring(5, 7));
				date = datetime.getDay();
					//Integer.valueOf(filenameDate.substring(8, 10));
				int hour = Integer.valueOf(csvBuffer.get(i - 1)[0].substring(
						11, 13));
				int minutes = Integer.valueOf(csvBuffer.get(i - 1)[0]
						.substring(14, 16));
				int seconds = Integer.valueOf(csvBuffer.get(i - 1)[0]
						.substring(17, 19));

				Calendar cal = new GregorianCalendar(year, month - 1, date,
						hour, minutes, seconds);

				Date temp = cal.getTime();

				long timePrevious = temp.getTime();
				
				int hourNow = Integer.valueOf(csvBuffer.get(i)[0].substring(
						11, 13));
				int minutesNow = Integer.valueOf(csvBuffer.get(i)[0]
						.substring(14, 16));
				int secondsNow = Integer.valueOf(csvBuffer.get(i)[0]
						.substring(17, 19));

				Calendar calNow = new GregorianCalendar(year, month - 1, date,
						hourNow, minutesNow, secondsNow);

				Date tempNow = calNow.getTime();

				long timeNow = tempNow.getTime();

				int wattNow = Integer.parseInt(csvBuffer.get(i)[1]);
				int wattPrevious = Integer.parseInt(csvBuffer.get(i - 1)[1]);
				
				if (timeNow != tempTime) {
					if (wattPrevious > 0 && startState == 0) {
						if(timePrevious != lastIntervalStart){
							// csv-file starts with a running phase
							startState = timePrevious;
							lastIntervalStart = timePrevious;
							tempTime = timeNow;
							startPos = i - 1;
							wattSum += wattPrevious;
						}
					} else if (wattPrevious > 0 && startState != 0) {
						wattSum += wattPrevious;
						tempTime = timeNow;
					}

					if (wattNow == 0 && startState != 0) {
						endState = timePrevious;
						endPos = i - 1;

						// calibration: add 1000ms to the duration
						// example: start=20sec,end=21sec, 21-20=1, but the
						// duration is 2 seconds
						// this is so because our endTime is the last time when
						// the device was inactive
						// if our endTime was the first time when the device
						// becomes inactive we wouldn't need this calibration
						long duration = ((endState - startState) + 1000);
						
						//if(i > 48017)
							//System.out.println("startState: " + startState + " endState: " + endState +
								//" duration: " + duration + " startPos: " + startPos + " endPos: " + endPos);
						intervals.add(new Object[] { startState, endState,
								duration, startPos, endPos, wattSum, 0 });

						startState = 0;
						endState = 0;
						startPos = 0;
						endPos = 0;
						wattSum = 0;
					} else if (i == iLimit && startState != 0){
						
						endState = timeNow;
						endPos = i;
						
						wattSum += wattNow;
						
						// calibration: add 1000ms to the duration
						// example: start=20sec,end=21sec, 21-20=1, but the
						// duration is 2 seconds
						// this is so because our endTime is the last time when
						// the device was inactive
						// if our endTime was the first time when the device
						// becomes inactive we wouldn't need this calibration
						long duration = ((endState - startState) + 1000);

						intervals.add(new Object[] { startState, endState,
								duration, startPos, endPos, wattSum, 0 });
					}
				}
				//für den Fall, dass am Ende der Datei das selbe Datum doppelt vorkommt 
				else if(i == iLimit && startState != 0){				
					endState = timeNow;
					endPos = i;
					
					wattSum += wattNow;
				
					// calibration: add 1000ms to the duration
					// example: start=20sec,end=21sec, 21-20=1, but the
					// duration is 2 seconds
					// this is so because our endTime is the last time when
					// the device was inactive
					// if our endTime was the first time when the device
					// becomes inactive we wouldn't need this calibration
					long duration = ((endState - startState) + 1000);
					
					intervals.add(new Object[] { startState, endState,
							duration, startPos, endPos, wattSum, 0 });
				
				}

			}
		} else {
			System.out.println("Error at reading the csv File");
			System.exit(1);
		}
	}
	
	private void process(String csvPath) {
		//System.out.println("process");
		if (intervals.size() > 0) {

			// extract intervals longer than 1 second
			extractBigIntervals(0);
			
			//if interval > 23 hours, for professional coffee makers and so on
			if(bigIntervals.size() == 1 && ((Number) bigIntervals.get(0)[2]).longValue() > 82800000){
				// extract Watt information about the interval
				intervalsEnergyConsumption(bigIntervals, intervalsConsumptions, intervalsConsumption, intervalsStartPeaks);
				totalEnergyConsumption();
				mostActiveIn(bigIntervals, intervalsConsumption);
				earliestAndLatestStart();
				//when we have one interval covering whole day
				//and for example the HiFi from Andreas avgPR is 10 but the start Peak is 11
				//if we set avgPr for our startIndicator we will not exctract any new intervals but the same one
				PowerRates = calculatePowerRateForIntervals(bigIntervals);
				double startIndicator = Math.max(this.intervalsStartPeaks[0], PowerRates[2]);
				Object[] entry = bigIntervals.get(0);
				int startPosition = ((Number) entry[3]).intValue();
				int endPosition = ((Number) entry[4]).intValue();
				long tempTime = Long.MAX_VALUE;
				long startState = 0;
				long endState = 0;
				int startPos = 0;
				int endPos = 0;
			// for the case that there is just one interval and its duration is 1 second, so it starts and ends at the same position	
			if(startPosition != endPosition) {
				for(int i=startPosition + 1; i<=endPosition; i++){
				
					int hourNow = Integer.valueOf(csvBuffer.get(i)[0].substring(
						11, 13));
					int minutesNow = Integer.valueOf(csvBuffer.get(i)[0]
						.substring(14, 16));
					int secondsNow = Integer.valueOf(csvBuffer.get(i)[0]
						.substring(17, 19));
					Calendar calNow = new GregorianCalendar(year, month - 1, date,
						hourNow, minutesNow, secondsNow);
					int hourPrev = Integer.valueOf(csvBuffer.get(i - 1)[0].substring(
							11, 13));
					int minutesPrev = Integer.valueOf(csvBuffer.get(i - 1)[0]
							.substring(14, 16));
					int secondsPrev = Integer.valueOf(csvBuffer.get(i - 1)[0]
							.substring(17, 19));
					Calendar calPrev = new GregorianCalendar(year, month - 1, date,
							hourPrev, minutesPrev, secondsPrev);

					Date temp = calNow.getTime();
					long timeNow = temp.getTime();
					temp = calPrev.getTime();
					long timePrevious = temp.getTime();

					int wattNow = Integer.parseInt(csvBuffer.get(i)[1]);
					int wattPrevious = Integer.parseInt(csvBuffer.get(i - 1)[1]);

					if (timeNow != tempTime) {
						if (wattPrevious >= startIndicator && startState == 0) {
							// csv-file starts with a running phase
							startState = timePrevious;
							tempTime = timeNow;
							startPos = i - 1;
						} else if (wattPrevious >= startIndicator && startState != 0) {
							tempTime = timeNow;
						}
						if (wattNow < startIndicator && startState != 0) {
							endState = timePrevious;
							endPos = i - 1;
							
							// calibration: add 1000ms to the duration
							// example: start=20sec,end=21sec, 21-20=1, but the
							// duration is 2 seconds
							// this is so because our endTime is the last time when
							// the device was inactive
							// if our endTime was the first time when the device
							// becomes inactive we wouldn't need this calibration
							long duration = ((endState - startState) + 1000);
							
							newIntervals.add(new Object[] { startState, endState,
								duration, startPos, endPos, 0, 0 });

							startState = 0;
							endState = 0;
							startPos = 0;
							endPos = 0;
							}
						else if (i == endPosition && startState != 0){
						
								endState = timeNow;
								endPos = i;
						
								// calibration: add 1000ms to the duration
								// example: start=20sec,end=21sec, 21-20=1, but the
								// duration is 2 seconds
							// this is so because our endTime is the last time when
								// the device was inactive
							// if our endTime was the first time when the device
								// becomes inactive we wouldn't need this calibration
								long duration = ((endState - startState) + 1000);

							newIntervals.add(new Object[] { startState, endState,
								duration, startPos, endPos, 0, 0 });
							}	
					}
					//für den Fall, dass am Ende der Datei das selbe Datum doppelt vorkommt 
					else if(i == endPosition && startState != 0){
				
						endState = timeNow;
						endPos = i;
				
						// calibration: add 1000ms to the duration
						// example: start=20sec,end=21sec, 21-20=1, but the
						// duration is 2 seconds
						// this is so because our endTime is the last time when
						// the device was inactive
						// if our endTime was the first time when the device
						// becomes inactive we wouldn't need this calibration
						long duration = ((endState - startState) + 1000);
					
						newIntervals.add(new Object[] { startState, endState,
							duration, startPos, endPos, 0, 0 });
				
					}
				
				}
			} else {
				newIntervals.add(bigIntervals.get(0));
			}
				
				
			bigIntervals = newIntervals;
			alreadyVisitedForBigIntervalsSize1 = true;
			}
		}
		//if we still have one interval it means it is something like water kettle of coffee machine that was turned just once 
		if(bigIntervals.size() == 1){
			
			numberOfIntervals = bigIntervals.size();
			
			intervalsEnergyConsumption(bigIntervals, intervalsConsumptions, intervalsConsumption, intervalsStartPeaks);
			
			// calculate the longest, shortest and average duration of the
			// bigIntervals
			intervalsDuration(bigIntervals, intervalsDurationInSeconds);
			
			retriveDayOfWeek(dt);

			activeDuring(dt);
			
			calcsWithIntervalsBelowAndAboveAverageDuration(bigIntervals, belowAvgDurationPowerRates, aboveAvgDurationPowerRates);
			
			intervalsEnergyConsumption(intervalsLongerThanAvgDuration, intervalsAbvAvgDurConsumptions, intervalsAbvAvgDurConsumption, intervalsAbvAvgDurStartPeaks);
			
			intervalsEnergyConsumption(intervalsShorterThanAvgDuration, intervalsBlwAvgDurConsumptions, intervalsBlwAvgDurConsumption, intervalsBlwAvgDurStartPeaks);
			
			double[] eq1 = detectEquility(intervalsLongerThanAvgDuration, timeBetweenIntervalsAboveAvgDur);
				this.eqBasedOnDurationAbv = eq1[0];
				this.eqBasedOnConsumptionAbv = eq1[1];
				this.eqBasedOnTimeBetweenIntervalsAbv = eq1[2];
			double[] eq2 = detectEquility(intervalsShorterThanAvgDuration, timeBetweenIntervalsBelowAvgDur);
				this.eqBasedOnDurationBlw = eq2[0];
				this.eqBasedOnConsumptionBlw = eq2[1];
				this.eqBasedOnTimeBetweenIntervalsBlw = eq2[2];
			intervalsDuration(intervalsLongerThanAvgDuration, intervalsAboveAvgDurDurationInSeconds);
			calcSimilarities();
			
			timeOfMostIntensiveInterval(intervalsLongerThanAvgDuration);
		
			earliestAndLatestStart();
						
		} else if (bigIntervals.size() > 1) {

			// ignore 5 second troughs and join the intervals
			//resizeIntervals(5);

			// calculate longest, shortest and average time between
			// bigIntervals
			timeBetweenIntervals(bigIntervals, timeBetweenIntervalsInSeconds, timeBetweenIntervals);

			// calculate the longest, shortest and average duration of the
			// bigIntervals
			intervalsDuration(bigIntervals, intervalsDurationInSeconds);

			// extract Watt information about the bigintervals
			if(!alreadyVisitedForBigIntervalsSize1)
				intervalsEnergyConsumption(bigIntervals, intervalsConsumptions, intervalsConsumption, intervalsStartPeaks);
			//using new Objects so that we dont overwrite the previous ones, that are for the original Interval
			//here we just calculate the Energy for every new interval
			else intervalsEnergyConsumption(bigIntervals, new ArrayList<Double>(), new double[3], new double[3]);
			
			retriveDayOfWeek(dt);

			activeDuring(dt);
			
			if(!alreadyVisitedForBigIntervalsSize1)
				earliestAndLatestStart();

			totalEnergyConsumption();

			PowerRates = calculatePowerRateForIntervals(bigIntervals);
							
			calcsWithIntervalsBelowAndAboveAverageDuration(bigIntervals, belowAvgDurationPowerRates, aboveAvgDurationPowerRates);
			
			
			// calculate longest, shortest and average time between
			// intervalsLongerThanAvgDuration
			timeBetweenIntervals(intervalsLongerThanAvgDuration, timeBetweenIntervalsAboveAvgDurInSeconds,timeBetweenIntervalsAboveAvgDur);
			
			timeBetweenIntervals(intervalsShorterThanAvgDuration, timeBetweenIntervalsBelowAvgDurInSeconds,timeBetweenIntervalsBelowAvgDur);
			
			double[] eq1 = detectEquility(intervalsLongerThanAvgDuration, timeBetweenIntervalsAboveAvgDur);
					this.eqBasedOnDurationAbv = eq1[0];
					this.eqBasedOnConsumptionAbv = eq1[1];
					this.eqBasedOnTimeBetweenIntervalsAbv = eq1[2];
			double[] eq2 = detectEquility(intervalsShorterThanAvgDuration, timeBetweenIntervalsBelowAvgDur);
					this.eqBasedOnDurationBlw = eq2[0];
					this.eqBasedOnConsumptionBlw = eq2[1];
					this.eqBasedOnTimeBetweenIntervalsBlw = eq2[2];
			
			//detectCoffeeMaker();
			
			// calculate the longest, shortest and average duration of the
			// intervalsLongerThanAvgDuration
			intervalsDuration(intervalsLongerThanAvgDuration, intervalsAboveAvgDurDurationInSeconds);
			
			intervalsDuration(intervalsShorterThanAvgDuration, intervalsBelowAvgDurDurationInSeconds);
			
			// extract Watt information about the intervalsLongerThanAvgDuration
			intervalsEnergyConsumption(intervalsLongerThanAvgDuration, intervalsAbvAvgDurConsumptions, intervalsAbvAvgDurConsumption, intervalsAbvAvgDurStartPeaks);
			
			intervalsEnergyConsumption(intervalsShorterThanAvgDuration, intervalsBlwAvgDurConsumptions, intervalsBlwAvgDurConsumption, intervalsBlwAvgDurStartPeaks);

			if(!alreadyVisitedForBigIntervalsSize1)
				mostActiveIn(intervalsLongerThanAvgDuration, intervalsAbvAvgDurConsumption);
			
			numberOfIntervals = bigIntervals.size();
			
			calcSimilarities();
			
			detectVampirePower();
			
			timeOfMostIntensiveInterval(intervalsLongerThanAvgDuration);
			
		} //else System.out.println("intervals.size() = 0" + "\n"); 
	}

	private void activeDuring(DateTime datetime) {
		//System.out.println("activeDuring");
		year = datetime.getYear(); 
		//= Integer.valueOf(filenameDate.substring(0, 4));
		month = datetime.getMonth();
			//Integer.valueOf(filenameDate.substring(5, 7));
		date = datetime.getDay();
			//Integer.valueOf(filenameDate.substring(8, 10));
		Calendar cal = new GregorianCalendar(year, month - 1, date);
	
		Date temp = cal.getTime();
	
		// 10 minutes = 600000 milliseconds
		long interval = 600000;
	
		// 00:00 start of the day
		long timeStart = temp.getTime();
		long timeEnd = timeStart + interval;
	
		// end of the day 23:50
		long limit = timeStart + 85800000;
	
		while (timeStart <= limit) {
	
			boolean covered = false;
	
			for (int i = 0; i < bigIntervals.size(); i++) {
	
				Object[] entry = bigIntervals.get(i);
				long start = ((Number) entry[0]).longValue();
				long end = ((Number) entry[1]).longValue();
	
				// check if the bigInterval starts or ends during our testing
				// interval or it covers it
				// set covered to true if there is an interval found matching
				// our criteria
				if ((start >= timeStart && start <= timeEnd)
						|| (end >= timeStart && end <= timeEnd)
						|| (start <= timeStart && end >= timeEnd)) {
					covered = true;
				}
			}	
	
			if (covered) {
				if (activeDuring != null){
					activeDuring += ",true";
					active.add(true);
				}
				else{
					activeDuring = "true";
					active.add(true);
					}
			} else {
				if (activeDuring != null){
					activeDuring += ",false";
					active.add(false);
					}
				else{
					activeDuring = "false";
					active.add(false);
				}
			}
	
			timeStart = timeEnd;
			timeEnd += interval;
		}
		
		int counter = 0;
		for(int i=0; i<active.size(); i++){
			if(active.get(i) == true)
				counter++;
		}
		if(active.size() == counter)
			activeAllDay = true;
	}
	
	@SuppressWarnings("unused")
	private void resizeIntervals(List<Object[]> intervalsList, long time) {
		// time input in seconds
		//System.out.println("resizeIntervals");
		int intervalsJoined = 0;

		time *= 1000;

		if(intervalsList.size() > 1){
			
			for (int i = 1; i < intervalsList.size(); i++) {

				Object[] entry = intervalsList.get(i);
				Object[] entryPrev = intervalsList.get(i - 1);

				long start = ((Number) entry[0]).longValue();
				long endPrev = ((Number) entryPrev[1]).longValue();
				long end = ((Number) entry[1]).longValue();
				long startPrev = ((Number) entryPrev[0]).longValue();
				int wattSum = ((Number) entry[5]).intValue();
				int wattSumPrev = ((Number) entryPrev[5]).intValue();
				long diff = start - endPrev;
//				// exclude the start to retrieve only the time between both
//				// intervals
//				diff -= 1000;

				if (diff < time) {
					entryPrev[1] = end;
					entryPrev[2] = (end - startPrev) + 1000;
					entryPrev[4] = entry[4];
					entryPrev[5] = wattSum + wattSumPrev;

					intervalsList.remove(i);

					intervalsJoined++;
				}
			}

			if (intervalsJoined > 0)
				resizeIntervals(intervalsList, time / 1000);
		}
	}
	
	private String timeOfMostIntensiveInterval(List<Object[]> intervalsList) {
		//System.out.println("timeOfMostIntensiveInterval");
		double biggest = 0;
		long biggestStart = 0;
		
		for (int i = 0; i < intervalsList.size(); i++) {
			
			Object[] entry = intervalsList.get(i);
			double intervalEngCons = ((Number) entry[5]).doubleValue();
			
			if(biggest <= intervalEngCons) {
				biggest = intervalEngCons;
				biggestStart = ((Number) entry[0]).longValue();
			}
			
		}
	
		this.mostIntensiveIntervalStartTime = strToDate.format(biggestStart);
		
		return this.mostIntensiveIntervalStartTime;
	}
	
	//needs intervalsConsumptions to be calculated first
	private void mostActiveIn(List<Object[]> intervalsList, double[] consumptions){
		//System.out.println("mostActiveIn");
		double avgIntervalsConsumptions = consumptions[2];
		
		boolean[] mostActive = new boolean[]{false, false, false, false, false, false, false, false, 
				false, false, false, false, false, false, false, false, 
				false, false, false, false, false, false, false, false};
		
		for(int i=0; i < intervalsList.size(); i++){
			
			Object[] entry = intervalsList.get(i);
			double engCons = ((Number) entry[5]).doubleValue();
			
			if(engCons >= avgIntervalsConsumptions){
				
				long startTime = ((Number) entry[0]).longValue();
				long endTime = ((Number) entry[1]).longValue();
	
				Calendar calStart = new GregorianCalendar();
				Calendar calEnd = new GregorianCalendar();
				calStart.setTimeInMillis(startTime);
				calEnd.setTimeInMillis(endTime);
				
				int startHour = calStart.get(Calendar.HOUR_OF_DAY);
				int endHour = calEnd.get(Calendar.HOUR_OF_DAY);
				
				for(int j=startHour; j<=endHour; j++){
					mostActive[j] = true;
					mostActive[j] = true;
				}	
			}
						
		}
		
		for(int i=0; i<=5; i++){
			if(mostActive[i] == true)
				f00t06 = true;
		}
		for(int i=6; i<=9; i++){
			if(mostActive[i] == true)
				f06t10 = true;
		}
		for(int i=10; i<=17; i++){
			if(mostActive[i] == true)
				f10t18 = true;
		}
		for(int i=18; i<=23; i++){
			if(mostActive[i] == true)
				f18t24 = true;
		}
		
		if(f00t06 && f06t10 && f10t18 && f18t24)
			mostActiveAllDay = true;
	}
	
	private void totalEnergyConsumption() {
		//System.out.println("totalEnergyConsumption");
		for (int i = 0; i < intervalsConsumptions.size(); i++) {
	
			totalEnergyConsumption += intervalsConsumptions.get(i);
		}
	}
	
	//requires intervalsLongerThanAvgDuration. use to detect fridge
	private double[] detectEquility(List<Object[]> intervalsList, List<Long> timeBetweenIntervals){
		//System.out.println("detectEquility");
		double eqBasedOnTimeBetweenIntervals = 0;
		double eqBasedOnDuration = 0;
		double eqBasedOnConsumption = 0;
	
			if (intervalsList.size() > 0) {
				////System.out.println(intervalsList.size());
				List<Long> durationsList = new ArrayList<Long>();
				List<Double> consumptionsList = new ArrayList<Double>();
				
				for (int i = 0; i < intervalsList.size(); i++) {
					Object[] entry = intervalsList.get(i);
					
					long duration = ((Number) entry[2]).longValue();
					durationsList.add(duration);
					
					double consumption = ((Number) entry[5]).doubleValue();
					consumptionsList.add(consumption);
					
				}
				
				
				Collections.sort(durationsList);
				Collections.sort(consumptionsList);
				Collections.sort(timeBetweenIntervals);
				int operations = 0;
				
			if(durationsList.size() > 1) {	
				for(int i = 0; i < durationsList.size(); i++){
					for(int j = i + 1; j < durationsList.size(); j++){
						double percentLocal = (double) durationsList.get(i) / durationsList.get(j);
						//System.out.println("percentLocal: " + percentLocal);
						eqBasedOnDuration += percentLocal;
						
						percentLocal = (double) consumptionsList.get(i) / consumptionsList.get(j);
						//System.out.println("percentLocal: " + percentLocal);
						eqBasedOnConsumption += percentLocal;
						operations++;
					}
				}
				
				eqBasedOnDuration /= operations;
				eqBasedOnConsumption /= operations;
				operations = 0;
				
			} else /*{
				eqBasedOnDuration = 1;
				eqBasedOnConsumption = 1;
				operations = 0;
			}*/
			
			if(timeBetweenIntervals.size() > 1) {
				for(int i = 0; i < timeBetweenIntervals.size(); i++){
					for(int j = i + 1; j < timeBetweenIntervals.size(); j++){
						double percentLocal = (double) timeBetweenIntervals.get(i) / timeBetweenIntervals.get(j);
						eqBasedOnTimeBetweenIntervals += percentLocal;
						operations++;
					}
				}
				eqBasedOnTimeBetweenIntervals /= operations;
			} /*else if(timeBetweenIntervals.size() == 1)
						eqBasedOnTimeBetweenIntervals = 1;*/
				}			
			return new double[] {eqBasedOnDuration, eqBasedOnConsumption, eqBasedOnTimeBetweenIntervals};
		}
	
	//requires belowAvgDurationPowerRates and aboveAvgDurationPowerRates
	private void detectVampirePower(){
		//System.out.println("detectVampirePower");
		//first requirement for VP
		if(belowAvgCounter > aboveAvgCounter){
			double belowAvgDurationPowerRatesAverage = belowAvgDurationPowerRates[2];
			
			// based on the European Commission regulation
			if(belowAvgDurationPowerRatesAverage <= 2)
				detectedVampirePower = true;
		}
		
	}
	
	private void calcSimilarities() {
		//System.out.println("calcSimilarities");
		double lowestSimiliraty;
		double highestSimiliraty;
		double averageSimiliraty;
		
		if(belowAvgDurationPowerRates[0] < aboveAvgDurationPowerRates[0])
			lowestSimiliraty = belowAvgDurationPowerRates[0] / aboveAvgDurationPowerRates[0];
		else lowestSimiliraty = aboveAvgDurationPowerRates[0] / belowAvgDurationPowerRates[0];
	
		if(belowAvgDurationPowerRates[1] < aboveAvgDurationPowerRates[1])
			highestSimiliraty = belowAvgDurationPowerRates[1] / aboveAvgDurationPowerRates[1];
		else highestSimiliraty = aboveAvgDurationPowerRates[1] / belowAvgDurationPowerRates[1];
	
		if(belowAvgDurationPowerRates[2] < aboveAvgDurationPowerRates[2])
			averageSimiliraty = belowAvgDurationPowerRates[2] / aboveAvgDurationPowerRates[2];
		else averageSimiliraty = aboveAvgDurationPowerRates[2] / belowAvgDurationPowerRates[2];
	
	
		similarityPRSbelowAboveAvgDuration = (lowestSimiliraty + highestSimiliraty + averageSimiliraty) / 3;
	
		
		if(intervalsBlwAvgDurStartPeaks[0] < intervalsAbvAvgDurStartPeaks[0])
			lowestSimiliraty = (double) intervalsBlwAvgDurStartPeaks[0] / intervalsAbvAvgDurStartPeaks[0];
		else lowestSimiliraty = (double) intervalsAbvAvgDurStartPeaks[0] / intervalsBlwAvgDurStartPeaks[0];
	
		if(intervalsBlwAvgDurStartPeaks[1] < intervalsAbvAvgDurStartPeaks[1])
			highestSimiliraty = (double) intervalsBlwAvgDurStartPeaks[1] / intervalsAbvAvgDurStartPeaks[1];
		else highestSimiliraty = (double) intervalsAbvAvgDurStartPeaks[1] / intervalsBlwAvgDurStartPeaks[1];
	
		if(intervalsBlwAvgDurStartPeaks[2] < intervalsAbvAvgDurStartPeaks[2])
			averageSimiliraty = (double) intervalsBlwAvgDurStartPeaks[2] / intervalsAbvAvgDurStartPeaks[2];
		else averageSimiliraty = (double) intervalsAbvAvgDurStartPeaks[2] / intervalsBlwAvgDurStartPeaks[2];
	
		similarityStartPeaksBelowAboveAvgDuration = (lowestSimiliraty + highestSimiliraty + averageSimiliraty) / 3;
	}
	
	private void extractBigIntervals(long time) {
		//System.out.println("extractBigIntervals");
		// for loop to write the intervals in bigIntervals

		int shortCounter = 0;

		for (int i = 0; i < intervals.size(); i++) {

			Object[] entry = intervals.get(i);

			long duration = ((Number) entry[2]).longValue() / 1000; // seconds

			Object[] temp;

			if (duration > time) {
				temp = new Object[] { entry[0], entry[1], entry[2], entry[3],
						entry[4], entry[5], entry[6] };
				bigIntervals.add(temp);
			}
			else
				shortCounter++;

		}
	}
	
	private void timeBetweenIntervals(List<Object[]> intervals, long[] timeBetweenIntervalsArray, List<Long> timeBetweenIntervals) {
		//System.out.println("timeBetweenIntervals");
		if (intervals.size() > 1) {
			for (int i = 1; i < intervals.size(); i++) {

				Object[] entryActual = intervals.get(i);
				Object[] entryPrevious = intervals.get(i - 1);

				long startActual = ((Number) entryActual[0]).longValue();
				long endPrevious = ((Number) entryPrevious[1]).longValue();
				long diff = startActual - endPrevious;

				timeBetweenIntervals.add(diff);
				
				//int pos = ((Number) entryActual[3]).intValue();
				
				}
		} else {
			timeBetweenIntervals.add(Long.valueOf(0));
		}

		if (timeBetweenIntervals.size() > 0) {

			long shortest = Long.MAX_VALUE;
			long longest = Long.MIN_VALUE;
			long average = 0;

			for (int i = 0; i < timeBetweenIntervals.size(); i++) {

				long iterator = timeBetweenIntervals.get(i);

				if (iterator < shortest)
					shortest = iterator;
				if (iterator > longest)
					longest = iterator;

				average += iterator;
			}

			average /= timeBetweenIntervals.size();

			timeBetweenIntervalsArray[0] = shortest / 1000;
			timeBetweenIntervalsArray[1] = longest / 1000;
			timeBetweenIntervalsArray[2] = average / 1000;
		}

	}

	private void intervalsDuration(List<Object[]> intervals, long[] intervalsDuration) {
		//System.out.println("intervalsDuration");
		List<Long> process = new ArrayList<Long>();

		for (int i = 0; i < intervals.size(); i++) {

			Object[] entry = intervals.get(i);

			long duration = ((Number) entry[2]).longValue();

			process.add(duration);

		}

		if (process.size() > 0) {

			long shortest = Long.MAX_VALUE;
			long longest = Long.MIN_VALUE;
			long average = 0;

			for (int i = 0; i < process.size(); i++) {

				long iterator = process.get(i);

				if (iterator < shortest)
					shortest = iterator;
				if (iterator > longest)
					longest = iterator;

				average += iterator;
			}

			average /= process.size();

			intervalsDuration[0] = shortest / 1000;
			intervalsDuration[1] = longest / 1000;
			intervalsDuration[2] = average / 1000;
		}
	}

	private void intervalsEnergyConsumption(List<Object[]> intervalsList, List<Double> outputToListTotalConsumptions, double[] outputToArray, double[] startPeaksList) {
		//System.out.println("intervalsEnergyConsumption");
		List<Integer> startPeaks = new ArrayList<Integer>();
		outputToListTotalConsumptions.clear();
		
		year = dt.getYear(); 
		//= Integer.valueOf(filenameDate.substring(0, 4));
		month = dt.getMonth();
			//Integer.valueOf(filenameDate.substring(5, 7));
		date = dt.getDay();
			//Integer.valueOf(filenameDate.substring(8, 10));

		double total = 0;

		for (int i = 0; i < intervalsList.size(); i++) {
			Object[] entryActual = intervalsList.get(i);
			int startPos = ((Number) entryActual[3]).intValue();
			int endPos = ((Number) entryActual[4]).intValue();
			long startTime = ((Number) entryActual[0]).longValue();
			long intervalEndTime = ((Number) entryActual[1]).longValue();
			long timePrev = 0;
			long sectionDuration = 60000;
			long timeBorder = startTime + sectionDuration;
			int samplesForSection = 0;
			double wattsForSection = 0;

			for (int j = startPos; j <= endPos; j++) {
				// System.out.println("i: " + i + "j: " + j + "startPos: " +
				// startPos + "endPos" + endPos);
				String[] entry = csvBuffer.get(j);

				int hour = Integer.valueOf(entry[0].substring(11, 13));
				int minutes = Integer.valueOf(entry[0].substring(14, 16));
				int seconds = Integer.valueOf(entry[0].substring(17, 19));

				Calendar cal = new GregorianCalendar(year, month - 1, date,
						hour, minutes, seconds);

				Date calDate = cal.getTime();

				long time = calDate.getTime();

				if (time < timeBorder && time < intervalEndTime) {
					if (time != timePrev) {

						wattsForSection += Integer.parseInt(entry[1]);
						samplesForSection++;
						
						//add peak in first second to the list
						if(j == startPos){startPeaks.add((int) wattsForSection);}
						
						timePrev = time;
					}
					// enter the else Statement if we've reached the section
					// limit or if we are at the end of the interval
				} else {
					// add for the last second because in the upper IF-Statement
					// we skip the last second
					if (time == intervalEndTime && time != timePrev) {
						wattsForSection += Integer.parseInt(entry[1]);
						samplesForSection++;
						//add peak in first and actually last second to the list. meant for 1s intervals
						if(startPos == endPos){startPeaks.add((int) wattsForSection);}
					}
					// if the last section in an interval is smaller than the
					// set sectionDuration, the sectionDuration Value must be
					// recalculated
					if ((timeBorder - time) > 0) {
						sectionDuration = sectionDuration - (timeBorder - time);
						// compensate: example sectionDuration = 60sec;
						// timeStart = 0:40, timeBorder = 1:20(80secoonds);
						// timeEnd = 1:10(70 seconds);
						// => 80-70 = 10 seconds left to the end of the
						// SectionDuration, but there are only 9 seconds left
						// in the first If-Statement we don't use this
						// calibration because we skip the border and leave it
						// as a start of the next Section
						sectionDuration += 1000;
					}
					double averageForSecondInSection = (wattsForSection / samplesForSection) / 3600.0;
					// System.out.println("averageForSecondInSection: " +
					// averageForSecondInSection);
					double totalWattsForSection = averageForSecondInSection
							* (sectionDuration / 1000);
					// System.out.println("totalWattsForSection: " +
					// totalWattsForSection);

					total += totalWattsForSection;

					long nextTime = 0;
					if (j != endPos) {
						String[] entryNext = csvBuffer.get(j + 1);
						int hourNext = Integer.valueOf(entryNext[0].substring(
								11, 13));
						int minutesNext = Integer.valueOf(entryNext[0]
								.substring(14, 16));
						int secondsNext = Integer.valueOf(entryNext[0]
								.substring(17, 19));
						Calendar calNext = new GregorianCalendar(year,
								month - 1, date, hourNext, minutesNext,
								secondsNext);
						Date calDateNext = calNext.getTime();
						nextTime = calDateNext.getTime();
					}

					timeBorder = nextTime + sectionDuration;
					wattsForSection = 0;
					samplesForSection = 0;
				}
			}

			outputToListTotalConsumptions.add(total);
			//set the Total Amount also in the interval list
			entryActual[5] = total;
			total = 0;

		}

		if (outputToListTotalConsumptions.size() > 0) {

			double bigest = Integer.MIN_VALUE;
			double smallest = Integer.MAX_VALUE;
			double average = 0;

			for (int i = 0; i < outputToListTotalConsumptions.size(); i++) {

				double iterator = outputToListTotalConsumptions.get(i);

				if (iterator < smallest)
					smallest = iterator;
				if (iterator > bigest)
					bigest = iterator;

				average += iterator;
			}

			average /= outputToListTotalConsumptions.size();
			
			outputToArray[0] = smallest;
			outputToArray[1] = bigest;
			outputToArray[2] = average;
		}
		
		if (startPeaks.size() > 0) {

			long smallest = Long.MAX_VALUE;
			long biggest = Long.MIN_VALUE;
			long average = 0;

			for (int i = 0; i < startPeaks.size(); i++) {

				long iterator = startPeaks.get(i);

				if (iterator < smallest)
					smallest = iterator;
				if (iterator > biggest)
					biggest = iterator;

				average += iterator;
			}

			average /= startPeaks.size();

			startPeaksList[0] = smallest;
			startPeaksList[1] = biggest;
			startPeaksList[2] = average;
		}
	}
		
	private void retriveDayOfWeek(DateTime datetime) {
		//System.out.println("retriveDayOfWeek");
		year = datetime.getYear(); 
		//= Integer.valueOf(filenameDate.substring(0, 4));
		month = datetime.getMonth();
			//Integer.valueOf(filenameDate.substring(5, 7));
		date = datetime.getDay();
			//Integer.valueOf(filenameDate.substring(8, 10));
		Calendar cal = new GregorianCalendar(year, month - 1, date);

		int dayOfWeekInt = cal.get(Calendar.DAY_OF_WEEK);

		switch (dayOfWeekInt) {
		case 2:
			dayOfWeek = "Monday";
			break;
		case 3:
			dayOfWeek = "Tuesday";
			break;
		case 4:
			dayOfWeek = "Wednesday";
			break;
		case 5:
			dayOfWeek = "Thursday";
			break;
		case 6:
			dayOfWeek = "Friday";
			break;
		case 7:
			dayOfWeek = "Saturday";
			break;
		case 1:
			dayOfWeek = "Sunday";
			break;
		default:
			dayOfWeek = null;
			break;
		}
	}

	private void earliestAndLatestStart() {
		//System.out.println("earliestAndLatestStart");
		Object[] entryFirst = bigIntervals.get(0);
		Object[] entryLast = bigIntervals.get(bigIntervals.size() - 1);

		long earliest = ((Number) entryFirst[0]).longValue();
		long latest = ((Number) entryLast[0]).longValue();

		earliestStart = strToDate.format(earliest);
		latestStart = strToDate.format(latest);

	}

	private void calcsWithIntervalsBelowAndAboveAverageDuration(List<Object[]> intervalsList, double[] belowAvgDurPRs, double[] aboveAvgDurPRs){
		//System.out.println("calcsWithIntervalsBelowAndAboveAverageDuration");
		long averageIntervalDuration = this.intervalsDurationInSeconds[2];
		//convert to milliseconds
		averageIntervalDuration *= 1000;
		
		//double averagePowerRate = this.PowerRates[2];	
		//double averageConsumption = this.intervalsConsumption[2];
		
		double[] shorter = new double[]{0, 0, 0};
		double[] longer = new double[]{0, 0, 0};
		
			
		for (int i = 0; i < intervalsList.size(); i++) {

				Object[] entry = intervalsList.get(i);

				long duration = ((Number) entry[2]).longValue();

				if(duration > averageIntervalDuration){
					this.intervalsLongerThanAvgDuration.add(entry);
					this.aboveAvgCounter++;
				}else{
					this.intervalsShorterThanAvgDuration.add(entry);
					this.belowAvgCounter++;
				}
		
			}
		
		shorter = calculatePowerRateForIntervals(this.intervalsShorterThanAvgDuration);
		longer =  calculatePowerRateForIntervals(this.intervalsLongerThanAvgDuration);
		for (int i=0; i<=2; i++){
			belowAvgDurPRs[i] = shorter[i];

			aboveAvgDurPRs[i] = longer[i];
		}
		
	}
	
	private double[] calculatePowerRateForIntervals(List<Object[]> intervalsList){
		//System.out.println("calculatePowerRateForIntervals");
		double[] powerRates = new double[]{0, 0, 0};
		
		List<Integer> lowestPR = new ArrayList<Integer>();
		List<Integer> highestPR = new ArrayList<Integer>();
		List<Double> averagePR = new ArrayList<Double>();
		
		for (int i = 0; i < intervalsList.size(); i++) {
						
			Object[] entry = intervalsList.get(i);

			int startPos = ((Number) entry[3]).intValue();
			int endPos = ((Number) entry[4]).intValue();

			long timePrevious = Long.MAX_VALUE;
			int counter = 0;
			long avg = 0;
			int lowestWh = Integer.MAX_VALUE;
			int highestWh = Integer.MIN_VALUE;

			for(int j = startPos; j <= endPos; j++){
	
				int hour = Integer.valueOf(csvBuffer.get(j)[0].substring(
					11, 13));
				int minutes = Integer.valueOf(csvBuffer.get(j)[0]
					.substring(14, 16));
				int seconds = Integer.valueOf(csvBuffer.get(j)[0]
					.substring(17, 19));
				Calendar cal = new GregorianCalendar(year, month - 1, date,
					hour, minutes, seconds);
				Date temp = cal.getTime();
				long timeNow = temp.getTime();
				
				if(timeNow != timePrevious){
					int WhNow = Integer.parseInt(csvBuffer.get(j)[1]);
					avg += WhNow;
					counter++;
					
					//System.out.println("WhNow " + WhNow + " j " + j + " lowestWh " + lowestWh);
				
					if(WhNow != 0){
						if(lowestWh > WhNow){
							lowestWh = WhNow;
						}
					}
					if(highestWh < WhNow)
						highestWh = WhNow;

					timePrevious = timeNow;
				}
			}
			if(lowestWh != Integer.MAX_VALUE)
				lowestPR.add(lowestWh);
			if(highestWh != Integer.MIN_VALUE)
				highestPR.add(highestWh);
			double averageWh = (double) avg / counter;
			averagePR.add(averageWh);
			entry[6] = averageWh;
			
			//System.out.println("lowestWh + " + lowestWh + " highestWh " + highestWh + " averageWh " + averageWh);
			
		}
		
		int lowestAvg = 0;
		int highestAvg = 0;
		double averageAvg = 0;
		
		for(int i=0; i<lowestPR.size(); i++){
				if(lowestAvg < lowestPR.get(i))
					lowestAvg = lowestPR.get(i);}
		
		for(int i=0; i<highestPR.size(); i++){
			if(highestAvg < highestPR.get(i))
				highestAvg = highestPR.get(i);}
		
		for(int i=0; i<averagePR.size(); i++){
			if(averageAvg < averagePR.get(i))
				averageAvg = averagePR.get(i);
		}
		
		powerRates[0] = lowestAvg;
		powerRates[1] = highestAvg;
		powerRates[2] = averageAvg;
		
		return powerRates;
	}
	
}
