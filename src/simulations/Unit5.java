/**
 * 
 */
package simulations;

import static data.State.molecules;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import main.Main;
import net.miginfocom.swing.MigLayout;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;

import Util.SimpleBar;

import data.DBinterface;
import data.State;

import simulations.models.Compound;
import simulations.models.Molecule;
import simulations.models.Simulation;
import simulations.models.Simulation.SpawnStyle;

/**
 * @author administrator
 *
 */
public class Unit5 extends UnitBase {
	
	
	//Output labels
	private JLabel lblConText1;    //Concentration output
	private JLabel lblConValue1;
	private JLabel lblConText2;
	private JLabel lblConValue2;
	private JLabel lblConText3;
	private JLabel lblConValue3;
	private JLabel lblConText4;
	private JLabel lblConValue4;

	private JLabel lblVolumeText;
	private JLabel lblVolumeValue;
	private JLabel lblTempText;
	private JLabel lblTempValue;
	private JLabel lblPressureText;
	private JLabel lblPressureValue;
	
	//Lables above bars
	public JLabel lblMultiplicationText1;
	public JLabel lblMultiplicationText2;
	public JLabel lblMultiplicationText3;
	public JLabel lblEqualText;
	public JLabel lblMoleText;
	public JLabel lblRText;
	
	private int numMoleculePerMole = 10;
	
	//Bars on right panel
	public SimpleBar barPressure;
	public SimpleBar barVolume;
	public SimpleBar barMol;
	public SimpleBar barTemp;
	

	public Unit5(P5Canvas parent, PBox2D box) {
		super(parent, box);
		unitNum = 5;
		setupSimulations();
		setupOutputLabels();
	}
	/* (non-Javadoc)
	 * @see simulations.UnitBase#setupSimulations()
	 */
	@Override
	public void setupSimulations() {
		simulations = new Simulation[SIMULATION_NUMBER];

		simulations[0] = new Simulation(unitNum, 1, 1);
		String[] elements0 = {"Methane","Oxygen"};
		SpawnStyle[] spawnStyles0 = { SpawnStyle.Gas,SpawnStyle.Gas };
		simulations[0].setupElements(elements0, spawnStyles0);
		
		simulations[1] = new Simulation(unitNum, 1, 2);
		String[] elements1 = { "Hydrogen-Iodide"};
		SpawnStyle[] spawnStyles1 = { SpawnStyle.Gas};
		simulations[1].setupElements(elements1, spawnStyles1);
		
		simulations[2] = new Simulation(unitNum, 2, 1);
		String[] elements2 = {"Sodium-Bicarbonate","Acetic-Acid"};
		SpawnStyle[] spawnStyles2 = { SpawnStyle.Precipitation,SpawnStyle.Solvent };
		simulations[2].setupElements(elements2, spawnStyles2);
		
		simulations[3] = new Simulation(unitNum, 2, 2);
		String[] elements3 = {"Sodium-Bicarbonate","Acetic-Acid"};
		SpawnStyle[] spawnStyles3 = { SpawnStyle.Precipitation,SpawnStyle.Solvent };
		simulations[3].setupElements(elements3, spawnStyles3);
		
		simulations[4] = new Simulation(unitNum, 3, 1);
		String[] elements4 = {"Nitryl-Chloride","Nitric-Oxide"};
		SpawnStyle[] spawnStyles4 = { SpawnStyle.Gas,SpawnStyle.Gas };
		simulations[4].setupElements(elements4, spawnStyles4);
		
		simulations[5] = new Simulation(unitNum, 3, 2);
		String[] elements5 = {"Nitryl-Chloride","Nitric-Oxide"};
		SpawnStyle[] spawnStyles5 = { SpawnStyle.Gas,SpawnStyle.Gas };
		simulations[5].setupElements(elements5, spawnStyles5);
		
		simulations[6] = new Simulation(unitNum, 3, 3);
		String[] elements6 = {"Nitryl-Chloride","Nitric-Oxide"};
		SpawnStyle[] spawnStyles6 = { SpawnStyle.Gas,SpawnStyle.Gas };
		simulations[6].setupElements(elements6, spawnStyles6);
		
		simulations[7] = new Simulation(unitNum, 3, 4);
		String[] elements7 = {"Nitrogen-Peroxide"};
		SpawnStyle[] spawnStyles7 = { SpawnStyle.Gas};
		simulations[7].setupElements(elements7, spawnStyles7);
		
		simulations[8] = new Simulation(unitNum, 4, 1);
		String[] elements8 = {"Ammonia"};
		SpawnStyle[] spawnStyles8 = { SpawnStyle.Gas};
		simulations[8].setupElements(elements8, spawnStyles8);
		
		simulations[9] = new Simulation(unitNum, 4, 2);
		String[] elements9 = {"Nitrogen-Dioxide"};
		SpawnStyle[] spawnStyles9 = { SpawnStyle.Gas};
		simulations[9].setupElements(elements9, spawnStyles9);
		
		simulations[10] = new Simulation(unitNum, 4, 3);
		String[] elements10 = {"Carbon-Monoxide","Nitrogen-Dioxide"};
		SpawnStyle[] spawnStyles10 = { SpawnStyle.Gas,SpawnStyle.Gas};
		simulations[10].setupElements(elements10, spawnStyles10);
		

	}
	
