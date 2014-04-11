package entity.disamb.process;

public class Calculations {
	
	private Entities entities;
	
	// These values will be calculated in calcMinMax()
	private double[] mins;
	private double[] maxs;
	
	private double[] means;
	private double[] stds;

	public Calculations(Entities entities){
		this.entities = entities;
	}
	/**
	 * Calculates mean normalization (Weka standardization) based on this formula:
	 * newVal = (x - mean) / standardDeviation
	 * @param cols
	 */
	public void meanNorm(int[] cols){
		calcStds(cols); // calculates mean and stDev of all columns.
		for (int i=0; i<cols.length; i++){
			for(Entity en : entities.getList()){
				double currentVal = Double.parseDouble(en.getFeature(cols[i]));
				double normVal = (currentVal - means[i])/ stds[i];
				en.setFeature(cols[i],Double.toString(normVal));
			}
		}
	}
	
	/**
	 * Calculates Standard deviation based on this formula: sqrt(sum((s - mean)^ 2) / n)
	 * webpage: http://www.mathsisfun.com/data/standard-deviation-formulas.html
	 * @param cols
	 */
	private void calcStds(int[] cols){
		calcMeans(cols); // calculates means
		stds = new double[cols.length];
		for (int i=0; i<cols.length; i++){
			double sum = 0; 
			int n = 0;
			for(Entity en: entities.getList()){
				double val = Double.parseDouble(en.getFeature(cols[i]));
				sum += Math.pow(val - means[i], 2);
				n++;
			}
			stds[i] = Math.sqrt(sum / (double) n);
 		}
	}
	
	private void calcMeans(int[] cols){
		means = new double[cols.length];
		for (int i=0; i<cols.length; i++){
			double sum = 0; 
			int n = 0;
			for(Entity en: entities.getList()){
				sum += Double.parseDouble(en.getFeature(cols[i]));
				n++;
			}
			means[i] = sum / (double) n;
 		}
	}
	
	/**
	 * performs Min-Max normalization
	 * @param cols: indexes of all columns that should be calculated.
	 */
	public void minMaxNorm(int[] cols){
		calcMinMax(cols);
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
	private void calcMinMax(int[] cols){
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
	
	public void logTransform(int[] cols){
		for(int i=0; i<cols.length; i++){
			for(Entity ins: entities.getList()){
				double currentVal = Double.parseDouble(ins.getFeature(cols[i]));
				ins.setFeature(cols[i], Double.toString(Math.log10(currentVal)));
			}
		}
	}
}
