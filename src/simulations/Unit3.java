/**
 * 
 */
package simulations;

import static model.State.molecules;

import java.util.ArrayList;
import java.util.Random;

import model.DBinterface;
import model.State;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.DistanceJoint;

import simulations.models.Anchor;
import simulations.models.Boundary;
import simulations.models.Compound;
import simulations.models.DistanceJointWrap;
import simulations.models.Molecule;
import simulations.models.Simulation;
import simulations.models.Simulation.SpawnStyle;

/**
 * @author Qin Li The Unit3 class provides all specific computations encountered
 *         in Unit 3 simulation, Chemical Reactions Only be called by P5Canvas
 *         object
 */
public class Unit3 extends UnitBase {

	private float sodiumJointLength;
	private int frameCounter = 0;
	private int computeTriggerInterval = 30;
	private boolean isAnchorSetup = false;

	public Unit3(P5Canvas parent, PBox2D box) {
		super(parent, box);
		// TODO Auto-generated constructor stub
		simulations = new Simulation[SIMULATION_NUMBER];
		unitNum = 3;
		setupSimulations();
	}

	@Override
	public void setupSimulations() {
		// TODO Auto-generated method stub
		simulations = new Simulation[SIMULATION_NUMBER];

		simulations[0] = new Simulation(unitNum, 1, 1);
		String[] elements0 = { "Sodium", "Chlorine" };
		SpawnStyle[] spawnStyles0 = { SpawnStyle.SolidPavement, SpawnStyle.Gas };
		simulations[0].setupElements(elements0, spawnStyles0);

		simulations[1] = new Simulation(unitNum, 1, 2);
		String[] elements1 = { "Hydrogen-Iodide" };
		SpawnStyle[] spawnStyles1 = { SpawnStyle.Gas };
		simulations[1].setupElements(elements1, spawnStyles1);

		simulations[2] = new Simulation(unitNum, 1, 3);
		String[] elements2 = { "Ethene", "Oxygen" };
		SpawnStyle[] spawnStyles2 = { SpawnStyle.Gas, SpawnStyle.Gas };
		simulations[2].setupElements(elements2, spawnStyles2);

		simulations[3] = new Simulation(unitNum, 1, 4);
		String[] elements3 = { "Copper", "Silver-Nitrate", "Water" };
		SpawnStyle[] spawnStyles3 = { SpawnStyle.SolidPavement,
				SpawnStyle.Precipitation, SpawnStyle.Solvent };
		simulations[3].setupElements(elements3, spawnStyles3);

		simulations[4] = new Simulation(unitNum, 1, 5);
		String[] elements4 = { "Methane", "Oxygen" };
		SpawnStyle[] spawnStyles4 = { SpawnStyle.Gas, SpawnStyle.Gas };
		simulations[4].setupElements(elements4, spawnStyles4);

		simulations[5] = new Simulation(unitNum, 1, 6);
		String[] elements5 = { "Iron", "Copper-II-Sulfate", "Water" };
		SpawnStyle[] spawnStyles5 = { SpawnStyle.SolidPavement,
				SpawnStyle.Precipitation, SpawnStyle.Solvent };
		simulations[5].setupElements(elements5, spawnStyles5);

		simulations[6] = new Simulation(unitNum, 1, 7);
		String[] elements6 = { "Hydrogen-Ion","Chloride", "Lithium-Sulfide", "Water" };
		SpawnStyle[] spawnStyles6 = { SpawnStyle.Solvent,SpawnStyle.Solvent,
				SpawnStyle.SolidSpecial, SpawnStyle.Solvent };
		simulations[6].setupElements(elements6, spawnStyles6);

		simulations[7] = new Simulation(unitNum, 1, 8);
		String[] elements7 = { "Hydrogen", "Chlorine" };
		SpawnStyle[] spawnStyles7 = { SpawnStyle.Gas, SpawnStyle.Gas };
		simulations[7].setupElements(elements7, spawnStyles7);

		simulations[8] = new Simulation(unitNum, 1, 9);
		String[] elements8 = { "Hydrogen-Peroxide" };
		SpawnStyle[] spawnStyles8 = { SpawnStyle.Gas };
		simulations[8].setupElements(elements8, spawnStyles8);

		simulations[9] = new Simulation(unitNum, 1, 10);
		String[] elements9 = { "Silver-Nitrate", "Sodium-Chloride", "Water" };
		SpawnStyle[] spawnStyles9 = { SpawnStyle.Precipitation,
				SpawnStyle.Precipitation, SpawnStyle.Solvent };
		simulations[9].setupElements(elements9, spawnStyles9);
		
		simulations[10] = new Simulation(unitNum,2,1);
		String[] elements10 = { "Potassium-Bromide", "Silver-Nitrate","Ammonium-Chloride","Sodium-Carbonate","Sodium-Hydroxide","Lithium-Nitrate", "Water" };
		SpawnStyle[] spawnStyles10 = { SpawnStyle.Precipitation,
				SpawnStyle.Precipitation,SpawnStyle.Precipitation,SpawnStyle.Precipitation,SpawnStyle.Precipitation,SpawnStyle.Precipitation, SpawnStyle.Solvent };
		simulations[10].setupElements(elements10, spawnStyles10);
		/*
		 * // Get Silver-Chloride size for setting anchors Vec2 size =
		 * Molecule.getShapeSize("Silver-Chloride", p5Canvas); int anchorNum =
		 * 5; Vec2 anchorPos[] = new Vec2[anchorNum]; float centerX =
		 * (p5Canvas.w - anchorNum * size.x/2) / 2; for (int i = 0; i <
		 * anchorNum; i++) { anchorPos[i] = new Vec2(); anchorPos[i].x =
		 * p5Canvas.x + centerX + size.x / 2 * i; anchorPos[i].y = p5Canvas.y +
		 * p5Canvas.h - size.y;
		 * 
		 * } simulations[9].setupAnchors(anchorNum, anchorPos);
		 */

	}

