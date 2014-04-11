package entity.disamb.process;

import java.util.ArrayList;

import com.google.common.collect.Multimap;

import disRanking.Logistic;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.Instance;

import entity.disamb.IO;
import entity.disamb.ml.Arff;

public class Geos extends Entities {
	
	public final int epop = 3;
	public final int cov = 4;
	public final int label = 5;

	public Geos (Entities entities){
		this.list = entities.list;
	}
	
	public Geos (ArrayList<Entity> entities){
		this.list = entities;
	}
	
	public Geos (String file){
		this.list = IO.readData(file).list;
	}
	
	public int numOfFeatures(){
		return this.list.get(0).features.size();
	}
	
	@Override
	public void cleanFeatures(){
		for(Entity en: this.list){
			en.features.remove(7); // remove res
			en.features.remove(6); // remove popMovie
			en.features.remove(4); // remove wpop
			en.features.remove(3); // remove wcpop
		}
	}
	
	@Override
	public void takeLog(){
		Calculations calcs = new Calculations(this);
		calcs.logTransform(new int[]{cov});
	}
	
	@Override
	public void normalization(){
		Calculations calcs = new Calculations(this);
		calcs.minMaxNorm(new int[]{epop,cov});
	}
	
	@Override
	public void standardization(){
		Calculations calcs = new Calculations(this);
		calcs.meanNorm(new int[]{epop,cov});
	}

	public void rankByDis(String modelFile){
		String model = IO.readFile(modelFile);
		Logistic.calcScore(this.list, new int[] {epop, cov}, model);
		System.out.println("Scores are calculated!!");
		
		Multimap<String,Entity> geoGroups= Operations.groupBy(this,name);
		Operations.sortByGroup(geoGroups, this.list.get(0).features.size()-1, true);
		this.list = Operations.flatten(geoGroups).list;
	}
	/**
	 * Generate the .arff file in "./output/" path.
	 * @param relName
	 */
	@Override
	public void genArff(String relName){
		Arff.gen(this, new int[]{epop, cov, label}, new String[]{"epop", "cov", "class"}, relName);
	}
//	
//	/**
//	 * Generate Weka instances
//	 * @param relName : Relation name
//	 * @return : Weka Instances
//	 */
//	public Instances getInstances(String relName){
//		// create attributes
//		FastVector atts = addAtts(); 
//		
//		// add instances
//		return addInss(relName, atts);
//	}
//	
//	private FastVector addAtts(){
//		 Attribute epop = new Attribute("epop");
//		 Attribute cov = new Attribute("cov");
//		 
//		 // Declare the class attribute along with its values
//		 FastVector fvLabel = new FastVector(3);
//		 fvLabel.addElement("1");
//		 fvLabel.addElement("2");
//		 fvLabel.addElement("3");
//		 Attribute label = new Attribute("label", fvLabel);
//		 
//		 // Declare the feature vector
//		 FastVector fvAtts = new FastVector(3);
//		 fvAtts.addElement(epop);    
//		 fvAtts.addElement(cov);    
//		 fvAtts.addElement(label);  
//		 
//		 return fvAtts;
//	}
//	
//	private Instances addInss(String relName, FastVector fvAtts){
//		 Instances geoInss = new Instances(relName, fvAtts, this.list.size()); 
//		 // setting label attribute
//		 geoInss.setClassIndex(2);
//		 
//		 for(Entity en : this.list){
//			 Instance ins = new Instance(3);
//			 // set value for attributes
//			 ins.setValue((Attribute)fvAtts.elementAt(0), en.getFeature(epop));  
//			 ins.setValue((Attribute)fvAtts.elementAt(1), en.getFeature(cov));  
//			 ins.setValue((Attribute)fvAtts.elementAt(2), en.getFeature(label));      
//			 // add instance to the arff Intances
//			 geoInss.add(ins);
//		 }
//		 
//		 return geoInss;
//	}
}
