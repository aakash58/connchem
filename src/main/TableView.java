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
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;

public class TableView extends JPanel {
	public static JTable table = null;
	public static boolean stopUpdating = false;
	public static JScrollPane scrollPane;
	public static ArrayList[] data = new ArrayList[3];
	private static int sat =222;
	public static Color[] colors; 
	public static int selectedRow=-1; 
	
	
	public TableView() {
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
		table.getColumnModel().getColumn(0).setPreferredWidth(10);
		table.getColumnModel().getColumn(1).setPreferredWidth(50);
		table.getColumnModel().getColumn(2).setPreferredWidth(130);
		table.setSelectionBackground(Color.GRAY);//new Color(40,60,220));
		
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.RIGHT);
		table.getColumnModel().getColumn(0).setCellRenderer(dtcr);
		
		
		// Set up renderer and editor for the Favorite Color column.
		table.setDefaultRenderer(Color.class, new ColorRenderer(true));
		table.setDefaultEditor(Color.class, new ColorEditor());
		// Add the scroll pane to this panel.
		add(scrollPane);
		Color c = new Color(241,241,241);
		scrollPane.getViewport().setBackground(c);
		table.setBackground(c);
		
		colors = new Color[Canvas.MAXCOMPOUND];
		colors[0]= new Color(255,0,0,sat);
		colors[1]= new Color(0,255,0,sat);
		colors[2]= new Color(0,0,255,sat);
		colors[3]= new Color(255,255,0,sat);
		colors[4]= new Color(0,255,255,sat);
		colors[5]= new Color(255,0 ,255,sat);
		colors[6]= Color.PINK;
		colors[7]= Color.ORANGE;
		for (int i = 8;i<Canvas.MAXCOMPOUND;i++){
			colors[i] = Color.BLACK;
		}
	}

	public static Color getColor(int index) {
		if (index<colors.length){
			return colors[index];
		}
		return Color.BLACK;
	}
	
	public static void setSelectedRow(int row) {
		selectedRow = row;
		table.clearSelection();
		if (selectedRow>=0){
			table.addRowSelectionInterval(selectedRow, selectedRow);
		}
		table.updateUI();
	}
	
	private class RowListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
			selectedRow = table.getSelectedRow();
			System.out.println("ROW2: "+selectedRow);
		}
	}

	class MyTableModel extends AbstractTableModel {
		private String[] columnNames = {};
		
		public MyTableModel() {
			data[0] = new ArrayList();
			data[1] = new ArrayList();
			data[2] = new ArrayList();
			columnNames = new String[3];
			columnNames[0] = "    #";
			columnNames[1] = "Color";
			columnNames[2] = "Molecule";
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
			if (col == 1) {
				return true;
			} else {
				return false;
			}
		}

		public void setValueAt(Object value, int row, int col) {
			//System.out.println("setValueAt:"+row +" "+col+" "+value);
			data[col].set(row, value);
			colors[row] = (Color) value;
			fireTableCellUpdated(row, col);
		}
	}

	
}
