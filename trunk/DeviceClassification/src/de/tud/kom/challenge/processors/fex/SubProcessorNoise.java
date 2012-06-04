package de.tud.kom.challenge.processors.fex;

import java.util.ArrayList;

public final class SubProcessorNoise{
	

	public static String[] getAttributeNames(){
		return new String[] {
				"offintervalsBetweenUsagesNoise_smallest", 	 
				"offintervalsBetweenUsagesNoise_highest", 	
				"offintervalsBetweenUsagesNoise_avg",
				
				"offintervalsInUsageNoise_smallest", 	 
				"offintervalsInUsageNoise_highest", 	
				"offintervalsInUsageNoise_avg",
				

				"onintervalsNoise_smallest", 	 
				"onintervalsNoise_highest", 	
				"onintervalsNoise_avg"
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
				"numeric"
				};
	}
	
	public static ArrayList<Object> process(ExtractUsages eus, ArrayList<TimeInterval> originalData) throws Exception{
		ArrayList<Object> result = new ArrayList<Object>();
		
		//offintervalsBetweenUsagesNoise_smallest 	 
		//offintervalsBetweenUsagesNoise_highest 	
		//offintervalsBetweenUsagesNoise_avg
		ArrayList<Double> offintervalsBetweenUsagesNoise = extractOffintervalsBetweenUsagesNoise(eus, originalData);
		ProcessorUtilities.addResult(result, ProcessorUtilities.calculateSmallestHighestAvgDouble(offintervalsBetweenUsagesNoise));
		
		//offintervalsInUsageNoise_smallest 	 
		//offintervalsInUsageNoise_highest 	
		//offintervalsInUsageNoise_avg
		ArrayList<Double> offintervalsInUsageNoise = extractOffintervalsInUsageNoise(eus, originalData);
		ProcessorUtilities.addResult(result, ProcessorUtilities.calculateSmallestHighestAvgDouble(offintervalsInUsageNoise));

		//onintervalsNoise_smallest 	 
		//onintervalsNoise_highest 	
		//onintervalsNoise_avg
		ArrayList<Double> onintervalsNoise = extractOnintervalsNoise(eus, originalData);
		ProcessorUtilities.addResult(result, ProcessorUtilities.calculateSmallestHighestAvgDouble(onintervalsNoise));
		
		return result;
	}
	
	// returns average powerlevel of offintervals between usages before filtering
	private static ArrayList<Double> extractOffintervalsBetweenUsagesNoise(ExtractUsages eus, ArrayList<TimeInterval> originalData) {
		ArrayList<Double> result = new ArrayList<Double>();
		for(OffInterval o:eus.getOffintervals()){
			ArrayList<TimeInterval> intersect = o.getIntersectionWithList(originalData);
			Double p = 0.0;
			int length = 0;
			for(TimeInterval t:intersect){
				p += Math.abs(t.getLevel() - o.getLevel()) * t.getLength();
				length += t.getLength();
			}
			result.add(p/length);
		}		
		return result;
	}
	
	// returns average powerlevel of offintervals in usages before filtering
	private static ArrayList<Double> extractOffintervalsInUsageNoise(ExtractUsages eus, ArrayList<TimeInterval> originalData) {
		ArrayList<Double> result = new ArrayList<Double>();
		for(ExtractUsage u:eus.getUsages()){
			for(OffInterval o:u.getOffintervals()){
				ArrayList<TimeInterval> intersect = o.getIntersectionWithList(originalData);
				Double p = 0.0;
				int length = 0;
				for(TimeInterval t:intersect){
					p += Math.abs(t.getLevel() - o.getLevel()) * t.getLength();
					length += t.getLength();
				}
				result.add(p/length);
			}
		}		
		return result;
	}

	// returns average powerlevel of noise in onintervals
	private static ArrayList<Double> extractOnintervalsNoise(ExtractUsages eus, ArrayList<TimeInterval> originalData) {
		ArrayList<Double> result = new ArrayList<Double>();
		for(ExtractUsage u:eus.getUsages()){
			for(OnInterval oi:u.getOnintervals()){
				Double p = 0.0;
				int length = 0;
				for(TimeInterval ti:oi.getIntervals()){
					ArrayList<TimeInterval> intersect = ti.getIntersectionWithList(originalData);
					for(TimeInterval i:intersect){
						p += Math.abs(ti.getLevel() - i.getLevel()) * i.getLength();
						length += i.getLength();
					}
				}
				if(length==0){ 
					result.add(0.0);				
				}
				else{
					result.add(p/length);
				}
			}
		}		
		return result;
	}
}
