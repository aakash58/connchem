package simulations;

import simulations.models.*;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
//import java.util.Timer;

import processing.core.PApplet;
import simulations.models.Boundary;
import simulations.models.Compound;
import simulations.models.Molecule;
import simulations.models.Water;

import main.Canvas;
import main.Main;
import main.TableView;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.*;

import data.DBinterface;
import data.State;
import data.YAMLinterface;

import Util.ColorScales;
import static data.State.*;
import static simulations.models.Compound.*;

public class P5Canvas extends PApplet{
	/**
	 * 
	 */
	private Main main=null;
	private final long serialVersionUID = 1L;
	public float x=0;
	public float y=0;
	public float w;//width of the boundary
	public float h;//width of the boundary
	public float defaultW;
	public float defaultH;
	
	// A reference to our box2d world
	private PBox2D box2d;
	private Unit1 unit1;
	private Unit2 unit2;   //Unit2 object containing all the functions used in Unit2
	private Unit3 unit3;
	private Unit4 unit4;
	//public Water waterComputation;
	public boolean isEnable = false; 
	public boolean isHidingEnabled = false;
	public boolean isDisplayForces = false;
	public boolean isDisplayJoints = false;
	public boolean isConvertMol = false;
	
	public int creationCount = 0;
	//Properties of container
	public float temp =25.f;
	public float pressure = 0.0f;
	public float mol = 0.0f;
	public final float R = 8.314f;  //8.314 J*K-1*mol -1
	
	//Default value of speed
	public float speedRate = 1.0f;
	//Default value of heat
	public float heatRate = 1.f;
	//Default value of Pressure
	public float pressureRate = 1.f;
	//Default value of scale slider
	public float scale = 0.77f;
	//Default value of volume slider
	public int defaultVolume =50;
	public int currentVolume =defaultVolume;
	public int multiplierVolume =10; // Multiplier from pixels to ml
	public float maxH=1100;//minimum height of container
	
	public int heatRGB = 0;
	
	//public static long count = 0;
	public long curTime = 0;
	public long oldTime =0;
	public int xStart = 0;
	public int yStart = 0;
	public int xDrag = 0;   //x offset after dragging
	public int yDrag = 0;   //y offset after dragging
	public boolean isDrag = false;
	ArrayList<String> products = new ArrayList<String>();
	ArrayList<Molecule> killingList = new ArrayList<Molecule>();
	public int draggingBoundary =-1;
	private boolean isFirstTime =true;
	public boolean isHidden=false;
	
	//Time step property
	private float defaultTimeStep= 1.0f/60.0f;
	private float timeStep= 1.0f/60.0f;
	private int velocityIterations = 6;
	private int positionIterations =2;
	public static DBinterface db = new DBinterface();
	public  YAMLinterface yaml = new YAMLinterface();
	
	public float FRAME_RATE =30;
	public float totalKineticEnergy = 0;
	public ArrayList<UnitBase> unitList = new ArrayList<UnitBase>();
	

	public P5Canvas(Main parent) {

		setMain(parent);
		box2d = new PBox2D(this);
		setUnit1(new Unit1(this,box2d));
		setUnit2(new Unit2(this, box2d));
		setUnit3(new Unit3(this, box2d));
		setUnit4(new Unit4(this,box2d));
		//waterComputation = new Water(this);
		unitList.add(unit1);
		unitList.add(unit2);
		unitList.add(unit3);
		unitList.add(unit4);
	}
	
	
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
		box2d.createWorld();
		box2d.setGravity(0f,-10f);
		
		// Turn on collision detection
		box2d.listenForCollisions();
		defaultW = 560/0.77f;
		defaultH = 635/0.77f;
		setBoundary(0,0,defaultW,defaultH);			
		
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
		int sliderValue = 0;
		if(main.volumeSlider!= null)
			sliderValue = getMain().volumeSlider.getValue();
		else
			sliderValue = this.defaultVolume;
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
		
		
		updateMolecules();  //update molecules which are newly created
		updateProperties(); //Update temperature and pressure etc
		
		/*   Change Scale   */
		this.scale(scale);
		/*   Change Time Speed  */
 		if (isEnable && !isDrag){
			if (speedRate<=1){
				timeStep = speedRate* defaultTimeStep;
			}
 			box2d.step(timeStep,velocityIterations,positionIterations);
 			
 			/*    Compute energy   */
			//computeEnergy();
			
			/*   Compute Forces between different compounds  */
			computeForces();
		}	
 		
 		/*   Show selected contour while user create rectangle by dragging mouse  */
		if (isHidingEnabled && isHidden){
			this.stroke(Color.WHITE.getRGB());
			this.noFill();
			this.rect(xStart/scale,yStart/scale, (mouseX/scale-xStart/scale), (mouseY/scale-yStart/scale));	
		}
		
