package simulations;

import org.jbox2d.dynamics.contacts.Contact;

public class UnitList {
	
	private P5Canvas p5Canvas;
	private PBox2D box2D;
	
	private UnitBase [] unitList = null;
	private final int UNIT_NUM = 9;
	
	public UnitList(P5Canvas parent, PBox2D world)
	{
		p5Canvas = parent;
		box2D = world;
		unitList = new UnitBase [UNIT_NUM] ;
		
		unitList[0]=new Unit1(p5Canvas,box2D);
		unitList[1] = new Unit2(p5Canvas,box2D);
		unitList[2] = new Unit3(p5Canvas,box2D);
		unitList[3] = new Unit4(p5Canvas,box2D);
		unitList[4] = new Unit5(p5Canvas,box2D);
		unitList[5] = null;
		unitList[6] = null;
		unitList[7] = null;
		unitList[8] = null;
		
	}
	
	public void initialize(int unit)
	{
		if(isExist(unit))
			unitList[unit-1].initialize();
	}
	
	public void applyForce(int unit, int sim, int set)
	{
		if(isExist(unit))
		unitList[unit-1].applyForce(sim, set);
	}
	
	public void computeForces(int unit,int sim,int set) {
		if(isExist(unit))
		unitList[unit - 1].computeForce(sim,set);

	}
	
	public boolean addMolecule(int unit, boolean enable,String compoundName,int count)
	{
		if(isExist(unit))
		return unitList[unit - 1].addMolecules(enable,
				compoundName, count);
		else return false;
	}
	
	public void reset(int unit)
	{
		if(isExist(unit))
		unitList[unit - 1].reset();
	}
	
	public void updateMolecules(int unit,int sim, int set)
	{
		if(isExist(unit))
		unitList[unit - 1].updateMolecules(sim,set);
	}
	
	public void beginContact(int unit, Contact c)
	{
		if(isExist(unit))
		unitList[unit -1].beginReaction(c);
	}
	
	public void setupReactionProducts(int unit, int sim,int set)
	{
		if(isExist(unit))
		unitList[unit-1].setupReactionProducts(sim, set);
	}
	
	public void resetDashboard(int unit,int sim, int set)
	{
		if(isExist(unit))
			unitList[unit-1].resetDashboard(sim,set);
	}
	
	public void updateOutput(int unit,int sim, int set)
	{
		if(isExist(unit))
			unitList[unit-1].updateOutput(sim,set);
	}
	
	public Unit2 getUnit2()
	{
		return (Unit2) unitList[1];
	}
	public Unit3 getUnit3()
	{
		return (Unit3) unitList[2];
	}
	public Unit4 getUnit4()
	{
		return (Unit4) unitList[3];
	}
	
	private boolean isExist(int unit)
	{
		return !(unitList[unit-1]==null);
	}
	
	
	


}