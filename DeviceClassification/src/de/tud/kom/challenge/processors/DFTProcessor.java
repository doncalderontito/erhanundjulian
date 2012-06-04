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
import de.tud.kom.challenge.processors.buckets.RelativeWidthBucketGenerator;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 * The DFTProcessor transforms each on bucket into the discrete N-point Fourier space.
 * To limit the computation costs all on buckets are divided into 10*N sample points.
 * A empirically determined value for N is 10. The result of the DFT is directly used as feature value.
 * Some devices have a nearly constant power consumption whereas other devices have always the same variance in their power consumption.
 * Despite of other influence factors like load each device kind has its unique power characteristics.
 * The DFT could nearly express this characteristics as a spectrum of N different values.
 * 
 */
public class DFTProcessor implements IFeaturePreProcessor {
	

	
	
	// 20 -> 94.1374
	// 15 -> 93.1323
	// 10 -> 94.3049
	// 5 -> 93.6348
	private int DftPoints = 10;
	
	private int SampleRate = 10 * this.DftPoints;
	
	public DFTProcessor() {
		// default constructor
	}
	
	public DFTProcessor(final int samples, final int points) {
		this.DftPoints = points;
		this.SampleRate = samples;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public Iterable<FeatureDescription> getFeatureDescription() {
		final ArrayList<FeatureDescription> features = new ArrayList<FeatureDescription>();
		
		for(int i = 0; i < this.DftPoints; i++) {
			features.add(new IntFeatureDescription("dft" + i));
		}
		
		return features;
	}
	

	public String[] processInput(final CsvContainer csv) {
		
		final ArrayList<LineData> lst = new ArrayList<LineData>(csv.getCompressedEntries());
		
		final OnOffBucketGenerator generator = new OnOffBucketGenerator();
		final List<LineDataBucket> onBuckets = generator.getBuckets(lst);
		
		final double[] accumulated = new double[this.DftPoints];
		
		int bucketCount = 0;
		
		for(final LineDataBucket onBucket : onBuckets) {
			
			if(!onBucket.hasEnergyConsumption()) {
				continue;
			}
			
			bucketCount++;
			
			final RelativeWidthBucketGenerator downsampler = new RelativeWidthBucketGenerator(this.SampleRate);
			
			final List<LineDataBucket> dftInputData = downsampler.getBuckets(onBucket.getData());
			
			if((dftInputData.size() % 2) != 0) {
				dftInputData.remove(dftInputData.size() - 1);
			}
			
			final DFT dft = new DFT(dftInputData.size(), this.DftPoints);
			
			dft.forward(this.lineDataBucketToArray(dftInputData));
			
			for(int i = 0; i < this.DftPoints; i++) {
				accumulated[i] += dft.getBand(i);
			}
		}
		
		final String[] result = new String[this.DftPoints];
		
		for(int i = 0; i < accumulated.length; i++) {
			result[i] = "" + (accumulated[i] / bucketCount);
		}
		return result;
	}
	
	private float[] lineDataBucketToArray(final List<LineDataBucket> data) {
		
		final float[] result = new float[data.size()];
		
		for(int i = 0; i < data.size(); i++) {
			result[i] = data.get(i).calculateAvgPowerConsumptionInWatt();
		}
		
		return result;
	}
	
}
