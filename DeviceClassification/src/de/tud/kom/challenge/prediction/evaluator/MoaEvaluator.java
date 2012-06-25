package de.tud.kom.challenge.prediction.evaluator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import moa.cluster.Clustering;
import moa.clusterers.AbstractClusterer;
import moa.clusterers.CobWeb;
import moa.gui.visualization.DataPoint;

import org.apache.log4j.Logger;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import de.tud.kom.challenge.prediction.PredictionFeature;

public class MoaEvaluator implements Evaluator  {

	int timestamp = 0;
	AbstractClusterer clusterer = new CobWeb();

	private final static Logger log = Logger.getLogger(MoaEvaluator.class.getSimpleName());
	Instances dataset=null;
	Clustering clustering = null;
	int oldNumberOfClusters=0;
	
	//filter
	private final int filterSize = 10;
	private Vector<Instance> instanceFilter = new Vector<Instance>();
	
	private HashMap<String, Double> levelsToMinDuration = new HashMap<String, Double>();
	private HashMap<String, Double> levelsToMaxDuration = new HashMap<String, Double>();
	
	@Override
	public boolean evaluate(Vector<PredictionFeature> results, boolean training) {
		
		if (dataset==null)
		{	
			log.error("dataset not initialized - trainFromArff should be called before");
			return false;
		}
		
		Instance instance = new DenseInstance(dataset.numAttributes());
		instance.setDataset(dataset);
		int pos=0;
		
		for (PredictionFeature feature:results)
		{
			
			if (feature.getResult() ==null)
			{
				pos++;
				continue;
			}
			
			if (dataset.attribute(pos).isNumeric())
			{
				instance.setValue(pos, (double) Double.valueOf(feature.getResult()));

			}
			else
			{
				instance.setValue(pos, feature.getResult());
			}

				
			pos++;
		}
		//instance.setClassValue(0.0);
		
		//ArrayList<DataPoint> pointBuffer0 = new ArrayList<DataPoint>();
		//DataPoint point0 = new DataPoint(instance,timestamp);
		//pointBuffer0.add(point0);

		return evaluate(instance);
	}




	private boolean evaluate(Instance instance) {
	
		boolean event=false;
		
		//manual checking
		String level = instance.stringValue(0);
		double min = instance.value(1);
		double max = instance.value(2);
		
		event = this.evaluateDuration(level, min, max);
		
		instance.setValue(1, -1);
		instance.setValue(2, -1);
		//end manual checking
		
		if(instanceFiltered(instance)){
			return event;
		}
		
		clusterer.trainOnInstanceImpl(instance);
		
		timestamp++;
		
		int numberOfClusters=((CobWeb)clusterer).numberOfClusters();
		if (oldNumberOfClusters < numberOfClusters)
		{
			String txt="event: new cluster created :"+numberOfClusters+" assigned to:";
			
			double[] result=clusterer.getVotesForInstance(instance);
			for(int i=0;i<result.length;i++)
				txt+=i+"="+result[i]+" | ";
		
			log.info("step:"+timestamp+" --> " +instance+ " --> "+txt);
		
			event=true;
		}
		
		oldNumberOfClusters=numberOfClusters;
		
		return event;
		
	}

	public String toString()
	{
		return clusterer.toString();
	}


	@Override
	public void trainFromArff(String path) {
		clusterer.prepareForUse();
		ArffLoader loader = new ArffLoader();
		try {
			loader.setFile(new File(path));
			
			dataset = loader.getDataSet();
			
			for (int i=0;i<dataset.numInstances();i++)
			{
				Instance instance=dataset.get(i);
				evaluate(instance);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private boolean instanceFiltered(Instance instance) {
		boolean found = false;
		for (Instance filterInstance : instanceFilter) {
			boolean equal = true;
			for (int i = 0; i < instance.numValues(); i++) {
				if (!("" + instance.value(i)).equals(""
						+ filterInstance.value(i))) {
					equal = false;
					break;
				}
			}
			if (equal) {
				found = true;
				break;
			}
		}

		if (found) {
			return true;
		} else {
			instanceFilter.add(instance);
			if (instanceFilter.size() > filterSize)
				instanceFilter.remove(0);
		}
		return false;
	}
	
	private boolean evaluateDuration(String level, double minDuration, double maxDuration){
		
		if(!levelsToMinDuration.containsKey(level)){
			levelsToMinDuration.put(level, Double.valueOf(minDuration));
			levelsToMaxDuration.put(level, Double.valueOf(maxDuration));
			return false;
			
		}
		
		boolean minEvent = false;
		boolean maxEvent = false;
		
		double oldMinDuration = levelsToMinDuration.get(level).doubleValue();
		if(oldMinDuration > minDuration){
			levelsToMinDuration.put(level, Double.valueOf(minDuration));
			minEvent = (minDuration < oldMinDuration * 0.85) && (Math.abs(minDuration - oldMinDuration) > 5);
		}
		
		double oldMaxDuration = levelsToMaxDuration.get(level).doubleValue();
		if(oldMaxDuration < maxDuration){
			levelsToMaxDuration.put(level, Double.valueOf(maxDuration));
			maxEvent = maxDuration > oldMaxDuration * 1.15 && (Math.abs(maxDuration - oldMaxDuration) > 5);
		}
		
		return (minEvent || maxEvent);
	}
	
}
