package simulations;

import static data.State.molecules;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import java.text.DecimalFormat;

import javax.swing.Timer;

import main.Main;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.JointType;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.PrismaticJointDef;

import data.DBinterface;

import simulations.models.Boundary;
import simulations.models.Compound;
import simulations.models.DistanceJointWrap;
import simulations.models.Molecule;
import simulations.models.Water;

import static simulations.P5Canvas.*;

public class Unit2 extends UnitBase{
	private int num_total = 0;
	private int num_gone = 0; // Number of molecules that has dissolved
	private int numWater = 0; // Number of water added to container
	private float massDissolved = 0;
	private int water100mL = 25;
	private int mToMass = 10;

	private Water waterComputation;
	//private int addWaterDelay = 8000; //Parameter specifies delay of saturation computation after we add water molecules to canvas
	                           //In seconds
	//private ActionListener timerPerformer = null; //ActionListener of timer used to increase numWater


	public Unit2(P5Canvas parent, PBox2D box) {
		super(parent, box);
		unitNum = 2;
		waterComputation = new Water(p5Canvas);

	}



	/******************************************************************
	 * FUNCTION : add2Ions DESCRIPTION : Specific function used to add NaCl2,
	 * Called by addMolecule()
	 * 
	 * INPUTS : ion1(String), ion2(String), count(int),
	 * box2d_(PBox2D),parent_(P5Canvas) OUTPUTS: boolean
	 *******************************************************************/
	public boolean add2Ions(String ion1, String ion2, int count, PBox2D box2d_,
			P5Canvas parent_) {

		boolean res = true;
		int numCol = (int) Math.ceil((float) count / 2); // number of column
		int numRow = (int) Math.ceil((float) count / numCol); // number of row

		if ((float) count / 2 > 2) // We set the max col number is 2
		{
			numCol = 2;
			numRow = (int) Math.ceil((float) count / numCol);

		}
		Vec2 size1 = Molecule.getShapeSize(ion1, parent_);
		Vec2 size2 = Molecule.getShapeSize(ion2, parent_);

		float increX = p5Canvas.w / 3;
		Random rand = new Random(); // Random number used to generate ions in
									// random location
		float centerX = p5Canvas.x + 50; // X coordinate around which we are going to add
								// Ions, 50 is border width
		// centerX += rand.nextFloat()*(w/3*2);
		float centerY = p5Canvas.y + 80 - Boundary.difVolume; // Y coordinate around
														// which we are going to
														// add Ions

		Vec2 topLeft = new Vec2(centerX-0.5f*size1.x, centerY-0.5f*size1.y);
		Vec2 botRight = new Vec2(centerX + numCol * (size1.x+size2.x), centerY + numRow
				* size1.y);

		boolean isClear = false;

		Vec2 molePos = new Vec2(0, 0); // Molecule position parameter
		Vec2 molePosInPix = new Vec2(0, 0);
		
		// Check if there are any molecules in add area. If yes, add molecules
		// to another area.
		while (!isClear) {
			// Specify new add area.
			
			// Reset flag
			isClear = true;

			for (int k = 0; k < molecules.size(); k++) {

				if (!((String) molecules.get(k).getName()).equals("Water")) {
					molePos.set(molecules.get(k).getPosition());
					molePosInPix.set(box2d.coordWorldToPixels(molePos));

			
					if (areaBodyCheck(molePosInPix, topLeft, botRight)) {
						isClear = false;
						break;
					}
				}
			}
			if (!isClear) {
				centerX += increX;
				topLeft = new Vec2(centerX-0.5f*size1.x, centerY-0.5f*size1.y);
				botRight = new Vec2(centerX + numCol * (size1.x+size2.x), centerY + numRow
						* size1.y);
				// If we have gone through all available areas.
				if (centerX > (p5Canvas.x + p5Canvas.w)) {
					isClear = true; // Ready to jump out
					res = false; // Set output bolean flag to false
					// TO DO: Show tooltip on Add button when we cant add more
					// compounds
				}
			}
		}
		if (res) // If there is enough space, add compounds
		{
			for (int i = 0; i < count; i++) {
				float x1, y1, angle1;
				float x2, y2, angle2;
				int r = i % numRow;
				x1 = centerX + (i / numRow) * (size1.x + size2.x);
				x2 = x1 + size1.x;
				if ((r % 2 == 1)) {
					float tmp = x1;
					x1 = x2;
					x2 = tmp;
				}
				y1 = centerY + (i % numRow) * size1.y;
				y2 = y1;
				angle1 = 0;
				angle2 = 0;
				
				molecules.add(new Molecule(x1, y1, ion1, box2d_, parent_,
						angle1));
				molecules.add(new Molecule(x2, y2, ion2, box2d_, parent_,
						angle2));
				int index1 = molecules.size() - 2;
				int index2 = molecules.size() - 1;
				Molecule m1 = molecules.get(index1);
				Molecule m2 = molecules.get(index2);
				joint2Ions(index1, index2, m1, m2);
				res = true;
			}
		}

		return res;
	}

	public void joint2Ions(int index1, int index2, Molecule m1, Molecule m2) { // draw
																				// background
		//DistanceJointDef djd = new DistanceJointDef();
		//djd.bodyA = m1.body;
		//djd.bodyB = m2.body;
		float length = PBox2D.scalarPixelsToWorld(2 * Molecule.clRadius);
		float frequency = 10.0f;
		float dampingRatio = 0.0f;
		//DistanceJoint dj = (DistanceJoint) PBox2D.world.createJoint(djd);
		DistanceJointWrap dj = new DistanceJointWrap( m1.body,m2.body,length, frequency,dampingRatio );
		m1.compoundJ = index2;
		m2.compoundJ = index1;
		m1.compoundJoint.add(dj);
		m2.compoundJoint.add(dj);
	}

	/******************************************************************
	 * FUNCTION : addSiO2 DESCRIPTION : Specific function used to add addSiO2,
	 * Called by addMolecule() The shape of molecule cluster is like a pyramid
	 * INPUTS : CompoundName(String), count(int),
	 * box2d_(PBox2D),parent_(P5Canvas) OUTPUTS: boolean
	 *******************************************************************/
	public boolean addSiO2(String compoundName_, int count, PBox2D box2d_,
			P5Canvas parent_) {

		boolean res = true;
		int numRow = 1;
		int sum = 0;
		for (int i = 1; i <= 6; i++) {
			sum = (numRow * (numRow + 1)) / 2;
			if (sum >= count)
				break;
			else
				numRow++;
		}

		int numCol = numRow;

		Vec2 size1 = Molecule.getShapeSize(compoundName_, parent_);

		float centerX = p5Canvas.x + 50; // X coordinate around which we are going to add
								// Ions, 260 is to make SiO2 spawn in the middle
		float centerY = p5Canvas.y + 80 - Boundary.difVolume; // Y coordinate around
														// which we are going to
														// add Ions
		Vec2 topLeft = new Vec2(centerX, centerY);
		Vec2 botRight = new Vec2();
		boolean isClear = false;

		topLeft.set(centerX, centerY);
		botRight.set(centerX + numCol * size1.x, centerY + numRow * size1.y);
		float increX = p5Canvas.w / 3;
		Vec2 molePos = new Vec2(0, 0);
		Vec2 molePosInPix = new Vec2(0, 0);

		topLeft.set(centerX, centerY);
		botRight.set(centerX + numCol * size1.x, centerY + numRow * size1.y);

		while (!isClear) {
	

			isClear = true;
			for (int k = 0; k < molecules.size(); k++) {

				if (!((String) molecules.get(k).getName()).equals("Water")) {
					molePos.set(molecules.get(k).getPosition());
					molePosInPix.set(box2d.coordWorldToPixels(molePos));

					if (areaBodyCheck(molePosInPix, topLeft, botRight)) {
						isClear = false;
						break;
					}
				}
			}
			if (!isClear) {
				centerX += increX;
				topLeft.set(centerX, centerY);
				botRight.set(centerX + numCol * size1.x, centerY + numRow * size1.y);
				// If we have gone through all available areas.
				if (centerX > (p5Canvas.x + p5Canvas.w)) {
					isClear = true; // Ready to jump out
					res = false; // Set output bolean flag to false
					// TO DO: Show tooltip on Add button when we cant add more
					// compounds
				}
			}
		}

		if (res) {
			int curRow = 1; // row number
			int rowSum = 0; // Max number of compound based on curRow
			int rowSumNext = 1; // Max number of compound based on next Row
			float x = 0;
			float y = 0;
			float angle = 0;
			int midOddCol = 0; // mid column ID of current odd row
			float midEvenCol = 0; // mid column ID of current even row
			int idOnCurRow = 0; // Id of i on current row
			float midX = centerX + numCol * size1.x / 2;
			for (int i = 0; i < count; i++) {

				idOnCurRow = i - rowSum + 1;
				y = centerY + ((float) curRow - 0.5f) * size1.y;

				// Odd row alignment
				if (curRow % 2 != 0) {
					midOddCol = curRow / 2 + 1;
					x = midX + (idOnCurRow - midOddCol) * size1.x;
				} else // Even row alignment
				{
					midEvenCol = (float) curRow / 2 + 0.5f;
					x = midX + (idOnCurRow - midEvenCol) * size1.x;
				}

				if ((i + 1) >= rowSumNext) {
					rowSum = rowSumNext;
					curRow++;
					rowSumNext = (curRow * (curRow + 1)) / 2;
				}

				angle = 0;
				molecules.add(new Molecule(x, y, compoundName_, box2d_,
						parent_, angle));

			}
		}

		return res;
	}

