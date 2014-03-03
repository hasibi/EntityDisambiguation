package entity.disamb;

import java.util.ArrayList;

public class Calc {
	
	private static double[] mins;
	private static double[] maxs;

	/**
	 * performs Min-Max normalization
	 * @param instances
	 * @param cols cols contains indexes od all columns that should be normalized.
	 */
	public static void minMaxNorm(ArrayList<Instance> instances, int[] cols){
		getMinMax(instances, cols);
		for (int i=0; i<cols.length; i++){
			for(Instance ins : instances){
				double currentVal = Double.parseDouble(ins.features.get(cols[i]));
				double normVal = (currentVal - mins[i])/ (maxs[i] - mins[i]);
				ins.features.set(cols[i],Double.toString(normVal));
			}
		}
	}
	
	/**
	 * Calculates min and max of all given columns
	 * @param instances
	 * @param cols
	 */
	private static void getMinMax(ArrayList<Instance> instances, int[] cols){
		mins = new double[cols.length];
		maxs = new double[cols.length];
		for (int i=0; i<cols.length; i++){
			// initial values for min and max (values of first instance)
			double min = Double.parseDouble(instances.get(0).features.get(cols[i]));
			double max = Double.parseDouble(instances.get(0).features.get(cols[i]));
			for(Instance ins: instances){
				double currentVal = Double.parseDouble(ins.features.get(cols[i]));
				if(currentVal < min)
					min = currentVal;
				if(currentVal > max)
					max = currentVal;
			}
			mins[i] = min;
			maxs[i] = max;
 		}
	}
	
	public static void logTransform(ArrayList<Instance> instances, int[] cols){
		for(int i=0; i<cols.length; i++){
			for(Instance ins: instances){
				double currentVal = Double.parseDouble(ins.features.get(cols[i]));
				ins.features.set(cols[i], Double.toString(Math.log10(currentVal)));
			}
		}
	}
}
