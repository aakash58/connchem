package data;
import static data.YAMLinterface.*;

import java.util.ArrayList;
import java.util.HashMap;

import simulations.models.Anchor;
import simulations.models.Boundary;
import simulations.models.Molecule;

public class State {
	// An ArrayList of particles that will fall on the surface
	public static ArrayList<Molecule> molecules = new ArrayList<Molecule>();
	public static ArrayList<Anchor> anchors = new ArrayList<Anchor>();
	// A list we'll use to track fixed objects

	public static HashMap<String,Integer> moleculesAdded = new HashMap<String,Integer>();
	
	public State()
	{
	
	}
	
	/*
	 * Unit, set and sim status
	 */
	private static int currentUnitNumber = 0;
	private static int currentSimNumber = 0;
	private static int currentSetNumber = 0;

	//public static void setCurrentUnit(int currUnit) {}
	//public static void setCurrentSim(int currSim) {}
	//public static void setCurrentSet(int currSet) {}
	
	
/*
	public static int getCurrentUnitNumber() {
		return currentUnitNumber;
	}
	public static int getCurrentSimNumber() {
		return currentSimNumber;
	}
	public static int getCurrentSetNumber() {
		return currentSetNumber;
	}*/
	public static int getCompoundNum()
	{
		return molecules.size();
	}
	public static int getCompoundNum(String compoundName)
	{
		int count = 0;
		for( int i = 0 ;i<molecules.size();i++)
		{
			if(molecules.get(i).getName().equals(compoundName))
				count++;
		}
		return count;
	}

	public static String getCurrentUnitName() {
		try {
			String output = getUnitName(currentUnitNumber);
			return output;
		} catch (Exception e) {
			System.out.println("Unit Name Does Not Exist");
		}
		return "Default";
	}
	public static String getCurrentSimName() {
		try {
			String output = getSimName(currentUnitNumber, currentSimNumber);
			return output;
		} catch (Exception e) {
			System.out.println("Simulation Name Does Not Exist");
		}
		return "Default";
	}
	
	public static void reset()
	{
		moleculesAdded.clear();
	}
	/*
	 * Default settings
	 */
	public static final Float defaultTemperature = 25.0f; // default temp for all sims/sets is 0° Celsius
	public static final Float defaultMinTemperature = -10.f;
	public static final Float defaultMaxTemperature = 200.f;
	
	/*
	 * Default compound settings
	 */
	public static final String defaultCompoundName = "Generic";
	public static final String defaultCompoundFormula = "G";
	public static final int defaultCompoundId = 0;
	public static final int polarity = 0;
	public static final int charge = 0;
	public static final Float density = 1.f;
	public static final Float boilingPointCelsius = 100.f;
	public static final Float freezingPointCelsius = 0.f;
}
