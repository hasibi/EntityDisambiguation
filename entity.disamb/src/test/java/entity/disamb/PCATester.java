package entity.disamb;

import weka.core.Instances;
import entity.disamb.ml.Arff;
import entity.disamb.ml.PCA;
import entity.disamb.process.Wikis;

public class PCATester {

	public static void main(String[] args){
		Wikis wikis = new Wikis(IO.readData("./output/wikis.log.norm.txt").getList());
		Arff.gen(wikis, new int[] {wikis.wcpop, wikis.wpop, wikis.epop, wikis.label}, new String[] {"wcpop", "wpop", "epop", "label"}, "wikis.log.norm");
		Instances inss = Arff.read("wikis.log.norm");
		PCA pca = new PCA();
		pca.learnPCA(inss);//(new String[] {"wcpop", "wpop", "epop"});
	}
}
