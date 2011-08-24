package main;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.ListSelectionModel;

import model.YAMLinterface;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;

public class TableSet extends JPanel {
	public static JTable table = null;
	public static JScrollPane scrollPane;
	public static ArrayList set = new ArrayList();
	public static int selectedRow=0; 
	
	
	public TableSet() {
		super(new GridLayout(1, 0));

		MyTableModel myTable = new MyTableModel();
		table = new JTable(myTable);
		
	    scrollPane = new JScrollPane(table);
		JScrollBar jj = new JScrollBar();
		jj.setOrientation(JScrollBar.HORIZONTAL);
		scrollPane.getViewport().setViewPosition(new java.awt.Point(0, 0));

		scrollPane.setHorizontalScrollBar(jj);
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(new RowListener());
		table.getColumnModel().getColumn(0).setPreferredWidth(50);
		table.setSelectionBackground(Color.GRAY);//new Color(40,60,220));
		
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumnModel().getColumn(0).setCellRenderer(dtcr);
		
		
		// Set up renderer and editor for the Favorite Color column.
		table.setDefaultRenderer(Color.class, new ColorRenderer(true));
		table.setDefaultEditor(Color.class, new ColorEditor());
		// Add the scroll pane to this panel.
		add(scrollPane);
		Color c = new Color(245,245,245);
		scrollPane.getViewport().setBackground(c);
		table.setBackground(c);
		
	}

	
	public static void setSelectedRow(int row) {
		selectedRow = row;
		table.clearSelection();
		if (selectedRow<0) selectedRow=0;
		table.addRowSelectionInterval(selectedRow, selectedRow);
		table.updateUI();
	}
	private class RowListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
			selectedRow = table.getSelectedRow();
			Main.selectedSet = selectedRow +1;
			Main.reset();
		}
	}

	public static void updataSet(){
		set = new ArrayList();
		ArrayList sets = YAMLinterface.getSets(Main.selectedUnit, Main.selectedSim);
		if (sets==null) return;
		
		for (int i=0; i<sets.size();i++){
			TableSet.set.add(i+1);
		}
		if (Main.tableSet !=null){
			table.updateUI();
		}		
	}
	
	class MyTableModel extends AbstractTableModel {
		private String[] columnNames = {};
		
		public MyTableModel() {
			set = new ArrayList();
			columnNames = new String[1];
			columnNames[0] = "             Set";
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return set.size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return set.get(row);
		}

		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}

		public void setValueAt(Object value, int row, int col) {
			set.set(row, value);
			fireTableCellUpdated(row, col);
		}
	}


	
}
