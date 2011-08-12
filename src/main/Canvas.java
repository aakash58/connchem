package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

import view.Molecule;
import view.Unit2;

import model.State;
import static view.P5Canvas.*;
import static view.Compound.*;

public class Canvas extends JPanel implements ActionListener, MouseListener, MouseMotionListener{
	public static Timer timer1;
	//public static ArrayList<String> mNames = new ArrayList<String>();
	//public static ArrayList<Integer> mCounts = new ArrayList<Integer>();
	public static final int MAXCOMPOUND = 50;
	public static ArrayList[] lines = new ArrayList[MAXCOMPOUND];
	
	public static int maxCount =8;
	public static int maxTime =60;
	public static int satCount =0;
	
	
	public Canvas() {
		for (int i=0; i<MAXCOMPOUND;i++){
			lines[i] = new ArrayList();
		}
		addMouseMotionListener(this);
		setFocusable(true);
		addMouseListener(this);
	}
	
	public void reset(){
		for (int i=0; i<MAXCOMPOUND;i++){
			lines[i] = new ArrayList();
		}
		maxCount =8;
		maxTime =60;
		if (Main.elapsedTime !=null)
			Main.elapsedTime.setText(formatTime(0));
	}
	
	public String formatTime(long count){
		long s = count %60; 
		long m = count /60;
		String sStr =""+s;
		String mStr =""+m;
		if (s<10)
			sStr = "0"+sStr;
		if (m<1)
			return sStr;
		else{
			if (m<10)
				mStr = "0"+mStr;
			return mStr+":"+sStr;
		}
	}
	
	public void paint(Graphics gx) {
		//System.out.println("CountTime"+second);
		Graphics2D g = (Graphics2D) gx;
		int w = 266;
		int h = 225;
		g.setColor(new Color(232,232,232));
		g.fillRect(0, 0, w, h);
		
		int margin =16;
		int w2 = w-3*margin/2;
		int h2 = h-3*margin/2;
		
		g.setColor(Color.GRAY);
		g.fillRect(margin, margin/2, w2, h2);
		g.setColor(Color.BLACK);
		g.drawRect(margin, margin/2, w2, h2);
		
		g.setColor(new Color(255,255,255,30));
		g.drawLine(margin, margin/2+1*h2/4, w-margin/2, margin/2+1*h2/4);
		g.drawLine(margin, margin/2+2*h2/4, w-margin/2, margin/2+2*h2/4);
		g.drawLine(margin, margin/2+3*h2/4, w-margin/2, margin/2+3*h2/4);
	
		// Draw X-axis
		g.setFont(new Font("Garamond", Font.PLAIN, 11));
		g.setColor(Color.DARK_GRAY);
		g.drawString("Start", margin, h-5);
		g.setFont(new Font("Garamond", Font.PLAIN, 12));
		g.setColor(Color.BLACK);
		g.drawString("Time", w/2-4, h-4);
		
		
		//Draw Y-Axis
		g.translate(12, h/2+30);
		g.rotate(-Math.PI/2.0);
		g.setFont(new Font("Garamond", Font.PLAIN, 12));
		g.setColor(Color.BLACK);
		g.drawString("# molecules", 0, 0);
		g.rotate(Math.PI/2.0);
		g.translate(-12, -(h/2+30));
		
		g.setFont(new Font("Garamond", Font.PLAIN, 11));
		g.setColor(Color.DARK_GRAY);
		g.drawString("0", 6, h-margin);
		g.setFont(new Font("Garamond", Font.PLAIN, 11));
		g.setColor(Color.DARK_GRAY);
		g.drawString(""+maxCount, 2, margin-5);
		
		satCount+=2;
		computeDisolved();
		resetMoleculeCount();
		updateTableView();
		if (second>maxTime){
			maxTime *=2;
		}
		//Rescale Y-axis
		for (int i=0; i< names.size();i++){
			int num2 = counts.get(i);
			//Rescale X-axis
			if (num2>=maxCount){
				if (maxCount==8)
					maxCount=12;
				else if (maxCount==12)
					maxCount=20;
				else
					maxCount *=2;
			}
			
			int num1 =0;
			if (lines[i].size()>0){
				Line tmpLine = (Line) lines[i].get(lines[i].size()-1);
				num1 = tmpLine.getNum2();
			}	
			Line l = new Line(margin, 225-margin, (int) second-1, (int) second,  num1, num2, h2, w2);
			lines[i].add(l);
		}
		Main.elapsedTime.setText(formatTime(second));
		
		
		for (int i=0; i< MAXCOMPOUND;i++){
			for (int index=0; index< lines[i].size();index++){
				Line l = (Line) lines[i].get(index);
				if (i==TableView.selectedRow)
					l.paint(g,blinkingColor(TableView.colors[i]));
				else
					l.paint(g,TableView.colors[i]);
			}
		}
		
		//Paint time Limit on X-axis
		g.setFont(new Font("Garamond", Font.PLAIN, 11));
		g.setColor(Color.DARK_GRAY);
		int mins = maxTime/60;
		if (mins ==1)
			g.drawString("1 min", w-38, h-6);
		else 
			g.drawString(maxTime/60+" mins", w-44, h-5);
		//Draw X-grid
		for (int i=1; i<mins;i++){
			g.setColor(new Color(255,255,255,30));
			g.drawLine(margin+w2*i/mins, margin/2, margin+w2*i/mins, margin/2+h2);
		}
	}
	

