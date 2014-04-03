package entity.disamb.process;

import java.util.ArrayList;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class Entities {
	protected ArrayList<Entity> list;
	final int id = 0;
	final int name =1;
	final int type = 2;
	
	int pred;
	int[] probablities;
	int score;
	int newPred;
	
	public Entities(ArrayList<Entity> entities){
		list = entities;
	}
	
	public Entities(){
		list = new ArrayList<Entity>();
	}
	public ArrayList<Entity> getList(){
		return list;
	}

	public Entity getEntity(int i){
		return list.get(i);
	}
	
	public void add(Entity e){
		list.add(e);
	}
	
	public void add(int i, Entity e){
		list.add(i, e);
	}
	public int getFeatureSize(){
		return this.list.get(0).features.size();
	}
	
	public int size(){
		return list.size();
	}
	
	/**
	 * removes redundant entities( e.g.2#Andre_(band) , 3#Andre_(band))
	 * @param entities
	 * @return
	 */
	public void rmRedundants(int popCol){
		//before putting a new instance, check if the ID exists or not (Using MultiMap).
		// if it exists, compare "popId" of two entities and keep the largest one
			Multimap<String,Entity> groups = ArrayListMultimap.create();	
			for(Entity ins: this.list){
				// remove source Id from beginning of entityId and compare the rest of entitId (e.g. 2#Den_(album)	-> Den_(album))
				int index = ins.getFeature(0).indexOf('#');
				//toLowerCade: to cover cases like "Den_(Pharaoh)" and "Den_(pharaoh)"
				String entityId = ins.getFeature(0).substring(index+1).toLowerCase();
				Entities sameEntityIds =  new Entities(new ArrayList<Entity> (groups.get(entityId))); 
				if(sameEntityIds.size()>0){	
					double prevPopValue = Double.parseDouble(sameEntityIds.getEntity(0).getFeature(popCol));
					double currPopValue = Double.parseDouble(ins.getFeature(popCol));
					if(prevPopValue < currPopValue){
						groups.removeAll(entityId);
						groups.put(entityId, ins);
					}
				}
				else if(sameEntityIds.size()==0){
					groups.put(entityId, ins);
				}
			}
			this.list = Operations.flatten(groups).list;
	}
	
	/**
	 * Remove POIs and delete ambiguous entity names with less than two entity instances
	 */
	public void rmPois(){
		// create a Multimap based on entity names
		Multimap<String,Entity> groups = ArrayListMultimap.create();
		// filter POIs from multimap
		for(Entity en: this.list){
			if(! en.getFeature(type).matches("POI"))
				groups.put(en.getFeature(name), en);
		}
		 
		// create a new list for entity names with less than two instances
		ArrayList<String> oneInsEntities = new ArrayList<String>();
		for(String name: groups.keySet()){
			Entities sameEntityNames =  new Entities(new ArrayList<Entity> (groups.get(name))); 
			if(sameEntityNames.size()==1)
				oneInsEntities.add(name);
		}
		for(String name:oneInsEntities)
			groups.removeAll(name);
		
		this.list = Operations.flatten(groups).list;
	}
	
}
