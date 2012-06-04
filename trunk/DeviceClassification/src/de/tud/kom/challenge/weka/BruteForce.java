package de.tud.kom.challenge.weka;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import de.tud.kom.challenge.arff.FileMapper;
import de.tud.kom.challenge.util.ClassifierParametersCombinations;
import de.tud.kom.challenge.util.FileUtil;
import de.tud.kom.challenge.util.ParalellTasks;
import de.tud.kom.challenge.util.Task;

public class BruteForce {
	
	private final static Logger log = Logger.getLogger(BruteForce.class.getSimpleName());

	/**
	 * Brute force approach to classifier testing
	 * @param filename the input ARFF file with the training data
	 */
	public void analyze(String filename) {
		
		// Files
		final String inputArff = filename;
		final String outFile = FileMapper.optimumPath + File.separator + FileMapper.optimumCSV;
		final String logFile = outFile.replace("csv", "log");
		
		// Constants
		final ArrayList<ResultExtended> resultList = new ArrayList<ResultExtended>();
		final MachineLearner ml = new MachineLearner(inputArff);
		final List<Classifier> classifiers = ml.getAllCompatibleClassifiers();
		final double[][] result = ml.attributeSelection(); // Iterate over attribute order - get it only once here

		// Clear files
		String header = "Feature Combination ; Classifier ; Parameter set ; Accuracy ; Time (milliseconds) \n";
		FileUtil.simpleWriteToDisc(outFile, header, false);
		FileUtil.simpleWriteToDisc(outFile.replace("csv", "mapping"), ml.getAttrMapping(), false);
		FileUtil.simpleWriteToDisc(outFile.replace("csv", "log"), "", false);

		try { // to catch out of memory errors 
			final Task<Classifier, List<ResultExtended>> task = new Task<Classifier, List<ResultExtended>>() {
				public List<ResultExtended> calculate(Classifier classifier) {
					List<ResultExtended> reList = new ArrayList<ResultExtended>();
					
					// Get all possible options for the given classifier
					List<ClassifierParameters> cpList = ClassifierParametersCombinations.
								getAllClassifierParamsCombinations(classifier, 
								ml.getNumAttributes(), ml.getNumInstances());
					int cpListSize = cpList.size();
	
					log.info("Evaluating "+cpListSize+" parameter constellation(s) for "+classifier.getClass().getSimpleName());
					FileUtil.simpleWriteToDisc(
							logFile,
							"Analyzing "+classifier.getClass().getSimpleName() +
							"with "+cpListSize+" parameter sets\n",
							true);
					
					// Iterate over parameter constellation
					for(int m=0; m<cpListSize; m++) {
						ClassifierParameters cp = cpList.get(m);
						
						for (int attr=0;attr<result.length;attr++) {
							
							// We define the attributes to remove at this point...
							int[] combination = new int[attr];
							for (int atr=0;atr<combination.length;atr++) {
								double last = result[result.length-1-atr][0];
								combination[atr] = new Double(last).intValue();
							}
							
							long time = System.currentTimeMillis();
							//log.info("Removing attributes "+Arrays.toString(combination));
							Evaluation ev = ml.bruteForceCrossValidation(combination, classifier, cp);
							time = System.currentTimeMillis() - time;
								
							if(ev != null) {
								reList.add(new ResultExtended(combination, combination[combination.length-1], classifier, cp.toString(classifier), ev, time));
								String resulting = Arrays.toString(combination) +";"
								+ classifier.getClass().getSimpleName() +";"
								+ cp.toString(classifier) + ";"
								+ (""+ev.pctCorrect()).replace(".",",") + ";" 
								+ time + "\n";
								FileUtil.simpleWriteToDisc(logFile, resulting, true);
							} //else log.info("No results");
						}
					}
					System.gc();
					return reList;
				}
			}; // Task definition
				
			for (List<ResultExtended> rl:ParalellTasks.map(classifiers, task, null)) {
				for(ResultExtended r: rl) {
					addSorted(resultList, r);
				}
			}
		} catch (OutOfMemoryError ex) {
			ex.printStackTrace();
		}
		
		StringWriter temp = new StringWriter();
		for(ResultExtended r:resultList) {
			if (r.getAccuracy() < 80) break;
			temp.append(r +"\n");
		}
		FileUtil.simpleWriteToDisc(outFile, temp.toString(), true);
	}
	
	private synchronized void addSorted(ArrayList<ResultExtended> resultList, ResultExtended result) {
		for (int i = 0; i < resultList.size(); i++) {
			if (result.isBetterThan(resultList.get(i))) {
				resultList.add(i, result);
				return;
			}
		}
		resultList.add(result);
	}	
}

