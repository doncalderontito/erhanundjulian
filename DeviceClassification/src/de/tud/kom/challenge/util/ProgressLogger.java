package de.tud.kom.challenge.util;

import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;

public class ProgressLogger implements Progress {
	
	private final static Logger log = Logger.getLogger(ProgressLogger.class.getSimpleName());
	private String type;
	
	public ProgressLogger(String type) {
		this.type = type;
	}
	
	public void notifyProgress(final float absoluteProgress) {
		log.info(type+": "+ (absoluteProgress) + "% done");
	}
	
	public void notifyBoth(final float absoluteProgress,
			ArrayBlockingQueue<String> notCompletedItems) {
		log.info(type+": "+ (absoluteProgress) + "% done");
		log.info(type+": Not completed items: " + notCompletedItems);
	}

	public void notifyIncomplete(ArrayBlockingQueue<String> notCompletedItems) {
		log.info(type+": Not completed items: " + notCompletedItems);
	}
}
