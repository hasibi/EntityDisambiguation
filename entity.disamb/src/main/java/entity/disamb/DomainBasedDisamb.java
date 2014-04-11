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
import entity.disamb.process.Geos;
import entity.disamb.process.Operations;
import entity.disamb.process.Wikis;

public class DomainBasedDisamb {
	private Wikis wikis;
	private Geos geos;
	int[] gains;
	
	public DomainBasedDisamb(Wikis wikis, Geos geos, int[] gains){
		this.wikis = wikis;
		this.geos = geos;
		this.gains= gains;		
	}
	
	public void disambiguate(){
		System.out.println("=== Learning Wiki Entities ===");
		learn(wikis,"wikis");

		System.out.println("=== Learning Geo Entities ===");
		learn(geos, "geos");
		
		System.out.println("=== Merging Wiki and Geo entities ===");
		Entities allEns = Operations.mergeDomain(wikis, geos);
		
		rank(allEns);
		
		System.out.println("=== Evaluating all entities ===");
		System.out.println("Same training and test set ...");
		
		String evalStr =  "Evaluation metrics for same training and test set: \n";
		evalStr += evaluate(allEns, wikis.label);
		
		System.out.println("10-fold Cross Validation");
		ArrayList<String> names = allEns.getNames();
		Collections.shuffle(names);
		try {
			evalStr +=  "Evaluation metric for 10-fold cross validation: \n";
			evalStr += crossValidate(names,10);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Writing evaluation results ...");
		IO.writeToFile(evalStr, "evaluations");
		
		System.out.println("=== Writing prediction results for same train and test set===");
		allEns = Operations.sortByGroup(allEns, allEns.name, allEns.score, false);
		IO.writeData(allEns, "allEns.rank");
	}
	
	/**
	 * Generate Arff file for train and test set, learn a new model 
	 * and predict the results for test set.
	 * @param trainSet
	 * @param testSet
	 */
	private void learn(Entities trainSet, Entities testSet, String setLabel){
		//train, test, calculate score for test set
		System.out.println("Generating Arff file for train set...");
		trainSet.genArff(setLabel + ".train");
		Instances trainInss = Arff.read(setLabel + ".train");
		
		System.out.println("Generating Arff file for test set ...");
		testSet.genArff(setLabel + ".test");
		Instances testInss = Arff.read(setLabel + ".test");
		
		Learning.learn(trainInss, testInss, testSet);
	}
	
	private void learn(Entities trainSet, String setLabel){
		System.out.println("Generating Arff file ...");
		trainSet.genArff(setLabel + ".train");
		Instances trainInss = Arff.read(setLabel + ".train");
		Learning.learn(trainInss, trainInss, trainSet);

	}
	
	/**
	 * Performs n-fold cross-validation and prints out evaluation results.
	 * @param folds
	 * @throws Exception 
	 */
	private String crossValidate(ArrayList<String> shuffledNames, int folds) throws Exception{
		if(folds <= 1)
			throw new Exception("Number of folds must be greater than 1.");
		Validation evalWikis = new Validation(wikis, shuffledNames);
		Validation evalGeos = new Validation(geos, shuffledNames);
		Entities allTestEns = new Entities();
		for (int i = 0; i < folds; i++){
			// learning Wiki Entities		
			System.out.println("Learning Wiki entities (Iteration " + i + ") ...");
			Wikis trainWiki = new Wikis(evalWikis.trainCV(folds, i));
			Wikis testWiki = new Wikis(evalWikis.testCV(folds, i));
			learn(trainWiki, testWiki, "cv/wikis.CV"+i);
			
			//Learning Geo entities
			System.out.println("Learning Geo entities (Iteration " + i + ") ...");
			Geos trainGeo = new Geos(evalGeos.trainCV(folds, i));
			Geos testGeo = new Geos(evalGeos.testCV(folds, i));
			learn(trainGeo, testGeo, "cv/geso.CV" + i);
			
			//merging Wiki and Geo entities
			System.out.println("Merging geo and wiki test set (Iteration " + i + ") ...");
			Entities mergedEns = Operations.mergeDomain(testWiki, testGeo);
			rank(mergedEns);
			allTestEns.addAll(mergedEns);			
		}
		// Evaluating all test sets
		int labelId = wikis.label;
		 return evaluate(allTestEns, labelId);
	}
	
	/**
	 * Calculate scores, label and rank entities.
	 * @param entities
	 */
	private void rank(Entities entities){
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
	private String evaluate(Entities entities, int labelId){
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
