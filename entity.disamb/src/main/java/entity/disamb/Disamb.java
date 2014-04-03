package entity.disamb;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import entity.disamb.ml.Arff;
import entity.disamb.ml.EntitiesInss;
import entity.disamb.ml.Learning;
import entity.disamb.ml.Ranking;
import entity.disamb.process.Entities;
import entity.disamb.process.Geos;
import entity.disamb.process.Operations;
import entity.disamb.process.Wikis;

public class Disamb {
	
	public static void disambiguate(String wikiFile, String geoFile){
		System.out.println("Reading processed Wiki and Geo Entities");
		Wikis wikis = new Wikis(IO.readData(wikiFile).getList());		
		Geos geos = new Geos(IO.readData(geoFile).getList());
		
		DomainBasedDisamb.disambiguate(wikis, geos);
		
	}
	
	
}
