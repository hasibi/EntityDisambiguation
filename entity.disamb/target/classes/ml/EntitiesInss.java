package entity.disamb.ml;

import java.util.ArrayList;

import weka.core.Instance;
import weka.core.Instances;

import entity.disamb.process.Entities;
import entity.disamb.process.Entity;

public class EntitiesInss {
	private Entities entities;
	private Instances instances;
	private ArrayList<String> classes;
	private ArrayList<double[]> probabilities;
	private ArrayList<Double> scores;
	
	public EntitiesInss(Entities ens, Instances inss) throws Exception{
		if(! checkSize(ens, inss))
			throw new Exception();
		entities = ens;
		instances = inss;
		classes = new ArrayList<String>();
		probabilities = new ArrayList<double[]>();
		scores = new ArrayList<Double>();
	}
	
	public EntitiesInss(Entities ens, Instances inss, ArrayList<double[]> probs) throws Exception{
		if(! checkSize(ens, inss))
			throw new Exception();
		entities = ens;
		classes = new ArrayList<String>();
		probabilities = probs;
		scores = new ArrayList<Double>();
	}	
	
	private boolean checkSize(Entities ens, Instances inss){
		return ens.size() == inss.numInstances();
	}
	
	public Entities getEntities(){
		return this.entities;
	}
	public Instances getInstances(){
		return this.instances;
	}
	
	public ArrayList<double[]> getProbabilities(){
		return this.probabilities;
	}
	public void setProbabilities(ArrayList<double[]> prds){
		this.probabilities = prds;
	}
	public ArrayList<String> getClasses(){
		return this.classes;
	}
	public ArrayList<Double > getScores(){
		return this.scores;
	}
	
	
	
	
	public int size(){
		return this.entities.size();
	}
	
	public Instance getInstance(int i){
		return this.instances.instance(i);
	}
	
	public double[] getprop(int i){
		return this.probabilities.get(i);
	}
	
	public void addProb(int i, double[] pred){
		this.probabilities.add(i,	pred);
	}
	
	public void addClass(int i, String cls){
		this.classes.add(i, cls);
	}
	
	public void addScore(int i, Double score){
		this.scores.add(score);
	}
	
	/**
	 * concatenate entities, predictions, probabilities and scores.
	 * @return a list of entities
	 */
	public Entities concatEntityPred(){
		Entities newEns = new Entities(); // to save new entities
		for(int i = 0; i< this.entities.size(); i ++){ 
			Entity en = this.entities.getEntity(i);
			en.addFeature(this.classes.get(i).toString());//add predicted classes to each entity
			for(Double d: this.probabilities.get(i)){ // add probabilities to each entity
				en.addFeature(d.toString());
			}
			if(this.scores.size()>0){ // add scores to each entity
				en.addFeature(this.scores.get(i).toString());
			}
			newEns.add(en);
		}
		return newEns;
	}
}
