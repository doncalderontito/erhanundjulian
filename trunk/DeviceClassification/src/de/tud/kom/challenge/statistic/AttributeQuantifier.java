package de.tud.kom.challenge.statistic;

import java.util.HashMap;
import java.util.Map;

/**
 * Saves the data to quantifie the Attributes
 * 
 * @author Kristopher
 *
 */

												//devicename,<attribute,weighting>
public class AttributeQuantifier extends HashMap<String,HashMap<String,Double>>{
	
	final double boundaryToBoostLowValues = 0.25;
	final double boundaryToBoostHighValues = 0.75;
	final double boostRatioHigh = 100.0;
	final double boostRatioLow = 100.0;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//plain constructor
	public AttributeQuantifier(){
	}

	//adding Data to a Device
	//use this method if low values help to identify important attributes, because they differ from 1 in fact to manipulated devices or they are other devices then the inspected 
	public void lowValuesAreBeneficial(String deviceName, HashMap<String, Double> deviceHashMap) {
		//if device does not exist it must be created
		if(!this.containsKey(deviceName)){
			this.put(deviceName, new HashMap<String,Double>());
		}
		this.boostLowValues(deviceName, deviceHashMap);
	}
	
	public void lowValuesAreBeneficial(String deviceName, HashMap<String, Double> deviceHashMap, Double boundaryToBoost) {
		//if device does not exist it must be created
		if(!this.containsKey(deviceName)){
			this.put(deviceName, new HashMap<String,Double>());
		}
		this.boostLowValues(deviceName, deviceHashMap, boundaryToBoost);
	}

	
	
	private void boostLowValues(String deviceName, HashMap<String, Double> deviceHashMap){
		this.boostLowValues(deviceName, deviceHashMap, boundaryToBoostLowValues);
	}
	
	
	//WICHTIG: hier findet gewichtung der Attribute statt
	private void boostLowValues(String deviceName, HashMap<String, Double> deviceHashMap, Double boundaryToBoost) {
		HashMap<String,Double> currentDeviceHashMap = this.get(deviceName);
		//loop over the Attributes
		for(Map.Entry<String, Double> e : deviceHashMap.entrySet()){
			//if Attribute does not exist it must be created
			if(!currentDeviceHashMap.containsKey(e.getKey())){
				currentDeviceHashMap.put(e.getKey(), 1.0);
			}
			try{	
				if(e.getValue()<boundaryToBoost){
					double newValue = currentDeviceHashMap.get(e.getKey())*boostRatioLow;
					currentDeviceHashMap.put(e.getKey(), newValue);
				}
			}catch(Exception exeption){}
		}
	}
	
	//adding Data to a Device
	//use this method if high values(near 1) appear on  devices which are most likely the same devices and without errors
	public void highValuesAreBeneficial(String deviceName, HashMap<String, Double> deviceHashMap) {
		//if device does not exist it must be created
		if(!this.containsKey(deviceName)){
			this.put(deviceName, new HashMap<String,Double>());
		}
		this.boostHighValues(deviceName, deviceHashMap);
	}

	
	private void boostHighValues(String deviceName, HashMap<String, Double> deviceHashMap){
		this.boostHighValues(deviceName, deviceHashMap, boundaryToBoostHighValues);
	}
	
	//WICHTIG: hier findet gewichtung der Attribute statt	
	private void boostHighValues(String deviceName, HashMap<String, Double> deviceHashMap, Double boundaryToBoost) {
		HashMap<String,Double> currentDeviceHashMap = this.get(deviceName);
		//loop over the Attributes
		for(Map.Entry<String, Double> e : deviceHashMap.entrySet()){
			System.out.println("e.getKey(): "+e.getKey());
			//if Attribute does not exist it must be created
			if(!currentDeviceHashMap.containsKey(e.getKey())){
				currentDeviceHashMap.put(e.getKey(), 1.0);
			}
			try{
				//TODO - je nach grad der abweichung(quotient) unterschiedlich hohe WErte addieren //anpassen des Bewertungsschemas
				if(e.getValue()>boundaryToBoost){
					double newValue = currentDeviceHashMap.get(e.getKey())*boostRatioHigh;
					currentDeviceHashMap.put(e.getKey(), newValue);
				}
			}catch(Exception exeption){}
		}
		
	}

	public Double[] getWeighting(String deviceName, String[] dataTypes) {
		System.out.println("process getWeighting()");
		Double resultArray[] = new Double[dataTypes.length];
		if(this.get(deviceName) == null){
			System.out.println("No quantification data for "+deviceName+"!");
			return null;
		}
		HashMap<String,Double> deviceHashMap = this.get(deviceName);
		for(int i = 0; i<dataTypes.length; i++){
			if(deviceHashMap.get(dataTypes[i]) == null){
				resultArray[i] = Double.NaN;
				System.out.println("deviceHashMap.get(dataTypes[i]) == null    for: "+dataTypes[i]);
			} else {
				System.out.println("deviceHashMap.get(dataTypes[i]): "+deviceHashMap.get(dataTypes[i])+" for: "+dataTypes[i]);
				resultArray[i] = deviceHashMap.get(dataTypes[i]);
			}
		}
		return resultArray;
	}
	

}
