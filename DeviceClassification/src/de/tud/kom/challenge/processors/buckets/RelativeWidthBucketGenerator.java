package de.tud.kom.challenge.processors.buckets;

import java.util.ArrayList;
import java.util.List;

import de.tud.kom.challenge.csvdatareader.DateTime;
import de.tud.kom.challenge.csvdatareader.LineData;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 */
public class RelativeWidthBucketGenerator implements IBucketGenerator {
	
	private final int numberOfBuckets;
	
	public RelativeWidthBucketGenerator(final int numberOfBuckets) {
		this.numberOfBuckets = numberOfBuckets;
	}
	
	private List<LineData> split(final List<LineData> datas, final int bucketLengthInSec) {
		final ArrayList<LineData> result = new ArrayList<LineData>();
		int currentLength = 0;
		for(LineData data : datas) {
			
			while(this.isSplitRequired(currentLength, data, bucketLengthInSec)) {
				final LineData first = data.createSubLineData(0, bucketLengthInSec - currentLength);
				result.add(first);
				
				final LineData rest = data.createSubLineData(bucketLengthInSec - currentLength);
				currentLength = 0;
				
				data = rest;
			}
			result.add(data);
			currentLength += data.getDuration();
			
			if(currentLength == bucketLengthInSec) {
				currentLength = 0;
			}
		}
		
		return result;
	}
	
	private boolean isSplitRequired(final int currentLength, final LineData data, final int bucketLengthInSec) {
		return ((currentLength + data.getDuration()) > bucketLengthInSec) || (currentLength == bucketLengthInSec);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tud.kom.challenge.processors.buckets.IBucketGenerator#getBuckets(java.util.List)
	 */
	public List<LineDataBucket> getBuckets(final List<LineData> lineData) {
		final List<LineDataBucket> buckets = new ArrayList<LineDataBucket>(this.numberOfBuckets);
		
		final int bucketLengthInSec = this.calculateBucketLength(lineData);
		
		if(bucketLengthInSec == 0) {
			return buckets;
		}
		
		final List<LineData> lst = this.split(lineData, bucketLengthInSec);
		
		if(this.calculateBucketLength(lst) != this.calculateBucketLength(lineData)) {
			throw new IllegalArgumentException("BucketLengthInSec was " + this.calculateBucketLength(lst) + " but the expected value should be " + bucketLengthInSec);
		}
		
		DateTime begin = lineData.get(0).getDateTime().getDate();
		DateTime end = begin.addSeconds(bucketLengthInSec);
		
		LineDataBucket bucket = this.createNewBucket(new ArrayList<LineDataBucket>(this.numberOfBuckets), begin, end);
		
		int currentDuration = 0;
		
		for(final LineData data : lst) {
			if((data.getDuration() + currentDuration) > bucketLengthInSec) {
				currentDuration = 0;
				begin = end;
				end = end.addSeconds(bucketLengthInSec);
				buckets.add(bucket);
				bucket = this.createNewBucket(new ArrayList<LineDataBucket>(), begin, end);
			}
			bucket.addData(data);
			currentDuration += data.getDuration();
		}
		buckets.add(bucket);
		
		return buckets;
	}
	
	private int calculateBucketLength(final List<LineData> lineData) {
		int dataLengthInSec = 0;
		
		for(final LineData ld : lineData) {
			dataLengthInSec += ld.getDuration();
		}
		return dataLengthInSec / this.numberOfBuckets;
	}
	
	private LineDataBucket createNewBucket(final ArrayList<LineDataBucket> buckets, final DateTime begin, final DateTime end) {
		LineDataBucket bucket;
		bucket = new LineDataBucket(begin, end, new ArrayList<LineData>());
		buckets.add(bucket);
		return bucket;
	}
	
}
