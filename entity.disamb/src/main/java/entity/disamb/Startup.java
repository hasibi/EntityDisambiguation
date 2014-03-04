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
//		String file = checkFile(args[args.length-1]);				
//	
//		ArrayList<Instance> instances = IO.readData(file);
		//===========================operations====================================
		int id ;
		//** args: data 
		if((id = searchInArgs(args,"-rmRed"))!=-1) {
			String data = checkFile(args[args.length-1]);				
			ArrayList<Instance> instances = IO.readData(data);
			int popCol = Integer.parseInt(args[id+1]);
			ArrayList<Instance> insList = Operations.rmRedundants(instances, popCol);
			IO.writeData(insList,"gt.rmRed" );
		}
		//** args: groupColId sortColId data
		else if((id = searchInArgs(args,"-groupSort"))!=-1) {
			String data = checkFile(args[args.length-1]);				
			ArrayList<Instance> instances = IO.readData(data);
			int groupColId = Integer.parseInt(args[id+1]);
			int sortColId = Integer.parseInt(args[id+2]);
			ArrayList<Instance> sortedList = groupSort(instances, groupColId, sortColId);
			IO.writeData(sortedList,"groupSort" );
		}
		// Split entities to "wiki" and "geo" entities, takes log and normalize entities.
		// The output would be text file and arff file for each domain.
		///** args:        (no args)
		else if((id = searchInArgs(args,"-splitDomains"))!=-1) {
			String data = checkFile(args[args.length-1]);				
			ArrayList<Instance> instances = IO.readData(data);
			Wikis wikis = new Wikis(Operations.splitDomain(instances, "Wiki", src));
			wikis.cleanfeatures();
			Operations.sort(wikis.instances, 1);
			IO.writeData(wikis.instances, "wikis");
			// Wiki data pre-processing
			wikis.takeLog();
			wikis.NormMinMax();
			IO.writeData(wikis.instances, "wikis.log.norm");
			wikis.genArff("wikis.log.norm");			
			
			Geos geos = new Geos(Operations.splitDomain(instances, "Geo", src));
			geos.cleanFeatures();
			Operations.sort(geos.instances, 1);
			IO.writeData(geos.instances, "geos");
			// log transformation
			geos.takeLog();
			geos.normMinMax();
			IO.writeData(geos.instances, "geos.log.norm");
			geos.genArff("goes.log.norm");
		}
		// assumptoin is that for all domains "coverage" is the latest feature and label and predictions are stored afterwards.
		//** args: domainName1(e.g. wiki) data1 domainName2(e.g. geo) data2
		else if ((id = searchInArgs(args, "-mergeDomains")) != -1){
			String wikiData = "", geoData = "";
			if(args[id+1].matches("wiki") && args[id+3].matches("geo")){
				wikiData = checkFile(args[id+2]);
				geoData = checkFile(args[id+4]);
			}
			else if (args[id+1].matches("geo") && args[id+3].matches("wiki")){
				geoData = checkFile(args[id+2]);
				wikiData = checkFile(args[id+4]);
			}
			Wikis wikis = new Wikis(IO.readData(wikiData));
			Geos geos = new Geos(IO.readData(geoData));
			
			// adding wiki instances
			System.out.println("Adding wiki entities ...");
			ArrayList<Instance> allInstances = wikis.instances;
			// adding Geo instances
			System.out.println("Adding geo entities ...");
			for(Instance ins : geos.instances){
				ArrayList<String> geoFeatures = new ArrayList<String>();
				for(int i = 0; i<geos.epop; i ++)
					geoFeatures.add(ins.features.get(i));
				geoFeatures.add(""); // add null String for "wcpop"
				geoFeatures.add(""); // add null string for "wpop"
				for(int i = geos.epop; i<geos.numOfFeatures(); i ++)
					geoFeatures.add(ins.features.get(i));
				
				allInstances.add(new Instance(geoFeatures));
			}
			System.out.println("sorting all entities ...");
			ArrayList<Instance> sortedList = groupSort(allInstances, 1, wikis.label+2); // rank base on probability for label 1
			System.out.println("Writing all instances in allInstacnes.prd.txt ...");
			IO.writeData(sortedList, "allInstacnes.prd.txt");
			
			
		}
		// args: domaninName(e.g. wiki) LogisticModelFile data
		else if((id = searchInArgs(args, "-logRank")) != -1){
			String domainName = args[id+1];
			String modelFile = checkFile(args[id+2]);
			String data = checkFile(args[args.length-1]);				
			ArrayList<Instance> instances = IO.readData(data);
			if(domainName.matches("wiki")){
				Wikis wikis = new Wikis(instances);
				wikis.rank(modelFile);
				IO.writeData(wikis.instances, "wikis.log.norm.rank");
			}
			else if(domainName.matches("geo")){
//				4.56	-0.64	-2.21
//				2.66	-3.21	0.04
//				-4.22	2.58	1.71
				Geos geos = new Geos(instances);
				geos.rank(modelFile);
				IO.writeData(geos.instances, "geos.log.norm.rank");
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
	
	private static String checkFile(String fileName){
		File f = new File(fileName);
		if(! f.exists()){
			System.out.println(" ERORR: " + f.getName() + " does not exist.");
			System.exit(0);
		}
		return fileName;
	}
	
	private static ArrayList<Instance> groupSort(ArrayList<Instance> instances, int groupColId, int sortColId){
		Multimap<String,Instance> groups= Operations.groupBy(instances,groupColId);
		Operations.sortByGroup(groups, sortColId);
		//Ranker.rank(groups);
		ArrayList<Instance> insList = Operations.flatten(groups);
		return insList;
	}
	
}