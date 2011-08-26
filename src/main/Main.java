package main;

//Connected Chemistry Simulations
//part of the Connected Chemistry Curriculum

//Project Leader: Mike Stieff, PhD, University of Illinois at Chicago
//Modeled in Processing by: Tuan Dang, Qin Li and Allan Berry

//This software is Copyright � 2010, 2011 University of Illinois at Chicago,
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

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.Timer;

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
import view.Compound;
import view.Unit2;

import java.awt.BorderLayout;
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
	public static int selectedUnit=2;
	public static int selectedSim=4;
	public static int selectedSet=1;
	public static boolean isWelcomed=true;
	public static Color selectedColor = new Color(200,200,150);
	public static Color defaultColor = Color.LIGHT_GRAY;
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
	
	public static JPanel dashboard;     //Dashboard on right panel showing mass and volume
	

	private static String sliderLabel = new String("Add ");	//Label parameter on left panel
	
	public static JPanel leftPanel;     //"Input" panel on left of application
	public static JPanel centerPanel;   //"Simulation" panel in the middle of application
	public static JPanel welcomePanel;  //"Welcome" Panel showing welcome info when application is first opened up
	public static Canvas canvas = new Canvas();
	public static TableView tableView;
	public static TableSet tableSet;
	private JMenuBar menuBar;
	
	/*******  Left Panel parameters  *******/
	private JLabel lblInput;
	private static JLabel lblInputTipR;
	private static JLabel lblInputTipL;
	
	
	
	private static JPanel clPanel;     //Center Left control Panel containing volume slider and Zoom Slider
	public static final JLabel  volumeLabel = new JLabel(P5Canvas.currenttVolume+"mL");
	public static JSlider volumeSlider = new JSlider(0, 100, P5Canvas.currenttVolume);
	private static JLabel canvasControlLabel_main_volume;
	//Pressure slider used to replace Volume Slider in Unit 2
	public static JSlider pressureSlider = new JSlider(0,10,1);
	public static JLabel pressureLabel;
	private static JLabel canvasControlLabel_main_pressure;
	public static int defaultPressure = 1;
	
	public static int defaultZoom =50;
	public static JSlider zoomSlider = new JSlider(0, 100, defaultZoom);
	public static int defaultSpeed =100;
	public static JSlider speedSlider = new JSlider(0, 100, defaultSpeed);
	public static int heatInit =25;
	public static int heatMin =-10;
	public static int heatMax =200;	
	public static JSlider heatSlider = new JSlider(heatMin, heatMax, heatInit);
	

	//private static boolean isPressureShowing;
	
	public static boolean isVolumeblocked = false;
	public static JLabel totalSystemEnergy;
	public static JLabel averageSystemEnergy;
	
	public static JLabel elapsedTime;   //"Elapsed Set Time" label
	public static JLabel m1Mass;        
	public static JLabel m1Disolved;    //"Dissolved" label showing how much solute has dissolved
	public static JLabel satMass;       //
	public static JLabel waterVolume;
	public static JLabel m1Label;
	public static JLabel m1MassLabel;
	public static JLabel solventLabel;
	public static JLabel satLabel;
	public static JLabel solutionLabel;
	public static JLabel soluteVolume;
	public static JCheckBox cBoxConvert;
	
	public static JButton playBtn;
	public static boolean isFirst =true; 
	
	public int pause = 0;   // the length of the pause at the begginning
	public int speed = 1000;  // recur every second.
	public Timer timer;
	public static int time = 0;
	
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
	

	/******************************************************************
	* FUNCTION :     updateDynamicPanel
	* DESCRIPTION :  Update molecule legends on the left panel. Called when reset.
	*
	* INPUTS :       None
	* OUTPUTS:       None
	*******************************************************************/
	public static void updateDynamicPanel(){
		if (dynamicPanel!=null){
			dynamicPanel.removeAll();
			defaultSetMolecules =  new ArrayList();

			//Get Compounds information in selected set from Yaml file
			ArrayList compounds= getSetCompounds(selectedUnit,selectedSim,selectedSet);
			if (compounds!=null){
				for (int i=0;i<compounds.size();i++){
					JPanel panel = new JPanel();
					panel.setBackground(Color.LIGHT_GRAY);
					panel.setLayout(new MigLayout("insets 6, gap 0", "[][][69.00]", "[][]"));
					dynamicPanel.add(panel, "cell 0 "+i+",grow");
					additionalPanelList.add(panel);
					
					//Get Compound Name 
					String cName =  getCompoundName(selectedUnit,selectedSim,selectedSet,i);
					defaultSetMolecules.add(cName);
					JLabel label = new JLabel(cName);
					final String fixedName = cName.replace(" ", "-");
					
					//Repaint molecules icon
					label.setIcon(new ImageIcon(Main.class.getResource("/resources/compoundsPng50/"+fixedName+".png")));
					panel.add(label, "cell 0 0 3 1,growx");
					
					//Repaint slider label
					final JLabel label_1 = new JLabel(sliderLabel+sliderValue);
					panel.add(label_1, "cell 0 1");
				
					//Repaint slider and set up slider event listener
					JSlider slider = new JSlider(minSliderValue,maxSliderValue,sliderValue);
					panel.add(slider, "cell 1 1");
					slider.addChangeListener(new ChangeListener() {
						public void stateChanged(ChangeEvent e) {
						int value = ((JSlider) e.getSource()).getValue(); 
						label_1.setText(sliderLabel+value);
						}
					});
					
					//Repaint Add buttion and set up button event listener
					JButton button_1 = new JButton("");
					button_1.setIcon(new ImageIcon(Main.class.getResource("/resources/png16x16/plus.png")));
					panel.add(button_1, "cell 2 1,growy");
					button_1.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent arg0) {
							//Get number showing on slider bar
							int count = Integer.parseInt(label_1.getText().substring(sliderLabel.length()));
							//Check if molecule number is going over predefined cap number
							//If yes, add molecules no more than cap number
							int cap = p5Canvas.getMoleculesCap(fixedName);
							int curNum = p5Canvas.getMoleculesNum(fixedName);
							if(cap<=(count+curNum))
							{
								count = cap - curNum;
								//Disable Add button
								arg0.getComponent().setEnabled(false);
							}
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
		boolean temp = P5Canvas.isEnable;
		P5Canvas.isEnable =false;
		if (isWelcomed && welcomePanel !=null){
			mainFrame.remove(welcomePanel);
			mainFrame.getContentPane().add(leftPanel, "cell 0 0,grow");
			mainFrame.getContentPane().add(centerPanel, "cell 1 0,grow");
			mainFrame.getContentPane().add(rightPanel, "cell 2 0,grow");
			isWelcomed = false;
		}
		
		p5Canvas.removeAllMolecules();
		//P5Canvas.count=0;
		P5Canvas.curTime=0;
		P5Canvas.oldTime=0;
		
		//Reset dashboard on right panel
		dashboard.removeAll();
		JLabel elapsedTimeLabel = new JLabel("Elapsed Set Time:");
		dashboard.add(elapsedTimeLabel, "flowx,cell 0 0,alignx right");
		dashboard.add(elapsedTime, "cell 1 0");
		if (selectedUnit==2){
			dashboard.add(cBoxConvert, "cell 0 1");
			dashboard.add(m1Label, "cell 0 2,alignx right");
			dashboard.add(m1Mass, "cell 1 2");
			dashboard.add(m1MassLabel, "cell 0 3,alignx right");
			dashboard.add(m1Disolved, "cell 1 3");
			//dashboard.add(satLabel, "cell 0 3,alignx right");
			//dashboard.add(satMass, "cell 1 3");
			dashboard.add(solventLabel, "cell 0 4,alignx right");
			dashboard.add(waterVolume, "cell 1 4");
			dashboard.add(solutionLabel, "cell 0 5,alignx right");
			dashboard.add(soluteVolume, "cell 1 5");
			
	
			
		}
			
		Unit2.reset();
		ArrayList a = getSetCompounds(selectedUnit,selectedSim,selectedSet);
		if (a!=null) {
			Compound.names = new ArrayList<String>();
			Compound.counts = new ArrayList<Integer>();
			Compound.caps = new ArrayList<Integer>();
			for (int i=0; i<a.size();i++){
				String s = (String) getCompoundName(selectedUnit,selectedSim,selectedSet,i);
				int num = Integer.parseInt(getCompoundQty(selectedUnit,selectedSim,selectedSet,i).toString());
				int cap = Integer.parseInt(getCompoundCap(selectedUnit,selectedSim,selectedSet,i).toString());
				s =s.replace(" ","-");
				Compound.names.add(s);
				Compound.counts.add(num);
				Compound.caps.add(cap);
				p5Canvas.addMoleculeRandomly(s,num);
			}
			if (selectedUnit==1){
				if (selectedSim==4){
					Compound.names.add("Water");
					Compound.counts.add(0);
					Compound.names.add("Oxygen");
					Compound.counts.add(0);
				}
			}
				
			else if (selectedUnit==2){
				if (selectedSet==1 && selectedSim<4){
					Compound.names.add("Sodium-Ion");
					Compound.counts.add(0);
					Compound.names.add("Chlorine-Ion");
					Compound.counts.add(0);
				}
				else if (selectedSet==4){
					Compound.names.add("Calcium-Ion");
					Compound.counts.add(0);
					Compound.names.add("Chlorine-Ion");
					Compound.counts.add(0);
				}
				else if (selectedSet==7){
					Compound.names.add("Sodium-Ion");
					Compound.counts.add(0);
					Compound.names.add("Bicarbonate");
					Compound.counts.add(0);
				}
				else if (selectedSet==1 && selectedSim==4){
					Compound.names.add("Potassium-Ion");
					Compound.counts.add(0);
					Compound.names.add("Chlorine-Ion");
					Compound.counts.add(0);
				}
			}	
			Compound.setProperties();
		}
		canvas.reset();
		TableView.setSelectedRow(-1);
		
		//For UNIT 2, Sim 3, ALL SETS, add input tip below Input title
		if( selectedUnit==2 && selectedSim==3)
		{
			leftPanel.add(lblInputTipL,"cell 0 1,gaptop 5,alignx left,width 45::");
			leftPanel.add(lblInputTipR,"cell 0 1,gaptop 5,alignx right");
		}
		else
		{
			if(leftPanel.isAncestorOf(lblInputTipL))
			{
				leftPanel.remove(lblInputTipL);
				leftPanel.remove(lblInputTipR);
			}
			
		}
		
		//Update Dynamic Panel
		updateDynamicPanel();
		
		//Update components on the left panel
		updateCenterPanel();
		
		
	
		P5Canvas.isEnable =temp;
		
		//reset timer
		resetTimer();
		
	} 
	
	/******************************************************************
	* FUNCTION :     updateCenterPanel()
	* DESCRIPTION :  Update sliders on the center panel
	*                Only be called in reset()
	*
	* INPUTS :       None
	* OUTPUTS:       None
	*******************************************************************/
	private static void updateCenterPanel()
	{
		if (playBtn!=null && centerPanel!=null){


			if( selectedUnit ==2)
			{
				//In Unit 2, we want to show pressure slider instead of volume Slider
				//In other Units, we want to show volume Slider only
				if(clPanel.isAncestorOf(volumeSlider))
				{
					clPanel.remove(volumeSlider);
					clPanel.remove(volumeLabel);
					clPanel.remove(canvasControlLabel_main_volume);
				}

				if(!clPanel.isAncestorOf(pressureSlider))
				{
					clPanel.add(pressureLabel, "cell 0 0,alignx right");
					clPanel.add(pressureSlider, "cell 0 1,alignx right");
					clPanel.add(canvasControlLabel_main_pressure, "cell 0 2, alignx center");
				}				

				pressureSlider.setVisible(true);
				pressureLabel.setVisible(true);
				canvasControlLabel_main_pressure.setVisible(true);
			
			pressureSlider.requestFocus();
			pressureSlider.lostFocus(null, null);
			pressureSlider.enable(P5Canvas.yaml.getControlPressureSliderState(selectedUnit, selectedSim));
			}
			else
			{
				if(clPanel.isAncestorOf(pressureSlider))
				{
					clPanel.remove(pressureLabel);
					clPanel.remove(pressureSlider);					
					clPanel.remove(canvasControlLabel_main_pressure);
				}
								
				if( !clPanel.isAncestorOf(volumeSlider))
				{
					//If pressure slider showing ,remove it
					clPanel.add(volumeLabel, "cell 0 0,alignx right");
					clPanel.add(volumeSlider, "cell 0 1,alignx right");					
					clPanel.add(canvasControlLabel_main_volume, "cell 0 2, alignx center");
				}

				volumeSlider.setVisible(true);
				volumeLabel.setVisible(true);
				canvasControlLabel_main_volume.setVisible(true);
				volumeSlider.requestFocus();
				volumeSlider.lostFocus(null, null);
				volumeSlider.enable(P5Canvas.yaml.getControlVolumeSliderState(selectedUnit, selectedSim));
			}
				
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
		}
	
	}
	

	/******************************************************************
	* FUNCTION :     resetTimer
	* DESCRIPTION :  Rest Timer, Only be called in reset()
	*
	* INPUTS :       None
	* OUTPUTS:       None
	*******************************************************************/
	public static void resetTimer()
	{
		time = 0 ;
	}
		
		
	/******************************************************************
	* FUNCTION :     initialize()
	* DESCRIPTION :  Initialize all swing components when program starts
	*
	* INPUTS :       None
	* OUTPUTS:       None
	*******************************************************************/
	private void initialize() {
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenDimension = tk.getScreenSize();
		    
		mainFrame = new JFrame();
		mainFrame.setBounds(0, 0, 1280, 700);
		//mainFrame.setBounds(0, 0, screenDimension.width, screenDimension.height-100);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		

		p5Canvas.setBackground(Color.WHITE);
		p5Canvas.init();
		
		//Set up Menu 
		initMenu();

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
				  scrollablePopupMenu.show(moleculeChooserBtn, -160,52,mainFrame.getHeight()-39);
			}
		});
			moleculeChooserBtn.setEnabled(false);
			moleculeChooserBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png24x24/iconCompound.png")));
			menuBar.add(moleculeChooserBtn);
		
		
		 
		
		JButton periodicTableBtn = new JButton("\n");
		periodicTableBtn.setEnabled(false);
		periodicTableBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png24x24/iconPeriodicTable.png")));
		menuBar.add(periodicTableBtn);
		mainFrame.getContentPane().setLayout(new MigLayout("insets 0, gap 0", "[285.00][480px,grow][320px]", "[][][grow]"));

		
		
		//*********************************** LEFT PANEL ********************************************
		leftPanel = new JPanel();
		mainFrame.getContentPane().add(leftPanel, "cell 0 2,grow");
		leftPanel.setLayout(new MigLayout("insets 6, gap 0", "[260]", "[][]20[215,top]18[][]"));
		
		//Add Input label and Initialize Input Tip label
		AddInputLabel();
		
		
		
		JPanel timerSubpanel = new JPanel();
		
		
		leftPanel.add(timerSubpanel, "cell 0 2,growx");
		timerSubpanel.setLayout(new MigLayout("insets 3, gap 4", "[110px][50px]", "[180px][grow]"));
		
		//Add Play button to timerSubpanel
		playBtn = new JButton("");
		playBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (P5Canvas.isEnable){ //playing, turning to PAUSE					
					//pause timer
					timer.stop();
					P5Canvas.isEnable = false;
					playBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png48x48/iconPlay.png")));
					
				}	
				else{ //Pausing, turning to PLAY
					playBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png48x48/iconPause.png")));
					P5Canvas.isEnable = true; 
					timer.start();
				}	
			}
		});
		
		if (P5Canvas.isEnable)
			playBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png48x48/iconPause.png")));
		else
			playBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png48x48/iconPlay.png")));
		
		timerSubpanel.add(playBtn, "cell 1 0, align center");
		
		//Add Reset button to timerSubpanel
		JButton resetBtn = new JButton("");
		resetBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png48x48/iconReset.png")));
		resetBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		timerSubpanel.add(resetBtn, "cell 1 0, align center");
		
		//Add Checkbox to checkBoxPanel
		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new BorderLayout());
		JCheckBox cBox1 =  new JCheckBox("Enable Molecule Hiding"); 
		cBox1.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					P5Canvas.isHidingEnabled =true;
				else if	(e.getStateChange() == ItemEvent.DESELECTED)
					P5Canvas.isHidingEnabled = false;
			}
		});
		JCheckBox cBox2 =  new JCheckBox("Display Forces"); 
		cBox2.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					P5Canvas.isDisplayForces =true;
				else if	(e.getStateChange() == ItemEvent.DESELECTED)
					P5Canvas.isDisplayForces = false;
			}
		});
		
		JCheckBox cBox3 =  new JCheckBox("Display Joints"); 
		cBox3.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					P5Canvas.isDisplayJoints =true;
				else if	(e.getStateChange() == ItemEvent.DESELECTED)
					P5Canvas.isDisplayJoints = false;
			}
		});
		checkBoxPanel.add(cBox1, BorderLayout.NORTH);
		checkBoxPanel.add(cBox2, BorderLayout.CENTER);
		//checkBoxPanel.add(cBox3, BorderLayout.SOUTH);
		timerSubpanel.add(checkBoxPanel, "cell 1 1");

		

		
		//Add Set Table to timerSubpanel
		tableSet = new TableSet();
		timerSubpanel.add(tableSet, "cell 0 0 1 2,growy");
		
		
		
		//**************************************** Add elements Control panel ************************************
		dynamicScrollPane = new JScrollPane();
		leftPanel.add(dynamicScrollPane, "cell 0 3,grow");
		
		dynamicPanel = new JPanel();
		dynamicScrollPane.setViewportView(dynamicPanel);
		dynamicPanel.setLayout(new MigLayout("insets 4", "[200.00,grow]", "[][]"));
		
		
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
		mainFrame.getContentPane().add(centerPanel, "cell 1 2,grow");
		// leftPanel Width=282 		rightPanel Width =255  
		centerPanel.setLayout(new MigLayout("insets 0, gap 2", "[][560.00px][]", "[690px][center]"));
		//centerPanel.setBorder((BorderFactory.createLineBorder(Color.BLACK)));
		// Add P5Canvas 
		centerPanel.add(p5Canvas, "cell 1 0,grow");
		
		
		
		clPanel = new JPanel();
		clPanel.setLayout(new MigLayout("insets 0, gap 0", "[]", "[][210.00][][40.00][][210.00][]"));
		
		//Set up Volume Slider
	
		//volumeSlider.setEnabled(false);		
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
	    //clPanel.add(volumeLabel, "flowy,cell 0 0,alignx right");
		//clPanel.add(volumeSlider, "cell 0 1,alignx right");
		canvasControlLabel_main_volume = new JLabel("Volume");		
		//clPanel.add(canvasControlLabel_main_volume, "cell 0 2,alignx center");

			
		//Set up Pressure Slide
		p5Canvas.setPressure(defaultPressure);
		pressureSlider.setOrientation(SwingConstants.VERTICAL);
		pressureLabel = new JLabel(defaultPressure+" atm");

		//Pressure is doing nothing, but we need event listener to change number on pressure label
		pressureSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
					int value = ((JSlider) e.getSource()).getValue(); 
					p5Canvas.setPressure(value);
					pressureLabel.setText(value+" atm");
				
			}
		});		
		//clPanel.add(pressureLabel, "flowy,cell 0 0,alignx right");
		//clPanel.add(pressureSlider, "cell 0 1,alignx left");
		canvasControlLabel_main_pressure = new JLabel("Pressure");
		//clPanel.add(canvasControlLabel_main_pressure, "cell 0 2, alignx center");
		
		//Set up Zoom Slider
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
		
		//Set up Speed slider
		JLabel canvasControlLabel_main_speed = new JLabel("Speed");
		final JLabel speedLabel = new JLabel("1x");
		cbPanel.add(speedLabel, "cell 0 0,alignx left");
		speedSlider =  new JSlider(0,100,defaultSpeed);
		speedSlider.setOrientation(SwingConstants.VERTICAL);
		speedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				float value = ((JSlider) e.getSource()).getValue(); 
				float speedRate = value/defaultSpeed;
				p5Canvas.setSpeed(speedRate);
				System.out.println("speedRate is "+speedRate);
				DecimalFormat df = new DecimalFormat("#.##");
				speedLabel.setText(df.format(speedRate)+"x");
			}
		});
		cbPanel.add(speedSlider, "cell 0 1,alignx left,growy");
		cbPanel.add(canvasControlLabel_main_speed, "cell 0 2");
		cbPanel.add(new JLabel("    "), "cell 0 3,alignx center");
		
		
		//Set up Heat Slider
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
		

		
		//After cbPanel has been set up, add it to CenterPanel
		centerPanel.add(cbPanel,"cell 2 0");
		
		
		
		//***************************************** RIGHT PANEL *******************************************
		rightPanel = new JPanel();
		mainFrame.getContentPane().add(rightPanel, "cell 2 2,grow");
		rightPanel.setLayout(new MigLayout("insets 0, gap 0", "[320.00,grow,center]", "[][][350.00,grow][][grow][grow]"));
		
		//Set up "Output" title
		JLabel lblOutput = new JLabel("Output");
		lblOutput.setLabelFor(rightPanel);
		lblOutput.setFont(new Font("Lucida Grande", Font.BOLD, 14));
		rightPanel.add(lblOutput, "cell 0 0");
		
		JLabel lblMacroscopid = new JLabel("Submicroscopic Level");
		lblMacroscopid.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		rightPanel.add(lblMacroscopid, "cell 0 1");

		//Set up Graph
		JTabbedPane graphTabs = new JTabbedPane(JTabbedPane.TOP);
		lblMacroscopid.setLabelFor(graphTabs);
		rightPanel.add(graphTabs, "cell 0 2,grow");

		JPanel graphSet_1 = new JPanel();
		graphTabs.addTab("Compounds", null, graphSet_1, null);
		graphSet_1.setLayout(new MigLayout("insets 0, gap 0", "[150:n,grow][]", "[235.00:n][grow]"));
		graphSet_1.add(canvas, "cell 0 0,grow");

		JButton graphPopoutBtn_1 = new JButton("");
		graphPopoutBtn_1.setEnabled(false);
		graphPopoutBtn_1.setIcon(new ImageIcon(Main.class.getResource("/resources/png24x24/iconZoom.png")));
		graphSet_1.add(graphPopoutBtn_1, "cell 1 0,aligny top");

		tableView = new TableView();
		graphSet_1.add(tableView, "cell 0 1,grow");
		
		JPanel graphSet_2 = new JPanel();
		
		JLabel lblOutputMacroscopicLevel = new JLabel("Macroscopic Level");
		lblOutputMacroscopicLevel.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		rightPanel.add(lblOutputMacroscopicLevel, "cell 0 3");
		//graphTabs.addTab("pH", null, graphSet_2, null); // this will get reactivated in Unit 8

		//Set up dashboard on Right Panel
		dashboard = new JPanel();
		lblOutputMacroscopicLevel.setLabelFor(dashboard);
		rightPanel.add(dashboard, "cell 0 4,grow");
		dashboard.setLayout(new MigLayout("", "[grow,right][100]", "[][][][][][]"));

		JLabel elapsedTimeLabel = new JLabel("Elapsed Set Time:");
		dashboard.add(elapsedTimeLabel, "flowx,cell 0 0,alignx right");

		elapsedTime = new JLabel("00");
		elapsedTime.setForeground(new Color(0, 128, 0));
		elapsedTime.setFont(new Font("Digital", Font.PLAIN, 28));
		dashboard.add(elapsedTime, "cell 1 0");

		
	/*	JLabel totalSystemEnergyLabel = new JLabel("Total System Energy:");
		dashboard.add(totalSystemEnergyLabel, "cell 0 1,alignx right");
		totalSystemEnergy= new JLabel("0 kJ");
		dashboard.add(totalSystemEnergy, "cell 1 1");

		JLabel averageSystemEnergyLabel = new JLabel("Average Molecule Energy:");
		dashboard.add(averageSystemEnergyLabel, "cell 0 2,alignx right");
		averageSystemEnergy= new JLabel("0 kJ");
		dashboard.add(averageSystemEnergy, "cell 1 2");*/
		
		
		m1Label = new JLabel("Compound Mass:");
		dashboard.add(m1Label, "cell 0 1,alignx right");
		m1Mass= new JLabel("0 g");
		dashboard.add(m1Mass, "cell 1 1");
		m1MassLabel = new JLabel("Dissolved:");
		dashboard.add(m1MassLabel, "cell 0 2,alignx right");
		m1Disolved= new JLabel("0 g");
		dashboard.add(m1Disolved, "cell 1 2");
		satLabel = new JLabel("Saturation:");
		dashboard.add(satLabel, "cell 0 3,alignx right");
		satMass= new JLabel("0 g");
		dashboard.add(satMass, "cell 1 3");
		
		solventLabel = new JLabel("Solvent Volume:");
		dashboard.add(solventLabel, "cell 0 4,alignx right");
		waterVolume= new JLabel("0 mL");
		dashboard.add(waterVolume, "cell 1 4");

		//Set up Solution Volume Label
		solutionLabel = new JLabel("Solution Volume:");
		dashboard.add(solutionLabel, "cell 0 5,alignx right");
		soluteVolume= new JLabel("0 mL");
		dashboard.add(soluteVolume, "cell 1 5");

		
				//Set up "Convert to Mass" Checkbox
				cBoxConvert =  new JCheckBox("Convert Mass to Moles"); 
				cBoxConvert.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED){
							P5Canvas.isConvertMol =true;
							//Change 'g' to 'mol' in Amount Added label
							P5Canvas.convertMassMol1();
							//Change 'g' to 'mol' in "Dissolved" label
							P5Canvas.convertMassMol2();
						}	
						else if	(e.getStateChange() == ItemEvent.DESELECTED){
							P5Canvas.isConvertMol = false;
							P5Canvas.convertMolMass1();
							P5Canvas.convertMolMass2();
						}	
					}
				});
				//dashboard.add(cBoxConvert, "cell 1 6");
				
				JPanel outputControls = new JPanel();
				rightPanel.add(outputControls, "cell 0 5,grow");
				outputControls.setLayout(new MigLayout("", "[]", "[]"));
				
				outputControls.add(cBoxConvert, "cell 0 0");

		
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
	    
			JLabel label2 = new JLabel("Please select a simuation from the menu at top left.");
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
		
		//Set up timer, start when users press PLAY button
	       timer = new Timer(speed, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//System.out.print(time+"  ");
					time++;
					canvas.repaint();
				}
			});
	        timer.setInitialDelay(pause);
	        

	}

	/******************************************************************
	* FUNCTION :     AddInputLabel()
	* DESCRIPTION :  Add Input tips label for particular simulation
	*                Called in Initialize()
	*
	* INPUTS :       None
	* OUTPUTS:       None
	*******************************************************************/
	private void AddInputLabel()
	{
		lblInput = new JLabel("Input");
		lblInput.setFont(new Font("Lucida Grande", Font.BOLD, 14));
		leftPanel.add(lblInput, "cell 0 0,alignx center");
		
		//Add Input Tip label to left panel
		lblInputTipL= new JLabel();
		lblInputTipL.setText("<html>step 1:<p><p>step 2:<p><P>step 3:<p><p>step 4:</html>");
		lblInputTipL.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		
		lblInputTipR = new JLabel();
		lblInputTipR.setText("<html>Select the amount of desired solute, and press \"add.\"" +
				"<p>Select the amount of desired water, and press \"add.\"" +
				"<p>Set the temperature slider to the desired temperature." +
				"<p> Press play.</html>");
		lblInputTipR.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		

	}
	
	/******************************************************************
	* FUNCTION :     initMenu()
	* DESCRIPTION :  Initialize Menu Bar when application starts
	*                Called in Initialize()
	*
	* INPUTS :       None
	* OUTPUTS:       None
	*******************************************************************/
	private void initMenu()
	{
		menuBar = new JMenuBar();
		mainFrame.setJMenuBar(menuBar);
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
		
	}
}
