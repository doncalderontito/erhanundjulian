package de.tud.kom.challenge.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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

import de.tud.kom.challenge.weka.ClassifierParameters;

public class ClassifierParametersCombinations {
	
	private static Random rnd = new Random();
	private static List<Boolean> bList = Arrays.asList(false, true);

	public static List<ClassifierParameters> getAllClassifierParamsCombinations(
			Classifier classifier, int numOfAttributes, int numOfInstances) {
		
		//In Java7 the If Cases can be exchanged with Switch Cases
		
		ArrayList<ClassifierParameters> result = new ArrayList<ClassifierParameters>();
		
		String ClSimpleName = classifier.getClass().getSimpleName();
		
		if (ClSimpleName.equals(RandomForest.class.getSimpleName())) {
			result.addAll(randomForestCombinations(numOfAttributes));
		} else

		if (ClSimpleName.equals(J48.class.getSimpleName())) {
			result.addAll(j48Combinations(numOfAttributes, numOfInstances));			
		} else

		if (ClSimpleName.equals(JRip.class.getSimpleName())) {
			result.addAll(JRipCombinations(numOfInstances));
		} else

		if (ClSimpleName.equals(NaiveBayes.class.getSimpleName())) {
			result.addAll(NaiveBayesCombinations());
		} else
		if (ClSimpleName.equals(BayesNet.class.getSimpleName())) {
			result.addAll(BayesNetCombinations());
		} else
		if (ClSimpleName.equals(RandomTree.class.getSimpleName())) {
			//classifierRT.setAllowUnclassifiedInstances(true);
			result.addAll(RandomTreeCombinations(numOfAttributes, numOfInstances));
		} else
		if (ClSimpleName.equals(REPTree.class.getSimpleName())) {
			result.addAll(REPTreeCombinations(numOfInstances));
		} else
//		if (ClSimpleName.equals(VFI.class.getSimpleName())) {
//			result.addAll(VFIcombinations());
//		} else
//		if (ClSimpleName.equals(NNge.class.getSimpleName())) {
//			result.addAll(NNgeCombinations());
//		} else
		if (ClSimpleName.equals(PART.class.getSimpleName())) {
			result.addAll(PARTcombinations(numOfInstances));
//		} 
//		else
//		if (ClSimpleName.equals(HyperPipes.class.getSimpleName())) {
//			result.addAll(HyperPipesCombinations());
//		} else
//		if (ClSimpleName.equals(IB1.class.getSimpleName())) {
//			result.addAll(IB1Combinations());
		} else
		if (ClSimpleName.equals(IBk.class.getSimpleName())) {
			result.addAll(IBkCombinations(numOfAttributes, numOfInstances));
		} else
		if (ClSimpleName.equals(OneR.class.getSimpleName())) {
			result.addAll(OneRcombinations());
		} else
		if (ClSimpleName.equals(SMO.class.getSimpleName())) {
			result.addAll(SMOcombinations());
		} // if classifier not defined here
		else
			result.add(new ClassifierParameters(false));
		
		return result;
	}

	public static ArrayList<ClassifierParameters> randomForestCombinations(int numOfAttributes) {

		int numFeaturesMin = 1;
		int numFeaturesMax = numOfAttributes + 1;
		
		
		ArrayList<ClassifierParameters> result = new ArrayList<ClassifierParameters>();
		
			while(numFeaturesMin != numFeaturesMax) {
				ClassifierParameters cp = new ClassifierParameters();
				cp.RFnumFeatures = numFeaturesMin++;
				cp.RFseed = rnd.nextInt();
				result.add(cp);
			}

		return result;
	}
	
