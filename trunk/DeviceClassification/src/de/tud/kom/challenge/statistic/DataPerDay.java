package de.tud.kom.challenge.statistic;

import java.util.Collection;
import java.util.Vector;

/**
 * Saves the data of a day in a vector
 * 
 * @author Kristopher
 *
 */

public class DataPerDay extends Vector<String>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public DataPerDay(){
		super();
	}
	
	public DataPerDay(Collection<String> c){
		super(c);
	}
	
	public DataPerDay(int initialCapacity){
		super(initialCapacity);
	}
	
	public DataPerDay(int initialCapacity, int capacityIncrement){
		super(initialCapacity, capacityIncrement);
	}
	
	
	public Double[] getValues(){
		//returnArray has one Element less compared to the DataPerDay-Vector, because the last element of the DataPerDay-Vector is the name of the device
		Double returnArray[] = new Double[this.size()-1];
		for(int i = 0; i<returnArray.length; i++){
		    try {
		    	returnArray[i] = Double.parseDouble(this.get(i));
		    } catch (NumberFormatException e) {
		    	returnArray[i] = Double.NaN;
		    }
		}
		return returnArray;
	}
	
	
	/**
	 * @return the device type of the data set
	 */
	public String getDeviceType(){
		return this.lastElement().toString();
	}

}
