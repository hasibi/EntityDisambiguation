package entity.disamb;

import java.util.ArrayList;
import java.util.Collections;

import entity.disamb.ml.Validation;
import entity.disamb.process.Entities;
import entity.disamb.process.Geos;
import entity.disamb.process.IO;
import entity.disamb.process.Operations;
import entity.disamb.process.Wikis;

public class OneDomainDisamb extends Disamb {
	
	public OneDomainDisamb(Wikis wikis, Geos geos, int[] gains) {
		super(wikis, geos, gains);
	}

	
	public void disambiguate(){
		System.out.println("=== Learning Wiki Entities ===");
		learn(wikis, wikis, "wikis", true);
		rank(wikis);	
		String evalStrWikis =  "Evaluation metrics for same training and test set: \n";
		evalStrWikis += evaluate(wikis, wikis.label);

		System.out.println("=== Learning Geo Entities ===");
		learn(geos, geos,"geos", true);
		rank(geos);
		String evalStrGeos =  "Evaluation metrics for same training and test set: \n";
		evalStrGeos += evaluate(geos, geos.label);		
		
		
		System.out.println("10-fold Cross Validation");
		ArrayList<String> names = wikis.getNames();
		Collections.shuffle(names);
		String[] nfoldEvals = null;
		try {
			nfoldEvals = crossValidate(names,10);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		evalStrWikis +=  "Evaluation metric for 10-fold cross validation: \n";
		evalStrWikis += nfoldEvals[0];
		
		evalStrGeos +=  "Evaluation metric for 10-fold cross validation: \n";
		evalStrGeos += nfoldEvals[1];
		
		System.out.println("Writing evaluation results ...");
		IO.writeToFile(evalStrWikis, "wikis.eval.txt");
		IO.writeToFile(evalStrGeos, "geos.eval.txt");


		System.out.println("=== Writing prediction results Wiki entities ===");
		wikis = new Wikis(Operations.sortByGroup(wikis, wikis.name, wikis.score, false));
		IO.writeData(wikis, "wikis.preds");
		
		System.out.println("=== Writing prediction results Geo entities ===");
		geos = new Geos(Operations.sortByGroup(geos, geos.name, geos.score, false));
		IO.writeData(geos, "geos.preds");
		
//		System.out.println("=== Writing prediction results for same train and test set===");
//		allEns = Operations.sortByGroup(allEns, allEns.name, allEns.score, false);
//		IO.writeData(allEns, outputFile + "preds");
	}
	
	/**
	 * Performs n-fold cross-validation and prints out evaluation results.
	 * @param folds
	 * @return String[]: the first element is for Wikis and second for Geos
	 * @throws Exception 
	 */
	private String[] crossValidate(ArrayList<String> shuffledNames, int folds) throws Exception{
		if(folds <= 1)
			throw new Exception("Number of folds must be greater than 1.");
		Validation evalWikis = new Validation(wikis, shuffledNames);
		Validation evalGeos = new Validation(geos, shuffledNames);
		Entities allTestWikis = new Entities();
		Entities allTestGeos = new Entities();
		for (int i = 0; i < folds; i++){
			// learning Wiki Entities		
			System.out.println("Learning Wiki entities (Iteration " + i + ") ...");
			Wikis trainWiki = new Wikis(evalWikis.trainCV(folds, i));
			Wikis testWiki = new Wikis(evalWikis.testCV(folds, i));
			learn(trainWiki, testWiki, "cv/wikis.CV" +i, false);
			rank(testWiki);
			allTestWikis.addAll(testWiki);
			
			//Learning Geo entities
			System.out.println("Learning Geo entities (Iteration " + i + ") ...");
			Geos trainGeo = new Geos(evalGeos.trainCV(folds, i));
			Geos testGeo = new Geos(evalGeos.testCV(folds, i));
			learn(trainGeo, testGeo, "cv/geos.CV" + i, false);
			rank(testGeo);
			allTestGeos.addAll(testGeo);			
		}
		// Evaluating all test sets
		String[] evals = new String[2];
		evals[0] = evaluate(allTestWikis, wikis.label);
		evals[1] = evaluate(allTestGeos, geos.label);
		return evals;
	}
	
}
