package view;

import processing.core.*;
import pbox2d.*;

import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.dynamics.*;

import Util.SVGReader;

public class Molecule {
	// We need to keep track of a Body and a width and height
	private Body body;
	private PBox2D box2d;
	private P5Canvas parent;
	private PShape pShape = new PShape();
	private float pShapeW = 0f;
	private float pShapeH = 0f;
	private float[][] circles;
	private float currentRate;

	
	// Constructor
	Molecule(float x, float y, String compoundName_, PBox2D box2d_,
			P5Canvas parent_, float speedRate) {
		parent = parent_;
		box2d = box2d_;
		currentRate = speedRate; 
		
		String path = "resources/compoundsSvg/"+compoundName_+".svg";
		pShape = parent.loadShape(path);
		pShapeW = pShape.width;
		pShapeH = pShape.height;
		
		circles = SVGReader.getSVG(path);
		
		// Define the body and make it from the shape
		BodyDef bd = new BodyDef();
		bd.position.set(box2d.coordPixelsToWorld(new Vec2(x, y)));
		
		// This infinitive loop fix nullPointerException  because box2d.createBody(bd) may create a null body
		// The error happens when user want to create many compound at the same time
		body = box2d.createBody(bd);
		int numTry =1;
		while (body ==null){ 
			body = box2d.createBody(bd);
			//System.out.println("Box2d:"+box2d+" bd:"+bd+" numTry:"+numTry);
			numTry++;
		}	
		
		for (int i=0; i<circles.length;i++){
			// Define a circle
			CircleDef cd = new CircleDef();
			// Offset its "local position" (relative to 0,0)
			Vec2 offset = new Vec2(circles[i][1]-pShapeW/2, circles[i][2]-pShapeH/2);
			cd.localPosition = box2d.vectorPixelsToWorld(offset);
			cd.radius = box2d.scalarPixelsToWorld(circles[i][0]);
			cd.density = 1.0f;
			cd.friction = 0.0f;
			cd.restitution = 1.0f;
		
			// Attach shapes!
			body.createShape(cd);
		}
		body.setMassFromShapes();
		
		// Give it some initial random velocity
		body.setLinearVelocity(new Vec2(parent.random(-10, 10)*currentRate, parent.random(-10,10)*currentRate));
		body.setAngularVelocity(parent.random(-10, 10)*currentRate);
		body.setUserData(this);
	}
	
	public void setSpeed(float newRate) {
		Vec2 v =  body.getLinearVelocity();
		body.setLinearVelocity(new Vec2( v.x*newRate/currentRate, v.y*newRate/currentRate));
		
		float angularVelocity = body.getAngularVelocity();
		body.setAngularVelocity(angularVelocity*newRate/currentRate);
		
		currentRate = newRate;
	}
	

	public void setSpeedByHeat(float newRate) {
		Vec2 v =  body.getLinearVelocity();
		body.setLinearVelocity(new Vec2( v.x*newRate, v.y*newRate));
		float angularVelocity = body.getAngularVelocity();
		body.setAngularVelocity(angularVelocity*newRate);
	}
	
	public void display() {
		// We look at each body and get its screen position
		Vec2 pos = box2d.getBodyPixelCoord(body);
		// Get its angle of rotation
		float a = body.getAngle();

		// parent.rectMode(parent.CENTER);
		parent.pushMatrix();
		parent.translate(pos.x, pos.y);
		parent.rotate(-a);
		
		parent.shape(pShape, pShapeW/-2, pShapeH/-2, pShapeW, pShapeH); // second two args center for p5
 		parent.fill(175);
		parent.stroke(0);
	 	parent.popMatrix();
	}
	
	
	
	// This function removes the particle from the box2d world
	public void killBody() {
		box2d.destroyBody(body);
	}

	/*/ Is the particle ready for deletion?
	public boolean done() {
		// Let's find the screen position of the particle
		Vec2 pos = box2d.getBodyPixelCoord(body);
		// Is it off the bottom of the screen?
		if (pos.y > r) {
			killBody();
			return true;
		}
		return false;
	}
	 */
	

	
}
