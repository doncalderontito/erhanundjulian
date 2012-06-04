package de.tud.kom.challenge.processors.fex;

import java.util.ArrayList;

public final class SubProcessorTime{

	public static String[] getAttributeNames(){
		return new String[] {
				//this usage times is calculated including off times in on usage
				"usageTime_smallest", 	 
				"usageTime_highest", 	
				"usageTime_avg",
				"usageTimePerDay",
				
				"sumOffTimePerUsage_smallest",
				"sumOffTimePerUsage_highst",
				"sumOffTimePerUsage_avg",

				"offTimePerUsage_smallest",
				"offTimePerUsage_highst",
				"offTimePerUsage_avg",
				
				"usedAtMorning",
				"usedAtNoon",
				"usedAtEvening",
				"usedAtNight",
				
				"timeBetweenUsages_smallest",
				"timeBetweenUsages_highest",
				"timeBetweenUsages_avg",
				
				"increasingOntimePerUsage",
				"increasingOfftimePerUsage",
				"increasingUsagetime",
				"increasingTimeBetweenUsages",
				
				
				"diff_TimeBetweenUsages",
				"diff_Usagetime",
				
				"sumOfftimePerUsageVar",

				};
	}
	public static String[] getAttributeValueranges(){
		return new String[] {
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
				
				"{true, false}",
				"{true, false}",
				"{true, false}",
				"{true, false}",
				
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				"numeric",
				
				"numeric",
				"numeric",
				
				"numeric"

				};
	}
	public static ArrayList<Object> process(ExtractUsages eus) throws Exception{
		ArrayList<Object> result = new ArrayList<Object>();
		
		
		
		//usageTime_smallest 	 
		//usageTime_highest 	
		//usageTime_avg
		ProcessorUtilities.addResult(result, ProcessorUtilities.calculateSmallestHighestAvgInteger(eus.getUsagetimes()));
		
		//usageTimePerDay
		result.add(ProcessorUtilities.sumInteger(eus.getUsagetimes()));
		
		//sumOffTimePerUsage_smallest
		//sumOffTimePerUsage_highst
		//sumOffTimePerUsage_avg
		ProcessorUtilities.addResult(result, ProcessorUtilities.calculateSmallestHighestAvgInteger(eus.getSumOfftimesPerUsage()));
		
		//offTimePerUsage_smallest
		//offTimePerUsage_highst
		//offTimePerUsage_avg
		ProcessorUtilities.addResult(result, ProcessorUtilities.calculateSmallestHighestAvgInteger(eus.getOfftimesPerUsage()));
		
		//usedAtMorning
		//usedAtNoon
		//usedAtEvening
		//usedAtNight1
		//usedAtNight2
		ProcessorUtilities.addResult(result, extractMainDaytimesUsage(eus));		
		
		//timeBetweenUsages_smallest
		//timeBetweenUsages_highest
		//timeBetweenUsages_avg
		ProcessorUtilities.addResult(result, ProcessorUtilities.calculateSmallestHighestAvgInteger(eus.getTimesBetweenUsages()));
		
		//increasingOntimePerUsage
		Double increasingOntimePerUsage = extractIncreasingOntimePerUsage(eus);
		result.add(increasingOntimePerUsage);
		
		//increasingOfftimePerUsage
		Double increasingOfftimePerUsage = extractIncreasingOfftimePerUsage(eus);
		result.add(increasingOfftimePerUsage);
		
		//increasingUsagetime
		Integer increasingUsagetime = ProcessorUtilities.isIncreasingIntegerSerie(eus.getUsagetimes());
		result.add(increasingUsagetime);
		
		//increasingTimeBetweenUsages
		Integer increasingTimeBetweenUsages = ProcessorUtilities.isIncreasingIntegerSerie(eus.getTimesBetweenUsages());
		result.add(increasingTimeBetweenUsages); 

		//diff_TimeBetweenUsages
		result.add(calculateTimeDifferenceRatio(ProcessorUtilities.calculateSmallestHighestAvgInteger(eus.getTimesBetweenUsages())));
		
		//diff_Usagetime
		result.add(calculateTimeDifferenceRatio(ProcessorUtilities.calculateSmallestHighestAvgInteger(eus.getUsagetimes())));
		
		//usagestimeVar
		//sumOfftimePerUsageVar
		//timeBetweenUsagesVar
		Double[] timeVar = calculateTimeVariances(eus);
		//result.add(timeVar[0]);
		result.add(timeVar[1]);
		//result.add(timeVar[2]);
		
		return result;
	}
	
