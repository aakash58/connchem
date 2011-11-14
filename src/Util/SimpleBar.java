package Util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class SimpleBar extends JPanel {
	private float min;
	private float max;
	private float value;
	private Color foreColor = new Color(16, 20, 132);
	private Color bgColor = new Color(16, 20, 132,128);
	private Color borderColor = new Color(max, max, max);
	
	
	public SimpleBar(float vMin, float vMax,float vValue)
	{
		super();
		min = vMin;
		max = vMax;
		value = vValue;	
	}
	public SimpleBar(float vMin, float vMax)
	{
		this(vMin,vMax,vMin);
	}

	 public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		//Graphics does not support anti-aliasing, so we transform it into graphics2d
		Graphics2D g2D =(Graphics2D) graphics;
	    RenderingHints  qualityHints=new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);   
	    qualityHints.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);   
	    g2D.setRenderingHints(qualityHints);   
		Dimension dim = getPreferredSize();
		int panelWidth = dim.width;
		int panelHeight = dim.height;
		int x = getInsets().left;
		int y = getInsets().top;
		int cornerRadius = panelWidth/6;
		
		
		int barHeight = (int) (((value-min)/(max-min))*panelHeight);
		//Draw background
		g2D.setColor(bgColor);
		  drawCustomBar(g2D,x,y,panelWidth,panelHeight,cornerRadius);
		//Draw bar
		g2D.setColor(foreColor);
		drawCustomBar(g2D,x, y+panelHeight-barHeight, panelWidth, barHeight,cornerRadius);
		//Draw border
	 }
	 
	 public void drawCustomBar(Graphics2D graphics,int x,int y,int w,int h,int radius)
	 {
		 graphics.fillRect(x, y+radius, w, h-radius);
		 graphics.fillRect(x+radius, y, w-radius*2, radius);
		 graphics.fillArc(x, y, radius*2, radius*2,90,90);//Top left coner
		 graphics.fillArc(x+w-radius*2, y, radius*2, radius*2,0,90); //Top right corner
	 }
	 
	 public void setValue(float v)
	 {
		 value = v;
	 }

}
