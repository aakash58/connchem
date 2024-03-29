package simulations.models;

import java.awt.Color;
import java.util.ArrayList;

import processing.core.*;
import simulations.P5Canvas;
import simulations.PBox2D;

import main.Canvas;
import main.Main;
import main.TableView;

import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.PrismaticJoint;

import data.DBinterface;
import data.State;

import Util.SVGReader;
import static data.State.*;

public class Molecule {
	// We need to keep track of a Body and a width and height
	public Body body;
	private ArrayList<Fixture> fixtures;
	private BodyDef bd;
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
	private int legendId=-1; //Legend Id used to connect molecule to graph legend

	private float xTmp;   //Temporary x to save x position while dragging, in world coordinates
	private float yTmp;   //Temporary x to save x position while dragging, in world coordinates
	private float minSize;
	private float maxSize;
	public boolean polarity;
	public boolean isHidden = false;
	public float freezingTem;
	public float boilingTem;
	public float mass = 0;

	public Vec2 force = new Vec2(0, 0);
	public Vec2[] loc = new Vec2[20];
	public Vec2[] locWorld = new Vec2[20];
	public float[] gap = new float[20]; // Distance from a molecule`s top left
										// corner to its center
	public float[] a1 = new float[20];

	public float[] sumForceX;
	public float[] sumForceY;
	public float[] sumForceWaterX;
	public float[] sumForceWaterY;

	public float chargeRate = 1;
	public static float clRadius = 28f;
	public static float oRadius = 18.495f; // Oxygen Radius. This depends on SVG
											// file

	public int compoundJ = -1; // Index of molecule to which this molecule is
								// connecting
								// Only be used in Unit2
	public int otherJ = -1;
	public int CaOtherJ = -1;

	public ArrayList<DistanceJointWrap> compoundJoint = null; // Reference of
																// joints of
																// this molecule
	// public ArrayList<Molecule> compoundJointPair = null; //Reference of
	// molecules to which this molecule is connecting
	public PrismaticJoint compoundJoints2 = null; // is Used for Unit 2 set 7
	public DistanceJoint otherJoints = null;
	
	//Neighbors information of this molecule, that is used to find react pairs
	public ArrayList<Molecule> neighbors = null;

	public float ionDis = 0; // Use to compute dissolve
	private boolean reactive = true; // Molecule can only react if this flag is
										// true
	private int tableIndex = -1;

