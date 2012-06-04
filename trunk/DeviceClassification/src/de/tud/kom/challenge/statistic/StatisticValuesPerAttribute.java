package de.tud.kom.challenge.statistic;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Contains for every Attribute(Feature) a Mapping of the name to a statical data and the related Value
 * On generation of the Object the Values are calculated
 * 
 * @author Kristopher
 *
 */

//TODO - Methode zum hinzufügen weiterer Daten zu einem bereits existierenden Objekt fehlt.

public class StatisticValuesPerAttribute extends HashMap<String, Double> {
	
	private String attributeName;
	private String dataType;
	private int numberOfDatasets;
	
	
	  
	
	public StatisticValuesPerAttribute(String attributeName, Vector<String> currentAttributeData, String dataType){
		this.attributeName = attributeName;
		this.dataType = dataType;
		this.numberOfDatasets = currentAttributeData.size();
		
		
		//TODO - auslagern der Berechnungen in eine eigene Klasse oder zumindest Methode?
		if(dataType.equals("numeric")){
			//TODO  - zu überarbeiten!!! unsauber und möglicherweise fehleranfällig
			//transformation of data-Vector to data-array
			String temporary[] = new String[currentAttributeData.size()];
			currentAttributeData.toArray(temporary);


//			System.out.println("Array: "+Arrays.toString(temporary));
//			Integer[] dataArray = Arrays.copyOf(temporary, temporary.length, Integer[].class);
			
			//ALTERNATIVE: manueller Ansatz
			Double[] dataArray = new Double[temporary.length];
			for(int i = 0; i < temporary.length; i++)
			{
				//TODO - improve solution for '?' values
				if(temporary[i].contains("?")){
					dataArray[i] = 0.0;
				}else{
				dataArray[i] = Double.valueOf(temporary[i]);
				}
			}

			//TODO - schlechter Bugfix, Ursache finden & beheben
			/*URSACHE: es wird für jedes Gerät statistische Daten erzeugt, auch wenn zu diesem gar keien testing Daten verfügbar sind
			 * => dringend zu beheben.			 * 
			 */
			System.out.println("dataArray.length = "+dataArray.length);
			if(dataArray.length == 0){
				dataArray = new Double[1];
				dataArray[0] = 0.0;
			}
				
				
			//min
			Arrays.sort(dataArray);  
			this.put("min", dataArray[0]);

			//max
			Arrays.sort(dataArray);  
			this.put("max", dataArray[dataArray.length-1]);

			
			//arithmetischer Mittelwert
				//Summe der Einzelwerte geteilt durch deren Anzahl
			double sum = 0.0;
			//for schleife
			for(int i = 0 ; i<dataArray.length; i++){
				sum = sum + dataArray[i];
			}
			this.put("arithmetic_mean", sum/dataArray.length);
		
			//geometrischer Mittelwert
			//n-te Wurzel aus dem Produkt der n Werte
			double geometricMean = geometricMean(dataArray);
			this.put("geometric_mean", geometricMean);
			
			
			//harmonischer Mittelwert
			double harmonicMean = harmonicMean(dataArray);
			this.put("harmonic_mean", harmonicMean);
			
			//quadratischer Mittelwert
			double rootMeanSquare = rootMeanSquare(dataArray);
			this.put("root_mean_square", rootMeanSquare);
			
			//gewichteter Mittelwert
			
			//median
				//sortierung der Messwerte nach ihrer Größe. Median = der WErt in der Mitte der Reihe. 
				//Bei ungerader Anzahl an Werten ist es ein kongretes Element. 
				//Bei gerader Anzahl an Werten wird der Mittelwert der beiden Werte in der Mitte gebildet.
			if(dataArray.length>0){
				//gerade Anzahl
				System.out.println("dataArray.length: "+dataArray.length);
				System.out.println("dataArray.length%2: "+dataArray.length%2);
				if(dataArray.length%2 == 0){
					//Bei gerader Anzahl an Werten wird der Mittelwert der beiden Werte in der Mitte gebildet.
					double sumOfMedianValues = dataArray[(dataArray.length/2)-1]+dataArray[(dataArray.length/2)];
					this.put("median", sumOfMedianValues/2);
				}
				//ungerade Anzahl
				else if(dataArray.length%2 == 1){
					//Bei ungerader Anzahl an Werten ist es ein kongretes Element. 
					int position = (dataArray.length-1)/2;
					this.put("median", dataArray[position]);
				}
			}
			
			/*
			 * MODUS
			 */
				//am häufigsten auftretender Wert im Datensatz aller Messwerte
				//ACHTUNG: neben unimodalen Verteilungen (ein Modalwert) sind auch bimodale (zwei Modalwerte) und multimodale Verteilungen (mehr als zwei Modalwerte) möglich
			//implementierung: Map mit Key=double-Wert, value=Anzahl des vorkommens
				//anschließend sortieren nach value
			//da die Klasse 'Double' das Interface 'Comparable' implementiert ist auch eine anspruchsvollere Lösung als die gewählte denkbar
			
			//alternativer Ansatz über sortiertes Array mit abzählen
			Arrays.sort(dataArray);  
			int counter = 0;
			int maxCounter = 0;
			double currentElement;
			double lastElement = dataArray[dataArray.length-1]; 
			Vector<Double> resultVector = new Vector<Double>();
			for( int i = 0; i< dataArray.length; i++){
				currentElement = dataArray[i];
				if(currentElement != lastElement){
					//überprüfen, ob der lastElement double Wert genauso häufig vorkommt wie alle vorherigen
					if(counter == maxCounter){
						resultVector.add(lastElement);
					}
					//überprüfen, ob der lastElement double Wert häufiger vorkommt als alle vorherigen
					else if(counter > maxCounter){
						//resultVector mit aktuellem Wert neu initialisieren
						resultVector.clear();
						resultVector.add(lastElement);
					}
					
					//zurücksetzen des counter auf 1 (für neuen double Wert)
					counter = 1;
					lastElement = currentElement;
				}
				else if(currentElement == lastElement){
					counter++;
				}
				else{
					System.out.println("error in calculation of 'Modus'");
				}
			}
			//abspeichern des Ergebnisses
			//unimodale Verteilung	
			if(resultVector.size() == 1){
				this.put("modus", resultVector.get(0));
			}
			//bimodale Verteilung
			else if(resultVector.size() == 2){
				this.put("bimodal 0", resultVector.get(0));
				this.put("bimodal 1", resultVector.get(1));
			}
			//multimodale Verteilung
			else if(resultVector.size() > 2){
				for(int i = 0; i<resultVector.size(); i++){
					String key = "multimodal ".concat(Integer.toString(i));
					this.put(key, resultVector.get(i));
				}
			}
			
			
			//quantile
				//da zu viele Möglichkeiten muss erst erörtert werden welche quantile Sinn macht.
			
			
		}
		else if(dataType.equals("{true,false}")){
			//this.put("{true,false}", 0);
			this.put("middle", 1.0);

			//WARNING: "min" and "max" values must be set
			
			//represents the number of 'false'
			this.put("min", numberOf(currentAttributeData, "false"));
			
			//represents the number of 'true'
			this.put("max", numberOf(currentAttributeData, "true"));
			
		}
		else{
			System.out.println("Der übergeben Datentyp ist: '"+dataType+"', daher ist keine Berechnung statistischer Werte möglich.");
//			this.put("+dataType+", 0);
			this.put("max", 2.0);
		}
		
		//TODO - statistisceh Werte erzeugen und ablegen
	}


