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
import view.P5Canvas;

import model.State;

public class Canvas extends JPanel implements ActionListener, MouseListener, MouseMotionListener{
	public static Timer timer1;
	public static ArrayList mNames = new ArrayList();
	public static ArrayList mCounts = new ArrayList();
	public static final int MAXCOMPOUND = 50;
	public static ArrayList[] lines = new ArrayList[MAXCOMPOUND];
	private long beginTime = 0;
	public static long before = 0;
	private long countTime = 0;
	public static int maxCount =8;
	public static int maxTime =60;
	
	public Canvas() {
		for (int i=0; i<MAXCOMPOUND;i++){
			lines[i] = new ArrayList();
		}
		beginTime = System.currentTimeMillis();
		before = beginTime;
		addMouseMotionListener(this);
		setFocusable(true);
		addMouseListener(this);
	}
	
	public void reset(){
		for (int i=0; i<MAXCOMPOUND;i++){
			lines[i] = new ArrayList();
		}
		beginTime = System.currentTimeMillis();
		before = beginTime;
		countTime = 0;
		maxCount =8;
		maxTime =60;
		if (Main.elapsedTime !=null)
			Main.elapsedTime.setText(formatTime(countTime));
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
		Graphics2D g = (Graphics2D) gx;
		int w = 236;
		int h = 178;
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
		g.setFont(new Font("Garamond", Font.PLAIN, 12));
		g.setColor(Color.DARK_GRAY);
		g.drawString("Start", margin, h-6);
		g.setFont(new Font("Garamond", Font.PLAIN, 13));
		g.setColor(Color.BLACK);
		g.drawString("Time", w/2-4, h-5);
		
		
		//Draw Y-Axis
		g.translate(13, h/2+30);
		g.rotate(-Math.PI/2.0);
		g.setFont(new Font("Garamond", Font.PLAIN, 13));
		g.setColor(Color.BLACK);
		g.drawString("# molecules", 0, 0);
		g.rotate(Math.PI/2.0);
		g.translate(-13, -(h/2+30));
		
		g.setFont(new Font("Garamond", Font.PLAIN, 12));
		g.setColor(Color.DARK_GRAY);
		g.drawString("0", 8, h-margin);
		g.setFont(new Font("Garamond", Font.PLAIN, 12));
		g.setColor(Color.DARK_GRAY);
		g.drawString(""+maxCount, 2, margin-5);
		
		if (!P5Canvas.isEnable)
			before = System.currentTimeMillis();
		
		long now = System.currentTimeMillis();
		long ccc = (long) ((now - before)*P5Canvas.speedRate/(1000));
		long last =countTime+ccc;
		if (last>countTime){
			resetMoleculeCount();
			updateTableView();
			
			if (last>maxTime){
				maxTime *=2;
			}
			//Rescale Y-axis
			for (int i=0; i< mNames.size();i++){
				int num2 = Integer.parseInt(mCounts.get(i).toString());
				
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
				Line l = new Line(margin, 178-margin, (int) countTime, (int) last,  num1, num2, h2, w2);
				lines[i].add(l);
			}
			Main.elapsedTime.setText(formatTime(countTime));
			P5Canvas.computeEnergy();
			countTime =last;
			before =now;
			
		}
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
		g.setFont(new Font("Garamond", Font.PLAIN, 12));
		g.setColor(Color.DARK_GRAY);
		int mins = maxTime/60;
		if (mins ==1)
			g.drawString("1 min", w-30, h-6);
		else 
			g.drawString(maxTime/60+" mins", w-35, h-6);
		//Draw X-grid
		for (int i=1; i<mins;i++){
			g.setColor(new Color(255,255,255,30));
			g.drawLine(margin+w2*i/mins, margin/2, margin+w2*i/mins, margin/2+h2);
			
		}
		
		
	}
	

	public static void resetMoleculeCount(){
		mNames = new ArrayList();
		mCounts = new ArrayList();
		for (int i=0; i<State.molecules.size();i++){
			Molecule m = (Molecule) State.molecules.get(i);
			String name = m.getName();
			int index = getNameIndex(name);
			if (index<0){
				mNames.add(name);
				mCounts.add(1);
			}
			else{
				int count = Integer.parseInt(mCounts.get(index).toString());
				count++;
				mCounts.set(index, count);
			}
		}
		
	}
	public static void updateTableView(){
		TableView.data[0] = new ArrayList();
		TableView.data[1] = new ArrayList();
		TableView.data[2] = new ArrayList();
		for (int i=0; i<mNames.size();i++){
			String name = mNames.get(i).toString();
			int count = Integer.parseInt(mCounts.get(i).toString());
			TableView.data[0].add(count);
			TableView.data[1].add(TableView.colors[i]);
			TableView.data[2].add(name);
		}
		if (Main.tableView !=null && !Main.tableView.stopUpdating){
			Main.tableView.table.updateUI();
		}		
	}
	
	public static int getNameIndex(String s){
		for (int i=0; i< mNames.size();i++){
			String mName = (String) mNames.get(i);
			if (mName.equals(s))
				return i;
		}
		return -1;
	}
	public static String getSelecttedmolecule(){
		int index = TableView.selectedRow;
		if (index<0 || index>=mNames.size())
			return "";
		return mNames.get(index).toString();
	}
	public static Color getSelecttedColor(){
		int index = TableView.selectedRow;
		if (index<0 || index>=mNames.size())
			return Color.BLACK;
		Color c1 = TableView.colors[index];
		return c1;
	}
	public static Color blinkingColor(Color c1){
		int num = (P5Canvas.count%26+1)*10;
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
		for (int i=0; i<mNames.size();i++){
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