	/******************************************************************
	 * FUNCTION : Molecule() DESCRIPTION : Molecule Constructor
	 * 
	 * INPUTS : x (float), y (float), compoundName_ (String), box2d_ (PBox2D),
	 * parent_ (P5Canvas), angle (float) OUTPUTS: None
	 *******************************************************************/
	public Molecule(float x, float y, String compoundName_, PBox2D box2d_,
			P5Canvas parent_, float angle) {
		p5Canvas = parent_;
		box2d = box2d_;
		name = compoundName_;
		compoundJoint = new ArrayList<DistanceJointWrap>();
		neighbors = new ArrayList<Molecule>();
		// compoundJointPair = new ArrayList<Molecule>();
		fixtures = new ArrayList<Fixture>();

		String path = "resources/compoundsSvg/" + compoundName_ + ".svg";
		pShape = p5Canvas.loadShape(path);
		pShapeW = pShape.width;
		pShapeH = pShape.height;
		minSize = Math.min(pShapeW, pShapeH);
		setMaxSize(Math.max(pShapeW, pShapeH));
		polarity = p5Canvas.db.getCompoundPolarity(compoundName_);

		circles = SVGReader.getSVG(path);
		if(name.equals("Silver-Carbonate") )
		{
			elementNames = new ArrayList<String> ();
			elementNames.add("Silver");
			elementNames.add("Silver");
			elementNames.add("Carbonate");
		}
		else if(name.equals("Silver-Hydroxide"))
		{
			elementNames = new ArrayList<String> ();
			elementNames.add("Silver");
			elementNames.add("Hydroxide");
		}
		else
		elementNames = SVGReader.getNames();
		elementCharges = new ArrayList<Integer>();

		int numElement = elementNames.size();
		for (int i = 0; i < numElement; i++) {
			int charge = DBinterface.getElementCharge(elementNames.get(i));
			elementCharges.add(charge);
			if (elementNames.get(i).equals("Chloride"))
				elementNames.set(i, new String("Chlorine")) ;
			else if(elementNames.get(i).equals("Bromide"))
				elementNames.set(i, new String("Bromine")) ;
			mass+= DBinterface.getElementMass(elementNames.get(i));
		}
		sumForceX = new float[numElement];
		sumForceY = new float[numElement];
		sumForceWaterX = new float[numElement];
		sumForceWaterY = new float[numElement];
		freezingTem = DBinterface.getCompoundFreezingPointCelsius(name);
		boilingTem = DBinterface.getCompoundBoilingPointCelsius(name);

		// Identify specific situation
		if ((name.equals("Sodium-Ion") || name.equals("Potassium-Ion"))
				&& (p5Canvas.getMain().selectedUnit == 2 && p5Canvas.getMain().selectedSet != 7)) {
			circles[0][0] = 28;
		} else if (name.equals("Calcium-Ion")) {
			circles[0][0] = 28;
		}

		// Set up gap: distance from a molecule`s top left corner to its center
		for (int i = 0; i < numElement; i++) {
			float xx = circles[i][1] - pShapeW / 2;
			float yy = -(circles[i][2] - pShapeH / 2);
			gap[i] = (float) Math.sqrt(xx * xx + yy * yy);
			gap[i] = PBox2D.scalarPixelsToWorld(gap[i]);
			if (xx != 0)
				a1[i] = (float) (Math.atan(yy / xx));
			if (xx < 0)
				a1[i] += Math.PI;
		}

		setPropertyByHeat(true);
		createBody(x, y, angle);
	}

	/******************************************************************
	 * FUNCTION : destroy() DESCRIPTION : Molecule Destroy function, return all
	 * other molecules to which this molecule is connecting
	 * 
	 * INPUTS : None OUTPUTS: ArrayList<Molecule>
	 *******************************************************************/
	public ArrayList<DistanceJointWrap> destroy() {
		ArrayList<DistanceJointWrap> res = null;
		// if(!this.compoundJointPair.isEmpty())
		// res = this.destoryAllJointsPair();
		if (!this.compoundJoint.isEmpty())
			res = this.destroyAllJoints();
		this.killBody();
		State.molecules.remove(this);
		return res;
	}

