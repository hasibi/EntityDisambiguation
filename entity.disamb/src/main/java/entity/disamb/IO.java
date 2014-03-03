package entity.disamb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

public class IO {
	
	final static String outputPath = "./output/";
	
	public static ArrayList<Instance> readData(String file){
		ArrayList<Instance> instances = new ArrayList<Instance>();
		String line = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			while((line = br.readLine())!= null){
				String[] arr = line.split("\t");
				ArrayList<String> arrList = new ArrayList<String>(Arrays.asList(arr));
				Instance instance =  new Instance(arrList);
				if(instance.features.size() != 0)
					instances.add(instance);
			}
			br.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return instances;
	}
	
	public static void writeData(ArrayList<Instance> instances, String fileName){
		String out = listToStr(instances);
		writeToFile(out, fileName + ".txt");
	}
	
	public static void writeData(Multimap<String, Instance> instances, String fileName){
		String out = groupsToStr(instances);
		writeToFile(out, fileName + ".txt");
	}
	
	public static String readFile(String file){
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
	
	private static String listToStr(ArrayList<Instance> instances){
		String str = "";
		// to add a line after each group of entities
		String prevName = instances.get(0).features.get(1);
		for(Instance ins: instances){
			if (! (prevName.matches(ins.features.get(1))))
				str += "\n";
			prevName = ins.features.get(1);
			
			
			for(String s:ins.features)
				str += s + "\t";
			str+= "\n";
		}
		return str;
	}
	
	private static String groupsToStr(Multimap<String, Instance> groups){
		String str = "";
		ArrayList<String> keys = new ArrayList<String> (groups.keySet());
		Collections.sort(keys);
		for(String key: keys){
			//List<String> vals = (List<String>) map.get(key);
			//str += key + "\t";
			ArrayList<Instance> instances = new ArrayList<Instance>(groups.get(key));
			for(Instance ins:instances){
				for(String s:ins.features)
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
}
