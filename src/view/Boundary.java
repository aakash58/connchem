package view;

import pbox2d.*;

import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.dynamics.*;

public class Boundary {

	private P5Canvas parent;
	// But we also have to make a body for box2d to know about it
	Body body;
	PBox2D box2d;
	private float x;
	private float y;
	private float w;
	private float h;
	private float box2dW;
	private float box2dH;
	
	Boundary(float x_,float y_, float w_, float h_, PBox2D box2d_, P5Canvas parent_) {
		this.parent = parent_;
		this.box2d = box2d_;
		x=x_;
		y=y_;
		w = w_;
		h = h_;
	
		
		// Figure out the box2d coordinates
		box2dW = box2d.scalarPixelsToWorld(w_/2);
		box2dH = box2d.scalarPixelsToWorld(h_/2);
		
		// Define the polygon
		PolygonDef sd = new PolygonDef();
		sd.setAsBox(box2dW, box2dH);
		sd.density = 0;    // No density means it won't move!
		sd.friction = 0.0f;

		// Create the body
		BodyDef bd = new BodyDef();
		bd.position.set(box2d.coordPixelsToWorld(new Vec2(x_,y_)));
		body = box2d.createBody(bd);
		body.createShape(sd);
		body.setUserData(this);
	}
	
	void display() {
	//	parent.fill(255,0,0);
		parent.fill(parent.heatRGB);
		parent.noStroke();
		parent.rectMode(parent.CENTER);
		parent.rect(x, y, w, h);
 		//parent.rectMode(parent.CORNER);
		parent.rectMode(parent.CORNER);
		
	}

	public void killBody() {
		box2d.destroyBody(body);
	}
}
