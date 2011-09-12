package view;

import java.awt.Color;
import java.util.ArrayList;

import processing.core.*;

import main.Canvas;
import main.Main;
import main.TableView;
import model.DBinterface;

import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.PrismaticJoint;

import Util.SVGReader;
import static model.State.*;


public class Molecule {
	// We need to keep track of a Body and a width and height
	public Body body;
	private PBox2D box2d;
	private P5Canvas p5Canvas;
	private PShape pShape = new PShape();
	private float pShapeW = 0f;
	private float pShapeH = 0f;
	public float[][] circles;
	public ArrayList<String> elementNames;
	public ArrayList<Integer> elementCharges;
	private String name;
	public float fric;
	public float res;
	private float scale = 1;

	private float xTmp;
	private float yTmp;
	private float minSize;
	private float maxSize;
	public boolean polarity;
	public boolean isHidden =false;
	public float freezingTem;
	public float boilingTem;
	
	public Vec2 force = new Vec2(0,0);
	public Vec2[] loc = new Vec2[20];
	public Vec2[] locWorld = new Vec2[20];
	public float[] gap = new float[20];     //Distance from a molecule`s top left corner to its center
	public float[] a1 = new float[20];
	
	public float[] sumForceX;
	public float[] sumForceY;
	public float[] sumForceWaterX;
	public float[] sumForceWaterY;
	
	public float chargeRate = 1;
	public static float clRadius = 28f;
	public static float oRadius = 18.495f; // Oxygen Radius. This depends on SVG file
	
	public int compoundJ = -1;    //Index of molecule to which this molecule is connecting
	                              //Only be used in Unit2
	public int otherJ = -1;
	public int CaOtherJ = -1;
	
	public ArrayList<DistanceJoint> compoundJoint = null; //Reference of joints of this molecule
	public ArrayList<Integer> compoundJointPair = null;   //Reference of molecules to which this molecule is connecting
	public PrismaticJoint compoundJoints2 = null; //is Used for Unit 2 set 7
	public DistanceJoint otherJoints = null;
	
	public float ionDis =0;  // Use to compute dissolve
	