	/******************************************************************
	 * FUNCTION : addCalciumChloride DESCRIPTION : Specific function used to add
	 * CalciumChloride, Called by addMolecule()
	 * 
	 * INPUTS : CompoundName(String), count(int),
	 * box2d_(PBox2D),parent_(P5Canvas) OUTPUTS: boolean
	 *******************************************************************/
	public boolean addCalciumChloride(String compoundName_, int count,
			PBox2D box2d_, P5Canvas parent_) {

		boolean res = true;
		String ion1 = "Chlorine-Ion";
		String ion2 = "Calcium-Ion";
		String ion3 = "Chlorine-Ion";
		Vec2 size1 = Molecule.getShapeSize(ion1, parent_);
		Vec2 size3 = Molecule.getShapeSize(ion3, parent_);

		float centerX = p5Canvas.x + 65; // X coordinate around which we are going to
									// add Ions, 50 is border width
		float centerY = p5Canvas.y + 100 - Boundary.difVolume; // Y coordinate around
														// which we are going to
														// add Ions

		float increX = p5Canvas.w / 3;
		
		
		Vec2 topLeft = new Vec2(0, 0);
		Vec2 botRight = new Vec2(0, 0);

		
		boolean isClear = false;

		Vec2 molePos = new Vec2(0, 0); // Molecule position parameter
		Vec2 molePosInPix = new Vec2(0, 0);

		// Specify new add area.
		setCalciumChlorideArea(count,centerX,centerY,size1,size3,topLeft,botRight);
		// Check if there are any molecules in add area. If yes, add molecules
		// to another area.
		while (!isClear) {
			
			// Reset flag
			isClear = true;

			for (int k = 0; k < molecules.size(); k++) {

				//if (!((String) molecules.get(k).getName()).equals("Water")) {
					molePos.set(molecules.get(k).getPosition());
					molePosInPix.set(box2d.coordWorldToPixels(molePos));

					if (areaBodyCheck(molePosInPix, topLeft, botRight)) {
						isClear = false;
						break;
					}
				//}
			}
			if (!isClear) {
				centerX += increX;
				// Specify new add area.
				setCalciumChlorideArea(count,centerX,centerY,size1,size3,topLeft,botRight);
				// If we have gone through all available areas.
				if (botRight.x > (p5Canvas.x + p5Canvas.w)||topLeft.x<p5Canvas.x) {
					isClear = true; // Ready to jump out
					res = false; // Set output bolean flag to false
					// TO DO: Show tooltip on Add button when we cant add more
					// compounds
				}
			}
		}

		if (res) {

			for (int i = 0; i < count; i++) {
				float x1, y1, angle1;
				float x2, y2, angle2;
				float x3, y3, angle3;
				x1 = centerX + (i % 2) * (size1.x * 3);
				x2 = x1 + size1.x;
				x3 = x1 + size1.x + size3.x;
				y1 = centerY + (i / 2) * 2 * size1.y;
				y2 = y1;
				y3 = y1;
				angle1 = 0;
				angle2 = 0;
				angle3 = 0;
				if (i % 4 == 1) {
					x2 = x1;
					x3 = x1;
					y1 = y1 - size1.x;
					y2 = y1 + size1.x;
					y3 = y2 + size1.x;
				} else if (i % 4 == 2) {
					x1 = x2;
					x3 = x2;
					y1 = y1 - size1.x;
					y2 = y1 + size1.x;
					y3 = y2 + size1.x;
				} else if (i % 4 == 3) {
					x1 = x1 - size1.x;
					x2 = x2 - size1.x;
					x3 = x3 - size1.x;
				}

				molecules.add(new Molecule(x1, y1, ion1, box2d_, parent_,
						angle1));
				molecules.add(new Molecule(x2, y2, ion2, box2d_, parent_,
						angle2));
				molecules.add(new Molecule(x3, y3, ion3, box2d_, parent_,
						angle3));

				int num = molecules.size();
				int index1 = num - 3;
				int index2 = num - 2;
				int index3 = num - 1;
				Molecule m1 = molecules.get(index1);
				Molecule m2 = molecules.get(index2);
				Molecule m3 = molecules.get(index3);
				jointCaCl(index1, index2, index3, m1, m2, m3);
			}
		}
		return res;
	}
	
	/******************************************************************
	 * FUNCTION : setCalciumChlorideArea
	 * DESCRIPTION : Calculate borders of area in which we are going to add CaCl2
	 *               Given CenterX,CenterY , got top left corner and bot right corner
	 * 
	 * INPUTS : count(int), centerX(float), centerY(float), size1(Vec2), size3(Vec2), topLeft(Vec2), botRight(Vec2)
	 * OUTPUTS: None
	 *******************************************************************/
	public void setCalciumChlorideArea(int count, float centerX,float centerY,Vec2 size1,Vec2 size3, Vec2 topLeft, Vec2 botRight)
	{
		switch (count)
		{
		case 1:
			//We make area check bigger for a single molecule
			topLeft.set(centerX-1.0f*size1.x,centerY-1.0f*size1.y);
			botRight.set(centerX+2.0f*size1.x+1.0f*size3.x,centerY+1.0f*size3.y);
			break;
		case 2:
			topLeft.set(centerX-0.5f*size1.x,centerY-1.5f*size1.y);
			botRight.set(centerX+3.0f*size1.x+0.5f*size3.x, centerY + 1.5f*size1.y);
			break;
		case 3:
			topLeft.set(centerX-0.5f*size1.x,centerY-1.5f*size1.y);
			botRight.set(centerX+3.0f*size1.x+0.5f*size3.x, centerY + 3.0f*size1.y+0.5f*size3.y);
			break;
		case 4:
			topLeft.set(centerX-0.5f*size1.x,centerY-1.5f*size1.y);
			botRight.set(centerX+4.0f*size1.x+0.5f*size3.x, centerY + 3.0f*size1.y+0.5f*size3.y);
			break;
		case 5:
			topLeft.set(centerX-0.5f*size1.x,centerY-1.5f*size1.y);
			botRight.set(centerX+4.0f*size1.x+0.5f*size3.x, centerY + 4.0f*size1.y+0.5f*size3.y);
			break;
		case 6:
			topLeft.set(centerX-0.5f*size1.x,centerY-1.5f*size1.y);
			botRight.set(centerX+4.0f*size1.x+0.5f*size3.x, centerY + 5.0f*size1.y+0.5f*size3.y);
			break;
		case 7:
		case 8:
			topLeft.set(centerX-0.5f*size1.x,centerY-1.5f*size1.y);
			botRight.set(centerX+4.0f*size1.x+0.5f*size3.x, centerY + 7.0f*size1.y+0.5f*size3.y);
			break;
		case 9:
			topLeft.set(centerX-0.5f*size1.x,centerY-1.5f*size1.y);
			botRight.set(centerX+4.0f*size1.x+0.5f*size3.x, centerY + 8.0f*size1.y+0.5f*size3.y);
			break;
		}
		
	}

