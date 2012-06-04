package de.tud.kom.challenge.weka;

import java.util.Arrays;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
//import weka.classifiers.lazy.IB1;
import weka.classifiers.lazy.IBk;
//import weka.classifiers.misc.HyperPipes;
//import weka.classifiers.misc.VFI;
import weka.classifiers.rules.JRip;
//import weka.classifiers.rules.NNge;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;

public class ClassifierParameters {
	
	// ALL
	public boolean paramsAvailable = true;
	public int randomSeed = 1;
	
	
	// Random Forest
	public int RFnumTrees = 500;
	public int RFnumFeatures = 0;
	public int RFseed = 1;
	public int RFdepth = 0;

	
	// J48
	public boolean J48unprunedTree = false;
	public double J48confidenceFactor = 0.25;
	public int J48minNumInstPerLeaf = 2;
	public boolean J48reducedErrorPruning = false;
	public int J48reNumFolds = 3;
	public int J48reSeed = 1;
	public boolean J48binarySplits = false;
	public boolean J48noSubtreeRaising = false;
	public boolean J48useLaplace = false;

	
	// JRip
	public int JRipNumFolds = 3;	
	public int JRipMinWeight = 2;
	public int JRipOptRuns = 2;
	public int JRipSeed = 1;
	public boolean JRipDontCheck = false;
	public boolean JRipDontUsePruning = false;
	
	
	// Naive Bayes
	public boolean NaiveBayesKernelEstimation = false;
	public boolean NaiveBayesSupervisedDicretization = false;
	
	
	// BaysNet
	public boolean BNdontUseADTree = true;
	private List<String> BNsearchA = Arrays.asList("weka.classifiers.bayes.net.search.local.GeneticSearch -- -L 10 -A 100 -U 10 -R 1 -M -C -S BAYES",
			"weka.classifiers.bayes.net.search.local.HillClimber -- -P 1 -S BAYES",
			"weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES",
			"weka.classifiers.bayes.net.search.local.LAGDHillClimber -- -L 2 -G 5 -P 1 -S BAYES",
			"weka.classifiers.bayes.net.search.local.RepeatedHillClimber -- -U 10 -A 1 -P 1 -S BAYES",
			"weka.classifiers.bayes.net.search.local.SimulatedAnnealing -- -A 10.0 -U 10000 -D 0.999 -R 1 -S BAYES",
			"weka.classifiers.bayes.net.search.local.TabuSearch -- -L 5 -U 10 -P 1 -S BAYES");
	private List<String> BNestimatorA = Arrays.asList("weka.classifiers.bayes.net.estimate.BayesNetEstimator -- -A 0.5",
			"weka.classifiers.bayes.net.estimate.BMAEstimator -- -A 0.5",
			"weka.classifiers.bayes.net.estimate.MultiNomialBMAEstimator -- -k2 -A 0.5",
			"weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5");
	
	//from 0 to 6
	public int BNsN = 2;
	//from 0 to 3
	public int BNeN = 3;
	
	
	// Random Tree
	public int RTnumAttrRnd = 0;
	//RTminNumInstPerLeaf<= numInst
	public int RTminNumInstPerLeaf = 1;
	public int RTseed = 1;
	//RTdepth = 0 => unlimited
	public int RTdepth = 0;
	//RTnumFolds<= numInst. = 0 => no Backfitting
	public int RTnumFolds = 0;
	public boolean RTalloUnclassifierInst = false;
	
	
	// REPTree
	public int REPTreeminNumInstPerLeaf = 2;
	public double REPTreeVarProp = 0.001;
	public int REPTreeREnumFolds = 3;
	public int REPTreeSeed = 1;
	public boolean REPTnoPruning = false;
	//maxDepth = -1 => no maximum
	public int REPTreeMaxDepth = -1;
	
	
	// VFI
	public boolean VFIdontWeight = false;
	public double VFIbias = 0.6;
	
	
	// NNge
	public int NNgeNumFolder = 5;
	public int NNgeGenAttempts = 5;
	
	
	// PART
	public double PARTconfidence = 0.25;
	public int PARTminNumInstPerLeaf = 2;
	public boolean PARTuseREP = false;
	public int PARTreNumFolds = 3;
	public boolean PARTuseBinarySplits = false;
	public boolean PARTunpruned = false;
	public int PARTreSeed = 1;

	
	// HyperPipes - no input parameters
	
	
	// IB1 - no input parameters
	
	
	// IBk
	public int IBkNNnumber = 1;
	public boolean IBkWeightInvDist = false;
	public boolean IBkWeightBy1 = false;
	public boolean IBkMinMSE = false;
	public int IBkWindowSize = 0;
	public boolean IBkCrossValidate = false;
	private List<String> IBkdistance = Arrays.asList("ChebyshevDistance", "EditDistance", "EuclideanDistance", "ManhattanDistance");
	//from 0 to 3
	public int IBkDist = 2;
	private List<String> IBkNN = Arrays.asList("BallTree", "CoverTree", "KDTree", "LinearNNSearch");
	//from 0 to 3
	public int IBkaN = 3;
		

