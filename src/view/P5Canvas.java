package view;

import java.awt.Color;
import java.util.ArrayList;

import pbox2d.*;
import processing.core.PApplet;


import org.jbox2d.collision.shapes.*;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.*;

import Util.ColorScales;
import static model.State.*;

public class P5Canvas extends PApplet{
	private float x;
	private float y;
	private float w;
	private float h;
	
	// A reference to our box2d world
	private PBox2D box2d = new PBox2D(this);
	public static boolean isEnable = true;
	public static float speedRate = 1.f;
	public static float heatRate = 1.f;
	public static final int heatMaxMultiplier = 2;
	public static int heatRGB = 0;
	
	public void setup() {
		smooth();
		frameRate(30);
		
		// Initialize box2d physics and create the world
		box2d.createWorld();
		box2d.setGravity(0f,0f);
		// Turn on collision listening!
		// TODO turn on collisions by un-commenting below
		box2d.listenForCollisions();
		
		setBoudary(0,0,500,400);
	}
	
	public void setBoudary(int xx, int yy, int ww, int hh) {
		x=xx;
		y=yy;
		w = ww;
		h = hh;
		size((int) w, (int) h);
		
		// Add a bunch of fixed boundaries
		float bW = 10.f; // boundary width
		Boundary lBound = new Boundary(x    , y+h/2, bW, h, box2d, this);
		Boundary rBound = new Boundary(x+w  , y+h/2, bW, h, box2d, this);
		Boundary tBound = new Boundary(x+w/2, y,     w,  bW, box2d, this);
		Boundary bBound = new Boundary(x+w/2, y+h,   w,  bW, box2d, this);
		if (boundaries[0] != null)
			boundaries[0].killBody();
		if (boundaries[1] != null)
			boundaries[1].killBody();
		if (boundaries[2] != null)
			boundaries[2].killBody();
		if (boundaries[3] != null)
			boundaries[3].killBody();
		boundaries[0]=lBound;
		boundaries[1]=rBound;
		boundaries[2]=tBound;
		boundaries[3]=bBound;
	}
		
	public void draw() {
		if (getSize().width != w || getSize().height!=h){
			setBoudary(0,0,this.getSize().width,this.getSize().height);
		}
		
		drawBackground();
		// We must always step through time!
		
		if (isEnable)
			box2d.step();
		
		// Display all molecules
		for (int i = 0; i < molecules.size(); i++) {
			Molecule m = molecules.get(i);
			m.display();
		}
		boundaries[0].display();
		boundaries[1].display();
		boundaries[2].display();
		boundaries[3].display();
		
		// boundaries are not displayed.  If they should be, use a display method in the Boundary class.
		// System.out.println("x: " + str(boundaries.get(2).x()) +" y: " + str(boundaries.get(2).y()) + " w: " + str(boundaries.get(2).w()) + "  h: " + str(boundaries.get(2).h()) );
		
	}
	
	/*
	 * Background methods
	 */
	
	private void drawBackground() { // draw background
		pushStyle();
		fill(63, 63, 127);
		rect(0, 0, width, height);
		popStyle();
	}
	
	/*
	 * Function to create compounds from outside the PApplet
	 */
	
	public void addMolecule(String compoundName, int count) {
		// The tmp variable helps to fix a Box2D Bug: 2147483647  because of Multithreading
		// at pbox2d.PBox2D.step(PBox2D.java:81)
		// at pbox2d.PBox2D.step(PBox2D.java:72)
		// at pbox2d.PBox2D.step(PBox2D.java:67)
		// at view.P5Canvas.draw(P5Canvas.java:73)
		boolean tmp = isEnable;
		isEnable = false;
		for (int i=0;i<count;i++){
			float x_ =(i+1)*(w/(count+1));
			float y_ = 100;
			molecules.add(new Molecule(x_, y_,compoundName, box2d, this, speedRate));
		}
		isEnable = tmp;
	}
	
	
	public void addMolecule(float x_, float y_, String compoundName) {
		molecules.add(new Molecule(x_,y_,compoundName, box2d, this, speedRate));
	}
	
	public void removeAllMolecules() {
		boolean tmp = isEnable;
		
		isEnable = false;
		for (int i =0; i< molecules.size(); i++){
			Molecule m = (Molecule) molecules.get(i);
			m.killBody();
		}
		molecules =  new ArrayList();
		
		isEnable = tmp;
	}
	
	//Set Speed of Molecules; values are from 0 to 100; 20 is default value 
	public void setSpeed(int value, int defaultSpeed) {
		boolean tmp = isEnable;
		isEnable = false;
		
		speedRate = (float) value/defaultSpeed;
		for (int i =0; i< molecules.size(); i++){
			Molecule m = (Molecule) molecules.get(i);
			m.setSpeed(speedRate);
		}
		
		isEnable = tmp;
	}
	
	
	//Set Speed of Molecules; values are from 0 to 100; 20 is default value 
	public void setHeat(int value) {
		if (value>=50)
			heatRate = 1.f+(float) (value-50)*(heatMaxMultiplier-1)/50;
		else{
			heatRate = (float) (50-value)/50;
			float reverseMultiplier = 1.f/heatMaxMultiplier;
			heatRate = (1-heatRate)*(1-reverseMultiplier) +reverseMultiplier;
		}
		double v = (double) value/100;
		Color color = ColorScales.getColor(1-v, "redblue", 1f);
		heatRGB = color.getRGB();
	}
	
	
	
	public void mousePressed() {
		addMolecule(mouseX, mouseY,"Water");
	}
	// Collision event functions!
	public void addContact(ContactPoint cp) {
		System.out.println("Contact heatRate: "+heatRate);
		// Get both shapes
		Shape s1 = cp.shape1;
		Shape s2 = cp.shape2;
		// Get both bodies
		Body b1 = s1.getBody();
		Body b2 = s2.getBody();
		// Get our objects that reference these bodies
		Object o1 = b1.getUserData();
		Object o2 = b2.getUserData();

		// What class are they?  Box or Particle?
		String c1 = o1.getClass().getName();
		String c2 = o2.getClass().getName();
		// If object 1 is a Box, then object 2 must be a particle
		// Note we are ignoring particle on particle collisions
		if (c1.contains("Boundary")) {
			Molecule p = (Molecule) o2;
			p.setSpeedByHeat(heatRate);
		} 
		// If object 2 is a Box, then object 1 must be a particle
		else if (c2.contains("Boundary")) {
			Molecule p = (Molecule) o1;
			p.setSpeedByHeat(heatRate);
		}
	}
}