	private void setupOutputLabels()
	{
		lblConText1 = new JLabel();
		lblConText2 = new JLabel();
		lblConText3 = new JLabel();
		lblConText4 = new JLabel();
		lblConText1 = new JLabel(" M");
		lblConText2 = new JLabel(" M");
		lblConText3 = new JLabel(" M");
		lblConText4 = new JLabel(" M");
		lblVolumeText = new JLabel("Volume:");
		lblVolumeValue = new JLabel(" mL");
		lblTempText = new JLabel("Temperature:");
		lblTempValue = new JLabel(" \u2103");
		lblPressureText = new JLabel("Pressure:");
		lblPressureValue = new JLabel(" kPa");
		
		//Set up bar parameter;
		Main main = p5Canvas.getMain();
		lblMultiplicationText1 = new JLabel("*");
		lblMultiplicationText2 = new JLabel("*");
		lblMultiplicationText3 = new JLabel("*");
		lblMoleText = new JLabel("n (mol)");
		lblRText = new JLabel("R");
		lblEqualText = new JLabel("=");
		barPressure = new SimpleBar(0,350,30);
		barVolume = new SimpleBar(main.minVolume,main.maxVolume,63);
		barMol = new SimpleBar(0,50,10);
		barTemp  = new SimpleBar(main.tempMin,main.tempMax,25);
	}

	/* (non-Javadoc)
	 * @see simulations.UnitBase#setupReactionProducts(int, int)
	 */
	@Override
	public void setupReactionProducts(int sim, int set) {
		ArrayList<String> products = new ArrayList<String>();
		if (!(sim == 2)) {
			products = DBinterface.getReactionOutputs(this.unitNum,
					sim, set);
			if (products != null) {
				for (String s : products) {
					if (!Compound.names.contains(s)) {
						Compound.names.add(s);
						Compound.counts.add(0);
						Compound.caps.add(95);
					}
				}
			}
		}

	}

	/* (non-Javadoc)
	 * @see simulations.UnitBase#updateMolecules(int, int)
	 */
	@Override
	public void updateMolecules(int sim, int set) {
		boolean reactionHappened = false; // Boolean flag indicating is
											// reactions have taken place

		Simulation simulation = this.getSimulation(sim, set);
		if (sim == 1) {
			switch (set) {
			case 1:
				reactionHappened = reactMethaneOxygen(simulation);
				break;
			case 2:
				reactionHappened = reactHydrogenIodide(simulation);
				break;
	

			}
		}
	}
	
