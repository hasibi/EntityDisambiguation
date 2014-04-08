package entity.disamb;

import weka.classifiers.Classifier;
import weka.core.Instances;
import entity.disamb.ml.Arff;
import entity.disamb.ml.Evaluation;
import entity.disamb.ml.Learning;
import entity.disamb.ml.Ranking;
import entity.disamb.process.Entities;
import entity.disamb.process.Geos;
import entity.disamb.process.Operations;
import entity.disamb.process.Wikis;

public class DomainBasedDisamb {
	
	public static void disambiguate(Wikis wikis, Geos geos){
		calcScore(wikis);
		calcScore(geos);
		
		 // **** concatenate entities and predictions **** 
//		Wikis wikiPreds = new Wikis(wikiInss.concatEntityPred().getList());
//		System.out.println("Writing prediction results for wiki entities ...");
//		IO.writeData(wikiPreds, "wikis.preds");
//		
//		Geos geoPreds = new Geos(geoInss.concatEntityPred().getList());
//		System.out.println("Writing prediction results for Geo entities ...");
//		IO.writeData(geoPreds, "geos.preds");
		
		// merging
		Entities allEns = Operations.mergeDomain(wikis, geos);
		// ranking and labeling all entities
		Ranking rank = new Ranking(allEns);
		rank.label();
		rank.rank();
		
		// Evaluationg results
		Evaluation eval = new Evaluation(allEns);
		int labelId = wikis.label;
		int[][] confMat = null;
		try {
			confMat = eval.confMatrix(labelId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		eval.printConfMat(confMat);
		eval.printAccuracyMatrix(confMat);
		
		System.out.println("Writing prediction results for all entities ...");
		allEns = Operations.sortByGroup(allEns, allEns.name, allEns.score, false);
		IO.writeData(allEns, "allEns.rank");
	}
	
	private static void calcScore(Geos geos){
		//train, test, calculate score
		System.out.println("Generating Arff file for Geo entities ...");
		geos.genArff("geos");
		System.out.println("Training Geo entities ...");
		Instances geoInss = Arff.read("geos");
		Classifier model = Learning.train(geoInss);
		System.out.println("Testing Geo entities ...");
		Learning.test(model, geoInss, geos);
//		Entities geoPreds = null;
		System.out.println("Calculating scores for Geo entities ...");
		try {
			Ranking rank = new Ranking(geos);
			rank.scoreByProbs(new int[] {9,4,1});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void calcScore(Wikis wikis){
		System.out.println("Generating Arff file for Wiki entities ...");
		wikis.genArff("wikis");
		System.out.println("Training Wiki entities ...");
		Instances wikiInss = Arff.read("wikis");
		Classifier model = Learning.train(wikiInss);
		System.out.println("Testing Wiki entities ...");
		Learning.test(model, wikiInss, wikis);
		System.out.println("Calculating scores for wiki entities ...");
		try {
			Ranking rank = new Ranking(wikis);
			rank.scoreByProbs(new int[] {9,4,1});
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
