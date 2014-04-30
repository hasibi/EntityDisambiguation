package entity.disamb;

import java.util.ArrayList;
import java.util.Collections;

import entity.disamb.ml.Validation;
import entity.disamb.process.Entities;
import entity.disamb.process.Geos;
import entity.disamb.process.IO;
import entity.disamb.process.Operations;
import entity.disamb.process.Wikis;

public class LateFusionDisamb extends Disamb {
	
	public LateFusionDisamb(Wikis wikis, Geos geos, int[] gains){
		super(wikis, geos, gains);		
	}
	
	public void disambiguate(String outputFile){
		System.out.println("=== Learning Wiki Entities ===");
		learn(wikis, wikis, "wikis", true);

		System.out.println("=== Learning Geo Entities ===");
		learn(geos, geos,"geos", true);
		
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
		IO.writeToFile(evalStr, outputFile + ".eval.txt");
		
		System.out.println("=== Writing prediction results for same train and test set===");
		allEns = Operations.sortByGroup(allEns, allEns.name, allEns.score, false);
		IO.writeData(allEns, outputFile + ".preds");
	}
	

//	
//	private void learn(Entities trainSet, String setLabel){
//		System.out.println("Generating Arff file ...");
//		trainSet.genArff(setLabel + ".train");
//		Instances trainInss = Arff.read(setLabel + ".train");
//		Learning.learn(trainInss, trainInss, trainSet);
//
//	}
	
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
			learn(trainWiki, testWiki, "cv/wikis.CV"+i, false);
			
			//Learning Geo entities
			System.out.println("Learning Geo entities (Iteration " + i + ") ...");
			Geos trainGeo = new Geos(evalGeos.trainCV(folds, i));
			Geos testGeo = new Geos(evalGeos.testCV(folds, i));
			learn(trainGeo, testGeo, "cv/geso.CV" + i, false);
			
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
}
