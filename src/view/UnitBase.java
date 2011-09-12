/**
 * 
 */
package view;

import static model.State.molecules;

import java.util.Random;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.DistanceJointDef;

import java.util.Random;

/**
 * @author Qin Li UnitBase class is base class of all Units class. Some simple
 *         functions implemented in this class
 * 
 */
public abstract class UnitBase {
	protected P5Canvas p5Canvas;
	protected PBox2D box2d;

	public UnitBase(P5Canvas parent, PBox2D box) {
		p5Canvas = parent;
		box2d = box;

	}

	/******************************************************************
	 * FUNCTION : addMolecules DESCRIPTION : Function to add molecules to
	 * PApplet, vary in different Unit
	 * 
	 * INPUTS : isAppEnable(boolean), compoundName(String), count(int) OUTPUTS:
	 * None
	 *******************************************************************/
	public abstract boolean addMolecules(boolean isAppEnable,
			String compoundName, int count);

	/******************************************************************
	 * FUNCTION : addWaterMolecules DESCRIPTION : Function to add water
	 * molecules to PApplet Only do space check when spawn
	 * 
	 * INPUTS : isAppEnable(boolean), compoundName(String), count(int) OUTPUTS:
	 * None
	 *******************************************************************/
	public boolean addWaterMolecules(boolean isAppEnable, String compoundName,
			int count) {
		boolean res = false;
		int creationCount = 0;

		if (isAppEnable) // if Applet is enable
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
		int leftBorder = 40;// Left padding
		int offsetX = 0; // X offset from left border to 3/4 width of canvas
		Random rand = null;

		Vec2 size = Molecule.getShapeSize(compoundName, p5Canvas);

		float moleWidth = size.x;
		float moleHeight = size.y;
		boolean isFit = false;
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

		// Check if there are enough space for water spawn,
		// in case that water molecules will not going out of screen
		while (!isFit) {
			rand = new Random();
			offsetX = rand.nextInt((int) ((p5Canvas.w / 5) * 4));
			centerX = p5Canvas.x + leftBorder + offsetX;
			centerY = p5Canvas.y + 80 - Boundary.difVolume + (mod - 1.5f) * 20;
			topLeft.set(centerX, centerY);
			botRight.set(centerX + colNum * moleWidth, centerY + rowNum
					* moleHeight);
			if (topLeft.x > p5Canvas.x && botRight.x < p5Canvas.x + p5Canvas.w
					&& topLeft.y > p5Canvas.y
					&& botRight.y < p5Canvas.y + p5Canvas.h)
				isFit = true;
		}

		// Add molecules into p5Canvas
		for (int i = 0; i < count; i++) {

			x_ = centerX + i % dimension * moleWidth + creationCount;
			y_ = centerY + i / dimension * moleHeight;

			res = molecules.add(new Molecule(x_, y_, compoundName, box2d,
					p5Canvas, 0));
		}

		return res;

	}