	/******************************************************************
	 * FUNCTION : destroyAllJoints() DESCRIPTION : Destroy all joints that are
	 * connecting to this molecule
	 * 
	 * INPUTS : None OUTPUTS: None
	 *******************************************************************/
	public ArrayList<DistanceJointWrap> destroyAllJoints() {

		Molecule pair = null;
		DistanceJointWrap dj = null;
		Anchor anchor = null;

		// Replicate references of all DistanceJoint to which this molecule is
		// connecting to
		ArrayList<DistanceJointWrap> djList = new ArrayList<DistanceJointWrap>();
		for (int m = 0; m < this.compoundJoint.size(); m++) {
			dj = compoundJoint.get(m);

			djList.add(new DistanceJointWrap(dj, false));
		}

		// Find other molecules to which this molecules is connecting to and
		// remove the distanceJoint reference in them
		if (this.compoundJoint != null && compoundJoint.size() > 0) {
			for (int i = 0; i < compoundJoint.size(); i++) {
				// Get the other molecule of this pair
				dj = compoundJoint.get(i);
				if (dj.getBodyA().getUserData() instanceof Molecule) {
					pair = (Molecule) dj.getBodyA().getUserData();
					if (pair == this) // Find the other one
					{
						if (dj.getBodyB().getUserData() instanceof Molecule) {

							pair = (Molecule) dj.getBodyB().getUserData();
							// Delete this joint reference in other Molecule
							// objects
							if (pair.compoundJoint.contains(dj))
								pair.compoundJoint.remove(dj);
						} else if (dj.getBodyB().getUserData() instanceof Anchor) {
							anchor = (Anchor) dj.getBodyB().getUserData();
							// Delete this joint reference in other Anchors
							if (anchor.compoundJoint.contains(dj))
								anchor.compoundJoint.remove(dj);
						}
					} else {
						// Delete this joint reference in other Molecule objects
						if (pair.compoundJoint.contains(dj))
							pair.compoundJoint.remove(dj);
					}

				} else if (dj.getBodyA().getUserData() instanceof Anchor) {
					anchor = (Anchor) dj.getBodyA().getUserData();
					// Delete this joint reference in other Anchors
					if (anchor.compoundJoint.contains(dj))
						anchor.compoundJoint.remove(dj);
				}

			}

		}

		// Delete this joint from world
		for (int k = 0; k < this.compoundJoint.size(); k++) {
			dj = compoundJoint.get(k);
			// PBox2D.world.destroyJoint(dj);
			dj.destroy();

		}
		compoundJoint.clear();

		/*
		 * //Remove this body from joints in djList for( int k =
		 * 0;k<djList.size();k++) {
		 * 
		 * dj = djList.get(k); if( dj.getBodyA()==this.body) dj.m_bodyA =null;
		 * else dj.m_bodyB = null;
		 * 
		 * }
		 */

		return djList;

	}

	/******************************************************************
	 * FUNCTION : SetPropertyByHeat() DESCRIPTION : Set restitution, friction
	 * and charge rate regarding to temperature
	 * 
	 * INPUTS : isIntial (boolean) OUTPUTS: None
	 *******************************************************************/
	public void setPropertyByHeat(boolean isInitial) {
		float temp = p5Canvas.temp;
		//Set up restituion
		res = (temp - freezingTem) / (boilingTem - freezingTem);
		if (res > 0.05)
			res = 1f;
		else //solid case
			res = 0.05f;

		if (temp <= freezingTem)
			fric = 0.6f;
		else
			fric = 0;

		if (name.equals("Water") && temp < 40)
			scale = 1 + (40 - temp) / 200f;
		else
			scale = 1f;

		if((p5Canvas.getMain().selectedUnit==1||p5Canvas.getMain().selectedUnit==2))
		{
		if (name.equals("Water"))
			chargeRate = 0.95f;
		else if (name.equals("Sodium-Ion")) {
			chargeRate = 0.93f;
			fric = 1;
			res = 0.55f;
			if(temp>150)
				res = 0.0f;
			
		} else if (name.equals("Chlorine-Ion")) {
			chargeRate = 0.93f;
			fric = 1;
			res = 0.f;
		} else if (name.equals("Calcium-Ion")) {
			chargeRate = 0.93f;
			fric = 1;
			res = 0.9f;
		} else if (name.equals("Silicon-Dioxide")) {
			chargeRate = 0.98f;
			fric = 1;
			res = 0.3f;
			scale = 1.2f;
		} else if (name.equals("Glycerol")) {
			chargeRate = 0.9f;
			scale = 1.1f;
		} else if (name.equals("Acetic-Acid")) {
			chargeRate = 0.85f;
			scale = 1.1f;
		} else if (name.equals("Bicarbonate")) {
			chargeRate = 0.88f;
			fric = 1;
			res = 0.0f;
			scale = 1.1f;
		} else if (name.equals("Potassium-Ion")) {
			chargeRate = 0.93f;
			fric = 1;
			res = 0.55f;
		}
		}

		if (!isInitial) {
			setRestitution(res);
			setFriction(fric);
			if (name.equals("Water"))
				setRadius(scale);
		}
	}

