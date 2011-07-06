package view;

import java.awt.Color;

import pbox2d.*;

import org.jbox2d.collision.PolygonDef;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

public class Boundary2 {

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
	private float yOriginal=0; //Original y of body when created
	public static float difVolume; //Increase or Decrease in Volume
	public static boolean isTransformed =false; //Increase or Decrease in Volume
	
	
	Boundary2(int id_,float x_,float y_, float w_, float h_,  PBox2D box2d_, P5Canvas parent_) {
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
		
		// Create the body
		BodyDef bd = new BodyDef();
		bd.position.set(box2d.coordPixelsToWorld(new Vec2(x_,y_)));
		body = box2d.createBody(bd);
		while (body ==null){ 
			body = box2d.createBody(bd);
		}	
		
		// Define the polygon
		PolygonDef sd = new PolygonDef();
		sd.setAsBox(box2dW, box2dH);
		sd.density = 0.02f;    // No density means it won't move!
		sd.friction = 1f;
		sd.restitution =0.8f;
		body.createShape(sd);
		body.setMassFromShapes();
		
		body.setUserData(this);
		yOriginal = body.getPosition().y ;
		isTransformed =true;
	}
	public float getId(){
		return id;
	}
	
	public float getX(){
		return x;
	}
	public float getY(){
		return y;
	}
		

	
	
	void display() {
		float a = body.getAngle();
		Vec2 pos = box2d.getBodyPixelCoord(body);
		parent.pushMatrix();
		parent.translate(pos.x, pos.y);
		parent.rotate(-a);
		float pShapeW =w;
		float pShapeH =h;
		parent.fill(Color.GRAY.getRGB());
		if (id>=1000)
			parent.fill(Color.ORANGE.getRGB());
		
			
	/*	if (id==1002){
			for (int i=250;i>1;i=i-10){
	 			Color col = new Color(255,0,0,(250-i)/10);
	 			parent.fill(col.getRGB());//.noFill();
				parent.ellipse(0, 324, i*4, i*4);
		 				
			}
		}*/
		
		parent.noStroke();
		parent.rect(pShapeW/-2 , pShapeH/-2 , pShapeW , pShapeH);
		parent.popMatrix();
		
	}

	
	public void killBody() {
		box2d.destroyBody(body);
		body.m_world =null;
	}
}
