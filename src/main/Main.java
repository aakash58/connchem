package main;

//Connected Chemistry Simulations
//part of the Connected Chemistry Curriculum

//Project Leader: Mike Stieff, PhD, University of Illinois at Chicago
//Modeled in Processing by: Tuan Dang and Allan Berry

//This software is Copyright © 2010, 2011 University of Illinois at Chicago,
//and is released under the GNU General Public License.
//Please see "resources/copying.txt" for more details.

/*--------------------------------------------------------------------------*/

//This file is part of the Connected Chemistry Simulations (CCS).

//CCS is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.

//CCS is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.

//You should have received a copy of the GNU General Public License
//along with CCS.  If not, see <http://www.gnu.org/licenses/>.

/*--------------------------------------------------------------------------*/


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

import model.YAMLinterface;
import net.miginfocom.swing.MigLayout;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import view.P5Canvas;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static model.YAMLinterface.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Main {
	// Controllers
	// this is a different change. 
	private static P5Canvas p5Canvas = new P5Canvas();
	// TODO flag
	public static JFrame mainFrame;
	public static JMenu simMenu = new JMenu("Choose Simulation");
	public static int selectedUnit=1;
	public static int selectedSim=1;
	public static int selectedSet=1;
	public static boolean isWelcomed=false;
	public static Color selectedColor = new Color(200,200,150);
	public static Color defaultColor = Color.LIGHT_GRAY;
	private int[] sliderValues = {5,6,7,6,3};
	private static int sliderValue = 5;
	
	private static int minSliderValue = 1;
	private static int maxSliderValue = 9;
	public static JPanel dynamicPanel;
	public static JScrollPane dynamicScrollPane; 
	public static ArrayList additionalPanelList =  new ArrayList();
	public static ArrayList defaultSetMolecules =  new ArrayList();
	public static CustomPopupMenu scrollablePopupMenu;
	public static String[] moleculeNames = null;
	public static JPanel rightPanel;
	public static JPanel leftPanel;
	public static JPanel centerPanel;
	public static JPanel welcomePanel;
	public static Canvas canvas = new Canvas();
	public static TableView tableView;
	public static TableSet tableSet;
	public static final JLabel  volumeLabel = new JLabel(P5Canvas.currenttVolume+"mL");
	public static JSlider volumeSlider = new JSlider(0, 100, P5Canvas.currenttVolume);
	public static int defaultZoom =50;
	public static JSlider zoomSlider = new JSlider(0, 100, defaultZoom);
	public static int defaultSpeed =50;
	public static JSlider speedSlider = new JSlider(0, 100, defaultSpeed);
	public static int heatInit =25;
	public static int heatMin =-10;
	public static int heatMax =200;
	
	public static JSlider heatSlider = new JSlider(heatMin, heatMax, heatInit);
	public static boolean isVolumeblocked = false;
	public static JLabel totalSystemEnergy;
	public static JLabel averageSystemEnergy;
	public static JLabel elapsedTime;
	public static JButton playBtn;
	public static boolean isFirst =true; 
	
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

	protected String[] parseNames(String[] files) {
    	int numMolecules = 0;
    	for (int i=0;i<files.length;i++){
    		if (files[i].endsWith(".png")){
    			numMolecules++;
    		}
    	}
    	String[] moleculeNames = new String[numMolecules];
    	int count =0;
    	for (int i=0;i<files.length;i++){
    		if (files[i].endsWith(".png")){
    			moleculeNames[count] = files[i].split(".png")[0];   
    			count++;
    		}
    	}
    	return moleculeNames;
    }
    

	String[] getResourceListing(Class clazz, String path) throws URISyntaxException, IOException {
	      URL dirURL = clazz.getClassLoader().getResource(path);
	     // System.out.println(dirURL);
			
	      if (dirURL != null && dirURL.getProtocol().equals("file")) {
	        return new File(dirURL.toURI()).list();
	      } 

	      if (dirURL == null) {
	        /* 
	         * In case of a jar file, we can't actually find a directory.
	         * Have to assume the same jar as clazz.
	         */
	        String me = clazz.getName().replace(".", "/")+".class";
	        dirURL = clazz.getClassLoader().getResource(me);
	      }
	      
	      if (dirURL.getProtocol().equals("jar")) {
	        /* A JAR path */
	        String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
	        JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
	        Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
	        ArrayList result = new ArrayList(); //avoid duplicates in case it is a subdirectory
	        while(entries.hasMoreElements()) {
	          String name = entries.nextElement().getName();
	          	
	          if (name.startsWith(path)) { //filter according to the path
	            String entry = name.substring(path.length());
	            int checkSubdir = entry.indexOf("/");
	            if (checkSubdir >= 0) {
	              // if it is a subdirectory, we just return the directory name
	              entry = entry.substring(0, checkSubdir);
	          	
	            }
	            result.add(entry);
	          }
	        }
	        Collections.sort(result);
	        String[] resultArray = new String[result.size()];
	        for (int i=0;i<result.size();i++){
	        	resultArray[i] = result.get(i).toString(); 
	        }
	        return resultArray;
	      } 
	        
	      throw new UnsupportedOperationException("Cannot list files for URL "+dirURL);
	  }
	
	
	public static void removeAdditionalMolecule(int additionalIndex){
		int pos = defaultSetMolecules.size()+additionalIndex;
		dynamicPanel.removeAll();
		additionalPanelList.remove(pos);
		for (int i=0; i<additionalPanelList.size();i++){
			JPanel p = (JPanel) additionalPanelList.get(i);
			dynamicPanel.add(p, "cell 0 "+i+",grow");
			
		}
	}
		
	public static void addAdditionalMolecule(){
		//Default unit setting
		if (selectedUnit==0 && defaultSetMolecules.size()==0){
			defaultSetMolecules.add("Water");
			defaultSetMolecules.add("Hydrochloric Acid");
			defaultSetMolecules.add("Hydronium");
			defaultSetMolecules.add("Methylammonium");
			defaultSetMolecules.add("Phenylpthalein");
		}
		JPanel panel = new JPanel();
		panel.setBackground(Main.selectedColor);
		panel.setLayout(new MigLayout("insets 6, gap 0", "[][][69.00]", "[][]"));
		
		//new molecule is at the end of additionalList
		int newMolecule =  CustomPopupMenu.additionalList.size()-1; 
		int pos = defaultSetMolecules.size() + newMolecule;
		dynamicPanel.add(panel, "cell 0 "+pos+",grow");
		additionalPanelList.add(panel);
		
		String cName = (String)  CustomPopupMenu.additionalList.get(newMolecule);
		JLabel label = new JLabel(cName);
		
		final String fixedName = cName.replace(" ", "-");
		label.setIcon(new ImageIcon(Main.class .getResource("/resources/compoundsPng50/"+fixedName+".png")));
		panel.add(label, "cell 0 0 3 1,growx");
		
		
		final JLabel label_1 = new JLabel(""+sliderValue);
		panel.add(label_1, "cell 0 1");
	
		
		JSlider slider = new JSlider(minSliderValue,maxSliderValue,sliderValue);
		panel.add(slider, "cell 1 1");
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
			int value = ((JSlider) e.getSource()).getValue(); 
			label_1.setText(""+value);
			}
		});
		
		
		JButton button_1 = new JButton("");
		button_1.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/plus.png")));
		panel.add(button_1, "cell 2 1,growy");
		button_1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				int count = Integer.parseInt(label_1.getText());
				p5Canvas.addMolecule(fixedName,count);
			}	
		});
		if (dynamicPanel.getComponentCount()>6){
			int h = dynamicPanel.getComponentCount()*100;
			dynamicScrollPane.getViewport().setViewPosition(new java.awt.Point(0,h));
		}
	}
	
	public static void updateDynamicPanel(){
		if (dynamicPanel!=null){
			dynamicPanel.removeAll();
			defaultSetMolecules =  new ArrayList();
			//System.out.println(""+getSetCompounds(selectedUnit,selectedSim,selectedIndex));
			ArrayList compounds= getSetCompounds(selectedUnit,selectedSim,selectedSet);
			if (compounds!=null){
				for (int i=0;i<compounds.size();i++){
					JPanel panel = new JPanel();
					panel.setBackground(Color.LIGHT_GRAY);
					panel.setLayout(new MigLayout("insets 6, gap 0", "[][][69.00]", "[][]"));
					dynamicPanel.add(panel, "cell 0 "+i+",grow");
					additionalPanelList.add(panel);
					
					String cName =  getCompoundName(selectedUnit,selectedSim,selectedSet,i);
					defaultSetMolecules.add(cName);
					JLabel label = new JLabel(cName);
					
					//System.out.println("cName: "+cName);
					final String fixedName = cName.replace(" ", "-");
					label.setIcon(new ImageIcon(Main.class.getResource("/resources/compoundsPng50/"+fixedName+".png")));
					panel.add(label, "cell 0 0 3 1,growx");
					
					
					final JLabel label_1 = new JLabel(""+sliderValue);
					panel.add(label_1, "cell 0 1");
				
					
					JSlider slider = new JSlider(minSliderValue,maxSliderValue,sliderValue);
					panel.add(slider, "cell 1 1");
					slider.addChangeListener(new ChangeListener() {
						public void stateChanged(ChangeEvent e) {
						int value = ((JSlider) e.getSource()).getValue(); 
						label_1.setText(""+value);
						}
					});
					
					
					JButton button_1 = new JButton("");
					button_1.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/plus.png")));
					panel.add(button_1, "cell 2 1,growy");
					button_1.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent arg0) {
							int count = Integer.parseInt(label_1.getText());
							p5Canvas.addMolecule(fixedName,count);
						}
					});
				
				}
			}
		}
	}
	
	
	public static void createPopupMenu(){
		scrollablePopupMenu = new CustomPopupMenu();
		for (int i=0;i<moleculeNames.length;i++){
			CustomButton xx = new CustomButton(moleculeNames[i].replace("-", " "));
			xx.setIcon(new ImageIcon(Main.class.getResource("/resources/compoundsPng50/"+moleculeNames[i]+".png")));
			xx.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					//scrollablePopupMenu.hidemenu();
				}
			});
			scrollablePopupMenu.add(xx, i);
		}
	}
	
	public static void reset(){
		if (isWelcomed && welcomePanel !=null){
			mainFrame.remove(welcomePanel);
			mainFrame.getContentPane().add(leftPanel, "cell 0 0,grow");
			mainFrame.getContentPane().add(centerPanel, "cell 1 0,grow");
			mainFrame.getContentPane().add(rightPanel, "cell 2 0,grow");
			isWelcomed = false;
		}
		
		p5Canvas.removeAllMolecules();
		canvas.reset();
		TableView.setSelectedRow(-1);
		
		ArrayList a = getSetCompounds(selectedUnit,selectedSim,selectedSet);
		if (a!=null) {
			for (int i=0; i<a.size();i++){
				String s = (String) getCompoundName(selectedUnit,selectedSim,selectedSet,i);
				int num = Integer.parseInt(getCompoundQty(selectedUnit,selectedSim,selectedSet,i).toString());
				p5Canvas.addMoleculeRandomly(s.replace(" ","-"),num);
			}
		}
		
		
		//CustomPopupMenu.additionalList = new ArrayList();
		//additionalPanelList = new ArrayList();
		updateDynamicPanel();
		//createPopupMenu();
		
		
		
		
		//if (P5Canvas.isEnable)
		//	playBtn.doClick();
	/*	if (playBtn!=null && centerPanel!=null){
			volumeSlider.requestFocus();
			volumeSlider.lostFocus(null, null);
			volumeSlider.enable(P5Canvas.yaml.getControlVolumeSliderState(selectedUnit, selectedSim));
				
			zoomSlider.requestFocus();
			zoomSlider.lostFocus(null, null);
			zoomSlider.enable(P5Canvas.yaml.getControlScaleSliderState(selectedUnit, selectedSim));
			speedSlider.requestFocus();
			speedSlider.lostFocus(null, null);
			speedSlider.enable(P5Canvas.yaml.getControlSpeedSliderState(selectedUnit, selectedSim));
			heatSlider.requestFocus();
			heatSlider.lostFocus(null,null);
			heatSlider.enable(P5Canvas.yaml.getControlHeatSliderState(selectedUnit, selectedSim));
				
			float heatMin =P5Canvas.yaml.getControlHeatSliderMin(selectedUnit, selectedSim);
			float heatMax = P5Canvas.yaml.getControlHeatSliderMax(selectedUnit, selectedSim);
			float heatInit =P5Canvas.yaml.getControlHeatSliderInit(selectedUnit, selectedSim);
			heatSlider.setMaximum((int) heatMax);
			heatSlider.setMinimum((int) heatMin);
			heatSlider.setValue((int) heatInit);
			speedSlider.setValue(defaultSpeed);
			
			leftPanel.updateUI();
			centerPanel.updateUI();		
		}	*/
	} 
		
		
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenDimension = tk.getScreenSize();
		    
		mainFrame = new JFrame();
		mainFrame.setBounds(0, 0, 1280, 705);
		//mainFrame.setBounds(0, 0, screenDimension.width, screenDimension.height-100);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		mainFrame.setJMenuBar(menuBar);
		p5Canvas.setBackground(Color.WHITE);
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
												simMenu.getItem(selectedUnit-1).setBackground(Color.WHITE);
												((JMenuItem) (subMenuArrayList[selectedUnit-1].get(selectedSim-1))).setBackground(Color.WHITE) ;
											}
											selectedUnit = i+1;
											selectedSim = j+1;
											simMenu.setText("Unit "+selectedUnit+": "+getUnitName(selectedUnit)+
													", Sim "+selectedSim+": "+getSimName(selectedUnit, selectedSim));
											simMenu.getItem(i).setBackground(selectedColor);
											((JMenuItem) (subMenuArrayList[i].get(j))).setBackground(selectedColor) ;
										
										}	
									}	
								}
								TableSet.updataSet();
								TableSet.setSelectedRow(0);
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
	
		Component headHStrut = Box.createHorizontalStrut(20);
		menuBar.add(headHStrut);

		// Get All molecules from Folder
		try {
			moleculeNames =parseNames(getResourceListing(Main.class, "resources/compoundsPng50/"));
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		final JButton moleculeChooserBtn = new JButton("");
		createPopupMenu();
		moleculeChooserBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				  scrollablePopupMenu.show(moleculeChooserBtn, -160,37,mainFrame.getHeight()-39);
			}
		});
			//moleculeChooserBtn.setEnabled(false);
			moleculeChooserBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png24x24/iconCompound.png")));
			menuBar.add(moleculeChooserBtn);
		
		
		 
		
		JButton periodicTableBtn = new JButton("\n");
		periodicTableBtn.setEnabled(false);
		periodicTableBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png24x24/iconPeriodicTable.png")));
		menuBar.add(periodicTableBtn);
		mainFrame.getContentPane().setLayout(new MigLayout("insets 0, gap 0", "[263.00][480px,grow][300px]", "[grow]"));

		
		
		//*********************************** LEFT PANEL ********************************************
		leftPanel = new JPanel();
		mainFrame.getContentPane().add(leftPanel, "cell 0 0,grow");
		leftPanel.setLayout(new MigLayout("insets 6, gap 18", "14[grow]", "[][][grow]"));

		JPanel timerSubpanel = new JPanel();
		//timerSubpanel.setBackground(new Color(211, 211, 211));
		leftPanel.add(timerSubpanel, "cell 0 0,grow");
		timerSubpanel.setLayout(new MigLayout("insets 3, gap 4", "[65.00][100.00][100px][50.00][grow]", "[12.00,grow][140px][]"));
		
		playBtn = new JButton("");
		playBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (P5Canvas.isEnable){
					P5Canvas.isEnable = false;
					playBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png48x48/iconPlay.png")));
					
				}	
				else{ 
					playBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png48x48/iconPause.png")));
					P5Canvas.isEnable = true; 
				}	
			}
		});
		
		if (P5Canvas.isEnable)
			playBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png48x48/iconPause.png")));
		else
			playBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png48x48/iconPlay.png")));
		
		timerSubpanel.add(playBtn, "cell 2 2 1 1,alignx right");

		
		JButton resetBtn = new JButton("");
		resetBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png48x48/iconReset.png")));
		resetBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		timerSubpanel.add(resetBtn, "cell 3 2 1 1,alignx left");
		
		
		tableSet = new TableSet();
		timerSubpanel.add(tableSet, "cell 0 1 5 1,growx");
		
	
		
		
		
		//***************** Add elements Control panel
		final String controlCompoundName_1 = "Water";
		final String controlCompoundName_2 = "Hydrochloric Acid";
		final String controlCompoundName_3 = "Hydronium";
		final String controlCompoundName_4 = "Methylammonium";
		final String controlCompoundName_5 = "Phenylpthalein";
		
		dynamicScrollPane = new JScrollPane();
		leftPanel.add(dynamicScrollPane, "cell 0 1,grow");
		
		dynamicPanel = new JPanel();
		dynamicScrollPane.setViewportView(dynamicPanel);
		dynamicPanel.setLayout(new MigLayout("insets 4", "[124.00,grow]", "[][]"));
		
		
		//****************************************** CENTER PANEL *****************************************************
		centerPanel = new JPanel();
		centerPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int volume = (int) p5Canvas.getSize().height/P5Canvas.multiplierVolume;
				p5Canvas.updateSize(p5Canvas.getSize(), volume);
				volumeLabel.setText(volume+" mL");
			}
		});
		mainFrame.getContentPane().add(centerPanel, "cell 1 0,grow");
		// leftPanel Width=282 		rightPanel Width =255  
		int wCenter = screenDimension.width - 282 -300 -50; 
		centerPanel.setLayout(new MigLayout("insets 0, gap 2", "[][604.00px][]", "[700px][center]"));

		// Add P5Canvas 
		centerPanel.add(p5Canvas, "cell 1 0,grow");
		
		
		
		JPanel clPanel = new JPanel();
		clPanel.setLayout(new MigLayout("insets 0, gap 0", "[]", "[][210.00][][40.00][][210.00][]"));
		
		volumeSlider.setOrientation(SwingConstants.VERTICAL);
		volumeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!isVolumeblocked){
					int value = ((JSlider) e.getSource()).getValue(); 
					p5Canvas.setVolume(value);
					int volume = (int) p5Canvas.getSize().height/P5Canvas.multiplierVolume;
					volumeLabel.setText((volume+P5Canvas.currenttVolume-P5Canvas.defaultVolume)+" mL");
				}
			}
		});
	    clPanel.add(volumeLabel, "flowy,cell 0 0,alignx right");
		clPanel.add(volumeSlider, "cell 0 1,alignx right,growy");
		JLabel canvasControlLabel_main_volume = new JLabel("   Volume");
		clPanel.add(canvasControlLabel_main_volume, "cell 0 2,alignx center");

		
		JLabel l2 = new JLabel(" ");
		clPanel.add(l2, "cell 0 3,alignx center");
		
		final JLabel scaleLabel = new JLabel(defaultZoom*2+"%");
		clPanel.add(scaleLabel, "cell 0 4,alignx right");
		zoomSlider = new JSlider(10,100,defaultZoom);
		zoomSlider.setOrientation(SwingConstants.VERTICAL);
		zoomSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = ((JSlider) e.getSource()).getValue(); 
				scaleLabel.setText(value*2+"%");
				p5Canvas.setScale(value,defaultZoom);
			
			}
		});
		clPanel.add(zoomSlider, "cell 0 5,alignx right,growy");
		JLabel canvasControlLabel_main_scale = new JLabel("Zoom");
		clPanel.add(canvasControlLabel_main_scale, "cell 0 6,alignx right");
		
		centerPanel.add(clPanel,"cell 0 0");
		
	
		
		//Center bottom
		
		JPanel cbPanel = new JPanel();
		cbPanel.setLayout(new MigLayout("insets 0, gap 0", "[]", "[][210.00][][40.00][][210.00][]"));
		
		
		JLabel canvasControlLabel_main_speed = new JLabel("Speed");
		final JLabel speedLabel = new JLabel("1x");
		cbPanel.add(speedLabel, "cell 0 0,alignx left");
		speedSlider =  new JSlider(1,100,defaultSpeed);
		speedSlider.setOrientation(SwingConstants.VERTICAL);
		speedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = ((JSlider) e.getSource()).getValue(); 
				float speedRate = (float) value/defaultSpeed;
				if (value>defaultSpeed)
					speedRate =1+2*(speedRate-1);
				p5Canvas.setSpeed(speedRate);
				
				DecimalFormat df = new DecimalFormat("#.#");

				speedLabel.setText(df.format(speedRate)+"x");
			}
		});
		cbPanel.add(speedSlider, "cell 0 1,alignx left,growy");
		cbPanel.add(canvasControlLabel_main_speed, "cell 0 2");
		cbPanel.add(new JLabel("    "), "cell 0 3,alignx center");
		
		
		
		JLabel canvasControlLabel_main_heat = new JLabel("Heat");
		final JLabel heatLabel = new JLabel(heatInit+"\u2103");
		cbPanel.add(heatLabel, "cell 0 4,alignx left");
		p5Canvas.setHeat(heatInit);
		heatSlider.setOrientation(SwingConstants.VERTICAL);
		heatSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = ((JSlider) e.getSource()).getValue(); 
				p5Canvas.setHeat(value);
				heatLabel.setText(value+"\u2103");
			}
		});
		cbPanel.add(heatSlider, "cell 0 5,alignx left,growy");
		cbPanel.add(canvasControlLabel_main_heat, "cell 0 6");
		
		centerPanel.add(cbPanel,"cell 2 0");
		
		
		
		//***************************************** RIGHT PANEL *******************************************
		rightPanel = new JPanel();
		mainFrame.getContentPane().add(rightPanel, "cell 2 0,grow");
		rightPanel.setLayout(new MigLayout("insets 0, gap 0", "[297.00]", "[380.00,grow][grow]"));

		JTabbedPane graphTabs = new JTabbedPane(JTabbedPane.TOP);
		rightPanel.add(graphTabs, "cell 0 0,grow");

		JPanel graphSet_1 = new JPanel();
		graphTabs.addTab("Compounds", null, graphSet_1, null);
		graphSet_1.setLayout(new MigLayout("insets 0, gap 0", "[150:n,grow][]", "[178.00:n][grow]"));
		graphSet_1.add(canvas, "cell 0 0,grow");

		JButton graphPopoutBtn_1 = new JButton("");
		graphPopoutBtn_1.setEnabled(false);
		graphPopoutBtn_1.setIcon(new ImageIcon(Main.class.getResource("/resources/png24x24/iconZoom.png")));
		graphSet_1.add(graphPopoutBtn_1, "cell 1 0,aligny top");

		tableView = new TableView();
		graphSet_1.add(tableView, "cell 0 1,grow");
		
		JPanel graphSet_2 = new JPanel();
		graphTabs.addTab("pH", null, graphSet_2, null);

		JPanel dashboard = new JPanel();
		rightPanel.add(dashboard, "cell 0 1,alignx center,growy");
		dashboard.setLayout(new MigLayout("", "[][100]", "[][][][]"));

		JLabel elapsedTimeLabel = new JLabel("Elapsed Set Time:");
		dashboard.add(elapsedTimeLabel, "flowx,cell 0 0,alignx right");

		elapsedTime = new JLabel("00");
		elapsedTime.setForeground(new Color(0, 128, 0));
		elapsedTime.setFont(new Font("Digital", Font.PLAIN, 30));
		dashboard.add(elapsedTime, "cell 1 0");

		
		JLabel totalSystemEnergyLabel = new JLabel("Total System Energy:");
		dashboard.add(totalSystemEnergyLabel, "cell 0 1,alignx right");
		totalSystemEnergy= new JLabel("0 kJ");
		dashboard.add(totalSystemEnergy, "cell 1 1");

		JLabel averageSystemEnergyLabel = new JLabel("Average Molecule Energy:");
		dashboard.add(averageSystemEnergyLabel, "cell 0 2,alignx right");
		averageSystemEnergy= new JLabel("a kJ");
		dashboard.add(averageSystemEnergy, "cell 1 2");

