package view;

import java.awt.Color;
import java.util.ArrayList;

import pbox2d.*;
import processing.core.PApplet;


import main.Main;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.*;

import Util.ColorScales;
import static model.State.*;
import model.DBinterface;

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
	public static float restitution =0.5f;
	public static float gravity =1.f;
	public static float scale = 1.f;
	public static int heatRGB = 0;
	
	public static int count = 0;
	public static int xStart = 0;
	public static int yStart = 0;
	public static int xDrag = 0;
	public static int yDrag = 0;
	public static boolean isDrag = false;
	
	
	/*
	 * for testing
	 */
	DBinterface db = new DBinterface();
	
	public void setup() {
		smooth();
		frameRate(24);
		
		// Initialize box2d physics and create the world
		box2d.createWorld(-1000,-1000, 2000, 2000);
		box2d.setGravity(0f,0f);
		// Turn on collision listening!
		// TODO turn on collisions by un-commenting below
		box2d.listenForCollisions();
		setBoundary(0,0,700,600);
		
		testDbInterface();
	}
	
	private void testDbInterface() {
		
		ArrayList<String> reactants = new ArrayList<String>();
		reactants.add("Ammonia");
		reactants.add("Water");
		//reactants.add("Ammonium");
		
		println(db.getReactionProducts(reactants));
		println(db.getReactionProbability(10));
	}
	
	public void setBoundary(int xx, int yy, int ww, int hh) {
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
			setBoundary(0,0,this.getSize().width,this.getSize().height);
		}
		
		drawBackground();
		// We must always step through time!
		
		if (isEnable)
			box2d.step();
		
		// Display all molecules
		this.scale(scale);
		
		for (int i = 0; i < molecules.size(); i++) {
			Molecule m = molecules.get(i);
			m.setRestitution(restitution);
			setForce(i,m);
		}
		for (int i = 0; i < molecules.size(); i++) {
			Molecule m = molecules.get(i);
			m.display();
		}
		boundaries[0].display();
		boundaries[1].display();
		boundaries[2].display();
		boundaries[3].display();
			
	}
	private void setForce(int index, Molecule mIndex) { // draw background
		for (int i = 0; i < molecules.size(); i++) {
			if (i==index)
				continue;
			Molecule m = molecules.get(i);
			Vec2 loc = m.getLocation();
			Vec2 locIndex = mIndex.getLocation();
			float x = loc.x-locIndex.x;
			float y = loc.y-locIndex.y;
			float dis = x*x +y*y;
			Vec2 normV = normalizeForce(new Vec2(x,y));
			float forceX =  (-normV.x/dis)*m.getMass()*mIndex.getMass()*2.5f*gravity;
			float forceY =  (-normV.y/dis)*m.getMass()*mIndex.getMass()*2.5f*gravity;
			m.addForce(new Vec2(forceX,forceY));
		}	
	}
		
	private Vec2 normalizeForce(Vec2 v){
		float dis = (float) Math.sqrt(v.x*v.x + v.y*v.y);
		return new Vec2(v.x/dis,v.y/dis);
		
	}
	

	
	
	/*
	 * Background methods
	 */
	
	private void drawBackground() { // draw background
		count++;
		if (count>10000)
			count =0;
		Main.canvas.repaint();
		pushStyle();
		fill(127, 127, 127);
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
			float x_ =w/3+(i+1)*(w/(4*(count+1)));
			x_ = boundaries[0].getCurrentX() +x_*scale;
			float y_ = boundaries[0].getCurrentY()+100*scale;
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
		if (value>defaultSpeed)
			speedRate *=2;
		for (int i =0; i< molecules.size(); i++){
			Molecule m = (Molecule) molecules.get(i);
			m.setSpeed(speedRate);
		}
		isEnable = tmp;
	}
	
	
	//Set Speed of Molecules; values are from 0 to 100; 50 is default value 
	public void setHeat(int value) {
		restitution = (float) (value)/50;
		restitution = 0.3f+restitution*0.5f;
		gravity = (100f-value);
		
		
	/*	if (value>=50){
			heatRate = (float) (value-50)/50;
			heatRate =1+heatRate/2;
			// Max heatRate = 1.5
		}	
		else{
			heatRate = (float) (50-value)/50;
			heatRate = (1-heatRate);
			heatRate =0.5f+heatRate/2;
			// MIN heatRate = 0.5
		}*/
		System.out.println("HEAT:"+heatRate);
		double v = (double) value/100;
		Color color = ColorScales.getColor(1-v, "redblue", 1f);
		heatRGB = color.getRGB();
	}
	
	//Set Scale of Molecules; values are from 0 to 100; 50 is default value 
	public void setScale(int value, int defaultScale) {
		boolean tmp = isEnable;
		isEnable = false;
		scale = (float) value/defaultScale;
		isEnable = tmp;
	}
	
	public void mousePressed() {
		xStart = mouseX;
		yStart = mouseY;
	}
	public void mouseReleased() {
		xDrag =0;
		yDrag =0;
		isDrag = false;
	}
		
	
	public void mouseDragged() {
		isDrag = true;
		xDrag = (int) ((mouseX-xStart)/scale);
		yDrag = (int) ((mouseY-yStart)/scale);
	}
		
	public void mouseClicked() {
		addMolecule(mouseX/scale, mouseY/scale,"Water");
	}
	// Collision event functions!
	public void addContact(ContactPoint cp) {
		//System.out.println("Contact heatRate: "+heatRate);
		//System.out.println(Main.leftPanel.getSize()+" "+Main.centerPanel+" "+Main.rightPanel.getSize());
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
			//p.setSpeedByHeat(heatRate);
		} 
		// If object 2 is a Box, then object 1 must be a particle
		else if (c2.contains("Boundary")) {
			Molecule p = (Molecule) o1;
			//p.setSpeedByHeat(heatRate);
		}
	}
	
	public void persistContact(ContactPoint cp) {
	}
	public void removeContact(ContactPoint cp) {
	}
	public void resultContact(ContactResult cr) {
	}
		
}
