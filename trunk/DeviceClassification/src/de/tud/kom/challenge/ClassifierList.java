package de.tud.kom.challenge;

import java.util.Vector;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
//import weka.classifiers.functions.RBFNetwork;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.SimpleLogistic;
//import weka.classifiers.lazy.IB1;
import weka.classifiers.lazy.IBk;
import weka.classifiers.lazy.KStar;
import weka.classifiers.lazy.LWL;
import weka.classifiers.meta.MultiClassClassifier;
//import weka.classifiers.misc.HyperPipes;
//import weka.classifiers.misc.VFI;
//import weka.classifiers.rules.ConjunctiveRule;
import weka.classifiers.rules.JRip;
//import weka.classifiers.rules.NNge;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.LMT;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;

public class ClassifierList {
	
	public static Vector<Classifier> getClassifiers() {
		// IF A CLASSIFIER IS ADDED HERE PLEASE UPDATE AS WELL
		// getCombinationsOfParams(Classifier c)

		Vector<Classifier> result = new Vector<Classifier>();

		result.add(new RandomForest());
		
		J48 classifierJ48 = new J48();
		classifierJ48.setUnpruned(false);
		result.add(classifierJ48);

		result.add(new JRip());
		result.add(new NaiveBayes());
		result.add(new BayesNet()); // filling in missing values in data set

		RandomTree classifierRT = new RandomTree();
		classifierRT.setAllowUnclassifiedInstances(true);
		result.add(classifierRT);

		result.add(new REPTree());
//		result.add(new VFI());
//		result.add(new NNge());
		result.add(new PART());

		// following are less 80% in combination of all feature extractors:
//		result.add(new HyperPipes());
//		result.add(new IB1());
		result.add(new IBk());
		result.add(new OneR());
		result.add(new SMO());

		// more than one minute runtime
		result.add(new MultiClassClassifier());

		// needs more then two minutes, reaches 75% cross validation
		result.add(new LMT()); //unsuitable for classification and very
		// expensive in terms of calculation time
		// needs more than eight minutes, reaches 78% cross validation
		result.add(new Logistic());
		// needs more than 15 minutes
//		result.add(new RBFNetwork());
		// more than one minute by only about 60%
		result.add(new SimpleLogistic());

		// following classifiers are not suitable because they have only bad
		// cross validation results: << 50 %
		result.add(new DecisionStump());
//		result.add(new ConjunctiveRule());
		result.add(new LWL()); // very expensive in terms of calculation time
		result.add(new KStar());

		return result;
	}
	
	/*public static List<Classifier> getClassifiers() {
		final ArrayList<Classifier> classifiers = new ArrayList<Classifier>();

		classifiers.add(new RandomForest());

		final J48 j48 = new J48();
		j48.setUnpruned(false);
		classifiers.add(j48);
		/*
		 * classifiers.add(new JRip()); classifiers.add(new NaiveBayes());
		 * classifiers.add(new SMO());
		 */
	//	return classifiers;
//	}

//	public static Classifier getClassifier() {
//		final Classifier classifier;

		// RandomForest
//		RandomForest cr = new RandomForest();
//		cr.setNumFeatures(6);
//		cr.setNumTrees(60);

//		classifier = cr;
		// J48
		// classifier = new J48();
		// ((J48) classifier).setUnpruned(false);

		// JRip
		// classifier = new JRip();

		// Naive Bayesian
		// classifier = new NaiveBayes();

		// SupportVector
		// classifier = new SMO();

//		MachineLearner.log.info("Used classifier is of type "
//				+ classifier.getClass().getSimpleName());
//		return classifier;
//	}*/

}
