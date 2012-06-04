package de.tud.kom.challenge.weka;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeSelection;
//import weka.attributeSelection.ChiSquaredAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import de.tud.kom.challenge.ClassifierList;

public class MachineLearner {

	private Instances data;
	private final static Logger log = Logger.getLogger(MachineLearner.class.getSimpleName());

	/**
	 * 
	 * @param filename
	 *            The path to the generated ARFF training file
	 */
	public MachineLearner(String filename) {
		try {
			DataSource source = new DataSource(filename);
			data = source.getDataSet();
			if (data.classIndex() == -1) {
				data.setClassIndex(data.numAttributes() - 1);
			}
			log.info("Training set has " + (this.data.numAttributes() - 1) + " attributes");
			log.info("Training set has " + this.data.numInstances() + " instances");
		} catch (Exception e) {
			log.error(e);
			System.exit(-1);
		}
	}
	
	public String getAttrMapping() {
		String attrNames = "";
		for(int i=0; i<data.numAttributes(); i++)
			attrNames += (i+1) + "," + data.attribute(i).name() + "\n";
		return attrNames;
	}

	public String getAttrMapping(int i) {
		return data.attribute(i).name();
	}

	public int getNumAttributes() {
		return data.numAttributes();
	}
	
	public int getNumInstances() {
		return data.numInstances();
	}

	/**
	 * Checks for the compatibility of the classifiers
	 * @return the list of classifiers compatible with the underlying data set
	 */
	public List<Classifier> getAllCompatibleClassifiers(){
		List<Classifier> clList = ClassifierList.getClassifiers();
		List<Classifier> result = new ArrayList<Classifier>();
		
		for(Classifier c:clList) {
			try {
				final Evaluation eval = new Evaluation(this.data);
				eval.crossValidateModel(c, this.data, 10, new Random(1));
				result.add(c);
			} catch (UnsupportedAttributeTypeException e1) {
				log.error("Classifier: " + c.getClass().getSimpleName() + " Message: " + e1.getMessage());
			} catch (Exception e2) {
				log.error("Classifier: " + c.getClass().getSimpleName() + " Message: " + e2.getMessage());
			}
		}
		
		log.info("Compatible classifier for this dataset are:");
		for(Classifier c:result) log.info(c.getClass().getSimpleName());

		return result;
	}

	/**
	 * Trigger the cross validation
	 * @param classifier the classifier to use
	 * @return the Evaluation result
	 */
	public Evaluation crossValidation(final Classifier classifier) {
		try {
			Evaluation eval = new Evaluation(data);
			eval.crossValidateModel(classifier, data, 10, new Random(1));
			return eval;
		} catch (final Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return null;
	}

	/**
	 * Conducts a brute-forced cross validation on the given data
	 * @param combination the combination of features to use (integer array containing their indices)
	 * @param classifier the classifier to use
	 * @param classifierParams the parameter settings for the classifier
	 * @return the evaluation result
	 */
	public Evaluation bruteForceCrossValidation(int[] combination,
			Classifier classifier, ClassifierParameters classifierParams) {

		// Set the classification options
//		try {
//			classifier.setOptions(classifierParams.getOptions(classifier));
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		} //TODO: SET OPTIONS ... CHANGED FROM WEKA 3.6.2 -> 3.7.5
		if (!classifierParams.paramsAvailable) {
			log.info("No available combination of parameters for the classifier "
						+ classifier.getClass().getSimpleName() + " available.");
			log.info("Please update MachineLearner.getCombinationsOfParams.");
			log.info("Continuing with default paramters");
		}
		
		// check that at least one column remains
		Instances instNew = removeAttributesInvert(combination);
		if (instNew.classIndex() == -1) {
			instNew.setClassIndex(instNew.numAttributes() - 1);
		}
				
		try {
			Evaluation eval = new Evaluation(instNew);
			eval.crossValidateModel(classifier, instNew, 10, new Random(1));
			return eval;

		} catch (Exception e) {
			// We get here when e.g. the classifier cannot process the given input types
			//e.printStackTrace();
			return null;
		}
	}

