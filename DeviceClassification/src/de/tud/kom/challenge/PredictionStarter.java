package de.tud.kom.challenge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import de.tud.kom.challenge.csvdatareader.DateTime;
import de.tud.kom.challenge.prediction.DataContainer;
import de.tud.kom.challenge.prediction.DataEntry;
import de.tud.kom.challenge.prediction.PredictionFeature;
import de.tud.kom.challenge.prediction.PredictionPath;
import de.tud.kom.challenge.prediction.PredictionReader;
import de.tud.kom.challenge.prediction.evaluator.Evaluator;
import de.tud.kom.challenge.prediction.evaluator.TimedEnergyLevelEvaluator;
import de.tud.kom.challenge.prediction.processors.PredictionProcessor;


public class PredictionStarter {

	private final static Logger log = Logger.getLogger(PredictionStarter.class.getSimpleName());
	private static Vector<PredictionProcessor> processors = PredictorList.getPredictors();
	private static DataContainer completeData = new DataContainer();
	private static Evaluator evaluator = null;
	private static PredictionReader pr;

	private static ExecutorService threadPool;
	
	private static int numberOfCPUs=4;
	
	public static void main(final String[] args) {
		PredictionStarter.initLogger();
		String commands = pollType();
		
		// TODO 1: Add your custom processors to the "processors" vector in PredictorList.java.
		// TODO 2: Add your own evaluation function here
		// evaluator = mySuperDuperEvaluationFunction();
//		evaluator = new WekaEvaluator();
		evaluator = new TimedEnergyLevelEvaluator();
//		evaluator = new SimpleEvaluator();	
		
		log.info("Starting device predition with "+processors.size()+" processor"+(processors.size()==1?"":"s")+"...");
		log.info("----------------------------------------------");

		// Check early termination conditions
		if (evaluator == null) {
			log.fatal("No evaluator defined, terminating!");
			System.exit(1);
		}
		
		if (processors.size() <= 0) {
			log.fatal("No processor has been instantiated, terminating!");
			System.exit(1);
		} else {
			for (PredictionProcessor processor:processors) {
				processor.setCompleteData(completeData);
			}
		}
		
		log.info("Initializing DataContainer from input directory "+PredictionPath.trainingPath);
		pr = new PredictionReader(PredictionPath.trainingPath);
		DataContainer d = pr.getCompleteData();
		if (d.getSize() == 0) {
			log.fatal("No training data, terminating!");
			System.exit(1);
		}
		log.info("----------------------------------------------");	
		
		
		// Evaluate input options ______________________ 1 _______________________
		if (commands.contains("1")) {
			appendToArff(getArffHeader() +"\n\n@DATA\n\n", true);
			
			int tp = d.getSize()/10, op = d.getSize()/100, st = 0;
			StringWriter line = new StringWriter();
			
			// Processing loop
			while (d.getSize() > 0) {
				DataEntry one = d.removeFirstEntry();   // Take reading from testing set
				Vector<PredictionFeature> results = new Vector<PredictionFeature>();
				
				threadPool = Executors.newFixedThreadPool(numberOfCPUs);
				Collection<ProcessorTask> tasks = new ArrayList<ProcessorTask>();
				for (PredictionProcessor processor:processors) {
					ProcessorTask task = new ProcessorTask(processor,one);
					tasks.add(task);
					threadPool.execute(task);
				}
				
				try {
					threadPool.shutdown();
					threadPool.awaitTermination(1, TimeUnit.MINUTES);
					for(ProcessorTask task:tasks) results.addAll(task.getResult());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}								
	
				for (int i=0;i<results.size();i++) {
					if (i!=0) line.append(",");
					if (results.get(i).getResult() == null) line.append("?");
					else line.append(results.get(i).getResult().replace("NaN", "?"));
				}
				line.append("\n");
				
				if (d.getSize()%op == 0) {
					appendToArff(line.toString());
					line = new StringWriter();
				}
				
				completeData.addEntry(one);
				if (d.getSize()%tp == 0)if(st==0)st++;else log.info("Extraction progress: "+(st++*10)+"%");
			}
			
			log.info("Training with "+completeData.getSize()+" instances done...");
		} else {
			for (PredictionProcessor processor:processors) {
				processor.setCompleteData(d);
			}
		}		
		log.info("--------------------------------------------------------------");
		
		// Evaluate input options ______________________ 2 _______________________
		if (commands.contains("2")) {
			String input = PredictionPath.featureFile;
			log.info("Training model from feature input at "+input);
			evaluator.trainFromArff(input);
			log.info("Resulting model is "+evaluator.toString());
		}
		
		log.info("--------------------------------------------------------------");
			
		// Evaluate input options ______________________ 3 _______________________
		if (commands.contains("3")) {
			if (processors.size() == 0) {
				log.warn("No processor has been instantiated");
			} else {
				log.info("Trying to add bits and pieces from "+PredictionPath.testingPath+" now...");
				PredictionReader newData = new PredictionReader(PredictionPath.testingPath);
				DataContainer dc = newData.getCompleteData();
				log.info("Replaying sensor readings from the testing file...");
				log.info("--------------------------------------------------------------");

				while (dc.getSize() > 0) {
					DataEntry one = dc.removeFirstEntry();   // Take reading from testing set
					Vector<PredictionFeature> results = new Vector<PredictionFeature>();
					
					threadPool = Executors.newFixedThreadPool(numberOfCPUs);
					Vector<ProcessorTask> tasks=new Vector<ProcessorTask>();
					for (PredictionProcessor processor:processors) {
						ProcessorTask task = new ProcessorTask(processor,one);
						tasks.add(task);
						threadPool.execute(task);
					}
				
					try {
						threadPool.shutdown();
						threadPool.awaitTermination(1, TimeUnit.MINUTES);
						for(ProcessorTask task:tasks) results.addAll(task.getResult());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}	
					
					boolean terminate = evaluator.evaluate(results, false);
					
					if (terminate) {
						log.info("Abnormal event detected at "+DateTime.fromLong(one.getTime()));
						System.exit(0);
					}
				
					d.addEntry(one);	// Add the testing value to the complete data set
				}
				
				log.info("No exceptions found in the testing trace!");
			}
		}
	}
	
