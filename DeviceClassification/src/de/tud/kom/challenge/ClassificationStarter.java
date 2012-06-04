package de.tud.kom.challenge;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import de.tud.kom.challenge.statistic.Initialiser;
import de.tud.kom.challenge.arff.FeatureExtractor;
import de.tud.kom.challenge.arff.FileMapper;
import de.tud.kom.challenge.processors.FeatureProcessor;
import de.tud.kom.challenge.util.AnalysisToolsGUI;
import de.tud.kom.challenge.util.NumberTool;
import de.tud.kom.challenge.weka.BruteForce;
import de.tud.kom.challenge.weka.MachineLearner;

public class ClassificationStarter {

	private final static Logger log = Logger.getLogger(ClassificationStarter.class.getSimpleName());

	private static final int QUANTIZATION = 1;

	public static void main(final String[] args) {
		ClassificationStarter.initLogger();
		Vector<FeatureProcessor> processors = ProcessorList.getProcessors();
		FeatureExtractor fe = null;
		MachineLearner ml = null;
		Initialiser init = null;
		
		String commands = pollType();
		
		if (commands.contains("1")) {
			fe = new FeatureExtractor(processors);
			fe.setQuantization(QUANTIZATION);
			fe.createTrainingSet();
		}
		if (commands.contains("2")) {
			ml = new MachineLearner(FileMapper.trainingPath + File.separator + FileMapper.trainingArff);
			long time = System.currentTimeMillis();
			Classifier cl = new RandomForest();
			Evaluation e = ml.crossValidation(cl);
			writeEvalResult(cl,e,time);
			time = System.currentTimeMillis();
		}
		
		if (commands.contains("3")) {
			ml = new MachineLearner(FileMapper.trainingPath + File.separator + FileMapper.trainingArff);
			Vector<Classifier> classifiers = ClassifierList.getClassifiers();
			
			for (int i=0; i<classifiers.size(); i++) {
				long time = System.currentTimeMillis();
				Evaluation e = ml.crossValidation(classifiers.get(i));
				writeEvalResult(classifiers.get(i),e,time);
				time = System.currentTimeMillis();
			}
		}
		
		// -- //
		
		if (commands.contains("4")) {
			if (fe == null) {
				fe = new FeatureExtractor(processors);
				fe.setQuantization(QUANTIZATION);
			}
			fe.createTestingSet();
		}
		if (commands.contains("5")) {
			ml = new MachineLearner(FileMapper.trainingPath + File.separator + FileMapper.trainingArff);
			ml.classify(FileMapper.testingPath + File.separator + FileMapper.testingArff, new RandomForest());
		}
		
		// -- //
		
		if (commands.contains("9")) {
			BruteForce bf = new BruteForce();
			bf.analyze(FileMapper.trainingPath + File.separator + FileMapper.trainingArff);
		}

		// -- //

		if (commands.contains("G")) {
			new AnalysisToolsGUI();
		}
		
		// -- //
		
		if (commands.contains("a")){
			System.out.println("a chosen");
			if (init == null) init = new Initialiser();
			init.crossValidation();
		}
		if (commands.contains("b")){
			System.out.println("b chosen");
			if (init == null) init = new Initialiser();
			init.crossValidationWithVariation(processors);
		}
		if (commands.contains("c")){
			System.out.println("c chosen");
			if (init == null) init = new Initialiser();
			if(fe == null) {
				fe = new FeatureExtractor(processors);
			}
			init.validateToReferenceDevices(fe);
		}
		if (commands.contains("d")){
			System.out.println("d chosen");
			if (init == null) init = new Initialiser();
			if(fe == null) {
				fe = new FeatureExtractor(processors);
			}
			init.analyseTestingData(processors ,fe);
		}
	}
	
	private static String pollType() {
		try {
			final BufferedReader stdin = new BufferedReader(
					new InputStreamReader(System.in));
			System.out.println("Choose your action:");
			System.out.println("[1] Extract features from training set (quantized to "+QUANTIZATION+" watt)");
			System.out.println("[2] Perform cross validation of training set using random forest");
			System.out.println("[3] Perform cross validation for all possible classifiers on training set");
			
			System.out.println("---");
			
			System.out.println("[4] Extract features from testing set");
			System.out.println("[5] Classify testing set");
			
			System.out.println("---");
			
			System.out.println("[9] Process all combinations of features with all combinations of classifier parameters for every classifier using cross validation.");

			System.out.println("---");
			
			System.out.println("[G] Start tool analysis GUI");
			
			System.out.println("---");
			
			System.out.println("[a] Crossvalidation of training data for Feature weighting");
			System.out.println("[b] Crossvalidation of training data with variation for Feature weighting");
			System.out.println("[c] Validation of training data to reference devices for Feature weighting");
			System.out.println("[d] Analysation of testing data");
			
			System.out.println("---");
			
			System.out.println("Make your selection (multiple selections possible, e.g. '234'): ");
			return stdin.readLine();
		} catch (final Exception e) {
			System.exit(1);
		}
		return "";
	}
	
	/**
	 * Output writer class for evaluation results
	 * @param c the classifier that has been evaluated
	 * @param e the evaluation performed
	 * @param time the system time at start of evaluation
	 */
	public static void writeEvalResult(Classifier c, Evaluation e, long time) {
		StringWriter out = new StringWriter();
		out.append("For classifier: "
				+ c.getClass().getSimpleName()
				+ " --> ");
		
		String[] summary = e.toSummaryString(false).split("\n");
		if (summary[2] != null) {
			List<String> val1 = NumberTool.extractGroupOfNumbersFromString(summary[2]);
			val1.remove(0);
			out.append(val1.get(0));
			if (val1.size()>1) out.append("."+val1.get(1));
			out.append("% accuracy");
		}
		out.append(" (runtime: "
				+ ((double) (System.currentTimeMillis() - time) / 1000)
				+ " seconds)");
		log.info(out.toString());	
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