		//   Draw boundary   
		for (int i = 0; i < 4; i++) {
			boundaries[i].display();
		}
		
		//   Draw all molecules   
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
		
		//Update anchors position
		getUnit3().resetAnchors(xDrag,yDrag);
		
		computeDissolved();
		
		
	}
	/******************************************************************
	* FUNCTION :     updateProperties
	* DESCRIPTION :  update pressure volume mol and temperature
	*
	* INPUTS :       None
	* OUTPUTS:       None
	*******************************************************************/
	private void updateProperties() {
		
		//TODO: Calculate temp by checking kinematic energy of all the molecules
		//Equation: 1/2 mv2 = 3/2 KT , K= 1.38 * 10-23 JK-1;
		//Note: 1 mole = 6.022* 10 23;
		float K = 1.38f;
		float mole = 6.022f;
		totalKineticEnergy = 0;
		for( int i = 0;i<State.molecules.size();i++)
		{
			totalKineticEnergy += State.molecules.get(i).getKineticEnergy();
		}
		//temp = (float) ((totalKineticEnergy *2) /(3*K*mole) -273.15);
		//System.out.println("totalKineticEnergy is "+totalKineticEnergy);
		
		//Known: V-currentVolume n-mol T-temp R
		mol = State.molecules.size();
		
		//Unknown: Pressure
		pressure = (mol* R* temp)/currentVolume;
		
		//Update bars
		if(getMain().barPressure!=null)
		if(getMain().barPressure.isShowing())
		{
			getMain().barPressure.setValue(pressure);
			getMain().barPressure.updateUI();
			//System.out.println("pressure is "+pressure);
			getMain().barVolume.setValue(currentVolume);
			getMain().barVolume.updateUI();
			//System.out.println("currentVolume is "+currentVolume);
			getMain().barMol.setValue(mol);
			getMain().barMol.updateUI();
			//System.out.println("mol is "+mol);
			getMain().barTemp.setValue(temp);
			getMain().barTemp.updateUI();
			//System.out.println("temp is "+temp);
		
		}
	}


	/******************************************************************
	* FUNCTION :     computeForces
	* DESCRIPTION :  Compute forces at the beginning of every frame
	*
	* INPUTS :       None
	* OUTPUTS:       None
	*******************************************************************/
	public void computeForces()
	{
		unitList.get(main.selectedUnit-1).computeForce(main.selectedSim, main.selectedSet);
		//Apply forces after set forces
		applyForce();
		
	}
	//Apply force at the begginning of every frame
	public void applyForce()
	{
		unitList.get(main.selectedUnit-1).applyForce(main.selectedSim, main.selectedSet);
	}
	
	public void computeEnergy(){
		
		for (int i=0; i< Compound.names.size();i++){
			//mAverage[i] = mAverage[i]/Canvas.mCounts.get(i); 
			float freezingTem = fTemp.get(i);
			float boilingTem = bTemp.get(i);
			String mName = Compound.names.get(i);
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
						energy = v*m.getBodyMass();
					}
					//System.out.println("mName:"+mName+"  expectedAverage:"+expectedAverage+" "+energy);
					if (energy>expectedAverage*2)
						m.body.setLinearVelocity(vec.mul(0.9f) );
					else if (energy<expectedAverage/2)
						m.body.setLinearVelocity(vec.mul(1.2f) );
				}
			}
		}
	
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
	public void convertMassMol1() {
		double mass = getUnit2().getTotalNum()* getUnit2().getMolToMass();
		if (Compound.names.size()<=1) return;
		float mol = (float) (mass/getMolMass(Compound.names.get(1))); 
		DecimalFormat df = new DecimalFormat("###.##");
		main.m1Mass.setText(df.format(mol)+" mol");
	}
	//Change 'g' to 'mol' in "Dissolved" label when "ConvertMassToMol" checkbox is selected
	public void convertMassMol2() {
		double dis = getUnit2().getMassDissolved();
		if (Compound.names.size()<=1) return;
		float mol2 = (float) (dis/getMolMass(Compound.names.get(1))); 
		DecimalFormat df = new DecimalFormat("###.##");
		main.m1Disolved.setText(df.format(mol2)+" mol");
	}
	//Change 'mol' to 'g' in "Amount Added" label when "ConvertMassToMol" checkbox is deselected
	public void convertMolMass1() {
		double mass = getUnit2().getTotalNum()*getUnit2().getMolToMass();
		DecimalFormat df = new DecimalFormat("###.##");
		main.m1Mass.setText(df.format(mass)+" g");
	}
	//Change 'mol' to 'g' in "Dissolved" label when "ConvertMassToMol" checkbox is deselected
	public void convertMolMass2(){
		double mass = getUnit2().getMassDissolved();
		if(Compound.names.size()<=1) return;
		DecimalFormat df = new DecimalFormat("###.##");
		main.m1Disolved.setText(df.format(mass)+" g");
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
			getUnit2().addWaterMolecules(count);
			DecimalFormat df = new DecimalFormat("###.#");
			float waterNum = getUnit2().getWaterNum();
			float water100 = (float)getUnit2().getWater100Ml()/100;
			main.waterVolume.setText(df.format(waterNum/water100)+" mL");
			computeSaturation();
		}
		if (main.selectedUnit==2 && !compoundName.equals("Water") && count>0){
			getUnit2().addTotalMolecules(count);
			DecimalFormat df = new DecimalFormat("###.#");
			//In Unit 2, ALL SETS, the output monitor for the amount added should be "amount added". 
			if(main.selectedUnit==2 )
				main.m1Label.setText("Amount Added:");
			else
				main.m1Label.setText(compoundName+":");
			float total = getUnit2().getTotalNum()*getUnit2().getMolToMass();
			main.m1Mass.setText(df.format(total)+" g");
			if (isConvertMol){
				convertMassMol1();
			}
		}
		//Compute SoluteVolume
		float waterVolume = (float) (getUnit2().getWaterNum()/(getUnit2().getWater100Ml()/100.));
		float cVolume =0;
		if (Compound.names.size()>1){
			float dens = getDensity(Compound.names.get(1));
			float total = getUnit2().getTotalNum()*getUnit2().getMolToMass();
			cVolume = total/dens;
		}
		
		DecimalFormat df = new DecimalFormat("###.#");
		//If there is no water molecules added at the beginning in Unit 2, we want "Solution Volume" label show nothing
		if(main.selectedUnit==2 && waterVolume==0 )
		{
				main.soluteVolume.setText(" ");
		}
		else
			main.soluteVolume.setText(df.format(waterVolume + cVolume)+" mL");
		
		main.dashboard.updateUI();
		
		getMain().getCanvas().satCount=0;
	}
	public void computeSaturation() {
		float sat = getUnit2().computeSat();
		if (main.satMass!=null){
			DecimalFormat df = new DecimalFormat("###.#");
			main.satMass.setText(df.format(sat)+" g");
			if (main.selectedSet==3 || main.selectedSet==5)
				main.satMass.setText("\u221e"); //u221e is Unicode Character "infinite"
			//Main.dashboard.updateUI();
		}

	}
	

	/******************************************************************
	* FUNCTION :     computeDisolved
	* DESCRIPTION :  Function to compute mass of dissolved solute
	*
	* INPUTS :       None
	* OUTPUTS:       None
	*******************************************************************/
	public void computeDissolved() {
		
		//If there is no Dissolved label, we dont compute solution
		if (getMain().m1Disolved==null) 
			return;
		
		switch(getMain().selectedUnit)
		{
		case 2:
			getUnit2().computeDissolved();
			break;
		case 3: //unit3.computeDissolved();
		break;
		
		
		}
		
		
	}
	
		
	
	/******************************************************************
	* FUNCTION :     addMoleculeRandomly
	* DESCRIPTION :  Initially add molecule to applet when a new set gets selected. Called when reset.
	*
	* INPUTS :       compoundName(String), count(int)
	* OUTPUTS:       boolean
	*******************************************************************/
	public boolean addMoleculeRandomly(String compoundName, int count) {
		
		boolean res = false;
		boolean tmp = isEnable;
		isEnable = false;

		res= unitList.get(main.selectedUnit-1).addMolecules(tmp, compoundName, count);
		
		//If we successfully added molecules, update compound number
		if(res)
		{
			//Compound.counts.set(index, addCount);
			int index = Compound.names.indexOf(compoundName);
			int cap = Compound.caps.get(index);
			int countNum = Compound.counts.get(index);
			//System.out.println("count is "+countNum+", cap is "+ cap);
			if(countNum>=cap) //Grey out add button
			{
				if(!getMain().addBtns.isEmpty())
				getMain().addBtns.get(compoundName).setEnabled(false);
			}
			computeOutput(compoundName,count);
				
		}
		
		isEnable = tmp;
		return res;
	}
	

	/******************************************************************
	* FUNCTION :     addMolecule
	* DESCRIPTION :  Function to create compounds from outside the PApplet
	*
	* INPUTS :       compoundName(String), count(int)
	* OUTPUTS:       None
	*******************************************************************/
	public boolean addMolecule(String compoundName, int count) {
		// The tmp variable helps to fix a Box2D Bug: 2147483647  because of Multithreading
		boolean tmp = isEnable;
		isEnable = false;
		boolean res = false;
		
		int index = Compound.names.indexOf(compoundName);
		int addCount = Compound.counts.get(index)+count;

		res= unitList.get(main.selectedUnit-1).addMolecules(tmp, compoundName, count);
				
		//If we successfully added molecules, update compound number
		if(res)
		{
			Compound.counts.set(index, addCount);
			computeOutput(compoundName,count);
		}
		
		isEnable = tmp;
		return res;
	}
	
	/******************************************************************
	* FUNCTION :     reset
	* DESCRIPTION :  Reset function called by Main reset()
	*
	* INPUTS :       None
	* OUTPUTS:       None
	*******************************************************************/
	public void reset()
	{
		isEnable =false;
		//Reset boundaries
		setBoundary(0,0,defaultW,defaultH);
		
		removeAllMolecules();
		removeAllAnchors();
		
		curTime=0;
		oldTime=0;
		//Reset Gravity
		box2d.setGravity(0f,-10f);

		unitList.get(main.selectedUnit-1).reset();
	}
	
	//Get current number of a certain molecule
	public int getMoleculesNum(String compoundName)
	{
		int index = Compound.names.indexOf(compoundName);
		int num= Compound.getMoleculeNum(index);	
		return num;
	}
	//Get max allowed number of molecules
	public int getMoleculesCap(String compoundName)
	{
		int index = Compound.names.indexOf(compoundName);
		int cap= Compound.getMoleculeCap(index);	
		return cap;
	}

		
	
	

	//Remove all existing molecules, called by reset()
	public void removeAllMolecules() {
		boolean tmp = isEnable;
		isEnable = false;
		
		for (int i =0; i< molecules.size(); i++){
			Molecule m = (Molecule) molecules.get(i);
			m.killBody();
		}
		molecules.clear();
		
		isEnable = tmp;
	}
	
	//Remove existing anchors, called by reset()
	public void removeAllAnchors(){
		boolean tmp = isEnable;
		isEnable = false;
		
		for (int i =0; i< anchors.size(); i++){
			Anchor anchor = (Anchor) anchors.get(i);
			anchor.destroy();
		}
		anchors.clear();
		
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
		double v = (double) (value-main.heatMin)/200;
		v=v+0.3;
		if (v>1) v=1;
		Color color = ColorScales.getColor(1-v, "redblue", 1f);
		heatRGB = color.getRGB();
		
		computeSaturation();
		getMain().getCanvas().satCount=0;
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
		
		currentVolume = value;
		isEnable = tmp;
	}
	
	/******************************************************************
	* FUNCTION :     updateMolecules
	* DESCRIPTION :  Kill molecules which have gone after reaction, and add new created molecules
	*
	* INPUTS :       None
	* OUTPUTS:       None
	*******************************************************************/
	private void updateMolecules()
	{
		unitList.get(main.selectedUnit-1).updateMolecules(main.selectedSim, main.selectedSet);
		
	}
	

	
	
	/******************************************************************
	* FUNCTION :     beginContact
	* DESCRIPTION :  Molecule collision detect function
	*                Called when contact happens
	*
	* INPUTS :       c(Contact)
	* OUTPUTS:       None
	*******************************************************************/
	public void beginContact(Contact c) {		
		unitList.get(main.selectedUnit-1).beginReaction(c);

	}
	
	//Set up reaction products while initializing for graph rendering 
	public void setupReactionProducts() {
		unitList.get(main.selectedUnit-1).setupReactionProducts(main.selectedSim, main.selectedSet);
	}
	
	/******************************** MOUSE EVENT ******************************/
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
				//TODO: reset anchors
				
			}
		}
	}


	


	public void endContact(Contact c) {
	}
	public void postSolve(Contact c, ContactImpulse i) {
	}
	public void preSolve(Contact c, Manifold m) {
	}

	/**
	 * @return the unit2
	 */
	public Unit2 getUnit2() {
		return unit2;
	}

	/**
	 * @param unit2 the unit2 to set
	 */
	public void setUnit2(Unit2 unit2) {
		this.unit2 = unit2;
	}

	/**
	 * @return the main
	 */
	public Main getMain() {
		return main;
	}

	/**
	 * @param main the main to set
	 */
	public void setMain(Main main) {
		this.main = main;
	}

	/**
	 * @return the unit3
	 */
	public Unit3 getUnit3() {
		return unit3;
	}
	/**
	 * @param unit3 the unit3 to set
	 */
	public void setUnit3(Unit3 unit3) {
		this.unit3 = unit3;
	}
	public Unit4 getUnit4() 
	{
		return unit4;
	}
	public void setUnit4(Unit4 unit4) {
		this.unit4 = unit4;
	}
	public Unit1 getUnit1() 
	{
		return unit1;
	}
	public void setUnit1(Unit1 unit1) {
		this.unit1 = unit1;
	}
	public TableView getTableView()
	{
		return main.getTableView();
	}
}
