package entity.disamb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Multimap;

import entity.disamb.process.*;



public class IO {
	
	final static String outputPath = "./output/";
	
	/**
	 * Read input data from the given absolute path
	 * @param file
	 * @return
	 */
	public static Entities readData(String file){
		checkFile(file);
		Entities entities = new Entities();
		String line = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			while((line = br.readLine())!= null){
				String[] arr = line.split("\t");
				ArrayList<String> arrList = new ArrayList<String>(Arrays.asList(arr));
				Entity entity =  new Entity(arrList);
				if(entity.features.size() != 0) // check to not add an empty line
					entities.add(entity);
			}
			br.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return entities;
	}
	
	/**
	 * Write data to the path = "./output/" + fileName
	 * @param entities
	 * @param fileName
	 */
	public static void writeData(Entities entities, String fileName){
		String out = listToStr(entities.getList());
		writeToFile(out, fileName + ".txt");
	}
	
	public static void writeData(Multimap<String, Entity> entities, String fileName){
		String out = groupsToStr(entities);
		writeToFile(out, fileName + ".txt");
	}
	
	public static String readFile(String file){
		checkFile(file);
		String out = "";
		String line = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			while((line = br.readLine()) != null){
				out += line + "\n";
			}
			br.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return out;
	}
	
	public static void writeToFile(String str,String fileName){
		BufferedWriter writer ;	
		try {
			writer = new BufferedWriter(new FileWriter( outputPath + fileName));
			writer.write(str);
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String listToStr(ArrayList<Entity> entities){
		String str = "";
		// to add a line after each group of entities
		//String prevName = entities.get(0).getFeature(1);
		for(int i = 0; i< entities.size(); i++){
			//if (! (prevName.matches(en.getFeature(1))))
			//	str += "\n";
			//prevName = en.getFeature(1);
			
			Entity en = entities.get(i);
			for(String s:en.features)
				str += s + "\t";
			if (i!= entities.size()-1)
				str+= "\n";
		}
		return str;
	}
	
	private static String groupsToStr(Multimap<String, Entity> groups){
		String str = "";
		ArrayList<String> keys = new ArrayList<String> (groups.keySet());
		Collections.sort(keys);
		for(String key: keys){
			//List<String> vals = (List<String>) map.get(key);
			//str += key + "\t";
			ArrayList<Entity> entities = new ArrayList<Entity>(groups.get(key));
			for(Entity en:entities){
				for(String s:en.features)
					str += s+"\t";
				str += "\n";
			}
			str += "\n";
		}
		return str;
	}
	
	public static void Hashmap(String file){
		BufferedReader br = null;
		List<String[]> ids = new ArrayList<String[]>();
		try {
			br = new BufferedReader(new FileReader(file));
			String line = "";
			while((line = br.readLine())!= null){
				byte[] bytes = line.getBytes("UTF-8");
				MessageDigest md;
				try {
					md = MessageDigest.getInstance("MD5");
					byte[] digest = md.digest(bytes);
					String[] ss = {line, digest.toString()};
					ids.add(ss);
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			String output = "";
			for(String[] ss: ids){
				output += ss[0] + "\t" + ss[1] + "\n";
			}
			System.out.println("writing to the file");
			writeToFile(output, "entityIds.hash");
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String checkFile(String fileName){
		File f = new File(fileName);
		if(! f.exists()){
			System.out.println(" ERORR: " + f.getName() + " does not exist.");
			System.exit(0);
		}
		return fileName;
	}
}
