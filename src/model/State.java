package model;
import static model.YAMLinterface.*;

import java.util.ArrayList;
import view.Boundary;
import view.Molecule;

public class State {
	// An ArrayList of particles that will fall on the surface
	public static ArrayList<Molecule> molecules = new ArrayList<Molecule>();
	// A list we'll use to track fixed objects
	public static Boundary[] boundaries = new Boundary[4];
	
	public State()
	{
	
	}
	
	/*
	 * Unit, set and sim status
	 */
	private static int currentUnitNumber = 0;
	private static int currentSimNumber = 0;
	private static int currentSetNumber = 0;

	public static void setCurrentUnit(int currUnit) {}
	public static void setCurrentSim(int currSim) {}
	public static void setCurrentSet(int currSet) {}

	public static int getCurrentUnitNumber() {
		return currentUnitNumber;
	}
	public static int getCurrentSimNumber() {
		return currentSimNumber;
	}
	public static int getCurrentSetNumber() {
		return currentSetNumber;
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
