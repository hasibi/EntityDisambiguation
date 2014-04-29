package entity.disamb;

import entity.disamb.process.Geos;
import entity.disamb.process.IO;
import entity.disamb.process.Wikis;





public class Startup {
	
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
			disambiguate(wikiFile, geoFile);
		}

		System.out.println("\n============ Program execution Done!! ============");
		
	}
	
	private static void disambiguate(String wikiFile, String geoFile){
		System.out.println("Reading processed Wiki and Geo Entities");
		Wikis wikis = new Wikis(IO.readData(wikiFile).getList());		
		Geos geos = new Geos(IO.readData(geoFile).getList());
		
//		int[] rankCoefs = new int[]{9,4,1};
		int[] rankCoefs = new int[]{3,2,1};

		
		System.out.println("\n" +
				"***********************************************************************************\n" +
				"************** No FUSION - SEPARATE DISAMB FOR WIKI AND GEO ENTITIES **************");
		OneDomainDisamb odDisamb = new OneDomainDisamb(wikis, geos, rankCoefs);
		odDisamb.disambiguate();
		
		Wikis wikis1 = new Wikis(IO.readData(wikiFile).getList());		
		Geos geos1 = new Geos(IO.readData(geoFile).getList());
		System.out.println("\n" +
				"******************************************\n" +
				"************** EARLY FUSION **************");
		EarlyFusionDisamb efDisamb = new EarlyFusionDisamb(wikis1, geos1, rankCoefs);
		efDisamb.disambiguate("earlyFusion");
		
		Wikis wikis2 = new Wikis(IO.readData(wikiFile).getList());		
		Geos geos2 = new Geos(IO.readData(geoFile).getList());
		System.out.println("\n" +
				"*****************************************\n"  +
				"************** LATE FUSION **************");
		System.out.println("Reading processed Wiki and Geo Entities");
		LateFusionDisamb lfDisamb = new LateFusionDisamb(wikis2, geos2, rankCoefs);
		lfDisamb.disambiguate("lateFusion");
		
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
}