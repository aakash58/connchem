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
	PBox2D box2d;
	Body body = new Body();
	PShape pShape = new PShape();
	PShape outline = pShape.findChild("outline");
	float pShapeW = 0f;
	float pShapeH = 0f;
	
	ArrayList<Vec2> vertices = new ArrayList<Vec2>();
	
	String compoundName = new String();
	Vec2 pos = new Vec2();
	float angle = 0f;
	int color = 0;
	
	PolygonDef sd = new PolygonDef();
	BodyDef bd = new BodyDef();

	Molecule(float x_, float y_, String compoundName_, PBox2D box2d_, P5Canvas parent_) {
		molecules.add(this);
		parent = parent_;
		box2d = box2d_;
		
		compoundName = compoundName_;
		
		pShape = parent.loadShape("resources/compoundsSvg/Water.svg");
		pShapeW = pShape.width;
		pShapeH = pShape.height;
		
		pos.x = x_;
		pos.y = y_;
		
		PShape outline = pShape.findChild("outline");
		
		for (int i = 0; i<outline.getChild(0).getVertexCount(); i++) {
			Vec2 vertex = new Vec2(outline.getChild(0).getVertexX(i), outline.getChild(0).getVertexY(i));
			vertices.add(vertex);
		}
		
		for (int i = 0; i<vertices.size(); i++) {
			System.out.println(vertices.get(i).x + " " + vertices.get(i).y);
		}

		makeBody(new Vec2(pos.x,pos.y));
	}

	void killBody() {
		box2d.destroyBody(body);
		molecules.remove(this);
	}

	void makeBody(Vec2 center_) {
		float box2dW = box2d.scalarPixelsToWorld(pShapeW/2);
	    float box2dH = box2d.scalarPixelsToWorld(pShapeH/2);
	    sd.setAsBox(box2dW, box2dH);
	 
	    sd.density = 1.0f;
	    sd.friction = 0.0f;
	    sd.restitution = 0.5f;
		
	    bd.position.set(box2d.coordPixelsToWorld(center_));
	 
	    body = box2d.createBody(bd);
	    body.createShape(sd);
	    body.setMassFromShapes();
	    
	    body.setLinearVelocity(new Vec2(parent.random(-5,5), parent.random(2,5)));
	    body.setAngularVelocity(parent.random(-5,5));
	    
	   
		////////
	    /* 
	    float box2dW = box2d.scalarPixelsToWorld(pShapeW/2);
		float box2dH = box2d.scalarPixelsToWorld(pShapeH/2);
		//sd.setAsBox(box2dW, box2dH);
		
		for (int i = 0; i < vertices.size();i++) {
			sd.addVertex(box2d.vectorPixelsToWorld(box2d.coordPixelsToWorld(new Vec2(vertices.get(i).x/-2,vertices.get(i).y/-2))));
    	}
		
		sd.density = 1.0f;
		sd.friction = 0.0f;
		sd.restitution = 1.0f;
		
		bd.position.set(box2d.coordPixelsToWorld(center_));
		//body = box2d.world.createBody(bd);

		body = box2d.createBody(bd);
		body.createShape(sd);
		body.setMassFromShapes();
		*/
		
	}
	
	void display() {
		parent.pushStyle();
		parent.pushMatrix();
			pos = box2d.getBodyPixelCoord(body);
			angle = body.getAngle();
		 	if (pos!=null){
		 		parent.translate(pos.x, pos.y);
		 		parent.rotate(angle * -1); // TODO not sure why rotate requires negative value
		 		
		 		// main p5 pic
		 		parent.shape(pShape, pShapeW/-2, pShapeH/-2, pShapeW, pShapeH); // second two args center for p5
		 		
		 		// background shape which ostensibly matches pbox
		 		parent.noFill();
		 		parent.stroke(127, 50);
		 		parent.rect(pShapeW * -1/2, pShapeH * -1/2, pShapeW, pShapeH);
		 		parent.beginShape();
	 			for (int i = 0; i<vertices.size(); i++) {
	 				Vec2 vec2 = vertices.get(i);
	 				parent.vertex(vec2.x, vec2.y);
	 			}
		 		parent.endShape();
		 	
		 		// center indicator
		 		parent.ellipseMode(parent.CENTER);
		 		parent.fill(200);
		 		parent.stroke(200);
		 		parent.ellipse(0, 0, 5, 5);
		 		parent.line(0,0,10,0);
		 	}
		parent.popMatrix();
		parent.popStyle();
	}
}
