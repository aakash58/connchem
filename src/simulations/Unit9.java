/**
 * 
 */
package simulations;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.Main;
import net.miginfocom.swing.MigLayout;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;

import data.State;
import data.YAMLinterface;

import Util.Constants;
import Util.Integrator;
import Util.MathFunction;

import simulations.models.Anchor;
import simulations.models.DistanceJointWrap;
import simulations.models.Molecule;
import simulations.models.Molecule.mState;
import simulations.models.Simulation;
import simulations.models.Simulation.SpawnStyle;

/**
 * @author administrator
 * 
 */
public class Unit9 extends UnitBase {

	private JLabel lblProtonText;
	private JLabel lblProtonValue;
	private JLabel lblNeutronText;
	private JLabel lblNeutronValue;
	private JLabel lblElectronText;
	private JLabel lblElectronValue;
	private JLabel lblAtomicText;
	private JLabel lblAtomicValue;
	private JLabel lblMassText;
	private JLabel lblMassValue;
	
	private JLabel lblElement1Text;
	private JLabel lblElement1Value;
	private JLabel lblE1ProtonText;
	private JLabel lblE1ProtonValue;
	private JLabel lblE1NeutronText;
	private JLabel lblE1NeutronValue;
	private JLabel lblE1AtomicText;
	private JLabel lblE1AtomicValue;
	private JLabel lblE1MassText;
	private JLabel lblE1MassValue;
	
	private JLabel lblElement2Text;
	private JLabel lblElement2Value;
	private JLabel lblE2ProtonText;
	private JLabel lblE2ProtonValue;
	private JLabel lblE2NeutronText;
	private JLabel lblE2NeutronValue;
	private JLabel lblE2AtomicText;
	private JLabel lblE2AtomicValue;
	private JLabel lblE2MassText;
	private JLabel lblE2MassValue;
	
	
	

	// Dynamic panel elements
	private JSlider sliderStrongForce;
	private JLabel lblStrongForce;
	private JSlider sliderWeakForce;
	private JLabel lblWeakForce;
	private JButton btnShoot ;               //Shoot button used in Sim2
	private JLabel lblShoot;


	private Integrator interpolatorShow; // Interpolator to do show animation
	private Integrator interpolatorHide; // Interpolator to do fade animation

	private String protonName = new String("Proton");
	private String neutronName = new String("Neutron");

	private int currentSimulationID = 0;  //Which nuclear is selected in current set
	private final int SET_NUM = 5;
	private ArrayList<String>[] IDMap; // Map set and button to specific
										// simulation
	private NuclearInfo nuclearInfo; // Object telling how many proton and
										// neutron are in one atom
	private HashMap<String, Vec2[]> nuclearStructure; // Hashmap that map atom
														// structure to their
														// name
	private HashMap<JButton, String> buttonMap; // Hashmap that map button to
												// corresponding name
	private String [][] fissionProduction;
	private int fissionNum = 4;
	private int fissionElementNum = 5;
	private ArrayList<JButton> buttonRef;    //Parameter that saves button reference
	private boolean strongForceSwitch;
	private boolean weakForceSwitch;
	
	
	public static int stepCount = 0;  //Count p5Canvas step
	public int shakeIteration = 3;
	private boolean isHit = false;    //If nuclear is hit in Sim 2
	private boolean firstHit = false; //The first frame after the nuclear is hit
	
	private boolean simStarted = false; //If play button is clicked
	private boolean nuclearSelected = false; //If user has already click nuclear button
	
	//Joint parameters
	float frequency = 15;
	float damp = 0.4f;
	float jointLen = 0.25f;


	// Listeners
	ActionListener moleculeBtnListener;
	ChangeListener sliderStrongForceListener;
	ChangeListener sliderWeakForceListener;
	ActionListener shootBtnListener;

