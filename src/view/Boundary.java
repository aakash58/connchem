package view;

import processing.core.*;
import p5.Area;
import pbox2d.*;
import p5.Region;

import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.dynamics.*;

public class Boundary extends Area{

	private Area parent;
	// But we also have to make a body for box2d to know about it
	Body b;
	PBox2D box2d;

	Boundary(float x_,float y_, float w_, float h_, PBox2D box2d_, Area parent_) {
		this.parent = parent_;
		this.box2d = box2d_;
		setDimensions(x_, y_, w_, h_);

		// Figure out the box2d coordinates
		float box2dW = box2d.scalarPixelsToWorld(w()/2);
		float box2dH = box2d.scalarPixelsToWorld(h()/2);
		Vec2 center = new Vec2(x(),y());

		// Define the polygon
		PolygonDef sd = new PolygonDef();
		sd.setAsBox(box2dW, box2dH);
		sd.density = 0;    // No density means it won't move!
		sd.friction = 0.0f;

		// Create the body
		BodyDef bd = new BodyDef();
		bd.position.set(box2d.coordPixelsToWorld(center));
		b = box2d.createBody(bd);
		b.createShape(sd);
		b.setUserData(this);
	}
}