	/******************************************************************
	 * FUNCTION : addNaHCO3 DESCRIPTION : Specific function used to add
	 * addNaHCO3, Called by addMolecule()
	 * 
	 * INPUTS : CompoundName(String), count(int),
	 * box2d_(PBox2D),parent_(P5Canvas) OUTPUTS: None
	 *******************************************************************/
	public boolean addNaHCO3(String compoundName_, int count, PBox2D box2d_,
			P5Canvas parent_) {
		
		boolean res = true;
		String ion1 = "Bicarbonate";
		String ion2 = "Sodium-Ion";
		Vec2 size1 = Molecule.getShapeSize(ion1, parent_);
		Vec2 size2 = Molecule.getShapeSize(ion2, parent_);

		
		int numCol = 3;
		if( count<=3)
		{
			numCol = count;
		}
		int numRow = (int) Math.ceil((float) count / numCol);
		
		
		
		float centerX = p5Canvas.x + 50; // X coordinate around which we are going to
									// add Ions, 50 is border width
		float centerY = p5Canvas.y + 100 - Boundary.difVolume; // Y coordinate around
														// which we are going to
														// add Ions

		Vec2 topLeft = new Vec2(0,0);
		Vec2 botRight = new Vec2(0,0);
		boolean isClear = false;

		float increX = p5Canvas.w / 3;
		Vec2 molePos = new Vec2(0, 0);
		Vec2 molePosInPix = new Vec2(0, 0);
	
		topLeft.set(centerX - 0.5f*size1.x, centerY-0.5f*size1.y);
		botRight.set(centerX + numCol * (size1.x+size2.x), centerY + numRow * size1.y);
		
		while (!isClear) {
			
			isClear = true;
			for (int k = 0; k < molecules.size(); k++) {

				if (!((String) molecules.get(k).getName()).equals("Water")) {
					molePos.set(molecules.get(k).getPosition());
					molePosInPix.set(box2d.coordWorldToPixels(molePos));

					if (areaBodyCheck(molePosInPix, topLeft, botRight)) {
						isClear = false;
						break;
					}
				}
			}
			if (!isClear) {
				centerX += increX;
				topLeft.set(centerX - 0.5f*size1.x, centerY-0.5f*size1.y);
				botRight.set(centerX + numCol * (size1.x+size2.x), centerY + numRow * size1.y);
				// If we have gone through all available areas.
				if (botRight.x > (p5Canvas.x + p5Canvas.w) || topLeft.x<p5Canvas.x ) {
					isClear = true; // Ready to jump out
					res = false; // Set output bolean flag to false
					// TO DO: Show tooltip on Add button when we cant add more
					// compounds
				}
			}
		}

		if(res)
		{
			for (int i = 0; i < count; i++) {
				float x1, y1, angle1;
				float x2, y2, angle2;
				x1 = centerX + (i % numCol) * (size1.x + size2.x);
				x2 = x1 + size1.x - 20;
				y1 = centerY + (i / numCol) * size1.y;
				y2 = y1;
				angle1 = 0;
				angle2 = 0;
				molecules.add(new Molecule(x1, y1, ion1, box2d_, parent_, angle1));
				molecules.add(new Molecule(x2, y2, ion2, box2d_, parent_, angle2));
	
				int index1 = molecules.size() - 2;
				int index2 = molecules.size() - 1;
				Molecule m1 = molecules.get(index1);
				Molecule m2 = molecules.get(index2);
				jointNaHCO3(index1, index2, m1, m2);
			}
		}
		
		return res;
	}
	
	/******************************************************************
	 * FUNCTION : addGlycerol DESCRIPTION : Specific function used to add
	 * addGlycerol and Pentane
	 * 
	 * INPUTS : CompoundName(String), count(int),
	 * box2d_(PBox2D),parent_(P5Canvas) OUTPUTS: None
	 *******************************************************************/
	public boolean addGlycerol(String compoundName, int count, PBox2D box2d,
			P5Canvas parent) {

		boolean res = true;
		int creationCount = 0;

		if (parent.isEnable) // if Applet is enable
			creationCount = 0;
		else
			creationCount++;
		// variables are used to distribute molecules
		int mod = creationCount % 4; // When the system is paused; Otherwise,
										// molecules are create at the same
										// position

		float centerX = 0; // X Coordinate around which we are going to add
							// molecules
		float centerY = 0; // Y Coordinate around which we are going to add
							// molecules
		float x_ = 0; // X Coordinate for a specific molecule
		float y_ = 0; // Y Coordinate for a specific molecule
		int dimension = 0; // Decide molecule cluster is 2*2 or 3*3

		Vec2 size = Molecule.getShapeSize(compoundName, p5Canvas);

		float moleWidth = size.x;
		float moleHeight = size.y;
		Vec2 topLeft = new Vec2(0, 0);
		Vec2 botRight = new Vec2(0, 0);
		// boolean dimensionDecided = false;
		int k = 0;
		for (k = 1; k < 10; k++) {
			if (count <= (k * k)) {
				dimension = k;
				break;
			}
		}
		int rowNum = count / dimension + 1;
		int colNum = dimension;
		boolean isClear = false;
		Vec2 molePos = new Vec2(0, 0); // Molecule position parameter
		Vec2 molePosInPix = new Vec2(0, 0);
		float increX = p5Canvas.w / 12;

		// Initializing
		centerX = p5Canvas.x + moleWidth/2;
		centerY = p5Canvas.y + moleHeight - Boundary.difVolume;
		topLeft = new Vec2(centerX - 0.5f * moleWidth, centerY - 0.5f * moleHeight);
		botRight = new Vec2(centerX + colNum * moleWidth, centerY + rowNum
				* moleHeight);
		// Check if there are any molecules in add area. If yes, add molecules
		// to another area.
	
		while (!isClear) {
			// Specify new add area.

			// Reset flag
			isClear = true;

			for (int m = 0; m < molecules.size(); m++) {

				if (!((String) molecules.get(m).getName()).equals("Water")) {
					molePos.set(molecules.get(m).getPosition());
					molePosInPix.set(box2d.coordWorldToPixels(molePos));

					if (areaBodyCheck(molePosInPix, topLeft, botRight)) {
						isClear = false;
					}
				}
			}
			if (!isClear) {
				centerX += increX;
				topLeft = new Vec2(centerX - moleWidth/2, centerY - moleHeight);
				botRight = new Vec2(centerX + colNum * moleWidth, centerY + rowNum
						* moleHeight);

				// If we have gone through all available areas.
				if (botRight.x > (p5Canvas.x + p5Canvas.w)
						|| topLeft.x < p5Canvas.x) {
					isClear = true; // Ready to jump out
					res = false; // Set output bolean flag to false
					// TO DO: Show tooltip on Add button when we cant add more
					// compounds
				}
			}
		}

		if (res) {
			// Add molecules into p5Canvas
			for (int i = 0; i < count; i++) {

				x_ = centerX + i % dimension * moleWidth + creationCount;
				y_ = centerY + i / dimension * moleHeight;

				res = molecules.add(new Molecule(x_, y_, compoundName, box2d,
						p5Canvas, 0));
			}
		}

		return res;
	}
	

	public void jointNaHCO3(int index1, int index2, Molecule m1, Molecule m2) { // draw
																				// background
		DistanceJointDef djd = new DistanceJointDef();
		djd.bodyA = m1.body;
		djd.bodyB = m2.body;
		// djd.initialize(m1.body, m2.body, new Vec2(0,0), new Vec2(0,0));
		float length = PBox2D.scalarPixelsToWorld(Molecule.oRadius + 34);

		// djd.dampingRatio = 0.5f;

		float frequency = 10.0f;
		//DistanceJoint dj = (DistanceJoint) PBox2D.world.createJoint(djd);
		float dampingRatio =0.0f;
		
		DistanceJointWrap dj = new DistanceJointWrap(m1.body,m2.body,length,frequency,dampingRatio );
		m1.compoundJ = index2;
		m2.compoundJ = index1;
		m2.compoundJoint.add(dj);

		PrismaticJointDef pjd = new PrismaticJointDef();
		pjd.initialize(m1.body, m2.body, m1.body.getWorldCenter(), new Vec2(1,
				0));
		PrismaticJoint pj = (PrismaticJoint) PBox2D.world.createJoint(pjd);
		m2.compoundJoints2 = pj;
	}

