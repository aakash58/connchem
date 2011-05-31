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
}
