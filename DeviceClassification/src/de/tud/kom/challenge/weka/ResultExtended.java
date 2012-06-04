package de.tud.kom.challenge.weka;

import java.io.Serializable;
import java.util.Arrays;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;

public class ResultExtended implements Serializable {

	private static final long serialVersionUID = 6593260731964314290L;
	
	private String delimiter = ";";
	private int[] combination;
	private Classifier classifier;
	private String classifierParams;
	private int newClassifier;
	private double correctAbsolut, correctRelative;
	private long time;

	public ResultExtended(int[] combination, int newClassifier, Classifier c, String classifierParams, Evaluation eval, long time) {
		this.combination = combination;
		this.classifier = c;
		this.time = time;
		this.classifierParams = classifierParams;
		this.newClassifier = newClassifier;
		
		this.correctAbsolut = eval.correct();
		this.correctRelative = eval.pctCorrect();
	}

	public boolean isBetterThan(ResultExtended otherResult) {
		final double correctAbsolutOther = otherResult.correctAbsolut;
		final boolean correctAbsoluteCompare = this.correctAbsolut > correctAbsolutOther;
		return correctAbsoluteCompare;
	}

	public String toString() {
		String result = "";
		result += Arrays.toString(this.combination) + this.delimiter;
		result += this.classifier.getClass().getSimpleName() + this.delimiter;
		result += this.classifierParams + this.delimiter;
		result += this.correctRelative;
		return result;
	}
	
	public double getAccuracy() {
		return correctRelative;
	}

	public int getClassifierIndex() {
		return newClassifier;
	}
	
	public String getClassifierName() {
		return classifier.getClass().getSimpleName();
	}
	
	public long getTime() {
		return time;
	}
	
	public String getParams() {
		return classifierParams;
	}
}