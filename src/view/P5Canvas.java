package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.ArrayList;

import pbox2d.*;
import processing.core.PApplet;

import main.Main;

import org.jbox2d.collision.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.*;

import Util.ColorScales;
import Util.MP3;
import static model.State.*;
import model.DBinterface;
import model.YAMLinterface;

public class P5Canvas extends PApplet{
	public static float x;
	public static float y;
	public static float w;//width of the boundary
	public static float h;//width of the boundary
	public static float width;//width of this Panel
	public static float height;//width of this  Panel
	
	// A reference to our box2d world
	private PBox2D box2d = new PBox2D(this);
	public static boolean isEnable = false; 
	public static int creationCount = 0;
	public static float temp =25.f;
	
	//Default value of speed
	public static float speedRate = 1.f;
	//Default value of heat
	public static float heatRate = 1.f;
	//Default value of scale slider
	public static float scale = 1.f;
	//Default value of volume slider
	public static int defaultVolume =50;
	public static int currenttVolume =defaultVolume;
	public static int multiplierVolume =10; // Multiplier from pixels to ml
	public static float maxH=1100;//minimum height of container
	
	public static int heatRGB = 0;
	
	public static int count = 0;
	public static int xStart = 0;
	public static int yStart = 0;
	public static int xDrag = 0;
	public static int yDrag = 0;
	public static boolean isDrag = false;
	ArrayList<String> products = new ArrayList<String>();
	ArrayList<Molecule> killingList = new ArrayList<Molecule>();
	public static int draggingBoundary =-1;
	private boolean isFirstTime =true;
	private float frameRate =24;
	
	
	/*
	 * for testing
	 */
	public static DBinterface db = new DBinterface();
	public static YAMLinterface yaml = new YAMLinterface();
	
	public void updateSize(Dimension d, int volume) {
		boolean tmp = isEnable;
		isEnable = false;
		
		//setBoundary(0,0,d.width,d.height);
		width = d.width;
		height = d.height;
		maxH = (volume + defaultVolume)*multiplierVolume;
		
		isEnable = tmp;
	}
		
	public void setup() {
		smooth();
		frameRate(frameRate);
		
		// Initialize box2d physics and create the world
		box2d.createWorld(-400,-400, 600, 600);
		box2d.setGravity(0f,-10f);
	
		
		// Turn on collision listening!
		// TODO turn on collisions by un-commenting below
		box2d.listenForCollisions();
		setBoundary(0,0,648,600);	
		//testDbInterface();
	}
	
	
	
	
		
	public void setBoundary(float xx, float yy, float ww, float hh) {
		if (hh>maxH) return;
		x=xx;
		y=yy;
		w = ww;
		h = hh;
		if (isFirstTime){
			size((int) w, (int) h);
			isFirstTime =false;
		}
		
		// Add a bunch of fixed boundaries
		float bW = 10.f; // boundary width
		int sliderValue = Main.volumeSlider.getValue();
		Boundary lBound = new Boundary(0,x 	,  y , bW, 2*h , sliderValue, box2d, this);
		Boundary rBound = new Boundary(1,x+w , y , bW, 2*h, sliderValue, box2d, this);
		Boundary tBound = new Boundary(2,x+w/2, y,     w +bW , bW, sliderValue, box2d, this);
		Boundary bBound = new Boundary(3,x+w/2, y+h,   w +bW , bW, sliderValue, box2d, this);
		
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
				Vec2 loc =m1.getPosition();
				float x1 = box2d.scalarWorldToPixels(loc.x);
				float y1 = h-box2d.scalarWorldToPixels(loc.y);
				Vec2 newVec =removeDuplicatePosition(new Vec2(x1,y1));
				Molecule m = new Molecule(newVec.x, newVec.y,products.get(i), box2d, this);
				molecules.add(m);
				if (i==0)
					m.setLinearVelocity(m1.getLinearVelocity());
				
				else{
					m.setLinearVelocity(m2.getLinearVelocity());
				}
					
			}
			m1.killBody();
			m2.killBody();
			molecules.remove(m1);
			molecules.remove(m2);
			
