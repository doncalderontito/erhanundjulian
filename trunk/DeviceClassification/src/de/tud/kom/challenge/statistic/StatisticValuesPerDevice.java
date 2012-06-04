package de.tud.kom.challenge.statistic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;

/**
 * Contains a mapping of the name of a Attribute(Feature) and the related 'StatisticValuePerAttribute' Objects
 * 
 * @author Kristopher
 *
 */

public class StatisticValuesPerDevice extends HashMap<String, StatisticValuesPerAttribute> {
	
	private String devicename; 
	
	//
	private TreeSet<String> statisticValues = new TreeSet<String>();
	
	//Konstruktor
	public StatisticValuesPerDevice(String devicename, CompleteDataset completeDataset){
		this.devicename = devicename;
		System.out.println("devicename: "+devicename);
		Vector attributes = completeDataset.getAttributes();
		System.out.println("Attributes: "+Arrays.toString(attributes.toArray()));
		Vector dataTypes = completeDataset.getDataTypes();
		DataPerDevice dataOfCurrentDevice = completeDataset.get(devicename);
		
		for(int currentAttribute = 0; currentAttribute<attributes.size(); currentAttribute++){
			String currentAttributeName = attributes.get(currentAttribute).toString();
			String currentAttributeDatatype = dataTypes.get(currentAttribute).toString();
			System.out.println("currentAttribute: Nr.: "+currentAttribute+"/"+attributes.size()+" Name: "+currentAttributeName+" DataType: "+currentAttributeDatatype);
			Vector<String> dataOfCurrentAttribute = dataOfCurrentDevice.getDataOfAttribute(currentAttribute);
			this.put(attributes.get(currentAttribute).toString(), new StatisticValuesPerAttribute(currentAttributeName, dataOfCurrentAttribute, currentAttributeDatatype));
		}
		
		// erstellen des Vectors vorhandener statistischer Werte
		//schleife �ber alle StatisticValuePerAttribute
			//auslesen der statistischen Werte und die noch nicht vorhandenen dem Vector anf�gen
		for ( Iterator statisticValuePerAttributeIterator = this.values().iterator(); statisticValuePerAttributeIterator.hasNext(); )
		{
			//auslesen der statistischen Werte und die noch nicht vorhandenen dem TreeSet 'statisticValues' anf�gen
			StatisticValuesPerAttribute currentStatisticValuePerAttribute = (StatisticValuesPerAttribute) statisticValuePerAttributeIterator.next();
			statisticValues.addAll(currentStatisticValuePerAttribute.keySet());
			
		}
		
		//sortierung des TreeSet's 'statisticValues' n�tig?
		

		
	}
	
	
	public String[] getStatisticValues(){
		

		String[] statisticValuesArray = Arrays.asList(statisticValues.toArray()).toArray(new String[statisticValues.toArray().length]);
//				Arrays.copyOf(statisticValues.toArray(), statisticValues.toArray().length, String[].class);
		return statisticValuesArray;
	}
	
	
	public StatisticValuesPerAttribute getStatisticValue(String attribute){
		return this.get(attribute);
	}
	
	
	public String getDevicename(){
		return devicename;
	}
	
	
	public int getMaxNumberOfDatasets(){
		int maxValue = 0;
		for(Map.Entry<String, StatisticValuesPerAttribute> e : this.entrySet()){
			if(e.getValue().getNumberOfDatasets()>maxValue)
				maxValue = e.getValue().getNumberOfDatasets();
		}
		return maxValue;
	}
	
	
	
	//TODO - �berarbeiten! Die Anzahl der statistischen WErte muss globaler vorhandne sein.
		//Zumindest f�r jedes Ger�t muss 'ersichtlich'sein Wieviel und welche statistischen Werte existieren
	public int maximumNumberOfStatisticalValues(){
		int returnValue = 0;
		//'StatisticValuePerAttribute'-Array containing the Values of the 'StatisticValuesPerDevice'-Object
		//TODO - checken ob das Array nur die Values des 'StatisticValuesPerDevice'-Object enth�lt oder auch die Keys
		StatisticValuesPerAttribute ownValues[] = null;
		ownValues = this.entrySet().toArray(ownValues);
		//schleife �ber alle enthaltenen 'StatisticValuePerAttribute' Objekte
		for(int i = 0; i<ownValues.length; i++){
			if(returnValue<ownValues[i].size()){
				returnValue=ownValues[i].size();
			}
		}
		return returnValue;
	}
	
	
	


}
