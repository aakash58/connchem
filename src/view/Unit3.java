/**
 * 
 */
package view;

import static model.State.molecules;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;

/**
 * @author Qin Li
 * The Unit3 class provides all specific computations encountered in Unit 3 simulation, Chemical Reactions
 * Only be called by P5Canvas object
 */
public class Unit3 extends UnitBase{

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
					p5Canvas, 0));

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
					joint2Elements(index1, index2, m1, m2,jointLength*1.4f);
				}
				/* In vertical direction, all molecules create a joint connecting to its down next molecule */
				if( ((i/dimension+1)!=rowNum) && ((i+dimension)<count)) /* bottom most molecules */
				{
					index1 = i + startIndex;
					index2 = i+dimension + startIndex;
					m1= molecules.get(index1);
					m2 = molecules.get(index2);
					joint2Elements(index1,index2,m1,m2,jointLength*1.2f);
				}
				/* In diagonal direction, all molecules create a joint connecting to its bottom right molecule */
				if( ((i+1)%dimension !=0)&&((i+dimension+1)<count) && (i/dimension)%2!=0 )
				{
					index1 = i+startIndex;
					index2 = i+ dimension +1 + startIndex;
					m1 = molecules.get(index1);
					m2 = molecules.get(index2);
					joint2Elements(index1,index2,m1,m2,jointLength*1.2f);
				}
				/* In diagonal direction, all molecules create a joint connecting to its top right molecule */
				if( (i-dimension+1)>=0 && (i+1)%dimension !=0 && (i/dimension)%2!=0)
				{
					index1 = i+startIndex;
					index2 = i- dimension +1 + startIndex;
					m1 = molecules.get(index1);
					m2 = molecules.get(index2);
					joint2Elements(index1,index2,m1,m2,jointLength*1.2f);
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
				
					ArrayList<String> newProduct = getReactionProducts(reactants);
					if (newProduct!=null && newProduct.size()>0){ 
						/* If there are some new stuff in newProducts, kill old ones and add new ones*/
						switch(p5Canvas.getMain().selectedSet)
						{
						case 1: /* 2Na + Cl2 -> 2NaCl */
							reactNaCl2(m1,m2);
							break;
						}
						
						
					}
				
			}
		}
		
	}
	private void reactNaCl2(Molecule m1, Molecule m2)
	{
		Molecule Chlorine = null;
		Molecule Sodium = null;
		if( m1.getName().equals("Chlorine"))   /* Identify Chlorine */
		{
			Chlorine =  m1;
			Sodium = m2;
		}
		else
		{
			Sodium = m1;
			Chlorine = m2;
		}
		
		/* Kill Chlorine */
		Chlorine.killBody();
		molecules.remove(Chlorine);
		
		/* Change Sodium to Sodium-Chloride */
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
	
}