	public void computeForceSiO2(int index, Molecule mIndex) { // draw
																// background
		for (int e = 0; e < mIndex.getNumElement(); e++) {
			int indexCharge = mIndex.elementCharges.get(e);
			Vec2 locIndex = mIndex.getElementLocation(e);
			mIndex.sumForceX[e] = 0;
			mIndex.sumForceY[e] = 0;
			for (int i = 0; i < molecules.size(); i++) {
				if (i == index)
					continue;
				Molecule m = molecules.get(i);
				if (m.getName().equals("Water"))
					continue;

				float forceX;
				float forceY;
				for (int e2 = 0; e2 < m.getNumElement(); e2++) {
					Vec2 loc = m.getElementLocation(e2);
					if (loc == null || locIndex == null)
						continue;
					float x = locIndex.x - loc.x;
					float y = locIndex.y - loc.y;
					float dis = x * x + y * y;
					forceX = (float) ((x / Math.pow(dis, 1.5)) * 10);
					forceY = (float) ((y / Math.pow(dis, 1.5)) * 10);

					int charge = m.elementCharges.get(e2);
					int mul = charge * indexCharge;
					if (mul < 0) {
						mIndex.sumForceX[e] += mul * forceX;
						mIndex.sumForceY[e] += mul * forceY;
					} else if (mul > 0) {
						mIndex.sumForceX[e] += mul * forceX * mIndex.chargeRate;
						mIndex.sumForceY[e] += mul * forceY * mIndex.chargeRate;
					}
				}
			}
		}
	}

	public void computeForceGlycerol(int index, Molecule mIndex) { // draw
																	// background
		float xMul = 1.4f;
		for (int e = 0; e < mIndex.getNumElement(); e++) {
			int indexCharge = mIndex.elementCharges.get(e);
			Vec2 locIndex = mIndex.getElementLocation(e);
			mIndex.sumForceX[e] = 0;
			mIndex.sumForceY[e] = 0;
			for (int i = 0; i < molecules.size(); i++) {
				if (i == index)
					continue;
				Molecule m = molecules.get(i);
				if (m.getName().equals("Water"))
					continue;
				float forceX;
				float forceY;
				for (int e2 = 0; e2 < m.getNumElement(); e2++) {
					Vec2 loc = m.getElementLocation(e2);
					if (loc == null || locIndex == null)
						continue;
					float x = locIndex.x - loc.x;
					float y = locIndex.y - loc.y;
					float dis = x * x + y * y;
					forceX = (float) ((x / Math.pow(dis, 1.5)) * 0.3);
					forceY = (float) ((y / Math.pow(dis, 1.5)) * 0.3);
					if (p5Canvas.temp < mIndex.freezingTem) {
						forceX *= 90;
						forceY *= 90;
					}
					int charge = m.elementCharges.get(e2);
					int mul = charge * indexCharge;
					if (mul < 0) {
						mIndex.sumForceX[e] += mul * forceX;
						mIndex.sumForceY[e] += mul * forceY;
					} else if (mul > 0) {
						mIndex.sumForceX[e] += mul * forceX * mIndex.chargeRate;
						mIndex.sumForceY[e] += mul * forceY * mIndex.chargeRate;
					}
				}
			}
		}
	}

	public void computeForceNaCl(int index, Molecule mIndex) { // draw
																// background
		for (int e = 0; e < mIndex.getNumElement(); e++) {
			int indexCharge = mIndex.elementCharges.get(e);
			Vec2 locIndex = mIndex.getElementLocation(e);
			mIndex.sumForceWaterX[e] = 0;
			mIndex.sumForceWaterY[e] = 0;
			mIndex.sumForceX[e] = 0;
			mIndex.sumForceY[e] = 0;
			for (int i = 0; i < molecules.size(); i++) {
				if (i == index)
					continue;
				Molecule m = molecules.get(i);
				float forceX;
				float forceY;
				for (int e2 = 0; e2 < m.getNumElement(); e2++) {
					Vec2 loc = m.getElementLocation(e2);
					float x = locIndex.x - loc.x;
					float y = locIndex.y - loc.y;
					float dis = x * x + y * y;
					forceX = (float) ((x / Math.pow(dis, 1.5)) * 40);
					forceY = (float) ((y / Math.pow(dis, 1.5)) * 40);

					int charge = m.elementCharges.get(e2);
					int mul = charge * indexCharge;
					if (m.getName().equals("Water")) {
						float r = 0.002f + p5Canvas.temp / 10000;
						if (mIndex.compoundJ >= 0) {
							forceX *= r;
							forceY *= r;
						} else {
							forceX *= 0.10;
							forceY *= 0.10;
						}
						if (p5Canvas.temp >= 100) {
							forceX = 0;
							forceY = 0;
						}
						if (mul < 0) {
							mIndex.sumForceWaterX[e] += mul * forceX;
							mIndex.sumForceWaterY[e] += mul * forceY;
						} else if (mul > 0) {
							mIndex.sumForceWaterX[e] += mul * forceX
									* mIndex.chargeRate;
							mIndex.sumForceWaterY[e] += mul * forceY
									* mIndex.chargeRate;
						}
					} else {
						if (mIndex.compoundJ < 0) { // Compute IonDis
							float dis2 = (float) (PBox2D
									.scalarWorldToPixels((float) Math.sqrt(dis)));
							if (mIndex.ionDis == 0)
								mIndex.ionDis = dis2;
							else {
								if (dis2 < mIndex.ionDis) {
									mIndex.ionDis = dis2;
								}
							}
						}

						if ((m.compoundJ < 0 || mIndex.compoundJ < 0)
								&& 0 < p5Canvas.temp && p5Canvas.temp < 100) {
							forceX *= 0.05f;
							forceY *= 0.05f;
						}
						if (num_gone > numGone_atSaturation()
								&& m.compoundJ < 0 && mIndex.compoundJ < 0
								&& mIndex.getName().equals("Sodium-Ion")
								&& m.getName().equals("Chlorine-Ion")) {
							float dif = (float) (PBox2D
									.scalarWorldToPixels((float) Math.sqrt(dis)) - Molecule.clRadius * 2);
							if (dif < 2) {
								joint2Ions(index, i, mIndex, m);
								num_gone--;
								//p5Canvas.computeDisolved();

							}
						}

						if (mul < 0) {
							mIndex.sumForceX[e] += mul * forceX;
							mIndex.sumForceY[e] += mul * forceY;
						} else if (mul > 0) {
							mIndex.sumForceX[e] += mul * forceX
									* mIndex.chargeRate;
							mIndex.sumForceY[e] += mul * forceY
									* mIndex.chargeRate;
						}
					}
				}
			}
		}
	}

	public void computeForceKCl(int index, Molecule mIndex) {
		for (int e = 0; e < mIndex.getNumElement(); e++) {
			int indexCharge = mIndex.elementCharges.get(e);
			Vec2 locIndex = mIndex.getElementLocation(e);
			mIndex.sumForceWaterX[e] = 0;
			mIndex.sumForceWaterY[e] = 0;
			mIndex.sumForceX[e] = 0;
			mIndex.sumForceY[e] = 0;
			for (int i = 0; i < molecules.size(); i++) {
				if (i == index)
					continue;
				Molecule m = molecules.get(i);
				float forceX;
				float forceY;
				for (int e2 = 0; e2 < m.getNumElement(); e2++) {
					Vec2 loc = m.getElementLocation(e2);
					float x = locIndex.x - loc.x;
					float y = locIndex.y - loc.y;
					float dis = x * x + y * y;
					forceX = (float) ((x / Math.pow(dis, 1.5)) * 40);
					forceY = (float) ((y / Math.pow(dis, 1.5)) * 40);

					int charge = m.elementCharges.get(e2);
					int mul = charge * indexCharge;
					if (m.getName().equals("Water")) {
						float r = 0.002f + p5Canvas.temp / 10000;
						if (mIndex.compoundJ >= 0) {
							forceX *= r;
							forceY *= r;
						} else {
							forceX *= 0.10;
							forceY *= 0.10;
						}
						if (p5Canvas.temp >= 100) {
							forceX = 0;
							forceY = 0;
						}
						if (mul < 0) {
							mIndex.sumForceWaterX[e] += mul * forceX;
							mIndex.sumForceWaterY[e] += mul * forceY;
						} else if (mul > 0) {
							mIndex.sumForceWaterX[e] += mul * forceX
									* mIndex.chargeRate;
							mIndex.sumForceWaterY[e] += mul * forceY
									* mIndex.chargeRate;
						}
					} else {
						if (mIndex.compoundJ < 0) { // Compute IonDis
							float dis2 = (float) (PBox2D
									.scalarWorldToPixels((float) Math.sqrt(dis)));
							if (mIndex.ionDis == 0)
								mIndex.ionDis = dis2;
							else {
								if (dis2 < mIndex.ionDis) {
									mIndex.ionDis = dis2;
								}
							}
						}

						if ((m.compoundJ < 0 || mIndex.compoundJ < 0)
								&& 0 < p5Canvas.temp && p5Canvas.temp < 100) {
							forceX *= 0.05f;
							forceY *= 0.05f;
						}
						if (num_gone > numGone_atSaturation()
								&& m.compoundJ < 0 && mIndex.compoundJ < 0
								&& mIndex.getName().equals("Potassium-Ion")
								&& m.getName().equals("Chlorine-Ion")) {
							float dif = (float) (PBox2D
									.scalarWorldToPixels((float) Math.sqrt(dis)) - Molecule.clRadius * 2);
							if (dif < 2) {
								joint2Ions(index, i, mIndex, m);
								num_gone--;
								//p5Canvas.computeDisolved();

							}
						}

						if (mul < 0) {
							mIndex.sumForceX[e] += mul * forceX;
							mIndex.sumForceY[e] += mul * forceY;
						} else if (mul > 0) {
							mIndex.sumForceX[e] += mul * forceX
									* mIndex.chargeRate;
							mIndex.sumForceY[e] += mul * forceY
									* mIndex.chargeRate;
						}
					}
				}
			}
		}
	}