	/******************************************************************
	 * FUNCTION : reactGeneric DESCRIPTION : Function for Sim 1 Set 1 raction
	 * INPUTS : simulation(Simulation) 
	 * OUTPUTS: None
	 *******************************************************************/
	public boolean reactMethaneOxygen(Simulation simulation) {
		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

			int numToKill = p5Canvas.killingList.size();
			int methaneIndex = -1;
			Molecule[] mOld = new Molecule[numToKill];
			for (int i = 0; i < numToKill; i++)
			{
				mOld[i] = (Molecule) p5Canvas.killingList.get(i);
				if(mOld[i].getName().equals("Methane"))
					methaneIndex = i;
			}
			// Molecule m2 = (Molecule) p5Canvas.killingList.get(1);

			Molecule mNew = null;
			String nameNew = null;
			boolean isFirstWater = true;
			// Actually there is only one reaction going in each frame
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				//Reacts at the postion of Methane
				Vec2 loc = mOld[methaneIndex].getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);
				nameNew = p5Canvas.products.get(i);
				if(nameNew.equals("Water")) //Add x off to water molecule
				{
					Vec2 size = Molecule.getShapeSize("Carbon-Dioxide", p5Canvas);
					if(isFirstWater)
					{newVec.x += size.x;
						isFirstWater = false;
					}
					else
						newVec.x -= size.x;
					
				}
				mNew = new Molecule(newVec.x, newVec.y,
						nameNew, box2d, p5Canvas,
						(float) (Math.PI / 2));
				molecules.add(mNew);

				mNew.body.setLinearVelocity(mOld[i].body
							.getLinearVelocity());
			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			int unit = p5Canvas.getMain().selectedUnit;
			int set = p5Canvas.getMain().selectedSet;
			int sim = p5Canvas.getMain().selectedSim;
			updateCompoundNumber(unit, sim, set);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see simulations.UnitBase#initialize()
	 */
	@Override
	public void initialize() {
		//Setup speed rate for different molecules
		setupSpeed();

	}
	
	//Set up speed ratio for molecules
	//Called by reset()
	public void setupSpeed()
	{
		String name = null;
		Molecule mole = null;
		for(int i =0;i<State.molecules.size();i++)
		{
			mole = State.molecules.get(i);
			name = new String(mole.getName());
			//Sim 1 set 1
			if (name.equals("Methane"))
				mole.setRatioKE(1.0f/8);
			else if (name.equals("Oxygen"))
				mole.setRatioKE(1.0f/8);
			else if (name.equals("Carbon-Dioxide"))
				mole.setRatioKE(1.0f/8);
			else if (name.equals("Water"))
				mole.setRatioKE(1.0f/8);
			//Sim 1 set 2
			else if (name.equals("Hydrogen-Iodide"))
				mole.setRatioKE(1.0f/12);
			else if (name.equals("Hydrogen"))
				mole.setRatioKE(1.0f/12);
			else if (name.equals("Iodine"))
				mole.setRatioKE(1.0f/12);
			//
			else if (name.equals(""))
				mole.setRatioKE(1.0f);
			else if (name.equals(""))
				mole.setRatioKE(1.0f);
		}
	}
	