	@Override
	protected SpawnStyle getSpawnStyle(int selectedSim, int selectedSet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateMolecules(int sim, int set) {
		// TODO Auto-generated method stub
		int unit = 3;
		boolean reactionHappened = false; //Boolean flag indicating is reactions have taken place

		Simulation simulation = this.getSimulation(sim, set);
		if (sim == 1) {
			switch (set) {
			case 1:
				reactionHappened = this.reactNaCl(simulation);
				break;
			case 2:
			case 3:
			case 5:
			case 8:
			case 9:
				reactionHappened = this.reactGeneric(simulation);
				break;
			case 4:
				reactionHappened = reactCopperToSilver(simulation);
				break;
			case 6:
				reactionHappened = reactIronToCopper(simulation);
				break;
			case 7:
				reactionHappened = reactLi2S(simulation);
				break;
			case 10:
				reactionHappened = reactAgNo3AndNacl(simulation);
				break;

			}
		}
		else if (sim ==2)
		{
			reactionHappened = reactSolubility(simulation);
		}
		
		//If reaction has taken place,update molecule counts to render correct chart
		if(reactionHappened)
		{
		//Get reaction input from DB
		ArrayList<String> input = DBinterface.getReactionInputs(unit, sim, set);
		int decreaseNum = 0;
		int resultNum = 0;
		int index = -1;
		for(String compound: input)
		{
			index = -1;
			index = Compound.names.indexOf(compound);
			if( index >=0 ) //Decrease products count by num
			{
				decreaseNum = DBinterface.getReactionCompoundsNum(unit, sim, set, compound);
				if( decreaseNum!=-1)
				{
				resultNum = Compound.counts.get(index)-decreaseNum;
				if( resultNum <0)
					resultNum=0;
				Compound.counts.set(index, resultNum);
				}
			}
			
		}
		//Get reaction output from DB
		ArrayList<String> products = DBinterface.getReactionOutputs(unit, sim, set);
		int increaseNum = 0;
		resultNum = 0;
		index = -1;
		for(String compound: products)
		{
			index = -1;
			index = Compound.names.indexOf(compound);
			if( index >=0 ) //Increase products count by num
			{
				increaseNum = DBinterface.getReactionCompoundsNum(unit, sim, set, compound);
				if(increaseNum!=-1)
				{
				resultNum = Compound.counts.get(index)+increaseNum;
				Compound.counts.set(index, resultNum);
				}
			}
			
		}
		}

	}

	public boolean addMolecules(boolean isAppEnable, String compoundName,
			int count) {
		boolean res = false;

		int sim = p5Canvas.getMain().selectedSim;
		int set = p5Canvas.getMain().selectedSet;
		Simulation simulation = this.getSimulation(sim, set);
		SpawnStyle spawnStyle = simulation.getSpawnStyle(compoundName);
		if (spawnStyle == SpawnStyle.Gas) {
			res = this.addGasMolecule(isAppEnable, compoundName, count);
		} else if (spawnStyle == SpawnStyle.Liquid) {
			res = this.addSingleIon(isAppEnable, compoundName, count);
		} else if (spawnStyle == SpawnStyle.Solvent) {
			res = this.addSolvent(isAppEnable, compoundName, count, simulation);
		} else if (spawnStyle == SpawnStyle.Precipitation) // Dissolvable
															// compound spawn
															// like
															// precipitation
		{
			res = this.addPrecipitation(isAppEnable, compoundName, count,
					simulation, (float) Math.PI);
		} else if (spawnStyle == SpawnStyle.SolidPavement) {
			res = this.addSolidMoleculeSodium(isAppEnable, compoundName, count,
					simulation);
		} else if (spawnStyle == SpawnStyle.SolidSpecial) {
			if (compoundName.equals("Lithium-Sulfide"))
				res = this.addSolidLi2S(isAppEnable, compoundName, count,
						simulation);
		}

		return res;
	}

	/******************************************************************
	 * FUNCTION : addSolidMoleculeSodium DESCRIPTION : Function to add Sodium
	 * molecules to PApplet The molecule alignment is different from that of
	 * general solid
	 * 
	 * INPUTS : isAppEnable(boolean), compoundName(String), count(int) OUTPUTS:
	 * None
	 *******************************************************************/
	public boolean addSolidMoleculeSodium(boolean isAppEnable,
			String compoundName, int count, Simulation simulation) {
		boolean res = true;

		// TODO: Add style parameter Cube or paved
		// Style depends how the solid molecules would be aligned
		SpawnStyle spawnStyle = simulation.getSpawnStyle(compoundName);

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
		sodiumJointLength = jointLength;
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

		if (spawnStyle == SpawnStyle.SolidCube) {
			// Create molecules align in cube pattern
			for (int i = 0; i < count; i++) {
				if ((i / dimension) % 2 == 0) /* Odd line */
				{
					x_ = centerX + i % dimension * moleWidth * 1.4f;
				} else /* even line */
				{
					x_ = centerX + 0.7f * moleWidth + i % dimension * moleWidth
							* 1.4f;
				}

				y_ = centerY + i / dimension * moleHeight;
				res = molecules.add(new Molecule(x_, y_, compoundName, box2d,
						p5Canvas, (float) (Math.PI / 2)));

			}

			/* Add joint for solid molecules */
			if (count > 1) {
				int index1 = 0;
				int index2 = 0;
				Molecule m1 = null;
				Molecule m2 = null;
				float xInterval = jointLength * 1.8f;

				for (int i = 0; i < count; i++) {
					/*
					 * In horizontal direction, all molecules create a joint
					 * connecting to its right next molecule
					 */
					if ((i + 1) % dimension != 0 && (i != count - 1)) /*
																	 * right
																	 * most
																	 * molecules
																	 */
					{
						index1 = i + startIndex;
						index2 = i + 1 + startIndex;
						m1 = molecules.get(index1);
						m2 = molecules.get(index2);
						joint2Elements(m1, m2, jointLength * 1.8f);
					}
					/*
					 * In vertical direction, all molecules create a joint
					 * connecting to its down next molecule
					 */
					if (((i / dimension + 1) != rowNum)
							&& ((i + dimension) < count)) /* bottom most molecules */
					{
						index1 = i + startIndex;
						index2 = i + dimension + startIndex;
						m1 = molecules.get(index1);
						m2 = molecules.get(index2);
						joint2Elements(m1, m2, jointLength * 1.4f);
					}
					/*
					 * In diagonal direction, all molecules create a joint
					 * connecting to its bottom right molecule
					 */
					if (((i + 1) % dimension != 0)
							&& ((i + dimension + 1) < count)
							&& (i / dimension) % 2 != 0) {
						index1 = i + startIndex;
						index2 = i + dimension + 1 + startIndex;
						m1 = molecules.get(index1);
						m2 = molecules.get(index2);
						joint2Elements(m1, m2, jointLength * 1.4f);
					}
					/*
					 * In diagonal direction, all molecules create a joint
					 * connecting to its top right molecule
					 */
					if ((i - dimension + 1) >= 0 && (i + 1) % dimension != 0
							&& (i / dimension) % 2 != 0) {
						index1 = i + startIndex;
						index2 = i - dimension + 1 + startIndex;
						m1 = molecules.get(index1);
						m2 = molecules.get(index2);
						joint2Elements(m1, m2, jointLength * 1.4f);
					}
				}
			}
		} else if (spawnStyle == SpawnStyle.SolidPavement)// Create molecules
															// align in a paved
															// way
		{
			dimension = 7;
			float xInterval = moleWidth * 2.0f;
			float lineSpace = moleHeight * 1.25f;
			rowNum = (int) Math.ceil((double) count / dimension);
			// SPECIFIED spawn location designed for particular molecules
			if (compoundName.equals("Sodium"))
				offsetX = p5Canvas.w / 2
						- ((dimension - 1) * (xInterval) + moleWidth) / 2;
			else if (compoundName.equals("Copper")
					|| compoundName.equals("Iron")) {
				dimension = 5;
				xInterval = moleWidth * 1.5f;
				offsetX = p5Canvas.w
						- ((dimension - 1) * (xInterval) + moleWidth);
				lineSpace = moleHeight * 1.0f;
			}
			centerX = p5Canvas.x + offsetX;
			for (int i = 0; i < count; i++) {
				if ((i / dimension) % 2 == 0) /* Odd line */
				{
					x_ = centerX + i % dimension * xInterval;
				} else /* even line */
				{
					x_ = centerX + 0.7f * moleWidth + i % dimension * xInterval;
				}

				centerY = p5Canvas.y + p5Canvas.h - rowNum * lineSpace
						- Boundary.difVolume;
				y_ = centerY + i / dimension * lineSpace;
				res = molecules.add(new Molecule(x_, y_, compoundName, box2d,
						p5Canvas, (float) (Math.PI / 2)));
				if ((i / dimension) != 0) // If molecules are at the bottom, we
											// set them as inreactive ones
					molecules.get(molecules.size() - 1).setReactive(false);

			}
			// Add molecule which is at bottom right of this molecule to
			// neighbor list of this molecule
			int thisIndex = 0;
			int neighborIndex = 0;
			Molecule thisMolecule = null;
			Molecule neighborMolecule = null;

			for (int i = 0; i < count; i++) {
				if ((i + dimension) < count) // bottom most molecules
				{
					thisIndex = i + startIndex;
					neighborIndex = i + dimension + startIndex;
					thisMolecule = molecules.get(thisIndex);
					neighborMolecule = molecules.get(neighborIndex);
					thisMolecule.neighbors.add(neighborMolecule);
				}
			}

			/* Add joint for solid molecules */
			if (count > 1) {
				int index1 = 0;
				int index2 = 0;
				Molecule m1 = null;
				Molecule m2 = null;
				float frequency = 5;
				float damp = 0.4f;
				float jointLen = 3.0f;
				float xp = 0;
				float yp = 0;

				for (int i = 0; i < count; i++) {

					/* For every molecule, create a anchor to fix its position */
					index1 = i + startIndex;
					m1 = molecules.get(index1);
					Vec2 m1Pos = box2d.coordWorldToPixels(m1.getPosition());
					Anchor anchor = new Anchor(m1Pos.x, m1Pos.y, box2d,
							p5Canvas);
					State.anchors.add(anchor);
					joint2Elements(m1, anchor, jointLen, frequency, damp);
					/*
					 * In horizontal direction, all molecules create a joint
					 * connecting to its right next molecule
					 */
					/*
					 * if ((i + 1) % dimension != 0 && (i != count - 1)) //
					 * right most molecules
					 * 
					 * { index1 = i + startIndex; index2 = i + 1 + startIndex;
					 * m1 = molecules.get(index1); m2 = molecules.get(index2);
					 * joint2Elements(m1, m2, xInterval,frequency,damp);
					 * 
					 * 
					 * } /* In vertical direction, all molecules create a joint
					 * connecting to its down next molecule
					 */
					/*
					 * if (((i / dimension + 1) != rowNum) && ((i + dimension) <
					 * count)) // bottom most molecules { index1 = i +
					 * startIndex; index2 = i + dimension + startIndex; m1 =
					 * molecules.get(index1); m2 = molecules.get(index2);
					 * joint2Elements(m1, m2, jointLength *
					 * 1.4f,frequency,damp); } /* In diagonal direction, all
					 * molecules create a joint connecting to its bottom right
					 * molecule
					 */
					/*
					 * if (((i + 1) % dimension != 0) && ((i + dimension + 1) <
					 * count) && (i / dimension) % 2 != 0) { index1 = i +
					 * startIndex; index2 = i + dimension + 1 + startIndex; m1 =
					 * molecules.get(index1); m2 = molecules.get(index2);
					 * joint2Elements(m1, m2, jointLength *
					 * 1.4f,frequency,damp); } /* In diagonal direction, all
					 * molecules create a joint connecting to its top right
					 * molecule
					 */
					/*
					 * if ((i - dimension + 1) >= 0 && (i + 1) % dimension != 0
					 * && (i / dimension) % 2 != 0) { index1 = i + startIndex;
					 * index2 = i - dimension + 1 + startIndex; m1 =
					 * molecules.get(index1); m2 = molecules.get(index2);
					 * joint2Elements(m1, m2, jointLength *
					 * 1.4f,frequency,damp); }
					 */
				}

			}
		}

		return res;
	}

	/******************************************************************
	 * FUNCTION : addSolidLi2S DESCRIPTION : Function to add Sodium molecules
	 * Li2S
	 * 
	 * INPUTS : isAppEnable(boolean), compoundName(String), count(int) OUTPUTS:
	 * None
	 *******************************************************************/
	public boolean addSolidLi2S(boolean isAppEnable, String compoundName,
			int count, Simulation simulation) {
		boolean res = true;

		// Li2S has fixed molecule number 6

		float centerX = 0; // X Coordinate around which we are going to add
							// molecules
		float centerY = 0; // Y Coordinate around which we are going to add
							// molecules
		float x_ = 0; // X Coordinate for a specific molecule
		float y_ = 0; // Y Coordinate for a specific molecule
						// molecules arraylist
		Vec2 size = Molecule.getShapeSize(compoundName, p5Canvas);

		float angle = 0;
		float moleWidth = size.x;
		float moleHeight = size.y;

		// offsetX = p5Canvas.w / 2 - (colNum * moleWidth) / 2;
		centerX = (p5Canvas.x + p5Canvas.w) / 2;
		centerY = p5Canvas.y + p5Canvas.h - (moleHeight + moleWidth)
				- Boundary.difVolume;
		float lineSpace = moleHeight / 2 + moleWidth / 2;
		
		int startIndex = State.molecules.size()-1;
		if( startIndex==-1)
			startIndex = 0;

		for (int i = 0; i < count; i++) {
			x_ = centerX + lineSpace * (i % 3 - 1);
			y_ = centerY + i / 3 * lineSpace;
			angle = (i % 2 == 0) ? 0.0f : ((float) Math.PI / 2);
			res = molecules.add(new Molecule(x_, y_, compoundName, box2d,
					p5Canvas, angle));
			State.molecules.get(molecules.size()-1).body.setFixedRotation(true);

		}
		float jointLength =0; ;
		float frequency = 15;
		float damp = 0;
		Molecule m1 = null;
		Molecule m2 = null;
		
		//Create joints in horizontal direction
		jointLength = moleHeight/2 +moleWidth/2;
		for (int i = 0; i < count; i++) {
			if((i+1)%3!=0)
			{
			m1 = State.molecules.get(startIndex + i);
			m2 = State.molecules.get(startIndex + i+1);
			joint2Elements(m1, m2, jointLength, frequency, damp);
			}
		}
		//Create joints in vertical direction
		for (int i = 0; i < count/2; i++) {
		m1 = State.molecules.get(startIndex + i);
		m2 = State.molecules.get(startIndex + i+3);
		joint2Elements(m1, m2, jointLength, frequency, damp);
		}
		
		//Create joints in diagonal direction
		jointLength = (float) (Math.sqrt(2)*(moleHeight/2 +moleWidth/2));
		m1 = State.molecules.get(startIndex + 0);
		m2 = State.molecules.get(startIndex + 4);
		joint2Elements(m1, m2, jointLength, frequency, damp);
		
		m1 = State.molecules.get(startIndex + 1);
		m2 = State.molecules.get(startIndex + 3);
		joint2Elements(m1, m2, jointLength, frequency, damp);
		
		m1 = State.molecules.get(startIndex + 1);
		m2 = State.molecules.get(startIndex + 5);
		joint2Elements(m1, m2, jointLength, frequency, damp);
		
		m1 = State.molecules.get(startIndex + 2);
		m2 = State.molecules.get(startIndex + 4);
		joint2Elements(m1, m2, jointLength, frequency, damp);
		
		
		return res;
	}

	/******************************************************************
	 * FUNCTION : beginReaction DESCRIPTION : Reaction function happens after
	 * collision
	 * 
	 * INPUTS : c ( Contact) OUTPUTS: None
	 *******************************************************************/
	public void beginReaction(Contact c) {

		// If there are some molecules have not been killed yet.
		// We skip this collision
		if (!p5Canvas.killingList.isEmpty())
			return;
		// Get our objects that reference these bodies
		Object o1 = c.m_fixtureA.m_body.getUserData();
		Object o2 = c.m_fixtureB.m_body.getUserData();

		int sim = p5Canvas.getMain().selectedSim;
		int set = p5Canvas.getMain().selectedSet;
		Simulation simulation = getSimulation(sim, set);

		if (o1 == null || o2 == null)
			return;
		// TODO: Get reaction elements based on Simulation object parameter
		String c1 = o1.getClass().getName();
		String c2 = o2.getClass().getName();

		// Make sure reaction only takes place between molecules or ions
		if (c1.contains("Molecule") && c2.contains("Molecule")) {
			Molecule m1 = (Molecule) o1;
			Molecule m2 = (Molecule) o2;

			// Check if both of these two molecules are reactive
			if (m1.getReactive() && m2.getReactive()) {

				ArrayList<String> reactants = new ArrayList<String>();
				reactants.add(m1.getName());
				reactants.add(m2.getName());
				if (true) { /* TODO: Maybe there are some conditions */

					p5Canvas.products = getReactionProducts(reactants, m1, m2);
					if (p5Canvas.products != null
							&& p5Canvas.products.size() > 0) {
						/*
						 * If there are some new stuff in newProducts, kill old
						 * ones and add new ones
						 */
						p5Canvas.killingList.add(m1);
						p5Canvas.killingList.add(m2);

					}

				}
			}
			// If inreactive molecules collide
			else if (!m1.getReactive() && !m2.getReactive()) {
				// If one of these two molecules is a water molecule
				// Handle dissolution
				if ((m1.getName().equals("Water") && !m2.getName().equals(
						"Water"))
						|| (!m1.getName().equals("Water") && m2.getName()
								.equals("Water"))) {

					ArrayList<String> collider = new ArrayList<String>();
					if (m1.getName().equals("Water")) {
						collider.add(m2.getName());
						p5Canvas.products = getDissolutionProducts(collider);
						if (p5Canvas.products.size() > 0) {
							p5Canvas.killingList.add(m2);
						}
					} else {
						collider.add(m1.getName());
						p5Canvas.products = getDissolutionProducts(collider);
						if (p5Canvas.products.size() > 0) {
							p5Canvas.killingList.add(m1);
						}
					}

				}
			}
		}

	}

	/******************************************************************
	 * FUNCTION : reactNaCl DESCRIPTION : Reaction for Sim 1 Set
	 * 
	 * INPUTS : simulation(Simulation) OUTPUTS: None
	 *******************************************************************/
	public boolean reactNaCl(Simulation simulation) {

		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
			Molecule m1 = (Molecule) p5Canvas.killingList.get(0);
			Molecule m2 = (Molecule) p5Canvas.killingList.get(1);

			Molecule mNew = null;
			Molecule mNew2 = null;

			// Actually there is only one reaction going in each frame
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				Vec2 loc = m1.getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * 0.77f
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);
				mNew = new Molecule(newVec.x, newVec.y,
						p5Canvas.products.get(i), box2d, p5Canvas,
						(float) (Math.PI / 2));
				molecules.add(mNew);
				mNew.body.setFixedRotation(true);
				if (i == 0)
					mNew.body.setLinearVelocity(m1.body.getLinearVelocity());

				else {
					mNew.body.setLinearVelocity(m2.body.getLinearVelocity());
				}
			}

