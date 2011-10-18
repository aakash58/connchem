package simulations.models;

import java.util.ArrayList;
import java.util.HashMap;

import Util.SVGReader;

import model.DBinterface;

// This class maintains all information about all compounds in a Simulation
public class Compound {
	public static ArrayList<String> names=new ArrayList<String>();
	public static ArrayList<Integer> counts = new ArrayList<Integer>();
	public static ArrayList<Integer> caps = new ArrayList<Integer>();
	public static ArrayList<Float> fTemp = new ArrayList<Float>();
	public static ArrayList<Float> bTemp = new ArrayList<Float>();
	public static ArrayList<Float> minLiquidEnergy = new ArrayList<Float>();
	public static ArrayList<Float> rangeLiquidEnergy = new ArrayList<Float>();
	public static ArrayList<Float> minGasEnergy = new ArrayList<Float>();
	public static ArrayList<Float> moleculeWeight = new ArrayList<Float>();
	
	public static void setProperties(){
		fTemp = new ArrayList<Float>();
		bTemp = new ArrayList<Float>();
		minLiquidEnergy = new ArrayList<Float>();
		rangeLiquidEnergy = new ArrayList<Float>();
		minGasEnergy = new ArrayList<Float>();
		for (int i=0; i<names.size();i++){
			float freezingTemp = DBinterface.getCompoundFreezingPointCelsius(names.get(i));
			float boilingTemp = DBinterface.getCompoundBoilingPointCelsius(names.get(i));
			fTemp.add(freezingTemp);
			bTemp.add(boilingTemp);
			float[] liquidEnergy = DBinterface.getMinimumLiquidEnergy(names.get(i));
			minLiquidEnergy.add(liquidEnergy[0]);
			rangeLiquidEnergy.add(liquidEnergy[1]);
			float gasEnergy = DBinterface.getMinimumGasEnergy(names.get(i));
			minGasEnergy.add(gasEnergy);
			//Set up molecule weight
			float weight = DBinterface.getCompoundMass(names.get(i));
			moleculeWeight.add(weight);
			
		}
	}
	public static int getMoleculeNum(int index){
		int num = counts.get(index);
		//System.out.println("Molecule number is "+num);
		return num;
	}
	public static int getMoleculeCap(int index){
		int cap = caps.get(index);
		//System.out.println("Molecule cap is "+cap);
		return cap;
	}
	

	
	
}
