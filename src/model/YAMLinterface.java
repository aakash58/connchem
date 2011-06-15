package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.io.BufferedReader;

import org.xml.sax.InputSource;

import processing.core.PApplet;


import com.esotericsoftware.yamlbeans.YamlReader;

public class YAMLinterface {
	public static String url = "model/sims.yml";
	public static String yaml;


	public static Map getYamlText() {
		HashMap hm = new HashMap();
		try {
			if (yaml == null) {
				ResourceReader rr = new ResourceReader(url);
				yaml = rr.read();
			}
			YamlReader reader = new YamlReader(yaml);

			hm = (HashMap)reader.read();

		} catch (Exception e) {
			System.out.println("Error: " + e);
		}

		return hm;
	}


	/*
	 * Unit Functions
	 */
	public static ArrayList getUnits() {
		try {
			ArrayList yaml = (ArrayList)getYamlText().get("units");
			return yaml;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	public static HashMap getUnit(int unitNumber) {
		ArrayList units = getUnits();
		HashMap unit = new HashMap();

		for (int i = 0; i<units.size(); i++) {
			unit = (HashMap)units.get(i);
			int n = Integer.parseInt((String)unit.get("unit"));
			if (unitNumber == n) {
				return unit;
			}
		}
		return null;
	}

	public static String getUnitName(int unitNumber) {
		HashMap unit = getUnit(unitNumber);
		String unitName = (String)unit.get("name");
		return unitName;
	}



	/*
	 * Sim Functions
	 */

	public static ArrayList getSims(int unitNumber) {
		HashMap unit = getUnit(unitNumber);
		if (unit!=null){
			ArrayList sims = (ArrayList)unit.get("sims");
			return sims;
		}
		else{
			return null;
		}
	}

	public static HashMap getSim(int unitNumber, int simNumber) {
		ArrayList sims = getSims(unitNumber);
		HashMap sim = new HashMap();

		if (sims==null) return null;
		for (int i = 0; i<sims.size(); i++) {
			sim = (HashMap)sims.get(i);
			int n = Integer.parseInt((String)sim.get("sim"));
			if (simNumber == n) {
				return sim;
			}
		}
		return null;
	}

	public static String getSimName(int unitNumber, int simNumber) {
		HashMap sim = getSim(unitNumber, simNumber);
		String simName = (String)sim.get("name");
		return simName;
	}

	/*
	 * Control Functions
	 */
	private static ArrayList getControls() {
		try {
			ArrayList yaml = (ArrayList)getYamlText().get("controls");
			return yaml;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	private static ArrayList getControls(int unitNumber) {
		HashMap unit = getUnit(unitNumber);

		ArrayList<HashMap> al1 = new ArrayList();
		al1 = getControls();

		ArrayList<HashMap> al2 = new ArrayList();
		al2 = (ArrayList)unit.get("controls");

		return combineControls(al1, al2);
	}

	private static ArrayList<HashMap> getControls(int unitNumber, int simNumber) {
		HashMap sim = getSim(unitNumber, simNumber);

		ArrayList<HashMap> al1 = new ArrayList();
		al1 = getControls(unitNumber);

		ArrayList<HashMap> al2 = new ArrayList();
		al2 = (ArrayList)sim.get("controls");

		return combineControls(al1, al2);
	}

	private static ArrayList<HashMap> combineControls(ArrayList<HashMap> al1, ArrayList<HashMap> al2) {
		ArrayList<HashMap> output = new ArrayList();

		if (al1 != null && al2 != null) {
			for (int i = 0; i < al1.size(); i++) {
				HashMap item1 = al1.get(i);
				String itemName = (String)item1.get("control");

				for (int j = 0; j<al2.size(); j++) {
					HashMap item2 = al2.get(j);
					if (item2.containsValue(itemName)) {
						item1.putAll(item2);
					}
				}
				output.add(item1);
			}
		} else if (al1 == null) {
			output = al2;
		} else {
			output = al1;
		}
		return output;
	}
	
	private static boolean getControlState(ArrayList<HashMap> controls, String controlName) {
		HashMap timer = new HashMap();
		for (int i = 0; i < controls.size(); i++) {
			HashMap control = controls.get(i);
			if (control.get("control").equals(controlName) && control.get("state").equals("off")) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean getTimerState(ArrayList<HashMap> controls) 	{ return getControlState(controls, "timer"); }
	private static boolean getTimerState() 								{ return getTimerState(getControls()); }
	private static boolean getTimerState(int unitNumber) 				{ return getTimerState(getControls(unitNumber)); }
	public static boolean getTimerState(int unitNumber, int simNumber) 	{ return getTimerState(getControls(unitNumber, simNumber)); }
	
	private static Float getTimerTime(ArrayList<HashMap> controls, String setting) {
		HashMap timer = new HashMap();
		for (int i = 0; i < controls.size(); i++) {
			HashMap control = controls.get(i);
			if (control.get("control").equals("timer")) {
				return Float.valueOf((String) control.get(setting)).floatValue();
			}
		}
		return 0.f;
	}
	private static Float getTimerTimeMin(ArrayList<HashMap> controls) 			{ return getTimerTime(controls, "min"); }
	private static Float getTimerTimeInit(ArrayList<HashMap> controls) 			{ return getTimerTime(controls, "init"); }
	private static Float getTimerTimeMax(ArrayList<HashMap> controls) 			{ return getTimerTime(controls, "max"); }
	private static Float getTimerTimeMin() 										{ return getTimerTimeMin(getControls()); }
	private static Float getTimerTimeMin(int unitNumber) 						{ return getTimerTimeMin(getControls(unitNumber)); }
	public static Float getTimerTimeMin(int unitNumber, int simNumber) 			{ return getTimerTimeMin(getControls(unitNumber, simNumber)); }
	private static Float getTimerTimeInit() 									{ return getTimerTimeInit(getControls()); }
	private static Float getTimerTimeInit(int unitNumber) 						{ return getTimerTimeInit(getControls(unitNumber)); }
	public static Float getTimerTimeInit(int unitNumber, int simNumber) 		{ return getTimerTimeInit(getControls(unitNumber, simNumber)); }
	private static Float getTimerTimeMax() 										{ return getTimerTimeMax(getControls()); }
	private static Float getTimerTimeMax(int unitNumber) 						{ return getTimerTimeMax(getControls(unitNumber)); }
	public static Float getTimerTimeMax(int unitNumber, int simNumber) 			{ return getTimerTimeMax(getControls(unitNumber, simNumber)); }
	
	private static boolean getVolumeSliderState(ArrayList<HashMap> controls) 	{ return getControlState(controls, "volume slider"); }
	private static boolean getVolumeSliderState() 								{ return getVolumeSliderState(getControls()); }
	private static boolean getVolumeSliderState(int unitNumber) 				{ return getVolumeSliderState(getControls(unitNumber)); }
	public static boolean getVolumeSliderState(int unitNumber, int simNumber) 	{ return getVolumeSliderState(getControls(unitNumber, simNumber)); }
	
	private static boolean getScaleSliderState(ArrayList<HashMap> controls) 	{ return getControlState(controls, "scale slider"); }
	private static boolean getScaleSliderState() 								{ return getScaleSliderState(getControls()); }
	private static boolean getScaleSliderState(int unitNumber) 					{ return getScaleSliderState(getControls(unitNumber)); }
	public static boolean getScaleSliderState(int unitNumber, int simNumber) 	{ return getScaleSliderState(getControls(unitNumber, simNumber)); }
	
	private static boolean getSpeedSliderState(ArrayList<HashMap> controls) 	{ return getControlState(controls, "speed slider"); }
	private static boolean getSpeedSliderState() 								{ return getSpeedSliderState(getControls()); }
	private static boolean getSpeedSliderState(int unitNumber) 					{ return getSpeedSliderState(getControls(unitNumber)); }
	public static boolean getSpeedSliderState(int unitNumber, int simNumber) 	{ return getSpeedSliderState(getControls(unitNumber, simNumber)); }
	
	private static boolean getHeatSliderState(ArrayList<HashMap> controls) 		{ return getControlState(controls, "heat slider"); }
	private static boolean getHeatSliderState() 								{ return getHeatSliderState(getControls()); }
	private static boolean getHeatSliderState(int unitNumber) 					{ return getHeatSliderState(getControls(unitNumber)); }
	public static boolean getHeatSliderState(int unitNumber, int simNumber) 	{ return getHeatSliderState(getControls(unitNumber, simNumber)); }
	
	private static Float getHeatSliderHeat(ArrayList<HashMap> controls, String setting) {
		HashMap timer = new HashMap();
		for (int i = 0; i < controls.size(); i++) {
			HashMap control = controls.get(i);
			if (control.get("control").equals("heat slider")) {
				return Float.valueOf((String) control.get(setting)).floatValue();
			}
		}
		return 0.f;
	}
	private static Float getHeatSliderMin(ArrayList<HashMap> controls) 			{ return getHeatSliderHeat(controls, "min"); }
	private static Float getHeatSliderInit(ArrayList<HashMap> controls) 		{ return getHeatSliderHeat(controls, "init"); }
	private static Float getHeatSliderMax(ArrayList<HashMap> controls) 			{ return getHeatSliderHeat(controls, "max"); }
	private static Float getHeatSliderMin() 									{ return getHeatSliderMin(getControls()); }
	private static Float getHeatSliderMin(int unitNumber) 						{ return getHeatSliderMin(getControls(unitNumber)); }
	public static Float getHeatSliderMin(int unitNumber, int simNumber) 		{ return getHeatSliderMin(getControls(unitNumber, simNumber)); }
	private static Float getHeatSliderInit() 									{ return getHeatSliderInit(getControls()); }
	private static Float getHeatSliderInit(int unitNumber) 						{ return getHeatSliderInit(getControls(unitNumber)); }
	public static Float getHeatSliderInit(int unitNumber, int simNumber) 		{ return getHeatSliderInit(getControls(unitNumber, simNumber)); }
	private static Float getHeatSliderMax() 									{ return getHeatSliderMax(getControls()); }
	private static Float getHeatSliderMax(int unitNumber) 						{ return getHeatSliderMax(getControls(unitNumber)); }
	public static Float getHeatSliderMax(int unitNumber, int simNumber) 		{ return getHeatSliderMax(getControls(unitNumber, simNumber)); }
	
	private static boolean getMoleculeSidebarState(ArrayList<HashMap> controls) { return getControlState(controls, "molecule sidebar"); }
	private static boolean getMoleculeSidebarState() 							{ return getMoleculeSidebarState(getControls()); }
	private static boolean getMoleculeSidebarState(int unitNumber) 				{ return getMoleculeSidebarState(getControls(unitNumber)); }
	public static boolean getMoleculeSidebarState(int unitNumber, int simNumber) 	{ return getMoleculeSidebarState(getControls(unitNumber, simNumber)); }
	
	private static boolean getPeriodicTableState(ArrayList<HashMap> controls) 	{ return getControlState(controls, "periodic table"); }
	private static boolean getPeriodicTableState() 								{ return getPeriodicTableState(getControls()); }
	private static boolean getPeriodicTableState(int unitNumber) 				{ return getPeriodicTableState(getControls(unitNumber)); }
	public static boolean getPeriodicTableState(int unitNumber, int simNumber) 	{ return getPeriodicTableState(getControls(unitNumber, simNumber)); }
	
	/*
	 * Set Functions
	 */

	public static ArrayList getSets(int unitNumber, int simNumber) {
		HashMap sim = getSim(unitNumber, simNumber);
		if (sim==null) return null;
		ArrayList sets = (ArrayList)sim.get("sets");
		return sets;
	}

	public static HashMap getSet(int unitNumber, int simNumber, int setNumber) {
		ArrayList sets = getSets(unitNumber, simNumber);
		HashMap set = new HashMap();

		if (sets==null) return null;
		for (int i = 0; i<sets.size(); i++) {
			set = (HashMap)sets.get(i);
			int n = Integer.parseInt((String)set.get("set"));
			if (setNumber == n) {
				return set;
			}
		}
		return null;
	}

	public static ArrayList getSetCompounds(int unitNumber, int simNumber, int setNumber) {
		HashMap set = getSet(unitNumber, simNumber, setNumber);
		if (set==null) return null;
		ArrayList compounds = (ArrayList)set.get("compounds");
		return compounds;
	}

	/*
	 * Temperature Functions
	 */

	private static Float getTemperature(int unitNumber) {
		Float temp = State.defaultTemperature;

		try {
			HashMap unit = getUnit(unitNumber);
			temp = Float.valueOf((String)unit.get("temperature")).floatValue();
		}
		catch (Exception e) {}
		return temp;
	}

	private static Float getTemperature(int unitNumber, int simNumber) {
		Float temp = getTemperature(unitNumber);

		try {
			HashMap sim = getSim(unitNumber, simNumber);
			temp = Float.valueOf((String)sim.get("temperature")).floatValue();
		}
		catch (Exception e) {}
		return temp;
	}

	public static Float getTemperature(int unitNumber, int simNumber, int setNumber) {
		Float temp = getTemperature(unitNumber, simNumber);

		try {
			HashMap set = getSet(unitNumber, simNumber, setNumber);
			temp = Float.valueOf((String)set.get("temperature")).floatValue();
		}
		catch (Exception e) {}
		return temp;
	}

	private static Float getMinTemperature(int unitNumber) {
		Float temp = State.defaultMinTemperature;

		try {
			HashMap unit = getUnit(unitNumber);
			temp = Float.valueOf((String)unit.get("minTemperature")).floatValue();
		}
		catch (Exception e) {}
		return temp;
	}

	private static Float getMinTemperature(int unitNumber, int simNumber) {
		Float temp = getMinTemperature(unitNumber);

		try {
			HashMap sim = getSim(unitNumber, simNumber);
			temp = Float.valueOf((String)sim.get("minTemperature")).floatValue();
		}
		catch (Exception e) {}
		return temp;
	}

	public static Float getMinTemperature(int unitNumber, int simNumber, int setNumber) {
		Float temp = getMinTemperature(unitNumber, simNumber);

		try {
			HashMap set = getSet(unitNumber, simNumber, setNumber);
			temp = Float.valueOf((String)set.get("minTemperature")).floatValue();
		}
		catch (Exception e) {}
		return temp;
	}

	private static Float getMaxTemperature(int unitNumber) {
		Float temp = State.defaultMaxTemperature;

		try {
			HashMap unit = getUnit(unitNumber);
			temp = Float.valueOf((String)unit.get("maxTemperature")).floatValue();
		}
		catch (Exception e) {}
		return temp;
	}

	private static Float getMaxTemperature(int unitNumber, int simNumber) {
		Float temp = getMaxTemperature(unitNumber);

		try {
			HashMap sim = getSim(unitNumber, simNumber);
			temp = Float.valueOf((String)sim.get("maxTemperature")).floatValue();
		}
		catch (Exception e) {}
		return temp;
	}

	public static Float getMaxTemperature(int unitNumber, int simNumber, int setNumber) {
		Float temp = getMaxTemperature(unitNumber, simNumber);

		try {
			HashMap set = getSet(unitNumber, simNumber, setNumber);
			temp = Float.valueOf((String)set.get("maxTemperature")).floatValue();
		}
		catch (Exception e) {}
		return temp;
	}


	/*
	 * Compound Functions
	 */

	public static HashMap getCompound(int unitNumber, int simNumber, int setNumber, int comNumber) {
		ArrayList compounds = getSetCompounds(unitNumber, simNumber, setNumber);
		HashMap compound = new HashMap();

		if (compounds ==null) return null;
		compound = (HashMap) compounds.get(comNumber);
		return compound;
	}

	public static String getCompoundQty(int unitNumber, int simNumber, int setNumber, int comNumber) {
		HashMap set = getCompound(unitNumber, simNumber, setNumber,comNumber);
		if (set==null) return null;
		return (String)set.get("qty");
	}

	public static String getCompoundName(int unitNumber, int simNumber, int setNumber, int comNumber) {
		HashMap set = getCompound(unitNumber, simNumber, setNumber,comNumber);
		if (set==null) return null;
		return (String)set.get("compound");
	}
}
