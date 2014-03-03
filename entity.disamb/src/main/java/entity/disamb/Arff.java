package entity.disamb;

import java.util.ArrayList;

public class Arff {

	/**
	 * Generates arff format of data 
	 * @param instances
	 * @param cols
	 * @param header
	 * @param fileName
	 */
	public static void gen(ArrayList<Instance> instances, int[] cols, String[] header, String fileName){
		String out = genHead(header, fileName);
		for(Instance ins : instances){
			for(int i = 0; i < cols.length; i ++)
				out += ins.features.get(cols[i]) + "\t";
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
