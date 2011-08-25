package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
//import java.util.Timer;

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
	private PBox2D box2d;
	public static boolean isEnable = false; 
	public static boolean isHidingEnabled = false;
	public static boolean isDisplayForces = false;
	public static boolean isDisplayJoints = false;
	public static boolean isConvertMol = false;
	
	public static int creationCount = 0;
	public static float temp =25.f;
	
	//Default value of speed
	public static float speedRate = 1.0f;
	//Default value of heat
	public static float heatRate = 1.f;
	//Default value of Pressure
	public static float pressureRate = 1.f;
	//Default value of scale slider
	public static float scale = 0.77f;
	//Default value of volume slider
	public static int defaultVolume =50;
	public static int currenttVolume =defaultVolume;
	public static int multiplierVolume =10; // Multiplier from pixels to ml
	public static float maxH=1100;//minimum height of container
	
	public static int heatRGB = 0;
	
	//public static long count = 0;
	public static long curTime = 0;
	public static long oldTime =0;
	public static int xStart = 0;
	public static int yStart = 0;
	public static int xDrag = 0;
	public static int yDrag = 0;
	public static boolean isDrag = false;
	ArrayList<String> products = new ArrayList<String>();
	ArrayList<Molecule> killingList = new ArrayList<Molecule>();
	public static int draggingBoundary =-1;
	private boolean isFirstTime =true;
	public boolean isHidden=false;
	
	//Time step property
	private float defaultTimeStep= 1.0f/60.0f;
	private float timeStep= 1.0f/60.0f;
	private int velocityIterations = 6;
	private int positionIterations =2;
	
	private float FRAME_RATE =30;
	
	
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
		frameRate(FRAME_RATE);
		
		// Initialize box2d physics and create the world
		box2d = new PBox2D(this);
		box2d.createWorld();
		box2d.setGravity(0f,-10f);
		
		// Turn on collision listening!
		// TODO turn on collisions by un-commenting below
		box2d.listenForCollisions();
		setBoundary(0,0,560/0.77f,635/0.77f);	
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
			killingList = new ArrayList<Molecule>();
		}
		
		this.scale(scale);
 		if (isEnable && !isDrag){
			if (speedRate<1){
				timeStep = speedRate* defaultTimeStep;
			}
 			box2d.step(timeStep,velocityIterations,positionIterations);
			//box2d.step();
 			//count++;
 			
 			
			computeEnergy();
 			
			if (Main.selectedUnit==2){
				if (Main.selectedSet==1 || Main.selectedSet==4 || Main.selectedSet==7){
					for (int i = 0; i < molecules.size(); i++) {
						Molecule m = molecules.get(i);
						m.ionDis =0;
						if (Main.selectedSet==4 && m.getName().equals("Calcium-Ion"))
								computeCaClPartner(i,m);
					}
					
				}
				
			}
			for (int i = 0; i < molecules.size(); i++) {
				Molecule m = molecules.get(i);
				if (m.getName().equals("Water"))
					setForceWater(i,m);
				else if (Main.selectedUnit==1)
					setForce(i,m);
				else {
					if(Main.selectedSet==1 && Main.selectedSim<4)
						computeForceNaCl(i,m);
					else if(Main.selectedSet==2)
						computeForceSiO2(i,m);
					else if(Main.selectedSet==3)
						computeForceGlycerol(i,m);
					else if(Main.selectedSet==4){
						computeForceCaCl(i,m);	
						computeForceFromWater(i,m);	
						checkSpeed(i,m);
					}
					else if(Main.selectedSet==5)
						computeForceAceticAcid(i,m);
					else if(Main.selectedSet==7){
						computeForceNaHCO3(i,m);
						computeForceFromWater(i,m);	
					}
					else if(Main.selectedSet==1 && Main.selectedSim==4){
						computeForceKCl(i,m);
					}
						
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
		if (isHidingEnabled && isHidden){
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
			if (isHidingEnabled && isHidden){
				Vec2 p = box2d.coordWorldToPixels(m.getPosition());
				if (xStart/scale <p.x && p.x< mouseX/scale &&
						yStart/scale <p.y && p.y< mouseY/scale )
					m.isHidden =true;
				else
					m.isHidden =false;
			}
			m.display();
		}
		
		//Print out framerate for testing
		/*
		fill(255, 255, 255);
		text("framerate: " + (int)frameRate,12,16);
		*/
		
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
	
	public float getDensity(String compoundName) {
		if (compoundName.equals("Sodium-Chloride"))
			return 2.165f;
		else if (compoundName.equals("Silicon-Dioxide"))
			return 1.52f;
		else if (compoundName.equals("Calcium-Chloride"))
			return 2.15f; 
		else if (compoundName.equals("Sodium-Bicarbonate"))
			return 2.20f; 
		else if (compoundName.equals("Potassium-Chloride"))
			return 1.984f; 
		else if (compoundName.equals("Glycerol"))
			return 1.261f; 	
		else if (compoundName.equals("Pentane"))
			return 0.63f; 	
		else if (compoundName.equals("Acetic-Acid"))
			return 1.049f;
		else 
			return 1;
	}
	public static float getMolMass(String compoundName) {
		if (compoundName.equals("Sodium-Chloride"))
			return 58f;
		else if (compoundName.equals("Silicon-Dioxide"))
			return 60f;
		else if (compoundName.equals("Calcium-Chloride"))
			return 110f; 
		else if (compoundName.equals("Sodium-Bicarbonate"))
			return 84f; 
		else if (compoundName.equals("Potassium-Chloride"))
			return 74.5f; 
		else if (compoundName.equals("Glycerol"))
			return 92f; 	
		else if (compoundName.equals("Pentane"))
			return 72; 	
		else if (compoundName.equals("Acetic-Acid"))
			return 60f;
		else 
			return 1;
	}
	
	//Change 'g' to 'mol' in "Amount Added" label when "ConvertMassToMol" checkbox is selected
	public static void convertMassMol1() {
		double mass = Unit2.num_total*Unit2.mToMass;
		if (Compound.names.size()<=1) return;
		float mol = (float) (mass/getMolMass(Compound.names.get(1))); 
		DecimalFormat df = new DecimalFormat("###.##");
		Main.m1Mass.setText(df.format(mol)+" mol");
	}
	//Change 'g' to 'mol' in "Dissolved" label when "ConvertMassToMol" checkbox is selected
	public static void convertMassMol2() {
		double dis = Unit2.massDissolved;
		if (Compound.names.size()<=1) return;
		float mol2 = (float) (dis/getMolMass(Compound.names.get(1))); 
		DecimalFormat df = new DecimalFormat("###.##");
		Main.m1Disolved.setText(df.format(mol2)+" mol");
	}
	//Change 'mol' to 'g' in "Amount Added" label when "ConvertMassToMol" checkbox is deselected
	public static void convertMolMass1() {
		double mass = Unit2.num_total*Unit2.mToMass;
		DecimalFormat df = new DecimalFormat("###.##");
		Main.m1Mass.setText(df.format(mass)+" g");
	}
	//Change 'mol' to 'g' in "Dissolved" label when "ConvertMassToMol" checkbox is deselected
	public static void convertMolMass2(){
		double mass = Unit2.massDissolved;
		if(Compound.names.size()<=1) return;
		DecimalFormat df = new DecimalFormat("###.##");
		Main.m1Disolved.setText(df.format(mass)+" g");
	}
	
	/******************************************************************
	* FUNCTION :     computeOutput
	* DESCRIPTION :  Compute total amount of water and other molecules
	*
	* INPUTS :       compoundName(String), count(int)
	* OUTPUTS:       None
	*******************************************************************/
	private void computeOutput(String compoundName, int count) {
		if (compoundName.equals("Water")){
			Unit2.numWater += count;
			DecimalFormat df = new DecimalFormat("###.#");
			Main.waterVolume.setText(df.format(Unit2.numWater/(Unit2.water100mL/100.))+" mL");
			computeSaturation();
		}
		if (Main.selectedUnit==2 && !compoundName.equals("Water") && count>0){
			num_total+=count;
			DecimalFormat df = new DecimalFormat("###.#");
			//In Unit 2, ALL SETS, the output monitor for the amount added should be "amount added". 
			if(Main.selectedUnit==2 )
				Main.m1Label.setText("Amount Added:");
			else
				Main.m1Label.setText(compoundName+":");
			float total = Unit2.num_total*Unit2.mToMass;
			Main.m1Mass.setText(df.format(total)+" g");
			if (isConvertMol){
				convertMassMol1();
			}
		}
		//Compute SoluteVolume
		float waterVolume = (float) (Unit2.numWater/(Unit2.water100mL/100.));
		float cVolume =0;
		if (Compound.names.size()>1){
			float dens = getDensity(Compound.names.get(1));
			float total = Unit2.num_total*Unit2.mToMass;
			cVolume = total/dens;
		}
		
		DecimalFormat df = new DecimalFormat("###.#");
		//If there is no water molecules added at the beginning in Unit 2, we want "Solution Volume" label show nothing
		if(Main.selectedUnit==2 && waterVolume==0 )
		{
				Main.soluteVolume.setText(" ");
		}
		else
			Main.soluteVolume.setText(df.format(waterVolume + cVolume)+" mL");
		
		Main.dashboard.updateUI();
		
		Canvas.satCount=0;
	}
	public void computeSaturation() {
		float sat = Unit2.computeSat();
		if (Main.satMass!=null){
			DecimalFormat df = new DecimalFormat("###.#");
			Main.satMass.setText(df.format(sat)+" g");
			if (Main.selectedSet==3 || Main.selectedSet==5)
				Main.satMass.setText("\u221e"); //u221e is Unicode Character "infinite"
			//Main.dashboard.updateUI();
		}

	}
	
	public static int numGone_atSaturation() { 
		int num = Math.round(computeSat()/ mToMass); 
		return num;
	}
	
	public static void computeDisolved() {
		//System.out.println("num_gone is"+Unit2.num_gone+", mToMass is "+Unit2.mToMass);
		if (Main.m1Disolved==null) return;
		DecimalFormat df = new DecimalFormat("###.#");
		if (Unit2.num_gone<numGone_atSaturation() || numGone_atSaturation()==0){
			float dis = Unit2.num_gone*Unit2.mToMass;
			Unit2.massDissolved = dis;
			Main.m1Disolved.setText(df.format(Unit2.massDissolved)+" g");
		}
		else if (Unit2.num_gone==numGone_atSaturation()) {
			float sat = Unit2.computeSat();
			float dis = computeIonSeperation()/(1+Canvas.satCount);
			Unit2.massDissolved = sat - dis;
			Main.m1Disolved.setText(df.format(Unit2.massDissolved)+" g");
		}
		else if (Unit2.num_gone>numGone_atSaturation()) {
			float sat = Unit2.computeSat();
			float gone = Unit2.num_gone*Unit2.mToMass;
			float average = (sat+gone)/2;
			float dis = computeIonSeperation()/(1+Canvas.satCount);
			Unit2.massDissolved = average+dis;
			Main.m1Disolved.setText(df.format(Unit2.massDissolved)+" g");
			if (Canvas.satCount>10){
				Unit2.massDissolved = sat+dis;
				Main.m1Disolved.setText(df.format(Unit2.massDissolved)+" g");
			}
		}
		
		double dis = Unit2.massDissolved;
		double total =  Unit2.num_total*Unit2.mToMass;
		if (dis>total){
			Unit2.massDissolved = (float) total;
			Main.m1Disolved.setText(df.format(Unit2.massDissolved)+" g");
		}
		if (Main.selectedSet==3 || Main.selectedSet==5){
			Unit2.massDissolved = Unit2.num_total*Unit2.mToMass;
			Main.m1Disolved.setText(df.format(Unit2.massDissolved)+" g");
		}	
		if (temp<=0 || temp>=100){ 
			Unit2.massDissolved =0;
			Main.m1Disolved.setText("0 g");
		}
		
		//If ConvertToMol checkbox is selected, we need to change 'g' to 'mol'
	
		if (isConvertMol){
			convertMassMol2();
		}
		
		//Main.dashboard.updateUI();
		
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
		
	
	/******************************************************************
	* FUNCTION :     addMoleculeRandomly
	* DESCRIPTION :  Initially add molecule to applet when a new set gets selected. Called when reset.
	*
	* INPUTS :       compoundName(String), count(int)
	* OUTPUTS:       None
	*******************************************************************/
	public void addMoleculeRandomly(String compoundName, int count) {
		boolean tmp = isEnable;
		isEnable = false;
		
		computeOutput(compoundName,count);
		
		float PAD =60;
		float freezingTem = DBinterface.getCompoundFreezingPointCelsius(compoundName);
		if (temp<=freezingTem){
			if (compoundName.equals("Sodium-Chloride"))
				Unit2.add2Ions("Sodium-Ion","Chlorine-Ion",count, box2d, this);
			else if (compoundName.equals("Silicon-Dioxide"))
				Unit2.addSiO2(compoundName,count, box2d, this); 
			else if (compoundName.equals("Calcium-Chloride"))
				Unit2.addCalciumChloride(compoundName,count, box2d, this); 
			else if (compoundName.equals("Sodium-Bicarbonate"))
				Unit2.addNaHCO3(compoundName,count, box2d, this); 
			else if (compoundName.equals("Potassium-Chloride"))
				Unit2.add2Ions("Potassium-Ion","Chlorine-Ion",count, box2d, this); 
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
	

	/******************************************************************
	* FUNCTION :     addMolecule
	* DESCRIPTION :  Function to create compounds from outside the PApplet
	*
	* INPUTS :       compoundName(String), count(int)
	* OUTPUTS:       None
	*******************************************************************/
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

		
		float freezingTem = DBinterface.getCompoundFreezingPointCelsius(compoundName);
		if (temp<=freezingTem){
			if (compoundName.equals("Sodium-Chloride"))
				Unit2.add2Ions("Sodium-Ion","Chlorine-Ion",count, box2d, this);
			else if (compoundName.equals("Silicon-Dioxide"))
				Unit2.addSiO2(compoundName,count, box2d, this); 
			else if (compoundName.equals("Calcium-Chloride"))
				Unit2.addCalciumChloride(compoundName,count, box2d, this); 
			else if (compoundName.equals("Sodium-Bicarbonate"))
				Unit2.addNaHCO3(compoundName,count, box2d, this); 
			else if (compoundName.equals("Potassium-Chloride"))
				Unit2.add2Ions("Potassium-Ion","Chlorine-Ion",count, box2d, this); 
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
			
			float centerX = 0 ; // X Coordinate around which we are going to add molecules
			float centerY = 0 ; // Y Coordinate around which we are going to add molecules
			float x_ = 0;       // X Coordinate for a specific molecule
			float y_ = 0;       // Y Coordinate for a specific molecule
			int dimension =0;   // Decide molecule cluster is 2*2 or 3*3
			int leftBorder = 40;// Left padding
			int offsetX =0;     // X offset from left border to 3/4 width of canvas
			Random rand = new Random();
			offsetX = rand.nextInt((int)( (w/4)*3));
			centerX = x + leftBorder + offsetX;
			centerY = y + 80-Boundary.difVolume +(mod-1.5f)*20;
			if(count<= (2*2) )
				dimension =2;
			else
				dimension = 3;
			
			for (int i=0;i<count;i++){		

				//System.out.println("medium is "+medium);
				if(compoundName.equals("Water"))
				{
					x_ =centerX + i%dimension*(w/11) + creationCount;
					y_ =centerY + i/dimension*(h/20);
				}
				else
				{
					x_ = centerX + (i-count/2.f)*(w/11) + creationCount;
					y_ = centerY;
				}
				molecules.add(new Molecule(x_, y_,compoundName, box2d, this,0));
			}
		}
		isEnable = tmp;
	}
	public int getMoleculesNum(String compoundName)
	{
		int index = Compound.names.indexOf(compoundName);
		int num= Compound.getMoleculeNum(index);	
		return num;
	}
	public int getMoleculesCap(String compoundName)
	{
		int index = Compound.names.indexOf(compoundName);
		int cap= Compound.getMoleculeCap(index);	
		return cap;
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
	
	//Set Speed of Molecules; values are from 0 to 100; 100 is default value 
	public void setSpeed(float speed) {
		speedRate = speed;
	}
	
	//Set Pressure of Container. Value is from 0 to 10, 1 is default
	public void setPressure(float pressure)
	{
		pressureRate = pressure;
	}
	
	//Set Heat of Molecules; values are from 0 to 100; 50 is default value 
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
		isHidden = true;
		xStart = mouseX;
		yStart = mouseY;
		draggingBoundary = boundaries[2].isIn(mouseX, mouseY);
	}
	
	public void mouseReleased() {
		isHidden =false;
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
		if (isHidingEnabled){
			
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