	// OneR
	public int OneRminBucketSize = 6;

	
	// SMO
	public double SMOcomplexity = 1;
	//SMOnsn from 0 to 2
	public int SMOnsn = 0;
	public double SMOtolerance = 0.001;
	public double SMOepsilonROE = 1.0E-12;
	public boolean SMOfitToSVM = false;
	public int SMOinternalCV = -1;
	public int SMOseed = 1;
	private List<String> SMOkernels = Arrays.asList("\"weka.classifiers.functions.supportVector.NormalizedPolyKernel -C 250007 -E 2.0\"", 
			"\"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 2.0\"",
			"\"weka.classifiers.functions.supportVector.PrecomputedKernelMatrixKernel -M kernelMatrix.matrix\"",
			"\"weka.classifiers.functions.supportVector.Puk -C 250007 -O 1.0 -S 1.0\"");	
	//from 0 to 3
	public int SMOkN = 1;
	
	
	public ClassifierParameters() {
	}

	public ClassifierParameters(boolean paramsAvailable) {
		this.paramsAvailable = paramsAvailable;
	}

	public String toString(Classifier classifier) {
		return Arrays.toString(getOptions(classifier));
	}

	public String[] getOptions(Classifier classifier) {
		String[] options = null;
		String clSimple = classifier.getClass().getSimpleName();
		String parameters = "";

		/*Valid options are:
			 -I <number of trees>
			  Number of trees to build.
			 -K <number of features>
			  Number of features to consider (<1=int(logM+1)).
			 -S
			  Seed for random number generator.
			  (default 1)
			 -depth <num>
			  The maximum depth of the trees, 0 for unlimited.
			  (default 0)
			 -D
			  If set, classifier is run in debug mode and
			  may output additional info to the console
		*/
		if (clSimple.equals(RandomForest.class.getSimpleName())) {
			parameters += "-I " + RFnumTrees +" ";
			parameters += "-K " + RFnumFeatures + " ";
			parameters += "-S " + RFseed + " ";
			if(RFdepth != 0) parameters += "-D " + RFdepth;
		} else

		/*
		 * Valid options are: -U Use unpruned tree. -C confidence Set confidence
		 * threshold for pruning. (Default: 0.25) -M number Set minimum number
		 * of instances per leaf. (Default: 2) -R Use reduced error pruning. No
		 * subtree raising is performed. -N number Set number of folds for
		 * reduced error pruning. One fold is used as the pruning set. (Default:
		 * 3) -B Use binary splits for nominal attributes. -S Don't perform
		 * subtree raising. -L Do not clean up after the tree has been built. -A
		 * If set, Laplace smoothing is used for predicted probabilites. -Q The
		 * seed for reduced-error pruning.
		 */
		if (clSimple.equals(J48.class.getSimpleName())) {
			if (J48unprunedTree)
				parameters += "-U ";
			else if (J48reducedErrorPruning)
					parameters += "-R " + "-N " + J48reNumFolds + " -Q " + J48reSeed + " ";
				else parameters += "-C " + Double.toString(J48confidenceFactor) + " ";
			parameters += "-M " + Integer.toString(J48minNumInstPerLeaf) + " ";
			if (J48binarySplits)
				parameters += "-B ";
			if (!J48unprunedTree && J48noSubtreeRaising)
				parameters += "-S ";
			if (J48useLaplace)
				parameters += "-A ";
		} else
		
		/*
		 * -F number 
		 * The number of folds for reduced error pruning. One fold is used as the pruning set. (Default: 3)
		 * -N number 
		 * The minimal weights of instances within a split. (Default: 2)
		 * -O number
		 * Set the number of runs of optimizations. (Default: 2)
		 * -D 
		 * Whether turn on the debug mode 
		 * -S number 
		 * The seed of randomization used in Ripper.(Default: 1)
		 * -E 
		 * Whether NOT check the error rate >= 0.5 in stopping criteria. (default: check)
		 * -P 
		 * Whether NOT use pruning. (default: use pruning)
		 */			
		if (clSimple.equals(JRip.class.getSimpleName())) {
			parameters += "-F " + JRipNumFolds + " ";
			parameters += "-N " + JRipMinWeight + " ";
			parameters += "-O " + JRipOptRuns + " ";
			parameters += "-S " + JRipSeed + " ";
			if(JRipDontCheck)
				parameters += "-E ";
			if(JRipDontUsePruning)
				parameters += "-P ";

		} else
		
		/*
		 * Valid options are:
		 * -K
		 * Use kernel estimation for modelling numeric attributes rather than a single normal distribution.
		 * -D
		 * Use supervised discretization to process numeric attributes.
		 */
		if (clSimple.equals(NaiveBayes.class.getSimpleName())) {
			
			if(NaiveBayesKernelEstimation && !NaiveBayesSupervisedDicretization)
				parameters += "-K ";
			if(NaiveBayesSupervisedDicretization && !NaiveBayesKernelEstimation)
					parameters += "-D";
		} else
		
		/*
			Valid options are:
				 -D
				  Do not use ADTree data structure
				 
				 -B <BIF file>
				  BIF file to compare with
				 
				 -Q weka.classifiers.bayes.net.search.SearchAlgorithm
				  Search algorithm
				 
				 -E weka.classifiers.bayes.net.estimate.SimpleEstimator
				  Estimator algorithm
		*/
		if (clSimple.equals(BayesNet.class.getSimpleName())) {
			if(BNdontUseADTree)
				parameters += "-D ";
			parameters += "-Q " + BNsearchA.get(BNsN) + " ";
			parameters += "-E " + BNestimatorA.get(BNeN);
			
		} else

		/*
		 * Valid options are:
		 * -K <number of attributes>
		 * Number of attributes to randomly investigate
		 * (<0 = int(log_2(#attributes)+1)).
		 * -M <minimum number of instances>
		 * Set minimum number of instances per leaf.
		 * -S <num>
		 * Seed for random number generator.
		 * (default 1)
		 * -depth <num>
		 * The maximum depth of the tree, 0 for unlimited.
		 * (default 0)
		 * -N <num>
		 * Number of folds for backfitting (default 0, no backfitting).
		 * -U
		 * Allow unclassified instances.
		 * -D
		 * If set, classifier is run in debug mode and
		 * may output additional info to the console
		 */
		if (clSimple.equals(RandomTree.class.getSimpleName())) {
			parameters += "-K " + RTnumAttrRnd + " ";
			parameters += "-M " + RTminNumInstPerLeaf + " ";
			parameters += "-S " + RTseed + " ";
			if(RTdepth != 0) parameters += "-depth " + RTdepth + " ";
			if(RTnumFolds != 0) parameters += "-N " + RTnumFolds + " ";
			if(RTalloUnclassifierInst) parameters += "-U ";
			
		} else

			/*
			 * -M number
			 * Set minimum number of instances per leaf (default 2).
			 * -V number 
			 * Set minimum numeric class variance proportion of train variance for split (default 1e-3).
			 * -N number 
			 * Number of folds for reduced error pruning (default 3).
			 * -S number 
			 * Seed for random data shuffling (default 1).
			 * -P 
			 * No pruning.
			 * -L 
			 * Maximum tree depth (default -1, no maximum).
			 */
		if (clSimple.equals(REPTree.class.getSimpleName())) {
			parameters += "-M " + REPTreeminNumInstPerLeaf + " ";
			parameters += "-V " + REPTreeVarProp + " ";
			parameters += "-N " + REPTreeREnumFolds + " ";
			parameters += "-S " + REPTreeSeed + " ";
			if(REPTnoPruning) parameters += "-P ";
			parameters += "-L " + REPTreeMaxDepth;			
		} else

		/*
		 * Valid options are:
		 * -C 
		 * Don't Weight voting intervals by confidence.
		 * -B 
		 * Set exponential bias towards confident intervals. default = 0.6
		 */
//		if (clSimple.equals(VFI.class.getSimpleName())) {
//			if(VFIdontWeight) parameters += "-C ";
//			parameters += "-B " + VFIbias;
// 		} else

 		/*
 		 * Valid options are:
 		 * -I num 
 		 * Set the number of folder to use in the computing of the mutual information (default 5)
 		 * -G num 
 		 * Set the number of attempts of generalisation (default 5)
 		 */
//		if (clSimple.equals(NNge.class.getSimpleName())) {
//			parameters += "-I " + NNgeNumFolder + " ";
//			parameters += "-G " + NNgeGenAttempts + " ";
//	
//		} else

		/*
		 * -C confidence 
		 * Set confidence threshold for pruning. (Default: 0.25)
		 * -M number 
		 * Set minimum number of instances per leaf. (Default: 2)
		 * -R 
		 * Use reduced error pruning.
		 * -N number 
		 * Set number of folds for reduced error pruning. One fold is used as the pruning set. (Default: 3)
		 * -B 
		 * Use binary splits for nominal attributes.
		 * -U 
		 * Generate unpruned decision list.
		 * -Q 
		 * The seed for reduced-error pruning.
		 */
		if (clSimple.equals(PART.class.getSimpleName())) {
			parameters += "-M " + PARTminNumInstPerLeaf + " ";
			if(PARTuseREP) 
				parameters += "-R " + "-N " + PARTreNumFolds + " -Q " + PARTreSeed +" ";
			else parameters += "-C " + PARTconfidence + " "; 
			if(PARTuseBinarySplits) parameters += "-B ";
			if(PARTunpruned) parameters += "-U ";
		} else

//		if (clSimple.equals(HyperPipes.class.getSimpleName())) {
//			// No input parameters to consider. Do nothing.
//		} else
//
//		if (clSimple.equals(IB1.class.getSimpleName())) {
//			// No input parameters to consider. Do nothing.
//		} else
			
		/*	Valid options are:
		 * -I
		 * Weight neighbours by the inverse of their distance (use when k > 1)
		 * -F
		 * Weight neighbours by 1 - their distance (use when k > 1)
		 * -K <number of neighbors>
		 * Number of nearest neighbours (k) used in classification. (Default = 1)
		 * -E
		 * Minimise mean squared error rather than mean absolute
		 * error when using -X option with numeric prediction.
		 * -W <window size>
		 * Maximum number of training instances maintained.
		 * Training instances are dropped FIFO. (Default = no window)
		 * -X
		 * Select the number of nearest neighbours between 1
		 * and the k value specified using hold-one-out evaluation
		 * on the training data (use when k > 1)
		 * -A
		 * The nearest neighbour search algorithm to use (default: weka.core.neighboursearch.LinearNNSearch).
		*/
		if (clSimple.equals(IBk.class.getSimpleName())) {
			parameters += "-K " + IBkNNnumber + " ";
			if(IBkWeightInvDist)
				parameters += "-I ";
			else if(IBkWeightBy1)
					parameters += "-F ";
			if(IBkMinMSE)
				parameters += "-E ";
			parameters += "-W " + IBkWindowSize + " ";
//			CV not needed when traversing all KNN			
//			if(IBkCrossValidate && IBkaN!=1) //CoverTree not compatible with hold-one-out cross-validation
//				parameters += "-X ";
			if(IBkaN == 1 || IBkaN == 2)
				IBkDist = 2;	// KDTree and CoverTree work currently only with EuclideanDistanceFuction
			String IBkNNSA = "\"weka.core.neighboursearch." + IBkNN.get(IBkaN) + " -A \\\"weka.core." + IBkdistance.get(IBkDist) + " -R first-last\\\"\"";
			parameters += "-A " + IBkNNSA;

		} else
		
		/*
		 * Valid options are:
		 * -B <minimum bucket size>
		 * The minimum number of objects in a bucket (default: 6).
		 */
		if (clSimple.equals(OneR.class.getSimpleName())) {
			parameters += "-B " + OneRminBucketSize;
		} else


		/*	Valid options are:
				 -D
				  If set, classifier is run in debug mode and
				  may output additional info to the console
				 -no-checks
				  Turns off all checks - use with caution!
				  Turning them off assumes that data is purely numeric, doesn't
				  contain any missing values, and has a nominal class. Turning them
				  off also means that no header information will be stored if the
				  machine is linear. Finally, it also assumes that no instance has
				  a weight equal to 0.
				  (default: checks on)
				 -C <double>
				  The complexity constant C. (default 1)
				 -N
				  Whether to 0=normalize/1=standardize/2=neither. (default 0=normalize)
				 -L <double>
				  The tolerance parameter. (default 1.0e-3)
				 -P <double>
				  The epsilon for round-off error. (default 1.0e-12)
				 -M
				  Fit logistic models to SVM outputs. 
				 -V <double>
				  The number of folds for the internal
				  cross-validation. (default -1, use training data)
				 -W <double>
				  The random number seed. (default 1)
				 -K <classname and parameters>
				  The Kernel to use.
				  (default: weka.classifiers.functions.supportVector.PolyKernel)
				 
				 Options specific to kernel weka.classifiers.functions.supportVector.PolyKernel:
				 
				 -D
				  Enables debugging output (if available) to be printed.
				  (default: off)
				 -no-checks
				  Turns off all checks - use with caution!
				  (default: checks on)
				 -C <num>
				  The size of the cache (a prime number), 0 for full cache and 
				  -1 to turn it off.
				  (default: 250007)
				 -E <num>
				  The Exponent to use.
				  (default: 1.0)
				 -L
				  Use lower-order terms.
				  (default: no)
		*/
		if (clSimple.equals(SMO.class.getSimpleName())) {
			parameters += "-C " + SMOcomplexity + " ";
			parameters += "-N " + SMOnsn + " ";
			parameters += "-L " + SMOtolerance + " ";
			parameters += "-P " + SMOepsilonROE + " ";
			parameters += "-M " + SMOfitToSVM +" ";
			parameters += "-V " + SMOinternalCV + " ";
			parameters += "-W " + SMOseed + " ";
			
			String SMOKernel = SMOkernels.get(SMOkN);
			parameters += "-K " + SMOKernel;
		}


		try {
			options = weka.core.Utils.splitOptions(parameters);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		return options;
	}
	
	public static void main(String[] args) {
		/*ClassifierParameters cp = new ClassifierParameters();
		System.out.println(cp.toString(new RandomForest()));
		System.out.println(cp.toString(new J48()));
		System.out.println(cp.toString(new JRip()));
		System.out.println(cp.toString(new NaiveBayes()));
		System.out.println(cp.toString(new BayesNet()));
		System.out.println(cp.toString(new RandomTree()));
		System.out.println(cp.toString(new REPTree()));
		System.out.println(cp.toString(new VFI()));
		System.out.println(cp.toString(new NNge()));
		System.out.println(cp.toString(new PART()));
		System.out.println(cp.toString(new HyperPipes()));
		System.out.println(cp.toString(new IB1()));
		System.out.println(cp.toString(new IBk()));
		System.out.println(cp.toString(new OneR()));
		System.out.println(cp.toString(new SMO()));
		System.out.println(100/10d);
		*/

	}

}