	/******************************************************************
	* FUNCTION :     Molecule()
	* DESCRIPTION :  Molecule Constructor
	*
	* INPUTS :       x (float), y (float), compoundName_ (String), box2d_ (PBox2D), parent_ (P5Canvas), angle (float)
	* OUTPUTS:       None
	*******************************************************************/
	Molecule(float x, float y, String compoundName_, PBox2D box2d_,
			P5Canvas parent_, float angle) {
		p5Canvas = parent_;
		box2d = box2d_;
		name = compoundName_;
		compoundJoint = new ArrayList<DistanceJoint>();
		compoundJointPair = new ArrayList<Integer>();

		String path = "resources/compoundsSvg/" + compoundName_ + ".svg";
		pShape = p5Canvas.loadShape(path);
		pShapeW = pShape.width;
		pShapeH = pShape.height;
		minSize = Math.min(pShapeW, pShapeH);
		maxSize = Math.max(pShapeW, pShapeH);
		polarity = p5Canvas.db.getCompoundPolarity(compoundName_);

		circles = SVGReader.getSVG(path);
		elementNames = SVGReader.getNames();
		elementCharges = new ArrayList<Integer>();
		
		int numElement = elementNames.size();
		for (int i=0; i<numElement;i++){
			int charge = DBinterface.getElementCharge(elementNames.get(i));
			elementCharges.add(charge);
		}
		sumForceX = new float[numElement];
		sumForceY = new float[numElement];
		sumForceWaterX = new float[numElement];
		sumForceWaterY = new float[numElement];
		freezingTem = DBinterface.getCompoundFreezingPointCelsius(name);
		boilingTem = DBinterface.getCompoundBoilingPointCelsius(name);
		
		//Identify specific situation
		if ((name.equals("Sodium-Ion") || name.equals("Potassium-Ion"))
				&& (p5Canvas.getMain().selectedUnit==2 && p5Canvas.getMain().selectedSet!=7)){
			circles[0][0] = 28;
		}
		else if (name.equals("Calcium-Ion")){
			circles[0][0] = 28;
		}
		
		//Set up gap: distance from a molecule`s top left corner to its center
		for (int i=0; i<numElement;i++){
			float xx = circles[i][1]-pShapeW/2;
			float yy = -(circles[i][2]-pShapeH/2);
			gap[i] = (float) Math.sqrt(xx*xx+yy*yy);
			gap[i] = PBox2D.scalarPixelsToWorld(gap[i]);
			if (xx!=0)
				a1[i] = (float) (Math.atan(yy/xx));
			if (xx<0) a1[i]+= Math.PI;
		}
		
		
		setPropertyByHeat(true);
		createBody(x, y,angle);
	}
	
	
	/******************************************************************
	* FUNCTION :     switchTo()
	* DESCRIPTION :  Switch current molecule to another one, reset property and body
	*
	* INPUTS :       compoundName_ (String)
	* OUTPUTS:       None
	*******************************************************************/
	public void switchTo(String compoundName_)
	{
	
	}
	
	
	/******************************************************************
	* FUNCTION :     SetPropertyByHeat()
	* DESCRIPTION :  Set restitution, friction and charge rate regarding to temperature
	*
	* INPUTS :       isIntial (boolean)
	* OUTPUTS:       None
	*******************************************************************/
	public void setPropertyByHeat(boolean isInitial) {
		float temp = p5Canvas.temp;
		res = (temp - freezingTem) / (boilingTem - freezingTem);
		if (res > 0)  	
			res =1f;
		else			
			res = 0.2f;
		
		//Set for solid case
		if( temp<this.freezingTem)
			res =0.0f;  //Restituion is bounciness
		
		if (temp <= freezingTem)	
			fric = 1;
		else						
			fric = 0;
		if (name.equals("Water") && temp < 40) 	
			scale = 1 + (40 - temp) / 200f;
		else									
			scale = 1f;
		
		if (name.equals("Water"))	
			chargeRate = 0.95f;
		else if (name.equals("Sodium-Ion")){
			chargeRate = 0.93f;
			fric = 1;
			res=0.55f;
		}
		else if (name.equals("Chlorine-Ion")){
			chargeRate = 0.93f;
			fric = 1;
			res=0.f;
		}
		else if (name.equals("Calcium-Ion")){
			chargeRate = 0.93f;
			fric = 1;
			res=0.9f;
		}
		else if (name.equals("Silicon-Dioxide")){
			chargeRate = 0.98f;
			fric = 1;
			res=0.3f;
			scale = 1.2f;
		}
		else if (name.equals("Glycerol")){
			chargeRate = 0.9f;
			scale = 1.1f;
		}
		else if (name.equals("Acetic-Acid")){
			chargeRate = 0.85f;
			scale = 1.1f;
		}
		else if (name.equals("Bicarbonate")){
			chargeRate = 0.88f;
			fric = 1;
			res=0.0f;
			scale = 1.1f;
		}
		else if (name.equals("Potassium-Ion")){
			chargeRate = 0.93f;
			fric = 1;
			res=0.55f;
		}
		
		if (!isInitial){
			setRestitution(res);
			setFriction(fric);
			if (name.equals("Water"))
				setRadius(scale);
		}
	}
	
