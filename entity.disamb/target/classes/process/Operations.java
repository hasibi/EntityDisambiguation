package entity.disamb.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;




public class Operations {
	
	public static  Multimap<String, Entity> groupBy (Entities entities, int colId){
		Multimap<String,Entity> groups = ArrayListMultimap.create();	
		for(Entity en: entities.getList())
			groups.put(en.getFeature(colId), en);
		return groups;
	}
	
//	/**
//	 * 
//	 * @param groups
//	 * @param entities
//	 */
//	public static void flatten(Multimap<String, Entity> groups, Entities entities){
//		enti
//		// First sort the Keys of groups and then created a sorted arrayList
//		List<String> names = new ArrayList<String>(groups.keySet());
//		Collections.sort(names);
//		for(String name:names){
//			List<Entity> groupInss = (List<Entity>) groups.get(name);
//			for(Entity en:groupInss)
//				entities.add(en);
//			//separator: to print a new line after each group
//			//entities.add(new Instance());
//		}
//	}
	
	public static Entities flatten(Multimap<String, Entity> groups){
		Entities entities= new Entities();
		// First sort the Keys of groups and then created a sorted arrayList
		List<String> names = new ArrayList<String>(groups.keySet());
		Collections.sort(names);
		for(String name:names){
			List<Entity> groupInss = (List<Entity>) groups.get(name);
			for(Entity en:groupInss)
				entities.add(en);
			//separator: to print a new line after each group
			//entities.add(new Instance());
		}		
		return entities;
	}
	
	public static void sort(Entities entities, int cmpId, boolean ascending){
		setCmpId(entities, cmpId);
		if(ascending)
			Collections.sort(entities.list);
		else
			Collections.sort(entities.list, Collections.reverseOrder());
	}
	
	
	/**
	 * Sorts every group of entities based on column ID.
	 * @param entities
	 * @param colId  This ID will define the column that the data should be sorted based on. (starts from zero)
	 */
	public static void sortByGroup(Multimap<String, Entity> multiMap, int cmpId, boolean ascending){
		for(String name: multiMap.keySet()){
			Entities group = new Entities(new ArrayList<Entity>(multiMap.get(name)));
			sort(group, cmpId, ascending);
			multiMap.replaceValues(name, group.list);
		}
	}
	
	/**
	 * 	First group entities based on groupId and then sorts groups of based on cmpId.
	 * @param entities
	 * @param groupColId
	 * @param cmpId
	 * @return
	 */
	public static Entities sortByGroup(Entities entities, int groupColId, int cmpId, boolean ascending){
		Multimap<String,Entity> groups= Operations.groupBy(entities,groupColId);
		Operations.sortByGroup(groups, cmpId, ascending);
		Entities insList = Operations.flatten(groups);
		return insList;
	}
	
	public static Entities splitDomain(Entities entities, String domain, int domainId){
		Entities domainIns = new Entities();
		for(Entity en : entities.getList()){
			if(en.getFeature(domainId).matches(domain)){
				domainIns.add(en);
			}
		}
		return domainIns;
	}
	
	public static Entities mergeDomain(Wikis wikis, Geos geos){
		//Adding wiki entities
		Entities allEntities = wikis;
		//Adding geo entities 
		for(Entity en : geos.getList()){
			ArrayList<String> geoFeatures = new ArrayList<String>();
			for(int i = 0; i<geos.epop; i ++)
				geoFeatures.add(en.getFeature(i));
			geoFeatures.add(""); // add null String for "wcpop"
			geoFeatures.add(""); // add null string for "wpop"
			for(int i = geos.epop; i<geos.numOfFeatures(); i ++)
				geoFeatures.add(en.getFeature(i));
			
			allEntities.add(new Entity(geoFeatures));
		}
		return allEntities;
	}

	
	private static void setCmpId(Entities entities, int id){
		for(Entity en:entities.getList()){
			en.setCmpId(id);
		}
	}
	/**
	 * mapping the range from [a1,a2] to [b1,b2]
	 * @param a1
	 * @param a2
	 * @param b1
	 * @param b2
	 * @param colId
	 * @param entities
	 */
	/*
	public static void mapRange(double a1, double a2, double b1, double b2, int colId, List<Instance> entities){
		for(Instance ins: entities){
			double d = Double.parseDouble(ins.features.get(colId));
			double val = b1 + ((d-a1) * (b2-b1) / (a2-a1));
			ins.features.add(Double.toString(val));
		}
	}
	

	*/
}