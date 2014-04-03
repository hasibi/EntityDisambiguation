package entity.disamb;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import disRanking.Logistic;

import entity.disamb.process.*;



public class LogisticTest {
	
	/**
	 * Test the relative distance calculation.
	 */
	@Test
	public void testGetRelDis() { //static void main (String[] args){
		ArrayList<String> features = new ArrayList<String>();
		features.add(Double.toString(0.08));
		features.add(Double.toString(0.59));
		Entity ins = new Entity(features);
		String[] line = new String[]{"5.58", "-7.18", "0.97"};
		
		/*
		 * (0.08162686029786 * 5.58) - (0.5957190423832721* 7.18) + 0.97 = -2.85178484385
		 * (5.58 * 5.58) + ( -7.18 * -7.18) = 82.6888
		 * -2.85178484385 / sqrt(82.6888) = -0.3136125342*/
		assertEquals("relative distance should be", -0.310095, Logistic.getRelDis(ins, new int[]{0,1} , line), 0.000001);
//		double dis = Logistic.getRelDis(ins, new int[]{0,1} , line);
//		System.out.println("relative distance should be -0.3136125342" + dis);
	}

}
