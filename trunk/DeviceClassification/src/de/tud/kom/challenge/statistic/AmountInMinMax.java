package de.tud.kom.challenge.statistic;

import java.util.Vector;


/**
 * provides the functionality to analyse how much of the compared datasets are in Min/Max range of the targetdevice
 * 
 * @author Kristopher
 *
 */

public class AmountInMinMax {
	
	
	private Double[] min;
	private Double[] max;
	private Vector<Double[]> dataVector;
	private Double[] resultValues;
	
	
	//constructor
	public AmountInMinMax(){
		dataVector = new Vector<Double[]>();
	}
	
	public void addData(Double newData[]){
		dataVector.add(newData);
	}
	
	
	public String[] getResult(){
		String resultArray[] = new String[(Math.max(this.min.length, this.max.length))+1];
		resultArray[0] = "in [min/max]";
		
		//differentiation of valid and invalid amount of data
		if((this.min != null) && (this.max!= null)){
			resultArray = analyse(this.min, this.max, this.dataVector);
		}else if(this.min == null){
			resultArray[1] = "min-values missing";
			System.out.println("!!min-values missing");
		}else if(this.max == null){
			resultArray[1] = "max-values missing";
			System.out.println("!!max-values missing");			
		}
		return resultArray;
	}

	private String[] analyse(Double[] min, Double[] max, Vector<Double[]> dataVector) {

		int sizeOfResultArray = Math.max(min.length, max.length);
		
		//initialize the Vector consisting of Strings for output in the SFF files
		Vector<String> resultVector = new Vector<String>();
		resultVector.add("in [min/max]");
		//initialize the Array consisting of Double values for calculating the weighting
		resultValues = new Double[sizeOfResultArray];
		//Schleife über die Features
		for(int i = 0; i<sizeOfResultArray; i++){
			int numberOfRelevantData = 0;
			int isBetween = 0;
			//reading data of the dataVector on Position 'i'
			Vector<Double> dobulevaluesOfFeature = new Vector<Double>();
			//Schleife über die Datensätze der Geräte
			for(int j = 0; j<dataVector.size(); j++){
				Double currentDataOfDevice[] = dataVector.get(j);
				try{
					double currentDoubleValue = currentDataOfDevice[i];
					if(dobulevaluesOfFeature.add(currentDoubleValue)){
						numberOfRelevantData++;
						double currentMin = min[i];
						double currentMax = max[i];
						System.out.print(+min[i] +"<=" +currentDoubleValue +" && " +currentDoubleValue  +"<=" +max[i]);
						if(currentMin <= currentDoubleValue && currentDoubleValue <= currentMax){
							isBetween++;
							System.out.println(" -> true");
						}
					}
					}
					catch(Exception e){
						System.out.println("Exception");
					}
				}
			//saving the result for one Feature in the syntax: 'isBetween-"of"-numberOfRelevantData'
			resultVector.add(String.valueOf(isBetween).concat("of").concat(String.valueOf(numberOfRelevantData)));
			//calculating double Value for 'resultValues'
			resultValues[i] = (double)isBetween/(double)numberOfRelevantData;
			
		}
		String[] resultArray = resultVector.toArray(new String[resultVector.size()]);
		return resultArray;
	}
 
	public Double[] getValues() {
		return resultValues;
	}

	public void setMinValues(Double[] doubleValues) {
		this.min = doubleValues;
			}

	public void setMaxValues(Double[] doubleValues) {
		this.max = doubleValues;		
	}
	
	
	

}