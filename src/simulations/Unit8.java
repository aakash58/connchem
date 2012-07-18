/**
 * 
 */
package simulations;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;

import main.Main;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;

import Util.Constants;
import Util.Integrator;

import data.DBinterface;
import data.State;

import simulations.models.Compound;
import simulations.models.Molecule;
import simulations.models.Simulation;
import simulations.models.Molecule.mState;
import simulations.models.Simulation.SpawnStyle;

/**
 * @author administrator
 *
 */
public class Unit8 extends UnitBase {

	/* (non-Javadoc)
	 * @see simulations.UnitBase#setupSimulations()
	 */
	private JLabel lblHConText;
	private JLabel lblHConValue;
	private JLabel lblOHConText;
	private JLabel lblOHConValue;
	private JLabel lblPHText;
	private JLabel lblPHValue;
	private JLabel lblPOHText;
	private JLabel lblPOHValue;
	private JLabel lblHNumberText;
	private JLabel lblHNumberValue;
	private JLabel lblOHNumberText;
	private JLabel lblOHNumberValue;
	private JLabel lblWaterNumberText;
	private JLabel lblWaterNumberValue;
	private JLabel lblKeqText;
	private JLabel lblKeqValue;
	private JLabel lblTempText;
	private JLabel lblTempValue;
	//Labels for Sim 7
	private JLabel lblMolesCompound1Text;
	private JLabel lblMolesCompound1Value;
	private JLabel lblMolesCompound2Text;
	private JLabel lblMolesCompound2Value;
	private JLabel lblMolesCompound3Text;
	private JLabel lblMolesCompound3Value;
	private JLabel lblMolesWaterText;
	private JLabel lblMolesWaterValue;
	
	
	
	private int numMoleculePerMole =10;
	private float moleFactor = (float) (0.005f/1.6);
	float keq = 0.01f;
	float defaultKeq =0.01f;
	float breakProbability = 0.75f; // The chance that compound will break apart
	private float pH;
	
	int oldTime = -1;
	int curTime = -1;
	
	Integrator interpolatorPos1 = new Integrator(0);
	Integrator interpolatorPos2 = new Integrator(0);
	Integrator interpolatorAngle1 = new Integrator(0);
	Integrator interpolatorAngle2 = new Integrator(0);
	Integrator interpolatorHide = new Integrator(0);
	Integrator interpolatorShow = new Integrator(0);
	//private float totalDist = 0;
	private Vec2 lastPositionFirst = new Vec2(0,0);
	private Vec2 lastPositionSecond = new Vec2(0,0);
	private Vec2  translateVectorFirst = new Vec2(0,0);
	private Vec2 translateVectorSecond = new Vec2(0,0);
//	private float minDist = 0;
	private int electronView = 0; //If current simulation is for Lewis Law
	private boolean hasFading = false;   //If this simulation has fading transition
	private boolean isFading = false;    //If in fading process
//	private Molecule newMolecule = null;
	
	//IonHash: used in sim2 to map hydrogen-Ion and hydrogen-Atom to hydrogen-Ion
	private HashMap<String,String []> ionHash = new HashMap<String,String[]>();
	
	//New molecule list for reactions in Sim 2
	ArrayList<Molecule> newMolecules = new ArrayList<Molecule>();

	

	
	HashMap<String, Float> moleculeConHash ;
	
	public Unit8(P5Canvas parent, PBox2D box) {
		super(parent, box);

		unitNum = 8;
		moleculeConHash = new HashMap<String, Float>();
		setupSimulations();
		setupOutputLabels();
	}
	@Override
	public void setupSimulations() {
		simulations = new Simulation[SIMULATION_NUMBER];

		simulations[0] = new Simulation(unitNum, 1, 1);
		String[] elements0 = { "Hydrogen-Ion","Chlorine-Ion", "Water" };
		SpawnStyle[] spawnStyles0 = { SpawnStyle.Solvent,SpawnStyle.Solvent, SpawnStyle.Solvent };
		simulations[0].setupElements(elements0, spawnStyles0);

		simulations[1] = new Simulation(unitNum, 1, 2);
		String[] elements1 = { "Sodium-Hydroxide", "Water" };
		SpawnStyle[] spawnStyles1 = { SpawnStyle.Precipitation, SpawnStyle.Solvent };
		simulations[1].setupElements(elements1, spawnStyles1);

		simulations[2] = new Simulation(unitNum, 1, 3);
		String[] elements2 = { "Hydrogen-Ion","Chlorine-Ion", "Sodium-Ion","Hydroxide","Water" };
		SpawnStyle[] spawnStyles2 = { SpawnStyle.Solvent, SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent };
		simulations[2].setupElements(elements2, spawnStyles2);

		simulations[3] = new Simulation(unitNum, 2, 1);
		String[] elements3 = { "Hydrogen-Chloride", "Sodium-Hydroxide" };
		SpawnStyle[] spawnStyles3 = { SpawnStyle.Gas,SpawnStyle.Gas };
		simulations[3].setupElements(elements3, spawnStyles3);
		
		simulations[4] = new Simulation(unitNum, 2, 2);
		String[] elements4 = { "Hydrogen-Chloride","Ammonia" };
		SpawnStyle[] spawnStyles4 = { SpawnStyle.Gas,SpawnStyle.Gas};
		simulations[4].setupElements(elements4, spawnStyles4);
		
		simulations[5] = new Simulation(unitNum, 2, 3);
		String[] elements5 = { "Cyanide","Hydrogen-Bromide" };
		SpawnStyle[] spawnStyles5 = { SpawnStyle.Gas,SpawnStyle.Gas};
		simulations[5].setupElements(elements5, spawnStyles5);
		
		simulations[6] = new Simulation(unitNum, 2, 4);
		String[] elements6 = { "Boron-Trichloride","Chlorine-Ion" };
		SpawnStyle[] spawnStyles6 = { SpawnStyle.Gas,SpawnStyle.Gas };
		simulations[6].setupElements(elements6, spawnStyles6);
		
		simulations[7] = new Simulation(unitNum, 3, 1);
		String[] elements7 = { "Hydrogen-Ion","Chlorine-Ion", "Water"};
		SpawnStyle[] spawnStyles7 = { SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent};
		simulations[7].setupElements(elements7, spawnStyles7);
		
		simulations[8] = new Simulation(unitNum, 3, 2);
		String[] elements8 = { "Hydrogen-Fluoride","Water"};
		SpawnStyle[] spawnStyles8 = { SpawnStyle.Solvent,SpawnStyle.Solvent};
		simulations[8].setupElements(elements8, spawnStyles8);
		
		simulations[9] = new Simulation(unitNum, 3, 3);
		String[] elements9 = { "Sodium-Ion","Hydroxide", "Water" };
		SpawnStyle[] spawnStyles9 = { SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent};
		simulations[9].setupElements(elements9, spawnStyles9);
		
		simulations[10] = new Simulation(unitNum, 3, 4);
		String[] elements10 = { "Ammonia","Water"};
		SpawnStyle[] spawnStyles10 = { SpawnStyle.Solvent,SpawnStyle.Solvent};
		simulations[10].setupElements(elements10, spawnStyles10);
		
		simulations[11] = new Simulation(unitNum, 4, 1);
		String[] elements11 = { "Acetic-Acid","Water"};
		SpawnStyle[] spawnStyles11 = { SpawnStyle.Solvent,SpawnStyle.Solvent};
		simulations[11].setupElements(elements11, spawnStyles11);
		
		simulations[12] = new Simulation(unitNum, 4, 2);
		String[] elements12 = { "Lithium-Ion","Hydroxide","Water"};
		SpawnStyle[] spawnStyles12 = { SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent};
		simulations[12].setupElements(elements12, spawnStyles12);
		
		simulations[13] = new Simulation(unitNum, 4, 3);
		String[] elements13 = { "Methylamine","Water"};
		SpawnStyle[] spawnStyles13 = { SpawnStyle.Solvent,SpawnStyle.Solvent};
		simulations[13].setupElements(elements13, spawnStyles13);
		
//		simulations[14] = new Simulation(unitNum, 4, 4);
//		String[] elements14 = { "Hydrogen-Ion","Nitrate","Water"};
//		SpawnStyle[] spawnStyles14 = { SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent};
//		simulations[14].setupElements(elements14, spawnStyles14);
		
//		simulations[15] = new Simulation(unitNum, 5, 1);
//		String[] elements15 = { "Hydrogen-Ion","Chlorine-Ion", "Sodium-Ion","Hydroxide","Water" };
//		SpawnStyle[] spawnStyles15 = { SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent};
//		simulations[15].setupElements(elements15, spawnStyles15);
		
		simulations[16] = new Simulation(unitNum, 6, 1);
		String[] elements16 = { "Hydrogen-Ion","Chlorine-Ion", "Sodium-Ion","Hydroxide","Water" };
		SpawnStyle[] spawnStyles16 = { SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent};
		simulations[16].setupElements(elements16, spawnStyles16);
		
		simulations[17] = new Simulation(unitNum, 5, 1);
		String[] elements17 = { "Ammonia","Ammonium","Hydrogen-Ion","Chlorine-Ion","Water" };
		SpawnStyle[] spawnStyles17 = { SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent};
		simulations[17].setupElements(elements17, spawnStyles17);
		
		ionHash.put("Hydrogen-Chloride", new String [] {"Hydrogen-Chloride","Hydrogen-Chloride"});
		ionHash.put("Sodium-Hydroxide", new String [] {"Sodium-Hydroxide","Sodium-Hydroxide"});
		ionHash.put("Ammonia", new String [] {"Ammonia","Ammonia"});
		ionHash.put("Ammonium", new String [] {"Ammonium","Ammonium"});
		ionHash.put("Cyanide", new String [] {"Cyanide","Cyanide"});
		ionHash.put("Hydrogen-Bromide", new String [] {"Hydrogen-Bromide","Hydrogen-Bromide"});
		ionHash.put("Boron-Trichloride", new String[] {"Boron-Trichloride","Boron-Trichloride"});
		
	}
	
