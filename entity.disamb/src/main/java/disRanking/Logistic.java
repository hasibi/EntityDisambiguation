package disRanking;

import java.util.ArrayList;

import entity.disamb.process.Entity;



public class Logistic { 

	public static void calcScore(ArrayList<Entity> entities, int[] colIds, String model){
		// The logistic regression model is saved on modelFile.
		// Each line in the modelFile represents the logistic regression line.
		// For each line, the first n features are coefficinets and n+1 th number is the Intercept.
		ArrayList<String[]> lines = getCoefs(model);
		
		for (Entity en: entities){
			double avgDis = 0;
			for(String[] line : lines){
				double dis = getRelDis(en, colIds, line);
				en.features.add(Double.toString(dis));
				avgDis += dis;
			}
			// calculate the average of relative distances as score
			avgDis = avgDis / lines.size();
			en.features.add(Double.toString(avgDis));
		}
	}
	
	public static double getRelDis(Entity en, int[] colIds, String[] line){
		double num = 0, den =0;
		int i=0;
		double coef = 0;
		for(i=0; i < colIds.length; i++){
			coef = Double.parseDouble(line[i]);
			num += coef * Double.parseDouble(en.getFeature(colIds[i]));
			den += coef * coef;
		}
		coef = Double.parseDouble(line[i]);
		num += coef;
		return num / (Math.sqrt(den));
	}
	
	private static ArrayList<String[]> getCoefs(String model){
		// Coefficients of all lines will be saved on lines
		ArrayList<String[]> lines = new ArrayList<String[]>();
		for(String line: model.split("\n")){
			lines.add(line.split("\t"));
		}
		return lines;
	}
}