	/******************************************************************
	 * FUNCTION : createBody() DESCRIPTION : Create body and shape for molecules
	 * 
	 * INPUTS : x (float), y (float), angle (float) OUTPUTS: None
	 *******************************************************************/
	public void createBody(float x, float y, float angle) {

		// Mannually set up density
		float mul = setMul();

		// Define the body and make it from the shape
		bd = new BodyDef();
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
			circleShape.m_radius = PBox2D.scalarPixelsToWorld(circles[i][0])
					* scale;

			float m = 1;
			String element = null;
			if (elementNames != null && i < elementNames.size()) {
				if (elementNames.get(i).equals("Chloride"))
					element = new String("Chlorine");
				else if(elementNames.get(i).equals("Bromide"))
					element = new String("Bromine");
				else
					element = new String(elementNames.get(i));
				m = DBinterface.getElementMass(element);
			}
			float d = m / (circles[i][0] * circles[i][0] * circles[i][0]);
			fd.shape = circleShape;
			fd.density = d * mul;
			fd.friction = fric;
			fd.restitution = res; // Restitution is bounciness
			// if( p5Canvas.temp < this.freezingTem)
			// fd.restitution = 1.0f;
			// Attach shapes!
			Fixture fixture = body.createFixture(fd);
			fixtures.add(fixture);
		}

