package entity.disamb;


import weka.core.Instances;
import entity.disamb.ml.Arff;
import entity.disamb.ml.Evaluation;
import entity.disamb.ml.Learning;
import entity.disamb.ml.Ranking;
import entity.disamb.process.Entities;
import entity.disamb.process.Geos;
import entity.disamb.process.Wikis;

public class Disamb {
	
	protected Wikis wikis;
	protected Geos geos;
	protected int[] gains;
	
	public Disamb(Wikis wikis, Geos geos, int[] gains){
		this.wikis = wikis;
		this.geos = geos;
		this.gains = gains;
	}
	
	/**
	 * Generate Arff file for train and test set, learn a new model 
	 * and predict the results for test set.
	 * @param trainSet
	 * @param testSet
	 * @param sameTrainTest
	 */
	protected void learn(Entities trainSet, Entities testSet, String setLabel, boolean sameTrainTest){
		//train, test, calculate score for test set
		System.out.println("Generating Arff file for train set...");
		trainSet.genArff(setLabel + ".train");
		Instances trainInss = Arff.read(setLabel + ".train");
		Instances testInss = null;
		if(! sameTrainTest){
			System.out.println("Generating Arff file for test set ...");
			testSet.genArff(setLabel + ".test");
			testInss = Arff.read(setLabel + ".test");
		}
		else
			testInss = trainInss;

		Learning.learn(trainInss, testInss, testSet);
	}
	
	/**
	 * Calculate scores, label and rank entities.
	 * @param entities
	 */
	protected void rank(Entities entities){
		Ranking ranker = new Ranking(entities);
		try {
			System.out.println("Calculating scores ...");
			ranker.scoreByProbs(gains);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("labeling ...");
		ranker.label();
		System.out.println("ranking ...");
		ranker.rank();		
	}
	
	/**
	 * Calculate confusion matrix and accuracy tables and return them as string.
	 * @param entities
	 * @param labelId
	 * @return String: Confusion Matrix + Accuracy Matrix
	 */
	protected String evaluate(Entities entities, int labelId){
		Evaluation eval = new Evaluation(entities);
		int[][] confMat = null;
		try {
			confMat = eval.confMatrix(labelId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String conf = eval.printConfMat(confMat);
		String acc = eval.printAccuracyMatrix(confMat);
		return conf + acc;
	}
	
}
