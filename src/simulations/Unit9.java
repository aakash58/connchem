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

import Util.Integrator;

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

	// Dynamic panel elements
	private JSlider sliderStrongForce;
	private JLabel lblStrongForce;
	private JSlider sliderWeakForce;
	private JLabel lblWeakForce;

	private Integrator interpolatorShow; // Interpolator to do show animation
	private Integrator interpolatorHide; // Interpolator to do fade animation

	private String protonName = new String("Proton");
	private String neutronName = new String("Neutron");

	private int currentSimulationID = 0;
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
	private boolean strongForceSwitch;
	private boolean weakForceSwitch;

	// Listeners
	ActionListener moleculeBtnListener;
	ChangeListener sliderStrongForceListener;
	ChangeListener SliderWeakForceListener;

	public Unit9(P5Canvas parent, PBox2D box) {
		super(parent, box);

		unitNum = 9;
		interpolatorShow = new Integrator(0);
		interpolatorHide = new Integrator(0);
		IDMap = new ArrayList[5];
		nuclearInfo = new NuclearInfo();
		nuclearStructure = new HashMap<String, Vec2[]>();
		buttonMap = new HashMap<JButton, String>();

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

	private void setupOutputLabels() {
		lblProtonText = new JLabel("Number of Proton:");
		lblProtonValue = new JLabel("");
		lblNeutronText = new JLabel("Number of Neutron:");
		lblNeutronValue = new JLabel("");
		lblElectronText = new JLabel("Electron:");
		lblElectronValue = new JLabel("");
		lblAtomicText = new JLabel("Atomic Number:");
		lblAtomicValue = new JLabel("");
		lblMassText = new JLabel("Mass Number:");
		lblMassValue = new JLabel("");

		sliderStrongForce = new JSlider(0, 1, 0);
		sliderStrongForce.setName("Strong Force");
		sliderWeakForce = new JSlider(0, 1, 0);
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
		sliderWeakForce.addChangeListener(SliderWeakForceListener);

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
				}

			}
		};

		sliderStrongForceListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = ((JSlider) e.getSource()).getValue();
				String name = ((JSlider) e.getSource()).getName();

				if (name.equals("Strong Force")) {
					strongForceSwitch = value == 0 ? false : true;
				} else if (name.equals("Weak Force")) {
					weakForceSwitch = value == 0 ? false : true;
				}
			}

		};
	}

	// Function that set up the nuclear structure, that is, what the alignment
	// of proton and neutron
	private void setupNuclearStructure() {
		// Helium - 4
		int massNum = 4;
		float x = 0;
		float y = 0;
		int column = 0;
		int row = 0;
		Vec2[] heliumStructure = new Vec2[massNum];
		Vec2 protonSize = Molecule.getShapeSize("Proton", p5Canvas);
		for (int i = 0; i < massNum; i++) {
			x = protonSize.x / 2 * (i / 2 == 0 ? -1 : 1);
			y = protonSize.y * ((i % 2) + -0.5f * (i / 2));
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
			x = protonSize.x * column;
			y = protonSize.y * (row + (column % 2 == 0 ? -0.5f : 0.0f));
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
			x = protonSize.x * column;
			y = protonSize.y
					* (row + (column % 2 == 0 ? -0.25f * column : 0.0f));
			if (column == 0)
				y -= protonSize.y * 0.25f;
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
			x = protonSize.x * column;
			y = protonSize.y * (row + (column % 2 == 0 ? -0.5f : 0.0f));
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
		h2[0] = new Vec2(protonSize.x*0.5f,protonSize.y*0.5f);
		h2[1] = new Vec2(protonSize.x*-0.5f,protonSize.y*-0.5f);
		nuclearStructure.put("H-2", h2);
		
		massNum = 3;
		Vec2[] he3 = new Vec2[massNum];
		he3[0] = new Vec2(0,protonSize.y*-1);
		he3[1] = new Vec2(0,protonSize.y);
		he3[2] = new Vec2(protonSize.x,0);
		nuclearStructure.put("He-3", he3);
		
		massNum = 4;
		Vec2[] he4 = new Vec2[massNum];
		he4[0] =  new Vec2(0,0);
		he4[1] = new Vec2(protonSize.x,protonSize.y*-0.5f);
		he4[2] = new Vec2(0,protonSize.y);
		he4[3] = new Vec2(protonSize.x,protonSize.y*0.5f);
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

	// Customize Interface in Main reset after all interface have been
	// initialized
	public void customizeInterface(int sim, int set) {
		Main main = p5Canvas.getMain();
		main.heatSlider.setEnabled(false);
		main.volumeSlider.setEnabled(false);

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

	private void resetDynamicPanel(int sim, int set) {

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
		buttonMap.clear();

		p5Canvas.isBoundaryShow = false;
		p5Canvas.setIfConstrainKE(false);

		p5Canvas.getMain().boxMoleculeHiding.setEnabled(false);
		p5Canvas.getMain().boxDisplayForce.setEnabled(false);

		// Set up speed
		setupSpeed();

		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();

		switch (sim) {
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
			dashboard.add(lblProtonText, "cell 0 1, align right");
			dashboard.add(lblProtonValue, "cell 1 1, align left");
			dashboard.add(lblNeutronText, "cell 0 2, align right");
			dashboard.add(lblNeutronValue, "cell 1 2,align left");
			dashboard.add(lblAtomicText, "cell 0 3, align right");
			dashboard.add(lblAtomicValue, "cell 1 3, align left");
			dashboard.add(lblMassText, "cell 0 4, align right");
			dashboard.add(lblMassValue, "cell 1 4, align left");
			break;
		case 2:
			dashboard.add(lblProtonText, "cell 0 1, align right");
			dashboard.add(lblProtonValue, "cell 1 1, align left");
			dashboard.add(lblNeutronText, "cell 0 2, align right");
			dashboard.add(lblNeutronValue, "cell 1 2,align left");
			dashboard.add(lblElectronText, "cell 0 3, align right");
			dashboard.add(lblElectronValue, "cell 1 3, align left");
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

		Random probRand = new Random();

		float spacing = moleWidth;
		float maxVelocity = 40;

		boolean isClear = false;

		// Using fixed position and fixed Number

		int protonNum = nuclearInfo.getProtonNumByName(compoundName);
		int electronNum = nuclearInfo.getElectronNumByName(compoundName);
		int neutronNum = nuclearInfo.getNeutronNumByName(compoundName);

		boolean createProton = false; // If proton is going to be created
		Vec2[] structure = nuclearStructure.get(compoundName);
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

				newMole = new Molecule(x_ + pos.x, y_ + pos.y,
						createProton ? protonName : neutronName, box2d,
						p5Canvas, 0);

				// newMole.setGravityScale(0f);
			
			
			
			res = State.molecules.add(newMole);
			float velocityX = 0;
			float velocityY = 0;
			newMole.setLinearVelocity(new Vec2(velocityX, velocityY));
			newMole.setEnableAutoStateChange(false);
			newMole.setState(mState.Gas);
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

}
