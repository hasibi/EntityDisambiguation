package entity.disamb;

import java.util.ArrayList;
import java.util.Collections;

import weka.attributeSelection.AttributeSelection;
import weka.core.Instances;
import entity.disamb.ml.Arff;
import entity.disamb.ml.Evaluation;
import entity.disamb.ml.Learning;
import entity.disamb.ml.PCA;
import entity.disamb.ml.Ranking;
import entity.disamb.ml.Validation;
import entity.disamb.process.Entities;
import entity.disamb.process.Entity;
import entity.disamb.process.Geos;
import entity.disamb.process.Operations;
import entity.disamb.process.Wikis;

public class PCABasedDisamb {
	private Wikis wikis;
	private Geos geos;
	int[] gains;
	
	public PCABasedDisamb(Wikis wikis, Geos geos, int[] gains){
		this.wikis = wikis;
		this.geos = geos;
		this.gains= gains;		
	}
	
	public void disambiguate(){
		System.out.println("=== Calculating PCA of Wiki Entities ===");
		Wikis cpoyWikis = new Wikis (wikis.clone());
		Geos copyGeos = new Geos(geos.clone());
		addSumurizedWikiPop(cpoyWikis, cpoyWikis, "pca/wikis", true);
		addSumurizedGeoPop(copyGeos);
		System.out.println("=== Merging Wiki and Geo Entities ===");
		Entities mergedEns = Operations.mergeDomain(cpoyWikis, copyGeos);
		System.out.println("=== Learning all Entities ===");
		learn(mergedEns, mergedEns,"wikiGeo", true);
		rank(mergedEns);
		
		System.out.println("=== Evaluating all Entities ===");
		System.out.println("Same training and test set ...");
		String evalStr =  "Evaluation metrics for same training and test set: \n";
		evalStr += evaluate(mergedEns, wikis.label);
		
		System.out.println("10-fold Cross Validation");
		ArrayList<String> names = mergedEns.getNames();
		Collections.shuffle(names);
		try {
			evalStr +=  "Evaluation metric for 10-fold cross validation: \n";
			evalStr += crossValidate(names,10);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Writing evaluation results ...");
		IO.writeToFile(evalStr, "PCABasedEvaluations");
		
		System.out.println("=== Writing prediction results for same train and test set===");
		mergedEns = Operations.sortByGroup(mergedEns, mergedEns.name, mergedEns.score, false);
		IO.writeData(mergedEns, "allEns.pca.rank");
	}
	
	/**
	 * gets wcpop, wpop, epop values of wiki entities,
	 * learns PCA for these features and 
	 * adds the first Pca feature to the Wikis entities as the last feature.
	 */
	private void addSumurizedWikiPop(Wikis trainSet, Wikis testSet, String setLabel, boolean sameTrainTest){
		System.out.println("Extracting popularity features ...");
		trainSet.genArff(setLabel+".pca.train", new int[] {wikis.wcpop, wikis.wpop, wikis.epop, wikis.label},
				new String[] {"wcpop", "wpop", "epop", "label"});
		Instances trainInss = Arff.read(setLabel+".pca.train");
		
		System.out.println("PCA learning ...");
		AttributeSelection selector = PCA.learnPCA(trainInss);
		
		System.out.println("Geting PCA feature for train set ...");
		Instances transformedtrain;
		try {
			transformedtrain = PCA.tarnsform(trainInss, selector);
			addPCAfeature(trainSet, transformedtrain);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Generate test Instances
		if(!sameTrainTest){
			System.out.println("Geting PCA feature for test set ...");
			testSet.genArff(setLabel+".pca.test", new int[] {wikis.wcpop, wikis.wpop, wikis.epop, wikis.label},
					new String[] {"wcpop", "wpop", "epop", "label"});
			Instances testInss = Arff.read(setLabel+".pca.test");
			Instances transformedtest;
			try {
				transformedtest = PCA.tarnsform(testInss, selector);
				addPCAfeature(testSet, transformedtest);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	}
	private void addPCAfeature(Entities ens, Instances inss){
		double[] results = inss.attributeToDoubleArray(0);
		for(int i = 0; i < ens.size(); i++){
			Entity en = ens.getEntity(i);
			en.addFeature(Double.toString(results[i]));
		}	
	}
	
	/**
	 * adds epop as the last feature of Geo entities
	 */
	private void addSumurizedGeoPop(Geos set){
		for(int i = 0; i < set.size(); i++){
			Entity en = set.getEntity(i);
			en.addFeature(en.getFeature(set.epop));
		}	
	}
	
	/**
	 * generate .arrf file from entity set, read arff file, train and test instances
	 * @param trainSet
	 * @param testSet
	 * @param setLabel
	 */
	private void learn(Entities trainSet, Entities testSet, String setLabel, boolean sameTrainTest){
		//train, test, calculate score for test set
		System.out.println("Generating Arff file for train set...");
		trainSet.genArff(setLabel + ".train", new int[] {trainSet.getFeatureSize()-1, wikis.cov, wikis.label}, new String[]{"pop", "cov", "label"});
		Instances trainInss = Arff.read(setLabel + ".train");
		
		Instances testInss = null;
		if(! sameTrainTest){
			System.out.println("Generating Arff file for test set ...");
			testSet.genArff(setLabel + ".test", new int[] {testSet.getFeatureSize()-1, wikis.cov, wikis.label}, new String[]{"pop", "cov", "label"});
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
	
	/**
	 * Performs n-fold cross-validation and prints out evaluation results.
	 * @param folds
	 * @throws Exception 
	 */
	private String crossValidate(ArrayList<String> shuffledNames, int folds) throws Exception{
		if(folds <= 1)
			throw new Exception("Number of folds must be greater than 1.");
		Entities allTestEns = new Entities();
		for (int i = 0; i < folds; i++){
			// learning Wiki Entities
			Validation evalWikis = new Validation(wikis, shuffledNames);
			Validation evalGeos = new Validation(geos, shuffledNames);
			System.out.println("PCA transformation for wiki entities (Iteration " + i + ") ...");
			Wikis trainWiki = new Wikis(evalWikis.trainCV(folds, i)); 
			Wikis testWiki = new Wikis(evalWikis.testCV(folds, i));
			addSumurizedWikiPop(trainWiki, testWiki, "pca/wikis.CV"+i, false);

			Geos trainGeo = new Geos(evalGeos.trainCV(folds, i));
			Geos testGeo = new Geos(evalGeos.testCV(folds, i));
			addSumurizedGeoPop(trainGeo);
			addSumurizedGeoPop(testGeo);
			
			System.out.println("Merging Wiki and Geo entities (train set) ...");
			Entities mergedTrainEns = Operations.mergeDomain(trainWiki, trainGeo);
			System.out.println("Merging Wiki and Geo entities (test set) ...");
			Entities mergedTestEns = Operations.mergeDomain(testWiki, testGeo);
			
			System.out.println("Learning merged Wiki and Geo entities (Iteration " + i + ") ...");
			learn(mergedTrainEns, mergedTestEns, "cv/wikiGeo.CV"+i, false);
			rank(mergedTestEns);
			
			allTestEns.addAll(mergedTestEns);			
		}
		// Evaluating all test sets
		int labelId = wikis.label;
		String str = evaluate(allTestEns, labelId);
		return str;
	}
}