	/******************************************************************
	* FUNCTION :     createBody()
	* DESCRIPTION :  Create body and shape for molecules
	*
	* INPUTS :       x (float), y (float), angle (float)
	* OUTPUTS:       None
	*******************************************************************/
	public void createBody(float x, float y,float angle) {
		
		//Mannually set up density
		float mul = setMul();
		
		// Define the body and make it from the shape
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
        bd.position.set(box2d.coordPixelsToWorld(new Vec2(x, y)));
        bd.angle = angle;
		// This infinitive loop fix nullPointerException because
		// box2d.createBody(bd) may create a null body
		body = box2d.createBody(bd);
		while (body == null) {
			body = box2d.createBody(bd);
		}
		
		FixtureDef fd = new FixtureDef();
		for (int i = 0; i < circles.length; i++) {
			// Define a circle
			CircleShape circleShape = new CircleShape();
			
			// Offset its "local position" (relative to 0,0)
			Vec2 offset = new Vec2(circles[i][1] - pShapeW / 2, circles[i][2]
					- pShapeH / 2);
			circleShape.m_p.set(box2d.vectorPixelsToWorld(offset));
			circleShape.m_radius = PBox2D.scalarPixelsToWorld(circles[i][0]) * scale;
			
			float m = 1;
			if (elementNames != null && i < elementNames.size()){
				m = DBinterface.getElementMass(elementNames.get(i));
			}	
			float d = m / (circles[i][0] * circles[i][0] * circles[i][0]);
			fd.shape = circleShape;
	        fd.density = d * mul;
			fd.friction = fric;
			fd.restitution = res;   // Restitution is bounciness
			if( p5Canvas.temp < this.freezingTem)
				fd.restitution = 0.0f;
			// Attach shapes!
			body.createFixture(fd);
		}

		// Give it some initial random velocity
		body.setLinearVelocity(new Vec2(p5Canvas.random(-1, 1), p5Canvas.random(-1,
				1)));
		body.setAngularVelocity(0);
		body.setUserData(this);
	}

	
	public static Vec2 getShapeSize(String compoundName_, P5Canvas parent_) {
		String path = "resources/compoundsSvg/" + compoundName_ + ".svg";
		if ((compoundName_.equals("Sodium-Ion") || compoundName_.equals("Potassium-Ion")) && 
				(parent_.getMain().selectedUnit==2 && parent_.getMain().selectedSet!=7)){
			path = "resources/compoundsSvg/" + "Chlorine-Ion" + ".svg";
		}	
		
		PShape pShape = parent_.loadShape(path);
		float pShapeW = pShape.width;
		float pShapeH = pShape.height;
		float[][] circles = SVGReader.getSVG(path);
		if (compoundName_.equals("Sodium-Chloride")){
			pShapeW = circles[1][0]*4;
			pShapeH = circles[1][0]*2;
		}	
		return new Vec2(pShapeW ,pShapeH);
	}
	
	public int getNumElement() {
		return circles.length;
	}

	public String getName() {
		return name;
	}

	public float getMass() {
		return body.getMass();
	}

	public Vec2 getPosition() {
		return body.getPosition();
	}

	public void setRestitution(float r) {
		Fixture s = body.getFixtureList();
		for (int i = 0; i < circles.length; i++) {
			if (s==null) continue;
			s.setRestitution(r);
			s = s.getNext();
		}
	}

	public void setFriction(float r) {
		Fixture s = body.getFixtureList();
		for (int i = 0; i < circles.length; i++) {
			s.setFriction(r);
			s = s.getNext();
		
		}
	}

	public Vec2 getElementLocation(int e) {
		Vec2 pos = body.getPosition();
		float a2 = body.getAngle();
		Vec2 v = new Vec2((float) Math.cos(a1[e]+a2), (float)  Math.sin(a1[e]+a2));
		return pos.add(v.mul(gap[e]));
	} 
		
	
	public void addForce(Vec2 f) {
		Vec2 pos = body.getPosition();
		body.applyForce(f, pos);
	}

	
	public void addForce(Vec2 f, int e) {
		force =f;
		Vec2 l =  getElementLocation(e);
		loc[e] = l;
		body.applyForce(force, l);	
	}
	
	public void setRadius(float scale) {
		Fixture s = body.getFixtureList();
		for (int i = 0; i < circles.length; i++) {
			s.m_shape.m_radius = PBox2D.scalarPixelsToWorld(circles[i][0]) * scale;
			s = s.getNext();
		}
	}

