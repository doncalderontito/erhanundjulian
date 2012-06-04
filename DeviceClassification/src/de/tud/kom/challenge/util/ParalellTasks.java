package de.tud.kom.challenge.util;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 */
public class ParalellTasks {
	
	final static int numberOfThreads = 5;
	static int opens = 0;
	
	public static <P, Q> Collection<Q> map(final Collection<P> data, final Task<P, Q> transformation, Progress progressNotificator) 
	{
		
		final ArrayBlockingQueue<P> itemsToProcess = new ArrayBlockingQueue<P>(data.size());
		itemsToProcess.addAll(data);
		
		final ArrayBlockingQueue<Integer> progressLog = new ArrayBlockingQueue<Integer>(data.size());
		final ArrayBlockingQueue<Q> resultData = new ArrayBlockingQueue<Q>(data.size());
		final ArrayBlockingQueue<String> notCompletedItems = new ArrayBlockingQueue<String>(data.size());
		final CountDownLatch barrier = new CountDownLatch(numberOfThreads);
		ReportRunnable progressRun = null;
		
		final Runnable task = new Runnable() {
			public void run() {
				P item = null;
				while((item = itemsToProcess.poll()) != null) {
					String itemS = FileUtil.getShortPath(item.toString());
					notCompletedItems.add(itemS);
					final Q result = transformation.calculate(item);
					notCompletedItems.remove(itemS);
					if(result != null) {
						resultData.add(result);
					}
					progressLog.add(1);
				}
				barrier.countDown();
			}
		};
		
		final ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
		for(int i = 0; i < numberOfThreads; i++) {
			newCachedThreadPool.execute(task);
		}
		
		if (progressNotificator != null) {
			progressRun = new ReportRunnable(progressLog, progressNotificator, data);
			newCachedThreadPool.execute(progressRun);
		}
		
		try {
			barrier.await();
			if (progressRun != null) progressRun.halt();
			newCachedThreadPool.shutdown();
		} catch(final InterruptedException e1) {
			e1.printStackTrace();
		}
		return resultData;
	}
}
