package de.tud.kom.challenge.statistic;

import java.util.Vector;

/**
 * Saves the data of a device in a vector
 * 
 * @author Kristopher
 *
 */

public class DataPerDevice extends Vector<DataPerDay> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
//	public DataPerDevice(){
//		super();
//	}
//	
//	public DataPerDevice(Collection c){
//		super(c);
//	}
//	
//	public DataPerDevice(int initialCapacity){
//		super(initialCapacity);
//	}
//	
//	public DataPerDevice(int initialCapacity, int capacityIncrement){
//		super(initialCapacity, capacityIncrement);
//	}

	public Vector<String> getDataOfAttribute(Integer attributeIndex) {
		Vector<String> returnVector = new Vector<String>();
		for(int i = 0; i<this.size(); i++){
			returnVector.add(this.get(i).get(attributeIndex));
		}
		return returnVector;
	}

}
