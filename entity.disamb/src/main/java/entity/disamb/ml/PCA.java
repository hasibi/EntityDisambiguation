package entity.disamb.ml;

import weka.core.Instances;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.PrincipalComponents;
import weka.attributeSelection.Ranker;


public class PCA {
//	private AttributeSelection selector;
//
//	public PCA(){
//		this.selector = new AttributeSelection();
//	}
	
	
	public static Instances tarnsform(Instances instances, AttributeSelection selector) throws Exception{
		if(selector.toResultsString().matches("") )
			throw new Exception("Attribute selector is null.");
		Instances transformedInss = null;
		transformedInss = selector.reduceDimensionality(instances);
		return transformedInss;
	}
	
	public static AttributeSelection learnPCA(Instances instances){
		AttributeSelection selector = new AttributeSelection();
		
		// Setting evaluator for selector
		PrincipalComponents pca = new PrincipalComponents();
		// pca.setMaximumAttributeNames(maxAtts);
		selector.setEvaluator(pca);
		
		// Setting ranker for selector
		Ranker ranker = new Ranker();
		selector.setSearch(ranker);
		
		//learning
		try {
			selector.SelectAttributes(instances);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		String results = selector.toResultsString();
		return selector;
	}
//	/**
//	 * gets coefficients of PCA transformation for the give max number of new attributes.
//	 * @param pcaOutput
//	 * @param features
//	 * @param maxAtts
//	 */
//	private double[][] getFormulas(String pcaOutput, String[] features, int maxAtts){
//		int start = pcaOutput.indexOf("Ranked attributes:") + "Ranked attributes: ".length();
//		int end = pcaOutput.indexOf("Selected attributes:");
//		String formulasSection = pcaOutput.substring(start, end); 
//		formulasSection = formulasSection.replaceAll(" +", "#"); // remove spaces
//		String[] formulas = formulasSection.split("#[0-9\\.]+#[0-9]#", -1); // remove the head of each line(formula)
//		// get coefficients of each line
//		double[][] allCoefs = new double[maxAtts][features.length];
//		int attCount = 0;
//		for(int i = 0; i < formulas.length && attCount < maxAtts ; i++){
//			if(formulas[i].length() > 1){
//				formulas[i] = formulas[i].replaceAll("\\s", ""); // remove new lines
//				double[] coefs = getFormulaCoefs(formulas[i], features);
//				allCoefs[attCount] = coefs;
//				attCount++;
//			}
//		}
//		return allCoefs;
////		for(int i = 0; i < allCoefs.length; i++){
////			for(int j = 0; j < allCoefs[i].length; j++)
////				System.out.print(allCoefs[i][j] + " ");
////			System.out.print("\n");
////		}
//	}
//	
//	/**
//	 * gets coefficients of string formula, which are order according to "features".
//	 * @param formula: The string format of formula
//	 * @param features: List of names of all feature names.
//	 * @return
//	 */
//	private double[] getFormulaCoefs(String formula, String[] features){
//		System.out.println("## Original PCA formula: \t" + formula);
//		ArrayList<Coef> coefs = new ArrayList<Coef>();
//		for(int i = 0; i < features.length; i ++){
//			Coef coef = new Coef();
//			coef.feature= features[i];
//			coef.index = formula.indexOf(features[i]);
//			// if one feature is not in the formula, assign a large value to the index.
//			// This will keep these features, the latest features in "coefs" after sorting.
//			if (coef.index == -1) 
//				coef.index = 1000;
//			coefs.add(i, coef);
//		}
//		Collections.sort(coefs);
//		String[] formulaFeatures = formula.split("[-+]?[0-9]+\\.[0-9]+", -1); // find every string except doubles
//		for(String s: formulaFeatures)
//			if(s.length() > 1){
//				formula = formula.replaceAll(s, "\t"); // remove lables to find coefficients
//			}
//		// assign coefficients to the variable names
//		String[] nums = formula.split("\t");
//		for(int i = 0; i < nums.length; i++)
//			coefs.get(i).value = nums[i];
//		// assign zero to the features that are not appeared in the formula
//		for(Coef c: coefs)
//			if(c.index == 1000) // 1000 is set above
//				c.value = "0";
//		
//		System.out.print("## Extracted coeficients:\t");
//		for(Coef c: coefs)
//			System.out.print(c.value + " " + c.feature + "\t" );
//		System.out.println();
//
//		return convertToDouble(coefs, features);
//	}
//	
//	/**
//	 * takes "Coef" objects and convert them to array of doubles
//	 * @param coefs
//	 * @param features
//	 * @return
//	 */
//	private double[] convertToDouble(ArrayList<Coef> coefs, String[] features){
//		double[] res = new double[features.length];
//		for(int i = 0; i < features.length; i++)
//			for(Coef c : coefs)
//				if(c.feature.matches(features[i]))
//					res[i] = Double.parseDouble(c.value);
//		return res;
//	}
//	
//	// This class keeps name and coefficient of each PCA formula
//	 private class Coef implements Comparator<Coef>, Comparable<Coef>{
//		public String feature;
//		public int index;
//		public String value;
//
//		Coef(){
//			feature = "";
//			index = 0;
//			value = "";
//		}
//		
//		public int compareTo(Coef c){
//			return Integer.valueOf(this.index).compareTo(c.index);
//		}
//		
//		public int compare(Coef c1, Coef c2){
//			return c1.compareTo(c2);
//		}
//	}
}