		// Give it some initial random velocity
		body.setLinearVelocity(new Vec2(p5Canvas.random(-1, 1), p5Canvas
				.random(-1, 1)));
		body.setAngularVelocity(0);
		body.setUserData(this);
	}	
	

	public static Vec2 getShapeSize(String compoundName_, P5Canvas parent_) {
		String path = "resources/compoundsSvg/" + compoundName_ + ".svg";
		if ((compoundName_.equals("Sodium-Ion") || compoundName_
				.equals("Potassium-Ion"))
				&& (parent_.getMain().selectedUnit == 2 && parent_.getMain().selectedSet != 7)) {
			path = "resources/compoundsSvg/" + "Chlorine-Ion" + ".svg";
		}

		PShape pShape = parent_.loadShape(path);
		float pShapeW = pShape.width;
		float pShapeH = pShape.height;
		float[][] circles = SVGReader.getSVG(path);
		if (compoundName_.equals("Sodium-Chloride")) {
			pShapeW = circles[1][0] * 4;
			pShapeH = circles[1][0] * 2;
		}
		return new Vec2(pShapeW, pShapeH);
	}

	public int getNumElement() {
		//return circles.length;
		return elementNames.size();
	}

	public String getName() {
		return name;
	}

	public float getBodyMass() {
		return body.getMass();
	}
	public float getMoleculeMass()
	{
		return mass;
	}
	//Get position in world coordinates
	public Vec2 getPosition() {
		return body.getPosition();
	}
	public void setPosition(Vec2 pos,float angle)
	{
		body.setTransform(pos, angle);
	}
	public float getAngle()
	{
		return body.getAngle();
	}

	public void setRestitution(float r) {
		Fixture s = body.getFixtureList();
		for (int i = 0; i < circles.length; i++) {
			if (s == null)
				continue;
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
		Vec2 v = new Vec2((float) Math.cos(a1[e] + a2), (float) Math.sin(a1[e]
				+ a2));
		if(this.getName().equals("Silver-Carbonate")&&e>=2) //Set Ag2CO3 element location at the center of CO3
			return pos.add(v.mul(gap[5]));
		return pos.add(v.mul(gap[e]));
	}

	public void addForce(Vec2 f) {
		Vec2 pos = body.getPosition();
		body.applyForce(f, pos);
	}

	/* Add a force to a certain position */
	public void addForce(Vec2 f, int e) {
		force = f;
		Vec2 l = getElementLocation(e);
		loc[e] = l;
		body.applyForce(force, l);
	}

	public void setRadius(float scale) {
		Fixture s = body.getFixtureList();
		for (int i = 0; i < circles.length; i++) {
			s.m_shape.m_radius = PBox2D.scalarPixelsToWorld(circles[i][0])
					* scale;
			s = s.getNext();
		}
	}

	public void display() {
		// float yyy = (2+body.getPosition().y)/90;
		// if (yyy<0) yyy=0;
		// if (P5Canvas.temp<100)
		// body.applyForce(new Vec2(0,-yyy), body.getPosition());

		//Update molecule positions
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
		/************************** Boundary Check **************************/
		/* If molecules go out of boundary, reset their position */
		/* Top boundary check, top boundary has max y value */
		if (body.getPosition().y + PBox2D.scalarPixelsToWorld(this.minSize / 2) > boundaries[2].body
				.getPosition().y) {
			Vec2 v = new Vec2(body.getPosition().x,
					boundaries[2].body.getPosition().y
							- PBox2D.scalarPixelsToWorld(getMaxSize() / 2));
			if (body != null && v != null)
				body.setTransform(v, body.getAngle());
		}
		/* Bottom boundary check, bot boundary has min y value */
		else if (body.getPosition().y
				- PBox2D.scalarPixelsToWorld(this.minSize / 2) < boundaries[3].body
					.getPosition().y) {
			Vec2 v = new Vec2(body.getPosition().x,
					boundaries[3].body.getPosition().y
							+ PBox2D.scalarPixelsToWorld(getMaxSize() / 2));
			if (body != null && v != null)
				body.setTransform(v, body.getAngle());
		}
		/* Left boundary check, left boundary has min x value */
		if (body.getPosition().x - PBox2D.scalarPixelsToWorld(this.minSize / 2) < boundaries[0].body
				.getPosition().x) {
			Vec2 v = new Vec2(boundaries[0].body.getPosition().x
					+ PBox2D.scalarPixelsToWorld(getMaxSize() / 2),
					body.getPosition().y);
			if (body != null && v != null)
				body.setTransform(v, body.getAngle());
		}
		/* Right boundary check, right boundary has max x value */
		else if (body.getPosition().x
				+ PBox2D.scalarPixelsToWorld(this.minSize / 2) > boundaries[1].body
					.getPosition().x) {
			Vec2 v = new Vec2(boundaries[1].body.getPosition().x
					- PBox2D.scalarPixelsToWorld(getMaxSize() / 2),
					body.getPosition().y);
			if (body != null && v != null)
				body.setTransform(v, body.getAngle());
		}

		// We look at each body and get its screen position
		Vec2 pos = box2d.getBodyPixelCoord(body);
		// Get its angle of rotation

		float a = body.getAngle();

		/********************* Draw Bodies *******************/
		p5Canvas.pushMatrix();
		p5Canvas.translate(pos.x, pos.y);
		float temp = p5Canvas.temp;
		p5Canvas.rotate(-a);
		p5Canvas.shape(pShape, pShapeW / -2, pShapeH / -2, pShapeW, pShapeH);
		// parent.noFill();
		p5Canvas.fill(Color.GRAY.getRGB(), 240);

		/*
		 * If molecules are selected or deselected in tableview, render or hide
		 * them
		 */

		if (! p5Canvas.getMain().getTableView().selectedRowsIsEmpty()) {

			String [] selectedMoleculesString = p5Canvas.getTableView().getSelectedMolecule();
			if(selectedMoleculesString!=null)
			{
				if(selectedMoleculesString.length>0)
				{
					
					boolean contains = false;
					if(this.tableIndex == -1 )
					{
						
						for(String moleName:selectedMoleculesString)
						{
							if(name.equals(moleName))
							{
								contains = true;
								break;
							}
						}
					}
					else //If we are using tableIndex to connect molecule with table index
					{
						int [] selectedRows = p5Canvas.getTableView().getSelectedRows();
						for(int index:selectedRows)
						{
							if(this.tableIndex==index)
							{
								contains = true;
								break;
							}
						}
					}
					if(!contains) //If selected molecules names dont contain this name
					{
						p5Canvas.noStroke();
						for (int i = 0; i < circles.length; i++) {
							p5Canvas.ellipse(circles[i][1] - pShapeW / 2, circles[i][2]
									- pShapeH / 2, circles[i][0] * 2, circles[i][0] * 2);
						}
					}
				
					
			}
			}

			
		}
		/* If hide checkbox is selected, hide them */
		else if (p5Canvas.isHidingEnabled && !isHidden) {
			p5Canvas.noStroke();
			for (int i = 0; i < circles.length; i++) {
				p5Canvas.ellipse(circles[i][1] - pShapeW / 2, circles[i][2]
						- pShapeH / 2, circles[i][0] * 2, circles[i][0] * 2);
			}
		}

		if (name.equals("Calcium-Ion")) {
			p5Canvas.stroke(Color.BLUE.getRGB());
		}
		p5Canvas.popMatrix();
		// End drawing

		/* Check if it is displaying forces */
		if (p5Canvas.isDisplayForces ) {
			int numElement = elementNames.size();
			for (int i = 0; i < numElement; i++) {
				if (loc[i] == null)
					continue;
				if(!(sumForceWaterX[i]==0&&sumForceWaterY[i]==0&&sumForceX[i]==0&&sumForceY[i]==0 ))
				{
				p5Canvas.stroke(Color.BLUE.getRGB());
				p5Canvas.line(
						PBox2D.scalarWorldToPixels(loc[i].x),
						p5Canvas.height - PBox2D.scalarWorldToPixels(loc[i].y),
						PBox2D.scalarWorldToPixels(loc[i].x)
								+ PBox2D.scalarWorldToPixels(sumForceWaterX[i]
										+ sumForceX[i]),
						p5Canvas.height
								- PBox2D.scalarWorldToPixels(loc[i].y)
								- PBox2D.scalarWorldToPixels(sumForceWaterY[i]
										+ sumForceY[i]));
				}
			}
			//this.clearForce();
		}

		/* Check if it is displaying joints */
		if (p5Canvas.isDisplayJoints) {
			// For Unit 1 and Unit 2
			if (p5Canvas.getMain().selectedUnit == 1
					|| p5Canvas.getMain().selectedUnit == 2) {
				if (compoundJ >= 0) {
					Vec2 pos2 = box2d.getBodyPixelCoord(molecules
							.get(compoundJ).body);
					p5Canvas.stroke(Color.BLACK.getRGB());
					p5Canvas.line(pos.x, pos.y, pos2.x, pos2.y);
				}

				if (otherJ >= 0) {
					Vec2 pos2 = box2d
							.getBodyPixelCoord(molecules.get(otherJ).body);
					p5Canvas.stroke(Color.RED.getRGB());
					p5Canvas.line(pos.x, pos.y, pos2.x, pos2.y);
				}
			} else {
				if (compoundJoint.size() != 0) {
					Vec2 pos2 = new Vec2();
					Body theOtherBody = null;
					for (int i = 0; i < compoundJoint.size(); i++) {
						if (compoundJoint.get(i).getBodyA().getUserData() == this)
							theOtherBody = compoundJoint.get(i).getBodyB();
						else
							theOtherBody = compoundJoint.get(i).getBodyA();

						pos2.set(box2d.getBodyPixelCoord(theOtherBody));
						p5Canvas.stroke(Color.BLACK.getRGB());
						p5Canvas.line(pos.x, pos.y, pos2.x, pos2.y);
					}
				}

			}
		}
		
		//Draw element center for testing
		/*
		for(int e=0;e<this.elementNames.size();e++)
		{
			int size = 5;
			p5Canvas.fill(204, 102, 0);
			Vec2 loc = new Vec2(PBox2D.vectorWorldToPixels(getElementLocation(e)));
			p5Canvas.ellipse(loc.x,p5Canvas.h * 0.77f+loc.y,size,size);
		}
		*/

	}

	// This function removes the particle from the box2d world
	public void killBody() {
		box2d.destroyBody(body);
		body.m_world = null;
	}
	
	public void clearForce(){
		
		for (int e = 0; e < this.getNumElement(); e++) {
			this.sumForceX[e]=0;
			this.sumForceY[e]=0;
			
			
			this.sumForceWaterX[e]=0;
			this.sumForceWaterY[e]=0;
		
		}
	}

	/******************************************************************
	 * FUNCTION : setMul() DESCRIPTION : Mannually set up density for different
	 * elements
	 * 
	 * INPUTS : None OUTPUTS: None
	 *******************************************************************/

	private float setMul() {
		float mul = 1.0f;
		if (name.equals("Pentane"))
			mul = 0.04f;
		else if (name.equals("Bromine"))
			mul = 0.45f;
		else if (name.equals("Mercury"))
			mul = 0.3f;
		else if (name.equals("Hydrogen-Peroxide"))
			mul = 0.8f;
		else if (name.equals("Sodium-Chloride"))
			if(p5Canvas.getMain().selectedUnit==1 ||p5Canvas.getMain().selectedUnit==2)
			mul = 1.0f;
			else
				mul =4.0f;
		else if (name.equals("Sodium-Ion"))
			mul = 0.011f / 0.006448616f;
		else if (name.equals("Chlorine-Ion"))
			mul = 4.0f;
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
			if(p5Canvas.getMain().selectedUnit==1 ||p5Canvas.getMain().selectedUnit==2)
			mul = 0.04f;
			else
				mul =0.4f;
		else if (name.equals("Sodium"))
			mul = 1.0f;
		else if (name.equals("Hydrogen-Ion"))
			mul =6.0f;
		else if (name.equals("Lithium-Ion"))
			mul =4f;
		else if (name.equals("Hydrogen-Sulfide"))
			mul =3f;
		else if (name.equals("Hydrogen"))
			mul = 15.0f;
		else if (name.equals("Chloride"))
			mul = 1.5f;
		else if (name.equals("Ammonium"))
			mul = 2.5f;
		else if (name.equals("Helium"))
			mul = 4.0f;
		return mul;
	}

	/**
	 * @return the maxSize
	 */
	public float getMaxSize() {
		return maxSize;
	}

	/**
	 * @param maxSize
	 *            the maxSize to set
	 */
	public void setMaxSize(float maxSize) {
		this.maxSize = maxSize;
	}

	public void setReactive(boolean flag) {
		this.reactive = flag;
	}

	public boolean getReactive() {
		return this.reactive;
	}

	public boolean isSolid() {
		float temp = p5Canvas.temp;
		if (temp < this.freezingTem)
			return true;
		else
			return false;
	}
	public Vec2 getLinearVelocity()
	{
		return body.getLinearVelocity();
	}
	public float getLinearVelocityScalar()
	{
		float scalar = 0;
		Vec2 vector = body.getLinearVelocity();
		scalar = vector.x*vector.x + vector.y*vector.y;
		scalar = (float) Math.sqrt(scalar);
		return scalar;
	}
	public float getKineticEnergy()
	{
		return (0.5f*mass*getLinearVelocityScalar()*getLinearVelocityScalar());
	}
	public void setLinearVelocity(Vec2 vec)
	{
		body.setLinearVelocity(vec);
	}
	public void setTableIndex(int index)
	{
		/*
		if(tableIndex==-1)
		{
			this.tableIndex = index;
			p5Canvas.getTableView().increaseRowCount(tableIndex, 1);
		}
		else
		{
			if(this.tableIndex==index)
				return;
			p5Canvas.getTableView().increaseRowCount(tableIndex, -1);
			tableIndex = index;
			p5Canvas.getTableView().increaseRowCount(tableIndex, 1);
		}*/
		this.tableIndex = index;
	}
	public int getTableIndex()
	{
		return this.tableIndex;
	}
}
