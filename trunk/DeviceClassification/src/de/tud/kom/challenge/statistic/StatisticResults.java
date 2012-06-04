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
		//TODO -  der Zugriff auf die Geräte muss möglicherweise für die 'testing' Daten überarbeitet werden
		// => eventuell überarbeitung von "CompleteDataset"
		Set<String> devices = completeDataset.keySet();
		
		//Typecast to String Array
		String[] devicesArray = Arrays.copyOf(devices.toArray(), devices.toArray().length,  String[].class);
		
		//TODO - bisher wird für alle bekannten Geräte ein 'StatisticValuesPerDevice' angelegt.
		// Das ist falsch/unzureichend, wenn für das Gerät überhaupt keine Daten vorliegen!
		// Dies ist insbesondere der Fall bei 'testing' Daten, wo oft nur von einem oder wenigen Geräten die Daten vorliegen
		// bzw. gerade die 'testing' Daten sind in der Regel nur ein Datensatz eines Gerätes.
		for(int i = 0; i<devicesArray.length; i++){
			//laden der Geräte
			String currentDevice = devicesArray[i];
			//zu jedem Gerät ein neues 'StatisticValuePerDevice' Objekt erzeugen, das die Attribute übergeben bekommt um sie als 'Keys' zu verwenden
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
				//entfernen der Geräte aus StatisticResults
				this.remove(deviceArray[i]);
				//entfernen der Geräte aus dem zugeordneten CompleteDataset	
				this.completeDataset.remove(deviceArray[i]);
			}
		}
	}


	public String[] getDeviceNames() {
		return this.completeDataset.getDeviceNames();
	}

}
