package entity.disamb;

import java.io.File;
import java.util.ArrayList;


import com.google.common.collect.Multimap;




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
		//set parameters 
		checkFile(args[args.length-1]);
		String file = args[args.length-1];				
	
		ArrayList<Instance> instances = IO.readData(file);
		//===========================operations====================================
		int id ;
		if((id = searchInArgs(args,"-rmRed"))!=-1) {
			int popCol = Integer.parseInt(args[id+1]);
			ArrayList<Instance> insList = Operations.rmRedundants(instances, popCol);
			IO.writeData(insList,"gt.rmRed" );
		}
		// the 1st arg after groupSort is groupingId and 2nd one is sortId
		else if((id = searchInArgs(args,"-groupSort"))!=-1) {
			int groupingId = Integer.parseInt(args[id+1]);
			int sortCol = Integer.parseInt(args[id+2]);
			Multimap<String,Instance> groups= Operations.groupBy(instances,groupingId);
			Operations.sortByGroup(groups, sortCol);
			//Ranker.rank(groups);
			ArrayList<Instance> insList = Operations.flatten(groups);
			IO.writeData(insList,"groupSort" );
		}
		// Has no arguments. Split entities to "wiki" and "geo" entities, takes log and normalize entities.
		// The output would be text file and arff file for each domain.
		else if((id = searchInArgs(args,"-splitDomain"))!=-1) {
			Wikis wikis = new Wikis(Operations.splitDomain(instances, "Wiki", src));
			wikis.cleanfeatures();
			Operations.sort(wikis.list, 1);
			IO.writeData(wikis.list, "wikis");
			// Wiki data pre-processing
			wikis.takeLog();
			wikis.NormMinMax();
			IO.writeData(wikis.list, "wikis.log.norm");
			wikis.genArff("wikis.log.norm");			
			
			Geos geos = new Geos(Operations.splitDomain(instances, "Geo", src));
			geos.cleanFeatures();
			Operations.sort(geos.list, 1);
			IO.writeData(geos.list, "geos");
			// log transformation
			geos.takeLog();
			geos.normMinMax();
			IO.writeData(geos.list, "geos.log.norm");
			geos.genArff("goes.log.norm");
		}
		// The first argument is domain. The second argument is the file that contains logistic model.
		// the last argument should be instances of one domain (i.e. wiki or geo entiies)
		else if((id = searchInArgs(args, "-logRank")) != -1){
			String domainName = args[id+1];
			checkFile(args[id+2]);
			String modelFile = args[id+2];
			if(domainName.matches("wiki")){
				Wikis wikis = new Wikis(instances);
				wikis.rank(modelFile);
				IO.writeData(wikis.list, "wikis.log.norm.rank");
			}
			else if(domainName.matches("geo")){
//				4.56	-0.64	-2.21
//				2.66	-3.21	0.04
//				-4.22	2.58	1.71
				Geos geos = new Geos(instances);
				geos.rank(modelFile);
				IO.writeData(geos.list, "geos.log.norm.rank");
			}
		}
		
		System.out.println("Program execution finished!!");
	}
	
	/**
	 * This method searches for an option in the input command of users (String[] args)
	 */
	private static int searchInArgs(String[] args, String s){
		for(int i=0; i<args.length; i++)
			if(args[i].matches(s))
				return i;
		return -1;
	}
	
	private static void checkFile(String fileName){
		File f = new File(fileName);
		if(! f.exists()){
			System.out.println(" ERORR: " + f.getName() + " does not exist.");
			System.exit(0);
		}
	}
	
}