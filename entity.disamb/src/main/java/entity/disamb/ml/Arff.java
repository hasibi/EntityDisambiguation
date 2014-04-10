package entity.disamb.ml;


import java.io.BufferedReader;
import java.io.FileReader;

import weka.core.Instances;

import entity.disamb.IO;
import entity.disamb.process.Entities;
import entity.disamb.process.Entity;



public class Arff {
	final static String outputPath = "./output/";

	/**
	 * Read .arff file from output directory.
	 * @param fileName
	 * @return
	 */
	public static Instances read(String fileName){
		Instances data = null;
		try {
			 BufferedReader reader = new BufferedReader(new FileReader(outputPath+ fileName + ".arff"));
			 data = new Instances(reader);
			 reader.close();
			 // setting class attribute
			 data.setClassIndex(data.numAttributes() - 1);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return data;
	}
	
	/**
	 * Generate .arff files
	 * @param entities
	 * @param cols
	 * @param header
	 * @param fileName
	 */
	public static void gen(Entities entities, int[] cols, String[] header, String fileName){
		String out = genHead(header, fileName);
		for(int i = 0; i < entities.size(); i++){
			Entity en = entities.getEntity(i);
			for(int j = 0; j < cols.length; j ++)
				out += en.getFeature(cols[j]) + "\t";
			if(i != entities.getList().size()-1)
				out += "\n";
		}
		IO.writeToFile(out, fileName + ".arff");
	}
	
	private static String genHead(String[] header, String fileName){
		String h = "@relation " + fileName + "\n";
		for(int i=0; i<header.length-1; i++)
			h += "@attribute " + header[i] + " numeric\n";
		h += "@attribute " + header[header.length-1] + " {1, 2, 3}\n";
		h += "@data\n";
		return h;
	}
}
