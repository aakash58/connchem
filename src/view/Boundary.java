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
	private float xCurrent;
	private float yCurrent;
	private float curDragX=0;
	private float curDragY=0;
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
		sd.friction = 0.0f;

		// Create the body
		BodyDef bd = new BodyDef();
		bd.position.set(box2d.coordPixelsToWorld(new Vec2(x_,y_)));
		body = box2d.createBody(bd);
		body.createShape(sd);
		body.setUserData(this);
	}
	
	public float getCurrentX(){
		return xCurrent;
	}
	public float getCurrentY(){
		return yCurrent;
	}
	
	void display() {
		parent.rectMode(parent.CENTER);
		if (P5Canvas.isDrag || P5Canvas.draggingBoundary==id){
			float difX = P5Canvas.xDrag -curDragX;
			float difY = P5Canvas.yDrag -curDragY;
			xCurrent += difX;
			yCurrent += difY;
			float x1 = body.getPosition().x + box2d.scalarPixelsToWorld(difX);
			float y1 = body.getPosition().y - box2d.scalarPixelsToWorld(difY);
			Vec2 v = new Vec2(x1, y1);
			body.setXForm(v, body.getAngle());
			
			//Move by Scale
			parent.fill(parent.heatRGB);
			parent.noStroke();
			parent.rect(x+xCurrent, y+yCurrent, w, h);
	 		parent.rectMode(parent.CORNER);	
	 		curDragX = P5Canvas.xDrag;
			curDragY = P5Canvas.yDrag;
		}
		else{
			curDragX =0;
			curDragY =0;
			
			parent.fill(parent.heatRGB);
			parent.noStroke();
			parent.rect(x+xCurrent, y+yCurrent, w, h);
	 		parent.rectMode(parent.CORNER);	
	 		curDragX = P5Canvas.xDrag;
	 		curDragY = P5Canvas.yDrag;
		}

		
		
	}

	public int isIn(float x_, float y_) {
		float xx=0, yy=0;
		if(id==0){
			xx=x; 	
			yy=y-h/2;
		}
		else if(id==1){
			xx=x-w; 	
			yy=y-h/2;
		}
		else if(id==2){
			xx=x-w/2; 	
			yy=y;
		}
		else if(id==3){
			xx=x-w/2; 	
			yy=y-h;
		}
		xx += xCurrent;
		yy += yCurrent;
		xx = xx*P5Canvas.scale;
		yy = yy*P5Canvas.scale;
		System.out.println("xCurrent:"+xCurrent+" "+x_);
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