	/**
	 * Selectively create copies of the data set with certain attributes removed
	 * @param attrNumbers the removed attributes
	 * @return the Instances without the specified fields
	 */
	public Instances removeAttributesInvert(int[] attrNumbers) {
		Remove remove = new Remove();
		remove.setAttributeIndicesArray(attrNumbers);
		// InvertSelection = true => selected Attributes are kept, otherwise
		// they are deleted.
		remove.setInvertSelection(true);

		try {
			remove.setInputFormat(data);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Instances instNew = null;
		try {
			instNew = Filter.useFilter(data, remove);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return instNew;
	}

	/**
	 * Classify a testing file with regard to the given model
	 * @param testFile the file to classify
	 * @param classifier the Classifier to use
	 */
	public void classify(String testFile, Classifier classifier) {
		try {
			classifier.buildClassifier(data);

			if (classifier instanceof J48) {
				final String[] summary = ((J48) classifier).toSummaryString().split("\n");
				MachineLearner.log.info("Classifier statistics:");
				MachineLearner.log.info("* " + summary[0]);
				MachineLearner.log.info("* " + summary[1]);
			}

			Visualizer.visualize(classifier);

			final DataSource source = new DataSource(testFile);
			final Instances testData = source.getDataSet();
			if (testData.classIndex() == -1) {
				testData.setClassIndex(testData.numAttributes() - 1);
			}

			MachineLearner.log.info("Trying to classify instances from file " + testFile);

			for (int k = 0; k < testData.numInstances(); k++) {
				final Instance i = testData.instance(k);
				MachineLearner.log.info("Instance " + (k + 1) + "/"
						+ testData.numInstances() + ": " + i.toString());
				final double x = classifier.classifyInstance(i);
				MachineLearner.log.info("Predicted type: "
						+ testData.classAttribute().value((int) x));
			}

		} catch (final Exception e) {
			e.printStackTrace();
			MachineLearner.log.error(e);
			System.exit(-1);
		}
	}

	public double[][] attributeSelection() { //TODO: ChiSquaredAttributeEval doesn't exist in weka 3.7.5
//		try {
//			AttributeSelection as = new AttributeSelection();
//			ChiSquaredAttributeEval cse = new ChiSquaredAttributeEval();
//			ASSearch sea = new Ranker();
//			
//			as.setEvaluator(cse);
//			as.setSearch(sea);
//			as.SelectAttributes(data);
//			
//			//log.info("Attribute mapping is "+as.toResultsString());
//			
//			return as.rankedAttributes();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
		return null;
	}
	
	/*
	 * @author Kristopher Born
	 * 
	 * the methods 'getClassifier' and 'classificationResult' are needed by 'statistic' package in class 'ClassifiedTestingARFFGenerator'
	 */
	public static Classifier getClassifier() {
		final Classifier classifier;
		
		// RandomForst
		RandomForest cr = new RandomForest();
		cr.setNumFeatures(6);
		cr.setNumTrees(60);
		
		classifier = cr;
		// J48
		// classifier = new J48();
		// ((J48) classifier).setUnpruned(false);
		
		// JRip
		// classifier = new JRip();
		
		// Naive Bayesian
		// classifier = new NaiveBayes();
		
		// SupportVector
		// classifier = new SMO();
		
		MachineLearner.log.info("Used classifier is of type " + classifier.getClass().getSimpleName());
		return classifier;
	}
	
	public String[] classificationResult(final String testFile, final Classifier classifier) {
		String resultString[] = new String[0];
		try {
			classifier.buildClassifier(this.data);
			
			if(classifier instanceof J48) {
				final String[] summary = ((J48) classifier).toSummaryString().split("\n");
				MachineLearner.log.info("Classifier statistics:");
				MachineLearner.log.info("* " + summary[0]);
				MachineLearner.log.info("* " + summary[1]);
			}
			
			Visualizer.visualize(classifier);
			
			final DataSource source = new DataSource(testFile);
			final Instances testData = source.getDataSet();
			if(testData.classIndex() == -1) {
				testData.setClassIndex(testData.numAttributes() - 1);
			}
			
			MachineLearner.log.info("Trying to classify instances from file " + testFile);
			
			resultString = new String[testData.numInstances()];
			
			for (int k=0; k<testData.numInstances();k++){
				final Instance i = testData.instance(k);
				MachineLearner.log.info("Instance "+ (k+1) + "/" + testData.numInstances() + ": "+i.toString());
				final double x = classifier.classifyInstance(i);
				MachineLearner.log.info("Predicted type: " + testData.classAttribute().value((int) x));
				
				// replacing of the '?' by the classified devicetype
				String [] attributeData = i.toString().split(",");
				attributeData[attributeData.length-1] = testData.classAttribute().value((int) x);
				String concatenated = new String();
				for(int l = 0; l<attributeData.length; l++){
					concatenated = concatenated.concat(attributeData[l]);
					if(l!=(attributeData.length-1))
						concatenated = concatenated.concat(",");
				}
				resultString[k] = concatenated;
			}
						
		} catch(final Exception e) {
			e.printStackTrace();
			MachineLearner.log.error(e);
			System.exit(-1);
		}
		return resultString;
	}

	// This seems to be dead code and nobody calls it...
	/*public String getClassPrediction(final String testFile,
			final Classifier classifier) {
		try {
			classifier.buildClassifier(this.data);

			Visualizer.visualize(classifier);

			final DataSource source = new DataSource(testFile);
			final Instances testData = source.getDataSet();
			if (testData.classIndex() == -1) {
				testData.setClassIndex(testData.numAttributes() - 1);
			}

			for (int k = 0; k < testData.numInstances(); k++) {
				final Instance i = testData.instance(k);
				final double x = classifier.classifyInstance(i);
				// but why return on the first opportunity
				return testData.classAttribute().value((int) x);
			}

		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// Check if this this method still valid and working?
	public static void classifyWithConfiguration(String trainARFF, String testARFF, String classifier, int[] combination, String[] options) {

		 DataSource source = null;
		 Instances train = null;
		 DataSource sourceTest = null;
		 Instances test = null;
		try {
			source = new DataSource(trainARFF);
			train = source.getDataSet();
			sourceTest = new DataSource(testARFF);
			test = sourceTest.getDataSet();
		
		 
			if (train.classIndex() == -1)
				train.setClassIndex(train.numAttributes() - 1);
		 
			if (test.classIndex() == -1)
				test.setClassIndex(test.numAttributes() - 1);
		
			int[] plusClass = Arrays.copyOf(combination, combination.length + 1);
			plusClass[combination.length] = train.numAttributes() - 1;
			
			// filter
			Remove rm = new Remove();
			rm.setAttributeIndicesArray(plusClass);
			rm.setInvertSelection(true);
		 
			// meta-classifier
			FilteredClassifier fc = new FilteredClassifier();
			fc.setFilter(rm);
		 
			if(classifier.equals(RandomForest.class.getSimpleName()))
				fc.setClassifier(new RandomForest());
			else if (classifier.equals(J48.class.getSimpleName()))
				fc.setClassifier(new J48());
			else if (classifier.equals(JRip.class.getSimpleName()))
				fc.setClassifier(new JRip());
			else if (classifier.equals(NaiveBayes.class.getSimpleName()))
				fc.setClassifier(new NaiveBayes());
			else if (classifier.equals(BayesNet.class.getSimpleName()))
				fc.setClassifier(new BayesNet());
			else if (classifier.equals(RandomTree.class.getSimpleName()))
				fc.setClassifier(new RandomTree());
			else if (classifier.equals(REPTree.class.getSimpleName()))
				fc.setClassifier(new REPTree());
			else if (classifier.equals(VFI.class.getSimpleName()))
			 	fc.setClassifier(new VFI());
			else if (classifier.equals(NNge.class.getSimpleName()))
				fc.setClassifier(new NNge());
			else if (classifier.equals(PART.class.getSimpleName()))
				fc.setClassifier(new PART());
			else if (classifier.equals(REPTree.class.getSimpleName()))
				fc.setClassifier(new REPTree());
			else if (classifier.equals(HyperPipes.class.getSimpleName()))
				fc.setClassifier(new HyperPipes());
			else if (classifier.equals(IB1.class.getSimpleName()))
				fc.setClassifier(new IB1());
			else if (classifier.equals(IBk.class.getSimpleName()))
				fc.setClassifier(new IBk());
			else if (classifier.equals(OneR.class.getSimpleName()))
				fc.setClassifier(new OneR());
			else if (classifier.equals(SMO.class.getSimpleName()))
				fc.setClassifier(new SMO());
		 
			fc.setOptions(options);

		 
			// train and make predictions
			fc.buildClassifier(train);
		
			for (int i = 0; i < test.numInstances(); i++) {
			
				double pred = 0;
				pred = fc.classifyInstance(test.instance(i));
		
				System.out.print("Instance : " + i);
				System.out.print(". Actual: " + test.classAttribute().value((int) test.instance(i).classValue()));
				System.out.println(". Predicted: " + test.classAttribute().value((int) pred));
			}
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}*/
}
