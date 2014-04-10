package entity.disamb.ml;

import entity.disamb.process.Entities;
import entity.disamb.process.Entity;

public class Evaluation {

	private Entities entities;
	
	public Evaluation(Entities ens){
		entities = ens;
	}
	
	/**
	 * Calculate confusion matrix for three classes, where the label of each class is "1", "2" or "3".
	 * Compare entites.newPred with entties.label
	 * @param labelId
	 * @return rows are real labels and columns are predictions.
	 * @throws Exception 
	 */
	public int[][] confMatrix(int labelId) throws Exception{
		if(labelId == 0)
			throw new Exception("Label Id cannot be 0.");
		
		int[][] confMat = new int[3][3];
		
		int pId = entities.newPred;
		for(Entity en : entities.getList()){
			if(en.getFeature(labelId).matches("1")){
				if(en.getFeature(pId).matches("1"))
					confMat[0][0]++;
				else if(en.getFeature(pId).matches("2"))
					confMat[0][1] ++;
				else 
					confMat[0][2]++;
			}
			else if (en.getFeature(labelId).matches("2")){
				if(en.getFeature(pId).matches("1"))
					confMat[1][0]++;
				else if(en.getFeature(pId).matches("2"))
					confMat[1][1]++;
				else 
					confMat[1][2]++;
			}
			else if (en.getFeature(labelId).matches("3")){
				if(en.getFeature(pId).matches("1"))
					confMat[2][0]++;
				else if(en.getFeature(pId).matches("2"))
					confMat[2][1]++;
				else 
					confMat[2][2]++;
			}
		}
		return confMat;	
	}
	
	public double[] precision(int[][] confMat){
		double[] precisions = new double[confMat[0].length];
		for(int i = 0; i< confMat.length; i++){ // row or real labels
			int s = 0;
			for(int j = 0; j< confMat[0].length; j++) // column or predictions
				s += confMat[j][i];
			precisions[i] = ((double) confMat[i][i]) / s;
		}
		return precisions;
	}
	
	/**
	 * 
	 * @param confMat
	 * @return double[] recalls
	 */
	public double[] recall(int[][] confMat){
		double[] recalls = new double[confMat[0].length];
		for(int i = 0; i< confMat.length; i++){// row or real labels
			int s = 0;
			for(int j = 0; j< confMat[0].length; j++)// column or predictions
				s += confMat[i][j];
			recalls[i] = ((double) confMat[i][i]) / s;
		}
		return recalls;
	}
	
	/**
	 * 
	 * @param precisions
	 * @param recalls
	 * @return double[] fMeasures
	 * @throws Exception
	 */
	public double[] fMeasure(double[] precisions, double[] recalls) throws Exception{
		if(precisions.length != recalls.length)
			throw new Exception("Number of precisions and recalls should be the same.");
		
		double[] fMeasures = new double[precisions.length];
		for(int i = 0; i< fMeasures.length; i ++){
			fMeasures[i] = (2 * precisions[i] * recalls[i]) / (precisions[i] + recalls[i]);
		}
		return fMeasures;
	}
	
	public double weightedAvg(int[][] confMat, double[] values){
		int[] classCounts = classCount(confMat);		
		int total = 0;
		double avgP = 0;
		for(int i = 0; i < classCounts.length; i ++){
			avgP += values[i] * classCounts[i];
			total += classCounts[i];
		}
		avgP = avgP/ ((double) total);
		return avgP;
	}
	/**
	 * calculates number of instances for each label (calculates labels not predictions)
	 * @param confMat
	 * @return
	 */
	private int[] classCount(int[][] confMat){
		int[] classCounts = new int[confMat[0].length];
		for(int i = 0; i< confMat.length; i++){
			for(int j = 0; j< confMat[1].length; j++)
				classCounts[i] += confMat[i][j];
		}
		return classCounts;
	}
	
	/**
	 * Generate accuracy matrix.
	 * labels for columns: precision, recall, fMeasure
	 * labels for rows: label1, label2, label3, weighted Avg.
	 * @param confMat
	 * @return
	 */
	public double[][] accuracyMatrix(int[][] confMat){
		double[][] matrix = new double[confMat[0].length+1][3]; 
		// precisions:
		double[] precisions = precision(confMat);
		for(int i = 0; i<precisions.length; i++)
			matrix[i][0] = precisions[i];
		//recalls:
		double[] recalls = recall(confMat);
		for(int i = 0; i<recalls.length; i++)
			matrix[i][1] = recalls[i];
		// fMeasures:
		double[] fMeasures = null;
		try {
			fMeasures = fMeasure(precisions,recalls);
			for(int i = 0; i<fMeasures.length; i++)
				matrix[i][2] = fMeasures[i];
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Weighted average for Precision, recall and fMeasures		
		matrix[3][0] = weightedAvg(confMat, precisions);
		matrix[3][1] = weightedAvg(confMat, recalls);
		matrix[3][2] = weightedAvg(confMat, fMeasures);
		return matrix;
	}
	
	public String printAccuracyMatrix(int[][] confMat){
		String out = "=== Accuracy Matrix ===\n";
		double[][] accMat = accuracyMatrix(confMat);		
		out += "	Precision	recall	F-Measure\n";
		for(int i = 0 ; i< accMat.length-1; i ++){
			int lbl = i+1;
			out += "lbl=" +lbl + " |	";
			for(int j = 0; j<accMat[1].length; j++)
				out += accMat[i][j] + "	";
			out += "\n";
		}
		out += "Weighted Avg.	";
		for(int j = 0; j<accMat[1].length; j++)
			out += accMat[accMat.length-1][j] + "	";
		out += "\n\n";
		return out;
	}
	public String printConfMat(int[][] confMat){
		String out = "=== Confusion Matrix ===\n" +
				"preds ->	1	2	3\n";
		for(int i = 0; i < confMat[0].length; i ++){
			int lbl = i+1;
			out += "lbl=" + lbl + " |	";
			for (int j = 0 ; j < confMat[1].length; j++)
				out += Integer.toString(confMat[i][j]) + "	";
			out +="\n";

		}
		out += "\n";
		return out;
	}
}
