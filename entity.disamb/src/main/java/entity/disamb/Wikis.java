package entity.disamb;

import java.util.ArrayList;

import com.google.common.collect.Multimap;

public class Wikis {
	ArrayList<Instance> list;
	
	final int id = 0;
	final int name =1;
	final int type = 2;
	final int wcpop = 3;
	final int wpop = 4;
	final int epop = 5;
	final int cov = 6;
	final int label = 7;
	
	public Wikis(ArrayList<Instance> instances){
		this.list = instances;
	}
	
	public void cleanfeatures(){
		for(Instance ins: this.list){
			ins.features.remove(7); // remove res 
			ins.features.remove(6); // remove popMovie
		}
	}
	
	public void takeLog(){
		Calc.logTransform(this.list, new int[] {wcpop, wpop, cov});
	}
	
	public void NormMinMax(){
		Calc.minMaxNorm(this.list, new int[] {wcpop,wpop,epop,cov});
	}
	
	public void rank(String modelFile){
		String model = IO.readFile(modelFile);
		Logistic.calcScore(this.list, new int[] {wcpop,wpop,epop,cov}, model);
		// sorts according to the last entry of each instance, which is score of instances.
		System.out.println("Scores are calculated!!");
		Multimap<String,Instance> wikiGroups= Operations.groupBy(this.list,name);
		Operations.sortByGroup(wikiGroups, this.list.get(0).features.size()-1);
		this.list = Operations.flatten(wikiGroups);

	}
	
	public void genArff(String fileName){
		Arff.gen(this.list, new int[]{wcpop,wpop,epop,cov, label}, new String[]{"wcpop", "wpop", "epop", "cov", "label"},fileName);
	}
	
	
}