	@Override
	public void setupReactionProducts(int sim, int set) {
		ArrayList<String> products = new ArrayList<String>();
		if (true) {
			products = DBinterface.getReactionOutputs(this.unitNum, sim, set);
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
	
	private void setupOutputLabels()
	{
		lblHConText = new JLabel("<html>[H<sub>3</sub>O<sup>+</sup>]: </html>");
		lblHConValue = new JLabel();
		lblOHConText = new JLabel("<html>[OH<sup>-</sup>]: </html>");
		lblOHConValue  = new JLabel();
		lblPHText = new JLabel("PH: ");
		lblPHValue = new JLabel();
		lblPOHText = new JLabel("POH: ");
		lblPOHValue = new JLabel();
		lblHNumberText = new JLabel("<html>Number of H<sub>3</sub>O<sup>+</sup> Ions: </html>");
		lblHNumberValue = new JLabel();
		lblOHNumberText = new JLabel("<html>Number of OH<sup>-</sup> Ions: </html>");
		lblOHNumberValue = new JLabel();
		lblWaterNumberText = new JLabel("Number of Water Molecules: ");
		lblWaterNumberValue = new JLabel();
		lblKeqText = new JLabel("Keq: ");
		lblKeqValue = new JLabel();
		lblTempText = new JLabel("Temperature: ");
		lblTempValue = new JLabel();
		lblMolesCompound1Text = new JLabel("<html>Moles of NH<sub>3</sub>: </html>");
		lblMolesCompound1Value = new JLabel();
		lblMolesCompound2Text = new JLabel("<html>Moles of NH<sub>4</sub>Cl: </html>");
		lblMolesCompound2Value = new JLabel();
		lblMolesCompound3Text = new JLabel("Moles of HCl:");
		lblMolesCompound3Value = new JLabel();
		lblMolesWaterText = new JLabel("Moles of Water: ");
		lblMolesWaterValue = new JLabel();
	}



	/* (non-Javadoc)
	 * @see simulations.UnitBase#updateMolecules(int, int)
	 */
	@Override
	public void updateMolecules(int sim, int set) {
		boolean reactionHappened = false;
		Simulation simulation = getSimulation(sim, set);
		curTime = p5Canvas.getMain().time;
		
		
		switch(sim)
		{
		case 1:
			if(set==1)
				reactionHappened = reactSim1Set1(simulation);
			if(set==2)
				reactionHappened = reactSim1Set2(simulation);
			else if(set==3)
				reactionHappened = reactSim1Set3(simulation);
			break;
		case 2:
			//reactionHappened = reactSim2Set1(simulation);
			if(set==1)
			updatePositionSim2Set1(simulation);
			else if(set==2)
				updatePositionSim2Set2(simulation);
			else if(set==3)
				updatePositionSim2Set3(simulation);
			else
				updatePositionSim2Set4(simulation);
			break;
		case 3:
			if(set==1)
				reactionHappened = reactSim1Set1(simulation);
			else if(set==2)
				reactionHappened = reactSim3Set2(simulation);
			else if(set==4)
				reactionHappened = reactSim3Set4(simulation);
			break;
		case 4:
			if(set==1)
				reactionHappened = reactSim4Set1(simulation);
			else if(set==2)
				;
			else if(set==3)
				reactionHappened = reactSim4Set3(simulation);
			else if(set==4)
				reactionHappened = reactSim4Set4(simulation);
			break;
//		case 5:
//			if(set==1)
//				reactionHappened = reactSim1Set3(simulation);
//			break;
		case 6:
			if(set==1)
				reactionHappened = reactSim1Set3(simulation);
			break;
		case 5:	
			if(set==1)
			{
				ArrayList<String> nameString = new ArrayList<String> ();
				for(Molecule mole: p5Canvas.killingList)
				{
					nameString.add(mole.getName());
				}
				if(nameString.contains("Ammonia")&&nameString.contains("Water"))
				{
					reactionHappened = reactSim3Set4(simulation);
					p5Canvas.killingList.clear();
				}
				else if(nameString.contains("Hydrogen-Ion")&&(nameString.contains("Hydroxide")||nameString.contains("Ammonia")))
				reactionHappened = reactSim7Set1(simulation);
			}
			break;

		}
	}
	//Update the position of microscope molecules for Sim 2 Set 1
	private boolean updatePositionSim2Set1(Simulation simulation)
	{
		Molecule hydrogenIon = State.getMoleculeByName(ionHash.get("Hydrogen-Chloride")[electronView]);
		Molecule hydroxide = State.getMoleculeByName(ionHash.get("Sodium-Hydroxide")[electronView]);

		updatePositionTwoMolcules(hydroxide,hydrogenIon,"Water","Sodium-Chloride");
		return true;
	}
	//Update the position of microscope molecules for Sim 2 Set 2
	private boolean updatePositionSim2Set2(Simulation simulation)
	{
		Molecule ammonia = State.getMoleculeByName(ionHash.get("Ammonia")[electronView]);
		Molecule hydrogen = State.getMoleculeByName(ionHash.get("Hydrogen-Chloride")[electronView]);

		updatePositionTwoMolcules(ammonia,hydrogen,"Ammonium");
		return true;
	}
	
	//Update the position of microscope molecules for Sim 2 Set 3
	private boolean updatePositionSim2Set3(Simulation simulation)
	{
		Molecule cyanide = State.getMoleculeByName(ionHash.get("Cyanide")[electronView]);
		Molecule hydrogen = State.getMoleculeByName(ionHash.get("Hydrogen-Bromide")[electronView]);

		updatePositionTwoMolcules(cyanide,hydrogen,"Hydrogen-Cyanide");
		return true;
	}
	
	//Update the position of microscope molecules for Sim 2 Set 4
	private boolean updatePositionSim2Set4(Simulation simulation)
	{
		Molecule boronTrichloride = State.getMoleculeByName(ionHash.get("Boron-Trichloride")[electronView]);
		Molecule chlorine = State.getMoleculeByName(ionHash.get("Chlorine-Ion")[electronView]);

		updatePositionTwoMolcules(boronTrichloride,chlorine,"Boron-Tetrachloride");
		return true;
	}
	
	private void updatePositionTwoMolcules(Molecule first, Molecule second,String ... product)
	{
		
		Vec2 pos = null;
		float angle =  0;
		
		if (!p5Canvas.isEnable || !p5Canvas.isSimStarted)
			return;
		
		//Rotate the first molecule
		if(interpolatorAngle1.isTargeting())
		{
			interpolatorAngle1.update();
			float newAngle = interpolatorAngle1.getValue();
			first.setAngle(newAngle);
			
//			if(!interpolatorAngle1.isTargeting())
//			{
//				System.out.println(" interpolatorAngle1 Stop targetting!");
//			}
		}
		//Rotate the second molecule
		if(interpolatorAngle2.isTargeting())
		{
			interpolatorAngle2.update();
			float newAngle = interpolatorAngle2.getValue();
			second.setAngle(newAngle);
			
//			if(!interpolatorAngle1.isTargeting())
//			{
//				System.out.println(" interpolatorAngle1 Stop targetting!");
//			}
		}
		
		//Move the first molecule
		if(interpolatorPos1.isTargeting())
		{
			interpolatorPos1.update();
			float currentPersentage = interpolatorPos1.getValue()/100;
			
			Vec2 translatePosition = new Vec2(translateVectorFirst.mul(currentPersentage));

			Vec2 currentPosition = new Vec2(lastPositionFirst.add(translatePosition));

			first.setPositionInPixel(currentPosition);

		}
		//Move the second molecule
		if(interpolatorPos2.isTargeting())
		{
			interpolatorPos2.update();
			float currentPersentage = interpolatorPos2.getValue()/100;
			
			Vec2 translatePosition = new Vec2(translateVectorSecond.mul(currentPersentage));

			Vec2 currentPosition = new Vec2(lastPositionSecond.add(translatePosition));

			second.setPositionInPixel(currentPosition);

//			if(!interpolatorPos2.isTargeting())
//			{
//				System.out.println(" interpolatorPos2 Stop targetting!");
//			}
		}
	
		
		if(hasFading)
		{
		//If both of the molecules finish moving, start fading out
		if(!interpolatorAngle1.isTargeting() && !interpolatorPos2.isTargeting()&& !isFading)
		{
			//Set up fade interpolator
			//Show`s transparency is from 0 - 100
			//Hide`s transparency is from 100 - 0
			interpolatorShow.set(100);
			interpolatorShow.target(0);
			interpolatorHide.set(0);
			interpolatorHide.target(100);
			

			//Create new molecules
			for(int i=0; i<product.length;i++)
			{
				if(i==0)
				{
					 pos = first.getPositionInPixel();
					 angle = first.getAngle();
				}
				else if(i==1)
				{
					 pos = second.getPositionInPixel();
					 angle = second.getAngle();
				}
				Molecule newMole = new Molecule(pos.x,pos.y,product[i],box2d,p5Canvas,angle);	
				newMole.setLinearVelocity(new Vec2(0, 0));
				newMole.setEnableAutoStateChange(false);
				newMole.setState(mState.Gas);
				newMole.setTransparent(1.0f);
				newMole.setFixtureCatergory(Constants.NONCOLLIDER_ID, Constants.BOUNDARY_ID);
				State.molecules.add(newMole);
				newMolecules.add(newMole);
				isFading = true;
			}
		}
		
		if(isFading)
		{
			
			if(interpolatorShow.isTargeting())
			{
				interpolatorShow.update();
				float tran = (float)interpolatorShow.getValue()/100;
				if(tran<0.01f)
					tran = 0.0f;
				System.out.println("Show trans is "+tran);

				for(Molecule newMole:newMolecules)
				{
					newMole.setTransparent(tran);
				}
			}
			if(interpolatorHide.isTargeting())
			{
				interpolatorHide.update();
				float trans = (float)interpolatorHide.getValue()/100;
				if(trans>0.98f)
					trans=1.0f;
				System.out.println("Hide trans is "+trans);
				first.setTransparent(trans);
				second.setTransparent(trans);
//				System.out.println("trans is "+trans);

			}
		}
		}

	}
	
	//Reaction function for sim 1 set 2
	public boolean reactSim1Set2(Simulation simulation) {

		if (!p5Canvas.killingList.isEmpty()) {
			// If it is dissolving process
			if (p5Canvas.killingList.get(0).getName().equals("Sodium-Hydroxide")) {
				if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

					int numToKill = p5Canvas.killingList.size();
					Molecule[] mOld = new Molecule[numToKill];
					Molecule dissolveCompound = null;
					for (int i = 0; i < numToKill; i++)
					{
						mOld[i] = (Molecule) p5Canvas.killingList.get(i);
						if(mOld[i].getName().equals("Sodium-Hydroxide"))
							dissolveCompound = mOld[i];
					}

					Molecule mNew = null;

					// Actually there is only one reaction going in each frame
					for (int i = 0; i < p5Canvas.products.size(); i++) {
						Vec2 loc = dissolveCompound.getPosition();
						String ionName = p5Canvas.products.get(i);
						float x1;
						
						int elementIndex = Compound.isIonOfElement(ionName, dissolveCompound);
						if(elementIndex !=-1 )
							loc.set(dissolveCompound.getElementLocation(elementIndex));
						x1 = PBox2D.scalarWorldToPixels(loc.x);
						float y1 = p5Canvas.h * p5Canvas.canvasScale
								- PBox2D.scalarWorldToPixels(loc.y);
						Vec2 newVec = new Vec2(x1, y1);
						mNew = new Molecule(newVec.x, newVec.y,
								ionName, box2d, p5Canvas,
								(float) (Math.PI / 2));
						mNew.setRatioKE(1 / simulation.getSpeed());

						State.molecules.add(mNew);
							mNew.body.setLinearVelocity(new Vec2(0,0));
							if(ionName.equals("Sodium-Ion")||ionName.equals("Hydroxide"))
							{
							//Set Sodium-Ion and Hydroxide tableIndex to "Sodium-Hydroxide"
							int tableIndex = p5Canvas.getTableView().getIndexByName("Sodium-Hydroxide");
							mNew.setTableIndex(tableIndex);
							}
						
					}
					for (int i = 0; i < numToKill; i++)
						mOld[i].destroy();

					p5Canvas.products.clear();
					p5Canvas.killingList.clear();
				}
			} 
		}
		return false;
	}
	//Reaction funciton for Sim 1 Set 3
	private boolean reactSim1Set3(Simulation simulation)
	{
		
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
			Molecule hydrogenIon = null;
			Molecule hydroxide = null;
			// Get Iron and copperIon reference
			if (p5Canvas.killingList.get(0).getName()
					.equals("Hydrogen-Ion")) {
				hydrogenIon = (Molecule) p5Canvas.killingList.get(0);
				hydroxide = (Molecule) p5Canvas.killingList.get(1);
			} else {
				hydroxide = (Molecule) p5Canvas.killingList.get(0);
				hydrogenIon = (Molecule) p5Canvas.killingList.get(1);
			}

//			Molecule silverChloride = null;
			Molecule newMole = null;
			Vec2 loc = null;

			//Create new molecule
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				loc = hydroxide.getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);

				String compoundName = new String(p5Canvas.products.get(i)); //"Water"
				newMole = new Molecule(newVec.x, newVec.y,
						compoundName, box2d, p5Canvas,
						(float) (Math.PI / 2));
				newMole.setRatioKE(1 / simulation.getSpeed());
				State.molecules.add(newMole);
				newMole.setLinearVelocity(hydroxide.body.getLinearVelocity());
				
				//Increate newMole count by 1
				int countIndex = Compound.names.indexOf(compoundName);
				Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
				
			}

