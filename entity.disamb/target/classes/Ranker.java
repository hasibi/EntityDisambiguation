package entity.disamb;

import java.util.ArrayList;
import com.google.common.collect.Multimap;

import entity.disamb.process.*;


public class Ranker {

	/**
	 * sorts and ranks each group of entities based on the comparingId.
	 * @param entities The Multimap object should be sorted for all groups of entities.
	 * @param cmpId
	 */
	public static void rankbyGroup(Multimap<String, Entity> entities, int cmpId){
		Operations.sortByGroup(entities, cmpId, true);
		for(String name:entities.keySet()){
			ArrayList<Entity> group = new ArrayList<Entity>(entities.get(name));
			int rank = 1;
			for(Entity ins: group){
				ins.features.add(Integer.toString(rank));
				rank ++;
			}
		}
	}
	
	/**
	 * Sets same comparingId for all entities of a group
	 */
	
}
