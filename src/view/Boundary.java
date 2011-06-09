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
	private int id =-1;
	
	
	Boundary(int id_,float x_,float y_, float w_, float h_, PBox2D box2d_, P5Canvas parent_) {
		id = id_;
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
		sd.friction = 1.0f;

		// Create the body
		BodyDef bd = new BodyDef();
		bd.position.set(box2d.coordPixelsToWorld(new Vec2(x_,y_)));
		body = box2d.createBody(bd);
		body.createShape(sd);
		body.setUserData(this);
	}
	
	
	void display() {
		parent.rectMode(parent.CENTER);
		
		
		
		parent.fill(parent.heatRGB);
			parent.noStroke();
			parent.rect(x, y, w, h);
	 		parent.rectMode(parent.CORNER);	
	}

	public int isIn(float x_, float y_) {
		float xx=0, yy=0;
		if(id==0){
			xx=x-w/2; 	
			yy=y-h/2;
		}
		else if(id==1){
			xx=x-w/2; 	
			yy=y-h/2;
		}
		else if(id==2){
			xx=x-w/2; 	
			yy=y-h/2;
		}
		else if(id==3){
			xx=x-w/2; 	
			yy=y-h/2;
		}
		xx = xx*P5Canvas.scale;
		yy = yy*P5Canvas.scale;
		if (xx<=x_ && x_<xx+w && yy<y_ && y_<yy+h){
			return id;
		}
		else 
			return -1;
	}
		
	public void killBody() {
		box2d.destroyBody(body);
	}
}
