package de.tud.kom.challenge.processors;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.audio.analysis.DFT;

import de.tud.kom.challenge.arff.featuredescription.FeatureDescription;
import de.tud.kom.challenge.arff.featuredescription.IntFeatureDescription;
import de.tud.kom.challenge.csvdatareader.CsvContainer;
import de.tud.kom.challenge.csvdatareader.LineData;
import de.tud.kom.challenge.processors.buckets.LineDataBucket;
import de.tud.kom.challenge.processors.buckets.OnOffBucketGenerator;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 */
public class FrequencyProcessor implements IFeaturePreProcessor {
	
	/**
	 * generated
	 */
	private static final long serialVersionUID = -7970809524636464331L;
	
	public Iterable<FeatureDescription> getFeatureDescription() {
		final ArrayList<FeatureDescription> features = new ArrayList<FeatureDescription>();
		
		features.add(new IntFeatureDescription("fft0"));
		features.add(new IntFeatureDescription("fft1"));
		features.add(new IntFeatureDescription("fft2"));
		features.add(new IntFeatureDescription("fft3"));
		features.add(new IntFeatureDescription("fft4"));
		features.add(new IntFeatureDescription("fft5"));
		features.add(new IntFeatureDescription("fft6"));
		features.add(new IntFeatureDescription("fft7"));
		features.add(new IntFeatureDescription("fft8"));
		features.add(new IntFeatureDescription("fft9"));
		
		return features;
	}
	

	public String[] processInput(final CsvContainer csv) {
		if(csv.getCompressedEntries().size() <= 2) {
			return null;
		}
		
		final ArrayList<LineData> lst = new ArrayList<LineData>(csv.getCompressedEntries());
		
		final OnOffBucketGenerator generator = new OnOffBucketGenerator();
		final List<LineDataBucket> buckets = generator.getBuckets(lst);
		
		final double[] accumulated = new double[10];
		int bucketCount = 0;
		
		for(final LineDataBucket lineDataBucket : buckets) {
			if(lineDataBucket.hasEnergyConsumption()) {
				int size = lineDataBucket.getData().size();
				
				if(size < 10) {
					continue;
				}
				
				bucketCount++;
				
				if((size % 2) != 0) {
					lineDataBucket.getData().remove(--size);
				}
				final float[] data = this.downsample(this.getDoubleArray(lineDataBucket.getData()));
				
				final DFT dft = new DFT(data.length, 10);
				
				dft.forward(data);
				
				for(int i = 0; i < 10; i++) {
					accumulated[i] += dft.getBand(i);
				}
			}
			
		}
		
		final String[] result = new String[10];
		
		for(int i = 0; i < accumulated.length; i++) {
			result[i] = "" + (accumulated[i] / bucketCount);
		}
		return result;
	}
	
	private float[] downsample(final float[] floatArray) {
		
		if(floatArray.length > 100) {
			final float[] result = new float[100];
			
			final double factor = floatArray.length / result.length;
			
			for(int i = 0; i < result.length; i++) {
				result[i] = floatArray[(int) Math.round(i * factor)];
			}
			return result;
		}
		
		return floatArray;
	}
	
	private float[] getDoubleArray(final List<LineData> data) {
		
		final float[] result = new float[data.size()];
		
		for(int i = 0; i < data.size(); i++) {
			result[i] = data.get(i).getConsumptionInWatt();
		}
		
		return result;
	}
	
}
