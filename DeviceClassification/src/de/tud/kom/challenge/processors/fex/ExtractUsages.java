package de.tud.kom.challenge.processors.fex;

import java.util.ArrayList;

public class ExtractUsages {
	private ArrayList<ExtractUsage> usages = new ArrayList<ExtractUsage>();
	private ArrayList<OffInterval> offintervalsBetweenUsages = new ArrayList<OffInterval>();
	private ArrayList<Integer> usagetimes = new ArrayList<Integer>();
	private ArrayList<Integer> timesBetweenUsages = new ArrayList<Integer>();
	private ArrayList<Integer> sumOffTimesPerUsage = new ArrayList<Integer>();
	private ArrayList<Integer> offTimesPerUsage = new ArrayList<Integer>();
	
	public ArrayList<ExtractUsage> getUsages(){
		return usages;
	}
	
	public int size(){
		return usages.size();
	}
	
	public ExtractUsage get(int index){
		return usages.get(index);
	}
	
	private ExtractUsages(){
	}
	
	// build ExtractUsages out of DeviceUsages
	public static ExtractUsages extract(ArrayList<DeviceUsage> usages){
		ExtractUsages theUsages = new ExtractUsages();
		theUsages.usages = ExtractUsage.extract(usages);
		theUsages.onetimeBuild();
		return theUsages;
	}

	// build here lists that is used more than one time to simplify the feature extraction
	private void onetimeBuild() {
		buildOffintervalsBetweenUsages();
		
		for(ExtractUsage eu:usages){
			usagetimes.add(eu.getLength());
			sumOffTimesPerUsage.add(eu.getOfftimeLength());
			for(OffInterval oi: eu.getOffintervals()){
				offTimesPerUsage.add(oi.getLength());
			}
		}
		for(OffInterval oi:offintervalsBetweenUsages){
			timesBetweenUsages.add(oi.getLength());
		}
	}
	
	// build list of offintervals between usages
	private void buildOffintervalsBetweenUsages() {
		if(usages.size() > 1){
			ExtractUsage last = usages.get(0); 
			for(int i = 1; i < usages.size(); i++){
				ExtractUsage current = usages.get(i);
				offintervalsBetweenUsages.add(new OffInterval(last.getLastOninterval(), current.getFirstOninterval()));
				last = current;
			}
		}
	}
	
	// get functions for those lists.
	public ArrayList<OffInterval> getOffintervals(){
		return offintervalsBetweenUsages;
	}
	public ArrayList<Integer> getUsagetimes() {
		return usagetimes;
	}
	public ArrayList<Integer> getTimesBetweenUsages() {
		return timesBetweenUsages;
	}
	public ArrayList<Integer> getSumOfftimesPerUsage() {
		return sumOffTimesPerUsage;
	}
	public ArrayList<Integer> getOfftimesPerUsage() {
		return offTimesPerUsage;
	}
	
	@Override
	public String toString(){
		String ret = "usages{\n";
		for(ExtractUsage eu:usages){
			ret += eu.toString() + "\n";
		}
		ret += "}";
		return ret;
	}
	
	public ArrayList<Double> getUsageEnergies() {
		ArrayList<Double> result = new ArrayList<Double>();
		for(ExtractUsage e:usages){
			result.add(e.getSumEnergy());
		}
		return result;
	}

	public ArrayList<Double> getUsagePowers() {
		ArrayList<Double> result = new ArrayList<Double>();
		for(ExtractUsage e:usages){
			result.add(e.getAvgPower());
		}
		return result;
	}
	
	// build list with all powersteps
	public ArrayList<Double> getPowerSteps() {
		ArrayList<Double> ret = new ArrayList<Double>();
		for(ExtractUsage eu:usages){
			for(OnInterval oi:eu.getOnintervals()){
				double last = 0.0;
				for(TimeInterval ti:oi.getIntervals()){
					ret.add(ti.getLevel() - last);
					last=ti.getLevel();
				}
				ret.add(0 - last);
			}
		}
		return ret;
	}
}

class ExtractUsage {
	private ArrayList<OnInterval> onIntervals = new ArrayList<OnInterval>();
	private ArrayList<OffInterval> offIntervals = new ArrayList<OffInterval>();
	
	public ArrayList<OnInterval> getOnintervals(){
		return onIntervals;
	}
	
	public Integer getOfftimeLength() {
		Integer result = 0;
		for(OffInterval o:offIntervals){
			result += o.getLength();
		}
		return result;
	}

	public OnInterval getFirstOninterval() {
		if(onIntervals.size() > 0) return onIntervals.get(0); 
		return null;
	}

	public OnInterval getLastOninterval() {
		if(onIntervals.size() > 0) return onIntervals.get(getOnintervalsSize() - 1); 
		return null;
	}
	
	public OnInterval getOninterval(int index){
		return onIntervals.get(index);
	}

	public ArrayList<OffInterval> getOffintervals(){
		return offIntervals;
	}
	
	public OffInterval getOffinterval(int index){
		return offIntervals.get(index);
	}
	
	public int getOnintervalsSize(){
		return onIntervals.size();
	}
	
	public int getOffintervalsSize(){
		return offIntervals.size();
	}
	
	public int getLength(){
		if(onIntervals.size() == 0) return 0;
		return (getEnd() - getStart() + 1);
	}
	
	
	public int getStart(){
		if(onIntervals.size() == 0) return 0;
		return onIntervals.get(0).getStart(); 
	}
	
	public int getEnd(){
		if(onIntervals.size() == 0) return 0;
		return onIntervals.get(onIntervals.size() - 1).getEnd();
	}
	
	public int getSumOntimes(){
		if(onIntervals.size() == 0) return 0;
		int result = 0;
		for(OnInterval i:onIntervals){
			result += i.getLength();
		}
		return result;
	}
	
