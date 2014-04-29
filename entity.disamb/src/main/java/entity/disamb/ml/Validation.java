package entity.disamb.ml;

import java.util.ArrayList;

import com.google.common.collect.Multimap;

import entity.disamb.process.Entities;
import entity.disamb.process.Entity;
import entity.disamb.process.Operations;

public class Validation {
	private Multimap<String, Entity> entityGroups;
	private ArrayList<String> ShuffledNames;
	
	
	public Validation(Entities entities, ArrayList<String> shuffledNames){
		entityGroups = Operations.groupBy(entities, entities.name);
		ShuffledNames = shuffledNames;
		System.out.println("Number of entity names: " + ShuffledNames.size());
	}
	
	public ArrayList<String> getShuffledNames(){
		return this.ShuffledNames;
	}
	
	/**
	 *  Gives the training set.
	 * @param names
	 * @param folds (number of folds)
	 * @param n (nth fold)
	 * @return Entities
	 */
	public Entities trainCV(int folds, int n){
		// 1. find the range of test set
		// 2. find entities before and after the range of test set
		// 3. Add two entity sets together.
		int splitSize = ShuffledNames.size() / folds;
		int testStart = splitSize * n; // test set range
		int testEnd;
		if(n == folds-1)
			testEnd = ShuffledNames.size() - 1; // test set range
		else
			testEnd = testStart + splitSize - 1; // test set range
		Entities beforeTestSet = getEntities(ShuffledNames, 0, testStart-1);
		Entities afterTestSet = getEntities(ShuffledNames, testEnd+1, ShuffledNames.size()-1);
		beforeTestSet.addAll(afterTestSet);
		return beforeTestSet;
	}
	
	/**
	 * Gives the training set.
	 * @param names
	 * @param folds (number of folds)
	 * @param n (nth fold)
	 * @return Entities
	 */
	public Entities testCV(int folds, int n){
		int splitSize = ShuffledNames.size() / folds;
		int start = splitSize * n;
		int end;
		if(n == folds-1)
			end = ShuffledNames.size() - 1;
		else
			end = start + splitSize - 1;
		Entities ens = getEntities(ShuffledNames, start, end);
		return ens;
	}
	
	/**
	 * Gives all entities a start and end range. (Start and end entities are included).
	 * @param names
	 * @param start
	 * @param end
	 * @return Entities
	 */
	private Entities getEntities(ArrayList<String> names, int start, int end){
		Entities entities = new Entities();
		for(int i= start; i<= end && i>= 0; i++){
			ArrayList<Entity> sameNameEns = new ArrayList<Entity>(this.entityGroups.get(names.get(i)));
			entities.addAll(new Entities(sameNameEns));
		}
		return entities;
	}
	
//	/**
//	 * create a shuffled list of entity names
//	 * @param entities
//	 * @return ArrayList<String>
//	 */
//	private ArrayList<String> shuffleNames(){
//		ArrayList<String> names = new ArrayList<String>(entityGroups.keySet());
//		Collections.shuffle(names);
//		return names;
//	}
}