	public void display() {
		//float yyy = (2+body.getPosition().y)/90;
		//if (yyy<0) yyy=0;
		//if (P5Canvas.temp<100)
		//	body.applyForce(new Vec2(0,-yyy), body.getPosition());
		
		if (p5Canvas.isDrag && p5Canvas.draggingBoundary < 0) {
			float xx = xTmp + PBox2D.scalarPixelsToWorld(p5Canvas.xDrag);
			float yy = yTmp - PBox2D.scalarPixelsToWorld(p5Canvas.yDrag);
			Vec2 v = new Vec2(xx, yy);
			body.setTransform(v, body.getAngle());
			body.setAngularVelocity(0);
		} else {
			xTmp = body.getPosition().x;
			yTmp = body.getPosition().y;
		}
		/**************************  Boundary Check **************************/
		/* If molecules go out of boundary, reset their position */
		/* Top boundary check, top boundary has max y value */
		if (body.getPosition().y+PBox2D.scalarPixelsToWorld(this.minSize/2) > boundaries[2].body.getPosition().y) {
			Vec2 v = new Vec2(body.getPosition().x, 
					boundaries[2].body.getPosition().y-PBox2D.scalarPixelsToWorld(maxSize/2));
			if (body != null && v != null)
				body.setTransform(v, body.getAngle());
		}
		/* Bottom boundary check, bot boundary has min y value */
		else if (body.getPosition().y-PBox2D.scalarPixelsToWorld(this.minSize/2) < boundaries[3].body.getPosition().y) {
			Vec2 v = new Vec2(body.getPosition().x, 
					boundaries[3].body.getPosition().y+PBox2D.scalarPixelsToWorld(maxSize/2));
			if (body != null && v != null)
				body.setTransform(v, body.getAngle());
		}
		/* Left boundary check, left boundary has min x value */
		if (body.getPosition().x-PBox2D.scalarPixelsToWorld(this.minSize/2) < boundaries[0].body.getPosition().x) {
			Vec2 v = new Vec2(boundaries[0].body.getPosition().x+PBox2D.scalarPixelsToWorld(maxSize/2), 
					body.getPosition().y);
			if (body != null && v != null)
				body.setTransform(v, body.getAngle());
		}
		/* Right boundary check, right boundary has max x value */
		else if (body.getPosition().x+PBox2D.scalarPixelsToWorld(this.minSize/2) > boundaries[1].body.getPosition().x) {
			Vec2 v = new Vec2(boundaries[1].body.getPosition().x - PBox2D.scalarPixelsToWorld(maxSize/2), 
					body.getPosition().y);
			if (body != null && v != null)
				body.setTransform(v, body.getAngle());
		}
		

		// We look at each body and get its screen position
		Vec2 pos = box2d.getBodyPixelCoord(body);
		// Get its angle of rotation
		
		float a = body.getAngle();
		

		/*********************  Draw Bodies *******************/
		p5Canvas.pushMatrix();
		p5Canvas.translate(pos.x, pos.y);
		float temp = p5Canvas.temp;
		p5Canvas.rotate(-a);
		p5Canvas.shape(pShape, pShapeW / -2, pShapeH / -2, pShapeW, pShapeH); 
		//parent.noFill();
		p5Canvas.fill(Color.GRAY.getRGB(),240);
		
		/* If molecules are selected or deselected in tableview, render or hide them */
		if (p5Canvas.getMain().getTableView().selectedRow>=0){
			if (!name.equals(p5Canvas.getMain().getCanvas().getSelectedMolecule())) {
				p5Canvas.noStroke();
				for (int i=0; i<circles.length;i++){
					p5Canvas.ellipse( circles[i][1]-pShapeW/2, circles[i][2]-pShapeH/2,circles[i][0]*2, circles[i][0]*2);
				}
			}
		}
		/* If hide checkbox is selected, hide them */
		else if (p5Canvas.isHidingEnabled && !isHidden){
			p5Canvas.noStroke();
			for (int i=0; i<circles.length;i++){
				p5Canvas.ellipse( circles[i][1]-pShapeW/2, circles[i][2]-pShapeH/2,circles[i][0]*2, circles[i][0]*2);
			}
		}
		
		if (name.equals("Calcium-Ion")){
			p5Canvas.stroke(Color.BLUE.getRGB());
		}
		p5Canvas.popMatrix();
		//End drawing
		
		
		/*  Check if it is displaying forces  */
		if (p5Canvas.isDisplayForces && !name.equals("Water")){
			int numElement = elementNames.size();
			for (int i=0; i<numElement;i++){
				if (loc[i]==null) continue;
				p5Canvas.stroke(Color.BLUE.getRGB());
				p5Canvas.line(PBox2D.scalarWorldToPixels(loc[i].x), p5Canvas.height-PBox2D.scalarWorldToPixels(loc[i].y),
					PBox2D.scalarWorldToPixels(loc[i].x)+PBox2D.scalarWorldToPixels(sumForceWaterX[i]+sumForceX[i]), 
					p5Canvas.height-PBox2D.scalarWorldToPixels(loc[i].y)-PBox2D.scalarWorldToPixels(sumForceWaterY[i]+sumForceY[i]));
			}
		}
		
		/*  Check if it is displaying joints  */
		if (p5Canvas.isDisplayJoints){
			// For Unit 1 and Unit 2
			if(p5Canvas.getMain().selectedUnit==1 || p5Canvas.getMain().selectedUnit==2 )
			{
				if (compoundJ>=0) {
					Vec2 pos2 = box2d.getBodyPixelCoord(molecules.get(compoundJ).body);
					p5Canvas.stroke(Color.BLACK.getRGB());
					p5Canvas.line(pos.x, pos.y,pos2.x,pos2.y);
				}
			
				if (otherJ>=0) {
					Vec2 pos2 = box2d.getBodyPixelCoord(molecules.get(otherJ).body);
					p5Canvas.stroke(Color.RED.getRGB());
					p5Canvas.line(pos.x, pos.y,pos2.x,pos2.y);
				}
			}
			else
			{
				if (compoundJointPair.size()>0) {
					Vec2 pos2 = new Vec2();
					for( int i = 0;i<compoundJointPair.size();i++)
					{
					pos2.set(box2d.getBodyPixelCoord(molecules.get(compoundJointPair.get(i)).body) );
					p5Canvas.stroke(Color.BLACK.getRGB());
					p5Canvas.line(pos.x, pos.y,pos2.x,pos2.y);
					}
				}

			}
		}
		
	}

	

