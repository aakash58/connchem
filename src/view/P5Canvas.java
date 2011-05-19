package view;

import java.util.ArrayList;

import pbox2d.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.*;
import static model.State.*;

import p5.Area;

public class P5Canvas extends Area {

	// A reference to our box2d world
	PBox2D box2d;
	
	public void setup() {
		smooth();
		setW(600);
		setH(600);
		setDimensions(0, 0, w(), h());  // this is a custom function from the Region interface, implemented in the Area class
		size(600, 600);
		
		// Initialize box2d physics and create the world
		box2d = new PBox2D(this);
		box2d.createWorld();
		box2d.setGravity(0f,-10f);
		// Turn on collision listening!
		// TODO turn on collisions by un-commenting below
		//box2d.listenForCollisions();

		
		// Add a bunch of fixed boundaries
		float bW = 1; // boundary width
		
		Boundary lBound = new Boundary(x(), mh(), bW, h(), box2d, this);
		Boundary rBound = new Boundary(r(), mh(), bW, h(), box2d, this);
		Boundary tBound = new Boundary(mw(), y(), w(), bW, box2d, this);
		Boundary bBound = new Boundary(mw(), b(), w(), bW, box2d, this);

		boundaries[0]=lBound;
		boundaries[1]=rBound;
		boundaries[2]=tBound;
		boundaries[3]=bBound;
		
	}

	public void draw() {
		drawBackground();
		
		// We must always step through time!
		box2d.step();
		
		//if (w!=width || h!=height){
		//	setup(width, height);
		//}
		
		
		// Display all molecules
		for (int i = 0; i < molecules.size(); i++) {
			Molecule m = molecules.get(i);
			m.display();
		}
		// boundaries are not displayed.  If they should be, use a display method in the Boundary class.
		// System.out.println("x: " + str(boundaries.get(2).x()) +" y: " + str(boundaries.get(2).y()) + " w: " + str(boundaries.get(2).w()) + "  h: " + str(boundaries.get(2).h()) );
		
	}
	
	/*
	 * Background methods
	 */
	
	private void drawBackground() { // draw background
		pushStyle();
		fill(63, 63, 127);
		rect(0, 0, width, height);
		popStyle();
	}
	
	/*
	 * Function to create compounds from outside the PApplet
	 */
	
	public void addMolecule(String compoundName) {
		float x_ = 200;
		float y_ = random(200, y());
		molecules.add(new Molecule(x_, y_,compoundName, box2d, this));
	}
	
	public void addMolecule() {
		addMolecule("Water");
	}
	
	public void addMolecule(float x_, float y_) {
		addMolecule(x_, y_, "Water");
	}
	
	public void addMolecule(float x_, float y_, String compoundName) {
		molecules.add(new Molecule(x_,y_,compoundName, box2d, this));
	}
	
	public void mousePressed() {
		addMolecule(mouseX, mouseY);
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
		if (c1.contains("Boundary") && ((Boundary)o1).equals(boundaries[0])) {
			Molecule p = (Molecule) o2;
		} 
		// If object 2 is a Box, then object 1 must be a particle
		else if (c2.contains("Boundary")&& ((Boundary)o2).equals(boundaries[0])) {
			Molecule p = (Molecule) o1;
		}
	}
}