	public void jointCaCl(int index1, int index2, int index3, Molecule m1,
			Molecule m2, Molecule m3) { // draw background
		//DistanceJointDef djd = new DistanceJointDef();
		//djd.bodyA = m3.body;
		//djd.bodyB = m1.body;
		float length = PBox2D.scalarPixelsToWorld(Molecule.clRadius * 4);
		float dampingRatio = 0.f;
		float frequencyHz = 15.0f;
		//DistanceJoint dj = (DistanceJoint) PBox2D.world.createJoint(djd);
		DistanceJointWrap dj = new DistanceJointWrap(m3.body,m1.body,length,frequencyHz,dampingRatio);
		m3.compoundJ = index1;
		m3.compoundJoint.add(dj);

		//djd.bodyA = m1.body;
		//djd.bodyB = m2.body;
		length = PBox2D.scalarPixelsToWorld(Molecule.clRadius * 2);
		dampingRatio = 0.f;
		frequencyHz = 10.0f;
		//dj = (DistanceJoint) PBox2D.world.createJoint(djd);
		dj = new DistanceJointWrap(m1.body,m2.body,length,frequencyHz,dampingRatio);
		m1.compoundJ = index2;
		m1.compoundJoint.add(dj);

		//djd.bodyA = m2.body;
		//djd.bodyB = m3.body;
		length = PBox2D.scalarPixelsToWorld(Molecule.clRadius * 2);
		dampingRatio = 0.f;
		frequencyHz = 10.0f;
		//dj = (DistanceJoint) PBox2D.world.createJoint(djd);
		dj = new DistanceJointWrap(m2.body,m3.body,length,frequencyHz,dampingRatio);
		m2.compoundJ = index3;
		m2.compoundJoint.add(dj);
	}

	public void computeCaClPartner(int index, Molecule mIndex) { // draw
																	// background
		int[] ClPartners = new int[2];
		ClPartners[0] = -1;
		ClPartners[1] = -1;
		if (p5Canvas.temp <= 0)
			return;
		Vec2 locIndex = mIndex.getElementLocation(0);
		for (int i = 0; i < molecules.size(); i++) {
			Molecule m = molecules.get(i);
			if (!m.getName().equals("Chlorine-Ion"))
				continue;
			if (i == mIndex.compoundJ
					|| (mIndex.compoundJ > 0 && i == molecules
							.get(mIndex.compoundJ).compoundJ))
				continue;
			Vec2 loc = m.getElementLocation(0);
			if (loc == null || locIndex == null)
				continue;
			float x = locIndex.x - loc.x;
			float y = locIndex.y - loc.y;
			float dis = x * x + y * y;
			float dif = (float) (PBox2D.scalarWorldToPixels((float) Math
					.sqrt(dis)) - Molecule.clRadius * 2);

			if (mIndex.compoundJ < 0) { // Compute IonDis
				float dis1 = (float) (PBox2D.scalarWorldToPixels((float) Math
						.sqrt(dis)));
				if (mIndex.ionDis == 0)
					mIndex.ionDis = dis1;
				else {
					if (dis1 < mIndex.ionDis) {
						mIndex.ionDis = dis1;
					}
				}
			}
			// Computer Ca Cl partner to form a compound
			if (mIndex.compoundJ < 0 && m.compoundJ < 0 && dif < 10) {
				if (ClPartners[0] < 0)
					ClPartners[0] = i;
				else if (ClPartners[1] < 0)
					ClPartners[1] = i;
			}
			if (dif < 3) {
				if (mIndex.compoundJ < 0 || m.compoundJ < 0
						|| mIndex.otherJ >= 0)
					continue;

				// Joint CaCl with another CaCl
				DistanceJointDef djd = new DistanceJointDef();

				// Connect Na to Cl of another NaCl
				djd.bodyA = mIndex.body;
				djd.bodyB = m.body;
				djd.length = PBox2D.scalarPixelsToWorld(Molecule.clRadius * 2);
				djd.dampingRatio = 0.f;
				djd.frequencyHz = 1000.0f;
				DistanceJoint dj = (DistanceJoint) PBox2D.world
						.createJoint(djd);
				mIndex.otherJ = i;
				mIndex.otherJoints = dj;

				// Connect Cl to Cl of another NaCl
				int anotherClIndex = m.compoundJ;
				Molecule anotherCl = molecules.get(anotherClIndex);
				if (!anotherCl.getName().equals("Chlorine-Ion")) {
					anotherClIndex = anotherCl.compoundJ;
					anotherCl = molecules.get(anotherClIndex);
				}
				anotherCl.CaOtherJ = index;
				int clIndex1 = mIndex.compoundJ;
				Molecule mCl1 = molecules.get(clIndex1);
				djd.bodyA = mCl1.body;
				djd.bodyB = anotherCl.body;
				djd.length = PBox2D
						.scalarPixelsToWorld((float) (Molecule.clRadius * Math
								.sqrt(40)));
				djd.dampingRatio = 0.f;
				djd.frequencyHz = 1000.0f;
				dj = (DistanceJoint) PBox2D.world.createJoint(djd);
				mCl1.otherJ = anotherClIndex;
				mCl1.otherJoints = dj;

				int clIndex2 = molecules.get(mIndex.compoundJ).compoundJ;
				Molecule mCl2 = molecules.get(clIndex2);
				djd.bodyA = mCl2.body;
				djd.bodyB = anotherCl.body;
				djd.length = PBox2D
						.scalarPixelsToWorld((float) (Molecule.clRadius * Math
								.sqrt(40)));
				djd.dampingRatio = 0.f;
				djd.frequencyHz = 1000.0f;
				dj = (DistanceJoint) PBox2D.world.createJoint(djd);
				mCl2.otherJ = anotherClIndex;
				mCl2.otherJoints = dj;
			}
		}
		int index1 = ClPartners[0];
		int index3 = ClPartners[1];
		if (index1 >= 0 && index3 >= 0) {
			Molecule m1 = molecules.get(index1);
			Molecule m3 = molecules.get(index3);
			if (num_gone > numGone_atSaturation()
					&& mIndex.compoundJ < 0 && m1.compoundJ < 0
					&& m3.compoundJ < 0) {
				jointCaCl(index1, index, index3, m1, mIndex, m3);
				num_gone--;
				//p5Canvas.computeDisolved();

			}
		}
	}

	public void computeForceCaCl(int index, Molecule mIndex) {
		mIndex.sumForceX[0] = 0;
		mIndex.sumForceY[0] = 0;
		for (int i = 0; i < molecules.size(); i++) {
			if (i == index)
				continue;
			Molecule m = molecules.get(i);
			Vec2 locIndex = mIndex.getElementLocation(0);
			Vec2 loc = m.getElementLocation(0);
			float x = locIndex.x - loc.x;
			float y = locIndex.y - loc.y;
			float dis3 = x * x + y * y;
			float dis = (float) Math.sqrt(dis3);
			dis3 = (float) Math.pow(dis3, 1.5);
			float forceX = 0;
			float forceY = 0;
			if (mIndex.getName().equals("Calcium-Ion")) {
				if (m.getName().equals("Calcium-Ion")) {
					if (PBox2D.scalarWorldToPixels(dis) <= Molecule.clRadius * 4) {
						forceX = (x / dis3) * 100;
						forceY = (y / dis3) * 100;
					} else {
						forceX = (x / dis3) * 3;
						forceY = (y / dis3) * 3;
					}
				} else if (m.getName().equals("Chlorine-Ion")) {
					forceX = -(x / dis3) * 16;
					forceY = -(y / dis3) * 16;
				}
			} else if (mIndex.getName().equals("Chlorine-Ion")) {
				if (m.getName().equals("Chlorine-Ion")) {
					if (PBox2D.scalarWorldToPixels(dis) <= Molecule.clRadius * 2.8282) {
						forceX = (x / dis3) * 100;
						forceY = (y / dis3) * 100;
					} else {
						forceX = (x / dis3) * 3;
						forceY = (y / dis3) * 3;
					}
				} else if (m.getName().equals("Calcium-Ion")) {
					forceX = -(x / dis3) * 24;
					forceY = -(y / dis3) * 24;
				}
			}
			if (mIndex.compoundJ < 0 || m.compoundJ < 0) {
				forceX *= 0.11;
				forceY *= 0.11;
			}
			mIndex.sumForceX[0] += forceX;
			mIndex.sumForceY[0] += forceY;
		}

	}

