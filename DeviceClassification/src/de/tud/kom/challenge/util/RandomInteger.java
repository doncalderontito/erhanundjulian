package de.tud.kom.challenge.util;


import java.util.Random;

/**
 * Diese Klasse generiert ein eine Integerzufallszahl
 * aus einer gegebenen Interval [a,b] (a und b sind inclusive) 
 * 
 * @author Leo Fuhr
 *
 */
public final class RandomInteger {

	/**
	 * 
	 */
	private Random random = null;
	
	
	/**
	 * 
	 */
	private int rangeStart;
	
	/**
	 * 
	 */
	private int rangeEnd;
	
	/**
	 * Beim Aufruf diesen Konstruktor muss die setRange-Methode aufgerufen,
	 * um den den Interval zu setzen.
	 */
	public RandomInteger(){
		this.random = new Random();
	}
	
	
	/**
	 * 
	 * @param start die kleinste Zahl des Intervalls
	 * @param end die groesste Zahl des Intervalls
	 */
	public RandomInteger(int start, int end){
		this.random = new Random();
		setRange(start, end);
	}
	
	
	/**
	 * setzt einen neuen Intervall ein
	 * 
	 * @param start die kleinste Zahl des Intervalls
	 * @param end die grš§te Zahl des Intervalls
	 */
	public void setRange(int start, int end){
		if ( start >= end ) {
		      throw new IllegalArgumentException("Start cannot exceed End.");
		}
		this.rangeStart = start;
		this.rangeEnd   = end;
	}
	
	/**
	 * 
	 * @return eine Integerzufallszahl aus dem Intervall [sRange,eRange]
	 */
	
	public int getRandomInteger(){
		
		//get the range, casting to long to avoid overflow problems
	    long range = (long)rangeEnd - (long)rangeStart + 1;
	    
	    // compute a fraction of the range, 0 <= frac < range
	    long fraction = (long)(range * random.nextDouble());
	    
	    return (int)(fraction + rangeStart); 
	}
	
	

	public int getRangeStart() {
		return this.rangeStart;
	}


	public int getRangeEnd() {
		return this.rangeEnd;
	}
}
