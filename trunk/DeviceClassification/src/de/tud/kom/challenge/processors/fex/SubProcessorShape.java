package de.tud.kom.challenge.processors.fex;

import java.util.ArrayList;

public final class SubProcessorShape{
	
	public final static double PeakLengthPercentage = 5; // accepted percentage of oninterval length for peak length
	public final static double PeakLevelPercentage = 150; // accepted percentage of oninterval power for peak power
	public final static double StartSlopeLengthPercentage = 20; // // accepted percentage of oninterval length for startslope length
	
	public static String[] getAttributeNames(){
		return new String[] {
				"noOfpeaksPerOnInterval_smallest", 	 
				"noOfpeaksPerOnInterval_highest", 	
				"noOfpeaksPerOnInterval_avg",
				
				"peaksRelativePosition_smallest", 	 
				"peaksRelativePosition_highest", 	
				"peaksRelativePosition_avg",
				
				"peaksPowerlevel_smallest", 	 
				"peaksPowerlevel_highest", 	
				"peaksPowerlevel_avg",
				
				
				"startSlopePerOnInterval_smallest", 	 
				"startSlopePerOnInterval_highest", 	
				"startSlopePerOnInterval_avg",
				
				"globalSlopePerOnInterval_smallest", 	 
				"globalSlopePerOnInterval_highest", 	
				"globalSlopePerOnInterval_avg",
				
				"timeOfAvgpowerPerUsage_smallest",
				"timeOfAvgpowerPerUsage_highest",
				"timeOfAvgpowerPerUsage_avg",
				
				"noOfOnIntervalsPerUsage_smallest", 	 
				"noOfOnIntervalsPerUsage_highest", 	
				"noOfOnIntervalsPerUsage_avg",
				
				"noOfUsagesPerDay"	

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
				"numeric",
				
				"numeric"
				};
	}
	
	public static ArrayList<Object> process(ExtractUsages eus, ExtractUsages aboveAvg, double noiseLevel) throws Exception{
		ArrayList<Object> result = new ArrayList<Object>();

		//noOfpeaksPerOnInterval_smallest 	 
		//noOfpeaksPerOnInterval_highest 	
		//noOfpeaksPerOnInterval_avg
		
		//peaksRelativePosition_smallest 	 
		//peaksRelativePosition_highest 	
		//peaksRelativePosition_avg
		
		//peaksPowerlevel_smallest 	 
		//peaksPowerlevel_highest 	
		//peaksPowerlevel_avg
		
		ArrayList<Integer> noOfpeaksPerOnInterval = new ArrayList<Integer>();
		ArrayList<Double> peaksRelativePosition = new ArrayList<Double>();
		ArrayList<Double> peaksPowerlevel = new ArrayList<Double>();
		extractPeaks(eus, noOfpeaksPerOnInterval, peaksRelativePosition, peaksPowerlevel);
		ProcessorUtilities.addResult(result, ProcessorUtilities.calculateSmallestHighestAvgInteger(noOfpeaksPerOnInterval));
		ProcessorUtilities.addResult(result, ProcessorUtilities.calculateSmallestHighestAvgDouble(peaksRelativePosition));
		ProcessorUtilities.addResult(result, ProcessorUtilities.calculateSmallestHighestAvgDouble(peaksPowerlevel));
		
		//startSlopePerOnInterval_smallest	 
		//startSlopePerOnInterval_highest 	
		//startSlopePerOnInterval_avg
		
		//globalSlopePerOnInterval_smallest 	 
		//globalSlopePerOnInterval_highest 	
		//globalSlopePerOnInterval_avg
		ArrayList<Double> startSlopePerOnInterval = new ArrayList<Double>();
		ArrayList<Double> globalSlopePerOnInterval = new ArrayList<Double>();
		ArrayList<Double> startSlopePositionPerOnInterval = new ArrayList<Double>();
		ArrayList<Double> globalSlopePositionPerOnInterval = new ArrayList<Double>();
		ArrayList<Double> startXForBoth = new ArrayList<Double>();
		ArrayList<Double> endXForBoth = new ArrayList<Double>();
		
		extractSlopes(aboveAvg, startSlopePerOnInterval, globalSlopePerOnInterval, startSlopePositionPerOnInterval, globalSlopePositionPerOnInterval, startXForBoth, endXForBoth);
		ProcessorUtilities.addResult(result, ProcessorUtilities.calculateSmallestHighestAvgDouble(startSlopePerOnInterval));
		ProcessorUtilities.addResult(result, ProcessorUtilities.calculateSmallestHighestAvgDouble(globalSlopePerOnInterval));
		
		//timeOfAvgpowerPerUsage_smallest
		//timeOfAvgpowerPerUsage_highest
		//timeOfAvgpowerPerUsage_avg
		ArrayList<Double> timeOfAvgpowerPerUsage = new ArrayList<Double>();
		extractTimeOfAvgpowerPerUsage(eus, timeOfAvgpowerPerUsage, noiseLevel);
		ProcessorUtilities.addResult(result, ProcessorUtilities.calculateSmallestHighestAvgDouble(timeOfAvgpowerPerUsage));
		
		
		//noOfOnIntervalsPerUsage_smallest 	 
		//noOfOnIntervalsPerUsage_highest 	
		//noOfOnIntervalsPerUsage_avg
		ProcessorUtilities.addResult(result, ProcessorUtilities.calculateSmallestHighestAvgInteger(extractNumberOfOnIntervalsPerUsage(eus))); 
		
		//noOfUsagesPerDay
		result.add(eus.size());
		
		return result;
	}
	
