package de.tud.kom.challenge.statistic;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

//import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Saves the complete data extracted from an ARFF-file in a HashMap including the devices with their data and attributes('attributes') with their type('dataType')
 * 
 * @author Kristopher
 *
 */

public class CompleteDataset extends HashMap<String, DataPerDevice> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Vector<String> attributes;
	private Vector<String> dataTypes;
	
	
	//simple constructor
	public CompleteDataset(Vector attributes, Vector datatype){
		this.setAttributesAndDataTypes(attributes, datatype);
	}
	
	
	//complete Constructor
	public CompleteDataset(/*Object[] valuesOfFeatures*/Instances instances, Vector deviceVector, String[] attributes, String[] datatype){
		
		//	I.		speichern der Attribute und Datentypen
			//anpassen von Stringarray zu Vector
			Vector attributesVector = new Vector(Arrays.asList(attributes));
			Vector dataTypeVector = new Vector(Arrays.asList(datatype));
			this.setAttributesAndDataTypes(attributesVector, dataTypeVector);
		
		//	II.		anlegen der Gerätevektoren
		for(int currentDevice = 0; currentDevice<deviceVector.capacity(); currentDevice++){
			//parametrisierung sieht zu jedem Gerät einen Vektor vor, der Vektoren enthält
			//Diese Vektoren enthalten die Features zu einem Tag, die wiederum Strings enthalten
			this.put(deviceVector.get(currentDevice).toString(), new DataPerDevice());
		}
		
		
		//	III.	hinzufügen der eingelesenen Daten von jedem Tag zum entsprechenden Gerät
		Enumeration instancesEnumeration = instances.enumerateInstances();
		while(instancesEnumeration.hasMoreElements()){
			Object currentInstance = instancesEnumeration.nextElement();
			//speichern als String zum zerteilen
			String tagesdaten = currentInstance.toString();
			//Aufteilen in die einzelnen Daten
			String[] splittet = tagesdaten.split(",");
		
			//Ausgabe der gesplitteten Daten
			System.out.println("Anzahl der Einzeldaten: "+splittet.length +"    Daten: "+Arrays.toString(splittet));
			
//			System.out.println("Instance eingelesen mit: "+Arrays.toString(v.toArray()));
			//laden eines neuen Tagesdatensatzes
			DataPerDay datasetOfADay = new DataPerDay(new Vector(Arrays.asList(splittet)));
			//Speichern des Datensatzes eines Tages zum entsprechenden Gerät
			DataPerDevice targetDeviceDataset = this.get(datasetOfADay.getDeviceType());
			System.out.println("Gerätetype: "+datasetOfADay.getDeviceType());
			System.out.println("datasetOfADay.size()="+datasetOfADay.size());
			targetDeviceDataset.add(datasetOfADay);
		}
		
		
		
		
//		for(int datasetNumber = 0; datasetNumber < valuesOfFeatures.length; datasetNumber++){
//			//laden eines Tagesdatensatzes
//			DataPerDay datasetOfADay = new DataPerDay(Arrays.asList(valuesOfFeatures[datasetNumber].toString().split(",")));
//			//Speichern des Datensatzes eines Tages zum entsprechenden Gerät
//			DataPerDevice targetDeviceDataset = this.get(datasetOfADay.getDeviceType());
//			targetDeviceDataset.add(datasetOfADay);
//		}
		
	}
	
	
	/**
	 * method to add a 'DataPerDay' object to a 'DataPerDevice' object
	 * 
	 * @param String deviceName to identify the target DataPerDevice object
	 * @param objectToSave DataPerDay to be appended to the DataPerDevice object
     * @return Returns true if this collection changed as a result of the call.
	 */
	public boolean addDatasetOfADay(String deviceName, DataPerDay objectToSave){
		return this.get(deviceName).add(objectToSave);
	}
	
	
	
	
	/**
	 * TODO - add information
	 * TODO - write test for the method
	 */
	public void setAttributesAndDataTypes (Vector attributesVector, Vector dataTypeVector){
		//check the vectors for equal number of attributes an data types
		if(attributesVector.size() == dataTypeVector.size()){
			//wegspeichern der attribute und der zugehörigen datatypes 
			attributes = attributesVector;
			dataTypes = dataTypeVector;
		}
		else{
		//ersetzen durch eine Exception
		System.out.println("Failure in method 'setAttributesAndDataTypes' executed on a 'CompleteDataset': number of Attributes does not match with the number of Datatypes");
		}
	}
	
	
	
	/**
	 * returns the 'attribute' vector
	 */	
	public Vector<String> getAttributes(){
		return attributes;
	}
	
	/**
	 * returns the 'datatypes' vector
	 */	
	public Vector<String> getDataTypes(){
		return dataTypes;
	}
	
	
	/**
	 * returns all positions of the corresponding datatype
	 */
	private Vector<Integer> findPositionsOfType(String datatype){
		int lastFoundPosition = -1;
		int newFoundPosition = 0;
		
		Vector<Integer> positions = new Vector<Integer>();
		
		if(attributes != null && dataTypes!= null){
			newFoundPosition = dataTypes.indexOf(datatype, lastFoundPosition+1);
						
				while((newFoundPosition != lastFoundPosition)){
					positions.add(newFoundPosition);						
					System.out.println("wegspeichern von: " +newFoundPosition);
					lastFoundPosition = newFoundPosition;
					newFoundPosition = dataTypes.indexOf("numeric", lastFoundPosition+1);
					if(newFoundPosition == -1){
						System.out.println("BREAK");
						break;
					}
				}
		}
		else{
			System.out.println("error in method:'findPositionsOfType' in class:'CompleteDataset'");
			System.out.println("Attributes or Datatypes are not set jet, at least one of them is still 'null'");
		}
		positions.trimToSize();
		//Ausgabe des Lösungsvektors
		return positions;
	}
	
	
	/**
	 * returns all attributes of the corresponding datatype
	 */
	public Vector getAttributesOfType(String datatype){
		Vector attributesOfType = new Vector();
		
		Vector<Integer> positionsOfType = findPositionsOfType(datatype);
		
		for(int currentPosition = 0; currentPosition<positionsOfType.size(); currentPosition++){
			attributesOfType.add(attributes.get(positionsOfType.get(currentPosition)));
		}
		
		return attributesOfType;
	}
	
	
	/**
	 * returns the Data of a given 'device' and 'attribute'
	 */
	public Vector getDataOf(String devicename, String attribute){
		int attributeIndex = attributes.indexOf(attribute);
		return this.get(devicename).getDataOfAttribute(attributeIndex);	
	}


	//returns the number of datasets to corresponding device 
	public int numberOfDatasets(String device) {
		if(this.get(device) == null)
		return 0;
		else
		return this.get(device).size();
	}
	
	
	//returns an Array containing the Devicenames
	public String[] getDeviceNames(){
		Set<String> deviceSet = this.keySet();
		String deviceArray[] = (String[])this.keySet().toArray(new String[deviceSet.size()]);
		return deviceArray;
	}
}
