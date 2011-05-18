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


	/***
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



	/***
	 * Sim Functions
	 */

	public static ArrayList getSims(int unitNumber) {
		HashMap unit = getUnit(unitNumber);
		ArrayList sims = (ArrayList)unit.get("sims");
		return sims;
	}

	public static HashMap getSim(int unitNumber, int simNumber) {
		ArrayList sims = getSims(unitNumber);
		HashMap sim = new HashMap();

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

	/***
	 * Set Functions
	 */

	public static ArrayList getSets(int unitNumber, int simNumber) {
		HashMap sim = getSim(unitNumber, simNumber);
		ArrayList sets = (ArrayList)sim.get("sets");
		return sets;
	}

	public static HashMap getSet(int unitNumber, int simNumber, int setNumber) {
		ArrayList sets = getSets(unitNumber, simNumber);
		HashMap set = new HashMap();

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
		ArrayList compounds = (ArrayList)set.get("compounds");
		return compounds;
	}


}
