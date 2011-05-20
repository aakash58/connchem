package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;


public class MoleculeChooserPanel extends JPanel implements MouseListener {
	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;
	private OrderVarTable table;
	private JPanel panel_3;
	public static int x=800,y=20, w=200,h=600;

	public MoleculeChooserPanel() {
		setLayout(new BorderLayout(10, 10));
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEtchedBorder());
		panel_3 = new JPanel(new FlowLayout());
		table = new OrderVarTable();
		panel_3.add(table);
		JPanel flowPanel_3 = new JPanel(new FlowLayout());
		
		JButton setVarButton = new JButton("Done");
		flowPanel_3.add(setVarButton);

		mainPanel.add(panel_3, BorderLayout.CENTER);
		mainPanel.add(flowPanel_3, BorderLayout.SOUTH);
		add(mainPanel, BorderLayout.CENTER);
		addListeners();
		this.addMouseListener(this);
	}

	private void addListeners() {
		butListener lis = new butListener();
	//	setVarButton.addActionListener(lis);
	}

	class butListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			/*if (e.getSource() == setVarButton) {
				Main.dynamicPanel.removeAll();
				Main.dynamicPanel.updateUI();
			}*/
		}
	}

	public void mouseClicked(MouseEvent e) {
		System.out.println("**********************");
		System.out.println("**********************");
		System.out.println("**********************");
		System.out.println("**********************");
		System.out.println("**********************");
		System.out.println("**********************");
		
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		System.out.println("------------------------------: "+e.getX());
		if (e.getX()<0 || e.getX()>w || e.getY()<0 || e.getY()>h)
			Main.moleculeChooserWindow.show(false);
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
