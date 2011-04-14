package main;
import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.event.*;
import net.miginfocom.swing.MigLayout;

import view.*;
import control.*;
import model.*;

import java.awt.Canvas;
import java.awt.Panel;


public class Simulation {

	private JFrame frmConnectedChemistrySimulations;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Simulation window = new Simulation();
					window.frmConnectedChemistrySimulations.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Simulation() {
		initialize();
	}


	// Demo
	Demo demo = new Demo();
	Demo_Controller demo_controller = new Demo_Controller(demo);
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmConnectedChemistrySimulations = new JFrame();
		frmConnectedChemistrySimulations.setTitle("Connected Chemistry Simulations");
		frmConnectedChemistrySimulations.setBounds(100, 100, 450, 300);
		frmConnectedChemistrySimulations.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmConnectedChemistrySimulations.getContentPane().setLayout(new MigLayout("", "[][grow,fill]", "[grow,fill]"));
		
		// Controller and View Initialization
		demo.init();
		
		// control panel
		JPanel controlPanel = new JPanel();
		frmConnectedChemistrySimulations.getContentPane().add(controlPanel, "cell 0 0,grow");
		controlPanel.setLayout(new MigLayout("", "[grow]", "[29px][]"));
		
		JButton btnLaunchDialog = new JButton("Launch Dialog");
		controlPanel.add(btnLaunchDialog, "cell 0 0,alignx left,aligny top");
		
	    // kite sides slider
	    JSlider js1 = new JSlider(JSlider.HORIZONTAL, 0, 255, 4);
	    js1.setMajorTickSpacing(48);
	    js1.setMinorTickSpacing(12);
	    js1.setPaintTicks(true);
	    js1.setPaintLabels(true);
	 
	    // handle js1 change events
	    js1.addChangeListener(new ChangeListener() {
	      public void stateChanged(ChangeEvent evt) {
	        JSlider slider = (JSlider)evt.getSource();
	 
	        if (!slider.getValueIsAdjusting()) {
	          demo.setBgColor(slider.getValue());
	        }
	      }
	    }
	    );
		controlPanel.add(js1, "cell 0 1,alignx left,aligny top");
		
		Panel demoPanel = new Panel();
		frmConnectedChemistrySimulations.getContentPane().add(demoPanel, "cell 1 0");
		demoPanel.add(demo, "cell 0 0,grow");

	}

}