	private static ArrayList<Integer> extractNumberOfOnIntervalsPerUsage(ExtractUsages eus) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for(ExtractUsage eu: eus.getUsages()){
			ret.add(eu.getOnintervalsSize());
		}
		return ret;
	}
	
	// extract slope for OnIntervals by using linear regression
	public static void extractSlopes(ExtractUsages eus, ArrayList<Double> startSlopePerOnInterval, ArrayList<Double> globalSlopePerOnInterval, 
			ArrayList<Double> startSlopePositionPerOnInterval, ArrayList<Double> globalSlopePositionPerOnInterval, ArrayList<Double> startXForBoth, ArrayList<Double> endXForBoth) {
		for(ExtractUsage eu:eus.getUsages()){
			
			for(OnInterval oi:eu.getOnintervals()){
				int totalOnTime = oi.getLength();
				int startSlopeLength = (int)(totalOnTime * StartSlopeLengthPercentage/100);
				
				
				int currentTimeFromBeginingOfInterval = 0;
				ArrayList<TimeInterval> is = oi.getIntervals();
				
				ArrayList<Double> sx = new ArrayList<Double>(); //start x
				ArrayList<Double> sy = new ArrayList<Double>(); //start y
				
				ArrayList<Double> gx = new ArrayList<Double>(); //global x
				ArrayList<Double> gy = new ArrayList<Double>(); //global y
				
				
				
				if(is.size() > 1){
					
					TimeInterval last = is.get(0);
					currentTimeFromBeginingOfInterval += last.getLength();
					
					sx.add((last.getStart() + last.getEnd())/2.0);
					sy.add(last.getLevel());
					
					gx.add((last.getStart() + last.getEnd())/2.0);
					gy.add(last.getLevel());
					for(int i = 1; i < is.size(); i++){
						TimeInterval current = is.get(i);
						if(currentTimeFromBeginingOfInterval < startSlopeLength){// is it included is startSlope or not
							currentTimeFromBeginingOfInterval += current.getLength();
							
							sx.add((current.getStart() + current.getEnd())/2.0);
							sy.add(current.getLevel());
						}
						
						gx.add((current.getStart() + current.getEnd())/2.0);
						gy.add(current.getLevel());
						last = current;
					}
				}
				double [] s = ProcessorUtilities.linearRegressionLeastSquare(sx, sy);
				double [] g = ProcessorUtilities.linearRegressionLeastSquare(gx, gy);
				
				startSlopePerOnInterval.add(s[0]);
				globalSlopePerOnInterval.add(g[0]);
				
				startSlopePositionPerOnInterval.add(s[1]);
				globalSlopePositionPerOnInterval.add(g[1]);
				
				if(is.size() > 1){
					startXForBoth.add(sx.get(0));
					endXForBoth.add((double)is.get(is.size()-1).getEnd());
					
				}else if(is.size() == 1){
					startXForBoth.add((is.get(0).getStart() + is.get(0).getEnd())/2.0);
					endXForBoth.add((double)is.get(is.size()-1).getEnd());
				}
			}
		}
	}
	
	// extract peaks by observing length and power
	private static void extractPeaks(ExtractUsages eus, ArrayList<Integer> noOfpeaksPerOnInterval, ArrayList<Double> peaksRelativePosition, ArrayList<Double> peaksPowerlevel) {
		for(ExtractUsage eu:eus.getUsages()){
			int usageTime = eu.getLength();
			int maxPeakTime = (int)(usageTime * PeakLengthPercentage /100);
			maxPeakTime = Math.max(1, maxPeakTime);
			
			double usageLevel = eu.getAvgPower();
			double minPeakLevel = usageLevel * PeakLevelPercentage / 100;
			minPeakLevel = Math.min(10, minPeakLevel);
			
			int numberOfPeaks = 0;
			for(OnInterval oi:eu.getOnintervals()){
				for(TimeInterval ti:oi.getIntervals()){
					if((ti.getLength() <= maxPeakTime) && (ti.getLevel() >= minPeakLevel)){
						numberOfPeaks++;
						double startpositionPercentage = (ti.getStart() - oi.getStart());
						startpositionPercentage = startpositionPercentage / oi.getLength();
						peaksRelativePosition.add(startpositionPercentage);
						peaksPowerlevel.add(ti.getLevel());
					}
				}
				noOfpeaksPerOnInterval.add(numberOfPeaks);
			}
		}		
	}
	
	// calculate how long the signal is near average power
	private static void extractTimeOfAvgpowerPerUsage(ExtractUsages eus, ArrayList<Double> timeOfAvgpowerPerUsage, double noiseLevel){
		for(ExtractUsage eu : eus.getUsages()){
			double minPowerlevel= eu.getAvgPower()-noiseLevel;
			double maxPowerlevel= eu.getAvgPower()+noiseLevel;
			double length=0;
			for(OnInterval oni: eu.getOnintervals()){
				for(TimeInterval ti: oni.getIntervals()){
					if((ti.getLevel()>minPowerlevel)&&(ti.getLevel()<maxPowerlevel)){
						length+=ti.getLength();
					}
				}
			}
			timeOfAvgpowerPerUsage.add(length/eu.getLength());
		}
	}
	
}
