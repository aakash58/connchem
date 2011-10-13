package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;




public class Line{
  private int x1;
  private int y1;
  private int x2;
  private int y2;
  private int marginX;
  private int marginY;
  private int time1;
  private int time2;
  private int num1;
  private int num2;
  private int height;
  private int width;
  private Canvas canvas;
    
  private Color color = new Color(255,0,0);
  
  public static int size =5;
  
 
  public Line(int marginX_, int marginY_, int time1_, int time2_, int num1_, int num2_, int height_, int width_, Canvas parent) {
	  marginX = marginX_;
	  marginY = marginY_;
	  time1 = time1_;
	  time2 = time2_;
	  num1 = num1_;
	  num2 = num2_;
	  height = height_;
	  width = width_;
	  canvas = parent;

  } 
  
  
  
  public int getNum2() {
	  	return num2;
  } 
  
  public void paint(Graphics2D g, Color c) {
	  g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		
	  	g.setStroke(new java.awt.BasicStroke(2));
		g.setColor(c);  
		x1 = marginX+time1*width/canvas.maxTime;
		x2 = marginX+time2*width/canvas.maxTime;
		y1 = marginY - num1*height/canvas.maxCount;
		y2 = marginY - num2*height/canvas.maxCount;
	  	g.drawLine(x1, y1, x2, y2); 
  }
  
  public boolean isIn(float mouseX, float mouseY) {
	  if (y1<=y2){
		  if (mouseX>=x1 && mouseX<x2 && mouseY>=y1-2 && mouseY<=y2+2){
			  return true;
		  }
	  }
	  else{
		  if (mouseX>=x1 && mouseX<x2 && mouseY>=y2-2 && mouseY<=y1+2){
			  return true;
		  }
	  }
	 
	  return false;
  }
  /*
  public JToolTip createToolTip() {
      JToolTip tip = super.createToolTip();
      tip.setBackground(Color.yellow);
      tip.setForeground(Color.green);
      return tip;
    }

    public boolean contains(int x, int y) {
    	System.out.println("Contain:x ="+x+" ,y = "+y);
        this.setToolTipText("ToolTips!");
      return super.contains(x, y);
    }
    
  
  public Point getToolTipLocation(MouseEvent e) {
      return new Point(e.getX(),e.getY());
    }
    */
  
}