			products = new ArrayList<String>();
			killingList = new ArrayList();
		}
		
		this.scale(scale);
		if (isEnable){
			float newFrameRate=24;
 			if (speedRate>=1){
 				newFrameRate = newFrameRate*speedRate;
			}
			if (frameRate !=newFrameRate){
				frameRate = newFrameRate;
				frameRate(frameRate);
			}
				
			float timeStep = 1 / 60.0f;
			if (speedRate<1){
				timeStep *= speedRate;
			}
 			box2d.step(timeStep,5);
			
			//System.out.println("speedRate: "+speedRate);
			// Apply gravity to molecules
			for (int i = 0; i < molecules.size(); i++) {
				Molecule m = molecules.get(i);
				if (m!=null && !isDrag){
					setForce(i,m);
				}	
			}
		}	
		
		
		for (int i = 0; i < molecules.size(); i++) {
			Molecule m = molecules.get(i);
			if (m.getName().equals("Hydrogen") || m.getName().equals("Sodium-Hydroxide"))
			m.display2();
		}
		
		for (int i = 0; i < 4; i++) {
			boundaries[i].display();
		}
		
			
		// Display all molecules
		for (int i = 0; i < molecules.size(); i++) {
			Molecule m = molecules.get(i);
			m.display();
		}
		
	}
	public static void computeEnergy(){
		float sum = 0f;
		for (int i = 0; i < molecules.size(); i++) {
			Molecule m = molecules.get(i);
			Vec2 vec = m.getSpeed();
			if (vec!=null){
				float v = vec.x*vec.x + vec.y*vec.y;
				sum += v*m.getMass();
			}
		}
		
		float average =0f;
		if (molecules.size()>0)
			average = sum/molecules.size();
		
		if (Main.totalSystemEnergy!=null){
			DecimalFormat df = new DecimalFormat("#.##");
			Main.totalSystemEnergy.setText(df.format(sum)+" kJ");
			Main.averageSystemEnergy.setText(df.format(average )+" kJ");
		}	
	}
	
	private void setForce(int index, Molecule mIndex) { // draw background
		for (int i = 0; i < molecules.size(); i++) {
			if (i==index)
				continue;
			Molecule m = molecules.get(i);
			Vec2 loc = m.getPosition();
			Vec2 locIndex = mIndex.getPosition();
			if(loc==null || locIndex==null) continue;
			float x = locIndex.x-loc.x;
			float y = locIndex.y-loc.y;
		   float dis = x*x +y*y;
			Vec2 normV = normalizeForce(new Vec2(x,y));
			float forceX;
			float forceY;
			if (mIndex.polarity==m.polarity){
				float fTemp = mIndex.freezingTem;
				float bTemp = mIndex.boilingTem;
				float gravityX,gravityY;
				if (temp>=bTemp){
					gravityX = 0;
					gravityY = 0;
				}
				else if (temp<=fTemp){
					gravityY = (bTemp-temp)/(bTemp-fTemp);
					gravityX = gravityY*1.75f;
				}	
				else{
					gravityY = (bTemp-temp)/(bTemp-fTemp);
					gravityX = gravityY*0.5f;
				}	
				forceX =  (-normV.x/dis)*m.getMass()*mIndex.getMass()*gravityX*5000;
				forceY =  (-normV.y/dis)*m.getMass()*mIndex.getMass()*gravityY*5000;
			}	
			else{
				float num = m.getNumElement();
				forceX =  (normV.x/dis)*m.getMass()*mIndex.getMass()*300*num;
				forceY =  (normV.y/dis)*m.getMass()*mIndex.getMass()*300*num;
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
		
		if (tmp) //if Applet is enable
			creationCount =0;
		else
			creationCount++;
									// variables are used to distribute molecules
		int mod = creationCount%4;  // When the system is paused; Otherwise, molecules are create at the same position
		for (int i=0;i<count;i++){
			float x_ =x + w/2 +40+ (i-count/2.f)*(w/11) + creationCount;
			float y_ =y + 80-Boundary.difVolume +(mod-1.5f)*20;
			Vec2 newVec =removeDuplicatePosition(new Vec2(x_,y_));
			molecules.add(new Molecule(newVec.x, newVec.y,compoundName, box2d, this));
		}
		isEnable = tmp;
	}
	public void addMoleculeRandomly(String compoundName, int count) {
		boolean tmp = isEnable;
		isEnable = false;
		
		float PAD =60;
		for (int i=0;i<count;i++){
			float x_ = x+ this.random(PAD, w-PAD);
			float y_ = y+ this.random(PAD, h-PAD);
			Vec2 newVec =removeDuplicatePosition(new Vec2(x_,y_));
			molecules.add(new Molecule(newVec.x, newVec.y,compoundName, box2d, this));
		}
		isEnable = tmp;
	}
		
	public boolean isDuplicatePosition(Vec2 v) {
		for (int i = 0; i < molecules.size(); i++) {
			Molecule m = molecules.get(i);
			if (m!=null){
				Vec2 vec = box2d.coordWorldToPixels(m.getPosition());
				if (v.x-1 <= vec.x && vec.x<=v.x+1 && v.y-1<=vec.y && vec.y<=v.y+1)
					return true;
			}	
		}
		return false;
	}
	public Vec2 removeDuplicatePosition(Vec2 v) {
		while (isDuplicatePosition(v)){
			v.x=v.x+5;
			v.y=v.y+5;
		}
		return v;
	}	
	
	public void addMolecule(float x_, float y_, String compoundName) {
		Vec2 newVec =removeDuplicatePosition(new Vec2(x_,y_));
		Molecule m = new Molecule(newVec.x, newVec.y,compoundName, box2d, this);
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
	public void setSpeed(float speed) {
		speedRate = speed;
	}
	
	
	//Set Speed of Molecules; values are from 0 to 100; 50 is default value 
	public void setHeat(int value) {
		temp = value;
		for (int i = 0; i < molecules.size(); i++) {
			Molecule m = molecules.get(i);
			float fTemp = m.freezingTem;
			float bTemp = m.boilingTem;
			float res = (temp-fTemp)/(bTemp-fTemp);
			
			if (res>0 && res<1){
				res =0.90f+res*0.13f;
				//res = (float) Math.pow(res, 0.5);
			}
			else if (res>=1)
				res =1.1f+res/10;
			else
				res=0.2f;
			m.setRestitution(res);
			
			
			float fric;
			if (temp<fTemp) fric =1;
			else  			fric =0; 
			m.setFriction(fric);
	
			float scale=1;
			if (m.getName().equals("Water") && temp<40){
				scale = 1+(40-temp)/200f;
			}	
			m.setRadius(scale);
			//System.out.println( "Res:"+res+" fric:"+fric+" scale"+scale);
		}
		
		
		double v = (double) (value-Main.heatMin)/200;
		v=v+0.3;
		if (v>1) v=1;
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
	public void setVolume(int value) {
		boolean tmp = isEnable;
		isEnable = false;
		boundaries[2].set(value);
		
		currenttVolume = value;
		isEnable = tmp;
	}
	
	
	//********************************************************* MOUSE EVENT ******************************
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
		
		//Check the top boundary
	/*	int id = boundaries[2].isIn(mouseX, mouseY);
		if (id==2)
			this.cursor(Cursor.N_RESIZE_CURSOR);
		else
			this.cursor(Cursor.DEFAULT_CURSOR);*/
	}
	
	public void mouseDragged() {
		isDrag = true;
			
		int xTmp = xDrag;
		int yTmp = yDrag;
		xDrag = (int) ((mouseX-xStart)/scale);
		yDrag = (int) ((mouseY-yStart)/scale);
		
		//Dragging the top boundary
		if (draggingBoundary!=2){
			setBoundary(x+xDrag -xTmp,y+yDrag - yTmp,w,h);
		}
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
		/*if (c1.contains("Boundary")) {
			Boundary b = (Boundary) o1;
			if (b.getId()==3){
				//Molecule p = (Molecule) o2;
				//p.setSpeedByHeat(1.5f);
			}
			
			
		} 
		// If object 2 is a Box, then object 1 must be a particle
		else if (c2.contains("Boundary")) {
			//Molecule p = (Molecule) o1;
			//p.setSpeedByHeat(heatRate);
		}
		else */
		if (c1.contains("Molecule") && c2.contains("Molecule")){
			Molecule m1 = (Molecule) o1;
			Molecule m2 = (Molecule) o2;
			ArrayList<String> reactants = new ArrayList<String>();
			reactants.add(m1.getName());
			reactants.add(m2.getName());
			if (temp>=110){
				float random = this.random(110, 210);
				if (random<temp){
					products = getReactionProducts(reactants);
					if (products!=null && products.size()>0){
						killingList.add(m1);
						killingList.add(m2);
					}
				}
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
