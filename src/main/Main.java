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
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class Main {

	private JFrame mainFrame;
	public static JMenu simMenu = new JMenu("Choose Simulation");
	private int selectedUnit=0;
	private int selectedSim=0;
	private Color selectedColor = new Color(200,200,150);
	private int[] sliderValues = {3,4,5,6,7};
	private int minSliderValue = 1;
	private int maxSliderValue = 9;
	
	
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
		simMenu.setBackground(selectedColor);
		

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
											if (selectedUnit>0){
												simMenu.getItem(selectedUnit).setBackground(Color.WHITE);
												((JMenuItem) (subMenuArrayList[selectedUnit].get(selectedSim-1))).setBackground(Color.WHITE) ;
											}
											selectedUnit = i;
											selectedSim = j+1;
											simMenu.setText("Unit "+selectedUnit+": "+getUnitName(selectedUnit)+
													", Sim "+selectedSim+": "+getSimName(i, j+1));
											simMenu.getItem(i).setBackground(selectedColor);
											((JMenuItem) (subMenuArrayList[i].get(j))).setBackground(selectedColor) ;
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
		mainFrame.getContentPane().setLayout(new MigLayout("insets 0, gap 0", "[263.00][480px,grow][250px]", "[grow]"));

		JPanel leftPanel = new JPanel();
		mainFrame.getContentPane().add(leftPanel, "cell 0 0,grow");
		leftPanel.setLayout(new MigLayout("insets 6, gap 2", "14[grow]", "[][][grow]"));

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

		
		//***************** Add elements Control panel
		
		final String controlCompoundName_1 = "Water";
		final String controlCompoundName_2 = "Hydronium";
		final String controlCompoundName_3 = "Acetate";
		JPanel legendSubpanel = new JPanel();
		leftPanel.add(legendSubpanel, "cell 0 1,grow");
		legendSubpanel.setLayout(new CardLayout(0, 0));

		JScrollPane legendScrollContainer_1 = new JScrollPane();
		legendSubpanel.add(legendScrollContainer_1, "name_1303765324750467000");

		JPanel legendPane_1 = new JPanel();
		legendScrollContainer_1.setViewportView(legendPane_1);
		legendPane_1.setLayout(new MigLayout("insets 6", "[174.00,grow]", "[53.00,grow][grow][grow][grow][grow]"));
		
		JPanel panel_2 = new JPanel();
		legendPane_1.add(panel_2, "cell 0 0");
		panel_2.setBackground(new Color(192, 192, 192));
		panel_2.setLayout(new MigLayout("insets 6, gap 0", "[][][69.00]", "[][]"));
		
		final JLabel label = new JLabel(""+sliderValues[0]);
		panel_2.add(label, "cell 0 1");
		
		JLabel lblWater = new JLabel("Water");
		lblWater.setIcon(new ImageIcon(Main.class.getResource("/resources/compoundsPng50/Hydroxide.png")));
		panel_2.add(lblWater, "cell 0 0 3 1,growx");
		
		JButton button = new JButton("");
		button .setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/plus.png")));
		panel_2.add(button, "cell 2 1 1 1,growy");
		button.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				for (int i=0;i<Integer.parseInt(label.getText());i++)
					mainController.addMolecule(controlCompoundName_1);
			}
		});
		
		JSlider slider_4 = new JSlider(minSliderValue,maxSliderValue,sliderValues[0]);
		panel_2.add(slider_4, "cell 1 1");
		slider_4.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
			int value = ((JSlider) e.getSource()).getValue(); 
				label.setText(""+value);
			}
		});
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		legendPane_1.add(panel, "cell 0 1,grow");
		panel.setLayout(new MigLayout("insets 6, gap 0", "[][][69.00]", "[][]"));
		
		JLabel lblHydrochloricAcid_1 = new JLabel("Hydrochloric Acid");
		lblHydrochloricAcid_1.setIcon(new ImageIcon(Main.class.getResource("/resources/compoundsPng50/Hydrochloric-Acid.png")));
		panel.add(lblHydrochloricAcid_1, "cell 0 0 3 1,growx");
		
		final JLabel lblNewLabel_1 = new JLabel(""+sliderValues[1]);
		panel.add(lblNewLabel_1, "cell 0 1");
		
		JButton button_1 = new JButton("");
		button_1.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/plus.png")));
		panel.add(button_1, "cell 2 1,growy");
		button_1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				for (int i=0;i<Integer.parseInt(lblNewLabel_1.getText());i++)
					mainController.addMolecule(controlCompoundName_2);
			}
		});
		
		JSlider slider = new JSlider(minSliderValue,maxSliderValue,sliderValues[1]);
		panel.add(slider, "cell 1 1");
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
			int value = ((JSlider) e.getSource()).getValue(); 
			lblNewLabel_1.setText(""+value);
			}
		});
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(new Color(192, 192, 192));
		legendPane_1.add(panel_1, "cell 0 2,grow");
		panel_1.setLayout(new MigLayout("insets 6, gap 0", "[][][69.00]", "[][]"));
		
		final JLabel lblNewLabel_2 = new JLabel(""+sliderValues[2]);
		panel_1.add(lblNewLabel_2, "cell 0 1");
		
		JLabel lblNewLabel_3 = new JLabel("Hydronium");
		lblNewLabel_3.setIcon(new ImageIcon(Main.class.getResource("/resources/compoundsPng50/Hydronium.png")));
		panel_1.add(lblNewLabel_3, "cell 0 0 3 1,growx");
		
		JButton button_2 = new JButton("");
		button_2.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/plus.png")));
		panel_1.add(button_2, "cell 2 1 1 1,growy");
		button_2.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				for (int i=0;i<Integer.parseInt(lblNewLabel_2.getText());i++)
					mainController.addMolecule(controlCompoundName_3);
			}
		});
		
		JSlider slider_1 = new JSlider(minSliderValue, maxSliderValue,sliderValues[2]);
		panel_1.add(slider_1, "cell 1 1");
		slider_1.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
			int value = ((JSlider) e.getSource()).getValue(); 
			lblNewLabel_2.setText(""+value);
			}
		});
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(new Color(192, 192, 192));
		legendPane_1.add(panel_3, "cell 0 3,grow");
		panel_3.setLayout(new MigLayout("insets 6, gap 0", "[][][69.00]", "[][]"));
		
		final JLabel label_1 = new JLabel(""+sliderValues[3]);
		panel_3.add(label_1, "cell 0 1");
		
		JLabel lblMethylammonium = new JLabel("Methylammonium");
		lblMethylammonium.setIcon(new ImageIcon(Main.class.getResource("/resources/compoundsPng50/Methylammonium.png")));
		panel_3.add(lblMethylammonium, "cell 0 0 3 1,growx");
		
		JButton button_3 = new JButton("");
		button_3.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/plus.png")));
		panel_3.add(button_3, "cell 2 1 1 1,growy");
		button_3.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				for (int i=0;i<Integer.parseInt(label_1.getText());i++)
					mainController.addMolecule(controlCompoundName_3);
			}
		});
		
		
		JSlider slider_2 = new JSlider(minSliderValue,maxSliderValue,sliderValues[3]);
		panel_3.add(slider_2, "cell 1 1");
		slider_2.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
			int value = ((JSlider) e.getSource()).getValue(); 
			label_1.setText(""+value);
			}
		});
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(new Color(192, 192, 192));
		legendPane_1.add(panel_4, "cell 0 4,grow");
		panel_4.setLayout(new MigLayout("insets 6, gap 0", "[][][69.00]", "[][]"));
		
		final JLabel label_3 = new JLabel(""+sliderValues[4]);
		panel_4.add(label_3, "cell 0 1");
		
		JLabel lblNewLabel_6 = new JLabel("Phenylpthalein");
		lblNewLabel_6.setIcon(new ImageIcon(Main.class.getResource("/resources/compoundsPng50/Phenylpthalein.png")));
		panel_4.add(lblNewLabel_6, "cell 0 0 3 1,growx");
		
		JButton button_4 = new JButton("");
		button_4.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/plus.png")));
		panel_4.add(button_4, "cell 2 1 1 1,growy");
		button_4.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				for (int i=0;i<Integer.parseInt(label_3.getText());i++)
					mainController.addMolecule(controlCompoundName_3);
			}
		});
		
		JSlider slider_3 = new JSlider(minSliderValue,maxSliderValue,sliderValues[4]);
		panel_4.add(slider_3, "cell 1 1");
		slider_3.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
			int value = ((JSlider) e.getSource()).getValue(); 
			label_3.setText(""+value);
			}
		});


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