	// This function removes the particle from the box2d world
	public void killBody() {
		box2d.destroyBody(body);
		body.m_world = null;
	}
	
	/******************************************************************
	* FUNCTION :     setMul()
	* DESCRIPTION :  Mannually set up density for different elements
	*
	* INPUTS :       None
	* OUTPUTS:       None
	*******************************************************************/
	
	private float setMul()
	{
		float mul =1.0f;
		if (name.equals("Pentane"))
			mul = 0.04f;
		else if (name.equals("Bromine"))
			mul = 0.45f;
		else if (name.equals("Mercury"))
			mul = 0.3f;
		else if (name.equals("Hydrogen-Peroxide"))
			mul = 0.8f;
		else if (name.equals("Sodium-Chloride"))
			mul = 1.0f;
		else if (name.equals("Sodium-Ion"))
			mul = 0.011f/0.006448616f;
		else if (name.equals("Chlorine-Ion"))
			mul = 0.015f/0.009944542f;
		else if (name.equals("Glycerol"))
			mul = 2.0f;
		else if (name.equals("Silicon-Dioxide"))
			mul = 1.f;
		else if (name.equals("Calcium-Ion"))
			mul = 1.6f;
		else if (name.equals("Bicarbonate"))
			mul = 1.50f;
		else if (name.equals("Potassium-Ion"))
			mul = 1.1f;
		else if (name.equals("Chlorine"))
			mul =0.04f;
		else if (name.equals("Sodium"))
			mul =1.0f;
		return mul;
	}
}
