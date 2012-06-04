package de.tud.kom.challenge.processors.fex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import de.tud.kom.challenge.arff.FileMapper;

public class MATLABVisualizer {
	private static int matlabSavedFileCount = 1; // counter for labeling

	// generate matlabscript to visualize TimeIntervals
	private static void saveIntervalsMATLAB(ArrayList<TimeInterval> intervals, BufferedWriter out, String prefix) throws Exception{
		StringBuilder p1 = new StringBuilder("time_" + prefix + "=[");
		StringBuilder p2 = new StringBuilder("level_" + prefix + "=[");
		StringBuilder p3 = new StringBuilder("steps_" + prefix + "=[");
		StringBuilder p4 = new StringBuilder("lengths_" + prefix + "=[");
		boolean first = true;
		
		for(TimeInterval interval : intervals){
				if( !first){
					p1.append(",");
					p2.append(",");
					p3.append(",");
					p4.append(",");
				}
				first = false;
				p1.append(interval.getStart());
				p2.append(interval.getLevel());
				p3.append(interval.getPowerStep());
				p4.append(interval.getLength());
			}
			out.append(p1.toString());
			out.append("];\n");
			out.append(p2.toString());
			out.append("];\n");
			out.append(p3.toString());
			out.append("];\n");
			out.append(p4.toString());
			out.append("];\n");
			

			out.append("[x_" + prefix + " y_" + prefix + "] = mkstep(time_" + prefix + ", level_" + prefix + ", lengths_" + prefix + ", 0);\n");
	}
	
	// convert ArrayList to matlabarray
	private static String getMATLABArray(ArrayList<Double> d){
		if(d.size() == 0) return "[]";
		if(d.size() == 1) return "[" + d.get(0) + "]";
		String ret = d.get(0) + "";
		for(int i = 1; i < d.size(); i++){
			ret += ", " + d.get(i);
		}
		return ("[" + ret + "];");
	}
	
