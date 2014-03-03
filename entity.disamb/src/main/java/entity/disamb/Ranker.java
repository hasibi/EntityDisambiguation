package entity.disamb;

import java.util.ArrayList;
import com.google.common.collect.Multimap;

public class Ranker {

	/**
	 * sorts and ranks each group of entities based on the comparingId.
	 * @param entities The Multimap object should be sorted for all groups of entities.
	 * @param cmpId
	 */
	public static void rankbyGroup(Multimap<String, Instance> entities, int cmpId){
		Operations.sortByGroup(entities, cmpId);
		for(String name:entities.keySet()){
			ArrayList<Instance> group = new ArrayList<Instance>(entities.get(name));
			int rank = 1;
			for(Instance ins: group){
				ins.features.add(Integer.toString(rank));
				rank ++;
			}
		}
	}
	
	/**
	 * Sets same comparingId for all instances of a group
	 */
	
}
