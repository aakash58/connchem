/**
 * 
 */
package view;

import static model.State.molecules;

import java.util.ArrayList;

import model.State;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.DistanceJoint;

/**
 * @author Qin Li
 * The Unit3 class provides all specific computations encountered in Unit 3 simulation, Chemical Reactions
 * Only be called by P5Canvas object
 */
public class Unit3 extends UnitBase{
	
	private float sodiumJointLength ;

	public Unit3(P5Canvas parent, PBox2D box) {
		super(parent, box);
		// TODO Auto-generated constructor stub
	}

	public boolean addMolecules(boolean isAppEnable, String compoundName,int count)
	{
		boolean res = false;
		//System.out.println("compoundName is "+compoundName);
		if(p5Canvas.getMain().selectedUnit==3)
		{
			if(p5Canvas.getMain().selectedSim==1)
			{
				switch(p5Canvas.getMain().selectedSet)
				{
				case 1: //2Na + Cl2 = 2NaCl
					if(compoundName.equals("Sodium"))
						res = this.addSolidMoleculeSodium(isAppEnable, compoundName, count);
					else if(compoundName.equals("Chlorine"))
						res = this.addSingleIon(isAppEnable, compoundName, count);
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
				case 8:
					break;
				case 9:
					break;
				case 10:
					break;
					default:
						break;
				}
			}
			else if(p5Canvas.getMain().selectedSim==2)
			{
				
			}
			else
			{
				
			}
	
		}

		return res;
	}
	
