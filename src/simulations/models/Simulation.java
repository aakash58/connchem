/**
 * 
 */
package simulations.models;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;

/**
 * @author Qin Li
 * Simulation is the basic unit of our chemical simulations
 * This class includes all parameter a simulation needs
 */
public class Simulation {
	
	//Enum parameter defines spawn styles for different molecules
	public enum SpawnStyle {
		Gas,
		Liquid,
		Solvent,
		Precipitation, //Dissolvable compound spawn like precipitation
		SolidCube,
		SolidPavement,
		SolidSpecial,
		
}
	
	private int unit;
	private int sim;
	private int set;
	private int elementNum;
	private final int MAX_ELEMENT_NUM = 25;
	private String [] elements;
	private SpawnStyle [] elementSpawnStyles;
	private float distanceBetweenMolecule;
	private int anchorNum = 0;
	private Vec2 anchorPos[];
	
	private float speed =1.0f; //Molecule speed moditifier for simulation
	
	public Simulation()
	{
		
	}
	public Simulation(int unit, int sim, int set)
	{
		this.unit = unit;
		this.sim = sim;
		this.set = set;
		elements = new String [MAX_ELEMENT_NUM];
		elementSpawnStyles =  new SpawnStyle[MAX_ELEMENT_NUM];
	}
	public Simulation(int unit, int sim, int set,int elementNum)
	{
		this.unit = unit;
		this.sim = sim;
		this.set = set;
		elements = new String [elementNum];
		elementSpawnStyles =  new SpawnStyle[elementNum];
	}
	public void setupElements(String []ele, SpawnStyle [] style)
	{
		elements = ele;
		elementSpawnStyles = style;
	}
	public void setupAnchors( int num, Vec2 [] aPos)
	{
		anchorNum = num;
		anchorPos = aPos;
	}
	public void setSpeed( float v)
	{
		speed = v;
	}
	
	public float getSpeed( )
	{
		return speed;
	}
	public int getAnchorNum()
	{
		return anchorNum;
	}
	public Vec2 getAnchorPos(int index )
	{
		return anchorPos[index];
	}
	public int getSimNum()
	{
		return sim;
	}
	public int getSetNum()
	{
		return set;
	}
	public int getUnitNum()
	{
		return unit;
	}
	public SpawnStyle getSpawnStyle(String ele)
	{
		int index = getIndexOfElement(ele);
		return this.elementSpawnStyles[index];
	}
	private int getIndexOfElement(String ele)
	{
		for( int i = 0;i<elements.length;i++)
		{
			if( elements[i].equals(ele))
				return i;
		}
		return -1;
	}
	
	public float getDistanceBetweenMolecule()
	{
		return distanceBetweenMolecule;
	}
	public void setDistanceBetweenMolecule(float dist)
	{
		distanceBetweenMolecule = dist;
	}

}
