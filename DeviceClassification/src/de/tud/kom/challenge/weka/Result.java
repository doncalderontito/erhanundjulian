package de.tud.kom.challenge.weka;

import java.io.Serializable;
import java.util.Vector;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import de.tud.kom.challenge.processors.FeatureProcessor;

public class Result implements Serializable {

	/**
	 * generated
	 */
	private static final long serialVersionUID = -873225036811386810L;

	// private transient final Evaluation evaluation; // not serializable

	private final Vector<FeatureProcessor> processors;
	private final Classifier classifier;
	@SuppressWarnings("unused")
	private final double correctAbsolut, correctRelative, incorrectAbsolut,
			incorrectRelative;

	private final String summaryString;

	// private String matrixString;

	public Result(final Vector<FeatureProcessor> processors,
			final Classifier c, final Evaluation eval) {
		this.processors = processors;
		this.classifier = c;
		// this.evaluation = eval;

		this.correctAbsolut = eval.correct();
		this.correctRelative = eval.pctCorrect();
		this.incorrectAbsolut = eval.incorrect();
		this.incorrectRelative = eval.pctIncorrect();
		this.summaryString = eval.toSummaryString();
		// try {
		// this.matrixString = eval.toMatrixString();
		// } catch(final Exception e) {
		// e.printStackTrace();
		// this.matrixString = "";
		// }
	}

	public boolean isBetterThan(final Result otherResult) {
		final double correctAbsolutOther = otherResult.correctAbsolut;

		final boolean correctAbsoluteCompare = this.correctAbsolut > correctAbsolutOther;

		return correctAbsoluteCompare;
	}

	@Override
	public String toString() {
		final String[] summary = this.summaryString.split("\n");

		String result = "";// this.matrixString + "\n";
		result += "Cross validation result:";
		result += "\n* " + summary[1];
		result += "\n* " + summary[2];
		result += "\nProcessors:\n";
		for (final FeatureProcessor fp : this.processors) {
			result += fp.getClass() + "\n";
		}
		result += "\nClassifier: " + this.classifier.getClass();

		return result;
	}
}
