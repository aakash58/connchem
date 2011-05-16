package view;

import processing.core.*;
import pbox2d.*;

import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.dynamics.*;

public class Molecule {
	P5Canvas parent;
	// We need to keep track of a Body and a radius
	Body body;
	float r;
	PBox2D box2d;
	String file;

	Molecule(float x_, float y_, String compoundName, PBox2D box2d_, P5Canvas parent_) {
		this.parent = parent_;
		this.box2d = box2d_;
		r = 25;
		file = compoundName;
		
		// This function puts the particle in the Box2d world
		makeBody(x_,y_,r);
		
		body.setUserData(this);
	}

	// This function removes the particle from the box2d world
	void killBody() {
		box2d.destroyBody(body);
	}

	// Change velocity when hit
	void change(double tEMPMOD) {
		body.setLinearVelocity(body.getLinearVelocity().mul((float)tEMPMOD));
	}

	// Is the particle ready for deletion?
	boolean done() {
		// Let's find the screen position of the particle
		Vec2 pos = box2d.getBodyPixelCoord(body);
		// Is it off the bottom of the screen?
		if (pos.y > parent.h()+r*2) {
			killBody();
			return true;
		}
		return false;
	}

	// 
	void display() {
		// We look at each body and get its screen position
		Vec2 pos = box2d.getBodyPixelCoord(body);
		// Get its angle of rotation
		float a = body.getAngle();
		parent.pushMatrix();
		parent.translate(pos.x,pos.y);
		parent.rotate(a);
		parent.stroke(0);
		parent.strokeWeight(1);
		parent.ellipse(0,0,r*2,r*2);
		// Let's add a line so we can see the rotation
		parent.line(0,0,r,0);
		parent.popMatrix();
	}

	// Here's our function that adds the particle to the Box2D world
	void makeBody(float x_, float y_, float r_) {
		// Define a body
		BodyDef bd = new BodyDef();
		// Set its position
		bd.position = box2d.coordPixelsToWorld(x_,y_);
		body = box2d.world.createBody(bd);

		// Make the body's shape a circle
		CircleDef cd = new CircleDef();
		cd.radius = box2d.scalarPixelsToWorld(r_);
		cd.density = 1.0f;
		cd.friction = 0.0f;
		cd.restitution = 1.0f; // Restitution is bounciness
		body.createShape(cd);

		// Always do this at the end
		body.setMassFromShapes();

		// Give it a random initial velocity (and angular velocity)
		body.setLinearVelocity(new Vec2(parent.random(-10f,10f),parent.random(5f,10f)));
		//body.setAngularVelocity(random(-10,10));
	}
}
