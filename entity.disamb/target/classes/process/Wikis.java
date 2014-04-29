package entity.disamb.process;

import java.util.ArrayList;

import com.google.common.collect.Multimap;

import disRanking.Logistic;

import entity.disamb.ml.Arff;


public class Wikis extends Entities implements Cloneable{	
	public final int wcpop = 3;
	public final int wpop = 4;
	public final int epop = 5;
	public final int cov = 6;
	public final int label = 7;
	
	public Wikis(){
		this.list = new ArrayList<Entity>();
	}
	public Wikis(Entities entities){
		this.list = entities.list;
	}
	
	public Wikis(ArrayList<Entity> entities){
		this.list = entities;
	}
	
	@Override
	public void cleanFeatures(){
		for(Entity en: this.list){
			en.features.remove(7); // remove res 
			en.features.remove(6); // remove popMovie
		}
	}
	
	@Override
	public void takeLog(){
		Calculations calcs = new Calculations(this);
		calcs.logTransform(new int[] {wcpop, wpop, cov});
	}
	@Override
	public void normalization(){
		Calculations calcs = new Calculations(this);
		calcs.minMaxNorm(new int[] {wcpop,wpop,epop,cov});
	}
	
	@Override
	public void standardization(){
		Calculations calcs = new Calculations(this);
		calcs.meanNorm(new int[] {wcpop,wpop,epop,cov});
	}
	
	public void rankByDis(String modelFile){
		String model = IO.readFile(modelFile);
		Logistic.calcScore(this.list, new int[] {wcpop,wpop,epop,cov}, model);
		// sorts according to the last entry of each instance, which is score of entities.
		System.out.println("Scores are calculated!!");
		Multimap<String,Entity> wikiGroups= Operations.groupBy(this,name);
		Operations.sortByGroup(wikiGroups, this.list.get(0).features.size()-1, true);
		this.list = Operations.flatten(wikiGroups).list;
	}
	
	@Override
	public void genArff(String fileName){
		Arff.gen(this, new int[]{wcpop,wpop,epop,cov, label}, new String[]{"wcpop", "wpop", "epop", "cov", "label"},fileName);
	}
	
	public int numOfFeatures(){
		return this.list.get(0).features.size();
	}
	
}
