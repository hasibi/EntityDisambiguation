package entity.disamb.ml;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.core.Instance;

public class Learning {

	public static Classifier train(EntitiesInss trainSet){
		Classifier logistic = (Classifier) new Logistic();
		try {
			logistic.buildClassifier(trainSet.getInstances());
			Evaluation eval = new Evaluation(trainSet.getInstances());
			eval.evaluateModel(logistic, trainSet.getInstances());
			System.out.println(eval.confusionMatrix().toString());
			System.out.println(eval.toSummaryString());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return logistic;
	}
	
	public static void test(Classifier model,EntitiesInss testSet){
		for(int i = 0; i < testSet.size(); i ++){
			try {
				Instance ins = testSet.getInstance(i);
				double[] probs = model.distributionForInstance(ins);
				testSet.addProb(i, probs);
				int cls = (int) model.classifyInstance(ins);
				ins.setClassValue(cls);
				
				testSet.addClass(i, ins.classAttribute().value(cls));
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
//		Evaluation eTest;
//		try {
//			eTest = new Evaluation(trainSet);
//			eTest.evaluateModel(model, testSet);
//
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			e.printStackTrace();
//		}
	}
}
