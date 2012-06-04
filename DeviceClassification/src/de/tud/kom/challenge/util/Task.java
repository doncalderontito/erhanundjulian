package de.tud.kom.challenge.util;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 */
public interface Task<P, Q> {
	public Q calculate(P input);
}
