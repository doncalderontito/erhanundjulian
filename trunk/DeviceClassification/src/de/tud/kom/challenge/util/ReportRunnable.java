package de.tud.kom.challenge.util;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ReportRunnable implements Runnable {
	boolean active = true;
	ArrayBlockingQueue<Integer> progressLog;
	Progress progressNotificator;
	Collection<?> data;
	
	public ReportRunnable(ArrayBlockingQueue<Integer> progressLog,
			Progress progressNotificator, Collection<?> data) {
		this.progressLog = progressLog;
		this.progressNotificator = progressNotificator;
		this.data = data;
	}
	public void halt() {
		active = false;
	}
	public void run() {
		Integer item = null;
		int count = 0;
		float lastReportedProgress = 0;
		try {
			while(active && (item = progressLog.poll(5, TimeUnit.MINUTES)) != null) {
				count += item;
				final float currentProgress = (float) ((100 * (1.0 * count)) / data.size());
				
				if((currentProgress - lastReportedProgress) > 1) {
					lastReportedProgress = currentProgress;
					if(progressNotificator != null) {
						progressNotificator.notifyProgress(currentProgress);
					}
				}
			}
		} catch(final InterruptedException e) {
			System.err.println("Progress Reporter Interrupted");
		}
	}
};
