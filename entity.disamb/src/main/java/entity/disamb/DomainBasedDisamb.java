package entity.disamb;

import weka.classifiers.Classifier;
import entity.disamb.ml.Arff;
import entity.disamb.ml.EntitiesInss;
import entity.disamb.ml.Learning;
import entity.disamb.ml.Ranking;
import entity.disamb.process.Entities;
import entity.disamb.process.Geos;
import entity.disamb.process.Operations;
import entity.disamb.process.Wikis;

public class DomainBasedDisamb {
	
	public static void disambiguate(Wikis wikis, Geos geos){
		EntitiesInss wikiInss = calcScore(wikis);
		EntitiesInss geoInss = calcScore(geos);
		
		 // **** concatenate entities and predictions **** 
		Wikis wikiPreds = new Wikis(wikiInss.concatEntityPred().getList());
		System.out.println("Writing prediction results for wiki entities ...");
		IO.writeData(wikiPreds, "wikis.preds");
		
		Geos geoPreds = new Geos(geoInss.concatEntityPred().getList());
		System.out.println("Writing prediction results for Geo entities ...");
		IO.writeData(geoPreds, "geos.preds");
		
		// merging
		Entities allEns = Operations.mergeDomain(wikiPreds, geoPreds);
		allEns = Ranking.rank(allEns);
		System.out.println("Writing prediction results for all entities ...");
		IO.writeData(allEns, "allEns.rank");

	}
	
	private static EntitiesInss calcScore(Geos geos){
		//train, test, calculate score
		System.out.println("Generating Arff file for Geo entities ...");
		geos.genArff("geos");
		System.out.println("Training Geo entities ...");
		EntitiesInss geoInss = Arff.read("geos", geos);
		Classifier model = Learning.train(geoInss);
		System.out.println("Testing Geo entities ...");
		Learning.test(model, geoInss);
//		Entities geoPreds = null;
		System.out.println("Calculating scores for Geo entities ...");
		try {
			Ranking.scoreByProbs(geoInss, new int[] {9,4,1});
//			System.out.println("ranking Geo entities ...");
//			geoPreds = Ranking.rank(geoInss);
			//Operations.sort(geoPreds, geoPreds.getFeatureSize()-1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return geoInss;
	}
	
	private static EntitiesInss calcScore(Wikis wikis){
		System.out.println("Generating Arff file for Wiki entities ...");
		wikis.genArff("wikis");
		System.out.println("Training Wiki entities ...");
		EntitiesInss wikiInss = Arff.read("wikis", wikis);
		Classifier model = Learning.train(wikiInss);
		System.out.println("Testing Wiki entities ...");
		Learning.test(model, wikiInss);
		System.out.println("Calculating scores for wiki entities ...");
		try {
			Ranking.scoreByProbs(wikiInss, new int[] {9,4,1});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wikiInss;
		
	}
}
