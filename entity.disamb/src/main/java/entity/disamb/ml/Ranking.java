package entity.disamb.ml;

import java.util.ArrayList;

import com.google.common.collect.Multimap;

import entity.disamb.process.Entities;
import entity.disamb.process.Entity;
import entity.disamb.process.Operations;


public class Ranking {

	private Entities entities;
	
	public Ranking(Entities ens){
		entities = ens;
	}
	
	/**
	 * Calculate the score of each entity using multiplication of gain and probabilities matrix.
	 * @param set
	 * @param gain
	 * @throws Exception if size of gain[] and the number of labels are not equal.
	 */
	public void scoreByProbs(int[] gain) throws Exception{
		
		if(! (gain.length == this.entities.probablities.length))
			throw new Exception("Number of gains is not equal to number of class probabilities."); // Check if the probabilities of entity classes are calculated
		for (int i=0 ; i < this.entities.probablities.length; i ++)
			if(this.entities.probablities[i]==0)
				throw new Exception("Probability " + i + " is not assigned to the entity set.");
		
		for(int i = 0; i<this.entities.size(); i++){
			Entity en = this.entities.getEntity(i);
			double score = 0;
			for(int j = 0; j<gain.length; j++){
				score += gain[j] * Double.parseDouble(en.getFeature(this.entities.probablities[j]));
			}
			en.addFeature(Double.toString(score)); // adding the score to the entity
		}
		
		// *** Assign the id of score to the Entities
		this.entities.score = this.entities.getFeatureSize() -1;
	}
	
	/**
	 * Give a new label to each entity.
	 * In this labeling, each group of entities with the same name has zero or only one entity with label one,
	 * which is the entity with highest score.
	 * @return
	 */
	public void label(){	
		Multimap <String, Entity> groups = Operations.groupBy(this.entities, this.entities.name);
		Operations.sortByGroup(groups, this.entities.score, false); // descending sort
		
		for(String name : groups.keySet()){
			Entities sameNameEns = new Entities(new ArrayList<Entity>(groups.get(name)));
			int predId = this.entities.pred;
//			boolean hasLabelOne = false;
			Entity firstEntity = sameNameEns.getEntity(0);
//			if(sameNameEns.getEntity(0).getFeature(predId).matches("1")){ // If the prediction for the first entity is "1"
////				hasLabelOne = true;
//				firstEntity.addFeature("1"); // add label one for the first entity
//			}
//			else 
				firstEntity.addFeature(firstEntity.getFeature(predId));
			for(int i = 1; i< sameNameEns.size(); i++){ // the other entities will get label 2 or 3
				
				Entity en = sameNameEns.getEntity(i);
				if(en.getFeature(predId).matches("1")){
					en.addFeature("2");
////					System.out.println("#####################################################\n" +
//							name +
//							"################################");
				}
				else 
					en.addFeature(en.getFeature(predId)); 
			}
		}
		this.entities.newPred = this.entities.getFeatureSize()-1;
	}
	
	/**
	 * Rank entities based on the scores in each group of entities with the same name.
	 * @return
	 */
	public void rank(){
		Multimap <String, Entity> groups = Operations.groupBy(this.entities, this.entities.name);
		Operations.sortByGroup(groups, this.entities.score, false); // descending sort
		
		for(String name : groups.keySet()){
			Entities sameNameEns = new Entities(new ArrayList<Entity>(groups.get(name)));
			int r = 1;
			for(int i = 0; i< sameNameEns.size(); i++){
				Entity en = sameNameEns.getEntity(i);
				en.addFeature(Integer.toString(r++));
			}
		}
		this.entities.rank = this.entities.getFeatureSize() -1;
	}
}
