package view;

import java.awt.Color;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

public class Boundary {

	private P5Canvas p5Canvas;
	// But we also have to make a body for box2d to know about it
	public Body body;
	PBox2D box2d;
	private float x;
	private float y;
	private float w;
	private float h;
	private float box2dW;
	private float box2dH;
	private int id =-1;
	private int volumeSliderValue;
	private int volumeSliderDefaultValue;
	private float yOriginal=0; //Original y of body when created
	public static float difVolume; //Increase or Decrease in Volume
	public static boolean isTransformed =false; //Increase or Decrease in Volume
	
	
	Boundary(int id_,float x_,float y_, float w_, float h_, int sliderValue_, PBox2D box2d_, P5Canvas parent_) {
		id = id_;
		this.p5Canvas = parent_;
		this.box2d = box2d_;
		x=x_;
		y=y_;
		w = w_;
		h = h_;
		volumeSliderValue =sliderValue_;
		volumeSliderDefaultValue =p5Canvas.defaultVolume;
		// Figure out the box2d coordinates
		box2dW = box2d.scalarPixelsToWorld(w_/2);
		box2dH = box2d.scalarPixelsToWorld(h_/2);
		
		// Create the body
		
		// Define the polygon
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox(box2dW, box2dH);
		
		BodyDef bd = new BodyDef();
		bd.type = BodyType.STATIC;
        bd.position.set(box2d.coordPixelsToWorld(new Vec2(x_,y_)));
		body = box2d.createBody(bd);
		while (body ==null){ 
			body = box2d.createBody(bd);
		}	
		
		FixtureDef fd = new FixtureDef();
        fd.shape = polygonShape;
    	fd.density = 0f;    // No density means it won't move!
        fd.friction = 1.0f;
    	fd.restitution =1f;
        body.createFixture(fd);
        
	//	body.setMassFromShapes();
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
		
	public void set(int v){
			volumeSliderValue = v;
			difVolume = (volumeSliderValue-volumeSliderDefaultValue)*p5Canvas.multiplierVolume;
			isTransformed =true;
	}
	
	
	void display() {
		float a = body.getAngle();
		// parent.rectMode(parent.CENTER);
		Vec2 pos = box2d.getBodyPixelCoord(body);
		p5Canvas.pushMatrix();
		p5Canvas.translate(pos.x, pos.y);
		p5Canvas.rotate(-a);
		float pShapeW =w;
		float pShapeH =h;
	
		if (id==2 && isTransformed){
			Vec2 v = new Vec2(body.getPosition().x, yOriginal + 
					box2d.scalarPixelsToWorld(difVolume));
			body.setTransform(v, body.getAngle());
			isTransformed =false;
		}	
		if (id==3)
			p5Canvas.fill(p5Canvas.heatRGB);
		else{
			p5Canvas.fill(Color.WHITE.getRGB());
		}	
		p5Canvas.noStroke();
		p5Canvas.rect(pShapeW/-2 , pShapeH/-2 , pShapeW , pShapeH);
		
		p5Canvas.popMatrix();
		
		
		//if (id==2)
		//	parent.rect(x, y-difVolume, w, h);
		
	 	
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
		xx = xx*p5Canvas.scale;
		yy = yy*p5Canvas.scale;
		if (xx<=x_ && x_<xx+w && yy<y_ && y_<yy+h){
			return id;
		}
		else 
			return -1;
	}
		
	public void killBody() {
		box2d.destroyBody(body);
		body.m_world =null;
	}
}
