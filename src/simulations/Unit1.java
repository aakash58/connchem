package simulations;

import static data.State.molecules;

import java.util.ArrayList;
import java.util.Random;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;

import data.State;

import simulations.models.Boundary;
import simulations.models.Compound;
import simulations.models.Molecule;
import simulations.models.Simulation;
import simulations.models.Water;
import simulations.models.Simulation.SpawnStyle;

public class Unit1 extends UnitBase {
	private Water waterComputation;

	public Unit1(P5Canvas parent, PBox2D box) {
		super(parent, box);
		// TODO Auto-generated constructor stub
		unitNum = 1;
		setupSimulations();
		waterComputation = new Water(p5Canvas);
	}

	@Override
	public void setupSimulations() {
		// TODO Auto-generated method stub
		simulations = new Simulation[SIMULATION_NUMBER];

		simulations[0] = new Simulation(unitNum, 1, 1);
		String[] elements0 = { "Water" };
		SpawnStyle[] spawnStyles0 = { SpawnStyle.Solvent };
		simulations[0].setupElements(elements0, spawnStyles0);

		simulations[1] = new Simulation(unitNum, 2, 1);
		String[] elements1 = { "Water" };
		SpawnStyle[] spawnStyles1 = { SpawnStyle.Solvent };
		simulations[1].setupElements(elements1, spawnStyles1);

	}

	@Override
	public void updateMolecules(int sim, int set) {
		// TODO Auto-generated method stub
		reactH202();
	}

	private void reactH202() {
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
			Molecule m1 = (Molecule) p5Canvas.killingList.get(0);
			Molecule m2 = (Molecule) p5Canvas.killingList.get(1);
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				Vec2 loc = m1.getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);
				Molecule m = new Molecule(newVec.x, newVec.y,
						p5Canvas.products.get(i), box2d, p5Canvas, 0);
				molecules.add(m);
				if (i == 0)
					m.body.setLinearVelocity(m1.body.getLinearVelocity());

