package view;

import processing.core.*;
import pbox2d.*;

import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.dynamics.*;

import static model.State.*;

public class Molecule {
	P5Canvas parent;
	// We need to keep track of a Body and a radius
	Body body;
	PBox2D box2d;
	String compoundName;
	float angle; // angle of body
	
	private int color = 0;

	Molecule(float x_, float y_, String compoundName_, PBox2D box2d_, P5Canvas parent_) {
		this.parent = parent_;
		this.box2d = box2d_;
		compoundName = compoundName_;
		
		// This function puts the particle in the Box2d world
		makeBody(new Vec2(x_,y_));
		
		body.setUserData(this);
		
	}

	// This function removes the particle from the box2d world
	void killBody() {
		box2d.destroyBody(body);
		molecules.remove(this);
	}


	float bodysize = 25f; // radius
	
	void display() {
		parent.pushStyle();
		parent.pushMatrix();
			// We look at each body and get its screen position
			Vec2 pos = box2d.getBodyPixelCoord(body);
			// Get its angle of rotation
			angle = body.getAngle();
			parent.translate(pos.x,pos.y);
			
			// this is temporary
			if (compoundName.equals("Water")) {
				parent.fill(25, 25, 127);
			} else {
				parent.fill(0, 127, 0);
			}

			parent.rotate(angle * -1);

/*			parent.ellipse(0,0,bodysize*2,bodysize*2);
			// Let's add a line so we can see the rotation
			parent.line(0,0,bodysize,0);*/
			
			parent.rect(bodysize/-2, bodysize/-2, bodysize, bodysize);
			
			parent.ellipseMode(parent.CENTER);
			parent.fill(255);
			parent.stroke(255);
			parent.ellipse(0, 0, 5, 5);
			parent.line(0,0,10,0);
		parent.popMatrix();
		parent.popStyle();
	}

	// Here's our function that adds the particle to the Box2D world
	void makeBody(Vec2 center_) {
		// Define a body
		BodyDef bd = new BodyDef();
		// Set its position
		bd.position = box2d.coordPixelsToWorld(center_);
		body = box2d.world.createBody(bd);

/*		// Make the body's shape a circle
		CircleDef cd = new CircleDef();
		cd.radius = box2d.scalarPixelsToWorld(bodysize);
		cd.density = 1.0f;
		cd.friction = 0.0f;
		cd.restitution = 1.0f; // Restitution is bounciness
		body.createShape(cd); */
		
		// I'm gonna try a square.
		PolygonDef sd = new PolygonDef();
		float boxW = box2d.scalarPixelsToWorld(bodysize/2);
		float boxH = box2d.scalarPixelsToWorld(bodysize/2);
		sd.setAsBox(boxW, boxH);
		sd.density = 1.0f;
		sd.friction = 0.0f;
		sd.restitution = 1.0f;
		
		body.createShape(sd);
		

		// Always do this at the end
		body.setMassFromShapes();

		// Give it a random initial velocity (and angular velocity)
		//body.setLinearVelocity(new Vec2(parent.random(-10f,10f),parent.random(5f,10f)));
		//body.setAngularVelocity(random(-10,10));
	}
}