	public static class ProcessorTask implements Runnable {
		private PredictionProcessor processor;
		private DataEntry one;
		private Vector<PredictionFeature> result;

		public ProcessorTask(PredictionProcessor processor, DataEntry one) {
			this.processor = processor;
			this.one = one;
		}
		
		public Vector<PredictionFeature> getResult() {
			return result;
		}

		public PredictionProcessor getProcessor() {
			return processor;
		}

		public void run() {
			result = processor.addValueToModel(one);
			if (result.size() != processor.getResultTypes().length) {
				log.fatal("Processor "+processor.getClass()+" returns a wrong number of features! Exiting...");
				System.exit(0);
			}
		}
	};
	
	private static String pollType() {
		try {
			final BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Choose your action:");
			System.out.println("[1] Extract features from data in "+PredictionPath.trainingPath);
			System.out.println("[2] Initialize model from feature data");
			System.out.println("[3] Gradually add data from "+PredictionPath.testingPath+" and check matches");
			
			System.out.println("---");
			
			System.out.println("Make your selection (multiple selections possible, e.g. '12'): ");
			return stdin.readLine();
		} catch (final Exception e) {
			System.exit(1);
		}
		return "";
	}
	
	
	private static void appendToArff(String data) {
		appendToArff(data, false);
	}
	
	private static void appendToArff(String data, boolean createNewFile) {
		try {
			String fname = PredictionPath.featureFile;
			FileWriter fstream = new FileWriter(fname, !createNewFile);
			BufferedWriter out = new BufferedWriter(fstream);
			out.append(data);
			out.close();
		} catch(final Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getArffHeader() {
		StringWriter header = new StringWriter();
		header.append("@relation ConsumerPrediction\n\n");
		
		for(PredictionProcessor p:processors) {
			for(int i = 0; i < p.getResultTypes().length; i++) {
				try {
					header.append("@attribute " + p.getResultTypes()[i] + " " + p.getResultRanges()[i] + "\n");
				} catch(final Exception e) {
					e.printStackTrace();
				}
			}
		}
		return header.toString();
	}
		
	public static void initLogger() {
		Logger rootlog = Logger.getRootLogger();
		final PatternLayout layout = new PatternLayout();
		layout.setConversionPattern("[%6r] %5p: %27C{1}:%-20M - %m%n");
		final ConsoleAppender consoleAppender = new ConsoleAppender(layout);
		rootlog.addAppender(consoleAppender);
		rootlog.setLevel(Level.INFO);
	}
}
