package simulations;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.JSlider;

import org.jbox2d.dynamics.contacts.Contact;

import data.State;

import simulations.models.Boundary;
import simulations.models.Molecule;
import simulations.models.Simulation;
import simulations.models.Simulation.SpawnStyle;

public class Unit4 extends UnitBase {
	
	private int collisionCount = 0;
	private int frameCounter = 0;
	private int computeTriggerInterval = p5Canvas.FRAME_RATE*5;

	public Unit4(P5Canvas parent, PBox2D box) {
		super(parent, box);
		// TODO Auto-generated constructor stub
		unitNum = 4;
		setupSimulations();
	}

	@Override
	public void setupSimulations() {
		// TODO Auto-generated method stub
		simulations = new Simulation[SIMULATION_NUMBER];

		simulations[0] = new Simulation(unitNum, 1, 1);
		String[] elements0 = {"Helium"};
		SpawnStyle[] spawnStyles0 = { SpawnStyle.Gas };
		simulations[0].setupElements(elements0, spawnStyles0);
		
		simulations[1] = new Simulation(unitNum, 1, 2);
		String[] elements1 = { "Chlorine","Oxygen"};
		SpawnStyle[] spawnStyles1 = { SpawnStyle.Gas,SpawnStyle.Gas };
		simulations[1].setupElements(elements1, spawnStyles1);
		
		simulations[2] = new Simulation(unitNum, 2, 1);
		String[] elements2 = {"Bromine"};
		SpawnStyle[] spawnStyles2 = { SpawnStyle.Gas };
		simulations[2].setupElements(elements2, spawnStyles2);
		
		simulations[3] = new Simulation(unitNum, 3, 1);
		String[] elements3 = {"Helium"};
		SpawnStyle[] spawnStyles3 = { SpawnStyle.Gas };
		simulations[3].setupElements(elements3, spawnStyles3);
		
		simulations[4] = new Simulation(unitNum, 4, 1);
		String[] elements4 = {"Helium"};
		SpawnStyle[] spawnStyles4 = { SpawnStyle.Gas };
		simulations[4].setupElements(elements4, spawnStyles4);
		
		simulations[5] = new Simulation(unitNum, 4, 2);
		String[] elements5 = {"Helium"};
		SpawnStyle[] spawnStyles5 = { SpawnStyle.Gas };
		simulations[5].setupElements(elements5, spawnStyles5);
		
		simulations[6] = new Simulation(unitNum, 4, 3);
		String[] elements6 = {"Helium"};
		SpawnStyle[] spawnStyles6 = { SpawnStyle.Gas };
		simulations[6].setupElements(elements6, spawnStyles6);
		
		simulations[7] = new Simulation(unitNum, 5, 1);
		String[] elements7 = {"Helium","Oxygen","Chlorine","Carbon-Dioxide"};
		SpawnStyle[] spawnStyles7 = { SpawnStyle.Gas,SpawnStyle.Gas,SpawnStyle.Gas,SpawnStyle.Gas };
		simulations[7].setupElements(elements7, spawnStyles7);
		
		
	}

	@Override
	public void updateMolecules(int sim, int set) {
		// TODO Auto-generated method stub
		//No reactions happen in this unit
		if(p5Canvas.isEnable)
		{
			frameCounter++;
			if (frameCounter >= this.computeTriggerInterval)
			{
				//System.out.println("Collision count is "+collisionCount);
				p5Canvas.getMain().lblCollisionValue.setText(Integer.toString(collisionCount));
				frameCounter = 0;
				collisionCount = 0;
			}
		}
	}

	@Override
	protected void reset() {
		// TODO Auto-generated method stub
		setupSimulations();
		
		
		switch(p5Canvas.getMain().selectedSim)
		{
		case 1:
			//Heat slider control disabled
			p5Canvas.getMain().heatSlider.setEnabled(false);
			break;
		case 2:
			//box2d.setGravity(0f, 0f);
			p5Canvas.getMain().heatSlider.setEnabled(false);
			p5Canvas.temp =60;
			
			//Add first point to trail points list.
			//if(State.molecules.size()>p5Canvas.trailMoleculeId)
			//p5Canvas.collisionPositions.add(State.molecules.get(p5Canvas.trailMoleculeId).getPosition());
			break;
		case 3:
			p5Canvas.getMain().heatSlider.setEnabled(false);
			break;
		case 4:
			if(p5Canvas.getMain().selectedSet==1)
			{
				p5Canvas.getMain().barPressure.setMax(800);
				p5Canvas.getMain().heatSlider.setEnabled(false);
			}
			else if (p5Canvas.getMain().selectedSet==2)
			{
				p5Canvas.getMain().volumeSlider.setEnabled(false);
			}
			else if (p5Canvas.getMain().selectedSet==3)
			{
				p5Canvas.getMain().volumeSlider.setEnabled(false);
			}
			break;
		case 5:
			p5Canvas.getMain().volumeSlider.setEnabled(false);
			p5Canvas.getMain().heatSlider.setEnabled(false);
			
			HashMap moleculeSliderMap = p5Canvas.getMain().moleculeSliderMap;
			JSlider slider =(JSlider) moleculeSliderMap.get("Helium");
			slider.setValue(8);
			slider.setEnabled(false);
			slider = (JSlider)moleculeSliderMap.get("Oxygen");
			slider.setValue(6);
			slider.setEnabled(false);
			slider = (JSlider) moleculeSliderMap.get("Chlorine");
			slider.setValue(8);
			slider.setEnabled(false);
			slider = (JSlider) moleculeSliderMap.get("Carbon-Dioxide");
			slider.setValue(5);
			slider.setEnabled(false);
			
			p5Canvas.temp=60;
			
			break;
			default:
				break;
		
		}
	}

	@Override
	protected void computeForce(int sim, int set) {
		// TODO Auto-generated method stub

	}



	@Override
	public boolean addMolecules(boolean isAppEnable, String compoundName,
			int count) {
		// TODO Auto-generated method stub
			boolean res = false;

			int sim = p5Canvas.getMain().selectedSim;
			int set = p5Canvas.getMain().selectedSet;
			Simulation simulation = this.getSimulation(sim, set);
			SpawnStyle spawnStyle = simulation.getSpawnStyle(compoundName);
			if (spawnStyle == SpawnStyle.Gas) {
				res = this.addGasMolecule(isAppEnable, compoundName, count);
			}
		return res;
	}

	@Override
	public void setupReactionProducts(int sim, int set) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beginReaction(Contact c) {
		// TODO Auto-generated method stub

		
		switch(p5Canvas.getMain().selectedSim)
		{
		case 3:
			countWallCollision(c);
			break;
		}
	}
	
	private void countWallCollision(Contact c)
	{
		Molecule mole = null;
		Boundary boundary = null;
	
		// Get our objects that reference these bodies
		Object o1 = c.m_fixtureA.m_body.getUserData();
		Object o2 = c.m_fixtureB.m_body.getUserData();
		if (o1 == null || o2 == null)
			return;

		String c1 = o1.getClass().getName();
		String c2 = o2.getClass().getName();
		// Make sure reaction only takes place between molecules and boundaries
		if (c1.equals("simulations.models.Molecule") && c2.equals("simulations.models.Boundary")) {
			mole = (Molecule) o1;
			boundary = (Boundary) o2;
		}
		else if ( c1.equals("simulations.models.Boundary") && c2.equals("simulations.models.Molecule"))
		{
			mole = (Molecule) o2;
			boundary = (Boundary)o1;
		}
		if( mole ==null ||boundary ==null)
			return;
		
		collisionCount ++;
		
	}

}