	public void computeForceFromWater(int index, Molecule mIndex) {
		int numElements = mIndex.getNumElement();
		mIndex.sumForceWaterX = new float[numElements];
		mIndex.sumForceWaterY = new float[numElements];

		for (int e = 0; e < numElements; e++) {
			int indexCharge = mIndex.elementCharges.get(e);
			Vec2 locIndex = mIndex.getElementLocation(e);
			for (int i = 0; i < molecules.size(); i++) {
				if (i == index)
					continue;
				Molecule m = molecules.get(i);
				if (!m.getName().equals("Water"))
					continue;
				for (int e2 = 0; e2 < m.getNumElement(); e2++) {
					Vec2 loc = m.getElementLocation(e2);
					if (loc == null || locIndex == null)
						continue;
					float x = locIndex.x - loc.x;
					float y = locIndex.y - loc.y;
					float dis = x * x + y * y;
					Vec2 normV = normalizeForce(new Vec2(x, y));

					int charge = m.elementCharges.get(e2);
					int mul = charge * indexCharge;
					float r = p5Canvas.temp / 100f;

					// if (m.elementNames.get(e2).equals("Oxygen")){
					// if (mIndex.getName().equals("Sodium-Ion") &&
					// Main.selectedSet==7)
					// r *=1.5;
					// }

					if (mIndex.compoundJ >= 0) {
						mIndex.sumForceWaterX[e] += mul * (normV.x / dis) * 0.1;
						mIndex.sumForceWaterY[e] += mul * (normV.y / dis) * 0.1;
					} else {
						mIndex.sumForceWaterX[e] += mul * (normV.x / dis)
								* (3 + r);
						mIndex.sumForceWaterY[e] += mul * (normV.y / dis)
								* (3 + r);
					}

					if (p5Canvas.temp >= 100) {
						mIndex.sumForceWaterX[e] = 0;
						mIndex.sumForceWaterY[e] = 0;
					}
				}
			}
		}
	}
	public Vec2 normalizeForce(Vec2 v){
		float dis = (float) Math.sqrt(v.x*v.x + v.y*v.y);
		return new Vec2(v.x/dis,v.y/dis);
	}

	public void removeCaOtherJ(Molecule mCl) {
		if (mCl.CaOtherJ < 0)
			return;
		Molecule mmm = molecules.get(mCl.CaOtherJ);

		if (mmm.otherJ >= 0) {
			DistanceJoint dj2 = mmm.otherJoints;
			PBox2D.world.destroyJoint(dj2);
			mmm.otherJoints = null;
			mmm.otherJ = -1;
		}
		if (mmm.compoundJ >= 0) {
			Molecule m = molecules.get(mmm.compoundJ);
			if (m.otherJ >= 0) {
				DistanceJoint dj2 = m.otherJoints;
				PBox2D.world.destroyJoint(dj2);
				m.otherJoints = null;
				m.otherJ = -1;
			}

			if (m.compoundJ >= 0) {
				m = molecules.get(m.compoundJ);
				if (m.otherJ >= 0) {
					DistanceJoint dj2 = m.otherJoints;
					PBox2D.world.destroyJoint(dj2);
					m.otherJoints = null;
					m.otherJ = -1;
				}
			}
		}

	}

	// Unit2 Set5
	public void computeForceAceticAcid(int index, Molecule mIndex) { // draw
																		// background
		float xMul = 2f;
		for (int e = 0; e < mIndex.getNumElement(); e++) {
			int indexCharge = mIndex.elementCharges.get(e);
			Vec2 locIndex = mIndex.getElementLocation(e);
			mIndex.sumForceX[e] = 0;
			mIndex.sumForceY[e] = 0;
			for (int i = 0; i < molecules.size(); i++) {
				if (i == index)
					continue;
				Molecule m = molecules.get(i);
				float forceX;
				float forceY;
				for (int e2 = 0; e2 < m.getNumElement(); e2++) {
					Vec2 loc = m.getElementLocation(e2);
					if (loc == null || locIndex == null)
						continue;
					float x = locIndex.x - loc.x;
					float y = locIndex.y - loc.y;
					float dis = x * x + y * y;
					forceX = (float) ((x / Math.pow(dis, 1.5)) * 0.1);
					forceY = (float) ((y / Math.pow(dis, 1.5)) * 0.1);
					if ((p5Canvas.temp <= 0)
							|| (p5Canvas.temp < mIndex.freezingTem && !m.getName()
									.equals("Water"))) {
						forceX *= 40;
						forceY *= 40;
					}
					int charge = m.elementCharges.get(e2);
					int mul = charge * indexCharge;
					if (mul < 0) {
						mIndex.sumForceX[e] += mul * forceX;
						mIndex.sumForceY[e] += mul * forceY;

					} else if (mul > 0) {
						mIndex.sumForceX[e] += mul * forceX * mIndex.chargeRate;
						mIndex.sumForceY[e] += mul * forceY * mIndex.chargeRate;
					}
				}
			}
		}
	}

	// Unit2 Set7
	public void computeForceNaHCO3(int index, Molecule mIndex) { // draw
																	// background
		for (int e = 0; e < mIndex.getNumElement(); e++) {
			int indexCharge = mIndex.elementCharges.get(e);
			Vec2 locIndex = mIndex.getElementLocation(e);
			mIndex.sumForceX[e] = 0;
			mIndex.sumForceY[e] = 0;
			for (int i = 0; i < molecules.size(); i++) {
				Molecule m = molecules.get(i);
				if (i == index || i == mIndex.compoundJ || index == m.compoundJ // No
																				// interMolecule
																				// force
						|| m.getName().equals("Water")) // No water attraction
														// here
					continue;

				float forceX;
				float forceY;
				for (int e2 = 0; e2 < m.getNumElement(); e2++) {
					Vec2 loc = m.getElementLocation(e2);
					if (loc == null || locIndex == null)
						continue;
					float x = locIndex.x - loc.x;
					float y = locIndex.y - loc.y;
					float dis = x * x + y * y;
					float dis3 = (float) Math.pow(dis, 1.5);
					forceX = (x / dis3) * 20;
					forceY = (y / dis3) * 20;

					if ((mIndex.compoundJ < 0 || m.compoundJ < 0) && 0 < p5Canvas.temp
							&& p5Canvas.temp < 100) { // Losing mIndex
						forceX *= 0.05f;
						forceY *= 0.05f;
					}
					if (mIndex.compoundJ < 0) { // Compute IonDis
						float dis2 = (float) (PBox2D
								.scalarWorldToPixels((float) Math.sqrt(dis)));
						if (mIndex.ionDis == 0)
							mIndex.ionDis = dis2;
						else {
							if (dis2 < mIndex.ionDis) {
								mIndex.ionDis = dis2;
							}
						}
					}
					if (num_gone > numGone_atSaturation() // Recombine
																	// compound
							&& m.compoundJ < 0
							&& mIndex.compoundJ < 0
							&& mIndex.getName().equals("Bicarbonate")
							&& m.getName().equals("Sodium-Ion")) {
						float dif = (float) (PBox2D
								.scalarWorldToPixels((float) Math.sqrt(dis)) - (Molecule.oRadius + 34));
						if (dif < 2) {
							Vec2 p1 = mIndex.body.getPosition();
							float a = mIndex.body.getAngle();
							float d = PBox2D
									.scalarPixelsToWorld(Molecule.oRadius + 30);
							float x2 = (float) (p1.x + d * Math.cos(a));
							float y2 = (float) (p1.y + d * Math.sin(a));
							m.body.setTransform(new Vec2(x2, y2), 0);
							jointNaHCO3(index, i, mIndex, m);
							num_gone--;
							//p5Canvas.computeDisolved();

						}
					}

					int charge = m.elementCharges.get(e2);
					int mul = (int) Math.signum(charge * indexCharge);
					if (mul < 0) {
						mIndex.sumForceX[e] += mul * forceX;
						mIndex.sumForceY[e] += mul * forceY;
					} else if (mul > 0) {
						mIndex.sumForceX[e] += mul * forceX * mIndex.chargeRate;
						mIndex.sumForceY[e] += mul * forceY * mIndex.chargeRate;
					}
				}

			}
		}
	}

