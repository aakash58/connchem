package simulations;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import javax.swing.JSlider;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;

import Util.ColorScales;

import data.State;

import simulations.models.Boundary;
import simulations.models.Molecule;
import simulations.models.Simulation;
import simulations.models.Simulation.SpawnStyle;

public class Unit4 extends UnitBase {
	
	private int collisionCount = 0;
	private int frameCounter = 0;
	private int computeTriggerInterval = p5Canvas.FRAME_RATE*5;
	public float lastTemp;
	private int lastVolume;
	private int lastMole;
	private float lastXDrag=0;
	private float lastYDrag=0;
	
	//Parameter for trails
	int trailFastColor = Color.RED.getRGB();
	int trailSlowColor = Color.BLUE.getRGB();
	ArrayList<Vec2> collisionPositions = new ArrayList<Vec2>();
	ArrayList<Color> collisionColors = new ArrayList<Color>();
	Color colorMax = Color.red;
	Color colorMin = Color.blue;
	int trailMoleculeId = 0; // Keep track of id of molecule whose trail is
	// showing, used in Unit 4 Sim 2
	private float trailDist = 600f;

	public Unit4(P5Canvas parent, PBox2D box) {
		super(parent, box);
		// TODO Auto-generated constructor stub
		unitNum = 4;
		setupSimulations();
		lastTemp = p5Canvas.temp;
		lastVolume = p5Canvas.currentVolume;
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
		lastTemp = p5Canvas.temp;
		lastVolume = p5Canvas.currentVolume;
		collisionPositions.clear();
		collisionColors.clear();
		
		switch(p5Canvas.getMain().selectedSim)
		{
		case 1:
			//Heat slider control disabled
			p5Canvas.getMain().heatSlider.setEnabled(false);
			if(p5Canvas.getMain().selectedSet==2)
				p5Canvas.temp = 400;
			break;
		case 2:
			//box2d.setGravity(0f, 0f);
			p5Canvas.getMain().heatSlider.setEnabled(false);
			p5Canvas.temp =100;
			
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
				//Make initial volume smaller
				p5Canvas.currentVolume/=2;
				lastTemp = p5Canvas.temp;
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
			if(!moleculeSliderMap.isEmpty())
			{
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
			}
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


	// Molecule Trail rendering function used in Unit 4 Sim 2
	public void  displayTrail() {
		Molecule mole = null;
		if (p5Canvas.isEnable && p5Canvas.isSimStarted) {
			if (State.molecules.size() > trailMoleculeId)
				;
			mole = State.molecules.get(trailMoleculeId);
		}
		if (mole == null)
			return;

		p5Canvas.stroke(204, 102, 0);
		Vec2 ori = null;
		Vec2 des = new Vec2(0, 0);
		Vec2 oriPixel = new Vec2();
		Vec2 desPixel = new Vec2();
		float totalDist = 0;
		float segDist = 0;
		float xDiff = 0; // X difference between origin and dest
		float yDiff = 0; // Y difference between origin and dest
		
		// We need to draw collision points in reverse order so that the old
		// ones do not overlay newer one
		// Use stack to save collsion points
		Stack<Vec2> collisionStack = new Stack<Vec2>();
		Stack<Color> cColor = new Stack<Color>();
		boolean isFull = false;
		
		//Add color based on current velocity
		Color color = calculateTrailColor(mole);
		cColor.push(color);

		p5Canvas.strokeWeight(3);
		for (int i = collisionPositions.size() - 1; i >= 0; i--) {
			if (!isFull) {
				if (ori == null) { //Get current molecule position
					ori = new Vec2(mole.getPosition());
					ori = box2d.coordWorldToPixels(ori);
					collisionStack.push(new Vec2(ori));
				}
				des.set(collisionPositions.get(i));
				if (!ori.equals(des)) {
					oriPixel.set(ori);
					desPixel.set(des);
					// Update trail position when user is dragging
					
					if (p5Canvas.isDrag) {
						desPixel.x += (p5Canvas.xDrag);
						desPixel.y += (p5Canvas.yDrag);
					}
					else
					{
						// Calculate distance of the whole trail
						xDiff = (desPixel.x - oriPixel.x);
						yDiff = desPixel.y - oriPixel.y;
						segDist = (float) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
						//stroke(collisionColors.get(i).getRGB());				
						//If trail is too long, trim from tail
						if (totalDist + segDist > trailDist) {
							float realDist = (trailDist - totalDist);
							float ratio = realDist / segDist;
							desPixel.x = xDiff * ratio + oriPixel.x;
							desPixel.y = yDiff * ratio + oriPixel.y;
							// line(oriPixel.x,oriPixel.y,desPixel.x,desPixel.y);
							totalDist += realDist;
							isFull = true;
						} else {
							totalDist += segDist;
							// line(oriPixel.x,oriPixel.y,desPixel.x,desPixel.y);
						}
					}
					if(!collisionColors.isEmpty())
					{	
						if(i<collisionColors.size())
						    cColor.push(collisionColors.get(i));
					}
					collisionStack.push(new Vec2(desPixel));
					ori.set(des);

				}
			} 
			
			else // If the points are enough to draw, we delete redundency
			{
				collisionPositions.remove(i);
				collisionColors.remove(i);
			}
			
		}
		//Ready to draw trail
		if( collisionStack.empty())
			return;
		Vec2 start=new Vec2(collisionStack.pop());
		Vec2 end = null;

		
		while(!collisionStack.empty())
		{
			end = new Vec2(collisionStack.pop());
			if(start!=null && end!=null)
			{
				p5Canvas.stroke(cColor.pop().getRGB());
				p5Canvas.line(start.x,start.y,end.x,end.y);
			}
			else
				break;
			start.set(end);
		}

	}
	
	private Color calculateTrailColor(Molecule mole)
	{
		float averageVelocity = (float) Math.sqrt(2 * p5Canvas.averageKineticEnergy
				/ mole.getBodyMass());
		float ratio = mole.getLinearVelocityScalar() / (averageVelocity);
		float factor = 0.5f;
		float min = 1 - factor;
		float max = 1 + factor;
		int red, green, blue;
		double v = (double) (ratio - min) / (max - min);
		if (v < 0)
			v = 0;
		else if (v > 1)
			v = 1;
		Color color = ColorScales.getColor(1 - v, "redblue", 1f);
		return color;
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
		case 2:
				trackMoleculeCollision(c);
		case 3:
			countWallCollision(c);
			break;
		case 4:
			if(p5Canvas.getMain().selectedSet==2)
				moveTopBoundary(c);
			break;
		}
	}
	
	// Function that save collision point of a overwatched molecule
	// Called by beginContact
	private void trackMoleculeCollision(Contact c) {
		Molecule mole = State.molecules.get(trailMoleculeId);
		if (mole == null)
			return;

		Object o1 = c.m_fixtureA.m_body.getUserData();
		Object o2 = c.m_fixtureB.m_body.getUserData();
		if (o1 == null || o2 == null || (o1 != mole && o2 != mole))
			return;

		// There must be one object that equals to mole
		Vec2 pos = new Vec2(mole.getPosition());
		pos = box2d.coordWorldToPixels(pos);
		if(!pos.equals(collisionPositions.get(collisionPositions.size()-1)))
		{
		collisionPositions.add(pos);
		// Calculate colors
		Color color = calculateTrailColor(mole);
		collisionColors.add(color);
		}
		// System.out.println("Collision Saved:"+mole.getPosition());
	}
	
	// For Unit4 Sim4 Set2, move top boundary when temperature changes in order
	// to keep pressure constant
	private void moveTopBoundary(Contact c) {
		
		if (!p5Canvas.isEnable || !p5Canvas.isSimStarted)
			return;
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
		if (c1.equals("simulations.models.Molecule") && o2 == p5Canvas.boundaries[2]) {
			mole = (Molecule) o1;
			boundary = (Boundary) o2;
		} else if (o1 == p5Canvas.boundaries[2]
				&& c2.equals("simulations.models.Molecule")) {
			mole = (Molecule) o2;
			boundary = (Boundary) o1;
		}
		if (mole == null || boundary == null)
			return;
		

		float oldPressure = p5Canvas.pressure;

		p5Canvas.temp = p5Canvas.getTempFromKE();
		lastVolume = p5Canvas.currentVolume;
		// According to below equation, volume should go up with temp-tempMin
		// proportionally
		// pressure = (mol* R* (temp-tempMin))/(currentVolume);
		float ratioTemp = (p5Canvas.temp - p5Canvas.tempMin) / (lastTemp - p5Canvas.tempMin);
		float ratioMole = (float)State.molecules.size()/lastMole;
		p5Canvas.currentVolume= (int) Math.round(ratioTemp*ratioMole * lastVolume);

		// Change volume slider
		if (p5Canvas.currentVolume < p5Canvas.volumeMinBoundary)
			p5Canvas.currentVolume = p5Canvas.volumeMinBoundary;
		if (p5Canvas.currentVolume > p5Canvas.volumeMaxBoundary)
			p5Canvas.currentVolume = p5Canvas.volumeMaxBoundary;

		p5Canvas.getMain().volumeSlider.setValue(p5Canvas.currentVolume);
		// Change volume label
		p5Canvas.getMain().volumeLabel.setText(p5Canvas.currentVolume + " mL");
		
		lastTemp = p5Canvas.temp;
		lastMole = State.molecules.size();

	}
	
	//For Unit4 Sim3, count collision time between molecules and walls
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

	@Override
	public void initialize() {
		
		// Add the start position of molecule whose trail shows
		if (State.molecules.size() > trailMoleculeId) {
			Vec2 pos = new Vec2(State.molecules.get(trailMoleculeId)
					.getPosition());
			pos = box2d.coordWorldToPixels(pos);
			collisionPositions.add(pos);
			//Color initialColor = ColorScales.getColor(0.5f, "redblue", 1f);
			//collisionColors.add(initialColor);
			// State.molecules.get(trailMoleculeId).setShowTrail(true);
		}
		lastMole = State.molecules.size();
		
	}
	
	public void mouseReleased()
	{
		//Update collision positions
		for(Vec2 vec: this.collisionPositions)
		{
			vec.x+=p5Canvas.xDrag;
			vec.y+=p5Canvas.yDrag;
		}
		
		lastXDrag = p5Canvas.xDrag;
		lastYDrag = p5Canvas.yDrag;
	}

}
