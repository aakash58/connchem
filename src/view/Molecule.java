package view;

import java.awt.Color;
import java.util.ArrayList;

import processing.core.*;
import pbox2d.*;

import main.Canvas;
import main.Main;
import model.DBinterface;

import org.jbox2d.common.*;
import org.jbox2d.collision.CircleDef;
import org.jbox2d.collision.CircleShape;
import org.jbox2d.collision.Shape;
import org.jbox2d.dynamics.*;

import Util.SVGReader;

public class Molecule {
	// We need to keep track of a Body and a width and height
	private Body body;
	private PBox2D box2d;
	private P5Canvas parent;
	private PShape pShape = new PShape();
	private float pShapeW = 0f;
	private float pShapeH = 0f;
	private float[][] circles;
	private ArrayList<String> elementNames;
	private String name;
	public float freezingTem;
	public float boilingTem;
	public float fric;
	public float res;
	private float scale = 1;

	private float xTmp;
	private float yTmp;
	private float minSize;
	public boolean polarity;
	public Vec2 off;

	// Constructor
	Molecule(float x, float y, String compoundName_, PBox2D box2d_,
			P5Canvas parent_) {
		parent = parent_;
		box2d = box2d_;
		name = compoundName_;

		String path = "resources/compoundsSvg/" + compoundName_ + ".svg";
		pShape = parent.loadShape(path);
		pShapeW = pShape.width;
		pShapeH = pShape.height;
		minSize = Math.min(pShapeW, pShapeH);

		polarity = parent.db.getCompoundPolarity(compoundName_);

		circles = SVGReader.getSVG(path);
		elementNames = SVGReader.getNames();

		freezingTem = P5Canvas.db.getCompoundFreezingPointCelsius(name);
		boilingTem = P5Canvas.db.getCompoundBoilingPointCelsius(name);
		float temp = P5Canvas.temp;
		res = (temp - freezingTem) / (boilingTem - freezingTem);
		if (res > 0) {
			res = 1f;
		} else
			res = 0.5f;

		if (temp <= freezingTem)
			fric = 1;
		else
			fric = 0;

		if (name.equals("Water") && temp < 40)
			scale = 1 + (40 - temp) / 200f;
		else
			scale = 1.05f;
		
		if (name.equals("Mercury")) {
		} else if (name.equals("Pentane")) {
			res = 0.7f;
		}
		
		createBody(x, y);
	}

	public void createBody(float x, float y) {
		// Define the body and make it from the shape
		BodyDef bd = new BodyDef();
		bd.position.set(box2d.coordPixelsToWorld(new Vec2(x, y)));

		// This infinitive loop fix nullPointerException because
		// box2d.createBody(bd) may create a null body
		body = box2d.createBody(bd);
		while (body == null) {
			body = box2d.createBody(bd);
		}

		float mul = 1;
		if (name.equals("Pentane"))
			mul = mul * 0.15f;
		else if (name.equals("Bromine"))
			mul = mul * 0.50f;
		else if (name.equals("Mercury"))
			mul = mul * 1.0f;
		else if (name.equals("Sodium"))
			mul = mul * 10.0f;

		for (int i = 0; i < circles.length; i++) {
			// Define a circle
			CircleDef cd = new CircleDef();
			// Offset its "local position" (relative to 0,0)
			Vec2 offset = new Vec2(circles[i][1] - pShapeW / 2, circles[i][2]
					- pShapeH / 2);
			cd.localPosition = box2d.vectorPixelsToWorld(offset);
			cd.radius = box2d.scalarPixelsToWorld(circles[i][0]) * scale;
			float m = 1;
			if (elementNames != null && i < elementNames.size())
				m = DBinterface.getElementMass(elementNames.get(i));

			float d = m / (circles[i][0] * circles[i][0] * circles[i][0]);
			cd.density = d * mul;
			cd.friction = fric;
			cd.restitution = res;
			// Attach shapes!
			body.createShape(cd);
		}
		body.setMassFromShapes();

		// System.out.println(name+ " get Mass "+body.getMass() +" DBmass:"+
		// +DBinterface.getCompoundMass(name));
		// Give it some initial random velocity
		body.setLinearVelocity(new Vec2(parent.random(-1, 1), parent.random(-1,
				1)));
		body.setAngularVelocity(0);
		body.setUserData(this);

	}

