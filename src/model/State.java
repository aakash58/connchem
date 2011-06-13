package model;
import static model.YAMLinterface.*;

import java.util.ArrayList;

import org.jbox2d.util.sph.Particle;

import view.Boundary;
import view.Molecule;

public class State {
	// An ArrayList of particles that will fall on the surface
	public static ArrayList<Molecule> molecules = new ArrayList<Molecule>();
	// A list we'll use to track fixed objects
	public static Boundary[] boundaries = new Boundary[4];
	
	
	
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
}
