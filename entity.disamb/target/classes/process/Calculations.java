package entity.disamb.process;

public class Calculations {
	
	private static double[] mins;
	private static double[] maxs;

	/**
	 * performs Min-Max normalization
	 * @param entities
	 * @param cols cols contains indexes od all columns that should be normalized.
	 */
	public static void minMaxNorm(Entities entities, int[] cols){
		calcMinMax(entities, cols);
		for (int i=0; i<cols.length; i++){
			for(Entity en : entities.getList()){
				double currentVal = Double.parseDouble(en.getFeature(cols[i]));
				double normVal = (currentVal - mins[i])/ (maxs[i] - mins[i]);
				en.setFeature(cols[i],Double.toString(normVal));
			}
		}
	}
	
	/**
	 * Calculates min and max of all given columns
	 * @param entities
	 * @param cols
	 */
	private static void calcMinMax(Entities entities, int[] cols){
		mins = new double[cols.length];
		maxs = new double[cols.length];
		for (int i=0; i<cols.length; i++){
			// initial values for min and max (values of first instance)
			double min = Double.parseDouble(entities.getEntity(0).getFeature(cols[i]));
			double max = Double.parseDouble(entities.getEntity(0).getFeature(cols[i]));
			for(Entity en: entities.getList()){
				double currentVal = Double.parseDouble(en.getFeature(cols[i]));
				if(currentVal < min)
					min = currentVal;
				if(currentVal > max)
					max = currentVal;
			}
			mins[i] = min;
			maxs[i] = max;
 		}
	}
	
	public static void logTransform(Entities entities, int[] cols){
		for(int i=0; i<cols.length; i++){
			for(Entity ins: entities.getList()){
				double currentVal = Double.parseDouble(ins.getFeature(cols[i]));
				ins.setFeature(cols[i], Double.toString(Math.log10(currentVal)));
			}
		}
	}
}
