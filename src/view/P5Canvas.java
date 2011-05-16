package view;

import java.util.ArrayList;

import pbox2d.*;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.*;
import static model.State.*;

import p5.Area;

public class P5Canvas extends Area {

	// A reference to our box2d world
	PBox2D box2d;

	public void setup() {
		//smooth();
		size(200, 200);
		setDimensions(0, 0, width, height);
		
		// Initialize box2d physics and create the world
		box2d = new PBox2D(this);
		box2d.createWorld();
		box2d.setGravity(0f,-100f);
		// Turn on collision listening!
		// TODO turn on collisions by uncommenting below
		//box2d.listenForCollisions();

		// Add a bunch of fixed boundaries
		
		float bW = 10; // boundary width
		
		Boundary lBound = new Boundary(x(), mh(), bW, h(), box2d, this);
		Boundary rBound = new Boundary(r(), mh(), bW, h(), box2d, this);
		Boundary tBound = new Boundary(mw(), y(), w(), bW, box2d, this);
		Boundary bBound = new Boundary(mw(), b(), w(), bW, box2d, this);

		boundaries.add(lBound);
		boundaries.add(rBound);
		boundaries.add(tBound);
		boundaries.add(bBound);

	}

	public void draw() {
		// We must always step through time!
		box2d.step();

		// Display all molecules
		for (int i = 0; i < molecules.size(); i++) {
			Molecule m = molecules.get(i);
			m.display();
		}
		// Display all boundaries
		for (int i = 0; i < boundaries.size(); i++) {
			Boundary b = boundaries.get(i);
			b.display();
		}
		
		//System.out.println("x: " + str(boundaries.get(2).x()) +" y: " + str(boundaries.get(2).y()) + " w: " + str(boundaries.get(2).w()) + "  h: " + str(boundaries.get(2).h()) );
	}
	
	/*
	 * Function to create compounds from outside the PApplet
	 */
	public void addMolecule(String compoundName) {
		float x_ = random(0, x());
		float y_ = random(0, y());
		molecules.add(new Molecule(x_, y_,compoundName, box2d, this));
	}
	
	public void addMolecule(float x_, float y_, String compoundName) {
		molecules.add(new Molecule(x_,y_,compoundName, box2d, this));
	}
	
	public void mousePressed() {
		addMolecule(mouseX, mouseY, "Default");
	}
	// Collision event functions!
	public void addContact(ContactPoint cp) {
		// Get both shapes
		Shape s1 = cp.shape1;
		Shape s2 = cp.shape2;
		// Get both bodies
		Body b1 = s1.getBody();
		Body b2 = s2.getBody();
		// Get our objects that reference these bodies
		Object o1 = b1.getUserData();
		Object o2 = b2.getUserData();

		// What class are they?  Box or Particle?
		String c1 = o1.getClass().getName();
		String c2 = o2.getClass().getName();
		// If object 1 is a Box, then object 2 must be a particle
		// Note we are ignoring particle on particle collisions
		if (c1.contains("Boundary") && ((Boundary)o1).equals(boundaries.get(0))) {
			Molecule p = (Molecule) o2;
		} 
		// If object 2 is a Box, then object 1 must be a particle
		else if (c2.contains("Boundary")&& ((Boundary)o2).equals(boundaries.get(0))) {
			Molecule p = (Molecule) o1;
		}
	}
}
