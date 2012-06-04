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
public class FixedWidthBucketGenerator implements IBucketGenerator {
	
	private final int numberOfBuckets;
	private final int bucketLengthInSec;
	
	public FixedWidthBucketGenerator(final int bucketLengthInSeconds) {
		this.bucketLengthInSec = bucketLengthInSeconds;
		this.numberOfBuckets = ((24 * 60 * 60) / this.bucketLengthInSec);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tud.kom.challenge.processors.buckets.IBucketGenerator#getBuckets(java.util.List)
	 */
	public List<LineDataBucket> getBuckets(final List<LineData> lineData) {
		final ArrayList<LineDataBucket> buckets = new ArrayList<LineDataBucket>(this.numberOfBuckets);
		
		DateTime begin = lineData.get(0).getDateTime().getDate();
		DateTime end = begin.addSeconds(this.bucketLengthInSec);
		
		LineDataBucket bucket = null;
		LineData lastLineData = new LineData(begin, 0, 0);
		for(final LineData data : lineData) {
			while((bucket == null) || data.getDateTime().isAfter(bucket.getTo())) {
				this.addBucketEndValue(bucket, lastLineData, begin);
				
				bucket = this.createNewBucket(buckets, begin, end);
				
				begin = end;
				end = begin.addSeconds(this.bucketLengthInSec);
				
				this.addBucketFirstValue(bucket, lastLineData);
			}
			bucket.addData(data);
			lastLineData = data;
		}
		
		this.expandBucketsToEndOfDay(buckets);
		
		return buckets;
	}
	
	private void expandBucketsToEndOfDay(final ArrayList<LineDataBucket> buckets) {
		LineDataBucket lastBucket = buckets.get(buckets.size() - 1);
		DateTime begin = lastBucket.getTo();
		DateTime end = begin.addSeconds(this.bucketLengthInSec);
		
		while(buckets.size() < this.numberOfBuckets) {
			final LineDataBucket b = this.createNewBucket(buckets, begin, end);
			this.addBucketFirstValue(b, lastBucket.getData().get(lastBucket.getData().size() - 1));
			this.addBucketEndValue(b, lastBucket.getData().get(lastBucket.getData().size() - 1), end);
			
			begin = end;
			end = begin.addSeconds(this.bucketLengthInSec);
			lastBucket = b;
		}
	}
	
	private LineDataBucket createNewBucket(final ArrayList<LineDataBucket> buckets, final DateTime begin, final DateTime end) {
		LineDataBucket bucket;
		bucket = new LineDataBucket(begin, end, new ArrayList<LineData>());
		buckets.add(bucket);
		return bucket;
	}
	
	private void addBucketFirstValue(final LineDataBucket bucket, final LineData lastLineData) {
		if(lastLineData != null) {
			bucket.addData(new LineData(bucket.getFrom(), lastLineData.getConsumptionInWatt(), lastLineData.getSmoothedConsumptionInWatt()));
		}
	}
	
	private void addBucketEndValue(final LineDataBucket bucket, final LineData lastLineData, final DateTime begin) {
		if(bucket != null) {
			bucket.addData(new LineData(begin.addSeconds(-1), lastLineData.getConsumptionInWatt(), lastLineData.getSmoothedConsumptionInWatt()));
		}
	}
	
}