/*		JLabel totalSystemPressureLabel = new JLabel("Total System Pressure:");
		dashboard.add(totalSystemPressureLabel, "cell 0 3,alignx right");

		JLabel totalSystemPressureOutput = new JLabel("100 kPa");
		dashboard.add(totalSystemPressureOutput, "cell 1 3");
*/
		if (isWelcomed){
			welcomePanel = new JPanel();
			welcomePanel.setLayout(new MigLayout("insets 10, gap 10", "[][]", "[100px][]"));
	
			JLabel label1 = new JLabel(" Welcome to The Connected Chemistry Curriculum");
			label1.setFont(new Font("Serif", Font.BOLD, 55));
			label1.setForeground(Color.blue);
			welcomePanel.add(label1, "cell 1 1,alignx center");
	    
			JLabel label2 = new JLabel("Please select a simuation on the top left coner");
			label2.setFont(new Font("Serif", Font.PLAIN, 33));
			//label2.setForeground(Color.RED);
			welcomePanel.add(label2, "cell 1 2,alignx center");
		    
			JLabel label3= new JLabel("");
			label3.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/cccLogo.png")));
			welcomePanel.add(label3, "cell 1 3,alignx center");
		    
			
			
			mainFrame.remove(leftPanel);
			mainFrame.remove(centerPanel);
			mainFrame.remove(rightPanel);
			mainFrame.getContentPane().add(welcomePanel, "cell 1 0,grow");
		}
	}

}
