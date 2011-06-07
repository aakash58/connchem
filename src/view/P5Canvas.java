package view;

import java.awt.Color;
import java.awt.Cursor;
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
	public static float volume = 1.f;
	public static int heatRGB = 0;
	
	public static int count = 0;
	public static int xStart = 0;
	public static int yStart = 0;
	public static int xDrag = 0;
	public static int yDrag = 0;
	public static boolean isDrag = false;
	ArrayList<String> products = new ArrayList<String>();
	ArrayList killingList = new ArrayList();
	public static int draggingBoundary =-1;
	
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
		setBoundary(0,0,648,600);
		
		//testDbInterface();
	}
	
	private void testDbInterface() {
		
		ArrayList<String> reactants = new ArrayList<String>();
		reactants.add("Hydrogen Peroxide");
		reactants.add("Hydrogen Peroxide");
		//reactants.add("Water");
		//reactants.add("Ammonium");
		
		println(db.getReactionProducts(reactants));
		println(db.getReactionProbability(10));
	}
	
	
	
	public void setBoundary(float xx, float yy, float ww, float hh) {
		x=xx;
		y=yy;
		w = ww;
		h = hh;
		size((int) w, (int) h);
		
		// Add a bunch of fixed boundaries
		float bW = 10.f; // boundary width
		Boundary lBound = new Boundary(0,x 	, y+h/2, bW, h , box2d, this);
		Boundary rBound = new Boundary(1,x+w  , y+h/2, bW , h, box2d, this);
		Boundary tBound = new Boundary(2,x+w/2, y,     w +bW ,  bW, box2d, this);
		Boundary bBound = new Boundary(3,x+w/2, y+h,   w +bW ,  bW, box2d, this);
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
			//setBoundary(0,0,this.getSize().width,this.getSize().height);
		}
		
		drawBackground();
		// We must always step through time!
		if (products!=null && products.size()>0){
			Molecule m1 = (Molecule) killingList.get(0);
			Molecule m2 = (Molecule) killingList.get(1);
			for (int i=0;i<products.size();i++){
				Vec2 loc =m1.getLocation();
				addMolecule(box2d.scalarWorldToPixels(loc.x),box2d.scalarWorldToPixels(loc.y),products.get(i));
			}
			m1.killBody();
			m2.killBody();
			molecules.remove(m1);
			molecules.remove(m2);
			
			products = new ArrayList<String>();
			killingList = new ArrayList();
		}
		
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
			float x = locIndex.x-loc.x;
			float y = locIndex.y-loc.y;
			float dis = x*x +y*y;
			Vec2 normV = normalizeForce(new Vec2(x,y));
			float forceX;
			float forceY;
			if (mIndex.getName().equals(m.getName())){
				forceX =  (-normV.x/dis)*m.getMass()*mIndex.getMass()*gravity;
				forceY =  (-normV.y/dis)*m.getMass()*mIndex.getMass()*gravity;
			}
			else{
				forceX =  (normV.x/dis)*m.getMass()*mIndex.getMass()*gravity;
				forceY =  (normV.y/dis)*m.getMass()*mIndex.getMass()*gravity;
			}
			mIndex.addForce(new Vec2(forceX,forceY));
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
			float x_ =w/3+i*(w/(3*(count+1)));
			x_ = x+x_*scale;
			float y_ =y+100*scale;
			molecules.add(new Molecule(x_, y_,compoundName, box2d, this, speedRate));
		}
		isEnable = tmp;
	}
	
	
	public void addMolecule(float x_, float y_, String compoundName) {
		Molecule m = new Molecule(x_,y_,compoundName, box2d, this, speedRate);
		molecules.add(m);
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
		gravity = (float) Math.pow(gravity, 1.3);
		
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
		//System.out.println("HEAT:"+heatRate);
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
	
	//Set Volume; values are from 0 to 100; 50 is default value 
	public void setVolume(int value, int defaultScale) {
		boolean tmp = isEnable;
		isEnable = false;
		volume = (float) value/defaultScale;
		System.out.println("Volume: "+volume);
		//float ww = (w*volume);
		//float hh = (h*volume);
		//setBoundary(x,y,  ww, h);
		isEnable = tmp;
	}
	
	
	
	public void mouseMoved() {
		//Check the top boundary
		int id = boundaries[2].isIn(mouseX, mouseY);
		if (id==2)
			this.cursor(Cursor.N_RESIZE_CURSOR);
		else
			this.cursor(Cursor.DEFAULT_CURSOR);
	}
		
	public void mousePressed() {
		xStart = mouseX;
		yStart = mouseY;
	
		draggingBoundary = boundaries[2].isIn(mouseX, mouseY);
		
	}
	public void mouseReleased() {
		xDrag =0;
		yDrag =0;
		isDrag = false;
		draggingBoundary =-1;
	
		
	}
		
	
	public void mouseDragged() {
		
		isDrag = true;
			
		int xTmp = xDrag;
		int yTmp = yDrag;
		xDrag = (int) ((mouseX-xStart)/scale);
		yDrag = (int) ((mouseY-yStart)/scale);
		if (draggingBoundary==2){
			//h = h-(yDrag - yTmp);
		}	
		setBoundary(x+xDrag -xTmp,y+yDrag - yTmp,w,h-(yDrag - yTmp));
		
	}
		
	public void mouseClicked() {
		addMolecule(mouseX/scale, mouseY/scale,"Water");
	}
	
	
	
	
	// A FAKE function for testing
	private ArrayList<String> getReactionProducts(ArrayList<String> reactants) {	
		if (reactants.get(0).equals("Hydrogen-Peroxide") &&
			reactants.get(1).equals("Hydrogen-Peroxide")){
			ArrayList<String> products = new ArrayList<String>();
			products.add("Water");
			products.add("Water");
			products.add("Oxygen");
			
			return products;
		}
		else{
			return null;
		}
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
		else if (c1.contains("Molecule") && c2.contains("Molecule")){
			Molecule m1 = (Molecule) o1;
			Molecule m2 = (Molecule) o2;
			ArrayList<String> reactants = new ArrayList<String>();
			reactants.add(m1.getName());
			reactants.add(m2.getName());
			products = getReactionProducts(reactants);
			if (products!=null && products.size()>0){
				killingList.add(m1);
				killingList.add(m2);
			}
			
			
		}
	}
	
	public void persistContact(ContactPoint cp) {
	}
	public void removeContact(ContactPoint cp) {
	}
	public void resultContact(ContactResult cr) {
	}
		
}
