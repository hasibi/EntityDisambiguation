package entity.disamb;

import weka.classifiers.Classifier;
import weka.core.Instances;
import entity.disamb.ml.Arff;
import entity.disamb.ml.Evaluation;
import entity.disamb.ml.Learning;
import entity.disamb.process.IO;
import entity.disamb.process.Wikis;

public class testEval {
	
	public static void main(String[] args){
		// calc labels and probabilities
		System.out.println("Reading processed Wiki Entities");
		Wikis wikis = new Wikis(IO.readData("./output/wikis.log.norm.txt").getList());
		calcScore(wikis);
		Evaluation eval = new Evaluation(wikis);
		wikis.newPred = wikis.pred; // compare Weka labels with original labels
		IO.writeData(wikis, "wiki.test");
		int[][] confMat = null;
		try {
			confMat = eval.confMatrix(wikis.label);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Calculate Confusion Matrix
		//Results should be the same as Weka results
		eval.printConfMat(confMat);
		
		// calculate accuracy matrix
		eval.printAccuracyMatrix(confMat);
	}

	private static void calcScore(Wikis wikis){
		System.out.println("Generating Arff file for Wiki entities ...");
		wikis.genArff("wikis");
		System.out.println("Training Wiki entities ...");
		Instances wikiInss = Arff.read("wikis");
		Classifier model = Learning.train(wikiInss);
		System.out.println("Testing Wiki entities ...");
		Learning.test(model, wikiInss, wikis);
				
	}
}
