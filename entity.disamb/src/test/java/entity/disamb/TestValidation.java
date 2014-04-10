package entity.disamb;

import java.util.ArrayList;
import java.util.Collections;

import weka.core.Instances;
import entity.disamb.ml.Arff;
import entity.disamb.ml.Evaluation;
import entity.disamb.ml.Learning;
import entity.disamb.ml.Ranking;
import entity.disamb.ml.Validation;
import entity.disamb.process.Entities;
import entity.disamb.process.Wikis;

public class TestValidation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Wikis wikis = new Wikis(IO.readData("./src/test/resources/testWikis.txt").getList());
		System.out.println("Generating Arff file for Wiki entities ...");
		
		ArrayList<String> names = wikis.getNames();
		Collections.shuffle(names);
		
		crossValidate(wikis, names, 4);
//		wikis.genArff("wikis");
//		System.out.println("Training Wiki entities ...");
//		Instances wikiInss = Arff.read("wikis");
	}
	
	/**
	 * Performs n-fold cross-validation and prints out evaluation results.
	 * @param folds
	 */
	private static void crossValidate(Entities geos,ArrayList<String> shuffledNames, int folds){
		Validation eval = new Validation(geos, shuffledNames);
		ArrayList<String> names = eval.getShuffledNames();
		writeNames(names);
		Entities allTestEns = new Entities();
		for (int i = 0; i < folds; i++){
			//Learning Geo entities
			Wikis trainGeo = new Wikis(eval.trainCV(folds, i));
			IO.writeData(trainGeo, "geos.CV.train" + i);
			Wikis testGeo = new Wikis(eval.testCV(folds, i));
			IO.writeData(testGeo, "geos.CV.test" + i);
			learn(trainGeo, testGeo, "geos.CV" + i);
			
			//merging Wiki and Geo entities
			//Entities mergedEns = Operations.mergeDomain(wikis, geos);
			rank(testGeo, new int[]{9,4,1});
			allTestEns.addAll(testGeo);			
		}
		// Evaluating all test sets
		int labelId = 7;
		evaluate(allTestEns, labelId);
	}
	
	/**
	 * Generate Arff file for train and test set, learn a new model 
	 * and predict the results for test set.
	 * @param trainSet
	 * @param testSet
	 */
	private static void learn(Entities trainSet, Entities testSet, String setLabel){
		//train, test, calculate score for test set
		trainSet.genArff(setLabel + ".train");
		
		Instances trainInss = Arff.read(setLabel + ".train");
		
		testSet.genArff(setLabel + ".test");
		Instances testInss = Arff.read(setLabel + ".test");
		
		Learning.learn(trainInss, testInss, testSet);
	}
	/**
	 * Calculate scores, label and rank entities.
	 * @param entities
	 */
	private static  void rank(Entities entities, int[] gains){
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
	 * Calculate confusion matrix and accuracy tables.
	 * @param entities
	 * @param labelId
	 */
	private static void evaluate(Entities entities, int labelId){
		Evaluation eval = new Evaluation(entities);
		int[][] confMat = null;
		try {
			confMat = eval.confMatrix(labelId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		eval.printConfMat(confMat);
		eval.printAccuracyMatrix(confMat);
	}
	
	private static void writeNames(ArrayList<String> names){
		String out = "";
		for(String name: names)
			out += name + "\n";
		IO.writeToFile(out, "shuffledNames");
	}
}