	/******************************************************************
	 * FUNCTION : addSingleIon DESCRIPTION : Function to add single ion to
	 * PApplet Do area clear check when spawn
	 * 
	 * INPUTS : isAppEnable(boolean), compoundName(String), count(int) OUTPUTS:
	 * None
	 *******************************************************************/
	public boolean addSingleIon(boolean isAppEnable, String compoundName,
			int count) {

		boolean res = true;
		int creationCount = 0;

		if (isAppEnable) // if Applet is enable
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
		float increX = p5Canvas.w / 3;

		// Initializing
		centerX = p5Canvas.x + 50;
		centerY = p5Canvas.y + 80 - Boundary.difVolume;
		topLeft = new Vec2(centerX - 0.5f * size.x, centerY - 0.5f * size.y);
		botRight = new Vec2(centerX + colNum * size.x, centerY + rowNum
				* size.y);
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
				topLeft = new Vec2(centerX - 0.5f * size.x, centerY - 0.5f
						* size.y);
				botRight = new Vec2(centerX + colNum * size.x, centerY + rowNum
						* size.y);

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

	/******************************************************************
	 * FUNCTION : addSolidMolecule 
	 * DESCRIPTION : Function to add solid molecules to PApplet 
	 * Do area clear check when spawn
	 * 
	 * INPUTS : isAppEnable(boolean), compoundName(String), count(int) OUTPUTS:
	 * None
	 *******************************************************************/
	public boolean addSolidMolecule(boolean isAppEnable, String compoundName,
			int count) {
		boolean res = true;

		float centerX = 0; // X Coordinate around which we are going to add
							// molecules
		float centerY = 0; // Y Coordinate around which we are going to add
							// molecules
		float x_ = 0; // X Coordinate for a specific molecule
		float y_ = 0; // Y Coordinate for a specific molecule
		int dimension = 0; // Decide molecule cluster is 2*2 or 3*3
		float offsetX = 0; // Offset x from left border
		int leftBorder = 0; // left padding from left border
		int startIndex = molecules.size(); // Start index of this group in
											// molecules arraylist
		Vec2 size = Molecule.getShapeSize(compoundName, p5Canvas);

		float moleWidth = size.x;
		float moleHeight = size.y;
		float jointLength = size.y;
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
		float increX = p5Canvas.w / 3;

		offsetX = p5Canvas.w / 2 - (colNum * moleWidth) / 2;
		centerX = p5Canvas.x + leftBorder + offsetX;
		centerY = p5Canvas.y + p5Canvas.h - rowNum * moleHeight
				- Boundary.difVolume;

		for (int i = 0; i < count; i++) {
			x_ = centerX + i % dimension * moleWidth;
			y_ = centerY + i / dimension * moleHeight;
			res = molecules.add(new Molecule(x_, y_, compoundName, box2d,
					p5Canvas, 0));

		}

		/* Add joint for solid molecules */
		if (count > 1) {
			int index1 = 0;
			int index2 = 0;
			Molecule m1 = null;
			Molecule m2 = null;
			
			for (int i = 0; i < count; i++) {
				/* In horizontal direction, all molecules create a joint connecting to its right next molecule */
				if ( (i+1)% dimension !=0 && (i!=count-1)) /* right most molecules */
				{
					index1 = i + startIndex;
					index2 = i+1 + startIndex;
					m1 = molecules.get(index1);
					m2 = molecules.get(index2);
					joint2Elements(index1, index2, m1, m2,jointLength);
				}
				/* In vertical direction, all molecules create a joint connecting to its down next molecule */
				if( ((i/dimension+1)!=rowNum) && ((i+dimension)<count)) /* bottom most molecules */
				{
					index1 = i + startIndex;
					index2 = i+dimension + startIndex;
					m1= molecules.get(index1);
					m2 = molecules.get(index2);
					joint2Elements(index1,index2,m1,m2,jointLength);
				}
				/* In diagonal direction, all molecules create a joint connecting to its bottom right molecule */
				if( ((i+1)%dimension !=0)&&((i+dimension+1)<count))
				{
					index1 = i+startIndex;
					index2 = i+ dimension +1 + startIndex;
					m1 = molecules.get(index1);
					m2 = molecules.get(index2);
					joint2Elements(index1,index2,m1,m2,jointLength*1.5f);
				}
				/* In diagonal direction, all molecules create a joint connecting to its top right molecule */
				if( (i-dimension+1)>=0 && (i+1)%dimension !=0 )
				{
					index1 = i+startIndex;
					index2 = i- dimension +1 + startIndex;
					m1 = molecules.get(index1);
					m2 = molecules.get(index2);
					joint2Elements(index1,index2,m1,m2,jointLength*1.5f);
				}
			}
		}

		return res;
	}

	/******************************************************************
	 * FUNCTION : areaBodyCheck DESCRIPTION : Check whether pos is in area
	 * (topLeft,botRight), return true if yes
	 * 
	 * INPUTS : pos(Vec2), topLeft(Vec2), botRight(Vec2) OUTPUTS: boolean
	 *******************************************************************/
	private boolean areaBodyCheck(Vec2 pos, Vec2 topLeft, Vec2 botRight) {
		boolean res = false;
		if (pos.x > topLeft.x && pos.x < botRight.x && pos.y > topLeft.y
				&& pos.y < botRight.y) {
			res = true;

		}
		return res;
	}

	/******************************************************************
	 * FUNCTION : joint2Element DESCRIPTION : Binding two elements together by
	 * creating joints between them
	 * 
	 * 
	 * INPUTS : index1(int), index2(int), m1(Molecule),
	 * m2(Molecule),length(float),frequency(float) 
	 * OUTPUTS: void
	 *******************************************************************/
	public void joint2Elements(int index1, int index2, Molecule m1, Molecule m2,
			float length, float frequency) {
		DistanceJointDef djd = new DistanceJointDef();
		djd.bodyA = m1.body;
		djd.bodyB = m2.body;
		djd.length = PBox2D.scalarPixelsToWorld(length);
		djd.frequencyHz = frequency;
		djd.dampingRatio = 1.0f;
		DistanceJoint dj = (DistanceJoint) PBox2D.world.createJoint(djd);

		/* Save joint reference */
		m1.compoundJoint.add(dj);
		m2.compoundJoint.add(dj);
		/* Save the other element`s index */
		m1.compoundJointPair.add(index2);
		m2.compoundJointPair.add(index1);
	}

	/******************************************************************
	 * FUNCTION : joint2Element DESCRIPTION : Bing two elements together by
	 * creating joints between them Default function length = Molecule.clRadius,
	 * frequency = 0
	 * 
	 * INPUTS : index1(int), index2(int), m1(Molecule), m2(Molecule) OUTPUTS:
	 * void
	 *******************************************************************/
	public void joint2Elements(int index1, int index2, Molecule m1, Molecule m2) {
		float length = 2 * Molecule.clRadius;
		joint2Elements(index1, index2, m1, m2, length, 5);
	}
	public void joint2Elements(int index1, int index2, Molecule m1, Molecule m2, float length) {
		joint2Elements(index1, index2, m1, m2, length, 5);
	}
	
	/******************************************************************
	 * FUNCTION : beginReaction 
	 * DESCRIPTION : Dummy function is to be implemented in children classes
	 * 
	 * INPUTS : c ( Contact)
	 * OUTPUTS: None
	 *******************************************************************/
	public void beginReaction(Contact c)
	{
		
	}


}
