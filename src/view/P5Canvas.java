package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.ArrayList;

import processing.core.PApplet;

import main.Canvas;
import main.Main;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.*;

import Util.ColorScales;
import static model.State.*;
import static view.Water.*;
import static view.Compound.*;
import static view.Unit2.*;
import model.DBinterface;
import model.YAMLinterface;

public class P5Canvas extends PApplet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static float x;
	public static float y;
	public static float w;//width of the boundary
	public static float h;//width of the boundary
	
	// A reference to our box2d world
	private PBox2D box2d = new PBox2D(this);
	public static boolean isEnable = false; 
	public static boolean isEnableBrushing = false;
	public static boolean isDisplayForces = false;
	public static boolean isDisplayJoints = false;
	
	public static int creationCount = 0;
	public static float temp =25.f;
	
	//Default value of speed
	public static float speedRate = 1.f;
	//Default value of heat
	public static float heatRate = 1.f;
	//Default value of scale slider
	public static float scale = 0.77f;
	//Default value of volume slider
	public static int defaultVolume =50;
	public static int currenttVolume =defaultVolume;
	public static int multiplierVolume =10; // Multiplier from pixels to ml
	public static float maxH=1100;//minimum height of container
	
	public static int heatRGB = 0;
	
	public static long count = 0;
	public static long second = 0;
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
	public boolean isBrushing=false;
	
	
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
		box2d.createWorld();
		box2d.setGravity(0f,-10f);
	
		
		// Turn on collision listening!
		// TODO turn on collisions by un-commenting below
		box2d.listenForCollisions();
		setBoundary(0,0,560/0.77f,635/0.77f);	
		//testDbInterface();
	}
	
	public void setBoundary(float xx, float yy, float ww, float hh) {
		if (hh>maxH) return;
		x=xx;
		y=yy;
		w = ww;
		h = hh;
		if (isFirstTime){
			size((int) (560), (int) (638));
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
		drawBackground();
		// We must always step through time!
		if (products!=null && products.size()>0){
			Molecule m1 = (Molecule) killingList.get(0);
			Molecule m2 = (Molecule) killingList.get(1);
			for (int i=0;i<products.size();i++){
				Vec2 loc =m1.getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = h*0.77f-PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec =new Vec2(x1,y1);
				Molecule m = new Molecule(newVec.x, newVec.y,products.get(i), box2d, this,0);
				molecules.add(m);
				if (i==0)
					m.body.setLinearVelocity(m1.body.getLinearVelocity());
				
				else{
					m.body.setLinearVelocity(m2.body.getLinearVelocity());
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
 		if (isEnable && !isDrag){
			float timeStep = 1 / 60.0f;
			if (speedRate<1){
				timeStep *= speedRate;
			}
 			box2d.step(timeStep,5,5);
 			count++;
 			
 			long sec = count/24;
 			if (sec>second){
 				second =sec;
 				Main.canvas.repaint();
 			}
 			
			computeEnergy();
 			
 			
			for (int i = 0; i < molecules.size(); i++) {
				Molecule m = molecules.get(i);
				m.waterPartner =-1;
				m.NaClPartner =-1;
				m.ionDis =0;
			}
		
			
 			/*for (int i = 0; i < molecules.size(); i++) {
				Molecule m = molecules.get(i);
				computeWaterPartner(i,m);
			}
 			
 			
			for (int i = 0; i < molecules.size(); i++) {
				Molecule m = molecules.get(i);
				//Unit 2, Set 1
				if (m.getName().equals("Sodium-Ion"))
					computeNaClPartner(i,m);
				//Unit 2, Set 4
				else if (m.getName().equals("Calcium-Ion"))
					computeCaClPartner(i,m);
			}
			*/
			
			
			for (int i = 0; i < molecules.size(); i++) {
				Molecule m = molecules.get(i);
				if (m.getName().equals("Water"))
					setForceWater(i,m);
				else if (Main.selectedUnit==1)
					setForce(i,m);
				else{
					if(Main.selectedSet==1)
						computeForceNaCl(i,m);
					else if(Main.selectedSet==2)
						computeForceSiliconDioxide(i,m);
					else if(Main.selectedSet==3)
						computeForceGlycerol(i,m);
					else if(Main.selectedSet==4){
						computeForceCaCl(i,m);	
						computeForceFromWater(i,m);	
					}
					else if(Main.selectedSet==5)
						computeForceAceticAcid(i,m);
					else if(Main.selectedSet==7){
						computeForceNaHCO3(i,m);
						computeForceFromWater(i,m);	
					}
				}
				if (Main.selectedUnit!=1 && !m.getName().equals("Water")){
					//checkSpeed(i,m);
				}
					
			}
			
			for (int i = 0; i < molecules.size(); i++) {
				Molecule m = molecules.get(i);
				if (m!=null && !isDrag){
					if (!m.getName().equals("Water") && Main.selectedUnit==2){
						applyForceUnit2(i,m);
					}
				}	
			}
		}	
		if (isEnableBrushing && isBrushing){
			this.stroke(Color.WHITE.getRGB());
			this.noFill();
			this.rect(xStart/scale,yStart/scale, (mouseX/scale-xStart/scale), (mouseY/scale-yStart/scale));	
		}
		
		
		
		for (int i = 0; i < 4; i++) {
			boundaries[i].display();
		}
		
		// Display all molecules
		for (int i = 0; i < molecules.size(); i++) {
			Molecule m = molecules.get(i);
			if (isEnableBrushing && isBrushing){
				Vec2 p = box2d.coordWorldToPixels(m.getPosition());
				if (xStart/scale <p.x && p.x< mouseX/scale &&
						yStart/scale <p.y && p.y< mouseY/scale )
					m.isBrushed =true;
				else
					m.isBrushed =false;
			}
			m.display();
		}
	}
	
	public static void computeEnergy(){
		/*float sum = 0f;
		float[] mAverage =  new float[numCompounds];
		for (int i = 0; i < molecules.size(); i++) {
			Molecule m = molecules.get(i);
			Vec2 vec = m.getLinearVelocity();
			if (vec!=null){
				float energy = (vec.x*vec.x + vec.y*vec.y)*m.getMass();
				sum += energy;
				for (int j=0; j<numCompounds;j++){
					if (Canvas.mNames.get(j).equals(m.getName()))
						mAverage[j] += energy;
				}
			}
		}*/
		for (int i=0; i<names.size();i++){
			//mAverage[i] = mAverage[i]/Canvas.mCounts.get(i); 
			float freezingTem = fTemp.get(i);
			float boilingTem = bTemp.get(i);
			String mName = names.get(i);
			float expectedAverage =0f;
			if (freezingTem<temp && temp<boilingTem){
				expectedAverage = minLiquidEnergy.get(i)+(temp-freezingTem)*
						rangeLiquidEnergy.get(i)/(boilingTem-freezingTem);
			}	
			else if (temp >=boilingTem){
				expectedAverage = minGasEnergy.get(i) +(temp-freezingTem)/(boilingTem-freezingTem);
			}	
		 	
			if (temp>freezingTem){
				for (int j = 0; j < molecules.size(); j++) {
					Molecule m = molecules.get(j);
					if (!m.getName().equals(mName))
						continue;
					Vec2 vec = m.body.getLinearVelocity();
					float energy = 0f;
					if (vec!=null){
						float v = vec.x*vec.x + vec.y*vec.y;
						energy = v*m.getMass();
					}
					//System.out.println("mName:"+mName+"  expectedAverage:"+expectedAverage+" "+energy);
					if (energy>expectedAverage*2)
						m.body.setLinearVelocity(vec.mul(0.9f) );
					else if (energy<expectedAverage/2)
						m.body.setLinearVelocity(vec.mul(1.2f) );
				}
			}
		}
		/*if (Canvas.count100Seconds>=10){
			float average =0f;
			if (molecules.size()>0)
				average = sum/molecules.size();
			if (Main.totalSystemEnergy!=null){
				DecimalFormat df = new DecimalFormat("#.##");
				Main.totalSystemEnergy.setText(df.format(sum)+" kJ");
				Main.averageSystemEnergy.setText(df.format(average )+" kJ");
			}	
		}*/
	}
	
	public static void checkSpeed(int index, Molecule m){
		float expectedAverage = 100f;
		Vec2 vec = m.body.getLinearVelocity();
		float v = 0f;
		if (vec!=null){
			v = vec.x*vec.x + vec.y*vec.y;
		}
		if (v>expectedAverage*2)
			m.body.setLinearVelocity(vec.mul(0.5f) );
		else if (v>expectedAverage)
			m.body.setLinearVelocity(vec.mul(0.9f) );
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
					gravityX = gravityY*2f;
				}	
				else{
					gravityY = (bTemp-temp)/(bTemp-fTemp);
					gravityX = gravityY*0.6f;
				}	
				forceX =  (-normV.x/dis)*m.getMass()*mIndex.getMass()*gravityX*3000;
				forceY =  (-normV.y/dis)*m.getMass()*mIndex.getMass()*gravityY*3000;
			}	
			else{
				float num = m.getNumElement();
				forceX =  (normV.x/dis)*m.getMass()*mIndex.getMass()*300*num;
				forceY =  (normV.y/dis)*m.getMass()*mIndex.getMass()*300*num;
			}
			mIndex.addForce(new Vec2(forceX,forceY));
		}
	}
		
	public static Vec2 normalizeForce(Vec2 v){
		float dis = (float) Math.sqrt(v.x*v.x + v.y*v.y);
		return new Vec2(v.x/dis,v.y/dis);
		
	}
	
	/*
	 * Background methods
	 */
	private void drawBackground() { // draw background
		

		pushStyle();
		fill(127, 127, 127);
		rect(0, 0, width, height);
		popStyle();
	}
	
	public void computeOutput(String compoundName, int count) {
		if (compoundName.equals("Water")){
			Unit2.numWater += count;
			DecimalFormat df = new DecimalFormat("###.#");
			Main.waterVolume.setText(df.format(Unit2.numWater/(Unit2.water100mL/100.))+" mL");
			computeSaturation();
		}
		if (Main.selectedUnit==2 && !compoundName.equals("Water") && count>0){
			num_total+=count;
			num_remain+=count;
			DecimalFormat df = new DecimalFormat("###.#");
			Main.m1Label.setText(compoundName+":");
			float total = Unit2.num_total*Unit2.mToMass;
			Main.m1Mass.setText(df.format(total)+" g");
			Main.dashboard.updateUI();
		}
		Canvas.satCount=0;
	}
	public void computeSaturation() {
		float sat = Unit2.computeSat();
		if (Main.satMass!=null){
			DecimalFormat df = new DecimalFormat("###.#");
			Main.satMass.setText(df.format(sat)+" g");
			if (Main.selectedSet==3 || Main.selectedSet==5)
				Main.satMass.setText("\u221e");
			Main.dashboard.updateUI();
		}

	}
	
	public static int numGone_atSaturation() { 
		int num = Math.round(computeSat()/ mToMass); 
		return num;
	}
	
	public static void computeDisolved() {
		if (Main.m1Disolved==null) return;
		DecimalFormat df = new DecimalFormat("###.#");
		if (Unit2.num_gone<numGone_atSaturation() || numGone_atSaturation()==0){
			float dis = Unit2.num_gone*Unit2.mToMass;
			Main.m1Disolved.setText(df.format(dis)+" g");
		}
		else{
			float sat = Unit2.computeSat();
			float dis = computeIonSeperation()/((Unit2.num_gone+1)*(1+Canvas.satCount));
			Main.m1Disolved.setText(df.format(sat-dis)+" g");
			if (Main.selectedSet==3 || Main.selectedSet==5)
				Main.m1Disolved.setText("\u221e");
		}
		Main.dashboard.updateUI();
		
	}
	public static float computeIonSeperation() {
		float dis = 0;
		for (int i = 0; i < molecules.size(); i++) {
			Molecule m = molecules.get(i);
			if (m.ionDis>0){
				dis += (2*Molecule.clRadius)/m.ionDis;
			}		
		}
		return dis*dis;
	}
		
	

	public void addMoleculeRandomly(String compoundName, int count) {
		boolean tmp = isEnable;
		isEnable = false;
		
		computeOutput(compoundName,count);
		
		float PAD =60;
		float freezingTem = P5Canvas.db.getCompoundFreezingPointCelsius(compoundName);
		if (temp<=freezingTem){
			if (compoundName.equals("Sodium-Chloride"))
				Unit2.add2Ions("Sodium-Ion","Chlorine-Ion",count, box2d, this);
			else if (compoundName.equals("Silicon-Dioxide"))
				Unit2.addSiliconDioxide(compoundName,count, box2d, this); 
			else if (compoundName.equals("Calcium-Chloride"))
				Unit2.addCalciumChloride(compoundName,count, box2d, this); 
			else if (compoundName.equals("Sodium-Bicarbonate"))
				Unit2.addSodiumBicarbonate(compoundName,count, box2d, this); 
			else	
				addSolid(compoundName,count);
		
		}
		else{
			for (int i=0;i<count;i++){
				float x_ = x+ this.random(PAD, w-2*PAD);
				float y_ = y+ this.random(PAD, h-2*PAD);
				molecules.add(new Molecule(x_, y_,compoundName, box2d, this,0));
			}
		}
		isEnable = tmp;
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
		
		computeOutput(compoundName,count);
		int index = Compound.names.indexOf(compoundName);
		int addCount = Compound.counts.get(index)+count;
		Compound.counts.set(index, addCount);

		
		float freezingTem = P5Canvas.db.getCompoundFreezingPointCelsius(compoundName);
		if (temp<=freezingTem){
			if (compoundName.equals("Sodium-Chloride"))
				Unit2.add2Ions("Sodium-Ion","Chlorine-Ion",count, box2d, this);
			else if (compoundName.equals("Silicon-Dioxide"))
				Unit2.addSiliconDioxide(compoundName,count, box2d, this); 
			else if (compoundName.equals("Calcium-Chloride"))
				Unit2.addCalciumChloride(compoundName,count, box2d, this); 
			else if (compoundName.equals("Sodium-Bicarbonate"))
				Unit2.addSodiumBicarbonate(compoundName,count, box2d, this); 
			else	
				addSolid(compoundName,count);
		}
		else{
			if (tmp) //if Applet is enable
				creationCount =0;
			else
				creationCount++;
										// variables are used to distribute molecules
			int mod = creationCount%4;  // When the system is paused; Otherwise, molecules are create at the same position
			for (int i=0;i<count;i++){
				float x_ =x + w/2 +40+ (i-count/2.f)*(w/11) + creationCount;
				float y_ =y + 80-Boundary.difVolume +(mod-1.5f)*20;
				molecules.add(new Molecule(x_, y_,compoundName, box2d, this,0));
			}
		}
		isEnable = tmp;
	}
	
	public void addSolid(String compoundName, int count) {
		int numRow = (int) (Math.ceil(count/6.)+1);
		for (int i=0;i<count;i++){
			float x_,y_,angle;
			Vec2 size = Molecule.getShapeSize(compoundName, this);
			x_ =x + 200+ (i/numRow)*2*size.x;
			y_ =y + 80-Boundary.difVolume+(numRow-1-i%numRow)*2*size.y;
			if ((i%numRow)%2==0){
				angle = 0;
			}
			else{
				angle = (float) Math.PI;
			}
			molecules.add(new Molecule(x_, y_,compoundName, 
					box2d, this,angle));
		}
	}
		
	
	
	public void addMolecule(float x_, float y_, String compoundName) {
		Molecule m = new Molecule(x_, y_,compoundName, box2d, this,0);
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
			m.setPropertyByHeat(false);
		}
		double v = (double) (value-Main.heatMin)/200;
		v=v+0.3;
		if (v>1) v=1;
		Color color = ColorScales.getColor(1-v, "redblue", 1f);
		heatRGB = color.getRGB();
		
		computeSaturation();
		Canvas.satCount=0;
	}
	
	//Set Scale of Molecules; values are from 0 to 100; 50 is default value 
	public void setScale(int value, int defaultScale) {
		boolean tmp = isEnable;
		isEnable = false;
		scale = (float) value*0.77f/defaultScale;
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
	public void keyPressed() {	
		
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
		isBrushing = true;
		xStart = mouseX;
		yStart = mouseY;
		draggingBoundary = boundaries[2].isIn(mouseX, mouseY);
	}
	
	public void mouseReleased() {
		isBrushing =false;
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
		if (isEnableBrushing){
			
		}
		else{	
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
	}
		
	public void mouseClicked() {
		addMolecule(mouseX/scale, mouseY/scale,"Water");
		//addMolecule(mouseX/scale, mouseY/scale,"Water");
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
	public void beginContact(Contact c) {
		// Get our objects that reference these bodies
		Object o1 = c.m_fixtureA.m_body.getUserData();
		Object o2 = c.m_fixtureB.m_body.getUserData();
		
			if (o1 ==null || o2==null)
			return;
		// What class are they?  Box or Particle?
		String c1 = o1.getClass().getName();
		String c2 = o2.getClass().getName();
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
	public void endContact(Contact c) {
	}
	public void postSolve(Contact c, ContactImpulse i) {
	}
	public void preSolve(Contact c, Manifold m) {
	}
}
