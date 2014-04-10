package entity.disamb;


import entity.disamb.process.Geos;
import entity.disamb.process.Wikis;

public class Disamb {
	
	public static void disambiguate(String wikiFile, String geoFile){
		System.out.println("Reading processed Wiki and Geo Entities");
		Wikis wikis = new Wikis(IO.readData(wikiFile).getList());		
		Geos geos = new Geos(IO.readData(geoFile).getList());
		
		DomainBasedDisamb dbDisamb = new DomainBasedDisamb(wikis, geos, new int[] {9,4,1});
		dbDisamb.disambiguate();
		
	}
	
	
}
