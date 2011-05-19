package view;

import java.util.ArrayList;

import processing.core.*;
import processing.xml.XMLElement;
import pbox2d.*;

import model.ResourceReader;

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
	String shapeString;
	PShapeSVG moleculeShape;
	
	ArrayList<PVector> vertices;

	Molecule(float x_, float y_, String compoundName_, PBox2D box2d_, P5Canvas parent_) {
		this.parent = parent_;
		this.box2d = box2d_;
		compoundName = compoundName_;
		
		vertices = new ArrayList<PVector>();
		
		ResourceReader reader = new ResourceReader("resources/compoundsSvg/Water.svg");
		shapeString = reader.read();
		
		XMLElement svg = new XMLElement();
		svg.parse(shapeString);
		System.out.println("the content is: " + svg.getContent());
		//moleculeShape = new PShapeSVG(svg);
		
		// TODO this needs to be converted to read "shapeString", but I don't know how to make it read a string rather than a file.  Exported Jars will break!
		moleculeShape = (PShapeSVG)parent.loadShape("/resources/compoundsSvg/Water.svg");
		
		PShape outline = moleculeShape.findChild("outline");
		System.out.println(outline);
		
		PVector v1 = new PVector(-30, 25);
		PVector v2 = new PVector(10,15);
		PVector v3 = new PVector(15,5);
		PVector v4 = new PVector(30,-15);
		PVector v5 = new PVector(-10,-20);
		
		vertices.add(v1);
		vertices.add(v2);
		vertices.add(v3);
		vertices.add(v4);
		vertices.add(v5);
		
		// This function puts the particle in the Box2d world
		makeBody(new Vec2(x_,y_));
		
		body.setUserData(this);
		

		
		//svg.setContent(shapeString);
		
		//PShapeSVG theShape = new PShapeSVG(svg);
		
		
		
		

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
			
			// this is for the ellipse
			//parent.rect(bodysize/-2, bodysize/-2, bodysize, bodysize);
			
			parent.beginShape();
			
			for (int i = 0; i<vertices.size(); i++) {
				parent.vertex(vertices.get(i).x, vertices.get(i).y);
			}
			
			parent.endShape();

			float moleculeShapeW = moleculeShape.width;
			float moleculeShapeH = moleculeShape.height;
			parent.shape(moleculeShape, moleculeShapeW/-2, moleculeShapeH/-2, moleculeShapeW, moleculeShapeH);
			
			parent.ellipseMode(parent.CENTER);
			parent.fill(200);
			parent.stroke(200);
			parent.ellipse(0, 0, 5, 5);
			parent.line(0,0,10,0);

		parent.popMatrix();
		parent.popStyle();
	}

	// Here's our function that adds the particle to the Box2D world
	void makeBody(Vec2 center_) {
		// Define the body and make it from the shape
		BodyDef bd = new BodyDef();
		bd.position = box2d.coordPixelsToWorld(center_);
		body = box2d.world.createBody(bd);

/*		// Make the body's shape a circle
		CircleDef cd = new CircleDef();
		cd.radius = box2d.scalarPixelsToWorld(bodysize);
		cd.density = 1.0f;
		cd.friction = 0.0f;
		cd.restitution = 1.0f; // Restitution is bounciness
		body.createShape(cd); */
		
/* 		// I'm gonna try a square.
		PolygonDef sd = new PolygonDef();
		float boxW = box2d.scalarPixelsToWorld(bodysize/2);
		float boxH = box2d.scalarPixelsToWorld(bodysize/2);
		sd.setAsBox(boxW, boxH);
		sd.density = 1.0f;
		sd.friction = 0.0f;
		sd.restitution = 1.0f;
		body.createShape(sd); */
		
		// Now trying an arbitrary polygon
	    // Define a polygon (this is what we use for a rectangle)
	    PolygonDef sd = new PolygonDef();
	    
	    for (int i = 0; i < vertices.size();i++) {
	    	sd.addVertex(box2d.vectorPixelsToWorld(new Vec2(vertices.get(i).x,vertices.get(i).y)));
	    }

	    // Parameters that affect physics
	    sd.density = 1.0f;
	    sd.friction = 0.0f;
	    sd.restitution = 1.0f;

	    body = box2d.createBody(bd);
	    body.createShape(sd);
		

		// Always do this at the end
		body.setMassFromShapes();

		// Give it a random initial velocity (and angular velocity)
		//body.setLinearVelocity(new Vec2(parent.random(-10f,10f),parent.random(5f,10f)));
		//body.setAngularVelocity(random(-10,10));
	}
}
