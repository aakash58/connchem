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
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;

import java.awt.Component;
import javax.swing.Box;

import model.DBinterface;
import model.State;
import model.YAMLinterface;
import net.miginfocom.swing.MigLayout;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;

import javax.swing.JSlider;
import javax.swing.SwingConstants;

import simulations.P5Canvas;
import simulations.Unit2;
import simulations.models.Compound;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.Insets;
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

import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
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
	private P5Canvas p5Canvas;
	//private Unit2 unit2 = new Unit2();
	// TODO flag
	public JFrame mainFrame;
	public JMenu simMenu = new JMenu("Choose Simulation");
	public int selectedUnit=2;
	public int selectedSim=4;
	public int selectedSet=1;
	public boolean isWelcomed=true;
	public Color selectedColor = new Color(200,200,150);
	public Color defaultColor = Color.LIGHT_GRAY;
	private int sliderValue = 5;
	
	private int minSliderValue = 1;
	private int maxSliderValue = 9;
	public JPanel dynamicPanel;
	public JScrollPane dynamicScrollPane; 
	public ArrayList additionalPanelList =  new ArrayList();
	public ArrayList defaultSetMolecules =  new ArrayList();
	private CustomPopupMenu scrollablePopupMenu;
	public String[] moleculeNames = null;



	private String sliderLabel = new String("Add ");	//Label parameter on left panel
	
	public JPanel leftPanel;     //"Input" panel on left of application
	public JPanel centerPanel;   //"Simulation" panel in the middle of application
	public JPanel welcomePanel;  //"Welcome" Panel showing welcome info when application is first opened up
	private Canvas canvas ;
	private TableView tableView;
	private TableSet tableSet;
	private JMenuBar menuBar;
	
	/*******  Left Panel parameters  *******/
	private JLabel lblInput;
	private JLabel lblInputTipR;
	private JLabel lblInputTipL;
	
	
	
	private JPanel clPanel;     //Center Left control Panel containing volume slider and Zoom Slider
	public JLabel  volumeLabel  = null;
	public JSlider volumeSlider  = null;
	public int defaultVolume = 63;
	private JLabel canvasControlLabel_main_volume;
	
	//Pressure slider used to replace Volume Slider in Unit 2
	public JSlider pressureSlider = new JSlider(0,10,1);
	public JLabel pressureLabel;
	private JLabel canvasControlLabel_main_pressure;
	public int defaultPressure = 1;
	
	public int defaultZoom =50;
	public JSlider zoomSlider = new JSlider(0, 100, defaultZoom);
	public JLabel scaleLabel = null;
	public int defaultSpeed =100;
	public JSlider speedSlider = new JSlider(0, 100, defaultSpeed);
	public int heatInit =25;
	public int heatMin =-10;
	public int heatMax =200;	
	public JSlider heatSlider = new JSlider(heatMin, heatMax, heatInit);
	

	//private static boolean isPressureShowing;
	/*********** Right Panel Parameter***********/
	public JPanel rightPanel;  //Right panel container
	JLabel lblOutput;          //output label
	JLabel lblMacroscopid;
	JLabel lblOutputMacroscopicLevel;
	JCheckBox cBoxHideWater;
	ItemListener cBoxHideWaterListener;
	public JPanel dashboard;     //Dashboard on right panel showing mass and volume
	
	
	public boolean isVolumeblocked = false;
	public JLabel totalSystemEnergy;
	public JLabel averageSystemEnergy;
	
	public JLabel elapsedTime;   //"Elapsed Set Time" label
	public JLabel m1Mass;        
	public JLabel m1Disolved;    //"Dissolved" label showing how much solute has dissolved
	public JLabel satMass;       //
	public JLabel waterVolume;
	public JLabel m1Label;
	public JLabel m1MassLabel;
	public JLabel solventLabel;
	public JLabel satLabel;
	public JLabel solutionLabel;
	public JLabel soluteVolume;
	public JCheckBox cBoxConvert;
	
	public JButton playBtn;
	private ActionListener playBtnListener;
	public boolean isFirst =true; 
	public JButton resetBtn;
	private ActionListener resetBtnListener;
	
	
	public int pause = 0;   // the length of the pause at the begginning
	public int speed = 1000;  // recur every second.
	public Timer timer;
	public static int time = 0;
	
	private MouseListener mulBtnListener;  //mouseListener used for 6 buttons in Unit3 Sim1
	private ArrayList<Integer> btnIds = new ArrayList<Integer>(); //Button Ids in Unit3 Sim2
	private ArrayList<String> btnNames = new ArrayList<String>(); //Button names in Unit3 Sim2
	//private int btnStartId =-1;
	private boolean started = false; //boolean flag for unit 3 sim 2
	
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
		setP5Canvas(new P5Canvas(this));
		setCanvas(new Canvas(this));
		setTableView(new TableView(this));

		setTableSet(new TableSet(this));
		scrollablePopupMenu = new CustomPopupMenu (this);
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
	
	
	public void removeAdditionalMolecule(int additionalIndex){
		int pos = defaultSetMolecules.size()+additionalIndex;
		dynamicPanel.removeAll();
		additionalPanelList.remove(pos);
		for (int i=0; i<additionalPanelList.size();i++){
			JPanel p = (JPanel) additionalPanelList.get(i);
			dynamicPanel.add(p, "cell 0 "+i+",grow");
			
		}
	}
		
	/******************************************************************
	* FUNCTION :     addAdditionalMolecule
	* DESCRIPTION :  Molecule Add Function for customized button. 
	*                Disable for now.
	*
	* INPUTS :       None
	* OUTPUTS:       None
	*******************************************************************/
	public void addAdditionalMolecule(){
		//Default unit setting
		JPanel panel = new JPanel();
		panel.setBackground(this.selectedColor);
		panel.setLayout(new MigLayout("insets 6, gap 0", "[][][69.00]", "[][]"));
		
		//new molecule is at the end of additionalList
		int newMolecule =  this.scrollablePopupMenu.additionalList.size()-1; 
		int pos = defaultSetMolecules.size() + newMolecule;
		dynamicPanel.add(panel, "cell 0 "+pos+",grow");
		additionalPanelList.add(panel);
		
		String cName = (String)  this.scrollablePopupMenu.additionalList.get(newMolecule);
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
				getP5Canvas().addMolecule(fixedName,count);
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
	public void updateDynamicPanel(){
		if (dynamicPanel!=null){
			dynamicPanel.removeAll();
			defaultSetMolecules =  new ArrayList();
			started = false;
			btnIds.clear();
			btnNames.clear();

			if(! (selectedUnit==3 &&selectedSim==2))
			{
			//Get Compounds information in selected set from Yaml file
			ArrayList compounds= getSetCompounds(selectedUnit,selectedSim,selectedSet);
			if (compounds!=null){
	
				dynamicPanel.setLayout(new MigLayout("insets 4", "[200.00,grow]", "[][]"));
				dynamicScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
							
							if(arg0.getComponent().isEnabled())
							{
								//Get number showing on slider bar
								int count = Integer.parseInt(label_1.getText().substring(sliderLabel.length()));
								//Check if molecule number is going over predefined cap number
								//If yes, add molecules no more than cap number
								int cap = getP5Canvas().getMoleculesCap(fixedName);
								//int curNum = getP5Canvas().getMoleculesNum(fixedName);
								
								if(cap<=(count+State.moleculesAdded))
								{
									count = cap - State.moleculesAdded;
									if(count<0)
										count =0;
									//Disable Add button
									if(getP5Canvas().addMolecule(fixedName,count))
									{
										if(!fixedName.equals("Water"))
											State.moleculesAdded += count;
										arg0.getComponent().setEnabled(false);
									}
								}
								else
								{
									if(getP5Canvas().addMolecule(fixedName,count))
									{
										if(!fixedName.equals("Water"))
										State.moleculesAdded += count;
									}
								}
									
							}
						}
					});
				
				}
			}
		}
			else //In unit3 sim 2 case
			{
				//Get Compounds information in set 1 from Yaml file
				ArrayList compounds= getSetCompounds(selectedUnit,selectedSim,selectedSet);
				if (compounds!=null){
					int rowNum = 4;
					int colNum = 2;
					dynamicPanel.setLayout(new MigLayout("insets 10,gap 0","[50]10[50]","[][]5[][]5[][]"));
					dynamicScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
					for (int i=0;i<compounds.size();i++){

						//Get Compound Name 
						String cName =  getCompoundName(selectedUnit,selectedSim,selectedSet,i);
						defaultSetMolecules.add(cName);
						if(cName.equals("Water"))
							continue;
						JLabel lblMolecule = new JLabel(cName);
						lblMolecule.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
						dynamicPanel.add(lblMolecule, "cell "+i%colNum +" "+ ((i/colNum)*2+1)+", center" );  //cell column row
						final String fixedName = cName.replace(" ", "-");
						//Draw Molecule button
						JButton btMolecule = new JButton();
						dynamicPanel.add(btMolecule, "cell "+i%colNum +" "+ ((i/colNum)*2)+", center, width 80:100:120, height 50: 55: 80");
						btMolecule.setIcon(new ImageIcon(Main.class.getResource("/resources/compoundsPng50/"+fixedName+".png")));
						//At the same time store fixed names into btnNames list
						this.btnNames.add(fixedName);
						btMolecule.addMouseListener(mulBtnListener);
			}
				}
			}
		}
	}
	
	
	public void createPopupMenu(){
		
		for (int i=0;i<moleculeNames.length;i++){
			CustomButton xx = new CustomButton(this, moleculeNames[i].replace("-", " "));
			xx.setIcon(new ImageIcon(Main.class.getResource("/resources/compoundsPng50/"+moleculeNames[i]+".png")));
			xx.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					//scrollablePopupMenu.hidemenu();
				}
			});
			scrollablePopupMenu.add(xx, i);
		}
	}
	
	public void reset(){
		//boolean temp = getP5Canvas().isEnable;
		
		//Disable p5Canvas and stop timer
		timer.stop();
		getP5Canvas().isEnable = false;
		playBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png48x48/iconPlay.png")));
		
		/*    Check if welcome menu showing    */
		if (isWelcomed && welcomePanel !=null){
			mainFrame.remove(welcomePanel);
			mainFrame.getContentPane().add(leftPanel, "cell 0 0,grow");
			mainFrame.getContentPane().add(centerPanel, "cell 1 0,grow");
			mainFrame.getContentPane().add(rightPanel, "cell 2 0,grow");
			isWelcomed = false;
		}
		
		//Update Molecule Legends on left panel
		updateDynamicPanel();
		
		//Update components on the left panel
		updateCenterPanel();
		
		//    Reset canvas   
		getP5Canvas().reset();
		//    Reset state parameter   
		State.reset();
		
		
		//    Reset right panel   
		updateRightPanel();
			

		//Load information of new generation
		ArrayList a = getSetCompounds(selectedUnit,selectedSim,selectedSet);
		if (a!=null) {
			Compound.names.clear();
			Compound.counts.clear();
			Compound.caps.clear();
			for (int i=0; i<a.size();i++){
				String s = (String) getCompoundName(selectedUnit,selectedSim,selectedSet,i);
				int num = Integer.parseInt(getCompoundQty(selectedUnit,selectedSim,selectedSet,i).toString());
				int cap = Integer.parseInt(getCompoundCap(selectedUnit,selectedSim,selectedSet,i).toString());
				s =s.replace(" ","-");
				Compound.names.add(s);
				Compound.counts.add(num);
				Compound.caps.add(cap);
				if( num >0)
				{
				//Add initial number of molecules into p5Canvas
					if( getP5Canvas().addMoleculeRandomly(s,num) )
					{
						State.moleculesAdded = num;
						//Need to add these molecules to canvas also
						
					}
				}
			}
			setupReactionProducts();
			Compound.setProperties();
		}
		getCanvas().reset();
		tableView.clearSelection();  //Deselect rows
		
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
		//getP5Canvas().isEnable =temp;
		
		//reset timer
		resetTimer();
	
	} 
	
	private void setupReactionProducts()
	{
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
		else 
		{
			ArrayList<String> products = new ArrayList<String>();
			products = DBinterface.getReactionOutputs(selectedUnit, selectedSim, selectedSet);
			if( products!=null)
			{
				for( String s:products)
				{
					Compound.names.add(s);
					Compound.counts.add(0);
				}
			}
		}
	}
	
	//Reset right panel
	private void updateRightPanel()
	{
		if( selectedUnit ==2)
		{
			rightPanel.remove(lblOutput);
			rightPanel.remove(cBoxHideWater);
			rightPanel.add(lblOutput, "cell 0 0");
			rightPanel.add(lblMacroscopid, "cell 0 1");
			rightPanel.add(lblOutputMacroscopicLevel, "cell 0 3");
		}
		else if (selectedUnit ==3)
		{
			//this.lblMacroscopid.setVisible(false);
			rightPanel.remove(lblOutputMacroscopicLevel);
			rightPanel.remove(lblOutput);
			rightPanel.remove(lblMacroscopid);
			rightPanel.add(lblOutput, "cell 0 1");
			if(( selectedSim==1 && (selectedSet==4||selectedSet==6||selectedSet==7||selectedSet==10))||selectedSim==2)
			rightPanel.add(cBoxHideWater, "cell 0 3");
			else
				rightPanel.remove(cBoxHideWater);
		}
		updateDashboard();  //Reset dashboard on right panel
	}
	
	//Reset dashboard on right panel
	private void updateDashboard()
	{
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
			cBoxConvert.setVisible(true);
			
		}
		else
		{
			cBoxConvert.setVisible(false);
		}
	}
	
	/******************************************************************
	* FUNCTION :     updateCenterPanel()
	* DESCRIPTION :  Update sliders on the center panel
	*                Only be called in reset()
	*
	* INPUTS :       None
	* OUTPUTS:       None
	*******************************************************************/
	private void updateCenterPanel()
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
			pressureSlider.enable(getP5Canvas().yaml.getControlPressureSliderState(selectedUnit, selectedSim));
			}
			else //Units setup except unit 2
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

			}
				
			//Reset zoomSlider
			zoomSlider.requestFocus();
			zoomSlider.setValue(defaultZoom);
			scaleLabel.setText(defaultZoom*2+"%");
			getP5Canvas().setScale(defaultZoom,defaultZoom);
			speedSlider.requestFocus();
			heatSlider.requestFocus();

				
			float heatMin =getP5Canvas().yaml.getControlHeatSliderMin(selectedUnit, selectedSim);
			float heatMax = getP5Canvas().yaml.getControlHeatSliderMax(selectedUnit, selectedSim);
			float heatInit =getP5Canvas().yaml.getControlHeatSliderInit(selectedUnit, selectedSim);
			heatSlider.setMaximum((int) heatMax);
			heatSlider.setMinimum((int) heatMin);
			heatSlider.setValue((int) heatInit);
			if( this.selectedUnit ==3)
			{
				heatSlider.setEnabled(false);
				volumeSlider.setEnabled(false);
			}
			//Reset animation speed
			speedSlider.setValue(defaultSpeed);
			float speedRate = defaultSpeed/defaultSpeed;
			getP5Canvas().setSpeed(speedRate);
			
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
		

		getP5Canvas().setBackground(Color.WHITE);
		getP5Canvas().init();
		
		//Set up Menu 
		initMenu();
		
        //Set up MouseListerns
        setupMouseListeners();
      //Set up Button Listerns
        setupComponentListener();

		// Get All molecules from Resources Folder
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
		playBtn.addActionListener(playBtnListener);
		
		if (getP5Canvas().isEnable)
			playBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png48x48/iconPause.png")));
		else
			playBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png48x48/iconPlay.png")));
		
		timerSubpanel.add(playBtn, "cell 1 0, align center");
		
		//Add Reset button to timerSubpanel
		resetBtn = new JButton("");
		resetBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png48x48/iconReset.png")));
		resetBtn.addActionListener(resetBtnListener);
		timerSubpanel.add(resetBtn, "cell 1 0, align center");
		
		//Add Checkbox to checkBoxPanel
		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new BorderLayout());
		JCheckBox cBox1 =  new JCheckBox("Enable Molecule Hiding"); 
		cBox1.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					getP5Canvas().isHidingEnabled =true;
				else if	(e.getStateChange() == ItemEvent.DESELECTED)
					getP5Canvas().isHidingEnabled = false;
			}
		});
		JCheckBox forceCheckbox =  new JCheckBox("Display Forces"); 
		forceCheckbox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					getP5Canvas().isDisplayForces =true;
				else if	(e.getStateChange() == ItemEvent.DESELECTED)
					getP5Canvas().isDisplayForces = false;
			}
		});
		
		JCheckBox jointsCheckbox =  new JCheckBox("Display Joints"); 
		jointsCheckbox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					getP5Canvas().isDisplayJoints =true;
				else if	(e.getStateChange() == ItemEvent.DESELECTED)
					getP5Canvas().isDisplayJoints = false;
			}
		});
		checkBoxPanel.add(cBox1, BorderLayout.NORTH);
		checkBoxPanel.add(forceCheckbox, BorderLayout.CENTER);
		checkBoxPanel.add(jointsCheckbox, BorderLayout.SOUTH);
		timerSubpanel.add(checkBoxPanel, "cell 1 1");

		

		
		
		timerSubpanel.add(getTableSet(), "cell 0 0 1 2,growy");
		
		
		
		//**************************************** Add elements Control panel ************************************
		dynamicScrollPane = new JScrollPane();
		leftPanel.add(dynamicScrollPane, "cell 0 3,grow");
		
		dynamicPanel = new JPanel();
		dynamicScrollPane.setViewportView(dynamicPanel);
		dynamicPanel.setLayout(new MigLayout("insets 4", "[200.00,grow]", "[][]"));
		
		
		//****************************************** CENTER PANEL *****************************************************
		centerPanel = new JPanel();
		volumeLabel = new JLabel(getP5Canvas().currenttVolume+"mL");
		volumeSlider = new JSlider(0, 100, getP5Canvas().currenttVolume);
		centerPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int volume = (int) getP5Canvas().getSize().height/getP5Canvas().multiplierVolume;
				getP5Canvas().updateSize(getP5Canvas().getSize(), volume);
				volumeLabel.setText(volume+" mL");
			}
		});
		mainFrame.getContentPane().add(centerPanel, "cell 1 2,grow");
		// leftPanel Width=282 		rightPanel Width =255  
		centerPanel.setLayout(new MigLayout("insets 0, gap 2", "[][560.00px][]", "[690px][center]"));

		// Add P5Canvas 
		centerPanel.add(getP5Canvas(), "cell 1 0,grow");
		
		
		
		clPanel = new JPanel();
		clPanel.setLayout(new MigLayout("insets 0, gap 0", "[]", "[][210.00][][40.00][][210.00][]"));
		
		//Set up Volume Slider
		
		volumeSlider.setOrientation(SwingConstants.VERTICAL);		
		volumeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!isVolumeblocked){
					int value = ((JSlider) e.getSource()).getValue(); 
					getP5Canvas().setVolume(value);
					int volume = (int) getP5Canvas().getSize().height/getP5Canvas().multiplierVolume;
					volumeLabel.setText((volume+getP5Canvas().currenttVolume-getP5Canvas().defaultVolume)+" mL");
				}
			}
		});
	    //clPanel.add(volumeLabel, "flowy,cell 0 0,alignx right");
		//clPanel.add(volumeSlider, "cell 0 1,alignx right");
		canvasControlLabel_main_volume = new JLabel("Volume");		
		//clPanel.add(canvasControlLabel_main_volume, "cell 0 2,alignx center");

			
		//Set up Pressure Slide
		getP5Canvas().setPressure(defaultPressure);
		pressureSlider.setOrientation(SwingConstants.VERTICAL);
		pressureLabel = new JLabel(defaultPressure+" atm");

		//Pressure is doing nothing, but we need event listener to change number on pressure label
		pressureSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
					int value = ((JSlider) e.getSource()).getValue(); 
					getP5Canvas().setPressure(value);
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
		
		scaleLabel = new JLabel(defaultZoom*2+"%");
		clPanel.add(scaleLabel, "cell 0 4,alignx right");
		zoomSlider = new JSlider(10,100,defaultZoom);
		zoomSlider.setOrientation(SwingConstants.VERTICAL);
		zoomSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = ((JSlider) e.getSource()).getValue(); 
				scaleLabel.setText(value*2+"%");
				getP5Canvas().setScale(value,defaultZoom);
			
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
				getP5Canvas().setSpeed(speedRate);
				
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
		getP5Canvas().setHeat(heatInit);
		heatSlider.setOrientation(SwingConstants.VERTICAL);
		heatSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = ((JSlider) e.getSource()).getValue(); 
				getP5Canvas().setHeat(value);
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
		rightPanel.setLayout(new MigLayout("insets 0, gap 2", "[320.00,grow,center]", "[6][][350.00,grow][][grow][grow]"));
		
		//Set up "Output" title
		lblOutput = new JLabel("Output");
		lblOutput.setLabelFor(rightPanel);
		lblOutput.setFont(new Font("Lucida Grande", Font.BOLD, 14));
		rightPanel.add(lblOutput, "cell 0 0");
		
		lblMacroscopid = new JLabel("Submicroscopic Level");
		lblMacroscopid.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		rightPanel.add(lblMacroscopid, "cell 0 1");

		//Set up Graph
		JTabbedPane graphTabs = new JTabbedPane(JTabbedPane.TOP);
		lblMacroscopid.setLabelFor(graphTabs);
		rightPanel.add(graphTabs, "cell 0 2,grow");

		JPanel graphSet_1 = new JPanel();
		graphTabs.addTab("Compounds", null, graphSet_1, null);
		graphSet_1.setLayout(new MigLayout("insets 0, gap 0", "[150:n,grow][]", "[235.00:n][grow]"));
		graphSet_1.add(getCanvas(), "cell 0 0,grow");

		JButton graphPopoutBtn_1 = new JButton("");
		graphPopoutBtn_1.setEnabled(false);
		graphPopoutBtn_1.setIcon(new ImageIcon(Main.class.getResource("/resources/png24x24/iconZoom.png")));
		graphSet_1.add(graphPopoutBtn_1, "cell 1 0,aligny top");

		
		graphSet_1.add(getTableView(), "cell 0 1,grow");
		
		JPanel graphSet_2 = new JPanel();
		
		//"Macrosopic Level" label
		lblOutputMacroscopicLevel = new JLabel("Macroscopic Level");
		lblOutputMacroscopicLevel.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		rightPanel.add(lblOutputMacroscopicLevel, "cell 0 3");
		//"Hide Water" label
		//lblHideWater = new JLabel("Hide Water Molecules");
		//lblHideWater.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		
		cBoxHideWater =  new JCheckBox("Hide Water Molecules"); 
		cBoxHideWater.addItemListener(cBoxHideWaterListener);

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
							getP5Canvas().isConvertMol =true;
							//Change 'g' to 'mol' in Amount Added label
							getP5Canvas().convertMassMol1();
							//Change 'g' to 'mol' in "Dissolved" label
							getP5Canvas().convertMassMol2();
						}	
						else if	(e.getStateChange() == ItemEvent.DESELECTED){
							getP5Canvas().isConvertMol = false;
							getP5Canvas().convertMolMass1();
							getP5Canvas().convertMolMass2();
						}	
					}
				});
				//dashboard.add(cBoxConvert, "cell 1 6");
				
				JPanel outputControls = new JPanel();
				rightPanel.add(outputControls, "cell 0 5,grow");
				outputControls.setLayout(new MigLayout("", "[]", "[]"));
				
				outputControls.add(cBoxConvert, "cell 0 0");


		
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
					getCanvas().repaint();
				}
			});
	        timer.setInitialDelay(pause);
	        

	        

	}
	
	//Set up MouseListerners
	private void setupMouseListeners()
	{
	    mulBtnListener = new MouseListener() {
	        public void mouseClicked(MouseEvent e) {
	        	int index = getComponentIndex(e.getComponent());
	        	
	        	if(!started) //Before simulation started user can change selections, but after simulation started they cant
	        	{
	        	if(index!=-1)
	        	{
	        		if(!btnIds.contains(index))	//If this button has not been selected yet
	        		{
		        		if(btnIds.size()<2)
			        	{
			        		btnIds.add(index);
			        		//Set selected as true this button after it gets selected
			        	    ((JButton)e.getComponent()).setSelected(true);

			        	}
	        		}
	        		else //If this button has been selected, deselect it
	        		{
	        			((JButton)e.getComponent()).setSelected(false);
	        			btnIds.remove(new Integer(index));
	        		}
	        	
	        	}
	        	}
	        	
	        }

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub	
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub	
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub	
			}
	      };
	}
	
	private void setupComponentListener()
	{
		playBtnListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (getP5Canvas().isEnable){ //playing, turning to PAUSE					
					//pause timer
					timer.stop();
					getP5Canvas().isEnable = false;
					playBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png48x48/iconPlay.png")));		
				}	
				else{ //Pausing, turning to PLAY
					
					//Special case
					if(selectedUnit ==3 && selectedSim==2)
					{
						if(!started) //If simulation has not started yet
						{
							if(btnIds.size()==2) //If there are two molecules have been selected
							{
							String fixedName = null;
							String waterName = new String("Water");
							int count =5, waterCount = 15;
							for( int i = 0;i<btnIds.size();i++)
							{
								//names.add(btnNames.get(btnIds.get(i)-btnStartId));
								fixedName = new String(btnNames.get((btnIds.get(i)-1)/2)); //Label ids are all even number
								getP5Canvas().addMolecule(fixedName,count);
							}
							getP5Canvas().addMolecule(waterName, waterCount);
						started = true;
						//Disable all molecule buttons on dynamic panel
							for( Component btn:dynamicPanel.getComponents())
								if(btn.getClass().getName().equals("javax.swing.JButton"))
								{
									if(!((JButton)btn).isSelected()) //We disable those non-selected button
										btn.setEnabled(false);
								}
									
									playBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png48x48/iconPause.png")));
									getP5Canvas().isEnable = true; 
									timer.start();
						}
						}
						else //Simulation already started
						{
							playBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png48x48/iconPause.png")));
							getP5Canvas().isEnable = true; 
							timer.start();
						}
					}
					else //General case
					{
						playBtn.setIcon(new ImageIcon(Main.class.getResource("/resources/png48x48/iconPause.png")));
						getP5Canvas().isEnable = true; 
						timer.start();
					}
	
				
				}	
			}
		};
		
		resetBtnListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		};
		
		cBoxHideWaterListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(tableView.contains("Water"))
				{
				if (e.getStateChange() == ItemEvent.SELECTED)  //Hide water
				{
					//Select all elements in tableview except water
					tableView.selectAllRows();
					int [] rows = new int [1];
					rows[0] = tableView.indexOf("Water");
					tableView.deselectRows(rows);
				}
				else if	(e.getStateChange() == ItemEvent.DESELECTED) //Show water
				{
					//Show water 
					if(!tableView.selectedRowsIsEmpty())
					{
						int waterIndex = tableView.indexOf("Water");
						if(!tableView.selectedRowsContain(waterIndex))
						{
							int [] rows = {waterIndex};
							tableView.addSelectedRow(rows);
						}
					}
					
				}
				}
			}
		};
	}
	
	  public  static int getComponentIndex(Component component) {
		    if (component != null && component.getParent() != null) {
		      Container c = component.getParent();
		      for (int i = 0; i < c.getComponentCount(); i++) {
		        if (c.getComponent(i) == component)
		          return i;
		      }
		    }

		    return -1;
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
					//Make sure we get sim information from yaml file
					
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
								tableSet.updataSet();
								tableSet.setSelectedRow(0);
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

	/**
	 * @return the p5Canvas
	 */
	public P5Canvas getP5Canvas() {
		return p5Canvas;
	}

	/**
	 * @param p5Canvas the p5Canvas to set
	 */
	public void setP5Canvas(P5Canvas p5Canvas) {
		this.p5Canvas = p5Canvas;
	}

	/**
	 * @return the canvas
	 */
	public Canvas getCanvas() {
		return canvas;
	}

	/**
	 * @param canvas the canvas to set
	 */
	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	/**
	 * @return the tableSet
	 */
	public TableSet getTableSet() {
		return tableSet;
	}

	/**
	 * @param tableSet the tableSet to set
	 */
	public void setTableSet(TableSet tableSet) {
		this.tableSet = tableSet;
	}

	/**
	 * @return the tableView
	 */
	public TableView getTableView() {
		return tableView;
	}

	/**
	 * @param tableView the tableView to set
	 */
	public void setTableView(TableView tableView) {
		this.tableView = tableView;
	}
}
