package disRanking;

//import weka.classifiers.Classifier;
//import weka.classifiers.Evaluation;
//import weka.classifiers.functions.Logistic;
//import weka.core.Instances;
//import entity.disamb.process.*;

public class DisRanking {
	/**
	 * train each domain
	 * calculate distance
	 * rank
	 * marge the results
	 */
//	public void rank(String geoFile){
//		Geos geos = new Geos(geoFile);
//		Instances geoInss = geos.genArff("geos");
//		// build the logistic classifier
//		Classifier logisticModel= (Classifier) new Logistic();
//		try {
//			logisticModel.buildClassifier(geoInss);
//			 // Test the model
//			 Evaluation evalTrain = new Evaluation(geoInss);
//			 //evalTrain.evaluateModel(logisticModel, isT);
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			e.printStackTrace();
//		}
//	}
}
