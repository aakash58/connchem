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

import data.DBinterface;
import data.State;


import simulations.P5Canvas;
import simulations.Unit2;
import simulations.models.Compound;
import simulations.models.Molecule;

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
	private P5Canvas p5Canvas  = null;
	private TableView tableView = null;
	private boolean paintLineEnabled;  //Flag representing whether we should draw lines or not
	int curTime =0;
	int oldTime =0;

	
	public Canvas( Main parent) {
		main = parent;
		p5Canvas = main.getP5Canvas();
		tableView = main.getTableView();
		for (int i=0; i<MAXCOMPOUND;i++){
			lines[i] = new ArrayList();
		}
		addMouseMotionListener(this);
		setFocusable(true);
		addMouseListener(this);
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		//Start to draw lines on graph
		setPaintLineEnable(false);
		
		
	}
	
	public void reset(){
		
		for (int i=0; i<MAXCOMPOUND;i++){
			lines[i].clear();
		}
		maxTime =60;
		switch(p5Canvas.getUnit())
		{
		
		case 3:
			maxCount = 160;
			break;
		default:
			maxCount =8;
			break;
		}

		
		
		if (main.elapsedTime !=null)
			main.elapsedTime.setText(formatTime(0));
		
		//Clean lines after reset
		//main.getTableView().updateTableView();
		setPaintLineEnable(true);
		
		this.repaint();
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
		switch(p5Canvas.getUnit())
		{
		case 1:
		case 2:
		g.drawString("# molecules", 0, 0);
		break;
		case 3:
		case 4:
			g.drawString("total mass", 0, 0);
			break;
		case 5:
		case 6:
			g.drawString("Concentration (M)", 0, 0);
			break;
		}
		
			
		g.rotate(Math.PI/2.0);
		g.translate(-12, -(h/2+30));
		
		g.setFont(new Font("Garamond", Font.PLAIN, 11));
		g.setColor(Color.DARK_GRAY);
		g.drawString("0", 6, h-margin);
		g.setFont(new Font("Garamond", Font.PLAIN, 11));
		g.setColor(Color.DARK_GRAY);
		g.drawString(""+maxCount, 2, margin-8);
		
		satCount+=2;
		
		//Expand x scale if time reaches maxTime
		if (main.time>maxTime){
			maxTime *=2;
		}
		
		paintLines(g,w,h,w2,h2,margin);
		
		main.elapsedTime.setText(formatTime(main.time));
	

		
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
	
	//Update table value and be ready to show
	private boolean updateLineValue(int w, int h,int w2, int h2,int margin)
	{
		//Get molecules number from simulation before painting
		updateMoleculeCount();
		
		//Update tableView, which is presenting molecule legends below chart
		boolean isDataEmpty = !main.getTableView().updateTableView();
		curTime = main.time;
		
		//Data value is only updated every second
		if(curTime ==  oldTime || curTime==0)
			return false;
		else
		{
			oldTime = curTime;
			
			if(isDataEmpty )
			{
				return false;
			}
			else
			{
				int linePadding = 1;
				int marginY = h+2-Compound.names.size()*linePadding;
				//int marginY = h;
				int unit = p5Canvas.getUnit();
					
					for (int i=0; i<Compound.names.size();i++){
						int num2 =  (int)dataConversion(unit, i);
						//int num2 = Compound.counts.get(i);
						//Rescale Y-axis
						if (num2>=maxCount){
							while(num2>=maxCount)
							{
								if (maxCount==8)
									maxCount=12;
								else if (maxCount==12)
									maxCount=20;
								else
									maxCount *=2;
							}
						}
						
						//########Paint lines########
						int num1 =0;
						if (lines[i].size()>0){
							Line tmpLine = (Line) lines[i].get(lines[i].size()-1);
							num1 = tmpLine.getNum2();
						}
						else //The first second
							num1 = num2;
						//Draw one line segment at the end of existing line every time rendering
						Line l = new Line(margin, marginY+i*linePadding-margin, (int) curTime-1, (int) curTime,  num1, num2, h2, w2, this);
						lines[i].add(l);
						
					}
					
				
				return true;
			}
		}
	}
	
	private float dataConversion(int unit,int indexOfCompound)
	{
		float res=0;
		String name = null;
		switch(unit)
		{
			default:
				res = Compound.counts.get(indexOfCompound);
				break;
			case 3:
				name = (String)Compound.names.get(indexOfCompound);
				res =  p5Canvas.getUnit3().getMassByName(name);
				break;
			case 5:
				res = Float.parseFloat((String) tableView.data[0].get(indexOfCompound));
				break;
			case 6:
				name = (String)Compound.names.get(indexOfCompound);
				res = p5Canvas.getUnit6().getConByName(name);
				break;
		}
		return res;
	}
	
	public void setPaintLineEnable(boolean flag)
	{
		paintLineEnabled = flag;
		
	}
	
	//Paint lines
	private void paintLines(Graphics2D g,int w, int h,int w2, int h2,int margin)
	{
		//Update table value and be ready to show
		//If values are not ready, not draw
		if (!updateLineValue(w,h,w2,h2,margin) || !paintLineEnabled)
			return;
		
		
		//Highlight selected line if any of them has been selected
		boolean blinkColor = false;
		for (int i=0; i< Compound.names.size();i++){
			blinkColor = (main.getTableView().selectedRowsContain(i))?true:false;					
			for (int index=0; index< lines[i].size();index++){
				Line l = (Line) lines[i].get(index);
					l.paint(g,blinkingColor(main.getTableView().colors[i],blinkColor));

			}
		}
	}
	
	//Get molecules number from simulation before painting
	public void updateMoleculeCount(){
	
		//For particular cases
		if (p5Canvas.getUnit()==1 && (p5Canvas.getSim()==4||(p5Canvas.getSim()==2&&p5Canvas.getSet()==2))){
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
		if (p5Canvas.getUnit()==2 && p5Canvas.getSet()==1 && p5Canvas.getSim()<4){
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
		else if (p5Canvas.getUnit()==2 && p5Canvas.getSet()==4){
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
		else if (p5Canvas.getUnit()==2 && p5Canvas.getSet()==7){
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
		else if (p5Canvas.getUnit()==2 && p5Canvas.getSet()==1 && p5Canvas.getSim()==4){
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
	

	

	
	/*public static Color getSelecttedColor(){
		int index = TableView.selectedRow;
		if (index<0 || index>=mNames.size())
			return Color.BLACK;
		Color c1 = TableView.colors[index];
		return c1;
	}*/
	public Color blinkingColor(Color c1,boolean blinkColor){
		
		if(blinkColor)
		{
		int num = 50;
		int r,g,b;
		r = c1.getRed()+num;   
		if (r>255) r = 255;
		g = c1.getGreen()+num; 
		if (g>255) g = 255;
		b = c1.getBlue()+num;  
		if (b>255) b = 255;
		Color c2 = new Color(r, g,b);
		//c2 = Color.gray;
		return c2;
		}
		else
			return c1;
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
					if(p5Canvas.getUnit()==3)
					{
						int index = i ;
						String name = Compound.names.get(index);
						//float mass = Compound.moleculeWeight.get(index)* Compound.counts.get(index);
						float mass = p5Canvas.getUnit3().getMassByName(name);
						DecimalFormat df = new DecimalFormat("###.##");
						String massStr = df.format(mass);
						String tooltipText = new String(name+": "+massStr+" g");
						this.setToolTipText(tooltipText);
					}
				}
			
			}
		}
	
	}

	

}
