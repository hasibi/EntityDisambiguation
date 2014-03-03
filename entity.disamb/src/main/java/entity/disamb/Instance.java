package entity.disamb;

import java.util.ArrayList;
import java.util.Comparator;

public class Instance implements Comparator<Instance>, Comparable<Instance> {

	ArrayList<String>  features;
	int cmpId;
	
	//static Comparator<String[]> dsc;
	
	public Instance(ArrayList<String> sArr, int id){
		features = sArr;
		cmpId = id;
	}
	public Instance(ArrayList<String> sArr){
		features = sArr;
		cmpId = 0;
	}
	public Instance(){
		features = new ArrayList<String>();
		cmpId = 0;
	}
	public void setCmpId(int cmpId){
		this.cmpId = cmpId;
	}
	public int compareTo(Instance ins){
		if(checkNumeric()){
			Double val1 = Double.parseDouble(this.features.get(this.cmpId));
			Double val2 = Double.parseDouble(ins.features.get(ins.cmpId));
			return val1.compareTo(val2);
		}
		else{
			return this.features.get(this.cmpId).compareTo(ins.features.get(ins.cmpId));
		}
	}
	
	public int compare(Instance ins1, Instance ins2){
		return ins1.compareTo(ins2);
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
