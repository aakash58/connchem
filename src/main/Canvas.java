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
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.plaf.ToolTipUI;


import simulations.Unit2;
import simulations.models.Compound;
import simulations.models.Molecule;

import model.DBinterface;
import model.State;
import static simulations.P5Canvas.*;
import static simulations.models.Compound.*;

public class Canvas extends JPanel implements ActionListener, MouseListener, MouseMotionListener{
	public Timer timer1;
	public final int MAXCOMPOUND = 50;
	public ArrayList[] lines = new ArrayList[MAXCOMPOUND];
	
	public int maxCount =8;
	public int maxTime =60;
	public int satCount =0;
	private Main main = null;

	
	public Canvas( Main parent) {
		main = parent;
		for (int i=0; i<MAXCOMPOUND;i++){
			lines[i] = new ArrayList();
		}
		addMouseMotionListener(this);
		setFocusable(true);
		addMouseListener(this);
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
	
		
		
	}
	
	public void reset(){
		for (int i=0; i<MAXCOMPOUND;i++){
			lines[i].clear();
		}
		maxCount =8;
		maxTime =60;
		
		
		if (main.elapsedTime !=null)
			main.elapsedTime.setText(formatTime(0));
		
		//Clean lines after reset
		this.updateTableView();
		this.updateUI();
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
	
	public void paintComponent(Graphics gx) {

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
		
		//Get molecules number from simulation before painting
		updateMoleculeCount();
		//Update tableView, which is presenting molecule legends below chart
		updateTableView();
		
		//Expand x scale if time reaches maxTime
		if (Main.time>maxTime){
			maxTime *=2;
		}
		//Rescale Y-axis and draw new line segment
		int linePadding = 3;
		int marginY = h+2-Compound.names.size()*linePadding;
		for (int i=0; i< Compound.names.size();i++){
			int num2 = Compound.counts.get(i);
			//Rescale Y-axis
			if (num2>=maxCount){
				if (maxCount==8)
					maxCount=12;
				else if (maxCount==12)
					maxCount=20;
				else
					maxCount *=2;
			}
			
			//####Paint lines####
			int num1 =0;
			if (lines[i].size()>0){
				Line tmpLine = (Line) lines[i].get(lines[i].size()-1);
				num1 = tmpLine.getNum2();
			}	
			//Draw one line segment at the end of existing line every time rendering
			Line l = new Line(margin, marginY+i*linePadding-margin, (int) Main.time, (int) Main.time+1,  num1, num2, h2, w2, this);
			lines[i].add(l);
			
		}
		main.elapsedTime.setText(formatTime(Main.time));
		
		//Highlight selected line if any of them has been selected
		for (int i=0; i< MAXCOMPOUND;i++){
			for (int index=0; index< lines[i].size();index++){
				Line l = (Line) lines[i].get(index);
				if (main.getTableView().selectedRowsContain(i))
					l.paint(g,blinkingColor(main.getTableView().colors[i]));
				else
					l.paint(g,main.getTableView().colors[i]);
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
	
	//Get molecules number from simulation before painting
	public void updateMoleculeCount(){
	
		//For particular cases
		if (main.selectedUnit==1 && main.selectedSim==4){
			int H2OIndex = names.indexOf("Water");
			int OIndex = names.indexOf("Oxygen");
			int H2O2Index = names.indexOf("Hydrogen-Peroxide");
			int H2OCount =0;
			int OCount =0;
			int H2O2Count =0;
			for (int i=0; i<State.molecules.size();i++){
				Molecule m = (Molecule) State.molecules.get(i);
				if (m.getName().equals("Water")){
					H2OCount++;
				}
				else if (m.getName().equals("Oxygen")){
					OCount++;
				}
				else if (m.getName().equals("Hydrogen-Peroxide")){
					H2O2Count++;
				}
			}
			Compound.counts.set(H2OIndex,H2OCount);
			Compound.counts.set(OIndex, OCount);
			Compound.counts.set(H2O2Index, H2O2Count);
			
		}
		if (main.selectedUnit==2 && main.selectedSet==1 && main.selectedSim<4){
			int NaIndex = names.indexOf("Sodium-Ion");
			int ClIndex = names.indexOf("Chlorine-Ion");
			int NaClIndex = names.indexOf("Sodium-Chloride");
			int NaClCount =0;
			for (int i=0; i<State.molecules.size();i++){
				Molecule m = (Molecule) State.molecules.get(i);
				if (m.getName().equals("Sodium-Ion") && m.compoundJ>=0){
					NaClCount++;
				}
			}
			Compound.counts.set(NaIndex,main.getP5Canvas().getUnit2().getTotalNum()-NaClCount);
			Compound.counts.set(ClIndex,main.getP5Canvas().getUnit2().getTotalNum()-NaClCount);
			Compound.counts.set(NaClIndex,NaClCount);
			
		}
		else if (main.selectedUnit==2 && main.selectedSet==4){
			int CaIndex = names.indexOf("Calcium-Ion");
			int ClIndex = names.indexOf("Chlorine-Ion");
			int CaClIndex = names.indexOf("Calcium-Chloride");
			int CaClCount =0;
			for (int i=0; i<State.molecules.size();i++){
				Molecule m = (Molecule) State.molecules.get(i);
				if (m.getName().equals("Calcium-Ion") && m.compoundJ>=0){
					CaClCount++;
				}
			}
			Compound.counts.set(CaIndex,main.getP5Canvas().getUnit2().getTotalNum()-CaClCount);
			Compound.counts.set(ClIndex,2*(main.getP5Canvas().getUnit2().getTotalNum()-CaClCount));
			Compound.counts.set(CaClIndex,CaClCount);
		}
		else if (main.selectedUnit==2 && main.selectedSet==7){
			int NaIndex = names.indexOf("Sodium-Ion");
			int HCO3Index = names.indexOf("Bicarbonate");
			int NaHCO3Index = names.indexOf("Sodium-Bicarbonate");
			int NaHCO3Count =0;
			for (int i=0; i<State.molecules.size();i++){
				Molecule m = (Molecule) State.molecules.get(i);
				if (m.getName().equals("Sodium-Ion") && m.compoundJ>=0){
					NaHCO3Count++;
				}
			}
			Compound.counts.set(NaIndex,main.getP5Canvas().getUnit2().getTotalNum()-NaHCO3Count);
			Compound.counts.set(HCO3Index,main.getP5Canvas().getUnit2().getTotalNum()-NaHCO3Count);
			Compound.counts.set(NaHCO3Index, NaHCO3Count);
		}
		else if (main.selectedUnit==2 && main.selectedSet==1 && main.selectedSim==4){
			int KIndex = names.indexOf("Potassium-Ion");
			if (KIndex<0) return;
			int ClIndex = names.indexOf("Chlorine-Ion");
			int KClIndex = names.indexOf("Potassium-Chloride");
			int KClCount =0;
			for (int i=0; i<State.molecules.size();i++){
				Molecule m = (Molecule) State.molecules.get(i);
				if (m.getName().equals("Potassium-Ion") && m.compoundJ>=0){
					KClCount++;
				}
			}
			Compound.counts.set(KIndex, main.getP5Canvas().getUnit2().getTotalNum()-KClCount);
			Compound.counts.set(ClIndex,main.getP5Canvas().getUnit2().getTotalNum()-KClCount);
			Compound.counts.set(KClIndex,KClCount);
		}
		
		
	}
	
	//Update tableView, which is presenting molecule legends below chart
	public void updateTableView(){
		
		main.getTableView().data[0].clear();
		main.getTableView().data[1].clear();
		main.getTableView().data[2].clear();
		
		for (int i=0; i<Compound.names.size();i++){
			main.getTableView().data[0].add(Compound.counts.get(i));
			main.getTableView().data[1].add(main.getTableView().colors[i]);
			main.getTableView().data[2].add(Compound.names.get(i));
		}
		if (main.getTableView() !=null && !main.getTableView().stopUpdating){
			main.getTableView().table.updateUI();
		}		
	}
	
	//Return molecule name when that molecule is being selected on legends
	public String [] getSelectedMolecule(){
		if(!main.getTableView().selectedRowsIsEmpty())
		{
			int [] selectedRows = main.getTableView().getSelectedRows();
			String [] molecules = new String [selectedRows.length];
			
			for(int i = 0;i<selectedRows.length;i++)
			{
				if(selectedRows[i]<Compound.names.size())
				molecules[i] = new String(Compound.names.get(selectedRows[i]));
			}
			
			return molecules;
		}
		return null;
		
	}
	
	/*public static Color getSelecttedColor(){
		int index = TableView.selectedRow;
		if (index<0 || index>=mNames.size())
			return Color.BLACK;
		Color c1 = TableView.colors[index];
		return c1;
	}*/
	public static Color blinkingColor(Color c1){
		//int num = (int) ((count%26+1)*10);
		int num = 30;
		int r,g,b;
		r = c1.getRed()+num;   
		if (r>255) r = 255;
		g = c1.getGreen()+num; 
		if (g>255) g = 255;
		b = c1.getBlue()+num;  
		if (b>255) b = 255;
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
		int mouseX = e.getX();
		int mouseY = e.getY();
		int select = -1;
		this.setToolTipText("");
		for (int i=0; i<names.size();i++){
			for (int j=0; j<lines[i].size();j++){
				Line l = (Line) lines[i].get(j);
				if (l.isIn(mouseX, mouseY)){
					select =i; //Pick up the last lines
					if(main.selectedUnit==3)
					{
						int index = i ;
						String name = Compound.names.get(index);
						float weight = Compound.moleculeWeight.get(index)* Compound.counts.get(index);
						DecimalFormat df = new DecimalFormat("###.##");
						String weightStr = df.format(weight);
						String tooltipText = new String(name+": "+weightStr+" g");
						this.setToolTipText(tooltipText);
					}
				}
			
			}
		}
		if (!main.getTableView().selectedRowsContain(select))
		{
			main.getTableView().addSelectedRow(select);
		}
	}
	

}