	/******************************************************************
	 * FUNCTION : reactGeneric DESCRIPTION : Function for Sim 1 Set 1 raction
	 * INPUTS : simulation(Simulation) 
	 * OUTPUTS: None
	 *******************************************************************/
	public boolean reactHydrogenIodide(Simulation simulation) {
		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

			int numToKill = p5Canvas.killingList.size();
			int hydrogenIodideIndex = -1;
			Molecule[] mOld = new Molecule[numToKill];
			for (int i = 0; i < numToKill; i++)
			{
				mOld[i] = (Molecule) p5Canvas.killingList.get(i);
				if(mOld[i].getName().equals("Hydrogen-Iodide"))
					hydrogenIodideIndex = i;
			}
			// Molecule m2 = (Molecule) p5Canvas.killingList.get(1);

			Molecule mNew = null;
			String nameNew = null;
			// Actually there is only one reaction going in each frame
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				//Reacts at the postion of Methane
				Vec2 loc = mOld[hydrogenIodideIndex].getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);
				nameNew = p5Canvas.products.get(i);
				if(nameNew.equals("Hydrogen")) //Add x off to water molecule
				{
					Vec2 size = Molecule.getShapeSize("Iodine", p5Canvas);
					newVec.x += size.y;		
				}
				mNew = new Molecule(newVec.x, newVec.y,
						nameNew, box2d, p5Canvas,
						(float) (Math.PI / 2));
				molecules.add(mNew);
				mNew.body.setLinearVelocity(mOld[i].body
							.getLinearVelocity());
			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			int unit = p5Canvas.getMain().selectedUnit;
			int set = p5Canvas.getMain().selectedSet;
			int sim = p5Canvas.getMain().selectedSim;
			updateCompoundNumber(unit, sim, set);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see simulations.UnitBase#reset()
	 */
	@Override
	protected void reset() {
		setupSimulations();
		//Reset parameters
		barMol.reset();
		barPressure.reset();
		barVolume.reset();
		barTemp.reset();
		
		//Customization
		int sim = p5Canvas.getMain().selectedSim;
		int set = p5Canvas.getMain().selectedSet;
		Main main = p5Canvas.getMain();
		switch(sim)
		{
		case 1:
			//p5Canvas.getMain().heatSlider.setEnabled(false);
			main.volumeSlider.setEnabled(false);
			if(set==1)
			p5Canvas.temp= 125;
			else if (set==2)
				p5Canvas.temp =190;
			break;
		case 2:
			main.heatSlider.setEnabled(false);
			main.volumeSlider.setEnabled(false);
			break;
		case 3:
			if(set==1)
				main.heatSlider.setEnabled(false);
			else if( set==2)
				main.volumeSlider.setEnabled(false);
			else if (set==4)
			{
				main.heatSlider.setEnabled(false);
				main.volumeSlider.setEnabled(false);
			}
			break;
		case 4:
			main.heatSlider.setEnabled(false);
			main.volumeSlider.setEnabled(false);
			break;
		}
	}
	