	public static ArrayList<ClassifierParameters> j48Combinations(int numOfAttributes, int numOfInstances) {
		
		ArrayList<ClassifierParameters> result = new ArrayList<ClassifierParameters>();

		for(boolean unprunedTree: bList) {
			for(int minNumInstPerLeaf = 0;  minNumInstPerLeaf <= numOfAttributes; minNumInstPerLeaf++) {
				for(boolean useLaplace: bList) {
					for(boolean binarySplits: bList) {
						for(boolean noSubtreeRaising: bList) {
							for(boolean reducedErrorPruning: bList) {
								if(reducedErrorPruning && !unprunedTree) {
									for(int numFolds = 2; numFolds < numOfInstances/2; numFolds++) {
										ClassifierParameters cp = new ClassifierParameters();
										cp.J48unprunedTree = unprunedTree;
										cp.J48minNumInstPerLeaf = minNumInstPerLeaf;
										cp.J48reducedErrorPruning = reducedErrorPruning;
										cp.J48reNumFolds = numFolds;
										cp.J48binarySplits = binarySplits;
										cp.J48noSubtreeRaising = noSubtreeRaising;
										cp.J48useLaplace = useLaplace;
										cp.J48reSeed = rnd.nextInt();
										result.add(cp);
									}		
								} else {
									for (double confidenceFactor = 1;  confidenceFactor <= 50; confidenceFactor+=5) {
										ClassifierParameters cp = new ClassifierParameters();
										cp.J48unprunedTree = unprunedTree;
										cp.J48confidenceFactor = confidenceFactor / 100d;
										cp.J48minNumInstPerLeaf = minNumInstPerLeaf;
										cp.J48reducedErrorPruning = reducedErrorPruning;
										cp.J48binarySplits = binarySplits;
										cp.J48noSubtreeRaising = noSubtreeRaising;
										cp.J48useLaplace = useLaplace;
										cp.J48reSeed = rnd.nextInt();
										result.add(cp);
									}
								}
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	public static ArrayList<ClassifierParameters> JRipCombinations(int numOfInstances){
		ArrayList<ClassifierParameters> result = new ArrayList<ClassifierParameters>();
		
		for(int JRipNumFolds = 2; JRipNumFolds<numOfInstances/2; JRipNumFolds++) {
			for(int JRipMinWeight = 0; JRipMinWeight<=100; JRipMinWeight++) {
				for(int JRipOptRuns = 0; JRipOptRuns<=100; JRipOptRuns++) {
					for(boolean JRipDontCheck: bList) {
						for(boolean JRipDontUsePruning: bList) {
							ClassifierParameters cp = new ClassifierParameters();
							cp.JRipNumFolds = JRipNumFolds;
							cp.JRipMinWeight = JRipMinWeight;
							cp.JRipOptRuns = JRipOptRuns;
							cp.JRipDontCheck = JRipDontCheck;
							cp.JRipDontUsePruning = JRipDontUsePruning;
							cp.JRipSeed = rnd.nextInt();
							result.add(cp);
						}
					}
				}
			}
		}
		return result;
	}
	
	public static ArrayList<ClassifierParameters> NaiveBayesCombinations(){
		ArrayList<ClassifierParameters> result = new ArrayList<ClassifierParameters>();
		
		for(boolean NaiveBayesKernelEstimation: bList) {
			ClassifierParameters cp = new ClassifierParameters();
			cp.NaiveBayesKernelEstimation = NaiveBayesKernelEstimation;
			cp.NaiveBayesSupervisedDicretization = !NaiveBayesKernelEstimation;
			result.add(cp);
		}
		result.add(new ClassifierParameters());
		
		return result;
	}
	
	public static ArrayList<ClassifierParameters> BayesNetCombinations(){
		ArrayList<ClassifierParameters> result = new ArrayList<ClassifierParameters>();
				
		for(boolean BNdontUseADTree: bList) {
			for(int BNsN=0; BNsN<=6; BNsN++) {
				for(int BNeN=0; BNeN<=3; BNeN++) {
					ClassifierParameters cp = new ClassifierParameters();
					cp.BNdontUseADTree = BNdontUseADTree;
					cp.BNsN = BNsN;
					cp.BNeN = BNeN;
					result.add(cp);
				}
			}
		}
		return result;
	}
	
	public static ArrayList<ClassifierParameters> RandomTreeCombinations(int numOfAttributes, int numOfInstances){
		ArrayList<ClassifierParameters> result = new ArrayList<ClassifierParameters>();
		
		for(int RTnumAttrRnd = 0; RTnumAttrRnd<= numOfAttributes; RTnumAttrRnd++) {
			for(int RTminNumInstPerLeaf=0; RTminNumInstPerLeaf<= numOfInstances; RTminNumInstPerLeaf++) {
				for(int RTnumFolds=2; RTnumFolds<numOfInstances/2; RTnumFolds++) {
					ClassifierParameters cp = new ClassifierParameters();
					cp.RTnumAttrRnd = RTnumAttrRnd;
					cp.RTminNumInstPerLeaf = RTminNumInstPerLeaf;
					cp.RTnumFolds = RTnumFolds;
					cp.RTseed = rnd.nextInt();
					result.add(cp);
				}
			}
		}
		
		return result;
	}
	
	public static ArrayList<ClassifierParameters> REPTreeCombinations(int numOfInstances){
		ArrayList<ClassifierParameters> result = new ArrayList<ClassifierParameters>();
		
		for(int REPTreeminNumInstPerLeaf=0; REPTreeminNumInstPerLeaf<= numOfInstances; REPTreeminNumInstPerLeaf++) {
			for(int REPTreeVarProp=0; REPTreeVarProp<=100; REPTreeVarProp++) {
				for(int REPTreeREnumFolds=2; REPTreeREnumFolds<numOfInstances/2; REPTreeREnumFolds++) {
					for(boolean REPTnoPruning: bList) {
						ClassifierParameters cp = new ClassifierParameters();
						cp.REPTreeminNumInstPerLeaf = REPTreeminNumInstPerLeaf;
						cp.REPTreeVarProp = REPTreeVarProp/1000d;
						cp.REPTreeREnumFolds = REPTreeREnumFolds;
						cp.REPTnoPruning = REPTnoPruning;
						cp.REPTreeSeed = rnd.nextInt();
						result.add(cp);
					}
				}
			}
		}
		return result;
	}
	
	public static ArrayList<ClassifierParameters> VFIcombinations(){
		ArrayList<ClassifierParameters> result = new ArrayList<ClassifierParameters>();
		
		for(boolean VFIdontWeight: bList) {
			for(int VFIbias=0; VFIbias<=10; VFIbias++) {
				ClassifierParameters cp = new ClassifierParameters();
				cp.VFIdontWeight = VFIdontWeight;
				cp.VFIbias = VFIbias/100d;
				result.add(cp);
			}
		}
		return result;
	}
	
	public static ArrayList<ClassifierParameters> NNgeCombinations(){
		ArrayList<ClassifierParameters> result = new ArrayList<ClassifierParameters>();
		
		for(int NNgeNumFolder=1; NNgeNumFolder<=100; NNgeNumFolder++) {
			for(int NNgeGenAttempts=1; NNgeGenAttempts<=100; NNgeGenAttempts++) {
				ClassifierParameters cp = new ClassifierParameters();
				cp.NNgeNumFolder = NNgeNumFolder;
				cp.NNgeGenAttempts = NNgeGenAttempts;
				result.add(cp);
			}
		}
		return result;
	}
	
	public static ArrayList<ClassifierParameters> PARTcombinations(int numOfInstances){
		ArrayList<ClassifierParameters> result = new ArrayList<ClassifierParameters>();
		
			for(int PARTminNumInstPerLeaf=0; PARTminNumInstPerLeaf<=numOfInstances; PARTminNumInstPerLeaf++)
				for(boolean PARTunpruned: bList)
					for(boolean PARTuseBinarySplits: bList)
						for(boolean PARTuseREP: bList)
							if(PARTuseREP)
								for(int PARTreNumFolds=2; PARTreNumFolds<numOfInstances/2;PARTreNumFolds++) {
									ClassifierParameters cp = new ClassifierParameters();
									//cp.PARTconfidence = PARTconfidence/100d;
									cp.PARTminNumInstPerLeaf = PARTminNumInstPerLeaf;
									cp.PARTunpruned = PARTunpruned;
									cp.PARTuseBinarySplits = PARTuseBinarySplits;
									cp.PARTuseREP = PARTuseREP;
									cp.PARTreNumFolds = PARTreNumFolds;
									result.add(cp);
								}
							else 
								for(int PARTconfidence=1; PARTconfidence<=50; PARTconfidence+=5){
									ClassifierParameters cp = new ClassifierParameters();
									cp.PARTconfidence = PARTconfidence/100d;
									cp.PARTminNumInstPerLeaf = PARTminNumInstPerLeaf;
									cp.PARTunpruned = PARTunpruned;
									cp.PARTuseBinarySplits = PARTuseBinarySplits;
									cp.PARTuseREP = PARTuseREP;
									//cp.PARTreNumFolds = PARTreNumFolds;
									result.add(cp);
							}
		return result;
	}
	
	public static ArrayList<ClassifierParameters> HyperPipesCombinations(){
		ArrayList<ClassifierParameters> result = new ArrayList<ClassifierParameters>();
		result.add(new ClassifierParameters());
		
		return result;
	}
	
	public static ArrayList<ClassifierParameters> IB1Combinations(){
		ArrayList<ClassifierParameters> result = new ArrayList<ClassifierParameters>();
		result.add(new ClassifierParameters());
		
		return result;
	}
	
	public static ArrayList<ClassifierParameters> IBkCombinations(int numOfAttributes, int numOfInstances){
		ArrayList<ClassifierParameters> result = new ArrayList<ClassifierParameters>();
	for(int IBkNNnumber = 1; IBkNNnumber<numOfAttributes; IBkNNnumber++)	
		for(boolean IBkWeightInvDist: bList)
			for(boolean IBkMinMSE: bList)
				for(boolean IBkCrossValidate: bList)
					for(int IBkWindowSize=0; IBkWindowSize<=numOfInstances; IBkWindowSize++)
						for(int IBkDist=0; IBkDist<=3; IBkDist++)
							for(int IBkaN=0; IBkaN<=3; IBkaN++) 
								if(!IBkWeightInvDist)
									for(boolean IBkWeightBy1: bList){
										ClassifierParameters cp = new ClassifierParameters();
										cp.IBkNNnumber = IBkNNnumber;
										//cp.IBkWeightInvDist = IBkWeightInvDist;
										cp.IBkWeightBy1 = IBkWeightBy1;
										cp.IBkMinMSE = IBkMinMSE;
										cp.IBkCrossValidate = IBkCrossValidate;
										cp.IBkWindowSize = IBkWindowSize;
										cp.IBkDist = IBkDist;
										cp.IBkaN = IBkaN;
										result.add(cp);
									}
								else {
									ClassifierParameters cp = new ClassifierParameters();
									cp.IBkNNnumber = IBkNNnumber;
									cp.IBkWeightInvDist = IBkWeightInvDist;
									//cp.IBkWeightBy1 = IBkWeightBy1;
									cp.IBkMinMSE = IBkMinMSE;
									cp.IBkCrossValidate = IBkCrossValidate;
									cp.IBkWindowSize = IBkWindowSize;
									cp.IBkDist = IBkDist;
									cp.IBkaN = IBkaN;
									result.add(cp);
								}
		return result;
	}
	
	public static ArrayList<ClassifierParameters> OneRcombinations(){
		ArrayList<ClassifierParameters> result = new ArrayList<ClassifierParameters>();
		
		for(int OneRminBucketSize=0; OneRminBucketSize<=100; OneRminBucketSize++) {
			ClassifierParameters cp = new ClassifierParameters();
			cp.OneRminBucketSize = OneRminBucketSize;
			result.add(cp);
		}
		return result;
	}
	
	public static ArrayList<ClassifierParameters> SMOcombinations(){
		ArrayList<ClassifierParameters> result = new ArrayList<ClassifierParameters>();

		for(int SMOcomplexity=0; SMOcomplexity<=10; SMOcomplexity++)
			for(int SMOnsn=0; SMOnsn<=2; SMOnsn++)
				for(int SMOtolerance=0; SMOtolerance<=100; SMOtolerance++)
					for(int SMOepsilonROE=0; SMOepsilonROE<=100; SMOepsilonROE++)
						for(boolean SMOfitToSVM: bList)
							for(int SMOkN=0; SMOkN<=3; SMOkN++) {
								ClassifierParameters cp = new ClassifierParameters();
								cp.SMOcomplexity = SMOcomplexity;
								cp.SMOnsn = SMOnsn;
								cp.SMOtolerance = SMOtolerance/1000d;
								cp.SMOepsilonROE = SMOepsilonROE/1000000000000d;
								cp.SMOfitToSVM = SMOfitToSVM;
								cp.SMOkN = SMOkN;
								cp.SMOseed = rnd.nextInt();
								result.add(cp);
							}
		return result;
	}
	
}
