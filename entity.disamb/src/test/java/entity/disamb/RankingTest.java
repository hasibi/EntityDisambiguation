package entity.disamb;

//import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Random;

//import org.junit.Test;

import com.google.common.collect.Multimap;

public class RankingTest {

	
	/**
	 * This is a program for testing ranking model based on relative distances on logistic classification.
	 * The test program first generates 3 random lines according to the pattern of existing logistic models for our data.
	 * Then this model is tested with a 2 dimensional data, where the first dimension is popularity and 2nd is coverage feature.
	 * this 2 dimensional data is generated with 2 nested for loops, where data is from 0 to 1.
	 * The pass condition is, by increasing popularity value for a coverage value, the ranking score should be increased.
	 * @param args
	 */
	
//	 This program shows that this algorithm does not work for all data. Here is a counter example of a logistic regression model:
//	11.080570983578363	-16.52028668212464	6.58615266827055  (classifier for label 1)
//	1.500104129647284	-86.81344458790589	10.292743683152082 (classifier for label 2)
//	-91.74213240776797	51.046825167146324	7.5819879834086095 (classifier for label 3)
	public static void main(String[] args) {
		// creates a coverage list
		//ArrayList<Instance> covs = IO.readData("./output/geo.covs.txt");
		
		//================== Generating the data ====================
				ArrayList<Instance> testInss = new ArrayList<Instance>();
		for(double i = 0 ; i <= 1; i += 0.1){ // cov
			for (double j = 0 ; j <=1; j += 0.1){ // pop
				ArrayList<String> features = new ArrayList<String>();
				features.add(Double.toString(j));
				features.add(Double.toString(i));
				// features.add(cov.features.get(0));
				testInss.add(new Instance(features));
			}
		}
		//================== Generating logistic regression model for n times ====================
		Random generator = new Random();
		String model ="";
		boolean check = true;
		// 
		for(int i =0; i<100; i++){
			System.out.println("iteration " + i + ": testing with a random model.");
			model = "";
			double[] pos = new double[3];
			double[] neg = new double[3];
			// 3 positive random number
			for(int j = 0; j<3; j++)
				pos[j] = generator.nextDouble() *20; 
			// 3 negative random number
			for(int j = 0; j<3; j++)
				neg[j] = generator.nextDouble() * -20; 
			// generate lines based on a pattern, where one line should be opposite of two other lines.
			for(int j = 0; j<2; j++){
				model += Double.toString(pos[j]) + "\t";
				model += Double.toString(neg[j]) + "\t";
				model += Double.toString(generator.nextDouble() *20 -10) + "\n"; 
			}
			model += Double.toString(neg[2]) + "\t";
			model += Double.toString(pos[2]) + "\t";
			model += Double.toString(generator.nextDouble() *20 -10) ; 
			// model is generated, test the model for ranking algorithm
			System.out.println("model: \n" + model);
			boolean b = testDis(testInss, model);
			System.out.println(b + "\n");
			check = check && b;
		}		
		System.out.println("Check the consistency for 100 random logistic model: " + check);
	}
	
	
	/**
	 * Calculates the relative distance of an instance from a logistic model and take the average of distances as a ranking score.
	 * @param testInss
	 * @param model
	 * @return
	 */
	public static boolean testDis(ArrayList<Instance> testInss, String model){
//		String model = "4.56	-0.64	-2.21\n" +
//				"2.66	-3.21	0.04\n" + 
//				"-4.22	2.58	1.71";

				Logistic.calcScore(testInss, new int[]{0, 1}, model);
				System.out.println("sorting");
				Multimap<String,Instance> testGroups= Operations.groupBy(testInss,1);
				Operations.sortByGroup(testGroups, testInss.get(0).features.size()-1);
				//IO.writeData(testGroups, "test.rank.txt");
				// checking
				boolean check = true;
				for(String cov : testGroups.keySet()){
					ArrayList<Instance> group = new ArrayList<Instance>(testGroups.get(cov));
					Instance prevIns = group.get(0);
					for(int i =1; i < group.size() ; i ++){
						Instance ins = group.get(i);
						check = check && (Double.parseDouble(prevIns.features.get(0)) <= Double.parseDouble(ins.features.get(0)));
					}
				}
				if(check)
					System.out.println("The ranking feature sorts entities according to their imortance features.");
				else
					System.out.println("The ranking feature is not working!!");
				return check;
	}
}
