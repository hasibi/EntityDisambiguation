package entity.disamb;

import entity.disamb.process.Geos;
import entity.disamb.process.Wikis;

public class PCABasedDisamb {
	private Wikis wikis;
	private Geos geos;
	int[] gains;
	
	public PCABasedDisamb(Wikis wikis, Geos geos, int[] gains){
		this.wikis = wikis;
		this.geos = geos;
		this.gains= gains;		
	}
	
	public void disambiguate(){
		System.out.println("=== Standardizing Wiki Entities ===");
		System.out.println("=== Standardizing geo Entities ===");
		System.out.println("=== Calculating PCA of Wiki Entities ===");
		System.out.println("=== Merging Wiki and Geo Entities ===");
		System.out.println("=== Learning all Entities ===");
		System.out.println("=== Evaluating all Entities ===");

	}
}
