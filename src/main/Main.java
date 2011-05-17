package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.Box;
import net.miginfocom.swing.MigLayout;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import control.MainController;

import view.P5Canvas;

import java.awt.Font;
import java.awt.Color;
import java.awt.CardLayout;
import java.awt.Panel;
import java.util.ArrayList;
import java.util.HashMap;

import static model.YAMLinterface.*;
import static model.State.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.ScrollPane;

public class Main {

	private JFrame mainFrame;
	public static JMenu simMenu = new JMenu("Choose Simulation");
	private int selectedUnit=0;
	private int selectedSim=0;
	
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.mainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	// Controllers
	P5Canvas p5Canvas = new P5Canvas();
	
	// Canvases
	MainController mainController = new MainController(p5Canvas);
	// TODO flag
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mainFrame = new JFrame();
		mainFrame.setBounds(0, 0, 1150, 700);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		JMenuBar menuBar = new JMenuBar();
		mainFrame.setJMenuBar(menuBar);
		p5Canvas.setBackground(Color.WHITE);
		
		// Controller and View Initialization
		p5Canvas.init();
		
		
		JMenu logoMenu = new JMenu("");
		logoMenu.setIcon(new ImageIcon(Main.class.getResource("/resources/png24x24/iconCcLogo.png")));
		menuBar.add(logoMenu);

		JMenuItem mntmAbout = new JMenuItem("About");
		logoMenu.add(mntmAbout);

		JMenuItem mntmHelp = new JMenuItem("Help");
		logoMenu.add(mntmHelp);

		/*
		 * Simulation Menu
		 */

		simMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("TTT:"+simMenu.getSelectedObjects());
			}
		});
		simMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				simMenu.doClick();
			}
		});
		menuBar.add(simMenu);


		// populate Units (first level of sim menu)
		final ArrayList units = getUnits();
		
		JMenu[] menuArray = new JMenu[units.size()];
		final ArrayList[] subMenuArrayList = new ArrayList[units.size()];
		
		for (int i = 0; i<units.size(); i++) {
			try {
				HashMap unit = (HashMap)units.get(i);
				int unitNo = Integer.parseInt((String)unit.get("unit"));
				String unitName = getUnitName(unitNo);

				JMenu menuItem = new JMenu("Unit "+ Integer.toString(unitNo) + ": " + unitName);
				menuArray[i] = menuItem;
				simMenu.add(menuArray[i]);

				
				// populate Sims (second level of sim menu)
				try {
					final ArrayList sims = getSims(unitNo);
					subMenuArrayList[i] =  new ArrayList();
					for (int j = 0; j<sims.size(); j++) {
						HashMap sim = (HashMap)sims.get(j);
						int simNo = Integer.parseInt((String)sim.get("sim"));
						String simName = getSimName(unitNo, simNo);
						
						JMenuItem subMenu = new JMenuItem("Sim "+ Integer.toString(simNo) + ": " + simName);
						
						//Add new subMenuItem
						menuItem.add(subMenu);
						subMenuArrayList[i].add(subMenu);
						                 
						subMenu.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								for (int i = 0; i<units.size(); i++) {
									for (int j = 0; j<subMenuArrayList[i].size(); j++) {
										if (e.getSource()==subMenuArrayList[i].get(j)){
											selectedUnit = i;
											selectedSim = j+1;
											simMenu.setText("Unit "+selectedUnit+": "+getUnitName(selectedUnit)+
													", Sim "+selectedSim+": "+getSimName(i, j+1));
										}	
									}	
								}
							}
						});
						
					}
				} catch (Exception e) {
					System.out.println("No Submenu Items: " + e);
				}
			} catch (Exception e) {
				System.out.println("No menu items: " + e);
			}
		}
		




		Component headHGlue = Box.createHorizontalGlue();
		menuBar.add(headHGlue);

		
		/*
		 * Menubar Unit/Sim/Set status area
		 */
		JLabel headInfo = new JLabel("Unit " + getCurrentUnitNumber() + ": " + getCurrentUnitName () + " • Simulation " + getCurrentSimNumber() + ": " + getCurrentSimName());
		menuBar.add(headInfo);

		Component headHStrut = Box.createHorizontalStrut(20);
		menuBar.add(headHStrut);

		JButton moleculeChooserBtn = new JButton("");
		moleculeChooserBtn.setEnabled(false);
		moleculeChooserBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png24x24/iconCompound.png")));
		menuBar.add(moleculeChooserBtn);

		JButton periodicTableBtn = new JButton("\n");
		periodicTableBtn.setEnabled(false);
		periodicTableBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png24x24/iconPeriodicTable.png")));
		menuBar.add(periodicTableBtn);
		mainFrame.getContentPane().setLayout(new MigLayout("insets 0, gap 0", "[250][480px,grow][250px]", "[grow]"));

		JPanel leftPanel = new JPanel();
		mainFrame.getContentPane().add(leftPanel, "cell 0 0,grow");
		leftPanel.setLayout(new MigLayout("insets 6, gap 2", "[grow]", "[][][grow]"));

		JPanel timerSubpanel = new JPanel();
		timerSubpanel.setBackground(new Color(211, 211, 211));
		leftPanel.add(timerSubpanel, "cell 0 0,grow");
		timerSubpanel.setLayout(new MigLayout("", "[][][][grow][]", "[grow][]"));

		JButton playBtn = new JButton("");
		playBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		playBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png48x48/iconPlay.png")));
		timerSubpanel.add(playBtn, "cell 0 0 1 2,growy");

		JComboBox setSelector = new JComboBox();
		timerSubpanel.add(setSelector, "cell 1 0 2 1,growx");

		JLabel timerLabel = new JLabel("Timer");
		timerSubpanel.add(timerLabel, "cell 4 0,alignx center");

		JButton setPrevBtn = new JButton("");
		setPrevBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png24x24/track-previous.png")));
		timerSubpanel.add(setPrevBtn, "cell 1 1,alignx center");

		JButton setNextBtn = new JButton("");
		setNextBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png24x24/track-next.png")));
		timerSubpanel.add(setNextBtn, "cell 2 1,growx");

		JLabel timerDisplay = new JLabel("30");
		timerDisplay.setForeground(new Color(0, 128, 0));
		timerDisplay.setFont(new Font("Digital", Font.PLAIN, 30));
		timerSubpanel.add(timerDisplay, "cell 4 1,alignx center");

		JPanel controlSubpanel = new JPanel();
		leftPanel.add(controlSubpanel, "cell 0 1,grow");
		controlSubpanel.setLayout(new MigLayout("insets 0", "[50%,fill][50%,fill]", "[][fill]"));

		
		/*
		 * Control Box 1
		 */
		final String controlCompoundName_1 = "Water";
		
		JPanel controlBox_1 = new JPanel();
		controlBox_1.setBackground(new Color(211, 211, 211));
		controlSubpanel.add(controlBox_1, "cell 0 0,growy");
		controlBox_1.setLayout(new MigLayout("insets 2, gap 2", "[][grow,center][][]", "[][][]"));

		JLabel controlLabel_1 = new JLabel(controlCompoundName_1);
		controlBox_1.add(controlLabel_1, "cell 1 0,alignx center");

		JButton controlAddBtn_1 = new JButton("");
		controlAddBtn_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		controlAddBtn_1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				mainController.addMolecule("Mercury");
				mainController.addMolecule(controlCompoundName_1);
				mainController.addMolecule(controlCompoundName_1);
				mainController.addMolecule(controlCompoundName_1);
				mainController.addMolecule(controlCompoundName_1);
			}
		});
		controlAddBtn_1.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/plus.png")));
		controlBox_1.add(controlAddBtn_1, "flowx,cell 1 1");

		JLabel controlAddQtyLabel_1 = new JLabel("5");
		controlBox_1.add(controlAddQtyLabel_1, "cell 1 1");

		JSlider controlAddQtySlider_1 = new JSlider();
		controlBox_1.add(controlAddQtySlider_1, "cell 1 2,growx");

		/*
		 * Control Box 2
		 */
		final String controlCompoundName_2 = "Acetate";
		
		JPanel controlBox_2 = new JPanel();
		controlBox_2.setBackground(new Color(211, 211, 211));
		controlSubpanel.add(controlBox_2, "cell 1 0,alignx center,growy");
		controlBox_2.setLayout(new MigLayout("insets 2, gap 2", "[grow,center]", "[][][]"));

		JLabel controlLabel_2 = new JLabel(controlCompoundName_2);
		controlBox_2.add(controlLabel_2, "cell 0 0,alignx center");

		JButton controlAddBtn_2 = new JButton("");
		controlAddBtn_2.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/plus.png")));
		controlBox_2.add(controlAddBtn_2, "flowx,cell 0 1");

		JLabel controlAddQtyLabel_2 = new JLabel("5");
		controlBox_2.add(controlAddQtyLabel_2, "cell 0 1");

		/*
		 * Control Box 3
		 */
		JPanel controlBox_3 = new JPanel();
		controlBox_3.setBackground(new Color(211, 211, 211));
		controlSubpanel.add(controlBox_3, "cell 0 1,grow");

		/*
		 * Control Box 4
		 */
		JPanel controlBox_4 = new JPanel();
		controlBox_4.setBackground(new Color(211, 211, 211));
		controlSubpanel.add(controlBox_4, "cell 1 1,grow");

		/*
		 * Legend
		 */
		JPanel legendSubpanel = new JPanel();
		leftPanel.add(legendSubpanel, "cell 0 2,grow");
		legendSubpanel.setLayout(new CardLayout(0, 0));

		JScrollPane legendScrollContainer_1 = new JScrollPane();
		legendSubpanel.add(legendScrollContainer_1, "name_1303765324750467000");

		JPanel legendPane_1 = new JPanel();
		legendScrollContainer_1.setViewportView(legendPane_1);
		legendPane_1.setLayout(new MigLayout("insets 6", "[grow][][]", "[][][][][]"));
		
		JLabel lblDff = new JLabel("Water");
		legendPane_1.add(lblDff, "cell 0 0");
		lblDff.setIcon(new ImageIcon(Main.class.getResource("/resources/compoundsPng50/Water.png")));

		JButton button_5 = new JButton("");
		legendPane_1.add(button_5, "cell 1 0");
		button_5.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/plus.png")));

		JButton button_6 = new JButton("");
		legendPane_1.add(button_6, "cell 2 0");
		button_6.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/minus.png")));

		JButton pane1_row2_compoundButton = new JButton("");
		pane1_row2_compoundButton.setToolTipText("Hydrochloric Acid");
		legendPane_1.add(pane1_row2_compoundButton, "cell 0 1,growx");
		pane1_row2_compoundButton.setIcon(new ImageIcon(Main.class.getResource("/resources/compoundsPng50/Hydrochloric-Acid.png")));

		JButton button_8 = new JButton("");
		button_8.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/plus.png")));
		legendPane_1.add(button_8, "cell 1 1");

		JButton button_9 = new JButton("");
		button_9.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/minus.png")));
		legendPane_1.add(button_9, "cell 2 1");

		JButton button_10 = new JButton("");
		button_10.setToolTipText("Hydronium");
		button_10.setIcon(new ImageIcon(Main.class.getResource("/resources/compoundsPng50/Hydronium.png")));
		legendPane_1.add(button_10, "cell 0 2,growx");

		JButton button_11 = new JButton("");
		button_11.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/plus.png")));
		legendPane_1.add(button_11, "cell 1 2");

		JButton button_12 = new JButton("");
		button_12.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/minus.png")));
		legendPane_1.add(button_12, "cell 2 2");

		JButton button_15 = new JButton("");
		button_15.setToolTipText("Methylammonium");
		button_15.setIcon(new ImageIcon(Main.class.getResource("/resources/compoundsPng50/Methylammonium.png")));
		legendPane_1.add(button_15, "cell 0 3,growx");

		JButton button_4 = new JButton("");
		button_4.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/plus.png")));
		legendPane_1.add(button_4, "cell 1 3");

		JButton button_17 = new JButton("");
		button_17.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/minus.png")));
		legendPane_1.add(button_17, "cell 2 3");

		JButton btnPhenylpthalein = new JButton("");
		btnPhenylpthalein.setToolTipText("Phenylpthalein");
		btnPhenylpthalein.setIcon(new ImageIcon(Main.class.getResource("/resources/compoundsPng50/Phenylpthalein.png")));
		legendPane_1.add(btnPhenylpthalein, "cell 0 4,growx");

		JButton button_16 = new JButton("");
		button_16.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/plus.png")));
		legendPane_1.add(button_16, "cell 1 4");

		JButton button_18 = new JButton("");
		button_18.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/minus.png")));
		legendPane_1.add(button_18, "cell 2 4");

		JScrollPane legendScrollContainer_2 = new JScrollPane();
		legendSubpanel.add(legendScrollContainer_2, "name_1303765387334599000");


		JPanel legendPane_2 = new JPanel();
		legendScrollContainer_2.setViewportView(legendPane_2);
		legendPane_2.setLayout(new MigLayout("insets 6", "[grow][][]", "[][][]"));

		JButton btnWater_1 = new JButton("Water");
		btnWater_1.setToolTipText("Water");
		legendPane_2.add(btnWater_1, "cell 0 0,growx");
		btnWater_1.setIcon(new ImageIcon(Main.class.getResource("/resources/compoundsPng50/Water.png")));

		JButton button_500 = new JButton("sds");
		legendPane_2.add(button_500, "cell 1 0");
		button_500.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/plus.png")));

		JButton button_600 = new JButton("");
		legendPane_2.add(button_600, "cell 2 0");
		button_600.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/minus.png")));

		JButton button_700 = new JButton("Acetate");
		button_700.setToolTipText("Acetate");
		legendPane_2.add(button_700, "cell 0 1,growx");
		button_700.setIcon(new ImageIcon(Main.class.getResource("/resources/compoundsPng50/Acetate.png")));

		JButton button_800 = new JButton("dsa");
		button_800.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/plus.png")));
		legendPane_2.add(button_800, "cell 1 1");

		JButton button_900 = new JButton("");
		button_900.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/minus.png")));
		legendPane_2.add(button_900, "cell 2 1");

		JButton button_1000 = new JButton("");
		button_1000.setToolTipText("Butane");
		button_1000.setIcon(new ImageIcon(Main.class.getResource("/resources/compoundsPng50/Butane.png")));
		legendPane_2.add(button_1000, "cell 0 2,growx");

		JButton button_1100 = new JButton("");
		button_1100.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/plus.png")));
		legendPane_2.add(button_1100, "cell 1 2");

		JButton button_1200 = new JButton("");
		button_1200.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/minus.png")));
		legendPane_2.add(button_1200, "cell 2 2");







		/*
		 * Center Panel
		 */
		JPanel centerPanel = new JPanel();
		mainFrame.getContentPane().add(centerPanel, "cell 1 0,grow");
		centerPanel.setLayout(new MigLayout("insets 2, gap 2", "[]", "[]"));

		JTabbedPane canvasTabs = new JTabbedPane(JTabbedPane.TOP);
		centerPanel.add(canvasTabs, "cell 0 0,grow");

		JPanel canvasPanel_mainView = new JPanel();
		canvasTabs.addTab("Main Simulation", null, canvasPanel_mainView, null);
		canvasPanel_mainView.setLayout(new MigLayout("insets 2, gap 2", "[center][600px]", "[600px][center]"));

		JSlider canvasControl_main_scale = new JSlider();
		canvasControl_main_scale.setOrientation(SwingConstants.VERTICAL);
		canvasPanel_mainView.add(canvasControl_main_scale, "flowy,cell 0 0");

		JLabel canvasControlLabel_main_scale = new JLabel("Scale");
		canvasPanel_mainView.add(canvasControlLabel_main_scale, "cell 0 0");

		JSlider canvasControl_main_speed = new JSlider();
		canvasControl_main_speed.setOrientation(SwingConstants.VERTICAL);
		canvasPanel_mainView.add(canvasControl_main_speed, "cell 0 0");

		canvasPanel_mainView.add(p5Canvas, "cell 1 0,grow");
		// TODO flag

		JLabel canvasControlLabel_main_area = new JLabel("Area");
		canvasPanel_mainView.add(canvasControlLabel_main_area, "flowx,cell 1 1");

		JSlider canvasControl_main_area = new JSlider();
		canvasPanel_mainView.add(canvasControl_main_area, "cell 1 1");

		JLabel canvasControlLabel_main_heat = new JLabel("Heat");
		canvasPanel_mainView.add(canvasControlLabel_main_heat, "cell 1 1");

		JSlider canvasControl_main_heat = new JSlider();
		canvasPanel_mainView.add(canvasControl_main_heat, "cell 1 1");

		JLabel canvasControlLabel_main_speed = new JLabel("Speed");
		canvasPanel_mainView.add(canvasControlLabel_main_speed, "cell 0 0");

		// close view
		Panel canvasPanel_closeView = new Panel();
		canvasTabs.addTab("Close Up", null, canvasPanel_closeView, null);
		canvasPanel_closeView.setLayout(new MigLayout("", "[grow,fill]", "[grow,fill]"));
		
		/*
		 * Right Panel
		 */
		JPanel rightPanel = new JPanel();
		mainFrame.getContentPane().add(rightPanel, "cell 2 0,grow");
		rightPanel.setLayout(new MigLayout("insets 2, gap 2", "[]", "[grow][grow]"));

		JTabbedPane graphTabs = new JTabbedPane(JTabbedPane.TOP);
		rightPanel.add(graphTabs, "cell 0 0,grow");

		JPanel graphSet_1 = new JPanel();
		graphTabs.addTab("Compounds", null, graphSet_1, null);
		graphSet_1.setLayout(new MigLayout("insets 6", "[150:n,grow][]", "[150:n][grow]"));

		JPanel graph_1 = new JPanel();
		graph_1.setBackground(Color.WHITE);
		graphSet_1.add(graph_1, "cell 0 0,grow");

		JButton graphPopoutBtn_1 = new JButton("");
		graphPopoutBtn_1.setEnabled(false);
		graphPopoutBtn_1.setIcon(new ImageIcon(Main.class.getResource("/resources/png24x24/iconZoom.png")));
		graphSet_1.add(graphPopoutBtn_1, "cell 1 0,aligny top");

		JPanel graphKey_1 = new JPanel();
		graphKey_1.setBackground(Color.WHITE);
		graphSet_1.add(graphKey_1, "cell 0 1,grow");

		JPanel graphSet_2 = new JPanel();
		graphTabs.addTab("pH", null, graphSet_2, null);

		JPanel dashboard = new JPanel();
		rightPanel.add(dashboard, "cell 0 1,alignx center,growy");
		dashboard.setLayout(new MigLayout("", "[][]", "[][][][][][]"));

		JLabel elapsedTimeLabel = new JLabel("Elapsed Set Time:");
		dashboard.add(elapsedTimeLabel, "flowx,cell 0 0,alignx right");

		JLabel elapsedTimeOutput = new JLabel("01:10:00");
		dashboard.add(elapsedTimeOutput, "cell 1 0");

		JLabel moleculeQuantityLabel = new JLabel("Total Molecule Quantity:");
		dashboard.add(moleculeQuantityLabel, "cell 0 1,alignx right");

		JLabel moleculeQuantityOutput = new JLabel("50");
		dashboard.add(moleculeQuantityOutput, "cell 1 1");

		JLabel totalSystemVolumeLabel = new JLabel("Total System Volume:");
		dashboard.add(totalSystemVolumeLabel, "cell 0 2,alignx right");

		JLabel totalSystemVolumeOutput = new JLabel("200 ml");
		dashboard.add(totalSystemVolumeOutput, "cell 1 2");

		JLabel totalSystemEnergyLabel = new JLabel("Total System Energy:");
		dashboard.add(totalSystemEnergyLabel, "cell 0 3,alignx right");

		JLabel totalSystemEnergyOutput = new JLabel("100 kJ");
		dashboard.add(totalSystemEnergyOutput, "cell 1 3");

		JLabel totalSystemPressureLabel = new JLabel("Total System Pressure:");
		dashboard.add(totalSystemPressureLabel, "cell 0 4,alignx right");

		JLabel totalSystemPressureOutput = new JLabel("100 kPa");
		dashboard.add(totalSystemPressureOutput, "cell 1 4");
		
	}

}