	public void resetDashboard(int sim,int set)
	{
		super.resetDashboard(sim, set);
		Main main = p5Canvas.getMain();
		JPanel dashboard = main.dashboard;
		
		lblConText1 = new JLabel();
		lblConText2 = new JLabel();
		lblConText3 = new JLabel();
		lblConText4 = new JLabel();
		lblConValue1 = new JLabel(" M");
		lblConValue2 = new JLabel(" M");
		lblConValue3 = new JLabel(" M");
		lblConValue4 = new JLabel(" M");
		lblVolumeText = new JLabel("Volume:");
		lblVolumeValue = new JLabel(" mL");
		lblTempText = new JLabel("Temperature:");
		lblTempValue = new JLabel(" \u2103");
		lblPressureText = new JLabel("Pressure:");
		lblPressureValue = new JLabel(" kPa");
		
		switch(sim)
		{
		case 1:
			if(set==1)
			{
				lblConText1.setText("[Methane]:");
				lblConValue1.setText(" M");
				lblConText2.setText("[Oxygen]:");
				lblConValue2.setText(" M");				
				lblConText3.setText("[Carbon Dioxide]:");
				lblConValue3.setText(" M");				
				lblConText4.setText("[Water]:");
				lblConValue4.setText(" M");
				lblVolumeValue.setText(" mL");
				dashboard.add(lblConText1,"cell 0 1");
				dashboard.add(lblConValue1,"cell 1 1");
				dashboard.add(lblConText2,"cell 0 2");
				dashboard.add(lblConValue2,"cell 1 2");				
				dashboard.add(lblConText3,"cell 0 3");
				dashboard.add(lblConValue3,"cell 1 3");				
				dashboard.add(lblConText4,"cell 0 4");
				dashboard.add(lblConValue4,"cell 1 4");
				dashboard.add(lblVolumeText,"cell 0 5");
				dashboard.add(lblVolumeValue,"cell 1 5");
			}
			else if(set==2)
			{
				lblConText1.setText("[Hydrogen Iodide]:");
				lblConValue1.setText(" M");
				lblConText2.setText("[Hydrogen]:");
				lblConValue2.setText(" M");				
				lblConText3.setText("[Iodine]:");
				lblConValue3.setText(" M");				
				lblVolumeValue.setText(" mL");
				dashboard.add(lblConText1,"cell 0 1");
				dashboard.add(lblConValue1,"cell 1 1");
				dashboard.add(lblConText2,"cell 0 2");
				dashboard.add(lblConValue2,"cell 1 2");				
				dashboard.add(lblConText3,"cell 0 3");
				dashboard.add(lblConValue3,"cell 1 3");				
				dashboard.add(lblVolumeText,"cell 0 4");
				dashboard.add(lblVolumeValue,"cell 1 4");
			}
			break;
		case 2:
			lblConText1.setText("[Concentration]:");
			lblConValue1.setText(" M");
//			lblConText2.setText("Hydrogen:");
//			lblConValue2.setText(" M");				
//			lblConText3.setText("Iodine:");
//			lblConValue3.setText(" M");				
			lblVolumeValue.setText(" mL");
			dashboard.add(lblConText1,"cell 0 1");
			dashboard.add(lblConValue1,"cell 1 1");
//			dashboard.add(lblConText2,"cell 0 2");
//			dashboard.add(lblConValue2,"cell 1 2");				
//			dashboard.add(lblConText3,"cell 0 3");
//			dashboard.add(lblConValue3,"cell 1 3");		
			dashboard.add(lblPressureText,"cell 0 2");
			dashboard.add(lblPressureValue,"cell 1 2");
			dashboard.add(lblTempText,"cell 0 3");
			dashboard.add(lblTempValue,"cell 1 3");
			dashboard.add(lblVolumeText,"cell 0 4");
			dashboard.add(lblVolumeValue,"cell 1 4");
			break;
		case 3:
		case 4:
			String alignStr = new String(", align center");

			int barWidth = 40;
			int barHeight = 120;
			dashboard.setLayout(new MigLayout("","[45][8][45][25][45][8][10][8][45]","[][][grow][]"));
			dashboard.add(main.lblElapsedTimeText, "cell 0 3 4 1, align center");
			dashboard.add(main.elapsedTime, "cell 4 3 3 1");
			
			dashboard.add(lblPressureText, "cell 0 0"+alignStr);
			dashboard.add(lblMultiplicationText1, "cell 1 0"+alignStr);
			dashboard.add(lblVolumeText,"cell 2 0"+alignStr);

			dashboard.add(lblEqualText,"cell 3 0"+alignStr);
			dashboard.add(lblMoleText,"cell 4 0"+alignStr); 
			dashboard.add(lblMultiplicationText2, "cell 5 0"+alignStr);
			dashboard.add(lblRText,"cell 6 0"+alignStr); 
			dashboard.add(lblMultiplicationText3, "cell 7 0"+alignStr);
			dashboard.add(lblTempText,"cell 8 0"+alignStr); 

			barPressure.setPreferredSize(new Dimension(barWidth,barHeight));
			barVolume.setPreferredSize(new Dimension(barWidth,barHeight));
			barMol.setPreferredSize(new Dimension(barWidth,barHeight));
			barTemp.setPreferredSize(new Dimension(barWidth,barHeight));
			dashboard.add(barPressure,"cell 0 2"+alignStr);
			dashboard.add(barVolume,"cell 2 2"+alignStr);
			dashboard.add(barMol,"cell 4 2"+alignStr);
			dashboard.add(barTemp,"cell 8 2"+alignStr);
			break;
			default:
				break;
		}
	}

