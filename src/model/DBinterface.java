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
			Connection conn = DriverManager.getConnection("jdbc:sqlite:src/model/chemdb");
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

	public static ArrayList getAllCompoundNames(String order_) {
		ArrayList output = new ArrayList();

		String[] args = new String[2];
		args[0] = "SELECT * FROM compounds ORDER BY " + order_;
		args[1] = "name";
		output = dbConnect(args);

		return output;
	}

	public static ArrayList getAllCompoundFormulas(String order_) {
		ArrayList output = new ArrayList();

		String[] args = new String[2];
		args[0] = "SELECT * FROM compounds ORDER BY " + order_;
		args[1] = "formula";
		output = dbConnect(args);

		return output;
	}

	public static ArrayList getAllElementNames(String order_) {
		ArrayList output = new ArrayList();

		String[] args = new String[2];
		args[0] = "SELECT * FROM elements ORDER BY " + order_;
		args[1] = "name";
		output = dbConnect(args);

		return output;
	}
	
	public static Float getElementMass(String elementName_) {
		
		//ArrayList results = new ArrayList();
		String[] args = new String[2];
		args[0] = "SELECT mass FROM elements WHERE name = \"" + elementName_ + "\"";
		args[1] = "mass";
		ArrayList results = dbConnect(args);
		
		try {
			return Float.valueOf((String)results.get(0)).floatValue();
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
	
	public static Float getElementDensity(String elementName_) {
		
		//ArrayList results = new ArrayList();
		String[] args = new String[2];
		args[0] = "SELECT density FROM elements WHERE name = \"" + elementName_ + "\"";
		args[1] = "density";
		ArrayList results = dbConnect(args);
		
		try {
			return Float.valueOf((String)results.get(0)).floatValue();
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	public static float getCompoundMass(String compoundName_) {
		ArrayList elementMasses = new ArrayList();
		float mass = 0;

		String[] args = new String[2];
		args[0] = "SELECT E.mass, E.name FROM compounds as C, compounds_elements as CE, elements as E WHERE C.name = \"" + compoundName_ + "\" and C.id = CE.compound_id and E.id = CE.element_id";
		args[1] = "mass";
		elementMasses = dbConnect(args);

		for (int i = 0; i<elementMasses.size();i++) {
			String elementMass = (String)elementMasses.get(i);
			mass += Float.valueOf(elementMass);
		}
		return mass;
	}
	
	public static String getCompoundFormula(String compoundName_) {
		String formula = "blork";
		
		String[] args = new String[2];
		args[0] = "SELECT formula FROM compounds WHERE name = \"" + compoundName_ + "\"";
		args[1] = "formula";
		ArrayList results = dbConnect(args);

		try {
			return (String)results.get(0);
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
	
	public static Integer getCompoundCharge(String compoundName_) {
		Integer charge = 0;
		
		ArrayList results = new ArrayList();
		String[] args = new String[2];
		args[0] = "SELECT charge FROM compounds WHERE name = \"" + compoundName_ + "\"";
		args[1] = "charge";
		results = dbConnect(args);
		
		charge = Integer.valueOf((String)results.get(0)).intValue();
		return charge;
	}
	
	public static Boolean getCompoundPolarity(String compoundName_) {

		String[] args = new String[2];
		args[0] = "SELECT polarity FROM compounds WHERE name = \"" + compoundName_ + "\"";
		args[1] = "polarity";
		ArrayList results = dbConnect(args);
		
		try {
			Integer truthy = Integer.valueOf((String)results.get(0)).intValue();
			if (truthy == 1) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	public static Integer getReactionNumber(ArrayList<String> reactants) {

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

		// make sure that a reaction is common for all reactants
		ArrayList commonReactions = new ArrayList();
		commonReactions = (ArrayList)possibleReactionsMatrix.get(0);
		for (int i = 0; i<possibleReactionsMatrix.size(); i++) {
			commonReactions.retainAll((Collection) possibleReactionsMatrix.get(i));
		}

		// short circuit if anything other than 1 reaction possible
		if (commonReactions.size() != 1) {
			return null;
		}

		String reaction = (String)commonReactions.get(0);

		// short circuit if quantity of input reactants does not match required number 
		args = new String[2];
		args[0] = "SELECT C.name FROM reactions as R, reactions_compounds as RC, compounds as C WHERE C.id = RC.compound_id and R.id = RC.reaction_id and RC.type = \"input\" and R.id = " + reaction;
		args[1] = "name";
		if (dbConnect(args).size() != reactants.size()) {
			return null;
		}
		return Integer.parseInt(reaction);
	}

	public static ArrayList<String> getReactionProducts(ArrayList<String> reactants) {
		ArrayList<String> products = new ArrayList<String>();

		Integer reaction = getReactionNumber(reactants);
		
		// get products
		String[] args = new String[2];
		args[0] = "SELECT C.name FROM reactions as R, reactions_compounds as RC, compounds as C WHERE C.id = RC.compound_id and R.id = RC.reaction_id and RC.type = \"output\" and R.id = " + reaction;
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
	
	public static Float getReactionProbability(ArrayList<String> reactants) {
		Integer reaction = getReactionNumber(reactants);
		try {
			return getReactionProbability(reaction);
		} catch (Exception e) {
			//System.out.println("Reaction invalid: " + e);
			return 0.0f;
		}
	}
	
	public static Float getReactionProbability(int reactionNumber_) {
		
		ArrayList results = new ArrayList();
		String[] args = new String[2];
		args[0] = "SELECT probability FROM reactions WHERE id = " + reactionNumber_;
		args[1] = "probability";
		results = dbConnect(args);
		
		try {
			return Float.parseFloat((String)results.get(0));
		} catch (Exception e) {
			//System.out.println("Reaction invalid: " + e);
			return 0.0f;
		}
	}
}