	// calculate a ratio to determine if usages have similar length
	private static Object calculateTimeDifferenceRatio(ArrayList<Object> smallestHighestAvg) {
		if(smallestHighestAvg == null) return null;
		if(smallestHighestAvg.size() != 3) return null;
		Integer smallest = (Integer)smallestHighestAvg.get(0);
		Integer highest = (Integer)smallestHighestAvg.get(1);
		Double avg = (Double)smallestHighestAvg.get(2);
		
		if(smallest == null || highest == null || avg == null){
			return null;
		}
		if(avg == 0) return null;
		double result = (highest - smallest)/avg;
		return result;
	}
	
	// check in which dayparts the device is used
	private static ArrayList<Object> extractMainDaytimesUsage(ExtractUsages eus){
			//usedAtMorning
			//usedAtNoon
			//usedAtEvening
			//usedAtNight1
			//usedAtNight2
			ArrayList<Object> result = new ArrayList<Object>();
			int morningStart 		= 4 * 60 * 60;
			int morningEnd 			= 11 * 60 * 60;
			
			int noonStart 			= 11 * 60 * 60;
			int noonEnd 			= 16 * 60 * 60;
			
			int eveningStart 		= 16 * 60 * 60;
			int eveningEnd 			= 20 * 60 * 60;
			
			int nightStart1 		= 20 * 60 * 60;
			int nightEnd1 			= 24 * 60 * 60;
			
			int nightStart2 		= 0 * 60 * 60;
			int nightEnd2 			= 4 * 60 * 60;
			
			Boolean usedAtMorning = null;
			Boolean usedAtNoon = null;
			Boolean usedAtEvening = null;
			Boolean usedAtNight = null;
			
			if(eus.size() > 0){

				usedAtMorning 	= false;
				usedAtNoon 		= false;
				usedAtEvening 	= false;
				usedAtNight 	= false;
				for(ExtractUsage eu:eus.getUsages()){
					for(OnInterval oi:eu.getOnintervals()){
						usedAtMorning 	|= oi.getIntersectionLengthWith(morningStart, morningEnd) > 0;
						usedAtNoon 		|= oi.getIntersectionLengthWith(noonStart, noonEnd) > 0;
						usedAtEvening 	|= oi.getIntersectionLengthWith(eveningStart, eveningEnd) > 0;
						usedAtNight 	|= oi.getIntersectionLengthWith(nightStart1, nightEnd1) > 0;
						usedAtNight 	|= oi.getIntersectionLengthWith(nightStart2, nightEnd2) > 0;
					}
				}
			}
					
			result.add(usedAtMorning);
			result.add(usedAtNoon);
			result.add(usedAtEvening);
			result.add(usedAtNight);
			return result;
	}
	
	// calculate if OnIntervals in one usage are increasing
	private static Double extractIncreasingOntimePerUsage(ExtractUsages eus){
		if(eus.size() == 0) return null;
		Double result = 0.0;
		int count = 0;
		for(int i = 0; i < eus.size(); i++){
			ExtractUsage eu = eus.get(i);
			if(eu.getOnintervalsSize() > 1){
				count++;
				result += ProcessorUtilities.isIncreasingIntegerSerie(eu.getOntimes());
			}
		}
		if(count == 0) return null;
		return result / eus.size();
	}
	
	// calculate if OffIntervals in one usage are increasing
	private static Double extractIncreasingOfftimePerUsage(ExtractUsages eus){
		if(eus.size() == 0) return null;
		Double result = 0.0;
		int count = 0;
		for(int i = 0; i < eus.size(); i++){
			ExtractUsage eu = eus.get(i);
			if(eu.getOffintervalsSize() > 1){
				count++;
				result += ProcessorUtilities.isIncreasingIntegerSerie(eu.getOfftimes());
			}
		}
		if(count == 0) return null;
		return result / eus.size();
	}
	// calculate variance of usages by using time
	private static Double[] calculateTimeVariances(ExtractUsages eus){
		Double[] result=new Double[]{null,null,null};
		if(eus.size() < 2) return result;
		
		result[0]=ProcessorUtilities.calcVarInteger(eus.getUsagetimes());
		result[1]=ProcessorUtilities.calcVarInteger(eus.getSumOfftimesPerUsage());
		if(eus.size() < 3) result[2]=ProcessorUtilities.calcVarInteger(eus.getTimesBetweenUsages());
		
		return result;
		
	}

}
