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
	private P5Canvas parent;
	private PShape pShape = new PShape();
	private float pShapeW = 0f;
	private float pShapeH = 0f;
	public float[][] circles;
	public ArrayList<String> elementNames;
	public ArrayList<Integer> elementCharges;
	private String name;
	public float freezingTem;
	public float boilingTem;
	public float fric;
	public float res;
	private float scale = 1;

	private float xTmp;
	private float yTmp;
	private float minSize;
	public boolean polarity;
	public Vec2 off;
	public boolean isBrushed =false;

	public Vec2 force = new Vec2(0,0);
	public Vec2 offset1 = new Vec2(0,0);
	public Vec2 offset2 = new Vec2(0,0);
	public Vec2 offset3 = new Vec2(0,0);
	public Vec2[] loc = new Vec2[30];
	public Vec2[] fff = new Vec2[30];
	public float[] faInternalX;
	public float[] faInternalY;
	public float[] frInternalX;
	public float[] frInternalY;
	public float[] faExternalX;
	public float[] faExternalY;
	public float[] frExternalX;
	public float[] frExternalY;
	public float[] sumForceX;
	public float[] sumForceY;
	public float[] sumForceWaterX;
	public float[] sumForceWaterY;
	
	public float chargeRate = 1;
	public boolean isGone = false;
	public int waterPartner = -1;	 // Set1 Unit2 
	public int NaClPartner = -1;  // Set1 Unit2
	
	public static float clRadius = 28f;
	public static float oRadius = 18.495f; // Oxygen Radius. This depends on SVG file
	
	public int[] ClPartners = new int[2];
	
	public int compoundJ = -1;
	public int otherJ = -1;
	public int CaOtherJ = -1;
	public DistanceJoint compoundJoints = null;
	public PrismaticJoint compoundJoints2 = null; //is Used for Unit 2 set 7
	public DistanceJoint otherJoints = null;
	
	public float ionDis =0;  // Use to compute dissolve
	
	// Constructor
	Molecule(float x, float y, String compoundName_, PBox2D box2d_,
			P5Canvas parent_, float angle) {
		loc[0] = new Vec2(0,0);
		loc[1] = new Vec2(0,0);
		loc[2] = new Vec2(0,0);
		fff[0] = new Vec2(0,0);
		fff[1] = new Vec2(0,0);
		fff[2] = new Vec2(0,0);
		
		parent = parent_;
		box2d = box2d_;
		name = compoundName_;

		String path = "resources/compoundsSvg/" + compoundName_ + ".svg";
		pShape = parent.loadShape(path);
		pShapeW = pShape.width;
		pShapeH = pShape.height;
		minSize = Math.min(pShapeW, pShapeH);
		polarity = parent.db.getCompoundPolarity(compoundName_);

		circles = SVGReader.getSVG(path);
		elementNames = SVGReader.getNames();
		elementCharges = new ArrayList<Integer>();
		
		int numElement = elementNames.size();
		for (int i=0; i<numElement;i++){
			int charge = P5Canvas.db.getElementCharge(elementNames.get(i));
			elementCharges.add(charge);
		}
		faInternalX=new float[numElement];
		faInternalY=new float[numElement];
		frInternalX=new float[numElement];
		frInternalY=new float[numElement];
		faExternalX=new float[numElement];
		faExternalY=new float[numElement];
		frExternalX=new float[numElement];
		frExternalY=new float[numElement];
		sumForceX = new float[numElement];
		sumForceY = new float[numElement];
		sumForceWaterX = new float[numElement];
		sumForceWaterY = new float[numElement];

		if (name.equals("Sodium-Ion") && (Main.selectedUnit==2 && Main.selectedSet!=7)){
			circles[0][0] = 28;
		}
		else if (name.equals("Calcium-Ion")){
			circles[0][0] = 28;
		}
		
		freezingTem = P5Canvas.db.getCompoundFreezingPointCelsius(name);
		boilingTem = P5Canvas.db.getCompoundBoilingPointCelsius(name);
		setPropertyByHeat(true);
		
		createBody(x, y,angle);
	}
	
	public void setPropertyByHeat(boolean isInitial) {
		float temp = P5Canvas.temp;
		res = (temp - freezingTem) / (boilingTem - freezingTem);
		if (res > 0)  	res =1f;
		else			res = 0.2f;
		if (temp <= freezingTem)	fric = 1;
		else						fric = 0;
		if (name.equals("Water") && temp < 40) 	scale = 1 + (40 - temp) / 200f;
		else									scale = 1f;
		
		if (name.equals("Water"))	chargeRate = 0.80f;
		else if (name.equals("Sodium-Ion")){
			chargeRate = 0.93f;
			fric = 1;
			res=0.5f;
		}
		else if (name.equals("Chlorine-Ion")){
			chargeRate = 0.93f;
			fric = 1;
			res=0.f;
		}
		else if (name.equals("Calcium-Ion")){
			chargeRate = 0.93f;
			fric = 1;
			res=0.f;
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
		
		if (!isInitial){
			setRestitution(res);
			setFriction(fric);
			if (name.equals("Water"))
				setRadius(scale);
		}
	}
	
	public void createBody(float x, float y,float angle) {
		float mul = 1;
		if (name.equals("Pentane"))
			mul = 0.10f;
		else if (name.equals("Bromine"))
			mul = 0.45f;
		else if (name.equals("Mercury"))
			mul = 0.3f;
		else if (name.equals("Hydrogen-Peroxide"))
			mul = 0.8f;
		else if (name.equals("Sodium-Chloride"))
			mul = 1.0f;
		else if (name.equals("Sodium-Ion"))
			mul = 0.015f/0.006448616f;
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
		//System.out.println("");
		//System.out.println("names:"+elementNames);
		
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
				//System.out.println("elementNames:"+elementNames.get(i));
				m = DBinterface.getElementMass(elementNames.get(i));
			}	
			float d = m / (circles[i][0] * circles[i][0] * circles[i][0]);
			fd.shape = circleShape;
	        fd.density = d * mul;
			fd.friction = fric;
			fd.restitution = res;
			// Attach shapes!
			body.createFixture(fd);
		}
		// System.out.println(name+ " get Mass "+body.getMass() +" DBmass:"+ +DBinterface.getCompoundMass(name));
		// Give it some initial random velocity
		body.setLinearVelocity(new Vec2(parent.random(-1, 1), parent.random(-1,
				1)));
		body.setAngularVelocity(0);
		body.setUserData(this);
	}

	
	public static Vec2 getShapeSize(String compoundName_, P5Canvas parent_) {
		String path = "resources/compoundsSvg/" + compoundName_ + ".svg";
		if (compoundName_.equals("Sodium-Ion") && 
				(main.Main.selectedUnit==2 && main.Main.selectedSet!=7)){
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
	public static int getNumberElement(String compoundName_, P5Canvas parent_) {
		String path = "resources/compoundsSvg/" + compoundName_ + ".svg";
		float[][] circles = SVGReader.getSVG(path);
		return circles.length;
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
		Vec2 offset = new Vec2(circles[e][1]-pShapeW/2,circles[e][2]-pShapeH/2);
		float x = offset.x;
		float y = -offset.y;
		float xy = (float) Math.sqrt(x*x+y*y);
		xy = PBox2D.scalarPixelsToWorld(xy);
		float a1 = 0;
		if (x!=0)
			a1 = (float) (Math.atan(y/x));
		if (x<0) a1+= Math.PI;
		float a2 = body.getAngle();
		Vec2 v = new Vec2((float) Math.cos(a1+a2), (float)  Math.sin(a1+a2));
		return pos.add(v.mul(xy));
	}
		
	
	public void addForce(Vec2 f) {
		Vec2 pos = body.getPosition();
		body.applyForce(f, pos);
	}

	
	public void addForce(Vec2 f, int e) {
		force =f;
		Vec2 l =  getElementLocation(e);
		loc[e] = l;
		fff[e] =force;
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
		
		if (P5Canvas.isDrag && P5Canvas.draggingBoundary < 0) {
			float xx = xTmp + PBox2D.scalarPixelsToWorld(P5Canvas.xDrag);
			float yy = yTmp - PBox2D.scalarPixelsToWorld(P5Canvas.yDrag);
			Vec2 v = new Vec2(xx, yy);
			body.setTransform(v, body.getAngle());
			body.setAngularVelocity(0);
		} else {
			xTmp = body.getPosition().x;
			yTmp = body.getPosition().y;
		}

		float t = P5Canvas.h - P5Canvas.y;
		float yy = PBox2D.scalarWorldToPixels(body.getPosition().y);

		if (yy > t - minSize / 3 + Boundary.difVolume) {
			Vec2 v = new Vec2(body.getPosition().x, PBox2D.scalarPixelsToWorld(t
					- minSize + Boundary.difVolume));
			if (body != null && v != null)
				body.setTransform(v, body.getAngle());
		}

		// We look at each body and get its screen position
		Vec2 pos = box2d.getBodyPixelCoord(body);
		// Get its angle of rotation
		
		float a = body.getAngle();
		// parent.rectMode(parent.CENTER);
		parent.pushMatrix();
		parent.translate(pos.x, pos.y);
		parent.rotate(-a);
		parent.shape(pShape, pShapeW / -2, pShapeH / -2, pShapeW, pShapeH); 
		//parent.noFill();
		parent.fill(Color.GRAY.getRGB(),240);
		if (TableView.selectedRow>=0){
			if (!name.equals(Canvas.getSelectedMolecule())) {
				parent.noStroke();
				for (int i=0; i<circles.length;i++){
					parent.ellipse( circles[i][1]-pShapeW/2, circles[i][2]-pShapeH/2,circles[i][0]*2, circles[i][0]*2);
				}
			}
		}
		else if (P5Canvas.isEnableBrushing && !isBrushed){
			parent.noStroke();
			for (int i=0; i<circles.length;i++){
				parent.ellipse( circles[i][1]-pShapeW/2, circles[i][2]-pShapeH/2,circles[i][0]*2, circles[i][0]*2);
			}
		}
		
		if (name.equals("Calcium-Ion")){
			parent.stroke(Color.BLUE.getRGB());
			//parent.line(0,0,30,0);
			//parent.ellipse( circles[0][0], 0,6, 6);
		}
		parent.popMatrix();
		
		if (P5Canvas.isDisplayForces && !name.equals("Water")){
			int numElement = elementNames.size();
			for (int i=0; i<numElement;i++){
				parent.stroke(Color.BLUE.getRGB());
				parent.line(PBox2D.scalarWorldToPixels(loc[i].x), parent.height-PBox2D.scalarWorldToPixels(loc[i].y),
					PBox2D.scalarWorldToPixels(loc[i].x)+PBox2D.scalarWorldToPixels(sumForceWaterX[i]+sumForceX[i]), 
				parent.height-PBox2D.scalarWorldToPixels(loc[i].y)-PBox2D.scalarWorldToPixels(sumForceWaterY[i]+sumForceY[i]));
			}
		}
		
		//pos = body.getPosition();
		if (P5Canvas.isDisplayJoints){
			
				if (compoundJ>=0) {
					Vec2 pos2 = box2d.getBodyPixelCoord(molecules.get(compoundJ).body);
					parent.stroke(Color.BLACK.getRGB());
					parent.line(pos.x, pos.y,pos2.x,pos2.y);
				}
			
				if (otherJ>=0) {
					Vec2 pos2 = box2d.getBodyPixelCoord(molecules.get(otherJ).body);
					parent.stroke(Color.RED.getRGB());
					parent.line(pos.x, pos.y,pos2.x,pos2.y);
				}
		}
		
	}

	

	// This function removes the particle from the box2d world
	public void killBody() {
		box2d.destroyBody(body);
		body.m_world = null;
	}
}
