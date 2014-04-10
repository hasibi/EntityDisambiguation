package entity.disamb.ml;

import entity.disamb.process.Entities;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.core.Instance;
import weka.core.Instances;

public class Learning {
	
	/**
	 * Trains and test Instances and save all the results in tesEns.
	 * @param trainInss: Instances
	 * @param testInss: Instances
	 * @param testEns : Entities
	 * @return Entities (testEns)
	 */
	public static Entities learn(Instances trainInss, Instances testInss, Entities testEns){
		System.out.println("Training ...");
		Classifier model = Learning.train(trainInss);
		System.out.println("Testing ...");
		Learning.test(model, testInss, testEns);
		return testEns;
	}

	/**
	 * Train a Logistic model using training set.
	 * @param trainSet
	 * @return Logistic Classifier Model
	 */
	public static Classifier train(Instances trainSet){
		Classifier logistic = (Classifier) new Logistic();
		try {
			logistic.buildClassifier(trainSet);
			Evaluation eval = new Evaluation(trainSet);
			eval.evaluateModel(logistic, trainSet);
//			System.out.println(eval.confusionMatrix().toString());
//			System.out.println(eval.toSummaryString());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return logistic;
	}
	
	/**
	 * Uses the model to find prediction of test set.
	 * Adds probability of each class and also predicted label to each entity.
	 * Assigns the index of predictions and labels to the Entities
	 * @param model
	 * @param testSet
	 * @param entities
	 */
	public static void test(Classifier model,Instances testSet, Entities entities){
		double[] probs = null; // to save the probability of assigning each entity to the classes
		int lastIndex = entities.getFeatureSize() - 1;  
		for(int i = 0; i < testSet.numInstances(); i ++){
			try { //*** Calculating probabilities
				Instance ins = testSet.instance(i);
				probs = model.distributionForInstance(ins);
				entities.probablities = new int[probs.length];
				for(int j = 0; j<probs.length; j++){//adds probabilities to each entity
					entities.getEntity(i).addFeature(Double.toString(probs[j])); 
				}
				
				//*** Calculating predicted labels
				int cls = (int) model.classifyInstance(ins);
				ins.setClassValue(cls);
				entities.getEntity(i).addFeature(ins.classAttribute().value(cls)); // adds prediction to each entity
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
		// *** Assigning index of prediction and probabilities to the list of Entities
		entities.probablities = new int[probs.length];
		for(int i = 0; i < entities.probablities.length; i ++){ // assign index of probabilities
			entities.probablities[i] = ++ lastIndex;
		}
		entities.pred = ++ lastIndex; // assign the index of prediction
		
	}
}