	/******************************************************************
	 * FUNCTION : addSolidMoleculeSodium
	 * DESCRIPTION : Function to add Sodium molecules to PApplet 
	 * The molecule alignment is different from that of general solid
	 * 
	 * INPUTS : isAppEnable(boolean), compoundName(String), count(int) OUTPUTS:
	 * None
	 *******************************************************************/
	public boolean addSolidMoleculeSodium(boolean isAppEnable, String compoundName,
			int count) {
		boolean res = true;
		
		//TODO: Add style parameter Cube or paved
		//Style depends how the solid molecules would be aligned
		boolean isCube = false;

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

		if( isCube)
		{
		//Create molecules align in cube pattern
		for (int i = 0; i < count; i++) {
			if( (i /dimension)%2 ==0 )  /* Odd line */
			{
				x_ = centerX + i % dimension * moleWidth*1.4f;
			}
			else /* even line */
			{
				x_ = centerX + 0.7f*moleWidth + i % dimension * moleWidth*1.4f;
			}
			
			y_ = centerY + i / dimension * moleHeight;
			res = molecules.add(new Molecule(x_, y_, compoundName, box2d,
					p5Canvas, (float) (Math.PI/2)));

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
					joint2Elements( m1, m2,jointLength*1.8f);
				}
				/* In vertical direction, all molecules create a joint connecting to its down next molecule */
				if( ((i/dimension+1)!=rowNum) && ((i+dimension)<count)) /* bottom most molecules */
				{
					index1 = i + startIndex;
					index2 = i+dimension + startIndex;
					m1= molecules.get(index1);
					m2 = molecules.get(index2);
					joint2Elements(m1,m2,jointLength*1.4f);
				}
				/* In diagonal direction, all molecules create a joint connecting to its bottom right molecule */
				if( ((i+1)%dimension !=0)&&((i+dimension+1)<count) && (i/dimension)%2!=0 )
				{
					index1 = i+startIndex;
					index2 = i+ dimension +1 + startIndex;
					m1 = molecules.get(index1);
					m2 = molecules.get(index2);
					joint2Elements(m1,m2,jointLength*1.4f);
				}
				/* In diagonal direction, all molecules create a joint connecting to its top right molecule */
				if( (i-dimension+1)>=0 && (i+1)%dimension !=0 && (i/dimension)%2!=0)
				{
					index1 = i+startIndex;
					index2 = i- dimension +1 + startIndex;
					m1 = molecules.get(index1);
					m2 = molecules.get(index2);
					joint2Elements(m1,m2,jointLength*1.4f);
				}
			}
		}
		}
		else //Create molecules align in a paved way
		{
			dimension =8;
			offsetX = p5Canvas.w / 2 - (dimension * moleWidth) / 2;
			centerX = p5Canvas.x +offsetX;
			for (int i = 0; i < count; i++) {
				if( (i /dimension)%2 ==0 )  /* Odd line */
				{
					x_ = centerX + i % dimension * moleWidth*1.4f;
				}
				else /* even line */
				{
					x_ = centerX + 0.7f*moleWidth + i % dimension * moleWidth*1.4f;
				}
				
				y_ = centerY + i / dimension * moleHeight;
				res = molecules.add(new Molecule(x_, y_, compoundName, box2d,
						p5Canvas, (float) (Math.PI/2)));

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
							joint2Elements( m1, m2,jointLength*1.6f);
						}
						/* In vertical direction, all molecules create a joint connecting to its down next molecule */
						if( ((i/dimension+1)!=rowNum) && ((i+dimension)<count)) /* bottom most molecules */
						{
							index1 = i + startIndex;
							index2 = i+dimension + startIndex;
							m1= molecules.get(index1);
							m2 = molecules.get(index2);
							joint2Elements(m1,m2,jointLength*1.4f);
						}
						/* In diagonal direction, all molecules create a joint connecting to its bottom right molecule */
						if( ((i+1)%dimension !=0)&&((i+dimension+1)<count) && (i/dimension)%2!=0 )
						{
							index1 = i+startIndex;
							index2 = i+ dimension +1 + startIndex;
							m1 = molecules.get(index1);
							m2 = molecules.get(index2);
							joint2Elements(m1,m2,jointLength*1.4f);
						}
						/* In diagonal direction, all molecules create a joint connecting to its top right molecule */
						if( (i-dimension+1)>=0 && (i+1)%dimension !=0 && (i/dimension)%2!=0)
						{
							index1 = i+startIndex;
							index2 = i- dimension +1 + startIndex;
							m1 = molecules.get(index1);
							m2 = molecules.get(index2);
							joint2Elements(m1,m2,jointLength*1.4f);
						}
					}
				
			}
		}

		return res;
	}
	
	/******************************************************************
	 * FUNCTION : beginReaction 
	 * DESCRIPTION : Reaction function happens after collision
	 * 
	 * INPUTS : c ( Contact)
	 * OUTPUTS: None
	 *******************************************************************/
	public void beginReaction(Contact c)
	{
		// Get our objects that reference these bodies
		Object o1 = c.m_fixtureA.m_body.getUserData();
		Object o2 = c.m_fixtureB.m_body.getUserData();
		
		if (o1 ==null || o2==null)
			return;
		// What class are they?  Box or Particle?
		String c1 = o1.getClass().getName();
		String c2 = o2.getClass().getName();
		
		if (c1.contains("Molecule") && c2.contains("Molecule")){
			Molecule m1 = (Molecule) o1;
			Molecule m2 = (Molecule) o2;
			ArrayList<String> reactants = new ArrayList<String>();
			reactants.add(m1.getName());
			reactants.add(m2.getName());
			if (true){ /*TODO: Maybe there are some conditions */
				
					p5Canvas.products = getReactionProducts(reactants);
					if (p5Canvas.products!=null && p5Canvas.products.size()>0){ 
						/* If there are some new stuff in newProducts, kill old ones and add new ones*/
						p5Canvas.killingList.add(m1);
						p5Canvas.killingList.add(m2);
						
					}
				
			}
		}
		
	}
	public void reactNaCl()
	{

		if (p5Canvas.products!=null && p5Canvas.products.size()>0){
			Molecule m1 = (Molecule) p5Canvas.killingList.get(0);
			Molecule m2 = (Molecule) p5Canvas.killingList.get(1);
			
			Molecule mNew = null;
			Molecule mNew2 = null;
			
			/* Actually there is only one reaction going in each frame */
			for (int i=0;i<p5Canvas.products.size();i++){
				Vec2 loc =m1.getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h*0.77f-PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec =new Vec2(x1,y1);
				mNew = new Molecule(newVec.x, newVec.y,p5Canvas.products.get(i), box2d, p5Canvas,(float) (Math.PI/2));
				molecules.add(mNew);
				mNew.body.setFixedRotation(true);
				if (i==0)
					mNew.body.setLinearVelocity(m1.body.getLinearVelocity());
				
				else{
					mNew.body.setLinearVelocity(m2.body.getLinearVelocity());
				}
			}
			
			//Get joint length
			float jointLength = sodiumJointLength;
			
			//Get joints to which this molecule is connecting to
			ArrayList<DistanceJointWrap> m1Joint = m1.destroy();
			ArrayList<DistanceJointWrap> m2Joint = m2.destroy();
			Molecule molecule1 = null;
			Molecule molecule2 = null;
			ArrayList<Molecule> neighborMolecules = new ArrayList<Molecule>();
			Molecule chlorine = null;
			
			Molecule jointTarget = null;
			float length =0;
			if( m1Joint == null )   //m2 is Sodium
			{
				chlorine = m1;
				for( int m =0;m<m2Joint.size();m++)
				{
					molecule1 = (Molecule) m2Joint.get(m).getBodyA().getUserData();
					molecule2 = (Molecule) m2Joint.get(m).getBodyB().getUserData();
					if( State.molecules.contains(molecule1) )
					{
						jointTarget = molecule1;
					}
					else if( State.molecules.contains(molecule2))
					{
						jointTarget = molecule2;
					}
					else
					{
						break;
					}
					//Save neighbor molecules to list
					if( jointTarget.getName().equals("Sodium"))
					neighborMolecules.add(jointTarget);
					//Create new joints between reaction created molecule and old molecules
					 length = PBox2D.scalarWorldToPixels(m2Joint.get(m).getLength());
					joint2Elements( mNew, jointTarget, length, this.SODIUM_JOINT_FREQUENCY ) ;
					
				}
			}
			else                  //m1 is Sodium  
			{
				chlorine = m2;
				for( int m =0;m<m1Joint.size();m++)
				{
					molecule1 = (Molecule) m1Joint.get(m).getBodyA().getUserData();
					molecule2 = (Molecule) m1Joint.get(m).getBodyB().getUserData();
					if( State.molecules.contains(molecule1))
					{
						jointTarget = molecule1;
					}
					else if( State.molecules.contains(molecule2))
					{
						jointTarget = molecule2;
					}
					else
					{
						break;
					}
					//Save neighbor molecules to list
					if( jointTarget.getName().equals("Sodium"))
					neighborMolecules.add(jointTarget);
					//Create new joints between reaction created molecule and old molecules
					length = PBox2D.scalarWorldToPixels(m1Joint.get(m).getLength());
					joint2Elements( mNew, jointTarget, length, this.SODIUM_JOINT_FREQUENCY ) ;
				}
				
			}
			
			//After we killed current sodium and created a sodium-Chloride at the same location
			//We need to pick another sodium in its neighbor which is the closest to reacted Chlorine
			if(!neighborMolecules.isEmpty())
			{
				
				Molecule secondSodium = null;
				
				//secondSodium = compareDistance(chlorine, neighborMolecules);
				secondSodium = pickRightBottomOne(mNew, neighborMolecules);
				
				
				//Create a new Sodium-Chloride
				Vec2 loc = secondSodium.getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h*0.77f-PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec =new Vec2(x1,y1);
				mNew2 = new Molecule(newVec.x, newVec.y,mNew.getName(), box2d, p5Canvas,(float) (Math.PI/2));
				molecules.add(mNew2);
				mNew2.body.setFixedRotation(true);
				mNew2.body.setLinearVelocity(mNew.body.getLinearVelocity());
				
				//Get joints to which this molecule is connecting to
				ArrayList<DistanceJointWrap> sodiumJoint = secondSodium.destroy();
				//Create new joints for new molecules
				for( int m =0;m<sodiumJoint.size();m++)
				{
					molecule1 = (Molecule) sodiumJoint.get(m).getBodyA().getUserData();
					molecule2 = (Molecule) sodiumJoint.get(m).getBodyB().getUserData();
					if( State.molecules.contains(molecule1))
						jointTarget = molecule1;
					else if( State.molecules.contains(molecule2))
						jointTarget = molecule2;
	
					//Create new joints between reaction created molecule and old molecules
					 length = PBox2D.scalarWorldToPixels(sodiumJoint.get(m).getLength());
					joint2Elements( mNew2, jointTarget, length, this.SODIUM_JOINT_FREQUENCY ) ;
					
				}
			}
			
			
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
		}
	}
	
	/******************************************************************
	* FUNCTION :     pickBottomOne
	* DESCRIPTION :  Pick a molecule which is under a reacted molecule
	*                Return reference of that picked molecule 
	*
	* INPUTS :       source(ArrayList<Molecule>)
	* OUTPUTS:       Molecule
	*******************************************************************/
	private Molecule pickRightBottomOne(Molecule source, ArrayList<Molecule> neighborMolecules) {
		// TODO Auto-generated method stub
		Molecule res = null;
		if( neighborMolecules.size()<1)
			return res;
		else if(neighborMolecules.size() ==1)
			return neighborMolecules.get(0);
		else
		{
			float highY = 0;
			int highIndex = -1;
			Vec2 pos = new Vec2();
			Vec2 posSource = new Vec2(this.box2d.coordWorldToPixels(source.getPosition()));
			float y = 0 ;
			//Go through each molecule in source list and check their position
			//Write down the index of molecule which has lowest y
			for(int i =0;i<neighborMolecules.size();i++)
			{
				pos = this.box2d.coordWorldToPixels(neighborMolecules.get(i).getPosition());
				//Check if this molecule is at right bottom of source molecule
				if(pos.x >= posSource.x && pos.y>= posSource.y)
				{
					//If it is, compare their y value
					y= pos.y;
				
					if(  y > highY )
					{
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
	* FUNCTION :     compareDistance
	* DESCRIPTION :  Compare distances between source molecules and targets
	*                Return reference of one source molecule which is the closest to target
	*
	* INPUTS :       target(Molecule), source(ArrayList<Molecule>)
	* OUTPUTS:       Molecule
	*******************************************************************/
	private Molecule compareDistance(Molecule target, ArrayList<Molecule> source)
	{
		Molecule res = null;
		if( source.size()<1)
			return res;
		else if(source.size() ==1)
			return source.get(0);
		else
		{
			float minDistance = 10000;
			int minIndex = 0;
			float dis = 0 ;
			//Go through each molecule in source list and calculate their distance from target
			//Write down the index of molecule which has minimum distance
			for(int i =0;i<source.size();i++)
			{
				dis = calculateDistance(target,source.get(i));
				if(  dis < minDistance )
				{
					minDistance = dis;
					minIndex = i;
				}
			}
			res = source.get(minIndex);
		}
		
		return res;
	}
	
	private float calculateDistance(Molecule target, Molecule source)
	{
		float distance = 0;
		Vec2 posTarget = target.getPosition();
		Vec2 posSource = source.getPosition();
		float xDifference = posTarget.x - posSource.x;
		float yDifference = posTarget.y - posSource.y;
		distance = (float) Math.sqrt(xDifference*xDifference + yDifference*yDifference);
		
		return distance;
	}
	
	/******************************************************************
	* FUNCTION :     getReactionProducts
	* DESCRIPTION :  Reture objects based on input name
	*                Called by beginContact
	*
	* INPUTS :       reactants (Array<String>)
	* OUTPUTS:       None
	*******************************************************************/
	private ArrayList<String> getReactionProducts(ArrayList<String> reactants) {	
		if (reactants.contains("Sodium") &&
			reactants.contains("Chlorine") && reactants.size()==2){
			ArrayList<String> products = new ArrayList<String>();
			products.add("Sodium-Chloride");
			return products;
		}
		else{
			return null;
		}
	}

	@Override
	public void setupParameters() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected SpawnStyle getSpawnStyle(int selectedSim, int selectedSet) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