			// Get joints to which this molecule is connecting to
			ArrayList<DistanceJointWrap> m1Joint = m1.destroy();
			ArrayList<DistanceJointWrap> m2Joint = m2.destroy();
			Molecule molecule1 = null;
			Molecule molecule2 = null;
			// ArrayList<Molecule> neighborMolecules = new
			// ArrayList<Molecule>();
			Molecule sodium = null;

			Anchor anchorTarget = null;
			Molecule jointTarget = null;
			// Get joint length and frequency
			float length = 0;
			float frequency = 0;
			float damp = 0;
			if (m1Joint == null) // m2 is Sodium
			{
				sodium = m2;
				for (int m = 0; m < m2Joint.size(); m++) {
					if (m2Joint.get(m).getBodyA().getUserData() instanceof Molecule)
						anchorTarget = (Anchor) m2Joint.get(m).getBodyB()
								.getUserData();
					else
						anchorTarget = (Anchor) m2Joint.get(m).getBodyA()
								.getUserData();

					// Create new joints between reaction created molecule and
					// old molecules
					length = PBox2D.scalarWorldToPixels(m2Joint.get(m)
							.getLength());
					frequency = m2Joint.get(m).getFrequency();
					damp = m2Joint.get(m).getDampingRatio();
					joint2Elements(mNew, anchorTarget, length, frequency, damp);

				}
			} else // m1 is Sodium
			{
				sodium = m1;
				for (int m = 0; m < m1Joint.size(); m++) {
					if (m1Joint.get(m).getBodyA().getUserData() instanceof Molecule)
						anchorTarget = (Anchor) m1Joint.get(m).getBodyB()
								.getUserData();
					else
						anchorTarget = (Anchor) m1Joint.get(m).getBodyA()
								.getUserData();

					// Create new joints between reaction created molecule and
					// old molecules
					length = PBox2D.scalarWorldToPixels(m1Joint.get(m)
							.getLength());
					frequency = m1Joint.get(m).getFrequency();
					damp = m1Joint.get(m).getDampingRatio();
					joint2Elements(mNew, anchorTarget, length, frequency, damp);

				}

			}

			// After we killed current sodium and created a sodium-Chloride at
			// the same location
			// We need to pick another sodium in its neighbor which is the
			// closest to reacted Chlorine

			if (!sodium.neighbors.isEmpty()) {

				Molecule secondSodium = null;

				// secondSodium = compareDistance(chlorine, neighborMolecules);
				// secondSodium = pickRightBottomOne(mNew, neighborMolecules);
				secondSodium = sodium.neighbors.get(0);

				// Create a new Sodium-Chloride
				Vec2 loc = secondSodium.getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * 0.77f
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);
				mNew2 = new Molecule(newVec.x, newVec.y, mNew.getName(), box2d,
						p5Canvas, (float) (Math.PI / 2));
				molecules.add(mNew2);
				mNew2.body.setFixedRotation(true);
				mNew2.body.setLinearVelocity(secondSodium.body
						.getLinearVelocity());

				// Get joints to which this molecule is connecting to
				ArrayList<DistanceJointWrap> sodiumJoint = secondSodium
						.destroy();
				
				
				