	// Compute saturation
	public float computeSat() {
		if (p5Canvas.temp > 99 || p5Canvas.temp < 0) {
			return 0;
		}
		float r = (float) (p5Canvas.temp / 99.);
		float sat = 0;
		if (p5Canvas.getMain().selectedSet == 1 && p5Canvas.getMain().selectedSim <= 3)
			sat = (35.7f + r * (39.9f - 35.7f)); // Take number of Water to
													// account
		else if (p5Canvas.getMain().selectedSet == 2)
			sat = 0;
		else if (p5Canvas.getMain().selectedSet == 3)
			sat = 0;
		else if (p5Canvas.getMain().selectedSet == 4) {
			if (0 < p5Canvas.temp && p5Canvas.temp <= 20) {
				r = (float) (p5Canvas.temp / 20.);
				sat = (59.5f + r * (74.5f - 59.5f));
			}
			if (20 < p5Canvas.temp && p5Canvas.temp <= 40) {
				r = (float) ((p5Canvas.temp - 20) / 20.);
				sat = (74.5f + r * (128f - 74.5f));
			}
			if (40 < p5Canvas.temp && p5Canvas.temp <= 60) {
				r = (float) ((p5Canvas.temp - 40) / 20.);
				sat = (128f + r * (137f - 128f));
			}
			if (60 < p5Canvas.temp && p5Canvas.temp <= 80) {
				r = (float) ((p5Canvas.temp - 60) / 20.);
				sat = (137f + r * (147f - 137));
			}
			if (80 < p5Canvas.temp && p5Canvas.temp <= 100) {
				r = (float) ((p5Canvas.temp - 80) / 20.);
				sat = (147f + r * (159f - 147f));
			}
		} else if (p5Canvas.getMain().selectedSet == 5) {
			sat = 0;
		} else if (p5Canvas.getMain().selectedSet == 6) {
			sat = 0;
		} else if (p5Canvas.getMain().selectedSet == 7)
			sat = (6.9f + r * (19.2f - 6.9f));
		else if (p5Canvas.getMain().selectedSet == 1 && p5Canvas.getMain().selectedSim == 4)
			sat = (28f + r * (56.3f - 28f));
		return sat * ((float) numWater / water100mL);
	}

	public void applyForceUnit2(int index, Molecule mIndex) { // draw background
		for (int e = 0; e < mIndex.getNumElement(); e++) {
			mIndex.addForce(new Vec2(mIndex.sumForceX[e], mIndex.sumForceY[e]),
					e);
			mIndex.addForce(new Vec2(mIndex.sumForceWaterX[e],
					mIndex.sumForceWaterY[e]), e);

			if (p5Canvas.getMain().selectedUnit == 2 && p5Canvas.getMain().selectedSet == 1) {
				int num = mIndex.getNumElement();
				float fX = 0;
				float fY = 0;
				for (int i = 0; i < num; i++) {
					fX += mIndex.sumForceWaterX[i];
					fY += mIndex.sumForceWaterY[i];
				}
				float s = fX * fX + fY * fY;
				float f = (float) Math.sqrt(s);

				if (num_gone < numGone_atSaturation()
						&& mIndex.compoundJ >= 0 && f > 0.02) {
					DistanceJointWrap dj1 = mIndex.compoundJoint.get(0);
					//PBox2D.world.destroyJoint(dj1);
					dj1.destroy();
					//this.destroyJoint(dj1);
					mIndex.compoundJoint.remove(0);
					Molecule m2 = molecules.get(mIndex.compoundJ);
					mIndex.compoundJ = -1;
					m2.compoundJ = -1;
					m2.compoundJoint.remove(0);
					num_gone++;
					//p5Canvas.computeDisolved();
				}
			} else if (p5Canvas.getMain().selectedUnit == 2 && p5Canvas.getMain().selectedSet == 4) {
				float s = mIndex.sumForceWaterX[0] * mIndex.sumForceWaterX[0]
						+ mIndex.sumForceWaterY[0] * mIndex.sumForceWaterY[0];
				float f = (float) Math.sqrt(s);
				if (num_gone < numGone_atSaturation()
						&& mIndex.compoundJ >= 0 && f > 0.02f) {
					DistanceJointWrap dj1 = mIndex.compoundJoint.get(0);
					//PBox2D.world.destroyJoint(dj1);
					dj1.destroy();
					//destroyJoint(dj1);
					mIndex.compoundJoint.remove(0);
					int jIndex = mIndex.compoundJ;
					mIndex.compoundJ = -1;

					if (mIndex.otherJ >= 0) {
						DistanceJoint dj2 = mIndex.otherJoints;
						PBox2D.world.destroyJoint(dj2);
						//destroyJoint(dj2);
						mIndex.otherJoints = null;
						mIndex.otherJ = -1;
					}
					removeCaOtherJ(mIndex);

					Molecule m = molecules.get(jIndex);
					dj1 = m.compoundJoint.get(0);
					//PBox2D.world.destroyJoint(dj1);
					dj1.destroy();
					//destroyJoint(dj1);
					m.compoundJoint.remove(0);
					jIndex = m.compoundJ;
					m.compoundJ = -1;

					if (m.otherJ >= 0) {
						DistanceJoint dj2 = m.otherJoints;
						PBox2D.world.destroyJoint(dj2);
						//destroyJoint(dj2);
						m.otherJoints = null;
						m.otherJ = -1;
					}
					removeCaOtherJ(m);

					m = molecules.get(jIndex);
					dj1 = m.compoundJoint.get(0);
					//PBox2D.world.destroyJoint(dj1);
					dj1.destroy();
					//destroyJoint(dj1);
					m.compoundJoint.remove(0);
					m.compoundJ = -1;
					if (m.otherJ >= 0) {
						DistanceJoint dj2 = m.otherJoints;
						PBox2D.world.destroyJoint(dj2);
						//destroyJoint(dj2);
						m.otherJoints = null;
						m.otherJ = -1;
					}
					removeCaOtherJ(m);
					num_gone++;
					//p5Canvas.computeDisolved();

				}
			} else if (p5Canvas.getMain().selectedUnit == 2 && p5Canvas.getMain().selectedSet == 7) {
				int num = mIndex.getNumElement();
				float fX = 0;
				float fY = 0;
				for (int i = 0; i < num; i++) {
					fX += mIndex.sumForceWaterX[i];
					fY += mIndex.sumForceWaterY[i];

				}
				float f = fX * fX + fY * fY;
				
				if (num_gone < numGone_atSaturation()
						&& mIndex.compoundJ >= 0 && f > 0.01) {

					Molecule mCa = mIndex;
					if (mIndex.getName().equals("Bicarbonate")) {
						mCa = molecules.get(mIndex.compoundJ);
					}

					DistanceJointWrap dj1 = mCa.compoundJoint.get(0);
					//PBox2D.world.destroyJoint(dj1);
					dj1.destroy();
					//destroyJoint(dj1);
					mCa.compoundJoint.remove(0);
					Molecule mHCO3 = molecules.get(mCa.compoundJ);
					mCa.compoundJ = -1;
					mHCO3.compoundJ = -1;

					PrismaticJoint dj2 = mCa.compoundJoints2;
					PBox2D.world.destroyJoint(dj2);
					
					mCa.compoundJoints2 = null;

					num_gone++;
					//p5Canvas.computeDisolved();
				}
			}

		}
	}
	
	
	public void updateMolecules(int sim, int set)
	{

			if (set==1 || set==4 || set==7){
				for (int i = 0; i < molecules.size(); i++) {
					Molecule m = molecules.get(i);
					m.ionDis =0;
					if (set==4 && m.getName().equals("Calcium-Ion"))
							computeCaClPartner(i,m);
				}
				
			}
			
		
	}
	
	
	public void setupReactionProducts(int sim, int set) {
		// TODO Auto-generated method stub
		
			if (set == 1 && sim < 4) {
				Compound.names.add("Sodium-Ion");
				Compound.counts.add(0);
				Compound.names.add("Chlorine-Ion");
				Compound.counts.add(0);
			} else if (set == 4) {
				Compound.names.add("Calcium-Ion");
				Compound.counts.add(0);
				Compound.names.add("Chlorine-Ion");
				Compound.counts.add(0);
			} else if (set == 7) {
				Compound.names.add("Sodium-Ion");
				Compound.counts.add(0);
				Compound.names.add("Bicarbonate");
				Compound.counts.add(0);
			} else if (set == 1 && sim == 4) {
				Compound.names.add("Potassium-Ion");
				Compound.counts.add(0);
				Compound.names.add("Chlorine-Ion");
				Compound.counts.add(0);
			}
		
	}
	/*
	private void destroyJoint(DistanceJoint dj)
	{
		PBox2D.world.destroyJoint(dj);
		//p5Canvas.computeDisolved();
	}
	*/
	public void reset() { // draw background
		num_total = 0;
		num_gone = 0;
		numWater = 0;
		this.mToMass = 10;
		if (p5Canvas.getMain().selectedSet == 4)
			this.mToMass = 20;
		computeDissolved();
	}