	/* (non-Javadoc)
	 * @see simulations.UnitBase#computeForce(int, int)
	 */
	@Override
	protected void computeForce(int sim, int set) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see simulations.UnitBase#addMolecules(boolean, java.lang.String, int)
	 */
	@Override
	public boolean addMolecules(boolean isAppEnable, String compoundName,
			int count) {
		boolean res = false;
		
		int sim = p5Canvas.getMain().selectedSim;
		int set = p5Canvas.getMain().selectedSet;
		Simulation simulation = getSimulation(sim, set);
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
		}
		else if (spawnStyle == SpawnStyle.SolidCube)
		{
//			res = this.addSolidCube(isAppEnable, compoundName, count,
//					simulation);
		}

		
		if(res)
		{
			//Connect new created molecule to table index
			int tIndex = p5Canvas.getTableView().getIndexByName(compoundName);
			int lastIndex = State.molecules.size()-1;
			for(int i = 0;i<count;i++)
			{
				State.molecules.get(lastIndex-i).setTableIndex(tIndex);
			}
		}
		
		return res;
	}

	/* (non-Javadoc)
	 * @see simulations.UnitBase#beginReaction(org.jbox2d.dynamics.contacts.Contact)
	 */
	@Override
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
	 * FUNCTION : getReactionProducts DESCRIPTION : Reture objects based on
	 * input name Called by beginReaction
	 * 
	 * INPUTS : reactants (Array<String>) OUTPUTS: None
	 *******************************************************************/
	private ArrayList<String> getReactionProducts(ArrayList<String> reactants,
			Molecule m1, Molecule m2) {
		ArrayList<String> products = new ArrayList<String>();
		// Sim 1 set 1
		if (reactants.contains("Methane") && reactants.contains("Oxygen")) {
			float radius = 175;

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
		// Sim 1 set 2
		else if (reactants.get(0).equals("Hydrogen-Iodide")
				&& reactants.get(1).equals("Hydrogen-Iodide")
				&& reactants.size() == 2) {
			products.add("Hydrogen");
			products.add("Iodine");

		}

		return products;
	}
	
	private float computeDistance(Vec2 v1, Vec2 v2) {
		float dis = 0;
		dis = (v1.x - v2.x) * (v1.x - v2.x) + (v1.y - v2.y) * (v1.y - v2.y);
		dis = (float) Math.sqrt(dis);

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
		// Sim 1 set 4, set 10 and sim 2 AgNO3
		if (collider.contains("Silver-Nitrate")) {
			products.add("Silver-Ion");
			products.add("Nitrate");
		}
		// Sim 1 set 6
		else if (collider.contains("Copper-II-Sulfate")) {
			products.add("Copper-II");
			products.add("Sulfate");
		}
		// Sim 1 set 10
		else if (collider.contains("Sodium-Chloride")) {
			products.add("Sodium-Ion");
			products.add("Chloride");
		}
		
		//Sim 2 KBr
		else if (collider.contains("Potassium-Bromide"))
		{
			products.add("Potassium-Ion");
			products.add("Bromine-Ion");
		}

		// Sim 2 NH4Cl
		else if (collider.contains("Ammonium-Chloride")) {
			products.add("Ammonium");
			products.add("Chloride");
		}
		// Sim 2 Na2CO3
		else if (collider.contains("Sodium-Carbonate")) {
			products.add("Sodium-Ion");
			products.add("Sodium-Ion");
			products.add("Carbonate");
		}
		// Sim 2 NaOH
		else if (collider.contains("Sodium-Hydroxide")) {
			products.add("Sodium-Ion");
			products.add("Hydroxide");
		}
		// Sim 2 LiNO3
		else if (collider.contains("Lithium-Nitrate")) {
			products.add("Lithium-Ion");
			products.add("Nitrate");
		} else {
			// return null;
		}
		return products;

	}
	@Override
	public void updateOutput(int sim, int set) {
		// Update lblTempValue
		DecimalFormat myFormatter = new DecimalFormat("###.##");
		String output = null;

		if(sim==1 &&set==1)
		{
			//Count molecule number
			int numMethane=0, numOxygen=0, numCarbonDioxide=0,numWater=0;
			float conMethane=0,conOxygen=0,conCarbonDioxide=0,conWater=0;
			float volume=0;
			String name = null;
			for(int i =0;i<State.molecules.size();i++)
			{
				name = State.molecules.get(i).getName();
				if(name.equals("Methane"))
					numMethane++;
				else if(name.equals("Oxygen"))
					numOxygen++;
				else if(name.equals("Carbon-Dioxide"))
					numCarbonDioxide++;
				else if(name.equals("Water"))
					numWater ++;
			}
			volume = (float)p5Canvas.currentVolume/1000;
			
			conMethane = ((float)numMethane/numMoleculePerMole ) /volume;
			conOxygen = ((float)numOxygen/numMoleculePerMole ) /volume;
			conCarbonDioxide = ((float)numCarbonDioxide/numMoleculePerMole ) /volume;
			conWater = ((float)numWater/numMoleculePerMole ) /volume;
			
			output = myFormatter.format(conMethane);
			lblConValue1.setText(output+" M");
			output = myFormatter.format(conOxygen);
			lblConValue2.setText(output+" M");
			output = myFormatter.format(conCarbonDioxide);
			lblConValue3.setText(output+" M");
			output = myFormatter.format(conWater);
			lblConValue4.setText(output+" M");
			
			
		}
		else if(sim==1 &&set ==2)
		{
			//Count molecule number
			int numHydrogenIodide=0, numHydrogen=0, numIodine=0;
			float conHydrogenIodide=0,conHydrogen=0,conIodine=0;
			float volume=0;
			String name = null;
			for(int i =0;i<State.molecules.size();i++)
			{
				name = State.molecules.get(i).getName();
				if(name.equals("Hydrogen-Iodide"))
					numHydrogenIodide++;
				else if(name.equals("Hydrogen"))
					numHydrogen++;
				else if(name.equals("Iodine"))
					numIodine++;
			}
			volume = (float)p5Canvas.currentVolume/1000;
			
			conHydrogenIodide = ((float)numHydrogenIodide/numMoleculePerMole ) /volume;
			conHydrogen = ((float)numHydrogen/numMoleculePerMole ) /volume;
			conIodine = ((float)numIodine/numMoleculePerMole ) /volume;
			
			output = myFormatter.format(conHydrogenIodide);
			lblConValue1.setText(output+" M");
			output = myFormatter.format(conHydrogen);
			lblConValue2.setText(output+" M");
			output = myFormatter.format(conIodine);
			lblConValue3.setText(output+" M");		
		}
		else if(sim==2 && set ==1)
		{
			
		}
		if (lblVolumeValue.isShowing()) {
			lblVolumeValue.setText(Float.toString(p5Canvas.currentVolume)
					+ " mL");
		}
		if (lblTempValue.isShowing()) {
			output = myFormatter.format(p5Canvas.temp);
			lblTempValue.setText(output + " \u2103");
		}

		if (lblPressureValue.isShowing()) {
			output = myFormatter.format(p5Canvas.pressure);
			lblPressureValue.setText(output + " kPa");
		}
		// Update bars
		if (barPressure != null)
			if (barPressure.isShowing()) {
				barPressure.setValue(p5Canvas.pressure);
				barPressure.updateUI();
				barVolume.setValue(p5Canvas.currentVolume);
				barVolume.updateUI();
				barMol.setValue(p5Canvas.mol);
				barMol.updateUI();
				barTemp.setValue(p5Canvas.temp);
				barTemp.updateUI();
			}
	
	}

}