				// Create new joints for new molecules
				for (int m = 0; m < sodiumJoint.size(); m++) {

					if (sodiumJoint.get(m).getBodyA().getUserData() instanceof Molecule)
						anchorTarget = (Anchor) sodiumJoint.get(m).getBodyB()
								.getUserData();
					else
						anchorTarget = (Anchor) sodiumJoint.get(m).getBodyA()
								.getUserData();

					// Create new joints between reaction created molecule and
					// existing anchors
					length = PBox2D.scalarWorldToPixels(sodiumJoint.get(m)
							.getLength());
					frequency = sodiumJoint.get(m).getFrequency();
					damp = sodiumJoint.get(m).getDampingRatio();
					joint2Elements(mNew2, anchorTarget, length, frequency, damp);

				}
			}

			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			return true;
		}
		return false;

	}

	/******************************************************************
	 * FUNCTION : reactCopperToSilver DESCRIPTION : Reaction for Sim 1 Set 4.
	 * Silver swap with copper
	 * 
	 * INPUTS : simulation(Simulation) OUTPUTS: None
	 *******************************************************************/
	public boolean reactCopperToSilver(Simulation simulation) {

		if (p5Canvas.killingList.isEmpty())
			return false;
		// If it is dissolving process
		if (p5Canvas.killingList.get(0).getName().equals("Silver-Nitrate")) {
			if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

				int numToKill = p5Canvas.killingList.size();
				Molecule[] mOld = new Molecule[numToKill];
				for (int i = 0; i < numToKill; i++)
					mOld[i] = (Molecule) p5Canvas.killingList.get(i);

				Molecule mNew = null;
				Molecule mNew2 = null;
				float offsetX = 0; // Set an offset to spawn position to make it
									// look real

				// Actually there is only one reaction going in each frame
				for (int i = 0; i < p5Canvas.products.size(); i++) {
					Vec2 loc = mOld[0].getPosition();
					float x1;
					offsetX = mOld[0].getMaxSize() / 5;
					if (p5Canvas.products.get(i).equals("Silver-Ion")) // Set an
																		// offset
																		// x for
																		// silver-ion
						x1 = PBox2D.scalarWorldToPixels(loc.x)
								+ mOld[0].getMaxSize() / 2 - offsetX;
					else
						x1 = PBox2D.scalarWorldToPixels(loc.x) - offsetX;
					float y1 = p5Canvas.h * 0.77f
							- PBox2D.scalarWorldToPixels(loc.y);
					Vec2 newVec = new Vec2(x1, y1);
					mNew = new Molecule(newVec.x, newVec.y,
							p5Canvas.products.get(i), box2d, p5Canvas,
							(float) (Math.PI / 2));

					molecules.add(mNew);

					if (i == 0)
						mNew.body.setLinearVelocity(mOld[0].body
								.getLinearVelocity());

					else {
						mNew.body.setLinearVelocity(mOld[0].body
								.getLinearVelocity());
					}
				}
				for (int i = 0; i < numToKill; i++)
					mOld[i].destroy();

				p5Canvas.products.clear();
				p5Canvas.killingList.clear();
			}
		} else { //If this is reaction process
			if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
				Molecule copper = null;
				Molecule silverIon = null;
				if (p5Canvas.killingList.get(0).getName().equals("Copper")) {
					copper = (Molecule) p5Canvas.killingList.get(0);
					silverIon = (Molecule) p5Canvas.killingList.get(1);
				} else {
					silverIon = (Molecule) p5Canvas.killingList.get(0);
					copper = (Molecule) p5Canvas.killingList.get(1);
				}

				Molecule newCopperII = null;
				Molecule newSilver = null;

				Vec2 loc = null;

				// Actually there is only one reaction going in each frame
				for (int i = 0; i < p5Canvas.products.size(); i++) {
					if (p5Canvas.products.get(i).equals("Silver"))
						loc = copper.getPosition();
					else
						loc = silverIon.getPosition();
					float x1 = PBox2D.scalarWorldToPixels(loc.x);
					float y1 = p5Canvas.h * 0.77f
							- PBox2D.scalarWorldToPixels(loc.y);
					Vec2 newVec = new Vec2(x1, y1);
					if (p5Canvas.products.get(i).equals("Silver")) {
						newSilver = new Molecule(newVec.x, newVec.y,
								p5Canvas.products.get(i), box2d, p5Canvas,
								(float) (Math.PI / 2));
						molecules.add(newSilver);
						newSilver.body.setLinearVelocity(silverIon.body
								.getLinearVelocity());
					} else // If new molecules is copper-II
					{
						newCopperII = new Molecule(newVec.x, newVec.y,
								p5Canvas.products.get(i), box2d, p5Canvas,
								(float) (Math.PI / 2));
						molecules.add(newCopperII);
						newCopperII.body.setLinearVelocity(copper.body
								.getLinearVelocity());

					}

				}

				// Get joints to which this molecule is connecting to
				ArrayList<DistanceJointWrap> mJoint = copper.destroy();
				ArrayList<DistanceJointWrap> m2Joint = silverIon.destroy();
				Molecule molecule1 = null;
				Molecule molecule2 = null;

				Anchor anchorTarget = null;
				Molecule jointTarget = null;
				// Get joint length and frequency
				float length = 0;
				float frequency = 0;
				float damp = 0;

				// copper = m2;
				for (int m = 0; m < mJoint.size(); m++) {
					if (mJoint.get(m).getBodyA().getUserData() instanceof Molecule)
						anchorTarget = (Anchor) mJoint.get(m).getBodyB()
								.getUserData();
					else
						anchorTarget = (Anchor) mJoint.get(m).getBodyA()
								.getUserData();

					// Create new joints between reaction created molecule and
					// old molecules
					length = PBox2D.scalarWorldToPixels(mJoint.get(m)
							.getLength());
					frequency = mJoint.get(m).getFrequency();
					damp = mJoint.get(m).getDampingRatio();
					joint2Elements(newSilver, anchorTarget, length, frequency,
							damp);

				}

				p5Canvas.products.clear();
				p5Canvas.killingList.clear();
				return true;
			}
		}
		return false;

	}

	/******************************************************************
	 * FUNCTION : reactCopperToSilver DESCRIPTION : Reaction for Sim 1 Set 4.
	 * Silver swap with copper
	 * 
	 * INPUTS : simulation(Simulation) OUTPUTS: None
	 *******************************************************************/
	public boolean reactIronToCopper(Simulation simulation) {

		if (p5Canvas.killingList.isEmpty())
			return false;
		// If it is dissolving process
		if (p5Canvas.killingList.get(0).getName().equals("Copper-II-Sulfate")) {
			if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

				int numToKill = p5Canvas.killingList.size();
				Molecule[] mOld = new Molecule[numToKill];
				for (int i = 0; i < numToKill; i++)
					mOld[i] = (Molecule) p5Canvas.killingList.get(i);

				Molecule mNew = null;
				Molecule mNew2 = null;
				float offsetX = 0; // Set an offset to spawn position to make it
									// look real

				// Actually there is only one reaction going in each frame
				for (int i = 0; i < p5Canvas.products.size(); i++) {
					Vec2 loc = mOld[0].getPosition();
					float x1;
					offsetX = mOld[0].getMaxSize() / 5;
					if (p5Canvas.products.get(i).equals("Copper-II")) // Set an
																		// offset
																		// x for
																		// silver-ion
						x1 = PBox2D.scalarWorldToPixels(loc.x)
								+ mOld[0].getMaxSize() / 2 - offsetX;
					else
						x1 = PBox2D.scalarWorldToPixels(loc.x) - offsetX;
					float y1 = p5Canvas.h * 0.77f
							- PBox2D.scalarWorldToPixels(loc.y);
					Vec2 newVec = new Vec2(x1, y1);
					mNew = new Molecule(newVec.x, newVec.y,
							p5Canvas.products.get(i), box2d, p5Canvas,
							(float) (Math.PI / 2));

					molecules.add(mNew);

					if (i == 0)
						mNew.body.setLinearVelocity(mOld[0].body
								.getLinearVelocity());

					else {
						mNew.body.setLinearVelocity(mOld[0].body
								.getLinearVelocity());
					}
				}
				for (int i = 0; i < numToKill; i++)
					mOld[i].destroy();

				p5Canvas.products.clear();
				p5Canvas.killingList.clear();
			}
		} else // Reaction: swap Iron with Copper-II
		{
			if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
				Molecule iron = null;
				Molecule copperIon = null;
				// Get Iron and copperIon reference
				if (p5Canvas.killingList.get(0).getName().equals("Copper-II")) {
					copperIon = (Molecule) p5Canvas.killingList.get(0);
					iron = (Molecule) p5Canvas.killingList.get(1);
				} else {
					iron = (Molecule) p5Canvas.killingList.get(0);
					copperIon = (Molecule) p5Canvas.killingList.get(1);
				}

				Molecule newIronII = null;
				Molecule newCopper = null;

				Vec2 loc = null;

				// Actually there is only one reaction going in each frame
				for (int i = 0; i < p5Canvas.products.size(); i++) {
					if (p5Canvas.products.get(i).equals("Copper"))
						loc = iron.getPosition();
					else
						loc = copperIon.getPosition();
					float x1 = PBox2D.scalarWorldToPixels(loc.x);
					float y1 = p5Canvas.h * 0.77f
							- PBox2D.scalarWorldToPixels(loc.y);
					Vec2 newVec = new Vec2(x1, y1);
					if (p5Canvas.products.get(i).equals("Copper")) {
						newCopper = new Molecule(newVec.x, newVec.y,
								p5Canvas.products.get(i), box2d, p5Canvas,
								(float) (Math.PI / 2));
						molecules.add(newCopper);
						newCopper.body.setLinearVelocity(copperIon.body
								.getLinearVelocity());
					} else // If new molecules is copper-II
					{
						newIronII = new Molecule(newVec.x, newVec.y,
								p5Canvas.products.get(i), box2d, p5Canvas,
								(float) (Math.PI / 2));
						molecules.add(newIronII);
						newIronII.body.setLinearVelocity(iron.body
								.getLinearVelocity());

					}

				}

				// Get joints to which this molecule is connecting to
				ArrayList<DistanceJointWrap> mJoint = iron.destroy();
				ArrayList<DistanceJointWrap> m2Joint = copperIon.destroy();
				Molecule molecule1 = null;
				Molecule molecule2 = null;

				Anchor anchorTarget = null;
				Molecule jointTarget = null;
				// Get joint length and frequency
				float length = 0;
				float frequency = 0;
				float damp = 0;

				// copper = m2;
				for (int m = 0; m < mJoint.size(); m++) {
					if (mJoint.get(m).getBodyA().getUserData() instanceof Molecule)
						anchorTarget = (Anchor) mJoint.get(m).getBodyB()
								.getUserData();
					else
						anchorTarget = (Anchor) mJoint.get(m).getBodyA()
								.getUserData();

					// Create new joints between reaction created molecule and
					// old molecules
					length = PBox2D.scalarWorldToPixels(mJoint.get(m)
							.getLength());
					frequency = mJoint.get(m).getFrequency();
					damp = mJoint.get(m).getDampingRatio();
					joint2Elements(newCopper, anchorTarget, length, frequency,
							damp);

				}

				p5Canvas.products.clear();
				p5Canvas.killingList.clear();
				return true;
			}
		}
		return false;

	}

	/******************************************************************
	 * FUNCTION : reactAgNo3AndNacl DESCRIPTION : Reaction for Sim 1 Set 10.
	 * AgNo3 reacts with Nacl
	 * 
	 * INPUTS : simulation(Simulation) OUTPUTS: None
	 *******************************************************************/
	public boolean reactAgNo3AndNacl(Simulation simulation) {

		
		if (!p5Canvas.killingList.isEmpty()) {
			// If it is dissolving process
			if (p5Canvas.killingList.get(0).getName().equals("Silver-Nitrate")
					|| p5Canvas.killingList.get(0).getName()
							.equals("Sodium-Chloride")) {
				if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

					int numToKill = p5Canvas.killingList.size();
					Molecule[] mOld = new Molecule[numToKill];
					for (int i = 0; i < numToKill; i++)
						mOld[i] = (Molecule) p5Canvas.killingList.get(i);

					Molecule mNew = null;
					Molecule mNew2 = null;
					float offsetX = 0; // Set an offset to spawn position to
										// make it
										// look real

					// Actually there is only one reaction going in each frame
					for (int i = 0; i < p5Canvas.products.size(); i++) {
						Vec2 loc = mOld[0].getPosition();
						float x1;
						offsetX = mOld[0].getMaxSize() / 5;
						if (p5Canvas.products.get(i).equals("Silver-Ion")
								|| p5Canvas.products.get(i)
										.equals("Sodium-Ion")) // Set
																// an
																// offset
																// x
																// for
																// silver-ion
							x1 = PBox2D.scalarWorldToPixels(loc.x)
									+ mOld[0].getMaxSize() / 2 - offsetX;
						else
							x1 = PBox2D.scalarWorldToPixels(loc.x) - offsetX;
						float y1 = p5Canvas.h * 0.77f
								- PBox2D.scalarWorldToPixels(loc.y);
						Vec2 newVec = new Vec2(x1, y1);
						mNew = new Molecule(newVec.x, newVec.y,
								p5Canvas.products.get(i), box2d, p5Canvas,
								(float) (Math.PI / 2));

						molecules.add(mNew);

						if (i == 0)
							mNew.body.setLinearVelocity(mOld[0].body
									.getLinearVelocity());

						else {
							mNew.body.setLinearVelocity(mOld[0].body
									.getLinearVelocity());
						}
					}
					for (int i = 0; i < numToKill; i++)
						mOld[i].destroy();

					p5Canvas.products.clear();
					p5Canvas.killingList.clear();
				}
			} else // Reaction: Silver-Ion reacts with Chlorine, generate
					// Silver-Chloride
			{
				if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
					Molecule silverIon = null;
					Molecule chloride = null;
					// Get Iron and copperIon reference
					if (p5Canvas.killingList.get(0).getName()
							.equals("Silver-Ion")) {
						silverIon = (Molecule) p5Canvas.killingList.get(0);
						chloride = (Molecule) p5Canvas.killingList.get(1);
					} else {
						chloride = (Molecule) p5Canvas.killingList.get(0);
						silverIon = (Molecule) p5Canvas.killingList.get(1);
					}

					Molecule silverChloride = null;

					Vec2 loc = null;

					// Actually there is only one reaction going in each frame
					for (int i = 0; i < p5Canvas.products.size(); i++) {
						loc = silverIon.getPosition();
						float x1 = PBox2D.scalarWorldToPixels(loc.x);
						float y1 = p5Canvas.h * 0.77f
								- PBox2D.scalarWorldToPixels(loc.y);
						Vec2 newVec = new Vec2(x1, y1);

						silverChloride = new Molecule(newVec.x, newVec.y,
								p5Canvas.products.get(i), box2d, p5Canvas,
								(float) (Math.PI / 2));
						molecules.add(silverChloride);
						silverChloride.body.setLinearVelocity(silverIon.body
								.getLinearVelocity());

					}

					silverIon.destroy();
					chloride.destroy();

					// Set up anchors if they have not been set up yet.
					if (!isAnchorSetup) {
						Vec2 pos = null;
						for (int i = 0; i < simulation.getAnchorNum(); i++) {
							pos = simulation.getAnchorPos(i);
							Anchor anchor = new Anchor(pos.x, pos.y, box2d,
									p5Canvas);
							State.anchors.add(anchor);
						}
						isAnchorSetup = true;

					}

					p5Canvas.products.clear();
					p5Canvas.killingList.clear();
					return true;
				}
			}
		}
		return false;


	}
	
	
	/******************************************************************
	 * FUNCTION : reactLi2S DESCRIPTION : Reaction for Sim 1 Set 7.
	 * Li2S reacts with 2HCl
	 * 
	 * INPUTS : simulation(Simulation) OUTPUTS: None
	 *******************************************************************/
	public boolean reactLi2S(Simulation simulation) {
		
		if (p5Canvas.killingList.isEmpty())
			return false  ;
		// If it is dissolving process
		
			if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
				if( p5Canvas.products.contains("Hydrogen-Sulfide"))
				{
				int numToKill = p5Canvas.killingList.size();
				Molecule[] mOld = new Molecule[numToKill];
				for (int i = 0; i < numToKill; i++)
					mOld[i] = (Molecule) p5Canvas.killingList.get(i);

				Molecule mNew = null;
	
				float offsetX = 0; // Set an offset to spawn position to make it
									// look real
				boolean left = true; //Boolean parameter used to set offsetX

				// Actually there is only one reaction going in each frame
				for (int i = 0; i < p5Canvas.products.size(); i++) {
					Vec2 loc = mOld[0].getPosition();
					float x1;
					offsetX = mOld[0].getMaxSize() / 3;
					if (p5Canvas.products.get(i).equals("Lithium-Ion")) // Set an
																		// offset
																		// x for
																		// silver-ion
					{
						if(left)
						{
						x1 = PBox2D.scalarWorldToPixels(loc.x)
								 - offsetX;
							left = false;
						}
						else
						{
							x1 = PBox2D.scalarWorldToPixels(loc.x)
									 + offsetX;
						}
					}
					else
						x1 = PBox2D.scalarWorldToPixels(loc.x); //H2S
					float y1 = p5Canvas.h * 0.77f
							- PBox2D.scalarWorldToPixels(loc.y);
					Vec2 newVec = new Vec2(x1, y1);
					mNew = new Molecule(newVec.x, newVec.y,
							p5Canvas.products.get(i), box2d, p5Canvas,
							(float) (Math.PI / 2));

					molecules.add(mNew);

					if (i == 0)
						mNew.body.setLinearVelocity(mOld[0].body
								.getLinearVelocity());

					else {
						mNew.body.setLinearVelocity(mOld[0].body
								.getLinearVelocity());
					}
				}
				for (int i = 0; i < numToKill; i++)
					mOld[i].destroy();

				p5Canvas.products.clear();
				p5Canvas.killingList.clear();
				return true;
			}
		} 
			return false;
	}
	
	/******************************************************************
	 * FUNCTION : reactSolubility 
	 * DESCRIPTION : Reaction for Sim 2 all sets
	 * 
	 * INPUTS : simulation(Simulation) OUTPUTS: None
	 *******************************************************************/
	public boolean reactSolubility(Simulation simulation) {

		
		if (!p5Canvas.killingList.isEmpty()) {
			// If it is dissolving process
			if (p5Canvas.killingList.get(0).getName().equals("Potassium-Bromide")
					|| p5Canvas.killingList.get(0).getName().equals("Silver-Nitrate") || p5Canvas.killingList.get(0).getName().equals("Ammonium-Chloride")||
					p5Canvas.killingList.get(0).getName().equals("Sodium-Carbonate") ||	p5Canvas.killingList.get(0).getName().equals("Sodium-Hydroxide") ||
					p5Canvas.killingList.get(0).getName().equals("Lithium-Nitrate")) {
				if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

					int numToKill = p5Canvas.killingList.size();
					Molecule[] mOld = new Molecule[numToKill];
					for (int i = 0; i < numToKill; i++)
						mOld[i] = (Molecule) p5Canvas.killingList.get(i);

					Molecule mNew = null;
					Molecule mNew2 = null;
					float offsetX = 0; // Set an offset to spawn position to
										// make it
										// look real

					// Actually there is only one reaction going in each frame
					for (int i = 0; i < p5Canvas.products.size(); i++) {
						Vec2 loc = mOld[0].getPosition();
						float x1;
						offsetX = mOld[0].getMaxSize() / 5;
						// Set an offset x for silver-ion
						if (p5Canvas.products.get(i).equals("Silver-Ion") ||p5Canvas.products.get(i).equals("Potassium-Ion")||p5Canvas.products.get(i).equals("Ammonium")
								|| p5Canvas.products.get(i)
										.equals("Sodium-Ion")||p5Canvas.products.get(i).equals("Lithium-Ion")) 
							x1 = PBox2D.scalarWorldToPixels(loc.x)
									+ mOld[0].getMaxSize() / 2 - offsetX;
						else
							x1 = PBox2D.scalarWorldToPixels(loc.x) - offsetX;
						float y1 = p5Canvas.h * 0.77f
								- PBox2D.scalarWorldToPixels(loc.y);
						Vec2 newVec = new Vec2(x1, y1);
						mNew = new Molecule(newVec.x, newVec.y,
								p5Canvas.products.get(i), box2d, p5Canvas,
								(float) (Math.PI / 2));

						molecules.add(mNew);

						if (i == 0)
							mNew.body.setLinearVelocity(mOld[0].body
									.getLinearVelocity());

						else {
							mNew.body.setLinearVelocity(mOld[0].body
									.getLinearVelocity());
						}
					}
					for (int i = 0; i < numToKill; i++)
						mOld[i].destroy();

					p5Canvas.products.clear();
					p5Canvas.killingList.clear();
				}
			} 
			/*
			else // Reaction: Silver-Ion reacts with Chlorine, generate
					// Silver-Chloride
			{
				if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
					Molecule silverIon = null;
					Molecule chloride = null;
					// Get Iron and copperIon reference
					if (p5Canvas.killingList.get(0).getName()
							.equals("Silver-Ion")) {
						silverIon = (Molecule) p5Canvas.killingList.get(0);
						chloride = (Molecule) p5Canvas.killingList.get(1);
					} else {
						chloride = (Molecule) p5Canvas.killingList.get(0);
						silverIon = (Molecule) p5Canvas.killingList.get(1);
					}

					Molecule silverChloride = null;

					Vec2 loc = null;

					// Actually there is only one reaction going in each frame
					for (int i = 0; i < p5Canvas.products.size(); i++) {
						loc = silverIon.getPosition();
						float x1 = PBox2D.scalarWorldToPixels(loc.x);
						float y1 = p5Canvas.h * 0.77f
								- PBox2D.scalarWorldToPixels(loc.y);
						Vec2 newVec = new Vec2(x1, y1);

						silverChloride = new Molecule(newVec.x, newVec.y,
								p5Canvas.products.get(i), box2d, p5Canvas,
								(float) (Math.PI / 2));
						molecules.add(silverChloride);
						silverChloride.body.setLinearVelocity(silverIon.body
								.getLinearVelocity());

					}

					silverIon.destroy();
					chloride.destroy();

					// Set up anchors if they have not been set up yet.
					if (!isAnchorSetup) {
						Vec2 pos = null;
						for (int i = 0; i < simulation.getAnchorNum(); i++) {
							pos = simulation.getAnchorPos(i);
							Anchor anchor = new Anchor(pos.x, pos.y, box2d,
									p5Canvas);
							State.anchors.add(anchor);
						}
						isAnchorSetup = true;

					}

					p5Canvas.products.clear();
					p5Canvas.killingList.clear();
					return true;
				}
			}
			*/
		}
		return false;


	}

	/******************************************************************
	 * FUNCTION : pickBottomOne DESCRIPTION : Pick a molecule which is under a
	 * reacted molecule Return reference of that picked molecule
	 * 
	 * INPUTS : source(ArrayList<Molecule>) OUTPUTS: Molecule
	 *******************************************************************/
	private Molecule pickRightBottomOne(Molecule source,
			ArrayList<Molecule> neighborMolecules) {
		// TODO Auto-generated method stub
		Molecule res = null;
		if (neighborMolecules.size() < 1)
			return res;
		else if (neighborMolecules.size() == 1)
			return neighborMolecules.get(0);
		else {
			float highY = 0;
			int highIndex = -1;
			Vec2 pos = new Vec2();
			Vec2 posSource = new Vec2(this.box2d.coordWorldToPixels(source
					.getPosition()));
			float y = 0;
			// Go through each molecule in source list and check their position
			// Write down the index of molecule which has lowest y
			for (int i = 0; i < neighborMolecules.size(); i++) {
				pos = this.box2d.coordWorldToPixels(neighborMolecules.get(i)
						.getPosition());
				// Check if this molecule is at right bottom of source molecule
				if (pos.x >= posSource.x && pos.y >= posSource.y) {
					// If it is, compare their y value
					y = pos.y;

					if (y > highY) {
						highY = y;
						highIndex = i;
					}
				}
			}
			res = neighborMolecules.get(highIndex);

		}

		return res;
	}

	/******************************************************************
	 * FUNCTION : compareDistance DESCRIPTION : Compare distances between source
	 * molecules and targets Return reference of one source molecule which is
	 * the closest to target
	 * 
	 * INPUTS : target(Molecule), source(ArrayList<Molecule>) OUTPUTS: Molecule
	 *******************************************************************/
	private Molecule compareDistance(Molecule target, ArrayList<Molecule> source) {
		Molecule res = null;
		if (source.size() < 1)
			return res;
		else if (source.size() == 1)
			return source.get(0);
		else {
			float minDistance = 10000;
			int minIndex = 0;
			float dis = 0;
			// Go through each molecule in source list and calculate their
			// distance from target
			// Write down the index of molecule which has minimum distance
			for (int i = 0; i < source.size(); i++) {
				dis = calculateDistance(target, source.get(i));
				if (dis < minDistance) {
					minDistance = dis;
					minIndex = i;
				}
			}
			res = source.get(minIndex);
		}

		return res;
	}

	private float calculateDistance(Molecule target, Molecule source) {
		float distance = 0;
		Vec2 posTarget = target.getPosition();
		Vec2 posSource = source.getPosition();
		float xDifference = posTarget.x - posSource.x;
		float yDifference = posTarget.y - posSource.y;
		distance = (float) Math.sqrt(xDifference * xDifference + yDifference
				* yDifference);

		return distance;
	}

	/******************************************************************
	 * FUNCTION : getReactionProducts DESCRIPTION : Reture objects based on
	 * input name Called by beginContact
	 * 
	 * INPUTS : reactants (Array<String>) OUTPUTS: None
	 *******************************************************************/
	private ArrayList<String> getReactionProducts(ArrayList<String> reactants,
			Molecule m1, Molecule m2) {
		ArrayList<String> products = new ArrayList<String>();
		// Sim 1 set 1
		if (reactants.contains("Sodium") && reactants.contains("Chlorine")
				&& reactants.size() == 2) {

			products.add("Sodium-Chloride");

		}
		// Sim 1 set 2
		else if (reactants.get(0).equals("Hydrogen-Iodide")
				&& reactants.get(1).equals("Hydrogen-Iodide")
				&& reactants.size() == 2) {
			products.add("Hydrogen");
			products.add("Iodine");

		}
		// Sim 1 set 3
		else if (reactants.contains("Ethene") && reactants.contains("Oxygen")
				&& reactants.size() == 2) {
			products.add("Carbon-Dioxide");
			products.add("Water");

		}
		// Sim 1 set 4
		else if (reactants.contains("Copper")
				&& reactants.contains("Silver-Ion") && reactants.size() == 2) {
			products.add("Copper-II");
			products.add("Silver");
		}
		// Sim1 set 5
		else if (reactants.contains("Methane") && reactants.contains("Oxygen")) {
			float radius = 75;

			// Compute midpoint of collision molecules
			Vec2 v1 = box2d.coordWorldToPixels(m1.getPosition());
			Vec2 v2 = box2d.coordWorldToPixels(m2.getPosition());
			Vec2 midpoint = new Vec2((v1.x + v2.x) / 2, (v1.y + v2.y) / 2);

			// Go through all molecules to check if there are any molecules
			// nearby
			for (int i = 0; i < State.molecules.size(); i++) {

				if (molecules.get(i).getName().equals("Oxygen")
						&& molecules.get(i) != m1 && molecules.get(i) != m2) {
					Vec2 thirdMolecule = box2d.coordWorldToPixels(molecules
							.get(i).getPosition());
					if (radius > computeDistance(midpoint, thirdMolecule)) {
						products.add("Water");
						products.add("Water");
						products.add("Carbon-Dioxide");
						// Need to kill the third molecule
						p5Canvas.killingList.add(molecules.get(i));
						break; // Break after we find one nearby
					}
				}
			}

		}
		// Sim 1 set 6
		else if (reactants.contains("Copper-II") && reactants.contains("Iron")) {
			products.add("Iron-II");
			products.add("Copper");
		}
		// Sim 1 set 7
		else if(reactants.contains("Lithium-Sulfide") && reactants.contains("Chloride"))
		{
			float radius = 200;
			// Compute midpoint of collision molecules
			Vec2 v1 = box2d.coordWorldToPixels(m1.getPosition());
			Vec2 v2 = box2d.coordWorldToPixels(m2.getPosition());
			Vec2 midpoint = new Vec2((v1.x + v2.x) / 2, (v1.y + v2.y) / 2);
			ArrayList<Molecule> chlorides = new ArrayList<Molecule>();
			ArrayList<Molecule> hydrogens = new ArrayList<Molecule>();
			if(m1.getName().equals("Chloride"))
				chlorides.add(m1);
			else
				chlorides.add(m2);
			// Go through all molecules to check if there are any molecules
						// nearby
			for (int i = 0; i < State.molecules.size(); i++) {

				if(chlorides.size()<2)
				{
					if (molecules.get(i).getName().equals("Chloride")
							&& molecules.get(i) != m1 && molecules.get(i) != m2)
					{
						Vec2 loc = box2d.coordWorldToPixels(molecules
								.get(i).getPosition());
						if (radius > computeDistance(midpoint, loc)) {
							chlorides.add(molecules.get(i));
						}
					}
				}
				if(hydrogens.size()<2)
				{
					if (molecules.get(i).getName().equals("Hydrogen-Ion")
							&& molecules.get(i) != m1 && molecules.get(i) != m2)
					{
						Vec2 loc = box2d.coordWorldToPixels(molecules
								.get(i).getPosition());
						if (radius > computeDistance(midpoint, loc)) {
							hydrogens.add(molecules.get(i));
						}
					}
				}
				if (chlorides.size()==2 && hydrogens.size()==2) //We got enough molecules
				{
					break;
				}
			}
			if (chlorides.size()==2 && hydrogens.size()==2) //We got enough molecules
			{
				products.add("Lithium-Ion");
				products.add("Lithium-Ion");
				products.add("Hydrogen-Sulfide");
				//Kill other  2 hydrogen-Ions
				//p5Canvas.killingList.add(chlorides.get(1));
				p5Canvas.killingList.add(hydrogens.get(0));
				p5Canvas.killingList.add(hydrogens.get(0));
			}
		}
		// Sim 1 set 8
		else if (reactants.contains("Hydrogen")
				&& reactants.contains("Chlorine") && reactants.size() == 2) {
			products.add("Hydrochloric-Acid");
			products.add("Hydrochloric-Acid");

		}

		// Sim 1 set 9
		else if (reactants.get(0).equals("Hydrogen-Peroxide")
				&& reactants.get(1).equals("Hydrogen-Peroxide")
				&& reactants.size() == 2) {
			products.add("Oxygen");
			products.add("Water");

		}
		// Sim 1 set 10
		else if (reactants.contains("Silver-Ion")
				&& reactants.contains("Chloride")) {
			products.add("Silver-Chloride");
		}

		else {
			return null;
		}
		return products;
	}

	private float computeDistance(Vec2 v1, Vec2 v2) {
		float dis = 0;
		dis = (v1.x - v2.x) * (v1.x - v2.x) + (v1.y - v2.y) * (v1.y - v2.y);
		dis = (float) Math.sqrt(dis);

		return dis;
	}

	// Compute Distance in pixel between molecule m1 and m2
	private float computeDistance(Molecule m1, Molecule m2) {
		float dis = 0;
		Vec2 v1 = box2d.coordWorldToPixels(m1.getPosition());
		Vec2 v2 = box2d.coordWorldToPixels(m2.getPosition());

		dis = computeDistance(v1, v2);

		return dis;

	}

	/******************************************************************
	 * FUNCTION : getDissolutionProducts DESCRIPTION : Return elements of
	 * reactants
	 * 
	 * INPUTS : reactants (Array<String>) OUTPUTS: None
	 *******************************************************************/
	private ArrayList<String> getDissolutionProducts(ArrayList<String> collider) {
		ArrayList<String> products = new ArrayList<String>();
		//Sim 1 set 4 and sim 2 AgNO3
		if (collider.contains("Silver-Nitrate")) {
			products.add("Silver-Ion");
			products.add("Nitrate");
		} 
		//Sim 1 set 6
		else if (collider.contains("Copper-II-Sulfate")) {
			products.add("Copper-II");
			products.add("Sulfate");
		} 
		//Sim 1 set 10
		else if (collider.contains("Sodium-Chloride")) {
			products.add("Sodium-Ion");
			products.add("Chloride");
		}
		//Sim 2 KBr
		else if (collider.contains("Potassium-Bromide")){
			products.add("Potassium-Ion");
			products.add("Bromine-Ion");
		}
		//Sim 2 NH4Cl
		else if (collider.contains("Ammonium-Chloride")){
			products.add("Ammonium");
			products.add("Chloride");
		}
		//Sim 2 Na2CO3
		else if (collider.contains("Sodium-Carbonate")){
			products.add("Sodium-Ion");
			products.add("Sodium-Ion");
			products.add("Carbonate");
		}
		//Sim 2 NaOH
		else if (collider.contains("Sodium-Hydroxide")){
			products.add("Sodium-Ion");
			products.add("Hydroxide");
		}
		//Sim 2 LiNO3
		else if (collider.contains("Lithium-Nitrate")){
			products.add("Lithium-Ion");
			products.add("Nitrate");
		}
		else {
			// return null;
		}
		return products;

	}

	@Override
	protected void computeForce(int sim, int set) {
		// TODO Auto-generated method stub
		// Set computeForce trigger interval
		// This function is triggered every 10(computeTriggerInterval) frame

		if (sim == 1) {
			switch (set) {
			case 1:
				if (frameCounter >= this.computeTriggerInterval)
					frameCounter = 0;
				if (frameCounter == 0) {
					clearAllMoleculeForce();
					computeForceNaCl();
				}
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				clearAllMoleculeForce();
				computeForceAgCopper();
				break;
			case 5:
				break;
			case 6:
				clearAllMoleculeForce();
				computeForceCopperIron();
				break;
			case 7:
				clearAllMoleculeForce();
				computeForceLi2S();
				break;
			case 8:
				break;
			case 9:
				break;
			case 10:
				clearAllMoleculeForce();
				computeForceSilverChloride();
				break;

			}
		}
		else if (sim ==2)
		{
			clearAllMoleculeForce();
			computeForceSolubility();
		}

	}
	private void clearAllMoleculeForce()
	{
		for(Molecule mole:State.molecules)
		{
			mole.clearForce();
		}
	}

	private void computeForceNaCl() {
		Molecule mole = null;
		Vec2 force = new Vec2();
		Random randX = new Random();
		Random randY = new Random();
		float scale = 2.0f; // How strong the force is

		float randXValue = 0;
		float randYValue = 0;
		boolean randXDir = false;
		boolean randYDir = false;

		for (int i = 0; i < molecules.size(); i++) {
			if (!molecules.get(i).getName().equals("Chlorine")) // Only compute
																// force for
																// solid
			{
				randXValue = randX.nextFloat() * scale;
				randYValue = randY.nextFloat() * scale;
				randXDir = randX.nextBoolean();
				randXValue *= (float) (randXDir ? 1 : -1);
				randYDir = randY.nextBoolean();
				randYValue *= (float) (randYDir ? 1 : -1);
				mole = molecules.get(i);
				for (int e = 0; e < mole.getNumElement(); e++) {

					mole.sumForceX[e] = randXValue;
					mole.sumForceY[e] = randYValue;
				}
			}
		}

		/*
		 * for( int i =0;i< molecules.size(); i++) { if(
		 * molecules.get(i).getName().equals("Chlorine")) //Only compute force
		 * for solid continue; mole = molecules.get(i);
		 * 
		 * if(mole.isSolid()) //Find solid molecules, which are Sodium and
		 * Sodium-Chloride { //Go through all the sub element of this molecule
		 * //for (int e = 0; e < mole.getNumElement(); e++) { for (int e = 0; e
		 * < 1; e++) { int indexCharge = mole.elementCharges.get(e); Vec2
		 * locIndex = mole.getElementLocation(e); mole.sumForceWaterX[e] = 0;
		 * mole.sumForceWaterY[e] = 0; mole.sumForceX[e] = 0; mole.sumForceY[e]
		 * = 0;
		 * 
		 * 
		 * for (int k = 0; k < molecules.size(); k ++) { //Search over all other
		 * molecules if (k == i ||
		 * molecules.get(k).getName().equals("Chlorine")) continue; Molecule m =
		 * molecules.get(k); float forceX; float forceY; //Go through all the
		 * sub element of the other molecule for (int e2 = 0; e2 <
		 * m.getNumElement(); e2++) { Vec2 loc = m.getElementLocation(e2); float
		 * x = locIndex.x - loc.x; float y = locIndex.y - loc.y; float dis = x *
		 * x + y * y; forceX = (float) (x / Math.sqrt(dis))*scale; forceY =
		 * (float) (y / Math.sqrt(dis))*scale;
		 * 
		 * int charge = m.elementCharges.get(e2); int mul = charge *
		 * indexCharge; //Calculate charge difference between two elements
		 * 
		 * 
		 * //If temperature is between 0 and 100, and has a joint connecting to
		 * other molecules? // if ((m.compoundJoint.isEmpty() ||
		 * mole.compoundJoint.isEmpty()) // && 0 < p5Canvas.temp &&
		 * p5Canvas.temp < 100) { // forceX *= 0.05f; // forceY *= 0.05f; // }
		 * 
		 * 
		 * if (mul < 0) { mole.sumForceX[e] += mul * forceX; mole.sumForceY[e]
		 * += mul * forceY; } else if (mul > 0) { mole.sumForceX[e] += mul *
		 * forceX mole.chargeRate; mole.sumForceY[e] += mul * forceY
		 * mole.chargeRate; }
		 * 
		 * } } } } }
		 */
	}

	// Foce computation form sim 1 set 4
	private void computeForceAgCopper() {
		Molecule mole = null;
		Vec2 force = new Vec2();

		float scale = 0.2f; // How strong the force is
		float copperScale = 0.2f;
		float forceYCompensation = 0.05f;
		float gravityCompensation = 0.2f;

		float xValue = 0;
		float yValue = 0;
		float dis = 0;
		float forceX = 0;
		float forceY = 0;
		boolean xDir = false;
		boolean yDir = false;
		Vec2 thisLoc = new Vec2(0, 0);
		Vec2 otherLoc = new Vec2(0, 0);

		for (int i = 0; i < molecules.size(); i++) {
			if (molecules.get(i).getName().equals("Silver-Ion")) // Compute
																	// force for
																	// silver-ion,
																	// in order
																	// to
																	// attract
																	// them to
																	// copper
			{

				mole = molecules.get(i);
				for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // Select
																				// element

					thisLoc.set(mole.getElementLocation(thisE));
					mole.sumForceX[thisE] = 0;
					mole.sumForceY[thisE] = 0;
					for (int k = 0; k < molecules.size(); k++) { // Go check
																	// forces
																	// from
																	// other
																	// molecules
						if (k == i)
							continue;
						Molecule m = molecules.get(k);
						if (m.getName().equals("Copper")) {
							for (int otherE = 0; otherE < m.getNumElement(); otherE++)
								otherLoc.set(m.getElementLocation(otherE));
							if (thisLoc == null || otherLoc == null)
								continue;
							xValue = otherLoc.x - thisLoc.x;
							yValue = otherLoc.y - thisLoc.y;
							dis = (float) Math.sqrt(xValue * xValue + yValue
									* yValue);
							forceX = (float) (xValue / dis) * scale;
							forceY = (float) (yValue / dis) * scale;

							mole.sumForceX[thisE] += forceX;
							mole.sumForceY[thisE] += forceY
									+ forceYCompensation;

						}
					}

				}
			} else if (molecules.get(i).getName().equals("Copper-II")) // Compute
																		// force
																		// for
																		// copper-II,
																		// in
																		// order
																		// to
																		// push
																		// them
																		// away
																		// from
																		// silver
			{
				mole = molecules.get(i);
				for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // Select
																				// element

					thisLoc.set(mole.getElementLocation(thisE));
					mole.sumForceX[thisE] = 0;
					mole.sumForceY[thisE] = 0;
					for (int k = 0; k < molecules.size(); k++) { // Go check
																	// forces
																	// from
																	// other
																	// molecules
						if (k == i)
							continue;
						Molecule m = molecules.get(k);
						if (m.getName().equals("Silver")
								|| m.getName().equals("Copper")) {
							for (int otherE = 0; otherE < m.getNumElement(); otherE++)
								otherLoc.set(m.getElementLocation(otherE));
							if (thisLoc == null || otherLoc == null)
								continue;
							xValue = thisLoc.x - otherLoc.x;
							yValue = thisLoc.y - otherLoc.y;
							dis = (float) Math.sqrt(xValue * xValue + yValue
									* yValue);
							forceX = (float) (xValue / dis) * scale
									* copperScale;
							forceY = (float) (yValue / dis) * scale
									* copperScale;

							mole.sumForceX[thisE] += forceX;
							mole.sumForceY[thisE] += forceY;

						}
					}

				}

			}
			// Check positions of all the molecules, in case they are not going
			// to high
			if (true) {
				mole = molecules.get(i);
				Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());
				if (pos.y < p5Canvas.h / 3 * 2) {
					for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // Select
																					// element

						mole.sumForceX[thisE] += 0;
						mole.sumForceY[thisE] += gravityCompensation * -1;

					}
				}
			}
		}
	}

	// Foce computation form sim 1 set 6
	private void computeForceCopperIron() {
		Molecule mole = null;

		float scale = 0.15f; // How strong the force is
		float ironScale = 0.2f;
		float forceYCompensation = 0.05f;
		float gravityCompensation = 0.2f;

		float xValue = 0;
		float yValue = 0;
		float dis = 0;
		float forceX = 0;
		float forceY = 0;

		Vec2 thisLoc = new Vec2(0, 0);
		Vec2 otherLoc = new Vec2(0, 0);

		for (int i = 0; i < molecules.size(); i++) {
			if (molecules.get(i).getName().equals("Copper-II")) // Compute force
																// for
																// copper-ion,
																// in order to
																// attract them
																// to copper
			{

				mole = molecules.get(i);
				for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // Select
																				// element

					thisLoc.set(mole.getElementLocation(thisE));
					mole.sumForceX[thisE] = 0;
					mole.sumForceY[thisE] = 0;
					for (int k = 0; k < molecules.size(); k++) { // Go check
																	// forces
																	// from
																	// other
																	// molecules
						if (k == i)
							continue;
						Molecule m = molecules.get(k);
						if (m.getName().equals("Iron")) {
							for (int otherE = 0; otherE < m.getNumElement(); otherE++)
								otherLoc.set(m.getElementLocation(otherE));
							if (thisLoc == null || otherLoc == null)
								continue;
							xValue = otherLoc.x - thisLoc.x;
							yValue = otherLoc.y - thisLoc.y;
							dis = (float) Math.sqrt(xValue * xValue + yValue
									* yValue);
							forceX = (float) (xValue / dis) * scale;
							forceY = (float) (yValue / dis) * scale;

							mole.sumForceX[thisE] += forceX;
							mole.sumForceY[thisE] += forceY
									+ forceYCompensation;

						}
					}

				}
			} else if (molecules.get(i).getName().equals("Iron-II")) // Compute
																		// force
																		// for
																		// iron-II,
																		// in
																		// order
																		// to
																		// push
																		// them
																		// away
																		// from
																		// copper
			{
				mole = molecules.get(i);
				for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // Select
																				// element

					thisLoc.set(mole.getElementLocation(thisE));
					mole.sumForceX[thisE] = 0;
					mole.sumForceY[thisE] = 0;
					for (int k = 0; k < molecules.size(); k++) { // Go check
																	// forces
																	// from
																	// other
																	// molecules
						if (k == i)
							continue;
						Molecule m = molecules.get(k);
						if (m.getName().equals("Copper")
								|| m.getName().equals("Iron")) {
							for (int otherE = 0; otherE < m.getNumElement(); otherE++)
								otherLoc.set(m.getElementLocation(otherE));
							if (thisLoc == null || otherLoc == null)
								continue;
							xValue = thisLoc.x - otherLoc.x;
							yValue = thisLoc.y - otherLoc.y;
							dis = (float) Math.sqrt(xValue * xValue + yValue
									* yValue);
							forceX = (float) (xValue / dis) * scale * ironScale;
							forceY = (float) (yValue / dis) * scale * ironScale;

							mole.sumForceX[thisE] += forceX;
							mole.sumForceY[thisE] += forceY;

						}
					}

				}

			}
			// Check position of other molecules, in case they are not going too
			// high
			if (true) {
				mole = molecules.get(i);
				Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());
				if (pos.y < p5Canvas.h / 3 * 2) {
					for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // Select
																					// element

						mole.sumForceX[thisE] += 0;
						mole.sumForceY[thisE] += gravityCompensation * -1;

					}
				}
			}
		}
	}

	// Compute force for sim 1 set 10
	private void computeForceSilverChloride() {
		Molecule thisMole = null;
		Molecule otherMole = null;
		Vec2 thisLoc = new Vec2();
		Vec2 otherLoc = new Vec2();
		float xValue = 0;
		float yValue = 0;
		float dis = 0;
		float forceX = 0;
		float forceY = 0;
		float scale = 0.15f;
		float chlorideScale = 0.1f;
		float anchorScale = 1.5f;
		float forceYCompensation = 0.05f;
		float gravityCompensation = 0.2f;
		float topBoundary = p5Canvas.h / 2;
		Anchor anchor = null;

		for (int i = 0; i < molecules.size(); i++) {
			if (molecules.get(i).getName().equals("Silver-Ion")) // Compute
																	// force for
																	// copper-ion,
																	// in order
																	// to
																	// attract
																	// them to
																	// copper
			{

				thisMole = molecules.get(i);
				for (int thisE = 0; thisE < thisMole.getNumElement(); thisE++) { // Select
																					// element

					thisLoc.set(thisMole.getElementLocation(thisE));
					thisMole.sumForceX[thisE] = 0;
					thisMole.sumForceY[thisE] = 0;
					for (int k = 0; k < molecules.size(); k++) { // Go check
																	// forces
																	// from
																	// other
																	// molecules
						if (k == i)
							continue;
						otherMole = molecules.get(k);
						if (otherMole.getName().equals("Chloride")) // We are
																	// looking
																	// for
																	// chloride
						{
							for (int otherE = 0; otherE < otherMole
									.getNumElement(); otherE++)
								otherLoc.set(otherMole
										.getElementLocation(otherE));
							if (thisLoc == null || otherLoc == null)
								continue;
							xValue = otherLoc.x - thisLoc.x;
							yValue = otherLoc.y - thisLoc.y;
							dis = (float) Math.sqrt(xValue * xValue + yValue
									* yValue);
							forceX = (float) (xValue / dis) * scale;
							forceY = (float) (yValue / dis) * scale;

							// Add attraction force to sodium-Ion
							thisMole.sumForceX[thisE] += forceX;
							thisMole.sumForceY[thisE] += forceY
									+ forceYCompensation;
							// At the same time add attraction force to Chloride
							// In this case, number of both Silver and Chloride
							// elements are 1;
							otherMole.sumForceX[thisE] += forceX * (-1)
									* chlorideScale;
							otherMole.sumForceY[thisE] += (forceY + forceYCompensation)
									* (-1) * chlorideScale;

						}
					}

				}
			}
			// If there are some chloride have been created
			// Pull them to anchors
			else if (molecules.get(i).getName().equals("Silver-Chloride")) {
				float disBoundary = 50;
				float silverChlorideScale = 8f;
				thisMole = molecules.get(i);


				for (int e = 0; e < thisMole.getNumElement(); e++) {
					int indexCharge = thisMole.elementCharges.get(e);
					Vec2 locIndex = thisMole.getElementLocation(e);
					thisMole.sumForceWaterX[e] = 0;
					thisMole.sumForceWaterY[e] = 0;
					thisMole.sumForceX[e] = 0;
					thisMole.sumForceY[e] = 0;
					for (int k = 0; k < molecules.size(); k++) {
						if (k == i)
							continue;
						Molecule m = molecules.get(k);
						if (m.getName().equals("Silver-Chloride")) // Find
																	// another
																	// silver-chloride
						{
							for (int e2 = 0; e2 < m.getNumElement(); e2++) {
								Vec2 loc = m.getElementLocation(e2);
								float x = locIndex.x - loc.x;
								float y = locIndex.y - loc.y;
								dis = (float) Math.sqrt(x * x + y * y);
								forceX = (x / dis) / (float)(Math.sqrt(dis))
										* silverChlorideScale;
								forceY = (y / dis) / (float)(Math.sqrt(dis))
										* silverChlorideScale;

								int charge = m.elementCharges.get(e2);
								int mul = charge * indexCharge;
								// If mul>0 replusive force
								// If mul<0 attractive force
								thisMole.sumForceX[e] += mul * forceX;
								thisMole.sumForceY[e] += mul * forceY;

							}
						}
					}
				}
			}
			// Check position of other molecules, in case they are not going too
			// high
			if (true) {
				thisMole = molecules.get(i);
				Vec2 pos = box2d.coordWorldToPixels(thisMole.getPosition());
				if (pos.y < topBoundary) {
					for (int thisE = 0; thisE < thisMole.getNumElement(); thisE++) { // Select
																						// element

						thisMole.sumForceX[thisE] += 0;
						thisMole.sumForceY[thisE] += (gravityCompensation) * -1;

					}
				}
			}
		}
	}

	// Compute force for set 1 sim 7
	private void computeForceLi2S() {
		Molecule thisMole = null;
		Molecule otherMole = null;
		Vec2 thisLoc = new Vec2();
		Vec2 otherLoc = new Vec2();
		int thisCharge =0;
		int otherCharge =0;
		float xValue=0;
		float yValue=0;
		float dis=0;
		float forceX=0;
		float forceY=0;
		float scale=20f;
		int forceDirection = 0;
		float topBoundary = p5Canvas.y + p5Canvas.h/5*3;
		float gravityCompensation =0.3f;
		float waterScale = 5;
		


		for (int i = 0; i < State.molecules.size(); i++) {
			
			
			// Check position of other molecules, in case they are not going too
			// high
			if (true) {
				thisMole = molecules.get(i);
				Vec2 pos = box2d.coordWorldToPixels(thisMole.getPosition());
				if (pos.y < topBoundary) {
					for (int thisE = 0; thisE < thisMole.getNumElement(); thisE++) { // Select
																						// element

						thisMole.sumForceX[thisE] = 0;
						if(thisMole.getName().equals("Water"))
							thisMole.sumForceY[thisE] = gravityCompensation* waterScale * -1;
						thisMole.sumForceY[thisE] = gravityCompensation * -1;
					}
				}
			}
		} //End loop

	}
	
	// Compute force for sim 2
		private void computeForceSolubility() {
			Molecule thisMole = null;
			Molecule otherMole = null;
			Vec2 thisLoc = new Vec2();
			Vec2 otherLoc = new Vec2();
			float xValue = 0;
			float yValue = 0;
			float dis = 0;
			float forceX = 0;
			float forceY = 0;
			float scale = 0.15f;
			float chlorideScale = 0.1f;
			float anchorScale = 1.5f;
			float forceYCompensation = 0.05f;
			float gravityCompensation = 0.2f;
			float topBoundary = p5Canvas.h / 2;
			Anchor anchor = null;
			
			for (int i = 0; i < molecules.size(); i++) {
				/*
				if (molecules.get(i).getName().equals("Silver-Ion")) // Compute
																		// force for
																		// copper-ion,
																		// in order
																		// to
																		// attract
																		// them to
																		// copper
				{

					thisMole = molecules.get(i);
					for (int thisE = 0; thisE < thisMole.getNumElement(); thisE++) { // Select
																						// element

						thisLoc.set(thisMole.getElementLocation(thisE));
						thisMole.sumForceX[thisE] = 0;
						thisMole.sumForceY[thisE] = 0;
						for (int k = 0; k < molecules.size(); k++) { // Go check
																		// forces
																		// from
																		// other
																		// molecules
							if (k == i)
								continue;
							otherMole = molecules.get(k);
							if (otherMole.getName().equals("Chloride")) // We are
																		// looking
																		// for
																		// chloride
							{
								for (int otherE = 0; otherE < otherMole
										.getNumElement(); otherE++)
									otherLoc.set(otherMole
											.getElementLocation(otherE));
								if (thisLoc == null || otherLoc == null)
									continue;
								xValue = otherLoc.x - thisLoc.x;
								yValue = otherLoc.y - thisLoc.y;
								dis = (float) Math.sqrt(xValue * xValue + yValue
										* yValue);
								forceX = (float) (xValue / dis) * scale;
								forceY = (float) (yValue / dis) * scale;

								// Add attraction force to sodium-Ion
								thisMole.sumForceX[thisE] += forceX;
								thisMole.sumForceY[thisE] += forceY
										+ forceYCompensation;
								// At the same time add attraction force to Chloride
								// In this case, number of both Silver and Chloride
								// elements are 1;
								otherMole.sumForceX[thisE] += forceX * (-1)
										* chlorideScale;
								otherMole.sumForceY[thisE] += (forceY + forceYCompensation)
										* (-1) * chlorideScale;

							}
						}

					}
				}
				// If there are some chloride have been created
				// Pull them to anchors
				else if (molecules.get(i).getName().equals("Silver-Chloride")) {
					float disBoundary = 50;
					float silverChlorideScale = 8f;
					thisMole = molecules.get(i);


					for (int e = 0; e < thisMole.getNumElement(); e++) {
						int indexCharge = thisMole.elementCharges.get(e);
						Vec2 locIndex = thisMole.getElementLocation(e);
						thisMole.sumForceWaterX[e] = 0;
						thisMole.sumForceWaterY[e] = 0;
						thisMole.sumForceX[e] = 0;
						thisMole.sumForceY[e] = 0;
						for (int k = 0; k < molecules.size(); k++) {
							if (k == i)
								continue;
							Molecule m = molecules.get(k);
							if (m.getName().equals("Silver-Chloride")) // Find
																		// another
																		// silver-chloride
							{
								for (int e2 = 0; e2 < m.getNumElement(); e2++) {
									Vec2 loc = m.getElementLocation(e2);
									float x = locIndex.x - loc.x;
									float y = locIndex.y - loc.y;
									dis = (float) Math.sqrt(x * x + y * y);
									forceX = (x / dis) / (float)(Math.sqrt(dis))
											* silverChlorideScale;
									forceY = (y / dis) / (float)(Math.sqrt(dis))
											* silverChlorideScale;

									int charge = m.elementCharges.get(e2);
									int mul = charge * indexCharge;
									// If mul>0 replusive force
									// If mul<0 attractive force
									thisMole.sumForceX[e] += mul * forceX;
									thisMole.sumForceY[e] += mul * forceY;

								}
							}
						}
					}
				}*/
				// Check position of other molecules, in case they are not going too
				// high
				if (true) {
					thisMole = molecules.get(i);
					Vec2 pos = box2d.coordWorldToPixels(thisMole.getPosition());
					if (pos.y < topBoundary) {
						for (int thisE = 0; thisE < thisMole.getNumElement(); thisE++) { // Select
																							// element

							thisMole.sumForceX[thisE] += 0;
							thisMole.sumForceY[thisE] += (gravityCompensation) * -1;

						}
					}
				}
			}
		}

	@Override
	protected void applyForce(int sim, int set) {
		// TODO Auto-generated method stub

		if (sim == 1) {
			switch (set) {
			case 1:
				if (frameCounter >= this.computeTriggerInterval)
					frameCounter = 0;

				if (frameCounter == 0) {
					
					super.applyForce(sim, set);
				}

				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				super.applyForce(sim, set);
				break;
			case 5:
				break;
			case 6:
				super.applyForce(sim, set);
				break;
			case 7:
				super.applyForce(sim, set);
				break;
			case 8:
				break;
			case 9:
				break;
			case 10:
				super.applyForce(sim, set);
				break;

			}
		}
		else if (sim==2)
		{
			super.applyForce(sim, set);
		}

		this.frameCounter++;
		// System.out.println("Apply force "+this.frameCounter);

	}

	@Override
	protected void reset() {
		// TODO Auto-generated method stub
		this.frameCounter = 0;
		int sim = p5Canvas.getMain().selectedSim;
		int set = p5Canvas.getMain().selectedSet;
		this.isAnchorSetup = false;
		setupSimulations();

		if (sim == 1 && set == 2) {
			p5Canvas.getMain().heatSlider.setValue(185);
			box2d.setGravity(0f, 0f);
		} else if (sim == 1 && set == 9) {
			p5Canvas.getMain().heatSlider.setValue(160);
			box2d.setGravity(0f, 0f);
		} else if (sim == 1 && (set == 1 || set == 3 || set == 5 || set == 8)) {
			box2d.setGravity(0f, 0f);
		} else if (sim == 1 && set == 10) {
			p5Canvas.getMain().heatSlider.setValue(50);
		}
		
		//Add particular reaction into Compound global parameter
	}

}