	private double rootMeanSquare(Double[] dataArray) {
		double sum = 0;
		double validNumbers = 0;
		for(int i = 0; i<dataArray.length; i++){
			//protection against illegal arguments
			if(dataArray[i].isInfinite() || dataArray[i].isNaN()){
				
			}
			else{
				sum = sum + dataArray[i]*dataArray[i];
				validNumbers++;
			}
		}
		double ruturnValue = Double.NaN;
		if(validNumbers > 0 ){
			double interimResult = sum/validNumbers;
			ruturnValue = Math.pow(interimResult, 0.5);
		}
		return ruturnValue;
	}


	private double harmonicMean(Double[] dataArray) {
		double result = 0;
		double validNumbers = 0;
		for(int i = 0; i<dataArray.length; i++){
			//protection against illegal arguments
			//Warning, against the definition of the harmonicMean, this calculation does not lead to a 'limit of function' as soon as one input value is zero. 
			//this is necessery due to the 'readin-in' function, which in current development state generates a 0.0 value for original unknow datas ('?')
			if(dataArray[i].isInfinite() || dataArray[i].isNaN() || (dataArray[i] == 0.0)){
				
			}
			else{
				result = result + 1/dataArray[i];
				validNumbers++;
			}
		}
		double ruturnValue = Double.NaN;
		if(validNumbers > 0 )
			ruturnValue = 1/validNumbers;
		
		return ruturnValue;
	}


	//calculates the geometric Mean for the values of the given Double Array
	private double geometricMean(Double[] dataArray) {
		double result = 1;
		double validNumbers = 0;
		for(int i = 0; i<dataArray.length; i++){
			//protection against illegal arguments
			if(dataArray[i].isInfinite() || dataArray[i].isNaN() || (dataArray[i] <= 0.0)){
				
			}
			else{
				result = result * dataArray[i];
				validNumbers++;
			}
		}
		double ruturnValue = Double.NaN;
		if(validNumbers > 0 )
			ruturnValue = Math.pow(result, 1/validNumbers);
		
		return ruturnValue;
	}


	//counts the number of the given String in the vector
	private Double numberOf(Vector<String> inputVector, String string) {
		int amount = 0;
		for(int i = 0; i<inputVector.size(); i++){
			if(inputVector.get(i).contains(string))
				amount++;
		}
		return Double.valueOf(amount);
	}



	//TODO - Methode die ein Set mit den Namen der statistischen Werte zurückgibt.
	public Collection<String> statisticValueNames() {
		return this.keySet();
	}
	
	
	public String getAttributeName(){
		return attributeName;
	}
	
	public String getDataTypeName(){
		return dataType;
	}
	
	public int getNumberOfDatasets(){
		return numberOfDatasets;
	}
}
