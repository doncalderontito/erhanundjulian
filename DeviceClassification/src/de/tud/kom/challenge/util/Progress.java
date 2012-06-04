package de.tud.kom.challenge.util;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Andreas Reinhardt
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 */
public interface Progress {
	public void notifyProgress(float absoluteProgress);
	public void notifyIncomplete(ArrayBlockingQueue<String> notCompletedItems);
	public void notifyBoth(float absoluteProgress, ArrayBlockingQueue<String> notCompletedItems);
}