			hydrogenIon.destroy();
			hydroxide.destroy();
			
			//Change tableview value
			boolean chlorineChanged=false;
			boolean sodiumChanged=false;
			Molecule mole = null;
			
			//Pick one chlorine-Ion  in reactants and set their table index as "Chloride"
			for( int i = 0;i<State.molecules.size();i++)
			{
				mole = State.molecules.get(i);
				//Change tableindex of Sodium-Ion from "Sodium-Bicarbonate" to "Sodium-Acetate"
				if(mole.getName().equals("Chlorine-Ion")&&mole.getTableIndex()==p5Canvas.getTableView().getIndexByName("Hydrochloric-Acid")&&!chlorineChanged)
				{
					int tableIndex = p5Canvas.getTableView().getIndexByName("Sodium-Chloride");
					mole.setTableIndex(tableIndex);
					chlorineChanged=true;
				}
				
				if(mole.getName().equals("Sodium-Ion")&&mole.getTableIndex()==p5Canvas.getTableView().getIndexByName("Sodium-Hydroxide")&&!sodiumChanged)
				{
					int tableIndex = p5Canvas.getTableView().getIndexByName("Sodium-Chloride");
					mole.setTableIndex(tableIndex);
					sodiumChanged=true;
				}
				
				

			}
			//Increase chlorine-Ion count by 1
			int countIndex = Compound.names.indexOf("Sodium-Chloride");
			Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);

			//Decrease Hydrochloride-Acid count by 1
			countIndex = Compound.names.indexOf("Hydrochloric-Acid");
			Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
			//Decrease Water count by 1
			countIndex = Compound.names.indexOf("Sodium-Hydroxide");
			Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);

			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			return true;
		}
		return false;
	}
		
	//Reaction funciton for Sim 1 Set 1
	private boolean reactSim1Set1(Simulation simulation)
	{
		
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
			Molecule hydrogenIon = null;
			Molecule water = null;
			// Get Iron and copperIon reference
			if (p5Canvas.killingList.get(0).getName()
					.equals("Hydrogen-Ion")) {
				hydrogenIon = (Molecule) p5Canvas.killingList.get(0);
				water = (Molecule) p5Canvas.killingList.get(1);
			} else {
				water = (Molecule) p5Canvas.killingList.get(0);
				hydrogenIon = (Molecule) p5Canvas.killingList.get(1);
			}

//			Molecule silverChloride = null;
			Molecule newMole = null;
			Vec2 loc = null;

			//Create new molecule
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				loc = water.getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);

				String compoundName = new String(p5Canvas.products.get(i)); //"Hydronium"
				newMole = new Molecule(newVec.x, newVec.y,
						compoundName, box2d, p5Canvas,
						(float) (Math.PI / 2));
				newMole.setRatioKE(1 / simulation.getSpeed());
				State.molecules.add(newMole);
				newMole.setLinearVelocity(water.body.getLinearVelocity());
				
				//Increate newMole count by 1
				int countIndex = Compound.names.indexOf(compoundName);
				Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
				
			}

			hydrogenIon.destroy();
			water.destroy();
			
			//Change tableview value
			boolean chlorineChanged=false;
			Molecule mole = null;
			
			//Pick one chlorine-Ion  in reactants and set their table index as "Chloride"
			for( int i = 0;i<State.molecules.size();i++)
			{
				mole = State.molecules.get(i);
				//Change tableindex of Sodium-Ion from "Sodium-Bicarbonate" to "Sodium-Acetate"
				if(mole.getName().equals("Chlorine-Ion")&&mole.getTableIndex()==p5Canvas.getTableView().getIndexByName("Hydrochloric-Acid")&&!chlorineChanged)
				{ 
					int tableIndex = p5Canvas.getTableView().getIndexByName("Chloride");
					mole.setTableIndex(tableIndex);
					chlorineChanged=true;
				}

			}
			//Increase chlorine-Ion count by 1
			int countIndex = Compound.names.indexOf("Chloride");
			Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);

			//Decrease Hydrochloride-Acid count by 1
			countIndex = Compound.names.indexOf("Hydrochloric-Acid");
			Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
			//Decrease Water count by 1
			countIndex = Compound.names.indexOf("Water");
			Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);

			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			return true;
		}
		return false;
	}
	
	
	//Reaction function for Sim 3 Set 2
	private boolean reactSim3Set2(Simulation simulation)
	{
		if(!p5Canvas.isSimStarted) //Reaction has not started yet
			return false;
		
		float conHF = getConByName("Hydrogen-Fluoride");
		float conH3O = getConByName("Hydronium");
		float conF = getConByName("Fluoride");
		float currentRatio = conH3O*conF/conHF;
		
		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0 && currentRatio<keq) {

			int numToKill = p5Canvas.killingList.size();
			Molecule[] mOld = new Molecule[numToKill];
			for (int i = 0; i < numToKill; i++)
				mOld[i] = (Molecule) p5Canvas.killingList.get(i);
			// Molecule m2 = (Molecule) p5Canvas.killingList.get(1);

			Molecule mNew = null;
			Molecule mNew2 = null;

			// Actually there is only one reaction going in each frame
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				Vec2 loc = mOld[0].getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);
				mNew = new Molecule(newVec.x, newVec.y,
						p5Canvas.products.get(i), box2d, p5Canvas,
						(float) (Math.PI / 2));
				mNew.setRatioKE(1 / simulation.getSpeed());
				State.molecules.add(mNew);

				//Add upward velocity
				Vec2 velocity = mOld[0].body.getLinearVelocity();
				mNew.body.setLinearVelocity(velocity);

			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			updateCompoundNumber(simulation);
			return true;
		}
		// Break up Compound if there are too many, in order to keep equalibrium
		if (curTime != oldTime) {
			
			if (currentRatio>keq) // If PCl5 is over numberred,break them up			
				{
				Random rand = new Random();
				if (rand.nextFloat() < breakProbability) {
					 return breakApartCompound(simulation);
				}
			}
			oldTime = curTime;
		}
		return false;
	}
	
	//Break apart N2O4 in Sim 1 Set 2  and Sim 2
	private boolean breakApartCompound(Simulation simulation)
	{
		Molecule moleProduct1 = null;
		Molecule moleProduct2 = null;
		String nameReactant1 = null;
		String nameReactant2 = null;
		String nameProduct1 = null;
		String nameProduct2 = null;
		
		if(simulation.isSimSelected(unitNum, 3, 2))//PCl3+Cl2<-->PCl5
		{
			nameReactant1 = "Hydrogen-Fluoride";
			nameReactant2 = "Water";
			nameProduct1 = "Fluoride";
			nameProduct2 = "Hydronium";

		}
		else if(simulation.isSimSelected(unitNum, 3, 4)||simulation.isSimSelected(unitNum, 7, 1))
 		{
			nameReactant1 = "Ammonia";
			nameReactant2 = "Water";
			nameProduct1 = "Ammonium";
			nameProduct2 = "Hydroxide";
		}
		else if(simulation.isSimSelected(unitNum, 4, 1))
		{
			nameReactant1 = "Acetic-Acid";
			nameReactant2 = "Water";
			nameProduct1 = "Acetate";
			nameProduct2 = "Hydronium";
		}
		else if(simulation.isSimSelected(unitNum, 4, 3))
		{
			nameReactant1 = "Methylamine";
			nameReactant2 = "Water";
			nameProduct1 = "Methylammonium";
			nameProduct2 = "Hydroxide";
		}
		
		
		for (int i = 0; i < State.molecules.size(); i++) {
			moleProduct1 = State.molecules.get(i);
			if (moleProduct1.getName().equals(nameProduct1)) {
				Vec2 loc = moleProduct1.getPosition();
			
				//Find the other product
				float radius = 100;
				Vec2 locThis = box2d.coordWorldToPixels(loc); //Pixel coordiate of this molecule
				Vec2 locOther = null;
				boolean foundProduct2 = false;
	
					// Go through all molecules to check if there are any molecules
					// nearby
					for (int j = 0; j < State.molecules.size(); j++) {
						moleProduct2 = State.molecules.get(j);
						if (moleProduct2.getName().equals(nameProduct2)
								&& moleProduct2!= moleProduct1 ) {
							 locOther = box2d.coordWorldToPixels(moleProduct2.getPosition());
							if (radius > computeDistance(locThis, locOther)) {
								foundProduct2 = true;
								break; // Break after we find one nearby
							}
						}
					}
					
					//Return if we didnt find another product2
					if(!foundProduct2)
						return false;
					
					//Ready to create reactants
					float x1 = PBox2D.scalarWorldToPixels(loc.x);
					float y1 = p5Canvas.h * p5Canvas.canvasScale
							- PBox2D.scalarWorldToPixels(loc.y);
					Vec2 newVec = new Vec2(x1, y1);
					
					Vec2 size = Molecule.getShapeSize(nameReactant1,
					p5Canvas);
				
				
					//Reactant 1
						newVec.x += size.x;
						Molecule mNew = new Molecule(newVec.x, newVec.y, nameReactant1, box2d,
								p5Canvas, (float) (Math.PI / 2));
						mNew.setRatioKE(1 / simulation.getSpeed());
						State.molecules.add(mNew);
						mNew.setFreezingPoint(0);
						mNew.setBoillingPoint(100);
						mNew.body.setLinearVelocity(moleProduct1.body
								.getLinearVelocity());
					//Reactant 2
						newVec.x -= size.x;
						Molecule mNew2 = new Molecule(newVec.x, newVec.y, nameReactant2, box2d,
								p5Canvas, (float) (Math.PI / 2));
						mNew2.setRatioKE(1 / simulation.getSpeed());
						State.molecules.add(mNew2);
						mNew2.setFreezingPoint(0);
						mNew2.setBoillingPoint(100);
						mNew2.body.setLinearVelocity(moleProduct1.body
								.getLinearVelocity().mulLocal(-1));
				
				if(mNew!=null &&mNew2!=null)
				{
					moleProduct1.destroy();
					if(moleProduct2!=null)
					moleProduct2.destroy();
	
					
					//Update molecule number
					int indexProduct1 = Compound.names.indexOf(nameProduct1);
					int indexProduct2 = Compound.names.indexOf(nameProduct2);
					int indexReactant1 = Compound.names.indexOf(nameReactant1);
					int indexReactant2 = Compound.names.indexOf(nameReactant2);
	
					Compound.counts.set(indexProduct1, Compound.counts.get(indexProduct1)-1);
					Compound.counts.set(indexProduct2, Compound.counts.get(indexProduct2)-1);
					Compound.counts.set(indexReactant1, Compound.counts.get(indexReactant1)+1);
					Compound.counts.set(indexReactant2, Compound.counts.get(indexReactant2)+1);
					
					oldTime = curTime;
					return true;
				}
			}
		}
		return false;
	}
	
	private float computeDistance(Vec2 v1, Vec2 v2) {
		float dis = 0;
		dis = (v1.x - v2.x) * (v1.x - v2.x) + (v1.y - v2.y) * (v1.y - v2.y);
		dis = (float) Math.sqrt(dis);

		return dis;
	}
	
	//Reaction function for Sim 3 Set 4
	private boolean reactSim3Set4(Simulation simulation)
	{
		
		if(!p5Canvas.isSimStarted) //Reaction has not started yet
			return false;
		
		float conNH3 = getConByName("Ammonia");
		float conNH4 = getConByName("Ammonium");
		float conOH = getConByName("Hydroxide");
		float currentRatio = conNH4*conOH/conNH3;
		
		if (p5Canvas.killingList.isEmpty())
			return false;
		
		if (p5Canvas.products != null && p5Canvas.products.size() > 0 && currentRatio<keq) {

			int numToKill = p5Canvas.killingList.size();
			Molecule[] mOld = new Molecule[numToKill];
			for (int i = 0; i < numToKill; i++)
				mOld[i] = (Molecule) p5Canvas.killingList.get(i);
			// Molecule m2 = (Molecule) p5Canvas.killingList.get(1);

			Molecule mNew = null;
			Molecule mNew2 = null;

			// Actually there is only one reaction going in each frame
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				Vec2 loc = mOld[0].getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);
				mNew = new Molecule(newVec.x, newVec.y,
						p5Canvas.products.get(i), box2d, p5Canvas,
						(float) (Math.PI / 2));
				mNew.setRatioKE(1 / simulation.getSpeed());
				mNew.setFreezingPoint(0);
				mNew.setBoillingPoint(100);
				State.molecules.add(mNew);

				//Add upward velocity
				Vec2 velocity = mOld[0].body.getLinearVelocity();
				mNew.body.setLinearVelocity(velocity);

			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			updateCompoundNumber(simulation);
			return true;
		}
		
		// Break up Compound if there are too many, in order to keep equalibrium
		if (curTime != oldTime) {
			
			if (currentRatio>keq) // If PCl5 is over numberred,break them up			
				{
				Random rand = new Random();
				if (rand.nextFloat() < breakProbability) {
					 return breakApartCompound(simulation);
				}
			}
			oldTime = curTime;
		}
		return false;
	}
	
	//Reaction function for Sim 4 Set 1
	private boolean reactSim4Set1(Simulation simulation)
	{
		if(!p5Canvas.isSimStarted) //Reaction has not started yet
			return false;
		
		float conAceticAcid = getConByName("Acetic-Acid");
		float conAcetate = getConByName("Acetate");
		float conH = getConByName("Hydronium");
		float currentRatio = conAcetate*conH/conAceticAcid;
		
		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0 && currentRatio<keq) {

			int numToKill = p5Canvas.killingList.size();
			Molecule[] mOld = new Molecule[numToKill];
			for (int i = 0; i < numToKill; i++)
				mOld[i] = (Molecule) p5Canvas.killingList.get(i);
			// Molecule m2 = (Molecule) p5Canvas.killingList.get(1);

			Molecule mNew = null;
			Molecule mNew2 = null;

			// Actually there is only one reaction going in each frame
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				Vec2 loc = mOld[0].getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);
				mNew = new Molecule(newVec.x, newVec.y,
						p5Canvas.products.get(i), box2d, p5Canvas,
						(float) (Math.PI / 2));
				mNew.setRatioKE(1 / simulation.getSpeed());
				mNew.setFreezingPoint(0);
				mNew.setBoillingPoint(100);
				State.molecules.add(mNew);

				//Add upward velocity
				Vec2 velocity = mOld[0].body.getLinearVelocity();
				mNew.body.setLinearVelocity(velocity);

			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			updateCompoundNumber(simulation);
			return true;
		}
		
		// Break up Compound if there are too many, in order to keep equalibrium
		if (curTime != oldTime) {
			
			if (currentRatio>keq) // If PCl5 is over numberred,break them up			
				{
				Random rand = new Random();
				if (rand.nextFloat() < breakProbability) {
					 return breakApartCompound(simulation);
				}
			}
			oldTime = curTime;
		}
		return false;
	}
	
	//Reaction function for Sim 4 Set 3
	private boolean reactSim4Set3(Simulation simulation)
	{
		if(!p5Canvas.isSimStarted) //Reaction has not started yet
			return false;
		
		float conMethylamine = getConByName("Methylamine");
		float conMethylammonium = getConByName("Methylammonium");
		float conOH = getConByName("Hydroxide");
		
		float currentRatio = conMethylammonium*conOH/conMethylamine;
		
		
		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0 && currentRatio<keq) {

			int numToKill = p5Canvas.killingList.size();
			Molecule[] mOld = new Molecule[numToKill];
			for (int i = 0; i < numToKill; i++)
				mOld[i] = (Molecule) p5Canvas.killingList.get(i);
			// Molecule m2 = (Molecule) p5Canvas.killingList.get(1);

			Molecule mNew = null;
			Molecule mNew2 = null;

			// Actually there is only one reaction going in each frame
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				Vec2 loc = mOld[0].getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);
				mNew = new Molecule(newVec.x, newVec.y,
						p5Canvas.products.get(i), box2d, p5Canvas,
						(float) (Math.PI / 2));
				mNew.setRatioKE(1 / simulation.getSpeed());
				mNew.setFreezingPoint(0);
				mNew.setBoillingPoint(100);
				State.molecules.add(mNew);

				//Add upward velocity
				Vec2 velocity = mOld[0].body.getLinearVelocity();
				mNew.body.setLinearVelocity(velocity);

			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			updateCompoundNumber(simulation);
			return true;
		}
		
		// Break up Compound if there are too many, in order to keep equalibrium
		if (curTime != oldTime) {
			
			if (currentRatio>keq) // If PCl5 is over numberred,break them up			
				{
				Random rand = new Random();
				if (rand.nextFloat() < breakProbability) {
					 return breakApartCompound(simulation);
				}
			}
			oldTime = curTime;
		}
		
		return false;
	}
	
	//Reaction funciton for Sim 4 Set 4
	private boolean reactSim4Set4(Simulation simulation)
	{
		
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
			Molecule hydrogenIon = null;
			Molecule water = null;
			// Get Iron and copperIon reference
			if (p5Canvas.killingList.get(0).getName()
					.equals("Hydrogen-Ion")) {
				hydrogenIon = (Molecule) p5Canvas.killingList.get(0);
				water = (Molecule) p5Canvas.killingList.get(1);
			} else {
				water = (Molecule) p5Canvas.killingList.get(0);
				hydrogenIon = (Molecule) p5Canvas.killingList.get(1);
			}

//			Molecule silverChloride = null;
			Molecule newMole = null;
			Vec2 loc = null;

			//Create new molecule
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				loc = water.getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);

				String compoundName = new String(p5Canvas.products.get(i)); //"Hydronium"
				newMole = new Molecule(newVec.x, newVec.y,
						compoundName, box2d, p5Canvas,
						(float) (Math.PI / 2));
				newMole.setRatioKE(1 / simulation.getSpeed());
				State.molecules.add(newMole);
				newMole.setLinearVelocity(water.body.getLinearVelocity());
				
				//Increate newMole count by 1
				int countIndex = Compound.names.indexOf(compoundName);
				Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
				
			}

			hydrogenIon.destroy();
			water.destroy();
			
			//Change tableview value
			boolean nitrateChanged=false;
			Molecule mole = null;
			
			//Pick one Nitrate  in reactants and set their table index as "Nitrate"
			for( int i = 0;i<State.molecules.size();i++)
			{
				mole = State.molecules.get(i);
				//Change tableindex of Sodium-Ion from "Sodium-Bicarbonate" to "Sodium-Acetate"
				if(mole.getName().equals("Nitrate")&&mole.getTableIndex()==p5Canvas.getTableView().getIndexByName("Nitric-Acid")&&!nitrateChanged)
				{ 
					int tableIndex = p5Canvas.getTableView().getIndexByName("Nitrate");
					mole.setTableIndex(tableIndex);
					nitrateChanged=true;
				}

			}
			//Increase chlorine-Ion count by 1
			int countIndex = Compound.names.indexOf("Nitrate");
			Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);

			//Decrease Hydrochloride-Acid count by 1
			countIndex = Compound.names.indexOf("Nitric-Acid");
			Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
			//Decrease Water count by 1
			countIndex = Compound.names.indexOf("Water");
			Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);

			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			return true;
		}
		return false;
	}
	
	
	//Reaction funciton for Sim 7 Set 1
	private boolean reactSim7Set1(Simulation simulation)
	{
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
			Molecule hydrogenIon = null;
			Molecule hydroxide = null;
			String name1 = p5Canvas.killingList.get(0).getName();
			String name2 = p5Canvas.killingList.get(1).getName();
			// Get Iron and copperIon reference
			if (name1.equals("Hydrogen-Ion")&&(name2.equals("Hydroxide")||name2.equals("Ammonia"))) {
				hydrogenIon = (Molecule) p5Canvas.killingList.get(0);
				hydroxide = (Molecule) p5Canvas.killingList.get(1);
			} else if (name2.equals("Hydrogen-Ion")&&(name1.equals("Hydroxide")||name1.equals("Ammonia"))){
				hydroxide = (Molecule) p5Canvas.killingList.get(0);
				hydrogenIon = (Molecule) p5Canvas.killingList.get(1);
			}
			if(hydrogenIon==null || hydroxide==null)
				return false;

//			Molecule silverChloride = null;
			Molecule newMole = null;
			Vec2 loc = null;

			//Create new molecule
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				loc = hydroxide.getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);

				String compoundName = new String(p5Canvas.products.get(i)); //"Water" or "Ammonium"
				newMole = new Molecule(newVec.x, newVec.y,
						compoundName, box2d, p5Canvas,
						(float) (Math.PI / 2));
				newMole.setRatioKE(1 / simulation.getSpeed());
				State.molecules.add(newMole);
				newMole.setLinearVelocity(hydroxide.body.getLinearVelocity());
				if(newMole.getName().equals("Ammonium"))
				{
					int tableIndex = p5Canvas.getTableView().getIndexByName("Ammonium-Chloride");
					newMole.setTableIndex(tableIndex);
				}
				
			}

			hydrogenIon.destroy();
			hydroxide.destroy();
			
			//Change tableview value
			boolean chlorineChanged=false;
			boolean ammoniumChanged=false;
			Molecule mole = null;
			
			//Associate molecule with correct table view index
			for( int i = 0;i<State.molecules.size();i++)
			{
				mole = State.molecules.get(i);
				//Change tableindex of Chlorine-Ion from "Hydrochloric-Acid" to "Ammonium-Chloride"
				if(mole.getName().equals("Chlorine-Ion")&&mole.getTableIndex()==p5Canvas.getTableView().getIndexByName("Hydrochloric-Acid")&&!chlorineChanged)
				{
					int tableIndex = p5Canvas.getTableView().getIndexByName("Ammonium-Chloride");
					mole.setTableIndex(tableIndex);
					chlorineChanged=true;
				}
				if(hydroxide.getName().equals("Hydroxide"))
				{
					//Change tableindex of Ammonium from "Ammonium" to "Ammonium-Chloride"
					if(mole.getName().equals("Ammonium")&&mole.getTableIndex()==p5Canvas.getTableView().getIndexByName("Ammonium")&&!ammoniumChanged)
					{
						int tableIndex = p5Canvas.getTableView().getIndexByName("Ammonium-Chloride");
						mole.setTableIndex(tableIndex);
						ammoniumChanged=true;
					}
				}
				

			}
			
			//Change count numbers in Table View
			int countIndex=0;
			//Increase Ammonium-Chloride count by 1
			countIndex = Compound.names.indexOf("Ammonium-Chloride");
			Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);

			//Decrease Hydrochloric-Acid count by 1
			countIndex = Compound.names.indexOf("Hydrochloric-Acid");
			Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
			
			if(hydroxide.getName().equals("Ammonia"))
			{
				//Decrease Ammonia count by 1
				countIndex = Compound.names.indexOf("Ammonia");
				Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
			}
			else if(hydroxide.getName().equals("Hydroxide"))
			{
				//Increate Water count by 1
				countIndex = Compound.names.indexOf("Water");
				Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
				
				//Decrease Ammonium count by 1
				countIndex = Compound.names.indexOf("Ammonium");
				Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
				countIndex = Compound.names.indexOf("Hydroxide");
				Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
			}

			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			return true;
		}
		return false;
	}
	
	
	/* (non-Javadoc)
	 * @see simulations.UnitBase#initialize()
	 */
	@Override
	public void initialize() {
		
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		Molecule first = null;
		Molecule second = null;
		
		if(sim==2)
		{
			if(set==1)
			{
				second = State.getMoleculeByName(ionHash.get("Hydrogen-Chloride")[electronView]);
				first  = State.getMoleculeByName(ionHash.get("Sodium-Hydroxide")[electronView]);
				initializeTwoMolcules(first	, second,(float)Math.PI,0.6f);
			}
			else if(set==2)
			{
				first = State.getMoleculeByName(ionHash.get("Ammonia")[electronView]);
				second  = State.getMoleculeByName(ionHash.get("Hydrogen-Chloride")[electronView]);
				initializeTwoMolcules(first	, second,(float)-Math.PI/2,0.525f);
			}
			else if(set==3)
			{
				first = State.getMoleculeByName(ionHash.get("Cyanide")[electronView]);
				second  = State.getMoleculeByName(ionHash.get("Hydrogen-Bromide")[electronView]);
				initializeTwoMolcules(first	, second,0,0.7f);
			}
			else if(set==4)
			{
				first = State.getMoleculeByName(ionHash.get("Boron-Trichloride")[electronView]);
				second  = State.getMoleculeByName(ionHash.get("Chlorine-Ion")[electronView]);
				initializeTwoMolcules(first	, second,(float)-Math.PI/2,0.55f);
			}
	
			
		}
	}
	
	
	//Initialize the interpolators for later molecule update
	//The first one rotates and the second translate
	//minRangeRatio means the ratio of minimum distance between the two molecules
	private void initializeTwoMolcules(Molecule first, Molecule second, float angle,float minRangeRatio)
	{

		Vec2 posFirst = first.getPositionInPixel();
		Vec2 posSecond = second.getPositionInPixel();
		float distance = computeDistance(posFirst,posSecond);
		
		float theta = 0;
		float angleFirst = 0 ;
		float angleSecond = 0;
		
		if(!interpolatorAngle1.isTargeting() && !interpolatorAngle2.isTargeting())
		{
			float acosValue = (posSecond.x-posFirst.x)/distance;
			theta = (float) Math.acos(acosValue);

			if(posFirst.y>posSecond.y) //In phase I and II cos function is monotone decreasing
			{
				//first = - theta
				//second = PI - theta
				angleFirst = -theta;
				angleSecond = (float) (Math.PI - theta);
			}
			else // posFirst.y < posSecond
			{
				//first = theta
				//second = PI + theta
				angleFirst = theta;
				angleSecond = (float) (Math.PI + theta);
			}
//			angleBetween+=Math.PI + angle; 
//			if(angleBetween>Math.PI)
//			{
//				angleBetween-=2*Math.PI;
//			}
			interpolatorAngle1.set(first.getAngle());
			interpolatorAngle1.target(angleFirst);
			interpolatorAngle2.set(second.getAngle());
			interpolatorAngle2.target(angleSecond);
		}

		if(!interpolatorPos2.isTargeting() && !interpolatorPos1.isTargeting())
		{
			float minDist = first.getShapeSize().x*minRangeRatio;
			if(distance>minDist)
			{
				
				float ratio = (distance - minDist)/distance;
				ratio/=2;
				lastPositionSecond.set(posSecond);
				translateVectorSecond.set((posFirst.sub(posSecond)).mul(ratio));
				
				lastPositionFirst.set(posFirst);
				translateVectorFirst.set((posSecond.sub(posFirst)).mul(ratio));
				
				interpolatorPos1.set(0);
				interpolatorPos1.target(100);  // 0 - 100 %
				interpolatorPos2.set(0);
				interpolatorPos2.target(100);  // 0 - 100 %

			}
		}

	}

	/* (non-Javadoc)
	 * @see simulations.UnitBase#reset()
	 */
	@Override
	protected void reset() {
		
		this.moleculeConHash.clear();
		keq = defaultKeq;
		curTime = 0;
		oldTime = 0;
		breakProbability = 0.75f; // The chance that N2O4 will break apart
		pH = 7;
		interpolatorAngle1.setTargeting(false);
		interpolatorAngle1.setAttraction(0.25f);
		interpolatorAngle1.setDamping(0.4f);
		interpolatorAngle2.setTargeting(false);
		interpolatorAngle2.setAttraction(0.25f);
		interpolatorAngle2.setDamping(0.4f);
		interpolatorPos1.setTargeting(false);
		interpolatorPos1.setAttraction(0.25f);
		interpolatorPos1.setDamping(0.4f);
		interpolatorPos2.setTargeting(false);
		interpolatorPos2.setAttraction(0.25f);
		interpolatorPos2.setDamping(0.4f);
		
		interpolatorHide.setTargeting(false);
		interpolatorHide.setAttraction(0.15f);
		interpolatorHide.setDamping(0.2f);
		interpolatorShow.setTargeting(false);
		interpolatorShow.setAttraction(0.15f);
		interpolatorShow.setDamping(0.2f);
		hasFading = false;
		isFading = false;
		
		newMolecules.clear();

		// Customization
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		Main main = p5Canvas.getMain();
		Simulation simulation = getSimulation(sim,set);

		
		// Set up speed ratio for molecules
		setupSpeed();
		
		switch(sim)
		{
		default:
			break;
		case 1:
			break;
		case 2:
			p5Canvas.isBoundaryShow = false;
			p5Canvas.setIfConstrainKE(false);
			p5Canvas.setEnableDrag(false);  //Disable drag function
			if(electronView==0)  //Bronsted Law
			{
				hasFading = true;
				//Set up simulation
				for(String name: ionHash.keySet())
				{
				simulation.setElementByIndex(ionHash.get(name)[0], simulation.getElementIndex(ionHash.get(name)[1]));
				}
			}
			else  //Lewis Law
			{
				hasFading = false;
				//Set up simulation
				for(String name: ionHash.keySet())
				{
				simulation.setElementByIndex(ionHash.get(name)[1], simulation.getElementIndex(ionHash.get(name)[0]));
				}
			}
			
			//disable view selection button
			main.btnBronsted.setEnabled(true);
			main.btnLewis.setEnabled(true);
			break;
		case 3:
			break;
		case 4:
			if(set==3)
				keq = 0.3f;
			break;
		case 5:
			break;
		case 6:
			break;
		case 7:
			break;
		}
		updateMoleculeCon();
	
	}
	
	//Called when user hit "PLAY"
	public void play()
	{
		if(p5Canvas.getSim()==2)
		{
			//disable view selection button
			Main main = p5Canvas.getMain();
			main.btnBronsted.setEnabled(false);
			main.btnLewis.setEnabled(false);
		}
	}
	
	private void setupSpeed() {
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		float speed = 1.0f;		
		
		switch(sim)
		{
		default:
			speed = 1;
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		case 5:
			break;
		case 6:
			break;
		case 7:
			break;
		}
		getSimulation(sim, set).setSpeed(speed);
	}
	
	public void resetDashboard(int sim,int set)
	{
		super.resetDashboard(sim, set);
		Main main = p5Canvas.getMain();
		Simulation simulation = getSimulation(sim,set);
		JPanel dashboard = main.dashboard;

		
		
		switch(sim)
		{
		case 1:
			if(set==1)
			{
			lblHConValue.setText("0");
			lblOHConValue.setText("0");
			}
			else if(set==2)
			{
				lblHConValue.setText("0");
				lblOHConValue.setText("0");
			}
			else if (set==3)
			{
				lblHConValue.setText("0");
				lblOHConValue.setText("0");
			}
			dashboard.add(this.lblHConText,"cell 0 1,align right");
			dashboard.add(this.lblHConValue,"cell 1 1,align left");
			dashboard.add(this.lblOHConText,"cell 0 2,align right");
			dashboard.add(this.lblOHConValue,"cell 1 2,align left");
			break;
		case 2:
			break;
		case 3:
			if(set==1)
			{
				lblHNumberValue.setText("0");
				lblOHNumberValue.setText("0");
				lblWaterNumberValue.setText("0");
			}
			else if(set==2)
			{
				lblHNumberValue.setText("0");
				lblOHNumberValue.setText("0");
				lblWaterNumberValue.setText("0");
			}
			else if(set==3)
			{
				lblHNumberValue.setText("0");
				lblOHNumberValue.setText("0");
				lblWaterNumberValue.setText("0");
			}
			else if(set==4)
			{
				lblHNumberValue.setText("0");
				lblOHNumberValue.setText("0");
				lblWaterNumberValue.setText("0");
			}
			dashboard.add(this.lblHNumberText,"cell 0 1,align right");
			dashboard.add(this.lblHNumberValue,"cell 1 1,align left");
			dashboard.add(this.lblOHNumberText,"cell 0 2, align right");
			dashboard.add(this.lblOHNumberValue,"cell 1 2, align left");
			dashboard.add(this.lblWaterNumberText,"cell 0 3,align right");
			dashboard.add(this.lblWaterNumberValue,"cell 1 3,align left");
			break;
		case 4:
			lblTempValue.setText(p5Canvas.temp+" \u2103");
			if(set==1)
			{
				lblHConValue.setText("0");
				lblOHConValue.setText("0");
				lblKeqValue.setText("0");
			}
			else if(set==2)
			{
				lblHConValue.setText("0");
				lblOHConValue.setText("0");
				lblKeqValue.setText("0");
			}
			else if(set==3)
			{
				lblHConValue.setText("0");
				lblOHConValue.setText("0");
				lblKeqValue.setText("0");
			}
			else if(set==4)
			{
				lblHConValue.setText("0");
				lblOHConValue.setText("0");
				lblKeqValue.setText("0");
			}
			dashboard.add(this.lblHConText,"cell 0 1,align right");
			dashboard.add(this.lblHConValue,"cell 1 1,align left");
			dashboard.add(this.lblOHConText,"cell 0 2,align right");
			dashboard.add(this.lblOHConValue,"cell 1 2,align left");
			dashboard.add(this.lblKeqText,"cell 0 3,align right");
			dashboard.add(this.lblKeqValue,"cell 1 3, align left");
			dashboard.add(this.lblTempText,"cell 0 4, align right");
			dashboard.add(this.lblTempValue,"cell 1 4, align left");
			break;
//		case 5:
//			break;
		case 6:
			main.btnGraphSwitch.setEnabled(true);
			break;
		case 5:
			if(set==1)
			{
				lblMolesCompound1Value.setText("0");
				lblMolesCompound2Value.setText("0");
				lblMolesCompound3Value.setText("0");
				lblMolesWaterValue.setText("0");
			}
			main.btnGraphSwitch.setEnabled(true);
			dashboard.add(this.lblPHText,"cell 0 1, align right");
			dashboard.add(this.lblPHValue,"cell 1 1, align left");
			dashboard.add(this.lblMolesCompound1Text,"cell 0 2, align right");
			dashboard.add(this.lblMolesCompound1Value,"cell 1 2, align left");
			dashboard.add(this.lblMolesCompound2Text,"cell 0 3, align right");
			dashboard.add(this.lblMolesCompound2Value,"cell 1 3, align left");
			dashboard.add(this.lblMolesCompound3Text,"cell 0 4, align right");
			dashboard.add(this.lblMolesCompound3Value,"cell 1 4, align left");
			dashboard.add(this.lblMolesWaterText,"cell 0 5,align right");
			dashboard.add(this.lblMolesWaterValue,"cell 1 5, align left");
			break;
		
		}
		
	}
	
	//Customize Interface in Main reset after all interface have been initialized
	public void customizeInterface(int sim, int set)
	{
		Main main = p5Canvas.getMain();
		//Customization
		main.heatSlider.setEnabled(false);
		main.volumeSlider.setEnabled(false);
		switch(p5Canvas.getSim())
		{
		case 1:
//			main.heatSlider.setEnabled(false);
//			main.volumeSlider.setEnabled(false);
			break;
		case 2:
//			main.volumeLabel.setText(p5Canvas.currentVolume+" L");
			main.heatSlider.setEnabled(false);
			main.volumeSlider.setEnabled(false);
			main.currentZoom = 200;
			main.zoomSlider.setEnabled(false);

			resetRightPanel(sim,set);
			break;
		
		}

	}

	private void resetRightPanel(int sim, int set) {
		Main main = p5Canvas.getMain();
		main.rightPanel.removeAll();

		switch(sim)
		{
		case 2:
			main.rightPanel.add(main.lblOutput, "cell 0 1");
			main.rightPanel.add(main.dashboard, "cell 0 2,growy");
			break;

		}

	}
	/* (non-Javadoc)
	 * @see simulations.UnitBase#updateOutput(int, int)
	 */
	@Override
	public void updateOutput(int sim, int set) {
		// Update lblTempValue
		DecimalFormat myFormatter = new DecimalFormat("###.##");
		String output = null;
		
		updateMoleculeCon();
		
		if(this.lblHConValue.isShowing())
		{
			output = myFormatter.format(getConByName("Hydronium"));
			lblHConValue.setText(output);
		}
		if(this.lblOHConValue.isShowing())
		{
			output = myFormatter.format(getConByName("Hydroxide"));
			lblOHConValue.setText(output);
		}
		if(this.lblPHValue.isShowing())
		{
		
			float result = getPH();
			
			output = myFormatter.format(result);
			lblPHValue.setText(output);
		}
		if(this.lblPOHValue.isShowing())
		{
			float pOH = (float) (-1* Math.log10(getConByName("Hydroxide")));
			output = myFormatter.format(pOH);
			lblPOHValue.setText(output);
		}
		if(this.lblHNumberValue.isShowing())
		{
			output = myFormatter.format(Compound.getMoleculeNum("Hydronium"));
			lblHNumberValue.setText(output);
		}
		if(this.lblOHNumberValue.isShowing())
		{
			output = myFormatter.format(Compound.getMoleculeNum("Hydroxide"));
			lblOHNumberValue.setText(output);
		}
		if(this.lblWaterNumberValue.isShowing())
		{
			output = myFormatter.format(Compound.getMoleculeNum("Water"));
			lblWaterNumberValue.setText(output);
		}
		if(this.lblMolesCompound1Value.isShowing())
		{
			output = myFormatter.format((float)Compound.getMoleculeNum("Ammonia")/this.numMoleculePerMole);
			lblMolesCompound1Value.setText(output);
		}
		if(this.lblMolesCompound2Value.isShowing())
		{
			output = myFormatter.format((float)Compound.getMoleculeNum("Ammonium-Chloride")/this.numMoleculePerMole);
			lblMolesCompound2Value.setText(output);
		}
		if(this.lblMolesCompound3Value.isShowing())
		{
			output = myFormatter.format((float)Compound.getMoleculeNum("Hydrochloric-Acid")/this.numMoleculePerMole);
			lblMolesCompound3Value.setText(output);
		}
		if(this.lblMolesWaterValue.isShowing())
		{
			output = myFormatter.format((float)Compound.getMoleculeNum("Water")/this.numMoleculePerMole);
			lblMolesWaterValue.setText(output);
		}
		if(this.lblKeqValue.isShowing())
		{
			//Update keq value label
			if(keq!=0)
			{
				output = myFormatter.format(keq);
				lblKeqValue.setText(output);
			}
			else
				lblKeqValue.setText("Infinity");
		}

	}
	
	public float getPH()
	{
		float pH;
		float pOH;
		float result;
		if(State.getMoleculeNumByName("Hydronium")!=0)
		pH = (float) (-1* Math.log10(getConByName("Hydronium")));
		else if(State.getMoleculeNumByName("Hydrogen-Ion")!=0)
			pH = (float) (-1* Math.log10(getConByName("Hydrogen-Ion")));
		else
			pH=7;
		
		if(State.getMoleculeNumByName("Hydroxide")!=0)
		pOH = (float) (-1* Math.log10(getConByName("Hydroxide")));
		else 
			pOH=7;
		
		result = (pH==7)?(pOH==7?7:14-pOH):pH;
		return result;
	}
	
	//Update Concentration value for all compounds
	private void updateMoleculeCon() {
		
		float mole = 0;
		float con = 0;
		float volume = (float) (p5Canvas.currentVolume/2) / 1000;
		//Clean old data
		Iterator it = moleculeConHash.keySet().iterator();
		while(it.hasNext())
		{
			String name = (String) it.next();
			moleculeConHash.put(name, 0.004f);
		}

		for (String name: State.getCompoundNames()) {
			//Special cases
			if(name.equals("Water"))
			{
				con =55.35f;
			}
			else  //General case
			{
				mole = (float) State.getMoleculeNumByName(name) / numMoleculePerMole;
				mole*=moleFactor;
				con = mole / volume;
			}
			moleculeConHash.put(name, con);
		}
		
	}

	/* (non-Javadoc)
	 * @see simulations.UnitBase#computeForce(int, int)
	 */
	@Override
	protected void computeForce(int sim, int set) {

		clearAllMoleculeForce();

		switch (sim) {
		case 1:
			if(set==1)
				computeForceLiftChloride();
			else if(set==2)
				computeForceSim1Set2();
			computeForceTopBoundary();
			break;
		case 2:
			break;
		case 3:
			if(set==1)
				;
			else if(set==2)
				computeForceSim3Set2();
			else if(set==3)
				;
			else if(set==4)
				;
			computeForceTopBoundary();

			break;
		case 4:
			computeForceTopBoundary();
			break;
//		case 5:
//			computeForceTopBoundary();
//			break;
		case 6:
			computeForceSim6Set1();
			computeForceTopBoundary();
			break;
		case 5:
			computeForceSim7Set1();
			computeForceTopBoundary();
			break;
		case 8:

		break;
		}
	}
	
	private void computeForceLiftChloride()
	{
		float gravityCompensation = 0.05f;

		for(Molecule mole: State.getMolecules())
		{
			Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());

				//Add anti-gravity to make them floating
				if(mole.getName().equals("Chlorine-Ion")) //Make sodium-Ion
				{
					//Anti-gravity force
					for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // element
						mole.sumForceX[thisE] += 0;
						if(mole.getName().equals("Hydrogen-Fluoride"))
						mole.sumForceY[thisE] += gravityCompensation;
						else
							mole.sumForceY[thisE] += gravityCompensation*3;

						
						}
				}
				//Attract Hydroxide to Hydrogen-Ion
				else if(mole.getName().equals("Hydroxide"))
				{
					
				}
		}		
		
	}
	
	// Foce computation for sim 1 set 2
	private void computeForceSim1Set2() {
		Molecule mole = null;
		String moleName = null;
		float repulsiveForceX = 1.0f;
		float repulsiveForceY = 0.5f;
		
		float forceYCompensation = 0.01f;


		float xValue = 0;
		float yValue = 0;
		float dis = 0;
		float disSquare = 0;
		float forceX = 0;
		float forceY = 0;
		Vec2 thisLoc = new Vec2(0, 0);
		Vec2 otherLoc = new Vec2(0, 0);

		for (int i = 0; i < State.getMoleculeNum(); i++) {
			mole = State.molecules.get(i);
			moleName = new String(mole.getName());

			if (moleName.equals("Sodium-Ion")) 
				//Force compute for NaOH, to separate Sodium-Ion from the solid
			{
				for (int thisE = 0; thisE < mole.getNumElement(); thisE++) {

					thisLoc.set(mole.getElementLocation(thisE));
					mole.sumForceX[thisE] = 0;
					mole.sumForceY[thisE] = 0;
					for (int k = 0; k < State.getMoleculeNum(); k++) {
						//Check all other molecules
						if (k == i)
							continue;
						Molecule m = State.molecules.get(k);
						String mName = m.getName();
						if (mName.equals("Sodium-Hydroxide")||mName.equals("Sodium-Ion")) {
							for (int otherE = 0; otherE < m.getNumElement(); otherE++)
							{
								otherLoc.set(m.getElementLocation(otherE));
								if (thisLoc == null || otherLoc == null)
									continue;
								xValue = thisLoc.x - otherLoc.x;
								yValue = thisLoc.y - otherLoc.y;
								disSquare = xValue * xValue + yValue* yValue;
								dis = (float) Math.sqrt(disSquare);
								xValue/=dis;
								yValue/=dis;
								forceX = (float) (xValue * (repulsiveForceX/disSquare));
								forceY = (float) (yValue * (repulsiveForceY/disSquare));
								if(forceY<0)
									forceY*=0;
								mole.sumForceX[thisE] += forceX;
								//mole.sumForceY[thisE] += forceY;
//							
								mole.sumForceY[thisE]+=forceYCompensation; 
							}

						}
					}

				}

			}

		}
	}
	
	private void computeForceSim3Set2()
	{
		float gravityCompensation = 0.05f;

		for(Molecule mole: State.getMolecules())
		{
			Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());

				//Add anti-gravity to make them floating
				if(mole.getName().equals("Hydrogen-Fluoride")||mole.getName().equals("Fluoride")) //Make sodium-Ion
				{
					//Anti-gravity force
					for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // element
						mole.sumForceX[thisE] += 0;
						if(mole.getName().equals("Hydrogen-Fluoride"))
						mole.sumForceY[thisE] += gravityCompensation;
						else
							mole.sumForceY[thisE] += gravityCompensation*3;

						
						}
				}
		}		
		
	}
	
	private void computeForceSim6Set1()
	{
		float gravityCompensation = 0.05f;
		float topBoundary = p5Canvas.h/2;
		float gravityScale = 0.01f;
		float xValue = 0 ;
		float yValue = 0;
		float dis = 0;
		float forceX = 0;
		float forceY = 0;
		float scale = 0.075f; // How strong the attract force is
		float forceYCompensation = 0.02f;
		float repulsiveForce = 1.2f; //How strong the repulsive force is
		Vec2 locThis = new Vec2();
		Vec2 locOther = new Vec2();

		for(Molecule mole: State.getMolecules())
		{
			Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());

				//Add anti-gravity to make them floating
				if(mole.getName().equals("Chlorine-Ion")) //Make sodium-Ion
				{
					//Anti-gravity force
					for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // element
						mole.sumForceX[thisE] += 0;
						if(mole.getName().equals("Hydrogen-Fluoride"))
						mole.sumForceY[thisE] += gravityCompensation;
						else
							mole.sumForceY[thisE] += gravityCompensation*3;

						
						}
				}
				//Attract Hydroxide to Hydrogen-Ion
				else if(mole.getName().equals("Hydroxide"))
				{
					
						for (int thisE = 0; thisE < mole.getNumElement(); thisE++) {
								locThis.set(mole.getElementLocation(thisE));
								mole.sumForceX[thisE] = 0;
								mole.sumForceY[thisE] = 0;
							ArrayList<Molecule> hydrogenIon = State.getMoleculesByName("Hydrogen-Ion");
							for(Molecule moleOther:hydrogenIon)
							{
								locOther.set(moleOther.getPosition());
								xValue = locOther.x - locThis.x;
								yValue = locOther.y - locThis.y;
								dis = (float) Math.sqrt(xValue * xValue + yValue
										* yValue);
								forceX = (float) (xValue / dis) * scale;
								forceY = (float) (yValue / dis) * scale+gravityCompensation*0.2f;
	
								mole.sumForceX[thisE] += forceX*2;
								mole.sumForceY[thisE] += forceY*2;
								
								moleOther.sumForceX[0] -= forceX;
								moleOther.sumForceY[0] -= forceY;
							}
							
						}
					
				}
		}		
		
	}
	
	private void computeForceSim7Set1()
	{
		float gravityCompensation = 0.05f;
		float topBoundary = p5Canvas.h/2;
		float gravityScale = 0.01f;
		float xValue = 0 ;
		float yValue = 0;
		float dis = 0;
		float forceX = 0;
		float forceY = 0;
		float scale = 0.075f; // How strong the attract force is
		float forceYCompensation = 0.02f;
		float repulsiveForce = 1.2f; //How strong the repulsive force is
		Vec2 locThis = new Vec2();
		Vec2 locOther = new Vec2();

		for(Molecule mole: State.getMolecules())
		{
			Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());

				//Add anti-gravity to make them floating
				if(mole.getName().equals("Chlorine-Ion")) //Make sodium-Ion
				{
					//Anti-gravity force
					for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // element
						mole.sumForceX[thisE] += 0;
						if(mole.getName().equals("Hydrogen-Fluoride"))
						mole.sumForceY[thisE] += gravityCompensation;
						else
							mole.sumForceY[thisE] += gravityCompensation*3;

						
						}
				}
				//Attract Ammonia to Hydrogen-Ion
				else if(mole.getName().equals("Ammonia"))
				{
					for (int thisE = 0; thisE < mole.getNumElement(); thisE++) {
							locThis.set(mole.getElementLocation(thisE));
							mole.sumForceX[thisE] = 0;
							mole.sumForceY[thisE] = 0;
						ArrayList<Molecule> hydrogenIon = State.getMoleculesByName("Hydrogen-Ion");
						for(Molecule moleOther:hydrogenIon)
						{
							locOther.set(moleOther.getPosition());
							xValue = locOther.x - locThis.x;
							yValue = locOther.y - locThis.y;
							dis = (float) Math.sqrt(xValue * xValue + yValue
									* yValue);
							forceX = (float) (xValue / dis) * scale;
							forceY = (float) (yValue / dis) * scale+gravityCompensation*0.2f;

							mole.sumForceX[thisE] += forceX*2;
							mole.sumForceY[thisE] += forceY*2;
							
							moleOther.sumForceX[0] -= forceX;
							moleOther.sumForceY[0] -= forceY;
						}
						
					}
				}
				//Attract Hydroxide to Hydrogen-Ion
				else if(mole.getName().equals("Hydroxide"))
				{
					if(State.getMoleculeNumByName("Ammonia")==0)
					{
						for (int thisE = 0; thisE < mole.getNumElement(); thisE++) {
								locThis.set(mole.getElementLocation(thisE));
								mole.sumForceX[thisE] = 0;
								mole.sumForceY[thisE] = 0;
							ArrayList<Molecule> hydrogenIon = State.getMoleculesByName("Hydrogen-Ion");
							for(Molecule moleOther:hydrogenIon)
							{
								locOther.set(moleOther.getPosition());
								xValue = locOther.x - locThis.x;
								yValue = locOther.y - locThis.y;
								dis = (float) Math.sqrt(xValue * xValue + yValue
										* yValue);
								forceX = (float) (xValue / dis) * scale;
								forceY = (float) (yValue / dis) * scale+gravityCompensation*0.2f;
	
								mole.sumForceX[thisE] += forceX*2;
								mole.sumForceY[thisE] += forceY*2;
								
								moleOther.sumForceX[0] -= forceX;
								moleOther.sumForceY[0] -= forceY;
							}
							
						}
					}
				}
		}		
		
	}
	
	private void computeForceTopBoundary()
	{
			float topBoundary = p5Canvas.h/2;
			float gravityCompensation = 0.2f;
			float gravityScale = 0.01f;
			// Check positions of all liquid molecules, in case they are not going
			// to high
			for(Molecule mole:State.getMolecules())
			{
				Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());

//				if(mole.getName().equals("Water"))
				{
					if (pos.y < topBoundary) {
						for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // Select
																						// element
							mole.sumForceX[thisE] += 0;
							mole.sumForceY[thisE] += (gravityCompensation+ gravityScale*(topBoundary-pos.y)) * -1;
			
						}
					}
				}
			}
	}

	/* (non-Javadoc)
	 * @see simulations.UnitBase#addMolecules(boolean, java.lang.String, int)
	 */
	@Override
	public boolean addMolecules(boolean isAppEnable, String compoundName,
			int count) {
		boolean res = false;

		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		Simulation simulation = getSimulation(sim, set);

		if(simulation.getSpawnStyle(compoundName) ==SpawnStyle.Precipitation)
		{
			res = this.addPrecipitation(isAppEnable, compoundName, count, simulation, 0);
			if (res) {
				// Connect new created molecule to table index
				int tIndex = p5Canvas.getTableView().getIndexByName(compoundName);
				int lastIndex = State.molecules.size() - 1;

				for (int i = 0; i < count; i++) {
					//Set up table view index
					State.molecules.get(lastIndex - i).setTableIndex(tIndex);
					//Set up speed
					State.molecules.get(lastIndex - i).setRatioKE(
							1 / simulation.getSpeed());
//					//Set up boiling point and freezing point
//					State.molecules.get(lastIndex - i).setBoillingPoint(100);
//					State.molecules.get(lastIndex - i).setFreezingPoint(0);
				}

			}
		}
		else //If compound is solvent or gas
		{
			String [] ionName = getIonsByName(compoundName);
			int len = ionName.length;
			for(int i = 0;i<len;i++)
			{
				SpawnStyle spawnStyle = simulation.getSpawnStyle(ionName[i]);
				if (spawnStyle == SpawnStyle.Solvent) {
					res = this.addSolvent(isAppEnable, ionName[i], count, simulation);
				}
				else if (spawnStyle == SpawnStyle.Gas)
				{
					res = this.addStaticMolecule(isAppEnable, ionName[i], count);
				}
			}
			if (res) {
				// Connect new created molecule to table index
				int tIndex = p5Canvas.getTableView().getIndexByName(compoundName);
				int lastIndex = State.molecules.size() - 1;

				for (int i = 0; i < len*count; i++) {
					//Set up table view index
					State.molecules.get(lastIndex - i).setTableIndex(tIndex);
					//Set up speed
					State.molecules.get(lastIndex - i).setRatioKE(
							1 / simulation.getSpeed());
					//Set up boiling point and freezing point
					State.molecules.get(lastIndex - i).setBoillingPoint(100);
					State.molecules.get(lastIndex - i).setFreezingPoint(0);
				}

			}
		}
		

		return res;	
	}
	
	/******************************************************************
	 * FUNCTION : addGasMolecule DESCRIPTION : Function to add gas molecules to
	 * PApplet Do area clear check when spawn
	 * 
	 * INPUTS : isAppEnable(boolean), compoundName(String), count(int) OUTPUTS:
	 * None
	 *******************************************************************/
	public boolean addStaticMolecule(boolean isAppEnable, String compoundName,
			int count) {
		boolean res = true;

		float x_ = 0; // X Coordinate for a specific molecule
		float y_ = 0; // Y Coordinate for a specific molecule

		Vec2 size = Molecule.getShapeSize(compoundName, p5Canvas);

		float moleWidth = size.x;
		float moleHeight = size.y;

		Random randX = new Random();
		Random randY = new Random();

		Vec2 molePos = new Vec2(0, 0);
		Vec2 molePosInPix = new Vec2(0, 0);
		Vec2 topLeft = new Vec2(0, 0);
		Vec2 botRight = new Vec2(0, 0);
		float spacing = moleWidth;
		float maxVelocity = 40;

		boolean isClear = false;

		for (int i = 0; i < count; i++) {

			isClear = false;
			while (!isClear) {
				isClear = true;
				x_ = moleWidth + randX.nextFloat()
						* (p5Canvas.w/2 - 2 * moleWidth);
				y_ = moleHeight + randY.nextFloat()
						* (p5Canvas.h/2 - 2 * moleHeight);
				molePos.set(x_, y_);
				topLeft.set(x_ - spacing, y_ - spacing);
				botRight.set(x_ + spacing, y_ + spacing);
				for (int m = 0; m < State.molecules.size(); m++) {

					if (!((String) State.molecules.get(m).getName()).equals("Water")) {
						molePos.set(State.molecules.get(m).getPosition());
						molePosInPix.set(box2d.coordWorldToPixels(molePos));

						if (areaBodyCheck(molePosInPix, topLeft, botRight)) {
							isClear = false;
						}
					}
				}

			}
			if (isClear) // We are able to add new molecule to current area if
							// it is clear
			{
				String svgFileName = null;
				if(electronView==1)  //Lewis Law
				{
					if(compoundName.equals("Hydroxide")||compoundName.equals("Ammonia")||compoundName.equals("Cyanide")||compoundName.equals("Boron-Trichloride"))
					{
						svgFileName = new String(compoundName+"-Dots");
					}
					else if(compoundName.equals("Chlorine-Atom")&& p5Canvas.getSim()==2 && p5Canvas.getSet()==4)
					{
						svgFileName = new String("Chloride-Dots");
					}
					else
						svgFileName = new String(compoundName);
				}
				else  //Bronsted Law
				{
					svgFileName = new String(compoundName);
				}
				Molecule newMole = new Molecule(x_, y_, compoundName, box2d,
						p5Canvas, 0,svgFileName);
				res = State.molecules.add(newMole);
				float velocityX = 0;
				float velocityY = 0;
				newMole.setLinearVelocity(new Vec2(velocityX, velocityY));
				newMole.setEnableAutoStateChange(false);
				newMole.setState(mState.Gas);
				//newMole.setGravityScale(0f);
			}

		}

		return res;
	}
	
	
	public void updateProperties(int sim,int set)
	{
	
//		if(sim==2)
//		{
//			for(Molecule mole: State.getMolecules())
//			{
//				mole.setGravityScale(0f);
//			}
//		}
	}
	
	
	//Translate compound in yaml config file into ions
	public String[] getIonsByName(String compound)
	{
		String [] res = new String [2];
		int index = 0;
		if(p5Canvas.getSim()==2)
			index = electronView;
		
		if(p5Canvas.getSim()!=2)
		{			
			if(compound.equals("Hydrochloric-Acid"))
			{
				res[0] = new String("Hydrogen-Ion");
				res[1] = new String ("Chlorine-Ion");
			}
			else if(compound.equals("Sodium-Hydroxide"))
			{
				res[0] = new String("Sodium-Ion");
				res[1] = new String("Hydroxide");
			}
			else if(compound.equals("Hydrogen-Bromide"))
			{
				res[0] = new String("Hydrogen-Ion");
				res[1] = new String("Bromine-Ion");
			}
			else if(compound.equals("Lithium-Hydroxide"))
			{
				res[0] = new String("Lithium-Ion");
				res[1] = new String("Hydroxide");
			}
			else if(compound.equals("Nitric-Acid"))
			{
				res[0] = new String("Nitrate");
				res[1] = new String("Hydrogen-Ion");
			}
			else if(compound.equals("Ammonium-Chloride"))
			{
				res[0] = new String("Ammonium");
				res[1] = new String("Chlorine-Ion");
			}
			else //Copy original compound to res
			{
				res = new String[1];
				if(compound.equals("Chloride"))
				{
					compound = new String("Chlorine-Ion");
				}
//				if(ionHash.containsKey(compound))
//				{
//					res[0] = new String(ionHash.get(compound)[index]);
//				}
				else
				{
				res[0] = new String(compound);
				}
			}
		}
		else   //For sim 2
		{
			res = new String[1];
			if(compound.equals("Chloride"))
			{
				compound = new String("Chlorine-Ion");
			}
			if(ionHash.containsKey(compound))
			{
				res[0] = new String(ionHash.get(compound)[index]);
			}
			else
			{
			res[0] = new String(compound);
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

		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();

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
			if (m1.getReactive() && m2.getReactive()) 
			{

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
	 * FUNCTION : getReactionProducts DESCRIPTION : Return objects based on
	 * input name Called by beginReaction
	 * 
	 * INPUTS : reactants (Array<String>) OUTPUTS: None
	 *******************************************************************/
	private ArrayList<String> getReactionProducts(ArrayList<String> reactants,
			Molecule m1, Molecule m2) {
		ArrayList<String> products = new ArrayList<String>();
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		Random rand = new Random();
		float probability = 1.0f;
		float randomFloat = 0f;
		switch (sim) {
		case 1:
			
			if(set==1)
			{
				// Sim 1 set 1  HCl + H2O -> H3O+ + Cl-
				if (reactants.contains("Hydrogen-Ion") && reactants.contains("Water")) {
					products.add("Hydronium");
				}
			}
			else if(set==2)
			{
				// Sim 1 set 2 NaOH + H2O -> Na+ + OH- + H2O
			}
			else if(set==3)
			{
				if(reactants.contains("Hydrogen-Ion")&&reactants.contains("Hydroxide"))
				{
					products.add("Water");
				}
			}
	
			break;
		case 2:
			if(set==1)
			{
				
			}
			else if(set==2)
			{
				
			}
			else if(set==3)
			{
				
			}
			break;
		case 3:
			if(set==1)
			{
				// Sim 3 set 1  HCl + H2O -> H3O+ + Cl-
				if (reactants.contains("Hydrogen-Ion") && reactants.contains("Water")) {
					products.add("Hydronium");
				}
			}
			else if(set==2)
			{
				if(reactants.contains("Hydrogen-Fluoride") && reactants.contains("Water"))
				{
					products.add("Hydronium");
					products.add("Fluoride");
				}
			}
			else if(set==3)
			{
				//Sim 3 set 3 NaOH+H20 ->Na+ + OH- +H2O
			}
			else if(set==4)
			{
				if(reactants.contains("Ammonia") && reactants.contains("Water"))
				{
					products.add("Ammonium");
					products.add("Hydroxide");
				}
			}
			break;
		case 4:
			if(set==1)
			{
				if(reactants.contains("Acetic-Acid") && reactants.contains("Water"))
				{
					products.add("Acetate");
					products.add("Hydronium");
				}
			}
			else if(set==2)
			{
				if(reactants.contains("Lithium-Hydroxide") && reactants.contains("Water"))
				{
					products.add("Lithium-Ion");
					products.add("Hydroxide");
				}
			}
			else if(set==3)
			{
				if(reactants.contains("Methylamine") && reactants.contains("Water"))
				{
					products.add("Methylammonium");
					products.add("Hydroxide");
				}
			}
			else if(set==4)
			{
				// Sim 3 set 1  HCl + H2O -> H3O+ + Cl-
				if (reactants.contains("Hydrogen-Ion") && reactants.contains("Water")) {
					products.add("Hydronium");
				}
			}
			break;
//		case 5:
//			if(set==1)
//			{
//				if(reactants.contains("Hydrogen-Ion")&&reactants.contains("Hydroxide"))
//				{
//					products.add("Water");
//				}
//			}
//			break;
		case 6:
			if(set==1)
			{
				if(reactants.contains("Hydrogen-Ion")&&reactants.contains("Hydroxide"))
				{
					products.add("Water");
				}
			}
			break;
		case 5:
			if(set==1)
			{
				if(reactants.contains("Ammonia") && reactants.contains("Water"))
				{
					products.add("Ammonium");
					products.add("Hydroxide");
				}
				else if(reactants.contains("Hydrogen-Ion")&&reactants.contains("Ammonia"))
				{
					products.add("Ammonium");
				}
				else if(reactants.contains("Hydroxide")&&reactants.contains("Hydrogen-Ion"))
				{
					if(State.getMoleculeNumByName("Ammonia")==0)
						products.add("Water");
				}
			}
			break;
		

		}
		return products;
	}
	
	/******************************************************************
	 * FUNCTION : getDissolutionProducts DESCRIPTION : Return elements of
	 * reactants
	 * 
	 * INPUTS : reactants (Array<String>) OUTPUTS: None
	 *******************************************************************/
	private ArrayList<String> getDissolutionProducts(ArrayList<String> collider) {
		ArrayList<String> products = new ArrayList<String>();
		// Sim 1 set 2 
		if (collider.contains("Sodium-Hydroxide")) {
			products.add("Sodium-Ion");
			products.add("Hydroxide");
		}
		else {
			// return null;
		}
		return products;

	}
	
	public float getConByName(String s) {
		if (moleculeConHash.containsKey(s))
			return moleculeConHash.get(s);
		else
			return 0;
	}

	//Last step of reset
	protected void initializeSimulation(int sim, int set) {
		this.updateMoleculeCon();
		
	}
	public void setElectronView(int v)
	{
		if(v==0 || v==1)
		{
			electronView =v;
		}
			
	}
	@Override
	public void updateMoleculeCountRelated(int sim, int set) {

		updateMoleculeCon();
	}


}