	public static void resetMoleculeCount(){
		if (Main.selectedUnit==2 && Main.selectedSet==4){
			int CaIndex = names.indexOf("Calcium-Ion");
			int ClIndex = names.indexOf("Chlorine-Ion");
			int CaClIndex = names.indexOf("Calcium-Chloride");
			int CaCount = counts.get(CaIndex);
			int ClCount = counts.get(ClIndex);
			int CaClCount =0;
			for (int i=0; i<State.molecules.size();i++){
				Molecule m = (Molecule) State.molecules.get(i);
				if (m.getName().equals("Calcium-Ion") && m.compoundJ>=0){
					CaClCount++;
				}
			}
			counts.set(CaIndex,CaCount-CaClCount);
			counts.set(ClIndex,ClCount-CaClCount*2);
			counts.set(CaClIndex,CaClCount);
			
		}
		/*else if (Main.selectedUnit==2 && Main.selectedSet==1 && Main.selectedSim<=3){
			int NaIndex = names.indexOf("Sodium-Ion");
			int ClIndex = getNameIndex("Chlorine-Ion");
			if (NaIndex>=0){
				int NaCount = mCounts.get(NaIndex);
				int ClCount = mCounts.get(ClIndex);
				int NaClCount =0;
				int NaClCount1 =0;
				int NaClCount2 =0;
				for (int i=0; i<State.molecules.size();i++){
					Molecule m = (Molecule) State.molecules.get(i);
					if (m.getName().equals("Sodium-Ion") && m.NaClPartner>=0){
						NaClCount1++;
					}
					else if (m.getName().equals("Chlorine-Ion") && m.NaClPartner>=0){
						NaClCount2++;
					}
				}
				NaClCount = Math.min(NaClCount1, NaClCount2);
				mNames.add(2,"Calcium-Chloride");
				mCounts.add(2,NaClCount);
				mCounts.set(NaIndex,NaCount-NaClCount);
				mCounts.set(ClIndex,ClCount-NaClCount);
			}
		}
		else if (Main.selectedUnit==2 && Main.selectedSet==7){
			int NaIndex = getNameIndex("Sodium-Ion");
			int HCO3Index = getNameIndex("Bicarbonate");
			if (NaIndex>=0){
				int NaCount = mCounts.get(NaIndex);
				int ClCount = mCounts.get(HCO3Index);
				int NaHCO3Count =Unit2.num_remain;
			
				mNames.add(2,"Sodium-Bicarbonate");
				mCounts.add(2,NaHCO3Count);
				mCounts.set(NaIndex,NaCount-NaHCO3Count);
				mCounts.set(HCO3Index,ClCount-NaHCO3Count);
			}
		}*/
		
	}
	public static void updateTableView(){
		TableView.data[0] = new ArrayList();
		TableView.data[1] = new ArrayList();
		TableView.data[2] = new ArrayList();
		for (int i=0; i<names.size();i++){
			TableView.data[0].add(counts.get(i));
			TableView.data[1].add(TableView.colors[i]);
			TableView.data[2].add(names.get(i));
		}
		if (Main.tableView !=null && !Main.tableView.stopUpdating){
			Main.tableView.table.updateUI();
		}		
	}
	
	public static String getSelectedMolecule(){
		int index = TableView.selectedRow;
		if (index<0 || index>=names.size())
			return "";
		if (names.get(index)==null) return "";
		return names.get(index).toString();
	}
	
	/*public static Color getSelecttedColor(){
		int index = TableView.selectedRow;
		if (index<0 || index>=mNames.size())
			return Color.BLACK;
		Color c1 = TableView.colors[index];
		return c1;
	}*/
	public static Color blinkingColor(Color c1){
		int num = (int) ((count%26+1)*10);
		int r,g,b;
		r = c1.getRed()+num;   if (r>255) r = 255;
		g = c1.getGreen()+num; if (g>255) g = 255;
		b = c1.getBlue()+num;  if (b>255) b = 255;
		Color c2 = new Color(r, g,b);
		return c2;
	}
		
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==timer1){
			
		}
	}
		
	public void mouseClicked(MouseEvent e) {
		
	}	
		
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int select = -1;
		for (int i=0; i<names.size();i++){
			for (int j=0; j<lines[i].size();j++){
				Line l = (Line) lines[i].get(j);
				if (l.isIn(x, y)){
					select =i; //Pick up the last lines
				}	
			}
		}
		if (select != TableView.selectedRow)
			TableView.setSelectedRow(select);
	}
	

}
