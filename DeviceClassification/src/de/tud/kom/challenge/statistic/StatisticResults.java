package de.tud.kom.challenge.statistic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Contains the complete statistical data in a HashMap including the devices with their attributes('StatisticalValuesPerDevice') and the related statistical values('StatisticalValuePerAttribute')
 * 
 * @author Kristopher
 *
 */

public class StatisticResults extends HashMap<String, StatisticValuesPerDevice> {
	

private CompleteDataset completeDataset;
	
	
	//Konstruktor
	public StatisticResults(CompleteDataset completeDataset){
		this.completeDataset = completeDataset;
		//TODO -  der Zugriff auf die Ger�te muss m�glicherweise f�r die 'testing' Daten �berarbeitet werden
		// => eventuell �berarbeitung von "CompleteDataset"
		Set<String> devices = completeDataset.keySet();
		
		//Typecast to String Array
		String[] devicesArray = Arrays.copyOf(devices.toArray(), devices.toArray().length,  String[].class);
		
		//TODO - bisher wird f�r alle bekannten Ger�te ein 'StatisticValuesPerDevice' angelegt.
		// Das ist falsch/unzureichend, wenn f�r das Ger�t �berhaupt keine Daten vorliegen!
		// Dies ist insbesondere der Fall bei 'testing' Daten, wo oft nur von einem oder wenigen Ger�ten die Daten vorliegen
		// bzw. gerade die 'testing' Daten sind in der Regel nur ein Datensatz eines Ger�tes.
		for(int i = 0; i<devicesArray.length; i++){
			//laden der Ger�te
			String currentDevice = devicesArray[i];
			//zu jedem Ger�t ein neues 'StatisticValuePerDevice' Objekt erzeugen, das die Attribute �bergeben bekommt um sie als 'Keys' zu verwenden
			this.put(currentDevice, new StatisticValuesPerDevice(currentDevice, completeDataset));
		}
	}
	
	
	/**
	 * @param device
	 * @param attribute
	 * @return 
	 */
	public StatisticValuesPerAttribute getStatisticValue(String device, String attribute){
		return this.get(device).getStatisticValue(attribute);
	}
	
	public CompleteDataset getCompleteDataset() {
		return completeDataset;
	}

	public void removeAllDevicesExcept(String deviceName) {
		String deviceArray[] = this.completeDataset.getDeviceNames();
		for(int i = 0; i<deviceArray.length; i++){
			System.out.println("compare: "+deviceArray[i]+" to "+deviceName);
			if(!deviceArray[i].equalsIgnoreCase(deviceName)){
				System.out.println("entfernen von : "+deviceArray[i]);
				//entfernen der Ger�te aus StatisticResults
				this.remove(deviceArray[i]);
				//entfernen der Ger�te aus dem zugeordneten CompleteDataset	
				this.completeDataset.remove(deviceArray[i]);
			}
		}
	}


	public String[] getDeviceNames() {
		return this.completeDataset.getDeviceNames();
	}

}