	public int getSumOfftimes(){
		if(onIntervals.size() == 0) return 0;
		int result = 0;
		for(OffInterval i:offIntervals){
			result += i.getLength();
		}
		return result;
	}
	
	public Double getAvgPower(){
		if(onIntervals.size() == 0) return 0.0;
		Double result = 0.0;
		int onTime = 0;
		for(OnInterval i:onIntervals){
			onTime += i.getLength();
			result += i.getAvgPower() * i.getLength();
		}
		return result/onTime;
	}
	
	public Double getSumEnergy(){
		if(onIntervals.size() == 0) return 0.0;
		Double result = 0.0;
		for(OnInterval i:onIntervals){
			result += i.getSumEnergy();
		}
		return result;
	}
	
	public ArrayList<Integer> getOfftimes(){
		ArrayList<Integer> result = new ArrayList<Integer>();
		for(OffInterval i:offIntervals){
			result.add(i.getLength());
		}
		return result;
	}
	
	public ArrayList<Integer> getOntimes(){
		ArrayList<Integer> result = new ArrayList<Integer>();
		for(OnInterval i:onIntervals){
			result.add(i.getLength());
		}
		return result;
	}
	
	private ExtractUsage(){
	}
	
	// build a list of ExtractUsages from list of DeviceUsages
	public static ArrayList<ExtractUsage> extract(ArrayList<DeviceUsage> usages){
		ArrayList<ExtractUsage> result = new ArrayList<ExtractUsage>();
		for(DeviceUsage u:usages){
			result.add(extract(u));
		}
		return result;
	}
	
	// build ExtractUsage from a DeviceUsage
	private static ExtractUsage extract(DeviceUsage u) {
		ExtractUsage e = new ExtractUsage();
		ArrayList<TimeInterval> intervals = u.getIntervals(); 
		for(TimeInterval i:intervals){
			add(e, i);
		}
		return e;
	}
	
	// add a TimeInterval to an ExtractUsage
	// creates On- and OffIntervals
	private static void add(ExtractUsage e, TimeInterval t) {
		/*
		In add we have three cases 
			1- this is the first interval.
			2- this TimeInterval belongs to a previous OnInterval.
			3- this TimeInterval is the first interval in a new OnInterval.
		*/
		
		//case 1
		if(e.onIntervals.size() == 0){
			OnInterval oi = new OnInterval(t);
			e.onIntervals.add(oi);
			return;
		}
		//case 2
		OnInterval last = e.onIntervals.get(e.getOnintervalsSize()-1);
		//System.out.println("last "+last.getEnd()+" start " + t.getStart());
		if((last.getEnd() + 1) == t.getStart()){
			last.add(t);
			return;
		}

		//case 3
		OnInterval oi = new OnInterval(t);
		e.onIntervals.add(oi);
		
		OffInterval offi = new OffInterval(last, oi);
		e.offIntervals.add(offi);
		
		return;		
	}
	
	@Override
	public String toString(){
		if(onIntervals.size() == 0) return "";
		OnInterval on = onIntervals.get(0);
		String start = 	"start=";
		String length = "length=";
		String level = 	"level=";
		OffInterval off = null;
		start += on.getStart();
		length += on.getLength();
		level += on.getLevel();
		for(int i = 1; i < onIntervals.size(); i++){
			off = offIntervals.get(i-1);
			on = onIntervals.get(i);
			
			start += ", " + off.getStart();
			length += ", " + off.getLength();
			level += ", " + off.getLevel();
			
			start += ", " + on.getStart();
			length += ", " + on.getLength();
			level += ", " + on.getLevel();
		}
		return start + "\n" + length + "\n" + level;
	}
}

class OnInterval extends BaseInterval{
	private ArrayList<TimeInterval> intervals = new ArrayList<TimeInterval>();
	public ArrayList<TimeInterval> getIntervals(){
		return intervals;
	}

	public OnInterval(TimeInterval interval) {
		super(0, 0, 0, 0);
		add(interval);
	}
	
	@Override
	public int getStart(){
		if(intervals.size() == 0) return 0;
		return intervals.get(0).getStart();	
	}
	
	@Override
	public int getEnd(){
		if(intervals.size() == 0) return 0;
		return intervals.get(intervals.size() -1).getEnd();
	}
	
	@Override
	public int getLength(){
		if(intervals.size() == 0) return 0;
		return (getEnd() - getStart() + 1);
	}
	
	@Override
	public double getLevel() {
		return getAvgPower();
	}
		
	public double getAvgPower(){
		if(intervals.size() == 0) return 0.0;
		double result = 0.0;
		int time = 0;
		for(TimeInterval i:intervals){
			time += i.getLength();
			result += i.getLevel() * i.getLength();
		}
		return result/time;
	}
	
	public double getSumEnergy(){
		if(intervals.size() == 0) return 0.0;
		double result = 0.0;
		for(TimeInterval i:intervals){
			result += i.getLevel() * i.getLength();
		}
		return result;
	}

	public void add(TimeInterval interval) {
		intervals.add(interval);
		start = getStart();
		length = getLength();
		level = getAvgPower();
	}
	
}

class OffInterval extends BaseInterval{
	private ArrayList<TimeInterval> intervals = new ArrayList<TimeInterval>();
	private OnInterval from = null;
	private OnInterval to = null;
	private int end = 0;
	
	public OffInterval(OnInterval from, OnInterval to) {
		super(0, 0, 0, 0);
		this.from = from;
		this.to = to;
		
		start = this.from.getEnd() + 1;
		end = this.to.getStart() - 1;
		length =  end - start + 1;
	}
	
	public ArrayList<TimeInterval> getIntervals(){
		return intervals;
	}

	
}
	


