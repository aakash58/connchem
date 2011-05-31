package model;

import java.util.ArrayList;
import java.util.Collections;

import java.sql.*;

public class DBinterface {

	public static ArrayList dbConnect(String[] args) {
		ArrayList output = new ArrayList();
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:model/chemdb");
			Statement stat = conn.createStatement();

			ResultSet rs = stat.executeQuery(args[0]);
			while (rs.next()) {
				output.add(rs.getString(args[1]));
			}
			rs.close();
			conn.close();

		} catch (Exception e) {
			System.out.println(e);
		}
		return output;
	}

	public static ArrayList getCompoundNames(String order_) {
		ArrayList output = new ArrayList();

		String[] args = new String[2];
		args[0] = "SELECT * FROM compounds ORDER BY " + order_;
		args[1] = "name";
		output = dbConnect(args);
		
		return output;
	}
	
	public static ArrayList getCompoundFormulas(String order_) {
		ArrayList output = new ArrayList();

		String[] args = new String[2];
		args[0] = "SELECT * FROM compounds ORDER BY " + order_;
		args[1] = "formula";
		output = dbConnect(args);
		
		return output;
	}
	
	public static ArrayList getElementNames(String order_) {
		ArrayList output = new ArrayList();

		String[] args = new String[2];
		args[0] = "SELECT * FROM elements ORDER BY " + order_;
		args[1] = "name";
		output = dbConnect(args);
		
		return output;
	}
	
	public static float getCompoundMass(String compoundName_) {
		ArrayList elementMasses = new ArrayList();
		float mass = 0;
		
		String[] args = new String[2];
		args[0] = "SELECT E.mass, E.name FROM compounds as C, compounds_elements as CE, elements as E WHERE C.name = \"Water\" and C.id = CE.compound_id and E.id = CE.element_id";
		args[1] = "mass";
		elementMasses = dbConnect(args);
		
		for (int i = 0; i<elementMasses.size();i++) {
			String elementMass = (String)elementMasses.get(i);
			mass += Float.valueOf(elementMass);
		}
		return mass;
	}
	
	public static float getCompoundDensity(String compoundName_) {
		ArrayList elementDensities = new ArrayList();
		float mass = 0;
		
		String[] args = new String[2];
		args[0] = "SELECT E.name, E.density FROM compounds as C, compounds_elements as CE, elements as E WHERE C.name = \"Water\" and C.id = CE.compound_id and E.id = CE.element_id";
		args[1] = "density";
		elementDensities = dbConnect(args);
		
		for (int i = 0; i<elementDensities.size();i++) {
			String elementDensity = (String)elementDensities.get(i);
			mass += Float.valueOf(elementDensity);
		}
		return mass / elementDensities.size();
	}
}