	// convert array to matlabarray
	private static String getMATLABArray(double[] d){
		if(d.length == 0) return "[]";
		if(d.length == 1) return "[" + d[0] + "]";
		String ret = d[0] + "";
		for(int i = 1; i < d.length; i++){
			ret += ", " + d[i];
		}
		return ("[" + ret + "];");
	}

	
	// generate matlab script to visualize different intervals
	public static void saveMATLAB(ArrayList<TimeInterval> intervals1, ArrayList<TimeInterval> intervals2, ArrayList<DeviceUsage> usages, String deviceName, String fileName) {
		if(deviceName == null){
			deviceName = "Unknown";
		}
		
		if(fileName == null){
			fileName = FileMapper.trainingPath + File.separator + Integer.toString(matlabSavedFileCount) + ".m";
			matlabSavedFileCount++;
		}
		try {
			FileWriter fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);
			
			String prefix1 = "i1";
			saveIntervalsMATLAB(intervals1, out, prefix1);
			//out.append("plot(x_" + prefix + " ,y_" + prefix + "y);\n");
			
			String prefix2 = "i2";
			saveIntervalsMATLAB(intervals2, out, prefix2);
			//out.append("plot(x_" + prefix + " ,y_" + prefix + "y);\n");
			
			for(int i = 0; i < usages.size(); i++){
				saveIntervalsMATLAB(usages.get(i).getIntervals(), out, Integer.toString(i));				
			}
			
			out.append("subplot(2, 1, 1), plot(x_i1, y_i1, x_i2, y_i2, '--' ");

			for(int i = 0; i < usages.size(); i++){
				String prefix = Integer.toString(i);
				out.append(", x_" + prefix + ", y_" + prefix + ", ':'");
			}
			out.append(");\n");
			out.append("ylim([0 max(y_i1)]);\n");
			out.append("xlim([min(x_i1) max(x_i1)]);\n");
			out.append("title('" + deviceName + "');\n");
			
			out.append("legend('intervals 1', 'intervals 2'");
			for(int i = 0; i < usages.size(); i++){
				String prefix = Integer.toString(i);
				out.append(", 'usage_" + prefix + "'");
			}
			out.append(");\n");
			
			out.append("subplot(2, 1, 2), plot(");
			for(int i = 0; i < usages.size(); i++){
				String prefix = Integer.toString(i);
				if(i != 0) out.append(", ");
				out.append("x_" + prefix + ", y_" + prefix );
			}
			out.append(");\n");
			out.append("ylim([0 max(y_i1)]);\n");
			out.append("xlim([min(x_i1) max(x_i1)]);\n");
			out.append("title('" + deviceName + "');\n");
			out.append("legend(");
			for(int i = 0; i < usages.size(); i++){
				String prefix = Integer.toString(i);
				if(i != 0) out.append(", ");
				out.append("'usage_" + prefix + "'");
			}
			out.append(");\n");
			
			out.close();
			fstream.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	// generate matlabscript to test l1 trendfiltering
	public static void saveMATLAB_l1tfTest(int[] time_i1, double[] level_i1, double[] steps_i1, int[] lengths_i1, double[] x, Boolean status, double lambda) {
		String fileName = FileMapper.trainingPath + File.separator + "test_java_l1tf" + ".m";
		
		try {
			//write to MATLAB file  the following
			//time_i1
			//level_i1
			//steps_i1
			//lengths_i1
			//[x_i1 y_i1] = mkstep(time_i1, level_i1, lengths_i1, 0);
			FileWriter fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);
			
			ArrayList<TimeInterval> temp = new ArrayList<TimeInterval>();
			for(int i = 0;i < time_i1.length; i++){
				temp.add(new TimeInterval(level_i1[i], time_i1[i], lengths_i1[i], steps_i1[i]));
			}
			saveIntervalsMATLAB(temp, out, "i1");
			
			//continue the rest of the file as in the following string.
			out.append("java_out = " + getMATLABArray(x));
			
			out.append(
					"lambda_max = l1tf_lambdamax(level_i1'); \n" +
					"%lambda_max = min(lambda_max, 100);\n" +
					"[z1, status] = l1tf(level_i1', " + lambda + "*lambda_max);\n" +
					"[x_z_i1 y_z_i1] = mkstep(time_i1, z1, lengths_i1, 0);\n" +
					"[x_java_i1 y_java_i1] = mkstep(time_i1, java_out, lengths_i1, 0);\n" +
					"\n" +
					"\n" +
					"%function y = i2ts(x1, y1, lengths);\n" +
					"all1 = i2ts(time_i1, level_i1, lengths_i1);\n" +
					"all2 = i2ts(time_i1, z1, lengths_i1);\n" +
					"all3 = i2ts(time_i1, java_out, lengths_i1);\n" +
					"\n" +
					"subplot(2,1, 1);\n" +
					"hold on;\n" +
					"plot(x_i1, y_i1, 'b-');\n" +
					"plot(x_z_i1, y_z_i1, 'r--');\n" +
					"plot(x_java_i1, y_java_i1, 'g:');\n" +
					"legend('original', 'total variation extended', 'java_out');\n" +
					"\n" +
					"subplot(2,1, 2);\n" +
					"hold on;\n" +
					"plot(all1, 'b-');\n" +
					"plot(all2, 'r--');\n" +
					"plot(all3, 'g:');\n" +
					"legend('original all', 'total variation all', 'java_out all');\n" +
					"hold off;\n" +
					"\n" +
					"figure;\n" +
					"hold on;\n" +
					"plot(all1, 'b-');\n" +
					"plot(all2, 'r--');\n" +
					"plot(all3, 'g:');\n" +
					"legend('original all', 'total variation all', 'java_out all');\n" +
					"hold off;\n");
			
			out.append("\n");
			out.close();
			fstream.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	// generate matlab script to test global slopes
	public static void saveMATLAB_TestLeastSquare(ArrayList<TimeInterval> original, ExtractUsages aboveAverage, ArrayList<DeviceUsage> usages, String deviceName, String fileName) {
		if(deviceName == null){
			deviceName = "Unknown";
		}
		
		if(fileName == null){
			fileName = FileMapper.trainingPath + File.separator + Integer.toString(matlabSavedFileCount) + ".m";
			matlabSavedFileCount++;
		}
		

		ArrayList<Double> sm = new ArrayList<Double>();
		ArrayList<Double> gm = new ArrayList<Double>();
		ArrayList<Double> sx0 = new ArrayList<Double>();
		ArrayList<Double> gx0 = new ArrayList<Double>();
		
		ArrayList<Double> startXForBoth = new ArrayList<Double>();
		ArrayList<Double> endXForBoth = new ArrayList<Double>();
		
		SubProcessorShape.extractSlopes(aboveAverage, sm, gm, sx0, gx0, startXForBoth, endXForBoth);
		
		try {
			FileWriter fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);
			
			String prefix1 = "i1";
			saveIntervalsMATLAB(original, out, prefix1);
	
						
			out.append("sm = " + getMATLABArray(sm) + "\n");
			out.append("gm = " + getMATLABArray(gm) + "\n");
			
			out.append("sx0 = " + getMATLABArray(sx0) + "\n");
			out.append("gx0 = " + getMATLABArray(gx0) + "\n");
			
			
			out.append("startXForBoth = " + getMATLABArray(startXForBoth) + "\n");
			out.append("endXForBoth = " + getMATLABArray(endXForBoth) + "\n");
			for(int i = 0; i < usages.size(); i++){
				saveIntervalsMATLAB(usages.get(i).getIntervals(), out, Integer.toString(i));				
			}
			for(int i = 0; i < startXForBoth.size(); i++){
				String prefix = Integer.toString(i);
				String prefix_m = Integer.toString(i+1);
				out.append("slopex_" +  prefix + " = " + "startXForBoth(" + prefix_m + "):1:endXForBoth(" + prefix_m + ");\n");
				out.append("slopey_" +  prefix + " = " + "gm(" + prefix_m + ")*slopex_" + prefix + " + gx0(" + prefix_m +");\n");
			}
			
			out.append("subplot(2, 1, 1), plot(x_i1, y_i1");

			for(int i = 0; i < usages.size(); i++){
				String prefix = Integer.toString(i);
				out.append(", x_" + prefix + ", y_" + prefix + ", ':'");
			}
			
			
			out.append(");\n");
			out.append("ylim([0 max(y_i1)]);\n");
			out.append("xlim([min(x_i1) max(x_i1)]);\n");
			out.append("title('" + deviceName + "');\n");
			
			out.append("legend('intervals 1'");
			for(int i = 0; i < usages.size(); i++){
				String prefix = Integer.toString(i);
				out.append(", 'usage_" + prefix + "'");
			}
			out.append(");\n");
			
			out.append("subplot(2, 1, 2), plot(");
			for(int i = 0; i < usages.size(); i++){
				String prefix = Integer.toString(i);
				if(i != 0) out.append(", ");
				out.append("x_" + prefix + ", y_" + prefix );
			}
			for(int i = 0; i < startXForBoth.size(); i++){
				String prefix = Integer.toString(i);
				out.append(", slopex_" + prefix + ", slopey_" + prefix );
				
			}
			
				
			out.append(");\n");
			out.append("ylim([0 max(y_i1)]);\n");
			out.append("xlim([min(x_i1) max(x_i1)]);\n");
			out.append("title('" + deviceName + "');\n");
			out.append("legend(");
			for(int i = 0; i < usages.size(); i++){
				String prefix = Integer.toString(i);
				if(i != 0) out.append(", ");
				out.append("'usage_" + prefix + "'");
			}
			for(int i = 0; i < startXForBoth.size(); i++){
				String prefix = Integer.toString(i);
				out.append(", 'slope_" + prefix + "'");
			}
			out.append(");\n");
			
			out.close();
			fstream.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
