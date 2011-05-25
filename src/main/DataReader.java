package main;

import java.util.ArrayList;

public class DataReader {
	
	public static void main(String[] args) {
			String fname = "Iodine.svg"; 
			float[][] circles = getSVG(fname);
			for (int i=0; i<circles.length;i++){
				System.out.println(circles[i][0]+" "+circles[i][1]+" "+circles[i][2]);
				
			}
	}
   public static float[][] getSVG(String fname) {
		java.io.BufferedReader fin;
		try {
			fin = new java.io.BufferedReader(new java.io.FileReader(fname));
		} catch (java.io.FileNotFoundException fe) {
			javax.swing.JOptionPane.showMessageDialog(null, "File not found!",
					"Alert: File not found", javax.swing.JOptionPane.ERROR_MESSAGE);
			return null;
		}
		try {
			String sText = null; // To get the line of text from the file
			sText = fin.readLine();
			sText = fin.readLine();
			int line =0;
			ArrayList xList = new ArrayList();
			ArrayList yList = new ArrayList();
			ArrayList rList = new ArrayList();
			while (sText != null) {
				if (sText.contains("<circle fill")){
					String[] values = sText.split("\"");
					xList.add(values[3]);
					yList.add(values[5]);
					rList.add(values[7]);
				}	
				sText = fin.readLine();
				line++;
			}
			float[][] circles = new float[xList.size()][3];
			for (int i =0;i<xList.size();i++){
				circles[i][0] = Float.parseFloat(rList.get(i).toString()); 
				circles[i][1] = Float.parseFloat(xList.get(i).toString()); 
				circles[i][2] = Float.parseFloat(yList.get(i).toString()); 
			}
			fin.close();
			return circles;
		} catch (java.io.IOException ie) {
			javax.swing.JOptionPane.showMessageDialog(null,
					"Error reading from the file2", "Alert",
					javax.swing.JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
}
