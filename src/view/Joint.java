package view;

import java.awt.Color;
import java.util.ArrayList;

import processing.core.*;

import main.Canvas;
import main.TableView;
import model.DBinterface;

import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.DistanceJointDef;

import Util.SVGReader;
import static model.State.*;

public class Joint {
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
	public float sumForceCaClWaterX;
	public float sumForceCaClWaterY;
	
	public float chargeRate = 1;
	public boolean isGone = false;
	public boolean isWaterGone = false;
	public int partner = -1;
	public static float clRadius = 28f;
	public int[] ClPartners = new int[2];
	
	public int compoundJ = -1;
	public int otherJ = -1;
	public int CaOtherJ = -1;
	public DistanceJoint compoundJoints = null;
	public DistanceJoint otherJoints = null;
	
	// Constructor
	Joint(float x, float y, String compoundName_, PBox2D box2d_,
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

		if (name.equals("Sodium-Chloride")){
			circles[0][0] = circles[1][0];
			circles[0][1] = 6f;
		}	
		else if (name.equals("Sodium-Ion")){
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
		
		if (name.equals("Water"))	chargeRate = 0.76f;
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
		if (compoundName_.equals("Sodium-Ion")){
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
	
	
	public Vec2 getLinearVelocity() {
		return body.getLinearVelocity();
	}

	public void setLinearVelocity(Vec2 v) {
		body.setLinearVelocity(v);
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
		//if (name.equals("Calcium-Ion"))
		//	 offset = new Vec2( circles[0][0],0);
			
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

	public static void display(P5Canvas parent, PBox2D box2d) {
		org.jbox2d.dynamics.joints.Joint j = PBox2D.world.getJointList() ;
		for (int i=0;i<PBox2D.world.getJointCount();i++){
			System.out.println(" Joint:"+i);
			Vec2 pos1 =box2d.getBodyPixelCoord(j.m_bodyA);
			Vec2 pos2 =box2d.getBodyPixelCoord(j.m_bodyB);
			parent.stroke(Color.CYAN.getRGB());
			parent.line(pos1.x, pos1.y,pos2.x,pos2.y);
			j=j.getNext();
		}
		
	}

	

	// This function removes the particle from the box2d world
	public void killBody() {
		box2d.destroyBody(body);
		body.m_world = null;
	}
}