	public int getTotalNum() {
		return num_total;
	}

	public int getDissolvedNum() {
		return num_gone;
	}

	public int getWaterNum() {
		return numWater;
	}

	public float getMassDissolved() {
		return this.massDissolved;
	}

	public void setMassDissolved(float mass) {
		this.massDissolved = mass;
	}

	public int getMolToMass() {
		return this.mToMass;
	}

	public int getWater100Ml() {
		return this.water100mL;
	}

	public void addWaterMolecules(final int count) {
		
		numWater += count;
		/*
		timerPerformer = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				numWater += count;
				//System.out.println("numWater is "+numWater);
			}
			
		};
		int delay = this.addWaterDelay;
		Timer timer = new Timer( delay, timerPerformer);   //delay is a dummy num
		timer.setInitialDelay(this.addWaterDelay);
		timer.setRepeats(false);  //Set the timer only go off once
		timer.start();
		*/

		
	}

	public void addTotalMolecules(int count) {
		this.num_total += count;
	}
	
		public void computeDissolved() {


		DecimalFormat df = new DecimalFormat("###.#");

		if ( getDissolvedNum()< numGone_atSaturation() || numGone_atSaturation()==0){
			float dis = getDissolvedNum()*getMolToMass();
			if(getMassDissolved()<=dis)
				setMassDissolved(dis);
		}
		else if (getDissolvedNum()==numGone_atSaturation()) {
			float sat = computeSat();
			float dis = 0;  //Distance of Ion
			setMassDissolved(sat - dis);
			
			
		}
		else if (getDissolvedNum()>numGone_atSaturation()) {
			float sat = computeSat();
			float gone = getDissolvedNum()*getMolToMass();
			float average = (sat+gone)/2;
			float dis = computeIonSeperation()/(1+p5Canvas.getMain().getCanvas().satCount);
			setMassDissolved(average+dis) ;
			if (p5Canvas.getMain().getCanvas().satCount>10){
				setMassDissolved(sat+dis);
			}
		}
		
		double dis = getMassDissolved();
		double total =  getTotalNum()* getMolToMass();
		if (dis>total){
			setMassDissolved((float) total) ;
		}
		if (p5Canvas.getMain().selectedSet==3 || p5Canvas.getMain().selectedSet==5){
			setMassDissolved(getTotalNum()* getMolToMass()) ;
		}	
		
		p5Canvas.getMain().m1Disolved.setText(df.format(getMassDissolved())+" g");
		float temp = p5Canvas.temp;
		if (temp<=0 || temp>=100){ 
			setMassDissolved(0) ;
			p5Canvas.getMain().m1Disolved.setText("0 g");
		}
		
		//If ConvertToMol checkbox is selected, we need to change 'g' to 'mol'
		if (p5Canvas.isConvertMol){
			p5Canvas.convertMassMol2();
		}
		
	}
	
		public float computeIonSeperation() {
		float dis = 0;
		for (int i = 0; i < molecules.size(); i++) {
			Molecule m = molecules.get(i);
			if (m.ionDis>0){
				dis += (2*Molecule.clRadius)/m.ionDis;
			}		
		}
		return dis*dis;
	}
	
		public int numGone_atSaturation() { 
		int num = Math.round(computeSat()/ getMolToMass()); 
		return num;
	}

		@Override
		public void setupSimulations() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void computeForce(int sim, int set) {
			// TODO Auto-generated method stub
			for (int i = 0; i < molecules.size(); i++) {
				Molecule m = molecules.get(i);
			if (m.getName().equals("Water"))
				waterComputation.setForceWater(i,m);
			if(set==1 && sim<4)
				computeForceNaCl(i,m);
			else if(set==1 && sim==4){
				computeForceKCl(i,m);
			}
			else if(set==2)
				computeForceSiO2(i,m);
			else if(set==3)
				computeForceGlycerol(i,m);
			else if(set==4){
				computeForceCaCl(i,m);	
				computeForceFromWater(i,m);	
				checkSpeed(i,m);
			}
			else if(set==5)
				computeForceAceticAcid(i,m);
			else if(set==7){
				computeForceNaHCO3(i,m);
				computeForceFromWater(i,m);	
			}
			}
			
		}
		
		//Apply force function for Unit
		public void applyForce(int sim, int set)
		{
			for (int i = 0; i < molecules.size(); i++) {
				Molecule m = molecules.get(i);
				if (m!=null && !p5Canvas.isDrag){
					if (!m.getName().equals("Water") ){
						applyForceUnit2(i,m);
					}
				}	
			}
		}

		@Override
		public boolean addMolecules(boolean isAppEnable, String compoundName,
				int count) {
			boolean res = false;
			/* Compounds status check */
			float freezingTem = DBinterface.getCompoundFreezingPointCelsius(compoundName);
			if (p5Canvas.temp<=freezingTem){
				if (compoundName.equals("Sodium-Chloride"))
					res = add2Ions("Sodium-Ion","Chlorine-Ion",count, box2d, p5Canvas);
				else if (compoundName.equals("Silicon-Dioxide"))
					res = addSiO2(compoundName,count, box2d, p5Canvas); 
				else if (compoundName.equals("Calcium-Chloride"))
					res = addCalciumChloride(compoundName,count, box2d, p5Canvas); 
				else if (compoundName.equals("Sodium-Bicarbonate"))
					res = addNaHCO3(compoundName,count, box2d, p5Canvas); 
				else if (compoundName.equals("Potassium-Chloride"))
					res = add2Ions("Potassium-Ion","Chlorine-Ion",count, box2d, p5Canvas); 
				else	
					res = addSolid(compoundName,count);
			}
			else{
				if(compoundName.equals("Glycerol")||compoundName.equals("Pentane"))
					res = addGlycerol(compoundName, count, box2d, p5Canvas);
				else
				res = addWaterMolecules(isAppEnable,compoundName,count);
				
			}
			// TODO Auto-generated method stub
			return res;
		}
		
		/******************************************************************
		* FUNCTION :     addSolid
		* DESCRIPTION :  Specific function used to add addSolid, Called by addMolecule()
		*
		* INPUTS :       CompoundName(String), count(int)
		* OUTPUTS:       None
		*******************************************************************/
		public boolean addSolid(String compoundName, int count) {
			boolean res = false;
			int numRow = (int) (Math.ceil(count/6.)+1);
			
			float centerX = p5Canvas.x + 200 ;                              //X coordinate around which we are going to add Ions, 50 is border width
			float centerY = p5Canvas.y + 80-Boundary.difVolume;             //Y coordinate around which we are going to add Ions
			
			for (int i=0;i<count;i++){
				float x_,y_,angle;
				Vec2 size = Molecule.getShapeSize(compoundName, p5Canvas);
				x_ =centerX+ (i/numRow)*2*size.x;
				y_ =centerY+(numRow-1-i%numRow)*2*size.y;
				if ((i%numRow)%2==0){
					angle = 0;
				}
				else{
					angle = (float) Math.PI;
				}
				molecules.add(new Molecule(x_, y_,compoundName, 
						box2d, p5Canvas,angle));
				res = true;
			}
			return res;
		}



		@Override
		public void beginReaction(Contact c) {
			// TODO Auto-generated method stub
			
		}
}
