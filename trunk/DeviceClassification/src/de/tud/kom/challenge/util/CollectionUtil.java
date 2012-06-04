package de.tud.kom.challenge.util;

import java.util.List;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 */
public class CollectionUtil {
	
	public static <T> T last(final List<T> collectedData) {
		if(collectedData.size() == 0) {
			return null;
		}
		return collectedData.get(collectedData.size() - 1);
	}
	
	public static <T> T first(final List<T> collectedData) {
		return collectedData.get(0);
	}
	
}
