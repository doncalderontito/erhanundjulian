package de.tud.kom.challenge.processors.fex;

import java.util.ArrayList;

public class ProcessorUtilities {
	
	public static double min(ArrayList<Double> lst){
		double ret = Double.MAX_VALUE;
		for(double d:lst){
			if(d < ret){
				ret = d;
			}
		}
		return ret;
	}
	
	public static double max(ArrayList<Double> lst){
		double ret = Double.MIN_VALUE;
		for(double d:lst){
			if(d > ret){
				ret = d;
			}
		}
		return ret;
	}
	
	// calculate linear regression
	public static double[] linearRegressionLeastSquare(ArrayList<Double> xx, ArrayList<Double> yy){
		if(yy.size() < 2){
			return new double[]{0, 0};
		}
		
		ArrayList<Double> x = new ArrayList<Double>();
		ArrayList<Double> y = new ArrayList<Double>();
		int xMin = (int)min(xx);
		int xMax = (int)max(xx);
		int yIndex = 0;
		for(int i = xMin; i <= xMax; i++){
			x.add((double)i);
			if(xx.get(yIndex) < i){
				yIndex++;
			}
			y.add(yy.get(yIndex));
		}
		
		double a = 0.0;
		double b = 0.0;
		double ssxy = 0.0, ssxx = 0.0;
		double m_x = 0.0, m_y = 0.0;

		for(int i = 0;i < y.size(); i++){
			m_x += x.get(i);
			m_y += y.get(i);
		}
		m_x = m_x/x.size();
		m_y = m_y/y.size();

		for(int i = 0;i < y.size(); i++){
			ssxy += (x.get(i) - m_x) * (y.get(i) - m_y);
			ssxx += (x.get(i) - m_x) * (x.get(i) - m_x);
		}
		
		ssxy = ssxy / x.size();
		ssxx = ssxx / x.size();
		
		a = ssxy / ssxx;
		b = m_y - (a * m_x);
		
		return new double[]{a, b};
	}
	
	//check if this list of integer is increasing
	public static Integer isIncreasingIntegerSerie(ArrayList<Integer> lst){
		if(lst == null || lst.size() < 2) 
			return null;
		int result = 0;
		for(int i = 1; i < lst.size(); i++){
			int dif = lst.get(i) - lst.get(i - 1);
			result += dif;
		}
		return result;
	}
	
	// calculate smallest, highest and average for ArrayList of type integer
	public static ArrayList<Object> calculateSmallestHighestAvgInteger(ArrayList<Integer> lst){
		ArrayList<Object> result = new ArrayList<Object>();
		if(lst == null || lst.size() == 0) {
			result.add(null);
			result.add(null);
			result.add(null);
			return result;
		} 
		int s = Integer.MAX_VALUE;
		int h = Integer.MIN_VALUE;
		double a = 0;
		for(int i:lst){
			s = Math.min(s, i);
			h = Math.max(h, i);
			a += i;
		}
		result.add(s);
		result.add(h);
		result.add(a/lst.size());
		return result;
	}
	
	public static Integer sumInteger(ArrayList<Integer> lst){
		if(lst == null || lst.size() == 0) return null;
		int result = 0;
		for(int i: lst){
			result += i; 
		}
		return result;
	}
	
	//check if this list of double is increasing
	public static Double isIncreasingDoubleSerie(ArrayList<Double> lst){
		if(lst == null || lst.size() < 2) 
			return null;
		Double result = 0.0;
		for(int i = 1; i < lst.size(); i++){
			double dif = lst.get(i) - lst.get(i - 1);
			result += dif;
		}
		return result;
	}
	// calculate smallest, highest and average for ArrayList of type double
	public static ArrayList<Object> calculateSmallestHighestAvgDouble(ArrayList<Double> lst){
		ArrayList<Object> result = new ArrayList<Object>();
		if(lst == null || lst.size() == 0) {
			result.add(null);
			result.add(null);
			result.add(null);
			return result;
		} 
		Double s = Double.MAX_VALUE;
		Double h = 0.0;
		double a = 0;
		for(double i:lst){
			s = Math.min(s, i);
			h = Math.max(h, i);
			a += i;
		}
		result.add(s);
		result.add(h);
		result.add(a/lst.size());
		return result;
	}
	
	public static Double sumDouble(ArrayList<Double> lst){
		if(lst == null || lst.size() == 0) return null;
		Double result = 0.0;
		for(double i: lst){
			result += i; 
		}
		return result;
	}	
	
	// copy utilities.
	public static ArrayList<TimeInterval> copyTimeIntervals(ArrayList<TimeInterval> intervals) {
		ArrayList<TimeInterval> out = new ArrayList<TimeInterval>();
		for(TimeInterval i:intervals){
			out.add(i.newCopy());
		}
		return out;
	}
	
	public static ArrayList<DeviceUsage> copyDeviceUsages(ArrayList<DeviceUsage> usages) {
		ArrayList<DeviceUsage> out = new ArrayList<DeviceUsage>();
		for(DeviceUsage u:usages){
			out.add(u.newCopy());
		}
		return out;
	}

	public static String objectToString(Object o){
		if(o == null)
			return "?";
		else
			return o.toString();
	}
	
	public static void addResult(ArrayList<Object> result, ArrayList<Object> toBeAdded){
		for(Object o:toBeAdded){
			result.add(o);
		}
	}
	
	public static Double calcMeanInteger(ArrayList<Integer> list){
		return (double) (sumInteger(list)/list.size());
	}
	
	// caclulate variance for arraylist of type integer
	public static Double calcVarInteger(ArrayList<Integer> list){
		Double var=0.0;
		Double mean=calcMeanInteger(list);
		for(Integer i : list){
			var+=(i-mean)*(i-mean);
		}
		var=var/(list.size()-1);
		return var;
	}
	
	public static Double calcMeanDouble(ArrayList<Double> list){
		return sumDouble(list)/list.size();
	}
	
	// caclulate variance for arraylist of type double
	public static Double calcVarDouble(ArrayList<Double> list){
		Double var=0.0;
		Double mean=calcMeanDouble(list);
		for(Double d : list){
			var+=(d-mean)*(d-mean);
		}
		var=var/(list.size()-1);
		return var;
	}
}