	public Unit9(P5Canvas parent, PBox2D box) {
		super(parent, box);

		unitNum = 9;
		interpolatorShow = new Integrator(0);
		interpolatorHide = new Integrator(0);
		IDMap = new ArrayList[5];
		nuclearInfo = new NuclearInfo();
		nuclearStructure = new HashMap<String, Vec2[]>();
		buttonMap = new HashMap<JButton, String>();
		buttonRef = new ArrayList<JButton>();

		// moleculeConHash = new HashMap<String, Float>();
		setupSimulations();
		setupListeners(); // Set up button listener for molecule buttons
		setupOutputLabels();
		setupNuclearStructure(); // Set up alignment of proton and neutron for
									// atoms

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simulations.UnitBase#setupSimulations()
	 */
	@Override
	protected void setupSimulations() {

		simulations = new Simulation[SIMULATION_NUMBER];

		simulations[0] = new Simulation(unitNum, 1, 1);
		String[] elements0 = { "Helium", "Carbon", "Aluminum", "Potassium" };
		SpawnStyle[] spawnStyles0 = { SpawnStyle.Gas, SpawnStyle.Gas,
				SpawnStyle.Gas, SpawnStyle.Gas };
		simulations[0].setupElements(elements0, spawnStyles0);

		simulations[1] = new Simulation(unitNum, 2, 1);
		String[] elements1 = { "Uranium-235", "Cesium-137" };
		SpawnStyle[] spawnStyles1 = { SpawnStyle.Gas, SpawnStyle.Gas };
		simulations[1].setupElements(elements1, spawnStyles1);

		simulations[2] = new Simulation(unitNum, 3, 1);
		String[] elements2 = { "H-1","H-2","He-3","He-4","Be-8" };
		SpawnStyle[] spawnStyles2 = { SpawnStyle.Gas,SpawnStyle.Gas,SpawnStyle.Gas,SpawnStyle.Gas,SpawnStyle.Gas };
		simulations[2].setupElements(elements2, spawnStyles2);

		simulations[3] = new Simulation(unitNum, 4, 1);
		String[] elements3 = { "Water" };
		SpawnStyle[] spawnStyles3 = { SpawnStyle.Gas };
		simulations[3].setupElements(elements3, spawnStyles3);

		simulations[4] = new Simulation(unitNum, 5, 1);
		String[] elements4 = { "Water" };
		SpawnStyle[] spawnStyles4 = { SpawnStyle.Gas };
		simulations[4].setupElements(elements4, spawnStyles4);

		simulations[5] = new Simulation(unitNum, 1, 2);
		String[] elements5 = { "Helium" };
		SpawnStyle[] spawnStyle5 = { SpawnStyle.Gas };
		simulations[5].setupElements(elements5, spawnStyle5);

		// Initialize IDMap
		IDMap[0] = new ArrayList<String>();
		IDMap[0].add("Helium");
		IDMap[0].add("Carbon");
		IDMap[0].add("Aluminum");
		IDMap[0].add("Potassium");
		IDMap[1] = new ArrayList<String>();
		IDMap[1].add("Uranium-235");
		IDMap[1].add("Cesium-137");
		IDMap[2] = new ArrayList<String>();
		IDMap[2].add("H-1");
		IDMap[2].add("H-2");
		IDMap[2].add("He-3");
		IDMap[2].add("He-4");
		IDMap[2].add("Be-8");
		IDMap[3] = new ArrayList<String>();
		IDMap[4] = new ArrayList<String>();
		
		setupFissionProduction();
	}

	// Set current Simulation ID by passing in name of nuclear
	public boolean setCurrentSimulationID(String str) {
		if (str != null || !str.isEmpty()) {
			int set = p5Canvas.getSet();
			if (IDMap[set - 1].contains(str)) {
				currentSimulationID = IDMap[set - 1].indexOf(str);
				return true;
			}
		}

		return false;
	}
	
	private void setupFissionProduction()
	{
		fissionProduction = new String [fissionNum][fissionElementNum];
		fissionProduction[0][0] = new String("Barium-144");
		fissionProduction[0][1] = new String("Krypton-89");
		
		fissionProduction[1][0] = new String("Rubidium-96");
		fissionProduction[1][1] = new String("Cesium-137");
		
		fissionProduction[2][0] = new String("Krypton-50");
		fissionProduction[2][1] = new String("Potassium-85");
		
		fissionProduction[3][0] = new String("Chlorine-45");
		fissionProduction[3][1] = new String("Strontium-90");
		
		for(int i =0;i<fissionNum;i++)
		{
			for(int j =2;j<fissionElementNum;j++)
				fissionProduction[i][j] = new String("Neutron");
		}

		
	}

	private void setupOutputLabels() {
		
		lblElement1Text = new JLabel("Element1:");
		lblElement1Value = new JLabel();
		lblElement2Text = new JLabel("Element2:");
		lblElement2Value = new JLabel();
		lblProtonText = new JLabel("Number of Proton:");
		lblE1ProtonText = new JLabel("Number of Proton:");
		lblE2ProtonText = new JLabel("Number of Proton:");

		lblProtonValue = new JLabel("");
		lblE1ProtonValue = new JLabel("");
		lblE2ProtonValue = new JLabel("");
		lblNeutronText = new JLabel("Number of Neutron:");
		lblE1NeutronText = new JLabel("Number of Neutron:");
		lblE2NeutronText = new JLabel("Number of Neutron:");

		lblNeutronValue = new JLabel("");
		lblE1NeutronValue = new JLabel("");
		lblE2NeutronValue = new JLabel("");

		lblElectronText = new JLabel("Electron:");
		lblElectronValue = new JLabel("");
		lblAtomicText = new JLabel("Atomic Number:");
		lblE1AtomicText = new JLabel("Atomic Number:");
		lblE2AtomicText = new JLabel("Atomic Number:");
		lblAtomicValue = new JLabel("");
		lblE1AtomicValue = new JLabel("");
		lblE2AtomicValue = new JLabel("");
		lblMassText = new JLabel("Mass Number:");
		lblE1MassText = new JLabel("Mass Number:");
		lblE2MassText = new JLabel("Mass Number:");
		lblMassValue = new JLabel("");
		lblE1MassValue = new JLabel("");
		lblE2MassValue = new JLabel("");


		sliderStrongForce = new JSlider(0, 1, 1);
		sliderStrongForce.setName("Strong Force");
		sliderWeakForce = new JSlider(0, 1, 1);
		sliderWeakForce.setName("Weak Force");
		lblStrongForce = new JLabel("Strong Force");
		lblWeakForce = new JLabel("Weak Force");
		sliderStrongForce.setOrientation(SwingConstants.HORIZONTAL);
		sliderWeakForce.setOrientation(SwingConstants.HORIZONTAL);
		sliderStrongForce.setSnapToTicks(true);
		sliderStrongForce.setPaintTicks(true);
		sliderStrongForce.setMinorTickSpacing(1);
		sliderStrongForce.addChangeListener(sliderStrongForceListener);
		sliderWeakForce.setSnapToTicks(true);
		sliderWeakForce.setPaintTicks(true);
		sliderWeakForce.setMinorTickSpacing(1);
		sliderWeakForce.addChangeListener(sliderWeakForceListener);
		btnShoot = new JButton();
		lblShoot = new JLabel("Shoot");
		btnShoot.addActionListener(shootBtnListener);

	}

	public void setupListeners() {
		moleculeBtnListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JButton buttonPressed = (JButton) e.getSource();

				// if (!p5Canvas.isSimStarted) // Before simulation started user
				// can change
				// selections, but after simulation started they
				// cant
				{
					String compoundName = buttonMap.get(buttonPressed);
					currentSimulationID = IDMap[p5Canvas.getSim() - 1]
							.indexOf(compoundName);

					// Clear existing molecules on canvas and draw new one
					p5Canvas.removeAllMolecules();

					addMolecules(p5Canvas.isEnable, compoundName, 1);

					initializeSimulation(p5Canvas.getSim(), p5Canvas.getSet());
					
					nuclearSelected = true;

					if( simStarted)
					{
						Unit9.this.sliderStrongForce.setEnabled(true);
						Unit9.this.sliderWeakForce.setEnabled(true);
						Unit9.this.btnShoot.setEnabled(true);
					}
				}

			}
		};

		sliderStrongForceListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				boolean oriStrongForceSwitch = strongForceSwitch;
				int value = ((JSlider) e.getSource()).getValue();
					strongForceSwitch = value == 0 ? false : true;	
					if(strongForceSwitch == false){
						//Disable the weak force slider
						Unit9.this.sliderWeakForce.setEnabled(false);}
					else{
						if(oriStrongForceSwitch==false)
						{
							Main main = Unit9.this.p5Canvas.getMain();
							main.reset();
							((JButton)Unit9.this.buttonRef.get(0)).doClick();
						}
					}
			}

		};
		
		sliderWeakForceListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = ((JSlider) e.getSource()).getValue();
					weakForceSwitch = value == 0 ? false : true;
			}

		};
		
		shootBtnListener = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {

				isHit = true;
				firstHit = true;
				Unit9.this.btnShoot.setEnabled(false);
				
			}
		};
		
	}

	// Function that set up the nuclear structure, that is, what the alignment
	// of proton and neutron
	private void setupNuclearStructure() {
		float x = 0;
		float y = 0;
		int column = 0;
		int row = 0;
		float padding = 0.2f;
		
		// Helium - 4
		int massNum = 4;
		Vec2[] heliumQuarkStructure = new Vec2[massNum];
		Vec2 protonSize = Molecule.getShapeSize("Proton-Quark", p5Canvas);
		float width = protonSize.x * (1+padding);
		float height = protonSize.y * (1+padding);
		for (int i = 0; i < massNum; i++) {
			x = width / 2 * (i / 2 == 0 ? -1 : 1);
			y = height * ((i % 2) + -0.5f * (i / 2));
			heliumQuarkStructure[i] = new Vec2(x, y);
		}
		nuclearStructure.put("Helium-Quark", heliumQuarkStructure);
		
		Vec2[] heliumStructure = new Vec2[massNum];
		protonSize = Molecule.getShapeSize("Proton", p5Canvas);
		width = protonSize.x * (1+padding);
		height = protonSize.y * (1+padding);
		for (int i = 0; i < massNum; i++) {
			x = width / 2 * (i / 2 == 0 ? -1 : 1);
			y = height * ((i % 2) + -0.5f * (i / 2));
			heliumStructure[i] = new Vec2(x, y);
		}
		nuclearStructure.put("Helium", heliumStructure);
		
		// Carbon - 14
		massNum = 14;
		Vec2[] carbonStructure = new Vec2[massNum];
		for (int i = 0; i < massNum; i++) {
			if (i < 2) {
				column = -2;
				row = i - 0;
			} else if (i < 5) {
				column = -1;
				row = i - 3;
			} else if (i < 9) {
				column = 0;
				row = i - 6;
			} else if (i < 12) {
				column = 1;
				row = i - 10;
			} else {
				column = 2;
				row = i - 12;
			}
			x = width * column;
			y = height * (row + (column % 2 == 0 ? -0.5f : 0.0f));
			carbonStructure[i] = new Vec2(x, y);
		}
		nuclearStructure.put("Carbon", carbonStructure);

		massNum = 26;
		Vec2[] aluminumStructure = new Vec2[massNum];
		for (int i = 0; i < massNum; i++) {
			if (i < 5) {
				column = -2;
				row = i - 2;
			} else if (i < 10) {
				column = -1;
				row = i - 7;
			} else if (i < 16) {
				column = 0;
				row = i - 12;
			} else if (i < 21) {
				column = 1;
				row = i - 18;
			} else // 21 - 25
			{
				column = 2;
				row = i - 23;
			}
			x = width * column;
			y = height
					* (row + (column % 2 == 0 ? -0.25f * column : 0.0f));
			if (column == 0)
				y -= height * 0.25f;
			aluminumStructure[i] = new Vec2(x, y);
		}
		nuclearStructure.put("Aluminum", aluminumStructure);

		massNum = 40;
		Vec2[] potassiumStructure = new Vec2[massNum];
		for (int i = 0; i < massNum; i++) {

			if (i < 2) {
				row = i;
				column = -4;
			} else if (i < 7) {
				row = i - 4;
				column = -3;
			} else if (i < 13) {
				row = i - 9;
				column = -2;
			} else if (i < 18) {
				row = i - 15;
				column = -1;
			} else if (i < 24) {
				row = i - 20;
				column = 0;
			} else if (i < 29) {
				row = i - 26;
				column = 1;
			} else if (i < 35) {
				row = i - 31;
				column = 2;
			} else {
				row = i - 37;
				column = 3;
			}
			x = width * column;
			y = height * (row + (column % 2 == 0 ? -0.5f : 0.0f));
			potassiumStructure[i] = new Vec2(x, y);

		}
		nuclearStructure.put("Potassium", potassiumStructure);
		
		Vec2 [] uranium = new Vec2[0];
		nuclearStructure.put("Uranium-235", uranium);
		
		Vec2 [] cesium = new Vec2[0];
		nuclearStructure.put("Cesium-137", cesium);
		
		massNum = 1;
		Vec2 [] h1 = new Vec2[massNum];
		h1[0] = new Vec2(0,0);
		nuclearStructure.put("H-1", h1);
		massNum = 2;
		Vec2[] h2 = new Vec2[massNum];
		h2[0] = new Vec2(width*0.5f,height*0.5f);
		h2[1] = new Vec2(width*-0.5f,height*-0.5f);
		nuclearStructure.put("H-2", h2);
		
		massNum = 3;
		Vec2[] he3 = new Vec2[massNum];
		he3[0] = new Vec2(0,height*-1);
		he3[1] = new Vec2(0,height);
		he3[2] = new Vec2(width,0);
		nuclearStructure.put("He-3", he3);
		
		massNum = 4;
		Vec2[] he4 = new Vec2[massNum];
		he4[0] =  new Vec2(0,0);
		he4[1] = new Vec2(width,height*-0.5f);
		he4[2] = new Vec2(0,height);
		he4[3] = new Vec2(width,height*0.5f);
		nuclearStructure.put("He-4", he4);
		
		massNum = 8 ;
		Vec2[] be8 = new Vec2[massNum];
		for(int i = 0 ;i<massNum;i++)
		{
			if(i<2)
			{
				column = -1;
				row = i-0;
			}
			else if(i<5)
			{
				column = 0 ;
				row = i-3;
			}
			else if(i<8)
			{
				column = 1;
				row = i - 6;
			}
		}
		nuclearStructure.put("Be-8",be8);
		
	}
	
	public void play()
	{
		simStarted = true;
		if(nuclearSelected)
		{
			this.sliderStrongForce.setEnabled(true);
			this.sliderWeakForce.setEnabled(true);
		btnShoot.setEnabled(true);
		}
	}

	// Customize Interface in Main reset after all interface have been
	// initialized
	public void customizeInterface(int sim, int set) {
		Main main = p5Canvas.getMain();
		main.heatSlider.setEnabled(false);
		main.volumeSlider.setEnabled(false);
		main.zoomSlider.setEnabled(false);

		switch (sim) {
		case 1:
			resetDynamicPanel(sim, set);
			resetRightPanel(sim, set);
			break;
		case 2:
			resetDynamicPanel(sim, set);
			resetRightPanel(sim,set);
			break;
		case 3:
			resetDynamicPanel(sim,set);
			resetRightPanel(sim,set);
			break;
		case 4:
			break;
		case 5:
			break;
		}
	}

	public void resetDynamicPanel(int sim, int set) {

		Main main = p5Canvas.getMain();
		// Reset dynamic panel
		JPanel dynamicPanel = main.dynamicPanel;
		if (dynamicPanel != null)
			dynamicPanel.removeAll();
		// Get Compounds information in set 1 from Yaml file
		ArrayList compounds = YAMLinterface.getSetCompounds(unitNum, sim, set);

		// Add multiButtonPanel
		if (compounds != null) {
			int rowNum = 2;
			int colNum = 2;
			// Panel containing multi-buttons
			JPanel multiButtonPanel = new JPanel();
			multiButtonPanel.setName("multiButtonPanel");
			multiButtonPanel.setLayout(new MigLayout("insets 8,gap 0",
					"[]15[]", "[][]5[][]"));
			// Panel rendering instruction
			JLabel instructionPanel = new JLabel();
			// instructionPanel
			// .setText("<html>Select two substances below"
			// + "<p> and then press play button</html>");
			dynamicPanel.setLayout(new MigLayout("insets 0,gap 0", "[grow]",
					"[][][]"));
			main.dynamicScrollPane
					.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			dynamicPanel.add(instructionPanel, "cell 0 0, align center");
			dynamicPanel.add(multiButtonPanel, "cell 0 1, align center");
			for (int i = 0; i < compounds.size(); i++) {

				// Get Compound Name
				String cName = YAMLinterface.getCompoundName(unitNum, sim, set,
						i);

				JLabel lblMolecule = new JLabel(cName);
				lblMolecule.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
				multiButtonPanel.add(lblMolecule, "cell " + i % colNum + " "
						+ ((i / colNum) * 2 + 1) + ", align center"); // cell
				// column
				// row
				final String fixedName = cName.replace(" ", "-");
				// Draw Molecule button
				JButton btnMolecule = new JButton();
				multiButtonPanel
						.add(btnMolecule,
								"cell "
										+ i
										% colNum
										+ " "
										+ ((i / colNum) * 2)
										+ ", align center, width 100:100:120, height 50: 55: 80");
				btnMolecule.setIcon(new ImageIcon(Main.class
						.getResource("/resources/compoundsPng50/" + fixedName
								+ ".png")));
				btnMolecule.addActionListener(moleculeBtnListener);
				buttonMap.put(btnMolecule, fixedName);
				buttonRef.add(btnMolecule);
			}
		}

		// Add more panel
		if (sim == 1 && set == 2) {
			// Add slider panel
			JPanel panelSliders = new JPanel();
			panelSliders.setLayout(new MigLayout("insets 0, gap 0", "[grow]",
					"[][]5[][]5"));
			main.dynamicPanel.add(panelSliders, "cell 0 2, align center");

			panelSliders.add(sliderStrongForce, "cell 0 0, align center");
			panelSliders.add(lblStrongForce, "cell 0 1, align center");
			panelSliders.add(sliderWeakForce, "cell 0 2, align center");
			panelSliders.add(lblWeakForce, "cell 0 3, align center");

		}
		else if(sim==2 && set==1)
		{
			JPanel panelSub = new JPanel();
			panelSub.setLayout(new MigLayout("insets 0, gap 0", "[grow]",
					"[]5[]"));
			main.dynamicPanel.add(panelSub, "cell 0 2, align center");
			
			panelSub.add(btnShoot,"cell 0 0 , align center");
			panelSub.add(lblShoot,"cell 0 1 , align center");


		}

	}

	private void resetRightPanel(int sim, int set) {
		Main main = p5Canvas.getMain();
		main.rightPanel.removeAll();

		switch(sim)
		{
		case 1:
				main.rightPanel.add(main.lblOutput, "cell 0 1");
				main.rightPanel.add(main.dashboard, "cell 0 2,growy");
			break;
		case 2:
			main.rightPanel.add(main.lblOutput, "cell 0 1");
			main.rightPanel.add(main.dashboard, "cell 0 2,growy");
			break;
		case 3:
			break;
		case 4:
			break;
		case 5:
			break;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simulations.UnitBase#setupReactionProducts(int, int)
	 */
	@Override
	public void setupReactionProducts(int sim, int set) {
		// There is no reation in nuclear simulations

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simulations.UnitBase#updateMolecules(int, int)
	 */
	@Override
	public void updateMolecules(int sim, int set) {
		boolean reactionHappened = false;
		Simulation simulation = getSimulation(sim, set);

		switch (sim) {
		case 1:
			if(set==1)
			shakeMolecules(sim,set,0.15);
			else if(set==2)
				shakeMoleculesWithQuark(sim,set);
			break;
		case 2:
			if(set==1)
				updateMoleculesSim2Set1(sim,set);
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
		
		stepCount++;

	}
	
	//Simulate molecule vibration
	private void shakeMolecules(int sim, int set, double impulseValueMax)
	{
		if(stepCount%shakeIteration !=0)
			return;
		Random impulseValueGenerator = new Random();
		Random impulseDirGenerator = new Random();
		double impulseDirXMax = 2;
		double impulseDirYMax = 2;
		double increment = 0.2;
		
		double impulseValue = impulseValueGenerator.nextDouble()*impulseValueMax;
		double impulseDirX = impulseDirXMax* impulseDirGenerator.nextDouble()-1;
		double impulseDirY = impulseDirYMax* impulseDirGenerator.nextDouble()-1;
		

		//Vec2 impulseDir = new Vec2((float)impulseDirX, (float)impulseDirY);
		Vec2 impulse = new Vec2();
		Vec2 jointVec = new Vec2();
		Vec2 velocity = new Vec2();
		DistanceJointWrap dj ;
		
		for(int i = 0; i<State.getMoleculeNum();i++)
		{
			Molecule mole = State.getMoleculeByIndex(i);
			//Set up increment to make impulse different
			impulseDirX = (impulseDirX+impulseDirXMax*i*increment)%impulseDirXMax;
			impulseDirY = (impulseDirY+impulseDirYMax*i*increment)%impulseDirYMax;
			impulseValue = (impulseValue+ impulseValueMax*i*increment) %impulseValueMax;
			impulse.set((float)(impulseDirX* impulseValue),(float)(impulseDirY*impulseValue));
			
			//Check current velocity, to see if molecule is rotating around anchor
			if(mole.compoundJoint.size()>0)
			{
				dj= mole.compoundJoint.get(0);
				//System.out.println("Joint Length:"+ MathFunction.computeDistance(dj.getBodyA().getPosition(),dj.getBodyB().getPosition()));
				jointVec.set(dj.getBodyB().getPosition().sub( dj.getBodyA().getPosition()) );
				velocity = mole.getLinearVelocity();
				float dot = Vec2.dot(velocity, jointVec);
				float velocityNorm = MathFunction.norm(velocity);
				if(velocityNorm>6)   //Velocity is perpendicular to the joint
				{	
					impulse.set(MathFunction.normalizeForce(velocity).mulLocal((float)(-1*impulseValue)));          //Flip it
				}
				mole.applyLinearImpulse(impulse, mole.getPosition());
			}
		}
			
	}
	
	//Molecule vibration simulation for Sim 1 Set 2
	private void shakeMoleculesWithQuark(int sim, int set)
	{
		Random impulseValueGenerator;
		double impulseValueMax = 0.15;
		Random impulseDirGenerator;
		double impulseDirXMax = 2;
		double impulseDirYMax = 2;
		double increment = 0.4;
		
		double impulseValue;
		double impulseDirX;
		double impulseDirY;
		Vec2 impulse = new Vec2();
		float x_ = p5Canvas.w / 2; // X Coordinate of center
		float y_ = p5Canvas.h / 2;
		
		//Set up randomly generated impulse
		impulseValueGenerator = new Random();
		impulseDirGenerator = new Random();
		
		impulseValue = impulseValueGenerator.nextDouble()*impulseValueMax;
		impulseDirX = impulseDirXMax* impulseDirGenerator.nextDouble()-1;
		impulseDirY = impulseDirYMax* impulseDirGenerator.nextDouble()-1;
		impulse.set((float)(impulseDirX* impulseValue),(float)(impulseDirY*impulseValue));
		
		if(strongForceSwitch==true)   //If strong force on
		{ 
			if(weakForceSwitch == true ) //If weak force on
			{
				if(stepCount%shakeIteration !=0)
					return;
				
		
				//Vec2 impulseDir = new Vec2((float)impulseDirX, (float)impulseDirY);
				
				Vec2 jointVec = new Vec2();
				Vec2 velocity = new Vec2();
				DistanceJointWrap dj ;
				
				//Original position of atoms
				Vec2 originalPositions[] = nuclearStructure.get("Helium-Quark");
				Vec2 forceDir = new Vec2(); //The direction from current position to original position
				Vec2 molePosition = new Vec2(); //Current position of mole
				float disMin = 150f;
				Vec2 oriPos = new Vec2(); //original position of a specific atom
				
				for(int i = 0; i<State.getMoleculeNum();i++)
				{
					Molecule mole = State.getMoleculeByIndex(i);
					molePosition = mole.getPosition();
					//Set up increment to make impulse different
					impulseDirX = (impulseDirX+impulseDirXMax*i*increment)%impulseDirXMax;
					impulseDirY = (impulseDirY+impulseDirYMax*i*increment)%impulseDirYMax;
					impulseValue = (impulseValue+ impulseValueMax*i*increment) %impulseValueMax;
					impulse.set((float)(impulseDirX* impulseValue),(float)(impulseDirY*impulseValue));
					
					//Check current velocity, to see if molecule is rotating around anchor
					if(mole.compoundJoint.size()>0)
					{
						dj= mole.compoundJoint.get(0);
						//System.out.println("Joint Length:"+ MathFunction.computeDistance(dj.getBodyA().getPosition(),dj.getBodyB().getPosition()));
						jointVec.set(dj.getBodyB().getPosition().sub( dj.getBodyA().getPosition()) );
						velocity = mole.getLinearVelocity();
						float velocityNorm = MathFunction.norm(velocity);
						if(velocityNorm>12)   //Velocity is perpendicular to the joint
						{	
							impulse.set(MathFunction.normalizeForce(velocity).mulLocal((float)(-1*impulseValue)));          //Flip it
						}
						mole.applyLinearImpulse(impulse, molePosition);
					}
					else //If atoms are not bounded on joints, check how close they are and build bound
					{
						//Check the distance
						oriPos.set(x_+originalPositions[i].x, y_+originalPositions[i].y);
						float dis = MathFunction.computeDistance(mole.getPositionInPixel(), oriPos);
						if(dis < disMin	) //build joints between atom and original position
						{
							
							Anchor anchor = new Anchor(oriPos.x, oriPos.y, box2d,
									p5Canvas);
							State.anchors.add(anchor);
							joint2Elements(mole, anchor, jointLen, frequency, damp);
						}
						
					}
				}
			}
			else //If weak force is off, atoms bounce around
			{
				
				for(int i = 0 ;i<State.getMoleculeNum();i++)
				{
					Molecule mole = State.getMoleculeByIndex(i);
					
					if(mole.compoundJoint.size()>0)
					{
						//1. disable joints
						mole.destroyAllJoints();
						//2. Add one time impulse to move them
						//Set up increment to make impulse different
						impulseDirX = (impulseDirX+impulseDirXMax*i*increment)%impulseDirXMax;
						impulseDirY = (impulseDirY+impulseDirYMax*i*increment)%impulseDirYMax;
						impulseValue = (impulseValue+ impulseValueMax*i*increment) %impulseValueMax;
						impulse.set((float)(impulseDirX* impulseValue),(float)(impulseDirY*impulseValue));
						mole.applyLinearImpulse(impulse, mole.getPosition());
					}
				}
			}
		}
		else //If strong force is off, circles disappear and quarks bounce around
		{
			Vec2 molePos = new Vec2();
			Molecule newMole;
			String moleName ;
			Molecule mole ;
			int moleNum = State.getMoleculeNum();
			//replace current atom with one atom and three quarks
			for(int i =0;i< moleNum;i++)
			{
				mole = State.getMoleculeByIndex(i);
				moleName = mole.getName();
				if(moleName.startsWith("Proton") || moleName.startsWith("Neutron"))
				{
					if(mole.getImageName().contains("Quark") && !mole.getImageName().contains("NoQuark"))
					{
						//Disjoint the joints
						mole.destroyAllJoints();
						//Set up increment to make impulse different
						impulseDirX = (impulseDirX+impulseDirXMax*i*increment)%impulseDirXMax;
						impulseDirY = (impulseDirY+impulseDirYMax*i*increment)%impulseDirYMax;
						impulseValue = (impulseValue+ impulseValueMax*i*increment) %impulseValueMax;
						impulse.set((float)(impulseDirX* impulseValue),(float)(impulseDirY*impulseValue));
						mole.applyLinearImpulse(impulse, mole.getPosition());
						//Change looks
						mole.setImage(moleName.replaceAll("Quark", "NoQuark"));
						
						//Set the molecule not collide with wall any more
						mole.setFixtureCatergory(Constants.MOLE_NOTBOUND_ID, Constants.MOLE_NOTBOUND_ID);
						
						//Add quarks
						if(moleName.startsWith("Proton"))
							createProtonQuark(mole.getPositionInPixel(),impulse);
						else if(moleName.startsWith("Neutron"))
							createNeutronQuark(mole.getPositionInPixel(),impulse);
					}
				
			}
			}

			
			//Disappear atoms gradually
			
		}
			
	}
	
	private void updateMoleculesSim2Set1(int sim,int set)
	{
		if(!isHit)
		{
			this.shakeMolecules(sim, set,6);
		}
		else
		{
			if(firstHit)
			{
				//Destroy original nuclear
				String reactant = IDMap[sim-1].get(currentSimulationID);
				Molecule mole = State.getMoleculeByName(reactant);
				Vec2 posInPixel = mole.getPositionInPixel();
				mole.destroy();
				
				//Create two new nuclears
				float x_ = 50;
				Random rand = new Random();
				int fissionIndex = (sim-1)*2 + rand.nextInt(1);
				String compoundName; 
				Molecule newMole;
				for(int i = 0 ;i<fissionElementNum;i++)
				//Add impulse to them
				{
					compoundName = fissionProduction[fissionIndex][i];
					newMole = new Molecule(posInPixel.x, posInPixel.y,
							compoundName, box2d,
							p5Canvas, 0);

					State.molecules.add(newMole);
					//Set the state to gas so there is no gravitivity
					newMole.setEnableAutoStateChange(false);
					newMole.setState(mState.Gas);
					//Set use png file when draw molecule
					newMole.setImage(compoundName);
				}
				//Set up output
				String element1 = fissionProduction[fissionIndex][0];
				int protonNum = nuclearInfo.getProtonNumByName(element1);
				int neutronNum = nuclearInfo.getNeutronNumByName(element1);
				int massNum = nuclearInfo.getMassNumByName(element1);
				int atomicNum = nuclearInfo.getAtomicNumByName(element1);
	
				lblElement1Value.setText(element1);
				lblE1ProtonValue.setText(Integer.toString(protonNum));
				lblE1NeutronValue.setText(Integer.toString(neutronNum));
				lblE1MassValue.setText(Integer.toString(massNum));
				lblE1AtomicValue.setText(Integer.toString(atomicNum));
				
				String element2 = fissionProduction[fissionIndex][1];
				 protonNum = nuclearInfo.getProtonNumByName(element2);
				 neutronNum = nuclearInfo.getNeutronNumByName(element2);
				 massNum = nuclearInfo.getMassNumByName(element2);
				 atomicNum = nuclearInfo.getAtomicNumByName(element2);
				lblElement2Value.setText(element2);
				lblE2ProtonValue.setText(Integer.toString(protonNum));
				lblE2NeutronValue.setText(Integer.toString(neutronNum));
				lblE2MassValue.setText(Integer.toString(massNum));
				lblE2AtomicValue.setText(Integer.toString(atomicNum));
				
				//Set firstHit as false
				firstHit = false;
			}
		}
		
	}
	
	private void createProtonQuark(Vec2 pos, Vec2 impulse)
	{
		int num = 3;
		String compoundName ;
		float x_ = 20;   //Horizontal distance from center
		float y_ = 20;   //Vertical distance from center
		float xDis;
		float yDis;
		for(int i = 0;i<num;i++)
		{
			compoundName = i/2==0?"Quark-Positive":"Quark-Negative";
			if(i/2==0)
			{
				xDis = (i%2==0?-1:1)*x_;
				
			}
			else 
			{
				xDis = 0;
			}
			Molecule quark = new Molecule(pos.x+xDis, pos.y+(i/2==0?-1:1) * y_,
					compoundName, box2d,
					p5Canvas, 0);
	
			// newMole.setGravityScale(0f);			
			
			State.molecules.add(quark);
			//Set the state to gas so there is no gravitivity
			quark.setEnableAutoStateChange(false);
			quark.setState(mState.Gas);
			//Set use png file when draw molecule
			quark.setImage(compoundName);
			quark.setFixtureCatergory(Constants.NOTMOLE_BOUND_ID, Constants.BOUNDARY_ID+Constants.NOTMOLE_BOUND_ID);
			quark.applyLinearImpulse(impulse, quark.getPosition());

		}
	}
	
	private void createNeutronQuark(Vec2 pos, Vec2 impulse)
	{
		int num = 3;
		String compoundName ;
		float x_ = 20;   //Horizontal distance from center
		float y_ = 20;   //Vertical distance from center
		float xDis;
		float yDis;
		for(int i = 0;i<num;i++)
		{
			compoundName = i/2==1?"Quark-Positive":"Quark-Negative";
			if(i/2==0)
			{
				xDis = (i%2==0?-1:1)*x_;
				
			}
			else 
			{
				xDis = 0;
			}
			Molecule quark = new Molecule(pos.x+xDis, pos.y+(i/2==1?-1:1) * y_,
					compoundName, box2d,
					p5Canvas, 0);
	
			// newMole.setGravityScale(0f);			
			
			State.molecules.add(quark);
			//Set the state to gas so there is no gravitivity
			quark.setEnableAutoStateChange(false);
			quark.setState(mState.Gas);
			//Set use png file when draw molecule
			quark.setImage(compoundName);
			quark.setFixtureCatergory(Constants.NOTMOLE_BOUND_ID, Constants.BOUNDARY_ID+Constants.NOTMOLE_BOUND_ID);
			quark.applyLinearImpulse(impulse, quark.getPosition());

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simulations.UnitBase#initialize()
	 */
	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simulations.UnitBase#reset()
	 */
	@Override
	protected void reset() {

		// Reset parameters
		interpolatorHide.setTargeting(false);
		interpolatorHide.setAttraction(0.15f);
		interpolatorHide.setDamping(0.2f);
		interpolatorShow.setTargeting(false);
		interpolatorShow.setAttraction(0.15f);
		interpolatorShow.setDamping(0.2f);
		currentSimulationID = -1;
		isHit = false;
		firstHit =false;
		buttonMap.clear();
		buttonRef.clear();

		p5Canvas.isBoundaryShow = false;
		p5Canvas.setIfConstrainKE(false);

		p5Canvas.getMain().boxMoleculeHiding.setEnabled(false);
		p5Canvas.getMain().boxDisplayForce.setEnabled(false);
		
		 simStarted = false; //If play button is clicked
		nuclearSelected = false; //If user has already click nuclear button

		// Set up speed
		setupSpeed();

		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		protonName = new String("Proton");
		neutronName = new String("Neutron");

		switch (sim) {
		case 1:
			p5Canvas.getMain().getCanvas().setEnabled(false);
			if(set==2)
			{
				protonName = new String("Proton-Quark");
				neutronName = new String("Neutron-Quark");

				sliderStrongForce.setEnabled(false);
				sliderStrongForce.setValue(1);
				sliderWeakForce.setEnabled(false);
				sliderWeakForce.setValue(1);
				
				strongForceSwitch = true;
				weakForceSwitch = true;
			}
			break;
		case 2:
			btnShoot.setEnabled(false);
			break;
		case 3:
			break;
		case 4:
			break;
		case 5:
			break;
		}

	}

	private void setupSpeed() {
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		float speed = 1.0f;

		switch (sim) {
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
		}
		getSimulation(sim, set).setSpeed(speed);
	}

	public void resetDashboard(int sim, int set) {
		super.resetDashboard(sim, set);
		Main main = p5Canvas.getMain();
		Simulation simulation = getSimulation(sim, set);
		JPanel dashboard = main.dashboard;

		switch (sim) {
		case 1:
			dashboard.add(lblMassText, "cell 0 1, align right");
			dashboard.add(lblMassValue, "cell 1 1, align left");
			dashboard.add(lblNeutronText, "cell 0 2, align right");
			dashboard.add(lblNeutronValue, "cell 1 2,align left");
			dashboard.add(lblProtonText, "cell 0 3, align right");
			dashboard.add(lblProtonValue, "cell 1 3, align left");
			dashboard.add(lblAtomicText, "cell 0 4, align right");
			dashboard.add(lblAtomicValue, "cell 1 4, align left");
			break;
		case 2:
			dashboard.setLayout(new MigLayout("", "[grow,right][100]",
					"[]20[][][][]20[][][][][]20[][][][][]"));
			dashboard.add(p5Canvas.getMain().lblElapsedTimeText, "flowx,cell 0 0,alignx right");
			dashboard.add(p5Canvas.getMain().elapsedTime, "cell 1 0");
			dashboard.add(lblMassText, "cell 0 1, align right");
			dashboard.add(lblMassValue, "cell 1 1, align left");
			dashboard.add(lblNeutronText, "cell 0 2, align right");
			dashboard.add(lblNeutronValue, "cell 1 2,align left");
			dashboard.add(lblProtonText, "cell 0 3, align right");
			dashboard.add(lblProtonValue, "cell 1 3, align left");
			dashboard.add(lblAtomicText, "cell 0 4, align right");
			dashboard.add(lblAtomicValue, "cell 1 4, align left");
			
			dashboard.add(lblElement1Text,"cell 0 5, align right");
			dashboard.add(lblElement1Value,"cell 1 5, align left");

			dashboard.add(lblE1MassText, "cell 0 6, align right");
			dashboard.add(lblE1MassValue, "cell 1 6, align left");
			dashboard.add(lblE1NeutronText, "cell 0 7, align right");
			dashboard.add(lblE1NeutronValue, "cell 1 7,align left");
			dashboard.add(lblE1ProtonText, "cell 0 8, align right");
			dashboard.add(lblE1ProtonValue, "cell 1 8, align left");
			dashboard.add(lblE1AtomicText, "cell 0 9, align right");
			dashboard.add(lblE1AtomicValue, "cell 1 9, align left");
			
			dashboard.add(lblElement2Text,"cell 0 10, align right");
			dashboard.add(lblElement2Value,"cell 1 10, align left");
			dashboard.add(lblE2MassText, "cell 0 11, align right");
			dashboard.add(lblE2MassValue, "cell 1 11, align left");
			dashboard.add(lblE2NeutronText, "cell 0 12, align right");
			dashboard.add(lblE2NeutronValue, "cell 1 12,align left");
			dashboard.add(lblE2ProtonText, "cell 0 13, align right");
			dashboard.add(lblE2ProtonValue, "cell 1 13, align left");
			dashboard.add(lblE2AtomicText, "cell 0 14, align right");
			dashboard.add(lblE2AtomicValue, "cell 1 14, align left");
			break;
		case 3:
			dashboard.add(lblProtonText, "cell 0 1, align right");
			dashboard.add(lblProtonValue, "cell 1 1, align left");
			dashboard.add(lblNeutronText, "cell 0 2, align right");
			dashboard.add(lblNeutronValue, "cell 1 2,align left");
			dashboard.add(lblElectronText, "cell 0 3, align right");
			dashboard.add(lblElectronValue, "cell 1 3, align left");
			break;
		case 4:
			dashboard.add(lblProtonText, "cell 0 1, align right");
			dashboard.add(lblProtonValue, "cell 1 1, align left");
			dashboard.add(lblNeutronText, "cell 0 2, align right");
			dashboard.add(lblNeutronValue, "cell 1 2,align left");
			dashboard.add(lblElectronText, "cell 0 3, align right");
			dashboard.add(lblElectronValue, "cell 1 3, align left");
			break;
		case 5:
			dashboard.add(lblProtonText, "cell 0 1, align right");
			dashboard.add(lblProtonValue, "cell 1 1, align left");
			dashboard.add(lblNeutronText, "cell 0 2, align right");
			dashboard.add(lblNeutronValue, "cell 1 2,align left");
			dashboard.add(lblElectronText, "cell 0 3, align right");
			dashboard.add(lblElectronValue, "cell 1 3, align left");
			break;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simulations.UnitBase#initializeSimulation(int, int)
	 */
	@Override
	protected void initializeSimulation(int sim, int set) {

		this.updateOutput(sim, set);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simulations.UnitBase#updateOutput(int, int)
	 */
	@Override
	public void updateOutput(int sim, int set) {

		int protonNum = 0;
		int neutronNum = 0;
		int electronNum = 0;
		int massNum = 0;
		int atomicNum = 0;

		// If any nuclear has been selected
		if (currentSimulationID != -1) {

			String name = IDMap[sim - 1].get(currentSimulationID);
			protonNum = nuclearInfo.getProtonNumByName(name);
			neutronNum = nuclearInfo.getNeutronNumByName(name);
			electronNum = nuclearInfo.getElectronNumByName(name);
			massNum = nuclearInfo.getMassNumByName(name);
			atomicNum = nuclearInfo.getAtomicNumByName(name);
		}
		lblProtonValue.setText(Integer.toString(protonNum));
		lblNeutronValue.setText(Integer.toString(neutronNum));
		lblElectronValue.setText(Integer.toString(electronNum));
		lblMassValue.setText(Integer.toString(massNum));
		lblAtomicValue.setText(Integer.toString(atomicNum));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simulations.UnitBase#computeForce(int, int)
	 */
	@Override
	protected void computeForce(int sim, int set) {

		clearAllMoleculeForce();

		switch (sim) {
		case 1:
			if(set==2)
				computeForceSim1Set2();
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:

			break;
		case 5:

			break;
		default:
			break;
		}

	}
	
	//Force compute function for Sim 1 Set 2
	//If the weak force switch is off and there is no disjoints bounded on molecule
	//Attract atoms to their original position
	private void computeForceSim1Set2()
	{
		
		if( this.weakForceSwitch && this.strongForceSwitch){  
			float x_ = p5Canvas.w / 2; // X Coordinate for a specific molecule
			float y_ = p5Canvas.h / 2;
			Vec2[] originalPositions = nuclearStructure.get("Helium-Quark");
			Vec2 molePos = new Vec2();
			Vec2 originalPos = new Vec2();
			Vec2 forceDir;
			float forceScale =0.1f;
			for(int i  = 0;i<State.getMoleculeNum();i++)
			{
				Molecule mole = State.getMoleculeByIndex(i);
				if(mole.compoundJoint.size()==0)
				{
					molePos.set(mole.getPosition());
					originalPos.set(box2d.coordPixelsToWorld(originalPositions[i].x+x_, originalPositions[i].y+y_));
					forceDir = originalPos.sub(molePos);
					mole.sumForceX[0] =  forceDir.x * forceScale ;
					mole.sumForceY[0] = forceDir.y * forceScale;
				}
				
				
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simulations.UnitBase#addMolecules(boolean, java.lang.String, int)
	 */

	@Override
	public boolean addMolecules(boolean isAppEnable, String compoundName,
			int count) {
		boolean res = false;

		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();

		Simulation simulation = getSimulation(sim, set);

		SpawnStyle spawnStyle = simulation.getSpawnStyle(compoundName);
		if (spawnStyle == SpawnStyle.Gas) {
			res = this.addNuclear(isAppEnable, compoundName, count);
		}

		if (res) {
			// Connect new created molecule to table index
			int tIndex = p5Canvas.getTableView().getIndexByName(compoundName);
			int lastIndex = State.molecules.size() - 1;

			for (int i = 0; i < count; i++) {
				// Set up table view index
				State.molecules.get(lastIndex - i).setTableIndex(tIndex);
				// Set up speed
				State.molecules.get(lastIndex - i).setRatioKE(
						1 / simulation.getSpeed());
				// Set up boiling point and freezing point
				State.molecules.get(lastIndex - i).setBoillingPoint(100);
				State.molecules.get(lastIndex - i).setFreezingPoint(0);
			}

		}

		return res;
	}

	/******************************************************************
	 * FUNCTION : addNuclear DESCRIPTION : Function to add nuclear to Using
	 * fixed position and fixed shape
	 * 
	 * INPUTS : isAppEnable(boolean), compoundName(String), count(int) OUTPUTS:
	 * None
	 *******************************************************************/
	public boolean addNuclear(boolean isAppEnable, String compoundName,
			int count) {
		boolean res = true;

		float x_ = p5Canvas.w / 2; // X Coordinate for a specific molecule
		float y_ = p5Canvas.h / 2; // Y Coordinate for a specific molecule

		Vec2 size = Molecule.getShapeSize(compoundName, p5Canvas);

		float moleWidth = size.x;
		float moleHeight = size.y;


		float spacing = moleWidth;


		boolean isClear = false;
		int startIndex = State.molecules.size(); // Start index of this group in
		// molecules arraylist

		// Using fixed position and fixed Number

		int protonNum = nuclearInfo.getProtonNumByName(compoundName);
		int electronNum = nuclearInfo.getElectronNumByName(compoundName);
		int neutronNum = nuclearInfo.getNeutronNumByName(compoundName);

		boolean createProton = false; // If proton is going to be created
		Vec2[] structure = nuclearStructure.get(compoundName);
		int sim  = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		if(sim==1 && set==2)
			structure = nuclearStructure.get("Helium-Quark");
		int len = structure.length;
		
		Molecule newMole =  null;

		for (int k = 0; k < count; k++) {
			
			if(len!=0)
			{
			for (int i = 0; i < len; i++) {
				
				if (protonNum == 0) // Only neutron left
				{
					createProton = false;
				} else if (neutronNum == 0) // only proton left
				{
					createProton = true;
				} else {
					createProton = (i % 2 == 0);
					if (createProton) {
						// Mark i with proton
						createProton = true;
						--protonNum;
					} else {
						// Mark i with neutron
						createProton = false;
						--neutronNum;
					}
				}

				// read preset position
				Vec2 pos = structure[i];
				String elementName = createProton ? protonName : neutronName;
				//System.out.println(elementName);
				newMole = new Molecule(x_ + pos.x, y_ + pos.y,
						elementName, box2d,
						p5Canvas, 0);

				// newMole.setGravityScale(0f);			
			
			res = State.molecules.add(newMole);
			float velocityX = 0;
			float velocityY = 0;
			newMole.setLinearVelocity(new Vec2(velocityX, velocityY));
			//Set the state to gas so there is no gravitivity
			newMole.setEnableAutoStateChange(false);
			newMole.setState(mState.Gas);
			//Set use png file when draw molecule
			newMole.setImage(elementName);
			}
			
			/* Add joint for solid molecules */
			//if (count > 1) 
			
				int index1 = 0;
				Molecule m1 = null;


				for ( int i = 0; i < len; i++) {

					/* For every molecule, create a anchor to fix its position */
					index1 = i + startIndex;
					m1 = State.molecules.get(index1);
					Vec2 m1Pos = box2d.coordWorldToPixels(m1.getPosition());
					Anchor anchor = new Anchor(m1Pos.x, m1Pos.y, box2d,
							p5Canvas);
					State.anchors.add(anchor);
					joint2Elements(m1, anchor, jointLen, frequency, damp);
				}
				
			}
			else //There is no predefined structure. Load original SVG file
			{

				newMole = new Molecule(x_, y_, compoundName, box2d,
						p5Canvas, 0);
				res = State.molecules.add(newMole);

				float velocityX = 0;
				float velocityY = 0;
				newMole.setLinearVelocity(new Vec2(velocityX, velocityY));
				newMole.setEnableAutoStateChange(false);
				newMole.setState(mState.Gas);
				
				if(sim==2)
				{
					newMole.setImage(compoundName);
					Vec2 m1Pos = box2d.coordWorldToPixels(newMole.getPosition());
					Anchor anchor = new Anchor(m1Pos.x, m1Pos.y, box2d,
							p5Canvas);
					State.anchors.add(anchor);
					joint2Elements(newMole, anchor, jointLen, frequency, damp);
				}
			}
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simulations.UnitBase#beginReaction(org.jbox2d.dynamics.contacts.Contact)
	 */
	@Override
	public void beginReaction(Contact c) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simulations.UnitBase#updateMoleculeCountRelated(int, int)
	 */
	@Override
	public void updateMoleculeCountRelated(int sim, int set) {
		// TODO Auto-generated method stub

	}

	protected class NuclearInfo {

		public HashMap<String, Nuclear> nuclearList;

		public NuclearInfo() {
			nuclearList = new HashMap<String, Nuclear>();
			setupList();
		}

		public void setupList() {
			Nuclear helium = new Nuclear("Helium", 2, 2, 2);
			nuclearList.put("Helium", helium);
			Nuclear carbon = new Nuclear("Carbon", 6, 8, 6);
			nuclearList.put("Carbon", carbon);
			Nuclear aluminum = new Nuclear("Aluminum", 13, 13, 13);
			nuclearList.put("Aluminum", aluminum);
			Nuclear potassium = new Nuclear("Potassium", 19, 21, 19);
			nuclearList.put("Potassium", potassium);
			Nuclear uranium = new Nuclear("Uranium-235",92,143,92);
			nuclearList.put("Uranium-235", uranium);
			Nuclear cesium = new Nuclear("Cesium-137",55,82,55);
			nuclearList.put("Cesium-137", cesium);
			Nuclear barium144 = new Nuclear("Barium-144",56,88,56);
			nuclearList.put("Barium-144", barium144);
			Nuclear krypton89 = new Nuclear("Krypton-89",36,53,36);
			nuclearList.put("Krypton-89", krypton89);
			Nuclear rubidium96 = new Nuclear("Rubidium-96",55,82,55);
			nuclearList.put("Rubidium-96", rubidium96);
			Nuclear krypton50 = new Nuclear("Krypton-50",19,31,19);
			nuclearList.put("Krypton-50", krypton50);
			Nuclear potassium85 = new Nuclear("Potassium-85",36,49,36);
			nuclearList.put("Potassium-85", potassium85);
			Nuclear chlorine45 = new Nuclear("Chlorine-45",17,28,17);
			nuclearList.put("Chlorine-45", chlorine45);
			Nuclear Strontium = new Nuclear("Strontium-90",38,52,38);
			nuclearList.put("Strontium-90", Strontium);
			
			
		}

		public int getProtonNumByName(String str) {
			if (nuclearList.containsKey(str)) {
				return nuclearList.get(str).getProtonNum();
			}
			return 0;
		}

		public int getNeutronNumByName(String str) {
			if (nuclearList.containsKey(str)) {
				return nuclearList.get(str).getNeutronNum();
			}
			return 0;
		}

		public int getElectronNumByName(String str) {
			if (nuclearList.containsKey(str)) {
				return nuclearList.get(str).getElectronNum();
			}
			return 0;
		}

		public int getAtomicNumByName(String str) {
			if (nuclearList.containsKey(str)) {
				return nuclearList.get(str).getAtomicNum();
			}
			return 0;
		}

		public int getMassNumByName(String str) {
			if (nuclearList.containsKey(str)) {
				return nuclearList.get(str).getProtonNum()
						+ nuclearList.get(str).getNeutronNum();
			}
			return 0;
		}

	}

	// Struct to save single nuclear information
	protected class Nuclear {
		private int protonNum;
		private int neutronNum;
		private int electronNum;
		private int atomicNum;
		private String name;

		Nuclear(String str, int pNum, int nNum, int aNum) {
			name = new String(str);
			protonNum = pNum;
			neutronNum = nNum;
			atomicNum = aNum;
			// electronNum = eNum;
		}

		public int getProtonNum() {
			return protonNum;
		}

		public int getNeutronNum() {
			return neutronNum;
		}

		public int getElectronNum() {
			return electronNum;
		}

		public int getAtomicNum() {
			return atomicNum;
		}

	}
	
	//Function that return the specific data to Canvas
	public float getDataGraph(int sim,int set,int indexOfGraph, int indexOfCompound)
	{
		return super.getDataGraph(sim, set, indexOfGraph, indexOfCompound);

	}

	@Override
	public void setMoleculeDensity() {
		// TODO Auto-generated method stub
		
	}

}
