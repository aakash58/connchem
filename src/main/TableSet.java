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
	public static ArrayList[] data = new ArrayList[2];
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
		table.getColumnModel().getColumn(1).setPreferredWidth(180);
		table.setSelectionBackground(Color.GRAY);//new Color(40,60,220));
		
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumnModel().getColumn(0).setCellRenderer(dtcr);
		
		
		// Set up renderer and editor for the Favorite Color column.
		table.setDefaultRenderer(Color.class, new ColorRenderer(true));
		table.setDefaultEditor(Color.class, new ColorEditor());
		// Add the scroll pane to this panel.
		add(scrollPane);
		Color c = new Color(241,241,241);
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
		data[0] = new ArrayList();
		data[1] = new ArrayList();
		ArrayList sets = YAMLinterface.getSets(Main.selectedUnit, Main.selectedSim);
		if (sets==null) return;
		
		for (int i=0; i<sets.size();i++){
			TableSet.data[0].add(i+1);
			TableSet.data[1].add("Page "+(i*10+1));
		}
		if (Main.tableSet !=null){
			table.updateUI();
		}		
	}
	
	class MyTableModel extends AbstractTableModel {
		private String[] columnNames = {};
		
		public MyTableModel() {
			data[0] = new ArrayList();
			data[1] = new ArrayList();
			columnNames = new String[2];
			columnNames[0] = "     Set";
			columnNames[1] = "Curriculum";
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data[0].size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data[col].get(row);
		}

		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}

		public void setValueAt(Object value, int row, int col) {
			data[col].set(row, value);
			fireTableCellUpdated(row, col);
		}
	}


	
}
