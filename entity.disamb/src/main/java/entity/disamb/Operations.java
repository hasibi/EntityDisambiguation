package entity.disamb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class Operations {
	
	public static  Multimap<String, Instance> groupBy (ArrayList<Instance> instances, int colId){
		Multimap<String,Instance> groups = ArrayListMultimap.create();	
		for(Instance ins: instances)
			groups.put(ins.features.get(colId), ins);
		return groups;
	}
	
	public static ArrayList<Instance> flatten(Multimap<String, Instance> groups){
		ArrayList<Instance> instances = new ArrayList<Instance>();
		// First sort the Keys of groups and then created a sorted arrayList
		List<String> names = new ArrayList<String>(groups.keySet());
		Collections.sort(names);
		for(String name:names){
			List<Instance> groupInss = (List<Instance>) groups.get(name);
			for(Instance instance:groupInss)
				instances.add(instance);
			//separator: to print a new line after each group
			//instances.add(new Instance());
		}
		return instances;
	}
	
	public static void sort(ArrayList<Instance> instances, int cmpId){
		setCmpId(instances, cmpId);
		Collections.sort(instances);
		
	}
	
	
	/**
	 * Sorts every group of entities based on column ID.
	 * @param entities
	 * @param colId  This ID will define the column that the data should be sorted based on. (starts from zero)
	 */
	public static void sortByGroup(Multimap<String, Instance> instances, int cmpId){
		for(String name: instances.keySet()){
			ArrayList<Instance> group = new ArrayList<Instance>(instances.get(name));
			sort(group, cmpId);
			instances.replaceValues(name, group);
		}
			//test
			//Multimap<String,Instance> miniMap = ArrayListMultimap.create();
//			for(Instance ins:group)
//				System.out.println(ins.features.get(3));
//			System.out.println();
//				miniMap.put(name, ins);
//			String str= Startup.groupToStr(miniMap);
////			Startup.writeFile(str, "ranks"+"."+ name);
//		for(String name: instances.keySet()){
//			ArrayList<Instance> group = new ArrayList<Instance>(instances.get(name));
//			for(Instance ins:group)
//				System.out.println(ins.features.get(group.get(0).features.size()-1));
//			System.out.println();
//		}
	}
	
	public static ArrayList<Instance> splitDomain(ArrayList<Instance> instances, String domain, int domainId){
		ArrayList<Instance> domainIns = new ArrayList<Instance>();
		for(Instance ins : instances){
			if(ins.features.get(domainId).matches(domain)){
				domainIns.add(ins);
			}
		}
		return domainIns;
	}
	

	/**
	 * removes redundant entities( e.g.2#Andre_(band) , 3#Andre_(band))
	 * @param instances
	 * @return
	 */
	public static ArrayList<Instance> rmRedundants(ArrayList<Instance> instances, int popCol){
		//before putting a new instance, check if the ID exists or not (Using MultiMap).
		// if it exists, compare "popId" of two instances and keep the largest one
			Multimap<String,Instance> groups = ArrayListMultimap.create();	
			for(Instance ins: instances){
				// remove source Id from beginning of entityId and compare the rest of entitId (e.g. 2#Den_(album)	-> Den_(album))
				int index = ins.features.get(0).indexOf('#');
				//toLowerCade: to cover cases like "Den_(Pharaoh)" and "Den_(pharaoh)"
				String entityId = ins.features.get(0).substring(index+1).toLowerCase();
				ArrayList<Instance> sameEntityIds =  new ArrayList<Instance>(groups.get(entityId)); 
				if(sameEntityIds.size()>0){	
					double prevPopValue = Double.parseDouble(sameEntityIds.get(0).features.get(popCol));
					double currPopValue = Double.parseDouble(ins.features.get(popCol));
					if(prevPopValue < currPopValue){
						groups.removeAll(entityId);
						groups.put(entityId, ins);
					}
				}
				else if(sameEntityIds.size()==0){
					groups.put(entityId, ins);
				}
			}
			ArrayList<Instance> newInstances = Operations.flatten(groups);
			return newInstances;
	}
	
	private static void setCmpId(Collection<Instance> group, int id){
		for(Instance instance:group){
			instance.setCmpId(id);
		}
	}
	/**
	 * mapping the range from [a1,a2] to [b1,b2]
	 * @param a1
	 * @param a2
	 * @param b1
	 * @param b2
	 * @param colId
	 * @param instances
	 */
	/*
	public static void mapRange(double a1, double a2, double b1, double b2, int colId, List<Instance> instances){
		for(Instance ins: instances){
			double d = Double.parseDouble(ins.features.get(colId));
			double val = b1 + ((d-a1) * (b2-b1) / (a2-a1));
			ins.features.add(Double.toString(val));
		}
	}
	

	*/
}