package entity.disamb.ml;

import java.util.ArrayList;

import com.google.common.collect.Multimap;

import entity.disamb.process.Entities;
import entity.disamb.process.Entity;
import entity.disamb.process.Operations;


public class Ranking {

	public static void scoreByProbs(EntitiesInss set, int[] gain) throws Exception{
		
		if(! (gain.length == set.getprop(0).length))
			throw new Exception(); // Check if the probabilities of entity classes are calculated
		
		for(int i = 0; i<set.size(); i++){
			double[] prob = set.getprop(i);
			double score = 0;
			for(int j = 0; j<prob.length; j++){
				score += gain[j] * prob[j];
			}
			set.addScore(i, score);
		}
	}
	
	public static Entities rank(Entities entities){
//		if(set.getClasses().size() == 0 || set.getScores().size() == 0)
//			throw new Exception(); 		// check if the prediction label and score of entities are calculated.
//		Entities entities = set.concatEntityPred();
//		
		 // Order of features in entities: ...., label, prediction, prob1, prob2, prob3, score
		int scoreId = entities.getFeatureSize() - 1;
		int predictionId = scoreId - 4; 
		
		// rank and label entities
		Multimap <String, Entity> groups = Operations.groupBy(entities, entities.name);
		Operations.sortByGroup(groups, scoreId, false);
		for(String name : groups.keySet()){
			int r = 1;
			Entities ens = new Entities(new ArrayList<Entity>(groups.get(name)));
			if(ens.getEntity(0).)
			for(int i = 1; i< ens.size(); i++){
				Entity en = ens.getEntity(i);
				
			}
			for(Entity en : groups.get(name)){
				en.addFeature(Integer.toString(r++)); // rank entities 
			}
		}
		return Operations.flatten(groups);
		
		//concatenate everything to each other -> generate Entites()
		// rank
		// reverse it back to EntitiesInss
//		for(int i = 0; i<set.size(); i++){
//			
//		}
	}
	
//	private static void rank(Multimap <String, Entity> groups){
//		Operations.sortByGroup(groups, cmpId)
//		for(String name : groups.keySet()){
//			Entities ens = new Entities(new ArrayList<Entity>(groups.get(name)));
//			
//			
//		}
//	}
}
