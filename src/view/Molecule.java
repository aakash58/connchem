package view;

import java.net.URL;

import processing.core.*;
import pbox2d.*;
import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.dynamics.*;

import main.DataReader;
import main.Main;

public class Molecule {
	// We need to keep track of a Body and a width and height
	Body body;
	float r;
	PBox2D box2d;
	P5Canvas parent;
	PShape pShape = new PShape();
	float pShapeW = 0f;
	float pShapeH = 0f;

	private float[][] circles;

	// Constructor
	Molecule(float x, float y, String compoundName_, PBox2D box2d_,
			P5Canvas parent_) {
		parent = parent_;
		box2d = box2d_;
		r = 20;
		
		String url = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String path = url + "resources/compoundsSvg/"+compoundName_+".svg";
		pShape = parent.loadShape(path);
		
		pShapeW = pShape.width;
		pShapeH = pShape.height;
		
		System.out.println("new path: "+path);
		circles = DataReader.getSVG(path);
		
		// Add the box to the box2d world
		makeBody(new Vec2(x, y));
	}

	// This function removes the particle from the box2d world
	void killBody() {
		box2d.destroyBody(body);
	}

	// Is the particle ready for deletion?
	boolean done() {
		// Let's find the screen position of the particle
		Vec2 pos = box2d.getBodyPixelCoord(body);
		// Is it off the bottom of the screen?
		if (pos.y > r) {
			killBody();
			return true;
		}
		return false;
	}

	// Drawing the box
	void display() {
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
		//parent.line(0,0,r,0);
	 	parent.popMatrix();
	}

	// This function adds the rectangle to the box2d world
	void makeBody(Vec2 center) {
		// Define the body and make it from the shape
		BodyDef bd = new BodyDef();
		bd.position.set(box2d.coordPixelsToWorld(center));
		body = box2d.createBody(bd);

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
		
			// Attach both shapes!
			body.createShape(cd);
		}
		body.setMassFromShapes();

		// Give it some initial random velocity
		body.setLinearVelocity(new Vec2(parent.random(-10, 10), parent.random(5,10)));
		body.setAngularVelocity(parent.random(-10, 10));
	}

}
