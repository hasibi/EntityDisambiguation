package entity.disamb;

import java.util.ArrayList;

import com.google.common.collect.Multimap;

public class Geos {
	public ArrayList<Instance> instances;
	
	final int id = 0;
	final int name =1;
	final int type = 2;
	final int epop = 3;
	final int cov = 4;
	final int label = 5;

	public Geos (ArrayList<Instance> instances){
		this.instances = instances;
	}
	
	public void cleanFeatures(){
		for(Instance ins: this.instances){
			ins.features.remove(7); // remove res
			ins.features.remove(6); // remove popMovie
			ins.features.remove(4); // remove wpop
			ins.features.remove(3); // remove wcpop
		}
	}
	
	public void takeLog(){
		Calc.logTransform(this.instances, new int[]{cov});
	}
	
	public void normMinMax(){
		Calc.minMaxNorm(this.instances, new int[]{epop,cov});
	}
	
	public void rank(String modelFile){
		String model = IO.readFile(modelFile);
		Logistic.calcScore(this.instances, new int[] {epop, cov}, model);
		System.out.println("Scores are calculated!!");
		
		Multimap<String,Instance> geoGroups= Operations.groupBy(this.instances,name);
		Operations.sortByGroup(geoGroups, this.instances.get(0).features.size()-1);
		this.instances = Operations.flatten(geoGroups);
//		
//		// test 
//		boolean check = true;
//		for(String name : geoGroups.keySet()){
//			ArrayList<Instance> group = new ArrayList<Instance>(geoGroups.get(name));
//			Instance prevIns = group.get(0);
//			for(int i =1; i < group.size() ; i ++){
//				Instance ins = group.get(i);
//				check = check && (Double.parseDouble(prevIns.features.get(3)) <= Double.parseDouble(ins.features.get(3)));
//			}	
//		}
//		System.out.println(check);
	}
	
	public void genArff(String fileName){
		Arff.gen(this.instances, new int[]{epop, cov, label}, new String[]{"epop", "cov", "label"}, "geos.log.norm");
	}
	
	public int numOfFeatures(){
		return this.instances.get(0).features.size();
	}
}