	public Vec2 getLinearVelocity() {
		return body.getLinearVelocity();
	}

	public void setLinearVelocity(Vec2 v) {
		body.setLinearVelocity(v);
	}

	public int getNumElement() {
		return circles.length;
	}

	public String getName() {
		return name;
	}

	public float getMass() {
		return body.getMass();
	}

	public Vec2 getPosition() {
		return body.getPosition();
	}


	public void setRestitution(float r) {
		Shape s = body.getShapeList();
		for (int i = 0; i < circles.length; i++) {
			s.setRestitution(r);
			s = s.getNext();
		}
	}

	public void setFriction(float r) {
		Shape s = body.getShapeList();
		for (int i = 0; i < circles.length; i++) {
			s.setFriction(r);
			s = s.getNext();
		}
	}

	public void setRadius(float scale) {
		Shape s = body.getShapeList();
		for (int i = 0; i < circles.length; i++) {
			CircleShape cs = (CircleShape) s;
			cs.m_radius = box2d.scalarPixelsToWorld(circles[i][0]) * scale;
			s = s.getNext();
		}
	}

	public void addForce(Vec2 f) {
		Vec2 pos = body.getPosition();
		// Vec2 offset = new Vec2(circles[2][1]-pShapeW/2,
		// circles[2][2]-pShapeH/2);
		// pos = pos.add(box2d.vectorPixelsToWorld(offset));
		body.applyForce(f, pos);
	}

	public void display() {
		/*
		 * if (P5Canvas.temp>100){ Vec2 v = body.getLinearVelocity(); v = new
		 * Vec2(v.x*10000000,v.y*10000000); body.setLinearVelocity(v); }
		 */
		if (P5Canvas.isDrag && P5Canvas.draggingBoundary < 0) {
			float xx = xTmp + box2d.scalarPixelsToWorld(P5Canvas.xDrag);
			float yy = yTmp - box2d.scalarPixelsToWorld(P5Canvas.yDrag);
			Vec2 v = new Vec2(xx, yy);
			body.setXForm(v, body.getAngle());
			body.setAngularVelocity(0);
		} else {
			xTmp = body.getPosition().x;
			yTmp = body.getPosition().y;
		}

		float t = P5Canvas.h - P5Canvas.y;
		float yy = box2d.scalarWorldToPixels(body.getPosition().y);

		if (yy > t - minSize / 3 + Boundary.difVolume) {
			Vec2 v = new Vec2(body.getPosition().x, box2d.scalarPixelsToWorld(t
					- minSize + Boundary.difVolume));
			if (body != null && v != null)
				body.setXForm(v, body.getAngle());
		}

		// We look at each body and get its screen position
		Vec2 pos = box2d.getBodyPixelCoord(body);
		// Get its angle of rotation

		float a = body.getAngle();
		// parent.rectMode(parent.CENTER);
		parent.pushMatrix();
		parent.translate(pos.x, pos.y);
		parent.rotate(-a);
		parent.shape(pShape, pShapeW / -2, pShapeH / -2, pShapeW, pShapeH); // second
																			// two
																			// args
																			// center
																			// for
																			// p5
		parent.noFill();

		if (name.equals(Canvas.getSelecttedmolecule())) {
			parent.stroke(0);
			Color c = Canvas.getSelecttedColor();
			parent.stroke(c.getRGB(), c.getAlpha());
			int margin = 5;
			parent.rect(pShapeW / -2 - margin, pShapeH / -2 - margin, pShapeW
					+ 2 * margin, pShapeH + 2 * margin);
		}
		parent.popMatrix();
	}

	public void display2() {

		// We look at each body and get its screen position
		Vec2 pos = box2d.getBodyPixelCoord(body);
		// Get its angle of rotation

		float a = body.getAngle();
		// parent.rectMode(parent.CENTER);
		parent.pushMatrix();
		parent.translate(pos.x, pos.y);
		parent.rotate(-a);

		for (int i = 150; i > 1; i = i - 5) {
			Color col = new Color(255, 0, 0, (150 - i) / 8);
			parent.fill(col.getRGB());// .noFill();
			parent.ellipse(0, 0, i * 2, i * 2);

		}
		parent.popMatrix();

	}

	// This function removes the particle from the box2d world
	public void killBody() {
		box2d.destroyBody(body);
		body.m_world = null;
	}
}
