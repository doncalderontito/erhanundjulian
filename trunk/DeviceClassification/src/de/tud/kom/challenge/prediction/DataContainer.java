package de.tud.kom.challenge.prediction;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import de.tud.kom.challenge.csvdatareader.DateTime;

public class DataContainer {

	private HashMap<Long,Integer> data;
	private long firstTime, lastTime, counter = 0;
	private long latestAdd;
	private final static Logger log = Logger.getLogger(DataContainer.class.getSimpleName());
	private static DataContainer cachedContainer;
	
	public DataContainer() {
		data = new HashMap<Long,Integer>();
		firstTime = Long.MAX_VALUE;
		lastTime = Long.MIN_VALUE;
		latestAdd = Long.MIN_VALUE;
	}
	
	public void addEntry(DataEntry entry) {
		if (firstTime == Long.MAX_VALUE) firstTime = entry.getTime();
		if (lastTime == Long.MIN_VALUE) lastTime = entry.getTime();
		
		if (entry.getTime() < latestAdd) {
			log.warn("Cannot add data backwards! Possible reasons for this error are:");
			log.warn("1) You have device data from different appliancs in the input directory");
			log.warn("2) Filenames don't follow the naming convention (device-year-month-day)");
			return;
			
		} else if ((latestAdd == Long.MIN_VALUE) || (latestAdd == entry.getTime() - 1)) {
			add(entry.getTime(), entry.getValue());
			
		} else if (latestAdd == entry.getTime() && data.get(new Long(entry.getTime())) != null) {
			// Average over two values for same timestamp 
			// Let us assume that no more than two readings per second exist
			Integer i = data.get(new Long(entry.getTime()));
			add(entry.getTime(),((entry.getValue()+i.intValue())/2));
			
		} else {
			if ((entry.getTime()-latestAdd) > 160) {
				log.warn("Gap larger than 160 seconds on "+DateTime.fromLong(entry.getTime()).toString()+" - not interpolating");
				for (long k=latestAdd+1;k<=entry.getTime();k++) {
					add(k,-1);
				}
			} else {
				int lastValue = 0;
				Integer x = data.get(new Long(latestAdd));
				if (x!=null && x.intValue() > 0) {
					lastValue = x.intValue();
				}
				
				int gap = entry.getValue() - lastValue;
				long timediff = entry.getTime() - latestAdd;
						
				// Interpolate
				for (long k=latestAdd+1;k<entry.getTime();k++) {
					int incr = gap * (int) ((double) ((k-latestAdd)/timediff));
					add(k, lastValue + incr);
				}
			
				add(entry.getTime(), entry.getValue());
			}
		}
		latestAdd = entry.getTime();
		lastTime = entry.getTime();
	}
	
	/**
	 * Get only the data of the last 24 hours in the container
	 * @return a DataContainer that only has 24 hours of data in it
	 */
	public DataContainer getLastDay() {
		
		long tdiff = lastTime - firstTime;
		long delta = Math.min(86399,tdiff);
	
		if (cachedContainer==null || cachedContainer.getSize() < 1) // || cachedContainer.getLastTime() > lastTime || cachedContainer.getFirstTime() < lastTime-delta) 
		{
			cachedContainer= new DataContainer();
			for (long i=delta; i>=0; i--) 
			{
				long time = lastTime-i;
				Integer value = getEntry(time);
				if (value != null) {
					DataEntry entry = new DataEntry(time, value);
					cachedContainer.addEntry(entry);				
				} else {
					log.warn("The data set is incomplete in the last 24 hours");
				}

			}
		}
		else
		{
			while (cachedContainer.getLastTime() < lastTime)
			{
				
				long nextStep=cachedContainer.getLastTime()+1;
				cachedContainer.addEntry(new DataEntry(nextStep,getEntry(cachedContainer.getLastTime()+1)));
			}
			
			while (cachedContainer.getFirstTime() < lastTime-delta)
			{
				cachedContainer.removeFirstEntry();
			}
			//log.info("container start:"+cachedContainer.getFirstTime()+" start:"+(lastTime-delta)+"  end:"+lastTime+" cend:"+cachedContainer.getLastTime());
			
		}
		return cachedContainer;
	}
	
	public Iterator<Integer> getValueIterator() {
		return data.values().iterator();
	}
	
	private void add(long time, int value) {
		counter++;
		if (value >= 0) data.put(new Long(time), new Integer(value));
		else data.put(new Long(time), null);
	}

	public String getStats() {
		return counter+" entries from "+DateTime.fromLong(firstTime).toString()+" to "+DateTime.fromLong(lastTime).toString();
	}

	public Integer getEntry(long time) {
		return data.get(new Long(time));
	}

	public DataEntry removeFirstEntry() {
		if (firstTime > lastTime) return null; // running empty
		
		int temporal = data.get(new Long(firstTime));
		DataEntry d = new DataEntry(firstTime, temporal);
		data.remove(new Long(firstTime));
		firstTime++;
		
		return d;
	}

	public int getSize() {
		return data.size();
	}

	public long getFirstTime() {
		return firstTime;
	}

	public long getLastTime() {
		return lastTime;
	}
	
	public DataContainer getDeepClone() {
		DataContainer result = new DataContainer();
		result.firstTime = firstTime;
		result.lastTime = lastTime;
		result.latestAdd = latestAdd;
		Iterator<Long> it = data.keySet().iterator();
		while (it.hasNext()) {
			Long k = it.next();
			result.data.put(k, data.get(k));
		}
		return result;
	}
}
