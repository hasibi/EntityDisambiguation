package entity.disamb.process;

import java.util.ArrayList;

import com.google.common.collect.Multimap;

import disRanking.Logistic;

import entity.disamb.IO;
import entity.disamb.ml.Arff;


public class Wikis extends Entities{	
	final int wcpop = 3;
	final int wpop = 4;
	final int epop = 5;
	final int cov = 6;
	final int label = 7;
	
	public Wikis(ArrayList<Entity> entities){
		this.list = entities;
	}
	
	public void cleanfeatures(){
		for(Entity en: this.list){
			en.features.remove(7); // remove res 
			en.features.remove(6); // remove popMovie
		}
	}
	
	public void takeLog(){
		Calculations.logTransform(this, new int[] {wcpop, wpop, cov});
	}
	
	public void norm(){
		Calculations.minMaxNorm(this, new int[] {wcpop,wpop,epop,cov});
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
	
	public void genArff(String fileName){
		Arff.gen(this, new int[]{wcpop,wpop,epop,cov, label}, new String[]{"wcpop", "wpop", "epop", "cov", "label"},fileName);
	}
	
	public int numOfFeatures(){
		return this.list.get(0).features.size();
	}
	
}
