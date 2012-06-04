package de.tud.kom.challenge.prediction.evaluator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
				if (feature.getResult().equals(Boolean.TRUE))
					instance.setValue(pos, "true");
				else
					instance.setValue(pos, "false");
			}

				
			pos++;
		}
		//instance.setClassValue(0.0);
		
		ArrayList<DataPoint> pointBuffer0 = new ArrayList<DataPoint>();
		DataPoint point0 = new DataPoint(instance,timestamp);
		pointBuffer0.add(point0);

		return evaluate(instance);
	}




	private boolean evaluate(Instance instance) {
		clusterer.trainOnInstanceImpl(instance);
		boolean event=false;
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
	
}
