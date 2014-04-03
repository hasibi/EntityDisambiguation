package entity.disamb;

import java.io.File;
import java.util.ArrayList;


import com.google.common.collect.Multimap;

import entity.disamb.ml.Learning;
import entity.disamb.process.*;





public class Startup {
//	static final int id = 0;
//	static final int name =1;
//	static final int type = 2;
//	static final int wcpop = 3;
//	static final int wpop = 4;
//	static final int epop = 5;
//	static final int mvpop = 6;
	static final int src = 7;
//	static final int cov = 8;
//	static final int label = 9;
	
	public static void main(String[] args) {
		//===========================operations====================================
		int id ;
		
		if ((id = searchInArgs(args,"-prep"))!=-1){
			String data = IO.checkFile(args[args.length-1]);	
			PreProcessing.process(data);
		}
		else if ((id = searchInArgs(args,"-disamb"))!=-1){
			String wikiFile = args[id+1];
			String geoFile = args[id+2];
			Disamb.disambiguate(wikiFile, geoFile);
		}
		
		// args: domaninName(e.g. wiki) LogisticModelFile data
		else if((id = searchInArgs(args, "-logRank")) != -1){
			String domainName = args[id+1];
			String modelFile = IO.checkFile(args[id+2]);
			String data = IO.checkFile(args[args.length-1]);				
			Entities entities = IO.readData(data);
			if(domainName.matches("wiki")){
				Wikis wikis = (Wikis) entities;
				wikis.rankByDis(modelFile);
				IO.writeData(wikis, "wikis.log.norm.rank");
			}
			else if(domainName.matches("geo")){
//				4.56	-0.64	-2.21
//				2.66	-3.21	0.04
//				-4.22	2.58	1.71
				Geos geos = (Geos) entities;
				geos.rankByDis(modelFile);
				IO.writeData(geos, "geos.log.norm.rank");
			}
		}
		
		System.out.println("Program execution Done!!");
		
//		ArrayList<Integer> list = new ArrayList<Integer>();
//		list.add(100);
//		test(list);
//		System.out.print(list.get(0));
		
	}
	
	/**
	 * Searches for the given option in the input command (i.e. String[] args)
	 */
	private static int searchInArgs(String[] args, String s){
		for(int i=0; i<args.length; i++)
			if(args[i].matches(s))
				return i;
		return -1;
	}
	
//	private static void test(ArrayList<Integer> list){
//		ArrayList<Integer> newList = new ArrayList<Integer>();
//		newList.add(1);
//		list = newList;
//	}
	
}