package simulations;

import org.jbox2d.dynamics.contacts.Contact;

import simulations.models.Simulation;
import simulations.models.Simulation.SpawnStyle;

public class Unit4 extends UnitBase {

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
		
		
	}

	@Override
	public void updateMolecules(int sim, int set) {
		// TODO Auto-generated method stub
		//No reactions happen in this unit

	}

	@Override
	protected void reset() {
		// TODO Auto-generated method stub
		setupSimulations();
		
		//box2d.setGravity(0f, 0f);
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
		
	}

}
