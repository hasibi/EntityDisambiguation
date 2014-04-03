package entity.disamb.process;

import java.util.ArrayList;
import java.util.Comparator;

public class Entity implements Comparator<Entity>, Comparable<Entity> {

	public ArrayList<String>  features;
	int cmpId;
	
	//static Comparator<String[]> dsc;
	
	public Entity(ArrayList<String> sArr, int id){
		features = sArr;
		cmpId = id;
	}
	public Entity(ArrayList<String> sArr){
		features = sArr;
		cmpId = 0;
	}
	public Entity(){
		features = new ArrayList<String>();
		cmpId = 0;
	}
	public void setCmpId(int cmpId){
		this.cmpId = cmpId;
	}
	
	public String getFeature(int i){
		return features.get(i);
	}
	
	public void setFeature(int i, String s){
		this.features.set(i, s);
	}
	
	public void addFeature(String s){
		this.features.add(s);
	}
	public int featureSize(){
		return this.features.size();
	}
	public int compareTo(Entity ins){
		if(checkNumeric()){
			Double val1 = Double.parseDouble(this.features.get(this.cmpId));
			Double val2 = Double.parseDouble(ins.features.get(ins.cmpId));
			return val1.compareTo(val2);
		}
		else{
			return this.features.get(this.cmpId).compareTo(ins.features.get(ins.cmpId));
		}
	}
	
	public int compare(Entity en1, Entity en2){
		return en1.compareTo(en2);
	}
	
	private boolean checkNumeric(){
		try{
			Double.parseDouble(this.features.get(this.cmpId));
		}
		catch(NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}
