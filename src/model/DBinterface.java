package model;

import java.util.ArrayList;
import java.util.Collection;
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
	
	public static ArrayList getProducts(ArrayList<String> reactants) {
		ArrayList<String> products = new ArrayList<String>();
		
		String reactant;
		String[] args = new String[2];
		ArrayList possibleReactionsMatrix = new ArrayList();
		
		// poll database for each reactant
		for (int i = 0; i<reactants.size(); i++) {
			reactant = reactants.get(i);
			args[0] = "SELECT R.id FROM reactions as R, reactions_compounds as RC, compounds as C WHERE C.id = RC.compound_id and R.id = RC.reaction_id and C.name = \"" + reactant + "\" and RC.type = \"input\"";
			args[1] = "id";
			possibleReactionsMatrix.add(dbConnect(args));
		}
		
		// short circuit if no reactions match reactants
		if (possibleReactionsMatrix.size() == 0) {
			return null;
		}
		
		ArrayList commonReactions = new ArrayList();
		commonReactions = (ArrayList)possibleReactionsMatrix.get(0);
		for (int i = 0; i<possibleReactionsMatrix.size(); i++) {
			commonReactions.retainAll((Collection) possibleReactionsMatrix.get(i));
		}
		
		// short circuit of anything other than 1 reaction possible
		if (commonReactions.size() != 1) {
			return null;
		}
		
		// get products
		args = new String[2];
		args[0] = "SELECT C.name FROM reactions as R, reactions_compounds as RC, compounds as C WHERE C.id = RC.compound_id and R.id = RC.reaction_id and RC.type = \"output\" and R.id = 1";
		args[1] = "name";
		products = dbConnect(args);
		
		// short circuit if no products
		if (products.size() == 0) {
			return null;
		}
		else {
			return products;
		}
	}
}
