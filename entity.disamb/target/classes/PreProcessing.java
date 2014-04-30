package entity.disamb;

import entity.disamb.process.Entities;
import entity.disamb.process.Geos;
import entity.disamb.process.IO;
import entity.disamb.process.Operations;
import entity.disamb.process.Wikis;

public class PreProcessing {
	static final int id = 0;
	static final int name =1;
	static final int type = 2;
	static final int wcpop = 3;
	static final int wpop = 4;
	static final int epop = 5;
	static final int mvpop = 6;
	static final int src = 7;
	static final int cov = 8;
	static final int label = 9;
	
	/****** PIPELINE OPERATIONS:
	 * Remove redundant entities (i.e. entities with same id, but different features)
	 * Split entities to wiki and geo entities
	 * Clean unneeded features from wiki and geo entities
	 * Take log of "wcpop" , "wpop", "cov" features
	 * Normalize all features
	 * Write wiki and geo entities in separate files
	 * @param file
	 */
	public static void process(String file){
		//pre-processing steps
		Entities entities = IO.readData(file);
		
		System.out.println("Removing redundant entities ...");
		entities.rmRedundants(epop);
		System.out.println("Removing POI entites ...");
		entities.rmPois();
		System.out.println("Removing single domain ambigious entities ...");
		entities.rmOneDomainEns();
		System.out.println("Writing all entites ...");
		IO.writeData(entities,"gt.rmRed" );

		
		System.out.println("Splitting Geo and Wiki entities ...");
		Wikis wikis = new Wikis(Operations.splitDomain(entities, "Wiki", src).getList());
		Geos geos = new Geos(Operations.splitDomain(entities, "Geo", src).getList());

		System.out.println("\n=== Processing Wiki entities ===");
		process(wikis);
		System.out.println("Writing  Wiki entities to the file ...");
		IO.writeData(wikis, "wikis.log.norm");
		
		System.out.println("\n=== Processing Geo entities ===");
		process(geos);
		System.out.println("Writing  Geo entities to the file ...");
		IO.writeData(geos, "geos.log.norm");
	}
//	
//	private static void processWikis(Wikis wikis){
//		System.out.println("Cleaning unneccessary features of Wiki entities ...");
//		wikis.cleanfeatures();
////		IO.writeData(wikis, "wikis");
//		System.out.println("Taking log of Wiki entities ...");
//		wikis.takeLog();
////		IO.writeData(wikis, "wikis.log");
//		System.out.println("Normalizing Wiki entities ...");
//		wikis.normalization();
////		IO.writeData(wikis, "wikis.log.norm");
//		System.out.println("Sorting Wiki entities by name ...");
//		Operations.sort(wikis, name, true);
//		System.out.println("Writing Wiki entities to the file ...");
//		IO.writeData(wikis, "wikis.log.norm");
//	}
	
	private static void process(Entities ens){
		System.out.println("Cleaning unneccessary features ...");
		ens.cleanFeatures();
		System.out.println("Taking log ...");
		ens.takeLog();
		System.out.println("Normalizing features ...");
		ens.standardization();
		System.out.println("Sorting entities by name ...");
		Operations.sort(ens, name, true);
	}
}