				else {
					m.body.setLinearVelocity(m2.body.getLinearVelocity());
				}
			}
			m1.killBody();
			m2.killBody();
			molecules.remove(m1);
			molecules.remove(m2);
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
		}
	}

	@Override
	protected void reset() {
		// TODO Auto-generated method stub
		p5Canvas.temp = 25;
		switch (p5Canvas.getMain().selectedSim) {
		case 1:
			break;
		case 2:
			if (p5Canvas.getMain().selectedSet == 2) // Hydrogen-Peroxide
				p5Canvas.temp = 75;

			else if (p5Canvas.getMain().selectedSet == 4) // Mercury
				p5Canvas.temp = 100;
			else if (p5Canvas.getMain().selectedSet == 6) // Silver
				p5Canvas.temp = 100;

			break;
		case 3:
			break;
		case 4:
			break;
		}

	}

	public void beginReaction(Contact c) {
		reactAfterContact(c);
	}

	@Override
	protected void computeForce(int sim, int set) {
		
		switch(sim)
		{
		case 1:
		case 3:
		case 4:
			computeForceGeneric();
			break;
		case 2:
			if(set==7)
				this.computeForceSiO2();
			else
				computeForceGeneric();
			break;
		case 5:
			computeForceGeneric();
			break;
		
		}

	}
	
	public void computeForceGeneric()
	{
		Molecule moleThis = null;
		Vec2 locThis = new Vec2();
		Molecule moleOther = null;
		Vec2 locOther = new Vec2();
		float xValue = 0;
		float yValue = 0;
		float forceX = 0;
		float forceY = 0;
		float dis = 0;
		// float scale = 3000;

		for (int i = 0; i < State.molecules.size(); i++) {

			moleThis = State.molecules.get(i);
			for (int thisE = 0; thisE < moleThis.getNumElement(); thisE++) { // Select

				moleThis.sumForceX[thisE] = 0;
				moleThis.sumForceY[thisE] = 0;
			}
			
			for (int k = 0; k < State.molecules.size(); k++) {
				moleOther = State.molecules.get(k);
				if (k == i || !moleThis.getName().equals(moleOther.getName()))
					//Only have forces on the same kind of molecule
					continue;
				
				locOther = moleOther.getPosition();
				locThis = moleThis.getPosition();
				if (locOther == null || locThis == null)
					continue;
				float x = locThis.x - locOther.x;
				float y = locThis.y - locOther.y;
				float disSquare = x * x + y * y;
				Vec2 direction = normalizeForce(new Vec2(x, y));
				if (moleThis.getName().equals( moleOther.getName())) {
					float fTemp = moleThis.freezingTem;
					float bTemp = moleThis.boilingTem;
					float gravityX, gravityY;
					if (p5Canvas.temp >= bTemp) { // Gas case
						gravityX = 0;
						gravityY = 0;
					} else if (p5Canvas.temp <= fTemp) { // Solid case
						gravityY = (bTemp - p5Canvas.temp) / (bTemp - fTemp);
						gravityX = gravityY * 2f;
					} else { // Liquid case
						gravityY = (bTemp - p5Canvas.temp) / (bTemp - fTemp);
						gravityX = gravityY * 0.6f;
					}
					forceX = (-direction.x / disSquare)
							* moleOther.getBodyMass() * moleThis.getBodyMass()
							* gravityX;
					forceY = (-direction.y / disSquare)
							* moleOther.getBodyMass() * moleThis.getBodyMass()
							* gravityY;
				}

				for (int thisE = 0; thisE < moleThis.getNumElement(); thisE++) { 

					// Water case
					if (moleThis.getName().equals("Water")) {
						if (thisE == 2) {
							moleThis.sumForceX[thisE] += forceX * 3000;
							moleThis.sumForceY[thisE] += forceY * 3000;
						}
					}
					// Hydrogen-Peroxide case
					else if (moleThis.getName().equals("Hydrogen-Peroxide")) {
						if (thisE == 2 || thisE == 3) {
							moleThis.sumForceX[thisE] += forceX / 2 * 3000;
							moleThis.sumForceY[thisE] += forceY / 2 * 3000;
						}
					} else if (moleThis.getName().equals("Pentane")) // No force
																		// applied
																		// on
																		// Pentane
					{
						/*
						 * if( thisE ==4) { moleThis.sumForceX[thisE]+=forceX;
						 * moleThis.sumForceY[thisE]+=forceY; }
						 */
					}
					// Silver case
					else if (moleThis.getName().equals("Silver")) {
						moleThis.sumForceX[thisE] += forceX * 200;
						moleThis.sumForceY[thisE] += forceY * 200;
					}
					// Silicon Dioxide case
					else if (moleThis.getName().equals("Silicon-Dioxide")) {
						moleThis.sumForceX[thisE] += forceX * 100;
						moleThis.sumForceY[thisE] += forceY * 100;
					} 
					// Silicon Dioxide case
					else if (moleThis.getName().equals("Mercury")) {
						moleThis.sumForceX[thisE] += forceX * 50;
						moleThis.sumForceY[thisE] += forceY * 30;
					} 
					else if (moleThis.getName().equals("Bromine")) {
						moleThis.sumForceX[thisE] += forceX * 50;
						moleThis.sumForceY[thisE] += forceY * 30;
					} 
					
					else {
						moleThis.sumForceX[thisE] += forceX * 30;
						moleThis.sumForceY[thisE] += forceY * 30;
					}

			

				}

			}
		}
	}

	public void applyForce(int sim, int set) {
		super.applyForce(sim, set);
	}

	/*
	private void setForce(int index, Molecule moleThis) { // draw background
		for (int i = 0; i < molecules.size(); i++) {
			if (i == index)
				continue;

			Molecule moleOther = molecules.get(i);
			Vec2 locOther = moleOther.getPosition();
			Vec2 locThis = moleThis.getPosition();
			if (locOther == null || locThis == null)
				continue;
			float x = locThis.x - locOther.x;
			float y = locThis.y - locOther.y;
			float disSquare = x * x + y * y;
			Vec2 direction = normalizeForce(new Vec2(x, y));
			float forceX = 0;
			float forceY = 0;

			if (moleThis.polarity == moleOther.polarity) {
				float fTemp = moleThis.freezingTem;
				float bTemp = moleThis.boilingTem;
				float gravityX, gravityY;
				if (p5Canvas.temp >= bTemp) { // Gas case
					gravityX = 0;
					gravityY = 0;
				} else if (p5Canvas.temp <= fTemp) { // Solid case
					gravityY = (bTemp - p5Canvas.temp) / (bTemp - fTemp);
					gravityX = gravityY * 2f;
				} else { // Liquid case
					gravityY = (bTemp - p5Canvas.temp) / (bTemp - fTemp);
					gravityX = gravityY * 0.6f;
				}
				forceX = (-direction.x / disSquare) * moleOther.getBodyMass()
						* moleThis.getBodyMass() * gravityX * 3000;
				forceY = (-direction.y / disSquare) * moleOther.getBodyMass()
						* moleThis.getBodyMass() * gravityY * 3000;
			} else {
				float num = moleOther.getNumElement();
				forceX = (direction.x / disSquare) * moleOther.getBodyMass()
						* moleThis.getBodyMass() * 300 * num;
				forceY = (direction.y / disSquare) * moleOther.getBodyMass()
						* moleThis.getBodyMass() * 300 * num;
			}
			// moleThis.addForce(new Vec2(forceX,forceY));

		}
	}
*/
	public Vec2 normalizeForce(Vec2 v) {
		float dis = (float) Math.sqrt(v.x * v.x + v.y * v.y);
		return new Vec2(v.x / dis, v.y / dis);
	}

	@Override
	public boolean addMolecules(boolean isAppEnable, String compoundName,
			int count) {
		// TODO Auto-generated method stub
		boolean res = false;
		/*
		 * int sim = p5Canvas.getMain().selectedSim; int set =
		 * p5Canvas.getMain().selectedSet; Simulation simulation =
		 * this.getSimulation(sim, set); SpawnStyle spawnStyle =
		 * simulation.getSpawnStyle(compoundName); if (spawnStyle ==
		 * SpawnStyle.Gas) { res = this.addGasMolecule(isAppEnable,
		 * compoundName, count); } else if (spawnStyle == SpawnStyle.Solvent){
		 * res = this.addSolvent(isAppEnable, compoundName, count, simulation);
		 * }
		 */
		// TO DO: Check if molecules are in gas or water
		if (compoundName.equals("Silicon-Dioxide"))
			res = addSiO2(compoundName,count, box2d, p5Canvas); 
		else
			res = addWaterMolecules(isAppEnable, compoundName, count);

		return res;
	}

	/******************************************************************
	 * FUNCTION : addPentane DESCRIPTION : Specific function used to add Pentane
	 * 
	 * INPUTS : CompoundName(String), count(int),
	 * box2d_(PBox2D),parent_(P5Canvas) OUTPUTS: None
	 *******************************************************************/
	public boolean addPentane(String compoundName, int count) {

		boolean res = true;
		int creationCount = 0;

		if (p5Canvas.isEnable) // if Applet is enable
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
		centerX = p5Canvas.x + moleWidth / 2;
		centerY = p5Canvas.y + moleHeight - Boundary.difVolume;
		topLeft = new Vec2(centerX - 0.5f * moleWidth, centerY - 0.5f
				* moleHeight);
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
				topLeft = new Vec2(centerX - moleWidth / 2, centerY
						- moleHeight);
				botRight = new Vec2(centerX + colNum * moleWidth, centerY
						+ rowNum * moleHeight);

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
	 * FUNCTION : reactAfterContact DESCRIPTION : react function after collision
	 * detected Called by beginContact()
	 * 
	 * INPUTS : c( Contact) OUTPUTS: None
	 *******************************************************************/
	private void reactAfterContact(Contact c) {
		// Get our objects that reference these bodies
		Object o1 = c.m_fixtureA.m_body.getUserData();
		Object o2 = c.m_fixtureB.m_body.getUserData();

		if (o1 == null || o2 == null)
			return;
		// What class are they? Box or Particle?
		String c1 = o1.getClass().getName();
		String c2 = o2.getClass().getName();
		if (c1.contains("Molecule") && c2.contains("Molecule")) {
			Molecule m1 = (Molecule) o1;
			Molecule m2 = (Molecule) o2;
			ArrayList<String> reactants = new ArrayList<String>();
			reactants.add(m1.getName());
			reactants.add(m2.getName());
			if (p5Canvas.temp >= 110) {
				float random = p5Canvas.random(110, 210);
				if (random < p5Canvas.temp) {
					p5Canvas.products = getReactionProducts(reactants);
					if (p5Canvas.products != null
							&& p5Canvas.products.size() > 0) {
						p5Canvas.killingList.add(m1);
						p5Canvas.killingList.add(m2);
					}
				}
			}
		}

	}

	/******************************************************************
	 * FUNCTION : getReactionProducts DESCRIPTION : Reture objects based on
	 * input name Called by beginContact
	 * 
	 * INPUTS : reactants (Array<String>) OUTPUTS: None
	 *******************************************************************/
	private ArrayList<String> getReactionProducts(ArrayList<String> reactants) {
		if (reactants.get(0).equals("Hydrogen-Peroxide")
				&& reactants.get(1).equals("Hydrogen-Peroxide")) {
			ArrayList<String> products = new ArrayList<String>();
			products.add("Water");
			products.add("Water");
			products.add("Oxygen");
			return products;
		} else {
			return null;
		}
	}

	@Override
	public void setupReactionProducts(int sim, int set) {
		// TODO Auto-generated method stub
		if ((sim == 2 && set == 2) || (sim == 4 && set == 1)) {
			Compound.names.add("Water");
			Compound.counts.add(0);
			Compound.names.add("Oxygen");
			Compound.counts.add(0);
		}
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

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

	public void computeForceSiO2() { // draw
		// background
		for (int n = 0; n < molecules.size(); n++) {
			Molecule mole = molecules.get(n);
		for (int e = 0; e < mole.getNumElement(); e++) {
			int indexCharge = mole.elementCharges.get(e);
			Vec2 locIndex = mole.getElementLocation(e);
			mole.sumForceX[e] = 0;
			mole.sumForceY[e] = 0;
			for (int i = 0; i < molecules.size(); i++) {
				if (i == n)
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
						mole.sumForceX[e] += mul * forceX;
						mole.sumForceY[e] += mul * forceY;
					} else if (mul > 0) {
						mole.sumForceX[e] += mul * forceX * mole.chargeRate;
						mole.sumForceY[e] += mul * forceY * mole.chargeRate;
					}
				}
			}
		}
	}
	}

}
